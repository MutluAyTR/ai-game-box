package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalNotificationScope
import com.example.service.PushNotificationHelper
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

/**
 * Kullanıcının sadece takip ettiği veya kuponuna eklediği maçlar için
 * özelleştirilmiş bildirim almasını sağlayan Gelişmiş Bildirim Ayarları Paneli.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsDialog(
  currentScope: GoalNotificationScope = GoalNotificationScope.ONLY_COUPON,
  onSaveScope: (GoalNotificationScope) -> Unit = {},
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current

  var selectedScope by remember { mutableStateOf(currentScope) }
  var notifyGoals by remember { mutableStateOf(true) }
  var notifyMatchStart by remember { mutableStateOf(true) }
  var notifyRedCards by remember { mutableStateOf(true) }
  var notifyCouponSettled by remember { mutableStateOf(true) }
  var notifyOddsChange by remember { mutableStateOf(false) }
  var enableSound by remember { mutableStateOf(true) }
  var enableVibration by remember { mutableStateOf(true) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFFF8FAFC),
    modifier = Modifier.testTag("notification_settings_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 18.dp, vertical = 8.dp)
        .padding(bottom = 28.dp)
    ) {
      // 1. Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color(0xFFE6FFFA)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.NotificationsActive,
              contentDescription = null,
              tint = TealDark,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Bildirim Ayarları Paneli",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "Kupon & Takip Bildirim Filtreleri",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF64748B))
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Kapsam Seçimi (Scope Selection)
      Text(
        text = "Hangi Maçlar İçin Bildirim Almak İstiyorsunuz?",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B)
      )
      Text(
        text = "Gereksiz spam bildirimleri engelleyin, sadece önem verdiğiniz maçları takip edin.",
        fontSize = 11.sp,
        color = Color(0xFF64748B)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Scope Option 1: Sadece Kuponumdaki Maçlar
      NotificationScopeCard(
        title = "🎯 Kuponumdaki Maçlar",
        subtitle = "Yalnızca aktif kuponunuza eklediğiniz maçlarda gol, kart ve sonuç bildirimi gelir (Sıfır spam).",
        badge = "ÖNERİLEN",
        badgeColor = Color(0xFF059669),
        isSelected = selectedScope == GoalNotificationScope.ONLY_COUPON,
        onClick = { selectedScope = GoalNotificationScope.ONLY_COUPON }
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Scope Option 2: Sadece Takip Ettiğim Maçlar (Favoriler)
      NotificationScopeCard(
        title = "⭐ Favori Karşılaşmalarım",
        subtitle = "Bültende yıldızladığınız favori takımlarınız ve maçlarınız için anlık uyarı alın.",
        badge = "FAVORİLER",
        badgeColor = GoldYellow,
        badgeTextColor = Color(0xFF78350F),
        isSelected = selectedScope == GoalNotificationScope.COUPON_AND_FAVORITES,
        onClick = { selectedScope = GoalNotificationScope.COUPON_AND_FAVORITES }
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Scope Option 3: Tüm Maçlar
      NotificationScopeCard(
        title = "🔔 Tüm Bülten Maçları",
        subtitle = "Tüm ligler ve karşılaşmalar için canlı gol ve skor bildirimleri alırsınız.",
        badge = "TÜMÜ",
        badgeColor = Color(0xFF64748B),
        isSelected = selectedScope == GoalNotificationScope.ALL_MATCHES,
        onClick = { selectedScope = GoalNotificationScope.ALL_MATCHES }
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Scope Option 4: Sessize Al
      NotificationScopeCard(
        title = "🔕 Bildirimleri Sessize Al",
        subtitle = "Hiçbir push bildirimi veya sesli gol uyarısı gönderilmez.",
        badge = "KAPALI",
        badgeColor = Color(0xFFEF4444),
        isSelected = selectedScope == GoalNotificationScope.MUTED,
        onClick = { selectedScope = GoalNotificationScope.MUTED }
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Olay Türü Tercihleri (Event Filter Preferences)
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Bildirim Olay Tercihleri",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
          Spacer(modifier = Modifier.height(8.dp))

          NotificationToggleRow(
            title = "⚽ Gol ve Basketbol Sayı Bildirimi",
            subtitle = "Skor değiştiğinde canlı gol bildirimi ve animasyonu",
            checked = notifyGoals,
            onCheckedChange = { notifyGoals = it }
          )

          NotificationToggleRow(
            title = "⏱️ Maç Başlangıç Uyarısı (5 dk önce)",
            subtitle = "Takip ettiğiniz veya kuponunuzdaki maç başlamadan önce hatırlatma",
            checked = notifyMatchStart,
            onCheckedChange = { notifyMatchStart = it }
          )

          NotificationToggleRow(
            title = "🟥 Kırmızı Kart & Kritik VAR Kararı",
            subtitle = "Maçın kaderini etkileyen kırmızı kartlarda bildirim al",
            checked = notifyRedCards,
            onCheckedChange = { notifyRedCards = it }
          )

          NotificationToggleRow(
            title = "🏆 Kupon Sonuçlandı (Kazandı / Kaybetti)",
            subtitle = "Kupon tuttuğunda anında kazanç tebriği ve TP aktarımı",
            checked = notifyCouponSettled,
            onCheckedChange = { notifyCouponSettled = it }
          )

          NotificationToggleRow(
            title = "⚡ Oran Değişimi & Kral Oran",
            subtitle = "Takipteki maçta oran yükseldiğinde anlık bildirim",
            checked = notifyOddsChange,
            onCheckedChange = { notifyOddsChange = it }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Ses ve Titreşim
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Ses & Titreşim",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
          Spacer(modifier = Modifier.height(8.dp))

          NotificationToggleRow(
            title = "🔊 Sesli Gol Sireni",
            subtitle = "Maçkolik orijinal gol sesi efekti çalsın",
            checked = enableSound,
            onCheckedChange = { enableSound = it }
          )

          NotificationToggleRow(
            title = "📳 Titreşim Uyarısı",
            subtitle = "Bildirim geldiğinde cihaz titresin",
            checked = enableVibration,
            onCheckedChange = { enableVibration = it }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 5. Test Bildirimi ve Kaydet Butonları
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          color = Color(0xFFF1F5F9),
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
          modifier = Modifier
            .weight(1f)
            .clickable {
              PushNotificationHelper.sendBetClosureAlert(
                context = context,
                matchName = "Galatasaray - Fenerbahçe",
                minutesRemaining = 5
              )
              Toast.makeText(context, "🔔 Test bildirimi gönderildi!", Toast.LENGTH_SHORT).show()
            }
        ) {
          Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Test Gönder", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
          }
        }

        Button(
          onClick = {
            onSaveScope(selectedScope)
            Toast.makeText(
              context,
              "✅ Bildirim tercihleriniz kaydedildi: ${selectedScope.label}",
              Toast.LENGTH_SHORT
            ).show()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TealDark),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1.3f)
        ) {
          Icon(Icons.Default.Done, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Ayarları Kaydet", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
      }
    }
  }
}

@Composable
private fun NotificationScopeCard(
  title: String,
  subtitle: String,
  badge: String,
  badgeColor: Color,
  badgeTextColor: Color = Color.White,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) Color(0xFFECFDF5) else Color.White,
    border = BorderStroke(
      width = if (isSelected) 1.8.dp else 1.dp,
      color = if (isSelected) Color(0xFF059669) else Color(0xFFE2E8F0)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      RadioButton(
        selected = isSelected,
        onClick = onClick,
        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF059669))
      )

      Spacer(modifier = Modifier.width(6.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = title,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color(0xFF065F46) else Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = badgeColor,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = badge,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Black,
              color = badgeTextColor,
              maxLines = 1,
              softWrap = false,
              letterSpacing = 0.5.sp,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontSize = 10.sp,
          color = Color(0xFF64748B),
          lineHeight = 14.sp
        )
      }
    }
  }
}

@Composable
private fun NotificationToggleRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF1E293B)
      )
      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = Color(0xFF64748B)
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = TealDark,
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = Color(0xFFCBD5E1)
      )
    )
  }
}
