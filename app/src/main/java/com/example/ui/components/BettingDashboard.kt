package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Sport
import com.example.service.BetAnalysisResult
import com.example.service.BetAnalyzer
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * BettingDashboard Screen Component
 *
 * Lists mock live and upcoming sports events with dynamic odds
 * and interactive buttons to place virtual currency bets (TP - Tahmin Puanı).
 * Integrated with the Gemini AI BetAnalyzer for real-time win probability forecasting.
 */
@Composable
fun BettingDashboard(
  matches: List<Match>,
  walletBalance: Long,
  onPlaceVirtualBet: (match: Match, pickName: String, odd: Double, stakeTp: Long) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedSportFilter by remember { mutableStateOf("Hepsi") }
  var onlyLiveFilter by remember { mutableStateOf(false) }

  // Quick bet placement state
  var pendingBetMatch by remember { mutableStateOf<Match?>(null) }
  var pendingPickName by remember { mutableStateOf("") }
  var pendingOdd by remember { mutableStateOf(1.85) }
  var selectedStakeTp by remember { mutableLongStateOf(250L) }
  var betSuccessNotice by remember { mutableStateOf<String?>(null) }

  // BetAnalyzer modal state
  var analyzerMatch by remember { mutableStateOf<Match?>(null) }
  var analysisResult by remember { mutableStateOf<BetAnalysisResult?>(null) }
  var isAnalyzing by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  val sportCategories = listOf("Hepsi", "Futbol", "Basketbol", "Tenis", "Voleybol", "Motor Sporları")

  val filteredList = remember(matches, selectedSportFilter, onlyLiveFilter) {
    matches.filter { match ->
      val matchesSport = when (selectedSportFilter) {
        "Futbol" -> match.sport == Sport.FOOTBALL
        "Basketbol" -> match.sport == Sport.BASKETBALL
        "Tenis" -> match.sport == Sport.TENNIS
        "Voleybol" -> match.sport == Sport.VOLLEYBALL
        "Motor Sporları" -> match.sport == Sport.MOTORSPORTS
        else -> true
      }
      val matchesLive = if (onlyLiveFilter) match.status == MatchStatus.LIVE else true
      matchesSport && matchesLive && match.status != MatchStatus.FINISHED
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.94f)
        .testTag("betting_dashboard_screen"),
      shape = RoundedCornerShape(20.dp),
      color = Color(0xFF0A0F1D),
      border = BorderStroke(1.5.dp, Color(0xFF1E293B))
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
        ) {
          // 1. Dashboard Top Header: Title & Virtual Currency Balance
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(TealDark),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.SportsScore,
                  contentDescription = "Canlı Bahis Dashboard",
                  tint = GoldYellow,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Canlı Bahis Dashboard",
                  color = Color.White,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = "Sanal TP ile Gerçek Zamanlı Bahis Masası",
                  color = Color(0xFF94A3B8),
                  fontSize = 11.sp
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              // Virtual Currency Balance Chip
              Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, GoldYellow)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("💰", fontSize = 12.sp)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${walletBalance} TP",
                    color = GoldYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }

              Spacer(modifier = Modifier.width(6.dp))

              IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_betting_dashboard")
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Kapat",
                  tint = Color(0xFF94A3B8)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 2. Filter Bar: Sports & Live Only
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Live Only Toggle
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (onlyLiveFilter) LiveRed else Color(0xFF1E293B),
              border = BorderStroke(1.dp, if (onlyLiveFilter) Color.White else Color(0xFF334155)),
              modifier = Modifier.clickable { onlyLiveFilter = !onlyLiveFilter }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("🔴", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Sadece Canlı",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = if (onlyLiveFilter) FontWeight.Black else FontWeight.Medium
                )
              }
            }

            sportCategories.forEach { sport ->
              val isSelected = selectedSportFilter == sport
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) TealDark else Color(0xFF1E293B),
                border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0xFF334155)),
                modifier = Modifier.clickable { selectedSportFilter = sport }
              ) {
                Text(
                  text = sport,
                  color = if (isSelected) GoldYellow else Color(0xFFCBD5E1),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }

          // Success notification banner if bet placed
          betSuccessNotice?.let { notice ->
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF065F46),
              border = BorderStroke(1.dp, Color(0xFF34D399)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(notice, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Kapat",
                  tint = Color.White,
                  modifier = Modifier
                    .size(16.dp)
                    .clickable { betSuccessNotice = null }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 3. Live Sports Events List
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(filteredList.take(40), key = { it.id }) { match ->
              DashboardEventCard(
                match = match,
                onSelectOdd = { pickName, odd ->
                  pendingBetMatch = match
                  pendingPickName = pickName
                  pendingOdd = odd
                },
                onOpenAnalyzer = {
                  analyzerMatch = match
                  isAnalyzing = true
                  coroutineScope.launch {
                    analysisResult = BetAnalyzer.analyzeMatch(match)
                    isAnalyzing = false
                  }
                }
              )
            }
          }

          // Padding for bottom bet placement bar
          if (pendingBetMatch != null) {
            Spacer(modifier = Modifier.height(110.dp))
          }
        }

        // 4. Floating Quick Bet Bottom Bar
        AnimatedVisibility(
          visible = pendingBetMatch != null,
          enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
          exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
          modifier = Modifier.align(Alignment.BottomCenter)
        ) {
          pendingBetMatch?.let { match ->
            QuickBetBottomPanel(
              match = match,
              pickName = pendingPickName,
              odd = pendingOdd,
              selectedStake = selectedStakeTp,
              walletBalance = walletBalance,
              onSelectStake = { selectedStakeTp = it },
              onConfirmBet = {
                if (walletBalance >= selectedStakeTp) {
                  onPlaceVirtualBet(match, pendingPickName, pendingOdd, selectedStakeTp)
                  val potWin = (selectedStakeTp * pendingOdd).roundToInt()
                  betSuccessNotice = "✅ ${match.homeTeam} - ${match.awayTeam} [$pendingPickName]: $selectedStakeTp TP yatırıldı! (Kazanç: $potWin TP)"
                  pendingBetMatch = null
                }
              },
              onCancel = { pendingBetMatch = null }
            )
          }
        }
      }
    }
  }

  // 5. BetAnalyzer Gemini AI Win Probabilities Dialog
  analyzerMatch?.let { match ->
    Dialog(
      onDismissRequest = {
        analyzerMatch = null
        analysisResult = null
      },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .fillMaxHeight(0.85f),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.5.dp, GoldYellow)
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
          // Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🤖", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "BetAnalyzer Gemini AI",
                  color = Color.White,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Geçmiş Performans & Poisson Analizi",
                  color = GoldYellow,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
            IconButton(onClick = {
              analyzerMatch = null
              analysisResult = null
            }) {
              Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          if (isAnalyzing || analysisResult == null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = GoldYellow)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "Gemini AI geçmiş takım verilerini ve olasılıkları hesaplıyor...",
                  color = Color(0xFF94A3B8),
                  fontSize = 12.sp,
                  textAlign = TextAlign.Center
                )
              }
            }
          } else {
            val res = analysisResult!!
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              item {
                // Match Header
                Card(
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      text = "${match.homeTeam} vs ${match.awayTeam}",
                      color = Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 15.sp
                    )
                    Text(
                      text = "${match.league} • ${match.matchDate}",
                      color = Color(0xFF94A3B8),
                      fontSize = 11.sp
                    )
                  }
                }
              }

              item {
                // Probability Bars (Home Win, Draw, Away Win)
                Card(
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text("📊 Maç Kazanma Olasılıkları", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text("Ev Sahibi: %${res.homeWinProb}", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      if (res.drawProb > 0) {
                        Text("Beraberlik: %${res.drawProb}", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }
                      Text("Deplasman: %${res.awayWinProb}", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Progress bar
                    LinearProgressIndicator(
                      progress = { (res.homeWinProb / 100f).coerceIn(0f, 1f) },
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                      color = Color(0xFF34D399),
                      trackColor = Color(0xFF334155)
                    )
                  }
                }
              }

              item {
                // Prediction Card
                Card(
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                  border = BorderStroke(1.dp, GoldYellow),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text("Yapay Zeka Önerisi", color = Color(0xFF94A3B8), fontSize = 10.sp)
                      Text(res.recommendedPick, color = GoldYellow, fontSize = 16.sp, fontWeight = FontWeight.Black)
                      Text("Tahmini Skor: ${res.predictedScore}", color = Color.White, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TealDark
                      ) {
                        Text(
                          text = "Oran: ${res.recommendedOdd}",
                          color = Color.White,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text("Güven: %${res.confidenceScore}", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }

              item {
                // Key Factors
                Card(
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text("🔑 Kritik Faktörler", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    res.keyFactors.forEach { factor ->
                      Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("•", color = GoldYellow, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(factor, color = Color(0xFFCBD5E1), fontSize = 11.sp)
                      }
                    }
                  }
                }
              }

              item {
                // Tactical Analysis
                Card(
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text("🧠 Taktiksel AI Yorumu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = res.tacticalAnalysis,
                      color = Color(0xFFCBD5E1),
                      fontSize = 11.sp,
                      lineHeight = 15.sp
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
              onClick = {
                pendingBetMatch = match
                pendingPickName = res.recommendedPick
                pendingOdd = res.recommendedOdd
                analyzerMatch = null
                analysisResult = null
              },
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text(
                text = "Bu Tahmine Sanal Bahis Yap (${res.recommendedPick} - ${res.recommendedOdd})",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Event Card for Dashboard listing
 */
@Composable
private fun DashboardEventCard(
  match: Match,
  onSelectOdd: (pickName: String, odd: Double) -> Unit,
  onOpenAnalyzer: () -> Unit
) {
  val isLive = match.status == MatchStatus.LIVE

  // Extract primary odds
  val msMarket = match.markets.find { it.type == MarketType.MATCH_RESULT }
  val ms1Odd = msMarket?.selections?.getOrNull(0)?.odd ?: 1.85
  val msXOdd = msMarket?.selections?.getOrNull(1)?.odd ?: 3.20
  val ms2Odd = msMarket?.selections?.getOrNull(2)?.odd ?: 3.80

  val ouMarket = match.markets.find { it.type == MarketType.TOTAL_GOALS_25 }
  val ustOdd = ouMarket?.selections?.find { it.name.contains("Üst") }?.odd ?: 1.75
  val altOdd = ouMarket?.selections?.find { it.name.contains("Alt") }?.odd ?: 1.90

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dashboard_event_card_${match.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
    border = BorderStroke(1.dp, if (isLive) Color(0xFF1E293B) else Color(0xFF1E293B))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top info row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (isLive) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = LiveRed
            ) {
              Text(
                text = "CANLI ${match.minute}'",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          } else {
            Text(match.startTime, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = match.league,
            color = Color(0xFF38BDF8),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // BetAnalyzer Trigger Button
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFF1E293B),
          border = BorderStroke(1.dp, GoldYellow),
          modifier = Modifier.clickable { onOpenAnalyzer() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🤖 AI Analiz", color = GoldYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Teams and Score Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(match.homeTeam, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
          Spacer(modifier = Modifier.height(2.dp))
          Text(match.awayTeam, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        // Score display
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFF0F172A),
          border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
          Text(
            text = "${match.homeScore} - ${match.awayScore}",
            color = if (isLive) GoldYellow else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Quick Odds Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // MS 1
        OddButton(
          label = "1",
          odd = ms1Odd,
          modifier = Modifier.weight(1f),
          onClick = { onSelectOdd("MS 1 (${match.homeTeam})", ms1Odd) }
        )

        // MS X (Draw)
        if (match.sport != Sport.BASKETBALL && match.sport != Sport.TENNIS) {
          OddButton(
            label = "X",
            odd = msXOdd,
            modifier = Modifier.weight(1f),
            onClick = { onSelectOdd("MS X (Beraberlik)", msXOdd) }
          )
        }

        // MS 2
        OddButton(
          label = "2",
          odd = ms2Odd,
          modifier = Modifier.weight(1f),
          onClick = { onSelectOdd("MS 2 (${match.awayTeam})", ms2Odd) }
        )

        // 2.5 ÜST
        OddButton(
          label = "Üst",
          odd = ustOdd,
          modifier = Modifier.weight(1f),
          onClick = { onSelectOdd("2.5 Üst", ustOdd) }
        )

        // 2.5 ALT
        OddButton(
          label = "Alt",
          odd = altOdd,
          modifier = Modifier.weight(1f),
          onClick = { onSelectOdd("2.5 Alt", altOdd) }
        )
      }
    }
  }
}

@Composable
private fun OddButton(
  label: String,
  odd: Double,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = Color(0xFF0F172A),
    border = BorderStroke(1.dp, Color(0xFF334155)),
    modifier = modifier.clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp, horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(label, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
      Text(
        text = String.format(java.util.Locale.US, "%.2f", odd),
        color = GoldYellow,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}

/**
 * Floating Quick Bet Placement Bottom Panel
 */
@Composable
private fun QuickBetBottomPanel(
  match: Match,
  pickName: String,
  odd: Double,
  selectedStake: Long,
  walletBalance: Long,
  onSelectStake: (Long) -> Unit,
  onConfirmBet: () -> Unit,
  onCancel: () -> Unit
) {
  val potentialReturn = (selectedStake * odd).roundToInt()
  val stakes = listOf(100L, 250L, 500L, 1000L, 2500L)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(12.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    border = BorderStroke(1.5.dp, GoldYellow)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Pick Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "${match.homeTeam} - ${match.awayTeam}",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Seçim: $pickName • Oran: $odd",
            color = GoldYellow,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }
        IconButton(onClick = onCancel, modifier = Modifier.size(24.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Vazgeç", tint = Color(0xFF94A3B8))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Stake Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        stakes.forEach { stake ->
          val isSelected = selectedStake == stake
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isSelected) GoldYellow else Color(0xFF0F172A),
            border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0xFF334155)),
            modifier = Modifier
              .weight(1f)
              .clickable { onSelectStake(stake) }
          ) {
            Text(
              text = "${stake}TP",
              color = if (isSelected) Color.Black else Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Confirm Button and Return calculation
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Olası Kazanç:", color = Color(0xFF94A3B8), fontSize = 10.sp)
          Text(
            text = "$potentialReturn TP",
            color = Color(0xFF34D399),
            fontSize = 15.sp,
            fontWeight = FontWeight.Black
          )
        }

        Button(
          onClick = onConfirmBet,
          enabled = walletBalance >= selectedStake,
          colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.FlashOn, contentDescription = "Bahis Oyna", tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (walletBalance >= selectedStake) "Bahsi Onayla ($selectedStake TP)" else "Yetersiz Bakiye",
            color = Color.Black,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}
