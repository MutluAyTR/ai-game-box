package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.SlipSelection
import com.example.engine.AutomatedFormPrediction
import com.example.engine.GeminiAnalysisService
import com.example.engine.PreMatchAiInsight
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.formatOdd
import kotlinx.coroutines.launch

@Composable
fun PreMatchAiInsightCard(
  match: Match,
  onAddRecommendationToSlip: (String, Double) -> Unit = { _, _ -> },
  modifier: Modifier = Modifier
) {
  var insight by remember { mutableStateOf<PreMatchAiInsight?>(null) }
  var formPrediction by remember { mutableStateOf<AutomatedFormPrediction?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  fun loadInsights() {
    coroutineScope.launch {
      isLoading = true
      insight = GeminiAnalysisService.generatePreMatchAiInsight(match)
      formPrediction = GeminiAnalysisService.generateAutomatedFormPrediction(match)
      isLoading = false
    }
  }

  LaunchedEffect(match.id) {
    loadInsights()
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("pre_match_ai_insight_card"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Gemini AI Branding & Refresh Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  listOf(Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFC084FC))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Gemini AI",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "GEMINI PRE-MATCH AI INSIGHT",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = TealDark,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.width(4.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFFEDE9FE))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "3.5 Flash",
                  color = Color(0xFF6D28D9),
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Text(
              text = "Varsayımsal Maç & Form Verisi Analizi",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        IconButton(
          onClick = { loadInsights() },
          modifier = Modifier.size(28.dp)
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = TealDark,
              strokeWidth = 2.dp
            )
          } else {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Yenile",
              tint = Color(0xFF64748B),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (isLoading && insight == null) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          CircularProgressIndicator(
            color = TealDark,
            strokeWidth = 2.dp,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Gemini yapay zeka modeli maç verilerini analiz ediyor...",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }
      } else {
        val currentInsight = insight
        if (currentInsight != null) {
          // Pre-Match Summary Box
          Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "📋 Maç Önü Taktiksel Özeti",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF1E293B)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentInsight.summaryText,
                fontSize = 11.sp,
                color = Color(0xFF334155),
                lineHeight = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Algorithmic Recommendation Pill & Odd
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5))
                )
              )
              .border(1.dp, Color(0xFF6EE7B7), RoundedCornerShape(10.dp))
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Bolt,
                  contentDescription = null,
                  tint = Color(0xFF059669),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Önerilen Seçim:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = Color(0xFF065F46)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = currentInsight.recommendedPick,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp,
                  color = Color(0xFF047857)
                )
              }

              Text(
                text = "Beklenen Skor: ${currentInsight.expectedScore} | Oran: ${currentInsight.recommendedOdd.formatOdd()}",
                fontSize = 10.sp,
                color = Color(0xFF047857)
              )
            }

            Button(
              onClick = {
                onAddRecommendationToSlip(
                  currentInsight.recommendedPick,
                  currentInsight.recommendedOdd
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .height(30.dp)
                .testTag("add_ai_recommendation_btn"),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
            ) {
              Text(
                text = "+ Kupona Ekle",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Tactical Risk Warning
          Surface(
            color = Color(0xFFFFFBEB),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Risk Uyarısı",
                tint = Color(0xFFD97706),
                modifier = Modifier
                  .size(14.dp)
                  .padding(top = 1.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = "Taktiksel Risk Faktörü:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  color = Color(0xFFB45309)
                )
                Text(
                  text = currentInsight.tacticalRiskAlert,
                  fontSize = 10.sp,
                  color = Color(0xFF92400E),
                  lineHeight = 14.sp
                )
              }
            }
          }

          // Automated Form Prediction Breakdown
          val currentForm = formPrediction
          if (currentForm != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "📊 Takım Form Değerlendirmesi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF334155)
                  )
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color(0xFF0D9488))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = currentForm.valueRating,
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    )
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "• ${currentForm.homeFormAnalysis}",
                  fontSize = 10.sp,
                  color = Color(0xFF475569)
                )
                Text(
                  text = "• ${currentForm.awayFormAnalysis}",
                  fontSize = 10.sp,
                  color = Color(0xFF475569)
                )
                Text(
                  text = "• H2H: ${currentForm.h2hSummary}",
                  fontSize = 10.sp,
                  color = Color(0xFF475569)
                )
              }
            }
          }
        }
      }
    }
  }
}
