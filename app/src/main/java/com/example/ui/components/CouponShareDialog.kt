package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Ticket
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

@Composable
fun CouponShareDialog(
  ticket: Ticket,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("coupon_share_dialog")
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        // Top Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "📢", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Kuponu Paylaş",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "Kupondaş veya sosyal medyada arkadaşlarınla paylaş",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
              )
            }
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF64748B))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Coupon Card Preview
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF0A2224),
          border = BorderStroke(1.5.dp, GoldYellow),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "TAHMİN ARENA KUPONU",
                color = GoldYellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
              Text(
                text = "#${ticket.ticketNumber}",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ticket.selections.take(4).forEach { sel ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "• ${sel.matchTeams}",
                  color = Color(0xFFE2E8F0),
                  fontSize = 11.sp,
                  maxLines = 1,
                  modifier = Modifier.weight(1f)
                )
                Text(
                  text = "${sel.selectionName} (%.2f)".format(sel.odd),
                  color = GoldYellow,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            if (ticket.selections.size > 4) {
              Text(
                text = "+${ticket.selections.size - 4} maç daha...",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "Toplam Oran", fontSize = 9.sp, color = Color(0xFF94A3B8), maxLines = 1, softWrap = false)
                Text(
                  text = "%.2f".format(ticket.totalOdds),
                  color = GoldYellow,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  maxLines = 1,
                  softWrap = false
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(text = "Potansiyel Kazanç", fontSize = 9.sp, color = Color(0xFF94A3B8), maxLines = 1, softWrap = false)
                Text(
                  text = "${ticket.potentialPoints} TP",
                  color = Color(0xFF4ADE80),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Button(
          onClick = {
            val shareText = buildString {
              appendLine("🎯 TahminArena'da yeni kupon yaptım!")
              appendLine("Kupon No: #${ticket.ticketNumber}")
              appendLine("Maç Sayısı: ${ticket.selections.size}")
              appendLine("Toplam Oran: %.2f".format(ticket.totalOdds))
              appendLine("Potansiyel Kazanç: ${ticket.potentialPoints} TP")
              appendLine("Hemen TahminArena'yı aç ve kuponu kopyala!")
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_SUBJECT, "TahminArena Kupon #${ticket.ticketNumber}")
              putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Kuponu Paylaş"))
          },
          colors = ButtonDefaults.buttonColors(containerColor = TealDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "WhatsApp & Sosyal Medyada Paylaş", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Kupon Kodu", ticket.ticketNumber)
            clipboard?.setPrimaryClip(clip)
            android.widget.Toast.makeText(context, "Kupon kodu kopyalandı: #${ticket.ticketNumber}", android.widget.Toast.LENGTH_SHORT).show()
          },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Kupon Kodunu Kopyala", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
