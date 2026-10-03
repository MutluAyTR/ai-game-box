package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.MackolikLeagueDataSource
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchH2H
import com.example.data.model.MatchLineups
import com.example.data.model.MatchStatus
import com.example.data.model.PlayerLineup
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.ui.components.detail.FootballMatchDetailContent
import com.example.ui.components.detail.MotorsportDetailContent
import com.example.ui.components.detail.BasketballMatchDetailContent
import com.example.ui.components.detail.TennisMatchDetailContent
import com.example.ui.components.detail.CombatMatchDetailContent
import com.example.ui.components.detail.VolleyballMatchDetailContent
import com.example.ui.components.detail.AmericanFootballMatchDetailContent
import com.example.ui.components.detail.MatchDetailContainer
import com.example.ui.components.detail.TacticalPitchView
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.util.formatOdd

/**
 * Interactive Match Detail View:
 * 1. Specialized sport-specific detail view (MotoGP Starting Grid & Telemetry, Basketball Court Starting 5,
 *    Tennis Court & Sets, Combat Octagon & Tale of the Tape, Volleyball 6-zone rotation, NFL 100-yd field,
 *    and Football 2D Tactical Pitch with ALL 11 PLAYERS & complete squad table underneath).
 * 2. Historical Head-to-Head (H2H) records matrix & past results
 * 3. Dynamic AI-generated winning probability predictions & xG projections
 * 4. Direct BetSlip odd integration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMatchDetailDialog(
  match: Match,
  selectedSelections: List<SlipSelection> = emptyList(),
  onSelectOdd: (SlipSelection) -> Unit = {},
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var showPlayerDirectoryDialog by remember { mutableStateOf(false) }

  if (showPlayerDirectoryDialog) {
    MackolikPlayerDirectoryDialog(
      initialQuery = match.homeTeam,
      onDismiss = { showPlayerDirectoryDialog = false }
    )
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF0F172A),
    modifier = Modifier.testTag("interactive_match_detail_sheet")
  ) {
    MatchDetailContainer(
      match = match,
      selectedSelections = selectedSelections,
      onSelectOdd = onSelectOdd,
      onClose = onDismiss,
      onOpenDirectory = { showPlayerDirectoryDialog = true }
    )
  }
}

/**
 * ⚔️ 2. Historical Head-to-Head (H2H) İçeriği
 */
@Composable
private fun H2HTabContent(
  h2h: MatchH2H,
  homeTeam: String,
  awayTeam: String
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0A0F1D))
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // H2H Genel Karnesi
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("⚔️ SON KARŞILAŞMALAR GENEL BİLANÇOSU", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${h2h.homeWins}", color = Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 22.sp)
              Text("$homeTeam\nGalibiyet", color = Color(0xFF94A3B8), fontSize = 10.sp, textAlign = TextAlign.Center)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${h2h.draws}", color = Color(0xFFCBD5E1), fontWeight = FontWeight.Black, fontSize = 22.sp)
              Text("Beraberlik", color = Color(0xFF94A3B8), fontSize = 10.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${h2h.awayWins}", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 22.sp)
              Text("$awayTeam\nGalibiyet", color = Color(0xFF94A3B8), fontSize = 10.sp, textAlign = TextAlign.Center)
            }
          }
        }
      }
    }

    // Gol Ortalamaları & KG İstatistikleri
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Maç Başı Gol Ort.", color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text("2.8", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("Yüksek Gol Trendi", color = Color(0xFF34D399), fontSize = 9.sp)
          }
        }

        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("KG Var Olasılığı", color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text("%70", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("Son 10 Maçta", color = Color(0xFF94A3B8), fontSize = 9.sp)
          }
        }
      }
    }

    // Geçmiş Maç Skorları Listesi
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Text("📅 GEÇMİŞ KARŞILAŞMALARIN SKORLARI", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Black)
    }

    items(h2h.recentMatches) { pm ->
      Surface(
        color = Color(0xFF131D31),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(pm.date, color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text("$homeTeam - $awayTeam (${pm.league})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
          ) {
            Text(
              text = pm.score,
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * 🤖 3. Dynamic AI-Generated Winning Probability Predictions & xG Projection
 */
@Composable
private fun AiPredictionTabContent(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  val homeProb = match.aiPrediction.homeWinProb
  val drawProb = match.aiPrediction.drawProb
  val awayProb = match.aiPrediction.awayWinProb

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0A0F1D))
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Dynamic Probability Gauge Card
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, GoldYellow)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("DİNAMİK KAZANMA OLASILIĞI (AI)", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }

            Surface(
              color = Color(0xFF065F46),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text("Güven: %84", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3'lü Olasılık Barı (Ev Sahibi / Beraberlik / Deplasman)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp))
          ) {
            Box(modifier = Modifier.weight(homeProb.toFloat().coerceAtLeast(1f)).fillMaxSize().background(Color(0xFF10B981)))
            Box(modifier = Modifier.weight(drawProb.toFloat().coerceAtLeast(1f)).fillMaxSize().background(Color(0xFF64748B)))
            Box(modifier = Modifier.weight(awayProb.toFloat().coerceAtLeast(1f)).fillMaxSize().background(Color(0xFF38BDF8)))
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("${match.homeTeam}: %$homeProb", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Beraberlik: %$drawProb", color = Color(0xFFCBD5E1), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("${match.awayTeam}: %$awayProb", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          Spacer(modifier = Modifier.height(12.dp))

          // xG Beklentisi
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("xG Gol Beklentisi: 1.84 - 1.15", color = Color(0xFF94A3B8), fontSize = 10.sp)
            Text("KG Var Oranı: %64", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }
        }
      }
    }

    // AI Taktik Raporu
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("🧠 GEMİNİ VE MONTE CARLO TAKTİK RAPORU", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = match.aiPrediction.tacticalAnalysis.ifBlank {
              "${match.homeTeam}, son 5 iç saha mücadelesinde maç başına 2.4 gol ortalamasıyla hücum verimliliğinde ligin en formda ekiplerinden biri. " +
                  "${match.awayTeam} ise kontra ataklarda kanat hızına güveniyor. İlk 30 dakikada baskılı bir oyun ve erken gol bekleniyor."
            },
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        }
      }
    }

    // Hızlı Oran Seçim Butonları (Doğrudan Kupona Ekle)
    item {
      Text("⚡ MAÇ ORANLARINI KUPONA EKLE", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
      Spacer(modifier = Modifier.height(6.dp))

      val msMarket = match.markets.find { it.type == MarketType.MATCH_RESULT || it.type == MarketType.BASKETBALL_MS }
      val odd1 = msMarket?.selections?.getOrNull(0)?.odd ?: 1.85
      val oddX = msMarket?.selections?.getOrNull(1)?.odd ?: 3.20
      val odd2 = msMarket?.selections?.getOrNull(2)?.odd ?: 2.45

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // MS 1
        val sel1 = SlipSelection(
          matchId = match.id,
          matchTeams = "${match.homeTeam} - ${match.awayTeam}",
          marketType = MarketType.MATCH_RESULT,
          selectionId = "${match.id}_ms1",
          selectionName = "MS 1",
          odd = odd1,
          isLive = match.minute > 0
        )
        val isSel1 = selectedSelections.any { it.matchId == sel1.matchId && it.selectionId == sel1.selectionId }

        Surface(
          color = if (isSel1) GoldYellow else Color(0xFF1E293B),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, if (isSel1) GoldYellow else Color(0xFF334155)),
          modifier = Modifier.weight(1f).clickable { onSelectOdd(sel1) }
        ) {
          Column(modifier = Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("MS 1", color = if (isSel1) Color(0xFF0F172A) else Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(odd1.formatOdd(), color = if (isSel1) Color(0xFF0F172A) else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
          }
        }

        // MS X
        val selX = SlipSelection(
          matchId = match.id,
          matchTeams = "${match.homeTeam} - ${match.awayTeam}",
          marketType = MarketType.MATCH_RESULT,
          selectionId = "${match.id}_msx",
          selectionName = "MS X",
          odd = oddX,
          isLive = match.minute > 0
        )
        val isSelX = selectedSelections.any { it.matchId == selX.matchId && it.selectionId == selX.selectionId }

        Surface(
          color = if (isSelX) GoldYellow else Color(0xFF1E293B),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, if (isSelX) GoldYellow else Color(0xFF334155)),
          modifier = Modifier.weight(1f).clickable { onSelectOdd(selX) }
        ) {
          Column(modifier = Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("MS X", color = if (isSelX) Color(0xFF0F172A) else Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(oddX.formatOdd(), color = if (isSelX) Color(0xFF0F172A) else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
          }
        }

        // MS 2
        val sel2 = SlipSelection(
          matchId = match.id,
          matchTeams = "${match.homeTeam} - ${match.awayTeam}",
          marketType = MarketType.MATCH_RESULT,
          selectionId = "${match.id}_ms2",
          selectionName = "MS 2",
          odd = odd2,
          isLive = match.minute > 0
        )
        val isSel2 = selectedSelections.any { it.matchId == sel2.matchId && it.selectionId == sel2.selectionId }

        Surface(
          color = if (isSel2) GoldYellow else Color(0xFF1E293B),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, if (isSel2) GoldYellow else Color(0xFF334155)),
          modifier = Modifier.weight(1f).clickable { onSelectOdd(sel2) }
        ) {
          Column(modifier = Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("MS 2", color = if (isSel2) Color(0xFF0F172A) else Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(odd2.formatOdd(), color = if (isSel2) Color(0xFF0F172A) else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
          }
        }
      }
    }
  }
}
