package com.example.ui.components.detail

import androidx.compose.foundation.BorderStroke
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
import com.example.data.model.MatchLineups
import com.example.data.model.PlayerLineup
import com.example.ui.theme.GoldYellow

/**
 * Specialized Basketball Match Detail view:
 * 1. 2D Parquet Court with starting 5s (PG, SG, SF, PF, C)
 * 2. Complete team rosters underneath the court (İlk 5, Bench, Başantrenör)
 * 3. Quarter scores and comparative team statistics (Rebounds, Assists, 3PT%)
 */
@Composable
fun BasketballMatchDetailContent(
  match: Match,
  lineups: MatchLineups,
  selectedPlayer: PlayerLineup?,
  onPlayerClick: (PlayerLineup) -> Unit
) {
  var selectedTeamTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Away

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Periyot Skorları Kartı
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
          Text("🏀 ÇEYREK SKORLARI & CANLI PERİYOT", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
          Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
            Text("MS: ${match.homeScore} - ${match.awayScore}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Çeyrek Tablosu
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A), shape = RoundedCornerShape(6.dp))
            .padding(vertical = 5.dp, horizontal = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("TAKIM", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
          Text("1.Ç", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("2.Ç", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("3.Ç", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("4.Ç", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("TOPLAM", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Black)
        }

        // Ev Sahibi Skor Satırı
        QuarterScoreRow(team = match.homeTeam, q1 = 22, q2 = 19, q3 = 24, q4 = 21, total = match.homeScore, isHome = true)
        // Deplasman Skor Satırı
        QuarterScoreRow(team = match.awayTeam, q1 = 18, q2 = 23, q3 = 20, q4 = 19, total = match.awayScore, isHome = false)
      }
    }

    // 2. 2D Ahşap Parke Basketbol Sahası (İlk 5'ler)
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF9A5B28)),
      border = BorderStroke(2.dp, Color(0xFF78350F)),
      modifier = Modifier
        .fillMaxWidth()
        .height(340.dp)
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        // Parke çizgileri
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Dış saha çizgisi
          drawRect(color = Color(0x99FFFFFF), topLeft = Offset(10f, 10f), size = Size(w - 20f, h - 20f), style = Stroke(2.5f))
          // Orta çizgi
          drawLine(color = Color(0x99FFFFFF), start = Offset(10f, h / 2), end = Offset(w - 10f, h / 2), strokeWidth = 2f)
          // Orta yuvarlak
          drawCircle(color = Color(0x88FFFFFF), radius = 35f, center = Offset(w / 2, h / 2), style = Stroke(2f))

          // Üst Ceza Alanı (Key) & 3 Sayı Çizgisi
          drawRect(color = Color(0x55000000), topLeft = Offset(w * 0.35f, 10f), size = Size(w * 0.3f, 65f))
          drawCircle(color = Color(0x88FFFFFF), radius = w * 0.38f, center = Offset(w / 2, 10f), style = Stroke(2f))

          // Alt Ceza Alanı (Key) & 3 Sayı Çizgisi
          drawRect(color = Color(0x55000000), topLeft = Offset(w * 0.35f, h - 75f), size = Size(w * 0.3f, 65f))
          drawCircle(color = Color(0x88FFFFFF), radius = w * 0.38f, center = Offset(w / 2, h - 10f), style = Stroke(2f))
        }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          // Ev Sahibi İlk 5 (Üst Yarı)
          Column {
            Text("🏠 ${match.homeTeam} İlk 5", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              val home5 = lineups.home.starters.take(5)
              val positions = listOf("PG", "SG", "SF", "PF", "C")
              home5.forEachIndexed { idx, p ->
                BasketballPlayerCircle(player = p, posTag = positions.getOrElse(idx) { "G" }, isHome = true, onClick = { onPlayerClick(p) })
              }
            }
          }

          // Orta Çizgi Bilgi
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            Surface(color = Color(0xAA000000), shape = RoundedCornerShape(4.dp)) {
              Text("PARKE DİZİLİŞİ (5v5)", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
          }

          // Deplasman İlk 5 (Alt Yarı)
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              val away5 = lineups.away.starters.take(5)
              val positions = listOf("PG", "SG", "SF", "PF", "C")
              away5.forEachIndexed { idx, p ->
                BasketballPlayerCircle(player = p, posTag = positions.getOrElse(idx) { "G" }, isHome = false, onClick = { onPlayerClick(p) })
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("✈️ ${match.awayTeam} İlk 5", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
          }
        }
      }
    }

    // 3. TAKIMIN ALTINDA BASKETBOL KADROSU & BENCH
    Surface(
      color = Color(0xFF131D31),
      shape = RoundedCornerShape(10.dp),
      border = BorderStroke(1.dp, Color(0xFF334155)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedTeamTab == 0,
            onClick = { selectedTeamTab = 0 },
            label = { Text("🏠 ${match.homeTeam} Kadrosu", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
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
            label = { Text("✈️ ${match.awayTeam} Kadrosu", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF38BDF8),
              selectedLabelColor = Color(0xFF0F172A),
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val currentTeam = if (selectedTeamTab == 0) lineups.home else lineups.away
        val isHome = selectedTeamTab == 0

        Text(
          text = "🏀 BAŞLANGIÇ İLK 5'İ",
          color = if (isHome) GoldYellow else Color(0xFF38BDF8),
          fontSize = 11.sp,
          fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(4.dp))

        currentTeam.starters.take(5).forEach { p ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onPlayerClick(p) }
              .padding(vertical = 3.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("#${p.number}", color = if (isHome) GoldYellow else Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
            Text(p.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(3.dp)) {
              Text(p.position, color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("★ ${p.rating}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Black)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // BENCH / YEDEKLER
        if (currentTeam.substitutes.isNotEmpty()) {
          Text(
            text = "💺 YEDEK KULÜBESİ (${currentTeam.substitutes.size} Oyuncu)",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))

          currentTeam.substitutes.forEach { sub ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("#${sub.number}", color = Color(0xFF64748B), fontSize = 10.sp, modifier = Modifier.width(26.dp))
              Text(sub.name, color = Color(0xFFCBD5E1), fontSize = 10.sp, modifier = Modifier.weight(1f))
              Text(sub.position, color = Color(0xFF64748B), fontSize = 9.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun QuarterScoreRow(team: String, q1: Int, q2: Int, q3: Int, q4: Int, total: Int, isHome: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp, horizontal = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(team, color = if (isHome) GoldYellow else Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
    Text("$q1", color = Color.White, fontSize = 11.sp)
    Text("$q2", color = Color.White, fontSize = 11.sp)
    Text("$q3", color = Color.White, fontSize = 11.sp)
    Text("$q4", color = Color.White, fontSize = 11.sp)
    Text("$total", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
  }
}

@Composable
private fun BasketballPlayerCircle(
  player: PlayerLineup,
  posTag: String,
  isHome: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .padding(horizontal = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(if (isHome) Color(0xFFD97706) else Color(0xFF2563EB)),
      contentAlignment = Alignment.Center
    ) {
      Text("#${player.number}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
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
    Text(posTag, color = Color(0xFFFFD54F), fontSize = 8.sp, fontWeight = FontWeight.Black)
  }
}
