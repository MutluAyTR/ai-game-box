package com.example.ui.components.charts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.datasource.MackolikStandingsAndOddsDataSource
import com.example.data.model.LeagueStandingRow
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary

/**
 * Recharts & D3-inspired Live League Points Table & Visual Analytics Component.
 * Supports:
 * 1. Dynamic switching between all major domestic & international leagues.
 * 2. Visual D3 / Recharts Area & Bar Chart for team points progression and goal difference.
 * 3. Interactive live standings table with real qualification zone colors, form badges, and column sorting.
 */
@Composable
fun LiveLeaguePointsTableVisualizer(
  modifier: Modifier = Modifier,
  initialLeague: String = "Trendyol Süper Lig"
) {
  val supportedLeagues = remember {
    listOf(
      "Trendyol Süper Lig",
      "Premier League",
      "La Liga",
      "Serie A",
      "Bundesliga",
      "Ligue 1",
      "EuroLeague",
      "Türkiye Basketbol Süper Ligi (BSL)",
      "Vodafone Sultanlar Ligi",
      "NBA"
    )
  }

  var selectedLeague by remember { mutableStateOf(initialLeague) }
  var visualizerMode by remember { mutableIntStateOf(0) } // 0: D3 Görsel Grafik, 1: Canlı Puan Tablosu
  var selectedSortColumn by remember { mutableStateOf("points") } // "points", "gd", "won"
  var isSortAscending by remember { mutableStateOf(false) }
  var hoveredTeam by remember { mutableStateOf<LeagueStandingRow?>(null) }

  // Load standings for current league
  val rawStandings = remember(selectedLeague) {
    MackolikStandingsAndOddsDataSource.getStandingsForLeague(selectedLeague)
  }

  // Sort standings dynamically
  val sortedStandings = remember(rawStandings, selectedSortColumn, isSortAscending) {
    val list = when (selectedSortColumn) {
      "points" -> rawStandings.sortedByDescending { it.points }
      "gd" -> rawStandings.sortedByDescending { it.goalDifference }
      "won" -> rawStandings.sortedByDescending { it.won }
      else -> rawStandings.sortedBy { it.rank }
    }
    if (isSortAscending) list.reversed() else list
  }

  // Set default hovered team to leader
  LaunchedEffect(sortedStandings) {
    hoveredTeam = sortedStandings.firstOrNull()
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("live_league_points_visualizer"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // 1. Header with Title & Badge
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
              .size(34.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(TealPrimary, TealDark))),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ShowChart,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Canlı Puan Durumu & D3 Veri Analizi",
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color.White
            )
            Text(
              text = "Recharts & Maçkolik Canlı Tablo Motoru",
              fontSize = 10.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFF1E293B),
          border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
              text = "Canlı",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF10B981)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Horizontal League Switcher Tabs
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(supportedLeagues) { league ->
          val isSelected = selectedLeague == league
          FilterChip(
            selected = isSelected,
            onClick = { selectedLeague = league },
            label = {
              Text(
                text = league,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealDark,
              selectedLabelColor = Color.White,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFFCBD5E1)
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) TealPrimary else Color(0xFF334155)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. View Mode Switcher (D3 Görsel Grafik vs Canlı Tablo)
      TabRow(
        selectedTabIndex = visualizerMode,
        containerColor = Color(0xFF1E293B),
        contentColor = GoldYellow,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[visualizerMode]),
            color = GoldYellow,
            height = 2.5.dp
          )
        },
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
      ) {
        Tab(
          selected = visualizerMode == 0,
          onClick = { visualizerMode = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(15.dp), tint = if (visualizerMode == 0) GoldYellow else Color(0xFF94A3B8))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "D3 Grafik Analizi",
                fontSize = 11.5.sp,
                fontWeight = if (visualizerMode == 0) FontWeight.Bold else FontWeight.Normal,
                color = if (visualizerMode == 0) GoldYellow else Color(0xFF94A3B8)
              )
            }
          }
        )
        Tab(
          selected = visualizerMode == 1,
          onClick = { visualizerMode = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.FormatListNumbered, contentDescription = null, modifier = Modifier.size(15.dp), tint = if (visualizerMode == 1) GoldYellow else Color(0xFF94A3B8))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Canlı Puan Tablosu",
                fontSize = 11.5.sp,
                fontWeight = if (visualizerMode == 1) FontWeight.Bold else FontWeight.Normal,
                color = if (visualizerMode == 1) GoldYellow else Color(0xFF94A3B8)
              )
            }
          }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Content according to selected mode
      if (visualizerMode == 0) {
        // --- D3 / RECHARTS VISUAL AREA & BAR CHART ---
        D3PointsTrendCanvasChart(
          standings = sortedStandings,
          hoveredTeam = hoveredTeam,
          onSelectTeam = { hoveredTeam = it }
        )
      } else {
        // --- LIVE POINTS TABLE ---
        LiveStandingsTableView(
          standings = sortedStandings,
          selectedSortColumn = selectedSortColumn,
          isSortAscending = isSortAscending,
          onSort = { col ->
            if (selectedSortColumn == col) {
              isSortAscending = !isSortAscending
            } else {
              selectedSortColumn = col
              isSortAscending = false
            }
          }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 5. Qualification Zone Legend Footer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          LegendDot(color = Color(0xFF10B981), label = "Şampiyonlar Ligi")
          LegendDot(color = Color(0xFF3B82F6), label = "Avrupa Ligi")
          LegendDot(color = Color(0xFFEF4444), label = "Düşme Hattı")
        }
        Text(
          text = "${sortedStandings.size} Takım",
          fontSize = 10.sp,
          color = Color(0xFF64748B),
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

@Composable
private fun LegendDot(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = label,
      fontSize = 9.sp,
      color = Color(0xFF94A3B8)
    )
  }
}

/**
 * Custom Canvas Data Visualization:
 * Inspired by D3.js and Recharts AreaChart & BarChart with smooth cubic curves,
 * gradient area fill, and interactive team data tooltips.
 */
@Composable
private fun D3PointsTrendCanvasChart(
  standings: List<LeagueStandingRow>,
  hoveredTeam: LeagueStandingRow?,
  onSelectTeam: (LeagueStandingRow) -> Unit
) {
  var isAnimated by remember { mutableStateOf(false) }
  LaunchedEffect(standings) {
    isAnimated = true
  }

  val animProgress by animateFloatAsState(
    targetValue = if (isAnimated) 1f else 0f,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "d3_chart_anim"
  )

  Column(modifier = Modifier.fillMaxWidth()) {
    // Tooltip Card for currently hovered/selected team
    if (hoveredTeam != null) {
      Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = Color(0xFF0F172A),
              modifier = Modifier.size(22.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "#${hoveredTeam.rank}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldYellow
                )
              }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = hoveredTeam.teamName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "${hoveredTeam.played} Maç | ${hoveredTeam.won}G ${hoveredTeam.drawn}B ${hoveredTeam.lost}M | AV: ${if (hoveredTeam.goalDifference > 0) "+${hoveredTeam.goalDifference}" else hoveredTeam.goalDifference}",
                fontSize = 9.5.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${hoveredTeam.points} Puan",
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = GoldYellow
            )
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
              hoveredTeam.form.takeLast(5).forEach { f ->
                FormPill(f, mini = true)
              }
            }
          }
        }
      }
    }

    // Chart Canvas (Height: 150dp)
    val maxPoints = remember(standings) {
      (standings.maxOfOrNull { it.points } ?: 30).coerceAtLeast(10)
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF1E293B))
        .pointerInput(standings) {
          detectTapGestures { offset ->
            val count = standings.size
            if (count > 0) {
              val slotWidth = size.width / count.toFloat()
              val tappedIndex = (offset.x / slotWidth).toInt().coerceIn(0, count - 1)
              onSelectTeam(standings[tappedIndex])
            }
          }
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp)) {
        val width = size.width
        val height = size.height
        val count = standings.size
        if (count < 2) return@Canvas

        val stepX = width / (count - 1).toFloat()

        // 1. Draw horizontal reference grid lines
        val gridLines = 3
        for (i in 0..gridLines) {
          val y = height * (i / gridLines.toFloat())
          drawLine(
            color = Color(0xFF334155).copy(alpha = 0.6f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
          )
        }

        // 2. Draw team points bars (Semi-transparent bars)
        standings.forEachIndexed { i, row ->
          val barWidth = (stepX * 0.55f).coerceIn(4f, 16f)
          val barHeight = (row.points / maxPoints.toFloat()) * height * animProgress
          val x = (i * stepX) - (barWidth / 2f)
          val y = height - barHeight

          val isHovered = hoveredTeam?.teamName == row.teamName
          val barColor = if (isHovered) GoldYellow else Color(0xFF0284C7).copy(alpha = 0.45f)

          drawRoundRect(
            color = barColor,
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
          )
        }

        // 3. Construct Smooth D3-style Cubic Curve for Points Trendline
        val curvePath = Path()
        val areaPath = Path()

        val pointsCoords = mutableListOf<Offset>()
        standings.forEachIndexed { i, row ->
          val x = i * stepX
          val y = height - ((row.points / maxPoints.toFloat()) * height * animProgress)
          pointsCoords.add(Offset(x, y))
        }

        if (pointsCoords.isNotEmpty()) {
          curvePath.moveTo(pointsCoords[0].x, pointsCoords[0].y)
          areaPath.moveTo(pointsCoords[0].x, height)
          areaPath.lineTo(pointsCoords[0].x, pointsCoords[0].y)

          for (i in 0 until pointsCoords.size - 1) {
            val p0 = pointsCoords[i]
            val p1 = pointsCoords[i + 1]
            val midX = (p0.x + p1.x) / 2f

            curvePath.cubicTo(
              midX, p0.y,
              midX, p1.y,
              p1.x, p1.y
            )
            areaPath.cubicTo(
              midX, p0.y,
              midX, p1.y,
              p1.x, p1.y
            )
          }

          areaPath.lineTo(pointsCoords.last().x, height)
          areaPath.close()

          // Draw Gradient Area fill below curve
          drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
              colors = listOf(
                Color(0xFF38BDF8).copy(alpha = 0.35f),
                Color(0xFF0284C7).copy(alpha = 0.02f)
              ),
              startY = 0f,
              endY = height
            )
          )

          // Draw Stroke line
          drawPath(
            path = curvePath,
            brush = Brush.horizontalGradient(
              listOf(Color(0xFF38BDF8), GoldYellow, Color(0xFF10B981))
            ),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
          )

          // Draw Glowing Points & Hover Indicator
          pointsCoords.forEachIndexed { idx, pt ->
            val isHovered = hoveredTeam?.teamName == standings[idx].teamName
            if (isHovered) {
              drawCircle(
                color = GoldYellow.copy(alpha = 0.4f),
                radius = 7.dp.toPx(),
                center = pt
              )
              drawCircle(
                color = GoldYellow,
                radius = 4.dp.toPx(),
                center = pt
              )
            } else if (idx == 0 || idx == count - 1 || idx % 2 == 0) {
              drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = pt
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Live Standings Table View with responsive columns, qualification zone badges,
 * and column sorting.
 */
@Composable
private fun LiveStandingsTableView(
  standings: List<LeagueStandingRow>,
  selectedSortColumn: String,
  isSortAscending: Boolean,
  onSort: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF1E293B))
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    // Table Header Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "#",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        modifier = Modifier.width(22.dp),
        textAlign = TextAlign.Center
      )
      Text(
        text = "Takım",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        modifier = Modifier.weight(1f)
      )
      TableHeaderCell(label = "O", active = false, onClick = {})
      TableHeaderCell(
        label = "G",
        active = selectedSortColumn == "won",
        onClick = { onSort("won") }
      )
      TableHeaderCell(label = "B", active = false, onClick = {})
      TableHeaderCell(label = "M", active = false, onClick = {})
      TableHeaderCell(
        label = "AV",
        active = selectedSortColumn == "gd",
        onClick = { onSort("gd") }
      )
      TableHeaderCell(
        label = "P",
        active = selectedSortColumn == "points",
        onClick = { onSort("points") },
        highlight = true
      )
      Text(
        text = "Form",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        modifier = Modifier.width(62.dp),
        textAlign = TextAlign.Center
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    // Table Data Rows
    standings.forEachIndexed { index, row ->
      val isLeader = index == 0
      val zoneColor = when {
        index < 2 -> Color(0xFF10B981) // Champions League
        index < 4 -> Color(0xFF3B82F6) // Europa League
        index >= standings.size - 3 -> Color(0xFFEF4444) // Relegation
        else -> Color(0xFF64748B)
      }

      Surface(
        color = if (index % 2 == 0) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.6f),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Rank with colored zone pill
          Box(
            modifier = Modifier.width(22.dp),
            contentAlignment = Alignment.Center
          ) {
            Surface(
              shape = RoundedCornerShape(3.dp),
              color = zoneColor.copy(alpha = 0.2f),
              border = BorderStroke(0.5.dp, zoneColor)
            ) {
              Text(
                text = "${row.rank}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = zoneColor,
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
              )
            }
          }

          // Team Name
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (isLeader) {
              Text("👑", fontSize = 10.sp)
              Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
              text = row.teamName,
              fontSize = 11.sp,
              fontWeight = if (isLeader) FontWeight.Bold else FontWeight.Medium,
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          // Stats
          TableStatCell(text = "${row.played}")
          TableStatCell(text = "${row.won}")
          TableStatCell(text = "${row.drawn}")
          TableStatCell(text = "${row.lost}")
          TableStatCell(
            text = if (row.goalDifference > 0) "+${row.goalDifference}" else "${row.goalDifference}",
            color = if (row.goalDifference > 0) Color(0xFF10B981) else if (row.goalDifference < 0) Color(0xFFEF4444) else Color(0xFF94A3B8)
          )
          TableStatCell(
            text = "${row.points}",
            color = GoldYellow,
            bold = true
          )

          // Form Pills (last 4-5)
          Row(
            modifier = Modifier.width(62.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            row.form.takeLast(4).forEach { f ->
              FormPill(f)
              Spacer(modifier = Modifier.width(2.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TableHeaderCell(
  label: String,
  active: Boolean,
  onClick: () -> Unit,
  highlight: Boolean = false
) {
  Box(
    modifier = Modifier
      .width(22.dp)
      .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = if (active || highlight) FontWeight.Black else FontWeight.Bold,
      color = if (active) GoldYellow else if (highlight) Color(0xFFF59E0B) else Color(0xFF94A3B8),
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun TableStatCell(
  text: String,
  color: Color = Color(0xFFCBD5E1),
  bold: Boolean = false
) {
  Box(
    modifier = Modifier.width(22.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontSize = 10.sp,
      fontWeight = if (bold) FontWeight.Black else FontWeight.Normal,
      color = color,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun FormPill(result: String, mini: Boolean = false) {
  val (bgColor, textColor) = when (result.uppercase()) {
    "G", "W" -> Pair(Color(0xFF10B981), Color.White)
    "B", "D" -> Pair(Color(0xFFF59E0B), Color.Black)
    else -> Pair(Color(0xFFEF4444), Color.White)
  }

  Surface(
    shape = CircleShape,
    color = bgColor,
    modifier = Modifier.size(if (mini) 10.dp else 12.dp)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = result.take(1).uppercase(),
        fontSize = if (mini) 6.sp else 7.sp,
        fontWeight = FontWeight.Bold,
        color = textColor
      )
    }
  }
}
