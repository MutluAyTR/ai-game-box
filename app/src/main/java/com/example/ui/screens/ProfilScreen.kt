package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.local.WalletEntity
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import com.example.data.model.UserProfile
import com.example.data.model.UserWallet
import com.example.data.model.WalletTransaction
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfilScreen(
  userProfile: UserProfile = UserProfile(),
  userWallet: UserWallet? = null,
  walletEntity: WalletEntity? = null,
  tickets: List<Ticket>,
  transactions: List<TransactionEntity>,
  onClaimDaily: () -> Unit,
  onDeposit: (Long) -> Unit = {},
  onOpenBetHistory: () -> Unit = {},
  onOpenLeaderboard: () -> Unit = {},
  onOpenSettings: () -> Unit = {},
  onOpenAuthDialog: () -> Unit = {},
  onOpenAdminPanel: () -> Unit = {},
  onLogout: () -> Unit = {}
) {
  val availableTP = userWallet?.availablePoints ?: walletEntity?.availablePoints ?: 10000L
  val lockedTP = userWallet?.lockedPoints ?: walletEntity?.lockedPoints ?: 0L
  val lifetimeWonTP = userWallet?.lifetimeWon ?: walletEntity?.lifetimeWon ?: 0L
  val totalBets = tickets.size
  val wonBets = tickets.count { it.status == TicketStatus.WON }
  val winRate = if (totalBets > 0) ((wonBets.toDouble() / totalBets) * 100).toInt() else 0

  val rank = userWallet?.rankTitle ?: when {
    wonBets >= 10 -> "Level 4: Uzman Analist ⭐"
    wonBets >= 5 -> "Level 3: Profesyonel 🏆"
    wonBets >= 2 -> "Level 2: Yarı Pro ⚽"
    else -> "Level 1: Çaylak Tahminci 🌱"
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("profil_screen")
  ) {
    // 1. Profile Header Card
    item {
      Surface(
        color = TealDark,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            IconButton(
              onClick = onOpenSettings,
              modifier = Modifier.testTag("profile_settings_button")
            ) {
              Icon(Icons.Default.Settings, contentDescription = "Ayarlar", tint = Color.White)
            }
          }

          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(GoldYellow),
            contentAlignment = Alignment.Center
          ) {
            Text(text = userProfile.avatarUrl, fontSize = 34.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = userProfile.fullName,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Text(
            text = "@${userProfile.username} • ${userProfile.email}",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(6.dp))

          Surface(
            color = Color(0xFF1E585B),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = rank,
              fontSize = 12.sp,
              color = GoldYellow,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    // 2. TP Wallet Balance & Actions Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Kullanılabilir Bakiye",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
              )
              Text(
                text = "$availableTP TP",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = TealDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (lockedTP > 0) {
                Text(
                  text = "Devam Eden Kuponlarda: $lockedTP TP",
                  fontSize = 11.sp,
                  color = Color(0xFFD97706),
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = onClaimDaily,
                colors = ButtonDefaults.buttonColors(
                  containerColor = GoldYellow,
                  contentColor = TealDark
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CardGiftcard,
                  contentDescription = "Bonus",
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "+250",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1
                )
              }

              Button(
                onClick = { onDeposit(1000L) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = TealDark,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text(
                  text = "+1000 TP",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Mini statistics grid with strict clipping to prevent text overflow
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            StatItem(title = "Toplam Kupon", value = "$totalBets", modifier = Modifier.weight(1f))
            StatItem(title = "Kazanan", value = "$wonBets", modifier = Modifier.weight(1f))
            StatItem(title = "Başarı Oranı", value = "%$winRate", modifier = Modifier.weight(1f))
            StatItem(title = "Kazanılan TP", value = "$lifetimeWonTP", modifier = Modifier.weight(1f))
          }
        }
      }
    }

    // 3. AI Predictive Level & Badges
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f, fill = false),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Yapay Zeka Analist İlerlemesi",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "%$winRate Doğruluk",
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = Color(0xFF0D9488),
              maxLines = 1,
              softWrap = false
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          LinearProgressIndicator(
            progress = { (winRate / 100f).coerceIn(0.05f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF0D9488),
            trackColor = Color(0xFFE2E8F0)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            BadgeCard(title = "İlk Kupon", emoji = "🎯", isUnlocked = totalBets >= 1, modifier = Modifier.weight(1f))
            BadgeCard(title = "Yapay Zeka", emoji = "🧠", isUnlocked = true, modifier = Modifier.weight(1f))
            BadgeCard(title = "Şampiyon", emoji = "🏆", isUnlocked = wonBets >= 1, modifier = Modifier.weight(1f))
          }
        }
      }
    }

    // 3.5 Recharts-style Betting Performance Chart
    item {
      com.example.ui.components.BettingPerformanceChart(
        tickets = tickets,
        currentBalance = availableTP,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
      )
    }

    // 3.6 Cloud Backup & Sync Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Text(text = "☁️", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Bulut Yedekleme & Firebase",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF1E3A8A)
              )
              Text(
                text = "Sanal TP bakiyenizi ve kuponlarınızı buluta senkronize edin.",
                fontSize = 10.sp,
                color = Color(0xFF3B82F6)
              )
            }
          }
          Button(
            onClick = onOpenAuthDialog,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("cloud_sync_button")
          ) {
            Text(text = "Eşitle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 4. Sanal Cüzdan Hareketleri (Ledger)
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Sanal Cüzdan Hareketleri (Ledger)",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Color(0xFF1E293B),
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Spacer(modifier = Modifier.height(6.dp))
    }

    if (transactions.isEmpty()) {
      item {
        Text(
          text = "Henüz işlem geçmişi bulunmuyor.",
          color = Color(0xFF94A3B8),
          fontSize = 12.sp,
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }
    } else {
      items(transactions) { tx ->
        TransactionRow(tx = tx)
      }
    }

    // 5. Account Actions (Bahis Geçmişi, Liderlik Tablosu, Ayarlar & Çıkış Yap)
    item {
      Spacer(modifier = Modifier.height(12.dp))
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onOpenBetHistory,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_open_bet_history"),
          colors = ButtonDefaults.buttonColors(
            containerColor = TealDark,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "📋 Bahis Geçmişi (Bet History)", fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onOpenLeaderboard,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_open_leaderboard"),
          colors = ButtonDefaults.buttonColors(
            containerColor = GoldYellow,
            contentColor = TealDark
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = TealDark, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "🏆 TP Liderlik Tablosu", fontWeight = FontWeight.Black)
        }

        Button(
          onClick = onOpenAdminPanel,
          modifier = Modifier.fillMaxWidth().testTag("open_admin_panel_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD97706),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("🛠️ Canlı Maç Yönetimi & Admin Paneli", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onOpenSettings,
          modifier = Modifier.fillMaxWidth().testTag("open_settings_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = TealDark
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Uygulama & Bildirim Ayarları", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onLogout,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Hesap / Giriş Ekranına Dön", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // 6. Yasal Bilgilendirme
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Bilgi",
            tint = Color(0xFFB45309),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Yasal Bilgilendirme: Bu uygulama tamamen simülasyon ve analiz amaçlıdır. Tahmin Puanı (TP) sanal bir puan olup nakde dönüştürülemez ve gerçek para ile oynanmaz.",
            fontSize = 11.sp,
            color = Color(0xFF78350F),
            lineHeight = 15.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(84.dp))
    }
  }
}

@Composable
private fun StatItem(title: String, value: String, modifier: Modifier = Modifier) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Text(
      text = title,
      fontSize = 10.sp,
      color = Color(0xFF64748B),
      maxLines = 1,
      softWrap = false,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      text = value,
      fontSize = 14.sp,
      fontWeight = FontWeight.Black,
      color = TealDark,
      maxLines = 1,
      softWrap = false,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
private fun BadgeCard(
  title: String,
  emoji: String,
  isUnlocked: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isUnlocked) Color(0xFFF0FDF4) else Color(0xFFE2E8F0),
    border = if (isUnlocked) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)) else null,
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = emoji, fontSize = 20.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = if (isUnlocked) Color(0xFF166534) else Color(0xFF94A3B8),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun TransactionRow(tx: TransactionEntity) {
  val dateStr = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date(tx.createdAt))
  val isPositive = tx.amount > 0

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 3.dp),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = tx.description,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF1E293B),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = dateStr,
          fontSize = 10.sp,
          color = Color(0xFF94A3B8)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = if (isPositive) "+${tx.amount} TP" else "${tx.amount} TP",
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = if (isPositive) Color(0xFF059669) else Color(0xFFDC2626),
        maxLines = 1
      )
    }
  }
}
