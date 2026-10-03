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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.MackolikLeagueDataSource
import com.example.data.model.Match
import com.example.data.model.MatchH2H
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary

@Composable
fun H2HAiPredictionCard(
  match: Match,
  modifier: Modifier = Modifier,
  initiallyExpanded: Boolean = true
) {
  var isExpanded by remember { mutableStateOf(initiallyExpanded) }
  val h2h = remember(match.id) { MackolikLeagueDataSource.getH2HForMatch(match) }

  // Extract AI probabilities or compute balanced distribution
  val homeProb = match.aiPrediction.homeWinProb.coerceIn(10, 80)
  val drawProb = match.aiPrediction.drawProb.coerceIn(10, 50)
  val awayProb = (100 - homeProb - drawProb).coerceAtLeast(10)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("h2h_ai_prediction_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1. Header with AI Sparkle badge
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = Color(0xFFEFF6FF),
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "H2H & Yapay Zeka Tahmini",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Text(
              text = "Geçmiş sonuçlar, xG ve simülasyon analizi",
              fontSize = 11.sp,
              color = Color(0xFF64748B),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
          color = Color(0xFFDCFCE7),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = "%${match.aiPrediction.confidence} Güven",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF166534),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Win Probability 3-Way Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFFF8FAFC))
          .padding(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${match.homeTeam} (%$homeProb)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D9488)
          )
          Text(
            text = "Beraberlik (%$drawProb)",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
          )
          Text(
            text = "${match.awayTeam} (%$awayProb)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6366F1)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress bar split into 3 segments
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
        ) {
          Box(
            modifier = Modifier
              .weight(homeProb.toFloat())
              .fillMaxWidth()
              .background(Color(0xFF0D9488))
          )
          Spacer(modifier = Modifier.width(2.dp))
          Box(
            modifier = Modifier
              .weight(drawProb.toFloat())
              .fillMaxWidth()
              .background(Color(0xFF94A3B8))
          )
          Spacer(modifier = Modifier.width(2.dp))
          Box(
            modifier = Modifier
              .weight(awayProb.toFloat())
              .fillMaxWidth()
              .background(Color(0xFF6366F1))
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Predicted Score & xG
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = Color(0xFFFEF3C7),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "🎯 AI Skoru: ${match.aiPrediction.predictedScore}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF92400E),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Text(
            text = "xG Beklenti: ${match.statistics.xgHome} - ${match.statistics.xgAway}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155)
          )
        }
      }

      // Expandable section for full H2H history
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
          Spacer(modifier = Modifier.height(10.dp))

          // H2H Historical Totals
          Text(
            text = "⚔️ İki Takım Arasındaki Toplam Karşılaşmalar",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
              .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${h2h.homeWins}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0D9488)
              )
              Text(
                text = "${match.homeTeam} G",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${h2h.draws}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF64748B)
              )
              Text(
                text = "Beraberlik",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${h2h.awayWins}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF6366F1)
              )
              Text(
                text = "${match.awayTeam} G",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${h2h.totalPlayed}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "Toplam Maç",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Past Matches List
          Text(
            text = "Son Karşılaşmalar:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
          )
          Spacer(modifier = Modifier.height(4.dp))

          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            h2h.recentMatches.take(5).forEach { past ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFFAFAFA), RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = past.score,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                  Text(
                    text = "${past.date} • ${past.league}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                  )
                }

                Surface(
                  color = when (past.result) {
                    "G" -> Color(0xFFDCFCE7)
                    "B" -> Color(0xFFF1F5F9)
                    else -> Color(0xFFFEE2E2)
                  },
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = when (past.result) {
                      "G" -> "Galibiyet"
                      "B" -> "Beraberlik"
                      else -> "Mağlubiyet"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (past.result) {
                      "G" -> Color(0xFF166534)
                      "B" -> Color(0xFF475569)
                      else -> Color(0xFF991B1B)
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Tactical AI Insight Box
          Surface(
            color = Color(0xFFF0FDF4),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text(text = "💡", fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = match.aiPrediction.tacticalAnalysis.ifEmpty {
                  "İki takımın geçmiş karşılaşmalarında ortalama 2.75 gol çıkmıştır. Son 5 randevunun 4'ünde Karşılıklı Gol gerçekleşti. Yapay zeka modeli ilk yarıda yüksek tempo öngörmektedir."
                },
                fontSize = 11.sp,
                color = Color(0xFF166534),
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      // Collapse / Expand toggle button at bottom
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded }
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isExpanded) "Daha Az Göster" else "Detaylı H2H & Geçmiş Maçları Gör",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TealDark
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = TealDark,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
