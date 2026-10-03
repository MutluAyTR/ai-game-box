package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.util.formatOdd

enum class MatchResultType(val label: String, val color: Color, val score: String, val opponent: String) {
  WIN("G", Color(0xFF16A34A), "3 - 1", "vs Beşiktaş"),
  WIN_2("G", Color(0xFF16A34A), "2 - 0", "vs Trabzonspor"),
  DRAW("B", Color(0xFF64748B), "1 - 1", "vs Başakşehir"),
  WIN_3("G", Color(0xFF16A34A), "4 - 2", "vs Kasımpaşa"),
  LOSS("M", Color(0xFFDC2626), "1 - 2", "vs Fenerbahçe")
}

data class TeamAnalysisData(
  val teamName: String,
  val league: String,
  val sport: Sport,
  val rank: Int,
  val points: Int,
  val last5Form: List<MatchResultType>,
  val winRatePercent: Int,
  val drawRatePercent: Int,
  val lossRatePercent: Int,
  val goalsScoredAvg: Double,
  val goalsConcededAvg: Double,
  val expectedGoalsXg: Double,
  val bttsRatePercent: Int,
  val over25RatePercent: Int,
  val homeWinRate: Int,
  val awayWinRate: Int,
  val nextMatchOpponent: String,
  val nextMatchTime: String,
  val nextMatchOdd: Double,
  val aiInsight: String
)

/**
 * Kullanıcının tercih ettiği liglere ve spor branşlarına özel
 * 'Favori Takım Analizi' modülü ve dashboard bileşeni.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTeamAnalyticsDialog(
  matches: List<Match> = emptyList(),
  onSelectOdd: (SlipSelection) -> Unit = {},
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val teamsData = remember {
    listOf(
      TeamAnalysisData(
        teamName = "Galatasaray",
        league = "Trendyol Süper Lig",
        sport = Sport.FOOTBALL,
        rank = 1,
        points = 15,
        last5Form = listOf(MatchResultType.WIN, MatchResultType.WIN_2, MatchResultType.WIN_3, MatchResultType.DRAW, MatchResultType.WIN),
        winRatePercent = 80,
        drawRatePercent = 14,
        lossRatePercent = 6,
        goalsScoredAvg = 2.80,
        goalsConcededAvg = 0.80,
        expectedGoalsXg = 2.45,
        bttsRatePercent = 65,
        over25RatePercent = 80,
        homeWinRate = 88,
        awayWinRate = 72,
        nextMatchOpponent = "Fenerbahçe (Derbi)",
        nextMatchTime = "Pazar 20:00 TSİ",
        nextMatchOdd = 1.95,
        aiInsight = "Icardi ve Osimhen ortak hücum kurgusuyla takım ligin en yüksek xG (2.45) oranına sahip. İlk 30 dakikada öne geçme sıklığı %75."
      ),
      TeamAnalysisData(
        teamName = "Fenerbahçe",
        league = "Trendyol Süper Lig",
        sport = Sport.FOOTBALL,
        rank = 2,
        points = 13,
        last5Form = listOf(MatchResultType.WIN, MatchResultType.WIN_3, MatchResultType.LOSS, MatchResultType.WIN, MatchResultType.DRAW),
        winRatePercent = 75,
        drawRatePercent = 15,
        lossRatePercent = 10,
        goalsScoredAvg = 2.40,
        goalsConcededAvg = 0.90,
        expectedGoalsXg = 2.18,
        bttsRatePercent = 60,
        over25RatePercent = 75,
        homeWinRate = 82,
        awayWinRate = 68,
        nextMatchOpponent = "Galatasaray (Derbi)",
        nextMatchTime = "Pazar 20:00 TSİ",
        nextMatchOdd = 2.45,
        aiInsight = "Mourinho taktik disipliniyle kanat organizasyonlarında çok üretken. Tadic ve Dzeko'nun ceza sahası etkinliği zirvede."
      ),
      TeamAnalysisData(
        teamName = "Real Madrid",
        league = "İspanya La Liga",
        sport = Sport.FOOTBALL,
        rank = 1,
        points = 18,
        last5Form = listOf(MatchResultType.WIN, MatchResultType.WIN_2, MatchResultType.WIN, MatchResultType.WIN_3, MatchResultType.WIN),
        winRatePercent = 90,
        drawRatePercent = 10,
        lossRatePercent = 0,
        goalsScoredAvg = 3.10,
        goalsConcededAvg = 0.60,
        expectedGoalsXg = 2.70,
        bttsRatePercent = 50,
        over25RatePercent = 85,
        homeWinRate = 95,
        awayWinRate = 85,
        nextMatchOpponent = "Barcelona (El Clásico)",
        nextMatchTime = "Cumartesi 22:00 TSİ",
        nextMatchOdd = 1.82,
        aiInsight = "Mbappe ve Vinicius Jr. hızlı geçiş hücumlarında durdurulamaz bir tempo yakaladı. Bernabeu'da kalesinde maç başına 0.4 gol görüyor."
      ),
      TeamAnalysisData(
        teamName = "Manchester City",
        league = "İngiltere Premier League",
        sport = Sport.FOOTBALL,
        rank = 1,
        points = 16,
        last5Form = listOf(MatchResultType.WIN_3, MatchResultType.WIN, MatchResultType.DRAW, MatchResultType.WIN, MatchResultType.WIN_2),
        winRatePercent = 85,
        drawRatePercent = 15,
        lossRatePercent = 0,
        goalsScoredAvg = 2.90,
        goalsConcededAvg = 0.75,
        expectedGoalsXg = 2.62,
        bttsRatePercent = 55,
        over25RatePercent = 80,
        homeWinRate = 90,
        awayWinRate = 80,
        nextMatchOpponent = "Arsenal",
        nextMatchTime = "Pazar 18:30 TSİ",
        nextMatchOdd = 1.78,
        aiInsight = "Haaland ligde maç başına 1.4 gol ortalamasıyla oynuyor. Topa sahip olma oranı %68 ile Premier League lideri."
      ),
      TeamAnalysisData(
        teamName = "Anadolu Efes",
        league = "EuroLeague Basketbol",
        sport = Sport.BASKETBALL,
        rank = 4,
        points = 12,
        last5Form = listOf(MatchResultType.WIN, MatchResultType.WIN_3, MatchResultType.LOSS, MatchResultType.WIN, MatchResultType.WIN),
        winRatePercent = 70,
        drawRatePercent = 0,
        lossRatePercent = 30,
        goalsScoredAvg = 86.4,
        goalsConcededAvg = 79.2,
        expectedGoalsXg = 84.0,
        bttsRatePercent = 90,
        over25RatePercent = 85,
        homeWinRate = 80,
        awayWinRate = 60,
        nextMatchOpponent = "Panathinaikos",
        nextMatchTime = "Cuma 20:30 TSİ",
        nextMatchOdd = 1.88,
        aiInsight = "Larkin ve Thompson guard ikilisinin dış şut isabeti %42.5. Tempolu maçlarda 85+ baremi kolay aşılıyor."
      ),
      TeamAnalysisData(
        teamName = "VakıfBank",
        league = "Sultanlar Voleybol Ligi",
        sport = Sport.VOLLEYBALL,
        rank = 1,
        points = 15,
        last5Form = listOf(MatchResultType.WIN, MatchResultType.WIN, MatchResultType.WIN_2, MatchResultType.WIN, MatchResultType.WIN_3),
        winRatePercent = 95,
        drawRatePercent = 0,
        lossRatePercent = 5,
        goalsScoredAvg = 3.0,
        goalsConcededAvg = 0.6,
        expectedGoalsXg = 3.0,
        bttsRatePercent = 40,
        over25RatePercent = 30,
        homeWinRate = 100,
        awayWinRate = 90,
        nextMatchOpponent = "Eczacıbaşı Dynavit",
        nextMatchTime = "Cumartesi 19:00 TSİ",
        nextMatchOdd = 1.65,
        aiInsight = "Zehra Güneş ve blok savunması set başına 3.8 blok ortalaması yakaladı. 3-0 ve 3-1 bitme ihtimali %80."
      )
    )
  }

  var selectedSportFilter by remember { mutableStateOf<Sport?>(null) }
  var selectedTeamIndex by remember { mutableStateOf(0) }

  val filteredTeams = remember(selectedSportFilter) {
    if (selectedSportFilter == null) teamsData else teamsData.filter { it.sport == selectedSportFilter }
  }

  val activeTeam = filteredTeams.getOrNull(selectedTeamIndex) ?: filteredTeams.first()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFFF8FAFC),
    modifier = Modifier.testTag("favorite_team_analytics_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 6.dp)
        .padding(bottom = 28.dp)
    ) {
      // 1. Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(Color(0xFFE6FFFA)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.QueryStats, contentDescription = null, tint = TealDark, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Favori Takım Analizi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(color = GoldYellow, shape = RoundedCornerShape(4.dp)) {
                Text(
                  text = "PRO AI",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF0F172A),
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "Son 5 maç formu, gol ortalamaları ve galibiyet oranları",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF64748B))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Spor Branşı Filtre Butonları
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(
          Pair(null, "Tümü 🏆"),
          Pair(Sport.FOOTBALL, "Futbol ⚽"),
          Pair(Sport.BASKETBALL, "Basketbol 🏀"),
          Pair(Sport.VOLLEYBALL, "Voleybol 🏐")
        ).forEach { (sport, title) ->
          val isSel = selectedSportFilter == sport
          Surface(
            color = if (isSel) TealDark else Color.White,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, if (isSel) TealDark else Color(0xFFE2E8F0)),
            modifier = Modifier
              .weight(1f)
              .clickable {
                selectedSportFilter = sport
                selectedTeamIndex = 0
              }
          ) {
            Text(
              text = title,
              color = if (isSel) Color.White else Color(0xFF475569),
              fontSize = 10.sp,
              fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Takım Seçici (Horizontal Pill Row)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        filteredTeams.forEachIndexed { index, team ->
          val isSel = activeTeam.teamName == team.teamName
          Surface(
            color = if (isSel) Color(0xFF0F172A) else Color.White,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, if (isSel) GoldYellow else Color(0xFFE2E8F0)),
            modifier = Modifier.clickable { selectedTeamIndex = index }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (team.sport == Sport.BASKETBALL) "🏀" else if (team.sport == Sport.VOLLEYBALL) "🏐" else "⚽",
                fontSize = 12.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = team.teamName,
                color = if (isSel) GoldYellow else Color(0xFF1E293B),
                fontWeight = if (isSel) FontWeight.Black else FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. AKTİF TAKIM ANALİZ KARTI (DASHBOARD)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Takım Başlığı & Lig Sırası
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = activeTeam.teamName,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "${activeTeam.league} • ${activeTeam.rank}. Sıra (${activeTeam.points} Puan)",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }

            Surface(
              color = Color(0xFFDCFCE7),
              shape = RoundedCornerShape(20.dp)
            ) {
              Text(
                text = "%${activeTeam.winRatePercent} Galibiyet",
                color = Color(0xFF15803D),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4.1 SON 5 MAÇLIK FORM DURUMU
          Text(
            text = "SON 5 MAÇLIK FORM GRAFİĞİ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF475569)
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            activeTeam.last5Form.forEach { result ->
              Surface(
                color = result.color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, result.color.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 6.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(result.color),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = result.label,
                      color = Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                  }
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(
                    text = result.score,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                  )
                  Text(
                    text = result.opponent,
                    fontSize = 8.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4.2 GOL & SAYI ORTALAMALARI (METRICS GRID)
          Text(
            text = "GOL / SAYI ORTALAMALARI & xG İSTATİSTİKLERİ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF475569)
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MetricStatBox(
              title = "Atılan Gol Ort.",
              value = "${activeTeam.goalsScoredAvg}",
              subtitle = "Maç Başına",
              color = Color(0xFF0D9488),
              modifier = Modifier.weight(1f)
            )
            MetricStatBox(
              title = "Yenen Gol Ort.",
              value = "${activeTeam.goalsConcededAvg}",
              subtitle = "Maç Başına",
              color = Color(0xFFEA580C),
              modifier = Modifier.weight(1f)
            )
            MetricStatBox(
              title = "Gol Beklentisi",
              value = "${activeTeam.expectedGoalsXg}",
              subtitle = "xG İndeksi",
              color = Color(0xFF6366F1),
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Oran Dağılım Barları: Galibiyet, KG Var, 2.5 Üst
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("KG Var Oranı", fontSize = 10.sp, color = Color(0xFF64748B))
                Text("%${activeTeam.bttsRatePercent}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
              }
              Spacer(modifier = Modifier.height(3.dp))
              LinearProgressIndicator(
                progress = { activeTeam.bttsRatePercent / 100f },
                color = Color(0xFF0D9488),
                trackColor = Color(0xFFE2E8F0),
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("2.5 Üst Oranı", fontSize = 10.sp, color = Color(0xFF64748B))
                Text("%${activeTeam.over25RatePercent}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
              }
              Spacer(modifier = Modifier.height(3.dp))
              LinearProgressIndicator(
                progress = { activeTeam.over25RatePercent / 100f },
                color = GoldYellow,
                trackColor = Color(0xFFE2E8F0),
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 4.3 İÇ SAHA VS DEPLASMAN BAŞARISI
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🏟️", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("İç Saha Galibiyeti:", fontSize = 10.sp, color = Color(0xFF64748B))
                Text("%${activeTeam.homeWinRate}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF059669))
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "✈️", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text("Deplasman Galibiyeti:", fontSize = 10.sp, color = Color(0xFF64748B))
                Text("%${activeTeam.awayWinRate}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF2563EB))
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 4.4 GEMINI AI TAKTİK ANALİZİ
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Gemini Yapay Zeka Taktik Raporu",
                  color = GoldYellow,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = activeTeam.aiInsight,
                color = Color(0xFFCBD5E1),
                fontSize = 10.sp,
                lineHeight = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 4.5 GELECEK MAÇ & ORAN BUTONU
          Surface(
            color = Color(0xFFFEF3C7),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Sıradaki Maç: ${activeTeam.nextMatchOpponent}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = Color(0xFF92400E)
                )
                Text(
                  text = activeTeam.nextMatchTime,
                  fontSize = 10.sp,
                  color = Color(0xFFB45309)
                )
              }

              Button(
                onClick = {
                  val sel = SlipSelection(
                    matchId = "team_quick_${activeTeam.teamName.lowercase()}",
                    matchTeams = "${activeTeam.teamName} - ${activeTeam.nextMatchOpponent}",
                    marketType = MarketType.MATCH_RESULT,
                    selectionId = "sel_1",
                    selectionName = "MS 1",
                    odd = activeTeam.nextMatchOdd,
                    isLive = false
                  )
                  onSelectOdd(sel)
                  onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Kupona Ekle (${activeTeam.nextMatchOdd.formatOdd()})", fontSize = 10.sp, fontWeight = FontWeight.Black)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MetricStatBox(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFFF8FAFC),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = title, fontSize = 9.sp, color = Color(0xFF64748B), maxLines = 1)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
      Text(text = subtitle, fontSize = 8.sp, color = Color(0xFF94A3B8))
    }
  }
}
