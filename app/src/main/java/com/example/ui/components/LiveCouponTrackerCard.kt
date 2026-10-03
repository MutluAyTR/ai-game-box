package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary

/**
 * Real-time Live Coupon Tracker Card for HomeScreen.
 * Displays user's active/pending bets with in-play live scores, live bet status,
 * and Instant Cashout (Erken Bahis Bozdur) button.
 */
@Composable
fun LiveCouponTrackerCard(
  tickets: List<Ticket>,
  matches: List<Match>,
  onCashout: (Ticket) -> Unit,
  onNavigateToCoupons: () -> Unit,
  modifier: Modifier = Modifier
) {
  val pendingTickets = remember(tickets) {
    tickets.filter { it.status == TicketStatus.PENDING }
  }

  if (pendingTickets.isEmpty()) return

  val activeTicket = pendingTickets.first()
  val hasLiveMatch = remember(activeTicket.selections, matches) {
    activeTicket.selections.any { sel ->
      matches.any { m ->
        (m.id == sel.matchId || m.homeTeam in sel.matchTeams || m.awayTeam in sel.matchTeams) &&
            m.status == MatchStatus.LIVE
      }
    }
  }

  // Calculate estimated cashout
  val estimatedCashout = remember(activeTicket, matches) {
    var factor = 1.0
    for (sel in activeTicket.selections) {
      val m = matches.firstOrNull { it.id == sel.matchId || it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams }
      if (m?.status == MatchStatus.LIVE) {
        val isWinning = when (sel.marketType) {
          MarketType.MATCH_RESULT -> {
            if (sel.selectionName.contains("1")) m.homeScore > m.awayScore
            else if (sel.selectionName.contains("2")) m.awayScore > m.homeScore
            else m.homeScore == m.awayScore
          }
          MarketType.TOTAL_GOALS_25 -> {
            val total = m.homeScore + m.awayScore
            if (sel.selectionName.contains("Üst", ignoreCase = true)) total >= 3 else total < 3
          }
          else -> true
        }
        factor *= if (isWinning) 0.85 else 0.35
      } else if (m?.status == MatchStatus.FINISHED) {
        factor *= 1.0
      }
    }
    val value = (activeTicket.potentialPoints * factor * 0.85).toLong()
    value.coerceIn((activeTicket.stakePoints * 0.7).toLong(), (activeTicket.potentialPoints * 0.95).toLong())
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("live_coupon_tracker_card"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    border = BorderStroke(1.5.dp, if (hasLiveMatch) Color(0xFF10B981) else Color(0xFF38BDF8)),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row: Header & Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (hasLiveMatch) Color(0xFF10B981) else Color(0xFF38BDF8))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (hasLiveMatch) "🔴 KUPONUMDAKİ CANLI MAÇLAR" else "🎫 AKTİF KUPONUM",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 0.5.sp
          )
        }

        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(0.8.dp, Color(0xFF475569))
        ) {
          Text(
            text = "#${activeTicket.ticketNumber}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Match items in this ticket
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        activeTicket.selections.forEach { sel ->
          val m = matches.firstOrNull {
            it.id == sel.matchId || it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams
          }

          val isWinning = when (sel.marketType) {
            MarketType.MATCH_RESULT -> {
              when {
                sel.selectionName.contains("1") -> (m?.homeScore ?: 0) > (m?.awayScore ?: 0)
                sel.selectionName.contains("2") -> (m?.awayScore ?: 0) > (m?.homeScore ?: 0)
                else -> (m?.homeScore ?: 0) == (m?.awayScore ?: 0)
              }
            }
            MarketType.TOTAL_GOALS_25 -> {
              val tot = (m?.homeScore ?: 0) + (m?.awayScore ?: 0)
              if (sel.selectionName.contains("Üst", ignoreCase = true)) tot >= 3 else tot < 3
            }
            MarketType.BOTH_TEAMS_SCORE -> {
              val both = (m?.homeScore ?: 0) > 0 && (m?.awayScore ?: 0) > 0
              if (sel.selectionName.contains("Var", ignoreCase = true)) both else true
            }
            else -> true
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF1E293B))
              .padding(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = sel.matchTeams,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Seçim: ${sel.selectionName} (Oran: %.2f)".format(sel.odd),
                    fontSize = 11.sp,
                    color = GoldYellow,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              // Match State / Live score
              if (m != null) {
                when (m.status) {
                  MatchStatus.LIVE -> {
                    Column(horizontalAlignment = Alignment.End) {
                      Surface(
                        color = Color(0xFFDC2626),
                        shape = RoundedCornerShape(4.dp)
                      ) {
                        Text(
                          text = "${m.minute}' • ${m.homeScore} - ${m.awayScore}",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Black,
                          color = Color.White,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = if (isWinning) "🟢 Önde" else "🔴 Riskte",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWinning) Color(0xFF4ADE80) else Color(0xFFF87171)
                      )
                    }
                  }
                  MatchStatus.FINISHED -> {
                    Surface(
                      color = if (isWinning) Color(0xFF065F46) else Color(0xFF991B1B),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "MS ${m.homeScore}-${m.awayScore} ${if (isWinning) "✓" else "✗"}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  else -> {
                    Text(
                      text = "⏱️ ${m.startTime}",
                      fontSize = 10.sp,
                      color = Color(0xFF94A3B8)
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row: Stake, Potential & Cashout Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          Text(
            text = "Yatırılan: ${activeTicket.stakePoints} TP • Oran: %.2f".format(activeTicket.totalOdds),
            fontSize = 10.sp,
            color = Color(0xFF94A3B8),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Potansiyel: ${activeTicket.potentialPoints} TP",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = GoldYellow,
            maxLines = 1,
            softWrap = false
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Erken Bahis Bozdur (Cashout) Button
          Button(
            onClick = { onCashout(activeTicket) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("cashout_button")
          ) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldYellow)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Bozdur: $estimatedCashout TP",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
