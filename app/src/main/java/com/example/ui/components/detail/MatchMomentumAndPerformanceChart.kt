package com.example.ui.components.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.PlayerLineup
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlin.math.cos
import kotlin.math.sin

data class MomentumPoint(
  val minute: Int,
  val value: Float, // -100f (Away Max) to +100f (Home Max)
  val event: String? = null // e.g. "⚽ Gol", "🟨 Kart"
)

data class PlayerRadarMetric(
  val label: String,
  val homeValue: Float, // 0..100
  val awayValue: Float  // 0..100
)

/**
 * D3 / Recharts-style Graphing Module for MatchDetailContainer
 * 1. Live Match Momentum Shift Chart (Area Gradient with interactive scrub line)
 * 2. Player Multi-Metric Radar Chart (D3 Spider Web comparing key attributes)
 */
@Composable
fun MatchMomentumAndPerformanceChart(
  match: Match,
  modifier: Modifier = Modifier
) {
  var selectedChartTab by remember { mutableIntStateOf(0) } // 0: Maç Momentumu, 1: Oyuncu Performans Radarı
  var scrubMinute by remember { mutableStateOf<Int?>(match.minute.coerceAtLeast(15)) }

  // Generate deterministic realistic momentum points
  val momentumPoints = remember(match.id) {
    listOf(
      MomentumPoint(5, 25f),
      MomentumPoint(10, 45f),
      MomentumPoint(15, 75f, "⚽ Gol"),
      MomentumPoint(20, 30f),
      MomentumPoint(25, -20f),
      MomentumPoint(30, -55f, "🟨 Kart"),
      MomentumPoint(35, -40f),
      MomentumPoint(40, 10f),
      MomentumPoint(45, 35f),
      MomentumPoint(50, 50f),
      MomentumPoint(55, -30f),
      MomentumPoint(60, -70f, "💥 Fırsat"),
      MomentumPoint(65, -20f),
      MomentumPoint(70, 40f),
      MomentumPoint(75, 65f),
      MomentumPoint(80, 80f, "⚽ Gol"),
      MomentumPoint(85, 30f),
      MomentumPoint(90, 15f)
    )
  }

  val radarMetrics = remember(match.id) {
    listOf(
      PlayerRadarMetric("xG / Tehdit", 85f, 65f),
      PlayerRadarMetric("İsabetli Pas", 91f, 82f),
      PlayerRadarMetric("İkili Mücadele", 74f, 78f),
      PlayerRadarMetric("Top Kazanma", 80f, 70f),
      PlayerRadarMetric("Hız / Sprint", 88f, 84f),
      PlayerRadarMetric("Taktik Disiplin", 92f, 86f)
    )
  }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
    border = BorderStroke(1.dp, Color(0xFF374151)),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header & Chart Switcher Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (selectedChartTab == 0) Icons.Default.Timeline else Icons.Default.BarChart,
            contentDescription = null,
            tint = GoldYellow,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (selectedChartTab == 0) "D3 CANLI MAÇ BASKI MOMENTUMU" else "RECHARTS OYUNCU RADAR GRAFİĞİ",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Surface(
            color = if (selectedChartTab == 0) GoldYellow else Color(0xFF1F2937),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.clickable { selectedChartTab = 0 }
          ) {
            Text(
              text = "Momentum",
              color = if (selectedChartTab == 0) Color.Black else Color(0xFF94A3B8),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }

          Surface(
            color = if (selectedChartTab == 1) GoldYellow else Color(0xFF1F2937),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.clickable { selectedChartTab = 1 }
          ) {
            Text(
              text = "Radar Analizi",
              color = if (selectedChartTab == 1) Color.Black else Color(0xFF94A3B8),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (selectedChartTab == 0) {
        // 1. RECHARTS / D3 AREA GRADIENT MOMENTUM CHART
        Text(
          text = "Parmağınızı grafik üzerinde kaydırarak dakika bazlı hücum baskısını inceleyin",
          color = Color(0xFF94A3B8),
          fontSize = 8.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color(0xFF0B132B), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
        ) {
          Canvas(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 14.dp, vertical = 12.dp)
              .pointerInput(Unit) {
                detectDragGestures(
                  onDragStart = { offset ->
                    val ratio = (offset.x / size.width).coerceIn(0f, 1f)
                    scrubMinute = (ratio * 90).toInt()
                  },
                  onDrag = { change, _ ->
                    change.consume()
                    val ratio = (change.position.x / size.width).coerceIn(0f, 1f)
                    scrubMinute = (ratio * 90).toInt()
                  }
                )
              }
          ) {
            val w = size.width
            val h = size.height
            val centerY = h / 2f

            // Neutral Centerline (0)
            drawLine(
              color = Color(0x55FFFFFF),
              start = Offset(0f, centerY),
              end = Offset(w, centerY),
              strokeWidth = 1.5f
            )

            // Timeline labels 15', 30', 45', 60', 75', 90'
            val timeSteps = listOf(0, 15, 30, 45, 60, 75, 90)
            for (t in timeSteps) {
              val x = (t / 90f) * w
              drawLine(
                color = Color(0x22FFFFFF),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
              )
            }

            // Build Paths for Area & Stroke
            val homePath = Path()
            val awayPath = Path()
            val linePath = Path()

            momentumPoints.forEachIndexed { i, pt ->
              val x = (pt.minute / 90f) * w
              val y = centerY - (pt.value / 100f) * (centerY * 0.85f)

              if (i == 0) {
                linePath.moveTo(x, y)
              } else {
                val prevPt = momentumPoints[i - 1]
                val prevX = (prevPt.minute / 90f) * w
                val prevY = centerY - (prevPt.value / 100f) * (centerY * 0.85f)
                val cX = (prevX + x) / 2f
                linePath.cubicTo(cX, prevY, cX, y, x, y)
              }

              // Event markers (Goals, Cards)
              if (pt.event != null) {
                drawCircle(
                  color = if (pt.value > 0) GoldYellow else Color(0xFF38BDF8),
                  radius = 4.5f,
                  center = Offset(x, y)
                )
                drawCircle(
                  color = Color.White,
                  radius = 2f,
                  center = Offset(x, y)
                )
              }
            }

            // Stroke
            drawPath(
              path = linePath,
              color = GoldYellow,
              style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )

            // Scrubbing line indicator
            scrubMinute?.let { min ->
              val scrubX = (min / 90f) * w
              drawLine(
                color = Color.White,
                start = Offset(scrubX, 0f),
                end = Offset(scrubX, h),
                strokeWidth = 2f
              )
              drawCircle(
                color = Color.White,
                radius = 5f,
                center = Offset(scrubX, centerY)
              )
            }
          }

          // Active scrub info chip
          scrubMinute?.let { min ->
            val closestPt = momentumPoints.minByOrNull { kotlin.math.abs(it.minute - min) }
            val attackingTeam = if ((closestPt?.value ?: 0f) >= 0) match.homeTeam else match.awayTeam
            val dominanceScore = kotlin.math.abs(closestPt?.value?.toInt() ?: 0)

            Surface(
              color = Color(0xDD0F172A),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(1.dp, GoldYellow),
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
            ) {
              Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(
                  text = "⏱️ $min. Dakika Baskısı",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "$attackingTeam %$dominanceScore Baskılı",
                  color = if ((closestPt?.value ?: 0f) >= 0) GoldYellow else Color(0xFF38BDF8),
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Black
                )
                if (closestPt?.event != null) {
                  Text(
                    text = "Olay: ${closestPt.event}",
                    color = Color(0xFF4ADE80),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("🏠 ${match.homeTeam} Baskısı", color = GoldYellow, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
          Text("Eşit Momentum (0)", color = Color(0xFF64748B), fontSize = 8.sp)
          Text("✈️ ${match.awayTeam} Baskısı", color = Color(0xFF38BDF8), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
        }
      } else {
        // 2. D3 SPIDER WEB RADAR CHART
        Text(
          text = "İki takımın hücum, savunma ve taktik verimlilik radar karşılaştırması",
          color = Color(0xFF94A3B8),
          fontSize = 8.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Radar Canvas
          Box(
            modifier = Modifier
              .weight(1.1f)
              .height(200.dp)
          ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
              val w = size.width
              val h = size.height
              val center = Offset(w / 2f, h / 2f)
              val radius = (minOf(w, h) / 2f) * 0.85f

              val numAxes = radarMetrics.size
              val angleStep = (2 * Math.PI / numAxes).toFloat()

              // Concentric Spider Rings (25%, 50%, 75%, 100%)
              val rings = listOf(0.25f, 0.50f, 0.75f, 1.0f)
              for (r in rings) {
                val ringPath = Path()
                for (i in 0 until numAxes) {
                  val angle = i * angleStep - (Math.PI / 2).toFloat()
                  val x = center.x + radius * r * cos(angle)
                  val y = center.y + radius * r * sin(angle)
                  if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
                }
                ringPath.close()
                drawPath(path = ringPath, color = Color(0x33475569), style = Stroke(1.2f))
              }

              // Axes from Center
              for (i in 0 until numAxes) {
                val angle = i * angleStep - (Math.PI / 2).toFloat()
                val x = center.x + radius * cos(angle)
                val y = center.y + radius * sin(angle)
                drawLine(color = Color(0x4494A3B8), start = center, end = Offset(x, y), strokeWidth = 1.2f)
              }

              // Home Polygon
              val homePath = Path()
              radarMetrics.forEachIndexed { i, m ->
                val angle = i * angleStep - (Math.PI / 2).toFloat()
                val r = radius * (m.homeValue / 100f)
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                if (i == 0) homePath.moveTo(x, y) else homePath.lineTo(x, y)
                drawCircle(color = GoldYellow, radius = 3.5f, center = Offset(x, y))
              }
              homePath.close()
              drawPath(path = homePath, color = GoldYellow.copy(alpha = 0.35f))
              drawPath(path = homePath, color = GoldYellow, style = Stroke(2f))

              // Away Polygon
              val awayPath = Path()
              radarMetrics.forEachIndexed { i, m ->
                val angle = i * angleStep - (Math.PI / 2).toFloat()
                val r = radius * (m.awayValue / 100f)
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                if (i == 0) awayPath.moveTo(x, y) else awayPath.lineTo(x, y)
                drawCircle(color = Color(0xFF38BDF8), radius = 3f, center = Offset(x, y))
              }
              awayPath.close()
              drawPath(path = awayPath, color = Color(0xFF38BDF8).copy(alpha = 0.30f))
              drawPath(path = awayPath, color = Color(0xFF38BDF8), style = Stroke(1.8f))
            }
          }

          // Metric Values Legend
          Column(
            modifier = Modifier
              .weight(0.9f)
              .padding(start = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            radarMetrics.forEach { metric ->
              Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(metric.label, color = Color(0xFFCBD5E1), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                  Row {
                    Text("${metric.homeValue.toInt()}", color = GoldYellow, fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                    Text(" / ", color = Color(0xFF64748B), fontSize = 8.sp)
                    Text("${metric.awayValue.toInt()}", color = Color(0xFF38BDF8), fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                LinearProgressIndicator(
                  progress = { metric.homeValue / 100f },
                  color = GoldYellow,
                  trackColor = Color(0xFF1E293B),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                )
              }
            }
          }
        }
      }
    }
  }
}
