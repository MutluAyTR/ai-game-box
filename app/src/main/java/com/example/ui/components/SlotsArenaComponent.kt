package com.example.ui.components

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.SlotGameMetadata
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.SlotAudioHapticsEngine
import com.example.util.formatTp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

/**
 * Dedicated 'Slots Arena' UI Component:
 * - Dynamic LazyVerticalGrid showcase with high-fidelity visual cards
 * - Real 2D/3D animated slot engine tailored to each game (Gates of Olympus 1000, Sweet Bonanza 1000, Big Bass)
 * - Auto-detects device screen dimensions to guarantee zero bottom cut-off
 * - Animated Zeus ("Dede") hovering & striking dynamic lightning bolts across the grid
 * - Animated sexy casino dancer companion ("Bella") dancing, blowing kisses and cheering in Turkish
 * - Integrated real-time audio synthesizer & hardware vibration haptic feedback
 */
@Composable
fun SlotsArenaComponent(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit,
  onActiveGameChanged: ((Boolean) -> Unit)? = null,
  onClose: (() -> Unit)? = null
) {
  val context = LocalContext.current
  LaunchedEffect(Unit) {
    SlotAudioHapticsEngine.init(context)
  }

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Tümü") }
  var activeSlotGame by remember { mutableStateOf<SlotGameMetadata?>(null) }

  LaunchedEffect(activeSlotGame) {
    onActiveGameChanged?.invoke(activeSlotGame != null)
  }

  val slotGames = remember {
    listOf(
      SlotGameMetadata(
        id = "gates_of_olympus_1000",
        title = "Gates of Olympus 1000",
        provider = "Pragmatic Play",
        iconEmoji = "👑",
        rtp = "%96.50",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "15.000x",
        category = "Pragmatic",
        isHot = true,
        bannerGradient = listOf(Color(0xFF78350F), Color(0xFF451A03), Color(0xFF1E1B4B))
      ),
      SlotGameMetadata(
        id = "gates_of_olympus",
        title = "Gates of Olympus",
        provider = "Pragmatic Play",
        iconEmoji = "⚡",
        rtp = "%96.50",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "5.000x",
        category = "Pragmatic",
        isHot = true,
        bannerGradient = listOf(Color(0xFF854D0E), Color(0xFF1E1B4B))
      ),
      SlotGameMetadata(
        id = "sweet_bonanza_1000",
        title = "Sweet Bonanza 1000",
        provider = "Pragmatic Play",
        iconEmoji = "🍬",
        rtp = "%96.53",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "25.000x",
        category = "Meyve & Şeker",
        isHot = true,
        bannerGradient = listOf(Color(0xFFF43F5E), Color(0xFF881337), Color(0xFF4C0519))
      ),
      SlotGameMetadata(
        id = "sweet_bonanza",
        title = "Sweet Bonanza",
        provider = "Pragmatic Play",
        iconEmoji = "🍭",
        rtp = "%96.48",
        volatility = "Yüksek ⚡⚡⚡⚡",
        maxWin = "21.100x",
        category = "Meyve & Şeker",
        isHot = true,
        bannerGradient = listOf(Color(0xFFEC4899), Color(0xFF831843))
      ),
      SlotGameMetadata(
        id = "sugar_rush_1000",
        title = "Sugar Rush 1000",
        provider = "Pragmatic Play",
        iconEmoji = "🐻",
        rtp = "%96.50",
        volatility = "Yüksek ⚡⚡⚡⚡",
        maxWin = "25.000x",
        category = "Meyve & Şeker",
        isHot = true,
        bannerGradient = listOf(Color(0xFF8B5CF6), Color(0xFF4C1D95))
      ),
      SlotGameMetadata(
        id = "big_bass_bonanza",
        title = "Big Bass Bonanza",
        provider = "Pragmatic Play",
        iconEmoji = "🎣",
        rtp = "%96.71",
        volatility = "Yüksek ⚡⚡⚡⚡",
        maxWin = "2.100x",
        category = "Pragmatic",
        isHot = true,
        bannerGradient = listOf(Color(0xFF0284C7), Color(0xFF0C4A6E))
      ),
      SlotGameMetadata(
        id = "big_bass_splash",
        title = "Big Bass Splash",
        provider = "Pragmatic Play",
        iconEmoji = "🐟",
        rtp = "%96.71",
        volatility = "Yüksek ⚡⚡⚡⚡",
        maxWin = "5.000x",
        category = "Pragmatic",
        bannerGradient = listOf(Color(0xFF0D9488), Color(0xFF134E4A))
      ),
      SlotGameMetadata(
        id = "the_dog_house_megaways",
        title = "The Dog House Megaways",
        provider = "Pragmatic Play",
        iconEmoji = "🐶",
        rtp = "%96.55",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "12.305x",
        category = "Megaways",
        bannerGradient = listOf(Color(0xFFB45309), Color(0xFF78350F))
      ),
      SlotGameMetadata(
        id = "starlight_princess_1000",
        title = "Starlight Princess 1000",
        provider = "Pragmatic Play",
        iconEmoji = "✨",
        rtp = "%96.50",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "15.000x",
        category = "Pragmatic",
        bannerGradient = listOf(Color(0xFF9333EA), Color(0xFF581C87))
      ),
      SlotGameMetadata(
        id = "wanted_dead_or_a_wild",
        title = "Wanted Dead or a Wild",
        provider = "Hacksaw Gaming",
        iconEmoji = "🤠",
        rtp = "%96.38",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "12.500x",
        category = "Hacksaw",
        isHot = true,
        bannerGradient = listOf(Color(0xFF3F3F46), Color(0xFF18181B))
      ),
      SlotGameMetadata(
        id = "rip_city",
        title = "RIP City",
        provider = "Hacksaw Gaming",
        iconEmoji = "🐱",
        rtp = "%96.10",
        volatility = "Orta-Yüksek ⚡⚡⚡",
        maxWin = "12.500x",
        category = "Hacksaw",
        bannerGradient = listOf(Color(0xFF52525B), Color(0xFF27272A))
      ),
      SlotGameMetadata(
        id = "klasik_777",
        title = "Sizzling 777 Hot",
        provider = "Novomatic / EGT",
        iconEmoji = "7️⃣",
        rtp = "%95.66",
        volatility = "Orta ⚡⚡⚡",
        maxWin = "1.000x",
        category = "Klasik 777",
        bannerGradient = listOf(Color(0xFFB91C1C), Color(0xFF7F1D1D))
      )
    )
  }

  val categories = listOf("Tümü", "Popüler", "Pragmatic", "Meyve & Şeker", "Megaways", "Hacksaw", "Klasik 777")

  val filteredGames = remember(slotGames, searchQuery, selectedCategory) {
    slotGames.filter { game ->
      val matchesSearch = searchQuery.isBlank() || game.title.contains(searchQuery, ignoreCase = true) || game.provider.contains(searchQuery, ignoreCase = true)
      val matchesCategory = selectedCategory == "Tümü" || game.category == selectedCategory || (selectedCategory == "Popüler" && game.isHot)
      matchesSearch && matchesCategory
    }
  }

  // Full Screen Active Game Presentation (Ensures 100% window size without Dialog clipping)
  if (activeSlotGame != null) {
    AuthenticPragmaticSlotModal(
      game = activeSlotGame!!,
      walletPoints = walletPoints,
      onDeductStake = onDeductStake,
      onAddWinnings = onAddWinnings,
      onDismiss = {
        activeSlotGame = null
        onActiveGameChanged?.invoke(false)
      }
    )
  } else {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF070B19))
        .padding(12.dp)
        .testTag("slots_arena_component")
    ) {
      // 1. Slots Arena Header Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A36)),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF1E1B4B), Color(0xFF2E1065), Color(0xFF0F172A))
              )
            )
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = GoldYellow, shape = RoundedCornerShape(4.dp)) {
                  Text(
                    text = "ROVBET SLOTS ARENA",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "👑 Gates of Olympus 1000", color = Color(0xFFC7D2FE), fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "2D & 3D Pragmatic Slot Lobisi",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
              )
              Text(
                text = "Animasyonlu Dansçı Kız, Çakan Zeus Şimşekleri, Sesler & Titreşim Haptics",
                color = Color(0xFFCBD5E1),
                fontSize = 10.sp
              )
            }

            if (onClose != null) {
              IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
              }
            } else {
              Text(text = "👑⚡🍭", fontSize = 28.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Search & Category Filters
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        placeholder = { Text("Slot oyunu veya sağlayıcı ara...", fontSize = 11.sp, color = Color(0xFF64748B)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(18.dp)) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
          }
        },
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF131A36),
          unfocusedContainerColor = Color(0xFF131A36),
          focusedBorderColor = GoldYellow,
          unfocusedBorderColor = Color(0xFF2E3856),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Category Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { cat ->
          val isSel = selectedCategory == cat
          Surface(
            color = if (isSel) GoldYellow else Color(0xFF131A36),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) GoldYellow else Color(0xFF2E3856)),
            modifier = Modifier.clickable { selectedCategory = cat }
          ) {
            Text(
              text = cat,
              color = if (isSel) Color(0xFF0F172A) else Color(0xFFE2E8F0),
              fontSize = 10.sp,
              fontWeight = if (isSel) FontWeight.Black else FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Dynamic Responsive Grid of Slot Machines
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredGames, key = { it.id }) { game ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131A36)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3856)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                SlotAudioHapticsEngine.vibrateSpinClick()
                SlotAudioHapticsEngine.playSpinTick()
                activeSlotGame = game
                onActiveGameChanged?.invoke(true)
              }
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(95.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Brush.verticalGradient(game.bannerGradient)),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = game.iconEmoji, fontSize = 34.sp)
                  if (game.isHot) {
                    Surface(
                      color = Color(0xFFEF4444),
                      shape = RoundedCornerShape(4.dp),
                      modifier = Modifier.padding(top = 2.dp)
                    ) {
                      Text(
                        text = "🔥 POPÜLER",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                }

                Surface(
                  color = Color(0xFF0F766E),
                  shape = RoundedCornerShape(bottomStart = 6.dp),
                  modifier = Modifier.align(Alignment.TopEnd)
                ) {
                  Text(
                    text = "BONUS BUY",
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = game.title,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Text(
                text = game.provider,
                fontSize = 9.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "RTP ${game.rtp}", fontSize = 8.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                Text(text = "Max ${game.maxWin}", fontSize = 8.sp, color = GoldYellow, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Authentic Full-Screen Pragmatic & Rovbet Slot Machine:
 * - Guaranteed Pinned Bottom Console (NO CUT-OFF on any screen aspect ratio or phone format!)
 * - Responsive sizing with BoxWithConstraints & safe scrollable viewport
 * - 6 Columns x 5 Rows (30 positions) matching Gates of Olympus 1000 & Sweet Bonanza 1000
 * - Animated Olympus Zeus ("Dede") with real branching Canvas Lightning Bolt strikes across the grid
 * - Animated glamorous Casino Dancer companion ("Bella") with lively hip sway, kisses, giggle voice & victory dance
 * - Real-time synthesized audio waveforms (spin ticks, reel stop, thunder rumble, win chimes, fanfare)
 * - Hardware vibrator haptics on spin, stop and lightning
 */
@Composable
internal fun AuthenticPragmaticSlotModal(
  game: SlotGameMetadata,
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit,
  onDismiss: () -> Unit
) {
  BackHandler { onDismiss() }

  var selectedBet by remember { mutableStateOf(50L) }
  var isSpinning by remember { mutableStateOf(false) }
  var isTurbo by remember { mutableStateOf(false) }
  var isAutoSpinActive by remember { mutableStateOf(false) }
  var autoSpinsLeft by remember { mutableIntStateOf(0) }
  var lastWinAmount by remember { mutableStateOf(0L) }
  var currentMultiplier by remember { mutableDoubleStateOf(0.0) }
  var celebratoryText by remember { mutableStateOf<String?>(null) }
  var isDoubleChanceActive by remember { mutableStateOf(false) }

  // Dancer and Zeus dynamic states
  var dancerQuote by remember { mutableStateOf("Hadi aşkım, şansını dene! 💋") }
  var showLightningBolt by remember { mutableStateOf(false) }
  var dancerKissHeart by remember { mutableStateOf(false) }
  var isBellaVictoryDancing by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()

  val isOlympus = game.id.contains("gates") || game.id.contains("starlight")
  val isSweet = game.id.contains("sweet") || game.id.contains("sugar") || game.id.contains("fruit")
  val isBass = game.id.contains("bass") || game.id.contains("fish")
  val isDogHouse = game.id.contains("dog")
  val isWestern = game.id.contains("wanted") || game.id.contains("dead") || game.id.contains("rip")

  // Distinct themed authentic symbols for each game
  val symbols = remember(game.id) {
    when {
      isSweet -> listOf(
        "🍭" to "Lolipop Scatter",
        "💖" to "Kırmızı Kalp",
        "💜" to "Mor Şeker",
        "💚" to "Yeşil Şeker",
        "💙" to "Mavi Şeker",
        "🍎" to "Kırmızı Elma",
        "🍇" to "Taze Üzüm",
        "🍉" to "Karpuz",
        "🍌" to "Muz",
        "💣" to "100x Bomba"
      )
      isBass -> listOf(
        "🧔" to "Balıkçı Wild",
        "🐟" to "Büyük Levrek",
        "🎣" to "Olta Kamışı",
        "🪱" to "Şamandıra",
        "📦" to "Olta Çantası",
        "🚤" to "Sürat Teknesi",
        "🌊" to "Mavi Dalga",
        "🔟" to "Altın Para",
        "👑" to "Kral Balık"
      )
      isDogHouse -> listOf(
        "🐶" to "Rottweiler",
        "🐕" to "Pug",
        "🐩" to "Kaniş",
        "🦴" to "Altın Kemik",
        "🍖" to "Lezzetli Et",
        "🐾" to "Pati İzi",
        "🏠" to "Köpek Kulübesi Wild",
        "👑" to "Kral Tasma"
      )
      isWestern -> listOf(
        "🤠" to "Kovboy Wild",
        "🔫" to "Altıpatlar",
        "💰" to "Para Çantası",
        "💀" to "Kurukafa",
        "🥃" to "Viski Şişesi",
        "⭐" to "Şerif Yıldızı",
        "🧨" to "Dinamit Scatter"
      )
      isOlympus -> listOf(
        "👑" to "Altın Taç",
        "⏳" to "Kum Saati",
        "💍" to "Yakut Yüzük",
        "🏆" to "Altın Kadeh",
        "🔴" to "Kırmızı Yakut",
        "🟣" to "Mor Ametist",
        "🟡" to "Sarı Topaz",
        "🟢" to "Yeşil Zümrüt",
        "🔵" to "Mavi Safir",
        "⚡" to "Zeus Scatter"
      )
      else -> listOf(
        "7️⃣" to "Vegas 7",
        "🔔" to "Altın Çan",
        "💎" to "Elmas",
        "🍒" to "Kiraz",
        "BAR" to "Triple Bar",
        "⭐" to "Yıldız Scatter",
        "💰" to "Para Çuvalı"
      )
    }
  }

  // 6 columns x 5 rows = 30 tiles
  val gridSymbols = remember {
    mutableStateListOf(*Array(30) { symbols.random().first })
  }

  val activeOrbs = remember { mutableStateListOf<Int>() }

  // Infinite Transitions for Zeus Hover & Dancer Rhythm
  val infiniteTransition = rememberInfiniteTransition(label = "CasinoAnimation")

  val zeusHoverOffset by infiniteTransition.animateFloat(
    initialValue = -8f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ZeusHover"
  )

  val zeusAuraGlow by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(750, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ZeusGlow"
  )

  val dancerHipSway by infiniteTransition.animateFloat(
    initialValue = -9f,
    targetValue = 9f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "DancerSway"
  )

  val dancerBounce by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = -5f,
    animationSpec = infiniteRepeatable(
      animation = tween(300, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "DancerBounce"
  )

  val dancerTwirl by animateFloatAsState(
    targetValue = if (isBellaVictoryDancing) 360f else 0f,
    animationSpec = tween(900, easing = FastOutSlowInEasing),
    finishedListener = { isBellaVictoryDancing = false },
    label = "DancerTwirl"
  )

  fun triggerSpin(isBonusBuy: Boolean = false) {
    val stakeCost = if (isBonusBuy) selectedBet * 100 else if (isDoubleChanceActive) (selectedBet * 1.25).toLong() else selectedBet
    val actionDesc = if (isBonusBuy) "${game.title} (Bonus Satın Al)" else "${game.title} Çevirme"

    if (!onDeductStake(stakeCost, actionDesc)) return

    // Sound & Haptic on Click
    SlotAudioHapticsEngine.vibrateSpinClick()
    SlotAudioHapticsEngine.playSpinTick()

    isSpinning = true
    lastWinAmount = 0L
    currentMultiplier = 0.0
    activeOrbs.clear()
    celebratoryText = null
    dancerQuote = if (isBonusBuy) "Dev bonus satın aldın aşkım! Vurgun yapıyoruz! 🔥" else "Çevir çevir sevgilim, büyük gelsin! 💋"

    coroutineScope.launch {
      // Rapid tumbling reel ticks
      val spinSteps = if (isTurbo) 3 else 7
      for (step in 0 until spinSteps) {
        delay(if (isTurbo) 45L else 80L)
        SlotAudioHapticsEngine.playSpinTick()
        for (i in 0 until 30) {
          gridSymbols[i] = symbols.random().first
        }
      }

      // Reel Stop Sound & Haptic
      SlotAudioHapticsEngine.playReelStop()
      SlotAudioHapticsEngine.vibrateReelStop()

      val winChance = if (isBonusBuy) 0.95 else if (isDoubleChanceActive) 0.62 else 0.48
      val isWin = Random.nextDouble() < winChance

      val hasZeusLightning = (isOlympus || isSweet || isBass) && (Random.nextDouble() < 0.55 || isBonusBuy)
      if (hasZeusLightning) {
        // Strike Zeus Lightning!
        showLightningBolt = true
        dancerQuote = "Ayy Zeus şimşeği çaktı, dev çarpanlar yağıyor! ⚡"
        SlotAudioHapticsEngine.playZeusThunder() // Thunder sound & haptic rumble!

        val orbCount = Random.nextInt(1, 4)
        for (i in 0 until orbCount) {
          val orbMultiplier = listOf(2, 5, 10, 25, 50, 100, 250, 500, 1000).random()
          activeOrbs.add(orbMultiplier)
        }
        delay(400L)
        showLightningBolt = false
      }

      if (isWin) {
        val baseMultiplier = Random.nextDouble(1.8, 14.0)
        val sumOrbs = activeOrbs.sum().coerceAtLeast(1)
        val finalMultiplier = if (hasZeusLightning) baseMultiplier * sumOrbs else baseMultiplier
        val won = (selectedBet * finalMultiplier).toLong()

        lastWinAmount = won
        currentMultiplier = finalMultiplier
        onAddWinnings(won, "${game.title} Kazancı")

        isBellaVictoryDancing = true
        SlotAudioHapticsEngine.playCoinShower()

        if (finalMultiplier >= 50.0) {
          dancerQuote = "İnanılmazsın şampiyon! SENSATIONAL MEGA WIN! 🏆💖"
          celebratoryText = "🎉 SENSATIONAL MEGA WIN! %.1fx Çarpan ile +%d TP!".format(finalMultiplier, won)
          SlotAudioHapticsEngine.playMegaWinFanfare()
        } else if (finalMultiplier >= 15.0) {
          dancerQuote = "Harikasın aşkım! BIG WIN kazandın! 🔥"
          celebratoryText = "⚡ BÜYÜK KAZANÇ (BIG WIN)! +%d TP!".format(won)
          SlotAudioHapticsEngine.playMegaWinFanfare()
        } else {
          dancerQuote = "Tebrikler sevgilim! +$won TP kazandın! 💋"
          SlotAudioHapticsEngine.playWinChime()
        }
      } else {
        if (!hasZeusLightning) {
          dancerQuote = "Devam et aşkım, bir sonraki dönüşte büyük vuracağız! ✨"
        }
      }

      isSpinning = false
    }
  }

  // Auto-spin handler
  LaunchedEffect(isAutoSpinActive, autoSpinsLeft, isSpinning) {
    if (isAutoSpinActive && autoSpinsLeft > 0 && !isSpinning) {
      delay(if (isTurbo) 350L else 800L)
      triggerSpin()
      autoSpinsLeft--
      if (autoSpinsLeft <= 0) isAutoSpinActive = false
    }
  }

  // Root BoxWithConstraints: Automatically senses phone dimensions to prevent ANY cut-off
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          if (isSweet) listOf(Color(0xFF3B0764), Color(0xFF701A75), Color(0xFF0F172A))
          else if (isBass) listOf(Color(0xFF0C4A6E), Color(0xFF0369A1), Color(0xFF082F49))
          else if (isDogHouse) listOf(Color(0xFF451A03), Color(0xFF78350F), Color(0xFF1E1B4B))
          else listOf(Color(0xFF1E1B4B), Color(0xFF1E293B), Color(0xFF0A0F1D))
        )
      )
  ) {
    val screenH = maxHeight
    val screenW = maxWidth

    // Proportional cell sizing strictly calculated to NEVER exceed screen boundaries
    val cellSize = when {
      screenH < 620.dp -> 26.dp
      screenH < 720.dp -> 30.dp
      screenH < 820.dp -> 34.dp
      else -> 38.dp
    }

    // A. UPPER SCROLLABLE CONTENT VIEWPORT (Padded at bottom so pinned console never covers it!)
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(bottom = 102.dp)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
      // 1. TOP HEADER (Height: 38dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(4.dp))
          Column {
            Text(
              text = game.title.uppercase(),
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = GoldYellow,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${game.provider.uppercase()} • RTP ${game.rtp}",
              fontSize = 8.sp,
              color = Color(0xFFCBD5E1)
            )
          }
        }

        // TP Balance Pill
        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🪙", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${walletPoints.formatTp()} TP",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
          }
        }
      }

      // 2. ACTIVE MULTIPLIER / ZEUS ORBS BANNER
      AnimatedVisibility(visible = activeOrbs.isNotEmpty()) {
        Surface(
          color = Color(0xFF78350F),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isSweet) "🍬 BOMBA ÇARPANLAR: " else if (isBass) "🎣 LEVREK ÇARPANLARI: " else "⚡ ZEUS ŞİMŞEKLERİ: ",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
            activeOrbs.forEach { orb ->
              Surface(
                color = if (orb >= 100) Color(0xFFDC2626) else if (orb >= 25) Color(0xFFD97706) else Color(0xFF0F766E),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow),
                modifier = Modifier.padding(horizontal = 2.dp)
              ) {
                Text(
                  text = "${orb}x",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      // 3. CENTRAL REEL ARENA (Bella on Left, Reels in Center, Zeus on Right)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // LEFT: Sexy Animated Companion Dancer "Bella" + Buy Bonus
          Column(
            modifier = Modifier
              .width(56.dp)
              .padding(end = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            // Interactive Sexy Animated Companion Dancer
            Box(
              modifier = Modifier
                .clickable {
                  dancerKissHeart = !dancerKissHeart
                  SlotAudioHapticsEngine.playPlayfulKiss()
                  SlotAudioHapticsEngine.playDancerGiggle()
                  SlotAudioHapticsEngine.vibrateSpinClick()
                  dancerQuote = "Sana bol şans öpücüğü gönderdim aşkım! 💋😘"
                },
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .offset(y = dancerBounce.dp)
                  .rotate(dancerHipSway + dancerTwirl)
              ) {
                // Sparkling tiara & dancer showgirl figure
                Surface(
                  shape = CircleShape,
                  color = Color(0xFFBE185D),
                  border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF472B6)),
                  modifier = Modifier.size(36.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(text = "💃", fontSize = 22.sp)
                  }
                }
                Spacer(modifier = Modifier.height(1.dp))
                Text(text = "BELLA", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color(0xFFF472B6))
                Text(text = "💋 DANS", fontSize = 6.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCE7F3))
              }

              if (dancerKissHeart) {
                Text(text = "💖", fontSize = 14.sp, modifier = Modifier.offset(x = 12.dp, y = -14.dp))
              }
            }

            // BUY FREE SPINS BUTTON
            Surface(
              color = Color(0xFF991B1B),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldYellow),
              modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isSpinning && walletPoints >= selectedBet * 100) {
                  triggerSpin(isBonusBuy = true)
                }
            ) {
              Column(
                modifier = Modifier.padding(vertical = 3.dp, horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "BUY", color = GoldYellow, fontSize = 8.sp, fontWeight = FontWeight.Black)
                Text(text = "BONUS", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                Text(text = "${selectedBet * 100}", color = GoldYellow, fontSize = 8.sp, fontWeight = FontWeight.Black)
              }
            }

            // DOUBLE CHANCE (ANTE BET)
            Surface(
              color = if (isDoubleChanceActive) Color(0xFF16A34A) else Color(0xFF1E293B),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isDoubleChanceActive) Color(0xFF86EFAC) else Color(0xFF64748B)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  SlotAudioHapticsEngine.vibrateSpinClick()
                  isDoubleChanceActive = !isDoubleChanceActive
                }
            ) {
              Column(
                modifier = Modifier.padding(vertical = 3.dp, horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "DOUBLE", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                Text(text = "CHANCE", color = GoldYellow, fontSize = 7.sp, fontWeight = FontWeight.Black)
                Text(text = if (isDoubleChanceActive) "ON" else "OFF", color = if (isDoubleChanceActive) Color.White else Color(0xFF94A3B8), fontSize = 7.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          // CENTER: 6 Columns x 5 Rows Golden Temple Grid
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E153A)),
            border = androidx.compose.foundation.BorderStroke(2.dp, GoldYellow),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
          ) {
            Column(
              modifier = Modifier
                .background(
                  Brush.verticalGradient(
                    listOf(Color(0xFF2E1065), Color(0xFF1E1B4B))
                  )
                )
                .padding(2.dp)
            ) {
              for (row in 0 until 5) {
                Row(
                  horizontalArrangement = Arrangement.spacedBy(2.dp),
                  modifier = Modifier.padding(vertical = 1.dp)
                ) {
                  for (col in 0 until 6) {
                    val index = row * 6 + col
                    val symbol = gridSymbols[index]
                    Surface(
                      color = Color(0xFF130E26),
                      shape = RoundedCornerShape(4.dp),
                      border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSpinning) Color(0xFFD97706) else Color(0xFF3B2D54)
                      ),
                      modifier = Modifier.size(cellSize)
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Text(
                          text = symbol,
                          fontSize = (cellSize.value * 0.52f).sp,
                          modifier = Modifier.offset {
                            if (isSpinning) IntOffset(0, Random.nextInt(-4, 4))
                            else IntOffset(0, 0)
                          }
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // RIGHT: Animated Olympus Zeus ("Dede") with Lightning Staff
          Column(
            modifier = Modifier
              .width(48.dp)
              .padding(start = 4.dp)
              .offset(y = zeusHoverOffset.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Surface(
              shape = CircleShape,
              color = Color(0xFFD97706),
              border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldYellow),
              modifier = Modifier
                .size(36.dp)
                .scale(zeusAuraGlow)
                .clickable {
                  SlotAudioHapticsEngine.playZeusThunder()
                  showLightningBolt = true
                  coroutineScope.launch {
                    delay(350L)
                    showLightningBolt = false
                  }
                }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = "⚡", fontSize = 22.sp)
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "ZEUS", color = GoldYellow, fontSize = 8.sp, fontWeight = FontWeight.Black)
            Text(text = "DEDE", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            Text(text = "1000x", color = Color(0xFFFBBF24), fontSize = 7.sp, fontWeight = FontWeight.Black)
          }
        }

        // DYNAMIC MULTI-BRANCHING CANVAS LIGHTNING BOLT FROM ZEUS ACROSS THE REELS!
        if (showLightningBolt) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val startX = size.width * 0.88f
            val startY = size.height * 0.35f

            // 3 Lightning Arcs hitting multiple spots on the reels
            val targets = listOf(
              Offset(size.width * 0.45f, size.height * 0.5f),
              Offset(size.width * 0.30f, size.height * 0.35f),
              Offset(size.width * 0.60f, size.height * 0.65f)
            )

            targets.forEach { target ->
              val path = Path().apply {
                moveTo(startX, startY)
                for (step in 1..5) {
                  val frac = step / 5f
                  val midX = startX + (target.x - startX) * frac + Random.nextInt(-20, 20)
                  val midY = startY + (target.y - startY) * frac + Random.nextInt(-16, 16)
                  lineTo(midX, midY)
                }
                lineTo(target.x, target.y)
              }

              // Glowing outer cyan electric aura
              drawPath(
                path = path,
                color = Color(0xFF38BDF8),
                style = Stroke(width = 8f, cap = StrokeCap.Round)
              )
              // Brilliant white electric core
              drawPath(
                path = path,
                color = Color.White,
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
              )
            }
          }
        }
      }

      // 4. DANCER LIVE CHEERING SPEECH BUBBLE & CELEBRATION
      Surface(
        color = Color(0xFF1E1B4B),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF472B6)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "💋", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = celebratoryText ?: dancerQuote,
            color = if (celebratoryText != null) Color(0xFF34D399) else Color(0xFFFCE7F3),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }

    // B. GUARANTEED PINNED BOTTOM CONSOLE (ELEVATED COMFORTABLY ABOVE BOTTOM EDGE)
    Surface(
      color = Color(0xFF0F172A),
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF334155)),
      shadowElevation = 12.dp,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .navigationBarsPadding()
        .padding(bottom = 22.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Bet & Win Info
        Column {
          Text(
            text = if (lastWinAmount > 0) "KAZANÇ: ${lastWinAmount} TP" else "SON KAZANÇ: 0 TP",
            color = if (lastWinAmount > 0) Color(0xFF10B981) else Color(0xFF94A3B8),
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "BAHİS: ", color = Color(0xFFCBD5E1), fontSize = 9.sp)
            IconButton(
              onClick = {
                SlotAudioHapticsEngine.vibrateSpinClick()
                val bets = listOf(10L, 25L, 50L, 100L, 250L, 500L)
                val idx = bets.indexOf(selectedBet)
                if (idx > 0) selectedBet = bets[idx - 1]
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Azalt", tint = GoldYellow, modifier = Modifier.size(15.dp))
            }
            Text(text = "${selectedBet} TP", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
            IconButton(
              onClick = {
                SlotAudioHapticsEngine.vibrateSpinClick()
                val bets = listOf(10L, 25L, 50L, 100L, 250L, 500L)
                val idx = bets.indexOf(selectedBet)
                if (idx < bets.size - 1) selectedBet = bets[idx + 1]
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Arttır", tint = GoldYellow, modifier = Modifier.size(15.dp))
            }
          }
        }

        // Turbo & Auto Play Toggles
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Turbo Toggle
          Surface(
            color = if (isTurbo) Color(0xFF0D9488) else Color(0xFF1E293B),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.clickable {
              SlotAudioHapticsEngine.vibrateSpinClick()
              isTurbo = !isTurbo
            }
          ) {
            Text(
              text = "TURBO",
              color = if (isTurbo) Color.White else Color(0xFF94A3B8),
              fontSize = 8.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
            )
          }

          // Auto Toggle
          Surface(
            color = if (isAutoSpinActive) Color(0xFF2563EB) else Color(0xFF1E293B),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.clickable {
              SlotAudioHapticsEngine.vibrateSpinClick()
              if (isAutoSpinActive) {
                isAutoSpinActive = false
                autoSpinsLeft = 0
              } else {
                isAutoSpinActive = true
                autoSpinsLeft = 25
              }
            }
          ) {
            Text(
              text = if (isAutoSpinActive) "OTO ($autoSpinsLeft)" else "OTO 25x",
              color = if (isAutoSpinActive) Color.White else Color(0xFF94A3B8),
              fontSize = 8.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
            )
          }
        }

        // Pragmatic Big Circular Tactile Spin Button
        Surface(
          shape = CircleShape,
          color = GoldYellow,
          border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFB45309)),
          modifier = Modifier
            .size(46.dp)
            .clickable(enabled = !isSpinning && walletPoints >= selectedBet) {
              triggerSpin()
            }
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              Icons.Default.Refresh,
              contentDescription = "Çevir",
              tint = Color(0xFF0F172A),
              modifier = Modifier
                .size(28.dp)
                .rotate(if (isSpinning) 180f else 0f)
            )
          }
        }
      }
    }
  }
}
