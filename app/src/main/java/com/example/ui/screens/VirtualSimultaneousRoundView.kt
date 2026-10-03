package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketType
import com.example.data.model.Selection
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.service.RoundPhase
import com.example.service.VirtualMatchItem
import com.example.service.VirtualRoundManager
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.formatOdd

/**
 * 22+ Simultaneous Matches & Synchronized Live Round Betting:
 * All matches start, progress, and end simultaneously with a synchronized global timer.
 * Users can pick selections from across all 22 football & basketball matches.
 */
@Composable
fun VirtualSimultaneousRoundView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  val roundState by VirtualRoundManager.state.collectAsState()
  var selectedSportFilter by remember { mutableStateOf<Sport?>(null) }
  var userMessage by remember { mutableStateOf<String?>(null) }
  val stake = 100L

  val filteredMatches = remember(roundState.matches, selectedSportFilter) {
    if (selectedSportFilter == null) roundState.matches
    else roundState.matches.filter { it.sport == selectedSportFilter }
  }

  val totalSlipOdds = remember(roundState.activeSlip) {
    if (roundState.activeSlip.isEmpty()) 0.0
    else roundState.activeSlip.fold(1.0) { acc, sel -> acc * sel.odd }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    // 1. Global Synchronized Round Timer Header
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF1E293B), Color(0xFF0F172A))
            )
          )
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = when (roundState.phase) {
                RoundPhase.BETTING_OPEN -> Color(0xFF10B981)
                RoundPhase.MATCHES_IN_PLAY -> Color(0xFFEF4444)
                RoundPhase.SETTLEMENT -> GoldYellow
              },
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = when (roundState.phase) {
                  RoundPhase.BETTING_OPEN -> "● KUPON KABULÜ AÇIK"
                  RoundPhase.MATCHES_IN_PLAY -> "● 22 MAÇ CANLI OYNANIYOR"
                  RoundPhase.SETTLEMENT -> "● SONUÇLANDI"
                },
                color = if (roundState.phase == RoundPhase.SETTLEMENT) TealDark else Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Hafta ${roundState.roundNumber}",
              color = Color(0xFF94A3B8),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Countdown
          Surface(
            color = Color(0xFF334155),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "⏱️ ${roundState.secondsRemaining} sn",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress bar
        LinearProgressIndicator(
          progress = {
            val total = when (roundState.phase) {
              RoundPhase.BETTING_OPEN -> 20f
              RoundPhase.MATCHES_IN_PLAY -> 25f
              RoundPhase.SETTLEMENT -> 5f
            }
            (roundState.secondsRemaining / total).coerceIn(0f, 1f)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = when (roundState.phase) {
            RoundPhase.BETTING_OPEN -> Color(0xFF10B981)
            RoundPhase.MATCHES_IN_PLAY -> Color(0xFFEF4444)
            RoundPhase.SETTLEMENT -> GoldYellow
          },
          trackColor = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = when (roundState.phase) {
            RoundPhase.BETTING_OPEN -> "Tüm maçlar aynı anda başlar. Süre bitmeden kuponunu yap!"
            RoundPhase.MATCHES_IN_PLAY -> "22 maç eş zamanlı devam ediyor. Skorlar ve dakikalar anlık akıyor!"
            RoundPhase.SETTLEMENT -> if (roundState.lastRoundWon) "🎉 TEBRİKLER! Kuponunuz kazandı: +${roundState.lastRoundPayout} TP!" else "Maçlar sonuçlandı. Yeni hafta başlıyor..."
          },
          color = Color(0xFFCBD5E1),
          fontSize = 10.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    // Live Event Feed Ticker during match play
    if (roundState.liveEventsFeed.isNotEmpty()) {
      Spacer(modifier = Modifier.height(6.dp))
      Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = if (roundState.phase == RoundPhase.MATCHES_IN_PLAY) Color(0xFFDC2626) else TealDark,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = if (roundState.phase == RoundPhase.MATCHES_IN_PLAY) "CANLI AKIŞ" else "BÜLTEN",
              color = Color.White,
              fontSize = 8.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = roundState.liveEventsFeed.first(),
            color = Color(0xFFE2E8F0),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Synchronized Live Coupon Tracking Bar (when matches in play or settled)
    if (roundState.liveSlipItems.isNotEmpty()) {
      Spacer(modifier = Modifier.height(6.dp))
      Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (roundState.liveWinningPercentage >= 50) Color(0xFF10B981) else Color(0xFFEF4444)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f, fill = false),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "📊 CANLI KUPON TAKİBİ",
                color = GoldYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                softWrap = false
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🟢 ${roundState.liveWinningCount} Tutuyor • 🔴 ${roundState.liveLosingCount} Bekliyor",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
              color = if (roundState.liveWinningPercentage >= 50) Color(0xFF059669) else Color(0xFFB91C1C),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "%${roundState.liveWinningPercentage} Başarı",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Mini items status row
          androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(roundState.liveSlipItems.size) { idx ->
              val item = roundState.liveSlipItems[idx]
              val isWinning = item.status == com.example.service.SlipItemLiveStatus.CURRENTLY_WINNING || item.status == com.example.service.SlipItemLiveStatus.WON
              Surface(
                color = if (isWinning) Color(0xFF064E3B) else Color(0xFF450A0A),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isWinning) Color(0xFF10B981) else Color(0xFFEF4444))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = if (isWinning) "✓" else "✕",
                    color = if (isWinning) Color(0xFF6EE7B7) else Color(0xFFFCA5A5),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "${item.homeTeam.take(3)}-${item.awayTeam.take(3)}: ${item.currentScore} (${item.slipSelection.selectionName.take(4)})",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2. Sport Switcher Tabs
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf(
        null to "🏆 Tümü (22)",
        Sport.FOOTBALL to "⚽ Futbol (12)",
        Sport.BASKETBALL to "🏀 Basketbol (10)"
      ).forEach { (sport, label) ->
        val isSel = selectedSportFilter == sport
        Surface(
          color = if (isSel) TealDark else Color.White,
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealDark else Color(0xFFCBD5E1)),
          modifier = Modifier
            .weight(1f)
            .clickable { selectedSportFilter = sport }
        ) {
          Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSel) FontWeight.Black else FontWeight.Bold,
            color = if (isSel) Color.White else Color(0xFF334155),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 3. Responsive 22 Matches Grid
    Box(modifier = Modifier.weight(1f)) {
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(filteredMatches, key = { it.id }) { match ->
          VirtualMatchCard(
            match = match,
            activeSlip = roundState.activeSlip,
            phase = roundState.phase,
            onSelectOdd = { sel ->
              VirtualRoundManager.toggleSlipSelection(sel)
            }
          )
        }
      }
    }

    // 4. Floating Sanal Kuponum Bar (when betting is open)
    AnimatedVisibility(visible = roundState.activeSlip.isNotEmpty()) {
      Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp)
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
              text = "SANAL KUPON (${roundState.activeSlip.size} Maç)",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
            Text(
              text = "Oran: %.2f • Kazanç: %d TP".format(totalSlipOdds, (stake * totalSlipOdds).toLong()),
              color = Color.White,
              fontSize = 10.sp
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
              onClick = { VirtualRoundManager.clearSlip() },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(text = "Sil", fontSize = 10.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                if (roundState.phase != RoundPhase.BETTING_OPEN) {
                  userMessage = "⚠️ Maçlar başladı! Sonraki haftayı bekleyin."
                  return@Button
                }
                if (onDeductStake(stake, "Sanal 22 Maç Kuponu")) {
                  VirtualRoundManager.placeRoundBet(stake)
                  userMessage = "🎉 Kuponunuz onaylandı! Canlı maçlar eş zamanlı başlıyor..."
                }
              },
              enabled = roundState.phase == RoundPhase.BETTING_OPEN,
              colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(text = "Oyna ($stake TP)", color = TealDark, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun VirtualMatchCard(
  match: VirtualMatchItem,
  activeSlip: List<SlipSelection>,
  phase: RoundPhase,
  onSelectOdd: (SlipSelection) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      // Top info row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = match.league,
          fontSize = 9.sp,
          color = Color(0xFF64748B),
          fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (phase == RoundPhase.MATCHES_IN_PLAY) {
            Surface(color = Color(0xFFDC2626), shape = RoundedCornerShape(3.dp)) {
              Text(
                text = "${match.minute}'",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          } else if (match.isFinished) {
            Text(text = "MS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
          } else {
            Text(text = "Başlamadı", fontSize = 9.sp, color = Color(0xFF94A3B8))
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Teams and Score
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = match.homeTeam, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
          Text(text = match.awayTeam, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Surface(
          color = if (phase == RoundPhase.MATCHES_IN_PLAY) Color(0xFFFEF2F2) else Color(0xFFF1F5F9),
          shape = RoundedCornerShape(6.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (phase == RoundPhase.MATCHES_IN_PLAY) Color(0xFFF87171) else Color(0xFFE2E8F0))
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(text = "${match.homeScore}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            Text(text = "${match.awayScore}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Odds Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        listOf(
          "MS 1" to match.odd1,
          (if (match.sport == Sport.FOOTBALL) "MS X" else "İY 1") to match.oddX,
          "MS 2" to match.odd2
        ).forEach { (outcome, odd) ->
          val isSelected = activeSlip.any { it.matchId == match.id && it.selectionName.startsWith(outcome) }
          Surface(
            color = if (isSelected) TealDark else Color(0xFFF8FAFC),
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) TealDark else Color(0xFFCBD5E1)),
            modifier = Modifier
              .weight(1f)
              .clickable(enabled = phase == RoundPhase.BETTING_OPEN) {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = MarketType.MATCH_RESULT,
                    selectionId = "${match.id}_$outcome",
                    selectionName = "$outcome ($odd)",
                    odd = odd,
                    isLive = true
                  )
                )
              }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = outcome,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF475569)
              )
              Text(
                text = odd.formatOdd(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) GoldYellow else TealDark
              )
            }
          }
        }
      }
    }
  }
}
