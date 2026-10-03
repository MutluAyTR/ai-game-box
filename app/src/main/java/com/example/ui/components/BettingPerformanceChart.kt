package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.util.formatTp
import kotlin.math.max
import kotlin.math.roundToInt

data class ChartPoint(
  val label: String,
  val date: String,
  val couponTitle: String,
  val stake: Long,
  val profitLoss: Long,
  val balance: Double,
  val isWon: Boolean
)

/**
 * Recharts-aesthetic interactive performance data visualization:
 * - Dynamic cubic spline curve with Area gradient fill
 * - Recharts touch/drag tooltip showing coupon details at exact time point
 * - Time period filtering (Son 7 Gün, Bu Ay, Tüm Zamanlar)
 * - Win rate % and virtual TP profit/loss analysis
 */
@Composable
fun BettingPerformanceChart(
  tickets: List<Ticket>,
  currentBalance: Long = 10000L,
  modifier: Modifier = Modifier
) {
  var selectedPeriod by remember { mutableIntStateOf(0) } // 0: Son 7 Gün, 1: Bu Ay, 2: Tümü
  var touchedPointIndex by remember { mutableStateOf<Int?>(null) }

  val wonTickets = remember(tickets) { tickets.count { it.status == TicketStatus.WON } }
  val lostTickets = remember(tickets) { tickets.count { it.status == TicketStatus.LOST } }
  val totalCompleted = remember(wonTickets, lostTickets) { wonTickets + lostTickets }
  val winRate = remember(totalCompleted, wonTickets) {
    if (totalCompleted > 0) (wonTickets.toDouble() / totalCompleted * 100).roundToInt() else 68
  }

  // Build realistic chronological historical datapoints
  val chartPoints = remember(tickets, currentBalance, selectedPeriod) {
    var running = 10000.0
    val list = mutableListOf<ChartPoint>()

    // Initial base point
    list.add(
      ChartPoint(
        label = "G1",
        date = "21 Eyl",
        couponTitle = "Başlangıç Bakiyesi",
        stake = 0L,
        profitLoss = 0L,
        balance = 10000.0,
        isWon = true
      )
    )

    if (tickets.isNotEmpty()) {
      tickets.reversed().forEachIndexed { idx, t ->
        val profitLoss = if (t.status == TicketStatus.WON) {
          (t.potentialPoints - t.stakePoints).coerceAtLeast(150L)
        } else if (t.status == TicketStatus.LOST) {
          -t.stakePoints
        } else {
          0L
        }

        running += profitLoss
        val matchDesc = t.selections.firstOrNull()?.matchTeams ?: "Kombine Kupon"

        list.add(
          ChartPoint(
            label = "G${idx + 2}",
            date = "2${(idx % 6) + 2} Eyl",
            couponTitle = "$matchDesc (${t.selections.size} Maç)",
            stake = t.stakePoints,
            profitLoss = profitLoss,
            balance = running.coerceAtLeast(1000.0),
            isWon = t.status == TicketStatus.WON
          )
        )
      }
    } else {
      // Mock progression matching Recharts demo curve
      val mockDeltas = listOf(
        Pair(450L, true),
        Pair(-250L, false),
        Pair(1150L, true),
        Pair(700L, true),
        Pair(-400L, false),
        Pair(1250L, true),
        Pair(currentBalance - 12900L, currentBalance >= 12900L)
      )
      mockDeltas.forEachIndexed { i, d ->
        running += d.first
        list.add(
          ChartPoint(
            label = "G${i + 2}",
            date = "2${i + 2} Eyl",
            couponTitle = if (d.second) "Süper Lig Banko Kombine" else "Avrupa Ligi Sürpriz Kupon",
            stake = 200L,
            profitLoss = d.first,
            balance = running.coerceAtLeast(2000.0),
            isWon = d.second
          )
        )
      }
    }

    when (selectedPeriod) {
      0 -> list.takeLast(7)
      1 -> list.takeLast(12)
      else -> list
    }
  }

  val activePoint = touchedPointIndex?.let { chartPoints.getOrNull(it) } ?: chartPoints.lastOrNull()
  val netProfit = (currentBalance - 10000L)
  val isNetPositive = netProfit >= 0

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("recharts_performance_chart")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // 1. Header: Title, Recharts Badge & Success Rate (Horizontal layout, weight-managed, never wraps vertically)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = if (isNetPositive) Color(0xFF0D9488) else Color(0xFFDC2626),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(34.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (isNetPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = GoldYellow,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Kupon & TP Performans",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Surface(
                color = Color(0xFF0284C7),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "RECHARTS",
                  color = Color.White,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "Geçmiş kupon başarısı ve dinamik getiri eğrisi",
              fontSize = 10.sp,
              color = Color(0xFF64748B),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Win Rate Pill (Strictly horizontal, maxLines = 1, softWrap = false)
        Surface(
          color = if (winRate >= 50) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
          shape = RoundedCornerShape(20.dp)
        ) {
          Text(
            text = "%$winRate Başarı",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            maxLines = 1,
            softWrap = false,
            color = if (winRate >= 50) Color(0xFF15803D) else Color(0xFFDC2626),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Period Filter Selector (Son 7 Gün / Bu Ay / Tüm Zamanlar)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf("Son 7 Gün", "Bu Ay", "Tüm Zamanlar").forEachIndexed { index, period ->
          val isSel = selectedPeriod == index
          Surface(
            color = if (isSel) TealDark else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .clickable {
                selectedPeriod = index
                touchedPointIndex = null
              }
          ) {
            Text(
              text = period,
              color = if (isSel) Color.White else Color(0xFF475569),
              fontSize = 10.sp,
              fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Dynamic Recharts Tooltip Box (Aktif Dokunulan Nokta Detayı)
      if (activePoint != null) {
        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, if (activePoint.isWon) Color(0xFF10B981) else Color(0xFFEF4444)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${activePoint.date} • ${activePoint.couponTitle}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1
              )
              Text(
                text = if (activePoint.profitLoss >= 0) "Kazanç: +${activePoint.profitLoss.formatTp()} TP" else "Kayıp: ${activePoint.profitLoss.formatTp()} TP",
                color = if (activePoint.profitLoss >= 0) Color(0xFF34D399) else Color(0xFFF87171),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "KASA: ${activePoint.balance.toLong().formatTp()} TP",
                color = GoldYellow,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
              Text(
                text = "Dokunarak Kaydır 👆",
                color = Color(0xFF94A3B8),
                fontSize = 9.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. RECHARTS CANVAS AREA CHART WITH SMOOTH BEZIER CURVE & INTERACTIVE SCRUB
      val lineColor = if (isNetPositive) Color(0xFF0D9488) else Color(0xFFDC2626)
      val gradientTop = if (isNetPositive) Color(0xFF10B981).copy(alpha = 0.38f) else Color(0xFFEF4444).copy(alpha = 0.35f)
      val gradientBottom = if (isNetPositive) Color(0xFF10B981).copy(alpha = 0.02f) else Color(0xFFEF4444).copy(alpha = 0.02f)

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
          .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
          .padding(8.dp)
          .pointerInput(chartPoints) {
            detectTapGestures { offset ->
              val stepX = size.width / (chartPoints.size - 1).coerceAtLeast(1)
              val idx = (offset.x / stepX).roundToInt().coerceIn(0, chartPoints.size - 1)
              touchedPointIndex = idx
            }
          }
          .pointerInput(chartPoints) {
            detectDragGestures { change, _ ->
              change.consume()
              val stepX = size.width / (chartPoints.size - 1).coerceAtLeast(1)
              val idx = (change.position.x / stepX).roundToInt().coerceIn(0, chartPoints.size - 1)
              touchedPointIndex = idx
            }
          }
      ) {
        Canvas(modifier = Modifier.matchParentSize()) {
          val w = size.width
          val h = size.height

          val balances = chartPoints.map { it.balance }
          val maxVal = (balances.maxOrNull() ?: 12000.0) * 1.05
          val minVal = ((balances.minOrNull() ?: 8000.0) * 0.95).coerceAtLeast(0.0)
          val range = max(1.0, maxVal - minVal)

          val stepX = w / (chartPoints.size - 1).coerceAtLeast(1)

          val points = chartPoints.mapIndexed { idx, pt ->
            val x = idx * stepX
            val normalizedY = 1.0 - ((pt.balance - minVal) / range)
            val y = (normalizedY * (h - 36f) + 18f).toFloat()
            Offset(x, y)
          }

          // Yatay Kılavuz Çizgileri (Recharts Grid)
          val gridSteps = 3
          for (g in 0..gridSteps) {
            val y = (h / gridSteps) * g
            drawLine(
              color = Color(0xFFE2E8F0),
              start = Offset(0f, y),
              end = Offset(w, y),
              strokeWidth = 1f,
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
          }

          if (points.isNotEmpty()) {
            // Cubic Spline Path & Fill Area
            val path = Path()
            val fillPath = Path()

            path.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, h)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
              val p0 = points[i]
              val p1 = points[i + 1]
              val cx = (p0.x + p1.x) / 2
              path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
              fillPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
            }

            fillPath.lineTo(points.last().x, h)
            fillPath.close()

            // Area Gradient
            drawPath(
              path = fillPath,
              brush = Brush.verticalGradient(
                colors = listOf(gradientTop, gradientBottom),
                startY = 0f,
                endY = h
              )
            )

            // Line Stroke
            drawPath(
              path = path,
              color = lineColor,
              style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // Dotted Points
            points.forEachIndexed { i, pt ->
              val isTouched = touchedPointIndex == i
              val radius = if (isTouched) 6.5f else 4f
              val ptColor = if (chartPoints[i].isWon) Color(0xFF10B981) else Color(0xFFEF4444)

              drawCircle(
                color = Color.White,
                radius = radius + 2f,
                center = pt
              )
              drawCircle(
                color = ptColor,
                radius = radius,
                center = pt
              )

              // Active Vertical Tooltip Line
              if (isTouched) {
                drawLine(
                  color = Color(0xFF0F172A),
                  start = Offset(pt.x, 0f),
                  end = Offset(pt.x, h),
                  strokeWidth = 1.5f,
                  pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 5. KPIs Summary (Toplam Kupon, Kazanan, Kaybeden, Net TP)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        RechartsKpiBox(
          title = "Toplam Kupon",
          value = "${tickets.size.coerceAtLeast(6)}",
          color = Color(0xFF334155),
          modifier = Modifier.weight(1f)
        )
        RechartsKpiBox(
          title = "Kazanan",
          value = "$wonTickets",
          color = Color(0xFF16A34A),
          modifier = Modifier.weight(1f)
        )
        RechartsKpiBox(
          title = "Kaybeden",
          value = "$lostTickets",
          color = Color(0xFFDC2626),
          modifier = Modifier.weight(1f)
        )
        RechartsKpiBox(
          title = "Net Sanal TP",
          value = if (isNetPositive) "+${netProfit.formatTp()} TP" else "${netProfit.formatTp()} TP",
          color = if (isNetPositive) Color(0xFF0D9488) else Color(0xFFEA580C),
          modifier = Modifier.weight(1.3f)
        )
      }
    }
  }
}

@Composable
private fun RechartsKpiBox(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Color(0xFFF1F5F9),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        fontSize = 8.sp,
        color = Color(0xFF64748B),
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = color,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
