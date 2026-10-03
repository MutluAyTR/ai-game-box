package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.service.AdManager
import com.example.service.AdNetwork
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import kotlinx.coroutines.delay

/**
 * High-polish Multi-Ad mediation banner view (AdMob, Unity Ads, AppLovin MAX).
 */
@Composable
fun StickyBannerAdView(
  modifier: Modifier = Modifier,
  onAdClick: () -> Unit = {}
) {
  var isDismissed by remember { mutableStateOf(false) }
  val adState by AdManager.state.collectAsState()

  if (isDismissed) return

  Surface(
    color = Color(0xFF0F172A),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp)
      .testTag("sticky_banner_ad")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onAdClick() }
        .padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Provider Badge
        Surface(
          color = Color(adState.activeNetwork.badgeColorHex),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = adState.activeNetwork.tag,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "⚽ Puma Future Ultimate Pro Krampon",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "AD",
              fontSize = 8.sp,
              color = GoldYellow,
              fontWeight = FontWeight.Black
            )
          }
          Text(
            text = "Sahada fark yarat! Yeni sezon koleksiyonu %25 indirimle.",
            fontSize = 9.sp,
            color = Color(0xFF94A3B8),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = TealDark,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = "İncele",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        IconButton(
          onClick = { isDismissed = true },
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Reklamı Gizle",
            tint = Color(0xFF64748B),
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}

/**
 * Rewarded Video Ad Dialog:
 * Plays a realistic 5-second simulated video ad, then grants user +500 TP or +1000 TP bonus.
 */
@Composable
fun RewardedAdDialog(
  rewardAmount: Long = 500L,
  onDismiss: () -> Unit,
  onRewardClaimed: (Long) -> Unit
) {
  var secondsLeft by remember { mutableIntStateOf(5) }
  var isCompleted by remember { mutableStateOf(false) }
  val adState by AdManager.state.collectAsState()

  LaunchedEffect(Unit) {
    AdManager.recordImpression()
    while (secondsLeft > 0) {
      delay(1000L)
      secondsLeft--
    }
    isCompleted = true
  }

  val progress by animateFloatAsState(
    targetValue = (5 - secondsLeft) / 5f,
    animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    label = "AdTimerProgress"
  )

  Dialog(
    onDismissRequest = {
      if (isCompleted) onDismiss()
    },
    properties = DialogProperties(dismissOnBackPress = isCompleted, dismissOnClickOutside = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("rewarded_ad_modal"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = Color(adState.activeNetwork.badgeColorHex),
            shape = RoundedCornerShape(6.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${adState.activeNetwork.displayName} • Ödüllü Reklam",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }

          if (isCompleted) {
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
            }
          } else {
            Surface(
              color = Color(0xFF334155),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "$secondsLeft sn",
                color = GoldYellow,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Ad Creative Video Simulation Canvas
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
              )
            )
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
          ) {
            Text(text = "🎮", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "EA SPORTS FC™ 25",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              textAlign = TextAlign.Center
            )
            Text(
              text = "Kulübün İçin Oyna! 19.000+ Oyuncu, 700+ Takım.",
              color = Color(0xFF94A3B8),
              fontSize = 11.sp,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              color = GoldYellow,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Hemen Oyna ➔",
                color = TealDark,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = if (isCompleted) Color(0xFF10B981) else GoldYellow,
          trackColor = Color(0xFF334155),
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Reward Status / Button
        if (isCompleted) {
          Button(
            onClick = {
              AdManager.recordRewardedAdWatched(rewardAmount)
              onRewardClaimed(rewardAmount)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("claim_ad_reward_button")
          ) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Tebrikler! +$rewardAmount TP Ödülü Al",
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color.White
            )
          }
        } else {
          Text(
            text = "Ödülü kazanmak için reklamın tamamlanmasını bekleyin...",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}

/**
 * Ad Network Selector & Mediation Stats Card:
 * Lets users switch between Google AdMob, Unity Ads, AppLovin MAX, ironSource.
 */
@Composable
fun AdNetworkSelectorCard(modifier: Modifier = Modifier) {
  val adState by AdManager.state.collectAsState()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "📢", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "MULTI-AD MEDIATION SERVİSİ",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF1E293B)
          )
        }

        Surface(
          color = Color(0xFFE0F2FE),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "Fill Rate %${adState.fillRatePercent}",
            color = Color(0xFF0284C7),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Aktif Reklam Ağı Sağlayıcısı:",
        fontSize = 11.sp,
        color = Color(0xFF64748B)
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        AdNetwork.entries.forEach { network ->
          val isSelected = adState.activeNetwork == network
          Surface(
            color = if (isSelected) Color(network.badgeColorHex) else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .clickable { AdManager.setActiveNetwork(network) }
          ) {
            Text(
              text = network.tag,
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
              color = if (isSelected) Color.White else Color(0xFF475569),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 7.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Toplam Gösterim: ${adState.totalImpressions}",
          fontSize = 10.sp,
          color = Color(0xFF64748B)
        )
        Text(
          text = "Kazanılan TP: +${adState.totalRewardedTpEarned} TP",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF059669)
        )
      }
    }
  }
}

/**
 * Google AdMob Interstitial Ad Dialog for Post-Betting monetization.
 * Shows a full-screen high engagement sponsor ad with 3-second countdown before skip/close.
 */
@Composable
fun AdMobInterstitialAdDialog(
  onDismiss: () -> Unit
) {
  var secondsRemaining by remember { mutableIntStateOf(3) }
  var canClose by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    AdManager.recordImpression()
    while (secondsRemaining > 0) {
      delay(1000L)
      secondsRemaining--
    }
    canClose = true
  }

  Dialog(
    onDismissRequest = {
      if (canClose) onDismiss()
    },
    properties = DialogProperties(dismissOnBackPress = canClose, dismissOnClickOutside = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("admob_interstitial_dialog"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = Color(0xFF4285F4),
            shape = RoundedCornerShape(6.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Google AdMob • Geçiş Reklamı",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }

          if (canClose) {
            Surface(
              color = Color(0xFF334155),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.clickable { onDismiss() }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Kapat ✕",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          } else {
            Surface(
              color = Color(0xFF334155),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "$secondsRemaining sn",
                color = GoldYellow,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Creative Graphic
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
              )
            )
            .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(14.dp)
          ) {
            Text(text = "🏆", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "UEFA ŞAMPİYONLAR LİGİ 2026",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 16.sp,
              textAlign = TextAlign.Center
            )
            Text(
              text = "Canlı Yayınlar, Opta İstatistikleri ve Özel Analizler Exxen Spor'da!",
              color = Color(0xFF93C5FD),
              fontSize = 11.sp,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              color = GoldYellow,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.clickable { if (canClose) onDismiss() }
            ) {
              Text(
                text = "Hemen İzle ➔",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = if (canClose) "Reklamı kapatmak için sağ üstteki butona dokunun." else "Geçiş reklamı tamamlanıyor...",
          color = Color(0xFF64748B),
          fontSize = 10.sp,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}
