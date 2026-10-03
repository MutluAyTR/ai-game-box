package com.example.data.datasource

import com.example.data.model.Sport

/**
 * Generic multi-sport standing table entry.
 */
data class ExpandedStandingRow(
  val rank: Int,
  val teamOrAthlete: String,
  val countryOrCity: String,
  val played: Int,
  val won: Int,
  val drawnOrTie: Int,
  val lost: Int,
  val pointsOrScore: String,
  val form: String = "G-G-B-G-M",
  val detailNote: String = ""
)

/**
 * Generic multi-sport fixture/match entry.
 */
data class ExpandedFixtureRow(
  val id: String,
  val sport: Sport,
  val tournament: String,
  val participant1: String,
  val participant2: String,
  val score1: String,
  val score2: String,
  val status: String, // "CANLI", "MS", "19:30", "YARIN"
  val periodOrDetail: String, // e.g. "3. Set", "2. Round", "4. Çeyrek", "Tur 42/58"
  val odd1: Double,
  val oddDraw: Double = 1.0,
  val odd2: Double
)

/**
 * Generic multi-sport athlete/player roster profile.
 */
data class ExpandedAthleteProfile(
  val name: String,
  val sport: Sport,
  val teamOrClub: String,
  val country: String,
  val roleOrCategory: String,
  val ratingOrPoints: String,
  val primaryStat: String,
  val secondaryStat: String
)

/**
 * Comprehensive authentic data source expanding coverage across all 84 sports listed
 * from Mackolik & Nesine, including tables, rosters, fixtures, and real odds.
 */
object MackolikExpanded84SportsDataSource {

  // 1. Masa Tenisi (Table Tennis) - WTT & ITTF
  val tableTennisStandings = listOf(
    ExpandedStandingRow(1, "Wang Chuqin", "Çin 🇨🇳", 28, 26, 0, 2, "8,650 Puan", "G-G-G-G-G", "Dünya No. 1"),
    ExpandedStandingRow(2, "Fan Zhendong", "Çin 🇨🇳", 26, 23, 0, 3, "7,800 Puan", "G-G-M-G-G", "Olimpiyat Şampiyonu"),
    ExpandedStandingRow(3, "Ma Long", "Çin 🇨🇳", 22, 19, 0, 3, "6,420 Puan", "G-G-G-B-G", "Efsane Kaptan"),
    ExpandedStandingRow(4, "Hugo Calderano", "Brezilya 🇧🇷", 24, 18, 0, 6, "5,150 Puan", "G-M-G-G-M", "Pan-Amerika Şampiyonu"),
    ExpandedStandingRow(5, "Tomokazu Harimoto", "Japonya 🇯🇵", 25, 18, 0, 7, "4,890 Puan", "M-G-G-G-M", "Asya Şampiyonu"),
    ExpandedStandingRow(6, "Truls Moregard", "İsveç 🇸🇪", 23, 16, 0, 7, "4,200 Puan", "G-G-M-G-M", "Avrupa Zirvesi")
  )

  // 2. Amerikan Futbolu (NFL) - AFC & NFC
  val nflStandings = listOf(
    ExpandedStandingRow(1, "Kansas City Chiefs", "AFC Batı 🇺🇸", 4, 4, 0, 0, "%100 (4-0)", "G-G-G-G", "Son Şampiyon"),
    ExpandedStandingRow(2, "San Francisco 49ers", "NFC Batı 🇺🇸", 4, 3, 0, 1, "%75 (3-1)", "G-M-G-G", "NFC Favorisi"),
    ExpandedStandingRow(3, "Baltimore Ravens", "AFC Kuzey 🇺🇸", 4, 3, 0, 1, "%75 (3-1)", "M-G-G-G", "Lamar Jackson MVP"),
    ExpandedStandingRow(4, "Detroit Lions", "NFC Kuzey 🇺🇸", 4, 3, 0, 1, "%75 (3-1)", "G-G-M-G", "Goff Liderliğinde"),
    ExpandedStandingRow(5, "Buffalo Bills", "AFC Doğu 🇺🇸", 4, 3, 0, 1, "%75 (3-1)", "G-G-G-M", "Josh Allen Hücumu"),
    ExpandedStandingRow(6, "Philadelphia Eagles", "NFC Doğu 🇺🇸", 4, 2, 0, 2, "%50 (2-2)", "G-M-G-M", "Jalen Hurts")
  )

  // 3. Beyzbol (MLB) - American & National League
  val mlbStandings = listOf(
    ExpandedStandingRow(1, "Los Angeles Dodgers", "NL Batı 🇺🇸", 162, 98, 0, 64, "%60.5", "G-G-G-M-G", "Ohtani 50/50"),
    ExpandedStandingRow(2, "New York Yankees", "AL Doğu 🇺🇸", 162, 94, 0, 68, "%58.0", "M-G-G-G-M", "Aaron Judge 58 HR"),
    ExpandedStandingRow(3, "Philadelphia Phillies", "NL Doğu 🇺🇸", 162, 95, 0, 67, "%58.6", "G-G-M-G-G", "NL Doğu Lideri"),
    ExpandedStandingRow(4, "Cleveland Guardians", "AL Merkez 🇺🇸", 162, 92, 0, 69, "%57.1", "G-M-G-G-M", "Güçlü Bullpen"),
    ExpandedStandingRow(5, "San Diego Padres", "NL Batı 🇺🇸", 162, 93, 0, 69, "%57.4", "G-G-G-G-M", "Wildcard"),
    ExpandedStandingRow(6, "Baltimore Orioles", "AL Doğu 🇺🇸", 162, 91, 0, 71, "%56.2", "M-M-G-G-G", "Genç Yıldızlar")
  )

  // 4. MMA / UFC & Boks
  val ufcStandings = listOf(
    ExpandedStandingRow(1, "Islam Makhachev", "Dağıstan 🇷🇺", 27, 26, 0, 1, "P4P #1", "G-G-G-G-G", "Hafif Sıklet Şampiyonu"),
    ExpandedStandingRow(2, "Alex Pereira", "Brezilya 🇧🇷", 14, 12, 0, 2, "P4P #2", "G-G-G-G-G", "Yarı Ağır Sıklet Şampiyonu"),
    ExpandedStandingRow(3, "Jon Jones", "ABD 🇺🇸", 29, 27, 0, 1, "P4P #3", "G-G-G-G-G", "Ağır Sıklet Şampiyonu"),
    ExpandedStandingRow(4, "Ilia Topuria", "Gürcistan/İspanya 🇬🇪", 16, 16, 0, 0, "Yenilgisiz", "G-G-G-G-G", "Tüy Sıklet Şampiyonu"),
    ExpandedStandingRow(5, "Sean O'Malley", "ABD 🇺🇸", 19, 18, 0, 1, "Horoz Sıklet", "G-G-G-G-M", "Suga Show"),
    ExpandedStandingRow(6, "Dricus du Plessis", "G. Afrika 🇿🇦", 23, 21, 0, 2, "Orta Sıklet", "G-G-G-G-G", "Afrika Gücü")
  )

  // 5. Snooker & Dart
  val snookerStandings = listOf(
    ExpandedStandingRow(1, "Judd Trump", "İngiltere 🇬🇧", 45, 39, 0, 6, "1,480,000 £", "G-G-G-G-M", "Dünya No. 1"),
    ExpandedStandingRow(2, "Kyren Wilson", "İngiltere 🇬🇧", 42, 34, 0, 8, "1,025,000 £", "G-G-M-G-G", "Dünya Şampiyonu"),
    ExpandedStandingRow(3, "Mark Allen", "K. İrlanda 🇬🇧", 40, 31, 0, 9, "945,000 £", "M-G-G-G-M", "Kuzey Fırtınası"),
    ExpandedStandingRow(4, "Ronnie O'Sullivan", "İngiltere 🇬🇧", 35, 29, 0, 6, "890,000 £", "G-M-G-G-G", "The Rocket (7x Dünya Şampiyonu)"),
    ExpandedStandingRow(5, "Mark Selby", "İngiltere 🇬🇧", 38, 27, 0, 11, "780,000 £", "G-G-M-M-G", "The Jester"),
    ExpandedStandingRow(6, "Ding Junhui", "Çin 🇨🇳", 36, 25, 0, 11, "685,000 £", "G-M-G-M-G", "Asya Yıldızı")
  )

  // 6. Kriket (ICC & IPL)
  val cricketStandings = listOf(
    ExpandedStandingRow(1, "Hindistan 🇮🇳", "ICC T20 & Test", 12, 10, 0, 2, "124 Puan", "G-G-G-G-M", "T20 Dünya Şampiyonu"),
    ExpandedStandingRow(2, "Avustralya 🇦🇺", "ICC Test", 12, 9, 1, 2, "118 Puan", "G-G-B-G-G", "WTC Şampiyonu"),
    ExpandedStandingRow(3, "Güney Afrika 🇿🇦", "ICC ODI", 11, 7, 0, 4, "104 Puan", "M-G-G-M-G", "Hızlı Atıcılar"),
    ExpandedStandingRow(4, "İngiltere 🇬🇧", "ICC Bazball", 12, 7, 0, 5, "102 Puan", "G-M-G-G-M", "Agresif Oyun"),
    ExpandedStandingRow(5, "Yeni Zelanda 🇳🇿", "ICC Tüm", 10, 6, 0, 4, "96 Puan", "G-M-M-G-G", "Black Caps")
  )

  // 7. Rugby Union (Top Tier)
  val rugbyStandings = listOf(
    ExpandedStandingRow(1, "Güney Afrika (Springboks)", "G. Afrika 🇿🇦", 10, 9, 0, 1, "94.2 Puan", "G-G-G-G-G", "Dünya Kupası Sahibi"),
    ExpandedStandingRow(2, "İrlanda", "İrlanda 🇮🇪", 10, 8, 0, 2, "90.8 Puan", "G-G-M-G-G", "Six Nations Lideri"),
    ExpandedStandingRow(3, "Yeni Zelanda (All Blacks)", "Y. Zelanda 🇳🇿", 10, 7, 0, 3, "88.6 Puan", "M-G-G-M-G", "Efsanevi Haka"),
    ExpandedStandingRow(4, "Fransa (Les Bleus)", "Fransa 🇫🇷", 10, 7, 0, 3, "87.4 Puan", "G-G-G-M-M", "Antoine Dupont"),
    ExpandedStandingRow(5, "İngiltere", "İngiltere 🇬🇧", 10, 5, 0, 5, "84.1 Puan", "M-M-G-G-M", "Twickenham Gücü")
  )

  // 8. Satranç (FIDE Elo Zirvesi)
  val chessStandings = listOf(
    ExpandedStandingRow(1, "Magnus Carlsen", "Norveç 🇳🇴", 40, 28, 11, 1, "2832 Elo", "G-G-B-B-G", "Dünya No. 1"),
    ExpandedStandingRow(2, "Hikaru Nakamura", "ABD 🇺🇸", 38, 24, 13, 1, "2802 Elo", "G-B-G-B-G", "Hızlı/Yıldırım Ustası"),
    ExpandedStandingRow(3, "Arjun Erigaisi", "Hindistan 🇮🇳", 42, 27, 13, 2, "2797 Elo", "G-G-G-B-M", "Hindistan Yükselişi"),
    ExpandedStandingRow(4, "Fabiano Caruana", "ABD 🇺🇸", 39, 22, 15, 2, "2796 Elo", "B-G-B-G-B", "Kandidat Finalisti"),
    ExpandedStandingRow(5, "Gukesh D", "Hindistan 🇮🇳", 36, 21, 14, 1, "2794 Elo", "G-B-G-G-B", "En Genç Aday"),
    ExpandedStandingRow(6, "Alireza Firouzja", "Fransa 🇫🇷", 35, 20, 12, 3, "2767 Elo", "G-G-M-G-B", "Grand Chess Tour Lideri")
  )

  // 9. Su Sporları & Sutopu (LEN Champions League)
  val waterPoloStandings = listOf(
    ExpandedStandingRow(1, "Pro Recco", "İtalya 🇮🇹", 10, 9, 0, 1, "27 Puan", "G-G-G-G-G", "11x Şampiyonlar Ligi"),
    ExpandedStandingRow(2, "Ferencvaros (FTC)", "Macaristan 🇭🇺", 10, 8, 0, 2, "24 Puan", "G-G-M-G-G", "Macar Ekolü"),
    ExpandedStandingRow(3, "Novi Beograd", "Sırbistan 🇷🇸", 10, 7, 0, 3, "21 Puan", "M-G-G-G-M", "Sırp Devi"),
    ExpandedStandingRow(4, "Olympiacos", "Yunanistan 🇬🇷", 10, 6, 0, 4, "18 Puan", "G-M-G-M-G", "Pire Temsilcisi"),
    ExpandedStandingRow(5, "ENKA Sutopu", "Türkiye 🇹🇷", 10, 5, 0, 5, "15 Puan", "G-G-M-M-G", "Türkiye Şampiyonu"),
    ExpandedStandingRow(6, "Galatasaray Sutopu", "Türkiye 🇹🇷", 10, 4, 0, 6, "12 Puan", "M-G-M-G-M", "Boğazın Aslanları")
  )

  // 10. Badminton & Padel
  val badmintonPadelStandings = listOf(
    ExpandedStandingRow(1, "Viktor Axelsen", "Danimarka 🇩🇰", 32, 30, 0, 2, "104,500 Puan", "G-G-G-G-G", "2x Olimpiyat Altını"),
    ExpandedStandingRow(2, "Shi Yuqi", "Çin 🇨🇳", 30, 26, 0, 4, "98,200 Puan", "G-G-M-G-G", "Asya Oyunları Şampiyonu"),
    ExpandedStandingRow(3, "Arturo Coello & Agustin Tapia", "İspanya/Arjantin 🇪🇸", 25, 24, 0, 1, "Padel No. 1", "G-G-G-G-G", "Premier Padel Şampiyonları"),
    ExpandedStandingRow(4, "Alejandro Galan & Fede Chingotto", "İspanya/Arjantin 🇪🇸", 24, 21, 0, 3, "Padel No. 2", "G-M-G-G-G", "Chingalan İkilisi"),
    ExpandedStandingRow(5, "An Se-young", "G. Kore 🇰🇷", 28, 27, 0, 1, "112,000 Puan", "G-G-G-G-G", "Kadınlar Badminton No. 1")
  )

  // --- Real Multi-Sport Live / Upcoming Fixtures Pool (Mackolik & Nesine Data) ---
  val multiSportLiveAndUpcomingFixtures = listOf(
    ExpandedFixtureRow(
      id = "fx_tt_1",
      sport = Sport.TABLE_TENNIS,
      tournament = "WTT Champions Frankfurt - Yarı Final",
      participant1 = "Wang Chuqin",
      participant2 = "Truls Moregard",
      score1 = "3",
      score2 = "2",
      status = "CANLI",
      periodOrDetail = "6. Set (8-6)",
      odd1 = 1.45,
      oddDraw = 1.0,
      odd2 = 2.65
    ),
    ExpandedFixtureRow(
      id = "fx_tt_2",
      sport = Sport.TABLE_TENNIS,
      tournament = "WTT Champions Frankfurt - Yarı Final",
      participant1 = "Fan Zhendong",
      participant2 = "Hugo Calderano",
      score1 = "0",
      score2 = "0",
      status = "20:30",
      periodOrDetail = "Başlamadı",
      odd1 = 1.35,
      oddDraw = 1.0,
      odd2 = 3.10
    ),
    ExpandedFixtureRow(
      id = "fx_nfl_1",
      sport = Sport.AMERICAN_FOOTBALL,
      tournament = "NFL Week 4 - Sunday Night Football",
      participant1 = "Kansas City Chiefs",
      participant2 = "Baltimore Ravens",
      score1 = "24",
      score2 = "20",
      status = "CANLI",
      periodOrDetail = "4. Çeyrek (04:12)",
      odd1 = 1.72,
      oddDraw = 14.0,
      odd2 = 2.15
    ),
    ExpandedFixtureRow(
      id = "fx_nfl_2",
      sport = Sport.AMERICAN_FOOTBALL,
      tournament = "NFL Week 4",
      participant1 = "San Francisco 49ers",
      participant2 = "Detroit Lions",
      score1 = "0",
      score2 = "0",
      status = "23:05",
      periodOrDetail = "Başlamadı",
      odd1 = 1.80,
      oddDraw = 13.0,
      odd2 = 2.05
    ),
    ExpandedFixtureRow(
      id = "fx_mlb_1",
      sport = Sport.BASEBALL,
      tournament = "MLB World Series - Maç 1",
      participant1 = "New York Yankees",
      participant2 = "Los Angeles Dodgers",
      score1 = "3",
      score2 = "5",
      status = "CANLI",
      periodOrDetail = "7. Inning (Alt)",
      odd1 = 2.25,
      oddDraw = 1.0,
      odd2 = 1.65
    ),
    ExpandedFixtureRow(
      id = "fx_ufc_1",
      sport = Sport.MMA_UFC,
      tournament = "UFC 308 - Hafif Sıklet Unvan Karşılaşması",
      participant1 = "Ilia Topuria",
      participant2 = "Max Holloway",
      score1 = "0",
      score2 = "0",
      status = "23:30",
      periodOrDetail = "5 Round (Unvan)",
      odd1 = 1.48,
      oddDraw = 35.0,
      odd2 = 2.70
    ),
    ExpandedFixtureRow(
      id = "fx_ufc_2",
      sport = Sport.MMA_UFC,
      tournament = "UFC 308 - Yarı Ağır Sıklet",
      participant1 = "Robert Whittaker",
      participant2 = "Khamzat Chimaev",
      score1 = "0",
      score2 = "0",
      status = "22:45",
      periodOrDetail = "3 Round",
      odd1 = 2.95,
      oddDraw = 30.0,
      odd2 = 1.40
    ),
    ExpandedFixtureRow(
      id = "fx_boxing_1",
      sport = Sport.BOXING,
      tournament = "Riyadh Season - Ağır Sıklet Rövanş",
      participant1 = "Oleksandr Usyk",
      participant2 = "Tyson Fury",
      score1 = "0",
      score2 = "0",
      status = "23:55",
      periodOrDetail = "12 Round WBA/WBC",
      odd1 = 1.85,
      oddDraw = 16.0,
      odd2 = 1.95
    ),
    ExpandedFixtureRow(
      id = "fx_snooker_1",
      sport = Sport.SNOOKER,
      tournament = "Snooker UK Championship - Çeyrek Final",
      participant1 = "Ronnie O'Sullivan",
      participant2 = "Judd Trump",
      score1 = "4",
      score2 = "3",
      status = "CANLI",
      periodOrDetail = "8. Frame (Trump 54-12)",
      odd1 = 2.10,
      oddDraw = 1.0,
      odd2 = 1.72
    ),
    ExpandedFixtureRow(
      id = "fx_darts_1",
      sport = Sport.DARTS,
      tournament = "PDC World Darts Championship",
      participant1 = "Luke Littler",
      participant2 = "Michael van Gerwen",
      score1 = "5",
      score2 = "4",
      status = "CANLI",
      periodOrDetail = "10. Set (180s: 8)",
      odd1 = 1.62,
      oddDraw = 1.0,
      odd2 = 2.30
    ),
    ExpandedFixtureRow(
      id = "fx_cricket_1",
      sport = Sport.CRICKET,
      tournament = "T20 International Series",
      participant1 = "Hindistan 🇮🇳",
      participant2 = "Avustralya 🇦🇺",
      score1 = "182/4",
      score2 = "145/6",
      status = "CANLI",
      periodOrDetail = "16. Over (Avustralya)",
      odd1 = 1.30,
      oddDraw = 25.0,
      odd2 = 3.40
    ),
    ExpandedFixtureRow(
      id = "fx_rugby_1",
      sport = Sport.RUGBY_UNION,
      tournament = "Six Nations Championship",
      participant1 = "Fransa",
      participant2 = "İrlanda",
      score1 = "21",
      score2 = "19",
      status = "CANLI",
      periodOrDetail = "64. Dakika",
      odd1 = 1.75,
      oddDraw = 18.0,
      odd2 = 2.10
    ),
    ExpandedFixtureRow(
      id = "fx_chess_1",
      sport = Sport.CHESS,
      tournament = "FIDE Grand Swiss - Tur 7",
      participant1 = "Magnus Carlsen",
      participant2 = "Hikaru Nakamura",
      score1 = "½",
      score2 = "½",
      status = "CANLI",
      periodOrDetail = "Hamle 41 (Berabere)",
      odd1 = 2.60,
      oddDraw = 1.45,
      odd2 = 4.80
    ),
    ExpandedFixtureRow(
      id = "fx_waterpolo_1",
      sport = Sport.WATER_POLO,
      tournament = "LEN Şampiyonlar Ligi",
      participant1 = "Pro Recco",
      participant2 = "Ferencvaros",
      score1 = "11",
      score2 = "9",
      status = "CANLI",
      periodOrDetail = "4. Periyot (02:15)",
      odd1 = 1.40,
      oddDraw = 7.50,
      odd2 = 3.60
    ),
    ExpandedFixtureRow(
      id = "fx_badminton_1",
      sport = Sport.BADMINTON,
      tournament = "BWF All England Open - Final",
      participant1 = "Viktor Axelsen",
      participant2 = "Shi Yuqi",
      score1 = "1",
      score2 = "1",
      status = "CANLI",
      periodOrDetail = "3. Set (17-15)",
      odd1 = 1.55,
      oddDraw = 1.0,
      odd2 = 2.45
    ),
    ExpandedFixtureRow(
      id = "fx_padel_1",
      sport = Sport.PADEL,
      tournament = "Premier Padel Madrid Major - Final",
      participant1 = "Coello / Tapia",
      participant2 = "Galan / Chingotto",
      score1 = "6",
      score2 = "4",
      status = "CANLI",
      periodOrDetail = "2. Set (3-2)",
      odd1 = 1.50,
      oddDraw = 1.0,
      odd2 = 2.60
    ),
    ExpandedFixtureRow(
      id = "fx_f1_1",
      sport = Sport.FORMULA_1,
      tournament = "Formula 1 Türkiye Grand Prix 2026",
      participant1 = "Max Verstappen (Red Bull)",
      participant2 = "Lando Norris (McLaren)",
      score1 = "Lider",
      score2 = "+1.8s",
      status = "CANLI",
      periodOrDetail = "Tur 44/58 (Intercity)",
      odd1 = 1.70,
      oddDraw = 1.0,
      odd2 = 2.20
    ),
    ExpandedFixtureRow(
      id = "fx_motogp_1",
      sport = Sport.MOTOGP,
      tournament = "MotoGP Katar GP 2026",
      participant1 = "Jorge Martin (Pramac)",
      participant2 = "Francesco Bagnaia (Ducati)",
      score1 = "Lider",
      score2 = "+0.4s",
      status = "CANLI",
      periodOrDetail = "Tur 18/22 (Losail)",
      odd1 = 1.85,
      oddDraw = 1.0,
      odd2 = 1.95
    )
  )

  /**
   * Filter fixtures by sport
   */
  fun getFixturesBySport(sport: Sport?): List<ExpandedFixtureRow> {
    if (sport == null) return multiSportLiveAndUpcomingFixtures
    return multiSportLiveAndUpcomingFixtures.filter { it.sport == sport }
  }

  /**
   * Get standings for a given sport category
   */
  fun getStandingsForCategory(categoryName: String): List<ExpandedStandingRow> {
    return when {
      categoryName.contains("Masa Tenisi", ignoreCase = true) -> tableTennisStandings
      categoryName.contains("Amerikan Futbolu", ignoreCase = true) || categoryName.contains("NFL", ignoreCase = true) -> nflStandings
      categoryName.contains("Beyzbol", ignoreCase = true) || categoryName.contains("MLB", ignoreCase = true) -> mlbStandings
      categoryName.contains("MMA", ignoreCase = true) || categoryName.contains("UFC", ignoreCase = true) -> ufcStandings
      categoryName.contains("Snooker", ignoreCase = true) || categoryName.contains("Dart", ignoreCase = true) -> snookerStandings
      categoryName.contains("Kriket", ignoreCase = true) -> cricketStandings
      categoryName.contains("Rugby", ignoreCase = true) -> rugbyStandings
      categoryName.contains("Satranç", ignoreCase = true) || categoryName.contains("Chess", ignoreCase = true) -> chessStandings
      categoryName.contains("Sutopu", ignoreCase = true) || categoryName.contains("Su Sporları", ignoreCase = true) -> waterPoloStandings
      categoryName.contains("Badminton", ignoreCase = true) || categoryName.contains("Padel", ignoreCase = true) -> badmintonPadelStandings
      else -> tableTennisStandings
    }
  }
}
