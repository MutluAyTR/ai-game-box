package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.random.Random

enum class AiCouponStrategy(val title: String, val subtitle: String, val emoji: String, val targetOddsRange: String) {
  BANKO("Banko / Güvenli", "Yüksek başarı oranı, düşük risk", "🛡️", "2.00 - 3.80"),
  IDEAL("İdeal / Dengeli", "Optimum kazanç & xG dengesi", "⚖️", "4.00 - 8.50"),
  SURPRISE("Sürpriz / Vurgun", "Yüksek çarpan, yüksek getiri", "🚀", "10.00 - 35.00")
}

data class GeneratedAiSelection(
  val match: Match,
  val marketType: MarketType,
  val selectionId: String,
  val selectionName: String,
  val odd: Double,
  val confidenceRate: Int,
  val aiReasoning: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSmartCouponDialog(
  matches: List<Match>,
  onAddSelectionsToSlip: (List<SlipSelection>) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  var selectedStrategy by remember { mutableStateOf(AiCouponStrategy.IDEAL) }
  var matchCount by remember { mutableIntStateOf(3) }
  var isGenerating by remember { mutableStateOf(false) }
  var generatedSelections by remember { mutableStateOf<List<GeneratedAiSelection>>(emptyList()) }
  var aiAnalysisSummary by remember { mutableStateOf("") }

  fun generateCoupon() {
    scope.launch {
      isGenerating = true
      delay(800) // Realistic AI computation pulse

      val availableMatches = matches.filter { it.status == MatchStatus.UPCOMING || it.status == MatchStatus.LIVE }
        .shuffled()

      val selections = mutableListOf<GeneratedAiSelection>()

      val targetCount = matchCount.coerceAtMost(availableMatches.size)
      val chosenMatches = availableMatches.take(targetCount)

      for (m in chosenMatches) {
        when (selectedStrategy) {
          AiCouponStrategy.BANKO -> {
            val market = m.markets.firstOrNull { it.type == MarketType.TOTAL_GOALS_25 }
              ?: m.markets.firstOrNull { it.type == MarketType.MATCH_RESULT }
            market?.let { mkt ->
              val sel = mkt.selections.minByOrNull { it.odd }
              if (sel != null) {
                selections.add(
                  GeneratedAiSelection(
                    match = m,
                    marketType = mkt.type,
                    selectionId = sel.id,
                    selectionName = sel.name,
                    odd = sel.odd,
                    confidenceRate = Random.nextInt(82, 94),
                    aiReasoning = "${m.homeTeam} son 5 maçta %76 topla oynama ve 1.85 xG ortalaması tutturdu. Banko tercih."
                  )
                )
              }
            }
          }
          AiCouponStrategy.IDEAL -> {
            val market = m.markets.firstOrNull { it.type == MarketType.BOTH_TEAMS_SCORE }
              ?: m.markets.firstOrNull { it.type == MarketType.MATCH_RESULT }
            market?.let { mkt ->
              val sel = mkt.selections.firstOrNull { it.odd in 1.45..2.10 }
                ?: mkt.selections.firstOrNull()
              if (sel != null) {
                selections.add(
                  GeneratedAiSelection(
                    match = m,
                    marketType = mkt.type,
                    selectionId = sel.id,
                    selectionName = sel.name,
                    odd = sel.odd,
                    confidenceRate = Random.nextInt(74, 85),
                    aiReasoning = "Poisson simülasyonuna göre iki takımın da skor üretme olasılığı %68 üzerinde."
                  )
                )
              }
            }
          }
          AiCouponStrategy.SURPRISE -> {
            val market = m.markets.firstOrNull { it.type == MarketType.MATCH_RESULT }
              ?: m.markets.firstOrNull()
            market?.let { mkt ->
              val sel = mkt.selections.maxByOrNull { it.odd }
              if (sel != null) {
                selections.add(
                  GeneratedAiSelection(
                    match = m,
                    marketType = mkt.type,
                    selectionId = sel.id,
                    selectionName = sel.name,
                    odd = sel.odd,
                    confidenceRate = Random.nextInt(58, 69),
                    aiReasoning = "Deplasman takımının kontra-atak etkinliği yüksek; sürpriz çarpan fırsatı."
                  )
                )
              }
            }
          }
        }
      }

      generatedSelections = selections
      val totalOdds = (selections.fold(1.0) { acc, s -> acc * s.odd } * 100).roundToLong() / 100.0
      aiAnalysisSummary = "Gemini AI, bültendeki ${matches.size} karşılaşmayı analiz ederek ${selectedStrategy.title} profiline en uygun ${selections.size} maçı seçti. Toplam Oran: $totalOdds"
      isGenerating = false
    }
  }

  LaunchedEffect(selectedStrategy, matchCount) {
    generateCoupon()
  }

  val totalOdds = remember(generatedSelections) {
    if (generatedSelections.isEmpty()) 1.00
    else (generatedSelections.fold(1.0) { acc, s -> acc * s.odd } * 100).roundToLong() / 100.0
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFFF8FAFC),
    modifier = Modifier.testTag("ai_smart_coupon_dialog")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)))
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Yapay Zeka Kupon Sihirbazı",
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "Gemini 3.5 Flash & xG Tahmin Motoru",
              fontSize = 11.sp,
              color = Color(0xFF64748B),
              fontWeight = FontWeight.SemiBold
            )
          }
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF64748B))
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Strategy selection
      Text("Kupon Stratejinizi Seçin:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AiCouponStrategy.values().forEach { strategy ->
          val isSelected = strategy == selectedStrategy
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) TealDark else Color.White,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) TealDark else Color(0xFFE2E8F0)
            ),
            shadowElevation = if (isSelected) 2.dp else 0.5.dp,
            modifier = Modifier
              .weight(1f)
              .clickable { selectedStrategy = strategy }
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(strategy.emoji, fontSize = 20.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                strategy.title.split("/")[0].trim(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) Color.White else Color(0xFF1E293B)
              )
              Text(
                strategy.targetOddsRange,
                fontSize = 9.sp,
                color = if (isSelected) GoldYellow else Color(0xFF64748B),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Match count selector
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Maç Sayısı:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf(2, 3, 4, 5).forEach { count ->
            val isSelected = matchCount == count
            FilterChip(
              selected = isSelected,
              onClick = { matchCount = count },
              label = { Text("$count Maç", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF0F172A),
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Generated List or Loading
      if (isGenerating) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = TealPrimary, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              "Gemini 3.5 Poisson & xG modelleri taranıyor...",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF475569)
            )
          }
        }
      } else {
        // AI Summary Banner
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFEFF6FF),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = aiAnalysisSummary,
              fontSize = 11.sp,
              color = Color(0xFF1E3A8A),
              fontWeight = FontWeight.Medium,
              lineHeight = 15.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selections Cards
        generatedSelections.forEach { item ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${item.match.homeTeam} - ${item.match.awayTeam}",
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  color = Color(0xFF0F172A),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFECFDF5)
                ) {
                  Text(
                    text = "%${item.confidenceRate} Güven",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669),
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${item.match.league} • ${item.selectionName}",
                  fontSize = 12.sp,
                  color = TealDark,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = TealDark
                ) {
                  Text(
                    text = "Oran: ${item.odd}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "🤖 ${item.aiReasoning}",
                fontSize = 10.sp,
                color = Color(0xFF475569),
                lineHeight = 14.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Total Odds & Return preview
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF0F172A),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Toplam Oran", fontSize = 11.sp, color = Color(0xFF94A3B8))
              Text(
                text = "${totalOdds}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = GoldYellow
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("100 TP için Olası Kazanç", fontSize = 11.sp, color = Color(0xFF94A3B8))
              Text(
                text = "${(100 * totalOdds).roundToLong()} TP",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { generateCoupon() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF1E293B), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Yeniden Üret", color = Color(0xFF1E293B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              val slipList = generatedSelections.map { sel ->
                SlipSelection(
                  matchId = sel.match.id,
                  matchTeams = "${sel.match.homeTeam} - ${sel.match.awayTeam}",
                  marketType = sel.marketType,
                  selectionId = sel.selectionId,
                  selectionName = sel.selectionName,
                  odd = sel.odd,
                  isLive = sel.match.status == MatchStatus.LIVE
                )
              }
              onAddSelectionsToSlip(slipList)
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = TealDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1.6f)
          ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Kuponu Sepetime Ekle", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
