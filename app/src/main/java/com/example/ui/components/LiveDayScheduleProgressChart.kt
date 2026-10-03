package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.model.MatchStatus
import com.example.service.CalendarManagementService
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.delay

@Composable
fun LiveDayScheduleProgressChart(
  matches: List<Match>,
  onAdvanceDay: () -> Unit = {}
) {
  var formattedClock by remember { mutableStateOf(CalendarManagementService.getCurrentFormattedClock()) }
  var formattedDate by remember { mutableStateOf(CalendarManagementService.getCurrentFormattedDateWithDay()) }
  var progressFraction by remember { mutableFloatStateOf(CalendarManagementService.getDayProgressFraction()) }

  // Live ticking updater
  LaunchedEffect(Unit) {
    while (true) {
      formattedClock = CalendarManagementService.getCurrentFormattedClock()
      formattedDate = CalendarManagementService.getCurrentFormattedDateWithDay()
      progressFraction = CalendarManagementService.getDayProgressFraction()
      delay(1000)
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alpha"
  )

  val liveCount = remember(matches) { matches.count { it.status == MatchStatus.LIVE } }
  val finishedCount = remember(matches) { matches.count { it.status == MatchStatus.FINISHED } }
  val upcomingCount = remember(matches) { matches.count { it.status == MatchStatus.SCHEDULED } }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .testTag("live_day_schedule_progress_chart"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    border = BorderStroke(1.dp, Color(0xFF1E293B))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Date & Live Clock
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(TealDark),
            contentAlignment = Alignment.Center
          ) {
            Text("⏱️", fontSize = 16.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = formattedDate,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "TSİ $formattedClock",
                color = GoldYellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• Canlı Zaman Akışı",
                color = Color(0xFF34D399),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E293B),
          border = BorderStroke(1.dp, GoldYellow),
          modifier = Modifier.clickable { onAdvanceDay() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("İlerle ⏩", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 24-Hour Visual Schedule Bar
      Text(
        text = "24 Saatlik Canlı Gün Grafiği & Maç Yoğunluğu",
        color = Color(0xFF94A3B8),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(6.dp))

      BoxWithConstraints(
        modifier = Modifier
          .fillMaxWidth()
          .height(24.dp)
      ) {
        val barWidth = maxWidth

        // Base 24h background gradient
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .align(Alignment.Center)
            .clip(RoundedCornerShape(7.dp))
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0xFF1E293B),
                  Color(0xFF0F766E),
                  Color(0xFFF59E0B),
                  Color(0xFFE11D48),
                  Color(0xFF312E81)
                )
              )
            )
        )

        // Elapsed Progress fill
        Box(
          modifier = Modifier
            .width(barWidth * progressFraction)
            .height(14.dp)
            .align(Alignment.CenterStart)
            .clip(RoundedCornerShape(7.dp))
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0xFF10B981).copy(alpha = 0.5f),
                  Color(0xFF34D399).copy(alpha = 0.9f)
                )
              )
            )
        )

        // Animated current time pulse marker
        Box(
          modifier = Modifier
            .offset(x = (barWidth * progressFraction - 8.dp).coerceAtLeast(0.dp))
            .align(Alignment.CenterStart)
            .size(16.dp)
            .clip(CircleShape)
            .background(GoldYellow.copy(alpha = pulseAlpha))
            .border(2.dp, Color.White, CircleShape)
        )
      }

      // Hour Labels below the bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("00:00 (Gece)", color = Color(0xFF64748B), fontSize = 9.sp)
        Text("06:00 (Sabah)", color = Color(0xFF64748B), fontSize = 9.sp)
        Text("12:00 (Öğlen)", color = Color(0xFF64748B), fontSize = 9.sp)
        Text("18:00 (Derbiler)", color = Color(0xFF64748B), fontSize = 9.sp)
        Text("24:00", color = Color(0xFF64748B), fontSize = 9.sp)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Match Status Summary Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Live matches chip
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E293B),
          border = BorderStroke(1.dp, if (liveCount > 0) LiveRed else Color(0xFF334155)),
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (liveCount > 0) LiveRed else Color(0xFF64748B))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "$liveCount Canlı",
              color = if (liveCount > 0) Color.White else Color(0xFF94A3B8),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Finished matches chip
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E293B),
          border = BorderStroke(1.dp, Color(0xFF334155)),
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text("🏁", fontSize = 10.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "$finishedCount Bitti",
              color = Color(0xFFCBD5E1),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Upcoming matches chip
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E293B),
          border = BorderStroke(1.dp, Color(0xFF334155)),
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text("⏳", fontSize = 10.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "$upcomingCount Bekliyor",
              color = Color(0xFFCBD5E1),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Auto-save & Continuity Footer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("💾", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Oyun verileri ve zaman otomatik kaydediliyor (Room DB)",
            color = Color(0xFF34D399),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
        Text(
          text = "%${(progressFraction * 100).toInt()} Gün Tamamlandı",
          color = GoldYellow,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
