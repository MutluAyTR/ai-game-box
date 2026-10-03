package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class WheelPrize(
  val amount: Long,
  val label: String,
  val color: Color,
  val isJackpot: Boolean = false
)

val WHEEL_PRIZES = listOf(
  WheelPrize(50L, "50 TP", Color(0xFF0D9488)),
  WheelPrize(250L, "250 TP", Color(0xFFF59E0B)),
  WheelPrize(100L, "100 TP", Color(0xFF3B82F6)),
  WheelPrize(500L, "500 TP", Color(0xFF10B981)),
  WheelPrize(50L, "50 TP", Color(0xFF6366F1)),
  WheelPrize(1000L, "1000 TP", Color(0xFFEC4899)),
  WheelPrize(100L, "100 TP", Color(0xFF8B5CF6)),
  WheelPrize(2500L, "🔥 2500 TP", Color(0xFFE11D48), isJackpot = true)
)

@Composable
fun LuckyWheelDialog(
  onDismiss: () -> Unit,
  onReward: (Long) -> Unit
) {
  val scope = rememberCoroutineScope()
  val rotation = remember { Animatable(0f) }
  var isSpinning by remember { mutableStateOf(false) }
  var wonPrize by remember { mutableStateOf<WheelPrize?>(null) }
  var hasSpunToday by remember { mutableStateOf(false) }

  val segmentAngle = 360f / WHEEL_PRIZES.size

  fun spinWheel() {
    if (isSpinning) return
    isSpinning = true
    wonPrize = null

    // Pick random winning index
    val prizeIndex = Random.nextInt(WHEEL_PRIZES.size)
    val targetPrize = WHEEL_PRIZES[prizeIndex]

    // Calculate rotation to land top arrow (at 270 degrees) on prize
    val extraSpins = 5 * 360f
    // Each segment i is at [i * segmentAngle, (i+1) * segmentAngle]
    // To land at top (270 degrees), rotation needed:
    val targetSegmentCenter = prizeIndex * segmentAngle + segmentAngle / 2f
    val finalTarget = extraSpins + (360f - targetSegmentCenter + 270f) % 360f

    scope.launch {
      rotation.snapTo(0f)
      rotation.animateTo(
        targetValue = finalTarget + 360f * 3, // multiple full spins
        animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
      )
      isSpinning = false
      wonPrize = targetPrize
      hasSpunToday = true
      onReward(targetPrize.amount)
    }
  }

  AlertDialog(
    onDismissRequest = {
      if (!isSpinning) onDismiss()
    },
    title = null,
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🍀", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Şans Çarkı",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = TealDark
              )
              Text(
                text = "Günlük Çevir & Ücretsiz TP Kazan!",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }
          }
          IconButton(
            onClick = { if (!isSpinning) onDismiss() },
            enabled = !isSpinning,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wheel Container with Top Pointer
        Box(
          modifier = Modifier
            .size(260.dp)
            .testTag("lucky_wheel_canvas_box"),
          contentAlignment = Alignment.Center
        ) {
          // Canvas Wheel
          Canvas(
            modifier = Modifier
              .size(240.dp)
              .rotate(rotation.value)
          ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            WHEEL_PRIZES.forEachIndexed { index, prize ->
              val startAngle = index * segmentAngle
              drawArc(
                color = prize.color,
                startAngle = startAngle,
                sweepAngle = segmentAngle,
                useCenter = true,
                size = Size(canvasSize, canvasSize),
                topLeft = Offset(center.x - radius, center.y - radius)
              )

              // Text on segment
              val textAngleRad = Math.toRadians((startAngle + segmentAngle / 2f).toDouble())
              val textRadius = radius * 0.65f
              val textX = center.x + (textRadius * cos(textAngleRad)).toFloat()
              val textY = center.y + (textRadius * sin(textAngleRad)).toFloat()

              drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                  color = android.graphics.Color.WHITE
                  textSize = 28f
                  typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                  textAlign = android.graphics.Paint.Align.CENTER
                }
                save()
                rotate((startAngle + segmentAngle / 2f + 90f), textX, textY)
                drawText(prize.label, textX, textY, paint)
                restore()
              }
            }

            // Outer border
            drawCircle(
              color = GoldYellow,
              radius = radius,
              center = center,
              style = Stroke(width = 8f)
            )

            // Center hub
            drawCircle(
              color = Color.White,
              radius = 24f,
              center = center
            )
            drawCircle(
              color = TealDark,
              radius = 18f,
              center = center
            )
          }

          // Top Pointer Arrow (🔻)
          Box(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .offset(y = (-8).dp)
          ) {
            Surface(
              color = Color(0xFFDC2626),
              shape = RoundedCornerShape(4.dp),
              shadowElevation = 6.dp,
              modifier = Modifier.size(width = 20.dp, height = 24.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = "▼", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prize Announcement or Status
        if (wonPrize != null) {
          Surface(
            color = Color(0xFFDCFCE7),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "🎉 TEBRİKLER!",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color(0xFF15803D)
              )
              Text(
                text = "+${wonPrize?.amount} TP Kazandınız!",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF166534)
              )
              Text(
                text = "Ödülünüz doğrudan cüzdan bakiyenize tanımlandı.",
                fontSize = 11.sp,
                color = Color(0xFF14532D),
                textAlign = TextAlign.Center
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Action Button
        Button(
          onClick = { spinWheel() },
          enabled = !isSpinning,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("spin_wheel_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = GoldYellow,
            contentColor = TealDark,
            disabledContainerColor = Color(0xFFE2E8F0),
            disabledContentColor = Color(0xFF94A3B8)
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = if (isSpinning) Icons.Default.RestartAlt else Icons.Default.EmojiEvents,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = when {
                isSpinning -> "Çark Dönüyor..."
                wonPrize != null -> "Tekrar Çevir (100 TP)"
                else -> "ÜCRETSİZ ÇEVİR!"
              },
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {}
  )
}
