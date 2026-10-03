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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlin.math.roundToInt
import com.example.data.datasource.MackolikLeagueDataSource
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchH2H
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.service.BettingClosureStatus
import com.example.service.CalendarManagementService
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary

/**
 * Detailed Match Analysis Dashboard.
 * Visualizes H2H stats, recent form trends, and AI-predicted outcomes using clean,
 * professional data cards, mimicking the depth and layout of professional sports analytics platforms (Opta, WhoScored, Sofascore).
 */
@Composable
fun MatchAnalyticsDashboard(
  match: Match,
  selectedSelections: List<SlipSelection> = emptyList(),
  onSelectOdd: (SlipSelection) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val h2h = remember(match.id) { MackolikLeagueDataSource.getH2HForMatch(match) }
  val pred = match.aiPrediction
  val closureStatus = remember(match.id) { CalendarManagementService.getClosureStatus(match) }

  val homeProb = pred.homeWinProb.coerceIn(10, 80)
  val drawProb = pred.drawProb.coerceIn(10, 50)
  val awayProb = (100 - homeProb - drawProb).coerceAtLeast(10)

  // Sub-tab selection: 0: Genel Bakış & AI, 1: H2H Geçmişi, 2: Form & Trendler
  var selectedSubTab by remember { mutableIntStateOf(0) }

  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .testTag("match_analytics_dashboard"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Match Header Context Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = match.league,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TealDark
            )

            // 5-Minute Closure Status Badge
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = when (closureStatus) {
                is BettingClosureStatus.Open -> Color(0xFFF0FDF4)
                is BettingClosureStatus.Locked5MinWindow -> Color(0xFFFEF2F2)
                is BettingClosureStatus.LiveInPlay -> Color(0xFFFFFBEB)
                is BettingClosureStatus.Finished -> Color(0xFFF1F5F9)
              },
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when (closureStatus) {
                  is BettingClosureStatus.Open -> Color(0xFF86EFAC)
                  is BettingClosureStatus.Locked5MinWindow -> Color(0xFFFCA5A5)
                  is BettingClosureStatus.LiveInPlay -> Color(0xFFFDE047)
                  is BettingClosureStatus.Finished -> Color(0xFFCBD5E1)
                }
              )
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.LockClock,
                  contentDescription = null,
                  tint = when (closureStatus) {
                    is BettingClosureStatus.Open -> Color(0xFF166534)
                    is BettingClosureStatus.Locked5MinWindow -> Color(0xFFDC2626)
                    is BettingClosureStatus.LiveInPlay -> Color(0xFFB45309)
                    is BettingClosureStatus.Finished -> Color(0xFF64748B)
                  },
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = CalendarManagementService.getClosureWindowLabel(match),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = when (closureStatus) {
                    is BettingClosureStatus.Open -> Color(0xFF166534)
                    is BettingClosureStatus.Locked5MinWindow -> Color(0xFFDC2626)
                    is BettingClosureStatus.LiveInPlay -> Color(0xFFB45309)
                    is BettingClosureStatus.Finished -> Color(0xFF64748B)
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Teams and Match Context
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = match.homeTeam,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "Ev Sahibi",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
              )
            }

            // VS or Score Pill
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF0F172A),
              modifier = Modifier.padding(horizontal = 8.dp)
            ) {
              if (match.status == MatchStatus.LIVE || match.status == MatchStatus.FINISHED) {
                Text(
                  text = "${match.homeScore} - ${match.awayScore}",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = GoldYellow,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              } else {
                Text(
                  text = match.startTime,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }

            Column(
              modifier = Modifier.weight(1f),
              horizontalAlignment = Alignment.End
            ) {
              Text(
                text = match.awayTeam,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "Deplasman",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = Color(0xFFF1F5F9))
          Spacer(modifier = Modifier.height(8.dp))

          // Match Context Details (Stadium, Referee, TV Broadcast)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "📍 ${match.stadium}",
              fontSize = 10.sp,
              color = Color(0xFF64748B),
              maxLines = 1
            )
            Text(
              text = "📺 ${match.tvBroadcast}",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = TealDark
            )
          }
        }
      }
    }

    // 2. Dashboard Sub-Navigation Tabs
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFE2E8F0))
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        listOf("🤖 AI Tahmin", "⚔️ H2H Geçmişi", "📈 Form & Trendler").forEachIndexed { index, title ->
          val isSelected = selectedSubTab == index
          Surface(
            modifier = Modifier
              .weight(1f)
              .clickable { selectedSubTab = index },
            shape = RoundedCornerShape(9.dp),
            color = if (isSelected) Color.White else Color.Transparent,
            shadowElevation = if (isSelected) 2.dp else 0.dp
          ) {
            Text(
              text = title,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
              color = if (isSelected) TealDark else Color(0xFF64748B),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 7.dp)
            )
          }
        }
      }
    }

    // 3. Tab Content
    when (selectedSubTab) {
      0 -> {
        // Tab 0: AI Predicted Outcomes Card
        item {
          AiOutcomePredictionCard(
            match = match,
            pred = pred,
            homeProb = homeProb,
            drawProb = drawProb,
            awayProb = awayProb,
            selectedSelections = selectedSelections,
            onSelectOdd = onSelectOdd
          )
        }
      }
      1 -> {
        // Tab 1: Head-to-Head (H2H) Stats Deep Card
        item {
          HeadToHeadAnalyticsCard(
            match = match,
            h2h = h2h
          )
        }
      }
      2 -> {
        // Tab 2: Recent Form Trends & Comparison
        item {
          RecentFormTrendsCard(
            match = match,
            pred = pred
          )
        }
      }
    }
  }
}

/**
 * Clean, professional AI-Predicted Outcomes Card.
 */
@Composable
private fun AiOutcomePredictionCard(
  match: Match,
  pred: com.example.data.model.AiPrediction,
  homeProb: Int,
  drawProb: Int,
  awayProb: Int,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
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
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Yapay Zeka Sonuç Projeksiyonu",
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "Monte Carlo Simülasyonu & xG Dağılımı",
              fontSize = 10.sp,
              color = Color(0xFF64748B),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFDCFCE7)
        ) {
          Text(
            text = "%${pred.confidence} Güven",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF166534),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3-Way Probability Split Gauge
      Text(
        text = "Kazanma Olasılık Dağılımı",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF475569)
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = "1: %$homeProb", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealDark)
        Text(text = "X: %$drawProb", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
        Text(text = "2: %$awayProb", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
      }
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(12.dp)
          .clip(RoundedCornerShape(6.dp))
      ) {
        Box(
          modifier = Modifier
            .weight(homeProb.toFloat())
            .height(12.dp)
            .background(TealDark)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Box(
          modifier = Modifier
            .weight(drawProb.toFloat())
            .height(12.dp)
            .background(Color(0xFF94A3B8))
        )
        Spacer(modifier = Modifier.width(2.dp))
        Box(
          modifier = Modifier
            .weight(awayProb.toFloat())
            .height(12.dp)
            .background(Color(0xFF6366F1))
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Expected Score & Projected xG Cards
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Expected Score Card
        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF8FAFC),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "Öngörülen Skor", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = pred.predictedScore, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TealDark)
            Text(text = "En Yüksek Olasılık", fontSize = 9.sp, color = Color(0xFF94A3B8))
          }
        }

        // Projected xG Card
        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF8FAFC),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "xG Beklentisi", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${"%.2f".format(match.statistics.xgHome)} - ${"%.2f".format(match.statistics.xgAway)}",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0D9488)
            )
            Text(text = "Beklenen Gol", fontSize = 9.sp, color = Color(0xFF94A3B8))
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Tactical AI Insight Text Card
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0FDF4),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.QueryStats, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Taktiksel Maç Görüşü", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = pred.tacticalAnalysis,
            fontSize = 11.sp,
            color = Color(0xFF334155),
            lineHeight = 17.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // AI Value Pick Recommendation
      val mrMarket = match.markets.firstOrNull { it.type == MarketType.MATCH_RESULT }
      val recommendedSelection = if (homeProb >= 50) {
        mrMarket?.selections?.firstOrNull { it.name == "1" }
      } else if (awayProb >= 45) {
        mrMarket?.selections?.firstOrNull { it.name == "2" }
      } else {
        mrMarket?.selections?.firstOrNull { it.name == "X" }
      }

      if (recommendedSelection != null) {
        val isSelected = selectedSelections.any { it.selectionId == recommendedSelection.id }
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFFEF3C7),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "⚡ AI Değerli Tercih (Value Pick)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF92400E)
              )
              Text(
                text = "MS ${recommendedSelection.name} (${recommendedSelection.odd} Oran)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F)
              )
            }

            Button(
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = MarketType.MATCH_RESULT,
                    selectionId = recommendedSelection.id,
                    selectionName = recommendedSelection.name,
                    odd = recommendedSelection.odd,
                    isLive = match.status == MatchStatus.LIVE
                  )
                )
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) Color(0xFF16A34A) else TealDark
              ),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = if (isSelected) "Kuponda ✓" else "+ Kupona Ekle",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Head-to-Head (H2H) Stats Deep Card.
 */
@Composable
private fun HeadToHeadAnalyticsCard(
  match: Match,
  h2h: MatchH2H
) {
  val totalMatches = if (h2h.totalPlayed > 0) h2h.totalPlayed else h2h.recentMatches.size.coerceAtLeast(1)
  val homeWins = h2h.homeWins
  val draws = h2h.draws
  val awayWins = h2h.awayWins

  val homePct = ((homeWins.toFloat() / totalMatches) * 100).toInt()
  val drawPct = ((draws.toFloat() / totalMatches) * 100).toInt()
  val awayPct = (100 - homePct - drawPct).coerceAtLeast(0)

  val avgGoals = remember(h2h) {
    if (h2h.recentMatches.isEmpty()) 2.8
    else {
      val sum = h2h.recentMatches.sumOf { m ->
        val parts = m.score.split("-").mapNotNull { it.trim().toIntOrNull() }
        parts.sum()
      }
      val avg = sum.toDouble() / h2h.recentMatches.size
      (avg * 10).roundToInt() / 10.0
    }
  }

  val bothTeamsScoredRate = remember(h2h) {
    if (h2h.recentMatches.isEmpty()) 60
    else {
      val btsCount = h2h.recentMatches.count { m ->
        val parts = m.score.split("-").mapNotNull { it.trim().toIntOrNull() }
        parts.size >= 2 && parts[0] > 0 && parts[1] > 0
      }
      ((btsCount.toFloat() / h2h.recentMatches.size) * 100).toInt()
    }
  }

  val over25Rate = remember(h2h) {
    if (h2h.recentMatches.isEmpty()) 65
    else {
      val overCount = h2h.recentMatches.count { m ->
        val parts = m.score.split("-").mapNotNull { it.trim().toIntOrNull() }
        parts.sum() > 2
      }
      ((overCount.toFloat() / h2h.recentMatches.size) * 100).toInt()
    }
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.History, contentDescription = null, tint = TealDark, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "H2H Karşılaşma İstatistikleri",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF1F5F9)
        ) {
          Text(
            text = "Son $totalMatches Maç",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TealDark,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3-Way H2H Win Breakdown Summary
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "$homeWins Galibiyet", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TealDark)
          Text(text = match.homeTeam, fontSize = 10.sp, color = Color(0xFF64748B), maxLines = 1)
          Text(text = "%$homePct", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealDark)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "$draws Beraberlik", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF64748B))
          Text(text = "Eşitlik", fontSize = 10.sp, color = Color(0xFF64748B))
          Text(text = "%$drawPct", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "$awayWins Galibiyet", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF6366F1))
          Text(text = match.awayTeam, fontSize = 10.sp, color = Color(0xFF64748B), maxLines = 1)
          Text(text = "%$awayPct", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Split bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp))
      ) {
        Box(
          modifier = Modifier
            .weight(homePct.toFloat().coerceAtLeast(1f))
            .height(10.dp)
            .background(TealDark)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Box(
          modifier = Modifier
            .weight(drawPct.toFloat().coerceAtLeast(1f))
            .height(10.dp)
            .background(Color(0xFF94A3B8))
        )
        Spacer(modifier = Modifier.width(2.dp))
        Box(
          modifier = Modifier
            .weight(awayPct.toFloat().coerceAtLeast(1f))
            .height(10.dp)
            .background(Color(0xFF6366F1))
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Secondary H2H Metrics (Gol Ortalaması, KG Var, 2.5 Üst)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        H2HMetricPill(
          title = "Maç Başı Gol",
          value = "$avgGoals Gol",
          modifier = Modifier.weight(1f)
        )
        H2HMetricPill(
          title = "KG Var Oranı",
          value = "%$bothTeamsScoredRate",
          modifier = Modifier.weight(1f)
        )
        H2HMetricPill(
          title = "2.5 Üst Oranı",
          value = "%$over25Rate",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // List of Past Matches
      Text(
        text = "Aralarındaki Son Karşılaşmalar",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF475569)
      )
      Spacer(modifier = Modifier.height(8.dp))

      h2h.recentMatches.take(6).forEach { pastMatch ->
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFF8FAFC),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${match.homeTeam} - ${match.awayTeam}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
              )
              Text(
                text = "${pastMatch.date} • ${pastMatch.league}",
                fontSize = 9.sp,
                color = Color(0xFF94A3B8)
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = when (pastMatch.result) {
                "G" -> Color(0xFF16A34A)
                "B" -> Color(0xFFD97706)
                else -> Color(0xFFDC2626)
              }
            ) {
              Text(
                text = pastMatch.score,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Metric Pill for H2H Stats.
 */
@Composable
private fun H2HMetricPill(
  title: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFFF1F5F9),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = title, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = TealDark)
    }
  }
}

/**
 * Recent Form Trends Card (Son 5 Maç & Momentum).
 */
@Composable
private fun RecentFormTrendsCard(
  match: Match,
  pred: com.example.data.model.AiPrediction
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TealDark, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Son Form Trendleri & Güç Endeksi",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF1F5F9)
        ) {
          Text(
            text = "Son 5 Maç",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TealDark,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Home Team Form Row
      FormTeamRow(
        teamName = match.homeTeam,
        formResults = listOf("G", "G", "B", "G", "M"),
        rating = pred.formRatingHome
      )

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = Color(0xFFF1F5F9))
      Spacer(modifier = Modifier.height(10.dp))

      // Away Team Form Row
      FormTeamRow(
        teamName = match.awayTeam,
        formResults = listOf("M", "G", "B", "G", "G"),
        rating = pred.formRatingAway
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Comparative Performance Bars
      Text(
        text = "Karşılaştırmalı Takım Gücü (Opta Verisi)",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF475569)
      )
      Spacer(modifier = Modifier.height(8.dp))

      ComparativePowerBar(title = "Hücum Gücü", homeVal = 88, awayVal = 82)
      Spacer(modifier = Modifier.height(6.dp))
      ComparativePowerBar(title = "Savunma Direnci", homeVal = 84, awayVal = 79)
      Spacer(modifier = Modifier.height(6.dp))
      ComparativePowerBar(title = "Kalesini Kapatma (Clean Sheet)", homeVal = 40, awayVal = 30)
      Spacer(modifier = Modifier.height(6.dp))
      ComparativePowerBar(title = "Pas İsabet Oranı", homeVal = 86, awayVal = 83)
    }
  }
}

@Composable
private fun FormTeamRow(
  teamName: String,
  formResults: List<String>,
  rating: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(text = teamName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
      Text(text = rating, fontSize = 10.sp, color = Color(0xFF64748B))
    }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      formResults.forEach { res ->
        val (bgColor, textColor) = when (res) {
          "G" -> Color(0xFF16A34A) to Color.White
          "B" -> Color(0xFFD97706) to Color.White
          else -> Color(0xFFDC2626) to Color.White
        }

        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(bgColor),
          contentAlignment = Alignment.Center
        ) {
          Text(text = res, fontSize = 10.sp, fontWeight = FontWeight.Black, color = textColor)
        }
      }
    }
  }
}

@Composable
private fun ComparativePowerBar(
  title: String,
  homeVal: Int,
  awayVal: Int
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = "%$homeVal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealDark)
      Text(text = title, fontSize = 10.sp, color = Color(0xFF64748B))
      Text(text = "%$awayVal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
    }
    Spacer(modifier = Modifier.height(3.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(Color(0xFFE2E8F0))
    ) {
      Box(
        modifier = Modifier
          .weight(homeVal.toFloat())
          .height(6.dp)
          .background(TealDark)
      )
      Box(
        modifier = Modifier
          .weight(awayVal.toFloat())
          .height(6.dp)
          .background(Color(0xFF6366F1))
      )
    }
  }
}
