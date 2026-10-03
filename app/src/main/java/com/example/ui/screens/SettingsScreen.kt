package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.PushNotificationHelper
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import com.example.data.model.GoalNotificationScope
import com.example.ui.components.AdNetworkSelectorCard
import com.example.util.formatTp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  walletPoints: Long = 10000L,
  goalNotificationScope: GoalNotificationScope = GoalNotificationScope.ONLY_COUPON,
  onSelectGoalNotificationScope: (GoalNotificationScope) -> Unit = {},
  onRechargeBalance: (Long) -> Unit = {},
  onClaimDailyBonus: () -> Unit = {},
  onWatchRewardedAd: () -> Unit = {},
  onResetBalance: () -> Unit = {},
  onOpenMissions: () -> Unit = {},
  onOpenSanalOyunlar: () -> Unit = {},
  onBack: () -> Unit,
  onLogout: () -> Unit
) {
  val context = LocalContext.current

  var notify5MinBeforeClosure by remember { mutableStateOf(true) }
  var notifyMatchResults by remember { mutableStateOf(true) }
  var notifyLiveGoals by remember { mutableStateOf(true) }
  var enableVoiceAssistantTts by remember { mutableStateOf(true) }
  var enableGoalSiren by remember { mutableStateOf(true) }
  var autoAcceptOddsChange by remember { mutableStateOf(true) }
  var defaultStake by remember { mutableLongStateOf(100L) }
  var oddsFormat by remember { mutableStateOf("Ondalık (1.85)") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("settings_screen")
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
                text = "AYARLAR & CÜZDAN YÖNETİMİ",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = Color.White
              )
              Text(
                text = "Sanal para bakiyesi, bildirimler ve sistem kontrolleri",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }
        }
      }
    }

    // 1.5. PRIMARY SECTION: Sanal Para Cüzdanı & Bakiye Yönetimi (Moved to Settings)
    item {
      SectionTitle(title = "Sanal Para Cüzdanı & TP Bakiyesi", icon = Icons.Default.AccountBalanceWallet)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .testTag("wallet_settings_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Balance Display Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Kullanılabilir Bakiye",
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(2.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${walletPoints.formatTp()} TP",
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Black,
                  color = TealDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  color = Color(0xFFFEF3C7),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = "🏆 VIP Seviye 4",
                    color = Color(0xFFB45309),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }

            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(GoldYellow),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🪙", fontSize = 22.sp)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Quick Recharge Row
          Text(
            text = "Hızlı Sanal TP Yükle (Ücretsiz):",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(1000L, 5000L, 25000L, 100000L).forEach { amount ->
              Surface(
                color = Color(0xFFE6FFFA),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF99F6E4)),
                modifier = Modifier
                  .weight(1f)
                  .clickable {
                    onRechargeBalance(amount)
                    Toast.makeText(context, "+${amount.formatTp()} TP kasanıza eklendi!", Toast.LENGTH_SHORT).show()
                  }
              ) {
                Text(
                  text = "+${amount.formatTp()}",
                  color = Color(0xFF0F766E),
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                  modifier = Modifier.padding(vertical = 8.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Action Buttons: Daily bonus, Rewarded Ad, Reset
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                onClaimDailyBonus()
                Toast.makeText(context, "🎁 Günlük bonus +2.500 TP alındı!", Toast.LENGTH_SHORT).show()
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Günlük Bonus (+2.5k)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = onWatchRewardedAd,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Reklam İzle (+500 TP)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Reset Balance Button
          OutlinedButton(
            onClick = {
              onResetBalance()
              Toast.makeText(context, "Bakiye 10.000 TP'ye sıfırlandı!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF64748B))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Bakiyeyi Sıfırla (10.000 TP Başlangıç)", fontSize = 11.sp, color = Color(0xFF475569))
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Navigation Links to Missions and Sanal Oyunlar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .clickable { onOpenMissions() }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "🎯", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Günlük Görevler", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
              }
            }

            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .clickable { onOpenSanalOyunlar() }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "🎮", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Sanal Oyunlar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
              }
            }
          }
        }
      }
    }

    // 1.8 Multi-Ad Mediation Provider Selector
    item {
      AdNetworkSelectorCard()
    }

    // 2. Section: Push Bildirimleri (Notification Settings)
    item {
      SectionTitle(title = "Push Bildirimleri", icon = Icons.Default.Notifications)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          SettingSwitchRow(
            title = "5 Dakika Kapanış Uyarısı",
            description = "Kuponunuzdaki maç başlamadan 5 dakika önce uyarı gönderilsin",
            checked = notify5MinBeforeClosure,
            onCheckedChange = { notify5MinBeforeClosure = it }
          )

          Spacer(modifier = Modifier.height(10.dp))

          SettingSwitchRow(
            title = "Maç Sonu & Kupon Sonucu",
            description = "Simüle edilen maçlar sonuçlandığında ve kupon tuttuğunda bildirim al",
            checked = notifyMatchResults,
            onCheckedChange = { notifyMatchResults = it }
          )

          Spacer(modifier = Modifier.height(10.dp))

          SettingSwitchRow(
            title = "Canlı Gol Bildirimleri",
            description = "Gol olduğunda ekranda ve sesli uyarı göster",
            checked = notifyLiveGoals,
            onCheckedChange = { notifyLiveGoals = it }
          )

          if (notifyLiveGoals) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Gol Bildirimi Filtre Kapsamı:",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
            Text(
              text = "Ana menü ve ekrandaki gol bildirimlerinin hangi maçlarda gösterileceğini seçin",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(6.dp))

            GoalNotificationScope.values().forEach { scope ->
              val isSelected = goalNotificationScope == scope
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(
                  width = if (isSelected) 1.5.dp else 1.dp,
                  color = if (isSelected) Color(0xFF059669) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp)
                  .clickable { onSelectGoalNotificationScope(scope) }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = isSelected,
                    onClick = { onSelectGoalNotificationScope(scope) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF059669))
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Column {
                    Text(
                      text = scope.label,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) Color(0xFF065F46) else Color(0xFF334155)
                    )
                    val desc = when (scope) {
                      GoalNotificationScope.ONLY_COUPON -> "Sadece yaptığınız kuponda yer alan maçlarda gol olduğunda bildirim ve kutlama gösterilir (Spam yapmaz)."
                      GoalNotificationScope.COUPON_AND_FAVORITES -> "Kuponunuzdaki maçlar ve yıldızladığınız favori maçlar için bildirim verir."
                      GoalNotificationScope.ALL_MATCHES -> "Bültendeki tüm maçlar için sürekli bildirim gelir."
                      GoalNotificationScope.MUTED -> "Gol bildirimlerini ve kutlamaları tamamen sessize al."
                    }
                    Text(text = desc, fontSize = 10.sp, color = Color(0xFF64748B))
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Test Notification Button
          Button(
            onClick = {
              PushNotificationHelper.sendBetClosureAlert(
                context = context,
                matchName = "Galatasaray - Fenerbahçe",
                minutesRemaining = 5
              )
              Toast.makeText(context, "🔔 Test bildirim gönderildi!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Örnek 5 Dakika Bildirimi Gönder", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 3. Section: Bahis & Oran Ayarları
    item {
      SectionTitle(title = "Sanal Bahis & Oran Tercihleri", icon = Icons.Default.Tune)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "Varsayılan Hızlı Bahis Tutarı (TP)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(50L, 100L, 250L, 500L).forEach { stake ->
              val isSelected = defaultStake == stake
              Surface(
                color = if (isSelected) TealDark else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .clickable { defaultStake = stake }
              ) {
                Text(
                  text = "$stake TP",
                  color = if (isSelected) Color.White else Color(0xFF334155),
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(vertical = 8.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          SettingSwitchRow(
            title = "Oran Değişikliklerini Otomatik Onayla",
            description = "Kupon yatırılırken oran artış ve düşüşlerinde onay sormadan kuponu oluştur",
            checked = autoAcceptOddsChange,
            onCheckedChange = { autoAcceptOddsChange = it }
          )
        }
      }
    }

    // 4. Section: Ses & AI Spiker
    item {
      SectionTitle(title = "Ses & Yapay Zeka Spiker", icon = Icons.AutoMirrored.Filled.VolumeUp)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          SettingSwitchRow(
            title = "Yapay Zeka Sesli Tahmin Spikeri (TTS)",
            description = "AI analizleri ve tahmin sonuçlarını Türkçe seslendir",
            checked = enableVoiceAssistantTts,
            onCheckedChange = { enableVoiceAssistantTts = it }
          )

          Spacer(modifier = Modifier.height(10.dp))

          SettingSwitchRow(
            title = "Gol Sireni & Efektler",
            description = "Canlı simülasyonda gol olduğunda kutlama sesi ve animasyon çalıştır",
            checked = enableGoalSiren,
            onCheckedChange = { enableGoalSiren = it }
          )
        }
      }
    }

    // 5. Section: Hesap & Veri Yönetimi
    item {
      SectionTitle(title = "Hesap & Sistem", icon = Icons.Default.Info)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                Toast.makeText(context, "Uygulama önbelleği temizlendi!", Toast.LENGTH_SHORT).show()
              }
              .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color(0xFF64748B))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Önbelleği Temizle", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
            }
            Text("14.2 MB", fontSize = 12.sp, color = Color(0xFF94A3B8))
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Uygulama Sürümü", fontSize = 13.sp, color = Color(0xFF64748B))
            Text("v2.5.0 Pro (Build 2026.09)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealDark)
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedButton(
            onClick = {
              onLogout()
              Toast.makeText(context, "Oturum kapatıldı.", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Çıkış Yap / Giriş Sayfasına Git", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
private fun SectionTitle(title: String, icon: ImageVector) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = TealDark, modifier = Modifier.size(16.dp))
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      color = Color(0xFF64748B),
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
private fun SettingSwitchRow(
  title: String,
  description: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = Color(0xFF1E293B)
      )
      Text(
        text = description,
        fontSize = 11.sp,
        color = Color(0xFF64748B),
        lineHeight = 15.sp
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = TealDark
      )
    )
  }
}
