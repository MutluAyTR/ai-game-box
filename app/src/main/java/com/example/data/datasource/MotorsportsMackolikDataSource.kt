package com.example.data.datasource

/**
 * Maçkolik & Nesine Resmi Motor Sporları Veri Tabanı
 * MotoGP Grand Prix ve WRC Dünya Rallisi Detaylı Verileri:
 * - Sürücüler & Takımlar Puan Durumu Tablosu
 * - 2026 Sezon Fikstürü & Yarış Takvimi
 * - Canlı Yarış, Grid Dizilimi & Özel Etap Sonuçları
 * - "Nasıl Oynanır?" (Maçkolik / İddaa Bahis Rehberi & Kuralları)
 * - "Kim Kazanır?" (AI Simülasyonu & Oran Karşılaştırması)
 */
object MotorsportsMackolikDataSource {

  // ==========================================
  // 1. MOTOGP GRAND PRIX MAÇKOLİK VERİLERİ
  // ==========================================

  data class MotoGpDriver(
    val rank: Int,
    val name: String,
    val number: Int,
    val team: String,
    val bike: String,
    val points: Int,
    val wins: Int,
    val podiums: Int,
    val poles: Int,
    val countryFlag: String,
    val winProbability: Int,
    val currentOdds: Double
  )

  data class MotoGpTeam(
    val rank: Int,
    val name: String,
    val bike: String,
    val points: Int,
    val wins: Int
  )

  data class MotoGpCalendarEvent(
    val round: Int,
    val grandPrix: String,
    val circuit: String,
    val country: String,
    val flag: String,
    val dateIso: String,
    val laps: Int,
    val distanceKm: Double,
    val status: String,
    val winner: String
  )

  data class MotoGpStartingGridItem(
    val gridPos: Int,
    val rider: String,
    val team: String,
    val q2Time: String,
    val gap: String,
    val topSpeedKmh: Double
  )

  data class MotoGpBettingGuide(
    val marketName: String,
    val iddaaCode: String,
    val description: String,
    val rules: String,
    val tip: String
  )

  val motoGpCircuit = mapOf(
    "name" to "Misano World Circuit Marco Simoncelli",
    "location" to "Misano Adriatico, İtalya",
    "grandPrix" to "Gran Premio Red Bull di San Marino e della Riviera di Rimini",
    "length" to "4.226 metre (2.626 mil)",
    "corners" to "16 viraj (10 Sağ, 6 Sol)",
    "longestStraight" to "530 metre",
    "raceDistance" to "27 Tur (114.102 km)",
    "sprintDistance" to "13 Tur (54.938 km)",
    "lapRecord" to "1:30.390 (Francesco Bagnaia - Ducati Lenovo)",
    "surfaceTemp" to "29°C (Kuru Asfalt, Güneşli)"
  )

  val motoGpDriversStandings: List<MotoGpDriver> = listOf(
    MotoGpDriver(1, "Jorge Martin", 89, "Prima Pramac Racing", "Ducati Desmosedici GP24", 341, 3, 10, 5, "🇪🇸", 32, 2.10),
    MotoGpDriver(2, "Francesco Bagnaia", 1, "Ducati Lenovo Team", "Ducati Desmosedici GP24", 317, 7, 9, 4, "🇮🇹", 35, 1.95),
    MotoGpDriver(3, "Marc Marquez", 93, "Gresini Racing MotoGP", "Ducati Desmosedici GP23", 282, 2, 8, 2, "🇪🇸", 16, 4.50),
    MotoGpDriver(4, "Enea Bastianini", 23, "Ducati Lenovo Team", "Ducati Desmosedici GP24", 282, 2, 7, 1, "🇮🇹", 10, 6.00),
    MotoGpDriver(5, "Brad Binder", 33, "Red Bull KTM Factory Racing", "KTM RC16", 165, 0, 1, 0, "🇿🇦", 2, 22.00),
    MotoGpDriver(6, "Pedro Acosta", 31, "Red Bull GASGAS Tech3", "KTM RC16", 157, 0, 3, 1, "🇪🇸", 3, 18.00),
    MotoGpDriver(7, "Maverick Viñales", 12, "Aprilia Racing", "Aprilia RS-GP", 139, 1, 2, 1, "🇪🇸", 1, 35.00),
    MotoGpDriver(8, "Aleix Espargaro", 41, "Aprilia Racing", "Aprilia RS-GP", 127, 1, 2, 2, "🇪🇸", 1, 40.00),
    MotoGpDriver(9, "Fabio Di Giannantonio", 49, "Pertamina Enduro VR46", "Ducati Desmosedici GP23", 121, 0, 0, 0, "🇮🇹", 0, 65.00),
    MotoGpDriver(10, "Marco Bezzecchi", 72, "Pertamina Enduro VR46", "Ducati Desmosedici GP23", 108, 0, 1, 0, "🇮🇹", 0, 75.00),
    MotoGpDriver(11, "Alex Marquez", 73, "Gresini Racing MotoGP", "Ducati Desmosedici GP23", 102, 0, 1, 0, "🇪🇸", 0, 80.00),
    MotoGpDriver(12, "Franco Morbidelli", 21, "Prima Pramac Racing", "Ducati Desmosedici GP24", 86, 0, 0, 0, "🇮🇹", 0, 90.00),
    MotoGpDriver(13, "Fabio Quartararo", 20, "Monster Energy Yamaha MotoGP", "Yamaha YZR-M1", 61, 0, 0, 0, "🇫🇷", 0, 110.00),
    MotoGpDriver(14, "Jack Miller", 43, "Red Bull KTM Factory Racing", "KTM RC16", 58, 0, 0, 0, "🇦🇺", 0, 130.00),
    MotoGpDriver(15, "Miguel Oliveira", 88, "Trackhouse Racing", "Aprilia RS-GP", 43, 0, 0, 0, "🇵🇹", 0, 150.00)
  )

  val motoGpTeamsStandings: List<MotoGpTeam> = listOf(
    MotoGpTeam(1, "Ducati Lenovo Team", "Ducati", 599, 9),
    MotoGpTeam(2, "Prima Pramac Racing", "Ducati", 427, 3),
    MotoGpTeam(3, "Gresini Racing MotoGP", "Ducati", 384, 2),
    MotoGpTeam(4, "Aprilia Racing", "Aprilia", 266, 2),
    MotoGpTeam(5, "Pertamina Enduro VR46 Team", "Ducati", 229, 0),
    MotoGpTeam(6, "Red Bull KTM Factory Racing", "KTM", 223, 0),
    MotoGpTeam(7, "Red Bull GASGAS Tech3", "KTM", 177, 0),
    MotoGpTeam(8, "Monster Energy Yamaha MotoGP", "Yamaha", 84, 0),
    MotoGpTeam(9, "Trackhouse Racing", "Aprilia", 67, 0),
    MotoGpTeam(10, "LCR Honda Castrol/Idemitsu", "Honda", 24, 0),
    MotoGpTeam(11, "Repsol Honda Team", "Honda", 17, 0)
  )

  val motoGpStartingGrid: List<MotoGpStartingGridItem> = listOf(
    MotoGpStartingGridItem(1, "Francesco Bagnaia", "Ducati Lenovo", "1:30.304", "POLE", 301.6),
    MotoGpStartingGridItem(2, "Jorge Martin", "Prima Pramac", "1:30.518", "+0.214s", 303.3),
    MotoGpStartingGridItem(3, "Marc Marquez", "Gresini Racing", "1:30.612", "+0.308s", 299.1),
    MotoGpStartingGridItem(4, "Enea Bastianini", "Ducati Lenovo", "1:30.684", "+0.380s", 302.5),
    MotoGpStartingGridItem(5, "Brad Binder", "Red Bull KTM", "1:30.824", "+0.520s", 304.2),
    MotoGpStartingGridItem(6, "Pedro Acosta", "GASGAS Tech3", "1:30.916", "+0.612s", 300.8),
    MotoGpStartingGridItem(7, "Marco Bezzecchi", "VR46 Ducati", "1:31.005", "+0.701s", 298.3),
    MotoGpStartingGridItem(8, "Maverick Viñales", "Aprilia Racing", "1:31.049", "+0.745s", 297.5),
    MotoGpStartingGridItem(9, "Fabio Quartararo", "Monster Yamaha", "1:31.114", "+0.810s", 295.8)
  )

  val motoGpSeasonCalendar: List<MotoGpCalendarEvent> = listOf(
    MotoGpCalendarEvent(1, "Katar GP", "Lusail International Circuit", "Katar", "🇶🇦", "2026-03-10", 22, 118.36, "Bitti", "F. Bagnaia"),
    MotoGpCalendarEvent(2, "Portekiz GP", "Autódromo Internacional do Algarve", "Portekiz", "🇵🇹", "2026-03-24", 25, 114.80, "Bitti", "J. Martin"),
    MotoGpCalendarEvent(3, "Amerika COTA GP", "Circuit of the Americas", "ABD", "🇺🇸", "2026-04-14", 20, 110.26, "Bitti", "M. Viñales"),
    MotoGpCalendarEvent(4, "İspanya GP", "Circuito de Jerez-Ángel Nieto", "İspanya", "🇪🇸", "2026-04-28", 25, 110.58, "Bitti", "F. Bagnaia"),
    MotoGpCalendarEvent(5, "Fransa GP", "Bugatti Circuit, Le Mans", "Fransa", "🇫🇷", "2026-05-12", 27, 112.99, "Bitti", "J. Martin"),
    MotoGpCalendarEvent(6, "İtalya GP", "Autodromo del Mugello", "İtalya", "🇮🇹", "2026-06-02", 23, 120.63, "Bitti", "F. Bagnaia"),
    MotoGpCalendarEvent(7, "Hollanda TT GP", "TT Circuit Assen", "Hollanda", "🇳🇱", "2026-06-30", 26, 118.09, "Bitti", "F. Bagnaia"),
    MotoGpCalendarEvent(8, "Almanya GP", "Sachsenring", "Almanya", "🇩🇪", "2026-07-07", 30, 110.13, "Bitti", "F. Bagnaia"),
    MotoGpCalendarEvent(9, "İngiltere GP", "Silverstone Circuit", "İngiltere", "🇬🇧", "2026-08-04", 20, 118.00, "Bitti", "E. Bastianini"),
    MotoGpCalendarEvent(10, "Avusturya GP", "Red Bull Ring, Spielberg", "Avusturya", "🇦🇹", "2026-08-18", 28, 121.74, "Bitti", "F. Bagnaia"),
    MotoGpCalendarEvent(11, "Aragon GP", "MotorLand Aragón", "İspanya", "🇪🇸", "2026-09-01", 23, 116.77, "Bitti", "M. Marquez"),
    MotoGpCalendarEvent(12, "San Marino GP (Misano)", "Misano World Circuit", "İtalya", "🇸🇲", "2026-09-26", 27, 114.10, "CANLI / GÜNCEL", "Bagnaia / Martin"),
    MotoGpCalendarEvent(13, "Japonya GP", "Mobility Resort Motegi", "Japonya", "🇯🇵", "2026-10-06", 24, 115.22, "Yaklaşıyor", "-"),
    MotoGpCalendarEvent(14, "Endonezya GP", "Mandalika Circuit, Lombok", "Endonezya", "🇮🇩", "2026-10-13", 27, 116.12, "Yaklaşıyor", "-"),
    MotoGpCalendarEvent(15, "Avustralya GP", "Phillip Island Circuit", "Avustralya", "🇦🇺", "2026-10-20", 27, 120.09, "Yaklaşıyor", "-"),
    MotoGpCalendarEvent(16, "Tayland GP", "Chang International Circuit", "Tayland", "🇹🇭", "2026-10-27", 26, 118.40, "Yaklaşıyor", "-"),
    MotoGpCalendarEvent(17, "Malezya GP", "Sepang International Circuit", "Malezya", "🇲🇾", "2026-11-03", 20, 110.86, "Yaklaşıyor", "-"),
    MotoGpCalendarEvent(18, "Valensiya GP (Büyük Final)", "Circuit Ricardo Tormo", "İspanya", "🇪🇸", "2026-11-17", 27, 108.13, "Yaklaşıyor", "-")
  )

  val motoGpBettingGuides: List<MotoGpBettingGuide> = listOf(
    MotoGpBettingGuide(
      marketName = "Yarışı Kim Kazanır? (Race Winner)",
      iddaaCode = "GP-KZN",
      description = "Pazar günü koşulan ana Grand Prix yarışını 1. sırada tamamlayıp damalı bayrağı ilk gören sürücüye oynanır.",
      rules = "Sürücünün resmi FIA/FIM podyum törenindeki sıralaması geçerlidir. Sonradan verilen cezalar FIA resmi bültenine göre sonuçlandırılır.",
      tip = "Misano pistinde Ducati motorları son 4 yıldır dominasyon kurdu. Pole pozisyonundaki Bagnaia ve Sprint şampiyonu Martin favoridir."
    ),
    MotoGpBettingGuide(
      marketName = "Podyum Derecesi (İlk 3 Sıra)",
      iddaaCode = "GP-POD",
      description = "Seçilen sürücünün yarışı ilk 3 sıra (1., 2. veya 3.) içerisinde tamamlayıp podyuma çıkması tahminidir.",
      rules = "Yarışı 1, 2 veya 3. bitiren tüm pilotlar kazanmış sayılır.",
      tip = "Marc Marquez ve Enea Bastianini'nin yarış temposu son turlarda çok yüksek olduğundan podyum oranları kasanıza güvenli kazanç sağlar."
    ),
    MotoGpBettingGuide(
      marketName = "Sürücü Karşılaşması (H2H - Head to Head)",
      iddaaCode = "GP-H2H",
      description = "Maçkolik'te açılan iki sürücü arasındaki düellodur (Örn: F. Bagnaia vs J. Martin).",
      rules = "Yarışı rakibinden daha önde tamamlayan sürücü kazanır. Her iki sürücü de kaza yapıp yarış dışı kalırsa daha fazla tur atan sürücü kazanmış sayılır.",
      tip = "Grid diziliminde Bagnaia (P1) Martin'in (P2) önünde yer alıyor. İlk viraja lider giren sürücü büyük avantaj yakalar."
    ),
    MotoGpBettingGuide(
      marketName = "En Hızlı Tur (Fastest Lap)",
      iddaaCode = "GP-EHT",
      description = "Ana yarış esnasında pistteki en hızlı tekil tur süresini (Mor Sektör) kaydeden sürücü.",
      rules = "Sadece Pazar günkü ana yarış turları geçerlidir. Sıralama veya antrenman turları sayılmaz.",
      tip = "Depo hafiflediğinde (18-24. turlar arası) Bastianini ve Marquez çok hızlı turlar atmaktadır."
    ),
    MotoGpBettingGuide(
      marketName = "Sprint Yarışı Birincisi",
      iddaaCode = "GP-SPR",
      description = "Cumartesi günü saat 16:00'da koşulan 13 turluk kısa mesafeli Sprint yarışını kazanan pilot.",
      rules = "Yarış mesafesi ana yarışın yarısı kadardır ve 12-1 puan sistemi uygulanır.",
      tip = "Jorge Martin bu sezon 5 Sprint zaferiyle 'Sprint Kralı' unvanını taşımaktadır."
    )
  )

  // ==========================================
  // 2. WRC DÜNYA RALLİSİ MAÇKOLİK VERİLERİ
  // ==========================================

  data class WrcDriver(
    val rank: Int,
    val name: String,
    val coDriver: String,
    val team: String,
    val car: String,
    val points: Int,
    val wins: Int,
    val podiums: Int,
    val countryFlag: String,
    val winProbability: Int,
    val currentOdds: Double
  )

  data class WrcManufacturer(
    val rank: Int,
    val name: String,
    val car: String,
    val points: Int,
    val wins: Int
  )

  data class WrcCalendarEvent(
    val round: Int,
    val rallyName: String,
    val country: String,
    val flag: String,
    val dateIso: String,
    val surface: String,
    val stagesCount: Int,
    val distanceKm: Double,
    val status: String,
    val winner: String
  )

  data class WrcStageItem(
    val stageCode: String,
    val stageName: String,
    val distanceKm: Double,
    val leaderDriver: String,
    val car: String,
    val time: String,
    val gap: String,
    val avgSpeedKmh: Double
  )

  data class WrcBettingGuide(
    val marketName: String,
    val iddaaCode: String,
    val description: String,
    val rules: String,
    val tip: String
  )

  val wrcRallyChileInfo = mapOf(
    "name" to "Rally Chile Bio Bío (WRC Şili Rallisi)",
    "location" to "Concepción, Bio Bío Bölgesi, Şili",
    "surface" to "Hızlı ve Ormanlık Çakıl (Gravel / Pasifik Kıyısı)",
    "totalStages" to "16 Özel Etap (SS1 - SS16)",
    "totalDistance" to "306.76 km Özel Etap + 932.40 km Bağlantı",
    "servicePark" to "Casino Marina del Sol, Talcahuano",
    "status" to "CANLI / GÜNCEL (2. Gün Özel Etapları Koşuluyor)",
    "overallLeader" to "Sébastien Ogier (Toyota GR Yaris Rally1)",
    "gapToSecond" to "+4.8s (Thierry Neuville takipte)"
  )

  val wrcDriversStandings: List<WrcDriver> = listOf(
    WrcDriver(1, "Thierry Neuville", "Martijn Wydaeghe", "Hyundai Shell Mobis WRT", "Hyundai i20 N Rally1 Hybrid", 192, 2, 5, "🇧🇪", 30, 2.30),
    WrcDriver(2, "Ott Tänak", "Martin Järveoja", "Hyundai Shell Mobis WRT", "Hyundai i20 N Rally1 Hybrid", 158, 1, 4, "🇪🇪", 24, 2.70),
    WrcDriver(3, "Sébastien Ogier", "Vincent Landais", "Toyota Gazoo Racing WRT", "Toyota GR Yaris Rally1 Hybrid", 154, 3, 5, "🇫🇷", 34, 1.90),
    WrcDriver(4, "Elfyn Evans", "Scott Martin", "Toyota Gazoo Racing WRT", "Toyota GR Yaris Rally1 Hybrid", 140, 0, 5, "🇬🇧", 7, 7.50),
    WrcDriver(5, "Adrien Fourmaux", "Alexandre Coria", "M-Sport Ford WRT", "Ford Puma Rally1 Hybrid", 130, 0, 4, "🇫🇷", 3, 14.00),
    WrcDriver(6, "Kalle Rovanperä", "Jonne Halttunen", "Toyota Gazoo Racing WRT", "Toyota GR Yaris Rally1 Hybrid", 114, 3, 3, "🇫🇮", 2, 8.00),
    WrcDriver(7, "Takamoto Katsuta", "Aaron Johnston", "Toyota Gazoo Racing WRT", "Toyota GR Yaris Rally1 Hybrid", 80, 0, 0, "🇯🇵", 0, 35.00),
    WrcDriver(8, "Dani Sordo", "Cándido Carrera", "Hyundai Shell Mobis WRT", "Hyundai i20 N Rally1 Hybrid", 44, 0, 1, "🇪🇸", 0, 45.00),
    WrcDriver(9, "Sami Pajari", "Enni Mälkönen", "Printsport / Toyota", "Toyota GR Yaris Rally2", 34, 0, 0, "🇫🇮", 0, 80.00),
    WrcDriver(10, "Grégoire Munster", "Louis Louka", "M-Sport Ford WRT", "Ford Puma Rally1 Hybrid", 27, 0, 0, "🇱🇺", 0, 95.00)
  )

  val wrcManufacturersStandings: List<WrcManufacturer> = listOf(
    WrcManufacturer(1, "Hyundai Shell Mobis WRT", "Hyundai i20 N Rally1", 435, 3),
    WrcManufacturer(2, "Toyota Gazoo Racing WRT", "Toyota GR Yaris Rally1", 415, 6),
    WrcManufacturer(3, "M-Sport Ford World Rally Team", "Ford Puma Rally1", 220, 0)
  )

  val wrcChileStages: List<WrcStageItem> = listOf(
    WrcStageItem("SS1", "Pulpería 1", 19.72, "Sébastien Ogier", "Toyota GR Yaris", "10:14.2", "LİDER", 115.6),
    WrcStageItem("SS2", "Rere 1", 13.34, "Thierry Neuville", "Hyundai i20 N", "06:55.1", "+0.8s", 115.9),
    WrcStageItem("SS3", "San Rosendo 1", 23.32, "Ott Tänak", "Hyundai i20 N", "12:44.8", "+2.1s", 109.8),
    WrcStageItem("SS4", "Pulpería 2", 19.72, "Sébastien Ogier", "Toyota GR Yaris", "10:07.5", "+0.0s", 116.9),
    WrcStageItem("SS5", "Rere 2", 13.34, "Elfyn Evans", "Toyota GR Yaris", "06:48.3", "+1.2s", 117.8),
    WrcStageItem("SS6", "San Rosendo 2", 23.32, "Thierry Neuville", "Hyundai i20 N", "12:39.0", "+1.5s", 110.6),
    WrcStageItem("SS7", "Pelun 1", 15.65, "Sébastien Ogier", "Toyota GR Yaris", "08:32.4", "+0.0s", 110.1),
    WrcStageItem("SS8", "Lota 1", 25.64, "Ott Tänak", "Hyundai i20 N", "14:21.0", "+4.8s", 107.2),
    WrcStageItem("SS16", "Bio Bío (Wolf Power Stage)", 8.78, "Canlı / Son Etap", "Hibrit Canlı", "Koşuluyor", "Ekstra 5 Puan", 118.4)
  )

  val wrcSeasonCalendar: List<WrcCalendarEvent> = listOf(
    WrcCalendarEvent(1, "Rallye Monte-Carlo", "Monako", "🇲🇨", "2026-01-25", "Asfalt / Kar / Buz", 17, 324.44, "Bitti", "T. Neuville"),
    WrcCalendarEvent(2, "Rally Sweden", "İsveç", "🇸🇪", "2026-02-15", "Saf Kar & Buz (Çivili)", 18, 301.76, "Bitti", "E. Lappi"),
    WrcCalendarEvent(3, "Safari Rally Kenya", "Kenya", "🇰🇪", "2026-03-31", "Zorlu Vahşi Çakıl (Fesh-Fesh)", 19, 367.76, "Bitti", "K. Rovanperä"),
    WrcCalendarEvent(4, "Croatia Rally", "Hırvatistan", "🇭🇷", "2026-04-21", "Dalgalı Asfalt", 20, 283.28, "Bitti", "S. Ogier"),
    WrcCalendarEvent(5, "Rally de Portugal", "Portekiz", "🇵🇹", "2026-05-12", "Hızlı Kumlu Çakıl", 22, 337.04, "Bitti", "S. Ogier"),
    WrcCalendarEvent(6, "Rally Italia Sardegna", "İtalya", "🇮🇹", "2026-06-02", "Dar & Sert Çakıl", 16, 266.12, "Bitti", "O. Tänak"),
    WrcCalendarEvent(7, "ORLEN Rally Poland", "Polonya", "🇵🇱", "2026-06-30", "Ultra Hızlı Kumlu Çakıl", 19, 303.16, "Bitti", "K. Rovanperä"),
    WrcCalendarEvent(8, "Tet Rally Latvia", "Letonya", "🇱🇻", "2026-07-21", "Çok Hızlı Çakıl", 20, 300.13, "Bitti", "K. Rovanperä"),
    WrcCalendarEvent(9, "Secto Rally Finland", "Finlandiya", "🇫🇮", "2026-08-04", "Bin Göller Rallisi (Büyük Zıplamalar)", 20, 305.69, "Bitti", "S. Ogier"),
    WrcCalendarEvent(10, "EKO Acropolis Rally Greece", "Yunanistan", "🇬🇷", "2026-09-08", "Tanrılar Rallisi (Taşlı & Sıcak)", 15, 305.30, "Bitti", "T. Neuville"),
    WrcCalendarEvent(11, "Rally Chile Bio Bío", "Şili", "🇨🇱", "2026-09-26", "Teknik Orman Çakılı", 16, 306.76, "CANLI / GÜNCEL", "Ogier / Neuville"),
    WrcCalendarEvent(12, "Central European Rally", "Almanya/Avusturya/Çekya", "🇩🇪", "2026-10-20", "Üç Ülke Asfaltı", 18, 302.51, "Yaklaşıyor", "-"),
    WrcCalendarEvent(13, "FORUM8 Rally Japan (Final)", "Japonya", "🇯🇵", "2026-11-24", "Dar Dağ Asfaltı", 21, 302.59, "Yaklaşıyor", "-")
  )

  val wrcBettingGuides: List<WrcBettingGuide> = listOf(
    WrcBettingGuide(
      marketName = "Ralliyi Kim Kazanır? (Overall Winner)",
      iddaaCode = "WRC-KZN",
      description = "4 gün boyunca koşulan 16-20 özel etabın toplamında en kısa süreyi kaydeden ve ralliyi 1. tamamlayan sürücü.",
      rules = "FIA resmi ralli sonuç bildirgesi esastır. Süre cezaları (jump start, servis gecikmesi) genel klasman süresine eklenir.",
      tip = "Sébastien Ogier Şili'de son gün etaplarında lastik yönetimini en iyi yapan sürücüdür. Liderlik avantajıyla favoridir."
    ),
    WrcBettingGuide(
      marketName = "Podyum Derecesi (İlk 3 Sıra)",
      iddaaCode = "WRC-POD",
      description = "Sürücünün ralli sonunda podyuma çıkıp ilk 3 sırada (1., 2. veya 3.) yer alması bahsidir.",
      rules = "Ralliyi tamamlayan ilk 3 sürücüye oynayan tüm kuponlar kazanır.",
      tip = "Thierry Neuville şampiyona liderliğini korumak için kontrollü bir sürüşle podyumu hedeflemektedir."
    ),
    WrcBettingGuide(
      marketName = "Power Stage Galibi (Son Özel Etap)",
      iddaaCode = "WRC-PWR",
      description = "Pazar günkü ralli kapanış etabı olan 'Wolf Power Stage' etabını en hızlı sürede geçen pilot.",
      rules = "Bu etapta ilk 5'e giren sürücülere ekstra 5-4-3-2-1 şampiyona puanı verilir. Pilotlar tüm riskleri alarak son etapta yarışır.",
      tip = "Ott Tänak ve Kalle Rovanperä Power Stage etaplarında ekstra agresif sürüş sergiler."
    ),
    WrcBettingGuide(
      marketName = "Pilot Eşleşmesi (H2H - Head to Head)",
      iddaaCode = "WRC-H2H",
      description = "İki pilot arasındaki ralli bitirme derecesi düellosudur (Örn: Sébastien Ogier vs Thierry Neuville).",
      rules = "Ralliyi genel klasmanda rakibinden daha üst sırada bitiren sürücü kazanır. Bir sürücü ralli dışı kalırsa diğeri kazanır.",
      tip = "Toyota araçları Şili çakıl zemininde daha dayanıklı süspansiyon geometrisiyle avantajlıdır."
    ),
    WrcBettingGuide(
      marketName = "Markalar / Üreticiler Birincisi",
      iddaaCode = "WRC-MRK",
      description = "Ralliyi hangi otomobil üreticisinin (Toyota GR Yaris mi, Hyundai i20 N mi yoksa M-Sport Ford Puma mı) kazanacağı.",
      rules = "Markanın en iyi dereceyi yapan 1. aracının sonucu baz alınır.",
      tip = "Toyota bu sezon 6 ralli galibiyetiyle Şili'de favori gösterilmektedir."
    )
  )
}
