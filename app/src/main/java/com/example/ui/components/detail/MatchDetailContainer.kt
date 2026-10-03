package com.example.ui.components.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.service.LiveTelemetryWebSocketService
import com.example.ui.components.MackolikPlayerDirectoryDialog
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.util.formatOdd

/**
 * MatchDetailContainer:
 * Flexible container that dynamically switches UI layouts based on the sport category:
 * - Specific HUD for WRC (Dünya Rallisi Özel Etap SS Telemetrisi & Orijinal Pilotlar: Ogier, Neuville, Tänak, Rovanperä)
 * - Specific HUD for MotoGP (Motosiklet Telemetrisi, Yatış Açısı & Orijinal Pilotlar: Bagnaia, Martin, Marquez)
 * - Specific HUD for F1 (Tur Telemetrisi, Hız, Devir, Sektörler & Pilotlar: Verstappen, Norris, Leclerc, Hamilton)
 * - Specific HUD for Tennis (Set-by-Set Progress Bars, Canlı Skor HUD, Break Point Alerts & 2D Kort)
 * - Specific HUD for Football (TacticalPitchView with dynamic formations 4-4-2, 3-5-2 & drag-and-drop planning)
 * - Real-time WebSocket Service Integration streaming low-latency telemetry updates
 * - Integrated D3 / Recharts-style graphing module for live match momentum shifts & player radar analysis
 */
@Composable
fun MatchDetailContainer(
  match: Match,
  selectedSelections: List<SlipSelection> = emptyList(),
  onSelectOdd: (SlipSelection) -> Unit = {},
  onClose: () -> Unit = {},
  onOpenDirectory: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var activeTab by remember { mutableIntStateOf(0) }
  val lineups = remember(match.id) { MackolikLeagueDataSource.getLineupsForMatch(match) }
  val h2h = remember(match.id) { MackolikLeagueDataSource.getH2HForMatch(match) }

  var selectedPlayer by remember { mutableStateOf<PlayerLineup?>(null) }
  var showDirectoryDialog by remember { mutableStateOf(false) }

  // Exact Series & Sport Detection
  val isWrc = match.sport == Sport.WRC_RALLY ||
      match.league.contains("WRC", ignoreCase = true) ||
      match.league.contains("Ralli", ignoreCase = true)

  val isMotoGP = match.sport == Sport.MOTOGP ||
      match.league.contains("MotoGP", ignoreCase = true) ||
      match.league.contains("Moto2", ignoreCase = true) ||
      match.league.contains("Moto3", ignoreCase = true)

  val isFormula1 = match.sport == Sport.FORMULA_1 ||
      match.sport == Sport.FORMULA_2 ||
      match.sport == Sport.FORMULA_3 ||
      match.league.contains("Formula", ignoreCase = true) ||
      match.league.contains("F1", ignoreCase = true)

  val isMotorsport = isWrc || isMotoGP || isFormula1 ||
      match.sport == Sport.MOTORSPORTS ||
      match.sport == Sport.NASCAR ||
      match.sport == Sport.INDYCAR ||
      match.league.contains("NASCAR", ignoreCase = true) ||
      match.league.contains("IndyCar", ignoreCase = true)

  val isTennis = match.sport == Sport.TENNIS ||
      match.sport == Sport.TABLE_TENNIS ||
      match.sport == Sport.PADEL ||
      match.sport == Sport.BADMINTON ||
      match.sport == Sport.SQUASH ||
      match.sport == Sport.PICKLEBALL ||
      match.league.contains("Tenis", ignoreCase = true) ||
      match.league.contains("WTA", ignoreCase = true) ||
      match.league.contains("ATP", ignoreCase = true)

  val isBasketball = match.sport == Sport.BASKETBALL ||
      match.sport == Sport.BASKETBALL_3X3 ||
      match.sport == Sport.E_BASKETBALL ||
      match.league.contains("Basketbol", ignoreCase = true) ||
      match.league.contains("NBA", ignoreCase = true) ||
      match.league.contains("EuroLeague", ignoreCase = true)

  val isCombat = match.sport == Sport.MMA_UFC ||
      match.sport == Sport.BOXING ||
      match.sport == Sport.KICKBOXING ||
      match.sport == Sport.MUAY_THAI ||
      match.sport == Sport.JUDO ||
      match.sport == Sport.KARATE ||
      match.sport == Sport.TAEKWONDO ||
      match.sport == Sport.WRESTLING ||
      match.league.contains("UFC", ignoreCase = true) ||
      match.league.contains("MMA", ignoreCase = true) ||
      match.league.contains("Boks", ignoreCase = true)

  val isVolleyball = match.sport == Sport.VOLLEYBALL ||
      match.sport == Sport.BEACH_VOLLEYBALL ||
      match.league.contains("Voleybol", ignoreCase = true) ||
      match.league.contains("Sultanlar", ignoreCase = true)

  val isAmericanFootball = match.sport == Sport.AMERICAN_FOOTBALL ||
      match.sport == Sport.RUGBY_UNION ||
      match.sport == Sport.RUGBY_LEAGUE ||
      match.league.contains("NFL", ignoreCase = true)

  val tabTitles = when {
    isWrc -> listOf("🏎️ WRC Etap & Sürücüler", "📈 D3 Maç Baskısı & Radar", "⚔️ Pilot H2H", "🤖 AI Etap Tahmini", "🎟️ Oranlar")
    isMotoGP -> listOf("🏍️ MotoGP Telemetri & Grid", "📈 D3 Maç Baskısı & Radar", "⚔️ Pilot H2H", "🤖 AI Podyum Tahmini", "🎟️ Oranlar")
    isFormula1 -> listOf("🏎️ F1 Telemetri & Grid", "📈 D3 Maç Baskısı & Radar", "⚔️ Pilot H2H", "🤖 AI Podyum Tahmini", "🎟️ Oranlar")
    isMotorsport -> listOf("🏁 Grid & Telemetri", "📈 D3 Maç Baskısı & Radar", "⚔️ Pilot H2H", "🤖 AI Tahmin", "🎟️ Oranlar")
    isTennis -> listOf("🎾 Set-by-Set & Kort HUD", "📈 D3 Maç Baskısı & Radar", "⚔️ Raket H2H", "🤖 AI Galibiyet Tahmini", "🎟️ Oranlar")
    isBasketball -> listOf("🏀 Parke & Kadro (İlk 5)", "📈 D3 Maç Baskısı & Radar", "⚔️ H2H Geçmişi", "🤖 AI Tahmini", "🎟️ Oranlar")
    isCombat -> listOf("🥊 Oktagon / Ring HUD", "📈 D3 Maç Baskısı & Radar", "⚔️ H2H Geçmişi", "🤖 AI Nakavt Tahmini", "🎟️ Oranlar")
    isVolleyball -> listOf("🏐 Saha Rotasyonu HUD", "📈 D3 Maç Baskısı & Radar", "⚔️ H2H Geçmişi", "🤖 AI Tahmini", "🎟️ Oranlar")
    isAmericanFootball -> listOf("🏈 100-Yd Saha HUD", "📈 D3 Maç Baskısı & Radar", "⚔️ H2H Geçmişi", "🤖 AI Tahmini", "🎟️ Oranlar")
    else -> listOf("📋 Taktik Saha & İlk 11", "📈 D3 Maç Baskısı & Radar", "⚔️ H2H Geçmişi", "🤖 AI Olasılık Tahmini", "🎟️ Oranlar")
  }

  if (showDirectoryDialog) {
    MackolikPlayerDirectoryDialog(
      initialQuery = match.homeTeam,
      onDismiss = { showDirectoryDialog = false }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0A0F1D))
      .testTag("match_detail_container")
  ) {
    // 1. Top Header Bar with Exact Series Pill
    MatchDetailHeaderBar(
      match = match,
      isWrc = isWrc,
      isMotoGP = isMotoGP,
      isFormula1 = isFormula1,
      isMotorsport = isMotorsport,
      isBasketball = isBasketball,
      isTennis = isTennis,
      isCombat = isCombat,
      isVolleyball = isVolleyball,
      onClose = onClose
    )

    // 2. Tab Navigation Row
    TabRow(
      selectedTabIndex = activeTab,
      containerColor = Color(0xFF0F172A),
      contentColor = GoldYellow,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
          color = GoldYellow
        )
      }
    ) {
      tabTitles.forEachIndexed { index, title ->
        Tab(
          selected = activeTab == index,
          onClick = { activeTab = index },
          text = {
            Text(
              text = title,
              fontSize = 10.5.sp,
              fontWeight = if (activeTab == index) FontWeight.Black else FontWeight.Bold,
              color = if (activeTab == index) GoldYellow else Color(0xFF94A3B8)
            )
          }
        )
      }
    }

    // 3. Dynamic Tab Content
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      when (activeTab) {
        0 -> {
          // Dynamic Sport Specific HUD & Primary View
          when {
            isMotorsport -> MotorsportTelemetryAndGridHUD(
              match = match,
              isWrc = isWrc,
              isMotoGP = isMotoGP,
              isFormula1 = isFormula1,
              onOpenDirectory = {
                if (onOpenDirectory != null) onOpenDirectory() else showDirectoryDialog = true
              }
            )
            isTennis -> TennisSetBySetProgressHUD(
              match = match
            )
            isBasketball -> BasketballMatchDetailContent(
              match = match,
              lineups = lineups,
              selectedPlayer = selectedPlayer,
              onPlayerClick = { selectedPlayer = it }
            )
            isCombat -> CombatMatchDetailContent(match = match)
            isVolleyball -> VolleyballMatchDetailContent(
              match = match,
              onOpenDirectory = {
                if (onOpenDirectory != null) onOpenDirectory() else showDirectoryDialog = true
              }
            )
            isAmericanFootball -> AmericanFootballMatchDetailContent(
              match = match,
              onOpenDirectory = {
                if (onOpenDirectory != null) onOpenDirectory() else showDirectoryDialog = true
              }
            )
            else -> FootballTacticalContainerContent(
              match = match,
              lineups = lineups,
              selectedPlayer = selectedPlayer,
              onPlayerClick = { selectedPlayer = it },
              onOpenDirectory = {
                if (onOpenDirectory != null) onOpenDirectory() else showDirectoryDialog = true
              }
            )
          }
        }
        1 -> {
          // Interactive D3 / Recharts-style Graphing Module
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              MatchMomentumAndPerformanceChart(match = match)
            }
          }
        }
        2 -> DetailH2HTabContent(
          h2h = h2h,
          homeTeam = match.homeTeam,
          awayTeam = match.awayTeam
        )
        3 -> DetailAiPredictionTabContent(
          match = match,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd
        )
        4 -> DetailOddsTabContent(
          match = match,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd
        )
      }
    }
  }
}

/**
 * Top Header Bar for MatchDetailContainer
 */
@Composable
private fun MatchDetailHeaderBar(
  match: Match,
  isWrc: Boolean,
  isMotoGP: Boolean,
  isFormula1: Boolean,
  isMotorsport: Boolean,
  isBasketball: Boolean,
  isTennis: Boolean,
  isCombat: Boolean,
  isVolleyball: Boolean,
  onClose: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0F172A))
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        val seriesTitle = when {
          isWrc -> "🏎️ WRC Dünya Rallisi"
          isMotoGP -> "🏍️ MotoGP Grand Prix"
          isFormula1 -> "🏎️ Formula 1"
          else -> "${match.sport.iconRes} ${match.league}"
        }

        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = seriesTitle,
            color = GoldYellow,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        val liveStatusText = when {
          isWrc -> "🏁 Canlı WRC Etap Telemetrisi"
          isMotoGP -> "🏍️ Canlı MotoGP Telemetrisi"
          isFormula1 -> "🏎️ Canlı F1 Telemetrisi"
          isMotorsport -> "🏁 Canlı Motor Sporları Telemetrisi"
          isBasketball -> if (match.minute > 0) "🏀 ${match.minute}' Canlı Periyot" else "🕒 ${match.startTime} TSİ"
          isTennis -> "🎾 Canlı Set & Game HUD"
          isCombat -> "🥊 Round 1/5 Şampiyonluk Maçı"
          isVolleyball -> "🏐 Canlı Set Skorları"
          match.status == MatchStatus.FINISHED -> "🏁 MS (Bitti)"
          match.minute >= 90 && match.currentExtraMinute > 0 -> "🟢 90+${match.currentExtraMinute}' Canlı"
          match.minute > 0 -> "🟢 ${match.minute}' Canlı"
          else -> "🕒 ${match.startTime} TSİ"
        }
        Text(
          text = liveStatusText,
          color = if (match.status == MatchStatus.FINISHED) GoldYellow else if (match.minute > 0 || isMotorsport || isTennis) Color(0xFF4ADE80) else Color(0xFF94A3B8),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = "${match.homeTeam} vs ${match.awayTeam}",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 15.5.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    IconButton(onClick = onClose) {
      Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
    }
  }
}

/**
 * Specialized Motorsport & Racing Telemetry HUD (WRC vs MotoGP vs F1 strictly separated)
 */
@Composable
fun MotorsportTelemetryAndGridHUD(
  match: Match,
  isWrc: Boolean,
  isMotoGP: Boolean,
  isFormula1: Boolean,
  onOpenDirectory: () -> Unit = {}
) {
  var selectedPilotIdx by remember { mutableIntStateOf(0) }

  // WebSocket Live Telemetry Streaming
  val liveTelemetry by LiveTelemetryWebSocketService.getTelemetryFlowForMatch(match).collectAsState()

  // Authentic driver/rider rosters strictly differentiated by series
  val riders = remember(match.id, isWrc, isMotoGP, isFormula1) {
    when {
      isWrc -> listOf(
        GridRider(1, 17, "Sébastien Ogier", "Toyota Gazoo Racing", "GR Yaris Rally1 Hybrid", "12:44.2", "LİDER", "Gravel (Çakıl)", 192.4, 191, Color(0xFFEF4444)),
        GridRider(2, 11, "Thierry Neuville", "Hyundai Shell Mobis", "i20 N Rally1 Hybrid", "12:46.5", "+2.3s", "Gravel (Çakıl)", 190.8, 225, Color(0xFF38BDF8)),
        GridRider(3, 8, "Ott Tänak", "Hyundai Shell Mobis", "i20 N Rally1 Hybrid", "12:47.9", "+3.7s", "Soft Çakıl", 191.2, 200, Color(0xFF38BDF8)),
        GridRider(4, 69, "Kalle Rovanperä", "Toyota Gazoo Racing", "GR Yaris Rally1", "12:48.4", "+4.2s", "Medium Çakıl", 193.1, 114, Color(0xFFEF4444)),
        GridRider(5, 33, "Elfyn Evans", "Toyota Gazoo Racing", "GR Yaris Rally1", "12:51.0", "+6.8s", "Hard Çakıl", 189.5, 160, Color(0xFFEF4444)),
        GridRider(6, 16, "Adrien Fourmaux", "M-Sport Ford WRT", "Puma Rally1 Hybrid", "12:53.2", "+9.0s", "Medium Çakıl", 188.4, 130, Color(0xFF2563EB)),
        GridRider(7, 18, "Takamoto Katsuta", "Toyota Gazoo Racing", "GR Yaris Rally1", "12:55.8", "+11.6s", "Soft Çakıl", 189.0, 86, Color(0xFFEF4444)),
        GridRider(8, 4, "Esapekka Lappi", "Hyundai Shell Mobis", "i20 N Rally1", "12:58.1", "+13.9s", "Hard Çakıl", 187.9, 33, Color(0xFF38BDF8))
      )
      isFormula1 -> listOf(
        GridRider(1, 1, "Max Verstappen", "Red Bull Racing", "RB20 - Honda RBPT", "1:21.845", "POLE", "Medium", 348.5, 429, Color(0xFF1E3A8A)),
        GridRider(2, 4, "Lando Norris", "McLaren F1 Team", "MCL38 - Mercedes", "1:21.912", "+0.067s", "Medium", 346.8, 349, Color(0xFFFF8000)),
        GridRider(3, 16, "Charles Leclerc", "Scuderia Ferrari", "SF-24 - Ferrari", "1:22.015", "+0.170s", "Hard", 349.2, 319, Color(0xFFDC2626)),
        GridRider(4, 81, "Oscar Piastri", "McLaren F1 Team", "MCL38 - Mercedes", "1:22.180", "+0.335s", "Medium", 345.9, 292, Color(0xFFFF8000)),
        GridRider(5, 55, "Carlos Sainz", "Scuderia Ferrari", "SF-24 - Ferrari", "1:22.250", "+0.405s", "Hard", 347.1, 258, Color(0xFFDC2626)),
        GridRider(6, 44, "Lewis Hamilton", "Mercedes-AMG Petronas", "W15 - Mercedes", "1:22.310", "+0.465s", "Soft", 344.8, 200, Color(0xFF00D2BE)),
        GridRider(7, 63, "George Russell", "Mercedes-AMG Petronas", "W15 - Mercedes", "1:22.420", "+0.575s", "Medium", 345.2, 210, Color(0xFF00D2BE)),
        GridRider(8, 14, "Fernando Alonso", "Aston Martin Aramco", "AMR24 - Mercedes", "1:22.580", "+0.735s", "Hard", 343.9, 62, Color(0xFF10B981))
      )
      else -> listOf(
        // Authentic MotoGP World Championship Riders
        GridRider(1, 1, "Francesco Bagnaia", "Ducati Lenovo Team", "Desmosedici GP24", "1:44.855", "POLE", "Soft", 356.4, 498, Color(0xFFDC2626)),
        GridRider(2, 89, "Jorge Martín", "Prima Pramac Racing", "Desmosedici GP24", "1:44.933", "+0.078s", "Medium", 354.8, 508, Color(0xFF8B5CF6)),
        GridRider(3, 93, "Marc Márquez", "Gresini Racing MotoGP", "Desmosedici GP23", "1:44.979", "+0.124s", "Soft", 355.2, 392, Color(0xFF38BDF8)),
        GridRider(4, 23, "Enea Bastianini", "Ducati Lenovo Team", "Desmosedici GP24", "1:45.070", "+0.215s", "Medium", 355.8, 368, Color(0xFFDC2626)),
        GridRider(5, 31, "Pedro Acosta", "Red Bull GASGAS Tech3", "KTM RC16", "1:45.195", "+0.340s", "Hard", 352.0, 215, Color(0xFFEF4444)),
        GridRider(6, 12, "Maverick Viñales", "Aprilia Racing", "RS-GP24", "1:45.265", "+0.410s", "Medium", 351.4, 162, Color(0xFF10B981)),
        GridRider(7, 33, "Brad Binder", "Red Bull KTM Factory Racing", "KTM RC16", "1:45.310", "+0.455s", "Soft", 353.6, 153, Color(0xFFF97316)),
        GridRider(8, 20, "Fabio Quartararo", "Monster Energy Yamaha", "YZR-M1", "1:45.420", "+0.565s", "Medium", 347.8, 73, Color(0xFF1E3A8A))
      )
    }
  }

  val activeRider = riders.getOrElse(selectedPilotIdx) { riders[0] }

  // Dynamic Live WebSocket Values
  val dynamicSpeed = liveTelemetry.f1Update?.speedKmh ?: activeRider.topSpeed
  val dynamicRpm = liveTelemetry.f1Update?.rpm ?: 12450
  val dynamicGear = liveTelemetry.f1Update?.gear ?: 7
  val dynamicThrottle = liveTelemetry.f1Update?.throttlePct ?: 96
  val dynamicBrake = liveTelemetry.f1Update?.brakePct ?: 0
  val dynamicDelta = if (selectedPilotIdx == 0) (if (isWrc) "LİDER" else "POLE") else (liveTelemetry.f1Update?.deltaToLeader ?: activeRider.gap)
  val dynamicLapTime = if (selectedPilotIdx == 0) (liveTelemetry.f1Update?.currentLapTime ?: activeRider.lapTime) else activeRider.lapTime

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 0. WebSocket Status Banner
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
          .border(1.dp, Color(0xFF059669), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFF10B981),
            shape = CircleShape,
            modifier = Modifier.size(8.dp)
          ) {}
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CANLI WEBSOCKET AKIŞI: ${liveTelemetry.latencyMs}ms | ${liveTelemetry.fps} FPS",
            color = Color(0xFF4ADE80),
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Black
          )
        }

        Text(
          text = if (isWrc) "WRC SS14 POWER STAGE" else if (isMotoGP) "MOTOGP Q2 POLE FIGHT" else "F1 GRAND PRIX",
          color = GoldYellow,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // 1. Live Race Director & Telemetry Status Card
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = Color(0xFF059669),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = if (isWrc) "🟢 ETAP AÇIK" else "🟢 TRACK CLEAR",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isWrc) "Çakıl / Asfalt Geçişi • 4x4 Hibrit Boost Aktif" else "DRS Bölgesi 2 Aktif • Sektör 1-2-3 Canlı",
              color = Color(0xFF94A3B8),
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = if (isWrc) "ETAP 14 / 22" else "TUR 34 / 53",
              color = GoldYellow,
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }

    // 2. Telemetry HUD Gauges (Speed, RPM, Gear, Throttle/Brake)
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        border = BorderStroke(1.5.dp, GoldYellow)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              val telemetryHeader = when {
                isWrc -> "🏎️ WRC RALLİ CANLI ETAP TELEMETRİSİ"
                isMotoGP -> "🏍️ MOTOGP CANLI PİST TELEMETRİSİ"
                else -> "🏎️ F1 CANLI TUR TELEMETRİSİ"
              }
              Text(telemetryHeader, color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Black)
              Text("${activeRider.name} (#${activeRider.number}) • ${activeRider.team}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Surface(
              color = activeRider.teamColor,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = dynamicDelta,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Telemetry Gauges Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Speedometer Gauge
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(if (isWrc) "ETAP HIZI" else "HIZ GÖSTERGESİ", color = Color(0xFF94A3B8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${dynamicSpeed.toInt()}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("KM/S", color = GoldYellow, fontSize = 8.sp, fontWeight = FontWeight.Black)
                LinearProgressIndicator(
                  progress = { (dynamicSpeed / 370.0).toFloat().coerceIn(0f, 1f) },
                  color = GoldYellow,
                  trackColor = Color(0xFF374151),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                )
              }
            }

            // Gear & RPM / Lean Angle
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(if (isMotoGP) "VİTES & YATIŞ" else "VİTES & DEVİR", color = Color(0xFF94A3B8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text("$dynamicGear. VİTES", color = Color(0xFF38BDF8), fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text(if (isMotoGP) "Yatış: 64° Sağ" else "$dynamicRpm RPM", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                  progress = { (dynamicRpm / 14000f).coerceIn(0f, 1f) },
                  color = Color(0xFF38BDF8),
                  trackColor = Color(0xFF374151),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                )
              }
            }

            // Throttle & Brake
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(if (isWrc) "GAZ / EL FRENİ" else "GAZ / FREN", color = Color(0xFF94A3B8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Gaz", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                  Text("%$dynamicThrottle", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
                LinearProgressIndicator(
                  progress = { dynamicThrottle / 100f },
                  color = Color(0xFF10B981),
                  trackColor = Color(0xFF374151),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Fren", color = Color(0xFFEF4444), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                  Text("%$dynamicBrake", color = Color(0xFFEF4444), fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
                LinearProgressIndicator(
                  progress = { dynamicBrake / 100f },
                  color = Color(0xFFEF4444),
                  trackColor = Color(0xFF374151),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Sector times
          Text(if (isWrc) "⏱️ ARA ZAMANLAR (SPLIT 1 • 2 • 3)" else "🏁 SEKTÖR ZAMANLARI (S1 • S2 • S3)", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            SectorBadge(sector = if (isWrc) "SPLIT 1" else "SEKTÖR 1", time = "27.420s", isPurple = true, isGreen = false, modifier = Modifier.weight(1f))
            SectorBadge(sector = if (isWrc) "SPLIT 2" else "SEKTÖR 2", time = "26.890s", isPurple = false, isGreen = true, modifier = Modifier.weight(1f))
            SectorBadge(sector = if (isWrc) "SPLIT 3" else "SEKTÖR 3", time = "27.535s", isPurple = false, isGreen = false, modifier = Modifier.weight(1f))
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Tyre & Stage Strategy
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(if (isWrc) "🛞 WRC ÇAKIL / ASFALT LASTİK STRATEJİSİ" else "🛞 LASTİK VE PİT STRATEJİSİ", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(if (isWrc) "Etap Seçimi: Sert Çakıl" else "Pit Penceresi: Tur 22-26", color = GoldYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            TyreWearIndicator(if (isWrc) "Yumuşak Çakıl" else "Yumuşak (Soft)", "12 Tur", 0.42f, Color(0xFFEF4444), modifier = Modifier.weight(1f))
            TyreWearIndicator(if (isWrc) "Orta Çakıl" else "Orta (Medium)", "24 Tur", 0.72f, Color(0xFFEAB308), modifier = Modifier.weight(1f))
            TyreWearIndicator(if (isWrc) "Sert Çakıl" else "Sert (Hard)", "Kullanılmadı", 1.0f, Color.White, modifier = Modifier.weight(1f))
          }
        }
      }
    }

    // 3. Grid / Stage Ranking Table with Original Pilot/Driver Names
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          val tableTitle = when {
            isWrc -> "🏁 WRC ÖZEL ETAP SIRALAMASI & GAP"
            isMotoGP -> "🏁 MOTOGP STARTING GRID & PİLOT SIRALAMASI"
            else -> "🏁 F1 STARTING GRID & PİLOT SIRALAMASI"
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(tableTitle, color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Text("Pilot seçerek telemetriyi inceleyin", color = Color(0xFF94A3B8), fontSize = 8.5.sp)
          }

          Spacer(modifier = Modifier.height(8.dp))

          riders.forEachIndexed { idx, rider ->
            val isSelected = selectedPilotIdx == idx
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .background(
                  if (isSelected) Color(0xFF334155) else Color(0xFF0F172A),
                  shape = RoundedCornerShape(8.dp)
                )
                .border(
                  width = 1.dp,
                  color = if (isSelected) GoldYellow else Color.Transparent,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable { selectedPilotIdx = idx }
                .padding(horizontal = 8.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = if (rider.position == 1) GoldYellow else Color(0xFF1E293B),
                  shape = CircleShape,
                  modifier = Modifier.size(22.dp)
                ) {
                  Text(
                    text = "${rider.position}",
                    color = if (rider.position == 1) Color.Black else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 3.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(rider.name, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("#${rider.number}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                  }
                  Text("${rider.team} • ${rider.bikeOrCar}", color = Color(0xFF94A3B8), fontSize = 8.5.sp)
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                  Text(if (idx == 0) dynamicLapTime else rider.lapTime, color = GoldYellow, fontSize = 10.5.sp, fontWeight = FontWeight.Black)
                  Text(if (idx == 0) (if (isWrc) "LİDER" else "POLE") else rider.gap, color = Color(0xFF38BDF8), fontSize = 8.5.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  color = when (rider.tyre.take(1)) {
                    "S", "Y" -> Color(0xFFEF4444)
                    "M", "O" -> Color(0xFFEAB308)
                    else -> Color.White
                  },
                  shape = CircleShape,
                  modifier = Modifier.size(16.dp)
                ) {
                  Text(
                    text = rider.tyre.take(1),
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Specialized Tennis Set-by-Set Progress Bars HUD
 */
@Composable
fun TennisSetBySetProgressHUD(
  match: Match
) {
  // Live WebSocket Telemetry for Tennis
  val liveTelemetry by LiveTelemetryWebSocketService.getTelemetryFlowForMatch(match).collectAsState()
  val tennisUpdate = liveTelemetry.tennisUpdate

  val set1H = tennisUpdate?.set1Home ?: 6
  val set1A = tennisUpdate?.set1Away ?: 4
  val set2H = tennisUpdate?.set2Home ?: 4
  val set2A = tennisUpdate?.set2Away ?: 6
  val set3H = tennisUpdate?.set3Home ?: 4
  val set3A = tennisUpdate?.set3Away ?: 3

  val liveGameText = if (tennisUpdate != null) {
    "${tennisUpdate.homeGameScore} - ${tennisUpdate.awayGameScore}"
  } else "40 - 30"

  val serveSpeed = tennisUpdate?.lastServeSpeedKmh ?: 212

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Break point notice banner if active
    if (tennisUpdate?.isBreakPoint == true) {
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF7F1D1D), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.FlashOn, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = tennisUpdate.breakPointNotice ?: "🔥 BREAK POINT FIRSATI! (Servis Kırma)",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    // 1. Live Set-by-Set Progress Bars Card
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.5.dp, GoldYellow)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🎾 SET-BY-SET CANLI İLERLEME ÇUBUKLARI", color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "OYUN: $liveGameText (Servis: ${serveSpeed} km/s)",
                color = Color(0xFF10B981),
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Set 1 Progress Bar
          TennisSetProgressBar(
            setTitle = "1. SET",
            homeScore = set1H,
            awayScore = set1A,
            homePlayer = match.homeTeam,
            awayPlayer = match.awayTeam,
            isFinished = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Set 2 Progress Bar
          TennisSetProgressBar(
            setTitle = "2. SET",
            homeScore = set2H,
            awayScore = set2A,
            homePlayer = match.homeTeam,
            awayPlayer = match.awayTeam,
            isFinished = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Set 3 Progress Bar (Live ongoing)
          TennisSetProgressBar(
            setTitle = "3. SET (CANLI)",
            homeScore = set3H,
            awayScore = set3A,
            homePlayer = match.homeTeam,
            awayPlayer = match.awayTeam,
            isFinished = false
          )
        }
      }
    }

    // 2. Tennis Court 2D Representation
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
        border = BorderStroke(1.5.dp, Color(0xFF38BDF8)),
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRect(color = Color(0x99FFFFFF), topLeft = Offset(14f, 14f), size = Size(w - 28f, h - 28f), style = Stroke(2.5f))
            drawLine(color = Color(0x88FFFFFF), start = Offset(w * 0.2f, 14f), end = Offset(w * 0.2f, h - 14f), strokeWidth = 1.5f)
            drawLine(color = Color(0x88FFFFFF), start = Offset(w * 0.8f, 14f), end = Offset(w * 0.8f, h - 14f), strokeWidth = 1.5f)
            drawLine(color = Color.White, start = Offset(10f, h / 2), end = Offset(w - 10f, h / 2), strokeWidth = 3f)
            drawLine(color = Color(0x88FFFFFF), start = Offset(w / 2, h * 0.25f), end = Offset(w / 2, h * 0.75f), strokeWidth = 1.5f)
            drawLine(color = Color(0x88FFFFFF), start = Offset(w * 0.2f, h * 0.25f), end = Offset(w * 0.8f, h * 0.25f), strokeWidth = 1.5f)
            drawLine(color = Color(0x88FFFFFF), start = Offset(w * 0.2f, h * 0.75f), end = Offset(w * 0.8f, h * 0.75f), strokeWidth = 1.5f)

            // Live bounce spot
            drawCircle(color = GoldYellow, radius = 6f, center = Offset(w * 0.42f, h * 0.65f))
            drawCircle(color = Color(0x66FACC15), radius = 14f, center = Offset(w * 0.42f, h * 0.65f), style = Stroke(1.5f))
          }

          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Surface(
              color = Color(0xCC0F172A),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, GoldYellow),
              modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
              Text(
                text = "🎾 ${match.homeTeam} (Serviste • $serveSpeed km/s)",
                color = GoldYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
              )
            }

            Surface(
              color = Color(0x88000000),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
              Text(
                text = "FİLE (NET)",
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Surface(
              color = Color(0xCC0F172A),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, Color(0xFF38BDF8)),
              modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
              Text(
                text = "${match.awayTeam} (Karşılayan)",
                color = Color(0xFF38BDF8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
              )
            }
          }
        }
      }
    }

    // 3. Serve, Break Points & Match Metrics
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("📊 SERVİS VE MAÇ METRİKLERİ HUD", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(10.dp))

          ComparativeProgressBar(
            title = "1. Servis Başarısı",
            homeLabel = "%68",
            awayLabel = "%64",
            homeRatio = 0.68f / (0.68f + 0.64f)
          )

          Spacer(modifier = Modifier.height(8.dp))

          ComparativeProgressBar(
            title = "Servis Kırma (Break Points)",
            homeLabel = "2/4 (%50)",
            awayLabel = "1/3 (%33)",
            homeRatio = 0.60f
          )

          Spacer(modifier = Modifier.height(8.dp))

          ComparativeProgressBar(
            title = "Ace Sayısı",
            homeLabel = "11",
            awayLabel = "8",
            homeRatio = 11f / 19f
          )

          Spacer(modifier = Modifier.height(8.dp))

          ComparativeProgressBar(
            title = "Kazanılan Puanlar Toplamı",
            homeLabel = "84",
            awayLabel = "76",
            homeRatio = 84f / 160f
          )
        }
      }
    }
  }
}

/**
 * Tennis set progress bar with game scores and visual ratio
 */
@Composable
private fun TennisSetProgressBar(
  setTitle: String,
  homeScore: Int,
  awayScore: Int,
  homePlayer: String,
  awayPlayer: String,
  isFinished: Boolean
) {
  val total = (homeScore + awayScore).coerceAtLeast(1)
  val homeRatio = (homeScore.toFloat() / total.toFloat()).coerceIn(0.05f, 0.95f)

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = setTitle,
          color = if (isFinished) Color(0xFF94A3B8) else Color(0xFF10B981),
          fontSize = 10.sp,
          fontWeight = FontWeight.Black
        )
        if (!isFinished) {
          Spacer(modifier = Modifier.width(4.dp))
          Surface(color = Color(0xFF10B981), shape = CircleShape, modifier = Modifier.size(6.dp)) {}
        }
      }
      Text(
        text = "$homeScore - $awayScore Oyun",
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(Color(0xFF0F172A))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(homeRatio)
          .fillMaxSize()
          .background(GoldYellow)
      )
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF38BDF8))
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "${homePlayer.take(12)} ($homeScore)",
        color = GoldYellow,
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "($awayScore) ${awayPlayer.take(12)}",
        color = Color(0xFF38BDF8),
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

/**
 * Comparative progress bar for sports metrics
 */
@Composable
private fun ComparativeProgressBar(
  title: String,
  homeLabel: String,
  awayLabel: String,
  homeRatio: Float
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(homeLabel, color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Black)
      Text(title, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
      Text(awayLabel, color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Black)
    }
    Spacer(modifier = Modifier.height(3.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(Color(0xFF0F172A))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(homeRatio.coerceIn(0.05f, 0.95f))
          .fillMaxSize()
          .background(GoldYellow)
      )
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF38BDF8))
      )
    }
  }
}

/**
 * Sector badge for Lap Telemetry
 */
@Composable
private fun SectorBadge(
  sector: String,
  time: String,
  isPurple: Boolean,
  isGreen: Boolean,
  modifier: Modifier = Modifier
) {
  val badgeColor = when {
    isPurple -> Color(0xFF8B5CF6)
    isGreen -> Color(0xFF10B981)
    else -> Color(0xFFEAB308)
  }

  Surface(
    color = Color(0xFF1F2937),
    shape = RoundedCornerShape(6.dp),
    border = BorderStroke(1.dp, badgeColor),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(sector, color = Color(0xFF94A3B8), fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(time, color = badgeColor, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
  }
}

/**
 * Tyre wear meter
 */
@Composable
private fun TyreWearIndicator(
  title: String,
  laps: String,
  healthRatio: Float,
  color: Color,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .background(Color(0xFF1F2937), RoundedCornerShape(6.dp))
      .padding(6.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Surface(color = color, shape = CircleShape, modifier = Modifier.size(6.dp)) {}
      Spacer(modifier = Modifier.width(4.dp))
      Text(title, color = Color.White, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
    Text(laps, color = Color(0xFF94A3B8), fontSize = 7.sp)
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { healthRatio },
      color = color,
      trackColor = Color(0xFF374151),
      modifier = Modifier
        .fillMaxWidth()
        .height(3.dp)
        .clip(RoundedCornerShape(1.5.dp))
    )
  }
}

/**
 * Football Tactical Container Content:
 * Integrates TacticalPitchView with dynamic formation switching & drag-and-drop planning,
 * followed by the complete Starting 11 squad table positioned strictly underneath the pitch.
 */
@Composable
private fun FootballTacticalContainerContent(
  match: Match,
  lineups: MatchLineups,
  selectedPlayer: PlayerLineup?,
  onPlayerClick: (PlayerLineup) -> Unit,
  onOpenDirectory: () -> Unit
) {
  var selectedTeamTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Away
  val activeTeamLineup = if (selectedTeamTab == 1) lineups.away else lineups.home
  val activeTeamName = if (selectedTeamTab == 1) match.awayTeam else match.homeTeam
  val isHomeTeam = selectedTeamTab != 1

  var dynamicFormation by remember(activeTeamLineup.formation) { mutableStateOf(activeTeamLineup.formation) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Team Selector Chips
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedTeamTab == 0,
          onClick = {
            selectedTeamTab = 0
            dynamicFormation = lineups.home.formation
          },
          label = {
            Text("🏠 ${match.homeTeam} (${lineups.home.formation})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GoldYellow,
            selectedLabelColor = Color(0xFF0F172A),
            containerColor = Color(0xFF1E293B),
            labelColor = Color.White
          ),
          border = BorderStroke(1.dp, if (selectedTeamTab == 0) GoldYellow else Color(0xFF334155)),
          modifier = Modifier.weight(1f)
        )

        FilterChip(
          selected = selectedTeamTab == 1,
          onClick = {
            selectedTeamTab = 1
            dynamicFormation = lineups.away.formation
          },
          label = {
            Text("✈️ ${match.awayTeam} (${lineups.away.formation})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF38BDF8),
            selectedLabelColor = Color(0xFF0F172A),
            containerColor = Color(0xFF1E293B),
            labelColor = Color.White
          ),
          border = BorderStroke(1.dp, if (selectedTeamTab == 1) Color(0xFF38BDF8) else Color(0xFF334155)),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 2. THE TACTICAL PITCH VIEW WITH DRAG-AND-DROP PLANNING
    item {
      TacticalPitchView(
        formation = dynamicFormation,
        teamName = activeTeamName,
        players = activeTeamLineup.starters,
        isHome = isHomeTeam,
        onFormationChanged = { newForm -> dynamicFormation = newForm },
        onPlayerClick = onPlayerClick
      )
    }

    // 3. STARTING 11 SQUAD TABLE STRICTLY UNDERNEATH THE PITCH ("sahayı takımın altında olsun takım 11 i")
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "📋 $activeTeamName — İLK 11 KADROSU",
                color = if (isHomeTeam) GoldYellow else Color(0xFF38BDF8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "Teknik Direktör: ${activeTeamLineup.coach}",
                color = Color(0xFF94A3B8),
                fontSize = 9.sp
              )
            }

            Surface(
              color = TealDark,
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.clickable { onOpenDirectory() }
            ) {
              Text(
                text = "Keşfet 🔍",
                color = GoldYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Squad Table Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
              .padding(vertical = 5.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("NO / POZ", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(55.dp))
            Text("FUTBOLCU", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("PUAN", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp), textAlign = TextAlign.Center)
            Text("DEĞER", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp), textAlign = TextAlign.End)
          }

          // Starting 11 Players
          activeTeamLineup.starters.take(11).forEachIndexed { idx, player ->
            val isEven = idx % 2 == 0
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(if (isEven) Color(0xFF1E293B) else Color(0xFF182234))
                .clickable { onPlayerClick(player) }
                .padding(vertical = 6.dp, horizontal = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.width(55.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  color = if (isHomeTeam) Color(0xFFDC2626) else Color(0xFF2563EB),
                  shape = CircleShape,
                  modifier = Modifier.size(18.dp)
                ) {
                  Text(
                    text = "${player.number}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 1.dp)
                  )
                }
                Spacer(modifier = Modifier.width(4.dp))
                DetailPositionBadge(position = player.position)
              }

              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = player.name,
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                if (player.isCaptain) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Surface(color = GoldYellow, shape = RoundedCornerShape(2.dp)) {
                    Text("K", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 2.dp))
                  }
                }
              }

              Text(
                text = "★ ${player.rating}",
                color = GoldYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(42.dp),
                textAlign = TextAlign.Center
              )

              Text(
                text = player.marketValue,
                color = Color(0xFF38BDF8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.End
              )
            }
          }

          // Substitutes Header
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "🔄 YEDEKLER / KULÜBE (${activeTeamLineup.substitutes.size} Oyuncu)",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
          )
          Spacer(modifier = Modifier.height(6.dp))

          activeTeamLineup.substitutes.forEach { sub ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("${sub.number}", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
              DetailPositionBadge(sub.position)
              Spacer(modifier = Modifier.width(6.dp))
              Text(sub.name, color = Color(0xFFCBD5E1), fontSize = 10.sp, modifier = Modifier.weight(1f))
              Text("★ ${sub.rating}", color = GoldYellow.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

/**
 * Head-to-Head Tab Content
 */
@Composable
private fun DetailH2HTabContent(
  h2h: MatchH2H,
  homeTeam: String,
  awayTeam: String
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
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
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("$homeTeam Galip", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text("${h2h.homeWins}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Beraberlik", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text("${h2h.draws}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("$awayTeam Galip", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text("${h2h.awayWins}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }

    items(h2h.recentMatches) { matchItem ->
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(matchItem.league, color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text(matchItem.date, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
          Surface(
            color = when (matchItem.result) {
              "G" -> Color(0xFF10B981)
              "B" -> GoldYellow
              else -> LiveRed
            },
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "${matchItem.score} (${matchItem.result})",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * AI Win Probability Prediction Tab Content
 */
@Composable
private fun DetailAiPredictionTabContent(
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
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, GoldYellow)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("GEMINI AI MAÇ OLASILIK MODELİ", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Modelimiz son 10 maçlık momentum, oyuncu form grafikleri ve taktik beklentilerini analiz ederek aşağıdaki olasılık dağılımını hesapladı:",
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(14.dp))

          // Visual Probability Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(12.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF0F172A))
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
        }
      }
    }

    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("🧠 GEMİNİ TAKTİK RAPORU VE ÖNGÖRÜ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = match.aiPrediction.tacticalAnalysis.ifBlank {
              "${match.homeTeam} hücumda kanat organizasyonları ve merkez bindirmelerle ligin en etkili ekiplerinden biri. " +
                  "${match.awayTeam} ise geçiş oyunlarında hızlı hücumlarla tehdit oluşturuyor."
            },
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        }
      }
    }
  }
}

/**
 * Odds Tab Content with Expanded Rich Markets (F1, MotoGP, WRC, Tennis, Football)
 */
@Composable
private fun DetailOddsTabContent(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  val isMotorsport = match.sport == Sport.MOTORSPORTS ||
      match.sport == Sport.MOTOGP ||
      match.sport == Sport.FORMULA_1 ||
      match.sport == Sport.WRC_RALLY ||
      match.league.contains("MotoGP", ignoreCase = true) ||
      match.league.contains("Formula", ignoreCase = true) ||
      match.league.contains("F1", ignoreCase = true) ||
      match.league.contains("WRC", ignoreCase = true)

  val msMarket = match.markets.find { it.type == MarketType.MATCH_RESULT || it.type == MarketType.BASKETBALL_MS }
  val odd1 = msMarket?.selections?.getOrNull(0)?.odd ?: 1.85
  val oddX = msMarket?.selections?.getOrNull(1)?.odd ?: 3.20
  val odd2 = msMarket?.selections?.getOrNull(2)?.odd ?: 2.45

  val sel1 = SlipSelection(
    matchId = match.id,
    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
    marketType = MarketType.MATCH_RESULT,
    selectionId = "${match.id}_ms1",
    selectionName = if (isMotorsport) "1. Pilot Kazanır" else "MS 1",
    odd = odd1,
    isLive = match.minute > 0
  )
  val isSel1 = selectedSelections.any { it.matchId == sel1.matchId && it.selectionId == sel1.selectionId }

  val selX = SlipSelection(
    matchId = match.id,
    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
    marketType = MarketType.MATCH_RESULT,
    selectionId = "${match.id}_msx",
    selectionName = if (isMotorsport) "Podyumda Bitirir" else "MS X",
    odd = oddX,
    isLive = match.minute > 0
  )
  val isSelX = selectedSelections.any { it.matchId == selX.matchId && it.selectionId == selX.selectionId }

  val sel2 = SlipSelection(
    matchId = match.id,
    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
    marketType = MarketType.MATCH_RESULT,
    selectionId = "${match.id}_ms2",
    selectionName = if (isMotorsport) "2. Pilot Kazanır" else "MS 2",
    odd = odd2,
    isLive = match.minute > 0
  )
  val isSel2 = selectedSelections.any { it.matchId == sel2.matchId && it.selectionId == sel2.selectionId }

  // Additional Rich Markets (Fastest Lap / Safety Car or 2.5 Alt/Üst)
  val extraOdd1 = 1.95
  val extraOdd2 = 1.75
  val extraSel1 = SlipSelection(
    matchId = match.id,
    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
    marketType = MarketType.TOTAL_GOALS_25,
    selectionId = "${match.id}_ext1",
    selectionName = if (isMotorsport) "En Hızlı Tur Zamanı (Fastest Lap)" else "2.5 Üst Gol",
    odd = extraOdd1,
    isLive = match.minute > 0
  )
  val isExtra1 = selectedSelections.any { it.matchId == extraSel1.matchId && it.selectionId == extraSel1.selectionId }

  val extraSel2 = SlipSelection(
    matchId = match.id,
    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
    marketType = MarketType.TOTAL_GOALS_25,
    selectionId = "${match.id}_ext2",
    selectionName = if (isMotorsport) "Güvenlik Aracı Girer (Safety Car: Evet)" else "2.5 Alt Gol",
    odd = extraOdd2,
    isLive = match.minute > 0
  )
  val isExtra2 = selectedSelections.any { it.matchId == extraSel2.matchId && it.selectionId == extraSel2.selectionId }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Market 1: Main Winner / Head to Head
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = if (isMotorsport) "🏁 YARIŞ / ETAP GALİBİ DÜELLOSU" else "🎟️ MAÇ SONUCU (MS 1 - X - 2)",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            DetailOddButton(
              label = if (isMotorsport) "1. Pilot" else "MS 1",
              odd = odd1,
              isSelected = isSel1,
              onClick = { onSelectOdd(sel1) },
              modifier = Modifier.weight(1f)
            )
            DetailOddButton(
              label = if (isMotorsport) "Podyum" else "MS X",
              odd = oddX,
              isSelected = isSelX,
              onClick = { onSelectOdd(selX) },
              modifier = Modifier.weight(1f)
            )
            DetailOddButton(
              label = if (isMotorsport) "2. Pilot" else "MS 2",
              odd = odd2,
              isSelected = isSel2,
              onClick = { onSelectOdd(sel2) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // Market 2: Extended Special Markets
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = if (isMotorsport) "⚡ ÖZEL MOTOR SPORLARI BAHİSLERİ" else "⚡ 2.5 ALT / ÜST GOLLER",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            DetailOddButton(
              label = if (isMotorsport) "En Hızlı Tur" else "2.5 Üst",
              odd = extraOdd1,
              isSelected = isExtra1,
              onClick = { onSelectOdd(extraSel1) },
              modifier = Modifier.weight(1f)
            )
            DetailOddButton(
              label = if (isMotorsport) "Güvenlik Aracı" else "2.5 Alt",
              odd = extraOdd2,
              isSelected = isExtra2,
              onClick = { onSelectOdd(extraSel2) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun DetailOddButton(
  label: String,
  odd: Double,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = if (isSelected) GoldYellow else Color(0xFF0F172A),
    shape = RoundedCornerShape(8.dp),
    border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0xFF334155)),
    modifier = modifier.clickable { onClick() }
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(label, color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(odd.formatOdd(), color = if (isSelected) Color(0xFF0F172A) else Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
    }
  }
}

@Composable
private fun DetailPositionBadge(position: String) {
  val (color, text) = when (position.uppercase()) {
    "GK", "KL" -> Color(0xFFEAB308) to "KL"
    "DEF", "DF", "STP", "SLB", "SĞB" -> Color(0xFF3B82F6) to "DF"
    "MID", "OS", "ÖNL", "ON", "KNT", "SLK", "SĞK" -> Color(0xFF10B981) to "OS"
    "FWD", "FV", "SNT", "FORVET" -> Color(0xFFEF4444) to "FV"
    else -> Color(0xFF64748B) to position.take(2)
  }

  Surface(
    color = color.copy(alpha = 0.2f),
    shape = RoundedCornerShape(3.dp)
  ) {
    Text(
      text = text,
      color = color,
      fontSize = 8.5.sp,
      fontWeight = FontWeight.Black,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
    )
  }
}
