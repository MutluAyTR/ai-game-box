package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.delay

@Composable
fun LiveGoalBanner(
  message: String?,
  onDismiss: () -> Unit
) {
  AnimatedVisibility(
    visible = message != null,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
  ) {
    if (message != null) {
      val isGoal = message.contains("GOL", ignoreCase = true)

      val infiniteTransition = rememberInfiniteTransition(label = "pulse")
      val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isGoal) 1.04f else 1.01f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
      )

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .scale(pulseScale)
          .testTag("live_goal_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isGoal) Color(0xFF062325) else Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(
          width = 1.5.dp,
          color = if (isGoal) GoldYellow else Color(0xFF38BDF8)
        )
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                colors = if (isGoal) {
                  listOf(Color(0xFF0F474A), Color(0xFF062224), Color(0xFF881337))
                } else {
                  listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                }
              )
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(if (isGoal) GoldYellow else Color(0xFF0284C7)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isGoal) Icons.Default.SportsSoccer else Icons.Default.NotificationsActive,
                  contentDescription = null,
                  tint = if (isGoal) TealDark else Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (isGoal) "⚡ CANLI MAÇ GELİŞMESİ - GOL!" else "CANLI SKOR BİLDİRİMİ",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = if (isGoal) GoldYellow else Color(0xFF38BDF8),
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = message,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(26.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Kapat",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}
