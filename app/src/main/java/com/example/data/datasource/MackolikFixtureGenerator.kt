package com.example.data.datasource

import com.example.data.model.AiPrediction
import com.example.data.model.BasketballStatistics
import com.example.data.model.EventType
import com.example.data.model.Market
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchEvent
import com.example.data.model.MatchStatistics
import com.example.data.model.MatchStatus
import com.example.data.model.Selection
import com.example.data.model.Sport
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Authentic High-Capacity Mackolik & Nesine Fixture Generator
 *
 * Implements full requirements:
 * 1. Automatically organizes daily 2000+ games for 26.09.2026.
 * 2. Starts from 00:00 through 23:55 with matches spaced every 5-10 minutes.
 * 3. Enforces strict sports calendar integrity:
 *    - Every team plays at most ONCE on 26.09.2026 (NO DUPLICATE TEAMS in the daily fixture).
 *    - Teams play their subsequent match 1-2 weeks later (03.10.2026 & 10.10.2026).
 * 4. Multi-sport global coverage: Football (all tiers from Süper Lig down to BAL and regional),
 *    NBA, EuroLeague, MotoGP, Formula 1, WRC Rally, Tennis, Table Tennis, Volleyball,
 *    Ice Hockey, Esports, Handball, Baseball, Snooker & Darts.
 * 5. Full authentic Nesine/İddaa markets (MS 1-X-2, 2.5 Alt/Üst, KG, Çifte Şans, Kral Oran, MBS).
 */
object MackolikFixtureGenerator {

  val todayIso: String
    get() = com.example.service.CalendarManagementService.getCurrentDateIso()

  val yesterdayIso: String
    get() = com.example.service.CalendarManagementService.getYesterdayDateIso()

  val tomorrowIso: String
    get() = com.example.service.CalendarManagementService.getTomorrowDateIso()

  fun getScorerForTeam(teamName: String): String {
    return GlobalSportsDatabase.getScorerForTeam(teamName)
  }

  fun generateAllMatches(): List<Match> {
    // Generates calendar-authentic weekly league and live bulletin matches based on active schedule
    return com.example.engine.MackolikWeeklyFixtureEngine.generateComprehensiveWeeklyBulletin()
  }

  /**
   * Headline derbies and showpiece events for 26.09.2026.
   */
  private fun createFeaturedMatchesForSept26(): List<Match> {
    val list = mutableListOf<Match>()

    // Derbi 1: Galatasaray - Fenerbahçe (Trendyol Süper Lig - CANLI DERBİ!)
    val m1Markets = generateNesineMarkets("m_feat_1", 2.05, 3.35, 3.10, 1.72, 1.88, 1.62, 2.05)
    list.add(
      Match(
        id = "m_feat_1",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Galatasaray",
        awayTeam = "Fenerbahçe",
        homeScore = 2,
        awayScore = 1,
        minute = 68,
        status = MatchStatus.LIVE,
        markets = m1Markets,
        statistics = MatchStatistics(
          possessionHome = 54, possessionAway = 46, shotsHome = 12, shotsAway = 9,
          shotsOnTargetHome = 6, shotsOnTargetAway = 4, cornersHome = 7, cornersAway = 5,
          foulsHome = 11, foulsAway = 14, yellowCardsHome = 2, yellowCardsAway = 3,
          xgHome = 1.95, xgAway = 1.45, ballInPlayTime = "58:30"
        ),
        aiPrediction = AiPrediction(
          homeWinProb = 52, drawProb = 26, awayWinProb = 22, predictedScore = "2 - 1", confidence = 87,
          xgSummary = "Galatasaray iç sahada 1.95 xG ile yüksek baskı kuruyor.",
          tacticalAnalysis = "Osimhen ve Džeko arasındaki gol düellosu devam ediyor.",
          formRatingHome = "G-G-G-B-G", formRatingAway = "G-G-B-G-G"
        ),
        events = listOf(
          MatchEvent("m1_ev1", 34, EventType.GOAL, "Mauro Icardi", "Galatasaray", "Ceza sahası köşesinden müthiş plase gol! ⚽"),
          MatchEvent("m1_ev2", 52, EventType.GOAL, "Edin Džeko", "Fenerbahçe", "Kafa vuruşuyla beraberlik golü! ⚽"),
          MatchEvent("m1_ev3", 65, EventType.GOAL, "Victor Osimhen", "Galatasaray", "Savunma arkasına koşu ve sert şutla 2-1! ⚽")
        ),
        startTime = "19:00",
        isHot = true,
        stadium = "Rams Park (52.280)",
        tvBroadcast = "beIN SPORTS 1",
        week = 1,
        matchDate = "${todayIso} 19:00 TSİ",
        matchDateIso = todayIso,
        iddaaCode = "10001",
        mbs = 1,
        isKralOran = true,
        popularBetPercentage = 95
      )
    )

    // Derbi 2: Real Madrid - Barcelona (El Clásico - 22:00 TSİ)
    val m2Markets = generateNesineMarkets("m_feat_2", 2.20, 3.50, 2.75, 1.95, 1.65, 1.50, 2.25)
    list.add(
      Match(
        id = "m_feat_2",
        sport = Sport.FOOTBALL,
        league = "La Liga",
        homeTeam = "Real Madrid",
        awayTeam = "Barcelona",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.SCHEDULED,
        markets = m2Markets,
        statistics = MatchStatistics(possessionHome = 51, possessionAway = 49),
        aiPrediction = AiPrediction(
          homeWinProb = 45, drawProb = 26, awayWinProb = 29, predictedScore = "3 - 2", confidence = 88,
          xgSummary = "Mbappé ve Vinicius kontralarda yüksek xG yaratıyor.",
          tacticalAnalysis = "Yamal ve Lewandowski kanat organizasyonlarıyla tehlikeli.",
          formRatingHome = "G-G-G-G-M", formRatingAway = "G-G-G-G-G"
        ),
        events = emptyList(),
        startTime = "22:00",
        isHot = true,
        stadium = "Santiago Bernabéu (81.044)",
        tvBroadcast = "S Sport",
        week = 1,
        matchDate = "${todayIso} 22:00 TSİ",
        matchDateIso = todayIso,
        iddaaCode = "10002",
        mbs = 1,
        isKralOran = true,
        popularBetPercentage = 92
      )
    )

    // Derbi 3: Arsenal - Manchester City (Premier League - 17:30 TSİ)
    val m3Markets = generateNesineMarkets("m_feat_3", 2.45, 3.25, 2.55, 1.80, 1.80, 1.68, 1.95)
    list.add(
      Match(
        id = "m_feat_3",
        sport = Sport.FOOTBALL,
        league = "Premier League",
        homeTeam = "Arsenal",
        awayTeam = "Manchester City",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.SCHEDULED,
        markets = m3Markets,
        aiPrediction = AiPrediction(
          homeWinProb = 38, drawProb = 30, awayWinProb = 32, predictedScore = "1 - 1", confidence = 82,
          xgSummary = "Arsenal Emirates'te savunma bloğunu derinde tutacak.",
          tacticalAnalysis = "Haaland ve De Bruyne kilit açma peşinde.",
          formRatingHome = "G-G-B-G-G", formRatingAway = "G-G-G-B-G"
        ),
        startTime = "17:30",
        isHot = true,
        stadium = "Emirates Stadium (60.704)",
        tvBroadcast = "beIN SPORTS 3",
        week = 1,
        matchDate = "${todayIso} 17:30 TSİ",
        matchDateIso = todayIso,
        iddaaCode = "10003",
        mbs = 1,
        isKralOran = true,
        popularBetPercentage = 89
      )
    )

    // Derbi 4: Beşiktaş - Trabzonspor (Trendyol Süper Lig - 19:00 TSİ)
    val m5Markets = generateNesineMarkets("m_feat_5", 1.95, 3.40, 3.20, 1.72, 1.88, 1.62, 2.05)
    list.add(
      Match(
        id = "m_feat_5",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Beşiktaş",
        awayTeam = "Trabzonspor",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.SCHEDULED,
        markets = m5Markets,
        statistics = MatchStatistics(possessionHome = 53, possessionAway = 47),
        aiPrediction = AiPrediction(
          homeWinProb = 46, drawProb = 28, awayWinProb = 26, predictedScore = "2 - 1", confidence = 84,
          xgSummary = "Beşiktaş Tüpraş Stadyumu'nda Rafa Silva ve Immobile ile hücumda üretken.",
          tacticalAnalysis = "Trabzonspor geçiş oyununda Visca ve Banza ile tehdit oluşturuyor.",
          formRatingHome = "G-G-G-B-G", formRatingAway = "B-B-B-G-B"
        ),
        startTime = "19:00",
        isHot = true,
        stadium = "Tüpraş Stadyumu (42.590)",
        tvBroadcast = "beIN SPORTS 1",
        week = 1,
        matchDate = "${todayIso} 19:00 TSİ",
        matchDateIso = todayIso,
        iddaaCode = "10008",
        mbs = 1,
        isKralOran = true,
        popularBetPercentage = 87
      )
    )

    // Motor Sporları 1: MotoGP Misano Sprint & Sıralama H2H (14:30 TSİ -> Şu an CANLI!)
    val motoGpMarkets = listOf(
      Market(
        id = "m_feat_motogp_h2h",
        type = MarketType.MOTORSPORTS_WINNER,
        name = "Yarış Kazananı (H2H)",
        selections = listOf(
          Selection("mgp_1", "m_feat_motogp_h2h", "Francesco Bagnaia", 1.85),
          Selection("mgp_2", "m_feat_motogp_h2h", "Jorge Martin", 1.95)
        )
      )
    )
    list.add(
      Match(
        id = "m_feat_motogp",
        sport = Sport.MOTORSPORTS,
        league = "MotoGP Grand Prix",
        homeTeam = "Francesco Bagnaia (Ducati)",
        awayTeam = "Jorge Martin (Pramac)",
        homeScore = 25,
        awayScore = 20,
        minute = 25,
        status = MatchStatus.FINISHED,
        markets = motoGpMarkets,
        statistics = MatchStatistics(possessionHome = 60, possessionAway = 40, shotsHome = 4, shotsAway = 3),
        aiPrediction = AiPrediction(
          homeWinProb = 52, drawProb = 0, awayWinProb = 48, predictedScore = "P1: Bagnaia (25P)", confidence = 81,
          xgSummary = "Bagnaia sıralama turlarında en hızlı tur zamanını yaptı.",
          tacticalAnalysis = "Martin son viraj çıkışında yüksek çekiş avantajına sahip.",
          formRatingHome = "1-2-1-1-3", formRatingAway = "2-1-2-2-1"
        ),
        events = listOf(
          MatchEvent("mge_1", 12, EventType.SHOT_ON_TARGET, "Marc Marquez", "Gresini", "İçten müthiş atakla 3. sıraya yükseldi! 🏎️")
        ),
        startTime = "15:00",
        isHot = false,
        stadium = "Misano World Circuit Marco Simoncelli",
        tvBroadcast = "S Sport 2",
        week = 1,
        matchDate = "${todayIso} 15:00 TSİ",
        matchDateIso = todayIso,
        iddaaCode = "10004",
        mbs = 1,
        isKralOran = false,
        popularBetPercentage = 74
      )
    )

    // Motor Sporları 2: WRC Dünya Rallisi Şili (13:10 TSİ -> Bitti)
    val wrcMarkets = listOf(
      Market(
        id = "m_feat_wrc_win",
        type = MarketType.MOTORSPORTS_WINNER,
        name = "Etap Kazananı",
        selections = listOf(
          Selection("wrc_1", "m_feat_wrc_win", "Sébastien Ogier", 2.25),
          Selection("wrc_2", "m_feat_wrc_win", "Thierry Neuville", 2.60)
        )
      )
    )
    list.add(
      Match(
        id = "m_feat_wrc",
        sport = Sport.MOTORSPORTS,
        league = "WRC Dünya Rallisi",
        homeTeam = "Sébastien Ogier (Toyota)",
        awayTeam = "Thierry Neuville (Hyundai)",
        homeScore = 25,
        awayScore = 18,
        minute = 25,
        status = MatchStatus.FINISHED,
        markets = wrcMarkets,
        statistics = MatchStatistics(),
        aiPrediction = AiPrediction(
          homeWinProb = 50, drawProb = 0, awayWinProb = 50, predictedScore = "Puan: 25 - 18", confidence = 80,
          xgSummary = "Ogier çakıl zeminde lastik yönetiminde lider tamamladı.",
          tacticalAnalysis = "Neuville genel klasman liderliğini korudu.",
          formRatingHome = "1-1-2-1-3", formRatingAway = "2-1-3-1-2"
        ),
        events = listOf(
          MatchEvent("wrce_1", 25, EventType.SHOT_ON_TARGET, "Ogier", "Toyota", "Etap tamamlandı, Ogier lider (25 Puan)! 🏁")
        ),
        startTime = "13:10",
        isHot = false,
        stadium = "Rally Chile Biobío Çakıl Etabı",
        tvBroadcast = "Red Bull TV",
        week = 1,
        matchDate = "${todayIso} 13:10 TSİ",
        matchDateIso = todayIso,
        iddaaCode = "10005",
        mbs = 1,
        isKralOran = false,
        popularBetPercentage = 68
      )
    )

    // Basketbol Derbisi: Fenerbahçe Beko - Anadolu Efes (EuroLeague / Süper Lig - CANLI DERBİ!)
    val euroMarkets = generateBasketballMarkets("m_feat_euro", 1.82, 1.88, 164.5)
    list.add(
      Match(
        id = "m_feat_euro",
        sport = Sport.BASKETBALL,
        league = "EuroLeague Derbisi",
        homeTeam = "Fenerbahçe Beko",
        awayTeam = "Anadolu Efes",
        homeScore = 74,
        awayScore = 71,
        minute = 32,
        status = MatchStatus.LIVE,
        markets = euroMarkets,
        statistics = MatchStatistics(shotsHome = 28, shotsAway = 26),
        aiPrediction = AiPrediction(
          homeWinProb = 53, drawProb = 0, awayWinProb = 47, predictedScore = "86 - 82", confidence = 84,
          xgSummary = "Fenerbahçe Beko 3. çeyrekte Hayes-Davis üçlükleriyle öne geçti.",
          tacticalAnalysis = "Larkin ve Thompson ikili oyunlarla potaya yükleniyor.",
          formRatingHome = "G-G-G-M-G", formRatingAway = "G-G-M-G-G"
        ),
        events = listOf(
          MatchEvent("b_ev1", 28, EventType.SHOT_ON_TARGET, "Nigel Hayes-Davis", "Fenerbahçe Beko", "Kritik köşe üçlüğüyle takımını öne geçirdi! 🏀"),
          MatchEvent("b_ev2", 30, EventType.SHOT_ON_TARGET, "Shane Larkin", "Anadolu Efes", "Hızlı hücumda turnike isabeti! 🏀")
        ),
        startTime = "20:15",
        isHot = true,
        stadium = "Ülker Spor ve Etkinlik Salonu (13.059)",
        tvBroadcast = "S Sport",
        week = 1,
        matchDate = "${todayIso} 20:15 TSİ",
        matchDateIso = todayIso,
        iddaaCode = "10006",
        mbs = 1,
        isKralOran = true,
        popularBetPercentage = 88
      )
    )

    // NBA: Boston Celtics - Los Angeles Lakers (02:30 TSİ Gece -> Bitti)
    val nbaMarkets = generateBasketballMarkets("m_feat_nba", 1.55, 2.45, 224.5)
    list.add(
      Match(
        id = "m_feat_nba",
        sport = Sport.BASKETBALL,
        league = "NBA",
        homeTeam = "Boston Celtics",
        awayTeam = "Los Angeles Lakers",
        homeScore = 118,
        awayScore = 112,
        minute = 40,
        status = MatchStatus.FINISHED,
        markets = nbaMarkets,
        statistics = MatchStatistics(
          shotsHome = 45, shotsAway = 42
        ),
        aiPrediction = AiPrediction(
          homeWinProb = 62, drawProb = 0, awayWinProb = 38, predictedScore = "118 - 112", confidence = 86,
          xgSummary = "Tatum ve Brown ikilisi 58 sayı üretti.",
          tacticalAnalysis = "Lakers dış şutlarda yüzde 31'de kaldı.",
          formRatingHome = "G-G-G-G-M", formRatingAway = "G-M-G-G-M"
        ),
        startTime = "02:30",
        isHot = false,
        stadium = "TD Garden (19.156)",
        tvBroadcast = "NBA TV / S Sport Plus",
        week = 1,
        matchDate = "${todayIso} 02:30 TSİ",
        matchDateIso = todayIso,
        iddaaCode = "10007",
        mbs = 1,
        isKralOran = false,
        popularBetPercentage = 82
      )
    )

    return list
  }

  /**
   * Generates 2000+ matches for 26.09.2026 with no team playing twice.
   */
  private fun generateDaily2000MatchesForSept26(alreadyScheduled: List<Match>): List<Match> {
    val bookedTeams = HashSet<String>(4500)
    alreadyScheduled.forEach {
      bookedTeams.add(it.homeTeam)
      bookedTeams.add(it.awayTeam)
    }

    // Build unique matchup pool
    val allUniqueMatchups = ArrayList<GeneratedMatchData>(2200)

    // 1. Football Unique Matchups
    allUniqueMatchups.addAll(buildFootballMatchups(bookedTeams))

    // 2. Basketball Unique Matchups
    allUniqueMatchups.addAll(buildBasketballMatchups(bookedTeams))

    // 3. Motorsports & Rally Unique Matchups
    allUniqueMatchups.addAll(buildMotorsportMatchups(bookedTeams))

    // 4. Tennis Unique Matchups
    allUniqueMatchups.addAll(buildTennisMatchups(bookedTeams))

    // 5. Table Tennis (Masa Tenisi) Unique Matchups
    allUniqueMatchups.addAll(buildTableTennisMatchups(bookedTeams))

    // 6. Volleyball Unique Matchups
    allUniqueMatchups.addAll(buildVolleyballMatchups(bookedTeams))

    // 7. Ice Hockey Unique Matchups
    allUniqueMatchups.addAll(buildIceHockeyMatchups(bookedTeams))

    // 8. Esports Unique Matchups
    allUniqueMatchups.addAll(buildEsportsMatchups(bookedTeams))

    // 9. Handball, Baseball, Snooker & Darts Unique Matchups
    allUniqueMatchups.addAll(buildOtherSportsMatchups(bookedTeams))

    val matches = ArrayList<Match>(allUniqueMatchups.size)
    var iddaaCounter = 10010
    val totalMatches = allUniqueMatchups.size

    // Simulated reference time: 15:30 TSİ on 26.09.2026 (930 minutes into the day)
    val simulatedCurrentMinuteOfDay = 15 * 60 + 30

    for (index in 0 until totalMatches) {
      val data = allUniqueMatchups[index]

      // Distribute kickoff times from 00:00 to 23:55 (every 5-10 minutes)
      val minuteOfDay = (index * 1435 / totalMatches)
      val hour = minuteOfDay / 60
      val minute = (minuteOfDay % 60 / 5) * 5 // Align to 5-minute ticks
      val timeStr = String.format(Locale.ROOT, "%02d:%02d", hour, minute)
      val kickoffMinuteOfDay = hour * 60 + minute

      val matchId = "m_20260926_${index + 1}"
      val iddaaCode = (iddaaCounter++).toString()

      // Calculate status and minute accurately according to time difference
      val elapsedMinutes = simulatedCurrentMinuteOfDay - kickoffMinuteOfDay
      val status: MatchStatus
      val matchMinute: Int
      val homeScore: Int
      val awayScore: Int
      val isLive: Boolean

      // Precise match timing and status calculation
      val maxRegulationMinutes = when (data.sport) {
        Sport.FOOTBALL -> 90
        Sport.BASKETBALL -> 40
        Sport.MOTORSPORTS -> 25
        Sport.TENNIS -> 90
        Sport.ICE_HOCKEY -> 60
        Sport.HANDBALL -> 60
        Sport.VOLLEYBALL -> 60
        else -> 45
      }

      val maxStoppageMinutes = if (data.sport == Sport.FOOTBALL) 6 else 0
      val totalLiveDuration = maxRegulationMinutes + maxStoppageMinutes

      if (elapsedMinutes < 0) {
        // Match hasn't started yet
        status = MatchStatus.SCHEDULED
        matchMinute = 0
        homeScore = 0
        awayScore = 0
        isLive = false
      } else if (elapsedMinutes <= totalLiveDuration) {
        // Match is LIVE in-play right now
        status = MatchStatus.LIVE
        matchMinute = elapsedMinutes.coerceIn(1, maxRegulationMinutes)
        isLive = true
        when (data.sport) {
          Sport.BASKETBALL -> {
            homeScore = 40 + (matchMinute * 1.5).roundToInt() + (index % 7)
            awayScore = 38 + (matchMinute * 1.4).roundToInt() + (index % 5)
          }
          Sport.MOTORSPORTS -> {
            // Official FIA/FIM Points System (MotoGP: 25-20-16, WRC: 25-18-15, F1: 25-18-15)
            val isWrc = data.league.contains("WRC", ignoreCase = true)
            val isMotoGp = data.league.contains("MotoGP", ignoreCase = true)
            if (isWrc) {
              homeScore = if (index % 2 == 0) 18 else 15
              awayScore = if (index % 2 == 0) 15 else 13
            } else if (isMotoGp) {
              homeScore = if (index % 2 == 0) 25 else 20
              awayScore = if (index % 2 == 0) 20 else 16
            } else {
              homeScore = if (index % 2 == 0) 25 else 18
              awayScore = if (index % 2 == 0) 18 else 15
            }
          }
          Sport.TENNIS -> {
            homeScore = if (matchMinute > 45) 1 else 0
            awayScore = if (matchMinute > 75 && index % 2 == 0) 1 else 0
          }
          Sport.VOLLEYBALL -> {
            homeScore = if (matchMinute > 40) 2 else (if (matchMinute > 20) 1 else 0)
            awayScore = if (matchMinute > 30) 1 else 0
          }
          Sport.ICE_HOCKEY -> {
            homeScore = (1 + (index % 3)).coerceAtLeast(1)
            awayScore = (index % 2)
          }
          Sport.HANDBALL -> {
            homeScore = 14 + (matchMinute / 3) + (index % 4)
            awayScore = 12 + (matchMinute / 3) + (index % 3)
          }
          else -> {
            homeScore = if (index % 3 == 0) 1 else 0
            awayScore = if (index % 5 == 0) 1 else 0
          }
        }
      } else {
        // Match is strictly FINISHED (Bitti / MS) - never still playing!
        status = MatchStatus.FINISHED
        matchMinute = maxRegulationMinutes
        isLive = false
        when (data.sport) {
          Sport.BASKETBALL -> {
            homeScore = 82 + (index % 25)
            awayScore = 79 + ((index * 3) % 24)
          }
          Sport.MOTORSPORTS -> {
            // Official FIA / FIM points awarded in race classification
            val isWrc = data.league.contains("WRC", ignoreCase = true)
            val isMotoGp = data.league.contains("MotoGP", ignoreCase = true)
            if (isWrc) {
              homeScore = if (index % 2 == 0) 25 else 18
              awayScore = if (index % 2 == 0) 18 else 15
            } else if (isMotoGp) {
              homeScore = if (index % 2 == 0) 25 else 20
              awayScore = if (index % 2 == 0) 20 else 16
            } else {
              homeScore = if (index % 2 == 0) 25 else 18
              awayScore = if (index % 2 == 0) 18 else 15
            }
          }
          Sport.TENNIS -> {
            // Tennis sets: Best of 3 sets: 2-0, 2-1
            homeScore = if (index % 2 == 0) 2 else (if (index % 3 == 0) 1 else 0)
            awayScore = if (homeScore == 2) (if (index % 3 == 0) 1 else 0) else 2
          }
          Sport.VOLLEYBALL -> {
            // Volleyball sets: Best of 5 sets: 3-0, 3-1, 3-2
            homeScore = if (index % 2 == 0) 3 else (if (index % 3 == 0) 1 else 2)
            awayScore = if (homeScore == 3) (index % 3) else 3
          }
          Sport.ICE_HOCKEY -> {
            val base = 2 + (index % 3)
            homeScore = if (index % 2 == 0) base + 1 else base
            awayScore = if (index % 3 == 0) base else (base - 1).coerceAtLeast(0)
          }
          Sport.HANDBALL -> {
            homeScore = 28 + (index % 7)
            awayScore = 26 + ((index * 2) % 6)
          }
          else -> {
            val baseGoals = (index % 3)
            homeScore = if (index % 2 == 0) baseGoals + 1 else baseGoals
            awayScore = if (index % 4 == 0) baseGoals else (baseGoals - 1).coerceAtLeast(0)
          }
        }
      }

      val markets = createMarketsForSport(matchId, data.sport, isLive, index)
      val isKral = (index % 11 == 0)
      val mbs = if (isKral || index % 6 == 0) 1 else 2
      val popularPercent = 35 + (index * 17 % 55)

      val shotsH = if (status != MatchStatus.SCHEDULED) 4 + (index % 8) else 0
      val shotsA = if (status != MatchStatus.SCHEDULED) 3 + (index % 7) else 0
      val shotsOnTargetH = if (status != MatchStatus.SCHEDULED) ((shotsH * 0.45).toInt()).coerceAtLeast(1) else 0
      val shotsOnTargetA = if (status != MatchStatus.SCHEDULED) ((shotsA * 0.40).toInt()).coerceAtLeast(1) else 0

      val calcXgHome = if (status == MatchStatus.SCHEDULED) {
        roundOdd(1.10 + (index % 9) * 0.12)
      } else {
        roundOdd((homeScore * 0.65 + shotsOnTargetH * 0.18 + shotsH * 0.05 + 0.28).coerceIn(0.55, 3.85))
      }
      val calcXgAway = if (status == MatchStatus.SCHEDULED) {
        roundOdd(0.95 + (index % 7) * 0.14)
      } else {
        roundOdd((awayScore * 0.60 + shotsOnTargetA * 0.17 + shotsA * 0.04 + 0.22).coerceIn(0.40, 3.45))
      }

      matches.add(
        Match(
          id = matchId,
          sport = data.sport,
          league = data.league,
          homeTeam = data.homeTeam,
          awayTeam = data.awayTeam,
          homeScore = homeScore,
          awayScore = awayScore,
          minute = matchMinute,
          status = status,
          markets = markets,
          statistics = MatchStatistics(
            possessionHome = 50 + (index % 13 - 6),
            possessionAway = 50 - (index % 13 - 6),
            shotsHome = shotsH,
            shotsAway = shotsA,
            shotsOnTargetHome = shotsOnTargetH,
            shotsOnTargetAway = shotsOnTargetA,
            xgHome = calcXgHome,
            xgAway = calcXgAway
          ),
          aiPrediction = AiPrediction(
            homeWinProb = 35 + (index % 25),
            drawProb = 20 + (index % 15),
            awayWinProb = 25 + (index % 20),
            predictedScore = if (data.sport == Sport.BASKETBALL) "86 - 81" else "2 - 1",
            confidence = 72 + (index % 22),
            xgSummary = "AI Modellemesi: ${data.homeTeam} oran dengesi.",
            tacticalAnalysis = "${data.league} karşılaşması.",
            formRatingHome = "G-B-G-M-G",
            formRatingAway = "M-G-B-G-M"
          ),
          events = if (isLive && homeScore > 0) {
            listOf(MatchEvent("ev_${matchId}", matchMinute.coerceAtLeast(10), EventType.GOAL, data.homeTeam, getScorerForTeam(data.homeTeam), "Gol! ⚽"))
          } else emptyList(),
          startTime = timeStr,
          isHot = isKral,
          stadium = GlobalSportsDatabase.stadiumMap[data.homeTeam] ?: "${data.homeTeam} Stadyumu",
          tvBroadcast = data.broadcast,
          week = 1,
          matchDate = "${todayIso} $timeStr TSİ",
          matchDateIso = todayIso,
          iddaaCode = iddaaCode,
          mbs = mbs,
          isKralOran = isKral,
          popularBetPercentage = popularPercent
        )
      )
    }

    return matches
  }

  // --- BUILD UNIQUE MATCHUPS HELPERS ---

  private data class GeneratedMatchData(
    val sport: Sport,
    val league: String,
    val homeTeam: String,
    val awayTeam: String,
    val broadcast: String = "beIN SPORTS"
  )

  private fun pairUp(
    sport: Sport,
    league: String,
    teams: List<String>,
    channel: String,
    booked: MutableSet<String>,
    out: MutableList<GeneratedMatchData>
  ) {
    val avail = teams.filterNot { booked.contains(it) }.toMutableList()
    while (avail.size >= 2) {
      val h = avail.removeAt(0)
      val a = avail.removeAt(0)
      booked.add(h)
      booked.add(a)
      out.add(GeneratedMatchData(sport, league, h, a, channel))
    }
  }

  private fun buildFootballMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()

    // Turkey
    pairUp(Sport.FOOTBALL, "Trendyol Süper Lig", GlobalSportsDatabase.superLigTeams, "beIN SPORTS 1", booked, list)
    pairUp(Sport.FOOTBALL, "Trendyol 1. Lig", GlobalSportsDatabase.tff1LigTeams, "TRT Spor", booked, list)
    pairUp(Sport.FOOTBALL, "TFF 2. Lig Kırmızı", GlobalSportsDatabase.tff2LigKirmiziTeams, "TFF Canlı", booked, list)
    pairUp(Sport.FOOTBALL, "TFF 2. Lig Beyaz", GlobalSportsDatabase.tff2LigBeyazTeams, "TFF Canlı", booked, list)
    pairUp(Sport.FOOTBALL, "TFF 3. Lig", GlobalSportsDatabase.tff3LigTeams, "TFF TV", booked, list)
    pairUp(Sport.FOOTBALL, "TFF 3. Lig Ekstra", GlobalExpandedSportsData.tff3LigExpanded, "TFF TV", booked, list)
    pairUp(Sport.FOOTBALL, "Bölgesel Amatör Lig (BAL)", GlobalExpandedSportsData.balTeams, "Yerel TV", booked, list)
    pairUp(Sport.FOOTBALL, "U19 Elit Gelişim Ligi", GlobalExpandedSportsData.u19ElitTeams, "Kulüp TV", booked, list)

    // England
    pairUp(Sport.FOOTBALL, "Premier League", GlobalSportsDatabase.premierLeagueTeams, "beIN SPORTS 3", booked, list)
    pairUp(Sport.FOOTBALL, "Championship", GlobalSportsDatabase.championshipTeams, "beIN Connect", booked, list)
    pairUp(Sport.FOOTBALL, "League One", GlobalSportsDatabase.leagueOneTeams, "Sky Sports", booked, list)
    pairUp(Sport.FOOTBALL, "League Two", GlobalExpandedSportsData.leagueTwoTeams, "Sky Sports+", booked, list)
    pairUp(Sport.FOOTBALL, "National League", GlobalExpandedSportsData.nationalLeagueTeams, "National League TV", booked, list)
    pairUp(Sport.FOOTBALL, "National League Kuzey/Güney", GlobalExpandedSportsData.nationalNorthSouthTeams, "DAZN UK", booked, list)

    // Spain
    pairUp(Sport.FOOTBALL, "La Liga", GlobalSportsDatabase.laLigaTeams, "S Sport", booked, list)
    pairUp(Sport.FOOTBALL, "Segunda Division", GlobalSportsDatabase.segundaTeams, "LaLiga TV Hypermotion", booked, list)
    pairUp(Sport.FOOTBALL, "Primera Federación", GlobalExpandedSportsData.primeraRfefTeams, "FEF TV", booked, list)

    // Italy
    pairUp(Sport.FOOTBALL, "Serie A", GlobalSportsDatabase.serieATeams, "S Sport Plus", booked, list)
    pairUp(Sport.FOOTBALL, "Serie B", GlobalSportsDatabase.serieBTeams, "DAZN Italia", booked, list)
    pairUp(Sport.FOOTBALL, "Serie C", GlobalExpandedSportsData.serieCTeams, "Sky Sport Calcio", booked, list)

    // Germany
    pairUp(Sport.FOOTBALL, "Bundesliga", GlobalSportsDatabase.bundesligaTeams, "Tivibu Spor 1", booked, list)
    pairUp(Sport.FOOTBALL, "2. Bundesliga", GlobalSportsDatabase.bundesliga2Teams, "Tivibu Spor 2", booked, list)
    pairUp(Sport.FOOTBALL, "3. Liga", GlobalExpandedSportsData.liga3GermanTeams, "Magenta Sport", booked, list)
    pairUp(Sport.FOOTBALL, "Regionalliga", GlobalExpandedSportsData.regionalligaTeams, "Leagues TV", booked, list)

    // France
    pairUp(Sport.FOOTBALL, "Ligue 1", GlobalSportsDatabase.ligue1Teams, "beIN SPORTS 4", booked, list)

    // Portugal & Netherlands
    pairUp(Sport.FOOTBALL, "Eredivisie", GlobalSportsDatabase.eredivisieTeams, "ESPN NL", booked, list)
    pairUp(Sport.FOOTBALL, "Eerste Divisie", GlobalExpandedSportsData.eredivisieEersteDivisie, "ESPN 2", booked, list)
    pairUp(Sport.FOOTBALL, "Liga Portugal", GlobalSportsDatabase.ligaPortugalTeams, "Sport TV", booked, list)
    pairUp(Sport.FOOTBALL, "Liga 2 Portugal", GlobalExpandedSportsData.liga2PortugalTeams, "Canal 11", booked, list)

    // Rest of Europe
    pairUp(Sport.FOOTBALL, "Belçika Pro League", GlobalSportsDatabase.belgiumProTeams, "Eleven Sports", booked, list)
    pairUp(Sport.FOOTBALL, "İskoçya Premiership", GlobalSportsDatabase.scotlandPremiershipTeams, "Sky Sports", booked, list)
    pairUp(Sport.FOOTBALL, "Avusturya & İsviçre Ligi", GlobalExpandedSportsData.austriaSwissTeams, "Sky Sport Austria", booked, list)
    pairUp(Sport.FOOTBALL, "İskandinavya Ligi", GlobalExpandedSportsData.scandinavianTeams, "Viaplay", booked, list)
    pairUp(Sport.FOOTBALL, "Doğu Avrupa Ligi", GlobalExpandedSportsData.easternEuropeTeams, "Arena Sport", booked, list)

    // Americas & Asia
    pairUp(Sport.FOOTBALL, "Brezilya Serie A", GlobalSportsDatabase.brazilSerieATeams, "SporTV", booked, list)
    pairUp(Sport.FOOTBALL, "Arjantin Primera", GlobalSportsDatabase.argentinaPrimeraTeams, "TyC Sports", booked, list)
    pairUp(Sport.FOOTBALL, "Güney Amerika & Meksika", GlobalExpandedSportsData.southAmericaTeams, "TUDN / DirecTV", booked, list)
    pairUp(Sport.FOOTBALL, "Suudi Pro League", GlobalSportsDatabase.saudiProTeams, "SSC", booked, list)
    pairUp(Sport.FOOTBALL, "MLS", GlobalSportsDatabase.mlsTeams, "Apple TV", booked, list)
    pairUp(Sport.FOOTBALL, "J-League", GlobalSportsDatabase.jLeagueTeams, "NHK BS", booked, list)
    pairUp(Sport.FOOTBALL, "Avustralya A-League", GlobalSportsDatabase.aLeagueTeams, "Paramount+", booked, list)

    // Authentic Turkish Regional & Lower League Matchups (TFF 2. Lig & 3. Lig & BAL)
    val authenticRegionalClubs = listOf(
      "Karşıyaka", "Bursaspor", "Batman Petrolspor", "Kastamonuspor", "İskenderunspor", "Sarıyer",
      "Vanspor FK", "1461 Trabzon", "Altınordu", "Ankaraspor", "Erbaaspor", "Kuşadasıspor",
      "Silivrispor", "Düzcespor", "Çorluspor 1947", "Karaman FK", "Beyoğlu Yeni Çarşı", "Somaspor",
      "Nazilli Belediyespor", "Fatsa Belediyespor", "Amasyaspor FK", "Aliağa Futbol", "Muşspor",
      "Artvin Hopaspor", "Kırşehir FSK", "Bornova 1877", "Anadolu Üniversitesi", "Ayvalıkgücü Bld",
      "Edirnespor", "Karabük İdmanyurdu", "Pazarspor", "Yozgat Bozokspor", "52 Orduspor FK",
      "Küçükçekmece Sinop", "Bayburt Özel İdare", "Efeler 09 SFK", "Sebat Gençlikspor", "Ergene Velimeşe",
      "Bulvarspor", "Büyükçekmece Tepecik", "Çatalcaspor", "Gümüşhanespor", "Hacettepe 1945",
      "Karaköprü Bld", "Kelkit Hürriyetspor", "Kırıkkalegücü", "Mardin 1969", "Sapanca Gençlik",
      "Siirt İl Özel İdare", "Sivas Dört Eylül", "Sultanbeyli Bld", "Talasgücü Bld", "Tarsus İdman Yurdu",
      "Bucaspor 1928", "Menemen FK", "Motolux 68 Aksarayspor", "Isparta 32 Spor", "İnegölspor",
      "Fethiyespor", "24Erzincanspor", "Kırklarelispor", "Arnavutköy Bld", "Diyarbekirspor",
      "Afyonspor", "Nazilli Spor", "Giresunspor", "Yeni Mersin İdman", "Altay", "Adana 01 FK",
      "Beykoz Anadoluspor", "Kepezspor", "İnegöl Kafkasspor", "Gölcükspor", "Yalova Yeşilova",
      "Kapaklıspor", "Tekirdağspor", "Lüleburgazspor", "Kullar 1975 Spor", "Diliskelesispor",
      "Aliağa FAŞ", "İzmirspor", "Bornova Yeşilovaspor", "Manisa 1965 SK", "Turgutluspor",
      "Sökespor", "Didim Belediyespor", "Muğlaspor", "Yatağanspor", "Burdur MAKÜ",
      "Kumluca Belediyespor", "Manavgat Belediyespor", "Eskişehir Yunusemrespor", "Polatlı Belediyespor",
      "Bartınspor", "Kdz. Ereğli Bld", "Çarşambaspor", "Ünye 1957 Spor", "1926 Bulancakspor",
      "Kars 36 Spor", "Ağrı 1970 Spor", "Iğdır Arasspor", "Cizrespor", "Nusaybin Demirspor",
      "Adıyaman FK", "Kahta 02 Spor", "Kilis Belediyespor", "Silifke Belediyespor", "Anamur Bld"
    )

    val shuffledRegional = authenticRegionalClubs.filterNot { booked.contains(it) }
    for (i in 0 until shuffledRegional.size - 1 step 2) {
      val h = shuffledRegional[i]
      val a = shuffledRegional[i + 1]
      booked.add(h)
      booked.add(a)
      val leagueName = when ((i / 2) % 6) {
        0 -> "Nesine 2. Lig Kırmızı Grup"
        1 -> "Nesine 2. Lig Beyaz Grup"
        2 -> "Nesine 3. Lig 1. Grup"
        3 -> "Nesine 3. Lig 2. Grup"
        4 -> "Nesine 3. Lig 3. Grup"
        else -> "Bölgesel Amatör Lig (BAL)"
      }
      list.add(GeneratedMatchData(Sport.FOOTBALL, leagueName, h, a, "Nesine TV"))
    }

    return list
  }

  private fun buildBasketballMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()

    pairUp(Sport.BASKETBALL, "NBA", GlobalSportsDatabase.nbaTeams, "NBA TV", booked, list)
    pairUp(Sport.BASKETBALL, "EuroLeague", GlobalSportsDatabase.euroLeagueTeams, "S Sport", booked, list)
    pairUp(Sport.BASKETBALL, "Basketbol Süper Ligi", GlobalSportsDatabase.bslTeams, "beIN SPORTS 5", booked, list)
    pairUp(Sport.BASKETBALL, "TBL 1. Lig", GlobalSportsDatabase.tblTeams, "TRT Spor Yıldız", booked, list)
    pairUp(Sport.BASKETBALL, "İspanya Liga ACB", GlobalSportsDatabase.acbTeams, "Movistar Deportes", booked, list)
    pairUp(Sport.BASKETBALL, "EuroCup Basketball", GlobalExpandedSportsData.euroCupBasketballTeams, "Euroleague TV", booked, list)

    // Distinct NCAA Division Conference Colleges
    val ncaaConferences = mapOf(
      "NCAA Big Ten" to listOf("Michigan Wolverines", "Ohio State Buckeyes", "Purdue Boilermakers", "Indiana Hoosiers", "Wisconsin Badgers", "Illinois Fighting Illini", "Michigan State Spartans", "Iowa Hawkeyes"),
      "NCAA SEC" to listOf("Kentucky Wildcats", "Alabama Crimson Tide", "Auburn Tigers", "Florida Gators", "Tennessee Volunteers", "Arkansas Razorbacks", "Texas A&M Aggies", "LSU Tigers"),
      "NCAA ACC" to listOf("Duke Blue Devils", "North Carolina Tar Heels", "Virginia Cavaliers", "Clemson Tigers", "Louisville Cardinals", "Miami Hurricanes", "Syracuse Orange", "NC State Wolfpack"),
      "NCAA Big 12" to listOf("Kansas Jayhawks", "Baylor Bears", "Houston Cougars", "Texas Tech Red Raiders", "Iowa State Cyclones", "Arizona Wildcats", "BYU Cougars", "Kansas State Wildcats"),
      "NCAA Big East" to listOf("UConn Huskies", "Marquette Golden Eagles", "Villanova Wildcats", "Creighton Bluejays", "St. John's Red Storm", "Providence Friars", "Seton Hall Pirates", "Xavier Musketeers"),
      "NCAA West Coast" to listOf("Gonzaga Bulldogs", "Saint Mary's Gaels", "San Diego State Aztecs", "UCLA Bruins", "Arizona State Sun Devils", "Oregon Ducks", "Colorado Buffaloes", "Washington Huskies")
    )

    for ((conference, colleges) in ncaaConferences) {
      pairUp(Sport.BASKETBALL, conference, colleges, "ESPN+", booked, list)
    }

    return list
  }

  private fun buildMotorsportMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()

    // MotoGP Matchups
    val motoGp = GlobalSportsDatabase.motoGpRacers.filterNot { booked.contains(it) }
    for (i in 0 until motoGp.size - 1 step 2) {
      val h = motoGp[i]
      val a = motoGp[i + 1]
      booked.add(h)
      booked.add(a)
      list.add(GeneratedMatchData(Sport.MOTORSPORTS, "MotoGP Grand Prix", h, a, "S Sport 2"))
    }

    // Moto2 & Moto3
    pairUp(Sport.MOTORSPORTS, "Moto2 & Moto3 Şampiyonası", GlobalExpandedSportsData.moto2Moto3Riders, "S Sport Plus", booked, list)

    // Formula 1 H2H
    val f1 = GlobalSportsDatabase.f1Drivers.filterNot { booked.contains(it) }
    for (i in 0 until f1.size - 1 step 2) {
      val h = f1[i]
      val a = f1[i + 1]
      booked.add(h)
      booked.add(a)
      list.add(GeneratedMatchData(Sport.MOTORSPORTS, "Formula 1 Grand Prix", h, a, "beIN SPORTS 2"))
    }

    // WRC Rally Stages H2H
    val wrc = GlobalSportsDatabase.wrcRallyDrivers.filterNot { booked.contains(it) }
    for (i in 0 until wrc.size - 1 step 2) {
      val h = wrc[i]
      val a = wrc[i + 1]
      booked.add(h)
      booked.add(a)
      list.add(GeneratedMatchData(Sport.MOTORSPORTS, "WRC Dünya Rallisi", h, a, "Red Bull TV"))
    }

    // WRC Stage Specials
    for (stage in GlobalExpandedSportsData.wrcChileStages) {
      val h = "Lider Zaman ($stage)"
      val a = "Rakip Takipçi ($stage)"
      if (!booked.contains(h) && !booked.contains(a)) {
        booked.add(h)
        booked.add(a)
        list.add(GeneratedMatchData(Sport.MOTORSPORTS, "WRC Şili Rallisi", h, a, "Red Bull TV"))
      }
    }

    // NASCAR Cup Series
    pairUp(Sport.MOTORSPORTS, "NASCAR Cup Series", GlobalExpandedSportsData.nascarDrivers, "FOX Sports", booked, list)

    return list
  }

  private fun buildTennisMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()

    pairUp(Sport.TENNIS, "ATP & WTA Tour", GlobalSportsDatabase.tennisPlayers, "Eurosport 1", booked, list)
    pairUp(Sport.TENNIS, "ATP Challenger Tour", GlobalExpandedSportsData.tennisChallengerItfPlayers, "ATP Tour TV", booked, list)

    // Challenger & ITF Tournaments worldwide
    for (tourney in listOf("Orleans Challenger", "Alicante Challenger", "Antalya ITF M25", "Monastir ITF W15", "Tiburon Challenger", "Sharm El Sheikh ITF")) {
      val circuitPlayers = listOf(
        "M. Copil ($tourney)", "S. Caruso ($tourney)", "F. Gaio ($tourney)", "Z. Kolář ($tourney)",
        "A. Vatutin ($tourney)", "J. De Loore ($tourney)", "N. Serdarušić ($tourney)", "D. Masur ($tourney)",
        "F. Baldi ($tourney)", "M. Vrbenský ($tourney)", "C. Heyman ($tourney)", "K. Sultanov ($tourney)",
        "E. Karlovskiy ($tourney)", "S. Diez ($tourney)", "E. Furness ($tourney)", "L. Giustino ($tourney)"
      )
      pairUp(Sport.TENNIS, tourney, circuitPlayers, "Canlı Skor TV", booked, list)
    }

    return list
  }

  private fun buildTableTennisMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()

    // Setka Cup, TT Cup, Win Cup matches (the most prominent live sport in Nesine & Maçkolik)
    pairUp(Sport.TENNIS, "Setka Cup Masa Tenisi", GlobalExpandedSportsData.tableTennisRoster, "Nesine Canlı TV", booked, list)

    val series = listOf("TT Cup Turnuvası", "Win Cup Pro", "Liga Pro Masa Tenisi", "Czech Liga Pro")
    for (ser in series) {
      val names = listOf(
        "J. Dvorak", "P. Svoboda", "M. Novak", "T. Cerny", "V. Prochazka", "L. Kucera",
        "J. Vesely", "P. Horak", "M. Nemec", "T. Marek", "V. Pospisil", "L. Pokorny",
        "O. Hladik", "D. Urban", "K. Jelinek", "R. Kral", "M. Ruzicka", "F. Benes",
        "J. Fiala", "S. Sedlacek", "M. Zeman", "P. Kolar", "T. Navratil", "L. Mach"
      )
      val modified = names.map { "$it ($ser)" }
      pairUp(Sport.TENNIS, ser, modified, "Maçkolik TV", booked, list)
    }

    return list
  }

  private fun buildVolleyballMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()
    pairUp(Sport.VOLLEYBALL, "Vodafone Sultanlar Ligi", GlobalSportsDatabase.sultanlarLigiTeams, "TRT Spor Yıldız", booked, list)
    pairUp(Sport.VOLLEYBALL, "SMS Grup Efeler Ligi", GlobalSportsDatabase.efelerLigiTeams, "TRT Spor", booked, list)
    pairUp(Sport.VOLLEYBALL, "Avrupa & Kadınlar 1. Ligi", GlobalExpandedSportsData.extendedVolleyballTeams, "Volleyball World TV", booked, list)
    return list
  }

  private fun buildIceHockeyMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()
    pairUp(Sport.ICE_HOCKEY, "NHL Buz Hokeyi", GlobalSportsDatabase.nhlTeams, "S Sport Plus", booked, list)
    pairUp(Sport.ICE_HOCKEY, "AHL & Avrupa Hokey Ligi", GlobalExpandedSportsData.ahlEuropeanHockeyTeams, "AHL TV", booked, list)
    return list
  }

  private fun buildEsportsMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()
    pairUp(Sport.ESPORTS, "CS2 Major Şampiyonası", GlobalSportsDatabase.cs2Teams, "Twitch / YouTube", booked, list)
    pairUp(Sport.ESPORTS, "LoL Dünya Şampiyonası", GlobalSportsDatabase.lolTeams, "Riot Games", booked, list)
    pairUp(Sport.ESPORTS, "Dota 2 & Valorant VCT", GlobalExpandedSportsData.extendedEsportsTeams, "Esports TV", booked, list)
    return list
  }

  private fun buildOtherSportsMatchups(booked: MutableSet<String>): List<GeneratedMatchData> {
    val list = mutableListOf<GeneratedMatchData>()

    pairUp(Sport.HANDBALL, "EHF Şampiyonlar Ligi", GlobalSportsDatabase.handballTeams, "EHF TV", booked, list)
    pairUp(Sport.BASEBALL, "MLB Beyzbol", GlobalSportsDatabase.mlbTeams, "MLB.TV", booked, list)
    pairUp(Sport.SNOOKER, "Dünya Snooker Şampiyonası", GlobalSportsDatabase.snookerPlayers, "Eurosport 2", booked, list)
    pairUp(Sport.SNOOKER, "PDC Dünya Dart Şampiyonası", GlobalSportsDatabase.dartsPlayers, "Sky Sports", booked, list)

    // Bilardo (3-Bant Dünya Bilardo Kupası & UMB Masters)
    val billiardsPlayers = listOf(
      "Semih Saygıner", "Torbjörn Blomdahl", "Tayfun Taşdemir", "Dick Jaspers",
      "Frédéric Caudron", "Marco Zanetti", "Murat Naci Çoklu", "Eddy Merckx",
      "Dani Sánchez", "Cho Myung-woo", "Sameh Sidhom", "Martin Horn",
      "Jérémy Bury", "Quyet Chien Tran", "Haeng Jik Kim", "Bao Phuong Vinh"
    )
    pairUp(Sport.BILLIARDS, "3-Bant Dünya Bilardo Kupası", billiardsPlayers, "TRT Spor Yıldız", booked, list)

    // Okey & Kart Oyunları (Türk Zeka & Kahvehane Oyunları, 101 Okey, Briç & Tavla Ligi)
    val okeyCardCompetitors = listOf(
      "Ahmet Usta (Kadıköy 101)", "Mehmet Çavuş (Bostancı 101)",
      "Galatasaray Briç SK", "Fenerbahçe Briç SK",
      "Beşiktaş Briç", "ODTÜ Briç Kulübü",
      "Ali Rıza (İstanbul Tavla Ustaları)", "Cemil Bey (İzmir Tavla Derneği)",
      "Hüseyin Dayı (Karşıyaka Okey)", "Kemal Usta (Alsancak 101)",
      "Boğaziçi King Kulübü", "Çankaya King Kulübü",
      "Sedat Usta (Bursa Düz Okey)", "Orhan Bey (Eskişehir 101)",
      "Metin Hoca (Ankara Batak)", "Selim Reis (Trabzon Tavla)"
    )
    pairUp(Sport.OKEY_CARDS, "101 Okey & Briç Türkiye Kupası", okeyCardCompetitors, "Maçkolik Canlı", booked, list)

    // Masa Tenisi (WTT Dünya Serisi & Grand Smash)
    val tableTennisPlayers = listOf(
      "Fan Zhendong", "Wang Chuqin", "Harimoto Tomokazu", "Truls Moregard",
      "Ma Long", "Hugo Calderano", "Felix Lebrun", "Lin Shidong",
      "Dimitrij Ovtcharov", "Patrick Franziska", "Dang Qiu", "Alexis Lebrun"
    )
    pairUp(Sport.TABLE_TENNIS, "WTT Dünya Masa Tenisi", tableTennisPlayers, "WTT TV", booked, list)

    return list
  }

  // --- MARKET BUILDERS ---

  private fun roundOdd(odd: Double): Double {
    return (odd * 100.0).roundToInt() / 100.0
  }

  fun generateNesineMarkets(
    matchId: String,
    ms1: Double,
    msX: Double,
    ms2: Double,
    alt25: Double,
    ust25: Double,
    kgVar: Double,
    kgYok: Double
  ): List<Market> {
    val msMarket = Market(
      id = "${matchId}_ms",
      type = MarketType.MATCH_RESULT,
      name = "Maç Sonucu",
      selections = listOf(
        Selection("${matchId}_1", "${matchId}_ms", "1", roundOdd(ms1)),
        Selection("${matchId}_x", "${matchId}_ms", "X", roundOdd(msX)),
        Selection("${matchId}_2", "${matchId}_ms", "2", roundOdd(ms2))
      )
    )

    val ouMarket = Market(
      id = "${matchId}_ou25",
      type = MarketType.TOTAL_GOALS_25,
      name = "2.5 Alt / Üst",
      selections = listOf(
        Selection("${matchId}_alt", "${matchId}_ou25", "Alt 2.5", roundOdd(alt25)),
        Selection("${matchId}_ust", "${matchId}_ou25", "Üst 2.5", roundOdd(ust25))
      )
    )

    val ou15Market = Market(
      id = "${matchId}_ou15",
      type = MarketType.TOTAL_GOALS_15,
      name = "1.5 Alt / Üst",
      selections = listOf(
        Selection("${matchId}_alt15", "${matchId}_ou15", "Alt 1.5", roundOdd((alt25 * 1.65).coerceAtMost(3.80))),
        Selection("${matchId}_ust15", "${matchId}_ou15", "Üst 1.5", roundOdd((ust25 * 0.68).coerceAtLeast(1.18)))
      )
    )

    val ou35Market = Market(
      id = "${matchId}_ou35",
      type = MarketType.TOTAL_GOALS_35,
      name = "3.5 Alt / Üst",
      selections = listOf(
        Selection("${matchId}_alt35", "${matchId}_ou35", "Alt 3.5", roundOdd((alt25 * 0.72).coerceAtLeast(1.22))),
        Selection("${matchId}_ust35", "${matchId}_ou35", "Üst 3.5", roundOdd((ust25 * 1.70).coerceAtMost(4.20)))
      )
    )

    val bttsMarket = Market(
      id = "${matchId}_kg",
      type = MarketType.BOTH_TEAMS_SCORE,
      name = "Karşılıklı Gol",
      selections = listOf(
        Selection("${matchId}_kg_v", "${matchId}_kg", "Var", roundOdd(kgVar)),
        Selection("${matchId}_kg_y", "${matchId}_kg", "Yok", roundOdd(kgYok))
      )
    )

    val dc1x = roundOdd(1.0 + (ms1 * msX) / (ms1 + msX) * 0.45)
    val dc12 = roundOdd(1.0 + (ms1 * ms2) / (ms1 + ms2) * 0.42)
    val dcX2 = roundOdd(1.0 + (msX * ms2) / (msX + ms2) * 0.45)

    val dcMarket = Market(
      id = "${matchId}_dc",
      type = MarketType.DOUBLE_CHANCE,
      name = "Çifte Şans",
      selections = listOf(
        Selection("${matchId}_1x", "${matchId}_dc", "1-X", dc1x.coerceAtLeast(1.10)),
        Selection("${matchId}_12", "${matchId}_dc", "1-2", dc12.coerceAtLeast(1.15)),
        Selection("${matchId}_x2", "${matchId}_dc", "X-2", dcX2.coerceAtLeast(1.10))
      )
    )

    val iy1 = roundOdd((ms1 * 1.55).coerceIn(1.70, 5.50))
    val iyX = roundOdd((msX * 0.75).coerceIn(1.85, 2.60))
    val iy2 = roundOdd((ms2 * 1.50).coerceIn(1.75, 6.00))

    val iyMarket = Market(
      id = "${matchId}_iy",
      type = MarketType.FIRST_HALF_RESULT,
      name = "İlk Yarı Sonucu",
      selections = listOf(
        Selection("${matchId}_iy1", "${matchId}_iy", "İY 1", iy1),
        Selection("${matchId}_iyx", "${matchId}_iy", "İY X", iyX),
        Selection("${matchId}_iy2", "${matchId}_iy", "İY 2", iy2)
      )
    )

    val hms1 = roundOdd((ms1 * 1.85).coerceIn(1.90, 7.50))
    val hmsX = roundOdd(3.75)
    val hms2 = roundOdd((ms2 * 0.70).coerceIn(1.25, 3.20))

    val hmsMarket = Market(
      id = "${matchId}_hms",
      type = MarketType.HANDICAP_RESULT,
      name = "Handikaplı Maç Sonucu (-1)",
      selections = listOf(
        Selection("${matchId}_hms1", "${matchId}_hms", "HMS 1", hms1),
        Selection("${matchId}_hmsx", "${matchId}_hms", "HMS X", hmsX),
        Selection("${matchId}_hms2", "${matchId}_hms", "HMS 2", hms2)
      )
    )

    val cornerMarket = Market(
      id = "${matchId}_corners",
      type = MarketType.TOTAL_CORNERS,
      name = "Toplam Korner (9.5)",
      selections = listOf(
        Selection("${matchId}_c_alt", "${matchId}_corners", "Alt 9.5", 1.80),
        Selection("${matchId}_c_ust", "${matchId}_corners", "Üst 9.5", 1.85)
      )
    )

    val cardMarket = Market(
      id = "${matchId}_cards",
      type = MarketType.TOTAL_CARDS,
      name = "Toplam Kart (4.5)",
      selections = listOf(
        Selection("${matchId}_card_alt", "${matchId}_cards", "Alt 4.5", 1.75),
        Selection("${matchId}_card_ust", "${matchId}_cards", "Üst 4.5", 1.90)
      )
    )

    val goalRangeMarket = Market(
      id = "${matchId}_range",
      type = MarketType.TOTAL_GOALS_RANGE,
      name = "Toplam Gol Aralığı",
      selections = listOf(
        Selection("${matchId}_rg_01", "${matchId}_range", "0-1 Gol", 2.95),
        Selection("${matchId}_rg_23", "${matchId}_range", "2-3 Gol", 1.85),
        Selection("${matchId}_rg_45", "${matchId}_range", "4-5 Gol", 3.40),
        Selection("${matchId}_rg_6p", "${matchId}_range", "6+ Gol", 7.50)
      )
    )

    val oddEvenMarket = Market(
      id = "${matchId}_oddeven",
      type = MarketType.ODD_EVEN,
      name = "Tek / Çift",
      selections = listOf(
        Selection("${matchId}_oe_tek", "${matchId}_oddeven", "Tek", 1.85),
        Selection("${matchId}_oe_cift", "${matchId}_oddeven", "Çift", 1.85)
      )
    )

    return listOf(msMarket, ouMarket, ou15Market, ou35Market, bttsMarket, dcMarket, iyMarket, hmsMarket, cornerMarket, cardMarket, goalRangeMarket, oddEvenMarket)
  }

  private fun generateBasketballMarkets(matchId: String, ms1: Double, ms2: Double, totalLine: Double): List<Market> {
    val msMarket = Market(
      id = "${matchId}_b_ms",
      type = MarketType.MATCH_RESULT,
      name = "Maç Sonucu (1-2)",
      selections = listOf(
        Selection("${matchId}_b1", "${matchId}_b_ms", "1", roundOdd(ms1)),
        Selection("${matchId}_b2", "${matchId}_b_ms", "2", roundOdd(ms2))
      )
    )

    val ouMarket = Market(
      id = "${matchId}_b_ou",
      type = MarketType.TOTAL_GOALS_25,
      name = "Toplam Sayı (${totalLine.toInt()})",
      selections = listOf(
        Selection("${matchId}_b_alt", "${matchId}_b_ou", "Alt $totalLine", 1.85),
        Selection("${matchId}_b_ust", "${matchId}_b_ou", "Üst $totalLine", 1.85)
      )
    )

    val handicapMarket = Market(
      id = "${matchId}_b_hnd",
      type = MarketType.BASKETBALL_HANDICAP,
      name = "Handikap (Ev -4.5 / Dep +4.5)",
      selections = listOf(
        Selection("${matchId}_b_h1", "${matchId}_b_hnd", "1 (-4.5)", 1.85),
        Selection("${matchId}_b_h2", "${matchId}_b_hnd", "2 (+4.5)", 1.85)
      )
    )

    val firstHalfMarket = Market(
      id = "${matchId}_b_iy",
      type = MarketType.BASKETBALL_FIRST_HALF,
      name = "İlk Yarı Sonucu",
      selections = listOf(
        Selection("${matchId}_b_iy1", "${matchId}_b_iy", "İY 1", roundOdd(ms1 * 0.95)),
        Selection("${matchId}_b_iy2", "${matchId}_b_iy", "İY 2", roundOdd(ms2 * 0.95))
      )
    )

    return listOf(msMarket, ouMarket, handicapMarket, firstHalfMarket)
  }

  private fun createMarketsForSport(
    matchId: String,
    sport: Sport,
    isLive: Boolean,
    seed: Int
  ): List<Market> {
    val r1 = 1.40 + (seed * 0.17 % 2.50)
    val rX = 2.80 + (seed * 0.11 % 1.20)
    val r2 = 1.50 + (seed * 0.23 % 3.00)

    return when (sport) {
      Sport.FOOTBALL -> generateNesineMarkets(
        matchId = matchId,
        ms1 = roundOdd(r1),
        msX = roundOdd(rX),
        ms2 = roundOdd(r2),
        alt25 = roundOdd(1.65 + (seed * 0.08 % 0.80)),
        ust25 = roundOdd(1.75 + (seed * 0.09 % 0.85)),
        kgVar = roundOdd(1.60 + (seed * 0.07 % 0.70)),
        kgYok = roundOdd(1.90 + (seed * 0.10 % 0.80))
      )
      Sport.BASKETBALL -> generateBasketballMarkets(
        matchId = matchId,
        ms1 = roundOdd(1.50 + (seed * 0.12 % 1.40)),
        ms2 = roundOdd(1.80 + (seed * 0.14 % 1.50)),
        totalLine = 158.5 + (seed % 14)
      )
      Sport.MOTORSPORTS -> listOf(
        Market(
          id = "${matchId}_ms_m",
          type = MarketType.MOTORSPORTS_WINNER,
          name = "Yarış Kazananı",
          selections = listOf(
            Selection("${matchId}_m1", "${matchId}_ms_m", "1", roundOdd(r1)),
            Selection("${matchId}_m2", "${matchId}_ms_m", "2", roundOdd(r2))
          )
        )
      )
      Sport.TENNIS -> listOf(
        Market(
          id = "${matchId}_t_ms",
          type = MarketType.MATCH_RESULT,
          name = "Maç Sonucu (1-2)",
          selections = listOf(
            Selection("${matchId}_t1", "${matchId}_t_ms", "1", roundOdd(r1)),
            Selection("${matchId}_t2", "${matchId}_t_ms", "2", roundOdd(r2))
          )
        ),
        Market(
          id = "${matchId}_t_tot",
          type = MarketType.TOTAL_GOALS_25,
          name = "Toplam Oyun (22.5)",
          selections = listOf(
            Selection("${matchId}_t_alt", "${matchId}_t_tot", "Alt 22.5", 1.80),
            Selection("${matchId}_t_ust", "${matchId}_t_tot", "Üst 22.5", 1.85)
          )
        )
      )
      Sport.BILLIARDS -> listOf(
        Market(
          id = "${matchId}_bil_ms",
          type = MarketType.BILLIARDS_MATCH_WINNER,
          name = "Maç Kazananı (1-2)",
          selections = listOf(
            Selection("${matchId}_bil_1", "${matchId}_bil_ms", "1", roundOdd(r1)),
            Selection("${matchId}_bil_2", "${matchId}_bil_ms", "2", roundOdd(r2))
          )
        ),
        Market(
          id = "${matchId}_bil_tot",
          type = MarketType.BILLIARDS_TOTAL_FRAMES,
          name = "Toplam Sayı / Frame (39.5)",
          selections = listOf(
            Selection("${matchId}_bil_alt", "${matchId}_bil_tot", "Alt 39.5", 1.82),
            Selection("${matchId}_bil_ust", "${matchId}_bil_tot", "Üst 39.5", 1.88)
          )
        )
      )
      Sport.OKEY_CARDS -> listOf(
        Market(
          id = "${matchId}_ok_ms",
          type = MarketType.OKEY_ROUND_WINNER,
          name = "El / Oyun Kazananı (1-2)",
          selections = listOf(
            Selection("${matchId}_ok_1", "${matchId}_ok_ms", "1", roundOdd(r1)),
            Selection("${matchId}_ok_2", "${matchId}_ok_ms", "2", roundOdd(r2))
          )
        ),
        Market(
          id = "${matchId}_ok_pts",
          type = MarketType.OKEY_TOTAL_POINTS,
          name = "Toplam Ceza Puanı (799.5)",
          selections = listOf(
            Selection("${matchId}_ok_alt", "${matchId}_ok_pts", "Alt 799.5", 1.85),
            Selection("${matchId}_ok_ust", "${matchId}_ok_pts", "Üst 799.5", 1.85)
          )
        )
      )
      Sport.TABLE_TENNIS -> listOf(
        Market(
          id = "${matchId}_tt_ms",
          type = MarketType.MATCH_RESULT,
          name = "Maç Kazananı (1-2)",
          selections = listOf(
            Selection("${matchId}_tt_1", "${matchId}_tt_ms", "1", roundOdd(r1)),
            Selection("${matchId}_tt_2", "${matchId}_tt_ms", "2", roundOdd(r2))
          )
        ),
        Market(
          id = "${matchId}_tt_pts",
          type = MarketType.TOTAL_GOALS_25,
          name = "Toplam Sayı (74.5)",
          selections = listOf(
            Selection("${matchId}_tt_alt", "${matchId}_tt_pts", "Alt 74.5", 1.80),
            Selection("${matchId}_tt_ust", "${matchId}_tt_pts", "Üst 74.5", 1.90)
          )
        )
      )
      else -> listOf(
        Market(
          id = "${matchId}_gen_ms",
          type = MarketType.MATCH_RESULT,
          name = "Maç Sonucu",
          selections = listOf(
            Selection("${matchId}_g1", "${matchId}_gen_ms", "1", roundOdd(r1)),
            Selection("${matchId}_gx", "${matchId}_gen_ms", "X", roundOdd(rX)),
            Selection("${matchId}_g2", "${matchId}_gen_ms", "2", roundOdd(r2))
          )
        )
      )
    }
  }

  /**
   * Yesterday's Finished Games (25.09.2026 Cuma)
   */
  private fun createYesterdayFinishedMatches(): List<Match> {
    val list = mutableListOf<Match>()
    val finishedItems = listOf(
      Triple("Trabzonspor", "Kayserispor", Pair(2, 0)),
      Triple("Sivasspor", "Göztepe", Pair(1, 1)),
      Triple("Chelsea", "Brighton", Pair(4, 2)),
      Triple("Borussia Dortmund", "VfL Bochum", Pair(4, 2)),
      Triple("Milan", "Lecce", Pair(3, 0)),
      Triple("PSG", "Rennes", Pair(3, 1)),
      Triple("Al Nassr", "Al Wehda", Pair(2, 0))
    )

    finishedItems.forEachIndexed { idx, item ->
      val home = item.first
      val away = item.second
      val score = item.third
      val mId = "m_fin_2509_${idx + 1}"

      list.add(
        Match(
          id = mId,
          sport = Sport.FOOTBALL,
          league = if (idx == 0 || idx == 1) "Trendyol Süper Lig" else "Avrupa Ligi",
          homeTeam = home,
          awayTeam = away,
          homeScore = score.first,
          awayScore = score.second,
          minute = 90,
          status = MatchStatus.FINISHED,
          markets = generateNesineMarkets(mId, 1.65, 3.40, 4.20, 1.80, 1.85, 1.70, 1.95),
          statistics = MatchStatistics(possessionHome = 55, possessionAway = 45, shotsHome = 11, shotsAway = 6),
          aiPrediction = AiPrediction(55, 25, 20, "${score.first} - ${score.second}", 88, "Maç tamamlandı.", "Kesinleşti.", "G-G-B", "M-B-M"),
          events = listOf(MatchEvent("fin_ev_${idx}", 76, EventType.GOAL, home, getScorerForTeam(home), "Maç sonucu kesinleşti 🏁")),
          startTime = "20:00",
          isHot = false,
          stadium = GlobalSportsDatabase.stadiumMap[home] ?: "$home Stadyumu",
          tvBroadcast = "beIN SPORTS",
          week = 1,
          matchDate = "${yesterdayIso} 20:00 TSİ",
          matchDateIso = yesterdayIso,
          iddaaCode = (9900 + idx).toString(),
          mbs = 1
        )
      )
    }

    return list
  }

  /**
   * Tomorrow's Bulletin (27.09.2026 Pazar)
   */
  private fun createTomorrowMatches(): List<Match> {
    val list = mutableListOf<Match>()
    val sundayFixtures = listOf(
      Triple("Beşiktaş", "Kayserispor", "Trendyol Süper Lig"),
      Triple("Alanyaspor", "Çaykur Rizespor", "Trendyol Süper Lig"),
      Triple("Manchester United", "Tottenham", "Premier League"),
      Triple("Atletico Madrid", "Real Madrid", "La Liga"),
      Triple("Juventus", "Napoli", "Serie A"),
      Triple("Roma", "Venezia", "Serie A"),
      Triple("Eintracht Frankfurt", "VfL Wolfsburg", "Bundesliga"),
      Triple("Strasbourg", "Marseille", "Ligue 1")
    )

    sundayFixtures.forEachIndexed { idx, item ->
      val mId = "m_sun_2709_${idx + 1}"
      list.add(
        Match(
          id = mId,
          sport = Sport.FOOTBALL,
          league = item.third,
          homeTeam = item.first,
          awayTeam = item.second,
          homeScore = 0,
          awayScore = 0,
          minute = 0,
          status = MatchStatus.SCHEDULED,
          markets = generateNesineMarkets(mId, 1.85, 3.25, 3.40, 1.75, 1.85, 1.65, 2.00),
          aiPrediction = AiPrediction(50, 28, 22, "2 - 1", 80, "Pazar bülteni maçı.", "Taktiksel analiz", "G-B-G", "M-G-B"),
          startTime = "17:00",
          isHot = true,
          stadium = GlobalSportsDatabase.stadiumMap[item.first] ?: "${item.first} Stadyumu",
          tvBroadcast = "beIN SPORTS",
          week = 1,
          matchDate = "${tomorrowIso} 17:00 TSİ",
          matchDateIso = tomorrowIso,
          iddaaCode = (12000 + idx).toString(),
          mbs = 1
        )
      )
    }

    return list
  }

  /**
   * Subsequent Weeks Fixtures (Week 2: 03.10.2026 & Week 3: 10.10.2026)
   * Strictly enforces the user requirement:
   * "aynı takım ile oynanan maç bir daha 1-2 hafta sonra oynayabilecek"
   */
  private fun generateSubsequentWeeksFixtures(): List<Match> {
    val list = mutableListOf<Match>()

    // Week 2 Fixtures: (+1 hafta sonra)
    val week2Iso = com.example.service.CalendarManagementService.getWeek2DateIso()
    val week3Iso = com.example.service.CalendarManagementService.getWeek3DateIso()

    val week2Teams = listOf(
      Pair("Beşiktaş", "Galatasaray"),
      Pair("Fenerbahçe", "Trabzonspor"),
      Pair("Manchester City", "Liverpool"),
      Pair("Chelsea", "Arsenal"),
      Pair("Barcelona", "Atletico Madrid"),
      Pair("Real Madrid", "Villarreal"),
      Pair("Bayern München", "Bayer Leverkusen"),
      Pair("Inter", "Juventus")
    )

    week2Teams.forEachIndexed { idx, pair ->
      val mId = "m_w2_${idx + 1}"
      list.add(
        Match(
          id = mId,
          sport = Sport.FOOTBALL,
          league = if (idx < 2) "Trendyol Süper Lig" else if (idx < 4) "Premier League" else "Avrupa Ligi",
          homeTeam = pair.first,
          awayTeam = pair.second,
          homeScore = 0,
          awayScore = 0,
          minute = 0,
          status = MatchStatus.SCHEDULED,
          markets = generateNesineMarkets(mId, 2.10, 3.25, 2.80, 1.80, 1.80, 1.65, 2.05),
          aiPrediction = AiPrediction(44, 30, 26, "2 - 1", 78, "2. Hafta Fikstürü (+1 Hafta Sonra).", "Dinlenmiş kadrolar.", "G-G-B", "G-M-G"),
          startTime = "20:00",
          isHot = true,
          stadium = GlobalSportsDatabase.stadiumMap[pair.first] ?: "${pair.first} Stadyumu",
          tvBroadcast = "beIN SPORTS",
          week = 2,
          matchDate = "$week2Iso 20:00 TSİ (+1 Hafta)",
          matchDateIso = week2Iso,
          iddaaCode = (20000 + idx).toString(),
          mbs = 1,
          isKralOran = true
        )
      )
    }

    // Week 3 Fixtures: (+2 hafta sonra)
    val week3Teams = listOf(
      Pair("Galatasaray", "Trabzonspor"),
      Pair("Fenerbahçe", "Kasımpaşa"),
      Pair("Liverpool", "Chelsea"),
      Pair("Arsenal", "Aston Villa"),
      Pair("Barcelona", "Sevilla"),
      Pair("Real Madrid", "Real Sociedad")
    )

    week3Teams.forEachIndexed { idx, pair ->
      val mId = "m_w3_${idx + 1}"
      list.add(
        Match(
          id = mId,
          sport = Sport.FOOTBALL,
          league = if (idx < 2) "Trendyol Süper Lig" else if (idx < 4) "Premier League" else "La Liga",
          homeTeam = pair.first,
          awayTeam = pair.second,
          homeScore = 0,
          awayScore = 0,
          minute = 0,
          status = MatchStatus.SCHEDULED,
          markets = generateNesineMarkets(mId, 1.75, 3.40, 3.80, 1.85, 1.75, 1.70, 1.95),
          aiPrediction = AiPrediction(54, 26, 20, "3 - 1", 82, "3. Hafta Fikstürü (+2 Hafta Sonra).", "Geniş rotasyon.", "G-G-G", "B-M-G"),
          startTime = "19:00",
          isHot = true,
          stadium = GlobalSportsDatabase.stadiumMap[pair.first] ?: "${pair.first} Stadyumu",
          tvBroadcast = "beIN SPORTS",
          week = 3,
          matchDate = "$week3Iso 19:00 TSİ (+2 Hafta)",
          matchDateIso = week3Iso,
          iddaaCode = (30000 + idx).toString(),
          mbs = 1,
          isKralOran = true
        )
      )
    }

    return list
  }
}
