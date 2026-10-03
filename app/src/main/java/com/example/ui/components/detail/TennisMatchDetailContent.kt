package com.example.ui.components.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.ui.theme.GoldYellow

/**
 * Specialized Tennis Match Detail view:
 * 1. 2D Tennis Court representation with serving indicator and net
 * 2. Real-time Set-by-Set and Game Scoreboards (e.g. 15-30, Deuce)
 * 3. Serve & Return statistics (Aces, Double Faults, 1st Serve %, Break Points)
 */
@Composable
fun TennisMatchDetailContent(
  match: Match
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Canlı Set Skor Tablosu
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("🎾 CANLI SETLER VE OYUN SKORU", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
          Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
            Text("Mevcut: 40 - 30", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Set Skorları Tablosu
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A), shape = RoundedCornerShape(6.dp))
            .padding(vertical = 5.dp, horizontal = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("RAKET", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
          Text("1. SET", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("2. SET", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("3. SET", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("OYUN", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Black)
        }

        TennisSetScoreRow(player = match.homeTeam, s1 = "6", s2 = "4", s3 = "4", game = "40 🎾", isServing = true, isHome = true)
        TennisSetScoreRow(player = match.awayTeam, s1 = "4", s2 = "6", s3 = "3", game = "30", isServing = false, isHome = false)
      }
    }

    // 2. 2D Tenis Kortu Görseli
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
      border = BorderStroke(2.dp, Color(0xFF2563EB)),
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        // Kort Çizgileri
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Dış kort çizgisi
          drawRect(color = Color(0x99FFFFFF), topLeft = Offset(14f, 14f), size = Size(w - 28f, h - 28f), style = Stroke(2.5f))
          // Servis kutusu sınırları
          drawLine(color = Color(0x88FFFFFF), start = Offset(w * 0.2f, 14f), end = Offset(w * 0.2f, h - 14f), strokeWidth = 1.5f)
          drawLine(color = Color(0x88FFFFFF), start = Offset(w * 0.8f, 14f), end = Offset(w * 0.8f, h - 14f), strokeWidth = 1.5f)
          // File (Net) Çizgisi
          drawLine(color = Color.White, start = Offset(10f, h / 2), end = Offset(w - 10f, h / 2), strokeWidth = 3f)
          // Orta servis çizgisi
          drawLine(color = Color(0x88FFFFFF), start = Offset(w / 2, h * 0.25f), end = Offset(w / 2, h * 0.75f), strokeWidth = 1.5f)
          drawLine(color = Color(0x88FFFFFF), start = Offset(w * 0.2f, h * 0.25f), end = Offset(w * 0.8f, h * 0.25f), strokeWidth = 1.5f)
          drawLine(color = Color(0x88FFFFFF), start = Offset(w * 0.2f, h * 0.75f), end = Offset(w * 0.8f, h * 0.75f), strokeWidth = 1.5f)
        }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          // Üst Dip Çizgi (Player 1)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(20.dp),
              border = BorderStroke(1.dp, GoldYellow)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("🎾 Servis Atan: ${match.homeTeam}", color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          // File Bilgisi
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            Surface(color = Color(0xAA000000), shape = RoundedCornerShape(4.dp)) {
              Text("FİLE (NET)", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp))
            }
          }

          // Alt Dip Çizgi (Player 2)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(20.dp),
              border = BorderStroke(1.dp, Color(0xFF38BDF8))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Karşılayan: ${match.awayTeam}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 3. Servis ve Oyun İstatistikleri
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text("📊 SERVİS VE MAÇ METRİKLERİ", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(8.dp))

        TennisStatRow("Ace Sayısı", "11", "8")
        TennisStatRow("Çift Hata", "2", "3")
        TennisStatRow("1. Servis Başarısı", "%68", "%64")
        TennisStatRow("Servis Kırma (Break Point)", "2/4 (%50)", "1/3 (%33)")
        TennisStatRow("Kazanılan Puanlar", "84", "76")
      }
    }
  }
}

@Composable
private fun TennisSetScoreRow(player: String, s1: String, s2: String, s3: String, game: String, isServing: Boolean, isHome: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp, horizontal = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(player, color = if (isHome) GoldYellow else Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
    Text(s1, color = Color.White, fontSize = 11.sp)
    Text(s2, color = Color.White, fontSize = 11.sp)
    Text(s3, color = Color.White, fontSize = 11.sp)
    Text(game, color = if (isServing) Color(0xFF10B981) else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
  }
}

@Composable
private fun TennisStatRow(title: String, homeVal: String, awayVal: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(homeVal, color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(60.dp), textAlign = TextAlign.Start)
    Text(title, color = Color(0xFF94A3B8), fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    Text(awayVal, color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
  }
}
