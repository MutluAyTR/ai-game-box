package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.TicketEntity
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.service.BettingTransactionResult
import com.example.service.BettingTransactionService
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main Dashboard UI component that connects directly to the Room database.
 * Displays:
 * 1. Current Virtual Balance (TP - Tahmin Puanı) with instant Room synchronization.
 * 2. Summary of Active Bets with live status, odds, potential returns, and Cash-Out.
 * 3. Upcoming Matches across all sports (Football, Basketball, MotoGP, WRC, Tennis, Volleyball)
 *    with instant betting validation via [BettingTransactionService].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardView(
  matches: List<Match>,
  onOpenMatchDetail: (Match) -> Unit,
  onOpenCouponsScreen: () -> Unit,
  onOpenDailyBonus: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val db = remember { AppDatabase.getDatabase(context) }
  val bettingService = remember { BettingTransactionService.getInstance(context) }

  // 1. Reactive Room State Connections
  val virtualBalance by bettingService.getVirtualBalanceFlow().collectAsState(initial = 10000L)
  val activeBets by bettingService.getActiveBetsFlow().collectAsState(initial = emptyList())
  val allBets by bettingService.getAllBetsFlow().collectAsState(initial = emptyList())

  var selectedSportFilter by remember { mutableStateOf<Sport?>(null) }
  var quickBetTargetMatch by remember { mutableStateOf<Match?>(null) }
  var quickBetPickName by remember { mutableStateOf("") }
  var quickBetOdd by remember { mutableDoubleStateOf(1.85) }
  var quickBetStake by remember { mutableLongStateOf(250L) }
  var successMessage by remember { mutableStateOf<String?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Filter upcoming & live matches
  val upcomingMatches = remember(matches, selectedSportFilter) {
    matches.filter { match ->
      val matchesSport = selectedSportFilter == null || match.sport == selectedSportFilter
      val isUpcomingOrLive = match.status == MatchStatus.SCHEDULED || match.status == MatchStatus.LIVE
      matchesSport && isUpcomingOrLive
    }
  }

  val totalActiveStake = remember(activeBets) { activeBets.sumOf { it.stakePoints } }
  val totalPotentialReturn = remember(activeBets) { activeBets.sumOf { it.potentialPoints } }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("main_dashboard_ui")
  ) {
    // Active Bets summary on the dashboard with pre-match & live cashout
    if (activeBets.isNotEmpty()) {
      ActiveBetsSummarySection(
        activeBets = activeBets,
        matches = matches,
        totalStake = totalActiveStake,
        totalPotentialReturn = totalPotentialReturn,
        onOpenAllCoupons = onOpenCouponsScreen,
        onCashOut = { ticket, offer ->
          coroutineScope.launch {
            val won = bettingService.cashOutBet(ticket.id, overrideAmount = offer)
            Toast.makeText(context, "💰 Bahis Bozduruldu: +$won TP hesabınıza aktarıldı!", Toast.LENGTH_SHORT).show()
          }
        }
      )
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Upcoming Matches Header & Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(GoldYellow)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "GÜNÜN BÜLTENİ & YAKLAŞAN MAÇLAR",
          fontSize = 12.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A),
          letterSpacing = 0.5.sp
        )
      }

      Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text(
          text = "${upcomingMatches.size} Maç",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF475569),
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Sport Filter Chips
    LazyRow(
      contentPadding = PaddingValues(horizontal = 14.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      item {
        FilterChip(
          selected = selectedSportFilter == null,
          onClick = { selectedSportFilter = null },
          label = { Text("Tümü", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TealDark,
            selectedLabelColor = Color.White
          )
        )
      }
      items(listOf(Sport.FOOTBALL, Sport.BASKETBALL, Sport.MOTORSPORTS, Sport.TENNIS, Sport.VOLLEYBALL)) { sport ->
        val isSelected = selectedSportFilter == sport
        FilterChip(
          selected = isSelected,
          onClick = { selectedSportFilter = if (isSelected) null else sport },
          label = {
            Text(
              text = "${sport.iconRes} ${sport.displayName}",
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TealDark,
            selectedLabelColor = Color.White
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Upcoming Match Cards with 1-Tap Bet buttons
    upcomingMatches.take(8).forEach { match ->
      UpcomingMatchDashboardCard(
        match = match,
        onCardClick = { onOpenMatchDetail(match) },
        onQuickBet = { pickName, odd ->
          quickBetTargetMatch = match
          quickBetPickName = pickName
          quickBetOdd = odd
          quickBetStake = 250L
        }
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
  }

  // Quick Bet Modal Dialog
  quickBetTargetMatch?.let { match ->
    AlertDialog(
      onDismissRequest = { quickBetTargetMatch = null },
      containerColor = Color(0xFF1E293B),
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Hızlı Bahis Onayı", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 16.sp)
          IconButton(onClick = { quickBetTargetMatch = null }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF94A3B8))
          }
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "${match.homeTeam} vs ${match.awayTeam}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "${match.league} • ${match.startTime}",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Seçim: $quickBetPickName", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = "Oran: ${String.format("%.2f", quickBetOdd)}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp)
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Mevcut Bakiyeniz:", color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text("$virtualBalance TP", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          // Stake selection chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(100L, 250L, 500L, 1000L).forEach { amount ->
              val isSelected = quickBetStake == amount
              Surface(
                color = if (isSelected) TealDark else Color(0xFF334155),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                  .weight(1f)
                  .clickable { quickBetStake = amount }
              ) {
                Text(
                  text = "$amount",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 6.dp)
                )
              }
            }
          }

          val potentialWin = (quickBetStake * quickBetOdd).toLong()
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Olası Kazanç:", color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text("$potentialWin TP", color = Color(0xFF34D399), fontWeight = FontWeight.Black, fontSize = 13.sp)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            coroutineScope.launch {
              val selection = SlipSelection(
                matchId = match.id,
                matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                marketType = MarketType.MATCH_RESULT,
                selectionId = "${match.id}_$quickBetPickName",
                selectionName = quickBetPickName,
                odd = quickBetOdd,
                isLive = match.status == MatchStatus.LIVE,
                matchStatus = match.status
              )
              val result = bettingService.placeBet(
                type = "TEKLİ",
                stakePoints = quickBetStake,
                totalOdds = quickBetOdd,
                selections = listOf(selection)
              )
              when (result) {
                is BettingTransactionResult.Success -> {
                  Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                  quickBetTargetMatch = null
                }
                is BettingTransactionResult.InsufficientBalance -> {
                  Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
                is BettingTransactionResult.InvalidStake -> {
                  Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
                is BettingTransactionResult.Error -> {
                  Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Kuponu Onayla ($quickBetStake TP)", color = Color(0xFF0F172A), fontWeight = FontWeight.Black)
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { quickBetTargetMatch = null },
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("İptal", color = Color(0xFFCBD5E1))
        }
      }
    )
  }
}

/**
 * 1. Virtual Balance Card connected to Room
 */
@Composable
private fun RoomVirtualBalanceCard(
  virtualBalance: Long,
  activeBetsCount: Int,
  totalBetsCount: Int,
  onDailyBonus: () -> Unit,
  onRecharge: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp)
      .testTag("room_virtual_balance_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    border = BorderStroke(1.dp, Color(0xFF1E293B)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFF0F3A3D)),
            contentAlignment = Alignment.Center
          ) {
            Text("🪙", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "SANAL CÜZDAN (ROOM DATABASE)",
              color = Color(0xFF94A3B8),
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "$virtualBalance TP",
              color = GoldYellow,
              fontSize = 22.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(0.8.dp, Color(0xFF334155))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Senkronize",
              color = Color(0xFFA7F3D0),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Balance Quick Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onRecharge,
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = TealDark),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(vertical = 8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("+2.500 TP Yükle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onDailyBonus,
          modifier = Modifier.weight(1f),
          border = BorderStroke(1.dp, GoldYellow),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(vertical = 8.dp)
        ) {
          Text("🎁 Günlük Görevler", color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * 2. Active Bets Summary Section connected to Room
 */
@Composable
private fun ActiveBetsSummarySection(
  activeBets: List<TicketEntity>,
  matches: List<Match> = emptyList(),
  totalStake: Long,
  totalPotentialReturn: Long,
  onOpenAllCoupons: () -> Unit,
  onCashOut: (TicketEntity, Long) -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp)
      .testTag("room_active_bets_section"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = TealDark, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "AKTİF KUPONLAR (${activeBets.size})",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
        }

        Text(
          text = "Tümünü Gör →",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TealDark,
          modifier = Modifier.clickable { onOpenAllCoupons() }
        )
      }

      if (activeBets.isEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = Color(0xFFF8FAFC),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🎟️", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Şu anda aktif kuponunuz bulunmuyor", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
              Text("Aşağıdaki maçlardan seçim yaparak ilk kuponunuzu hemen oynayın!", fontSize = 10.sp, color = Color(0xFF64748B))
            }
          }
        }
      } else {
        Spacer(modifier = Modifier.height(8.dp))

        // Metrics Banner: Total Stake & Potential Return
        Surface(
          color = Color(0xFFF1F5F9),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Toplam Yatırılan:", color = Color(0xFF64748B), fontSize = 9.5.sp)
              Text("$totalStake TP", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Toplam Olası Kazanç:", color = Color(0xFF64748B), fontSize = 9.5.sp)
              Text("$totalPotentialReturn TP", color = Color(0xFF059669), fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal List of active tickets
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(activeBets, key = { it.id }) { ticket ->
            ActiveTicketCard(
              ticket = ticket,
              matches = matches,
              onCashOut = { offer -> onCashOut(ticket, offer) }
            )
          }
        }
      }
    }
  }
}

/**
 * Individual Active Ticket Card with pre-match 50% and live dynamic cashout calculation
 */
@Composable
private fun ActiveTicketCard(
  ticket: TicketEntity,
  matches: List<Match> = emptyList(),
  onCashOut: (Long) -> Unit
) {
  // Parse selections
  val isAnyLive = remember(ticket.selectionsJson, matches) {
    try {
      val arr = org.json.JSONArray(ticket.selectionsJson)
      var live = false
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        val mId = obj.optString("matchId")
        val teams = obj.optString("matchTeams")
        val m = matches.firstOrNull { it.id == mId }
          ?: matches.firstOrNull { (it.homeTeam in teams || it.awayTeam in teams) && it.status != MatchStatus.FINISHED }
        if (m?.status == MatchStatus.LIVE) {
          live = true
          break
        }
      }
      live
    } catch (_: Exception) {
      false
    }
  }

  // Exact rule implementation:
  // "bozdur maç başlamadan önce bozdurmak isteyenler için kupon için yatırdığı tp nin yarısı olacak sonra maç başladığında kupon daki maç durumuna göre artıp azalacak otamatik"
  val calculatedCashout = remember(ticket, matches, isAnyLive) {
    if (!isAnyLive) {
      (ticket.stakePoints / 2L).coerceAtLeast(1L)
    } else {
      var liveScoreRatioSum = 0.0
      var count = 0
      try {
        val arr = org.json.JSONArray(ticket.selectionsJson)
        count = arr.length()
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          val mId = obj.optString("matchId")
          val teams = obj.optString("matchTeams")
          val selName = obj.optString("selectionName")
          val m = matches.firstOrNull { it.id == mId }
            ?: matches.firstOrNull { (it.homeTeam in teams || it.awayTeam in teams) && it.status != MatchStatus.FINISHED }

          if (m?.status == MatchStatus.LIVE) {
            val isWinning = (selName.contains("1") && m.homeScore > m.awayScore) ||
                (selName.contains("2") && m.awayScore > m.homeScore) ||
                (selName.contains("X") && m.homeScore == m.awayScore) ||
                (selName.contains("Üst") && m.homeScore + m.awayScore >= 3)
            val minProg = (m.minute.coerceIn(1, 90) / 90.0)
            val factor = if (isWinning) 0.55 + (minProg * 0.40) else (0.45 - (minProg * 0.30)).coerceAtLeast(0.08)
            liveScoreRatioSum += factor
          } else {
            liveScoreRatioSum += 0.50
          }
        }
      } catch (_: Exception) {}

      val totalCount = if (count > 0) count else 1
      val avg = (liveScoreRatioSum / totalCount.toDouble()).coerceIn(0.05, 1.0)
      if (avg >= 0.50) {
        val prog = (avg - 0.50) / 0.50
        val half = ticket.stakePoints / 2.0
        val maxP = ticket.potentialPoints * 0.95
        (half + (maxP - half) * prog).toLong()
      } else {
        val prog = (0.50 - avg) / 0.50
        val half = ticket.stakePoints / 2.0
        val minP = (ticket.stakePoints * 0.10).coerceAtLeast(1.0)
        (half - (half - minP) * prog).toLong()
      }.coerceIn((ticket.stakePoints * 0.05).toLong().coerceAtLeast(1L), (ticket.potentialPoints * 0.98).toLong())
    }
  }

  Surface(
    color = Color(0xFF0F172A),
    shape = RoundedCornerShape(10.dp),
    border = BorderStroke(1.dp, Color(0xFF334155)),
    modifier = Modifier.width(220.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "#${ticket.ticketNumber}",
            color = GoldYellow,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }

        Surface(
          color = if (isAnyLive) LiveRed else Color(0xFF0284C7),
          shape = RoundedCornerShape(4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color.White))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = if (isAnyLive) "CANLI" else "BAŞLAMADI",
              color = Color.White,
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Tip: ${ticket.type}", color = Color(0xFFCBD5E1), fontSize = 10.sp)
        Text("Oran: ${String.format("%.2f", ticket.totalOdds)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Yatırılan: ${ticket.stakePoints} TP", color = Color(0xFF94A3B8), fontSize = 10.sp)
        Text("Kazanç: ${ticket.potentialPoints} TP", color = Color(0xFF34D399), fontWeight = FontWeight.Black, fontSize = 11.sp)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Cash-out button using calculated offer
      Button(
        onClick = { onCashOut(calculatedCashout) },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Bahis Bozdur ($calculatedCashout TP)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * 3. Upcoming Match Card for the Dashboard
 */
@Composable
private fun UpcomingMatchDashboardCard(
  match: Match,
  onCardClick: () -> Unit,
  onQuickBet: (pickName: String, odd: Double) -> Unit
) {
  val msMarket = match.markets.find {
    it.type == MarketType.MATCH_RESULT || it.type == MarketType.BASKETBALL_MS || it.type == MarketType.MOTORSPORTS_WINNER
  }
  val isMotorsport = match.sport == Sport.MOTORSPORTS ||
      match.league.contains("MotoGP", ignoreCase = true) ||
      match.league.contains("WRC", ignoreCase = true)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp)
      .clickable { onCardClick() }
      .testTag("upcoming_match_card_${match.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Header: League & Start Time
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = match.sport.iconRes, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = match.league, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
        }

        Surface(
          color = if (match.status == MatchStatus.LIVE) LiveRed else Color(0xFFF1F5F9),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = if (match.status == MatchStatus.LIVE) "${match.minute}' CANLI" else "${match.startTime} TSİ",
            color = if (match.status == MatchStatus.LIVE) Color.White else Color(0xFF475569),
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Teams
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = match.homeTeam, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1)
          Text(text = match.awayTeam, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1)
        }

        if (match.status != MatchStatus.SCHEDULED) {
          Column(horizontalAlignment = Alignment.End) {
            val homeText = if (isMotorsport) "${match.homeScore} P" else "${match.homeScore}"
            val awayText = if (isMotorsport) "${match.awayScore} P" else "${match.awayScore}"
            Text(text = homeText, fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (isMotorsport) GoldYellow else LiveRed)
            Text(text = awayText, fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (isMotorsport) Color(0xFF94A3B8) else LiveRed)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Quick Odds Buttons
      if (msMarket != null && msMarket.selections.isNotEmpty()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          msMarket.selections.forEach { sel ->
            Surface(
              color = Color(0xFFF8FAFC),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
              modifier = Modifier
                .weight(1f)
                .clickable { onQuickBet(sel.name, sel.odd) }
            ) {
              Column(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = sel.name,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF475569),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = String.format("%.2f", sel.odd),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF0F172A)
                )
              }
            }
          }
        }
      }
    }
  }
}
