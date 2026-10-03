package com.example.ui.components.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.ui.theme.GoldYellow

/**
 * Specialized Combat Sports (MMA / UFC & Boks) Detail view:
 * 1. Octagon / Boxing Ring visualization with Red and Blue corners
 * 2. Tale of the Tape comparison (Height, Weight, Reach, Stance, KO %)
 * 3. Significant strikes target breakdown and round predictions
 */
@Composable
fun CombatMatchDetailContent(
  match: Match
) {
  val isBoxing = match.league.contains("Boks", ignoreCase = true) || match.league.contains("Boxing", ignoreCase = true)
  val arenaTitle = if (isBoxing) "Boks Ringi (12 Round)" else "UFC Oktagonu (5 Round Unvan)"

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Dövüş Başlık Kartı
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(arenaTitle, color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
          Text(match.league, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text("Ağırlık Sıkleti: Unvan Karşılaşması", color = Color(0xFF94A3B8), fontSize = 10.sp)
        }

        Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
          Text("CANLI 🔴", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
        }
      }
    }

    // 2. Oktagon / Ring Görseli & Köşeler
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
      border = BorderStroke(2.dp, Color(0xFF3F3F46)),
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
      Box(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Kırmızı Köşe
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFDC2626)),
              contentAlignment = Alignment.Center
            ) {
              Text("🥊", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(match.homeTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Surface(color = Color(0xFF991B1B), shape = RoundedCornerShape(3.dp)) {
              Text("KIRMIZI KÖŞE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
          }

          // Merkez VS & Oktagon Kafesi
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
              color = Color(0xFF27272A),
              shape = RoundedCornerShape(20.dp),
              border = BorderStroke(1.dp, Color(0xFF52525B))
            ) {
              Text("OKTAGON", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("VS", color = Color(0xFF71717A), fontWeight = FontWeight.Black, fontSize = 14.sp)
          }

          // Mavi Köşe
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF2563EB)),
              contentAlignment = Alignment.Center
            ) {
              Text("🥊", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(match.awayTeam, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Surface(color = Color(0xFF1E40AF), shape = RoundedCornerShape(3.dp)) {
              Text("MAVİ KÖŞE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
          }
        }
      }
    }

    // 3. Tale of the Tape (Fiziksel & Vuruş Karşılaştırması)
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text("📐 TALE OF THE TAPE (FİZİKSEL KARŞILAŞTIRMA)", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(8.dp))

        CombatTapeRow("Kariyer Karnesi", "26-1-0", "21-3-0")
        CombatTapeRow("Boy (Height)", "178 cm", "180 cm")
        CombatTapeRow("Kilo (Weight)", "70.3 kg", "70.1 kg")
        CombatTapeRow("Erişim (Reach)", "179 cm", "183 cm")
        CombatTapeRow("Duruş (Stance)", "Southpaw (Sol)", "Ortodoks (Sağ)")
        CombatTapeRow("Nakavt / Bitiriş Oranı", "%74", "%68")
        CombatTapeRow("Takedown Başarısı", "3.2 / Maç", "1.1 / Maç")
      }
    }
  }
}

@Composable
private fun CombatTapeRow(title: String, redVal: String, blueVal: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(redVal, color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(70.dp), textAlign = TextAlign.Start)
    Text(title, color = Color(0xFF94A3B8), fontSize = 10.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    Text(blueVal, color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(70.dp), textAlign = TextAlign.End)
  }
}
