package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.datasource.MackolikComprehensivePlayerDatabase
import com.example.data.model.MackolikPlayerProfile
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

@Composable
fun MackolikPlayerDirectoryDialog(
  initialQuery: String = "",
  initialSport: String = "Tümü",
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf(initialQuery) }
  var selectedSport by remember { mutableStateOf(initialSport) }
  var selectedLeague by remember { mutableStateOf("Tümü") }
  var displayLimit by remember { mutableIntStateOf(60) }
  var selectedPlayerDetail by remember { mutableStateOf<MackolikPlayerProfile?>(null) }

  val sportTabs = listOf("Tümü", "Futbol", "Basketbol", "Voleybol", "Tenis", "Motor Sporları", "Hentbol")
  val leagueFilters = listOf("Tümü", "Süper Lig", "Premier League", "La Liga", "EuroLeague", "NBA", "Sultanlar Ligi", "Formula 1", "ATP & WTA", "Hentbol Süper Lig")

  val playerResults = remember(searchQuery, selectedSport, selectedLeague, displayLimit) {
    MackolikComprehensivePlayerDatabase.searchPlayers(
      query = searchQuery,
      sportFilter = selectedSport,
      leagueFilter = if (selectedLeague == "Tümü") "Tümü" else selectedLeague,
      limit = displayLimit
    )
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.94f)
        .testTag("mackolik_player_directory_dialog"),
      shape = RoundedCornerShape(20.dp),
      color = Color(0xFF0A0F1D),
      border = BorderStroke(1.5.dp, Color(0xFF1E293B))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(14.dp)
      ) {
        // 1. Header with Close Button & Stats Counter
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(TealDark),
              contentAlignment = Alignment.Center
            ) {
              Text("⭐", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Maçkolik Sporcu Veritabanı",
                  color = Color.White,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = "Doğrulanmış",
                  tint = GoldYellow,
                  modifier = Modifier.size(16.dp)
                )
              }
              Text(
                text = "${MackolikComprehensivePlayerDatabase.TOTAL_REGISTERED_PLAYERS.toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}+ Tescilli Sporcu • Asla Tekrar Etmeyen İsimler",
                color = Color(0xFF34D399),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_player_directory")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Kapat",
              tint = Color(0xFF94A3B8)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Search Input Field
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("player_search_input"),
          placeholder = {
            Text("50.000+ sporcu, kulüp, uyruk veya mevki ara...", fontSize = 12.sp, color = Color(0xFF64748B))
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Ara", tint = GoldYellow)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Temizle", tint = Color(0xFF94A3B8))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF131D31),
            unfocusedContainerColor = Color(0xFF0F172A),
            focusedBorderColor = GoldYellow,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Sport Branch Filter Chips (Futbol, Basketbol, Voleybol, Tenis, Motor, Hentbol)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          sportTabs.forEach { sport ->
            val isSelected = selectedSport == sport
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) TealDark else Color(0xFF1E293B),
              border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0xFF334155)),
              modifier = Modifier.clickable {
                selectedSport = sport
                displayLimit = 60
              }
            ) {
              val icon = when (sport) {
                "Futbol" -> "⚽"
                "Basketbol" -> "🏀"
                "Voleybol" -> "🏐"
                "Tenis" -> "🎾"
                "Motor Sporları" -> "🏎️"
                "Hentbol" -> "🤾"
                else -> "🏆"
              }
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(icon, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = sport,
                  color = if (isSelected) GoldYellow else Color(0xFFCBD5E1),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3.5 Quick League Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          leagueFilters.forEach { league ->
            val isSelected = selectedLeague == league
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isSelected) Color(0xFF0F766E) else Color(0xFF0F172A),
              border = BorderStroke(0.8.dp, if (isSelected) Color(0xFF2DD4BF) else Color(0xFF1E293B)),
              modifier = Modifier.clickable { selectedLeague = league }
            ) {
              Text(
                text = league,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Selected Player Preview (If clicked)
        selectedPlayerDetail?.let { p ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.5.dp, GoldYellow),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(TealDark),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("#${p.number}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp)
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(p.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                      if (p.isCaptain) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("(K)", color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Black)
                      }
                    }
                    Text("${p.team} • ${p.position} • ${p.nationality}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                  }
                }
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFF065F46)
                ) {
                  Text(
                    text = "${p.rating} ★",
                    color = Color(0xFF34D399),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Piyasa Değeri: ${p.marketValue}", color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text("Yaş: ${p.age} • ${p.league}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
              }
              if (p.statsSummary.isNotEmpty() || p.goals > 0 || p.assists > 0 || p.points > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                val statsText = when {
                  p.statsSummary.isNotEmpty() -> p.statsSummary
                  p.goals > 0 || p.assists > 0 -> "Sezon Performansı: ${p.goals} Gol, ${p.assists} Asist (${p.matchCount} Maç)"
                  p.points > 0 -> "İstatistikler: ${p.points} Sayı, ${p.rebounds} Ribaund"
                  else -> "Maçkolik Doğrulanmış Sporcu Profili"
                }
                Text(
                  text = "📊 $statsText",
                  color = Color(0xFF38BDF8),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }

        // 5. Results Count & List
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${playerResults.size} Sporcu Görüntüleniyor (50.000+ Havuzdan)",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
          )
          Text(
            text = "Benzersiz İsim Garantisi ✓",
            color = Color(0xFF10B981),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(playerResults, key = { it.id }) { player ->
            PlayerDirectoryRow(
              player = player,
              isSelected = selectedPlayerDetail?.id == player.id,
              onClick = { selectedPlayerDetail = if (selectedPlayerDetail?.id == player.id) null else player }
            )
          }

          item {
            Button(
              onClick = { displayLimit += 50 },
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("load_more_players_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, GoldYellow)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = "Daha Fazla", tint = GoldYellow, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Daha Fazla Sporcu Yükle (+50) • 58.450+ Havuz", color = GoldYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PlayerDirectoryRow(
  player: MackolikPlayerProfile,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31)
    ),
    border = BorderStroke(
      1.dp,
      if (isSelected) GoldYellow else Color(0xFF1E293B)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Forma Numarası Rozeti
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${player.number}",
            color = GoldYellow,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = player.name,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (player.isCaptain) {
              Spacer(modifier = Modifier.width(4.dp))
              Text("(K)", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = player.team,
              color = Color(0xFF38BDF8),
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = " • ${player.position}",
              color = Color(0xFF94A3B8),
              fontSize = 10.sp
            )
            Text(
              text = " • ${player.nationality}",
              color = Color(0xFF64748B),
              fontSize = 9.sp
            )
          }
        }
      }

      // Reyting & Değer Rozetleri
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = player.marketValue,
            color = GoldYellow,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = player.sport,
            color = Color(0xFF64748B),
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = when {
            player.rating >= 8.5 -> Color(0xFF16A34A)
            player.rating >= 7.5 -> Color(0xFF0D9488)
            else -> Color(0xFFD97706)
          }
        ) {
          Text(
            text = String.format(java.util.Locale.US, "%.1f", player.rating),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
