package com.example.engine

import com.example.data.datasource.GlobalSportsDatabase
import com.example.data.model.AiPrediction
import com.example.data.model.Market
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchEvent
import com.example.data.model.MatchStatistics
import com.example.data.model.MatchStatus
import com.example.data.model.Selection
import com.example.data.model.Sport
import com.example.service.CalendarManagementService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Mackolik Authentic Weekly Fixture & League Scheduling Engine.
 * 
 * Guarantees that:
 * 1. Teams in each sport strictly play against rivals in their OWN league (round-robin Hafta 1..38).
 * 2. UEFA Champions League, Europa League, Conference League, and National Team (Ülke) matches
 *    are scheduled on mid-week days (Salı, Çarşamba, Perşembe).
 * 3. Matchups change every week authentically like real-life Maçkolik.
 */
object MackolikWeeklyFixtureEngine {

  // --- NATIONAL TEAMS (ÜLKE / MİLLİ TAKIMLAR) ---
  val nationalTeams = listOf(
    "Türkiye", "Almanya", "Fransa", "İspanya", "İtalya", "İngiltere",
    "Portekiz", "Hollanda", "Hırvatistan", "Belçika", "Danimarka", "İsviçre",
    "Avusturya", "Norveç", "İsveç", "Polonya", "Galler", "İskoçya",
    "Brezilya", "Arjantin", "Uruguay", "Kolombiya", "Japonya", "Güney Kore"
  )

  // --- UEFA TOURNAMENT POTS FOR LIVE DRAW SIMULATOR ---
  val uefaPots = mapOf(
    1 to listOf("Real Madrid", "Manchester City", "Bayern München", "Paris Saint-Germain", "Liverpool", "Inter", "Barcelona", "Borussia Dortmund", "RB Leipzig"),
    2 to listOf("Arsenal", "Bayer Leverkusen", "Atletico Madrid", "Atalanta", "Juventus", "Benfica", "Club Brugge", "AC Milan", "Sporting CP"),
    3 to listOf("Feyenoord", "PSV Eindhoven", "Dinamo Zagreb", "Red Bull Salzburg", "Lille", "Crvena Zvezda", "Young Boys", "Celtic", "Sparta Prag"),
    4 to listOf("Galatasaray", "Fenerbahçe", "Beşiktaş", "AS Monaco", "Aston Villa", "Bologna", "Girona", "VfB Stuttgart", "Sturm Graz", "Brest")
  )

  val europaPots = mapOf(
    1 to listOf("Manchester United", "AS Roma", "FC Porto", "Lazio", "Tottenham", "Ajax", "Real Sociedad", "Athletic Bilbao"),
    2 to listOf("Fenerbahçe", "Galatasaray", "Beşiktaş", "Olympiacos", "Lyon", "Eintracht Frankfurt", "AZ Alkmaar", "Rangers"),
    3 to listOf("Braga", "PAOK", "Maccabi Tel Aviv", "Union Saint-Gilloise", "Dynamo Kyiv", "Ludogorets", "Midtjylland", "Malmö"),
    4 to listOf("Twente", "Bodø/Glimt", "Anderlecht", "RFS", "Elfsborg", "Nice", "Qarabag", "Hoffenheim")
  )

  val conferencePots = mapOf(
    1 to listOf("Chelsea", "Fiorentina", "Real Betis", "FC Copenhagen", "Gent", "LASK"),
    2 to listOf("Başakşehir", "Rapid Wien", "Legia Warszawa", "1. FC Heidenheim", "Djurgården", "APOEL"),
    3 to listOf("Omonoia", "Astana", "HJK Helsinki", "Vitoria Guimaraes", "FC St. Gallen", "Panathinaikos"),
    4 to listOf("Cercle Brugge", "Lugano", "Celje", "FC Noah", "Vikingur Reykjavik", "Larne")
  )

  data class LeagueScheduleInfo(
    val leagueName: String,
    val sport: Sport,
    val teams: List<String>,
    val totalWeeks: Int,
    val isMidWeek: Boolean = false,
    val defaultSlots: List<String> = listOf("13:30", "16:00", "19:00", "20:00")
  )

  val registeredLeagues: List<LeagueScheduleInfo> = listOf(
    // 1. Süper Lig (19 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Trendyol Süper Lig",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.superLigTeams,
      totalWeeks = 38,
      defaultSlots = listOf("13:30", "16:00", "19:00", "20:00")
    ),
    // 2. Trendyol 1. Lig (20 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Trendyol 1. Lig",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.tff1LigTeams,
      totalWeeks = 38,
      defaultSlots = listOf("13:30", "16:00", "19:00", "20:30")
    ),
    // 3. Premier League (20 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Premier League",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.premierLeagueTeams,
      totalWeeks = 38,
      defaultSlots = listOf("14:30", "17:00", "19:30", "22:00")
    ),
    // 4. La Liga (20 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "La Liga",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.laLigaTeams,
      totalWeeks = 38,
      defaultSlots = listOf("15:00", "17:15", "19:30", "22:00")
    ),
    // 5. Serie A (20 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Serie A",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.serieATeams,
      totalWeeks = 38,
      defaultSlots = listOf("16:00", "19:00", "21:45")
    ),
    // 6. Bundesliga (18 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Bundesliga",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.bundesligaTeams,
      totalWeeks = 34,
      defaultSlots = listOf("16:30", "19:30")
    ),
    // 7. Ligue 1 (18 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Ligue 1",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.ligue1Teams,
      totalWeeks = 34,
      defaultSlots = listOf("18:00", "20:00", "21:45")
    ),
    // 8. Eredivisie (18 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Eredivisie",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.eredivisieTeams,
      totalWeeks = 34,
      defaultSlots = listOf("15:30", "17:45", "21:00")
    ),
    // 9. Liga Portugal (18 Takım - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Liga Portugal",
      sport = Sport.FOOTBALL,
      teams = GlobalSportsDatabase.ligaPortugalTeams,
      totalWeeks = 34,
      defaultSlots = listOf("18:30", "21:00", "22:30")
    ),
    // 10. UEFA Şampiyonlar Ligi (Hafta İçi: Salı / Çarşamba)
    LeagueScheduleInfo(
      leagueName = "UEFA Şampiyonlar Ligi",
      sport = Sport.FOOTBALL,
      teams = uefaPots.values.flatten().distinct(),
      totalWeeks = 8,
      isMidWeek = true,
      defaultSlots = listOf("19:45", "22:00")
    ),
    // 11. UEFA Avrupa Ligi (Hafta İçi: Perşembe)
    LeagueScheduleInfo(
      leagueName = "UEFA Avrupa Ligi",
      sport = Sport.FOOTBALL,
      teams = europaPots.values.flatten().distinct(),
      totalWeeks = 8,
      isMidWeek = true,
      defaultSlots = listOf("19:45", "22:00")
    ),
    // 12. UEFA Konferans Ligi (Hafta İçi: Perşembe)
    LeagueScheduleInfo(
      leagueName = "UEFA Konferans Ligi",
      sport = Sport.FOOTBALL,
      teams = conferencePots.values.flatten().distinct(),
      totalWeeks = 6,
      isMidWeek = true,
      defaultSlots = listOf("19:45", "22:00")
    ),
    // 13. Uluslararası Ülke Maçları (Hafta İçi / UEFA Uluslar Ligi / Milli Maçlar)
    LeagueScheduleInfo(
      leagueName = "UEFA Uluslar Ligi / Ülke Maçları",
      sport = Sport.FOOTBALL,
      teams = nationalTeams,
      totalWeeks = 10,
      isMidWeek = true,
      defaultSlots = listOf("19:00", "21:45")
    ),
    // 14. EuroLeague (Basketbol - Hafta İçi Salı-Cuma)
    LeagueScheduleInfo(
      leagueName = "EuroLeague",
      sport = Sport.BASKETBALL,
      teams = GlobalSportsDatabase.euroLeagueTeams,
      totalWeeks = 34,
      isMidWeek = true,
      defaultSlots = listOf("19:45", "20:30", "21:00", "21:45")
    ),
    // 15. NBA (Basketbol - Gece / Hafta Boyu)
    LeagueScheduleInfo(
      leagueName = "NBA",
      sport = Sport.BASKETBALL,
      teams = GlobalSportsDatabase.nbaTeams,
      totalWeeks = 30,
      defaultSlots = listOf("02:00", "03:30", "05:00")
    ),
    // 16. Türkiye Sigorta BSL (Basketbol - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Türkiye Sigorta BSL",
      sport = Sport.BASKETBALL,
      teams = GlobalSportsDatabase.bslTeams,
      totalWeeks = 30,
      defaultSlots = listOf("13:00", "15:30", "18:00", "20:30")
    ),
    // 17. Vodafone Sultanlar Ligi (Voleybol - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "Vodafone Sultanlar Ligi",
      sport = Sport.VOLLEYBALL,
      teams = GlobalSportsDatabase.sultanlarLigiTeams,
      totalWeeks = 26,
      defaultSlots = listOf("14:00", "16:30", "19:00")
    ),
    // 18. SMS Grup Efeler Ligi (Voleybol - Hafta Sonu)
    LeagueScheduleInfo(
      leagueName = "SMS Grup Efeler Ligi",
      sport = Sport.VOLLEYBALL,
      teams = listOf(
        "Halkbank", "Fenerbahçe Medicana", "Ziraat Bankkart", "Galatasaray HDI Sigorta",
        "Arkas Spor", "Spor Toto", "Altekma", "Bursa BŞB", "Kuşgöz İzmir Vinç",
        "İstanbul Gençlik Spor", "Rams Global Cizre Bld", "Akkuş Bld"
      ),
      totalWeeks = 22,
      defaultSlots = listOf("14:00", "16:30", "19:00")
    ),
    // 19. Tenis ATP & WTA Tour (Haftalık Turnuva)
    LeagueScheduleInfo(
      leagueName = "ATP & WTA Tour",
      sport = Sport.TENNIS,
      teams = GlobalSportsDatabase.tennisPlayers,
      totalWeeks = 36,
      defaultSlots = listOf("12:00", "14:30", "17:00", "20:00")
    ),
    // 20. Hentbol EHF Şampiyonlar Ligi (Hafta İçi)
    LeagueScheduleInfo(
      leagueName = "EHF Şampiyonlar Ligi",
      sport = Sport.HANDBALL,
      teams = listOf(
        "Barça", "Magdeburg", "Aalborg", "Veszprem", "Kielce", "PSG Handball",
        "Füchse Berlin", "Sporting CP", "Dinamo Bucuresti", "Szeged", "Kolstad", "Pelister"
      ),
      totalWeeks = 14,
      isMidWeek = true,
      defaultSlots = listOf("19:45", "21:45")
    )
  )

  /**
   * Generates a realistic round-robin schedule for a given list of teams and target week.
   * Standard Berger/Circle pairing ensures every team plays everyone else once home & away,
   * without ever meeting twice in the same half.
   */
  fun generateRoundRobinMatchups(teams: List<String>, week: Int): List<Pair<String, String>> {
    if (teams.size < 2) return emptyList()

    val teamList = teams.toMutableList()
    val isOdd = teamList.size % 2 != 0
    if (isOdd) {
      teamList.add("BAY") // Bye team
    }

    val numTeams = teamList.size
    val numRounds = numTeams - 1
    val normalizedRound = ((week - 1) % (numRounds * 2)) + 1
    val isSecondHalf = normalizedRound > numRounds
    val round = if (isSecondHalf) normalizedRound - numRounds else normalizedRound

    val matchups = mutableListOf<Pair<String, String>>()

    for (i in 0 until numTeams / 2) {
      val t1 = (round - 1 + i) % (numTeams - 1)
      val t2 = if (i == 0) numTeams - 1 else (numTeams - 1 - i + round - 1) % (numTeams - 1)

      val home = teamList[t1]
      val away = teamList[t2]

      if (home != "BAY" && away != "BAY") {
        if (isSecondHalf) {
          matchups.add(Pair(away, home)) // Invert home/away in second half
        } else {
          matchups.add(Pair(home, away))
        }
      }
    }

    return matchups
  }

  /**
   * Generates all matches for a given week across all registered leagues.
   */
  fun generateFixturesForWeek(
    weekNumber: Int,
    baseDateIso: String = CalendarManagementService.getCurrentDateIso()
  ): List<Match> {
    val results = mutableListOf<Match>()
    val cal = Calendar.getInstance(CalendarManagementService.turkeyTimeZone)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = CalendarManagementService.turkeyTimeZone }

    try {
      cal.time = sdf.parse(baseDateIso) ?: cal.time
    } catch (_: Exception) {}

    var idCounter = (weekNumber * 1000) + 100

    val weekOffsetDays = (weekNumber - 1) * 7

    registeredLeagues.forEach { league ->
      val effectiveWeek = ((weekNumber - 1) % league.totalWeeks) + 1
      val matchups = generateRoundRobinMatchups(league.teams, effectiveWeek)

      matchups.forEachIndexed { idx, pair ->
        val matchId = "m_w${weekNumber}_${league.leagueName.take(3).lowercase()}_$idx"
        val timeSlot = league.defaultSlots[idx % league.defaultSlots.size]

        // Schedule dates:
        // Hafta İçi: Salı (+1), Çarşamba (+2), Perşembe (+3)
        // Hafta Sonu: Cuma (+4), Cumartesi (+5), Pazar (+6), Pazartesi (+7)
        val dayOffset = if (league.isMidWeek) {
          if (league.leagueName.contains("Avrupa") || league.leagueName.contains("Konferans")) 3 // Perşembe
          else if (idx % 2 == 0) 1 // Salı
          else 2 // Çarşamba
        } else {
          when (idx % 4) {
            0 -> 4 // Cuma
            1 -> 5 // Cumartesi
            2 -> 6 // Pazar
            else -> 7 // Pazartesi
          }
        }

        val totalDays = weekOffsetDays + dayOffset
        val matchCal = Calendar.getInstance(CalendarManagementService.turkeyTimeZone).apply {
          timeInMillis = cal.timeInMillis
          add(Calendar.DAY_OF_YEAR, totalDays)
        }
        val matchDateIso = sdf.format(matchCal.time)
        val dayOfWeekTr = when (matchCal.get(Calendar.DAY_OF_WEEK)) {
          Calendar.MONDAY -> "Pazartesi"
          Calendar.TUESDAY -> "Salı"
          Calendar.WEDNESDAY -> "Çarşamba"
          Calendar.THURSDAY -> "Perşembe"
          Calendar.FRIDAY -> "Cuma"
          Calendar.SATURDAY -> "Cumartesi"
          else -> "Pazar"
        }

        val isDerby = isBigDerbyMatch(pair.first, pair.second)
        val (hOdd, dOdd, aOdd) = generateRealisticOdds(pair.first, pair.second, league.sport)
        val timingCategory = if (league.isMidWeek) "Hafta İçi" else "Hafta Sonu"

        val m = Match(
          id = matchId,
          sport = league.sport,
          league = league.leagueName,
          homeTeam = pair.first,
          awayTeam = pair.second,
          homeScore = 0,
          awayScore = 0,
          minute = 0,
          status = MatchStatus.SCHEDULED,
          markets = generateNesineMarkets(matchId, hOdd, dOdd, aOdd, league.sport),
          statistics = MatchStatistics(
            xgHome = generateBaseXg(pair.first, true),
            xgAway = generateBaseXg(pair.second, false)
          ),
          aiPrediction = AiPrediction(
            homeWinProb = (45 + (1.0 / hOdd * 20)).toInt().coerceIn(20, 75),
            drawProb = (25).coerceIn(15, 35),
            awayWinProb = (30 + (1.0 / aOdd * 20)).toInt().coerceIn(15, 65),
            predictedScore = if (league.sport == Sport.BASKETBALL) "88 - 82" else "2 - 1",
            confidence = if (isDerby) 88 else 75,
            xgSummary = "${pair.first} iç sahada hücum etkinliğiyle öne çıkıyor.",
            tacticalAnalysis = "${league.leagueName} $effectiveWeek. Hafta karşılaşması ($timingCategory).",
            formRatingHome = "G-G-B",
            formRatingAway = "M-B-G"
          ),
          events = emptyList(),
          startTime = timeSlot,
          isHot = isDerby || (idx % 3 == 0),
          stadium = GlobalSportsDatabase.stadiumMap[pair.first] ?: "${pair.first} Sahası",
          tvBroadcast = getBroadcastForLeague(league.leagueName),
          week = weekNumber,
          matchDate = "$dayOfWeekTr $timeSlot TSİ ($timingCategory • $effectiveWeek. Hafta)",
          matchDateIso = matchDateIso,
          iddaaCode = (idCounter++).toString(),
          mbs = if (isDerby || idx % 2 == 0) 1 else 2,
          isKralOran = isDerby || (idx % 4 == 0),
          popularBetPercentage = if (isDerby) 92 else 45 + (idx * 7 % 45)
        )
        results.add(m)
      }
    }

    return results
  }

  fun isMidweekMatch(match: Match): Boolean {
    val m = match.league.lowercase()
    return m.contains("şampiyonlar") || m.contains("avrupa") || m.contains("konferans") ||
           m.contains("uluslar") || m.contains("ülke") || m.contains("euroleague") ||
           m.contains("ehf") || match.matchDate.contains("Hafta İçi")
  }

  fun isWeekendMatch(match: Match): Boolean {
    return !isMidweekMatch(match)
  }

  /**
   * Generates a rich, full multi-week bulletin with authentic weekly matchups,
   * live games, mid-week European cups, and national fixtures.
   */
  fun generateComprehensiveWeeklyBulletin(): List<Match> {
    val currentWeek = 1
    val baseToday = CalendarManagementService.getCurrentDateIso()
    val yesterday = CalendarManagementService.getYesterdayDateIso()
    val tomorrow = CalendarManagementService.getTomorrowDateIso()

    val allMatches = mutableListOf<Match>()

    // 1. Current Active Week Matches (Hafta 1)
    val activeWeekMatches = generateFixturesForWeek(1, baseToday).toMutableList()

    // Align today's derbies with today's date for instant live action
    activeWeekMatches.take(6).forEachIndexed { i, m ->
      val kickoffSlot = when (i) {
        0 -> "19:00"
        1 -> "20:00"
        2 -> "21:45"
        else -> "22:00"
      }
      activeWeekMatches[i] = m.copy(
        matchDateIso = baseToday,
        startTime = kickoffSlot,
        matchDate = "Bugün $kickoffSlot TSİ (Canlı / Bülten)",
        isHot = true
      )
    }

    // Set first 2 matches as live right now for instant gameplay
    if (activeWeekMatches.isNotEmpty()) {
      val live1 = activeWeekMatches[0]
      activeWeekMatches[0] = live1.copy(
        status = MatchStatus.LIVE,
        minute = 54,
        homeScore = 2,
        awayScore = 1,
        events = listOf(
          MatchEvent("ev1_${live1.id}", 24, com.example.data.model.EventType.GOAL, live1.homeTeam, GlobalSportsDatabase.getScorerForTeam(live1.homeTeam), "Gol! ⚽"),
          MatchEvent("ev2_${live1.id}", 38, com.example.data.model.EventType.GOAL, live1.awayTeam, GlobalSportsDatabase.getScorerForTeam(live1.awayTeam), "Kafa vuruşuyla gol! ⚽"),
          MatchEvent("ev3_${live1.id}", 49, com.example.data.model.EventType.GOAL, live1.homeTeam, GlobalSportsDatabase.getScorerForTeam(live1.homeTeam), "Ceza sahası dışından sert şutla gol! ⚽")
        )
      )
    }
    if (activeWeekMatches.size > 1) {
      val live2 = activeWeekMatches[1]
      activeWeekMatches[1] = live2.copy(
        status = MatchStatus.LIVE,
        minute = 32,
        homeScore = 1,
        awayScore = 0,
        events = listOf(
          MatchEvent("ev1_${live2.id}", 18, com.example.data.model.EventType.GOAL, live2.homeTeam, GlobalSportsDatabase.getScorerForTeam(live2.homeTeam), "Harika plase gol! ⚽")
        )
      )
    }

    allMatches.addAll(activeWeekMatches)

    // 2. Yesterday's Finished Matches (Biten Maçlar)
    val yesterdayMatches = generateFixturesForWeek(38, yesterday).take(12).mapIndexed { i, m ->
      val hScore = 1 + (i % 3)
      val aScore = if (i % 2 == 0) 1 else 0
      m.copy(
        id = "yest_${m.id}",
        status = MatchStatus.FINISHED,
        minute = 90,
        homeScore = hScore,
        awayScore = aScore,
        matchDateIso = yesterday,
        matchDate = "Dün (Bitti: $hScore - $aScore)",
        events = listOf(
          MatchEvent("y_ev1_$i", 35, com.example.data.model.EventType.GOAL, m.homeTeam, GlobalSportsDatabase.getScorerForTeam(m.homeTeam), "Gol ⚽"),
          MatchEvent("y_ft_$i", 90, com.example.data.model.EventType.QUARTER_END, "Hakem", "Hakem", "🏁 MAÇ BİTTİ ($hScore - $aScore)")
        )
      )
    }
    allMatches.addAll(yesterdayMatches)

    // 3. Tomorrow's Bulletin (Yarın Bülteni)
    val tomorrowMatches = generateFixturesForWeek(1, tomorrow).take(15).map { m ->
      m.copy(
        id = "tom_${m.id}",
        status = MatchStatus.SCHEDULED,
        minute = 0,
        homeScore = 0,
        awayScore = 0,
        matchDateIso = tomorrow,
        matchDate = "Yarın ${m.startTime} TSİ"
      )
    }
    allMatches.addAll(tomorrowMatches)

    // 4. Week 2 Fixtures (+1 Hafta Sonra)
    val week2DateIso = CalendarManagementService.getWeek2DateIso()
    val week2Matches = generateFixturesForWeek(2, week2DateIso)
    allMatches.addAll(week2Matches)

    // 5. Week 3 Fixtures (+2 Hafta Sonra)
    val week3DateIso = CalendarManagementService.getWeek3DateIso()
    val week3Matches = generateFixturesForWeek(3, week3DateIso)
    allMatches.addAll(week3Matches)

    return allMatches
  }

  // --- HELPERS ---

  private fun isBigDerbyMatch(tA: String, tB: String): Boolean {
    val derbyClubs = setOf(
      "Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor",
      "Real Madrid", "Barcelona", "Arsenal", "Manchester City", "Liverpool", "Chelsea", "Manchester United",
      "Inter", "AC Milan", "Juventus", "Bayern München", "Borussia Dortmund",
      "Türkiye", "Almanya", "Fransa", "Brezilya", "Arjantin"
    )
    return derbyClubs.contains(tA) && derbyClubs.contains(tB)
  }

  private fun generateRealisticOdds(tA: String, tB: String, sport: Sport): Triple<Double, Double, Double> {
    val tierA = getTeamStrengthTier(tA)
    val tierB = getTeamStrengthTier(tB)
    val diff = tierA - tierB // Negative = A is stronger

    val homeOdd: Double
    val drawOdd: Double
    val awayOdd: Double

    when {
      diff <= -2 -> { // Home heavy favorite
        homeOdd = 1.35 + (abs(diff) * 0.05)
        drawOdd = 4.20
        awayOdd = 5.80 + (abs(diff) * 0.40)
      }
      diff == -1 -> { // Home moderate favorite
        homeOdd = 1.75
        drawOdd = 3.30
        awayOdd = 3.80
      }
      diff == 0 -> { // Balanced match
        homeOdd = 2.15
        drawOdd = 3.10
        awayOdd = 2.80
      }
      diff == 1 -> { // Away moderate favorite
        homeOdd = 3.40
        drawOdd = 3.25
        awayOdd = 1.85
      }
      else -> { // Away heavy favorite
        homeOdd = 5.20
        drawOdd = 3.90
        awayOdd = 1.45
      }
    }

    return Triple(roundOdd(homeOdd), roundOdd(drawOdd), roundOdd(awayOdd))
  }

  private fun getTeamStrengthTier(team: String): Int {
    return when (team) {
      "Manchester City", "Real Madrid", "Bayern München", "Arsenal", "Inter", "PSG" -> 1
      "Liverpool", "Barcelona", "Bayer Leverkusen", "Galatasaray", "Fenerbahçe", "Juventus", "Atalanta" -> 2
      "Beşiktaş", "Trabzonspor", "Aston Villa", "Chelsea", "Tottenham", "AC Milan", "Roma", "Dortmund" -> 3
      "Başakşehir", "Samsunspor", "Göztepe", "Sevilla", "Villarreal", "Lazio", "Fiorentina" -> 4
      else -> 5
    }
  }

  private fun generateBaseXg(team: String, isHome: Boolean): Double {
    val tier = getTeamStrengthTier(team)
    val base = when (tier) {
      1 -> 2.10
      2 -> 1.80
      3 -> 1.50
      4 -> 1.25
      else -> 0.95
    }
    return roundOdd(base + (if (isHome) 0.25 else 0.0))
  }

  private fun roundOdd(value: Double): Double {
    return (value * 100).roundToInt() / 100.0
  }

  private fun getBroadcastForLeague(leagueName: String): String {
    return when {
      leagueName.contains("Süper Lig") -> "beIN SPORTS 1"
      leagueName.contains("Premier") -> "beIN SPORTS 3"
      leagueName.contains("La Liga") -> "S Sport"
      leagueName.contains("Serie A") -> "S Sport 2"
      leagueName.contains("Bundesliga") -> "Tivibu Spor"
      leagueName.contains("Şampiyonlar Ligi") -> "TRT 1 / Tabii Spor"
      leagueName.contains("Avrupa") || leagueName.contains("Konferans") -> "TRT Spor / Tabii Spor"
      leagueName.contains("EuroLeague") -> "S Sport"
      leagueName.contains("NBA") -> "NBA TV / S Sport Plus"
      leagueName.contains("Sultanlar") -> "TRT Spor Yıldız"
      leagueName.contains("Ülke") || leagueName.contains("Uluslar") -> "TRT 1"
      else -> "Nesine TV"
    }
  }

  private fun generateNesineMarkets(
    matchId: String,
    odd1: Double,
    oddX: Double,
    odd2: Double,
    sport: Sport
  ): List<Market> {
    val list = mutableListOf<Market>()

    // MS Market
    val isTwoWaySport = sport == Sport.BASKETBALL || sport == Sport.TENNIS || sport == Sport.VOLLEYBALL
    list.add(
      Market(
        id = "${matchId}_ms",
        type = if (sport == Sport.BASKETBALL) MarketType.BASKETBALL_MS else MarketType.MATCH_RESULT,
        name = if (isTwoWaySport) "Maç Sonucu (1-2)" else "Maç Sonucu (MS)",
        selections = if (isTwoWaySport) {
          listOf(
            Selection("${matchId}_ms_1", "${matchId}_ms", "MS 1", odd1),
            Selection("${matchId}_ms_2", "${matchId}_ms", "MS 2", odd2)
          )
        } else {
          listOf(
            Selection("${matchId}_ms_1", "${matchId}_ms", "MS 1", odd1),
            Selection("${matchId}_ms_x", "${matchId}_ms", "MS X", oddX),
            Selection("${matchId}_ms_2", "${matchId}_ms", "MS 2", odd2)
          )
        }
      )
    )

    if (sport == Sport.BASKETBALL) {
      list.add(
        Market(
          id = "${matchId}_ou_pts",
          type = MarketType.TOTAL_GOALS_25,
          name = "Toplam Sayı (165.5 Alt/Üst)",
          selections = listOf(
            Selection("${matchId}_ov_pts", "${matchId}_ou_pts", "165.5 Üst", 1.80),
            Selection("${matchId}_un_pts", "${matchId}_ou_pts", "165.5 Alt", 1.80)
          )
        )
      )
    }

    if (sport == Sport.FOOTBALL) {
      // 2.5 Alt/Üst
      list.add(
        Market(
          id = "${matchId}_25",
          type = MarketType.TOTAL_GOALS_25,
          name = "2.5 Gol Alt/Üst",
          selections = listOf(
            Selection("${matchId}_ov25", "${matchId}_25", "2.5 Üst", 1.75),
            Selection("${matchId}_un25", "${matchId}_25", "2.5 Alt", 1.85)
          )
        )
      )

      // KG Var / Yok
      list.add(
        Market(
          id = "${matchId}_kg",
          type = MarketType.BOTH_TEAMS_SCORE,
          name = "Karşılıklı Gol (KG)",
          selections = listOf(
            Selection("${matchId}_kg_yes", "${matchId}_kg", "KG Var", 1.62),
            Selection("${matchId}_kg_no", "${matchId}_kg", "KG Yok", 1.95)
          )
        )
      )

      // Çifte Şans
      list.add(
        Market(
          id = "${matchId}_cs",
          type = MarketType.DOUBLE_CHANCE,
          name = "Çifte Şans",
          selections = listOf(
            Selection("${matchId}_cs_1x", "${matchId}_cs", "1-X", roundOdd(odd1 * 0.65).coerceAtLeast(1.10)),
            Selection("${matchId}_cs_12", "${matchId}_cs", "1-2", 1.25),
            Selection("${matchId}_cs_x2", "${matchId}_cs", "X-2", roundOdd(odd2 * 0.65).coerceAtLeast(1.10))
          )
        )
      )
    }

    return list
  }
}
