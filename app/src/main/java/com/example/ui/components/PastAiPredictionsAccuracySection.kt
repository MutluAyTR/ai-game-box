package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HistoricalAiPrediction
import com.example.data.repository.AiPredictionHistoryRepository
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

/**
 * UI section in the dashboard to display past AI betting predictions and their actual outcomes
 * to allow users to track the accuracy of the Gemini model's advice.
 */
@Composable
fun PastAiPredictionsAccuracySection(
  modifier: Modifier = Modifier
) {
  val predictions by AiPredictionHistoryRepository.predictionsFlow.collectAsState(initial = emptyList())
  var selectedFilter by remember { mutableStateOf("Tümü") }
  var isExpanded by remember { mutableStateOf(true) }

  val totalCount = predictions.size
  val wonCount = remember(predictions) { predictions.count { it.isWon } }
  val winRate = if (totalCount > 0) (wonCount.toDouble() / totalCount * 100).toInt() else 0
  val totalReturnTp = remember(predictions) { predictions.sumOf { it.returnTp } }
  val totalStakeTp = remember(predictions) { predictions.sumOf { it.stakeTp } }
  val netProfitTp = totalReturnTp - totalStakeTp
  val averageOdds = remember(predictions) {
    if (predictions.isNotEmpty()) {
      (predictions.map { it.predictedOdds }.average() * 100).toInt() / 100.0
    } else 0.0
  }

  val filteredPredictions = remember(predictions, selectedFilter) {
    when (selectedFilter) {
      "Tutanlar ✅" -> predictions.filter { it.isWon }
      "Yatanlar ❌" -> predictions.filter { !it.isWon }
      "Yüksek Oran (2.00+)" -> predictions.filter { it.predictedOdds >= 2.0 }
      else -> predictions
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("past_ai_predictions_accuracy_section"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    border = BorderStroke(1.dp, Color(0xFF1E293B)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { isExpanded = !isExpanded }
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xFF0F3A3D)),
            contentAlignment = Alignment.Center
          ) {
            Text("🤖", fontSize = 18.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "GEMINI AI TAHMİN DOĞRULUK TAKİBİ",
                color = GoldYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(color = Color(0xFF065F46), shape = RoundedCornerShape(4.dp)) {
                Text(
                  text = "%$winRate İSABET",
                  color = Color(0xFFA7F3D0),
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "Geçmiş Yapay Zeka Tavsiyeleri & Gerçek Maç Sonuçları",
              color = Color(0xFF94A3B8),
              fontSize = 10.sp
            )
          }
        }

        IconButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Daralt" else "Genişlet",
            tint = Color(0xFF94A3B8)
          )
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column {
          Spacer(modifier = Modifier.height(12.dp))

          // 1. Accuracy Summary Stats Card
          Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("Başarı Oranı", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  Text(
                    text = "%$winRate ($wonCount / $totalCount)",
                    color = Color(0xFF34D399),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                  )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Ortalama Oran", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  Text(
                    text = "${String.format("%.2f", averageOdds)}",
                    color = GoldYellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text("Sanal Net Getiri", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  val sign = if (netProfitTp >= 0) "+" else ""
                  Text(
                    text = "$sign$netProfitTp TP",
                    color = if (netProfitTp >= 0) Color(0xFF34D399) else LiveRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Accuracy Progress Bar
              LinearProgressIndicator(
                progress = { winRate / 100f },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(CircleShape),
                color = Color(0xFF10B981),
                trackColor = Color(0xFF334155)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 2. Filter Chips
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
          ) {
            items(listOf("Tümü", "Tutanlar ✅", "Yatanlar ❌", "Yüksek Oran (2.00+)")) { filter ->
              val isSelected = selectedFilter == filter
              FilterChip(
                selected = isSelected,
                onClick = { selectedFilter = filter },
                label = { Text(filter, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = TealDark,
                  selectedLabelColor = Color.White,
                  containerColor = Color(0xFF1E293B),
                  labelColor = Color(0xFFCBD5E1)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 3. Historical Prediction Cards
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredPredictions.forEach { item ->
              HistoricalPredictionItemCard(item = item)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun HistoricalPredictionItemCard(item: HistoricalAiPrediction) {
  var showDetails by remember { mutableStateOf(false) }

  Surface(
    color = Color(0xFF1E293B),
    shape = RoundedCornerShape(10.dp),
    border = BorderStroke(
      1.dp,
      if (item.isWon) Color(0xFF059669) else Color(0xFFDC2626).copy(alpha = 0.6f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { showDetails = !showDetails }
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Header: League & Date & Win/Loss Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(item.sport.iconRes, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${item.league} • ${item.matchDate}",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Outcome Badge
        Surface(
          color = if (item.isWon) Color(0xFF065F46) else Color(0xFF7F1D1D),
          shape = RoundedCornerShape(4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (item.isWon) "TUTTU ✅ (+${item.returnTp} TP)" else "YATTI ❌ (-${item.stakeTp} TP)",
              color = if (item.isWon) Color(0xFFA7F3D0) else Color(0xFFFECACA),
              fontSize = 9.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Match Teams & Actual Score
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.matchTitle,
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 13.sp,
          modifier = Modifier.weight(1f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "MS: ${item.actualOutcomeScore}",
            color = GoldYellow,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Gemini Advised Bet & Confidence
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFF0F3A3D),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "🤖 Gemini Önerisi:",
              color = Color(0xFF38BDF8),
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = item.predictedTip,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Oran: ${String.format("%.2f", item.predictedOdds)}",
            color = GoldYellow,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Black
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = Color(0xFF334155),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "%${item.confidence} Güven",
              color = Color(0xFFCBD5E1),
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }
      }

      // Expandable Reasoning Rationale
      if (showDetails) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(
              text = "🧠 Model Analiz Özeti (${item.geminiModel}):",
              color = Color(0xFF94A3B8),
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = item.analysisSummary,
              color = Color(0xFFE2E8F0),
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
        }
      }
    }
  }
}
