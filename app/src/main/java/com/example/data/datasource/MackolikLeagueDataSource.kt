package com.example.data.datasource

import com.example.data.model.AiPrediction
import com.example.data.model.BasketballStatistics
import com.example.data.model.EventType
import com.example.data.model.H2HMatch
import com.example.data.model.Market
import com.example.data.model.Match
import com.example.data.model.MatchEvent
import com.example.data.model.MatchH2H
import com.example.data.model.MatchLineups
import com.example.data.model.MatchStatistics
import com.example.data.model.MatchStatus
import com.example.data.model.PlayerLineup
import com.example.data.model.Sport
import com.example.data.model.TeamLineup
import com.example.engine.AiOddsEngine

/**
 * Mackolik League Data Provider
 * Provides comprehensive league fixtures covering:
 * - Trendyol Süper Lig
 * - UEFA Şampiyonlar Ligi
 * - İngiltere Premier League
 * - İspanya La Liga
 * - İtalya Serie A
 * - Almanya Bundesliga
 * - Fransa Ligue 1
 * - Turkish Airlines EuroLeague
 */
object MackolikLeagueDataSource {

  fun getAllLeagues(): List<String> = listOf(
    "Tümü",
    "Trendyol Süper Lig",
    "UEFA Şampiyonlar Ligi",
    "UEFA Avrupa Ligi",
    "UEFA Konferans Ligi",
    "UEFA Uluslar Ligi / Ülke Maçları",
    "Hafta İçi (UEFA & Ülke)",
    "Hafta Sonu (Ligler)",
    "Trendyol 1. Lig",
    "Premier League",
    "La Liga",
    "Serie A",
    "Bundesliga",
    "Ligue 1",
    "Eredivisie",
    "Liga Portugal",
    "EuroLeague",
    "NBA",
    "Türkiye Sigorta BSL",
    "Vodafone Sultanlar Ligi",
    "SMS Grup Efeler Ligi",
    "ATP & WTA Tour",
    "EHF Şampiyonlar Ligi",
    "NHL Buz Hokeyi",
    "TFF 2. Lig Kırmızı",
    "TFF 2. Lig Beyaz",
    "Championship",
    "Segunda Division",
    "Serie B",
    "2. Bundesliga",
    "MotoGP Grand Prix",
    "WRC Dünya Rallisi",
    "Formula 1 Grand Prix",
    "CS2 Major Şampiyonası"
  )

  fun getInitialMackolikMatches(): List<Match> {
    val list = mutableListOf<Match>()

    // 1. Trendyol Süper Lig: Galatasaray - Fenerbahçe
    val (m1Markets, m1Ai) = AiOddsEngine.generateInitialMarkets("m1", 1750.0, 1740.0, 1.95, 1.45)
    list.add(
      Match(
        id = "m1",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Galatasaray",
        awayTeam = "Fenerbahçe",
        homeScore = 1,
        awayScore = 0,
        minute = 54,
        status = MatchStatus.LIVE,
        markets = m1Markets,
        statistics = MatchStatistics(
          possessionHome = 56,
          possessionAway = 44,
          shotsHome = 9,
          shotsAway = 5,
          shotsOnTargetHome = 5,
          shotsOnTargetAway = 2,
          cornersHome = 5,
          cornersAway = 3,
          foulsHome = 7,
          foulsAway = 11,
          yellowCardsHome = 1,
          yellowCardsAway = 3,
          redCardsHome = 0,
          redCardsAway = 0,
          offsidesHome = 2,
          offsidesAway = 1,
          xgHome = 1.62,
          xgAway = 0.74,
          dangerousAttacksHome = 38,
          dangerousAttacksAway = 26,
          ballInPlayTime = "58:40"
        ),
        aiPrediction = m1Ai,
        events = listOf(
          MatchEvent("e1", 14, EventType.GOAL, "Galatasaray", "Mauro Icardi", "Ceza sahası içinden şık plase vuruşla gol! ⚽"),
          MatchEvent("e2", 19, EventType.YELLOW_CARD, "Fenerbahçe", "Alexander Djiku", "Sert faul nedeniyle sarı kart 🟨"),
          MatchEvent("e3", 42, EventType.YELLOW_CARD, "Galatasaray", "Lucas Torreira", "Taktik faul sarı kart 🟨")
        ),
        startTime = "20:00",
        isHot = true,
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 0,
        extraTimeMinutes = 5,
        referee = "Halil Umut Meler",
        stadium = "Rams Park (52.280 Seyirci)",
        weather = "17°C Parçalı Bulutlu",
        tvBroadcast = "beIN SPORTS 1"
      )
    )

    // 2. Premier League: Arsenal - Manchester City
    val (m2Markets, m2Ai) = AiOddsEngine.generateInitialMarkets("m2", 1840.0, 1860.0, 1.60, 1.70)
    list.add(
      Match(
        id = "m2",
        sport = Sport.FOOTBALL,
        league = "Premier League",
        homeTeam = "Arsenal",
        awayTeam = "Manchester City",
        homeScore = 0,
        awayScore = 0,
        minute = 37,
        status = MatchStatus.LIVE,
        markets = m2Markets,
        statistics = MatchStatistics(
          possessionHome = 47,
          possessionAway = 53,
          shotsHome = 4,
          shotsAway = 6,
          shotsOnTargetHome = 2,
          shotsOnTargetAway = 3,
          cornersHome = 2,
          cornersAway = 4,
          foulsHome = 5,
          foulsAway = 4,
          yellowCardsHome = 1,
          yellowCardsAway = 1,
          redCardsHome = 0,
          redCardsAway = 0,
          offsidesHome = 1,
          offsidesAway = 2,
          xgHome = 0.65,
          xgAway = 0.88,
          dangerousAttacksHome = 24,
          dangerousAttacksAway = 31,
          ballInPlayTime = "56:15"
        ),
        aiPrediction = m2Ai,
        events = listOf(
          MatchEvent("e4", 22, EventType.SHOT_ON_TARGET, "Manchester City", "Erling Haaland", "Direkten dönen top kalecide kaldı! 🎯"),
          MatchEvent("e5", 31, EventType.YELLOW_CARD, "Arsenal", "Declan Rice", "Orta alanda geciken müdahale 🟨")
        ),
        startTime = "18:30",
        isHot = true,
        halfTimeHomeScore = 0,
        halfTimeAwayScore = 0,
        extraTimeMinutes = 3,
        referee = "Michael Oliver",
        stadium = "Emirates Stadium (60.704 Seyirci)",
        weather = "12°C Yağmurlu",
        tvBroadcast = "beIN SPORTS 3"
      )
    )

    // 3. Trendyol Süper Lig: Beşiktaş - Trabzonspor
    val (m3Markets, m3Ai) = AiOddsEngine.generateInitialMarkets("m3", 1680.0, 1660.0, 1.75, 1.35)
    list.add(
      Match(
        id = "m3",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Beşiktaş",
        awayTeam = "Trabzonspor",
        homeScore = 2,
        awayScore = 1,
        minute = 68,
        status = MatchStatus.LIVE,
        markets = m3Markets,
        statistics = MatchStatistics(
          possessionHome = 54,
          possessionAway = 46,
          shotsHome = 11,
          shotsAway = 8,
          shotsOnTargetHome = 6,
          shotsOnTargetAway = 4,
          cornersHome = 6,
          cornersAway = 3,
          foulsHome = 9,
          foulsAway = 12,
          yellowCardsHome = 2,
          yellowCardsAway = 3,
          redCardsHome = 0,
          redCardsAway = 0,
          offsidesHome = 3,
          offsidesAway = 1,
          xgHome = 1.92,
          xgAway = 1.28,
          dangerousAttacksHome = 46,
          dangerousAttacksAway = 35,
          ballInPlayTime = "53:10"
        ),
        aiPrediction = m3Ai,
        events = listOf(
          MatchEvent("e6", 12, EventType.GOAL, "Beşiktaş", "Ciro Immobile", "Kafa vuruşu ile ağları sarstı ⚽"),
          MatchEvent("e7", 41, EventType.GOAL, "Trabzonspor", "Simon Banza", "Penaltı noktasından fileleri buldu ⚽"),
          MatchEvent("e8", 58, EventType.GOAL, "Beşiktaş", "Rafa Silva", "Ceza yayından mükemmel falsolu şut! ⚽")
        ),
        startTime = "20:00",
        isHot = false,
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 1,
        extraTimeMinutes = 6,
        referee = "Ali Şansalan",
        stadium = "Tüpraş Stadyumu (42.590 Seyirci)",
        weather = "15°C Rüzgarlı",
        tvBroadcast = "beIN SPORTS 1"
      )
    )

    // 4. UEFA Şampiyonlar Ligi: Real Madrid - Bayern Münih
    val (m4Markets, m4Ai) = AiOddsEngine.generateInitialMarkets("m4", 1880.0, 1860.0, 2.10, 1.80)
    list.add(
      Match(
        id = "m4",
        sport = Sport.FOOTBALL,
        league = "UEFA Şampiyonlar Ligi",
        homeTeam = "Real Madrid",
        awayTeam = "Bayern Münih",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.SCHEDULED,
        markets = m4Markets,
        statistics = MatchStatistics(),
        aiPrediction = m4Ai,
        events = emptyList(),
        startTime = "22:00",
        isHot = true,
        halfTimeHomeScore = 0,
        halfTimeAwayScore = 0,
        extraTimeMinutes = 4,
        referee = "Szymon Marciniak",
        stadium = "Santiago Bernabéu (81.044 Seyirci)",
        weather = "19°C Açık",
        tvBroadcast = "TRT 1 / Tabii Spor"
      )
    )

    // 5. İspanya La Liga: Barcelona - Atletico Madrid
    val (m5Markets, m5Ai) = AiOddsEngine.generateInitialMarkets("m5", 1850.0, 1790.0, 2.05, 1.15)
    list.add(
      Match(
        id = "m5",
        sport = Sport.FOOTBALL,
        league = "La Liga",
        homeTeam = "Barcelona",
        awayTeam = "Atletico Madrid",
        homeScore = 1,
        awayScore = 1,
        minute = 49,
        status = MatchStatus.LIVE,
        markets = m5Markets,
        statistics = MatchStatistics(
          possessionHome = 64,
          possessionAway = 36,
          shotsHome = 12,
          shotsAway = 5,
          shotsOnTargetHome = 5,
          shotsOnTargetAway = 3,
          cornersHome = 7,
          cornersAway = 2,
          foulsHome = 6,
          foulsAway = 14,
          yellowCardsHome = 1,
          yellowCardsAway = 4,
          redCardsHome = 0,
          redCardsAway = 0,
          offsidesHome = 1,
          offsidesAway = 4,
          xgHome = 1.85,
          xgAway = 0.92,
          dangerousAttacksHome = 52,
          dangerousAttacksAway = 21,
          ballInPlayTime = "57:30"
        ),
        aiPrediction = m5Ai,
        events = listOf(
          MatchEvent("e9", 25, EventType.GOAL, "Barcelona", "Lamine Yamal", "Ceza sahası dışından inanılmaz falsolu vuruş! ⚽"),
          MatchEvent("e10", 38, EventType.GOAL, "Atletico Madrid", "Antoine Griezmann", "Hızlı kontratakta sol ayak plase ⚽")
        ),
        startTime = "21:30",
        isHot = true,
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 1,
        extraTimeMinutes = 4,
        referee = "Jesus Gil Manzano",
        stadium = "Estadi Olímpic Lluís Companys",
        weather = "16°C Açık",
        tvBroadcast = "S Sport Plus"
      )
    )

    // 6. İtalya Serie A: Inter - Juventus
    val (m6Markets, m6Ai) = AiOddsEngine.generateInitialMarkets("m6", 1820.0, 1800.0, 1.65, 1.20)
    list.add(
      Match(
        id = "m6",
        sport = Sport.FOOTBALL,
        league = "Serie A",
        homeTeam = "Inter",
        awayTeam = "Juventus",
        homeScore = 1,
        awayScore = 0,
        minute = 73,
        status = MatchStatus.LIVE,
        markets = m6Markets,
        statistics = MatchStatistics(
          possessionHome = 51,
          possessionAway = 49,
          shotsHome = 8,
          shotsAway = 6,
          shotsOnTargetHome = 4,
          shotsOnTargetAway = 2,
          cornersHome = 4,
          cornersAway = 5,
          foulsHome = 13,
          foulsAway = 11,
          yellowCardsHome = 3,
          yellowCardsAway = 2,
          redCardsHome = 0,
          redCardsAway = 0,
          offsidesHome = 2,
          offsidesAway = 1,
          xgHome = 1.45,
          xgAway = 0.81,
          dangerousAttacksHome = 36,
          dangerousAttacksAway = 32,
          ballInPlayTime = "51:40"
        ),
        aiPrediction = m6Ai,
        events = listOf(
          MatchEvent("e11", 52, EventType.GOAL, "Inter", "Lautaro Martinez", "Köşe vuruşunda ön direkte dokunuş ⚽")
        ),
        startTime = "21:45",
        isHot = false,
        halfTimeHomeScore = 0,
        halfTimeAwayScore = 0,
        extraTimeMinutes = 5,
        referee = "Daniele Orsato",
        stadium = "San Siro (75.817 Seyirci)",
        weather = "14°C Sisli",
        tvBroadcast = "S Sport 2"
      )
    )

    // 7. Almanya Bundesliga: Bayern Münih - Borussia Dortmund
    val (m7Markets, m7Ai) = AiOddsEngine.generateInitialMarkets("m7", 1870.0, 1780.0, 2.45, 1.65)
    list.add(
      Match(
        id = "m7",
        sport = Sport.FOOTBALL,
        league = "Bundesliga",
        homeTeam = "Bayern Münih",
        awayTeam = "Borussia Dortmund",
        homeScore = 3,
        awayScore = 2,
        minute = 81,
        status = MatchStatus.LIVE,
        markets = m7Markets,
        statistics = MatchStatistics(
          possessionHome = 58,
          possessionAway = 42,
          shotsHome = 16,
          shotsAway = 10,
          shotsOnTargetHome = 8,
          shotsOnTargetAway = 5,
          cornersHome = 8,
          cornersAway = 4,
          foulsHome = 8,
          foulsAway = 10,
          yellowCardsHome = 1,
          yellowCardsAway = 2,
          redCardsHome = 0,
          redCardsAway = 0,
          offsidesHome = 2,
          offsidesAway = 3,
          xgHome = 2.76,
          xgAway = 1.84,
          dangerousAttacksHome = 61,
          dangerousAttacksAway = 40,
          ballInPlayTime = "62:10"
        ),
        aiPrediction = m7Ai,
        events = listOf(
          MatchEvent("e12", 18, EventType.GOAL, "Bayern Münih", "Harry Kane", "Ceza sahasından sert şut ⚽"),
          MatchEvent("e13", 29, EventType.GOAL, "Borussia Dortmund", "Serhou Guirassy", "Defans arkasına koşu ve gol ⚽"),
          MatchEvent("e14", 55, EventType.GOAL, "Bayern Münih", "Jamal Musiala", "Çalımlarla içeri girdi ve attı ⚽"),
          MatchEvent("e15", 67, EventType.GOAL, "Borussia Dortmund", "Julian Brandt", "Ceza yayından köşeye vuruş ⚽"),
          MatchEvent("e16", 74, EventType.GOAL, "Bayern Münih", "Harry Kane", "Kafa vuruşuyla duble yaptı! ⚽")
        ),
        startTime = "19:30",
        isHot = true,
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 1,
        extraTimeMinutes = 4,
        referee = "Felix Zwayer",
        stadium = "Allianz Arena (75.000 Seyirci)",
        weather = "11°C Bulutlu",
        tvBroadcast = "Tivibu Spor 1"
      )
    )

    // 8. Fransa Ligue 1: Paris Saint-Germain - Monaco
    val (m8Markets, m8Ai) = AiOddsEngine.generateInitialMarkets("m8", 1830.0, 1720.0, 2.20, 1.10)
    list.add(
      Match(
        id = "m8",
        sport = Sport.FOOTBALL,
        league = "Ligue 1",
        homeTeam = "Paris Saint-Germain",
        awayTeam = "Monaco",
        homeScore = 2,
        awayScore = 0,
        minute = 61,
        status = MatchStatus.LIVE,
        markets = m8Markets,
        statistics = MatchStatistics(
          possessionHome = 66,
          possessionAway = 34,
          shotsHome = 13,
          shotsAway = 4,
          shotsOnTargetHome = 7,
          shotsOnTargetAway = 1,
          cornersHome = 6,
          cornersAway = 2,
          foulsHome = 5,
          foulsAway = 12,
          yellowCardsHome = 1,
          yellowCardsAway = 3,
          redCardsHome = 0,
          redCardsAway = 0,
          offsidesHome = 2,
          offsidesAway = 2,
          xgHome = 2.15,
          xgAway = 0.54,
          dangerousAttacksHome = 54,
          dangerousAttacksAway = 18,
          ballInPlayTime = "59:20"
        ),
        aiPrediction = m8Ai,
        events = listOf(
          MatchEvent("e17", 22, EventType.GOAL, "Paris Saint-Germain", "Ousmane Dembélé", "Sağ kanattan içeri kat edip attı ⚽"),
          MatchEvent("e18", 48, EventType.GOAL, "Paris Saint-Germain", "Bradley Barcola", "Hızlı kontratak golü ⚽")
        ),
        startTime = "22:00",
        isHot = false,
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 0,
        extraTimeMinutes = 4,
        referee = "Clément Turpin",
        stadium = "Parc des Princes (48.583 Seyirci)",
        weather = "13°C Parçalı Bulutlu",
        tvBroadcast = "beIN SPORTS 2"
      )
    )

    // 9. EuroLeague Basketbol: Fenerbahçe Beko - Panathinaikos
    val (m9Markets, m9Ai) = AiOddsEngine.generateBasketballMarkets("m9", "Fenerbahçe Beko", "Panathinaikos", 1.58, 2.35, 3.5, 162.5)
    list.add(
      Match(
        id = "m9",
        sport = Sport.BASKETBALL,
        league = "EuroLeague",
        homeTeam = "Fenerbahçe Beko",
        awayTeam = "Panathinaikos",
        homeScore = 74,
        awayScore = 71,
        minute = 33,
        status = MatchStatus.LIVE,
        markets = m9Markets,
        statistics = MatchStatistics(
          shotsHome = 26,
          shotsAway = 24,
          dangerousAttacksHome = 10,
          dangerousAttacksAway = 8
        ),
        quarterScoresHome = listOf(22, 19, 21, 12),
        quarterScoresAway = listOf(18, 20, 20, 13),
        basketballStats = BasketballStatistics(
          twoPointersHome = "21/39 (%54)",
          twoPointersAway = "18/36 (%50)",
          threePointersHome = "10/24 (%42)",
          threePointersAway = "9/22 (%41)",
          freeThrowsHome = "12/15 (%80)",
          freeThrowsAway = "8/11 (%73)",
          reboundsHome = 34,
          reboundsAway = 31,
          assistsHome = 21,
          assistsAway = 17,
          stealsHome = 6,
          stealsAway = 5,
          blocksHome = 4,
          blocksAway = 2,
          foulsHome = 15,
          foulsAway = 18
        ),
        aiPrediction = m9Ai,
        events = listOf(
          MatchEvent("e19", 10, EventType.THREE_POINTER, "Fenerbahçe Beko", "Nigel Hayes-Davis", "Köşeden kritik üçlük isabeti! 🏀"),
          MatchEvent("e20", 24, EventType.DUNK, "Panathinaikos", "Kendrick Nunn", "Boya alandan turnike basket 🏀")
        ),
        startTime = "20:45",
        isHot = true,
        halfTimeHomeScore = 41,
        halfTimeAwayScore = 38,
        referee = "Sreten Radovic",
        stadium = "Ülker Sports Arena (13.059 Seyirci)",
        tvBroadcast = "S Sport"
      )
    )

    // 10. ATP Dünya Turu Finali (Tenis): Jannik Sinner vs Carlos Alcaraz (CANLI)
    val (m10Markets, m10Ai) = AiOddsEngine.generateInitialMarkets("m10", 1930.0, 1920.0, 1.85, 1.85)
    list.add(
      Match(
        id = "m10",
        sport = Sport.TENNIS,
        league = "ATP Final",
        homeTeam = "Jannik Sinner",
        awayTeam = "Carlos Alcaraz",
        homeScore = 1, // Set 1 Sinner
        awayScore = 1, // Set 2 Alcaraz
        minute = 118,
        status = MatchStatus.LIVE,
        markets = m10Markets,
        statistics = MatchStatistics(
          possessionHome = 52,
          possessionAway = 48,
          shotsHome = 12, // Aces
          shotsAway = 9,
          shotsOnTargetHome = 38, // Winners
          shotsOnTargetAway = 34,
          foulsHome = 14, // Unforced errors
          foulsAway = 19,
          dangerousAttacksHome = 5, // Break points
          dangerousAttacksAway = 4,
          xgHome = 1.82,
          xgAway = 1.78,
          ballInPlayTime = "1:58:20"
        ),
        aiPrediction = m10Ai,
        events = listOf(
          MatchEvent("e21", 46, EventType.SHOT_ON_TARGET, "Jannik Sinner", "Sinner", "1. Set Tie-break ile Sinner'ın (7-6) 🎾"),
          MatchEvent("e22", 95, EventType.SHOT_ON_TARGET, "Carlos Alcaraz", "Alcaraz", "2. Set Alcaraz'ın güçlü forehandleriyle (4-6) 🎾"),
          MatchEvent("e23", 112, EventType.SHOT_ON_TARGET, "Jannik Sinner", "Sinner", "3. Set 4-3, servis kırma şansı yakalandı! 🎾")
        ),
        startTime = "18:00",
        isHot = true,
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 0,
        extraTimeMinutes = 0,
        referee = "Carlos Ramos",
        stadium = "Inalpi Arena, Torino (Sert Kort)",
        weather = "Salon (Kapalı Kort)",
        tvBroadcast = "TRT Spor Yıldız"
      )
    )

    // 11. EuroLeague Basketbol: Real Madrid - Anadolu Efes (CANLI)
    val (m11Markets, m11Ai) = AiOddsEngine.generateBasketballMarkets("m11", "Real Madrid", "Anadolu Efes", 1.48, 2.65, 5.5, 166.5)
    list.add(
      Match(
        id = "m11",
        sport = Sport.BASKETBALL,
        league = "EuroLeague",
        homeTeam = "Real Madrid",
        awayTeam = "Anadolu Efes",
        homeScore = 82,
        awayScore = 79,
        minute = 36,
        status = MatchStatus.LIVE,
        markets = m11Markets,
        statistics = MatchStatistics(
          shotsHome = 31,
          shotsAway = 28,
          dangerousAttacksHome = 12,
          dangerousAttacksAway = 11,
          xgHome = 2.10,
          xgAway = 1.95
        ),
        quarterScoresHome = listOf(24, 20, 22, 16),
        quarterScoresAway = listOf(21, 21, 23, 14),
        basketballStats = BasketballStatistics(
          twoPointersHome = "23/40 (%57)",
          twoPointersAway = "20/38 (%52)",
          threePointersHome = "9/23 (%39)",
          threePointersAway = "11/27 (%40)",
          freeThrowsHome = "15/18 (%83)",
          freeThrowsAway = "10/12 (%83)",
          reboundsHome = 37,
          reboundsAway = 33,
          assistsHome = 23,
          assistsAway = 19,
          stealsHome = 5,
          stealsAway = 7,
          blocksHome = 5,
          blocksAway = 1,
          foulsHome = 17,
          foulsAway = 19
        ),
        aiPrediction = m11Ai,
        events = listOf(
          MatchEvent("e24", 14, EventType.DUNK, "Real Madrid", "Facundo Campazzo", "Hızlı hücumda asist ve turnike 🏀"),
          MatchEvent("e25", 28, EventType.THREE_POINTER, "Anadolu Efes", "Shane Larkin", "Logo mesafesinden üçlük! 🏀")
        ),
        startTime = "21:00",
        isHot = false,
        halfTimeHomeScore = 44,
        halfTimeAwayScore = 42,
        referee = "Ilija Belosevic",
        stadium = "WiZink Center, Madrid (15.000 Seyirci)",
        tvBroadcast = "S Sport Plus"
      )
    )

    // 12. BİTEN MAÇ: Trendyol Süper Lig: Trabzonspor - Samsunspor (BİTTİ / FINISHED)
    val (m12Markets, m12Ai) = AiOddsEngine.generateInitialMarkets("m12", 1640.0, 1580.0, 1.85, 1.25)
    list.add(
      Match(
        id = "m12",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Trabzonspor",
        awayTeam = "Samsunspor",
        homeScore = 2,
        awayScore = 1,
        minute = 90,
        status = MatchStatus.FINISHED,
        markets = m12Markets,
        statistics = MatchStatistics(
          possessionHome = 57,
          possessionAway = 43,
          shotsHome = 14,
          shotsAway = 7,
          shotsOnTargetHome = 6,
          shotsOnTargetAway = 3,
          cornersHome = 7,
          cornersAway = 4,
          foulsHome = 11,
          foulsAway = 15,
          yellowCardsHome = 2,
          yellowCardsAway = 4,
          redCardsHome = 0,
          redCardsAway = 1,
          offsidesHome = 2,
          offsidesAway = 1,
          xgHome = 2.05,
          xgAway = 0.94,
          dangerousAttacksHome = 51,
          dangerousAttacksAway = 28,
          ballInPlayTime = "54:45"
        ),
        aiPrediction = m12Ai,
        events = listOf(
          MatchEvent("e26", 28, EventType.GOAL, "Trabzonspor", "Simon Banza", "Ceza sahası köşesinden sert plase ⚽"),
          MatchEvent("e27", 54, EventType.GOAL, "Samsunspor", "Marius Mouandilmadji", "Kafa vuruşuyla beraberlik ⚽"),
          MatchEvent("e28", 78, EventType.RED_CARD, "Samsunspor", "Zeki Yavru", "İkinci sarıdan kırmızı kart! 🟥"),
          MatchEvent("e29", 84, EventType.GOAL, "Trabzonspor", "Edin Visca", "Uzak direkte müthiş vole golü! ⚽")
        ),
        startTime = "16:00",
        isHot = false,
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 0,
        extraTimeMinutes = 6,
        referee = "Cihan Aydın",
        stadium = "Papara Park (38.500 Seyirci)",
        weather = "13°C Yağmurlu",
        tvBroadcast = "beIN SPORTS 1"
      )
    )

    // 13. BİTEN MAÇ: EuroLeague: Olympiakos - Barcelona (BİTTİ / FINISHED)
    val (m13Markets, m13Ai) = AiOddsEngine.generateInitialMarkets("m13", 1700.0, 1680.0, 1.70, 1.80)
    list.add(
      Match(
        id = "m13",
        sport = Sport.BASKETBALL,
        league = "EuroLeague",
        homeTeam = "Olympiakos",
        awayTeam = "Barcelona",
        homeScore = 84,
        awayScore = 81,
        minute = 40,
        status = MatchStatus.FINISHED,
        markets = m13Markets,
        statistics = MatchStatistics(
          shotsHome = 33,
          shotsAway = 31,
          dangerousAttacksHome = 11,
          dangerousAttacksAway = 9,
          xgHome = 2.30,
          xgAway = 2.15
        ),
        aiPrediction = m13Ai,
        events = listOf(
          MatchEvent("e30", 12, EventType.SHOT_ON_TARGET, "Olympiakos", "Sasha Vezenkov", "Hızlı hücum üçlüğü 🏀"),
          MatchEvent("e31", 39, EventType.SHOT_ON_TARGET, "Olympiakos", "Evan Fournier", "Son saniye maç kazandıran basket! 🏀")
        ),
        startTime = "19:00",
        isHot = false,
        halfTimeHomeScore = 42,
        halfTimeAwayScore = 40,
        referee = "Borys Ryzhyk",
        stadium = "Barış ve Dostluk Salonu, Atina",
        tvBroadcast = "S Sport Plus"
      )
    )

    // 14. BİTEN MAÇ: Roland Garros Final (Tenis): Novak Djokovic - Alexander Zverev (BİTTİ / FINISHED)
    val (m14Markets, m14Ai) = AiOddsEngine.generateInitialMarkets("m14", 1950.0, 1900.0, 1.65, 2.10)
    list.add(
      Match(
        id = "m14",
        sport = Sport.TENNIS,
        league = "ATP Final",
        homeTeam = "Novak Djokovic",
        awayTeam = "Alexander Zverev",
        homeScore = 3, // Setler 3 - 1
        awayScore = 1,
        minute = 210,
        status = MatchStatus.FINISHED,
        markets = m14Markets,
        statistics = MatchStatistics(
          possessionHome = 54,
          possessionAway = 46,
          shotsHome = 18, // Aces
          shotsAway = 22,
          shotsOnTargetHome = 52, // Winners
          shotsOnTargetAway = 41,
          foulsHome = 21,
          foulsAway = 34,
          dangerousAttacksHome = 7,
          dangerousAttacksAway = 3,
          xgHome = 2.40,
          xgAway = 1.60,
          ballInPlayTime = "3:30:15"
        ),
        aiPrediction = m14Ai,
        events = listOf(
          MatchEvent("e32", 52, EventType.SHOT_ON_TARGET, "Novak Djokovic", "Djokovic", "1. Set Djokovic'in (6-4) 🎾"),
          MatchEvent("e33", 108, EventType.SHOT_ON_TARGET, "Alexander Zverev", "Zverev", "2. Set Zverev'in (3-6) 🎾"),
          MatchEvent("e34", 165, EventType.SHOT_ON_TARGET, "Novak Djokovic", "Djokovic", "3. Set Djokovic'in (7-5) 🎾"),
          MatchEvent("e35", 205, EventType.SHOT_ON_TARGET, "Novak Djokovic", "Djokovic", "Şampiyonluk sayısı ve kupa Djokovic'in! (6-3) 🎾")
        ),
        startTime = "15:00",
        isHot = false,
        halfTimeHomeScore = 2,
        halfTimeAwayScore = 1,
        referee = "Damien Dumusois",
        stadium = "Court Philippe-Chatrier, Paris (Toprak Kort)",
        weather = "22°C Güneşli",
        tvBroadcast = "Eurosport 1",
        week = 1,
        matchDate = "22 Eylül Salı 15:00 TSİ"
      )
    )

    // 15. Canlı: Süper Lig: Eyüpspor - Kasımpaşa
    val (m15Markets, m15Ai) = AiOddsEngine.generateInitialMarkets("m15", 1580.0, 1560.0, 1.95, 1.35)
    list.add(
      Match(
        id = "m15",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Eyüpspor",
        awayTeam = "Kasımpaşa",
        homeScore = 1,
        awayScore = 1,
        minute = 38,
        status = MatchStatus.LIVE,
        markets = m15Markets,
        statistics = MatchStatistics(possessionHome = 52, possessionAway = 48, shotsHome = 8, shotsAway = 7, xgHome = 1.15, xgAway = 1.05),
        aiPrediction = m15Ai,
        events = listOf(
          MatchEvent("e36", 18, EventType.GOAL, "Eyüpspor", "Ahmed Kutucu", "Ceza yayından sert vuruşla gol! ⚽"),
          MatchEvent("e37", 32, EventType.GOAL, "Kasımpaşa", "Haris Hajradinovic", "Penaltı golü ⚽")
        ),
        startTime = "17:00",
        week = 1,
        matchDate = "23 Eylül Çarşamba 17:00 TSİ"
      )
    )

    // 16. Canlı: Süper Lig: Başakşehir - Sivasspor
    val (m16Markets, m16Ai) = AiOddsEngine.generateInitialMarkets("m16", 1620.0, 1540.0, 1.80, 1.40)
    list.add(
      Match(
        id = "m16",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Başakşehir",
        awayTeam = "Sivasspor",
        homeScore = 2,
        awayScore = 0,
        minute = 58,
        status = MatchStatus.LIVE,
        markets = m16Markets,
        statistics = MatchStatistics(possessionHome = 61, possessionAway = 39, shotsHome = 12, shotsAway = 4, xgHome = 1.85, xgAway = 0.45),
        aiPrediction = m16Ai,
        events = listOf(
          MatchEvent("e38", 22, EventType.GOAL, "Başakşehir", "Krzysztof Piatek", "Kafa vuruşuyla gol ⚽"),
          MatchEvent("e39", 51, EventType.GOAL, "Başakşehir", "Deniz Türüç", "Sol ayakla şık plase ⚽")
        ),
        startTime = "18:30",
        week = 1,
        matchDate = "23 Eylül Çarşamba 18:30 TSİ"
      )
    )

    // 17. Canlı: Ligue 1: Paris Saint-Germain - Marseille
    val (m17Markets, m17Ai) = AiOddsEngine.generateInitialMarkets("m17", 1880.0, 1720.0, 1.60, 2.10)
    list.add(
      Match(
        id = "m17",
        sport = Sport.FOOTBALL,
        league = "Ligue 1",
        homeTeam = "Paris Saint-Germain",
        awayTeam = "Marseille",
        homeScore = 2,
        awayScore = 1,
        minute = 65,
        status = MatchStatus.LIVE,
        markets = m17Markets,
        statistics = MatchStatistics(possessionHome = 65, possessionAway = 35, shotsHome = 14, shotsAway = 8, xgHome = 2.20, xgAway = 1.10),
        aiPrediction = m17Ai,
        events = listOf(
          MatchEvent("e40", 14, EventType.GOAL, "Paris Saint-Germain", "Bradley Barcola", "Hızlı hücum golü ⚽"),
          MatchEvent("e41", 38, EventType.GOAL, "Marseille", "Mason Greenwood", "Ceza sahası dışından sert şut ⚽"),
          MatchEvent("e42", 55, EventType.GOAL, "Paris Saint-Germain", "Ousmane Dembélé", "Çalımlarla içeri girip gol ⚽")
        ),
        startTime = "21:45",
        week = 1,
        matchDate = "23 Eylül Çarşamba 21:45 TSİ"
      )
    )

    // 18. Canlı: EuroLeague: Partizan - AS Monaco
    val (m18Markets, m18Ai) = AiOddsEngine.generateInitialMarkets("m18", 1680.0, 1690.0, 1.85, 1.75)
    list.add(
      Match(
        id = "m18",
        sport = Sport.BASKETBALL,
        league = "EuroLeague",
        homeTeam = "Partizan",
        awayTeam = "AS Monaco",
        homeScore = 54,
        awayScore = 51,
        minute = 24,
        status = MatchStatus.LIVE,
        markets = m18Markets,
        statistics = MatchStatistics(shotsHome = 22, shotsAway = 21, xgHome = 1.70, xgAway = 1.65),
        aiPrediction = m18Ai,
        events = listOf(
          MatchEvent("e43", 10, EventType.SHOT_ON_TARGET, "Partizan", "Carlik Jones", "Üç sayılık basket 🏀"),
          MatchEvent("e44", 20, EventType.SHOT_ON_TARGET, "AS Monaco", "Mike James", "Step-back basket 🏀")
        ),
        startTime = "20:30",
        week = 1,
        matchDate = "23 Eylül Çarşamba 20:30 TSİ"
      )
    )

    // ==================== 2. HAFTA MAÇLARI ====================
    val (w2m1Mk, w2m1Ai) = AiOddsEngine.generateInitialMarkets("w2m1", 1740.0, 1710.0, 1.90, 1.50)
    list.add(
      Match(
        id = "w2m1",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Fenerbahçe",
        awayTeam = "Beşiktaş",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w2m1Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w2m1Ai,
        events = emptyList(),
        startTime = "19:00",
        isHot = true,
        week = 2,
        matchDate = "26 Eylül Cumartesi 19:00 TSİ",
        referee = "Atilla Karaoğlan",
        stadium = "Ülker Stadyumu Şükrü Saracoğlu Spor Kompleksi",
        tvBroadcast = "beIN SPORTS 1"
      )
    )

    val (w2m2Mk, w2m2Ai) = AiOddsEngine.generateInitialMarkets("w2m2", 1680.0, 1760.0, 1.65, 1.85)
    list.add(
      Match(
        id = "w2m2",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Trabzonspor",
        awayTeam = "Galatasaray",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w2m2Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w2m2Ai,
        events = emptyList(),
        startTime = "20:00",
        isHot = true,
        week = 2,
        matchDate = "27 Eylül Pazar 20:00 TSİ",
        stadium = "Papara Park, Trabzon",
        tvBroadcast = "beIN SPORTS 1"
      )
    )

    val (w2m3Mk, w2m3Ai) = AiOddsEngine.generateInitialMarkets("w2m3", 1920.0, 1890.0, 1.80, 1.70)
    list.add(
      Match(
        id = "w2m3",
        sport = Sport.FOOTBALL,
        league = "Premier League",
        homeTeam = "Manchester City",
        awayTeam = "Liverpool",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w2m3Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w2m3Ai,
        events = emptyList(),
        startTime = "18:30",
        isHot = true,
        week = 2,
        matchDate = "27 Eylül Pazar 18:30 TSİ",
        stadium = "Etihad Stadium, Manchester",
        tvBroadcast = "beIN SPORTS 3"
      )
    )

    val (w2m4Mk, w2m4Ai) = AiOddsEngine.generateInitialMarkets("w2m4", 1850.0, 1820.0, 1.75, 1.75)
    list.add(
      Match(
        id = "w2m4",
        sport = Sport.FOOTBALL,
        league = "La Liga",
        homeTeam = "Barcelona",
        awayTeam = "Atletico Madrid",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w2m4Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w2m4Ai,
        events = emptyList(),
        startTime = "22:00",
        week = 2,
        matchDate = "27 Eylül Pazar 22:00 TSİ",
        stadium = "Lluís Companys Olimpiyat Stadı",
        tvBroadcast = "S Sport Plus"
      )
    )

    val (w2m5Mk, w2m5Ai) = AiOddsEngine.generateInitialMarkets("w2m5", 1710.0, 1730.0, 1.72, 1.80)
    list.add(
      Match(
        id = "w2m5",
        sport = Sport.BASKETBALL,
        league = "EuroLeague",
        homeTeam = "Anadolu Efes",
        awayTeam = "Olympiakos",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w2m5Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w2m5Ai,
        events = emptyList(),
        startTime = "20:30",
        week = 2,
        matchDate = "25 Eylül Cuma 20:30 TSİ",
        stadium = "Basketbol Gelişim Merkezi, İstanbul",
        tvBroadcast = "S Sport"
      )
    )

    // ==================== 3. HAFTA MAÇLARI ====================
    val (w3m1Mk, w3m1Ai) = AiOddsEngine.generateInitialMarkets("w3m1", 1750.0, 1700.0, 1.85, 1.65)
    list.add(
      Match(
        id = "w3m1",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Galatasaray",
        awayTeam = "Beşiktaş",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w3m1Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w3m1Ai,
        events = emptyList(),
        startTime = "19:00",
        isHot = true,
        week = 3,
        matchDate = "03 Ekim Cumartesi 19:00 TSİ",
        stadium = "Rams Park, İstanbul",
        tvBroadcast = "beIN SPORTS 1"
      )
    )

    val (w3m2Mk, w3m2Ai) = AiOddsEngine.generateInitialMarkets("w3m2", 1820.0, 1860.0, 1.95, 1.60)
    list.add(
      Match(
        id = "w3m2",
        sport = Sport.FOOTBALL,
        league = "Premier League",
        homeTeam = "Chelsea",
        awayTeam = "Arsenal",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w3m2Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w3m2Ai,
        events = emptyList(),
        startTime = "18:30",
        week = 3,
        matchDate = "03 Ekim Cumartesi 18:30 TSİ",
        stadium = "Stamford Bridge, Londra",
        tvBroadcast = "beIN SPORTS 3"
      )
    )

    val (w3m3Mk, w3m3Ai) = AiOddsEngine.generateInitialMarkets("w3m3", 1760.0, 1800.0, 1.88, 1.72)
    list.add(
      Match(
        id = "w3m3",
        sport = Sport.FOOTBALL,
        league = "Serie A",
        homeTeam = "Milan",
        awayTeam = "Inter",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w3m3Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w3m3Ai,
        events = emptyList(),
        startTime = "21:45",
        week = 3,
        matchDate = "04 Ekim Pazar 21:45 TSİ",
        stadium = "San Siro, Milano",
        tvBroadcast = "S Sport Plus"
      )
    )

    // ==================== 4. HAFTA MAÇLARI ====================
    val (w4m1Mk, w4m1Ai) = AiOddsEngine.generateInitialMarkets("w4m1", 1680.0, 1720.0, 1.85, 1.70)
    list.add(
      Match(
        id = "w4m1",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Trabzonspor",
        awayTeam = "Beşiktaş",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w4m1Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w4m1Ai,
        events = emptyList(),
        startTime = "19:00",
        isHot = true,
        week = 4,
        matchDate = "17 Ekim Cumartesi 19:00 TSİ",
        stadium = "Papara Park, Trabzon",
        tvBroadcast = "beIN SPORTS 1"
      )
    )

    val (w4m2Mk, w4m2Ai) = AiOddsEngine.generateInitialMarkets("w4m2", 1880.0, 1820.0, 1.65, 2.15)
    list.add(
      Match(
        id = "w4m2",
        sport = Sport.FOOTBALL,
        league = "Premier League",
        homeTeam = "Liverpool",
        awayTeam = "Manchester United",
        homeScore = 0,
        awayScore = 0,
        minute = 0,
        status = MatchStatus.UPCOMING,
        markets = w4m2Mk,
        statistics = MatchStatistics(possessionHome = 50, possessionAway = 50),
        aiPrediction = w4m2Ai,
        events = emptyList(),
        startTime = "18:30",
        isHot = true,
        week = 4,
        matchDate = "18 Ekim Pazar 18:30 TSİ",
        stadium = "Anfield, Liverpool",
        tvBroadcast = "beIN SPORTS 3"
      )
    )

    // Additional Finished Matches to ensure BİTEN KARŞILAŞMALAR has rich data across all sports
    val (m40Mk, m40Ai) = AiOddsEngine.generateInitialMarkets("m40", 1750.0, 1600.0, 1.65, 1.95)
    list.add(
      Match(
        id = "m40",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Beşiktaş",
        awayTeam = "Antalyaspor",
        homeScore = 3,
        awayScore = 1,
        minute = 90,
        status = MatchStatus.FINISHED,
        markets = m40Mk,
        statistics = MatchStatistics(
          possessionHome = 62,
          possessionAway = 38,
          shotsHome = 17,
          shotsAway = 6,
          shotsOnTargetHome = 8,
          shotsOnTargetAway = 2,
          cornersHome = 8,
          cornersAway = 3,
          xgHome = 2.45,
          xgAway = 0.78,
          ballInPlayTime = "56:20"
        ),
        aiPrediction = m40Ai,
        events = listOf(
          MatchEvent("e50", 19, EventType.GOAL, "Beşiktaş", "Ciro Immobile", "Ceza sahası köşesinden sert vuruş ⚽"),
          MatchEvent("e51", 42, EventType.GOAL, "Antalyaspor", "Sam Larsson", "Karşı atak plasesi ⚽"),
          MatchEvent("e52", 67, EventType.GOAL, "Beşiktaş", "Rafa Silva", "Şık çalımlarla gelen gol ⚽"),
          MatchEvent("e53", 88, EventType.GOAL, "Beşiktaş", "Semih Kılıçsoy", "Frikikten dönen topu tamamladı ⚽")
        ),
        startTime = "20:00",
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 1,
        extraTimeMinutes = 5,
        referee = "Halil Umut Meler",
        stadium = "Tüpraş Stadyumu (42.000 Seyirci)",
        tvBroadcast = "beIN SPORTS 1",
        matchDate = "21 Eylül Pazartesi 20:00 TSİ"
      )
    )

    val (m41Mk, m41Ai) = AiOddsEngine.generateInitialMarkets("m41", 1820.0, 1520.0, 1.45, 2.30)
    list.add(
      Match(
        id = "m41",
        sport = Sport.FOOTBALL,
        league = "Trendyol Süper Lig",
        homeTeam = "Galatasaray",
        awayTeam = "Çaykur Rizespor",
        homeScore = 2,
        awayScore = 0,
        minute = 90,
        status = MatchStatus.FINISHED,
        markets = m41Mk,
        statistics = MatchStatistics(
          possessionHome = 68,
          possessionAway = 32,
          shotsHome = 19,
          shotsAway = 5,
          shotsOnTargetHome = 7,
          shotsOnTargetAway = 1,
          cornersHome = 10,
          cornersAway = 2,
          xgHome = 2.85,
          xgAway = 0.35
        ),
        aiPrediction = m41Ai,
        events = listOf(
          MatchEvent("e54", 31, EventType.GOAL, "Galatasaray", "Victor Osimhen", "Kafa vuruşuyla gol ⚽"),
          MatchEvent("e55", 74, EventType.GOAL, "Galatasaray", "Gabriel Sara", "Ceza sahası dışından füze ⚽")
        ),
        startTime = "20:00",
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 0,
        extraTimeMinutes = 4,
        referee = "Arda Kardeşler",
        stadium = "Rams Park (51.200 Seyirci)",
        tvBroadcast = "beIN SPORTS 1",
        matchDate = "20 Eylül Pazar 20:00 TSİ"
      )
    )

    val (m42Mk, m42Ai) = AiOddsEngine.generateInitialMarkets("m42", 1890.0, 1780.0, 1.75, 1.95)
    list.add(
      Match(
        id = "m42",
        sport = Sport.FOOTBALL,
        league = "Premier League",
        homeTeam = "Manchester City",
        awayTeam = "Chelsea",
        homeScore = 3,
        awayScore = 2,
        minute = 90,
        status = MatchStatus.FINISHED,
        markets = m42Mk,
        statistics = MatchStatistics(
          possessionHome = 59,
          possessionAway = 41,
          shotsHome = 16,
          shotsAway = 11,
          shotsOnTargetHome = 9,
          shotsOnTargetAway = 5,
          xgHome = 2.65,
          xgAway = 1.90
        ),
        aiPrediction = m42Ai,
        events = listOf(
          MatchEvent("e56", 14, EventType.GOAL, "Manchester City", "Erling Haaland", "Altıpas vuruşu ⚽"),
          MatchEvent("e57", 29, EventType.GOAL, "Chelsea", "Cole Palmer", "Penaltı golü ⚽"),
          MatchEvent("e58", 53, EventType.GOAL, "Chelsea", "Nicolas Jackson", "Kontratak golü ⚽"),
          MatchEvent("e59", 70, EventType.GOAL, "Manchester City", "Phil Foden", "Sol ayakla plase ⚽"),
          MatchEvent("e60", 87, EventType.GOAL, "Manchester City", "Erling Haaland", "Son dakika galibiyet golü ⚽")
        ),
        startTime = "18:30",
        halfTimeHomeScore = 1,
        halfTimeAwayScore = 1,
        extraTimeMinutes = 7,
        referee = "Michael Oliver",
        stadium = "Etihad Stadium, Manchester",
        tvBroadcast = "beIN SPORTS 3",
        matchDate = "21 Eylül Pazartesi 18:30 TSİ"
      )
    )

    val (m43Mk, m43Ai) = AiOddsEngine.generateInitialMarkets("m43", 1720.0, 1710.0, 1.82, 1.82)
    list.add(
      Match(
        id = "m43",
        sport = Sport.BASKETBALL,
        league = "EuroLeague",
        homeTeam = "Real Madrid",
        awayTeam = "Panathinaikos",
        homeScore = 88,
        awayScore = 85,
        minute = 40,
        status = MatchStatus.FINISHED,
        markets = m43Mk,
        statistics = MatchStatistics(shotsHome = 34, shotsAway = 32, dangerousAttacksHome = 12, dangerousAttacksAway = 10, xgHome = 2.45, xgAway = 2.35),
        aiPrediction = m43Ai,
        events = listOf(
          MatchEvent("e61", 15, EventType.SHOT_ON_TARGET, "Real Madrid", "Facundo Campazzo", "Harika asist ve turnike 🏀"),
          MatchEvent("e62", 38, EventType.SHOT_ON_TARGET, "Panathinaikos", "Kendrick Nunn", "Kritik üçlük 🏀"),
          MatchEvent("e63", 40, EventType.SHOT_ON_TARGET, "Real Madrid", "Walter Tavares", "Blok ve smaçla maçı bitirdi 🏀")
        ),
        startTime = "21:45",
        halfTimeHomeScore = 44,
        halfTimeAwayScore = 43,
        stadium = "WiZink Center, Madrid",
        tvBroadcast = "S Sport Plus",
        matchDate = "22 Eylül Salı 21:45 TSİ"
      )
    )

    val (m44Mk, m44Ai) = AiOddsEngine.generateInitialMarkets("m44", 1750.0, 1700.0, 1.70, 1.95)
    list.add(
      Match(
        id = "m44",
        sport = Sport.BASKETBALL,
        league = "NBA",
        homeTeam = "Boston Celtics",
        awayTeam = "Los Angeles Lakers",
        homeScore = 112,
        awayScore = 108,
        minute = 48,
        status = MatchStatus.FINISHED,
        markets = m44Mk,
        statistics = MatchStatistics(shotsHome = 44, shotsAway = 40, xgHome = 2.80, xgAway = 2.60),
        aiPrediction = m44Ai,
        events = listOf(
          MatchEvent("e64", 20, EventType.SHOT_ON_TARGET, "Boston Celtics", "Jayson Tatum", "Step-back üçlük 🏀"),
          MatchEvent("e65", 46, EventType.SHOT_ON_TARGET, "Los Angeles Lakers", "LeBron James", "Hızlı hücum smacı 🏀")
        ),
        startTime = "03:00",
        halfTimeHomeScore = 58,
        halfTimeAwayScore = 55,
        stadium = "TD Garden, Boston",
        tvBroadcast = "S Sport",
        matchDate = "22 Eylül Salı 03:00 TSİ"
      )
    )

    val (m45Mk, m45Ai) = AiOddsEngine.generateInitialMarkets("m45", 1920.0, 1850.0, 1.72, 1.98)
    list.add(
      Match(
        id = "m45",
        sport = Sport.TENNIS,
        league = "ATP Turin Zirvesi",
        homeTeam = "Carlos Alcaraz",
        awayTeam = "Daniil Medvedev",
        homeScore = 3, // Setler 3 - 2
        awayScore = 2,
        minute = 245,
        status = MatchStatus.FINISHED,
        markets = m45Mk,
        statistics = MatchStatistics(
          possessionHome = 52,
          possessionAway = 48,
          shotsHome = 14, // Aces
          shotsAway = 19,
          shotsOnTargetHome = 48, // Winners
          shotsOnTargetAway = 36,
          ballInPlayTime = "4:05:30"
        ),
        aiPrediction = m45Ai,
        events = listOf(
          MatchEvent("e66", 45, EventType.SHOT_ON_TARGET, "Carlos Alcaraz", "Alcaraz", "1. Set Alcaraz (6-3) 🎾"),
          MatchEvent("e67", 95, EventType.SHOT_ON_TARGET, "Daniil Medvedev", "Medvedev", "2. Set Medvedev (4-6) 🎾"),
          MatchEvent("e68", 145, EventType.SHOT_ON_TARGET, "Daniil Medvedev", "Medvedev", "3. Set Medvedev (3-6) 🎾"),
          MatchEvent("e69", 195, EventType.SHOT_ON_TARGET, "Carlos Alcaraz", "Alcaraz", "4. Set Alcaraz (7-5) 🎾"),
          MatchEvent("e70", 240, EventType.SHOT_ON_TARGET, "Carlos Alcaraz", "Alcaraz", "Karar seti ve galibiyet Alcaraz'ın! (6-4) 🎾")
        ),
        startTime = "16:00",
        stadium = "Pala Alpitour, Turin",
        tvBroadcast = "Eurosport 2",
        matchDate = "22 Eylül Salı 16:00 TSİ"
      )
    )

    return list
  }

  fun getH2HForMatch(match: Match): MatchH2H {
    val h = match.homeTeam
    val a = match.awayTeam

    return when {
      (h.contains("Galatasaray") && a.contains("Fenerbahçe")) || (h.contains("Fenerbahçe") && a.contains("Galatasaray")) -> {
        MatchH2H(
          totalPlayed = 402,
          homeWins = 127,
          draws = 124,
          awayWins = 151,
          recentMatches = listOf(
            H2HMatch("21.09.2024", "Süper Lig", "Fenerbahçe 1 - 3 Galatasaray", "G"),
            H2HMatch("19.05.2024", "Süper Lig", "Galatasaray 0 - 1 Fenerbahçe", "M"),
            H2HMatch("07.04.2024", "Süper Kupa", "Galatasaray 3 - 0 Fenerbahçe", "G"),
            H2HMatch("24.12.2023", "Süper Lig", "Fenerbahçe 0 - 0 Galatasaray", "B"),
            H2HMatch("04.06.2023", "Süper Lig", "Galatasaray 3 - 0 Fenerbahçe", "G")
          )
        )
      }
      (h.contains("Real Madrid") && a.contains("Barcelona")) || (h.contains("Barcelona") && a.contains("Real Madrid")) -> {
        MatchH2H(
          totalPlayed = 257,
          homeWins = 105,
          draws = 52,
          awayWins = 100,
          recentMatches = listOf(
            H2HMatch("21.04.2024", "La Liga", "Real Madrid 3 - 2 Barcelona", "G"),
            H2HMatch("14.01.2024", "Supercopa", "Real Madrid 4 - 1 Barcelona", "G"),
            H2HMatch("28.10.2023", "La Liga", "Barcelona 1 - 2 Real Madrid", "G"),
            H2HMatch("05.04.2023", "Copa del Rey", "Barcelona 0 - 4 Real Madrid", "G"),
            H2HMatch("19.03.2023", "La Liga", "Barcelona 2 - 1 Real Madrid", "M")
          )
        )
      }
      (h.contains("Arsenal") && a.contains("Manchester City")) || (h.contains("Manchester City") && a.contains("Arsenal")) -> {
        MatchH2H(
          totalPlayed = 211,
          homeWins = 99,
          draws = 46,
          awayWins = 66,
          recentMatches = listOf(
            H2HMatch("22.09.2024", "Premier League", "Manchester City 2 - 2 Arsenal", "B"),
            H2HMatch("31.03.2024", "Premier League", "Manchester City 0 - 0 Arsenal", "B"),
            H2HMatch("08.10.2023", "Premier League", "Arsenal 1 - 0 Manchester City", "G"),
            H2HMatch("06.08.2023", "Community Shield", "Arsenal 1 - 1 (P) Man City", "G"),
            H2HMatch("26.04.2023", "Premier League", "Manchester City 4 - 1 Arsenal", "M")
          )
        )
      }
      (h.contains("Beşiktaş") && a.contains("Trabzonspor")) || (h.contains("Trabzonspor") && a.contains("Beşiktaş")) -> {
        MatchH2H(
          totalPlayed = 139,
          homeWins = 55,
          draws = 37,
          awayWins = 47,
          recentMatches = listOf(
            H2HMatch("15.09.2024", "Süper Lig", "Trabzonspor 1 - 1 Beşiktaş", "B"),
            H2HMatch("23.05.2024", "Türkiye Kupası", "Beşiktaş 3 - 2 Trabzonspor", "G"),
            H2HMatch("04.02.2024", "Süper Lig", "Beşiktaş 2 - 0 Trabzonspor", "G"),
            H2HMatch("17.09.2023", "Süper Lig", "Trabzonspor 3 - 0 Beşiktaş", "M"),
            H2HMatch("16.04.2023", "Süper Lig", "Trabzonspor 0 - 0 Beşiktaş", "B")
          )
        )
      }
      (h.contains("Inter") && a.contains("Juventus")) || (h.contains("Juventus") && a.contains("Inter")) -> {
        MatchH2H(
          totalPlayed = 251,
          homeWins = 77,
          draws = 62,
          awayWins = 112,
          recentMatches = listOf(
            H2HMatch("04.02.2024", "Serie A", "Inter 1 - 0 Juventus", "G"),
            H2HMatch("26.11.2023", "Serie A", "Juventus 1 - 1 Inter", "B"),
            H2HMatch("26.04.2023", "Coppa Italia", "Inter 1 - 0 Juventus", "G"),
            H2HMatch("04.04.2023", "Coppa Italia", "Juventus 1 - 1 Inter", "B"),
            H2HMatch("19.03.2023", "Serie A", "Inter 0 - 1 Juventus", "M")
          )
        )
      }
      (h.contains("Fenerbahçe Beko") || a.contains("Fenerbahçe Beko")) -> {
        MatchH2H(
          totalPlayed = 36,
          homeWins = 19,
          draws = 0,
          awayWins = 17,
          recentMatches = listOf(
            H2HMatch("24.05.2024", "EuroLeague F4", "Panathinaikos 73 - 57 Fenerbahçe", "M"),
            H2HMatch("09.02.2024", "EuroLeague", "Panathinaikos 74 - 63 Fenerbahçe", "M"),
            H2HMatch("17.10.2023", "EuroLeague", "Fenerbahçe 83 - 69 Panathinaikos", "G"),
            H2HMatch("10.01.2023", "EuroLeague", "Panathinaikos 88 - 94 Fenerbahçe", "G"),
            H2HMatch("18.11.2022", "EuroLeague", "Fenerbahçe 107 - 77 Panathinaikos", "G")
          )
        )
      }
      (h.contains("Sinner") || a.contains("Sinner")) -> {
        MatchH2H(
          totalPlayed = 11,
          homeWins = 5,
          draws = 0,
          awayWins = 6,
          recentMatches = listOf(
            H2HMatch("02.10.2024", "ATP Beijing Final", "Alcaraz 2 - 1 Sinner (6-7, 6-4, 7-6)", "M"),
            H2HMatch("07.06.2024", "Roland Garros SF", "Alcaraz 3 - 2 Sinner (2-6, 6-3, 3-6, 6-4, 6-3)", "M"),
            H2HMatch("17.03.2024", "Indian Wells SF", "Alcaraz 2 - 1 Sinner (1-6, 6-3, 6-2)", "M"),
            H2HMatch("03.10.2023", "ATP Beijing SF", "Sinner 2 - 0 Alcaraz (7-6, 6-1)", "G"),
            H2HMatch("01.04.2023", "Miami Masters SF", "Sinner 2 - 1 Alcaraz (6-7, 6-4, 6-2)", "G")
          )
        )
      }
      else -> {
        MatchH2H(
          totalPlayed = 42,
          homeWins = 18,
          draws = 11,
          awayWins = 13,
          recentMatches = listOf(
            H2HMatch("12.04.2024", match.league, "${match.homeTeam} 2 - 1 ${match.awayTeam}", "G"),
            H2HMatch("18.11.2023", match.league, "${match.awayTeam} 1 - 1 ${match.homeTeam}", "B"),
            H2HMatch("24.03.2023", match.league, "${match.homeTeam} 3 - 1 ${match.awayTeam}", "G"),
            H2HMatch("08.10.2022", match.league, "${match.awayTeam} 2 - 0 ${match.homeTeam}", "M"),
            H2HMatch("15.02.2022", match.league, "${match.homeTeam} 2 - 2 ${match.awayTeam}", "B")
          )
        )
      }
    }
  }

  fun getLineupsForMatch(match: Match): MatchLineups {
    return MackolikComprehensivePlayerDatabase.getLineupsForMatch(match)
  }
}
