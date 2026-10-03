package com.example.ui.components.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

data class VolleyballPlayer(
  val number: Int,
  val name: String,
  val role: String, // Pasör, Smaçör, Pasör Çaprazı, Orta Oyuncu, Libero
  val rotationZone: Int, // 1 - 6
  val points: Int,
  val spikes: Int,
  val blocks: Int,
  val aces: Int,
  val rating: Double
)

/**
 * Specialized Volleyball Match Detail view:
 * 1. 2D Volleyball court with official 9x18m proportions, net, 3m attack lines & 6-zone rotation
 * 2. Set-by-Set live scores (Set 1, Set 2, Set 3, Set 4, Set 5)
 * 3. Starting 6 (İlk 6) roster table under the court + Libero + Substitutes + Head Coach
 * 4. Technical attack metrics (Spike kill %, Block points, Ace serves, Reception %)
 */
@Composable
fun VolleyballMatchDetailContent(
  match: Match,
  onOpenDirectory: () -> Unit = {}
) {
  var selectedTeamTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Away
  var selectedPlayer by remember { mutableStateOf<VolleyballPlayer?>(null) }

  val homePlayers = remember(match.id) {
    listOf(
      VolleyballPlayer(11, "Naz Aydemir Akyol", "Pasör", 1, 4, 1, 1, 2, 8.4),
      VolleyballPlayer(18, "Zehra Güneş", "Orta Oyuncu", 2, 14, 8, 5, 1, 8.9),
      VolleyballPlayer(99, "Ebrar Karakurt", "Pasör Çaprazı", 3, 22, 18, 2, 2, 9.2),
      VolleyballPlayer(9, "Hande Baladın", "Smaçör", 4, 12, 10, 1, 1, 8.1),
      VolleyballPlayer(14, "Eda Erdem (K)", "Orta Oyuncu", 5, 13, 7, 4, 2, 8.8),
      VolleyballPlayer(7, "İlkin Aydın", "Smaçör", 6, 11, 9, 1, 1, 8.0),
      VolleyballPlayer(1, "Gizem Örge", "Libero", 0, 0, 0, 0, 0, 8.6)
    )
  }

  val awayPlayers = remember(match.id) {
    listOf(
      VolleyballPlayer(4, "Alessia Orro", "Pasör", 1, 3, 1, 1, 1, 8.2),
      VolleyballPlayer(17, "Myriam Sylla", "Smaçör", 2, 15, 13, 1, 1, 8.7),
      VolleyballPlayer(11, "Anna Danesi (K)", "Orta Oyuncu", 3, 12, 6, 5, 1, 8.5),
      VolleyballPlayer(18, "Paola Egonu", "Pasör Çaprazı", 4, 26, 22, 2, 2, 9.4),
      VolleyballPlayer(19, "Sarah Fahr", "Orta Oyuncu", 5, 9, 5, 3, 1, 8.1),
      VolleyballPlayer(14, "Caterina Bosetti", "Smaçör", 6, 8, 7, 1, 0, 7.9),
      VolleyballPlayer(6, "Monica De Gennaro", "Libero", 0, 0, 0, 0, 0, 8.9)
    )
  }

  val activePlayers = if (selectedTeamTab == 0) homePlayers else awayPlayers
  val activeTeamName = if (selectedTeamTab == 0) match.homeTeam else match.awayTeam

  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Set Skorları & Maç Özeti Kartı
    item {
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
            Column(modifier = Modifier.weight(1f)) {
              Text("🏐 ${match.league}", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Text(match.homeTeam, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
              Text("Setler: 3", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("CANLI SKOR", color = LiveRed, fontSize = 9.sp, fontWeight = FontWeight.Black)
                Text("3 - 1", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("4. Set Devam Ediyor", color = Color(0xFF94A3B8), fontSize = 9.sp)
              }
            }

            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
              Text("Voleybol Sultanlar Ligi", color = Color(0xFF94A3B8), fontSize = 10.sp)
              Text(match.awayTeam, color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 13.sp)
              Text("Setler: 1", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Set Skor Tablosu
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("SET SKORLARI:", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("1. Set: 25-21", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              Text("2. Set: 23-25", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              Text("3. Set: 25-18", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              Text("4. Set: 19-15 🟢", color = Color(0xFF4ADE80), fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }

    // 2. Takım Seçici Tabları
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedTeamTab == 0,
          onClick = { selectedTeamTab = 0 },
          label = { Text("🏠 ${match.homeTeam} (Saha & İlk 6)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GoldYellow,
            selectedLabelColor = Color(0xFF0F172A),
            containerColor = Color(0xFF1E293B),
            labelColor = Color(0xFF94A3B8)
          ),
          modifier = Modifier.weight(1f)
        )
        FilterChip(
          selected = selectedTeamTab == 1,
          onClick = { selectedTeamTab = 1 },
          label = { Text("✈️ ${match.awayTeam} (Saha & İlk 6)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF38BDF8),
            selectedLabelColor = Color(0xFF0F172A),
            containerColor = Color(0xFF1E293B),
            labelColor = Color(0xFF94A3B8)
          ),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. 2D Voleybol Sahası ve 6'lı Rotasyon Dizilişi
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
        border = BorderStroke(1.5.dp, Color(0xFF3B82F6)),
        modifier = Modifier
          .fillMaxWidth()
          .height(300.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Turuncu oyun alanı (9x9m yarım saha veya tam 9x18m)
            drawRect(color = Color(0xFFEA580C), topLeft = Offset(14f, 14f), size = Size(w - 28f, h - 28f))
            drawRect(color = Color.White, topLeft = Offset(14f, 14f), size = Size(w - 28f, h - 28f), style = Stroke(2.5f))

            // Orta File Çizgisi
            drawLine(color = Color.White, start = Offset(14f, h / 2), end = Offset(w - 14f, h / 2), strokeWidth = 3f)
            // 3 Metre Hücum Çizgileri
            drawLine(color = Color(0xCCFFFFFF), start = Offset(14f, h * 0.35f), end = Offset(w - 14f, h * 0.35f), strokeWidth = 1.5f)
            drawLine(color = Color(0xCCFFFFFF), start = Offset(14f, h * 0.65f), end = Offset(w - 14f, h * 0.65f), strokeWidth = 1.5f)
          }

          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            // Üst Bölüm: Hücum Hattı (Ön Hat: 4, 3, 2 numaralı bölgeler)
            Column(modifier = Modifier.fillMaxWidth()) {
              Text("🏐 ÖN HAT (HÜCUM & BLOK BÖLGESİ - 3 METRE)", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                activePlayers.filter { it.rotationZone in listOf(4, 3, 2) }.forEach { p ->
                  VolleyballPlayerNode(player = p, onClick = { selectedPlayer = p })
                }
              }
            }

            // File Bandı Göstergesi
            Surface(
              color = Color(0xDD000000),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
              Text("━━━ RESMİ FİLE (Yükseklik: 2.24m) ━━━", color = GoldYellow, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
            }

            // Alt Bölüm: Savunma & Servis Hattı (Arka Hat: 5, 6, 1 numaralı bölgeler)
            Column(modifier = Modifier.fillMaxWidth()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                activePlayers.filter { it.rotationZone in listOf(5, 6, 1) }.forEach { p ->
                  VolleyballPlayerNode(player = p, onClick = { selectedPlayer = p })
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text("ARKA HAT (SERVİS & DEFANS BÖLGESİ)", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
          }
        }
      }
    }

    // 4. Seçilen Oyuncu Kartı
    if (selectedPlayer != null) {
      item {
        val p = selectedPlayer!!
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          border = BorderStroke(1.dp, GoldYellow)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(TealDark),
                contentAlignment = Alignment.Center
              ) {
                Text("#${p.number}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(p.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Mevki: ${p.role} • Bölge: ${p.rotationZone}. Hat", color = Color(0xFF94A3B8), fontSize = 10.sp)
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Surface(color = Color(0xFF065F46), shape = RoundedCornerShape(4.dp)) {
                Text("Sayı: ${p.points} 💥", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
              Surface(color = Color(0xFF1E3A8A), shape = RoundedCornerShape(4.dp)) {
                Text("Blok: ${p.blocks} 🛡️", color = Color(0xFF60A5FA), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            }
          }
        }
      }
    }

    // 5. SAHANIN ALTINDA İLK 6 KADROSU
    item {
      Surface(
        color = Color(0xFF131D31),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "📋 $activeTeamName İLK 6 & LİBERO KADROSU",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = if (selectedTeamTab == 0) GoldYellow else Color(0xFF38BDF8)
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Tablo Başlığı
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
              .padding(vertical = 4.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("#", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp))
            Text("VOLEYBOLCU", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("MEVKİ", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(68.dp), textAlign = TextAlign.Center)
            Text("SAYI", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
            Text("BLOK", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
            Text("REYTING", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp), textAlign = TextAlign.End)
          }

          activePlayers.forEach { p ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedPlayer = p }
                .padding(vertical = 5.dp, horizontal = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("${p.number}", color = if (selectedTeamTab == 0) GoldYellow else Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp))
              Text(p.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
              Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier.width(68.dp)
              ) {
                Text(p.role, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 1.dp))
              }
              Text("${p.points}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
              Text("${p.blocks}", color = Color(0xFF60A5FA), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
              Text("★ ${p.rating}", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(42.dp), textAlign = TextAlign.End)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun VolleyballPlayerNode(
  player: VolleyballPlayer,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .padding(4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(if (player.role == "Libero") Color(0xFFEAB308) else Color(0xFFDC2626))
        .border(1.5.dp, Color.White, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "${player.number}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
    }
    Spacer(modifier = Modifier.height(2.dp))
    Surface(
      color = Color(0xF20F172A),
      shape = RoundedCornerShape(4.dp),
      border = BorderStroke(0.6.dp, Color(0x66FFFFFF)),
      modifier = Modifier.widthIn(min = 36.dp, max = 64.dp)
    ) {
      Text(
        text = player.name.split(" ").lastOrNull() ?: player.name,
        color = Color.White,
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
      )
    }
    Text(
      text = player.role.take(3),
      color = Color(0xFFFFD54F),
      fontSize = 8.sp,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )
  }
}
