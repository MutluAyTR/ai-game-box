package com.example.data.model

enum class Sport(val displayName: String, val iconRes: String) {
  FOOTBALL("Futbol", "⚽"),
  BASKETBALL("Basketbol", "🏀"),
  MOTORSPORTS("Motor Sporları", "🏎️"),
  TENNIS("Tenis", "🎾"),
  VOLLEYBALL("Voleybol", "🏐"),
  ICE_HOCKEY("Buz Hokeyi", "🏒"),
  HANDBALL("Hentbol", "🤾"),
  ESPORTS("E-Spor", "🎮"),
  SNOOKER("Snooker & Dart", "🎯"),
  BASEBALL("Beyzbol", "⚾"),
  TABLE_TENNIS("Masa Tenisi", "🏓"),
  AMERICAN_FOOTBALL("Amerikan Futbolu", "🏈"),
  SOFTBALL("Softbol", "🥎"),
  CRICKET("Kriket", "🏏"),
  RUGBY_UNION("Rugby Union", "🏉"),
  RUGBY_LEAGUE("Rugby League", "🏉"),
  FORMULA_1("Formula 1", "🏎️"),
  FORMULA_2("Formula 2", "🏁"),
  FORMULA_3("Formula 3", "🏁"),
  MOTOGP("MotoGP", "🏍️"),
  NASCAR("NASCAR", "🏎️"),
  INDYCAR("IndyCar", "🏎️"),
  WRC_RALLY("WRC / Ralli", "🏎️"),
  BOXING("Boks", "🥊"),
  MMA_UFC("MMA / UFC", "🥋"),
  KICKBOXING("Kick Boks", "🥋"),
  MUAY_THAI("Muay Thai", "🥋"),
  JUDO("Judo", "🥋"),
  KARATE("Karate", "🥋"),
  TAEKWONDO("Tekvando", "🥋"),
  WRESTLING("Güreş", "🤼"),
  BILLIARDS("Bilardo", "🎱"),
  DARTS("Dart", "🎯"),
  GOLF("Golf", "⛳"),
  BADMINTON("Badminton", "🏸"),
  FIELD_HOCKEY("Çim Hokeyi", "🏑"),
  INDOOR_HOCKEY("Salon Hokeyi", "🏑"),
  LACROSSE("Lacrosse", "🥍"),
  WATER_POLO("Sutopu", "🤽"),
  SWIMMING("Yüzme", "🏊"),
  OPEN_WATER_SWIMMING("Açık Su Yüzme", "🏊"),
  ROWING("Kürek", "🚣"),
  CANOEING("Kano", "🛶"),
  SAILING("Yelken", "⛵"),
  ROAD_CYCLING("Yol Bisikleti", "🚴"),
  MOUNTAIN_BIKING("Dağ Bisikleti", "🚵"),
  TRACK_CYCLING("Pist Bisikleti", "🚴"),
  BMX("BMX", "🚲"),
  ATHLETICS("Atletizm", "🏃"),
  WEIGHTLIFTING("Halter", "🏋️"),
  ARTISTIC_GYMNASTICS("Artistik Cimnastik", "🤸"),
  RHYTHMIC_GYMNASTICS("Ritmik Cimnastik", "🤸"),
  HORSE_RACING("At Yarışı", "🏇"),
  EQUESTRIAN("Binicilik", "🐎"),
  ARCHERY("Okçuluk", "🏹"),
  SHOOTING("Atıcılık", "🎯"),
  ALPINE_SKIING("Alp Disiplini", "⛷️"),
  CROSS_COUNTRY_SKIING("Kayaklı Koşu", "🎿"),
  SNOWBOARD("Snowboard", "🏂"),
  CURLING("Curling", "🥌"),
  FIGURE_SKATING("Artistik Buz Pateni", "⛸️"),
  SPEED_SKATING("Sürat Pateni", "⛸️"),
  SKATEBOARDING("Kaykay", "🛹"),
  SPORT_CLIMBING("Spor Tırmanış", "🧗"),
  SURFING("Sörf", "🏄"),
  WINDSURFING("Windsurf", "🏄"),
  KITESURFING("Kitesurf", "🏄"),
  FENCING("Eskrim", "🤺"),
  CROSSFIT("CrossFit", "🏋️"),
  POWERLIFTING("Powerlifting", "🏋️"),
  TRIATHLON("Triatlon", "🏆"),
  MODERN_PENTATHLON("Modern Pentatlon", "🏆"),
  ULTIMATE_FRISBEE("Ultimate Frisbee", "🥏"),
  BOWLING("Bowling", "🎳"),
  BOCCE("Bocce", "🥌"),
  BEACH_VOLLEYBALL("Plaj Voleybolu", "🏐"),
  FUTSAL("Futsal", "⚽"),
  BEACH_SOCCER("Plaj Futbolu", "⚽"),
  BASKETBALL_3X3("3x3 Basketbol", "🏀"),
  PADEL("Padel", "🎾"),
  SQUASH("Squash", "🎾"),
  PICKLEBALL("Pickleball", "🎾"),
  CHESS("Satranç", "♟️"),
  E_FOOTBALL("E-Futbol", "⚽"),
  E_BASKETBALL("E-Basketbol", "🏀"),
  OKEY_CARDS("Okey & Kart", "🀄")
}

enum class MatchStatus(val label: String) {
  SCHEDULED("Başlamadı"),
  UPCOMING("Başlamadı"),
  LIVE("Canlı"),
  HALFTIME("İY"),
  FINISHED("MS"),
  SUSPENDED("Askıda")
}

enum class MarketType(val displayName: String) {
  MATCH_RESULT("Maç Sonucu (MS)"),
  DOUBLE_CHANCE("Çifte Şans"),
  TOTAL_GOALS_25("2.5 Gol Alt/Üst"),
  TOTAL_GOALS_15("1.5 Gol Alt/Üst"),
  TOTAL_GOALS_35("3.5 Gol Alt/Üst"),
  BOTH_TEAMS_SCORE("Karşılıklı Gol (KG)"),
  FIRST_HALF_RESULT("İlk Yarı Sonucu (İY)"),
  FIRST_HALF_GOALS("İY 1.5 Gol"),
  TOTAL_CORNERS("Toplam Korner (9.5)"),
  TOTAL_CARDS("Toplam Kart (4.5)"),
  NEXT_GOAL("Sıradaki Gol"),
  HANDICAP_RESULT("Handikaplı Maç Sonucu (HMS)"),
  PLAYER_ANYTIME_GOAL("Oyuncu Gol Atar"),
  PLAYER_FIRST_GOAL("İlk Golü Atar"),
  PLAYER_SHOT_ON_TARGET("İsabetli Şut (1.5)"),
  // Basketbol Nesine / İddaa / Maçkolik Marketleri
  BASKETBALL_MS("Basketbol MS (1-2)"),
  BASKETBALL_HANDICAP("Handikaplı Maç Sonucu (HMS)"),
  BASKETBALL_TOTAL_POINTS("Toplam Sayı Alt/Üst"),
  BASKETBALL_FIRST_HALF("İlk Yarı Sonucu (İY 1-2)"),
  BASKETBALL_FIRST_HALF_TOTAL("İY Toplam Sayı Alt/Üst"),
  BASKETBALL_PLAYER_POINTS("Oyuncu Toplam Sayı"),
  BASKETBALL_PLAYER_ASSISTS("Oyuncu Toplam Asist"),
  BASKETBALL_PLAYER_REBOUNDS("Oyuncu Toplam Ribaund"),
  // Motor Sporları & Özel Marketler
  MOTORSPORTS_WINNER("Yarış Kazananı"),
  MOTORSPORTS_PODIUM("Podyum Derecesi (İlk 3)"),
  MOTORSPORTS_H2H("Pilot Eşleşmesi (H2H)"),
  TENNIS_MATCH_WINNER("Maç Kazananı (1-2)"),
  TENNIS_TOTAL_GAMES("Toplam Oyun Alt/Üst"),
  VOLLEYBALL_MATCH_WINNER("Maç Kazananı (1-2)"),
  ICE_HOCKEY_MS("Buz Hokeyi MS"),
  ICE_HOCKEY_TOTAL_GOALS("Toplam Gol Alt/Üst (5.5)"),
  ESPORTS_MATCH_WINNER("Maç Kazananı (1-2)"),
  BILLIARDS_MATCH_WINNER("Bilardo Maç Kazananı (1-2)"),
  BILLIARDS_TOTAL_FRAMES("Toplam Frame / Sayı Alt/Üst"),
  OKEY_ROUND_WINNER("Okey / El Kazananı"),
  OKEY_TOTAL_POINTS("Toplam Puan Alt/Üst"),
  TOTAL_GOALS_RANGE("Toplam Gol Aralığı"),
  ODD_EVEN("Tek / Çift")
}

data class Selection(
  val id: String,
  val marketId: String,
  val name: String,
  val odd: Double,
  val previousOdd: Double = odd,
  val isOddsUp: Boolean = false,
  val isOddsDown: Boolean = false
)

data class Market(
  val id: String,
  val type: MarketType,
  val name: String,
  val selections: List<Selection>,
  val isSuspended: Boolean = false
)

enum class EventType {
  GOAL, YELLOW_CARD, RED_CARD, CORNER, VAR_CHECK, PENALTY, SHOT_ON_TARGET, INJURY,
  // Basketbol Olayları
  THREE_POINTER, DUNK, FREE_THROW, TIMEOUT, FOUL_TROUBLE, QUARTER_END
}

data class MatchEvent(
  val id: String,
  val minute: Int,
  val type: EventType,
  val team: String,
  val player: String,
  val description: String
)

data class BasketballStatistics(
  val twoPointersHome: String = "22/41 (%53)",
  val twoPointersAway: String = "19/38 (%50)",
  val threePointersHome: String = "11/27 (%40)",
  val threePointersAway: String = "9/25 (%36)",
  val freeThrowsHome: String = "14/17 (%82)",
  val freeThrowsAway: String = "12/16 (%75)",
  val reboundsHome: Int = 38,
  val reboundsAway: Int = 34,
  val offensiveReboundsHome: Int = 11,
  val offensiveReboundsAway: Int = 9,
  val assistsHome: Int = 22,
  val assistsAway: Int = 18,
  val stealsHome: Int = 7,
  val stealsAway: Int = 5,
  val blocksHome: Int = 4,
  val blocksAway: Int = 3,
  val turnoversHome: Int = 10,
  val turnoversAway: Int = 13,
  val foulsHome: Int = 16,
  val foulsAway: Int = 19,
  val timeoutsRemainingHome: Int = 2,
  val timeoutsRemainingAway: Int = 1
)

data class MatchStatistics(
  val possessionHome: Int = 50,
  val possessionAway: Int = 50,
  val shotsHome: Int = 0,
  val shotsAway: Int = 0,
  val shotsOnTargetHome: Int = 0,
  val shotsOnTargetAway: Int = 0,
  val cornersHome: Int = 0,
  val cornersAway: Int = 0,
  val foulsHome: Int = 0,
  val foulsAway: Int = 0,
  val yellowCardsHome: Int = 0,
  val yellowCardsAway: Int = 0,
  val redCardsHome: Int = 0,
  val redCardsAway: Int = 0,
  val offsidesHome: Int = 0,
  val offsidesAway: Int = 0,
  val xgHome: Double = 1.45,
  val xgAway: Double = 1.18,
  val dangerousAttacksHome: Int = 0,
  val dangerousAttacksAway: Int = 0,
  val ballInPlayTime: String = "54:20"
)

data class AiPrediction(
  val homeWinProb: Int,
  val drawProb: Int,
  val awayWinProb: Int,
  val predictedScore: String,
  val confidence: Int,
  val xgSummary: String,
  val tacticalAnalysis: String,
  val formRatingHome: String,
  val formRatingAway: String
)

data class Match(
  val id: String,
  val sport: Sport,
  val league: String,
  val homeTeam: String,
  val awayTeam: String,
  val homeScore: Int,
  val awayScore: Int,
  val minute: Int,
  val status: MatchStatus,
  val markets: List<Market>,
  val statistics: MatchStatistics = MatchStatistics(),
  val aiPrediction: AiPrediction = AiPrediction(
    homeWinProb = 45,
    drawProb = 30,
    awayWinProb = 25,
    predictedScore = "1-0",
    confidence = 75,
    xgSummary = "1.50 - 1.10 xG",
    tacticalAnalysis = "Tempolu ve karşılıklı pozisyonlu mücadele bekleniyor.",
    formRatingHome = "G-G-B",
    formRatingAway = "M-B-G"
  ),
  val events: List<MatchEvent> = emptyList(),
  val startTime: String,
  val isHot: Boolean = false,
  val halfTimeHomeScore: Int = 0,
  val halfTimeAwayScore: Int = 0,
  val extraTimeMinutes: Int = 0, // Uzatma süresi (örn: +4, +6)
  val currentExtraMinute: Int = 0, // Anlık oynanan uzatma dakikası (örn: 90+3')
  val referee: String = "Halil Umut Meler",
  val stadium: String = "Rams Park",
  val weather: String = "18°C Açık",
  val tvBroadcast: String = "beIN SPORTS 1",
  val week: Int = 1,
  val matchDate: String = "23 Eylül Çarşamba 20:00 TSİ",
  val matchDateIso: String = "2026-09-23",
  val quarterScoresHome: List<Int> = emptyList(),
  val quarterScoresAway: List<Int> = emptyList(),
  val basketballStats: BasketballStatistics? = null,
  val iddaaCode: String = "10401",
  val mbs: Int = 1,
  val isKralOran: Boolean = false,
  val popularBetPercentage: Int = 0
)

data class SlipSelection(
  val matchId: String,
  val matchTeams: String,
  val marketType: MarketType,
  val selectionId: String,
  val selectionName: String,
  val odd: Double,
  val isLive: Boolean,
  val matchStatus: MatchStatus = MatchStatus.LIVE
)

enum class TicketType(val label: String) {
  TEKLI("Tekli"),
  KOMBINE("Kombine"),
  SISTEM("Sistem")
}

enum class TicketStatus(val label: String) {
  PENDING("Devam Ediyor"),
  WON("Kazandı 🎉"),
  LOST("Kaybetti")
}

data class Ticket(
  val id: Long,
  val ticketNumber: String,
  val type: TicketType,
  val stakePoints: Long,
  val totalOdds: Double,
  val potentialPoints: Long,
  val status: TicketStatus,
  val createdAt: Long,
  val selections: List<SlipSelection>
)

data class SocialCoupon(
  val id: String,
  val authorName: String,
  val authorTitle: String,
  val authorAvatarEmoji: String,
  val title: String,
  val selections: List<SlipSelection>,
  val totalOdds: Double,
  val stake: Long,
  val likeCount: Int,
  val copyCount: Int,
  val isEditor: Boolean = false,
  val winRate: Int = 78,
  val matchStartTimeTs: Long = System.currentTimeMillis() + 45 * 60 * 1000L,
  val formattedKickoff: String = "20:00 TSİ",
  val isUserShared: Boolean = false,
  val isLikedByMe: Boolean = false,
  val category: String = "Süper Lig",
  val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType(val title: String, val isPositive: Boolean) {
  WELCOME_BONUS("Hoş Geldin Bonusu", true),
  DAILY_REWARD("Günlük Giriş Bonusu", true),
  BET_STAKE("Kupon Bedeli (Stake)", false),
  BET_WIN("Kupon Kazancı", true),
  AI_CHALLENGE_REWARD("AI Challenge Ödülü", true)
}

data class UserWallet(
  val userId: String = "user_default",
  val availablePoints: Long = 10000L,
  val lockedPoints: Long = 0L,
  val totalPoints: Long = availablePoints + lockedPoints,
  val lifetimeWon: Long = 0L,
  val lifetimeLost: Long = 0L,
  val totalBetsPlaced: Int = 0,
  val totalBetsWon: Int = 0,
  val lastDailyClaimDate: String = "",
  val rankTitle: String = "Çaylak Tahminci"
) {
  val winRatePercent: Int
    get() = if (totalBetsPlaced > 0) ((totalBetsWon.toDouble() / totalBetsPlaced) * 100).toInt() else 0
}

data class WalletTransaction(
  val id: Long,
  val type: TransactionType,
  val amount: Long,
  val balanceAfter: Long,
  val description: String,
  val timestamp: Long
)

data class UserProfile(
  val username: String = "aycadogan",
  val email: String = "aycadogan6464@gmail.com",
  val fullName: String = "Ayça Doğan",
  val avatarUrl: String = "👑",
  val isLoggedIn: Boolean = false
)

data class LiveGoalAlert(
  val matchId: String,
  val homeTeam: String,
  val awayTeam: String,
  val scoringTeam: String,
  val scorerName: String,
  val minute: Int,
  val homeScore: Int,
  val awayScore: Int,
  val rawMessage: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class UserCouponMatchInfo(
  val ticketNumber: String,
  val selectionName: String,
  val marketName: String,
  val odd: Double,
  val isWinningNow: Boolean = true,
  val totalPotentialPoints: Long = 0L
)

enum class GoalNotificationScope(val label: String) {
  ONLY_COUPON("Sadece Kuponumdaki Maçlar (Önerilen)"),
  COUPON_AND_FAVORITES("Kuponlarım ve Favorilerim"),
  ALL_MATCHES("Tüm Maçlar"),
  MUTED("Sessiz Mod")
}

