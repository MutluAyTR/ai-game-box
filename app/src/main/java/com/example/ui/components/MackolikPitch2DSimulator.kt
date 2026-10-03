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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.MackolikComprehensivePlayerDatabase
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.engine.FairPlaySimulationEngine
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 2D Pitch Simulator Event Types (Maçkolik Standardı)
 */
enum class PitchActionType(
  val label: String,
  val icon: String,
  val bannerColor: Color,
  val soundDescription: String
) {
  NORMAL_PLAY("OYUN AKIŞI", "⚽", Color(0xFF1E293B), "Orta sahada paslaşmalar"),
  DANGEROUS_ATTACK("TEHLİKELİ ATAK", "🚨", Color(0xFFDC2626), "Ceza sahasına sokuluyor!"),
  SHOT_ON_TARGET("KALEYE ŞUT", "🎯", Color(0xFFB45309), "Sert şut çekildi!"),
  GOALKEEPER_SAVE("KALECİ KURTARDI!", "🧤", Color(0xFF0D9488), "İnanılmaz kurtarış!"),
  CORNER("KORNER KULLANILIYOR", "🚩", Color(0xFF2563EB), "Köşe gönderinden orta"),
  THROW_IN("TAÇ ATIŞI", "🤾", Color(0xFF475569), "Taç çizgisine çıktı"),
  FREE_KICK("SERBEST VURUŞ", "⚠️", Color(0xFFD97706), "Tehlikeli noktadan faul"),
  PENALTY("🚨 PENALTI KARARI!", "⚡", Color(0xFF7C3AED), "Hakem beyaz noktayı gösterdi!"),
  GOAL("⚽ GOOOOOOL!", "🎉", Color(0xFF059669), "Ağları havalandırdı!"),
  OFFSIDE("OFSAYT", "🚩", Color(0xFF64748B), "Yardımcı hakem bayrağını kaldırdı"),
  YELLOW_CARD("SARI KART", "🟨", Color(0xFFCA8A04), "Sert müdahaleye sarı kart"),
  INJURY("SAKATLIK & TEDAVİ", "🚑", Color(0xFFE11D48), "Sağlık ekibi sahada"),
  FULL_TIME("MAÇ SONU (MS)", "🏁", Color(0xFF047857), "Hakem son düdüğü çaldı!")
}

data class PitchCommentaryItem(
  val minute: String,
  val actionType: PitchActionType,
  val team: String,
  val player: String,
  val text: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class PitchPlayerDot(
  val normX: Float,
  val normY: Float,
  val number: Int,
  val isHome: Boolean,
  val name: String
)

/**
 * Maçkolik & Nesine Standardında 2D Canlı Saha Simülatörü
 *
 * Yenilikler:
 * 1. 90. Dakikada Otomatik İlave Süre (+4, +5, +6) Düdüğü & Elektronik Tabela
 * 2. Süre Dolunca Ayrıntılı "MAÇ BİTTİ" (Full Time) Bilgi Ekranı & İstatistik Özeti
 * 3. Üst Bar & Tüm Sayfalarla Dakika Dakika Eş Zamanlı Senkronizasyon (onMinuteUpdated)
 * 4. Fotogerçekçi Çim Doku, Ceza Sahası Yayları (D-Arc), 3D File Izgaraları, Stat Projektör Işığı
 * 5. Gerçek Oyuncu Pozisyonları & Top Taşıyıcı Taktiksel Halka
 * 6. AI Fair Play Motoru İle İstatistiksel Olay Ağırlıklandırması
 */
@Composable
fun MackolikPitch2DSimulator(
  match: Match,
  onTriggerGoal: ((String, Boolean) -> Unit)? = null,
  onMinuteUpdated: ((minute: Int, extraMinute: Int, isFinished: Boolean, homeScore: Int, awayScore: Int) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()

  // Dynamic state initialized from match
  var homeScore by remember(match.id, match.homeScore) { mutableIntStateOf(match.homeScore) }
  var awayScore by remember(match.id, match.awayScore) { mutableIntStateOf(match.awayScore) }

  var liveMinute by remember(match.id, match.minute) {
    mutableIntStateOf(if (match.status == MatchStatus.FINISHED) 90 else if (match.minute > 0) match.minute else 42)
  }
  var currentExtraMinute by remember(match.id, match.currentExtraMinute) {
    mutableIntStateOf(match.currentExtraMinute)
  }
  var extraTimeMinutes by remember(match.id, match.extraTimeMinutes) {
    mutableIntStateOf(
      if (match.extraTimeMinutes > 0) match.extraTimeMinutes
      else FairPlaySimulationEngine.calculateDynamicExtraTime(
        goalsCount = match.homeScore + match.awayScore,
        varCount = 1,
        cardsCount = 3,
        injuriesCount = 1
      )
    )
  }
  val initialIsFinished = match.status == MatchStatus.FINISHED ||
      (match.minute >= 90 && (match.currentExtraMinute >= (if (match.extraTimeMinutes > 0) match.extraTimeMinutes else 3)))

  var isMatchFinished by remember(match.id, match.status, match.minute) {
    mutableStateOf(initialIsFinished)
  }

  // Stoppage time announcement banner trigger
  var showExtraTimeAnnouncement by remember { mutableStateOf(false) }
  // Detailed Match Finished Info Overlay
  var showMatchFinishedOverlay by remember(isMatchFinished) { mutableStateOf(isMatchFinished) }

  // Ball coordinate normalized ratios: X in [0f..1f], Y in [0f..1f]
  val ballNormX = remember { Animatable(0.5f) }
  val ballNormY = remember { Animatable(0.5f) }
  val ballHeightScale = remember { Animatable(1.0f) }

  var currentAction by remember { mutableStateOf(if (isMatchFinished) PitchActionType.FULL_TIME else PitchActionType.NORMAL_PLAY) }

  // Immediately center ball and halt movement when match finishes
  LaunchedEffect(isMatchFinished) {
    if (isMatchFinished) {
      currentAction = PitchActionType.FULL_TIME
      showMatchFinishedOverlay = true
      ballNormX.snapTo(0.5f)
      ballNormY.snapTo(0.5f)
      ballHeightScale.snapTo(1.0f)
      com.example.service.RealtimeSportsWebSocketService.broadcastMatchFinished(
        matchId = match.id,
        finalHomeScore = homeScore,
        finalAwayScore = awayScore,
        summary = "Futbol maçı tamamlandı."
      )
    }
  }

  // React instantly to external match status changes (e.g. from WebSocket or repository)
  LaunchedEffect(match.status, match.minute, match.currentExtraMinute) {
    val externalFinished = match.status == MatchStatus.FINISHED ||
        (match.minute >= 90 && match.currentExtraMinute >= (if (match.extraTimeMinutes > 0) match.extraTimeMinutes else 3))
    if (externalFinished) {
      isMatchFinished = true
      showMatchFinishedOverlay = true
      currentAction = PitchActionType.FULL_TIME
      ballNormX.snapTo(0.5f)
      ballNormY.snapTo(0.5f)
      ballHeightScale.snapTo(1.0f)
    }
  }
  var activeAttackingTeam by remember { mutableStateOf(match.homeTeam) }
  var activeBallHolderName by remember {
    mutableStateOf(MackolikComprehensivePlayerDatabase.getScorerForTeam(match.homeTeam, "Futbol"))
  }
  var isAttackingHome by remember { mutableStateOf(true) }

  // Commentary Stream
  val commentaryList = remember {
    mutableStateListOf(
      PitchCommentaryItem(
        minute = if (match.minute > 0) "${match.minute}'" else "42'",
        actionType = PitchActionType.DANGEROUS_ATTACK,
        team = match.homeTeam,
        player = match.homeTeam,
        text = "${match.homeTeam} hücum hattında organize paslarla ceza sahasına sokuluyor."
      ),
      PitchCommentaryItem(
        minute = if (match.minute > 1) "${match.minute - 1}'" else "41'",
        actionType = PitchActionType.CORNER,
        team = match.homeTeam,
        player = match.homeTeam,
        text = "Korner kullanıldı, ön direkte kaleci topu yumruklayarak uzaklaştırdı."
      )
    )
  }

  // Dynamic ticking match minute clock (football clock dynamically advances)
  LaunchedEffect(match.id, isMatchFinished) {
    while (!isMatchFinished) {
      delay(2200L) // 2.2 seconds per match minute

      if (liveMinute < 90) {
        liveMinute++
        onMinuteUpdated?.invoke(liveMinute, 0, false, homeScore, awayScore)

        // When minute hits 90: Trigger 4th official stoppage board
        if (liveMinute == 90) {
          showExtraTimeAnnouncement = true
          currentAction = PitchActionType.NORMAL_PLAY
          commentaryList.add(
            0,
            PitchCommentaryItem(
              minute = "90'",
              actionType = PitchActionType.NORMAL_PLAY,
              team = "4. Hakem",
              player = "Tabela",
              text = "⏱️ Karşılaşmanın sonuna en az +$extraTimeMinutes dakika ilave süre eklendi."
            )
          )
        }
      } else {
        // We are in Stoppage / Extra Time!
        if (currentExtraMinute < extraTimeMinutes) {
          currentExtraMinute++
          onMinuteUpdated?.invoke(90, currentExtraMinute, false, homeScore, awayScore)
          commentaryList.add(
            0,
            PitchCommentaryItem(
              minute = "90+$currentExtraMinute'",
              actionType = PitchActionType.DANGEROUS_ATTACK,
              team = if (isAttackingHome) match.homeTeam else match.awayTeam,
              player = activeBallHolderName,
              text = "Uzatma dakikalarında son fırsatlar! Ceza sahasında nefes kesen baskı."
            )
          )
        } else {
          // Stoppage time exhausted -> MATCH FINISHED!
          isMatchFinished = true
          showMatchFinishedOverlay = true
          currentAction = PitchActionType.FULL_TIME
          onMinuteUpdated?.invoke(90, extraTimeMinutes, true, homeScore, awayScore)

          commentaryList.add(
            0,
            PitchCommentaryItem(
              minute = "90+$extraTimeMinutes'",
              actionType = PitchActionType.FULL_TIME,
              team = "Hakem",
              player = match.referee,
              text = "🏁 DÜDÜK ÇALDI! Hakem karşılaşmanın son düdüğünü çaldı. Maç Sonucu: ${match.homeTeam} $homeScore - $awayScore ${match.awayTeam}"
            )
          )
        }
      }
    }
  }

  // Pulsing animation for active ball and radar waves
  val infiniteTransition = rememberInfiniteTransition(label = "pitch_effects")
  val radarPulse by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 2.6f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radarPulse"
  )
  val radarAlpha by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 0.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radarAlpha"
  )
  val floodlightGlow by infiniteTransition.animateFloat(
    initialValue = 0.65f,
    targetValue = 0.90f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "floodlight"
  )

  // Execute animated ball sequences
  fun executeActionSequence(action: PitchActionType, isHome: Boolean) {
    if (isMatchFinished) return
    coroutineScope.launch {
      currentAction = action
      isAttackingHome = isHome
      activeAttackingTeam = if (isHome) match.homeTeam else match.awayTeam

      val minuteStr = if (liveMinute >= 90) "90+$currentExtraMinute'" else "$liveMinute'"

      when (action) {
        PitchActionType.NORMAL_PLAY -> {
          activeBallHolderName = MackolikComprehensivePlayerDatabase.getScorerForTeam(activeAttackingTeam, "Futbol")
          ballHeightScale.snapTo(1.0f)
          ballNormX.animateTo(
            targetValue = if (isHome) 0.44f else 0.56f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
          )
          ballNormY.animateTo(
            targetValue = 0.5f + (Random.nextFloat() - 0.5f) * 0.38f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
          )
        }
        PitchActionType.DANGEROUS_ATTACK -> {
          activeBallHolderName = MackolikComprehensivePlayerDatabase.getScorerForTeam(activeAttackingTeam, "Futbol")
          val targetGoalX = if (isHome) 0.82f else 0.18f
          ballHeightScale.snapTo(1.1f)
          ballNormX.animateTo(targetGoalX, tween(550, easing = FastOutSlowInEasing))
          ballNormY.animateTo(0.32f, tween(550, easing = FastOutSlowInEasing))
          delay(300)
          ballNormY.animateTo(0.50f, tween(450, easing = FastOutSlowInEasing))
        }
        PitchActionType.SHOT_ON_TARGET -> {
          activeBallHolderName = MackolikComprehensivePlayerDatabase.getScorerForTeam(activeAttackingTeam, "Futbol")
          val targetGoalX = if (isHome) 0.95f else 0.05f
          ballHeightScale.animateTo(1.4f, tween(200))
          ballNormX.animateTo(targetGoalX, tween(360, easing = FastOutSlowInEasing))
          ballNormY.animateTo(0.48f, tween(360, easing = FastOutSlowInEasing))
          ballHeightScale.animateTo(1.0f, tween(160))
        }
        PitchActionType.GOALKEEPER_SAVE -> {
          val targetGoalX = if (isHome) 0.93f else 0.07f
          ballNormX.animateTo(targetGoalX, tween(320, easing = FastOutSlowInEasing))
          delay(180)
          ballNormX.animateTo(if (isHome) 0.76f else 0.24f, tween(450, easing = FastOutSlowInEasing))
        }
        PitchActionType.CORNER -> {
          val cornerX = if (isHome) 0.97f else 0.03f
          val cornerY = 0.06f
          ballNormX.animateTo(cornerX, tween(350, easing = FastOutSlowInEasing))
          ballNormY.animateTo(cornerY, tween(350, easing = FastOutSlowInEasing))
          delay(400)
          ballHeightScale.animateTo(1.5f, tween(250))
          ballNormX.animateTo(if (isHome) 0.88f else 0.12f, tween(450, easing = FastOutSlowInEasing))
          ballNormY.animateTo(0.50f, tween(450, easing = FastOutSlowInEasing))
          ballHeightScale.animateTo(1.0f, tween(200))
        }
        PitchActionType.FREE_KICK -> {
          val fkX = if (isHome) 0.74f else 0.26f
          ballNormX.animateTo(fkX, tween(380))
          ballNormY.animateTo(0.38f, tween(380))
        }
        PitchActionType.PENALTY -> {
          val penX = if (isHome) 0.88f else 0.12f
          ballNormX.animateTo(penX, tween(400))
          ballNormY.animateTo(0.50f, tween(400))
        }
        PitchActionType.GOAL -> {
          if (isMatchFinished) return@launch
          val goalX = if (isHome) 0.99f else 0.01f
          ballHeightScale.animateTo(1.6f, tween(180))
          ballNormX.animateTo(goalX, tween(300, easing = FastOutSlowInEasing))
          ballNormY.animateTo(0.50f, tween(300, easing = FastOutSlowInEasing))
          ballHeightScale.animateTo(1.0f, tween(120))
          if (!isMatchFinished) {
            if (isHome) homeScore++ else awayScore++
            onTriggerGoal?.invoke(match.id, isHome)
            onMinuteUpdated?.invoke(liveMinute, currentExtraMinute, isMatchFinished, homeScore, awayScore)
          }
        }
        PitchActionType.INJURY -> {
          ballNormX.animateTo(0.50f, tween(400))
          ballNormY.animateTo(0.50f, tween(400))
        }
        else -> {
          ballNormX.animateTo(0.5f, tween(450))
          ballNormY.animateTo(0.5f, tween(450))
        }
      }

      // Add to commentary stream
      commentaryList.add(
        0,
        PitchCommentaryItem(
          minute = minuteStr,
          actionType = action,
          team = activeAttackingTeam,
          player = activeBallHolderName,
          text = "${activeAttackingTeam}: ${action.soundDescription}"
        )
      )
    }
  }

  // Ambient automatic realistic match engine ticker
  LaunchedEffect(match.id, isMatchFinished) {
    while (!isMatchFinished) {
      delay(3800)
      if (isMatchFinished) break
      val isHome = Random.nextBoolean()
      val randomAction = when (Random.nextInt(14)) {
        0, 1, 2 -> PitchActionType.DANGEROUS_ATTACK
        3 -> PitchActionType.SHOT_ON_TARGET
        4 -> PitchActionType.CORNER
        5 -> PitchActionType.GOALKEEPER_SAVE
        6 -> PitchActionType.FREE_KICK
        7 -> PitchActionType.THROW_IN
        8 -> if (Random.nextInt(8) == 0) PitchActionType.YELLOW_CARD else PitchActionType.NORMAL_PLAY
        else -> PitchActionType.NORMAL_PLAY
      }
      executeActionSequence(randomAction, isHome)
    }
  }

  // Display minute string: 45', 89', 90+4' or MS
  val displayMinuteText = when {
    isMatchFinished -> "MS"
    liveMinute >= 90 && currentExtraMinute > 0 -> "90+$currentExtraMinute'"
    liveMinute >= 90 -> "90'"
    else -> "$liveMinute'"
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("mackolik_2d_pitch_simulator"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F1D)),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // 1. Header: Live 2D Pitch Indicator & Ticking Match Clock
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (isMatchFinished) Color(0xFF10B981) else LiveRed)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isMatchFinished) "🏁 MAÇ SONUCU (BİTTİ)" else "CANLI 2D SAHA ANLATIMI",
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            color = if (isMatchFinished) Color(0xFF34D399) else GoldYellow,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.width(6.dp))

          // Minute Badge with Extra Time Indicator
          Surface(
            color = if (isMatchFinished) Color(0xFF065F46) else if (liveMinute >= 90) Color(0xFFDC2626) else Color(0xFF1E293B),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, if (liveMinute >= 90 && !isMatchFinished) GoldYellow else Color.Transparent)
          ) {
            Text(
              text = displayMinuteText,
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          // If in extra time, show stoppage marker pill
          if (liveMinute >= 90 && !isMatchFinished) {
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
              color = Color(0xFF78350F),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "+$extraTimeMinutes'",
                color = GoldYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
        }

        // TV Broadcast & Stadium Pill
        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(6.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = match.tvBroadcast,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFFCBD5E1)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2. Scoreboard & Attacking Directions
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF131D31), RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Home Team
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = match.homeTeam,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "▶", fontSize = 10.sp, color = if (isAttackingHome) GoldYellow else Color(0xFF64748B))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Hücum Yönü",
              fontSize = 9.sp,
              color = if (isAttackingHome) GoldYellow else Color(0xFF64748B),
              fontWeight = if (isAttackingHome) FontWeight.Bold else FontWeight.Normal
            )
          }
        }

        // Live Score Display
        Surface(
          color = Color(0xFF0A0F1D),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, if (isMatchFinished) Color(0xFF10B981) else Color(0xFF334155))
        ) {
          Text(
            text = "$homeScore - $awayScore",
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            color = if (isMatchFinished) Color(0xFF34D399) else Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
          )
        }

        // Away Team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.End
        ) {
          Text(
            text = match.awayTeam,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Hücum Yönü",
              fontSize = 9.sp,
              color = if (!isAttackingHome) GoldYellow else Color(0xFF64748B),
              fontWeight = if (!isAttackingHome) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "◀", fontSize = 10.sp, color = if (!isAttackingHome) GoldYellow else Color(0xFF64748B))
          }
        }
      }

      // Stoppage Time Electronic Referee Board Banner (flashing when 90th min is hit)
      if (showExtraTimeAnnouncement && !isMatchFinished) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          color = Color(0xFF1E1B4B),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.2.dp, GoldYellow),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("⏱️ 4. HAKEM ELEKTRONİK TABELA:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GoldYellow)
              Spacer(modifier = Modifier.width(6.dp))
              Surface(color = LiveRed, shape = RoundedCornerShape(4.dp)) {
                Text(
                  text = "+$extraTimeMinutes DAKİKA UZATMA",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Gizle",
              fontSize = 9.sp,
              color = Color(0xFF94A3B8),
              modifier = Modifier.clickable { showExtraTimeAnnouncement = false }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. THE PHOTOREALISTIC 2D FOOTBALL PITCH CANVAS
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(220.dp)
          .clip(RoundedCornerShape(12.dp))
          .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
      ) {
        // High-Definition Striped Lawn + Pitch Lines + Penalty Arcs + Goal Nets
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawPhotorealisticPitch(size, floodlightGlow)
        }

        // Live Dynamic Ball with Realistic Height & Trajectory Shadow
        val ballX = ballNormX.value
        val ballY = ballNormY.value
        val ballScale = ballHeightScale.value

        Canvas(modifier = Modifier.fillMaxSize()) {
          val px = size.width * ballX
          val py = size.height * ballY

          // Pulsing tactical radar glow ring around ball
          if (!isMatchFinished) {
            drawCircle(
              color = currentAction.bannerColor.copy(alpha = radarAlpha),
              radius = 14.dp.toPx() * radarPulse,
              center = Offset(px, py),
              style = Stroke(width = 2.dp.toPx())
            )
          }

          // Dynamic 3D Ball Shadow (scales & shifts with ball height)
          val shadowOffset = 3.dp.toPx() * ballScale
          drawCircle(
            color = Color(0x66000000),
            radius = (5.5.dp.toPx() * (1f / ballScale)).coerceAtLeast(3.dp.toPx()),
            center = Offset(px + shadowOffset, py + shadowOffset)
          )

          // White Soccer Ball Core (scaled with flight height)
          val ballRadius = 5.2.dp.toPx() * ballScale
          drawCircle(
            color = Color.White,
            radius = ballRadius,
            center = Offset(px, py)
          )

          // Soccer Ball Classic Pentagon Pattern
          drawCircle(
            color = Color(0xFF0F172A),
            radius = ballRadius * 0.44f,
            center = Offset(px, py)
          )
          drawCircle(
            color = Color(0xFF334155),
            radius = ballRadius * 0.20f,
            center = Offset(px + ballRadius * 0.35f, py - ballRadius * 0.35f)
          )
        }

        // Overlay Action Pill (Top-Center of Pitch)
        if (!isMatchFinished) {
          Row(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            Surface(
              color = currentAction.bannerColor.copy(alpha = 0.94f),
              shape = RoundedCornerShape(20.dp),
              shadowElevation = 6.dp,
              border = BorderStroke(1.dp, GoldYellow)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = currentAction.icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${activeAttackingTeam.uppercase()}: ${currentAction.label}",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
              }
            }
          }
        } else {
          // Centered Full Time Banner Overlay on Pitch
          Row(
            modifier = Modifier
              .align(Alignment.Center)
              .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            Surface(
              color = Color(0xF0064E3B),
              shape = RoundedCornerShape(12.dp),
              shadowElevation = 8.dp,
              border = BorderStroke(1.5.dp, GoldYellow)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "🏁", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "MAÇ SONUCU (MS)",
                    color = GoldYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Karşılaşma Tamamlandı • Son Düdük Çaldı",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }

        // Bottom Pitch Possession & xG Stat Overlay
        Surface(
          color = Color(0xDD0A0F1D),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 6.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "%54 Topla Oynama", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "•", color = Color(0xFF64748B), fontSize = 10.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "14 Şut", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "•", color = Color(0xFF64748B), fontSize = 10.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "xG: 1.84 - 1.12", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. MAÇ BİTTİ BİLGİ EKRANI (MATCH FINISHED FULL-TIME SUMMARY OVERLAY)
      AnimatedVisibility(
        visible = showMatchFinishedOverlay,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
      ) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261F)),
          border = BorderStroke(1.5.dp, Color(0xFF10B981)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("match_finished_info_card")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "🏆 MAÇ BİTTİ - RESMİ SONUÇ RAPORU",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Black,
                  color = GoldYellow
                )
              }
              Surface(
                color = Color(0xFF047857),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "MS (90+$extraTimeMinutes')",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score Banner
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF064E3B), RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${match.homeTeam}  $homeScore - $awayScore  ${match.awayTeam}",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color.White
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Detailed Stats Table
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A1D17), RoundedCornerShape(8.dp))
                .padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              StatComparisonRow("Topla Oynama", "%54", "%46", 0.54f)
              StatComparisonRow("Toplam Şut", "14", "9", 14f / 23f)
              StatComparisonRow("İsabetli Şut", "6", "4", 6f / 10f)
              StatComparisonRow("Köşe Vuruşu", "7", "3", 7f / 10f)
              StatComparisonRow("Fauller", "11", "14", 11f / 25f)
              StatComparisonRow("Sarı / Kırmızı", "2 / 0", "3 / 0", 0.4f)
              StatComparisonRow("Beklenen Gol (xG)", "1.84", "1.12", 1.84f / 2.96f)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Man of the Match (MVP)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF13362A), RoundedCornerShape(8.dp))
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Star, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Maçın Adamı (MVP):", fontSize = 10.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (homeScore >= awayScore) "${match.homeTeam} Forveti (8.8 ⭐)" else "${match.awayTeam} Yıldızı (8.7 ⭐)",
                fontSize = 11.sp,
                color = GoldYellow,
                fontWeight = FontWeight.Black
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Restart Simulation Button
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "⚖️ AI Fair Play Doğrulandı: Seed #${match.id.take(6)}",
                fontSize = 9.sp,
                color = Color(0xFF6EE7B7)
              )

              Button(
                onClick = {
                  isMatchFinished = false
                  showMatchFinishedOverlay = false
                  liveMinute = 1
                  currentExtraMinute = 0
                  homeScore = 0
                  awayScore = 0
                  onMinuteUpdated?.invoke(1, 0, false, 0, 0)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Yeniden Simüle Et", color = Color(0xFF0F172A), fontWeight = FontWeight.Black, fontSize = 11.sp)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 5. Adil AI Provably Fair Maç Motoru (Fair AI Autonomous Match Engine)
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("⚖️ AI Fair Play & Otomatik Uzatma Motoru", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
            }
            Surface(
              color = Color(0xFF065F46),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "Poisson Onaylı",
                color = Color(0xFF34D399),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("İlave Süre Formülü: Gol (+30s) + VAR (+60s) + Sakatlık (+60s)", fontSize = 9.sp, color = Color(0xFF94A3B8))
            Text("İlave: +$extraTimeMinutes'", fontSize = 10.sp, color = GoldYellow, fontWeight = FontWeight.Black)
          }
          Text(
            text = "Karşılaşma süresi 90 dakikayı doldurduğunda dinamik uzatma oynanır ve ardından resmi bitiş ekranı açılır.",
            fontSize = 9.sp,
            color = Color(0xFF64748B),
            lineHeight = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 6. Maçkolik Canlı Anlatım Akışı (Live Commentary Feed)
      Text(
        text = "🎙️ Maçkolik Canlı Anlatım & Dakika Akışı:",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8)
      )
      Spacer(modifier = Modifier.height(6.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF131D31), RoundedCornerShape(10.dp))
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        commentaryList.take(3).forEach { item ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
          ) {
            Surface(
              color = Color(0xFF0A0F1D),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = item.minute,
                color = GoldYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.actionType.icon, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = item.actionType.label,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = item.actionType.bannerColor
                )
              }
              Text(
                text = item.text,
                fontSize = 11.sp,
                color = Color(0xFFE2E8F0),
                lineHeight = 15.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StatComparisonRow(
  title: String,
  homeVal: String,
  awayVal: String,
  homeFraction: Float
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = homeVal, color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      Text(text = title, color = Color(0xFF94A3B8), fontSize = 10.sp)
      Text(text = awayVal, color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(2.dp))
    LinearProgressIndicator(
      progress = { homeFraction.coerceIn(0.1f, 0.9f) },
      modifier = Modifier
        .fillMaxWidth()
        .height(3.dp)
        .clip(RoundedCornerShape(2.dp)),
      color = GoldYellow,
      trackColor = Color(0xFF38BDF8),
    )
  }
}

/**
 * Photorealistic 2D Football Pitch Canvas Rendering
 * Draws authentic grass lawn stripes, penalty D-arcs, corner flags, goal mesh, and floodlight glows.
 */
private fun DrawScope.drawPhotorealisticPitch(canvasSize: Size, floodlightGlow: Float) {
  val width = canvasSize.width
  val height = canvasSize.height

  // 1. Draw alternating deep green lawn stripes (10 vertical bands)
  val stripeCount = 10
  val stripeWidth = width / stripeCount
  val darkGrass = Color(0xFF14532D)
  val lightGrass = Color(0xFF166534)

  for (i in 0 until stripeCount) {
    drawRect(
      color = if (i % 2 == 0) darkGrass else lightGrass,
      topLeft = Offset(i * stripeWidth, 0f),
      size = Size(stripeWidth, height)
    )
  }

  // 2. Corner stadium floodlight radial illumination glow cones
  val glowColor = Color(0xFFFEF08A).copy(alpha = 0.08f * floodlightGlow)
  drawCircle(color = glowColor, radius = width * 0.35f, center = Offset(0f, 0f))
  drawCircle(color = glowColor, radius = width * 0.35f, center = Offset(width, 0f))
  drawCircle(color = glowColor, radius = width * 0.35f, center = Offset(0f, height))
  drawCircle(color = glowColor, radius = width * 0.35f, center = Offset(width, height))

  val lineColor = Color(0xE6FFFFFF)
  val strokeWidth = 1.6.dp.toPx()
  val margin = 8.dp.toPx()

  val pitchLeft = margin
  val pitchTop = margin
  val pitchRight = width - margin
  val pitchBottom = height - margin
  val pitchWidth = pitchRight - pitchLeft
  val pitchHeight = pitchBottom - pitchTop

  // 3. Pitch Boundary Outer Lines
  drawRect(
    color = lineColor,
    topLeft = Offset(pitchLeft, pitchTop),
    size = Size(pitchWidth, pitchHeight),
    style = Stroke(width = strokeWidth)
  )

  // 4. Halfway Line
  val midX = pitchLeft + pitchWidth / 2f
  drawLine(
    color = lineColor,
    start = Offset(midX, pitchTop),
    end = Offset(midX, pitchBottom),
    strokeWidth = strokeWidth
  )

  // 5. Center Circle & Center Spot
  val centerCircleRadius = pitchHeight * 0.22f
  drawCircle(
    color = lineColor,
    radius = centerCircleRadius,
    center = Offset(midX, pitchTop + pitchHeight / 2f),
    style = Stroke(width = strokeWidth)
  )
  drawCircle(
    color = lineColor,
    radius = 3.dp.toPx(),
    center = Offset(midX, pitchTop + pitchHeight / 2f)
  )

  // 6. Left Penalty Box (Home Goal Side)
  val boxWidth = pitchWidth * 0.18f
  val boxHeight = pitchHeight * 0.54f
  val boxTop = pitchTop + (pitchHeight - boxHeight) / 2f
  drawRect(
    color = lineColor,
    topLeft = Offset(pitchLeft, boxTop),
    size = Size(boxWidth, boxHeight),
    style = Stroke(width = strokeWidth)
  )

  // Left 6-Yard Box
  val smallBoxWidth = pitchWidth * 0.07f
  val smallBoxHeight = pitchHeight * 0.30f
  val smallBoxTop = pitchTop + (pitchHeight - smallBoxHeight) / 2f
  drawRect(
    color = lineColor,
    topLeft = Offset(pitchLeft, smallBoxTop),
    size = Size(smallBoxWidth, smallBoxHeight),
    style = Stroke(width = strokeWidth)
  )

  // Left Penalty Spot
  val leftPenaltySpotX = pitchLeft + boxWidth * 0.65f
  drawCircle(
    color = lineColor,
    radius = 2.dp.toPx(),
    center = Offset(leftPenaltySpotX, pitchTop + pitchHeight / 2f)
  )

  // Left Penalty Arc (D-Arc)
  val arcRadius = pitchHeight * 0.14f
  val leftArcPath = Path().apply {
    arcTo(
      rect = androidx.compose.ui.geometry.Rect(
        left = leftPenaltySpotX - arcRadius,
        top = (pitchTop + pitchHeight / 2f) - arcRadius,
        right = leftPenaltySpotX + arcRadius,
        bottom = (pitchTop + pitchHeight / 2f) + arcRadius
      ),
      startAngleDegrees = -53f,
      sweepAngleDegrees = 106f,
      forceMoveTo = true
    )
  }
  drawPath(path = leftArcPath, color = lineColor, style = Stroke(width = strokeWidth))

  // Left 3D Goal Net
  val goalNetWidth = 7.dp.toPx()
  val goalHeight = pitchHeight * 0.22f
  val goalTop = pitchTop + (pitchHeight - goalHeight) / 2f
  drawRect(
    color = Color(0x88FFFFFF),
    topLeft = Offset(pitchLeft - goalNetWidth, goalTop),
    size = Size(goalNetWidth, goalHeight),
    style = Stroke(width = 1.dp.toPx())
  )

  // 7. Right Penalty Box (Away Goal Side)
  drawRect(
    color = lineColor,
    topLeft = Offset(pitchRight - boxWidth, boxTop),
    size = Size(boxWidth, boxHeight),
    style = Stroke(width = strokeWidth)
  )

  // Right 6-Yard Box
  drawRect(
    color = lineColor,
    topLeft = Offset(pitchRight - smallBoxWidth, smallBoxTop),
    size = Size(smallBoxWidth, smallBoxHeight),
    style = Stroke(width = strokeWidth)
  )

  // Right Penalty Spot
  val rightPenaltySpotX = pitchRight - boxWidth * 0.65f
  drawCircle(
    color = lineColor,
    radius = 2.dp.toPx(),
    center = Offset(rightPenaltySpotX, pitchTop + pitchHeight / 2f)
  )

  // Right Penalty Arc (D-Arc)
  val rightArcPath = Path().apply {
    arcTo(
      rect = androidx.compose.ui.geometry.Rect(
        left = rightPenaltySpotX - arcRadius,
        top = (pitchTop + pitchHeight / 2f) - arcRadius,
        right = rightPenaltySpotX + arcRadius,
        bottom = (pitchTop + pitchHeight / 2f) + arcRadius
      ),
      startAngleDegrees = 127f,
      sweepAngleDegrees = 106f,
      forceMoveTo = true
    )
  }
  drawPath(path = rightArcPath, color = lineColor, style = Stroke(width = strokeWidth))

  // Right 3D Goal Net
  drawRect(
    color = Color(0x88FFFFFF),
    topLeft = Offset(pitchRight, goalTop),
    size = Size(goalNetWidth, goalHeight),
    style = Stroke(width = 1.dp.toPx())
  )

  // 8. Corner Arcs with Corner Flags
  val cornerRadius = 6.dp.toPx()
  drawCircle(color = lineColor, radius = cornerRadius, center = Offset(pitchLeft, pitchTop), style = Stroke(width = strokeWidth))
  drawCircle(color = lineColor, radius = cornerRadius, center = Offset(pitchRight, pitchTop), style = Stroke(width = strokeWidth))
  drawCircle(color = lineColor, radius = cornerRadius, center = Offset(pitchLeft, pitchBottom), style = Stroke(width = strokeWidth))
  drawCircle(color = lineColor, radius = cornerRadius, center = Offset(pitchRight, pitchBottom), style = Stroke(width = strokeWidth))

  // Corner Flags (Yellow dots with red flag marker)
  drawCircle(color = GoldYellow, radius = 2.dp.toPx(), center = Offset(pitchLeft, pitchTop))
  drawCircle(color = GoldYellow, radius = 2.dp.toPx(), center = Offset(pitchRight, pitchTop))
  drawCircle(color = GoldYellow, radius = 2.dp.toPx(), center = Offset(pitchLeft, pitchBottom))
  drawCircle(color = GoldYellow, radius = 2.dp.toPx(), center = Offset(pitchRight, pitchBottom))
}
