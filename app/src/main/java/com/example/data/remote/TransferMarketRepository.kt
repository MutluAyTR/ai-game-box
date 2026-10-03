package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.AiTransferEvaluation
import com.example.data.model.HistoricalTransfer
import com.example.data.model.LiveTransferItem
import com.example.data.model.TransferMarketPlayer
import com.example.data.model.UserVirtualTransfer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Maçkolik & KAP Onaylı Canlı Transfer Masası ve Alım-Satım AI Yönetimi
 */
object TransferMarketRepository {

  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private val _liveTransfers = MutableStateFlow<List<LiveTransferItem>>(getCuratedLiveTransfers())
  val liveTransfersFlow: Flow<List<LiveTransferItem>> = _liveTransfers.asStateFlow()

  private val _availablePlayers = MutableStateFlow<List<TransferMarketPlayer>>(getCuratedAvailablePlayers())
  val availablePlayersFlow: Flow<List<TransferMarketPlayer>> = _availablePlayers.asStateFlow()

  private val _userVirtualTransfers = MutableStateFlow<List<UserVirtualTransfer>>(emptyList())
  val userVirtualTransfersFlow: Flow<List<UserVirtualTransfer>> = _userVirtualTransfers.asStateFlow()

  private val _isGeneratingAiTransfer = MutableStateFlow(false)
  val isGeneratingAiTransfer: Flow<Boolean> = _isGeneratingAiTransfer.asStateFlow()

  /**
   * Executes a virtual transfer request using user's token balance.
   */
  fun executeVirtualTransfer(
    player: TransferMarketPlayer,
    tokenBid: Long,
    userBalance: Long
  ): Result<UserVirtualTransfer> {
    if (userBalance < tokenBid) {
      return Result.failure(IllegalStateException("Yetersiz TP Bakiyesi! Mevcut bakiyeniz: $userBalance TP, Teklif: $tokenBid TP."))
    }
    val minAcceptableBid = (player.tokenPrice * 0.70).toLong()
    if (tokenBid < minAcceptableBid) {
      return Result.failure(IllegalStateException("Kulüp bu teklifi kabul etmedi! ${player.club} en az $minAcceptableBid TP talep ediyor."))
    }

    val transfer = UserVirtualTransfer(
      id = "v_trans_${System.currentTimeMillis()}",
      player = player,
      tokenPaid = tokenBid,
      date = "Bugün",
      contractYears = 3,
      verdictSummary = "Anlaşma Sağlandı: ${player.name}, $tokenBid TP karşılığında sanal kadronuza katıldı! ✍️"
    )

    val current = _userVirtualTransfers.value.toMutableList()
    current.add(0, transfer)
    _userVirtualTransfers.value = current

    return Result.success(transfer)
  }

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  init {
    // Background simulation of new KAP alerts during active transfer window
    scope.launch {
      while (true) {
        delay(45_000L)
        simulateNewKapNotification()
      }
    }
  }

  private fun simulateNewKapNotification() {
    val potentialTransfers = listOf(
      LiveTransferItem(
        id = "kap_auto_${System.currentTimeMillis()}",
        playerName = "Ademola Lookman",
        fromTeam = "Atalanta",
        toTeam = "Paris Saint-Germain",
        transferFee = "€42.000.000",
        feeType = "Bonservis",
        contractYears = 4,
        salaryAnnual = "€6.5M Net",
        status = "KAP Resmi Açıklaması",
        date = "Az önce",
        nationality = "Nijerya",
        age = 26,
        position = "Sol Kanat",
        rating = 8.8,
        ffpImpact = "UEFA FFP Onaylandı (%14 Bütçe Payı)",
        announcementSummary = "KAP Bildirimi: Oyuncunun bonservisi için Atalanta kulübü ile 42M€ ve 3 taksitte ödenmek üzere kesin mutabakata varılmıştır."
      ),
      LiveTransferItem(
        id = "kap_auto_${System.currentTimeMillis() + 1}",
        playerName = "Jhon Durán",
        fromTeam = "Aston Villa",
        toTeam = "Chelsea",
        transferFee = "€35.000.000",
        feeType = "Bonservis + Bonus",
        contractYears = 5,
        salaryAnnual = "€4.2M Net",
        status = "Sağlık Kontrolü",
        date = "1 dk önce",
        nationality = "Kolombiya",
        age = 20,
        position = "Santrafor",
        rating = 8.5,
        ffpImpact = "İngiltere Premier Lig Harcama Sınırı İçinde",
        announcementSummary = "Sağlık kontrollerini tamamlamak üzere Londra'ya gelen forvet ile 5 yıllık mukavele imzalanacağı bildirildi."
      )
    )
    val randomTransfer = potentialTransfers[Random.nextInt(potentialTransfers.size)]
    val current = _liveTransfers.value.toMutableList()
    if (current.none { it.playerName == randomTransfer.playerName }) {
      current.add(0, randomTransfer)
      _liveTransfers.value = current
    }
  }

  /**
   * AI Buy/Sell Trading Mode Evaluation using Gemini API REST with local analytical fallback
   */
  suspend fun evaluateAiPlayerNegotiation(
    playerName: String,
    buyerTeam: String,
    sellerTeam: String,
    offeredFeeMillions: Double,
    offeredSalaryMillions: Double,
    isSellingMode: Boolean
  ): AiTransferEvaluation = withContext(Dispatchers.IO) {
    _isGeneratingAiTransfer.value = true
    val apiKey = BuildConfig.GEMINI_API_KEY

    val baseFairValue = when {
      playerName.contains("Osimhen", ignoreCase = true) -> 75.0
      playerName.contains("Icardi", ignoreCase = true) -> 17.0
      playerName.contains("Barış Alper", ignoreCase = true) -> 24.0
      playerName.contains("Ferdi", ignoreCase = true) -> 30.0
      playerName.contains("Szymanski", ignoreCase = true) -> 20.0
      playerName.contains("Immobile", ignoreCase = true) -> 8.0
      playerName.contains("Rafa Silva", ignoreCase = true) -> 14.0
      playerName.contains("Haaland", ignoreCase = true) -> 180.0
      playerName.contains("Mbappe", ignoreCase = true) -> 180.0
      playerName.contains("Vinicius", ignoreCase = true) -> 160.0
      playerName.contains("Musiala", ignoreCase = true) -> 130.0
      playerName.contains("Wirtz", ignoreCase = true) -> 130.0
      playerName.contains("Rodri", ignoreCase = true) -> 130.0
      else -> 18.0 + (playerName.hashCode().coerceAtLeast(0) % 35)
    }

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      _isGeneratingAiTransfer.value = false
      return@withContext calculateAlgorithmicTransferEvaluation(
        playerName, buyerTeam, sellerTeam, offeredFeeMillions, baseFairValue, isSellingMode
      )
    }

    try {
      val prompt = """
        Sen UEFA Finansal Fair Play ve Maçkolik Transfer Komitesi Kıdemli Yapay Zeka Danışmanısın.
        Aşağıdaki transfer teklifini analiz et ve JSON olarak döndür:
        
        Oyuncu: $playerName
        Alıcı Kulüp: $buyerTeam
        Satıcı Kulüp: $sellerTeam
        Teklif Edilen Bonservis: €$offeredFeeMillions Milyon
        Yıllık Maaş Teklifi: €$offeredSalaryMillions Milyon
        Mod: ${if (isSellingMode) "Satış (Oyuncu Elden Çıkarılıyor)" else "Alım (Kadroyu Güçlendirme)"}
        Tahmini Piyasa Değeri: €$baseFairValue Milyon
        
        Gereken JSON formatı:
        {
          "isAccepted": true/false,
          "probabilityPct": 0-100,
          "agentFeedback": "Menajerin ve kulüp başkanının Türkçe cevabı",
          "ffpStatus": "UEFA Uyumlu" / "Riskli Harcama" / "FFP Limit Aşımı",
          "squadSynergyScore": 60-99,
          "xgContributionEstimate": "+0.35 xG / Maç",
          "verdictTitle": "Kabul Edildi / Pazarlık Yapılıyor / Reddedildi",
          "verdictExplanation": "Transferin taktiğe, bütçeye ve FFP limitlerine etkisi hakkında 2-3 cümlelik Türkçe profesyonel açıklama"
        }
        Sadece geçerli bir JSON döndür, kod blokları veya markdown ekleme.
      """.trimIndent()

      val jsonBody = JSONObject().apply {
        val contentsArray = JSONArray().apply {
          val contentObj = JSONObject().apply {
            val partsArray = JSONArray().apply {
              val partObj = JSONObject().apply { put("text", prompt) }
              put(partObj)
            }
            put("parts", partsArray)
          }
          put(contentObj)
        }
        put("contents", contentsArray)
      }

      val request = Request.Builder()
        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
        .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = client.newCall(request).execute()
      if (!response.isSuccessful) {
        _isGeneratingAiTransfer.value = false
        return@withContext calculateAlgorithmicTransferEvaluation(
          playerName, buyerTeam, sellerTeam, offeredFeeMillions, baseFairValue, isSellingMode
        )
      }

      val responseString = response.body?.string() ?: ""
      val rootJson = JSONObject(responseString)
      val text = rootJson.optJSONArray("candidates")?.optJSONObject(0)
        ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text") ?: ""

      val cleanJson = text.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()

      val obj = JSONObject(cleanJson)
      _isGeneratingAiTransfer.value = false

      AiTransferEvaluation(
        playerName = playerName,
        buyerTeam = buyerTeam,
        offeredFeeEurMillions = offeredFeeMillions,
        estimatedFairValueEurMillions = baseFairValue,
        isAccepted = obj.optBoolean("isAccepted", offeredFeeMillions >= baseFairValue * 0.9),
        probabilityPct = obj.optInt("probabilityPct", 78),
        agentFeedback = obj.optString("agentFeedback", "Menajer kulübün projesine ve teklif edilen şartlara sıcak bakıyor."),
        ffpStatus = obj.optString("ffpStatus", "UEFA Uyumlu"),
        squadSynergyScore = obj.optInt("squadSynergyScore", 85),
        xgContributionEstimate = obj.optString("xgContributionEstimate", "+0.28 xG"),
        verdictTitle = obj.optString("verdictTitle", if (offeredFeeMillions >= baseFairValue * 0.9) "Prensipte Anlaşıldı" else "Pazarlık Sürüyor"),
        verdictExplanation = obj.optString("verdictExplanation", "Kulüp finansal yapısı ve teknik heyet raporu bu hamleyi onaylamaktadır.")
      )
    } catch (_: Exception) {
      _isGeneratingAiTransfer.value = false
      calculateAlgorithmicTransferEvaluation(
        playerName, buyerTeam, sellerTeam, offeredFeeMillions, baseFairValue, isSellingMode
      )
    }
  }

  private fun calculateAlgorithmicTransferEvaluation(
    playerName: String,
    buyerTeam: String,
    sellerTeam: String,
    offeredFee: Double,
    fairValue: Double,
    isSellingMode: Boolean
  ): AiTransferEvaluation {
    val ratio = if (fairValue > 0) offeredFee / fairValue else 1.0
    val isAccepted: Boolean
    val prob: Int
    val verdict: String
    val feedback: String
    val ffp = when {
      offeredFee > 60.0 -> "Riskli Harcama (Sıkı FFP Takibi)"
      offeredFee > 100.0 -> "FFP Limit Aşımı Uyarısı"
      else -> "UEFA Uyumlu (TFF Harcama Limiti İçi)"
    }

    if (isSellingMode) {
      if (ratio >= 1.05) {
        isAccepted = true
        prob = 92
        verdict = "Teklif Kabul Edildi (Mükemmel Kâr)"
        feedback = "$buyerTeam yönetimi teklif edilen €${offeredFee}M bonservisi onayladı. Kulüp kasasına net kaynak aktarılacak."
      } else if (ratio >= 0.85) {
        isAccepted = true
        prob = 74
        verdict = "Pazarlık Sonucu Anlaşma"
        feedback = "Küçük bonus maddeleriyle transfer tamamlandı. Bir sonraki satıştan %15 pay şartı eklendi."
      } else {
        isAccepted = false
        prob = 22
        verdict = "Teklif Reddedildi"
        feedback = "$buyerTeam teklif edilen tutarı oyuncunun piyasa değerinin altında buldu."
      }
    } else {
      if (ratio >= 1.1) {
        isAccepted = true
        prob = 95
        verdict = "Transfer Tamamlandı (Bonservis Onaylandı)"
        feedback = "$sellerTeam başkanı teklifi kabul etti. Oyuncu sağlık kontrolü için şehre davet edilecek."
      } else if (ratio >= 0.9) {
        isAccepted = true
        prob = 78
        verdict = "Prensip Anlaşmasına Varıldı"
        feedback = "Ödeme planı 3 yıla yayılarak mutabakat sağlandı. Sözleşme 4 yıllık olacak."
      } else {
        isAccepted = false
        prob = 28
        verdict = "Teklif Yetersiz Bulundu"
        feedback = "$sellerTeam kapıyı €${(fairValue * 1.1).roundToInt()}M seviyesinden açıyor. Teklifi artırmanız bekleniyor."
      }
    }

    return AiTransferEvaluation(
      playerName = playerName,
      buyerTeam = buyerTeam,
      offeredFeeEurMillions = offeredFee,
      estimatedFairValueEurMillions = fairValue,
      isAccepted = isAccepted,
      probabilityPct = prob,
      agentFeedback = feedback,
      ffpStatus = ffp,
      squadSynergyScore = 80 + (ratio * 10).toInt().coerceIn(0, 18),
      xgContributionEstimate = if (ratio >= 1.0) "+0.38 xG / Maç" else "+0.15 xG / Maç",
      verdictTitle = verdict,
      verdictExplanation = "Yapay zeka transfer simülasyonu, $buyerTeam kadrosu ve Süper Lig rekabet dengelerini dikkate alarak bu kararı vermiştir."
    )
  }

  fun getHistoricalTransfers(): List<HistoricalTransfer> = listOf(
    HistoricalTransfer("h1", "2024/2025", "Victor Osimhen", "Napoli", "Galatasaray", "Kiralık (€0)", "€75.000.000", "Türk futbol tarihinin en yüksek piyasa değerli kiralık transferi. Maaş: 6M€"),
    HistoricalTransfer("h2", "2024/2025", "Youssef En-Nesyri", "Sevilla", "Fenerbahçe", "€19.500.000", "€20.000.000", "Fenerbahçe tarihinin bonservis rekoru. 4 taksitte ödenecek."),
    HistoricalTransfer("h3", "2024/2025", "Gabriel Sara", "Norwich City", "Galatasaray", "€18.000.000", "€18.000.000", "Galatasaray tarihinin en pahalı bonservis bedeli. 3 yıllık taksit."),
    HistoricalTransfer("h4", "2024/2025", "Sofyan Amrabat", "Fiorentina", "Fenerbahçe", "Kiralık + €13M Opsiyon", "€22.000.000", "Zorunlu satın alma opsiyonlu orta saha takviyesi."),
    HistoricalTransfer("h5", "2024/2025", "Ciro Immobile", "Lazio", "Beşiktaş", "€3.000.000", "€4.000.000", "Yıllık 6M€ maaş ile Beşiktaş'ın yeni 9 numarası oldu."),
    HistoricalTransfer("h6", "2024/2025", "Rafa Silva", "Benfica", "Beşiktaş", "Bedelsiz (Serbest)", "€14.000.000", "3 yıl için 10M€ imza parası ve yıllık 6M€ garanti ücret."),
    HistoricalTransfer("h7", "2024/2025", "Kylian Mbappé", "Paris Saint-Germain", "Real Madrid", "Bedelsiz (Serbest)", "€180.000.000", "Yüzyılın transferi. 100M€ imza parası ve 5 yıllık dev mukavele."),
    HistoricalTransfer("h8", "2024/2025", "Julián Álvarez", "Manchester City", "Atlético Madrid", "€75.000.000", "€90.000.000", "La Liga'nın yaz dönemindeki en büyük nakit bonservis harcaması."),
    HistoricalTransfer("h9", "2023/2024", "Arda Güler", "Fenerbahçe", "Real Madrid", "€20.000.000 + €10M Bonus", "€15.000.000", "Türk genç yetenek rekoru ve sonraki satıştan %20 pay."),
    HistoricalTransfer("h10", "2023/2024", "Sacha Boey", "Galatasaray", "Bayern München", "€30.000.000 + €5M Bonus", "€22.000.000", "Süper Lig tarihinin en yüksek oyuncu satış rekoru.")
  )

  private fun getCuratedLiveTransfers(): List<LiveTransferItem> = listOf(
    LiveTransferItem(
      id = "trans_1",
      playerName = "Victor Osimhen",
      fromTeam = "Napoli",
      toTeam = "Galatasaray",
      transferFee = "Kiralık (€0 Bonservis)",
      feeType = "Kiralık",
      contractYears = 1,
      salaryAnnual = "€6.000.000 Net",
      status = "KAP Resmi Açıklaması",
      date = "Bugün 18:45",
      nationality = "Nijerya",
      age = 25,
      position = "Santrafor",
      rating = 9.4,
      ffpImpact = "UEFA FFP Uyumlu (Maaş Sponsor Destekli)",
      announcementSummary = "Kamuoyu Aydınlatma Platformu'na yapılan bildirimde futbolcunun 2024-2025 sezonu için geçici transferi konusunda Napoli kulübü ile anlaşmaya varılmıştır."
    ),
    LiveTransferItem(
      id = "trans_2",
      playerName = "Youssef En-Nesyri",
      fromTeam = "Sevilla",
      toTeam = "Fenerbahçe",
      transferFee = "€19.500.000",
      feeType = "Bonservis",
      contractYears = 4,
      salaryAnnual = "€4.000.000 Net",
      status = "KAP Resmi Açıklaması",
      date = "Dün 21:15",
      nationality = "Fas",
      age = 27,
      position = "Santrafor",
      rating = 9.0,
      ffpImpact = "Kulüp Rekoru (4 Eşit Taksit)",
      announcementSummary = "Fenerbahçe Futbol A.Ş., Faslı milli golcü Youssef En-Nesyri'nin transferi için Sevilla kulübüne 19.5 milyon Euro ödeneceğini resmen açıkladı."
    ),
    LiveTransferItem(
      id = "trans_3",
      playerName = "Ciro Immobile",
      fromTeam = "Lazio",
      toTeam = "Beşiktaş",
      transferFee = "€3.000.000",
      feeType = "Bonservis",
      contractYears = 2,
      salaryAnnual = "€6.000.000 Net",
      status = "İmzalandı / KAP",
      date = "2 gün önce",
      nationality = "İtalya",
      age = 34,
      position = "Santrafor",
      rating = 8.9,
      ffpImpact = "Düşük Bonservis • Yüksek Maaş Dengesi",
      announcementSummary = "Beşiktaş Jimnastik Kulübü, İtalyan golcü Ciro Immobile ile 2 yıllık resmi sözleşme imzalandığını duyurdu."
    ),
    LiveTransferItem(
      id = "trans_4",
      playerName = "Sofyan Amrabat",
      fromTeam = "Fiorentina",
      toTeam = "Fenerbahçe",
      transferFee = "Kiralık + €13.000.000 Zorunlu Opsiyon",
      feeType = "Opsiyonlu Kiralık",
      contractYears = 4,
      salaryAnnual = "€3.800.000 Net",
      status = "KAP Resmi Açıklaması",
      date = "3 gün önce",
      nationality = "Fas",
      age = 28,
      position = "Ön Libero",
      rating = 8.8,
      ffpImpact = "Zorunlu Opsiyon 2025/2026 Bütçesine Dahil",
      announcementSummary = "Fenerbahçe, Fiorentina ile Amrabat'ın geçici transferi ve şarta bağlı zorunlu satın alma opsiyonu konusunda anlaşma sağladı."
    ),
    LiveTransferItem(
      id = "trans_5",
      playerName = "Gabriel Sara",
      fromTeam = "Norwich City",
      toTeam = "Galatasaray",
      transferFee = "€18.000.000",
      feeType = "Bonservis",
      contractYears = 5,
      salaryAnnual = "€2.750.000 Net",
      status = "KAP Resmi Açıklaması",
      date = "4 gün önce",
      nationality = "Brezilya",
      age = 25,
      position = "Merkez Orta Saha",
      rating = 9.1,
      ffpImpact = "3 Yılda 6 Eşit Taksitte Ödenecek",
      announcementSummary = "Galatasaray Sportif A.Ş., Gabriel Davi Gomes Sara'nın transferi konusunda Norwich City FC ile mutabakata vardı."
    ),
    LiveTransferItem(
      id = "trans_6",
      playerName = "Simon Banza",
      fromTeam = "Braga",
      toTeam = "Trabzonspor",
      transferFee = "Kiralık (€2.000.000)",
      feeType = "Kiralık",
      contractYears = 1,
      salaryAnnual = "€2.100.000 Net",
      status = "KAP Resmi Açıklaması",
      date = "5 gün önce",
      nationality = "Kongo DC",
      age = 28,
      position = "Santrafor",
      rating = 8.7,
      ffpImpact = "TFF Harcama Limiti Onaylandı",
      announcementSummary = "Trabzonspor, Portekiz'in SC Braga kulübünden profesyonel futbolcu Simon Banza'yı 1 yıl kiraladığını KAP'a bildirdi."
    )
  )

  private fun getCuratedAvailablePlayers(): List<TransferMarketPlayer> = listOf(
    TransferMarketPlayer(
      id = "p_osimhen",
      name = "Victor Osimhen",
      club = "Galatasaray",
      league = "Trendyol Süper Lig",
      position = "SNT",
      age = 25,
      rating = 9.4,
      marketValueEur = "€75.000.000",
      tokenPrice = 1500L,
      nationality = "Nijerya",
      statusText = "Kiralık (Kulüple Sözleşme Aşamasında)",
      xgPerMatch = "+0.85 xG",
      scoutingSummary = "Dünya standartlarında patlayıcı forvet, olağanüstü hava topu hakimiyeti ve ceza sahası bitiriciliği."
    ),
    TransferMarketPlayer(
      id = "p_icardi",
      name = "Mauro Icardi",
      club = "Galatasaray",
      league = "Trendyol Süper Lig",
      position = "SNT",
      age = 31,
      rating = 9.1,
      marketValueEur = "€17.000.000",
      tokenPrice = 750L,
      nationality = "Arjantin",
      statusText = "Sözleşmeli Yıldız",
      xgPerMatch = "+0.72 xG",
      scoutingSummary = "Ölümcül ceza sahası sezgisi, liderlik ve tek dokunuşla skor üretme kabiliyeti."
    ),
    TransferMarketPlayer(
      id = "p_rafa",
      name = "Rafa Silva",
      club = "Beşiktaş",
      league = "Trendyol Süper Lig",
      position = "ONN",
      age = 31,
      rating = 9.2,
      marketValueEur = "€14.000.000",
      tokenPrice = 700L,
      nationality = "Portekiz",
      statusText = "Kilit Oyun Kurucu",
      xgPerMatch = "+0.55 xG",
      scoutingSummary = "Dribling, dikine hücum hızlanması ve ceza sahası çevresinde yüksek anahtar pas yüzdesi."
    ),
    TransferMarketPlayer(
      id = "p_nesyri",
      name = "Youssef En-Nesyri",
      club = "Fenerbahçe",
      league = "Trendyol Süper Lig",
      position = "SNT",
      age = 27,
      rating = 8.9,
      marketValueEur = "€22.000.000",
      tokenPrice = 900L,
      nationality = "Fas",
      statusText = "Yeni Transfer / Rekor Bonservis",
      xgPerMatch = "+0.68 xG",
      scoutingSummary = "Olağanüstü sıçrama ve kafa vuruşu becerisi, yüksek fiziksel pres gücü."
    ),
    TransferMarketPlayer(
      id = "p_baris",
      name = "Barış Alper Yılmaz",
      club = "Galatasaray",
      league = "Trendyol Süper Lig",
      position = "SĞK",
      age = 24,
      rating = 9.0,
      marketValueEur = "€24.000.000",
      tokenPrice = 950L,
      nationality = "Türkiye",
      statusText = "Avrupa Scoutlarının Radarında",
      xgPerMatch = "+0.45 xG",
      scoutingSummary = "Yorulmak bilmeyen atletizm, kanat yıpratıcılığı ve çift yönlü sprint gücü."
    ),
    TransferMarketPlayer(
      id = "p_ferdi",
      name = "Ferdi Kadıoğlu",
      club = "Brighton & Hove Albion",
      league = "Premier League",
      position = "SLB",
      age = 24,
      rating = 9.1,
      marketValueEur = "€30.000.000",
      tokenPrice = 1100L,
      nationality = "Türkiye",
      statusText = "Premier Lig Yıldızı",
      xgPerMatch = "+0.25 xG",
      scoutingSummary = "Modern ters ayaklı bek, kusursuz pas dağıtımı ve orta saha geçiş zekası."
    ),
    TransferMarketPlayer(
      id = "p_sara",
      name = "Gabriel Sara",
      club = "Galatasaray",
      league = "Trendyol Süper Lig",
      position = "OS",
      age = 25,
      rating = 9.0,
      marketValueEur = "€20.000.000",
      tokenPrice = 850L,
      nationality = "Brezilya",
      statusText = "Orta Saha Maestro",
      xgPerMatch = "+0.38 xG",
      scoutingSummary = "Duran top uzmanı, kilit pas organizasyonu ve yüksek pas isabeti."
    ),
    TransferMarketPlayer(
      id = "p_szymanski",
      name = "Sebastian Szymański",
      club = "Fenerbahçe",
      league = "Trendyol Süper Lig",
      position = "ONN",
      age = 25,
      rating = 8.8,
      marketValueEur = "€19.000.000",
      tokenPrice = 800L,
      nationality = "Polonya",
      statusText = "Tekliflere Açık",
      xgPerMatch = "+0.42 xG",
      scoutingSummary = "Ön alan pres lideri, uzaktan şut tehdidi ve yüksek koşu mesafesi."
    ),
    TransferMarketPlayer(
      id = "p_semih",
      name = "Semih Kılıçsoy",
      club = "Beşiktaş",
      league = "Trendyol Süper Lig",
      position = "SNT",
      age = 19,
      rating = 8.7,
      marketValueEur = "€15.000.000",
      tokenPrice = 650L,
      nationality = "Türkiye",
      statusText = "Genç Yetenek / Geleceğin Yıldızı",
      xgPerMatch = "+0.50 xG",
      scoutingSummary = "Alçak ağırlık merkezi, güçlü fiziksel direnç ve her iki ayakla sert şut kabiliyeti."
    ),
    TransferMarketPlayer(
      id = "p_arda",
      name = "Arda Güler",
      club = "Real Madrid",
      league = "La Liga",
      position = "ONN",
      age = 19,
      rating = 9.3,
      marketValueEur = "€45.000.000",
      tokenPrice = 1400L,
      nationality = "Türkiye",
      statusText = "Dünya Yıldızı Adayı",
      xgPerMatch = "+0.55 xG",
      scoutingSummary = "Klas sol ayak, dar alanda sihirli çalımlar ve ceza sahası dışından nokta vuruşlar."
    ),
    TransferMarketPlayer(
      id = "p_haaland",
      name = "Erling Haaland",
      club = "Manchester City",
      league = "Premier League",
      position = "SNT",
      age = 24,
      rating = 9.8,
      marketValueEur = "€180.000.000",
      tokenPrice = 3000L,
      nationality = "Norveç",
      statusText = "Süperstar / Satış Dışı",
      xgPerMatch = "+1.15 xG",
      scoutingSummary = "Durdurulamaz gol makinesi, inanılmaz hız, güç ve klinik bitiricilik."
    ),
    TransferMarketPlayer(
      id = "p_mbappe",
      name = "Kylian Mbappé",
      club = "Real Madrid",
      league = "La Liga",
      position = "SNT",
      age = 25,
      rating = 9.8,
      marketValueEur = "€180.000.000",
      tokenPrice = 3000L,
      nationality = "Fransa",
      statusText = "Galáctico Lideri",
      xgPerMatch = "+1.05 xG",
      scoutingSummary = "Dünyanın en hızlı forveti, açık alanda rakipsiz hız ve teknik."
    ),
    TransferMarketPlayer(
      id = "p_vinicius",
      name = "Vinicius Jr",
      club = "Real Madrid",
      league = "La Liga",
      position = "SLK",
      age = 24,
      rating = 9.7,
      marketValueEur = "€160.000.000",
      tokenPrice = 2800L,
      nationality = "Brezilya",
      statusText = "Ballon d'Or Adayı",
      xgPerMatch = "+0.75 xG",
      scoutingSummary = "Dünyanın en iyi kanat forveti, birebirde durdurulamaz dribling ve skor katkısı."
    ),
    TransferMarketPlayer(
      id = "p_muslera",
      name = "Fernando Muslera",
      club = "Galatasaray",
      league = "Trendyol Süper Lig",
      position = "KL",
      age = 38,
      rating = 8.8,
      marketValueEur = "€1.500.000",
      tokenPrice = 250L,
      nationality = "Uruguay",
      statusText = "Efsane Kaptan",
      xgPerMatch = "0.0 xG",
      scoutingSummary = "Refleksleri, tecrübesi ve kritik kurtarışlarıyla kulüp tarihinin en başarılı kalecisi."
    ),
    TransferMarketPlayer(
      id = "p_livakovic",
      name = "Dominik Livaković",
      club = "Fenerbahçe",
      league = "Trendyol Süper Lig",
      position = "KL",
      age = 29,
      rating = 8.9,
      marketValueEur = "€11.000.000",
      tokenPrice = 500L,
      nationality = "Hırvatistan",
      statusText = "1 Numaralı File Bekçisi",
      xgPerMatch = "0.0 xG",
      scoutingSummary = "Penaltı canavarı, çizgi üzerinde üstün refleksler ve milli takım tecrübesi."
    ),
    TransferMarketPlayer(
      id = "p_sanchez",
      name = "Davinson Sánchez",
      club = "Galatasaray",
      league = "Trendyol Süper Lig",
      position = "STP",
      age = 28,
      rating = 9.0,
      marketValueEur = "€18.000.000",
      tokenPrice = 800L,
      nationality = "Kolombiya",
      statusText = "Savunma Lideri",
      xgPerMatch = "+0.12 xG",
      scoutingSummary = "Hızlı, agresif hamle stoperi, yüksek kademe başarısı ve geriden oyun kurma."
    )
  )
}
