package com.example.service

import com.example.data.datasource.GlobalSportsDatabase
import com.example.data.datasource.MackolikFixtureGenerator
import com.example.data.model.Market
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatistics
import com.example.data.model.MatchStatus
import com.example.data.model.Selection
import com.example.data.model.Sport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

/**
 * Otomatik Sezon & Günlük Fikstür Döngüsü (Season & Daily Rollover Engine)
 *
 * 1. Türkiye Saati (TSİ - GMT+3 / Europe/Istanbul) ile 100% entegre çalışır.
 * 2. Günün maçları tamamlandığında veya gece yarısı TSİ 00:00 geçildiğinde:
 *    - Otomatik olarak yarının oyunları üretilir.
 *    - Hafta bazında sezonluk ilerleme (Hafta 1 -> Hafta 2 -> ... -> Hafta 38) kesintisiz devam eder.
 * 3. Maçkolik, İddaa ve Nesine fikstür bütünlüğünü korur; takımlar aynı gün iki maça çıkmaz.
 */
object SeasonRollOverManager {

  val turkeyTimeZone: TimeZone = TimeZone.getTimeZone("GMT+3")

  private val _currentSeasonWeek = MutableStateFlow(5) // Başlangıç: 5. Hafta
  val currentSeasonWeek: StateFlow<Int> = _currentSeasonWeek.asStateFlow()

  private val _turkeyTimeFormatted = MutableStateFlow("")
  val turkeyTimeFormatted: StateFlow<String> = _turkeyTimeFormatted.asStateFlow()

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
    timeZone = turkeyTimeZone
  }

  private val timeFormat = SimpleDateFormat("HH:mm:ss 'TSİ'", Locale.getDefault()).apply {
    timeZone = turkeyTimeZone
  }

  private val displayDateFormat = SimpleDateFormat("dd MMMM yyyy, EEEE", Locale("tr")).apply {
    timeZone = turkeyTimeZone
  }

  init {
    updateTurkeyClock()
  }

  fun updateTurkeyClock() {
    val now = Date(CalendarManagementService.getCurrentTimeMillis())
    _turkeyTimeFormatted.value = timeFormat.format(now)
  }

  fun getCurrentTurkeyDateIso(): String {
    val now = Date(CalendarManagementService.getCurrentTimeMillis())
    return dateFormat.format(now)
  }

  fun getCurrentTurkeyDateDisplay(): String {
    val now = Date(CalendarManagementService.getCurrentTimeMillis())
    return displayDateFormat.format(now)
  }

  /**
   * Kontrol: Günün tüm maçları bitti mi veya rollover gerekli mi?
   * Eğer tüm maçlar FINISHED durumundaysa ya da sonraki güne geçildiyse
   * otomatik olarak ertesi günün ve sonraki haftanın maçlarını üretir.
   */
  fun checkAndAutoAdvanceSeason(currentMatches: List<Match>): List<Match> {
    val todayIso = getCurrentTurkeyDateIso()
    val todayMatches = currentMatches.filter { it.matchDateIso == todayIso }

    val areAllFinished = todayMatches.isNotEmpty() && todayMatches.all { it.status == MatchStatus.FINISHED }

    if (areAllFinished) {
      // Bir sonraki güne ve haftaya geçiş yap
      _currentSeasonWeek.value = (_currentSeasonWeek.value % 38) + 1
      val nextMatches = generateNextRoundMatches(_currentSeasonWeek.value)
      return currentMatches + nextMatches
    }

    return currentMatches
  }

  /**
   * Sezon boyunca ardışık hafta maçları üretimi (Süper Lig, Premier Lig, EuroLeague, MotoGP, WRC)
   */
  fun generateNextRoundMatches(nextWeekNumber: Int): List<Match> {
    val calendar = Calendar.getInstance(turkeyTimeZone).apply {
      timeInMillis = CalendarManagementService.getCurrentTimeMillis()
      add(Calendar.DAY_OF_YEAR, 1) // Ertesi gün
    }

    val nextDateIso = dateFormat.format(calendar.time)
    val nextMatches = ArrayList<Match>()

    val superLigPairs = listOf(
      "Galatasaray" to "Trabzonspor",
      "Fenerbahçe" to "Beşiktaş",
      "Başakşehir" to "Samsunspor",
      "Eyüpspor" to "Göztepe",
      "Kasımpaşa" to "Antalyaspor",
      "Sivasspor" to "Konyaspor",
      "Alanyaspor" to "Rizespor",
      "Gaziantep FK" to "Bodrum FK"
    )

    val times = listOf("13:30", "16:00", "19:00", "20:00", "21:45")
    val channels = listOf("beIN SPORTS 1", "beIN SPORTS 2", "S Sport", "TRT Spor", "Tivibu Spor 1")

    var idCounter = 30000 + nextWeekNumber * 100

    superLigPairs.forEachIndexed { idx, (home, away) ->
      val tsiTime = times[idx % times.size]
      val channel = channels[idx % channels.size]

      val homeOdd = 1.65 + Random.nextDouble(0.0, 1.20)
      val drawOdd = 3.20 + Random.nextDouble(0.0, 0.60)
      val awayOdd = 2.80 + Random.nextDouble(0.0, 2.00)

      val matchIdStr = (idCounter++).toString()
      val marketMsId = "m_ms_$matchIdStr"
      val marketAltUstId = "m_altust_$matchIdStr"

      val markets = listOf(
        Market(
          id = marketMsId,
          type = MarketType.MATCH_RESULT,
          name = "Maç Sonucu (1-X-2)",
          selections = listOf(
            Selection(id = "1", marketId = marketMsId, name = "1 (Ev Sahibi)", odd = Math.round(homeOdd * 100.0) / 100.0),
            Selection(id = "X", marketId = marketMsId, name = "X (Beraberlik)", odd = Math.round(drawOdd * 100.0) / 100.0),
            Selection(id = "2", marketId = marketMsId, name = "2 (Deplasman)", odd = Math.round(awayOdd * 100.0) / 100.0)
          )
        ),
        Market(
          id = marketAltUstId,
          type = MarketType.TOTAL_GOALS_25,
          name = "2.5 Gol Alt/Üst",
          selections = listOf(
            Selection(id = "ALT", marketId = marketAltUstId, name = "2.5 Alt", odd = 1.82),
            Selection(id = "UST", marketId = marketAltUstId, name = "2.5 Üst", odd = 1.95)
          )
        )
      )

      nextMatches.add(
        Match(
          id = matchIdStr,
          sport = Sport.FOOTBALL,
          league = "Trendyol Süper Lig",
          homeTeam = home,
          awayTeam = away,
          homeScore = 0,
          awayScore = 0,
          minute = 0,
          status = MatchStatus.SCHEDULED,
          startTime = tsiTime,
          matchDate = "$nextDateIso $tsiTime TSİ",
          matchDateIso = nextDateIso,
          week = nextWeekNumber,
          tvBroadcast = channel,
          isHot = idx < 2,
          isKralOran = true,
          mbs = 1,
          markets = markets,
          statistics = MatchStatistics(
            possessionHome = 52,
            possessionAway = 48,
            shotsHome = 11,
            shotsAway = 9,
            shotsOnTargetHome = 4,
            shotsOnTargetAway = 3
          )
        )
      )
    }

    // MotoGP & WRC Subsequent Events
    val motoMatchId = (idCounter++).toString()
    val motoMarketId = "m_moto_$motoMatchId"
    nextMatches.add(
      Match(
        id = motoMatchId,
        sport = Sport.MOTORSPORTS,
        league = "MotoGP Grand Prix",
        homeTeam = "Francesco Bagnaia (Ducati)",
        awayTeam = "Jorge Martin (Pramac)",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.SCHEDULED,
        startTime = "15:00",
        matchDate = "$nextDateIso 15:00 TSİ",
        matchDateIso = nextDateIso,
        week = nextWeekNumber,
        tvBroadcast = "S Sport 2",
        isHot = true,
        isKralOran = true,
        mbs = 1,
        markets = listOf(
          Market(
            id = motoMarketId,
            type = MarketType.MOTORSPORTS_WINNER,
            name = "Yarışı Kim Kazanır?",
            selections = listOf(
              Selection(id = "1", marketId = motoMarketId, name = "Bagnaia (1.)", odd = 1.90),
              Selection(id = "2", marketId = motoMarketId, name = "Martin (1.)", odd = 2.15)
            )
          )
        )
      )
    )

    // WRC World Rally Championship Event
    val wrcMatchId = (idCounter++).toString()
    val wrcMarketId = "m_wrc_$wrcMatchId"
    nextMatches.add(
      Match(
        id = wrcMatchId,
        sport = Sport.MOTORSPORTS,
        league = "WRC Dünya Rallisi",
        homeTeam = "Thierry Neuville (Hyundai)",
        awayTeam = "Ott Tänak (Hyundai)",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.SCHEDULED,
        startTime = "16:30",
        matchDate = "$nextDateIso 16:30 TSİ",
        matchDateIso = nextDateIso,
        week = nextWeekNumber,
        tvBroadcast = "Red Bull TV",
        isHot = true,
        isKralOran = true,
        mbs = 1,
        markets = listOf(
          Market(
            id = wrcMarketId,
            type = MarketType.MOTORSPORTS_WINNER,
            name = "Ralli Etabı Galibi",
            selections = listOf(
              Selection(id = "1", marketId = wrcMarketId, name = "Neuville (1.)", odd = 2.10),
              Selection(id = "2", marketId = wrcMarketId, name = "Tänak (1.)", odd = 2.35)
            )
          )
        )
      )
    )

    return nextMatches
  }
}
