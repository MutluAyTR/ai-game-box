package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.formatTp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class SlotGameMetadata(
  val id: String,
  val title: String,
  val provider: String,
  val iconEmoji: String,
  val rtp: String,
  val volatility: String,
  val maxWin: String,
  val category: String,
  val isHot: Boolean = false,
  val hasBonusBuy: Boolean = true,
  val bannerGradient: List<Color>
)

/**
 * Rovbet & Pragmatic Play Style Virtual Slot Casino Hub:
 * Features 20+ genuine slots with verified RTP, volatility, and max win parameters,
 * search and category filtering, plus fully playable interactive slot machines:
 * - Gates of Olympus (Zeus / Dede with Tumble & Multiplier Orbs up to 500x)
 * - Sweet Bonanza (Tumbling Fruits & 100x Bombs)
 * - Big Bass Bonanza (Fisherman Cash Collection)
 * - Klasik 777 Vegas Slot
 */
@Composable
fun RovbetSlotCasinoView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Tümü") }
  var selectedGameToPlay by remember { mutableStateOf<SlotGameMetadata?>(null) }

  val slotGames = remember {
    listOf(
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
        bannerGradient = listOf(Color(0xFF78350F), Color(0xFF1E1B4B))
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
        id = "gates_of_olympus_1000",
        title = "Gates of Olympus 1000",
        provider = "Pragmatic Play",
        iconEmoji = "👑",
        rtp = "%96.50",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "15.000x",
        category = "Pragmatic",
        isHot = true,
        bannerGradient = listOf(Color(0xFFD97706), Color(0xFF451A03))
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
        bannerGradient = listOf(Color(0xFFF43F5E), Color(0xFF881337))
      ),
      SlotGameMetadata(
        id = "sugar_rush",
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
        id = "fruit_party",
        title = "Fruit Party 2",
        provider = "Pragmatic Play",
        iconEmoji = "🍇",
        rtp = "%96.53",
        volatility = "Yüksek ⚡⚡⚡⚡",
        maxWin = "5.000x",
        category = "Meyve & Şeker",
        bannerGradient = listOf(Color(0xFF16A34A), Color(0xFF14532D))
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
        id = "book_of_dead",
        title = "Book of Dead",
        provider = "Play'n GO",
        iconEmoji = "📜",
        rtp = "%96.21",
        volatility = "Yüksek ⚡⚡⚡⚡",
        maxWin = "5.000x",
        category = "Popüler",
        bannerGradient = listOf(Color(0xFFB45309), Color(0xFF451A03))
      ),
      SlotGameMetadata(
        id = "wolf_gold",
        title = "Wolf Gold Jackpot",
        provider = "Pragmatic Play",
        iconEmoji = "🐺",
        rtp = "%96.01",
        volatility = "Orta ⚡⚡⚡",
        maxWin = "Mega Jackpot",
        category = "Jackpot",
        bannerGradient = listOf(Color(0xFF1E3A8A), Color(0xFF172554))
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
      ),
      SlotGameMetadata(
        id = "madame_destiny_megaways",
        title = "Madame Destiny Megaways",
        provider = "Pragmatic Play",
        iconEmoji = "🔮",
        rtp = "%96.56",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "5.000x",
        category = "Megaways",
        bannerGradient = listOf(Color(0xFF4C1D95), Color(0xFF2E1065))
      ),
      SlotGameMetadata(
        id = "buffalo_king_megaways",
        title = "Buffalo King Megaways",
        provider = "Pragmatic Play",
        iconEmoji = "🦬",
        rtp = "%96.52",
        volatility = "Çok Yüksek ⚡⚡⚡⚡⚡",
        maxWin = "5.000x",
        category = "Megaways",
        bannerGradient = listOf(Color(0xFF854D0E), Color(0xFF713F12))
      ),
      SlotGameMetadata(
        id = "wild_west_gold",
        title = "Wild West Gold",
        provider = "Pragmatic Play",
        iconEmoji = "⭐",
        rtp = "%96.51",
        volatility = "Yüksek ⚡⚡⚡⚡",
        maxWin = "10.000x",
        category = "Pragmatic",
        bannerGradient = listOf(Color(0xFFA16207), Color(0xFF451A03))
      ),
      SlotGameMetadata(
        id = "fire_joker",
        title = "Fire Joker Respin",
        provider = "Play'n GO",
        iconEmoji = "🃏",
        rtp = "%96.15",
        volatility = "Orta ⚡⚡⚡",
        maxWin = "800x",
        category = "Klasik 777",
        bannerGradient = listOf(Color(0xFFC2410C), Color(0xFF7C2D12))
      ),
      SlotGameMetadata(
        id = "cleocatra",
        title = "Cleocatra Sticky Wilds",
        provider = "Pragmatic Play",
        iconEmoji = "😸",
        rtp = "%96.20",
        volatility = "Yüksek ⚡⚡⚡⚡",
        maxWin = "5.000x",
        category = "Pragmatic",
        bannerGradient = listOf(Color(0xFFCA8A04), Color(0xFF713F12))
      )
    )
  }

  val categories = listOf("Tümü", "Popüler", "Pragmatic", "Meyve & Şeker", "Megaways", "Hacksaw", "Klasik 777", "Jackpot")

  val filteredGames = remember(slotGames, searchQuery, selectedCategory) {
    slotGames.filter { game ->
      val matchesSearch = searchQuery.isBlank() || game.title.contains(searchQuery, ignoreCase = true) || game.provider.contains(searchQuery, ignoreCase = true)
      val matchesCategory = selectedCategory == "Tümü" || game.category == selectedCategory || (selectedCategory == "Popüler" && game.isHot)
      matchesSearch && matchesCategory
    }
  }

  if (selectedGameToPlay != null) {
    com.example.ui.components.AuthenticPragmaticSlotModal(
      game = selectedGameToPlay!!,
      walletPoints = walletPoints,
      onDeductStake = onDeductStake,
      onAddWinnings = onAddWinnings,
      onDismiss = { selectedGameToPlay = null }
    )
  } else {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF0B132B))
        .padding(10.dp)
    ) {
    // 1. Rovbet Casino Hero Banner
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2541)),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A506B))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF1E1B4B))
            )
          )
          .padding(12.dp)
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
                  text = "ROVBET CASINO",
                  color = Color(0xFF0F172A),
                  fontWeight = FontWeight.Black,
                  fontSize = 9.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "⚡ Pragmatic & Hacksaw", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Gates of Olympus & Sweet Bonanza",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
            Text(
              text = "500x Zeus Şimşekleri, 100x Bombalar ve Bonus Satın Al!",
              color = Color(0xFFCBD5E1),
              fontSize = 10.sp
            )
          }

          Text(text = "⚡👑🍭", fontSize = 28.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

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
        focusedContainerColor = Color(0xFF1C2541),
        unfocusedContainerColor = Color(0xFF1C2541),
        focusedBorderColor = GoldYellow,
        unfocusedBorderColor = Color(0xFF3A506B),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
      )
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Category Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(categories) { cat ->
        val isSel = selectedCategory == cat
        Surface(
          color = if (isSel) GoldYellow else Color(0xFF1C2541),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) GoldYellow else Color(0xFF3A506B)),
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

    Spacer(modifier = Modifier.height(8.dp))

    // 3. Dynamic Responsive Grid of Slot Games
    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 150.dp),
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(filteredGames, key = { it.id }) { game ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2541)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A506B)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedGameToPlay = game }
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            // Game Visual Banner
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.verticalGradient(game.bannerGradient)),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = game.iconEmoji, fontSize = 32.sp)
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

              if (game.hasBonusBuy) {
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
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title & Provider
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
