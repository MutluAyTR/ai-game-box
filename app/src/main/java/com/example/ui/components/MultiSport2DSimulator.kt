package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Sport
import com.example.service.RealtimeSportsWebSocketService
import com.example.service.WebSocketConnectionState
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Branş bazlı 2D Canlı Simülatör Türleri
 */
enum class SimulatorBranch(val title: String, val icon: String, val primarySport: Sport) {
  FOOTBALL("Futbol", "⚽", Sport.FOOTBALL),
  BASKETBALL("Basketbol", "🏀", Sport.BASKETBALL),
  TENNIS("Tenis", "🎾", Sport.TENNIS),
  VOLLEYBALL("Voleybol", "🏐", Sport.VOLLEYBALL),
  ICE_HOCKEY("Buz Hokeyi", "🏒", Sport.ICE_HOCKEY),
  MOTOGP("MotoGP", "🏍️", Sport.MOTOGP),
  WRC("WRC Ralli", "🏎️", Sport.WRC_RALLY),
  BILLIARDS("Bilardo", "🎱", Sport.BILLIARDS),
  TABLE_TENNIS("Masa Tenisi", "🏓", Sport.TABLE_TENNIS),
  OKEY_CARDS("Okey & Kart", "🀄", Sport.OKEY_CARDS);

  companion object {
    /**
     * Gelen maçın asıl branşına göre dinamik 2D render kaynağını tespit eder
     */
    fun fromMatch(match: Match): SimulatorBranch {
      return when {
        match.sport == Sport.TENNIS ||
            match.league.contains("ATP", ignoreCase = true) ||
            match.league.contains("WTA", ignoreCase = true) ||
            match.league.contains("Tenis", ignoreCase = true) ||
            match.league.contains("Wimbledon", ignoreCase = true) ||
            match.league.contains("Roland Garros", ignoreCase = true) ||
            match.league.contains("US Open", ignoreCase = true) ||
            match.league.contains("Australian Open", ignoreCase = true) -> TENNIS
        match.sport == Sport.BASKETBALL ||
            match.league.contains("NBA", ignoreCase = true) ||
            match.league.contains("EuroLeague", ignoreCase = true) ||
            match.league.contains("Basket", ignoreCase = true) -> BASKETBALL
        match.sport == Sport.VOLLEYBALL ||
            match.league.contains("Voleybol", ignoreCase = true) ||
            match.league.contains("Sultanlar", ignoreCase = true) -> VOLLEYBALL
        match.sport == Sport.ICE_HOCKEY ||
            match.league.contains("NHL", ignoreCase = true) ||
            match.league.contains("Hokey", ignoreCase = true) -> ICE_HOCKEY
        match.sport == Sport.TABLE_TENNIS ||
            match.league.contains("Masa Tenisi", ignoreCase = true) -> TABLE_TENNIS
        match.sport == Sport.BILLIARDS ||
            match.sport == Sport.SNOOKER ||
            match.league.contains("Bilardo", ignoreCase = true) ||
            match.league.contains("Snooker", ignoreCase = true) -> BILLIARDS
        match.sport == Sport.OKEY_CARDS ||
            match.league.contains("Okey", ignoreCase = true) ||
            match.league.contains("Briç", ignoreCase = true) -> OKEY_CARDS
        match.league.contains("WRC", ignoreCase = true) ||
            match.sport == Sport.WRC_RALLY -> WRC
        match.league.contains("MotoGP", ignoreCase = true) ||
            match.sport == Sport.MOTOGP ||
            match.sport == Sport.MOTORSPORTS ||
            match.sport == Sport.FORMULA_1 ||
            match.league.contains("Formula", ignoreCase = true) ||
            match.league.contains("F1", ignoreCase = true) -> MOTOGP
        else -> FOOTBALL
      }
    }
  }
}

/**
 * Maçkolik & Nesine Canlı 2D Simülatör Master Bileşeni:
 * Her spor branşına (Futbol, Basketbol, Tenis, Voleybol, Buz Hokeyi, MotoGP, WRC, Bilardo, Okey) özel
 * özgün 2D grafikler, saha zeminleri, top/araç fiziği ve canlı anlatım sunar.
 * Asla futbol takımlarını basketbol potasında, tenis kortunda veya yarış motorunda göstermez;
 * her branşa kendi gerçek ve popüler takımlarını/sporcularını atar.
 */
@Composable
fun MultiSport2DSimulator(
  match: Match,
  onTriggerGoal: (String, Boolean) -> Unit = { _, _ -> },
  onMinuteUpdated: ((minute: Int, extraMinute: Int, isFinished: Boolean, homeScore: Int, awayScore: Int) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  // Varsayılan branşı maça göre otomatik belirle (Tenis / ATP / WTA kesinlikle Tenis 2D açar)
  val initialBranch = remember(match) {
    when {
      match.sport == Sport.TENNIS ||
          match.league.contains("ATP", ignoreCase = true) ||
          match.league.contains("WTA", ignoreCase = true) ||
          match.league.contains("Tenis", ignoreCase = true) ||
          match.league.contains("Wimbledon", ignoreCase = true) ||
          match.league.contains("Roland Garros", ignoreCase = true) ||
          match.league.contains("US Open", ignoreCase = true) ||
          match.league.contains("Australian Open", ignoreCase = true) -> SimulatorBranch.TENNIS
      match.league.contains("WRC", ignoreCase = true) || match.sport == Sport.WRC_RALLY -> SimulatorBranch.WRC
      match.league.contains("MotoGP", ignoreCase = true) || match.sport == Sport.MOTOGP || match.sport == Sport.MOTORSPORTS || match.sport == Sport.FORMULA_1 -> SimulatorBranch.MOTOGP
      match.sport == Sport.BASKETBALL || match.league.contains("NBA", ignoreCase = true) || match.league.contains("EuroLeague", ignoreCase = true) -> SimulatorBranch.BASKETBALL
      match.sport == Sport.VOLLEYBALL || match.league.contains("Voleybol", ignoreCase = true) || match.league.contains("Sultanlar", ignoreCase = true) -> SimulatorBranch.VOLLEYBALL
      match.sport == Sport.ICE_HOCKEY || match.league.contains("NHL", ignoreCase = true) || match.league.contains("Hokey", ignoreCase = true) -> SimulatorBranch.ICE_HOCKEY
      match.sport == Sport.TABLE_TENNIS || match.league.contains("Masa Tenisi", ignoreCase = true) -> SimulatorBranch.TABLE_TENNIS
      match.sport == Sport.BILLIARDS || match.sport == Sport.SNOOKER || match.league.contains("Bilardo", ignoreCase = true) || match.league.contains("Snooker", ignoreCase = true) -> SimulatorBranch.BILLIARDS
      match.sport == Sport.OKEY_CARDS || match.league.contains("Okey", ignoreCase = true) || match.league.contains("Briç", ignoreCase = true) -> SimulatorBranch.OKEY_CARDS
      else -> SimulatorBranch.FOOTBALL
    }
  }

  var selectedBranch by remember(match.id, initialBranch) { mutableStateOf(initialBranch) }

  LaunchedEffect(match.id, initialBranch) {
    selectedBranch = initialBranch
  }

  // 🎾 Tenis Maçı: Maç zaten tenis ise doğrudan kendi gerçek sporcularını kullanır (Alcaraz, Djokovic, Sinner vb.)
  val isTennisMatch = match.sport == Sport.TENNIS ||
      match.league.contains("ATP", ignoreCase = true) ||
      match.league.contains("WTA", ignoreCase = true) ||
      match.league.contains("Tenis", ignoreCase = true)
  val tennisMatch = remember(match) {
    if (isTennisMatch) match
    else match.copy(
      id = "sim_ten_${match.id}",
      sport = Sport.TENNIS,
      league = "ATP Masters 1000",
      homeTeam = "Carlos Alcaraz",
      awayTeam = "Novak Djokovic",
      homeScore = 6,
      awayScore = 4,
      minute = 75,
      status = MatchStatus.LIVE
    )
  }

  // Her branş için gerçek takımları hazırla (Futbol takımı asla basketbol, tenis veya motorda oynamaz!)
  val basketballMatch = remember(match) {
    if (match.sport == Sport.BASKETBALL) match
    else match.copy(
      id = "sim_bsk_${match.id}",
      sport = Sport.BASKETBALL,
      league = "EuroLeague Derbisi",
      homeTeam = "Fenerbahçe Beko",
      awayTeam = "Anadolu Efes",
      homeScore = 74,
      awayScore = 71,
      minute = 32,
      status = MatchStatus.LIVE
    )
  }

  val volleyballMatch = remember(match) {
    if (match.sport == Sport.VOLLEYBALL) match
    else match.copy(
      id = "sim_vol_${match.id}",
      sport = Sport.VOLLEYBALL,
      league = "Vodafone Sultanlar Ligi",
      homeTeam = "VakıfBank",
      awayTeam = "Eczacıbaşı Dynavit",
      homeScore = 2,
      awayScore = 1,
      minute = 54,
      status = MatchStatus.LIVE
    )
  }

  val iceHockeyMatch = remember(match) {
    if (match.sport == Sport.ICE_HOCKEY) match
    else match.copy(
      id = "sim_ice_${match.id}",
      sport = Sport.ICE_HOCKEY,
      league = "NHL Stanley Cup",
      homeTeam = "New York Rangers",
      awayTeam = "Florida Panthers",
      homeScore = 3,
      awayScore = 2,
      minute = 48,
      status = MatchStatus.LIVE
    )
  }

  val motoGpMatch = remember(match) {
    if (match.league.contains("MotoGP", ignoreCase = true)) match
    else match.copy(
      id = "sim_mgp_${match.id}",
      sport = Sport.MOTORSPORTS,
      league = "MotoGP Grand Prix",
      homeTeam = "Francesco Bagnaia (Ducati)",
      awayTeam = "Jorge Martin (Pramac)",
      homeScore = 25,
      awayScore = 20,
      minute = 18,
      status = MatchStatus.LIVE
    )
  }

  val wrcMatch = remember(match) {
    if (match.league.contains("WRC", ignoreCase = true)) match
    else match.copy(
      id = "sim_wrc_${match.id}",
      sport = Sport.MOTORSPORTS,
      league = "WRC Dünya Rallisi",
      homeTeam = "Sébastien Ogier (Toyota)",
      awayTeam = "Thierry Neuville (Hyundai)",
      homeScore = 25,
      awayScore = 18,
      minute = 25,
      status = MatchStatus.LIVE
    )
  }

  val billiardsMatch = remember(match) {
    if (match.sport == Sport.BILLIARDS || match.sport == Sport.SNOOKER) match
    else match.copy(
      id = "sim_bil_${match.id}",
      sport = Sport.BILLIARDS,
      league = "3-Bant Dünya Bilardo Kupası",
      homeTeam = "Semih Saygıner",
      awayTeam = "Torbjörn Blomdahl",
      homeScore = 34,
      awayScore = 31,
      minute = 45,
      status = MatchStatus.LIVE
    )
  }

  val okeyMatch = remember(match) {
    if (match.sport == Sport.OKEY_CARDS) match
    else match.copy(
      id = "sim_ok_${match.id}",
      sport = Sport.OKEY_CARDS,
      league = "101 Okey & Briç Türkiye Kupası",
      homeTeam = "Ahmet Usta (Ege 101)",
      awayTeam = "Mehmet Çavuş (Marmara 101)",
      homeScore = 420,
      awayScore = 390,
      minute = 28,
      status = MatchStatus.LIVE
    )
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    border = BorderStroke(1.5.dp, Color(0xFF1E293B)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("multisport_2d_simulator")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      // 0. Active Match Identity & WebSocket Live Connection Badge
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = initialBranch.icon, fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = match.league,
              color = GoldYellow,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${match.homeTeam} vs ${match.awayTeam}",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Surface(
          color = Color(0xFF10B981).copy(alpha = 0.15f),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
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
              text = "WebSocket 2D Canlı",
              color = Color(0xFF34D399),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // 1. Branch Selector Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        SimulatorBranch.values().forEach { branch ->
          val isSelected = selectedBranch == branch
          val isMatchSport = branch == initialBranch
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) TealDark else Color(0xFF1E293B),
            border = BorderStroke(
              width = if (isSelected) 1.5.dp else if (isMatchSport) 1.dp else 0.5.dp,
              color = if (isSelected) GoldYellow else if (isMatchSport) Color(0xFF38BDF8) else Color(0xFF334155)
            ),
            modifier = Modifier.clickable { selectedBranch = branch }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = branch.icon, fontSize = 11.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isMatchSport) "${branch.title} 2D ★" else "${branch.title} 2D",
                color = if (isSelected) GoldYellow else if (isMatchSport) Color(0xFF38BDF8) else Color(0xFFCBD5E1),
                fontSize = 10.sp,
                fontWeight = if (isSelected || isMatchSport) FontWeight.Black else FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Branşa Özel 2D Simülatör Render (Her branş kendi gerçek takımlarıyla!)
      when (selectedBranch) {
        SimulatorBranch.FOOTBALL -> {
          val activeFootballMatch = if (match.sport == Sport.FOOTBALL) match else match.copy(
            homeTeam = "Galatasaray",
            awayTeam = "Fenerbahçe",
            league = "Trendyol Süper Lig"
          )
          MackolikPitch2DSimulator(
            match = activeFootballMatch,
            onTriggerGoal = onTriggerGoal,
            onMinuteUpdated = onMinuteUpdated
          )
        }
        SimulatorBranch.BASKETBALL -> {
          val activeBasketballMatch = if (match.sport == Sport.BASKETBALL) match else basketballMatch
          Basketball2DCourtSimulator(match = activeBasketballMatch, onTriggerScore = onTriggerGoal)
        }
        SimulatorBranch.TENNIS -> {
          val activeTennisMatch = if (isTennisMatch) match else tennisMatch
          Tennis2DCourtSimulator(match = activeTennisMatch, onTriggerPoint = onTriggerGoal)
        }
        SimulatorBranch.VOLLEYBALL -> {
          val activeVolleyballMatch = if (match.sport == Sport.VOLLEYBALL) match else volleyballMatch
          Volleyball2DCourtSimulator(match = activeVolleyballMatch, onTriggerPoint = onTriggerGoal)
        }
        SimulatorBranch.ICE_HOCKEY -> {
          val activeIceHockeyMatch = if (match.sport == Sport.ICE_HOCKEY) match else iceHockeyMatch
          IceHockey2DRinkSimulator(match = activeIceHockeyMatch, onTriggerGoal = onTriggerGoal)
        }
        SimulatorBranch.MOTOGP -> {
          val activeMotoMatch = if (match.sport == Sport.MOTOGP || match.sport == Sport.MOTORSPORTS || match.sport == Sport.FORMULA_1) match else motoGpMatch
          Motorsport2DCircuitSimulator(match = activeMotoMatch, isWrc = false)
        }
        SimulatorBranch.WRC -> {
          val activeWrcMatch = if (match.sport == Sport.WRC_RALLY || match.league.contains("WRC", ignoreCase = true)) match else wrcMatch
          Motorsport2DCircuitSimulator(match = activeWrcMatch, isWrc = true)
        }
        SimulatorBranch.BILLIARDS -> {
          val activeBilliardsMatch = if (match.sport == Sport.BILLIARDS || match.sport == Sport.SNOOKER) match else billiardsMatch
          Billiards2DCourtSimulator(match = activeBilliardsMatch)
        }
        SimulatorBranch.TABLE_TENNIS -> {
          val activeTableTennisMatch = if (match.sport == Sport.TABLE_TENNIS) match else match.copy(
            sport = Sport.TABLE_TENNIS,
            league = "WTT Champions",
            homeTeam = "Fan Zhendong",
            awayTeam = "Ma Long",
            homeScore = 3,
            awayScore = 2
          )
          TableTennis2DCourtSimulator(match = activeTableTennisMatch)
        }
        SimulatorBranch.OKEY_CARDS -> {
          val activeOkeyMatch = if (match.sport == Sport.OKEY_CARDS) match else okeyMatch
          OkeyCards2DSimulator(match = activeOkeyMatch)
        }
      }
    }
  }
}

/**
 * 🏀 BASKETBOL 2D CANLI SAHA SİMÜLATÖRÜ (Maçkolik / Nesine Canlı İddaa Standardı)
 * - Cilalı Ahşap Parke Zemin
 * - 3'lük Çizgisi, Boyalı Alan (Key), Serbest Atış Dairesi, Pota ve Fileler
 * - 24 Saniye Şut Saati Geri Sayımı
 * - Topun Parabolik Kavisle Potaya Atılışı (3 Sayılık, Turnike, Smaç)
 * - Faul, Ribaund, Mola ve Çeyrek Göstergesi
 */
@Composable
fun Basketball2DCourtSimulator(
  match: Match,
  onTriggerScore: (String, Boolean) -> Unit = { _, _ -> }
) {
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED ||
      (match.sport == Sport.BASKETBALL && match.minute >= 40)
  var isFinished by remember(match.id, match.status, match.minute) {
    mutableStateOf(matchIsAlreadyFinished)
  }
  var shotClock by remember(match.id) { mutableIntStateOf(if (isFinished) 0 else 18) }
  var quarter by remember(match.id) { mutableStateOf(if (isFinished) "MAÇ SONUCU (MS)" else "3. Çeyrek") }
  var quarterSecondsRemaining by remember(match.id) { mutableIntStateOf(if (isFinished) 0 else 278) } // 04:38
  var quarterTime by remember(match.id) { mutableStateOf(if (isFinished) "00:00" else "04:38") }
  var homeFouls by remember(match.id) { mutableIntStateOf(3) }
  var awayFouls by remember(match.id) { mutableIntStateOf(4) }
  var currentAction by remember(match.id) {
    mutableStateOf(
      if (isFinished) "🏁 Karşılaşma tamamlandı. Maç Sonucu: ${match.homeTeam} ${match.homeScore} - ${match.awayScore} ${match.awayTeam}"
      else "⚡ Hücum: ${match.homeTeam} top çeviriyor"
    )
  }
  var actionBadge by remember(match.id) { mutableStateOf(if (isFinished) "MS BİTTİ" else "HÜCUM") }
  var homeScore by remember(match.id, match.homeScore) {
    mutableIntStateOf(if (match.sport == Sport.BASKETBALL && match.homeScore > 0) match.homeScore else 84)
  }
  var awayScore by remember(match.id, match.awayScore) {
    mutableIntStateOf(if (match.sport == Sport.BASKETBALL && match.awayScore > 0) match.awayScore else 81)
  }

  val ballX = remember { Animatable(0.5f) }
  val ballY = remember { Animatable(0.5f) }
  val ballArc = remember { Animatable(0f) }
  var showScoreExplosion by remember { mutableStateOf(false) }

  // Canlı Saat Tickerı (Saniyeler gerçekçi geriye akar - maç bittiğinde durur)
  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      delay(1000L)
      if (quarterSecondsRemaining > 0) {
        quarterSecondsRemaining--
        val m = quarterSecondsRemaining / 60
        val s = quarterSecondsRemaining % 60
        quarterTime = "%02d:%02d".format(m, s)
      } else {
        if (quarter == "4. Çeyrek") {
          isFinished = true
          quarter = "MAÇ SONUCU (MS)"
          quarterTime = "00:00"
          currentAction = "🏁 Karşılaşma tamamlandı! Maç Sonucu: ${match.homeTeam} $homeScore - $awayScore ${match.awayTeam}"
          actionBadge = "MS BİTTİ"
          RealtimeSportsWebSocketService.broadcastMatchFinished(match.id, homeScore, awayScore, "Basketbol karşılaşması tamamlandı.")
          break
        } else {
          quarter = "4. Çeyrek"
          quarterSecondsRemaining = 600
          quarterTime = "10:00"
        }
      }
    }
  }

  // React to external match status changes (e.g. from WebSocket)
  LaunchedEffect(match.status, match.minute) {
    if (match.status == MatchStatus.FINISHED || (match.sport == Sport.BASKETBALL && match.minute >= 40)) {
      isFinished = true
      quarter = "MAÇ SONUCU (MS)"
      quarterTime = "00:00"
      actionBadge = "MS BİTTİ"
    }
  }

  // Canlı Simülasyon Döngüsü (Maç bittiğinde kesinlikle skor artışı yapmaz)
  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      delay(2200L)
      if (isFinished) break
      shotClock = if (shotClock > 2) shotClock - 2 else 24

      val eventType = Random.nextInt(10)
      when (eventType) {
        0, 1 -> { // 3 Sayılık Basket!
          val isHome = Random.nextBoolean()
          val shooter = if (isHome) match.homeTeam else match.awayTeam
          currentAction = "🔥 3 SAYILIK BASKET! $shooter yay gerisinden gönderdi!"
          actionBadge = "3 SAYI"
          val targetX = if (isHome) 0.88f else 0.12f
          ballX.animateTo(targetX, tween(900, easing = FastOutSlowInEasing))
          showScoreExplosion = true
          if (isHome) homeScore += 3 else awayScore += 3
          onTriggerScore(shooter, isHome)
          delay(1200L)
          showScoreExplosion = false
        }
        2, 3 -> { // Turnike / Smaç
          val isHome = Random.nextBoolean()
          val scorer = if (isHome) match.homeTeam else match.awayTeam
          currentAction = "⚡ SMAÇ! $scorer pota altını parçaladı!"
          actionBadge = "SMAÇ"
          val targetX = if (isHome) 0.90f else 0.10f
          ballX.animateTo(targetX, tween(700))
          if (isHome) homeScore += 2 else awayScore += 2
        }
        4 -> { // Blok!
          currentAction = "🛑 HARİKA BLOK! Pota altı geçilmez!"
          actionBadge = "BLOK"
          ballY.animateTo(0.2f, tween(400))
          ballY.animateTo(0.5f, tween(400))
        }
        5 -> { // Savunma Ribaundu
          currentAction = "🏀 Savunma ribaundu alındı, hızlı hücum başlıyor!"
          actionBadge = "RİBAUND"
          shotClock = 24
          ballX.animateTo(0.45f, tween(600))
        }
        6 -> { // Hücum Faul
          currentAction = "⚠️ Hücum Faul! Hakem düdüğü çaldı."
          actionBadge = "FAUL"
          if (Random.nextBoolean()) homeFouls++ else awayFouls++
        }
        else -> { // Set hücumu
          val isHome = Random.nextBoolean()
          currentAction = "⏱️ $quarter [$quarterTime] - ${if (isHome) match.homeTeam else match.awayTeam} sete yerleşti"
          actionBadge = "HÜCUM"
          ballX.animateTo(Random.nextFloat() * 0.4f + 0.3f, tween(700))
          ballY.animateTo(Random.nextFloat() * 0.4f + 0.3f, tween(700))
        }
      }
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Skor Panosu & Şut Saati
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(10.dp),
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
          Text(text = match.homeTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Text(text = "Faul: $homeFouls/5", color = if (homeFouls >= 4) LiveRed else Color(0xFF94A3B8), fontSize = 9.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "$homeScore", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
          Text(text = " - ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text(text = "$awayScore", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(text = match.awayTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Text(text = "Faul: $awayFouls/5", color = if (awayFouls >= 4) LiveRed else Color(0xFF94A3B8), fontSize = 9.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Şut Saati & Çeyrek Şeridi
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        color = if (shotClock <= 5) LiveRed else Color(0xFF0F766E),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text(
          text = "⏱️ ŞUT SAATİ: ${shotClock}s",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 10.sp,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
      }

      Surface(
        color = Color(0xFF334155),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text(
          text = "🏀 $quarter ($quarterTime)",
          color = GoldYellow,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2D BASKETBOL PARKE SAHASI CANVAS
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(RoundedCornerShape(12.dp))
        .border(2.dp, Color(0xFF92400E), RoundedCornerShape(12.dp))
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Ahşap Parke Çizgileri & Zemin
        drawRect(
          brush = Brush.verticalGradient(
            listOf(Color(0xFFD97706), Color(0xFFB45309), Color(0xFF92400E))
          ),
          size = size
        )

        // Yatay parke tahta şeritleri
        val plankCount = 14
        for (i in 0..plankCount) {
          val y = (h / plankCount) * i
          drawLine(
            color = Color(0xFF78350F).copy(alpha = 0.3f),
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 1f
          )
        }

        val courtLineColor = Color.White.copy(alpha = 0.85f)
        val lineStroke = Stroke(width = 2.5f)

        // Dış Sınır Çizgileri
        drawRect(
          color = courtLineColor,
          topLeft = Offset(10f, 10f),
          size = Size(w - 20f, h - 20f),
          style = lineStroke
        )

        // Orta Çizgi & Orta Yuvarlak
        drawLine(
          color = courtLineColor,
          start = Offset(w / 2, 10f),
          end = Offset(w / 2, h - 10f),
          strokeWidth = 2.5f
        )
        drawCircle(
          color = courtLineColor,
          radius = 32f,
          center = Offset(w / 2, h / 2),
          style = lineStroke
        )

        // Sol Boyalı Alan (Key) & 3'lük Çizgisi
        drawRect(
          color = Color(0xFF0369A1).copy(alpha = 0.45f),
          topLeft = Offset(10f, h * 0.30f),
          size = Size(w * 0.16f, h * 0.40f)
        )
        drawRect(
          color = courtLineColor,
          topLeft = Offset(10f, h * 0.30f),
          size = Size(w * 0.16f, h * 0.40f),
          style = lineStroke
        )
        drawCircle(
          color = courtLineColor,
          radius = 28f,
          center = Offset(10f + w * 0.16f, h / 2),
          style = lineStroke
        )
        // Sol Pota Çemberi
        drawCircle(
          color = Color(0xFFEA580C),
          radius = 8f,
          center = Offset(32f, h / 2),
          style = Stroke(width = 3f)
        )

        // Sağ Boyalı Alan (Key) & 3'lük Çizgisi
        drawRect(
          color = Color(0xFF0369A1).copy(alpha = 0.45f),
          topLeft = Offset(w - 10f - w * 0.16f, h * 0.30f),
          size = Size(w * 0.16f, h * 0.40f)
        )
        drawRect(
          color = courtLineColor,
          topLeft = Offset(w - 10f - w * 0.16f, h * 0.30f),
          size = Size(w * 0.16f, h * 0.40f),
          style = lineStroke
        )
        drawCircle(
          color = courtLineColor,
          radius = 28f,
          center = Offset(w - 10f - w * 0.16f, h / 2),
          style = lineStroke
        )
        // Sağ Pota Çemberi
        drawCircle(
          color = Color(0xFFEA580C),
          radius = 8f,
          center = Offset(w - 32f, h / 2),
          style = Stroke(width = 3f)
        )

        // HAREKETLİ BASKETBOL TOPU
        val bx = ballX.value * w
        val by = ballY.value * h
        // Top gölgesi
        drawCircle(
          color = Color.Black.copy(alpha = 0.35f),
          radius = 7f,
          center = Offset(bx + 2f, by + 4f)
        )
        // Turuncu Basketbol Topu
        drawCircle(
          color = Color(0xFFEA580C),
          radius = 7.5f,
          center = Offset(bx, by)
        )
        // Topun siyah dikiş çizgisi
        drawCircle(
          color = Color.Black.copy(alpha = 0.7f),
          radius = 7.5f,
          center = Offset(bx, by),
          style = Stroke(width = 1.2f)
        )
      }

      // Canlı Basket Patlama Animasyonu
      if (showScoreExplosion) {
        Surface(
          color = GoldYellow,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .align(Alignment.Center)
            .padding(6.dp)
        ) {
          Text(
            text = "🏀 BASKET! +3 SAYI! 🔥",
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Canlı Anlatım Tickerı
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = when (actionBadge) {
            "3 SAYI" -> Color(0xFF16A34A)
            "SMAÇ" -> GoldYellow
            "BLOK" -> LiveRed
            else -> TealDark
          },
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = actionBadge,
            color = if (actionBadge == "SMAÇ") Color(0xFF0F172A) else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = currentAction,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * 🏐 VOLEYBOL 2D CANLI SAHA SİMÜLATÖRÜ (Sultanlar Ligi & CEV Şampiyonlar Ligi Standardı)
 * - Mavi & Turuncu Resmi Taraflex Zemin
 * - Ortada File, 3 Metre Hücum Çizgileri, Antenler
 * - Sarı-Mavi Mikasa Voleybol Topu ve Kavisli Smaç/Servis Trajektorisi
 * - Set Skorları (Örn: Set 1: 25-22, Set 2: 21-25) & Ralli Sayacı
 */
@Composable
fun Volleyball2DCourtSimulator(
  match: Match,
  onTriggerPoint: (String, Boolean) -> Unit = { _, _ -> }
) {
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED ||
      (match.sport == Sport.VOLLEYBALL && (match.homeScore >= 3 || match.awayScore >= 3))
  var isFinished by remember(match.id, match.status) {
    mutableStateOf(matchIsAlreadyFinished)
  }
  var setScoreText by remember(match.id) {
    mutableStateOf(if (isFinished) "MAÇ SONUCU: ${match.homeScore} - ${match.awayScore} Set (BİTTİ)" else "1. Set: 25-22 | 2. Set: 20-25 | 3. Set: 16-14")
  }
  var homeSetPoints by remember(match.id) { mutableIntStateOf(if (isFinished) 25 else 16) }
  var awaySetPoints by remember(match.id) { mutableIntStateOf(if (isFinished) 21 else 14) }
  var rallyCount by remember(match.id) { mutableIntStateOf(if (isFinished) 0 else 6) }
  var currentAction by remember(match.id) {
    mutableStateOf(
      if (isFinished) "🏁 Karşılaşma tamamlandı. Setler: ${match.homeTeam} ${match.homeScore} - ${match.awayScore} ${match.awayTeam}"
      else "🏐 Ebrar Karakurt sert smaçla file üstünden vurdu!"
    )
  }
  var actionBadge by remember(match.id) { mutableStateOf(if (isFinished) "MS BİTTİ" else "SMAÇ") }
  val ballX = remember { Animatable(0.35f) }
  val ballY = remember { Animatable(0.48f) }

  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      delay(2400L)
      if (isFinished) break
      rallyCount++
      val event = Random.nextInt(7)
      when (event) {
        0, 1 -> { // Smaç Sayı
          val isHome = Random.nextBoolean()
          val spiker = if (isHome) match.homeTeam else match.awayTeam
          currentAction = "💥 SERT SMAÇ! $spiker bloğu delip geçti, sayı!"
          actionBadge = "SMAÇ"
          val targetX = if (isHome) 0.78f else 0.22f
          ballX.animateTo(targetX, tween(600, easing = FastOutSlowInEasing))
          if (isHome) homeSetPoints++ else awaySetPoints++
          rallyCount = 1
          onTriggerPoint(spiker, isHome)

          if (homeSetPoints >= 25 || awaySetPoints >= 25) {
            isFinished = true
            setScoreText = "MAÇ SONUCU (BİTTİ)"
            currentAction = "🏁 Karşılaşma tamamlandı! Kazanan: ${if (homeSetPoints > awaySetPoints) match.homeTeam else match.awayTeam} 🏆"
            actionBadge = "MS BİTTİ"
            break
          }
        }
        2 -> { // İkili Blok
          currentAction = "🛡️ İKİLİ BLOK! File üstünde duvar örüldü, blok sayısı!"
          actionBadge = "BLOK"
          ballX.animateTo(0.50f, tween(400))
        }
        3 -> { // Ace Servis
          currentAction = "⚡ ACE SERVİS! Doğrudan köşe çizgisine indi!"
          actionBadge = "ACE"
          ballX.animateTo(0.85f, tween(700))
        }
        4 -> { // File Teması
          currentAction = "⚠️ Hakem kararı: File teması ve servis değişimi"
          actionBadge = "HATA"
        }
        else -> { // Ralli Paslaşma
          currentAction = "🏐 Ralli devam ediyor ($rallyCount. vuruş) - Manşet karşılama ve pas"
          actionBadge = "RALLİ"
          ballX.animateTo(Random.nextFloat() * 0.6f + 0.2f, tween(800))
        }
      }
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Voleybol Set Skorları
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = match.homeTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "$homeSetPoints - $awaySetPoints", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
          Text(text = setScoreText, color = Color(0xFF94A3B8), fontSize = 9.sp)
        }

        Text(text = match.awayTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2D VOLEYBOL TARAFLEX SAHASI CANVAS
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(RoundedCornerShape(12.dp))
        .border(2.dp, Color(0xFF0369A1), RoundedCornerShape(12.dp))
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Dış Mavi Taraflex Bölge
        drawRect(color = Color(0xFF0284C7))

        // 2. İç Turuncu Oyun Alanı (9m x 18m oranında)
        val padX = w * 0.08f
        val padY = h * 0.12f
        val courtW = w - padX * 2
        val courtH = h - padY * 2

        drawRect(
          color = Color(0xFFEA580C),
          topLeft = Offset(padX, padY),
          size = Size(courtW, courtH)
        )

        // Beyaz Dış Sınır Çizgileri
        drawRect(
          color = Color.White,
          topLeft = Offset(padX, padY),
          size = Size(courtW, courtH),
          style = Stroke(width = 3f)
        )

        // 3m Hücum Çizgileri (Attack lines)
        val leftAttackX = padX + courtW * 0.333f
        val rightAttackX = padX + courtW * 0.666f
        drawLine(
          color = Color.White,
          start = Offset(leftAttackX, padY),
          end = Offset(leftAttackX, padY + courtH),
          strokeWidth = 2.5f
        )
        drawLine(
          color = Color.White,
          start = Offset(rightAttackX, padY),
          end = Offset(rightAttackX, padY + courtH),
          strokeWidth = 2.5f
        )

        // ORTA FİLE (Yüksek Voleybol Filesi & Kırmızı/Beyaz Antenler)
        val netX = w / 2
        drawLine(
          color = Color.White,
          start = Offset(netX, padY - 14f),
          end = Offset(netX, padY + courtH + 14f),
          strokeWidth = 4.5f
        )
        // File ızgara efekti
        val step = 14f
        var curY = padY
        while (curY <= padY + courtH) {
          drawLine(
            color = Color(0xFF475569),
            start = Offset(netX - 5f, curY),
            end = Offset(netX + 5f, curY),
            strokeWidth = 1.5f
          )
          curY += step
        }
        // Antenler (Kırmızı çubuklar)
        drawLine(
          color = Color.Red,
          start = Offset(netX, padY - 18f),
          end = Offset(netX, padY),
          strokeWidth = 3f
        )
        drawLine(
          color = Color.Red,
          start = Offset(netX, padY + courtH),
          end = Offset(netX, padY + courtH + 18f),
          strokeWidth = 3f
        )

        // HAREKETLİ SARI-MAVİ VOLEYBOL TOPU
        val bx = ballX.value * w
        val by = ballY.value * h
        drawCircle(
          color = Color.Black.copy(alpha = 0.25f),
          radius = 7f,
          center = Offset(bx + 2f, by + 4f)
        )
        drawCircle(
          color = Color(0xFFFACC15),
          radius = 7.5f,
          center = Offset(bx, by)
        )
        drawCircle(
          color = Color(0xFF1D4ED8),
          radius = 4f,
          center = Offset(bx, by)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Canlı Anlatım Çubuğu
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = when (actionBadge) {
            "SMAÇ" -> LiveRed
            "ACE" -> GoldYellow
            "BLOK" -> Color(0xFF0284C7)
            else -> TealDark
          },
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = actionBadge,
            color = if (actionBadge == "ACE") Color(0xFF0F172A) else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = currentAction,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * 🏎️ MOTOR SPORLARI 2D CANLI PİST & TELEMETRİ SİMÜLATÖRÜ (MotoGP & WRC Ralli Standardı)
 * - Asfalt Yarış Pisti / Ralli Çakıl Yolu, Kırmızı-Beyaz Kerbler, Başlangıç Grid Çizgileri
 * - Canlı Telemetri HUD: Hız Kadranı (342 km/h), Vites (G6), Yatma Açısı (58° Lean Angle), DRS / Slipstream
 * - S1, S2, S3 Sektör Zamanları ve Liderle Fark (+0.238s)
 * - Puan Tablosu ve Pit Stop Uyarısı
 */
@Composable
fun Motorsport2DCircuitSimulator(
  match: Match,
  isWrc: Boolean = false
) {
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED ||
      (match.sport == Sport.MOTORSPORTS && match.minute >= 25)
  var isFinished by remember(match.id, match.status) {
    mutableStateOf(matchIsAlreadyFinished)
  }
  var speedKmh by remember(match.id) { mutableIntStateOf(if (isFinished) 0 else 318) }
  var currentGear by remember(match.id) { mutableStateOf(if (isFinished) "N" else "G6") }
  var leanAngle by remember(match.id) { mutableIntStateOf(if (isFinished) 0 else 56) }
  var currentLap by remember(match.id) { mutableIntStateOf(if (isFinished) 27 else 16) }
  var lapTime by remember(match.id) { mutableStateOf("1:31.428") }
  var gapToLeader by remember(match.id) { mutableStateOf(if (isFinished) "FİNİŞ" else "+0.184s") }
  var tyreCondition by remember(match.id) { mutableStateOf(if (isFinished) "Soğuma" else "Soft (%82)") }
  var s1Status by remember(match.id) { mutableStateOf("🟣 28.140s") }
  var s2Status by remember(match.id) { mutableStateOf("🟡 34.210s") }
  var s3Status by remember(match.id) { mutableStateOf("🟣 29.078s") }
  var liveEventText by remember(match.id) {
    mutableStateOf(
      if (isFinished) "🏁 DAMALI BAYRAK! Yarış tamamlandı. Lider: ${match.homeTeam}"
      else if (isWrc) "🏁 WRC Özel Etap 14: Çakıl zemin, Power Stage liderliği!"
      else "🏍️ MotoGP Mugello: Arka düzlükte 348 km/h son sürat!"
    )
  }

  val bikeProgress = remember { Animatable(if (isFinished) 1.0f else 0f) }

  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      bikeProgress.snapTo(0f)
      // Düzlük Hızlanma
      speedKmh = Random.nextInt(335, 355)
      currentGear = "G6"
      leanAngle = Random.nextInt(0, 15)
      bikeProgress.animateTo(0.45f, tween(1800, easing = LinearEasing))

      // Viraj Frenleme ve Yatma
      speedKmh = Random.nextInt(120, 160)
      currentGear = "G3"
      leanAngle = Random.nextInt(52, 64)
      liveEventText = if (isWrc) "⚠️ Keskin Saç Tokası Virajı - Driftle geçildi!" else "🏍️ Casanova Virajı: 62° yatma açısıyla apexe indi!"
      bikeProgress.animateTo(0.75f, tween(1600, easing = FastOutSlowInEasing))

      // Çıkış ve Finiş
      speedKmh = Random.nextInt(260, 310)
      currentGear = "G5"
      leanAngle = Random.nextInt(15, 30)
      bikeProgress.animateTo(1.0f, tween(1200, easing = LinearEasing))
      if (currentLap >= 27) {
        isFinished = true
        speedKmh = 0
        currentGear = "N"
        gapToLeader = "FİNİŞ"
        liveEventText = "🏁 DAMALI BAYRAK! Yarış tamamlandı. Lider: ${match.homeTeam}"
        break
      } else {
        currentLap++
      }
      delay(300L)
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // 1. Canlı Telemetri HUD Barı (Hız, Vites, Fark)
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Hız Kadranı
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Speed, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(text = "$speedKmh KM/H", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(text = "Tur: $currentLap/27 • Vites: $currentGear", color = Color(0xFF94A3B8), fontSize = 9.sp)
          }
        }

        // Sektör Dereceleri
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
            Text(text = "S1", color = Color(0xFFA855F7), fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
          }
          Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
            Text(text = "S2", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
          }
          Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
            Text(text = "S3", color = Color(0xFFA855F7), fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
          }
        }

        // Liderle Fark & Lastik
        Column(horizontalAlignment = Alignment.End) {
          Text(text = gapToLeader, color = if (gapToLeader.startsWith("+")) LiveRed else Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 13.sp)
          Text(text = "Lastik: $tyreCondition", color = Color(0xFFCBD5E1), fontSize = 9.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2D YARIŞ PİSTİ / DEVRE CANVAS
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(RoundedCornerShape(12.dp))
        .border(2.dp, if (isWrc) Color(0xFFB45309) else Color(0xFF334155), RoundedCornerShape(12.dp))
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Zemin (Çim / Çakıl)
        drawRect(color = if (isWrc) Color(0xFF78350F) else Color(0xFF166534))

        // 2. Asfalt Yarış Pisti Eğrisi (Oval pist + S Virajları)
        val asphaltColor = if (isWrc) Color(0xFF9A3412) else Color(0xFF1E293B)
        val trackWidth = 38f

        val circuitPath = Path().apply {
          moveTo(w * 0.15f, h * 0.5f)
          cubicTo(w * 0.15f, h * 0.12f, w * 0.85f, h * 0.12f, w * 0.85f, h * 0.5f)
          cubicTo(w * 0.85f, h * 0.88f, w * 0.15f, h * 0.88f, w * 0.15f, h * 0.5f)
          close()
        }

        // Dış Kerbler (Kırmızı & Beyaz Yarış Bordürleri)
        drawPath(
          path = circuitPath,
          color = Color.Red,
          style = Stroke(width = trackWidth + 10f, cap = StrokeCap.Round)
        )
        drawPath(
          path = circuitPath,
          color = Color.White,
          style = Stroke(width = trackWidth + 6f, cap = StrokeCap.Round)
        )

        // Asfalt Pist Gövdesi
        drawPath(
          path = circuitPath,
          color = asphaltColor,
          style = Stroke(width = trackWidth, cap = StrokeCap.Round)
        )

        // Beyaz Yarış Çizgisi (Apex kesik çizgisi)
        drawPath(
          path = circuitPath,
          color = Color.White.copy(alpha = 0.45f),
          style = Stroke(width = 2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f)))
        )

        // Finiş / Start Çizgisi (Dama deseni)
        drawLine(
          color = Color.White,
          start = Offset(w * 0.5f, h * 0.88f - trackWidth / 2),
          end = Offset(w * 0.5f, h * 0.88f + trackWidth / 2),
          strokeWidth = 4f
        )

        // HAREKETLİ PİLOT / ARAÇ (MotoGP Motosikleti / WRC Ralli Otomobili)
        val progress = bikeProgress.value
        val angle = progress * 2 * Math.PI
        val rx = w * 0.35f
        val ry = h * 0.38f
        val cx = w * 0.5f + (rx * cos(angle)).toFloat()
        val cy = h * 0.5f + (ry * sin(angle)).toFloat()

        // Gölge
        drawCircle(
          color = Color.Black.copy(alpha = 0.5f),
          radius = 9f,
          center = Offset(cx + 3f, cy + 3f)
        )

        // Pilot Noktası (Kırmızı/Sarı Yarış Rengi)
        drawCircle(
          color = if (isWrc) Color(0xFFDC2626) else GoldYellow,
          radius = 9.5f,
          center = Offset(cx, cy)
        )
        drawCircle(
          color = Color.White,
          radius = 5.5f,
          center = Offset(cx, cy)
        )
      }

      // Pilot İsim Rozeti
      Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.85f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = if (isWrc) "🏎️ Neuville / Ogier" else "🏍️ Bagnaia / Martin", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "P1 (Canlı Lider)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 9.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Canlı Yarış Durumu Tickerı
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = Color(0xFFD97706),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = if (isWrc) "WRC CANLI" else "MOTOGP GRID",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = liveEventText,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * 🎱 BİLARDO & SNOOKER 2D CANLI MASA SİMÜLATÖRÜ (Maçkolik / Nesine Canlı Bilardo)
 * - Yeşil Çuha Masa Zemini & Masif Ahşap Küpeşte Kenarlıklar
 * - 3-Bant Karom / Snooker Topları (Beyaz, Sarı, Kırmızı)
 * - İsteka Vuruşu, Kavisli Bant Sekmeleri & Karom Sayı Efekti
 * - Canlı Seri (High Break) & Sayı Skorbordu
 */
@Composable
fun Billiards2DCourtSimulator(
  match: Match
) {
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED ||
      (match.homeScore >= 40 || match.awayScore >= 40)
  var isFinished by remember(match.id, match.status) {
    mutableStateOf(matchIsAlreadyFinished)
  }
  var homeScore by remember(match.id) { mutableIntStateOf(if (match.homeScore > 0) match.homeScore else 40) }
  var awayScore by remember(match.id) { mutableIntStateOf(if (match.awayScore > 0) match.awayScore else 36) }
  var currentRun by remember(match.id) { mutableIntStateOf(if (isFinished) 0 else 6) }
  var activePlayer by remember(match.id) { mutableStateOf(match.homeTeam) }
  var liveEventText by remember(match.id) {
    mutableStateOf(
      if (isFinished) "🎱 MAÇ SONUCU (BİTTİ): Hedef 40 sayıya ulaşıldı! ${match.homeTeam} $homeScore - $awayScore ${match.awayTeam}"
      else "🎱 ${match.homeTeam} nefis bir 3-bant brikol vuruşuyla seriye devam ediyor (Seri: 6)!"
    )
  }

  val whiteBallX = remember { Animatable(0.35f) }
  val whiteBallY = remember { Animatable(0.48f) }
  val yellowBallX = remember { Animatable(0.65f) }
  val yellowBallY = remember { Animatable(0.55f) }
  val redBallX = remember { Animatable(0.50f) }
  val redBallY = remember { Animatable(0.32f) }

  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      delay(3500L)
      if (isFinished) break
      whiteBallX.animateTo(0.48f, tween(600))
      redBallX.animateTo(0.58f, tween(500))
      delay(1200L)
      homeScore++
      currentRun++
      liveEventText = "⚡ Sayı! ${activePlayer} harika karom yaptı (+1 Sayı, Seri: $currentRun)"
      if (homeScore >= 40 || awayScore >= 40) {
        isFinished = true
        liveEventText = "🎱 MAÇ SONUCU (BİTTİ): Hedef 40 sayı tamamlandı! Kazanan: $activePlayer 🏆"
        break
      }
      delay(3000L)
      whiteBallX.animateTo(0.35f, tween(700))
      redBallX.animateTo(0.50f, tween(600))
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Bilardo Skorbord
    Surface(
      color = Color(0xFF0F2B1D),
      shape = RoundedCornerShape(12.dp),
      border = BorderStroke(1.dp, GoldYellow.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = match.homeTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = "Aktif İsteka • Seri: $currentRun", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(color = Color(0xFF064E3B), shape = RoundedCornerShape(6.dp)) {
            Text(
              text = "$homeScore - $awayScore",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 16.sp,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(text = match.awayTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = "Hedef: 40 Sayı", color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
      }
    }

    // 2D Çuha Masa Çizimi
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFF5C3317)) // Ahşap Küpeşte
        .border(4.dp, Color(0xFF3E2210), RoundedCornerShape(14.dp))
        .padding(8.dp)
    ) {
      androidx.compose.foundation.Canvas(
        modifier = Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(6.dp))
          .background(Color(0xFF1B6B40)) // Yeşil Çuha
      ) {
        val w = size.width
        val h = size.height

        // Bant Elmas Noktaları (Cushion Diamonds)
        val diamondColor = Color.White.copy(alpha = 0.6f)
        for (i in 1..7) {
          drawCircle(color = diamondColor, radius = 2.5f, center = Offset(w * (i / 8f), 4f))
          drawCircle(color = diamondColor, radius = 2.5f, center = Offset(w * (i / 8f), h - 4f))
        }
        for (i in 1..3) {
          drawCircle(color = diamondColor, radius = 2.5f, center = Offset(4f, h * (i / 4f)))
          drawCircle(color = diamondColor, radius = 2.5f, center = Offset(w - 4f, h * (i / 4f)))
        }

        // Top 1: Beyaz Top
        drawCircle(color = Color.White, radius = 9f, center = Offset(w * whiteBallX.value, h * whiteBallY.value))
        drawCircle(color = Color(0xFFE2E8F0), radius = 3f, center = Offset(w * whiteBallX.value - 2f, h * whiteBallY.value - 2f))

        // Top 2: Sarı Top
        drawCircle(color = GoldYellow, radius = 9f, center = Offset(w * yellowBallX.value, h * yellowBallY.value))
        drawCircle(color = Color(0xFFFEF08A), radius = 3f, center = Offset(w * yellowBallX.value - 2f, h * yellowBallY.value - 2f))

        // Top 3: Kırmızı Top
        drawCircle(color = LiveRed, radius = 9f, center = Offset(w * redBallX.value, h * redBallY.value))
        drawCircle(color = Color(0xFFFCA5A5), radius = 3f, center = Offset(w * redBallX.value - 2f, h * redBallY.value - 2f))
      }

      // Canlı Durum Rozeti
      Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.85f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.align(Alignment.BottomStart).padding(6.dp)
      ) {
        Text(
          text = "🎱 3-BANT UMB DÜNYA KUPASI",
          color = GoldYellow,
          fontWeight = FontWeight.Bold,
          fontSize = 9.5.sp,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Canlı Bilardo Anlatım Tickerı
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "🎙️", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = liveEventText,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * 🀄 101 OKEY & KART OYUNLARI 2D CANLI MASA SİMÜLATÖRÜ (Maçkolik / Nesine Özel Zeka Oyunları)
 * - 4'lü Masa Düzeni & Ahşap Istakalar
 * - Perler, Gösterge Taşı, Okey Atma & Ceza Skorbordu
 * - Anlık Taş Çekme / Atma Animasyonu
 */
@Composable
fun OkeyCards2DSimulator(
  match: Match
) {
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED
  var isFinished by remember(match.id, match.status) {
    mutableStateOf(matchIsAlreadyFinished)
  }
  var currentHand by remember(match.id) { mutableIntStateOf(if (isFinished) 11 else 7) }
  var homeScore by remember(match.id) { mutableIntStateOf(if (match.homeScore > 0) match.homeScore else 420) }
  var awayScore by remember(match.id) { mutableIntStateOf(if (match.awayScore > 0) match.awayScore else 390) }
  var currentActionText by remember(match.id) {
    mutableStateOf(
      if (isFinished) "🏁 Oyun tamamlandı. Final Ceza Puanları: ${match.homeTeam} $homeScore - $awayScore ${match.awayTeam}"
      else "🀄 ${match.homeTeam} ortadan taş çekti, perini tamamlayıp el açtı (-101 Puan)!"
    )
  }
  var gostergeTasi by remember(match.id) { mutableStateOf("🔴 Kırmızı 7 (Okey: Kırmızı 8)") }

  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      delay(4000L)
      if (isFinished) break
      homeScore += 10
      currentActionText = "🀄 ${match.homeTeam} seriyi bozmadan taş attı. Mehmet Çavuş yandan taşı aldı!"
      delay(4000L)
      if (isFinished) break
      awayScore += 20
      currentHand++
      if (currentHand >= 11) {
        isFinished = true
        currentActionText = "🏁 11. El tamamlandı! Masa kapandı. Kazanan: ${if (homeScore < awayScore) match.homeTeam else match.awayTeam} 🏆"
        break
      }
      currentActionText = "⚡ ${match.awayTeam} çift açtı! Masaya okey dönüyor ($currentHand / 11. El)."
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Okey Skorbord
    Surface(
      color = Color(0xFF2E1065),
      shape = RoundedCornerShape(12.dp),
      border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = match.homeTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = "Ceza Puanı: $homeScore", color = Color(0xFFE9D5FF), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
        Surface(color = Color(0xFF581C87), shape = RoundedCornerShape(6.dp)) {
          Text(
            text = "EL: 7 / 11",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(text = match.awayTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = "Ceza Puanı: $awayScore", color = Color(0xFFE9D5FF), fontSize = 10.sp)
        }
      }
    }

    // 2D Okey Masası ve Istaka Çizimi
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFF1E3A2F)) // Yeşil Çuha Masa
        .border(3.dp, Color(0xFF2D1810), RoundedCornerShape(14.dp))
        .padding(10.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Rakip Istaka Temsili
        Surface(color = Color(0xFF78350F), shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth(0.7f).height(16.dp)) {
          Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Rakip Istaka (14 Taş Kapalı)", fontSize = 8.5.sp, color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold)
          }
        }

        // Masa Ortası: Taş Destesi ve Gösterge Taşı
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = Color(0xFFFEF3C7),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color(0xFFD97706)),
            modifier = Modifier.size(width = 38.dp, height = 52.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
              Text(text = "DESTE", fontSize = 7.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
              Text(text = "🀄", fontSize = 18.sp)
            }
          }

          // Gösterge Taşı
          Surface(
            color = Color(0xFFFFFBEB),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.5.dp, LiveRed),
            modifier = Modifier.size(width = 38.dp, height = 52.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
              Text(text = "7", fontSize = 20.sp, color = LiveRed, fontWeight = FontWeight.Black)
              Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LiveRed))
            }
          }
        }

        // Oyuncu Istakası (Perler)
        Surface(
          color = Color(0xFF92400E),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, Color(0xFFB45309)),
          modifier = Modifier.fillMaxWidth().height(36.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            listOf("1", "2", "3", "•", "10", "11", "12", "•", "5", "5", "5").forEach { tile ->
              Surface(
                color = Color(0xFFFFFBEB),
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier.size(width = 20.dp, height = 26.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(text = tile, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (tile == "•") Color.Gray else Color(0xFF1E3A8A))
                }
              }
            }
          }
        }
      }

      // Gösterge Rozeti
      Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.85f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.align(Alignment.TopStart)
      ) {
        Text(
          text = gostergeTasi,
          color = GoldYellow,
          fontWeight = FontWeight.Bold,
          fontSize = 8.5.sp,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Canlı Okey Anlatım Tickerı
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "🀄", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = currentActionText,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * 🎾 TENİS 2D CANLI KORT SİMÜLATÖRÜ (ATP & WTA Tour / Grand Slam Standardı)
 * - Mavi & Lacivert Sert Zemin / Roland Garros Toprak / Wimbledon Çim Kort
 * - File, Servis Kutuları, Dip Çizgi (Baseline), Yan Çizgiler (Tramlines)
 * - Optik Sarı Tenis Topu, Kavisli Servis, Forehand/Backhand Winner ve Ralli Fiziği
 * - Canlı Set Skorları (Örn: 6-4, 5-7, 4-3), Oyun Puanı (15, 30, 40, Avantaj), Servis Atan Göstergesi (🎾)
 * - Karşılaşma bittiğinde oyunu kesinlikle durdurur, MS özetini ve kazanan kupasını gösterir.
 */
@Composable
fun Tennis2DCourtSimulator(
  match: Match,
  onTriggerPoint: (String, Boolean) -> Unit = { _, _ -> }
) {
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED ||
      (match.sport == Sport.TENNIS && match.minute >= 90)
  var isFinished by remember(match.id, match.status, match.minute) {
    mutableStateOf(matchIsAlreadyFinished)
  }

  var homeSets by remember(match.id) { mutableIntStateOf(if (isFinished) 2 else if (match.homeScore > 0) match.homeScore else 1) }
  var awaySets by remember(match.id) { mutableIntStateOf(if (isFinished) 1 else if (match.awayScore > 0) match.awayScore else 1) }
  var homeGames by remember(match.id) { mutableIntStateOf(if (isFinished) 6 else 4) }
  var awayGames by remember(match.id) { mutableIntStateOf(if (isFinished) 3 else 3) }
  var set1Score by remember(match.id) { mutableStateOf("6-4") }
  var set2Score by remember(match.id) { mutableStateOf(if (isFinished) "4-6" else "4-6") }
  var set3Score by remember(match.id) { mutableStateOf(if (isFinished) "6-3" else "4-3") }
  var currentPointText by remember(match.id) { mutableStateOf(if (isFinished) "OYUN" else "40 - 30") }
  var isHomeServing by remember(match.id) { mutableStateOf(true) }
  var rallyShots by remember(match.id) { mutableIntStateOf(if (isFinished) 0 else 5) }
  var serveSpeed by remember(match.id) { mutableIntStateOf(214) }

  var currentAction by remember(match.id) {
    mutableStateOf(
      if (isFinished) "🏁 Karşılaşma tamamlandı! Kazanan: ${match.homeTeam} 🏆 (2-1 Set)"
      else "🎾 ${match.homeTeam} 1. servis atıyor (214 km/h)..."
    )
  }
  var actionBadge by remember(match.id) { mutableStateOf(if (isFinished) "MS BİTTİ" else "SERVİS") }

  val ballX = remember { Animatable(0.5f) }
  val ballY = remember { Animatable(if (isFinished) 0.5f else 0.20f) }
  val ballScale = remember { Animatable(1.0f) }

  // Maç bittiğinde topu fileye sabitle
  LaunchedEffect(isFinished) {
    if (isFinished) {
      ballX.snapTo(0.5f)
      ballY.snapTo(0.5f)
      ballScale.snapTo(1.0f)
      RealtimeSportsWebSocketService.broadcastMatchFinished(match.id, homeSets, awaySets, "Tenis maçı tamamlandı.")
    }
  }

  // React to external match status changes (e.g. from WebSocket)
  LaunchedEffect(match.status, match.minute) {
    if (match.status == MatchStatus.FINISHED || (match.sport == Sport.TENNIS && match.minute >= 90)) {
      isFinished = true
      currentAction = "🏁 Karşılaşma tamamlandı! Maç Sonucu (MS)"
      actionBadge = "MS BİTTİ"
      ballX.snapTo(0.5f)
      ballY.snapTo(0.5f)
      ballScale.snapTo(1.0f)
    }
  }

  // Canlı Tenis Ralli ve Sayı Döngüsü
  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      delay(2400L)
      if (isFinished) break

      val event = Random.nextInt(8)
      when (event) {
        0 -> { // Ace Servis
          val isHome = isHomeServing
          val server = if (isHome) match.homeTeam else match.awayTeam
          serveSpeed = Random.nextInt(198, 226)
          currentAction = "⚡ ACE! $server harika bir servis attı ($serveSpeed km/h)!"
          actionBadge = "ACE"
          val targetY = if (isHome) 0.85f else 0.15f
          ballScale.animateTo(1.4f, tween(200))
          ballY.animateTo(targetY, tween(450, easing = FastOutSlowInEasing))
          ballScale.animateTo(1.0f, tween(150))
          rallyShots = 1
          currentPointText = if (isHome) "40 - 15" else "15 - 40"
          onTriggerPoint(server, isHome)
        }
        1, 2 -> { // Forehand / Backhand Winner
          val isHome = Random.nextBoolean()
          val hitter = if (isHome) match.homeTeam else match.awayTeam
          val shotType = if (Random.nextBoolean()) "Forehand Paralel Winner" else "Backhand Çapraz Winner"
          currentAction = "🔥 $shotType! $hitter çizgiyi buldu, puan!"
          actionBadge = "WINNER"
          ballX.animateTo(if (isHome) 0.82f else 0.18f, tween(500, easing = FastOutSlowInEasing))
          ballY.animateTo(if (isHome) 0.88f else 0.12f, tween(500, easing = FastOutSlowInEasing))
          rallyShots = Random.nextInt(3, 9)
          if (isHome) {
            homeGames++
            if (homeGames >= 6 && homeGames - awayGames >= 2) {
              homeSets++
              if (homeSets >= 2) {
                isFinished = true
                currentAction = "🏁 MAÇ SONUCU: ${match.homeTeam} şampiyon! 🏆"
                actionBadge = "MS BİTTİ"
                break
              } else {
                homeGames = 0
                awayGames = 0
              }
            }
          } else {
            awayGames++
            if (awayGames >= 6 && awayGames - homeGames >= 2) {
              awaySets++
              if (awaySets >= 2) {
                isFinished = true
                currentAction = "🏁 MAÇ SONUCU: ${match.awayTeam} şampiyon! 🏆"
                actionBadge = "MS BİTTİ"
                break
              } else {
                homeGames = 0
                awayGames = 0
              }
            }
          }
          currentPointText = "OYUN ($homeGames - $awayGames)"
          isHomeServing = !isHomeServing
        }
        3 -> { // File Volesi
          val isHome = Random.nextBoolean()
          val netPlayer = if (isHome) match.homeTeam else match.awayTeam
          currentAction = "🏸 File Önü Volesi! $netPlayer fileye inip puanı bitirdi."
          actionBadge = "VOLE"
          ballX.animateTo(0.5f, tween(400))
          ballY.animateTo(0.48f, tween(400))
          rallyShots = 3
        }
        4 -> { // Basit Hata (Unforced Error)
          val isHome = Random.nextBoolean()
          val errorPlayer = if (isHome) match.homeTeam else match.awayTeam
          currentAction = "⚠️ Basit Hata! $errorPlayer topu fileye taktı."
          actionBadge = "HATA"
          ballX.animateTo(0.5f, tween(350))
          ballY.animateTo(0.5f, tween(350))
        }
        else -> { // Ralli devam ediyor
          val isTopToBottom = rallyShots % 2 == 0
          rallyShots++
          currentAction = "🎾 Nefes Kesen Ralli ($rallyShots vuruş) - Tempolu karşılıklı vuruşlar"
          actionBadge = "RALLİ"
          ballX.animateTo(Random.nextFloat() * 0.5f + 0.25f, tween(650, easing = LinearEasing))
          ballY.animateTo(if (isTopToBottom) 0.80f else 0.20f, tween(650, easing = LinearEasing))
        }
      }
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // 1. Tenis Skorbordu (Setler & Oyun Puanı)
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = match.homeTeam,
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (isHomeServing && !isFinished) {
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "🎾", fontSize = 10.sp)
            }
          }
          Text(text = "Setler: $homeSets", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, if (isFinished) Color(0xFF10B981) else GoldYellow)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = if (isFinished) "MS" else currentPointText,
              color = if (isFinished) Color(0xFF34D399) else GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
            Text(
              text = "$homeGames - $awayGames",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isHomeServing && !isFinished) {
              Text(text = "🎾", fontSize = 10.sp)
              Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
              text = match.awayTeam,
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Text(text = "Setler: $awaySets", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2. 2D TENİS KORTU CANVAS
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(230.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF172554)) // Dış çevre kort rengi (Lacivert)
        .border(1.5.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val padX = w * 0.10f
        val padY = h * 0.08f
        val courtW = w - (padX * 2)
        val courtH = h - (padY * 2)

        // İç Kort Alanı (Resmi ATP Mavi Zemin)
        drawRect(
          color = Color(0xFF1D4ED8),
          topLeft = Offset(padX, padY),
          size = Size(courtW, courtH)
        )

        // Dış Çizgiler (Baseline & Doubles Sidelines)
        drawRect(
          color = Color.White,
          topLeft = Offset(padX, padY),
          size = Size(courtW, courtH),
          style = Stroke(width = 2.dp.toPx())
        )

        // Tekler Yan Çizgileri (Singles Sidelines)
        val singlesIndent = courtW * 0.12f
        drawLine(
          color = Color(0xDDFFFFFF),
          start = Offset(padX + singlesIndent, padY),
          end = Offset(padX + singlesIndent, padY + courtH),
          strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
          color = Color(0xDDFFFFFF),
          start = Offset(padX + courtW - singlesIndent, padY),
          end = Offset(padX + courtW - singlesIndent, padY + courtH),
          strokeWidth = 1.5.dp.toPx()
        )

        // Servis Çizgileri (Service Lines)
        val serviceY1 = padY + courtH * 0.28f
        val serviceY2 = padY + courtH * 0.72f
        drawLine(
          color = Color(0xDDFFFFFF),
          start = Offset(padX + singlesIndent, serviceY1),
          end = Offset(padX + courtW - singlesIndent, serviceY1),
          strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
          color = Color(0xDDFFFFFF),
          start = Offset(padX + singlesIndent, serviceY2),
          end = Offset(padX + courtW - singlesIndent, serviceY2),
          strokeWidth = 1.5.dp.toPx()
        )

        // Orta Servis Çizgisi (Center Service Line)
        val midX = padX + courtW / 2
        drawLine(
          color = Color(0xDDFFFFFF),
          start = Offset(midX, serviceY1),
          end = Offset(midX, serviceY2),
          strokeWidth = 1.5.dp.toPx()
        )

        // Dip Çizgi Orta İşareti (Center Marks)
        drawLine(
          color = Color.White,
          start = Offset(midX, padY),
          end = Offset(midX, padY + 6.dp.toPx()),
          strokeWidth = 2.dp.toPx()
        )
        drawLine(
          color = Color.White,
          start = Offset(midX, padY + courtH - 6.dp.toPx()),
          end = Offset(midX, padY + courtH),
          strokeWidth = 2.dp.toPx()
        )

        // NET (FİLE) - Kortu İkiye Bölen Çelik Tel & Beyaz File Bandı
        val netY = padY + courtH / 2
        // File Gölgesi
        drawLine(
          color = Color(0x66000000),
          start = Offset(padX - 8.dp.toPx(), netY + 2.dp.toPx()),
          end = Offset(padX + courtW + 8.dp.toPx(), netY + 2.dp.toPx()),
          strokeWidth = 4.dp.toPx()
        )
        // File Beyaz Bandı
        drawLine(
          color = Color.White,
          start = Offset(padX - 6.dp.toPx(), netY),
          end = Offset(padX + courtW + 6.dp.toPx(), netY),
          strokeWidth = 3.5.dp.toPx()
        )
        // File Yan Direkleri (Net Posts)
        drawCircle(color = Color(0xFFCBD5E1), radius = 3.dp.toPx(), center = Offset(padX - 6.dp.toPx(), netY))
        drawCircle(color = Color(0xFFCBD5E1), radius = 3.dp.toPx(), center = Offset(padX + courtW + 6.dp.toPx(), netY))

        // DİNAMİK OPTİK SARI TENİS TOPU VE GÖLGESİ
        val bx = size.width * ballX.value
        val by = size.height * ballY.value
        val bScale = ballScale.value

        // Top Gölgesi
        drawCircle(
          color = Color(0x55000000),
          radius = 4.5.dp.toPx() * (1f / bScale),
          center = Offset(bx + 2.dp.toPx(), by + 2.dp.toPx())
        )
        // Optik Sarı Top Çekirdeği
        drawCircle(
          color = Color(0xFFCCFF00),
          radius = 5.dp.toPx() * bScale,
          center = Offset(bx, by)
        )
        // Top Beyaz Kavis Çizgisi
        drawCircle(
          color = Color.White,
          radius = 2.5.dp.toPx() * bScale,
          center = Offset(bx - 1.dp.toPx(), by - 1.dp.toPx()),
          style = Stroke(width = 1.dp.toPx())
        )
      }

      // Oyuncu İsimleri Kort Yerleşimi (Üst ve Alt Çizgiler)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.TopCenter)
          .padding(top = 4.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Surface(color = Color(0xCC0F172A), shape = RoundedCornerShape(4.dp)) {
          Text(
            text = "🎾 ${match.homeTeam}",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Surface(color = Color(0xCC0F172A), shape = RoundedCornerShape(4.dp)) {
          Text(
            text = "Hız: $serveSpeed km/h",
            color = GoldYellow,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(bottom = 4.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Surface(color = Color(0xCC0F172A), shape = RoundedCornerShape(4.dp)) {
          Text(
            text = "🎾 ${match.awayTeam}",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Surface(color = Color(0xCC0F172A), shape = RoundedCornerShape(4.dp)) {
          Text(
            text = "Ralli: $rallyShots",
            color = Color(0xFF38BDF8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      // MAÇ BİTTİĞİNDE KORT ÜSTÜ RESMİ SONUÇ ROZETİ
      if (isFinished) {
        Surface(
          color = Color(0xF0064E3B),
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, GoldYellow),
          modifier = Modifier.align(Alignment.Center)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Text(
              text = "🏁 MAÇ SONUCU (MS)",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
            Text(
              text = "Kazanan: ${if (homeSets > awaySets) match.homeTeam else match.awayTeam} 🏆",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
            Text(
              text = "Setler: $set1Score, $set2Score, $set3Score",
              color = Color(0xFF6EE7B7),
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 3. Canlı Tenis Anlatım Tickerı
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = if (isFinished) Color(0xFF065F46) else Color(0xFF1D4ED8),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = actionBadge,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = currentAction,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * 🏒 BUZ HOKEYİ 2D CANLI PİST SİMÜLATÖRÜ (NHL & KHL Standardı)
 * - Beyaz Buz Zemin, Kırmızı Orta Çizgi, Mavi Ofsayt Çizgileri, Başlama Daireleri ve Kale Kafesleri
 * - Siyah Pak Fiziği, Sert Şut (Slapshot), Kaleci Kurtarışı & Güç Oyunu (Power Play)
 * - Periyot Göstergesi (1.P, 2.P, 3.P, MS)
 */
@Composable
fun IceHockey2DRinkSimulator(
  match: Match,
  onTriggerGoal: (String, Boolean) -> Unit = { _, _ -> }
) {
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED ||
      (match.sport == Sport.ICE_HOCKEY && match.minute >= 60)
  var isFinished by remember(match.id, match.status) {
    mutableStateOf(matchIsAlreadyFinished)
  }

  var homeGoals by remember(match.id, match.homeScore) { mutableIntStateOf(if (match.homeScore > 0) match.homeScore else 3) }
  var awayGoals by remember(match.id, match.awayScore) { mutableIntStateOf(if (match.awayScore > 0) match.awayScore else 2) }
  var period by remember(match.id) { mutableStateOf(if (isFinished) "MAÇ SONUCU (MS)" else "3. Periyot") }
  var periodMinutesRemaining by remember(match.id) { mutableIntStateOf(if (isFinished) 0 else 6) }
  var currentAction by remember(match.id) {
    mutableStateOf(
      if (isFinished) "🏁 Karşılaşma tamamlandı! Maç Sonucu: ${match.homeTeam} $homeGoals - $awayGoals ${match.awayTeam}"
      else "🏒 ${match.homeTeam} mavi çizgiyi geçti, sert bilek vuruşu (Wrist Shot)!"
    )
  }
  var actionBadge by remember(match.id) { mutableStateOf(if (isFinished) "MS BİTTİ" else "HÜCUM") }

  val puckX = remember { Animatable(0.5f) }
  val puckY = remember { Animatable(0.5f) }

  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      delay(2500L)
      if (isFinished) break

      val event = Random.nextInt(7)
      when (event) {
        0 -> { // GOL!
          val isHome = Random.nextBoolean()
          val scorer = if (isHome) match.homeTeam else match.awayTeam
          currentAction = "🚨 GOL! $scorer maviden füze gibi şutla ağları sarstı!"
          actionBadge = "GOL"
          puckX.animateTo(if (isHome) 0.94f else 0.06f, tween(350, easing = FastOutSlowInEasing))
          if (isHome) homeGoals++ else awayGoals++
          onTriggerGoal(scorer, isHome)
        }
        1, 2 -> { // Kaleci Kurtarışı
          val isHome = Random.nextBoolean()
          currentAction = "🛡️ İNANILMAZ KURTARIŞ! Kaleci pediyle topu çeldi!"
          actionBadge = "KURTARIŞ"
          puckX.animateTo(if (isHome) 0.88f else 0.12f, tween(400))
        }
        3 -> { // Slapshot
          currentAction = "⚡ SLAPSHOT! 165 km/h hızla kaleye giden şut direkten döndü!"
          actionBadge = "ŞUT"
          puckX.animateTo(0.85f, tween(300))
        }
        else -> { // Oyun Kurulumu
          val isHome = Random.nextBoolean()
          currentAction = "🏒 ${if (isHome) match.homeTeam else match.awayTeam} baskı kuruyor, pak çevriliyor"
          actionBadge = "PAS"
          puckX.animateTo(Random.nextFloat() * 0.4f + 0.3f, tween(600))
          puckY.animateTo(Random.nextFloat() * 0.6f + 0.2f, tween(600))
        }
      }

      if (periodMinutesRemaining > 0) {
        periodMinutesRemaining--
      } else {
        isFinished = true
        period = "MAÇ SONUCU (MS)"
        currentAction = "🏁 Karşılaşma tamamlandı! Maç Sonucu: ${match.homeTeam} $homeGoals - $awayGoals ${match.awayTeam}"
        actionBadge = "MS BİTTİ"
        break
      }
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Skorbord
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = match.homeTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(6.dp)) {
          Text(
            text = "$homeGoals - $awayGoals • $period",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Text(text = match.awayTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2D Buz Pisti Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFFE2E8F0)) // Açık gri/beyaz buz
        .border(2.dp, Color(0xFF0284C7), RoundedCornerShape(12.dp))
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Kırmızı Orta Çizgi
        drawLine(color = Color(0xFFDC2626), start = Offset(w / 2, 0f), end = Offset(w / 2, h), strokeWidth = 3.dp.toPx())
        // Orta Başlama Dairesi
        drawCircle(color = Color(0xFF0284C7), radius = 22.dp.toPx(), center = Offset(w / 2, h / 2), style = Stroke(2.dp.toPx()))

        // Mavi Çizgiler (Ofsayt / Bölge Çizgileri)
        drawLine(color = Color(0xFF2563EB), start = Offset(w * 0.32f, 0f), end = Offset(w * 0.32f, h), strokeWidth = 2.5.dp.toPx())
        drawLine(color = Color(0xFF2563EB), start = Offset(w * 0.68f, 0f), end = Offset(w * 0.68f, h), strokeWidth = 2.5.dp.toPx())

        // Kaleler
        drawRect(color = Color(0x88DC2626), topLeft = Offset(4f, h / 2 - 16.dp.toPx()), size = Size(8.dp.toPx(), 32.dp.toPx()))
        drawRect(color = Color(0x88DC2626), topLeft = Offset(w - 12.dp.toPx(), h / 2 - 16.dp.toPx()), size = Size(8.dp.toPx(), 32.dp.toPx()))

        // Siyah Pak (Puck)
        val px = w * puckX.value
        val py = h * puckY.value
        drawCircle(color = Color(0xFF0F172A), radius = 5.dp.toPx(), center = Offset(px, py))
      }

      if (isFinished) {
        Surface(
          color = Color(0xF0064E3B),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, GoldYellow),
          modifier = Modifier.align(Alignment.Center)
        ) {
          Text(
            text = "🏁 MAÇ SONUCU (BİTTİ)\n$homeGoals - $awayGoals",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = if (isFinished) Color(0xFF065F46) else Color(0xFF0284C7),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = actionBadge,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = currentAction, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
      }
    }
  }
}

/**
 * 🏓 MASA TENİSİ 2D CANLI MASA SİMÜLATÖRÜ (WTT & ITTF Standardı)
 * - Mavi Turnuva Masası, Beyaz Orta Çizgi & File
 * - Beyaz Top ve Hızlı Ralli Fiziği
 */
@Composable
fun TableTennis2DCourtSimulator(
  match: Match
) {
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED
  var isFinished by remember(match.id, match.status) {
    mutableStateOf(matchIsAlreadyFinished)
  }

  var homePoints by remember(match.id) { mutableIntStateOf(if (isFinished) 11 else 9) }
  var awayPoints by remember(match.id) { mutableIntStateOf(if (isFinished) 7 else 8) }
  var setNumber by remember(match.id) { mutableIntStateOf(if (isFinished) 5 else 4) }
  var liveActionText by remember(match.id) {
    mutableStateOf(
      if (isFinished) "🏁 Karşılaşma tamamlandı! Maç Sonucu: ${match.homeTeam} 3 - 2 ${match.awayTeam}"
      else "🏓 ${match.homeTeam} sert topspin ile hücum ediyor!"
    )
  }

  val ballX = remember { Animatable(0.5f) }
  val ballY = remember { Animatable(0.5f) }

  LaunchedEffect(match.id, isFinished) {
    if (isFinished) return@LaunchedEffect
    while (!isFinished) {
      delay(1800L)
      if (isFinished) break
      val isHome = Random.nextBoolean()
      ballX.animateTo(if (isHome) 0.85f else 0.15f, tween(400, easing = LinearEasing))
      if (isHome) homePoints++ else awayPoints++

      if (homePoints >= 11 || awayPoints >= 11) {
        isFinished = true
        liveActionText = "🏁 MAÇ SONUCU (BİTTİ): Kazanan: ${if (homePoints > awayPoints) match.homeTeam else match.awayTeam} 🏆"
        break
      } else {
        liveActionText = "🏓 Sayı: ${if (isHome) match.homeTeam else match.awayTeam} (+1 Sayı, Skor: $homePoints - $awayPoints)"
      }
    }
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = match.homeTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(text = "$homePoints - $awayPoints ($setNumber. Set)", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp)
        Text(text = match.awayTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF1E3A8A)) // Mavi Masa Tenisi Masası
        .border(2.dp, Color.White, RoundedCornerShape(12.dp))
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Orta Çizgi
        drawLine(color = Color(0x88FFFFFF), start = Offset(0f, h / 2), end = Offset(w, h / 2), strokeWidth = 1.5.dp.toPx())
        // File (Net)
        drawLine(color = Color.White, start = Offset(w / 2, 0f), end = Offset(w / 2, h), strokeWidth = 3.dp.toPx())

        // Top
        val bx = w * ballX.value
        val by = h * ballY.value
        drawCircle(color = Color.White, radius = 4.5.dp.toPx(), center = Offset(bx, by))
      }

      if (isFinished) {
        Surface(
          color = Color(0xF0064E3B),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, GoldYellow),
          modifier = Modifier.align(Alignment.Center)
        ) {
          Text(
            text = "🏁 MAÇ SONUCU (BİTTİ)",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "🏓", fontSize = 12.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = liveActionText, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
      }
    }
  }
}
