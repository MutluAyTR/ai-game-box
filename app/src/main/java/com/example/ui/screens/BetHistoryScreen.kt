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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.formatOdd
import com.example.util.formatTp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetHistoryScreen(
  tickets: List<Ticket>,
  matches: List<Match> = emptyList(),
  onBack: () -> Unit = {},
  onShareTicket: (Ticket) -> Unit = {},
  onReplayTicket: (Ticket) -> Unit = {}
) {
  var selectedTab by remember { mutableIntStateOf(0) }

  val filteredTickets = when (selectedTab) {
    1 -> tickets.filter { it.status == TicketStatus.WON }
    2 -> tickets.filter { it.status == TicketStatus.LOST }
    3 -> tickets.filter { it.status == TicketStatus.PENDING }
    else -> tickets
  }

  // Statistical aggregates
  val totalBets = tickets.size
  val wonCount = remember(tickets) { tickets.count { it.status == TicketStatus.WON } }
  val lostCount = remember(tickets) { tickets.count { it.status == TicketStatus.LOST } }
  val pendingCount = remember(tickets) { tickets.count { it.status == TicketStatus.PENDING } }
  val totalWonTp = remember(tickets) {
    tickets.filter { it.status == TicketStatus.WON }.sumOf { it.potentialPoints }
  }
  val totalStakedTp = remember(tickets) { tickets.sumOf { it.stakePoints } }
  val netProfit = totalWonTp - totalStakedTp
  val winRate = if (wonCount + lostCount > 0) {
    (wonCount * 100.0 / (wonCount + lostCount)).toInt()
  } else 0

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Bahis Geçmişi (Bet History)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color.White
            )
            Text(
              text = "Room Veritabanı Kayıtları ($totalBets Kupon)",
              fontSize = 11.sp,
              color = Color(0xFFB0BEC5)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Geri",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TealDark),
        modifier = Modifier.testTag("bet_history_top_bar")
      )
    },
    containerColor = Color(0xFFF1F5F9),
    modifier = Modifier.testTag("bet_history_screen")
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // 1. Performance Overview Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("bet_history_stats_card"),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Genel Tahmin Performansı",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              StatColumn(title = "Toplam Kupon", value = "$totalBets", modifier = Modifier.weight(1f))
              StatColumn(title = "Kazanan", value = "$wonCount", valueColor = Color(0xFF059669), modifier = Modifier.weight(1f))
              StatColumn(title = "Kaybeden", value = "$lostCount", valueColor = Color(0xFFDC2626), modifier = Modifier.weight(1f))
              StatColumn(title = "Başarı", value = "%$winRate", valueColor = Color(0xFF0D9488), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 10.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                  contentDescription = null,
                  tint = if (netProfit >= 0) Color(0xFF059669) else Color(0xFFDC2626),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Net TP Kazancı:",
                  fontWeight = FontWeight.Medium,
                  fontSize = 12.sp,
                  color = Color(0xFF475569)
                )
              }

              Text(
                text = "${if (netProfit >= 0) "+" else ""}${netProfit.formatTp()} TP",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = if (netProfit >= 0) Color(0xFF059669) else Color(0xFFDC2626)
              )
            }
          }
        }
      }

      // 2. Filter Tabs
      item {
        SecondaryTabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.White,
          contentColor = TealDark,
          modifier = Modifier.padding(horizontal = 12.dp)
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Tümü ($totalBets)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Kazanan ($wonCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669)) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("Kaybeden ($lostCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)) }
          )
          Tab(
            selected = selectedTab == 3,
            onClick = { selectedTab = 3 },
            text = { Text("Açık ($pendingCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706)) }
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      // 3. Tickets List
      if (filteredTickets.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Bu kategoride henüz kayıtlı kupon bulunmuyor.",
              color = Color(0xFF94A3B8),
              fontSize = 13.sp
            )
          }
        }
      } else {
        items(filteredTickets, key = { it.id }) { ticket ->
          BetHistoryTicketCard(
            ticket = ticket,
            matches = matches,
            onShare = { onShareTicket(ticket) },
            onReplay = { onReplayTicket(ticket) }
          )
        }
      }
    }
  }
}

@Composable
private fun StatColumn(
  title: String,
  value: String,
  valueColor: Color = Color(0xFF1E293B),
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Text(
      text = title,
      fontSize = 11.sp,
      color = Color(0xFF64748B),
      maxLines = 1,
      softWrap = false,
      overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      fontSize = 15.sp,
      fontWeight = FontWeight.Black,
      color = valueColor,
      maxLines = 1,
      softWrap = false
    )
  }
}

@Composable
fun BetHistoryTicketCard(
  ticket: Ticket,
  matches: List<Match> = emptyList(),
  onShare: () -> Unit,
  onReplay: () -> Unit
) {
  val dateStr = remember(ticket.createdAt) {
    SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.forLanguageTag("tr-TR")).format(Date(ticket.createdAt))
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 5.dp)
      .testTag("bet_ticket_${ticket.ticketNumber}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top header: Ticket number, date, and Status Indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "#${ticket.ticketNumber} • ${ticket.type.name}",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF1E293B)
          )
          Text(
            text = dateStr,
            fontSize = 10.sp,
            color = Color(0xFF94A3B8)
          )
        }

        // Status Indicator Badge
        BetStatusIndicatorBadge(status = ticket.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Selections List with Specific Outcome of Matched Events
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFFF8FAFC))
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        ticket.selections.forEach { sel ->
          val matchedGame = matches.find { it.id == sel.matchId || it.homeTeam in sel.matchTeams }
          val isMatchFinished = matchedGame?.status == MatchStatus.FINISHED
          val isMatchLive = matchedGame?.status == MatchStatus.LIVE

          // Compute specific match outcome for this selection
          val isSelectionWon = when {
            matchedGame == null -> ticket.status == TicketStatus.WON
            sel.marketType == MarketType.MATCH_RESULT || sel.marketType == MarketType.BASKETBALL_MS -> {
              val homeWon = matchedGame.homeScore > matchedGame.awayScore
              val draw = matchedGame.homeScore == matchedGame.awayScore
              val awayWon = matchedGame.awayScore > matchedGame.homeScore
              when {
                sel.selectionName.contains("1") -> homeWon
                sel.selectionName.contains("X") || sel.selectionName.contains("0") -> draw
                sel.selectionName.contains("2") -> awayWon
                else -> ticket.status == TicketStatus.WON
              }
            }
            sel.marketType == MarketType.TOTAL_GOALS_25 -> {
              val totalGoals = matchedGame.homeScore + matchedGame.awayScore
              if (sel.selectionName.contains("Üst")) totalGoals > 2.5 else totalGoals < 2.5
            }
            sel.marketType == MarketType.BOTH_TEAMS_SCORE -> {
              if (sel.selectionName.contains("Var")) matchedGame.homeScore > 0 && matchedGame.awayScore > 0 else (matchedGame.homeScore == 0 || matchedGame.awayScore == 0)
            }
            else -> ticket.status == TicketStatus.WON
          }

          Surface(
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              // Row 1: Match teams & live/final score of the event
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = sel.matchTeams,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = Color(0xFF0F172A),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f)
                )

                if (matchedGame != null) {
                  Surface(
                    color = if (isMatchLive) Color(0xFFFEF2F2) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    val liveTimeStr = if (matchedGame.minute >= 90 && matchedGame.currentExtraMinute > 0) "Canlı 90+${matchedGame.currentExtraMinute}'" else "Canlı ${matchedGame.minute}'"
                    Text(
                      text = "${matchedGame.homeScore} - ${matchedGame.awayScore} (${if (isMatchLive) liveTimeStr else if (isMatchFinished) "MS" else matchedGame.startTime})",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isMatchLive) Color(0xFFDC2626) else Color(0xFF334155),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(4.dp))

              // Row 2: Selected Pick, Odds, and Specific Outcome (Won/Lost/Pending)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "${sel.marketType.displayName}: ",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                  )
                  Text(
                    text = sel.selectionName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "@${sel.odd.formatOdd()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = TealDark
                  )
                }

                // Event Outcome Badge
                Surface(
                  color = when {
                    ticket.status == TicketStatus.WON || (isMatchFinished && isSelectionWon) -> Color(0xFFDCFCE7)
                    ticket.status == TicketStatus.LOST || (isMatchFinished && !isSelectionWon) -> Color(0xFFFEE2E2)
                    isMatchLive -> Color(0xFFFEF3C7)
                    else -> Color(0xFFF1F5F9)
                  },
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = when {
                      ticket.status == TicketStatus.WON || (isMatchFinished && isSelectionWon) -> "KAZANDI ✅"
                      ticket.status == TicketStatus.LOST || (isMatchFinished && !isSelectionWon) -> "KAYBETTİ ❌"
                      isMatchLive -> "OYNANIYOR ⏳"
                      else -> "BEKLENİYOR ⏱️"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                      ticket.status == TicketStatus.WON || (isMatchFinished && isSelectionWon) -> Color(0xFF15803D)
                      ticket.status == TicketStatus.LOST || (isMatchFinished && !isSelectionWon) -> Color(0xFFB91C1C)
                      isMatchLive -> Color(0xFFB45309)
                      else -> Color(0xFF64748B)
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Financials row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Yatırılan: ${ticket.stakePoints.formatTp()} TP | Oran: ${ticket.totalOdds.formatOdd()}",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
          Text(
            text = if (ticket.status == TicketStatus.WON) {
              "Kazanılan: +${ticket.potentialPoints.formatTp()} TP"
            } else {
              "Olası Kazanç: ${ticket.potentialPoints.formatTp()} TP"
            },
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = if (ticket.status == TicketStatus.WON) Color(0xFF059669) else TealDark
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onShare,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Paylaş",
              tint = TealDark,
              modifier = Modifier.size(16.dp)
            )
          }

          Button(
            onClick = onReplay,
            modifier = Modifier.height(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Replay,
              contentDescription = null,
              tint = TealDark,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Tekrar Oyna",
              color = TealDark,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun BetStatusIndicatorBadge(status: TicketStatus) {
  val (bgColor, textColor, label, icon) = when (status) {
    TicketStatus.WON -> Quadruple(
      Color(0xFFDCFCE7),
      Color(0xFF15803D),
      "KAZANDI (Won)",
      Icons.Default.CheckCircle
    )
    TicketStatus.LOST -> Quadruple(
      Color(0xFFFEE2E2),
      Color(0xFFB91C1C),
      "KAYBETTİ (Lost)",
      Icons.Default.Close
    )
    TicketStatus.PENDING -> Quadruple(
      Color(0xFFFEF3C7),
      Color(0xFFB45309),
      "DEVAM EDİYOR",
      Icons.Default.HourglassTop
    )
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = textColor,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      )
    }
  }
}

private data class Quadruple<A, B, C, D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)
