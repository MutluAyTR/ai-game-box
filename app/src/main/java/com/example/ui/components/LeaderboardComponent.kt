package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.UserStatistics
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.formatTp

/**
 * Clean List Component for Leaderboard:
 * Ranks simulated users based on their total earnings and profit margins (ROI %),
 * featuring an interactive sorting selector and visual podium for top performers.
 */
@Composable
fun LeaderboardComponent(
  leaderboard: List<UserStatistics>,
  currentUserId: String = "default_user",
  modifier: Modifier = Modifier
) {
  // 0: Toplam Kazanç (Total Earnings), 1: Kâr Marjı (Profit Margin / ROI %), 2: Kazanma Oranı (Win Rate)
  var rankingCriterion by remember { mutableIntStateOf(0) }

  val sortedList = remember(leaderboard, rankingCriterion) {
    when (rankingCriterion) {
      1 -> leaderboard.sortedByDescending { it.roiPercent }
      2 -> leaderboard.sortedByDescending { it.winRate }
      else -> leaderboard.sortedByDescending { it.totalTpWon }
    }
  }

  val top1 = sortedList.getOrNull(0)
  val top2 = sortedList.getOrNull(1)
  val top3 = sortedList.getOrNull(2)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("leaderboard_component")
  ) {
    // 1. Header Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = TealDark)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF0D3B3E), Color(0xFF145357))
            )
          )
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Leaderboard,
            contentDescription = "Liderlik",
            tint = GoldYellow,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "LİDERLİK SIRALAMASI & KÂR MARJI",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Simüle edilen kullanıcıların toplam kazanç ve ROI kâr marjları",
              color = Color(0xFFB0BEC5),
              fontSize = 11.sp
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GoldYellow)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "CANLI LİSTE",
            color = TealDark,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }
    }

    // 2. Ranking Criterion Tabs (Toplam Kazanç vs Kâr Marjı vs Kazanma Oranı)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf(
        0 to "💰 Toplam Kazanç",
        1 to "📈 Kâr Marjı (ROI)",
        2 to "🎯 Kazanma Oranı"
      ).forEach { (index, label) ->
        val isSelected = rankingCriterion == index
        Surface(
          color = if (isSelected) TealDark else Color.White,
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) TealDark else Color(0xFFCBD5E1)),
          modifier = Modifier
            .weight(1f)
            .clickable { rankingCriterion = index }
            .testTag("ranking_filter_$index")
        ) {
          Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF334155),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 7.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 3. Top 3 Podium
    if (top1 != null && top2 != null && top3 != null) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
      ) {
        // 2nd Place (Silver)
        PodiumCard(
          rank = 2,
          user = top2,
          badgeColor = Color(0xFFB0BEC5),
          medal = "🥈",
          cardHeight = 145,
          rankingCriterion = rankingCriterion,
          modifier = Modifier.weight(1f)
        )

        // 1st Place (Gold)
        PodiumCard(
          rank = 1,
          user = top1,
          badgeColor = GoldYellow,
          medal = "👑",
          cardHeight = 165,
          rankingCriterion = rankingCriterion,
          modifier = Modifier.weight(1.1f)
        )

        // 3rd Place (Bronze)
        PodiumCard(
          rank = 3,
          user = top3,
          badgeColor = Color(0xFFCD7F32),
          medal = "🥉",
          cardHeight = 135,
          rankingCriterion = rankingCriterion,
          modifier = Modifier.weight(1f)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 4. Clean List Component for All Ranked Users
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      sortedList.forEachIndexed { index, user ->
        val isCurrentUser = user.userId == currentUserId
        LeaderboardRowItem(
          rank = index + 1,
          user = user,
          isCurrentUser = isCurrentUser,
          rankingCriterion = rankingCriterion
        )
      }
    }
  }
}

@Composable
private fun PodiumCard(
  rank: Int,
  user: UserStatistics,
  badgeColor: Color,
  medal: String,
  cardHeight: Int,
  rankingCriterion: Int,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .height(cardHeight.dp)
      .testTag("podium_rank_$rank"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = medal, fontSize = 16.sp)
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(badgeColor),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "$rank",
            color = TealDark,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = user.username,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = Color(0xFF1E293B),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "+${user.totalTpWon.formatTp()}",
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          color = Color(0xFF059669)
        )
        Text(
          text = "+%${user.roiPercent} Kâr",
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          color = Color(0xFF0D9488)
        )
      }

      Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(4.dp)
      ) {
        Text(
          text = "%${user.winRate} İsabet",
          color = Color(0xFF475569),
          fontWeight = FontWeight.SemiBold,
          fontSize = 9.sp,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
      }
    }
  }
}

/**
 * Clean list item component for individual ranked user.
 * Displays rank, username, badge, total earnings, profit margin (ROI %), and win rate.
 */
@Composable
private fun LeaderboardRowItem(
  rank: Int,
  user: UserStatistics,
  isCurrentUser: Boolean,
  rankingCriterion: Int
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("leaderboard_item_${user.userId}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCurrentUser) Color(0xFFE6FFFA) else Color.White
    ),
    border = if (isCurrentUser) {
      androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0D9488))
    } else null,
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 9.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Rank & User Profile Info
      Row(
        modifier = Modifier.weight(1.3f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(
              when (rank) {
                1 -> GoldYellow
                2 -> Color(0xFFCBD5E1)
                3 -> Color(0xFFFDBA74)
                else -> Color(0xFFF1F5F9)
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "$rank",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = if (rank <= 3) TealDark else Color(0xFF475569)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = user.username,
              fontWeight = if (isCurrentUser) FontWeight.Black else FontWeight.Bold,
              fontSize = 12.sp,
              color = if (isCurrentUser) Color(0xFF0F766E) else Color(0xFF0F172A),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (isCurrentUser) {
              Spacer(modifier = Modifier.width(4.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF0D9488))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "SEN",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 8.sp
                )
              }
            }
          }

          Text(
            text = "${user.badge} • ${user.wonBets}G/${user.lostBets}M Kupon",
            fontSize = 10.sp,
            color = Color(0xFF64748B)
          )
        }
      }

      // Right: Total Earnings and Profit Margin (Clean Visualization)
      Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.End
      ) {
        // Total Earnings
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = Color(0xFF059669),
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "+${user.totalTpWon.formatTp()} TP",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF059669)
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Profit Margin (ROI %) & Win Rate
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFFF0FDF4),
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF86EFAC))
          ) {
            Text(
              text = "+%${user.roiPercent} Kâr Marjı",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          Text(
            text = "%${user.winRate}",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
          )
        }
      }
    }
  }
}
