package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun QuickActionGrid(
  onOpenLiveScores: () -> Unit,
  onOpenEditors: () -> Unit,
  onOpenPopularCoupons: () -> Unit,
  onOpenSimulation: () -> Unit,
  onOpenLeaderboard: () -> Unit,
  onOpenSporToto: () -> Unit,
  onOpenFinishedMatches: () -> Unit = {},
  onOpenVoiceAi: () -> Unit = {},
  onOpenLuckyWheel: () -> Unit = {},
  onOpenNews: () -> Unit = {},
  onOpenAiWizard: () -> Unit = {},
  onOpenMissions: () -> Unit = {},
  onOpenSanalOyunlar: () -> Unit = {},
  onOpenRewardedAd: () -> Unit = {},
  onOpenFavoriteTeamAnalytics: () -> Unit = {},
  onOpenCouponVisualizer: () -> Unit = {},
  onOpenPlayerDirectory: () -> Unit = {},
  onOpenBettingDashboard: () -> Unit = {},
  onOpenGeminiIngestion: () -> Unit = {},
  onOpenTransferMarket: () -> Unit = {},
  onOpenLiveDraw: () -> Unit = {},
  onOpenLiveStandings: () -> Unit = {}
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("quick_action_grid")
  ) {
    // Row 1: Premier AI & Live Draw
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      QuickCard(
        title = "Canlı Kura",
        emoji = "🎲",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenLiveDraw
      )
      QuickCard(
        title = "AI Sihirbaz",
        emoji = "🤖",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenAiWizard
      )
      QuickCard(
        title = "Biten Maçlar",
        emoji = "🏁",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenFinishedMatches
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Row 2
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      QuickCard(
        title = "Canlı Sonuçlar",
        emoji = "📢",
        isNew = false,
        modifier = Modifier.weight(1f),
        onClick = onOpenLiveScores
      )
      QuickCard(
        title = "D3 Puan Tablosu",
        emoji = "📊",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenLiveStandings
      )
      QuickCard(
        title = "Popüler Kuponlar",
        emoji = "📑",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenPopularCoupons
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Row 3
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      QuickCard(
        title = "Spor Toto",
        emoji = "1️⃣✖️2️⃣",
        isNew = false,
        modifier = Modifier.weight(1f),
        onClick = onOpenSporToto
      )
      QuickCard(
        title = "Kazanan 10",
        emoji = "👑",
        isNew = false,
        modifier = Modifier.weight(1f),
        onClick = onOpenLeaderboard
      )
      QuickCard(
        title = "Şans Çarkı",
        emoji = "🍀",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenLuckyWheel
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Row 4: Günlük Görevler, Reklam İzle & TP Kazan, Favori Takım Analizi
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      QuickCard(
        title = "Görevler",
        emoji = "🎯",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenMissions
      )
      QuickCard(
        title = "Reklam & TP",
        emoji = "📺",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenRewardedAd
      )
      QuickCard(
        title = "Favori Takım",
        emoji = "⭐",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenFavoriteTeamAnalytics
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Row 5: Kupon Paylaş, 50K+ Sporcular, Transfer & AI
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      QuickCard(
        title = "Kupon Paylaş",
        emoji = "🎨",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenCouponVisualizer
      )
      QuickCard(
        title = "50K+ Sporcular",
        emoji = "👤",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenPlayerDirectory
      )
      QuickCard(
        title = "Transfer & AI",
        emoji = "✈️",
        isNew = true,
        modifier = Modifier.weight(1f),
        onClick = onOpenTransferMarket
      )
    }
  }
}

@Composable
private fun QuickCard(
  title: String,
  emoji: String,
  isNew: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color.White,
    shadowElevation = 1.dp,
    modifier = modifier
      .clickable { onClick() }
      .testTag("quick_card_$title")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
      if (isNew) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(4.dp))
            .background(GoldYellow)
            .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
          Text(
            text = "YENİ",
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Black,
            color = TealDark
          )
        }
      }

      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = title,
          fontSize = 10.5.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF1E293B),
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
