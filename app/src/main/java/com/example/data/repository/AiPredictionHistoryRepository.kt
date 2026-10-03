package com.example.data.repository

import com.example.data.model.HistoricalAiPrediction
import com.example.data.model.Sport
import com.example.data.model.TeamFormPoint
import com.example.data.model.VisualStandingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AiPredictionHistoryRepository {

  private val _predictions = MutableStateFlow<List<HistoricalAiPrediction>>(getCuratedPredictions())
  val predictionsFlow: Flow<List<HistoricalAiPrediction>> = _predictions.asStateFlow()

  fun getCuratedPredictions(): List<HistoricalAiPrediction> = listOf(
    HistoricalAiPrediction(
      id = "pred_1",
      matchTitle = "Galatasaray vs Fenerbahçe",
      league = "Trendyol Süper Lig",
      sport = Sport.FOOTBALL,
      matchDate = "21 Eylül 2024",
      predictedTip = "MS 1 & 2.5 Üst",
      predictedOdds = 2.45,
      actualOutcomeScore = "3 - 1",
      isWon = true,
      confidence = 86,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "Galatasaray'ın orta saha pres hacmi ve Icardi-Osimhen ikilisinin yüksek bitiricilik xG beklentisiyle derbiyi kazanacağı ve en az 3 gol çıkacağı tahmin edildi."
    ),
    HistoricalAiPrediction(
      id = "pred_2",
      matchTitle = "Real Madrid vs Barcelona",
      league = "La Liga",
      sport = Sport.FOOTBALL,
      matchDate = "26 Ekim 2024",
      predictedTip = "2.5 Üst & KG Var",
      predictedOdds = 1.78,
      actualOutcomeScore = "0 - 4",
      isWon = true,
      confidence = 88,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "El Clásico'da her iki ekibin ofansif geçiş oyunları ve yüksek tempo analiziyle gollü bir karşılaşma geçeceği öngörüldü."
    ),
    HistoricalAiPrediction(
      id = "pred_3",
      matchTitle = "Arsenal vs Paris Saint-Germain",
      league = "UEFA Şampiyonlar Ligi",
      sport = Sport.FOOTBALL,
      matchDate = "1 Ekim 2024",
      predictedTip = "MS 1 (Arsenal Kazanır)",
      predictedOdds = 1.82,
      actualOutcomeScore = "2 - 0",
      isWon = true,
      confidence = 81,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "Arteta'nın duran top set hücumları ve Emirates'teki kusursuz savunma organizasyonu ile PSG geçişlerinin durdurulacağı modellendi."
    ),
    HistoricalAiPrediction(
      id = "pred_4",
      matchTitle = "Manchester City vs Inter Milan",
      league = "UEFA Şampiyonlar Ligi",
      sport = Sport.FOOTBALL,
      matchDate = "18 Eylül 2024",
      predictedTip = "MS 1 & 1.5 Üst",
      predictedOdds = 1.65,
      actualOutcomeScore = "0 - 0",
      isWon = false,
      confidence = 79,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "City'nin Etihad'da gol bulması beklenirken Inzaghi'nin 5'li blok savunması gol beklentisini 0.85 xG'de tutarak maçı kilitledi."
    ),
    HistoricalAiPrediction(
      id = "pred_5",
      matchTitle = "Panathinaikos vs Fenerbahçe Beko",
      league = "EuroLeague Basketball",
      sport = Sport.BASKETBALL,
      matchDate = "29 Mart 2024",
      predictedTip = "Toplam Sayı 162.5 Üst",
      predictedOdds = 1.90,
      actualOutcomeScore = "85 - 82",
      isWon = true,
      confidence = 83,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "OAKA'daki tempolu hücumlar ve iki takımın yüksek dış atış yüzdesi toplam sayının 162.5 baremini aşmasını sağladı."
    ),
    HistoricalAiPrediction(
      id = "pred_6",
      matchTitle = "Francesco Bagnaia vs Jorge Martin",
      league = "MotoGP Misano Grand Prix",
      sport = Sport.MOTORSPORTS,
      matchDate = "8 Eylül 2024",
      predictedTip = "Yarış Kazananı: Marc Marquez",
      predictedOdds = 3.20,
      actualOutcomeScore = "P1: Marquez (25 P)",
      isWon = true,
      confidence = 74,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "Hafif yağmur ihtimali ve Misano'nun kaygan virajlarında Marquez'in ıslak zemin telemetry üstünlüğüyle sürpriz zafer öngörüldü."
    ),
    HistoricalAiPrediction(
      id = "pred_7",
      matchTitle = "Beşiktaş vs Eyüpspor",
      league = "Trendyol Süper Lig",
      sport = Sport.FOOTBALL,
      matchDate = "22 Eylül 2024",
      predictedTip = "MS 1 & KG Var",
      predictedOdds = 2.60,
      actualOutcomeScore = "2 - 1",
      isWon = true,
      confidence = 82,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "Eyüpspor'un hızlı hücum tehdidiyle gol bulacağı ancak Beşiktaş'ın taraftar desteği ve Immobile ile maçı koparacağı öngörüldü."
    ),
    HistoricalAiPrediction(
      id = "pred_8",
      matchTitle = "Bayern Münih vs Bayer Leverkusen",
      league = "Bundesliga",
      sport = Sport.FOOTBALL,
      matchDate = "28 Eylül 2024",
      predictedTip = "3.5 Üst",
      predictedOdds = 2.05,
      actualOutcomeScore = "1 - 1",
      isWon = false,
      confidence = 77,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "İki hücum canavarının karşılaşmasında Xabi Alonso'nun temkinli orta saha bloğu nedeniyle maç beklentinin altında gollü bitti."
    ),
    HistoricalAiPrediction(
      id = "pred_9",
      matchTitle = "Trabzonspor vs Konyaspor",
      league = "Trendyol Süper Lig",
      sport = Sport.FOOTBALL,
      matchDate = "29 Eylül 2024",
      predictedTip = "MS 1 (Trabzonspor)",
      predictedOdds = 1.72,
      actualOutcomeScore = "3 - 2",
      isWon = true,
      confidence = 85,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "Şenol Güneş yönetimindeki Trabzonspor'un kanat bindirmeleri ve Banza'nın ceza sahası etkinliğiyle galibiyete uzanacağı doğru tahmin edildi."
    ),
    HistoricalAiPrediction(
      id = "pred_10",
      matchTitle = "Liverpool vs Chelsea",
      league = "Premier League",
      sport = Sport.FOOTBALL,
      matchDate = "20 Ekim 2024",
      predictedTip = "MS 1 & 2.5 Üst",
      predictedOdds = 2.20,
      actualOutcomeScore = "2 - 1",
      isWon = true,
      confidence = 80,
      geminiModel = "Gemini 2.5 Flash",
      analysisSummary = "Slot'un Liverpool'unun Anfield'daki geçiş hücumu hızı ve Chelsea'nin defans zaafları doğrulanarak kupon kazandırdı."
    )
  )

  /**
   * Generates realistic form trend data for a team (last 5-8 matches)
   * used by the Recharts / D3 Compose graph component.
   */
  fun getTeamFormTrend(teamName: String): List<TeamFormPoint> {
    return when {
      teamName.contains("Galatasaray", ignoreCase = true) -> listOf(
        TeamFormPoint(1, "Hatayspor", true, "W", 3, 2, 1, 2.4, "2 - 1"),
        TeamFormPoint(2, "Konyaspor", false, "W", 3, 2, 1, 1.8, "2 - 1"),
        TeamFormPoint(3, "Adana Demirspor", false, "W", 3, 5, 1, 3.6, "5 - 1"),
        TeamFormPoint(4, "Çaykur Rizespor", true, "W", 3, 5, 0, 4.1, "5 - 0"),
        TeamFormPoint(5, "Fenerbahçe", false, "W", 3, 3, 1, 2.3, "3 - 1"),
        TeamFormPoint(6, "Kasımpaşa", true, "D", 1, 3, 3, 2.8, "3 - 3"),
        TeamFormPoint(7, "Alanyaspor", true, "W", 3, 1, 0, 1.9, "1 - 0")
      )
      teamName.contains("Fenerbahçe", ignoreCase = true) -> listOf(
        TeamFormPoint(1, "Adana Demirspor", true, "W", 3, 1, 0, 2.1, "1 - 0"),
        TeamFormPoint(2, "Göztepe", false, "D", 1, 2, 2, 1.7, "2 - 2"),
        TeamFormPoint(3, "Çaykur Rizespor", false, "W", 3, 5, 0, 3.2, "5 - 0"),
        TeamFormPoint(4, "Alanyaspor", true, "W", 3, 3, 0, 2.7, "3 - 0"),
        TeamFormPoint(5, "Kasımpaşa", false, "W", 3, 2, 0, 1.9, "2 - 0"),
        TeamFormPoint(6, "Galatasaray", true, "L", 0, 1, 3, 1.4, "1 - 3"),
        TeamFormPoint(7, "Antalyaspor", false, "W", 3, 2, 0, 2.2, "2 - 0")
      )
      teamName.contains("Beşiktaş", ignoreCase = true) -> listOf(
        TeamFormPoint(1, "Samsunspor", false, "W", 3, 2, 0, 1.8, "2 - 0"),
        TeamFormPoint(2, "Antalyaspor", true, "W", 3, 4, 2, 2.9, "4 - 2"),
        TeamFormPoint(3, "Sivasspor", true, "W", 3, 2, 0, 2.1, "2 - 0"),
        TeamFormPoint(4, "Trabzonspor", false, "D", 1, 1, 1, 1.5, "1 - 1"),
        TeamFormPoint(5, "Eyüpspor", true, "W", 3, 2, 1, 2.4, "2 - 1"),
        TeamFormPoint(6, "Kayserispor", false, "W", 3, 3, 0, 2.8, "3 - 0"),
        TeamFormPoint(7, "Gaziantep FK", false, "D", 1, 1, 1, 1.6, "1 - 1")
      )
      else -> listOf(
        TeamFormPoint(1, "Rakip 1", true, "W", 3, 2, 0, 1.8, "2 - 0"),
        TeamFormPoint(2, "Rakip 2", false, "D", 1, 1, 1, 1.4, "1 - 1"),
        TeamFormPoint(3, "Rakip 3", true, "W", 3, 3, 1, 2.5, "3 - 1"),
        TeamFormPoint(4, "Rakip 4", false, "L", 0, 0, 2, 0.9, "0 - 2"),
        TeamFormPoint(5, "Rakip 5", true, "W", 3, 2, 1, 2.0, "2 - 1")
      )
    }
  }

  /**
   * Generates mock authentic Süper Lig standings for the visual standings chart.
   */
  fun getSuperLigStandings(): List<VisualStandingItem> = listOf(
    VisualStandingItem(1, "Galatasaray", 8, 7, 1, 0, 24, 8, 16, 22, listOf("W", "W", "W", "W", "D"), isUserFavorite = true),
    VisualStandingItem(2, "Fenerbahçe", 8, 6, 1, 1, 20, 7, 13, 19, listOf("W", "W", "W", "L", "W")),
    VisualStandingItem(3, "Samsunspor", 8, 6, 0, 2, 14, 7, 7, 18, listOf("W", "W", "W", "W", "W")),
    VisualStandingItem(4, "Beşiktaş", 7, 5, 2, 0, 15, 5, 10, 17, listOf("W", "W", "D", "W", "D")),
    VisualStandingItem(5, "Eyüpspor", 8, 4, 3, 1, 14, 8, 6, 15, listOf("D", "L", "W", "W", "W")),
    VisualStandingItem(6, "Göztepe", 8, 3, 4, 1, 14, 10, 4, 13, listOf("D", "W", "W", "L", "D")),
    VisualStandingItem(7, "Trabzonspor", 7, 2, 5, 0, 7, 5, 2, 11, listOf("D", "D", "D", "W", "D")),
    VisualStandingItem(8, "Kasımpaşa", 8, 2, 4, 2, 9, 10, -1, 10, listOf("D", "L", "D", "W", "D")),
    VisualStandingItem(9, "Konyaspor", 8, 2, 2, 4, 8, 11, -3, 8, listOf("L", "L", "W", "D", "L")),
    VisualStandingItem(10, "Alanyaspor", 8, 2, 2, 4, 7, 12, -5, 8, listOf("L", "L", "W", "L", "L"))
  )
}
