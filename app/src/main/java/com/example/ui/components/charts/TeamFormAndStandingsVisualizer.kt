package com.example.ui.components.charts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamFormPoint
import com.example.data.model.VisualStandingItem
import com.example.data.repository.AiPredictionHistoryRepository
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

/**
 * Recharts / D3 Equivalent Data Visualization Component in Jetpack Compose.
 * Renders:
 * 1. Smooth Cubic Bezier Line & Area Gradient Chart for recent match form trends.
 * 2. Visual League Standings Chart with interactive comparative bars.
 */
@Composable
fun TeamStandingsAndFormTrendsVisualizer(
  homeTeamName: String = "Galatasaray",
  awayTeamName: String = "Fenerbahçe",
  onDismiss: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Form Trendi (Area Chart), 1: Puan Durumu (Bar Chart)
  val homeFormData = remember(homeTeamName) { AiPredictionHistoryRepository.getTeamFormTrend(homeTeamName) }
  val awayFormData = remember(awayTeamName) { AiPredictionHistoryRepository.getTeamFormTrend(awayTeamName) }
  val standingsData = remember { AiPredictionHistoryRepository.getSuperLigStandings() }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("team_standings_and_form_visualizer"),
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0xFF0F3A3D)),
            contentAlignment = Alignment.Center
          ) {
            Text("📈", fontSize = 16.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "FORM & PUAN GRAFİKLERİ",
                color = GoldYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(color = TealDark, shape = RoundedCornerShape(4.dp)) {
                Text(
                  text = "RECHARTS ENGINE",
                  color = Color.White,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "$homeTeamName vs $awayTeamName • İstatistiksel Trend",
              color = Color(0xFF94A3B8),
              fontSize = 10.sp
            )
          }
        }

        if (onDismiss != null) {
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Navigation Tabs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFF1E293B),
        contentColor = GoldYellow,
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Form Eğrisi (Area Chart)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Puan Durumu (Bar Chart)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Tab Content
      when (selectedTab) {
        0 -> FormTrendAreaChartContent(
          homeTeamName = homeTeamName,
          awayTeamName = awayTeamName,
          homePoints = homeFormData,
          awayPoints = awayFormData
        )
        1 -> StandingsBarChartContent(standings = standingsData)
      }
    }
  }
}

/**
 * 1. Recharts / D3 Equivalent Area & Line Chart (Compose Canvas with Cubic Bezier Curves)
 */
@Composable
private fun FormTrendAreaChartContent(
  homeTeamName: String,
  awayTeamName: String,
  homePoints: List<TeamFormPoint>,
  awayPoints: List<TeamFormPoint>
) {
  var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
  var isHomeSelected by remember { mutableStateOf(true) }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Legend
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { isHomeSelected = true }
        ) {
          Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
          Spacer(modifier = Modifier.width(4.dp))
          Text(homeTeamName, color = if (isHomeSelected) Color(0xFF38BDF8) else Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { isHomeSelected = false }
        ) {
          Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
          Spacer(modifier = Modifier.width(4.dp))
          Text(awayTeamName, color = if (!isHomeSelected) Color(0xFFF59E0B) else Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Text("Son ${homePoints.size} Karşılaşma (0-3 Puan)", color = Color(0xFF64748B), fontSize = 9.5.sp)
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Recharts Area Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .background(Color(0xFF090D1A), RoundedCornerShape(10.dp))
        .padding(8.dp)
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(homePoints) {
            detectTapGestures { offset ->
              val totalWidth = size.width
              val step = totalWidth / (homePoints.size - 1).coerceAtLeast(1)
              val tappedIndex = ((offset.x + step / 2) / step).toInt().coerceIn(0, homePoints.size - 1)
              selectedPointIndex = tappedIndex
            }
          }
      ) {
        val width = size.width
        val height = size.height
        val maxPoints = 3f

        // Draw Cartesian Grid Lines (Recharts CartesianGrid)
        val gridLines = 4
        for (i in 0 until gridLines) {
          val y = height * (i.toFloat() / (gridLines - 1))
          drawLine(
            color = Color(0xFF1E293B),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
          )
        }

        // Draw Area & Line for Away Team (Amber Curve)
        drawBezierCurve(
          data = awayPoints.map { it.points.toFloat() },
          maxVal = maxPoints,
          lineColor = Color(0xFFF59E0B),
          fillGradient = Brush.verticalGradient(
            colors = listOf(Color(0x55F59E0B), Color(0x00F59E0B))
          )
        )

        // Draw Area & Line for Home Team (Cyan Curve)
        drawBezierCurve(
          data = homePoints.map { it.points.toFloat() },
          maxVal = maxPoints,
          lineColor = Color(0xFF38BDF8),
          fillGradient = Brush.verticalGradient(
            colors = listOf(Color(0x6638BDF8), Color(0x0038BDF8))
          )
        )

        // Draw Point Dots for Home Points
        val count = homePoints.size
        val stepX = width / (count - 1).coerceAtLeast(1)
        homePoints.forEachIndexed { i, pt ->
          val cx = i * stepX
          val cy = height - (pt.points.toFloat() / maxPoints) * height
          val dotColor = when (pt.result) {
            "W" -> Color(0xFF10B981) // Green
            "D" -> Color(0xFFF59E0B) // Amber
            else -> Color(0xFFEF4444) // Red
          }
          drawCircle(color = Color(0xFF0F172A), radius = 6f, center = Offset(cx, cy))
          drawCircle(color = dotColor, radius = 4f, center = Offset(cx, cy))
        }
      }

      // Recharts Tooltip Overlay
      selectedPointIndex?.let { idx ->
        val activeList = if (isHomeSelected) homePoints else awayPoints
        val activeName = if (isHomeSelected) homeTeamName else awayTeamName
        if (idx in activeList.indices) {
          val pt = activeList[idx]
          Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, GoldYellow),
            modifier = Modifier
              .align(Alignment.TopCenter)
              .padding(4.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${pt.matchNumber}. Maç: vs ${pt.opponent} (${pt.score})",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = when (pt.result) {
                  "W" -> Color(0xFF065F46)
                  "D" -> Color(0xFF78350F)
                  else -> Color(0xFF7F1D1D)
                },
                shape = RoundedCornerShape(3.dp)
              ) {
                Text(
                  text = when (pt.result) {
                    "W" -> "G (+3P)"
                    "D" -> "B (+1P)"
                    else -> "M (0P)"
                  },
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text("xG: ${pt.xg}", color = Color(0xFF38BDF8), fontSize = 9.sp)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Form Summary Badges (G-G-B-G-M)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$homeTeamName: ", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        homePoints.takeLast(5).forEach { pt ->
          FormBadge(pt.result)
          Spacer(modifier = Modifier.width(3.dp))
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$awayTeamName: ", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        awayPoints.takeLast(5).forEach { pt ->
          FormBadge(pt.result)
          Spacer(modifier = Modifier.width(3.dp))
        }
      }
    }
  }
}

/**
 * 2. Visual League Standings Bar Chart (Recharts BarChart Equivalent)
 */
@Composable
private fun StandingsBarChartContent(standings: List<VisualStandingItem>) {
  Column(modifier = Modifier.fillMaxWidth()) {
    // Header Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text("#  TAKIM", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2.5f))
      Text("O", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
      Text("G", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
      Text("B", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
      Text("M", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
      Text("AV", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.Center)
      Text("PUAN", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
    }

    // List of Teams with visual points bar
    val maxPts = standings.firstOrNull()?.points ?: 30
    standings.take(8).forEach { item ->
      StandingRowItem(item = item, maxPoints = maxPts)
    }
  }
}

@Composable
private fun StandingRowItem(item: VisualStandingItem, maxPoints: Int) {
  val barRatio = (item.points.toFloat() / maxPoints.toFloat()).coerceIn(0.1f, 1f)
  val rankColor = when (item.rank) {
    1, 2 -> Color(0xFF38BDF8) // Şampiyonlar Ligi
    3, 4 -> Color(0xFFF59E0B) // Avrupa Ligi
    else -> Color(0xFF64748B)
  }

  Surface(
    color = if (item.isUserFavorite) Color(0xFF1E293B) else Color.Transparent,
    shape = RoundedCornerShape(6.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(modifier = Modifier.weight(2.5f), verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${item.rank}.",
            color = rankColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(18.dp)
          )
          Text(
            text = item.teamName,
            color = if (item.isUserFavorite) GoldYellow else Color.White,
            fontSize = 11.sp,
            fontWeight = if (item.isUserFavorite) FontWeight.Black else FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Text("${item.played}", color = Color(0xFF94A3B8), fontSize = 10.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
        Text("${item.won}", color = Color(0xFF34D399), fontSize = 10.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
        Text("${item.drawn}", color = Color(0xFFFBBF24), fontSize = 10.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
        Text("${item.lost}", color = Color(0xFFF87171), fontSize = 10.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
        Text("${item.goalDifference}", color = Color(0xFF94A3B8), fontSize = 10.sp, modifier = Modifier.weight(0.9f), textAlign = TextAlign.Center)

        Text(
          text = "${item.points}",
          color = GoldYellow,
          fontSize = 12.sp,
          fontWeight = FontWeight.Black,
          modifier = Modifier.weight(1.2f),
          textAlign = TextAlign.End
        )
      }

      // Visual Progress Bar (Recharts Bar)
      Spacer(modifier = Modifier.height(2.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .clip(CircleShape)
          .background(Color(0xFF1E293B))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(barRatio)
            .fillMaxHeight()
            .clip(CircleShape)
            .background(
              Brush.horizontalGradient(
                colors = listOf(rankColor, rankColor.copy(alpha = 0.6f))
              )
            )
        )
      }
    }
  }
}

@Composable
private fun FormBadge(result: String) {
  val (color, text) = when (result) {
    "W" -> Color(0xFF059669) to "G"
    "D" -> Color(0xFFD97706) to "B"
    else -> Color(0xFFDC2626) to "M"
  }
  Surface(
    color = color,
    shape = CircleShape,
    modifier = Modifier.size(16.dp)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(text, color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Black)
    }
  }
}

/**
 * Extension helper to draw smooth cubic Bezier curve on Canvas with area gradient fill.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBezierCurve(
  data: List<Float>,
  maxVal: Float,
  lineColor: Color,
  fillGradient: Brush
) {
  if (data.size < 2) return

  val width = size.width
  val height = size.height
  val stepX = width / (data.size - 1)

  val points = data.mapIndexed { index, value ->
    val x = index * stepX
    val y = height - (value / maxVal) * height
    Offset(x, y)
  }

  val linePath = Path().apply {
    moveTo(points.first().x, points.first().y)
    for (i in 0 until points.size - 1) {
      val p0 = points[i]
      val p1 = points[i + 1]
      val controlX = (p0.x + p1.x) / 2
      cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
    }
  }

  val areaPath = Path().apply {
    addPath(linePath)
    lineTo(points.last().x, height)
    lineTo(points.first().x, height)
    close()
  }

  // Draw Area gradient fill
  drawPath(path = areaPath, brush = fillGradient)

  // Draw Line curve
  drawPath(
    path = linePath,
    color = lineColor,
    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
  )
}
