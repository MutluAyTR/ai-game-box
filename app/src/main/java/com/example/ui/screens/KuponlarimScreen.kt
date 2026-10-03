package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.util.CouponDeepLinkManager
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.MarketType
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuponlarimScreen(
  tickets: List<Ticket>,
  matches: List<Match> = emptyList(),
  onShareToKupondas: (Ticket) -> Unit = {},
  onReplayTicket: (Ticket) -> Unit = {},
  onCashoutTicket: (Ticket) -> Unit = {},
  onOpenDetailedBetHistory: () -> Unit = {}
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var visualizerTicket by remember { mutableStateOf<Ticket?>(null) }

  val filteredTickets = when (selectedTab) {
    1 -> tickets.filter { it.status == TicketStatus.PENDING }
    2 -> tickets.filter { it.status == TicketStatus.WON }
    3 -> tickets.filter { it.status == TicketStatus.LOST }
    else -> tickets
  }

  // Summary Metrics
  val totalWonTickets = remember(tickets) { tickets.count { it.status == TicketStatus.WON } }
  val totalLostTickets = remember(tickets) { tickets.count { it.status == TicketStatus.LOST } }
  val totalPendingTickets = remember(tickets) { tickets.count { it.status == TicketStatus.PENDING } }
  val totalWonPoints = remember(tickets) {
    tickets.filter { it.status == TicketStatus.WON }.sumOf { it.potentialPoints }
  }
  val totalLostPoints = remember(tickets) {
    tickets.filter { it.status == TicketStatus.LOST }.sumOf { it.stakePoints }
  }
  val netProfit = totalWonPoints - totalLostPoints
  val winRate = if (totalWonTickets + totalLostTickets > 0) {
    (totalWonTickets * 100.0 / (totalWonTickets + totalLostTickets)).toInt()
  } else 0

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("kuponlarim_screen")
  ) {
    // 1. Header Banner
    item {
      Surface(
        color = TealDark,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Text(
            text = "DETAYLI BAHİS GEÇMİŞİ & KUPONLARIM",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = Color.White
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Tüm sanal TP bahislerinizin kazanan (yeşil) ve kaybeden (kırmızı) sonuçları.",
            fontSize = 11.sp,
            color = Color(0xFFB0BEC5)
          )
        }
      }
    }

    // 2. Performance Summary KPIs Card (Yeşil Kazanç / Kırmızı Kayıp)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "📊 SANAL TP PERFORMANS RAPORU",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF334155)
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Kazanan TP (Yeşil Kutu)
            Surface(
              modifier = Modifier.weight(1f),
              color = Color(0xFFECFDF5),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, Color(0xFFA7F3D0))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "KAZANILAN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "+$totalWonPoints TP",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF047857)
                )
                Text(
                  text = "$totalWonTickets Kupon Kazandı",
                  fontSize = 10.sp,
                  color = Color(0xFF065F46)
                )
              }
            }

            // Kaybedilen TP (Kırmızı Kutu)
            Surface(
              modifier = Modifier.weight(1f),
              color = Color(0xFFFEF2F2),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, Color(0xFFFECACA))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "KAYBEDİLEN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "-$totalLostPoints TP",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFB91C1C)
                )
                Text(
                  text = "$totalLostTickets Kupon Kaybetti",
                  fontSize = 10.sp,
                  color = Color(0xFF991B1B)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Net Kar / Zarar & Başarı Oranı
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Net Kar / Zarar:", fontSize = 11.sp, color = Color(0xFF64748B), maxLines = 1, softWrap = false)
              Text(
                text = "${if (netProfit >= 0) "+" else ""}$netProfit TP",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (netProfit >= 0) Color(0xFF059669) else Color(0xFFDC2626),
                maxLines = 1,
                softWrap = false
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(text = "Başarı Oranı:", fontSize = 11.sp, color = Color(0xFF64748B), maxLines = 1, softWrap = false)
              Text(
                text = "%$winRate İsabet",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = TealDark,
                maxLines = 1,
                softWrap = false
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = onOpenDetailedBetHistory,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("open_bet_history_detail_button"),
            colors = ButtonDefaults.buttonColors(containerColor = TealDark),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.HourglassTop,
              contentDescription = null,
              tint = GoldYellow,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Detaylı Bahis Geçmişi Tablosu (Bet History)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    // 2.5 Recharts Dinamik Kupon Başarısı & TP Performans Grafiği
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        com.example.ui.components.BettingPerformanceChart(
          tickets = tickets,
          currentBalance = (10000L + netProfit).coerceAtLeast(0L)
        )
      }
    }

    // 3. Tab Filter Bar
    item {
      SecondaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Tümü (${tickets.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              "Devam Eden ($totalPendingTickets)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E40AF)
            )
          }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Text(
              "✓ Kazanan ($totalWonTickets)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF059669)
            )
          }
        )
        Tab(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          text = {
            Text(
              "✗ Kaybeden ($totalLostTickets)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFDC2626)
            )
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
    }

    // 4. Ticket Items or Empty
    if (filteredTickets.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🧾", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = when (selectedTab) {
                1 -> "Devam eden kuponunuz bulunmuyor."
                2 -> "Henüz kazanan bir kuponunuz bulunmuyor."
                3 -> "Kaybeden kuponunuz bulunmuyor."
                else -> "Henüz sanal kupon oynamadınız."
              },
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF64748B),
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Bültenden oranlara tıklayarak hemen ilk kuponunuzu oluşturabilirsiniz.",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }
      }
    } else {
      items(filteredTickets, key = { it.id }) { ticket ->
        DetailedTicketCard(
          ticket = ticket,
          matches = matches,
          onShare = { onShareToKupondas(ticket) },
          onReplay = { onReplayTicket(ticket) },
          onCashout = { onCashoutTicket(ticket) },
          onOpenVisualizer = { visualizerTicket = ticket }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Kupon Görselleştirici & Sosyal Medya Paylaşım Modülü
  if (visualizerTicket != null) {
    com.example.ui.components.CouponVisualizerDialog(
      ticket = visualizerTicket,
      onShareToKupondas = onShareToKupondas,
      onDismiss = { visualizerTicket = null }
    )
  }
}

@Composable
private fun DetailedTicketCard(
  ticket: Ticket,
  matches: List<Match>,
  onShare: () -> Unit,
  onReplay: () -> Unit,
  onCashout: () -> Unit = {},
  onOpenVisualizer: () -> Unit = {}
) {
  val dateStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(ticket.createdAt))

  // Check if any match in this ticket is actively LIVE right now
  val hasLiveMatches = remember(ticket.selections, matches) {
    ticket.selections.any { sel ->
      val m = matches.firstOrNull { it.id == sel.matchId || it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams }
      m?.status == MatchStatus.LIVE
    }
  }

  // Color Coding as requested: Won = Green, Lost = Red, Pending = Blue/Gray
  val (cardBorderColor, cardBgColor, badgeBg, badgeTextColor, statusLabel, statusIcon) = when {
    ticket.status == TicketStatus.WON -> Tuple6(
      Color(0xFF10B981), // Green Border
      Color(0xFFF0FDF4), // Light Green Tint
      Color(0xFFDCFCE7),
      Color(0xFF047857),
      "🎉 KAZANDI (+${ticket.potentialPoints} TP)",
      Icons.Default.CheckCircle
    )
    ticket.status == TicketStatus.LOST -> Tuple6(
      Color(0xFFEF4444), // Red Border
      Color(0xFFFEF2F2), // Light Red Tint
      Color(0xFFFEE2E2),
      Color(0xFFB91C1C),
      "❌ KAYBETTİ (-${ticket.stakePoints} TP)",
      Icons.AutoMirrored.Filled.TrendingDown
    )
    hasLiveMatches -> Tuple6(
      Color(0xFFE11D48), // Live Rose Red Border
      Color(0xFFFFF1F2),
      Color(0xFFFFE4E6),
      Color(0xFFBE123C),
      "🔴 CANLI SKOR TAKİBİ",
      Icons.Default.HourglassTop
    )
    else -> Tuple6(
      Color(0xFF38BDF8),
      Color.White,
      Color(0xFFE0F2FE),
      Color(0xFF0369A1),
      "⏳ DEVAM EDİYOR",
      Icons.Default.HourglassTop
    )
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = cardBgColor),
    border = BorderStroke(1.5.dp, cardBorderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Ticket number, date, and colored status banner (Horizontal, no vertical squishing)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(cardBorderColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = ticket.ticketNumber,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp,
              color = Color(0xFF0F172A),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Text(
            text = "$dateStr • ${ticket.type.label}",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
          color = badgeBg,
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(0.8.dp, cardBorderColor.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = statusIcon,
              contentDescription = null,
              tint = badgeTextColor,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = statusLabel,
              color = badgeTextColor,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp,
              maxLines = 1,
              softWrap = false
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Selections list with real-time live match results, scores, and status
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White, RoundedCornerShape(8.dp))
          .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ticket.selections.forEach { sel ->
          val match = matches.firstOrNull { it.id == sel.matchId }
            ?: matches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && (it.status == MatchStatus.SCHEDULED || it.status == MatchStatus.UPCOMING || it.status == MatchStatus.LIVE) }
            ?: matches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && it.status != MatchStatus.FINISHED }
            ?: matches.firstOrNull { it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                if (match?.status == MatchStatus.LIVE) Color(0xFFFFFBEB) else Color.Transparent,
                RoundedCornerShape(6.dp)
              )
              .padding(4.dp)
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
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF1E293B),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${sel.marketType.displayName}: ${sel.selectionName}",
                  fontSize = 11.sp,
                  color = Color(0xFF64748B),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "%.2f".format(sel.odd),
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp,
                  color = TealDark,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            // Real-Time Live Score Bar for each match
            if (match != null) {
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                when (match.status) {
                  MatchStatus.LIVE -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Surface(
                        color = Color(0xFFDC2626),
                        shape = RoundedCornerShape(4.dp)
                      ) {
                        Text(
                          text = "CANLI ${match.minute}'",
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Black,
                          color = Color.White,
                          modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Skor: ${match.homeScore} - ${match.awayScore}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                      )
                    }

                    // Live evaluation badge
                    val isFavorable = when {
                      sel.selectionName.contains("MS 1") && match.homeScore > match.awayScore -> true
                      sel.selectionName.contains("MS 2") && match.awayScore > match.homeScore -> true
                      sel.selectionName.contains("MS X") && match.homeScore == match.awayScore -> true
                      sel.selectionName.contains("2.5 Üst") && (match.homeScore + match.awayScore >= 3) -> true
                      sel.selectionName.contains("KG Var") && match.homeScore > 0 && match.awayScore > 0 -> true
                      else -> false
                    }

                    Surface(
                      color = if (isFavorable) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = if (isFavorable) "🟢 Tahmin Tutuyor" else "🟡 Devam Ediyor",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFavorable) Color(0xFF047857) else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }

                  MatchStatus.FINISHED -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Surface(
                        color = Color(0xFF475569),
                        shape = RoundedCornerShape(4.dp)
                      ) {
                        Text(
                          text = "MS (BİTTİ)",
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White,
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Sonuç: ${match.homeScore} - ${match.awayScore}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                      )
                    }
                  }

                  MatchStatus.HALFTIME -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Surface(
                        color = Color(0xFFEAB308),
                        shape = RoundedCornerShape(4.dp)
                      ) {
                        Text(
                          text = "İY",
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Black,
                          color = Color.White,
                          modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Skor: ${match.homeScore} - ${match.awayScore}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                      )
                    }
                  }

                  MatchStatus.UPCOMING, MatchStatus.SCHEDULED -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "⏱️ Başlamadı (Saat: ${match.startTime})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0369A1)
                      )
                    }
                  }

                  else -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "⏱️ ${match.startTime}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                      )
                    }
                  }
                }
              }
            } else {
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "⏱️ Başlamadı (Bülten Bekleniyor)",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF64748B)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Stake, Odds, Potential Winnings (No vertical wrapping)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "Yatırılan:", fontSize = 10.sp, color = Color(0xFF64748B), maxLines = 1, softWrap = false)
          Text(
            text = "${ticket.stakePoints} TP",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF0F172A),
            maxLines = 1,
            softWrap = false
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Toplam Oran:", fontSize = 10.sp, color = Color(0xFF64748B), maxLines = 1, softWrap = false)
          Text(
            text = "%.2f".format(ticket.totalOdds),
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = TealDark,
            maxLines = 1,
            softWrap = false
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = if (ticket.status == TicketStatus.WON) "Kazanılan Ödül:" else "Potansiyel Kazanç:",
            fontSize = 10.sp,
            color = Color(0xFF64748B),
            maxLines = 1,
            softWrap = false
          )
          Text(
            text = "${ticket.potentialPoints} TP",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            color = if (ticket.status == TicketStatus.WON) Color(0xFF047857) else Color(0xFF0F172A),
            maxLines = 1,
            softWrap = false
          )
        }
      }

      // Live Cashout Option for Pending Tickets
      if (ticket.status == TicketStatus.PENDING) {
        val estimatedCashout = remember(ticket, matches) {
          var anyLive = false
          var anyLost = false
          var allScheduled = true
          var liveScoreRatioSum = 0.0

          for (sel in ticket.selections) {
            val m = matches.firstOrNull { it.id == sel.matchId }
              ?: matches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && (it.status == MatchStatus.SCHEDULED || it.status == MatchStatus.UPCOMING || it.status == MatchStatus.LIVE) }
              ?: matches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && it.status != MatchStatus.FINISHED }
              ?: matches.firstOrNull { it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams }

            if (m == null || m.status == MatchStatus.SCHEDULED || m.status == MatchStatus.UPCOMING) {
              liveScoreRatioSum += 0.50
              continue
            }

            allScheduled = false

            if (m.status == MatchStatus.FINISHED) {
              val won = when (sel.marketType) {
                MarketType.MATCH_RESULT, MarketType.BASKETBALL_MS, MarketType.MOTORSPORTS_WINNER -> {
                  when {
                    sel.selectionName.contains("1") -> m.homeScore > m.awayScore
                    sel.selectionName.contains("2") -> m.awayScore > m.homeScore
                    else -> m.homeScore == m.awayScore
                  }
                }
                MarketType.TOTAL_GOALS_25 -> {
                  val total = m.homeScore + m.awayScore
                  if (sel.selectionName.contains("Üst", ignoreCase = true)) total > 2 else total < 3
                }
                MarketType.BOTH_TEAMS_SCORE -> {
                  val both = m.homeScore > 0 && m.awayScore > 0
                  if (sel.selectionName.contains("Var", ignoreCase = true)) both else !both
                }
                else -> true
              }
              if (!won) {
                anyLost = true
                break
              } else {
                liveScoreRatioSum += 1.0
              }
              continue
            }

            // LIVE in-play
            anyLive = true
            val isWinningLive = when (sel.marketType) {
              MarketType.MATCH_RESULT, MarketType.BASKETBALL_MS, MarketType.MOTORSPORTS_WINNER -> {
                when {
                  sel.selectionName.contains("1") -> m.homeScore > m.awayScore
                  sel.selectionName.contains("2") -> m.awayScore > m.homeScore
                  else -> m.homeScore == m.awayScore
                }
              }
              MarketType.TOTAL_GOALS_25 -> {
                val total = m.homeScore + m.awayScore
                if (sel.selectionName.contains("Üst", ignoreCase = true)) total >= 3 else total < 3
              }
              MarketType.BOTH_TEAMS_SCORE -> {
                val both = m.homeScore > 0 && m.awayScore > 0
                if (sel.selectionName.contains("Var", ignoreCase = true)) both else true
              }
              else -> true
            }

            val isDrawing = m.homeScore == m.awayScore
            val maxMin = when (m.sport) {
              com.example.data.model.Sport.BASKETBALL -> 40.0
              com.example.data.model.Sport.MOTORSPORTS -> 25.0
              else -> 90.0
            }
            val minuteProgress = (m.minute.coerceIn(1, maxMin.toInt()) / maxMin).coerceIn(0.05, 1.0)

            val matchFactor = if (isWinningLive) {
              0.55 + (minuteProgress * 0.40)
            } else if (isDrawing && (sel.selectionName.contains("1") || sel.selectionName.contains("2"))) {
              (0.50 - (minuteProgress * 0.28)).coerceAtLeast(0.12)
            } else {
              (0.38 - (minuteProgress * 0.30)).coerceAtLeast(0.06)
            }
            liveScoreRatioSum += matchFactor
          }

          if (anyLost) {
            0L
          } else if (allScheduled && !anyLive) {
            // Kural 1: Maç başlamadan önce yatırılanın yarısı
            (ticket.stakePoints / 2L).coerceAtLeast(1L)
          } else {
            // Kural 2: Maç başladığında duruma göre otomatik artıp azalacak
            val avg = (liveScoreRatioSum / ticket.selections.size.toDouble()).coerceIn(0.05, 1.0)
            val calculated = if (avg >= 0.50) {
              val prog = (avg - 0.50) / 0.50
              val half = ticket.stakePoints / 2.0
              val maxP = ticket.potentialPoints * 0.95
              (half + (maxP - half) * prog).toLong()
            } else {
              val prog = (0.50 - avg) / 0.50
              val half = ticket.stakePoints / 2.0
              val minP = (ticket.stakePoints * 0.10).coerceAtLeast(1.0)
              (half - (half - minP) * prog).toLong()
            }
            calculated.coerceIn(
              (ticket.stakePoints * 0.05).toLong().coerceAtLeast(1L),
              (ticket.potentialPoints * 0.98).toLong()
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = onCashout,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
        ) {
          Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "💰 Bahis Bozdur (Cashout): $estimatedCashout TP",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons: Kupondaş'ta Paylaş, Deep-Link & Yeniden Oyna
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val context = LocalContext.current

        // Deep-Link Share Button
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF1F5F9),
          border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
          modifier = Modifier.clickable {
            val link = CouponDeepLinkManager.generateDeepLink(ticket)
            CouponDeepLinkManager.copyToClipboard(context, link)
            CouponDeepLinkManager.shareDeepLink(context, ticket.ticketNumber, ticket.totalOdds, link)
            Toast.makeText(context, "🔗 Kupon Linki Kopyalandı & Paylaşılıyor!", Toast.LENGTH_SHORT).show()
          }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Link, contentDescription = "Deep-Link", tint = TealDark, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Link", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealDark)
          }
        }

        // Visual Share Card Button
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF0F172A),
          border = BorderStroke(1.dp, GoldYellow),
          modifier = Modifier.clickable { onOpenVisualizer() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🎨", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text("Kart", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
          }
        }

        OutlinedButton(
          onClick = onShare,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, TealDark),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp)
        ) {
          Icon(Icons.Default.Share, contentDescription = null, tint = TealDark, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Kupondaş", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealDark)
        }

        Button(
          onClick = onReplay,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = TealDark),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp)
        ) {
          Icon(Icons.Default.Replay, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Tekrarla", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

private data class Tuple6<A, B, C, D, E, F>(
  val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
)
