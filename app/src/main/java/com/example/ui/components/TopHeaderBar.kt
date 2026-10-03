package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

/**
 * Persistent, professional TopAppBar displaying the app name 'TahminArena'
 * and a notification action icon. Remains fixed at the top while users scroll.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeaderBar(
  walletPoints: Long,
  turkeyTimeText: String = "",
  seasonWeekText: String = "5. Hafta",
  onClaimDaily: () -> Unit,
  onOpenProfile: () -> Unit,
  onOpenVoiceAssistant: () -> Unit = {},
  onOpenLuckyWheel: () -> Unit = {},
  onOpenSettings: () -> Unit = {},
  onOpenNotificationSettings: () -> Unit = {},
  onOpenAdminPanel: () -> Unit = {},
  onOpenLiveDraw: () -> Unit = {},
  onOpenLiveStandings: () -> Unit = {}
) {
  Surface(
    color = TealDark,
    shadowElevation = 3.dp,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("persistent_top_app_bar")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Brand Logo & Title
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable { onOpenProfile() }
            .testTag("app_brand_title")
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(GoldYellow),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "⚡",
              fontSize = 14.sp
            )
          }
          Spacer(modifier = Modifier.width(7.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Tahmin",
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              color = Color.White,
              letterSpacing = 0.2.sp
            )
            Text(
              text = "Arena",
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              color = GoldYellow,
              letterSpacing = 0.2.sp
            )
          }
        }

        // Actions: Wallet Pill & Notification & Profile
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Kura Çekimi Button
          Surface(
            color = Color(0xFF1E3A8A),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFF3B82F6)),
            modifier = Modifier
              .clickable { onOpenLiveDraw() }
              .testTag("top_bar_kura_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "🎲", fontSize = 11.sp)
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Kura",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1,
                softWrap = false
              )
            }
          }

          Spacer(modifier = Modifier.width(5.dp))

          // D3 Puan Durumu Button
          Surface(
            color = Color(0xFF0F766E),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFF14B8A6)),
            modifier = Modifier
              .clickable { onOpenLiveStandings() }
              .testTag("top_bar_d3_standings_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "📊", fontSize = 11.sp)
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "D3 Tablo",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1,
                softWrap = false
              )
            }
          }

          Spacer(modifier = Modifier.width(5.dp))

          // Wallet TP Points pill (Interactive 1-tap bonus)
          Surface(
            color = Color(0xFF0F3A3D),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFF1E585B)),
            modifier = Modifier
              .clickable { onClaimDaily() }
              .testTag("top_bar_wallet_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "🪙", fontSize = 11.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "$walletPoints TP",
                color = GoldYellow,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Notification Icon with unread badge dot
          IconButton(
            onClick = { onOpenNotificationSettings() },
            modifier = Modifier
              .size(36.dp)
              .testTag("top_app_bar_notifications_button")
          ) {
            BadgedBox(
              badge = {
                Badge(
                  containerColor = LiveRed,
                  modifier = Modifier.size(6.dp)
                )
              }
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = GoldYellow,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          // User Profile Avatar Button
          IconButton(
            onClick = { onOpenProfile() },
            modifier = Modifier
              .size(36.dp)
              .testTag("top_app_bar_profile_button")
          ) {
            Icon(
              imageVector = Icons.Default.AccountCircle,
              contentDescription = "Profile",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }
    }
  }
}
