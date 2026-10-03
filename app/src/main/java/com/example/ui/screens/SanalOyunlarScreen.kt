package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.formatTp

enum class SanalOyunCategory(val title: String, val icon: String) {
  HUB("Tüm Oyunlar", "🎮"),
  ROVBET_SLOTS("Rovbet Slotlar", "🎰"),
  SIMULTANEOUS_ROUND("22 Maç Canlı", "⚡"),
  KAZI_KAZAN("Altın Yumurta", "🥚"),
  KENO("Hızlı On Keno", "🎱"),
  SUPER_CARK("Süper Çark", "🎡"),
  SANSLI_ARABALAR("Şanslı Arabalar", "🏎️"),
  FUTBOL("Sanal Futbol", "⚽"),
  BASKETBOL("Sanal Basketbol", "🏀"),
  AT_YARISI("Sanal At Yarışı", "🐎"),
  TAZI_YARISI("Tazı Yarışı", "🐕"),
  ZEPLIN("Zeplin (Crash)", "🚀"),
  MAYIN_TARLASI("Mayın Tarlası", "💣"),
  PENALTI("Penaltı", "🥅"),
  SPOR_TOTO("Spor Toto 15", "📋")
}

data class GameHubCardItem(
  val category: SanalOyunCategory,
  val title: String,
  val provider: String,
  val icon: String,
  val badge: String,
  val badgeColor: Color,
  val rtpDescription: String
)

/**
 * Flagship Sanal Oyunlar & Şans Oyunları Hub (Milli Piyango Online, Oley, Nesine & Bilyoner)
 * Features dynamic Adaptive LazyVerticalGrid, zero text overflows, 22+ simultaneous match round timer,
 * and complete interactive games portfolio.
 */
@Composable
fun SanalOyunlarScreen(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit,
  onBack: () -> Unit
) {
  var selectedCategory by remember { mutableStateOf(SanalOyunCategory.SIMULTANEOUS_ROUND) }

  val hubItems = remember {
    listOf(
      GameHubCardItem(
        SanalOyunCategory.ROVBET_SLOTS,
        "Rovbet Slot Casino",
        "Pragmatic & Hacksaw",
        "🎰",
        "20+ SLOT OYUNU",
        GoldYellow,
        "Gates of Olympus, Sweet Bonanza, 500x"
      ),
      GameHubCardItem(
        SanalOyunCategory.SIMULTANEOUS_ROUND,
        "22 Maç Canlı Sanal İddaa",
        "Süper Lig & EuroLeague",
        "⚡",
        "CANLI EŞ ZAMANLI",
        Color(0xFFDC2626),
        "Aynı anda başlar ve biter"
      ),
      GameHubCardItem(
        SanalOyunCategory.KAZI_KAZAN,
        "Kazı Kazan: Altın Yumurta",
        "Milli Piyango Online",
        "🥚",
        "5.000 TP İKRAMİYE",
        Color(0xFFD97706),
        "3 Aynı Sembolü Kazı & Bul"
      ),
      GameHubCardItem(
        SanalOyunCategory.KENO,
        "Hızlı On / 20 Top Keno",
        "Milli Piyango Online",
        "🎱",
        "250x ÇARPAN",
        Color(0xFF2563EB),
        "80 Toptan 20 Top Çekilişi"
      ),
      GameHubCardItem(
        SanalOyunCategory.SUPER_CARK,
        "Süper Çark (Dream Catcher)",
        "Oley Şans Oyunları",
        "🎡",
        "40x DEV ÇARPAN",
        Color(0xFF7C3AED),
        "Çarkı Döndür, Çarpanı Yakala"
      ),
      GameHubCardItem(
        SanalOyunCategory.SANSLI_ARABALAR,
        "Şanslı Arabalar Hız Pisti",
        "Milli Piyango Sanal",
        "🏎️",
        "HIZ YARIŞI",
        Color(0xFFE11D48),
        "6 Formula Aracı Oval Pist"
      ),
      GameHubCardItem(
        SanalOyunCategory.FUTBOL,
        "Sanal Futbol Ligi",
        "Nesine & Bilyoner",
        "⚽",
        "SÜPER LİG",
        TealDark,
        "Anlık Maç Radarı & 1-X-2"
      ),
      GameHubCardItem(
        SanalOyunCategory.BASKETBOL,
        "Sanal Basketbol Ligi",
        "EuroLeague & BSL",
        "🏀",
        "ÇEYREK SKOR",
        Color(0xFFEA580C),
        "Alt/Üst & Handikap Bahisleri"
      ),
      GameHubCardItem(
        SanalOyunCategory.AT_YARISI,
        "Sanal At Yarışı",
        "TJK Veliefendi 1200m",
        "🐎",
        "GANYAN",
        Color(0xFF059669),
        "6 Safkan At Çim Pist"
      ),
      GameHubCardItem(
        SanalOyunCategory.TAZI_YARISI,
        "Sanal Tazı Yarışı",
        "Nesine Sanal Yarışlar",
        "🐕",
        "SPRINT",
        Color(0xFF9333EA),
        "480m Kum Pist Sprint"
      ),
      GameHubCardItem(
        SanalOyunCategory.ZEPLIN,
        "Zeplin / Aviator (Crash)",
        "Nesine Sanal Oyunlar",
        "🚀",
        "KASAYI BOZDUR",
        Color(0xFF0284C7),
        "Yükselen Çarpan Simülatörü"
      ),
      GameHubCardItem(
        SanalOyunCategory.MAYIN_TARLASI,
        "Mayın Tarlası (Mines)",
        "Nesine Sanal Oyunlar",
        "💣",
        "3 MAYIN",
        Color(0xFFB45309),
        "5x5 Kutuda Elmas Avı"
      ),
      GameHubCardItem(
        SanalOyunCategory.PENALTI,
        "Penaltı Atışları",
        "Milli Piyango Pro",
        "🥅",
        "SERİ GOL",
        Color(0xFF16A34A),
        "5 Köşeye Şut Çek"
      ),
      GameHubCardItem(
        SanalOyunCategory.SPOR_TOTO,
        "Spor Toto 15'li Bülteni",
        "Nesine Spor Toto",
        "📋",
        "15.450.000 TP",
        TealDark,
        "15 Derbi Maçını Tahmin Et"
      )
    )
  }

  var isSlotGameFullScreen by remember { mutableStateOf(false) }

  BackHandler {
    if (isSlotGameFullScreen) {
      isSlotGameFullScreen = false
    } else if (selectedCategory != SanalOyunCategory.HUB) {
      selectedCategory = SanalOyunCategory.HUB
    } else {
      onBack()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(if (isSlotGameFullScreen) Color(0xFF070B19) else Color(0xFFF1F5F9))
      .then(if (!isSlotGameFullScreen) Modifier.navigationBarsPadding().padding(bottom = 8.dp) else Modifier)
      .testTag("sanal_oyunlar_screen")
  ) {
    if (!isSlotGameFullScreen) {
      // 1. Top Bar (Clean, non-wrapping)
      Surface(
        color = TealDark,
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
      ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "SANAL BAHİS & ŞANS OYUNLARI",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "Milli Piyango Online, Oley, Nesine & Bilyoner Portföyü",
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Safe non-wrapping balance pill
          Surface(
            color = Color(0xFF072628),
            shape = RoundedCornerShape(12.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "🪙", fontSize = 11.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${walletPoints.formatTp()} TP",
                color = GoldYellow,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                maxLines = 1,
                softWrap = false
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Scrollable Category Tabs
        ScrollableTabRow(
          selectedTabIndex = selectedCategory.ordinal,
          edgePadding = 0.dp,
          containerColor = Color.Transparent,
          divider = {},
          indicator = {}
        ) {
          SanalOyunCategory.entries.forEach { cat ->
            val isSelected = selectedCategory == cat
            Surface(
              color = if (isSelected) GoldYellow else Color(0xFF0F766E),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .padding(end = 6.dp)
                .clickable { selectedCategory = cat }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = cat.icon, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = cat.title,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                  color = if (isSelected) TealDark else Color.White,
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
          }
        }
      }
    }
  }

    // 3. Main Content (Weight 1f guarantees content is STRICTLY inside viewport bounds)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      when (selectedCategory) {
        SanalOyunCategory.HUB -> {
          // Dynamic Adaptive LazyVerticalGrid
          LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
              .fillMaxSize()
              .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(hubItems) { item ->
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { selectedCategory = item.category }
              ) {
                Column(
                  modifier = Modifier.padding(10.dp),
                  verticalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(text = item.icon, fontSize = 24.sp)
                    Surface(
                      color = item.badgeColor,
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = item.badge,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )

                  Text(
                    text = item.provider,
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  Text(
                    text = item.rtpDescription,
                    fontSize = 9.sp,
                    color = TealDark,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }
        }

        SanalOyunCategory.ROVBET_SLOTS -> com.example.ui.components.SlotsArenaComponent(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings,
          onActiveGameChanged = { isSlotGameFullScreen = it },
          onClose = null
        )

        SanalOyunCategory.SIMULTANEOUS_ROUND -> VirtualSimultaneousRoundView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.KAZI_KAZAN -> KaziKazanAltinYumurtaView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.KENO -> HizliOnKenoView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.SUPER_CARK -> SuperCarkOleyView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.SANSLI_ARABALAR -> SansliArabalarView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.BASKETBOL -> SanalBasketbolView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.TAZI_YARISI -> SanalTaziYarisiView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.MAYIN_TARLASI -> MayinTarlasiView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.PENALTI -> PenaltiAtislariView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.FUTBOL -> VirtualSimultaneousRoundView(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.AT_YARISI -> SanalAtYarisiViewStandalone(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.ZEPLIN -> ZeplinCrashViewStandalone(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )

        SanalOyunCategory.SPOR_TOTO -> SporToto15ViewStandalone(
          walletPoints = walletPoints,
          onDeductStake = onDeductStake,
          onAddWinnings = onAddWinnings
        )
      }
    }
  }
}

/**
 * At Yarışı Standalone Wrapper
 */
@Composable
fun SanalAtYarisiViewStandalone(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  SanalAtYarisiView(
    walletPoints = walletPoints,
    onDeductStake = onDeductStake,
    onAddWinnings = onAddWinnings
  )
}

/**
 * Zeplin Standalone Wrapper
 */
@Composable
fun ZeplinCrashViewStandalone(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  ZeplinCrashView(
    walletPoints = walletPoints,
    onDeductStake = onDeductStake,
    onAddWinnings = onAddWinnings
  )
}

/**
 * Spor Toto Standalone Wrapper
 */
@Composable
fun SporToto15ViewStandalone(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  SporToto15View(
    walletPoints = walletPoints,
    onDeductStake = onDeductStake,
    onAddWinnings = onAddWinnings
  )
}
