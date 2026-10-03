package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.MackolikStandingsAndOddsDataSource
import com.example.data.model.LeagueStandingRow
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OddsGreen
import com.example.ui.theme.TealDark
import kotlin.math.max

/**
 * High-performance, D3 / Recharts-inspired Live Standings Visualization component.
 * Allows users to dynamically switch between all football & basketball leagues,
 * and explore interactive points distributions, diverging goal difference bars,
 * form momentum spline curves, and detailed probability metrics.
 */
@Composable
fun D3LiveStandingsVisualization(
  modifier: Modifier = Modifier,
  initialLeague: String = "Trendyol Süper Lig",
  onOpenMatchDetailByName: (String) -> Unit = {}
) {
  val supportedLeagues = listOf(
    Pair("Trendyol Süper Lig", "🇹🇷 Süper Lig"),
    Pair("Premier League", "🏴󠁧󠁢󠁥󠁮󠁧󠁿 Premier League"),
    Pair("La Liga", "🇪🇸 La Liga"),
    Pair("Serie A", "🇮🇹 Serie A"),
    Pair("Bundesliga", "🇩🇪 Bundesliga"),
    Pair("Ligue 1", "🇫🇷 Ligue 1"),
    Pair("UEFA Şampiyonlar Ligi", "⭐ Şampiyonlar Ligi"),
    Pair("UEFA Avrupa Ligi", "🏆 Avrupa Ligi"),
    Pair("UEFA Konferans Ligi", "🌍 Konferans Ligi"),
    Pair("Trendyol 1. Lig", "🇹🇷 TFF 1. Lig"),
    Pair("EuroLeague", "🏀 EuroLeague"),
    Pair("NBA", "🇺🇸 NBA"),
    Pair("BSL", "🇹🇷 BSL")
  )

  var selectedLeagueKey by remember { mutableStateOf(initialLeague) }
  var visualMode by remember { mutableIntStateOf(0) } // 0: D3 Bar & Dağılım, 1: Recharts Averaj, 2: D3 Form Spline, 3: Canlı Tablo

  val rawStandings = remember(selectedLeagueKey) {
    MackolikStandingsAndOddsDataSource.getStandingsForLeague(selectedLeagueKey)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("d3_live_standings_visualization"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // 1. Header with live status badge and D3 branding
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = "D3 Standings",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Canlı Puan Tablosu & D3 Grafikleri",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A),
              maxLines = 1,
              softWrap = false
            )
            Text(
              text = "D3 / Recharts dinamik lig analizi & form trendi",
              fontSize = 10.sp,
              color = Color(0xFF64748B),
              maxLines = 1,
              softWrap = false
            )
          }
        }

        Surface(
          color = Color(0xFFF0FDF4),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, Color(0xFFBBF7D0))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "CANLI D3",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF15803D),
              maxLines = 1,
              softWrap = false
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Dynamic League Switcher (Horizontal scrollable chip list)
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("d3_league_switcher_row")
      ) {
        items(supportedLeagues) { (key, display) ->
          val isSelected = (selectedLeagueKey == key || (key == "Trendyol Süper Lig" && selectedLeagueKey == "Süper Lig"))
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) TealDark else Color(0xFFF1F5F9),
            border = BorderStroke(1.dp, if (isSelected) TealDark else Color(0xFFE2E8F0)),
            modifier = Modifier.clickable { selectedLeagueKey = key }
          ) {
            Text(
              text = display,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else Color(0xFF334155),
              maxLines = 1,
              softWrap = false,
              modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Visualization Mode Switcher Tabs (D3 Bar, Recharts Averaj, Form Spline, Canlı Tablo)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
          .padding(3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val modes = listOf(
          Pair(0, "📊 Puan (D3)"),
          Pair(1, "⚖️ Averaj"),
          Pair(2, "📈 Form Trend"),
          Pair(3, "📋 Tablo")
        )
        modes.forEach { (index, title) ->
          val isSelected = visualMode == index
          Surface(
            color = if (isSelected) Color.White else Color.Transparent,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = if (isSelected) 1.5.dp else 0.dp,
            modifier = Modifier
              .weight(1f)
              .clickable { visualMode = index }
          ) {
            Box(
              modifier = Modifier.padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                color = if (isSelected) TealDark else Color(0xFF64748B),
                maxLines = 1,
                softWrap = false
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Active Visualization Body
      when (visualMode) {
        0 -> D3PointsBarChartView(standings = rawStandings)
        1 -> RechartsDivergingGoalDiffView(standings = rawStandings)
        2 -> D3FormMomentumSplineView(standings = rawStandings)
        3 -> DetailedLiveStandingsTableView(standings = rawStandings)
      }
    }
  }
}

/**
 * Mode 0: Recharts-style Animated Horizontal Points Bar Chart with qualification thresholds.
 */
@Composable
private fun D3PointsBarChartView(standings: List<LeagueStandingRow>) {
  val topTeams = remember(standings) { standings.take(8) }
  val maxPoints = remember(topTeams) { topTeams.maxOfOrNull { it.points } ?: 20 }
  var animateBars by remember { mutableStateOf(false) }

  LaunchedEffect(standings) {
    animateBars = true
  }

  val animFraction by animateFloatAsState(
    targetValue = if (animateBars) 1f else 0f,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "d3_bars_anim"
  )

  Column(modifier = Modifier.fillMaxWidth()) {
    // Metric legend
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Şampiyonlar Ligi", fontSize = 9.5.sp, color = Color(0xFF64748B), maxLines = 1, softWrap = false)
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF3B82F6)))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Avrupa Ligi", fontSize = 9.5.sp, color = Color(0xFF64748B), maxLines = 1, softWrap = false)
      }
      Text(
        text = "Maksimum: $maxPoints Puan",
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        maxLines = 1,
        softWrap = false
      )
    }

    // Horizontal Bars List (Side-by-side layout, zero vertical text wrapping)
    topTeams.forEachIndexed { index, row ->
      val progress = if (maxPoints > 0) (row.points.toFloat() / maxPoints.toFloat()) * animFraction else 0f
      val barColor = when (index) {
        0 -> Color(0xFF10B981)
        1, 2 -> Color(0xFF059669)
        3, 4 -> Color(0xFF3B82F6)
        else -> Color(0xFF64748B)
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 3.5.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Rank Badge
        Surface(
          color = Color(row.zoneColor),
          shape = CircleShape,
          modifier = Modifier.size(18.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "${row.rank}",
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.width(7.dp))

        // Team Name (Max width allocated, horizontal, no wrapping)
        Text(
          text = row.teamName,
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1E293B),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          softWrap = false,
          modifier = Modifier.width(96.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Bar container
        Box(
          modifier = Modifier
            .weight(1f)
            .height(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF1F5F9))
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .fillMaxWidth(progress.coerceIn(0.04f, 1f))
              .clip(RoundedCornerShape(4.dp))
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    barColor.copy(alpha = 0.8f),
                    barColor
                  )
                )
              )
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Points Value text
        Text(
          text = "${row.points} P",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A),
          maxLines = 1,
          softWrap = false,
          modifier = Modifier.width(36.dp),
          textAlign = TextAlign.End
        )
      }
    }
  }
}

/**
 * Mode 1: Recharts Diverging Bar Chart (+ / - Goal Difference & Attack/Defense Efficiency).
 */
@Composable
private fun RechartsDivergingGoalDiffView(standings: List<LeagueStandingRow>) {
  val teams = remember(standings) { standings.take(8) }
  val maxAbsDiff = remember(teams) {
    teams.maxOfOrNull { kotlin.math.abs(it.goalDifference) }?.coerceAtLeast(6) ?: 10
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Header explanation
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("🔴 Eksi Averaj (-)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), maxLines = 1, softWrap = false)
      Text("Merkez 0", fontSize = 9.sp, color = Color(0xFF94A3B8), maxLines = 1, softWrap = false)
      Text("🟢 Artı Averaj (+)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), maxLines = 1, softWrap = false)
    }

    teams.forEach { row ->
      val diff = row.goalDifference
      val isPositive = diff >= 0
      val ratio = (kotlin.math.abs(diff).toFloat() / maxAbsDiff.toFloat()).coerceIn(0.04f, 1f)

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Team Name
        Text(
          text = row.teamName,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF1E293B),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          softWrap = false,
          modifier = Modifier.width(90.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Diverging Dual Bar Layout
        Row(
          modifier = Modifier
            .weight(1f)
            .height(14.dp)
            .background(Color(0xFFF8FAFC), RoundedCornerShape(3.dp)),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Negative Side (Left)
          Box(
            modifier = Modifier
              .weight(1f)
              .height(14.dp),
            contentAlignment = Alignment.CenterEnd
          ) {
            if (!isPositive) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(ratio)
                  .height(10.dp)
                  .clip(RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp))
                  .background(Color(0xFFEF4444))
              )
            }
          }

          // Center Divider
          Box(
            modifier = Modifier
              .width(2.dp)
              .height(14.dp)
              .background(Color(0xFFCBD5E1))
          )

          // Positive Side (Right)
          Box(
            modifier = Modifier
              .weight(1f)
              .height(14.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            if (isPositive) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(ratio)
                  .height(10.dp)
                  .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                  .background(Color(0xFF10B981))
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Diff text
        val prefix = if (diff > 0) "+$diff" else "$diff"
        Text(
          text = prefix,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = if (isPositive) Color(0xFF15803D) else Color(0xFFDC2626),
          modifier = Modifier.width(34.dp),
          textAlign = TextAlign.End,
          maxLines = 1,
          softWrap = false
        )
      }
    }
  }
}

/**
 * Mode 2: D3-inspired Multi-Spline Momentum Curve (Rolling points over last 5 matches).
 */
@Composable
private fun D3FormMomentumSplineView(standings: List<LeagueStandingRow>) {
  val topFour = remember(standings) { standings.take(4) }
  var animProgress by remember { mutableStateOf(false) }

  LaunchedEffect(standings) {
    animProgress = true
  }

  val progress by animateFloatAsState(
    targetValue = if (animProgress) 1f else 0f,
    animationSpec = tween(1000, easing = FastOutSlowInEasing),
    label = "d3_spline_anim"
  )

  Column(modifier = Modifier.fillMaxWidth()) {
    // Legend Row (Top 4 teams with colors)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val colors = listOf(Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFFF59E0B), Color(0xFF8B5CF6))
      topFour.forEachIndexed { i, t ->
        val c = colors.getOrElse(i) { Color.Gray }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(c))
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = t.teamName.take(9),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569),
            maxLines = 1,
            softWrap = false
          )
        }
      }
    }

    // D3 Canvas Curve
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(130.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF0F172A))
    ) {
      Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height

        // Draw horizontal grid lines (0, 3, 6, 9, 12, 15 pts)
        val gridLines = 4
        for (g in 0..gridLines) {
          val y = h * (1f - (g.toFloat() / gridLines))
          drawLine(
            color = Color(0xFF334155).copy(alpha = 0.5f),
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 1f
          )
        }

        val colors = listOf(Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFFF59E0B), Color(0xFF8B5CF6))

        // Plot each team's cumulative rolling points
        topFour.forEachIndexed { idx, team ->
          val color = colors.getOrElse(idx) { Color.White }
          var cumulative = 0
          val points = mutableListOf<Float>()
          points.add(0f)
          team.form.takeLast(5).forEach { f ->
            cumulative += when (f) {
              "G" -> 3
              "B" -> 1
              else -> 0
            }
            points.add(cumulative.toFloat())
          }

          val maxPossible = 15f
          val stepX = w / (points.size - 1)

          val path = Path()
          points.forEachIndexed { pIdx, pt ->
            val px = pIdx * stepX
            val py = h * (1f - ((pt / maxPossible) * progress).coerceIn(0f, 1f))
            if (pIdx == 0) {
              path.moveTo(px, py)
            } else {
              val prevX = (pIdx - 1) * stepX
              val prevY = h * (1f - ((points[pIdx - 1] / maxPossible) * progress).coerceIn(0f, 1f))
              val cp1X = prevX + (px - prevX) / 2f
              val cp2X = cp1X
              path.cubicTo(cp1X, prevY, cp2X, py, px, py)
            }
          }

          drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
          )

          // Glowing end point dot
          val lastX = (points.size - 1) * stepX
          val lastY = h * (1f - ((points.last() / maxPossible) * progress).coerceIn(0f, 1f))
          drawCircle(color = color, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
          drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(lastX, lastY))
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text("Maç -5", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
      Text("Maç -4", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
      Text("Maç -3", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
      Text("Maç -2", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
      Text("Son Maç", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
    }
  }
}

/**
 * Mode 3: Detailed Live Standings Table with Expandable AI Predictions.
 * Designed with guaranteed horizontal alignment to avoid vertical text wrap.
 */
@Composable
private fun DetailedLiveStandingsTableView(standings: List<LeagueStandingRow>) {
  var expandedTeamName by remember { mutableStateOf<String?>(null) }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Table Header (Strict single line, no wrapping)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
        .padding(vertical = 6.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("#", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(18.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("TAKIM", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f), maxLines = 1, softWrap = false)
      Text("O", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("G", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("B", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("M", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("AV", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("P", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(26.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("FORM", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(50.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
    }

    Spacer(modifier = Modifier.height(3.dp))

    // Table Rows
    standings.take(10).forEach { row ->
      val isExpanded = expandedTeamName == row.teamName

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { expandedTeamName = if (isExpanded) null else row.teamName }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Rank
          Surface(
            color = Color(row.zoneColor),
            shape = CircleShape,
            modifier = Modifier.size(16.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "${row.rank}",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Team Name (Horizontal, single line)
          Text(
            text = row.teamName,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            modifier = Modifier.weight(1f)
          )

          Text("${row.played}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
          Text("${row.won}", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
          Text("${row.drawn}", fontSize = 11.sp, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
          Text("${row.lost}", fontSize = 11.sp, color = Color(0xFFDC2626), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)

          val diffStr = if (row.goalDifference > 0) "+${row.goalDifference}" else "${row.goalDifference}"
          Text(diffStr, fontSize = 10.sp, color = Color(0xFF475569), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)

          Text("${row.points}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(26.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)

          // Form pills (Strictly horizontal)
          Row(
            modifier = Modifier.width(50.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            row.form.takeLast(4).forEach { f ->
              val (bg, txt) = when (f) {
                "G" -> Pair(Color(0xFF10B981), "G")
                "B" -> Pair(Color(0xFF94A3B8), "B")
                else -> Pair(Color(0xFFEF4444), "M")
              }
              Box(
                modifier = Modifier
                  .padding(horizontal = 1.dp)
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(bg),
                contentAlignment = Alignment.Center
              ) {
                Text(txt, fontSize = 6.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        // Expandable Detail with AI Projections
        AnimatedVisibility(visible = isExpanded) {
          Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 4.dp, vertical = 4.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "⚡ AI Sezon Projeksiyonu: ${row.teamName}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = TealDark,
                  maxLines = 1,
                  softWrap = false
                )
                Text(
                  text = "xPTS: ${(row.points * 1.08).toInt()}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF64748B),
                  maxLines = 1,
                  softWrap = false
                )
              }
              Spacer(modifier = Modifier.height(6.dp))

              // Probabilities
              val champProb = if (row.rank <= 2) (72 - (row.rank - 1) * 28) else if (row.rank <= 4) 8 else 1
              val euroProb = if (row.rank <= 4) (95 - (row.rank - 1) * 8) else if (row.rank <= 7) 45 else 10
              val relRisk = if (row.rank >= 16) (65 + (row.rank - 16) * 12) else 3

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Surface(
                  color = Color(0xFFDCFCE7),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(6.dp)) {
                    Text("Şampiyonluk", fontSize = 8.5.sp, color = Color(0xFF166534), maxLines = 1, softWrap = false)
                    Text("%$champProb", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF15803D), maxLines = 1, softWrap = false)
                  }
                }

                Surface(
                  color = Color(0xFFDBEAFE),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(6.dp)) {
                    Text("Avrupa Kotası", fontSize = 8.5.sp, color = Color(0xFF1E40AF), maxLines = 1, softWrap = false)
                    Text("%$euroProb", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF1D4ED8), maxLines = 1, softWrap = false)
                  }
                }

                Surface(
                  color = Color(0xFFFEE2E2),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(6.dp)) {
                    Text("Küme Düşme", fontSize = 8.5.sp, color = Color(0xFF991B1B), maxLines = 1, softWrap = false)
                    Text("%$relRisk", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFDC2626), maxLines = 1, softWrap = false)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Top-level Dialog wrapper for D3 Live Standings Visualization.
 */
@Composable
fun D3LiveStandingsDialog(
  initialLeague: String = "Trendyol Süper Lig",
  onDismiss: () -> Unit
) {
  androidx.compose.ui.window.Dialog(
    onDismissRequest = onDismiss,
    properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .padding(vertical = 20.dp),
      shape = RoundedCornerShape(16.dp),
      color = Color.White
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📊", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "D3 Canlı Puan Tablosu",
              fontWeight = FontWeight.Black,
              fontSize = 14.sp,
              color = Color(0xFF0F172A),
              maxLines = 1,
              softWrap = false
            )
          }
          androidx.compose.material3.IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            androidx.compose.material3.Icon(
              androidx.compose.material.icons.Icons.Default.Close,
              contentDescription = "Kapat",
              tint = Color(0xFF64748B)
            )
          }
        }
        D3LiveStandingsVisualization(
          initialLeague = initialLeague
        )
      }
    }
  }
}

