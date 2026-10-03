package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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

data class LeaderboardUser(
  val rank: Int,
  val username: String,
  val avatarEmoji: String,
  val titleBadge: String,
  val totalWonTP: Long,
  val winRate: Int,
  val couponCount: Int,
  val sampleOdds: Double
)

val LEADERBOARD_USERS = listOf(
  LeaderboardUser(1, "Kerem_Tahminci", "👑", "Efsane Tahminci", 48250L, 82, 14, 18.45),
  LeaderboardUser(2, "BurakArena99", "🥈", "Oran Avcısı", 36100L, 78, 12, 12.20),
  LeaderboardUser(3, "Zeynep_Goal", "🥉", "Bankocu", 29800L, 74, 11, 8.90),
  LeaderboardUser(4, "EmreFutbol", "⚡", "Süper Lig Uzmanı", 24300L, 71, 9, 6.75),
  LeaderboardUser(5, "Caner_BetPro", "🎯", "Sürprizci", 21900L, 68, 8, 24.50),
  LeaderboardUser(6, "Mehmet_Corner", "⛳", "Korner & Kart", 19500L, 66, 7, 5.40),
  LeaderboardUser(7, "Selin_Basket", "🏀", "Euroleague Kralı", 17800L, 64, 8, 7.80),
  LeaderboardUser(8, "Ahmet_Score", "⚽", "KG & Gol", 16200L, 62, 6, 4.90),
  LeaderboardUser(9, "Kaan_GoalMachine", "🚀", "Kombine Ustası", 14750L, 60, 6, 9.15),
  LeaderboardUser(10, "Merve_Analiz", "📊", "Yapay Zeka Takipçisi", 13200L, 59, 5, 6.10)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardDialog(
  currentUserWonTP: Long,
  onCopyPunterCoupon: (LeaderboardUser) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFFF8FAFC),
    modifier = Modifier.testTag("leaderboard_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.92f)
    ) {
      // Header
      Surface(
        color = TealDark,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = GoldYellow,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "TOP 10",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = TealDark,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Haftalık Kazanan 10",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = Color.White
              )
              Text(
                text = "Bu haftanın en çok kazanan iddaacıları",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
          }
        }
      }

      // Podium Top 3 View
      Surface(
        color = Color(0xFF0F172A),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.Bottom
        ) {
          // #2 Silver
          PodiumColumn(
            user = LEADERBOARD_USERS[1],
            heightDp = 100,
            badgeColor = Color(0xFFCBD5E1),
            badgeLabel = "2"
          )

          // #1 Gold
          PodiumColumn(
            user = LEADERBOARD_USERS[0],
            heightDp = 125,
            badgeColor = GoldYellow,
            badgeLabel = "👑 1"
          )

          // #3 Bronze
          PodiumColumn(
            user = LEADERBOARD_USERS[2],
            heightDp = 90,
            badgeColor = Color(0xFFF97316),
            badgeLabel = "3"
          )
        }
      }

      // Rest of the list (Rank 4 to 10)
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        itemsIndexed(LEADERBOARD_USERS) { index, user ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                // Rank Circle
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                      when (user.rank) {
                        1 -> GoldYellow
                        2 -> Color(0xFFCBD5E1)
                        3 -> Color(0xFFFED7AA)
                        else -> Color(0xFFF1F5F9)
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "#${user.rank}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (user.rank <= 3) TealDark else Color(0xFF64748B)
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Avatar
                Text(text = user.avatarEmoji, fontSize = 20.sp)

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = user.username,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${user.titleBadge} • %${user.winRate} Başarı",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              // TP Won & Copy Button
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "+${user.totalWonTP} TP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF059669)
                  )
                  Text(
                    text = "${user.couponCount} Kupon",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                  )
                }

                OutlinedButton(
                  onClick = { onCopyPunterCoupon(user) },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.height(30.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp), tint = TealDark)
                  Spacer(modifier = Modifier.width(3.dp))
                  Text("Kopyala", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealDark)
                }
              }
            }
          }
        }
      }

      // User's own Rank Footer
      Surface(
        color = Color(0xFF1E293B),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(GoldYellow),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "⚡", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Senin Sıralaman: #42",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
              )
              Text(
                text = "Toplam Kazanç: $currentUserWonTP TP",
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          Text(
            text = "İlk 10'a 3,200 TP kaldı!",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = GoldYellow
          )
        }
      }
    }
  }
}

@Composable
private fun PodiumColumn(
  user: LeaderboardUser,
  heightDp: Int,
  badgeColor: Color,
  badgeLabel: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom
  ) {
    Text(text = user.avatarEmoji, fontSize = 24.sp)
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = user.username.take(9) + if (user.username.length > 9) ".." else "",
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White,
      textAlign = TextAlign.Center
    )
    Text(
      text = "+${user.totalWonTP} TP",
      fontSize = 9.sp,
      fontWeight = FontWeight.ExtraBold,
      color = GoldYellow
    )
    Spacer(modifier = Modifier.height(4.dp))

    // Podium Block
    Surface(
      color = badgeColor,
      shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
      modifier = Modifier
        .width(80.dp)
        .height(heightDp.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
      ) {
        Text(
          text = badgeLabel,
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = TealDark
        )
      }
    }
  }
}
