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

data class NflPlayer(
  val number: Int,
  val name: String,
  val position: String, // QB, RB, WR, TE, OT, DE, MLB, CB, FS
  val unit: String, // Hücum, Savunma
  val statPrimary: String, // e.g. "320 Yds, 3 TD"
  val rating: Double
)

/**
 * Specialized American Football (NFL & Ragbi) Detail view:
 * 1. 2D NFL 100-Yard Field with endzones, yardlines and hash marks
 * 2. Down & Distance tracker (e.g. 3rd & 4 at NE 28)
 * 3. Starting Lineup under the field (Hücum & Savunma 11'leri)
 * 4. Passing, Rushing, Receiving & Turnover statistics
 */
@Composable
fun AmericanFootballMatchDetailContent(
  match: Match,
  onOpenDirectory: () -> Unit = {}
) {
  var selectedTeamTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Away
  var selectedUnitTab by remember { mutableIntStateOf(0) } // 0: Hücum (Offense), 1: Savunma (Defense)
  var selectedPlayer by remember { mutableStateOf<NflPlayer?>(null) }

  val homeOffense = remember(match.id) {
    listOf(
      NflPlayer(15, "Patrick Mahomes", "QB", "Hücum", "285 Yds • 3 TD", 9.6),
      NflPlayer(10, "Isiah Pacheco", "RB", "Hücum", "84 Yds • 1 TD", 8.4),
      NflPlayer(87, "Travis Kelce", "TE", "Hücum", "92 Yds • 1 TD", 9.3),
      NflPlayer(4, "Rashee Rice", "WR", "Hücum", "76 Yds • 0 TD", 8.2),
      NflPlayer(11, "Marquez Valdes-Scantling", "WR", "Hücum", "45 Yds • 1 TD", 7.8),
      NflPlayer(74, "Jawaan Taylor", "OT", "Hücum", "0 Sacks Allowed", 8.0),
      NflPlayer(52, "Creed Humphrey", "C", "Hücum", "99% Snap Accuracy", 9.1)
    )
  }

  val homeDefense = remember(match.id) {
    listOf(
      NflPlayer(95, "Chris Jones", "DT", "Savunma", "2.0 Sacks • 4 TFL", 9.4),
      NflPlayer(56, "George Karlaftis", "DE", "Savunma", "1.0 Sack • 3 QB Hits", 8.5),
      NflPlayer(32, "Nick Bolton", "MLB", "Savunma", "11 Solo Tackles", 8.9),
      NflPlayer(22, "Trent McDuffie", "CB", "Savunma", "2 Pass Defended", 9.0),
      NflPlayer(20, "Justin Reid", "FS", "Savunma", "1 INT • 6 Tackles", 8.3)
    )
  }

  val awayOffense = remember(match.id) {
    listOf(
      NflPlayer(13, "Brock Purdy", "QB", "Hücum", "267 Yds • 2 TD", 8.9),
      NflPlayer(23, "Christian McCaffrey", "RB", "Hücum", "112 Yds • 2 TD", 9.7),
      NflPlayer(85, "George Kittle", "TE", "Hücum", "68 Yds • 0 TD", 9.1),
      NflPlayer(11, "Brandon Aiyuk", "WR", "Hücum", "88 Yds • 1 TD", 8.8),
      NflPlayer(19, "Deebo Samuel", "WR", "Hücum", "74 Scrimmage Yds", 8.7),
      NflPlayer(71, "Trent Williams", "OT", "Hücum", "All-Pro Blocking", 9.8)
    )
  }

  val awayDefense = remember(match.id) {
    listOf(
      NflPlayer(97, "Nick Bosa", "DE", "Savunma", "2.5 Sacks • 5 Pressures", 9.6),
      NflPlayer(90, "Javon Hargrave", "DT", "Savunma", "1.0 Sack • 3 Tackles", 8.4),
      NflPlayer(54, "Fred Warner", "MLB", "Savunma", "13 Tackles • 1 FF", 9.5),
      NflPlayer(7, "Charvarius Ward", "CB", "Savunma", "3 Pass Defended", 8.8),
      NflPlayer(29, "Talanoa Hufanga", "SS", "Savunma", "1 INT • 7 Tackles", 8.5)
    )
  }

  val activePlayers = if (selectedTeamTab == 0) {
    if (selectedUnitTab == 0) homeOffense else homeDefense
  } else {
    if (selectedUnitTab == 0) awayOffense else awayDefense
  }

  val activeTeamName = if (selectedTeamTab == 0) match.homeTeam else match.awayTeam

  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Canlı Skor ve Down/Distance Kartı
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
              Text("🏈 ${match.league}", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Text(match.homeTeam, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
              Text("Score: 24", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                Text("CANLI 3. ÇEYREK", color = LiveRed, fontSize = 9.sp, fontWeight = FontWeight.Black)
                Text("24 - 21", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("3rd & 4 • KC 38 Yard", color = GoldYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }

            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
              Text("NFL Super Bowl", color = Color(0xFF94A3B8), fontSize = 10.sp)
              Text(match.awayTeam, color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 13.sp)
              Text("Score: 21", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    }

    // 2. Takım ve Ünite Filtre Butonları
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedTeamTab == 0,
            onClick = { selectedTeamTab = 0 },
            label = { Text("🏠 ${match.homeTeam}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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
            label = { Text("✈️ ${match.awayTeam}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF38BDF8),
              selectedLabelColor = Color(0xFF0F172A),
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedUnitTab == 0,
            onClick = { selectedUnitTab = 0 },
            label = { Text("⚡ Hücum Kadrosu (Offense)", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF10B981),
              selectedLabelColor = Color.White,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.weight(1f)
          )
          FilterChip(
            selected = selectedUnitTab == 1,
            onClick = { selectedUnitTab = 1 },
            label = { Text("🛡️ Savunma Kadrosu (Defense)", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF3B82F6),
              selectedLabelColor = Color.White,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 3. 2D NFL Sahası (100 Yard Çizgileri ve Endzone)
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D)),
        border = BorderStroke(1.5.dp, Color(0xFF16A34A)),
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Endzone sol & sağ
            val endzoneWidth = w * 0.10f
            drawRect(color = Color(0xFFB91C1C), topLeft = Offset(10f, 10f), size = Size(endzoneWidth, h - 20f))
            drawRect(color = Color(0xFF1D4ED8), topLeft = Offset(w - 10f - endzoneWidth, 10f), size = Size(endzoneWidth, h - 20f))

            // Yeşil Saha & Yard Çizgileri
            val fieldWidth = w - 20f - (2 * endzoneWidth)
            val lineCount = 10
            for (i in 0..lineCount) {
              val x = 10f + endzoneWidth + (i * (fieldWidth / lineCount))
              drawLine(color = Color(0x66FFFFFF), start = Offset(x, 10f), end = Offset(x, h - 10f), strokeWidth = 1.5f)
            }

            // Dış sınır
            drawRect(color = Color.White, topLeft = Offset(10f, 10f), size = Size(w - 20f, h - 20f), style = Stroke(2f))
          }

          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Text("🏈 100 YARD NFL SAHASI & DİZİLİŞ KONUMLANDIRMASI", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              activePlayers.take(5).forEach { p ->
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.clickable { selectedPlayer = p }
                ) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(if (selectedUnitTab == 0) Color(0xFFDC2626) else Color(0xFF2563EB))
                      .border(1.5.dp, GoldYellow, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("#${p.number}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                  }
                  Text(p.name.split(" ").lastOrNull() ?: p.name, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
                  Text(p.position, color = GoldYellow, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
              }
            }

            Text("LINE OF SCRIMMAGE • DOWN: 3rd & 4", color = GoldYellow, fontSize = 9.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
          }
        }
      }
    }

    // 4. SAHANIN ALTINDA NFL OYUNCULARI TABLOSU
    item {
      Surface(
        color = Color(0xFF131D31),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "📋 $activeTeamName ${if (selectedUnitTab == 0) "HÜCUM (OFFENSE)" else "SAVUNMA (DEFENSE)"} KADROSU",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = if (selectedTeamTab == 0) GoldYellow else Color(0xFF38BDF8)
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
              .padding(vertical = 4.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("#", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp))
            Text("OYUNCU", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("MEVKİ", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(45.dp), textAlign = TextAlign.Center)
            Text("İSTATİSTİK", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp), textAlign = TextAlign.End)
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
                modifier = Modifier.width(45.dp)
              ) {
                Text(p.position, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 1.dp))
              }
              Text(p.statPrimary, color = Color(0xFF34D399), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp), textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
              Text("★ ${p.rating}", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(42.dp), textAlign = TextAlign.End)
            }
          }
        }
      }
    }
  }
}
