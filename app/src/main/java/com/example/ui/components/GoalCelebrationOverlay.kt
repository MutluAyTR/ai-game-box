package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.delay
import kotlin.random.Random

data class ConfettiParticle(
  val x: Float,
  val y: Float,
  val size: Float,
  val color: Color,
  val speedY: Float,
  val angle: Float
)

@Composable
fun GoalCelebrationOverlay(
  goalMessage: String?,
  couponInfo: com.example.data.model.UserCouponMatchInfo? = null,
  onDismiss: () -> Unit
) {
  val isVisible = goalMessage != null && goalMessage.contains("GOL", ignoreCase = true)

  LaunchedEffect(goalMessage) {
    if (isVisible) {
      delay(4000) // auto dismiss celebration after 4 seconds
      onDismiss()
    }
  }

  AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(tween(250)) + scaleIn(tween(350, easing = FastOutSlowInEasing)),
    exit = fadeOut(tween(300)) + scaleOut(tween(250))
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xCC000000))
        .clickable { onDismiss() }
        .testTag("goal_celebration_overlay"),
      contentAlignment = Alignment.Center
    ) {
      // Dynamic Confetti Canvas
      val infiniteTransition = rememberInfiniteTransition(label = "confetti")
      val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(1200, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        ),
        label = "confettiProg"
      )

      val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
          animation = tween(2000, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        ),
        label = "ballRotate"
      )

      val confettiColors = remember {
        listOf(
          GoldYellow,
          LiveRed,
          Color(0xFF38BDF8),
          Color(0xFF4ADE80),
          Color(0xFFA855F7),
          Color.White
        )
      }

      val particles = remember {
        List(40) {
          ConfettiParticle(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            size = Random.nextFloat() * 8f + 4f,
            color = confettiColors[Random.nextInt(confettiColors.size)],
            speedY = Random.nextFloat() * 0.4f + 0.2f,
            angle = Random.nextFloat() * 360f
          )
        }
      }

      Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
          val currentY = ((p.y + animProgress * p.speedY) % 1f) * size.height
          val currentX = (p.x + kotlin.math.sin(animProgress * 6.28 + p.angle) * 0.05f).toFloat() * size.width
          drawCircle(
            color = p.color,
            radius = p.size,
            center = Offset(currentX, currentY)
          )
        }
      }

      // Center Trophy / Goal Card
      Card(
        modifier = Modifier
          .padding(24.dp)
          .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = TealDark),
        border = androidx.compose.foundation.BorderStroke(2.dp, GoldYellow),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
      ) {
        Column(
          modifier = Modifier
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF0F474A),
                  Color(0xFF062325)
                )
              )
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Spinning Soccer ball
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(GoldYellow),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SportsSoccer,
              contentDescription = "GOL!",
              tint = TealDark,
              modifier = Modifier
                .size(44.dp)
                .rotate(rotationAnim)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Headline banner
          if (couponInfo != null) {
            Surface(
              color = Color(0xFF064E3B),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow)
            ) {
              Text(
                text = "🎯 KUPONUNDAKİ MAÇTA GOL! ⚡",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = GoldYellow,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
          } else {
            Text(
              text = "⚡ GOOOOOOL! ⚡",
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              color = GoldYellow,
              letterSpacing = 1.sp,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Coupon Prediction Box
          if (couponInfo != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF042F2E))
                .border(1.5.dp, Color(0xFF14B8A6), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "KUPON NO: #${couponInfo.ticketNumber}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF99F6E4)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "Senin Tahminin: ${couponInfo.selectionName} (Oran: %.2f)".format(couponInfo.odd),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "🟢 Kuponun Yeşilleniyor! Potansiyel: ${couponInfo.totalPotentialPoints} TP",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldYellow
                )
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Scorer Badge (Dedicated card for the goalscorer)
          val rawMessage = goalMessage ?: ""
          val scorerName = remember(rawMessage) {
            when {
              rawMessage.contains("GOL!") -> {
                val afterGoal = rawMessage.substringAfter("GOL!").trim()
                if (afterGoal.contains(" - ")) afterGoal.substringBefore(" - ").trim() else afterGoal
              }
              rawMessage.contains(":") -> rawMessage.substringAfter(":").trim()
              else -> "Forvet Oyuncusu"
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0A2224))
              .border(1.5.dp, GoldYellow, RoundedCornerShape(12.dp))
              .padding(horizontal = 18.dp, vertical = 8.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "GOLÜ ATAN OYUNCU",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = GoldYellow,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = scorerName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = goalMessage ?: "",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFE2E8F0),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Canlı oranlar ve Poisson olasılıkları anında güncellendi!",
            fontSize = 11.sp,
            color = Color(0xFF80CBC4),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(14.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(GoldYellow)
              .clickable { onDismiss() }
              .padding(horizontal = 20.dp, vertical = 8.dp)
          ) {
            Text(
              text = "Devam Et",
              color = TealDark,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp
            )
          }
        }
      }
    }
  }
}
