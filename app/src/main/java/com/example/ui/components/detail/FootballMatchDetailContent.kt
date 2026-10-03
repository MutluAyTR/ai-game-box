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
import com.example.data.model.MatchLineups
import com.example.data.model.PlayerLineup
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

/**
 * Specialized Football Match Detail view:
 * 1. 2D Tactical Pitch with ALL 11 PLAYERS arranged strictly by tactical formations (4-3-3, 4-2-3-1, 4-4-2, etc.)
 * 2. Dedicated team pitch mode so all 11 players have generous spacing, jersey numbers, names & ratings without any clipping
 * 3. Complete Starting 11 squad table positioned directly underneath the pitch ("sahayı takımın altında olsun takım 11 i")
 * 4. Bench / Substitutes, Head Coach, market values, and tactical stats
 */
@Composable
fun FootballMatchDetailContent(
  lineups: MatchLineups,
  homeTeam: String,
  awayTeam: String,
  selectedPlayer: PlayerLineup?,
  onPlayerClick: (PlayerLineup) -> Unit,
  onOpenPlayerDirectory: () -> Unit = {}
) {
  var selectedTeamTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Away, 2: Tam Saha (İki Takım)
  var internalSelectedPlayer by remember { mutableStateOf<PlayerLineup?>(selectedPlayer) }

  val activeTeamLineup = if (selectedTeamTab == 1) lineups.away else lineups.home
  val activeTeamName = if (selectedTeamTab == 1) awayTeam else homeTeam
  val isHomeTeam = selectedTeamTab != 1

  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 0. Maçkolik 50.000+ Oyuncu Veritabanı Keşfetme Kartı
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, GoldYellow),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOpenPlayerDirectory() }
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("⭐", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("58.450+ Maçkolik Futbolcu Veritabanı", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Text("Tüm Süper Lig ve Dünya kulüpleri kadro & piyasa değerleri", color = Color(0xFF94A3B8), fontSize = 9.sp)
            }
          }
          Surface(
            color = TealDark,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text("Gözat 🔍", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
          }
        }
      }
    }

    // 1. Taktik Karşılaşma Özeti Kartı
    item {
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
          Column(modifier = Modifier.weight(1f)) {
            Text(homeTeam, color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Diziliş: ${lineups.home.formation}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("TD: ${lineups.home.coach}", color = Color(0xFF94A3B8), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
          }

          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.padding(horizontal = 8.dp)
          ) {
            Text("VS", color = Color(0xFF64748B), fontWeight = FontWeight.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
          }

          Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(awayTeam, color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Diziliş: ${lineups.away.formation}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("TD: ${lineups.away.coach}", color = Color(0xFF94A3B8), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
        }
      }
    }

    // 2. Takım Seçici Tablar (Ev Sahibi / Deplasman / Tam Saha)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        FilterChip(
          selected = selectedTeamTab == 0,
          onClick = { selectedTeamTab = 0 },
          label = { Text("🏠 $homeTeam (${lineups.home.formation})", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
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
          label = { Text("✈️ $awayTeam (${lineups.away.formation})", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF38BDF8),
            selectedLabelColor = Color(0xFF0F172A),
            containerColor = Color(0xFF1E293B),
            labelColor = Color(0xFF94A3B8)
          ),
          modifier = Modifier.weight(1f)
        )

        FilterChip(
          selected = selectedTeamTab == 2,
          onClick = { selectedTeamTab = 2 },
          label = { Text("🏟️ Tam Saha", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF10B981),
            selectedLabelColor = Color.White,
            containerColor = Color(0xFF1E293B),
            labelColor = Color(0xFF94A3B8)
          )
        )
      }
    }

    // 3. TAKTİK SAHA - DİZİLİŞE GÖRE 11 OYUNCUNUN TAMAMI (DRAG-AND-DROP DESTEKLİ)
    item {
      if (selectedTeamTab == 2) {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
          border = BorderStroke(1.5.dp, Color(0xFF059669)),
          modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val w = size.width
              val h = size.height

              val stripes = 8
              val stripeHeight = h / stripes
              for (i in 0 until stripes) {
                val c = if (i % 2 == 0) Color(0xFF065F46) else Color(0xFF047857)
                drawRect(color = c, topLeft = Offset(0f, i * stripeHeight), size = Size(w, stripeHeight))
              }

              drawRect(color = Color(0x66FFFFFF), topLeft = Offset(10f, 10f), size = Size(w - 20f, h - 20f), style = Stroke(2f))
              drawLine(color = Color(0x88FFFFFF), start = Offset(10f, h / 2), end = Offset(w - 10f, h / 2), strokeWidth = 2.5f)
              drawCircle(color = Color(0x66FFFFFF), radius = 38f, center = Offset(w / 2, h / 2), style = Stroke(2f))
              drawRect(color = Color(0x55FFFFFF), topLeft = Offset(w * 0.25f, 10f), size = Size(w * 0.5f, 55f), style = Stroke(1.5f))
              drawRect(color = Color(0x55FFFFFF), topLeft = Offset(w * 0.25f, h - 65f), size = Size(w * 0.5f, 55f), style = Stroke(1.5f))
            }

            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 10.dp),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceEvenly) {
                val homeRows = getFormationRows(lineups.home.starters, lineups.home.formation, isHome = true)
                for (row in homeRows) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    for (p in row) {
                      TacticalPitchPlayerBadge(
                        player = p,
                        isHome = true,
                        isCompact = true,
                        onClick = {
                          internalSelectedPlayer = p
                          onPlayerClick(p)
                        }
                      )
                    }
                  }
                }
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("🏠 $homeTeam (${lineups.home.formation})", color = GoldYellow, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Text("✈️ $awayTeam (${lineups.away.formation})", color = Color(0xFF38BDF8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
              }

              Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceEvenly) {
                val awayRows = getFormationRows(lineups.away.starters, lineups.away.formation, isHome = false)
                for (row in awayRows) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    for (p in row) {
                      TacticalPitchPlayerBadge(
                        player = p,
                        isHome = false,
                        isCompact = true,
                        onClick = {
                          internalSelectedPlayer = p
                          onPlayerClick(p)
                        }
                      )
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        // Tek Takım Taktik Sahası: TacticalPitchView ile dinamik diziliş ve Drag-and-Drop yönetici planlaması
        TacticalPitchView(
          formation = activeTeamLineup.formation,
          teamName = activeTeamName,
          players = activeTeamLineup.starters,
          isHome = isHomeTeam,
          onPlayerClick = {
            internalSelectedPlayer = it
            onPlayerClick(it)
          }
        )
      }
    }

    // 4. Seçilen Oyuncu Detay Pop-Up Kartı
    if (internalSelectedPlayer != null) {
      item {
        val p = internalSelectedPlayer!!
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
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(TealDark),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "#${p.number}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(p.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  if (p.isCaptain) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(color = GoldYellow, shape = RoundedCornerShape(2.dp)) {
                      Text("KAPTAN", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 3.dp))
                    }
                  }
                }
                Text("Mevki: ${p.position} • Piyasa Değeri: ${p.marketValue}", color = Color(0xFF94A3B8), fontSize = 10.sp)
              }
            }

            Surface(
              color = Color(0xFF065F46),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text("Maçkolik Reyting: ★ ${p.rating}", color = Color(0xFF34D399), fontWeight = FontWeight.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
          }
        }
      }
    }

    // 5. SAHANIN ALTINDA TAKIMIN 11'İ ("sahayı takımın altında olsun takım 11 i")
    item {
      Surface(
        color = Color(0xFF131D31),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          // Başlık
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "📋 $activeTeamName İLK 11 KADROSU (${activeTeamLineup.formation})",
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = if (isHomeTeam) GoldYellow else Color(0xFF38BDF8)
            )
            Text(
              text = "TD: ${activeTeamLineup.coach}",
              fontSize = 10.sp,
              color = Color(0xFFCBD5E1),
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Tablo Başlık Sütunları
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
              .padding(vertical = 5.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("#", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
            Text("FUTBOLCU", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("MEVKİ", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
            Text("DEĞER", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(55.dp), textAlign = TextAlign.End)
            Text("REYTİNG", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp), textAlign = TextAlign.End)
          }

          // 11 Futbolcunun Tamamı Eksiksiz Liste
          activeTeamLineup.starters.forEach { p ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  internalSelectedPlayer = p
                  onPlayerClick(p)
                }
                .padding(vertical = 5.dp, horizontal = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "${p.number}",
                color = if (isHomeTeam) GoldYellow else Color(0xFF38BDF8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(24.dp)
              )
              Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                  p.name,
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                if (p.isCaptain) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Surface(color = GoldYellow, shape = RoundedCornerShape(2.dp)) {
                    Text("C", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 2.dp))
                  }
                }
                if (p.goals > 0) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("⚽${p.goals}", fontSize = 9.sp)
                }
              }
              FootballPositionBadge(position = p.position, modifier = Modifier.width(44.dp))
              Text(
                p.marketValue,
                color = Color(0xFF10B981),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(55.dp),
                textAlign = TextAlign.End
              )
              Text(
                "★ " + String.format(java.util.Locale.US, "%.1f", p.rating),
                color = Color(0xFF38BDF8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(42.dp),
                textAlign = TextAlign.End
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // YEDEKLER
          if (activeTeamLineup.substitutes.isNotEmpty()) {
            Text(
              text = "💺 YEDEK KULÜBESİ (${activeTeamLineup.substitutes.size} Oyuncu)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(4.dp))

            activeTeamLineup.substitutes.forEach { sub ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    internalSelectedPlayer = sub
                    onPlayerClick(sub)
                  }
                  .padding(vertical = 4.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("${sub.number}", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(sub.name, color = Color(0xFFCBD5E1), fontSize = 10.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                FootballPositionBadge(position = sub.position, modifier = Modifier.width(44.dp))
                Text(sub.marketValue, color = Color(0xFF64748B), fontSize = 9.sp, modifier = Modifier.width(55.dp), textAlign = TextAlign.End)
                Text("-", color = Color(0xFF64748B), fontSize = 10.sp, modifier = Modifier.width(42.dp), textAlign = TextAlign.End)
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Splits 11 starters into tactical lines according to formation.
 * Format: 4-3-3 -> Row 1: GK (1), Row 2: DEF (4), Row 3: MID (3), Row 4: FWD (3)
 */
private fun getFormationRows(
  starters: List<PlayerLineup>,
  formation: String,
  isHome: Boolean
): List<List<PlayerLineup>> {
  if (starters.isEmpty()) return emptyList()

  val parts = formation.split("-").mapNotNull { it.toIntOrNull() }
  val lines = mutableListOf<List<PlayerLineup>>()

  if (parts.size >= 3 && parts.sum() == 10 && starters.size >= 11) {
    var cursor = 1
    val gk = listOf(starters[0])
    val outfieldRows = mutableListOf<List<PlayerLineup>>()

    for (count in parts) {
      val end = minOf(cursor + count, starters.size)
      if (cursor < starters.size) {
        outfieldRows.add(starters.subList(cursor, end))
      }
      cursor = end
    }

    if (isHome) {
      lines.add(gk) // Row 0: GK
      lines.addAll(outfieldRows) // Row 1: DEF, Row 2: MID, Row 3: FWD
    } else {
      lines.addAll(outfieldRows.reversed()) // FWD, MID, DEF
      lines.add(gk) // GK at bottom
    }
  } else {
    // Standard default 4-3-3 split
    val gk = starters.take(1)
    val def = starters.drop(1).take(4)
    val mid = starters.drop(5).take(3)
    val fwd = starters.drop(8).take(3)

    if (isHome) {
      lines.add(gk)
      if (def.isNotEmpty()) lines.add(def)
      if (mid.isNotEmpty()) lines.add(mid)
      if (fwd.isNotEmpty()) lines.add(fwd)
    } else {
      if (fwd.isNotEmpty()) lines.add(fwd)
      if (mid.isNotEmpty()) lines.add(mid)
      if (def.isNotEmpty()) lines.add(def)
      lines.add(gk)
    }
  }

  return lines
}

@Composable
private fun TacticalPitchPlayerBadge(
  player: PlayerLineup,
  isHome: Boolean,
  isCompact: Boolean,
  onClick: () -> Unit
) {
  val circleSize = if (isCompact) 22.dp else 28.dp
  val fontSize = if (isCompact) 8.5.sp else 9.5.sp
  val nameWidth = if (isCompact) 58.dp else 72.dp

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .padding(horizontal = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .size(circleSize)
        .clip(CircleShape)
        .background(if (isHome) Color(0xFFDC2626) else Color(0xFF2563EB))
        .border(1.5.dp, if (player.isCaptain) GoldYellow else Color.White, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "${player.number}",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = if (isCompact) 9.sp else 11.sp
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    // High contrast dark pill background for player name so it's 100% visible on grass
    Surface(
      color = Color(0xF20B132B),
      shape = RoundedCornerShape(4.dp),
      border = BorderStroke(0.6.dp, Color(0x66FFFFFF)),
      modifier = Modifier.widthIn(min = 36.dp, max = nameWidth)
    ) {
      Text(
        text = player.name.split(" ").lastOrNull() ?: player.name,
        color = Color.White,
        fontSize = fontSize,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
      )
    }

    if (!isCompact) {
      Text(
        text = "★ ${player.rating}",
        color = GoldYellow,
        fontSize = 7.5.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
private fun FootballPositionBadge(position: String, modifier: Modifier = Modifier) {
  val (color, text) = when (position.uppercase()) {
    "GK", "KL" -> Color(0xFFEAB308) to "KL"
    "DEF", "DF", "STP", "SOL BEK", "SAĞ BEK" -> Color(0xFF3B82F6) to "DF"
    "MID", "OS", "ÖNL", "ON NUMARA", "KNT" -> Color(0xFF10B981) to "OS"
    "FWD", "FV", "SNT", "FORVET" -> Color(0xFFEF4444) to "FV"
    else -> Color(0xFF64748B) to position.take(2)
  }

  Surface(
    color = color.copy(alpha = 0.2f),
    shape = RoundedCornerShape(3.dp),
    modifier = modifier
  ) {
    Text(
      text = text,
      color = color,
      fontSize = 9.sp,
      fontWeight = FontWeight.Black,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(vertical = 1.dp)
    )
  }
}
