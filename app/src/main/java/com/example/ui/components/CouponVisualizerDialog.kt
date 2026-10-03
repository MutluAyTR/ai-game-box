package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SlipSelection
import com.example.data.model.Ticket
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.util.formatOdd
import com.example.util.formatTp

enum class VisualizerTheme(val title: String, val bgColors: List<Color>, val accentColor: Color) {
  DARK_PRO("Zümrüt Pro", listOf(Color(0xFF0F172A), Color(0xFF072628), Color(0xFF0F172A)), GoldYellow),
  GOLD_CARBON("Kral Karbon", listOf(Color(0xFF1C1917), Color(0xFF292524), Color(0xFF0C0A09)), Color(0xFFF59E0B)),
  MIDNIGHT_BLUE("Gece Mavisi", listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0F172A)), Color(0xFF38BDF8))
}

/**
 * Kupon Görselleştirici Modülü:
 * Hazırlanan kuponları sosyal medyada (WhatsApp, Telegram, X/Twitter, Instagram)
 * veya Kupondaş topluluğunda paylaşmak için yüksek kaliteli görsel kart oluşturur.
 * Kart üzerinde toplam oran ve potansiyel kazanç TP bilgisi öne çıkarılır.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponVisualizerDialog(
  ticket: Ticket? = null,
  selections: List<SlipSelection> = emptyList(),
  stakePoints: Long = 100L,
  totalOdds: Double = 1.0,
  onShareToKupondas: (Ticket) -> Unit = {},
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current

  var currentTheme by remember { mutableStateOf(VisualizerTheme.DARK_PRO) }

  // Resolve values whether called from a completed Ticket or from active BetSlip
  val effectiveSelections = ticket?.selections ?: selections
  val effectiveStake = ticket?.stakePoints ?: stakePoints
  val effectiveOdds = ticket?.totalOdds ?: totalOdds
  val potentialWinPoints = ticket?.potentialPoints ?: (effectiveStake * effectiveOdds).toLong()
  val couponCode = ticket?.ticketNumber ?: "KPN-78219"

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF0F172A),
    modifier = Modifier.testTag("coupon_visualizer_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .padding(bottom = 32.dp)
    ) {
      // 1. Modal Top Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(TealDark),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Kupon Görselleştirici",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = "Sosyal Medya & Kupondaş Paylaşım Kartı",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Tema Değiştirici Butonları
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        VisualizerTheme.values().forEach { theme ->
          val isSel = currentTheme == theme
          Surface(
            color = if (isSel) theme.accentColor else Color(0xFF1E293B),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .clickable { currentTheme = theme }
          ) {
            Text(
              text = theme.title,
              color = if (isSel) Color(0xFF0F172A) else Color(0xFFCBD5E1),
              fontSize = 11.sp,
              fontWeight = if (isSel) FontWeight.Black else FontWeight.Bold,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 7.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. PAYLAŞILABİLİR KUPON KART GÖRSELİ (THE VISUAL SHARE CARD)
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(2.dp, currentTheme.accentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(currentTheme.bgColors))
            .padding(16.dp)
        ) {
          // Kart Başlığı & Resmi Doğrulama
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(currentTheme.accentColor)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "TAHMİN",
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp,
                  color = Color(0xFF0F172A)
                )
              }
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "ARENA",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = Color.White
              )
            }

            Surface(
              color = Color(0x33000000),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(1.dp, currentTheme.accentColor.copy(alpha = 0.6f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = currentTheme.accentColor, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "RESMİ BİLET",
                  color = Color.White,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Kupon Kodu & Başlık
          Text(
            text = "🔥 GÜNÜN KRAL BANKO KOMBİNESİ",
            color = currentTheme.accentColor,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
          Text(
            text = "Kupon No: #$couponCode • 27 Eylül 2026 TSİ",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Kesikli Ayraç Çizgisi
          Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
            drawLine(
              color = Color(0xFF475569),
              start = Offset(0f, 0f),
              end = Offset(size.width, 0f),
              strokeWidth = 1.5f,
              pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Kupondaki Maçlar Listesi
          effectiveSelections.take(5).forEachIndexed { index, sel ->
            Surface(
              color = Color(0x22FFFFFF),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = sel.matchTeams,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "Tahmin: ${sel.selectionName} (${sel.marketType})",
                    color = Color(0xFFCBD5E1),
                    fontSize = 9.sp
                  )
                }

                Surface(
                  color = currentTheme.accentColor,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = sel.odd.formatOdd(),
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }

          if (effectiveSelections.size > 5) {
            Text(
              text = "+${effectiveSelections.size - 5} diğer maç daha...",
              color = Color(0xFF94A3B8),
              fontSize = 9.sp,
              modifier = Modifier.padding(top = 2.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // ÖNE ÇIKAN BÜYÜK METRİKLER (TOPLAM ORAN & POTANSİYEL KAZANÇ TP)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0B132B), RoundedCornerShape(12.dp))
              .border(1.5.dp, currentTheme.accentColor, RoundedCornerShape(12.dp))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // TOPLAM ORAN
            Column {
              Text(
                text = "TOPLAM ORAN",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
              )
              Text(
                text = effectiveOdds.formatOdd(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = currentTheme.accentColor
              )
              Text(
                text = "Yatırılan: ${effectiveStake.formatTp()} TP",
                fontSize = 9.sp,
                color = Color(0xFFCBD5E1)
              )
            }

            // POTANSİYEL KAZANÇ TP (GÖZ ALICI ROZET)
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "POTANSİYEL KAZANÇ",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
              )
              Surface(
                color = Color(0xFF059669),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "+${potentialWinPoints.formatTp()} TP",
                  color = Color.White,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
              Text(
                text = "TahminArena Sanal Kasa",
                fontSize = 8.sp,
                color = Color(0xFF10B981)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Alt Barkod & QR Şeridi
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(text = "tahminarena://kupon?code=$couponCode", color = Color(0xFF94A3B8), fontSize = 8.sp)
                Text(text = "Doğrulanmış Bülten Kuponu", color = Color(0xFFCBD5E1), fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }

            Text(text = "⭐ VIP Analist Kuponu", color = GoldYellow, fontSize = 9.sp, fontWeight = FontWeight.Black)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. PAYLAŞIM EYLEM BUTONLARI
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Sosyal Medya Paylaş Butonu (WhatsApp / Telegram / X / Instagram)
        Button(
          onClick = {
            val shareText = """
              🔥 Tahmin Arena'da Günün Kuponu!
              📋 Kupon Kodu: #$couponCode
              🎯 Toplam Oran: ${effectiveOdds.formatOdd()}
              💰 Potansiyel Kazanç: +${potentialWinPoints.formatTp()} TP
              🔗 Kupon Bağlantısı: tahminarena://kupon?code=$couponCode
              Sen de Tahmin Arena'ya katıl, sanal TP ile yarış!
            """.trimIndent()

            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, shareText)
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Kuponu Paylaş"))
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Sosyal Medyada Paylaş", fontSize = 11.sp, fontWeight = FontWeight.Black)
        }

        // Kupondaş'ta Paylaş Butonu
        Button(
          onClick = {
            if (ticket != null) {
              onShareToKupondas(ticket)
            }
            Toast.makeText(context, "✅ Kupon Kupondaş Topluluğu'nda paylaşıldı!", Toast.LENGTH_SHORT).show()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TealDark),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Verified, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Kupondaş'ta Paylaş", fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Panoya Kopyala Butonu
      Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Kupon Deep-Link", "tahminarena://kupon?code=$couponCode&odds=${effectiveOdds.formatOdd()}")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "📋 Kupon bağlantısı ve kodu panoya kopyalandı!", Toast.LENGTH_SHORT).show()
          }
      ) {
        Row(
          modifier = Modifier.padding(vertical = 10.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Kupon Bağlantısını Panoya Kopyala", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
