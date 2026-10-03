package com.example.data.remote

import com.example.data.model.ArenaNewsArticle
import com.example.engine.GeminiAnalysisService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ArenaNewsRepository {

  private val _news = MutableStateFlow<List<ArenaNewsArticle>>(getCuratedNews())
  val newsFlow: Flow<List<ArenaNewsArticle>> = _news.asStateFlow()

  private val _isRefreshing = MutableStateFlow(false)
  val isRefreshing: Flow<Boolean> = _isRefreshing.asStateFlow()

  private val scope = CoroutineScope(Dispatchers.IO)

  init {
    // Periodically fetch and generate AI daily news feed summaries
    scope.launch {
      delay(3000L) // Initial slight delay after app start
      fetchGeminiDailyNews()
      while (true) {
        delay(60_000L) // Refresh periodically every 60 seconds
        fetchGeminiDailyNews()
      }
    }
  }

  fun getCategories(): List<String> = listOf(
    "Tümü",
    "Performans Analizi",
    "Sakatlık Raporu",
    "Transfer Dedikodusu",
    "Transfer & KAP",
    "Süper Lig",
    "Gol Krallığı & İstatistik",
    "Mali Durum & Borçlar",
    "Basketbol",
    "Motorspor"
  )

  suspend fun fetchGeminiDailyNews() {
    _isRefreshing.value = true
    try {
      val matches = com.example.engine.SimulationEngine.latestMatches
      val geminiArticles = if (matches.isNotEmpty()) {
        GeminiAnalysisService.generateNewsFromSimulatedMatches(matches)
      } else {
        GeminiAnalysisService.generateGeminiDailyNewsFeed()
      }
      if (geminiArticles.isNotEmpty()) {
        val current = _news.value.toMutableList()
        geminiArticles.reversed().forEach { newArt ->
          if (current.none { it.id == newArt.id || it.title == newArt.title }) {
            current.add(0, newArt)
          }
        }
        _news.value = current
      }
    } catch (_: Exception) {
    } finally {
      _isRefreshing.value = false
    }
  }

  fun refreshNews() {
    scope.launch {
      fetchGeminiDailyNews()
    }
  }

  private fun getCuratedNews(): List<ArenaNewsArticle> = listOf(
    ArenaNewsArticle(
      id = "news_1",
      title = "Dev Derbide Taktik Savaşları: Galatasaray - Fenerbahçe Öncesi Son Gelişmeler",
      summary = "Rams Park'ta oynanacak tarihi derbi öncesi iki teknik adamın orta saha tercihleri netleşti. AI simülasyonları maçın kaderini xG değerleri ile belirledi.",
      content = "Süper Lig'in 5. haftasında futbolseverlerin nefesini kesecek Galatasaray - Fenerbahçe derbisi öncesi taktik planlar sızdı. Ev sahibi sarı-kırmızılı ekipte pres gücü yüksek bir orta saha kurgulanırken, konuk Fenerbahçe kanat organizasyonları ve hızlı geçiş hücumları üzerinde duruyor. Yapay zeka simülasyonlarımıza göre maçta karşılıklı gol olma olasılığı %62 seviyesinde seyrediyor.",
      category = "Süper Lig",
      source = "Tahmin Arena Spor Masası",
      author = "Uğur Meleke & Güntekin Onay",
      publishedAgo = "5 dk önce",
      readTimeMinutes = 4,
      readCount = 5420,
      relatedMatchId = "m1",
      relatedMatchTeams = "Galatasaray - Fenerbahçe",
      emojiBadge = "🔥",
      tags = listOf("Süper Lig", "Derbi", "Galatasaray", "Fenerbahçe", "xG Analizi")
    ),
    ArenaNewsArticle(
      id = "news_trans_kap",
      title = "KAP Bildirimi: Süper Lig'de Son Dakika Yıldız Transferi ve Bonservis Detayları",
      summary = "Kulüpler Birliği ve TFF harcama limitleri onaylandı. Yıldız forvetin sözleşme fesih bedeli ve 3 yıllık maaş takvimi Kamuoyu Aydınlatma Platformu'na bildirildi.",
      content = "Süper Lig devi, Kamuoyu Aydınlatma Platformu'na (KAP) yaptığı resmi bildirimle yeni transferin maliyetini duyurdu. Kulüpten yapılan açıklamaya göre oyuncuya yıllık 4.5 milyon Euro net garanti ücret ödenecek. Bonservis bedeli 3 taksit halinde ödenecek olup oyuncu sağlık kontrollerinin ardından bu akşam kampa katılacak.",
      category = "Transfer & KAP",
      source = "KAP Resmi Açıklaması",
      author = "Özgür Sancar & Yağız Sabuncuoğlu",
      publishedAgo = "14 dk önce",
      readTimeMinutes = 3,
      readCount = 8920,
      relatedMatchId = null,
      relatedMatchTeams = null,
      emojiBadge = "✈️",
      tags = listOf("KAP", "Transfer", "Bonservis", "Flaş Haber")
    ),
    ArenaNewsArticle(
      id = "news_scorer_stats",
      title = "Süper Lig Gol Krallığı Yarışı Kızıştı: xG Liderleri ve Asist Tablosu",
      summary = "5. hafta geride kalırken gol krallığı tablosunda kıyasıya rekabet. Ceza sahası etkinliği ve kilit pas istatistikleri Maçkolik verileriyle açıklandı.",
      content = "Süper Lig'de gol krallığı zirvesinde kıran kırana mücadele devam ediyor. Ciro Immobile 7 golle ilk sırada yer alırken, Edin Dzeko ve Mauro Icardi 5'er golle takibini sürdürüyor. xG (Gol Beklentisi) endeksinde maç başına 1.28 ile zirvede olan Victor Osimhen ise son 3 haftada rakip ceza sahasında en çok topla buluşan futbolcu oldu. Asist krallığında Dusan Tadic ve Gabriel Sara 4'er asistle zirveyi paylaşıyor.",
      category = "Gol Krallığı & İstatistik",
      source = "Maçkolik Opta Analytics",
      author = "Fırat Günayer",
      publishedAgo = "28 dk önce",
      readTimeMinutes = 4,
      readCount = 6120,
      relatedMatchId = "m1",
      relatedMatchTeams = "Galatasaray - Fenerbahçe",
      emojiBadge = "👑",
      tags = listOf("Gol Krallığı", "Immobile", "Icardi", "Dzeko", "Asist", "Opta")
    ),
    ArenaNewsArticle(
      id = "news_financial_crisis",
      title = "Kulüplerin Mali Raporu: UEFA FFP Kriterleri, Borç Yapılandırması ve Transfer Yasakları",
      summary = "TFF Lisans Kurulu ve UEFA Denetleme Kurulu kulüplerin mali tablolarını denetledi. 4 kulübe transfer tahtası uyarısı ve puan silme riski.",
      content = "Türkiye Futbol Federasyonu Kulüp Lisans Kurulu, 2026 sezonu harcama limitleri ve borç stoklarını açıkladı. Dört büyük kulübün toplam finansal borcunun yapılandırılması konusunda Bankalar Birliği anlaşması revize edildi. Alt liglerdeki ve Süper Lig'deki bazı takımların vadesi geçmiş borçlar sebebiyle transfer tahtasının geçici olarak kapatıldığı ve 15 günlük ek süre tanındığı bildirildi.",
      category = "Mali Durum & Borçlar",
      source = "TFF Resmi Bülteni & Finans Masası",
      author = "Atilla Gökçe",
      publishedAgo = "42 dk önce",
      readTimeMinutes = 5,
      readCount = 4250,
      relatedMatchId = null,
      relatedMatchTeams = null,
      emojiBadge = "💰",
      tags = listOf("Mali Kriz", "FFP", "TFF", "Transfer Yasağı", "Ekonomi")
    ),
    ArenaNewsArticle(
      id = "news_relegation_championship",
      title = "Monte Carlo Simülasyonu: Küme Düşme Barajı 42 Puan, Şampiyonluk Olasılıkları Belli Oldu",
      summary = "Yapay zeka 10.000 simülasyon çalıştırdı. Şampiyonluk şansı: %54 Galatasaray, %42 Fenerbahçe, %4 Beşiktaş. Düşme hattında tehlike çanları.",
      content = "Tahmin Arena AI Algoritmaları tarafından 10.000 sezon simülasyonu ile yapılan analize göre bu sezon Süper Lig'de ligde kalma barajının 42 puan olacağı öngörüldü. Küme düşme potasında yer alan takımların iç saha puan kayıpları kritik eşiğe ulaştı. Zirve yarışında ise şampiyonluk ihtimali %54 ile Galatasaray, %42 ile Fenerbahçe arasında iki kutuplu bir rekabeti işaret ediyor.",
      category = "Küme Düşme & Zirve",
      source = "Tahmin Arena AI Algoritma Masası",
      author = "Bülent Timurlenk",
      publishedAgo = "1 saat önce",
      readTimeMinutes = 4,
      readCount = 7340,
      relatedMatchId = "m1",
      relatedMatchTeams = "Galatasaray - Fenerbahçe",
      emojiBadge = "📉",
      tags = listOf("Küme Düşme", "Şampiyonluk", "Monte Carlo", "Süper Lig", "Simülasyon")
    ),
    ArenaNewsArticle(
      id = "news_2",
      title = "Şampiyonlar Ligi'nde Erken Final: Real Madrid - Barcelona El Clasico Heyecanı",
      summary = "Santiago Bernabeu'da Avrupa'nın en büyük randevusu için geri sayım başladı. İki dev kulübün son 5 maçtaki gol ortalaması 3.4'e ulaştı.",
      content = "Devler Ligi çeyrek final eşleşmesinde Real Madrid ve Barcelona İspanya sınırlarını aşan bir rekabetle sahne alıyor. Ancelotti'nin hücum hattındaki hızlı ayakları ile Flick'in yüksek baskılı pres oyunu çarpışacak. Bültende 2.5 Üst oranı 1.55 ile yoğun ilgi görüyor.",
      category = "Süper Lig",
      source = "UEFA News & Reuters",
      author = "Kerim Can Özdal",
      publishedAgo = "1 saat önce",
      readTimeMinutes = 3,
      readCount = 3910,
      relatedMatchId = "m2",
      relatedMatchTeams = "Real Madrid - Barcelona",
      emojiBadge = "⭐",
      tags = listOf("Şampiyonlar Ligi", "El Clasico", "Real Madrid", "Barcelona")
    ),
    ArenaNewsArticle(
      id = "news_euroleague",
      title = "EuroLeague Çift Maç Haftası: Anadolu Efes ve Fenerbahçe Beko'dan Kritik Sınav",
      summary = "Avrupa basketbolunun kalbi parkede atıyor. Guard rotasyonları, sakatlık raporları ve pota altı ribaund istatistikleri masada.",
      content = "Turkish Airlines EuroLeague'de çift maç haftası heyecanı başladı. Temsilcimiz Anadolu Efes deplasmanda Panathinaikos ile karşılaşırken, Fenerbahçe Beko ise evinde Olympiakos'u ağırlıyor. Dış şut isabeti ve serbest atış çizgisi kaderi belirleyecek.",
      category = "Basketbol",
      source = "EuroLeague Basketball Media",
      author = "İsmail Şenol",
      publishedAgo = "2 saat önce",
      readTimeMinutes = 3,
      readCount = 3450,
      relatedMatchId = "m13",
      relatedMatchTeams = "Anadolu Efes - Panathinaikos",
      emojiBadge = "🏀",
      tags = listOf("EuroLeague", "Basketbol", "Efes", "Fenerbahçe Beko")
    ),
    ArenaNewsArticle(
      id = "news_volleyball",
      title = "Sultanlar Ligi'nde Derbi Haftası: VakıfBank - Eczacıbaşı Dynavit Randevusu",
      summary = "Dünya voleybolunun en prestijli liginde namağlup iki dev karşı karşıya. Blok savunması ve smaçör verimliliği analiz edildi.",
      content = "Vodafone Sultanlar Ligi'nde haftanın maçında VakıfBank ile Eczacıbaşı Dynavit kozlarını paylaşıyor. Tijana Boskovic ve Zehra Güneş gibi dünya yıldızlarının sahne alacağı dev maçta set bahisleri yoğun ilgi görüyor. Maçkolik canlı anlatımında anlık ralli takibi yapılacak.",
      category = "Voleybol",
      source = "TVF & CEV Media",
      author = "Enis Karakaya",
      publishedAgo = "3 saat önce",
      readTimeMinutes = 3,
      readCount = 2890,
      relatedMatchId = null,
      relatedMatchTeams = null,
      emojiBadge = "🏐",
      tags = listOf("Sultanlar Ligi", "Voleybol", "VakıfBank", "Eczacıbaşı")
    ),
    ArenaNewsArticle(
      id = "news_motorsports",
      title = "MotoGP & WRC Ralli Analizi: Mugello GP Pole Savaşı ve Çakıl Etap Zorluğu",
      summary = "Bagnaia ile Martin arasında şampiyonluk düellosu son virajda. WRC Akropolis Rallisi'nde lastik stratejisi belirleyici oldu.",
      content = "Mugello'da gerçekleştirilen MotoGP sıralama turlarında Francesco Bagnaia 1:44.855 ile pist rekorunu kırarak pole pozisyonunu kaptı. Arka düzlükte 361 km/h azami hıza ulaşan Ducati'ler rakiplerine fark attı. WRC cephesinde ise sert çakıl taşları sebebiyle pilotlar yumuşak lastik tercihinden kaçınıyor.",
      category = "Motorspor",
      source = "Motorsport.com & Dorna",
      author = "Serhan Acar",
      publishedAgo = "4 saat önce",
      readTimeMinutes = 4,
      readCount = 4120,
      relatedMatchId = "m10004",
      relatedMatchTeams = "Francesco Bagnaia - Jorge Martin",
      emojiBadge = "🏍️",
      tags = listOf("MotoGP", "WRC", "Motorspor", "Bagnaia", "Martin")
    )
  )
}
