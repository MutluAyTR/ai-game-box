package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.MotorsportsMackolikDataSource
import com.example.data.model.MarketType
import com.example.data.model.SlipSelection
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

enum class MotorsportSeries(val title: String, val icon: String) {
  MOTOGP("MotoGP Grand Prix", "🏍️"),
  WRC("WRC Dünya Rallisi", "🏎️")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorsportsHubDialog(
  initialSeries: MotorsportSeries = MotorsportSeries.MOTOGP,
  selectedSelections: List<SlipSelection> = emptyList(),
  onSelectOdd: (SlipSelection) -> Unit = {},
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var currentSeries by remember { mutableStateOf(initialSeries) }
  var selectedTab by remember { mutableIntStateOf(0) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFFF8FAFC),
    modifier = Modifier.testTag("motorsports_hub_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.94f)
    ) {
      // 1. Header Bar: Series Switcher & Close
      Surface(
        color = Color(0xFF0F172A),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🏁", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "MAÇKOLİK MOTOR SPORLARI MERKEZİ",
                  color = GoldYellow,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "Resmi Fikstür, Puan Durumu, Canlı Yarış & Nasıl Oynanır",
                  color = Color(0xFF94A3B8),
                  fontSize = 10.sp
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(28.dp)
                .background(Color(0x33FFFFFF), CircleShape)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White, modifier = Modifier.size(16.dp))
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Series Switcher: MotoGP vs WRC
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            MotorsportSeries.values().forEach { series ->
              val isSelected = currentSeries == series
              Surface(
                color = if (isSelected) TealDark else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .clickable {
                    currentSeries = series
                  }
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 8.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = series.icon, fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = series.title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFF94A3B8)
                  )
                }
              }
            }
          }
        }
      }

      // 2. Navigation Sub-Tabs (Canlı Yarış, Puan Tablosu, Fikstür & Takvim, Nasıl Oynanır, Kim Kazanır)
      val tabs = listOf(
        "🏁 Canlı & Grid",
        "🏆 Puan Durumu",
        "📅 2026 Fikstür",
        "📖 Nasıl Oynanır?",
        "🤖 Kim Kazanır?"
      )

      ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        edgePadding = 12.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                color = if (selectedTab == index) TealDark else Color(0xFF64748B)
              )
            }
          )
        }
      }

      // 3. Tab Contents
      Box(modifier = Modifier.fillMaxSize()) {
        when (currentSeries) {
          MotorsportSeries.MOTOGP -> {
            when (selectedTab) {
              0 -> MotoGpLiveGridTab()
              1 -> MotoGpStandingsTab()
              2 -> MotoGpCalendarTab()
              3 -> MotoGpBettingGuideTab()
              4 -> MotoGpPredictionAiTab(
                selectedSelections = selectedSelections,
                onSelectOdd = onSelectOdd
              )
            }
          }
          MotorsportSeries.WRC -> {
            when (selectedTab) {
              0 -> WrcLiveStagesTab()
              1 -> WrcStandingsTab()
              2 -> WrcCalendarTab()
              3 -> WrcBettingGuideTab()
              4 -> WrcPredictionAiTab(
                selectedSelections = selectedSelections,
                onSelectOdd = onSelectOdd
              )
            }
          }
        }
      }
    }
  }
}

// ==========================================
// MOTOGP TAB VIEW IMPLEMENTATIONS
// ==========================================

@Composable
private fun MotoGpLiveGridTab() {
  val circuit = MotorsportsMackolikDataSource.motoGpCircuit
  val grid = MotorsportsMackolikDataSource.motoGpStartingGrid

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    // Current Grand Prix Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(color = LiveRed, shape = RoundedCornerShape(4.dp)) {
                Text(
                  text = "CANLI BÜLTEN",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🇸🇲 San Marino Misano GP",
                color = GoldYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "27 Tur (114.1 km)",
              color = Color(0xFF94A3B8),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = circuit["name"] ?: "",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = circuit["location"] ?: "",
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Circuit Specs Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Column(
              modifier = Modifier
                .weight(1f)
                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text("Pist Uzunluğu", fontSize = 9.sp, color = Color(0xFF94A3B8))
              Text(circuit["length"] ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(
              modifier = Modifier
                .weight(1f)
                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text("Virajlar", fontSize = 9.sp, color = Color(0xFF94A3B8))
              Text(circuit["corners"] ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(
              modifier = Modifier
                .weight(1f)
                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text("Pist Rekoru", fontSize = 9.sp, color = Color(0xFF94A3B8))
              Text("1:30.390 (Bagnaia)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "🚦 Sıralama Turları & Grid Dizilimi",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )
        Text(text = "Q2 Sonuçları", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(grid) { item ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (item.gridPos == 1) Color(0xFFFEF3C7) else Color.White
        ),
        border = BorderStroke(1.dp, if (item.gridPos == 1) GoldYellow else Color(0xFFE2E8F0))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Pos badge
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(if (item.gridPos == 1) GoldYellow else Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "P${item.gridPos}",
              fontWeight = FontWeight.Black,
              fontSize = 11.sp,
              color = if (item.gridPos == 1) Color(0xFF78350F) else Color.White
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.rider,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color(0xFF0F172A)
            )
            Text(
              text = item.team,
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = item.q2Time,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = if (item.gridPos == 1) Color(0xFFB45309) else TealDark
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = item.gap,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${item.topSpeedKmh} km/s",
                fontSize = 9.sp,
                color = Color(0xFF0F766E),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MotoGpStandingsTab() {
  val drivers = MotorsportsMackolikDataSource.motoGpDriversStandings
  val teams = MotorsportsMackolikDataSource.motoGpTeamsStandings
  var viewMode by remember { mutableStateOf("drivers") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Surface(
          color = if (viewMode == "drivers") TealDark else Color.Transparent,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { viewMode = "drivers" }
        ) {
          Text(
            text = "🏍️ Sürücüler Klasmanı (${drivers.size})",
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (viewMode == "drivers") Color.White else Color(0xFF334155),
            modifier = Modifier.padding(vertical = 6.dp)
          )
        }

        Surface(
          color = if (viewMode == "teams") TealDark else Color.Transparent,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { viewMode = "teams" }
        ) {
          Text(
            text = "🏭 Takımlar & Markalar (${teams.size})",
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (viewMode == "teams") Color.White else Color(0xFF334155),
            modifier = Modifier.padding(vertical = 6.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    if (viewMode == "drivers") {
      items(drivers) { driver ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(
            containerColor = when (driver.rank) {
              1 -> Color(0xFFFEF3C7)
              2 -> Color(0xFFF1F5F9)
              3 -> Color(0xFFFFF7ED)
              else -> Color.White
            }
          ),
          border = BorderStroke(
            1.dp,
            when (driver.rank) {
              1 -> GoldYellow
              2 -> Color(0xFFCBD5E1)
              3 -> Color(0xFFFDBA74)
              else -> Color(0xFFE2E8F0)
            }
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${driver.rank}.",
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color(0xFF0F172A),
              modifier = Modifier.width(24.dp)
            )

            Text(text = driver.countryFlag, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = driver.name,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  color = Color(0xFFE2E8F0),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "#${driver.number}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "${driver.team} • ${driver.bike}",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${driver.points} P",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = TealDark
              )
              Text(
                text = "${driver.wins}G • ${driver.podiums}Podyum",
                fontSize = 9.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    } else {
      items(teams) { team ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${team.rank}.",
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color(0xFF0F172A),
              modifier = Modifier.width(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = team.name,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "Motosiklet: ${team.bike}",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${team.points} P",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = TealDark
              )
              Text(
                text = "${team.wins} Galibiyet",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MotoGpCalendarTab() {
  val calendar = MotorsportsMackolikDataSource.motoGpSeasonCalendar

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "📅 2026 MotoGP Sezon Takvimi",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )
        Text(
          text = "Toplam 18 Grand Prix",
          fontSize = 11.sp,
          color = Color(0xFF64748B),
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    items(calendar) { event ->
      val isCurrent = event.status.contains("CANLI", ignoreCase = true)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isCurrent) Color(0xFFECFDF5) else Color.White
        ),
        border = BorderStroke(
          width = if (isCurrent) 1.5.dp else 1.dp,
          color = if (isCurrent) Color(0xFF059669) else Color(0xFFE2E8F0)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = event.flag, fontSize = 20.sp)
          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = event.grandPrix,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color(0xFF0F172A)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = if (isCurrent) Color(0xFF059669) else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = event.status,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrent) Color.White else Color(0xFF475569),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "${event.circuit} • ${event.laps} Tur",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = event.dateIso,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF334155)
            )
            if (event.winner.isNotBlank() && event.winner != "-") {
              Text(
                text = "🏆 ${event.winner}",
                fontSize = 10.sp,
                color = Color(0xFF059669),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MotoGpBettingGuideTab() {
  val guides = MotorsportsMackolikDataSource.motoGpBettingGuides

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF042F2E))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Maçkolik & İddaa MotoGP Nasıl Oynanır?",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "MotoGP bahisleri sıralama turları, sprint ve pazar günkü ana yarışta geçerlidir. İşte tüm marketler ve resmi kurallar:",
            color = Color(0xFFCCFBF1),
            fontSize = 11.sp
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    items(guides) { guide ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = guide.marketName,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = TealDark
            )
            Surface(
              color = Color(0xFFFEF3C7),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = guide.iddaaCode,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB45309),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(text = guide.description, fontSize = 11.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)

          Spacer(modifier = Modifier.height(6.dp))
          Row {
            Text(text = "📜 Kural: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
            Text(text = guide.rules, fontSize = 10.sp, color = Color(0xFF64748B))
          }

          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
              .padding(8.dp)
          ) {
            Text(text = "💡 Maçkolik Analiz İpucu: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
            Text(text = guide.tip, fontSize = 10.sp, color = Color(0xFF334155))
          }
        }
      }
    }
  }
}

@Composable
private fun MotoGpPredictionAiTab(
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  val drivers = MotorsportsMackolikDataSource.motoGpDriversStandings

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Yarışı Kim Kazanır? (AI & Maçkolik Olasılık)",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Misano Marco Simoncelli GP için son 5 yarış telemetrisi, grid pozisyonu ve hava sıcaklığı parametreleriyle hesaplanan kazanma olasılıkları.",
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    items(drivers.take(8)) { driver ->
      val selId = "motogp_win_${driver.number}"
      val isSelected = selectedSelections.any { it.selectionId == selId }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0xFFE2E8F0))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = driver.countryFlag, fontSize = 20.sp)
          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = driver.name,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color(0xFF0F172A)
            )
            Text(
              text = driver.team,
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "Kazanma İhtimali: ", fontSize = 10.sp, color = Color(0xFF64748B))
              Text(
                text = "%${driver.winProbability}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (driver.winProbability >= 30) Color(0xFF059669) else Color(0xFF334155)
              )
            }
          }

          // Oran & Kupona Ekle Butonu
          Surface(
            color = if (isSelected) GoldYellow else TealDark,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable {
              val sel = SlipSelection(
                matchId = "10004",
                matchTeams = "MotoGP: ${driver.name} (Kazanan)",
                marketType = MarketType.MOTORSPORTS_WINNER,
                selectionId = selId,
                selectionName = "${driver.name} (1.)",
                odd = driver.currentOdds,
                isLive = true
              )
              onSelectOdd(sel)
            }
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = if (isSelected) "Eklendi" else "Kupona Ekle",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF78350F) else Color.White
              )
              Text(
                text = "%.2f".format(driver.currentOdds),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) Color(0xFF78350F) else GoldYellow
              )
            }
          }
        }
      }
    }
  }
}

// ==========================================
// WRC DÜNYA RALLİSİ TAB VIEW IMPLEMENTATIONS
// ==========================================

@Composable
private fun WrcLiveStagesTab() {
  val info = MotorsportsMackolikDataSource.wrcRallyChileInfo
  val stages = MotorsportsMackolikDataSource.wrcChileStages

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(color = LiveRed, shape = RoundedCornerShape(4.dp)) {
                Text(
                  text = "CANLI RALLİ",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🇨🇱 Rally Chile Bio Bío",
                color = GoldYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "16 Özel Etap (306.7 km)",
              color = Color(0xFFC7D2FE),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = info["name"] ?: "",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "${info["location"]} • Zemin: ${info["surface"]}",
            color = Color(0xFFE0E7FF),
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Leader Box
          Surface(
            color = Color(0xFF312E81),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Genel Klasman Lideri:", fontSize = 9.sp, color = Color(0xFFA5B4FC))
                Text(info["overallLeader"] ?: "", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldYellow)
              }
              Text(
                text = info["gapToSecond"] ?: "",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "⏱️ Özel Etap Dereceleri (Stages)",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )
        Text(text = "SS1 - SS16", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(stages) { stage ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = stage.stageCode,
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "${stage.stageName} (${stage.distanceKm} km)",
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "Etap Galibi: ${stage.leaderDriver} (${stage.car})",
              fontSize = 10.sp,
              color = Color(0xFF475569)
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = stage.time,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = TealDark
            )
            Text(
              text = stage.gap,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF64748B)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun WrcStandingsTab() {
  val drivers = MotorsportsMackolikDataSource.wrcDriversStandings
  val manufacturers = MotorsportsMackolikDataSource.wrcManufacturersStandings
  var viewMode by remember { mutableStateOf("drivers") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Surface(
          color = if (viewMode == "drivers") TealDark else Color.Transparent,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { viewMode = "drivers" }
        ) {
          Text(
            text = "🏎️ Sürücüler Klasmanı (${drivers.size})",
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (viewMode == "drivers") Color.White else Color(0xFF334155),
            modifier = Modifier.padding(vertical = 6.dp)
          )
        }

        Surface(
          color = if (viewMode == "manufacturers") TealDark else Color.Transparent,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { viewMode = "manufacturers" }
        ) {
          Text(
            text = "🚗 Markalar Puan Durumu",
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (viewMode == "manufacturers") Color.White else Color(0xFF334155),
            modifier = Modifier.padding(vertical = 6.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    if (viewMode == "drivers") {
      items(drivers) { driver ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(
            containerColor = when (driver.rank) {
              1 -> Color(0xFFFEF3C7)
              2 -> Color(0xFFF1F5F9)
              3 -> Color(0xFFFFF7ED)
              else -> Color.White
            }
          ),
          border = BorderStroke(
            1.dp,
            when (driver.rank) {
              1 -> GoldYellow
              2 -> Color(0xFFCBD5E1)
              3 -> Color(0xFFFDBA74)
              else -> Color(0xFFE2E8F0)
            }
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${driver.rank}.",
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color(0xFF0F172A),
              modifier = Modifier.width(24.dp)
            )

            Text(text = driver.countryFlag, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = driver.name,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "Co-Pilot: ${driver.coDriver} • ${driver.team}",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${driver.points} P",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = TealDark
              )
              Text(
                text = "${driver.wins} Galibiyet",
                fontSize = 9.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    } else {
      items(manufacturers) { mfg ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${mfg.rank}.",
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color(0xFF0F172A),
              modifier = Modifier.width(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = mfg.name,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "Rally1 Aracı: ${mfg.car}",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${mfg.points} P",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = TealDark
              )
              Text(
                text = "${mfg.wins} Ralli Zaferi",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun WrcCalendarTab() {
  val calendar = MotorsportsMackolikDataSource.wrcSeasonCalendar

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "📅 2026 WRC Sezon Ralli Takvimi",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )
        Text(
          text = "Toplam 13 Ralli",
          fontSize = 11.sp,
          color = Color(0xFF64748B),
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    items(calendar) { event ->
      val isCurrent = event.status.contains("CANLI", ignoreCase = true)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isCurrent) Color(0xFFECFDF5) else Color.White
        ),
        border = BorderStroke(
          width = if (isCurrent) 1.5.dp else 1.dp,
          color = if (isCurrent) Color(0xFF059669) else Color(0xFFE2E8F0)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = event.flag, fontSize = 20.sp)
          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = event.rallyName,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color(0xFF0F172A)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = if (isCurrent) Color(0xFF059669) else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = event.status,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrent) Color.White else Color(0xFF475569),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "${event.surface} • ${event.stagesCount} Etap (${event.distanceKm} km)",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = event.dateIso,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF334155)
            )
            if (event.winner.isNotBlank() && event.winner != "-") {
              Text(
                text = "🏆 ${event.winner}",
                fontSize = 10.sp,
                color = Color(0xFF059669),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun WrcBettingGuideTab() {
  val guides = MotorsportsMackolikDataSource.wrcBettingGuides

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Maçkolik & İddaa WRC Ralli Nasıl Oynanır?",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "FIA Dünya Ralli Şampiyonası etap etap zamana karşı koşulur. İddaa ve Maçkolik bülteninde açılan popüler ralli bahisleri:",
            color = Color(0xFFE0E7FF),
            fontSize = 11.sp
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    items(guides) { guide ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = guide.marketName,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = TealDark
            )
            Surface(
              color = Color(0xFFFEF3C7),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = guide.iddaaCode,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB45309),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(text = guide.description, fontSize = 11.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)

          Spacer(modifier = Modifier.height(6.dp))
          Row {
            Text(text = "📜 Kural: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
            Text(text = guide.rules, fontSize = 10.sp, color = Color(0xFF64748B))
          }

          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
              .padding(8.dp)
          ) {
            Text(text = "💡 Maçkolik Analiz İpucu: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
            Text(text = guide.tip, fontSize = 10.sp, color = Color(0xFF334155))
          }
        }
      }
    }
  }
}

@Composable
private fun WrcPredictionAiTab(
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  val drivers = MotorsportsMackolikDataSource.wrcDriversStandings

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Ralliyi Kim Kazanır? (AI & Maçkolik Olasılık)",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Rally Chile Bio Bío orman çakıl etapları, pilotların zemin tecrübesi ve hibrit güç dengesine göre hesaplanan canlı kazanma ihtimalleri.",
            color = Color(0xFFE0E7FF),
            fontSize = 11.sp
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    items(drivers.take(8)) { driver ->
      val selId = "wrc_win_${driver.name.replace(" ", "_")}"
      val isSelected = selectedSelections.any { it.selectionId == selId }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0xFFE2E8F0))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = driver.countryFlag, fontSize = 20.sp)
          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = driver.name,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = Color(0xFF0F172A)
            )
            Text(
              text = driver.team,
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "Kazanma İhtimali: ", fontSize = 10.sp, color = Color(0xFF64748B))
              Text(
                text = "%${driver.winProbability}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (driver.winProbability >= 30) Color(0xFF059669) else Color(0xFF334155)
              )
            }
          }

          // Oran & Kupona Ekle Butonu
          Surface(
            color = if (isSelected) GoldYellow else TealDark,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable {
              val sel = SlipSelection(
                matchId = "10005",
                matchTeams = "WRC: ${driver.name} (Kazanan)",
                marketType = MarketType.MOTORSPORTS_WINNER,
                selectionId = selId,
                selectionName = "${driver.name} (1.)",
                odd = driver.currentOdds,
                isLive = true
              )
              onSelectOdd(sel)
            }
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = if (isSelected) "Eklendi" else "Kupona Ekle",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF78350F) else Color.White
              )
              Text(
                text = "%.2f".format(driver.currentOdds),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) Color(0xFF78350F) else GoldYellow
              )
            }
          }
        }
      }
    }
  }
}
