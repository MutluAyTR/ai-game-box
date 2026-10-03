package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

/**
 * Reusable AI Insight Component
 * Displays real-time prediction probabilities calculated dynamically from match flow,
 * momentum gauge, next-goal radar, and live tactical AI commentary.
 */
@Composable
fun AiMatchFlowInsightComponent(
  match: Match,
  modifier: Modifier = Modifier,
  selectedSelections: List<SlipSelection> = emptyList(),
  onSelectOdd: (SlipSelection) -> Unit = {}
) {
  // 1. Dynamic Match Flow Probabilities Calculation
  val (liveHomeProb, liveDrawProb, liveAwayProb) = remember(
    match.homeScore,
    match.awayScore,
    match.minute,
    match.statistics.possessionHome,
    match.statistics.redCardsHome,
    match.statistics.redCardsAway,
    match.statistics.xgHome,
    match.statistics.xgAway
  ) {
    calculateLiveFlowProbabilities(match)
  }

  // Next goal probabilities based on live flow
  val (nextGoalHome, nextGoalAway, noMoreGoals) = remember(
    match.homeScore,
    match.awayScore,
    match.minute,
    match.statistics.xgHome,
    match.statistics.xgAway
  ) {
    calculateNextGoalProbabilities(match)
  }

  // Momentum Index (0 to 100, >50 Home Dominance, <50 Away Dominance)
  val homeMomentum = remember(match.statistics) {
    val shotWeight = match.statistics.shotsHome * 4 - match.statistics.shotsAway * 4
    val cornerWeight = match.statistics.cornersHome * 3 - match.statistics.cornersAway * 3
    val attackWeight = (match.statistics.dangerousAttacksHome - match.statistics.dangerousAttacksAway)
    val raw = 50 + (shotWeight + cornerWeight + attackWeight) / 2
    raw.coerceIn(15, 85)
  }

  // Animated bars
  val animatedHomeProb by animateFloatAsState(
    targetValue = liveHomeProb / 100f,
    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    label = "homeProbAnim"
  )
  val animatedDrawProb by animateFloatAsState(
    targetValue = liveDrawProb / 100f,
    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    label = "drawProbAnim"
  )
  val animatedAwayProb by animateFloatAsState(
    targetValue = liveAwayProb / 100f,
    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    label = "awayProbAnim"
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Premium Navy
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: AI Live Flow Engine Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7)))),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "AI CANLI MAÇ AKIŞI",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = Color(0xFF22C55E).copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF22C55E))
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "CANLI",
                    color = Color(0xFF4ADE80),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }
            }
            Text(
              text = "Dakika ${match.minute}' verileri ve xG akışı",
              fontSize = 10.sp,
              color = Color(0xFF94A3B8),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Confidence Indicator (Horizontal, no vertical squishing)
        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(0.5.dp, Color(0xFF334155))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "%${match.aiPrediction.confidence} Güven",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = GoldYellow,
              maxLines = 1,
              softWrap = false
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 1. Live Match Win Probabilities (Ev Sahibi % / Beraberlik % / Deplasman %)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
          .padding(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CANLI MAÇ SONUCU OLASILIĞI",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFCBD5E1)
          )
          Text(
            text = "${match.homeScore} - ${match.awayScore} (${match.minute}')",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Three-way Multi-segmented Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFF334155))
        ) {
          if (animatedHomeProb > 0.02f) {
            Box(
              modifier = Modifier
                .weight(animatedHomeProb)
                .fillMaxHeight()
                .background(Color(0xFF3B82F6)) // Home Blue
            )
          }
          if (animatedDrawProb > 0.02f) {
            Box(
              modifier = Modifier
                .weight(animatedDrawProb)
                .fillMaxHeight()
                .background(Color(0xFF94A3B8)) // Draw Slate
            )
          }
          if (animatedAwayProb > 0.02f) {
            Box(
              modifier = Modifier
                .weight(animatedAwayProb)
                .fillMaxHeight()
                .background(Color(0xFFEF4444)) // Away Red
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Percentage Labels
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Home
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF3B82F6)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${match.homeTeam}: %$liveHomeProb",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          // Draw
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF94A3B8)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Beraberlik: %$liveDrawProb",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFCBD5E1)
            )
          }

          // Away
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${match.awayTeam}: %$liveAwayProb",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Next Goal Radar & Live Pressure Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Next Goal Radar
        Surface(
          modifier = Modifier.weight(1.1f),
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "SIRADAKİ GOL İHTİMALİ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFE2E8F0)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Ev Sahibi:", fontSize = 10.sp, color = Color(0xFF94A3B8))
              Text(text = "%$nextGoalHome", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF60A5FA))
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Deplasman:", fontSize = 10.sp, color = Color(0xFF94A3B8))
              Text(text = "%$nextGoalAway", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFF87171))
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Gol Olmaz:", fontSize = 10.sp, color = Color(0xFF94A3B8))
              Text(text = "%$noMoreGoals", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))
            }
          }
        }

        // Live Pressure / Momentum Index
        Surface(
          modifier = Modifier.weight(1f),
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "BASKI & MOMENTUM",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFE2E8F0)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            // Pressure Bar
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF334155))
            ) {
              Box(
                modifier = Modifier
                  .weight(homeMomentum.toFloat())
                  .fillMaxHeight()
                  .background(Color(0xFF38BDF8))
              )
              Box(
                modifier = Modifier
                  .weight((100 - homeMomentum).toFloat())
                  .fillMaxHeight()
                  .background(Color(0xFFF43F5E))
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "%$homeMomentum", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
              Text(
                text = if (homeMomentum >= 50) "Ev Baskılı" else "Dep Baskılı",
                fontSize = 9.sp,
                color = Color(0xFF94A3B8)
              )
              Text(text = "%${100 - homeMomentum}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF43F5E))
            }
            Text(
              text = "xG: %.2f - %.2f".format(match.statistics.xgHome, match.statistics.xgAway),
              fontSize = 10.sp,
              color = Color(0xFFCBD5E1),
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Dynamic Tactical Insight Commentary
      val tacticalCommentary = remember(match.minute, match.homeScore, match.awayScore, match.statistics) {
        generateTacticalInsight(match, liveHomeProb, liveDrawProb, liveAwayProb)
      }

      Surface(
        color = Color(0xFF1E293B).copy(alpha = 0.7f),
        border = BorderStroke(0.5.dp, Color(0xFF334155)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.Top
        ) {
          Text(text = "🧠", fontSize = 15.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = tacticalCommentary,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = Color(0xFFE2E8F0)
          )
        }
      }

      // 4. One-tap AI Pick button if match has active markets
      val recommendedSelection = remember(match, liveHomeProb, liveAwayProb) {
        findRecommendedLiveSelection(match, liveHomeProb, liveAwayProb)
      }

      if (recommendedSelection != null) {
        Spacer(modifier = Modifier.height(10.dp))
        val isAlreadySelected = selectedSelections.any {
          it.matchId == match.id && it.selectionId == recommendedSelection.selectionId
        }

        Surface(
          color = if (isAlreadySelected) Color(0xFF047857) else Color(0xFFF59E0B),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectOdd(recommendedSelection) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                imageVector = if (isAlreadySelected) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                tint = if (isAlreadySelected) Color.White else Color(0xFF0F172A),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "AI Canlı Değer Bahsi: ${recommendedSelection.selectionName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isAlreadySelected) Color.White else Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Text(
              text = "%.2f Oran".format(recommendedSelection.odd),
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = if (isAlreadySelected) Color.White else Color(0xFF0F172A)
            )
          }
        }
      }
    }
  }
}

/**
 * Calculates live match win/draw/away probabilities considering minute, score, and xG
 */
private fun calculateLiveFlowProbabilities(match: Match): Triple<Int, Int, Int> {
  val baseHome = match.aiPrediction.homeWinProb
  val baseDraw = match.aiPrediction.drawProb
  val baseAway = match.aiPrediction.awayWinProb

  val diff = match.homeScore - match.awayScore
  val min = match.minute.coerceIn(1, 95)
  val remainingFactor = (90 - min).coerceAtLeast(0) / 90.0

  return when {
    diff > 0 -> {
      // Home leading
      val boost = ((1.0 - remainingFactor) * 45).toInt() + (diff - 1) * 20
      val h = (baseHome + boost).coerceIn(40, 97)
      val d = ((100 - h) * 0.65).toInt().coerceAtLeast(2)
      val a = (100 - h - d).coerceAtLeast(1)
      Triple(h, d, a)
    }
    diff < 0 -> {
      // Away leading
      val boost = ((1.0 - remainingFactor) * 45).toInt() + (-diff - 1) * 20
      val a = (baseAway + boost).coerceIn(40, 97)
      val d = ((100 - a) * 0.65).toInt().coerceAtLeast(2)
      val h = (100 - a - d).coerceAtLeast(1)
      Triple(h, d, a)
    }
    else -> {
      // Tied
      val drawSurge = if (min > 70) ((min - 70) * 1.5).toInt() else 0
      val d = (baseDraw + drawSurge).coerceIn(25, 75)
      val remain = 100 - d
      val hWeight = match.statistics.possessionHome / 100.0
      val h = (remain * hWeight).toInt().coerceAtLeast(10)
      val a = (remain - h).coerceAtLeast(10)
      Triple(h, d, a)
    }
  }
}

/**
 * Calculates dynamic probability for the next goal event
 */
private fun calculateNextGoalProbabilities(match: Match): Triple<Int, Int, Int> {
  val min = match.minute.coerceIn(1, 95)
  val remainingMin = (90 - min).coerceAtLeast(0)

  val noGoalProb = if (remainingMin < 15) {
    (55 + (15 - remainingMin) * 2.5).toInt().coerceIn(30, 85)
  } else {
    (18 + (90 - remainingMin) * 0.2).toInt().coerceIn(15, 45)
  }

  val activeProb = 100 - noGoalProb
  val homeRatio = (match.statistics.possessionHome * 0.5 + match.statistics.shotsHome * 2.5)
  val awayRatio = (match.statistics.possessionAway * 0.5 + match.statistics.shotsAway * 2.5)
  val totalRatio = (homeRatio + awayRatio).coerceAtLeast(1.0)

  val homeNext = (activeProb * (homeRatio / totalRatio)).toInt().coerceIn(5, 75)
  val awayNext = (activeProb - homeNext).coerceAtLeast(5)

  return Triple(homeNext, awayNext, noGoalProb)
}

/**
 * Generates situation-specific real-time AI tactical commentary
 */
private fun generateTacticalInsight(
  match: Match,
  homeProb: Int,
  drawProb: Int,
  awayProb: Int
): String {
  val diff = match.homeScore - match.awayScore
  val min = match.minute

  return when {
    diff > 0 && min >= 75 -> {
      "${match.homeTeam} skor üstünlüğünü korumak için savunma bloklarını sıklaştırdı. Son 10 dakikada üretilen xG: %.2f. AI galibiyet beklentisi %$homeProb seviyesinde.".format(match.statistics.xgHome)
    }
    diff < 0 && min >= 75 -> {
      "${match.awayTeam} deplasmanda skoru koruyor. ${match.homeTeam} risk alarak stoperlerini ileri çıkardı; kontra atak tehdidi ve bir sonraki gol beklentisi yüksek.".format(match.statistics.xgAway)
    }
    diff == 0 && min >= 75 -> {
      "Dakika $min': Maçta beraberlik ihtimali %$drawProb'e tırmandı. Her iki takım da puan kaybetmemek adına temkinli pas trafiği yürütüyor."
    }
    match.statistics.shotsHome > match.statistics.shotsAway + 4 -> {
      "${match.homeTeam} hücum hattında üstün baskı kurdu (${match.statistics.shotsHome} şut, ${match.statistics.cornersHome} korner). AI modeli maç sonuna kadar en az 1 gol daha bekliyor."
    }
    else -> {
      "${match.homeTeam} topla oynama oranında %${match.statistics.possessionHome} ile oyunu kontrol ediyor. Beklenen gol (xG): %.2f - %.2f.".format(match.statistics.xgHome, match.statistics.xgAway)
    }
  }
}

/**
 * Finds recommended value bet in the match
 */
private fun findRecommendedLiveSelection(
  match: Match,
  homeProb: Int,
  awayProb: Int
): SlipSelection? {
  val mrMarket = match.markets.firstOrNull { it.type == MarketType.MATCH_RESULT } ?: return null
  val candidate = if (homeProb > awayProb && homeProb >= 50) {
    mrMarket.selections.firstOrNull { it.name == "1" }
  } else if (awayProb > homeProb && awayProb >= 45) {
    mrMarket.selections.firstOrNull { it.name == "2" }
  } else {
    mrMarket.selections.firstOrNull { it.name == "X" }
  }
  val bestSel = candidate ?: mrMarket.selections.firstOrNull() ?: return null

  return SlipSelection(
    matchId = match.id,
    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
    marketType = mrMarket.type,
    selectionId = bestSel.id,
    selectionName = bestSel.name,
    odd = bestSel.odd,
    isLive = match.status == MatchStatus.LIVE,
    matchStatus = match.status
  )
}
