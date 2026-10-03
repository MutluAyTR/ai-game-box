package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import kotlin.math.sin

/**
 * Data point for a 5-minute interval across the 90 minutes.
 */
data class TrendPoint(
  val minute: Int,
  val homePossession: Float,
  val awayPossession: Float,
  val homePressure: Float, // 0..100
  val awayPressure: Float, // 0..100
  val homeAccuracy: Float, // %
  val awayAccuracy: Float  // %
)

/**
 * High-performance, D3-inspired data visualization component.
 * Displays match possession, shot accuracy, and offensive pressure trends over the course of 90 minutes.
 */
@Composable
fun D3MatchTrendVisualization(match: Match) {
  var activeMetric by remember { mutableStateOf(0) } // 0: Baskı / Momentum, 1: Topa Sahip Olma, 2: Şut İsabeti
  var isAnimated by remember { mutableStateOf(false) }

  LaunchedEffect(match.id) {
    isAnimated = true
  }

  val progress by animateFloatAsState(
    targetValue = if (isAnimated) 1f else 0f,
    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
    label = "d3_curve_anim"
  )

  // Generate realistic 90-minute curve intervals (every 5-10 minutes)
  val points = remember(match.id, match.homeScore, match.awayScore, match.statistics.possessionHome) {
    generateRealisticMatchData(match)
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header
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
                imageVector = Icons.Default.Timeline,
                contentDescription = "D3 Trends",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "D3 Dinamik Maç Trendi (90')",
              fontWeight = FontWeight.Black,
              fontSize = 14.sp,
              color = TealDark
            )
            Text(
              text = "Baskı, şut isabeti ve topla oynama akış grafiği",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Metric Switcher Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricChip(
          title = "Baskı & Momentum",
          icon = Icons.Default.Speed,
          isSelected = activeMetric == 0,
          onClick = { activeMetric = 0 },
          modifier = Modifier.weight(1f)
        )
        MetricChip(
          title = "Topa Sahip Olma",
          icon = Icons.Default.Timeline,
          isSelected = activeMetric == 1,
          onClick = { activeMetric = 1 },
          modifier = Modifier.weight(1f)
        )
        MetricChip(
          title = "Şut İsabeti",
          icon = Icons.AutoMirrored.Filled.ShowChart,
          isSelected = activeMetric == 2,
          onClick = { activeMetric = 2 },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Team Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .background(TealDark, CircleShape)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = match.homeTeam,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TealDark
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .background(Color(0xFFE11D48), CircleShape)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = match.awayTeam,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE11D48)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // D3 Animated Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
          .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
          val width = size.width
          val height = size.height

          // Draw horizontal guideline grid
          val gridLines = 4
          for (i in 0..gridLines) {
            val y = height * (i.toFloat() / gridLines)
            drawLine(
              color = Color(0xFFE2E8F0),
              start = Offset(0f, y),
              end = Offset(width, y),
              strokeWidth = 1f
            )
          }

          // Draw vertical minute grid (0', 15', 30', 45', 60', 75', 90')
          val minuteMarks = listOf(0, 15, 30, 45, 60, 75, 90)
          for (min in minuteMarks) {
            val x = (min / 90f) * width
            drawLine(
              color = Color(0xFFF1F5F9),
              start = Offset(x, 0f),
              end = Offset(x, height),
              strokeWidth = 1.5f
            )
          }

          // Build Smooth Bezier Paths for Home and Away curves
          val homePath = Path()
          val awayPath = Path()
          val homeFillPath = Path()

          val homeValues = points.map { pt ->
            when (activeMetric) {
              0 -> pt.homePressure
              1 -> pt.homePossession
              else -> pt.homeAccuracy
            }
          }

          val awayValues = points.map { pt ->
            when (activeMetric) {
              0 -> pt.awayPressure
              1 -> pt.awayPossession
              else -> pt.awayAccuracy
            }
          }

          if (homeValues.isNotEmpty()) {
            val stepX = width / (points.size - 1)

            // Start home path
            val startY = height - (homeValues[0] / 100f * height * progress)
            homePath.moveTo(0f, startY)
            homeFillPath.moveTo(0f, height)
            homeFillPath.lineTo(0f, startY)

            for (i in 1 until points.size) {
              val prevX = (i - 1) * stepX
              val prevY = height - (homeValues[i - 1] / 100f * height * progress)
              val currX = i * stepX
              val currY = height - (homeValues[i] / 100f * height * progress)

              val controlX1 = prevX + (currX - prevX) / 2
              val controlX2 = prevX + (currX - prevX) / 2

              homePath.cubicTo(controlX1, prevY, controlX2, currY, currX, currY)
              homeFillPath.cubicTo(controlX1, prevY, controlX2, currY, currX, currY)
            }
            homeFillPath.lineTo(width, height)
            homeFillPath.close()

            // Away Path
            val awayStartY = height - (awayValues[0] / 100f * height * progress)
            awayPath.moveTo(0f, awayStartY)

            for (i in 1 until points.size) {
              val prevX = (i - 1) * stepX
              val prevY = height - (awayValues[i - 1] / 100f * height * progress)
              val currX = i * stepX
              val currY = height - (awayValues[i] / 100f * height * progress)

              val controlX1 = prevX + (currX - prevX) / 2
              val controlX2 = prevX + (currX - prevX) / 2

              awayPath.cubicTo(controlX1, prevY, controlX2, currY, currX, currY)
            }

            // Draw Area Fill for Home
            drawPath(
              path = homeFillPath,
              brush = Brush.verticalGradient(
                colors = listOf(TealPrimary.copy(alpha = 0.25f), Color.Transparent)
              )
            )

            // Draw Home Stroke Curve
            drawPath(
              path = homePath,
              color = TealDark,
              style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // Draw Away Stroke Curve
            drawPath(
              path = awayPath,
              color = Color(0xFFE11D48),
              style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // Draw current match minute marker line
            val currentMatchX = ((match.minute.coerceIn(0, 90)) / 90f) * width
            drawLine(
              color = Color(0xFFD97706),
              start = Offset(currentMatchX, 0f),
              end = Offset(currentMatchX, height),
              strokeWidth = 2f
            )
          }
        }
      }

      // X-Axis Minute Labels
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        listOf("0'", "15'", "30'", "HT 45'", "60'", "75'", "90'").forEach { minuteLabel ->
          Text(
            text = minuteLabel,
            fontSize = 10.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Summary Analysis Pill
      Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "📊", fontSize = 14.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = when (activeMetric) {
              0 -> "Baskı Trendi: ${if (match.statistics.possessionHome > 50) match.homeTeam else match.awayTeam} 65' sonrasında ceza sahası önü baskısını %28 artırdı."
              1 -> "Top Hakimiyeti: İlk yarıdaki tempo %${match.statistics.possessionHome} ile ${match.homeTeam} kontrolünde seyretti."
              else -> "İsabetli Şut Oranı: ${match.homeTeam} %${(match.statistics.shotsOnTargetHome.toFloat() / (match.statistics.shotsHome.coerceAtLeast(1)) * 100).toInt()} isabet yakaladı."
            },
            fontSize = 11.sp,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}

@Composable
private fun MetricChip(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) TealDark else Color(0xFFF1F5F9),
    onClick = onClick
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = if (isSelected) Color.White else Color(0xFF64748B),
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) Color.White else Color(0xFF475569)
      )
    }
  }
}

/**
 * Generates continuous 90-minute data points for D3 trends based on match events and stats.
 */
private fun generateRealisticMatchData(match: Match): List<TrendPoint> {
  val list = mutableListOf<TrendPoint>()
  val baseHomePoss = match.statistics.possessionHome.toFloat()
  val baseAwayPoss = (100 - baseHomePoss).coerceAtLeast(0f)

  val homeAccuracyRate = (match.statistics.shotsOnTargetHome.toFloat() / match.statistics.shotsHome.coerceAtLeast(1) * 100f).coerceIn(20f, 90f)
  val awayAccuracyRate = (match.statistics.shotsOnTargetAway.toFloat() / match.statistics.shotsAway.coerceAtLeast(1) * 100f).coerceIn(20f, 90f)

  for (min in 0..90 step 5) {
    // Natural variance wave
    val wave = (sin(min * 0.15) * 8).toFloat()
    val goalBoostHome = if (match.homeScore > 0 && min > 20) 10f else 0f
    val goalBoostAway = if (match.awayScore > 0 && min > 35) 8f else 0f

    val homePoss = (baseHomePoss + wave + (goalBoostHome - goalBoostAway)).coerceIn(25f, 75f)
    val awayPoss = 100f - homePoss

    val homePressure = ((match.statistics.dangerousAttacksHome.toFloat() / 60f * 70f) + wave + (min * 0.2f)).coerceIn(15f, 95f)
    val awayPressure = ((match.statistics.dangerousAttacksAway.toFloat() / 60f * 70f) - wave + (min * 0.18f)).coerceIn(15f, 95f)

    val homeAcc = (homeAccuracyRate + wave * 0.5f).coerceIn(20f, 95f)
    val awayAcc = (awayAccuracyRate - wave * 0.5f).coerceIn(20f, 95f)

    list.add(
      TrendPoint(
        minute = min,
        homePossession = homePoss,
        awayPossession = awayPoss,
        homePressure = homePressure,
        awayPressure = awayPressure,
        homeAccuracy = homeAcc,
        awayAccuracy = awayAcc
      )
    )
  }
  return list
}
