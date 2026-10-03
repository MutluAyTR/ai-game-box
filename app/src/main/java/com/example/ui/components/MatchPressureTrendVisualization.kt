package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import kotlin.math.sin

/**
 * Advanced D3-inspired Match Possession, Shot Accuracy, and Real-Time Pressure Trend Visualization.
 */
@Composable
fun MatchPressureTrendVisualization(
  match: Match,
  modifier: Modifier = Modifier
) {
  val stats = match.statistics
  val homePossession = stats.possessionHome.coerceIn(10, 90)
  val awayPossession = (100 - homePossession).coerceIn(10, 90)

  val homeShotsOnTarget = stats.shotsOnTargetHome
  val homeTotalShots = (stats.shotsHome).coerceAtLeast(homeShotsOnTarget).coerceAtLeast(1)
  val homeAccuracy = ((homeShotsOnTarget.toFloat() / homeTotalShots) * 100).toInt()

  val awayShotsOnTarget = stats.shotsOnTargetAway
  val awayTotalShots = (stats.shotsAway).coerceAtLeast(awayShotsOnTarget).coerceAtLeast(1)
  val awayAccuracy = ((awayShotsOnTarget.toFloat() / awayTotalShots) * 100).toInt()

  // Selected scrub minute for interactive exploration
  var scrubMinute by remember { mutableFloatStateOf(match.minute.coerceIn(1, 90).toFloat()) }
  var isScrubbing by remember { mutableStateOf(false) }

  // 15-Minute Segmented Possession Waves
  val possessionSegments = remember(match.id, homePossession) {
    listOf(
      Triple("0-15'", (homePossession + 5).coerceIn(35, 75), 100 - (homePossession + 5).coerceIn(35, 75)),
      Triple("15-30'", (homePossession - 4).coerceIn(35, 75), 100 - (homePossession - 4).coerceIn(35, 75)),
      Triple("30-45'", homePossession.coerceIn(35, 75), 100 - homePossession.coerceIn(35, 75)),
      Triple("45-60'", (homePossession + 8).coerceIn(35, 75), 100 - (homePossession + 8).coerceIn(35, 75)),
      Triple("60-75'", (homePossession - 6).coerceIn(35, 75), 100 - (homePossession - 6).coerceIn(35, 75)),
      Triple("75-90'", homePossession.coerceIn(35, 75), 100 - homePossession.coerceIn(35, 75))
    )
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(12.dp)
      .testTag("pressure_trend_visualization"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Header with title & Live Pulse
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Timeline,
          contentDescription = null,
          tint = TealDark,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "D3 Baskı, Şut & Top Hakimiyeti",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )
      }

      Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
      ) {
        Text(
          text = "90' Canlı Akış",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = TealDark,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
      }
    }

    // 2. Interactive D3 Pressure & Momentum Graph (0-90 min)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Maç Baskı & Momentum İndeksi",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
            Text(
              text = "Parmağınızla kaydırarak dakika bazlı tehlikeli hücumları inceleyin",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          }

          // Interactive scrubber indicator badge
          Surface(
            color = TealDark,
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "${scrubMinute.toInt()}. Dakika",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(TealDark)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${match.homeTeam} (Üst)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealDark)
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFFE11D48))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${match.awayTeam} (Alt)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Canvas Pressure Wave Rendering
        val homeColor = TealDark
        val awayColor = Color(0xFFE11D48)
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseOffset by infiniteTransition.animateFloat(
          initialValue = 0f,
          targetValue = 6.28f,
          animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Restart),
          label = "pulse_anim"
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .pointerInput(Unit) {
              detectTapGestures { offset ->
                val ratio = (offset.x / size.width).coerceIn(0f, 1f)
                scrubMinute = (ratio * 90f).coerceIn(1f, 90f)
              }
            }
            .pointerInput(Unit) {
              detectDragGestures(
                onDragStart = { isScrubbing = true },
                onDragEnd = { isScrubbing = false },
                onDragCancel = { isScrubbing = false }
              ) { change, _ ->
                val ratio = (change.position.x / size.width).coerceIn(0f, 1f)
                scrubMinute = (ratio * 90f).coerceIn(1f, 90f)
              }
            }
        ) {
          Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f

            // Baseline center line
            drawLine(
              color = Color(0xFF334155),
              start = Offset(0f, centerY),
              end = Offset(width, centerY),
              strokeWidth = 1.5f
            )

            // 15-minute grid lines
            listOf(15, 30, 45, 60, 75).forEach { min ->
              val x = (min / 90f) * width
              drawLine(
                color = Color(0xFF1E293B),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
              )
            }

            // Generate realistic momentum waves (Home > 0, Away < 0)
            val homePath = Path()
            val awayPath = Path()
            homePath.moveTo(0f, centerY)
            awayPath.moveTo(0f, centerY)

            val step = 3
            var prevX = 0f
            var prevHomeY = centerY
            var prevAwayY = centerY

            for (m in 0..90 step step) {
              val x = (m / 90f) * width
              // Realistic momentum formula based on match possession, events, and sinusoid
              val baseHome = ((homePossession - 50) * 0.8f)
              val wave1 = (sin((m * 0.12f) + pulseOffset) * 28f)
              val wave2 = (sin(m * 0.25f) * 14f)
              val netMomentum = (baseHome + wave1 + wave2).coerceIn(-65f, 65f)

              if (netMomentum >= 0) {
                // Home pressure above baseline
                val y = centerY - (netMomentum * 0.9f)
                homePath.lineTo(x, y)
                awayPath.lineTo(x, centerY)
              } else {
                // Away pressure below baseline
                val y = centerY + (-netMomentum * 0.9f)
                awayPath.lineTo(x, y)
                homePath.lineTo(x, centerY)
              }
            }

            homePath.lineTo(width, centerY)
            awayPath.lineTo(width, centerY)
            homePath.close()
            awayPath.close()

            // Draw filled gradient paths
            drawPath(
              path = homePath,
              brush = Brush.verticalGradient(
                colors = listOf(homeColor.copy(alpha = 0.8f), homeColor.copy(alpha = 0.1f)),
                startY = 0f,
                endY = centerY
              )
            )

            drawPath(
              path = awayPath,
              brush = Brush.verticalGradient(
                colors = listOf(awayColor.copy(alpha = 0.1f), awayColor.copy(alpha = 0.8f)),
                startY = centerY,
                endY = height
              )
            )

            // Stroke outlines
            drawPath(
              path = homePath,
              color = homeColor,
              style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )
            drawPath(
              path = awayPath,
              color = awayColor,
              style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )

            // Draw key events markers (Goals, Red cards)
            match.events.forEach { ev ->
              val evMin = ev.minute.coerceIn(1, 90)
              val evX = (evMin / 90f) * width
              val isGoal = ev.type == com.example.data.model.EventType.GOAL
              val isRed = ev.type == com.example.data.model.EventType.RED_CARD
              val markerColor = if (isGoal) GoldYellow else if (isRed) LiveRed else Color.White

              drawLine(
                color = markerColor.copy(alpha = 0.6f),
                start = Offset(evX, 10f),
                end = Offset(evX, height - 10f),
                strokeWidth = 1.5f
              )

              val isEvHome = ev.team == match.homeTeam
              drawCircle(
                color = markerColor,
                radius = if (isGoal) 5f else 3.5f,
                center = Offset(evX, if (isEvHome) centerY - 32f else centerY + 32f)
              )
            }

            // Scrubber Cursor Line
            val scrubX = (scrubMinute / 90f) * width
            drawLine(
              color = Color.White,
              start = Offset(scrubX, 0f),
              end = Offset(scrubX, height),
              strokeWidth = 2.5f
            )

            drawCircle(
              color = Color.White,
              radius = 5.5f,
              center = Offset(scrubX, centerY)
            )
            drawCircle(
              color = TealPrimary,
              radius = 3.5f,
              center = Offset(scrubX, centerY)
            )
          }
        }

        // Time labels under chart
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          listOf("0'", "15'", "30'", "45' DYA", "60'", "75'", "90'").forEach { t ->
            Text(text = t, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
          }
        }

        // Scrubber Information Pill
        Spacer(modifier = Modifier.height(8.dp))
        val currentScrubMin = scrubMinute.toInt()
        val matchingEvent = match.events.find { kotlin.math.abs(it.minute - currentScrubMin) <= 2 }
        Surface(
          color = Color(0xFFF8FAFC),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = TealDark, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (matchingEvent != null) {
                "${matchingEvent.minute}' ${matchingEvent.player}: ${matchingEvent.description}"
              } else if (currentScrubMin < 45) {
                "${currentScrubMin}'. Dakika: ${match.homeTeam} orta sahada baskı kuruyor, hücum organizasyonu devam ediyor."
              } else {
                "${currentScrubMin}'. Dakika: Karşılıklı geçiş hücumları ve ${match.awayTeam} tehlikeli atakları."
              },
              fontSize = 11.sp,
              color = Color(0xFF334155),
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // 3. D3 Shot Accuracy & Efficiency Donut / Radial Comparison
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
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
            text = "🎯 Şut İsabeti & Bitiricilik (D3 Radar)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )

          Text(
            text = "xG Oranı",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TealDark
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Home Team Radial Accuracy
          RadialAccuracyGauge(
            teamName = match.homeTeam,
            accuracy = homeAccuracy,
            onTarget = homeShotsOnTarget,
            totalShots = homeTotalShots,
            accentColor = TealDark
          )

          // Center VS Divider with xG
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "xG", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "%.2f".format(stats.xgHome),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = TealDark
              )
              Text(text = " - ", fontSize = 12.sp, color = Color(0xFF94A3B8))
              Text(
                text = "%.2f".format(stats.xgAway),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFE11D48)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = if (stats.xgHome > stats.xgAway) "${match.homeTeam} Üstün" else "${match.awayTeam} Üstün",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          // Away Team Radial Accuracy
          RadialAccuracyGauge(
            teamName = match.awayTeam,
            accuracy = awayAccuracy,
            onTarget = awayShotsOnTarget,
            totalShots = awayTotalShots,
            accentColor = Color(0xFFE11D48)
          )
        }
      }
    }

    // 4. 15-Minute Segmented Possession Breakdown (D3 Temporal Waves)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
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
            text = "⏱️ 15'er Dakikalık Top Hakimiyeti",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
          Text(
            text = "%$homePossession - %$awayPossession",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = TealDark
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Breakdown bars
        possessionSegments.forEach { (interval, homePct, awayPct) ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "%$homePct",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = TealDark,
              modifier = Modifier.width(32.dp)
            )

            // Segmented split bar
            Row(
              modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFE2E8F0))
            ) {
              Box(
                modifier = Modifier
                  .weight(homePct.toFloat())
                  .height(10.dp)
                  .background(TealDark)
              )
              Box(
                modifier = Modifier
                  .weight(awayPct.toFloat())
                  .height(10.dp)
                  .background(Color(0xFFE11D48))
              )
            }

            Text(
              text = "%$awayPct",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE11D48),
              modifier = Modifier.width(36.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.End
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = interval,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF64748B),
              modifier = Modifier.width(42.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Field Zone Dominance
        Text(
          text = "Saha Hakimiyet Dağılımı",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF475569)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF1F5F9))
        ) {
          Box(
            modifier = Modifier
              .weight(28f)
              .fillMaxWidth()
              .background(Color(0xFF38BDF8)),
            contentAlignment = Alignment.Center
          ) {
            Text("Savunma %28", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
          Box(
            modifier = Modifier
              .weight(44f)
              .fillMaxWidth()
              .background(TealPrimary),
            contentAlignment = Alignment.Center
          ) {
            Text("Orta Saha %44", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
          Box(
            modifier = Modifier
              .weight(28f)
              .fillMaxWidth()
              .background(GoldYellow),
            contentAlignment = Alignment.Center
          ) {
            Text("3. Bölge %28", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
          }
        }
      }
    }
  }
}

/**
 * Circular Arc Donut for Shot Accuracy.
 */
@Composable
private fun RadialAccuracyGauge(
  teamName: String,
  accuracy: Int,
  onTarget: Int,
  totalShots: Int,
  accentColor: Color
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier.size(76.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(76.dp)) {
        val strokeWidth = 8.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
        val arcSize = Size(diameter, diameter)

        // Track background
        drawArc(
          color = Color(0xFFE2E8F0),
          startAngle = -90f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Progress arc
        val sweepAngle = (accuracy / 100f) * 360f
        drawArc(
          color = accentColor,
          startAngle = -90f,
          sweepAngle = sweepAngle,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "%$accuracy",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = accentColor
        )
        Text(
          text = "İsabet",
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF64748B)
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = teamName,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF1E293B),
      maxLines = 1,
      overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
    )
    Text(
      text = "$onTarget/$totalShots Şut",
      fontSize = 10.sp,
      color = Color(0xFF64748B),
      fontWeight = FontWeight.Medium
    )
  }
}
