package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DailyMissionEntity
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.formatTp

/**
 * Daily Missions screen:
 * Displays user's daily missions persisted in Room, allowing users to earn bonus virtual balance
 * by completing tasks like 'Place 3 bets' or 'Bet on 2 different sports'.
 */
@Composable
fun DailyMissionsScreen(
  missions: List<DailyMissionEntity>,
  walletPoints: Long,
  onClaimReward: (String) -> Unit,
  onBack: () -> Unit
) {
  val completedCount = missions.count { it.isCompleted }
  val totalRewardPossible = missions.sumOf { it.rewardTp }
  val claimedRewardTotal = missions.filter { it.isClaimed }.sumOf { it.rewardTp }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("daily_missions_screen")
  ) {
    // 1. Header Bar
    item {
      Surface(
        color = TealDark,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "GÜNLÜK GÖREVLER & ÖDÜL MERKEZİ",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color.White
              )
              Text(
                text = "Görevleri tamamla, kasana ekstra sanal TP bakiye ekle",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }
        }
      }
    }

    // 2. Summary Dashboard Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFF0F172A), Color(0xFF1E293B))
              )
            )
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "GÜNLÜK İLERLEME",
                color = GoldYellow,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "$completedCount / ${missions.size} Görev Tamamlandı",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            }

            Surface(
              color = Color(0xFF334155),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Mevcut: ${walletPoints.formatTp()} TP",
                color = Color(0xFF86EFAC),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Overall Progress
          val overallProgress = if (missions.isNotEmpty()) completedCount.toFloat() / missions.size else 0f
          LinearProgressIndicator(
            progress = { overallProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = GoldYellow,
            trackColor = Color(0xFF334155)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Kazanılan: +${claimedRewardTotal.formatTp()} TP",
              color = Color(0xFF86EFAC),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Toplam Havuz: +${totalRewardPossible.formatTp()} TP",
              color = Color(0xFF94A3B8),
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // 3. Missions List
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "BUGÜNKÜ GÖREVLER (ROOM DB)",
          fontSize = 12.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF64748B),
          letterSpacing = 0.5.sp
        )
        Text(
          text = "Her gece 00:00'da yenilenir",
          fontSize = 10.sp,
          color = Color(0xFF94A3B8)
        )
      }
    }

    items(missions, key = { it.id }) { mission ->
      DailyMissionCard(
        mission = mission,
        onClaim = { onClaimReward(mission.id) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
private fun DailyMissionCard(
  mission: DailyMissionEntity,
  onClaim: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 5.dp)
      .testTag("mission_item_${mission.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (mission.isClaimed) Color(0xFFF8FAFC) else Color.White
    ),
    border = if (mission.isCompleted && !mission.isClaimed) {
      androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF059669))
    } else {
      androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    },
    elevation = CardDefaults.cardElevation(defaultElevation = if (mission.isClaimed) 0.dp else 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                when {
                  mission.isClaimed -> Color(0xFFE2E8F0)
                  mission.isCompleted -> Color(0xFFDCFCE7)
                  else -> Color(0xFFFEF3C7)
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(text = mission.icon, fontSize = 18.sp)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = mission.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (mission.isClaimed) Color(0xFF64748B) else Color(0xFF0F172A)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = mission.category,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF475569),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = mission.description,
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        // Reward Badge
        Surface(
          color = if (mission.isClaimed) Color(0xFFE2E8F0) else Color(0xFFFEF9C3),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "+${mission.rewardTp.formatTp()} TP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = if (mission.isClaimed) Color(0xFF64748B) else Color(0xFF854D0E),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress bar & Claim Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          val progressRatio = (mission.currentProgress.toFloat() / mission.targetProgress).coerceIn(0f, 1f)
          LinearProgressIndicator(
            progress = { progressRatio },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = if (mission.isCompleted) Color(0xFF059669) else TealDark,
            trackColor = Color(0xFFE2E8F0)
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = "${mission.currentProgress} / ${mission.targetProgress} Tamamlandı",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        when {
          mission.isClaimed -> {
            Surface(
              color = Color(0xFFE2E8F0),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Alındı",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF475569)
                )
              }
            }
          }
          mission.isCompleted -> {
            Button(
              onClick = onClaim,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("claim_btn_${mission.id}")
            ) {
              Text(
                text = "Ödülü Al 🎁",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }
          }
          else -> {
            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Devam Ediyor",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }
  }
}
