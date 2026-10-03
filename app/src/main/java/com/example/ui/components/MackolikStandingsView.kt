package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.MackolikMultiSportStatsDataSource
import com.example.data.datasource.MackolikStandingsAndOddsDataSource
import com.example.data.datasource.MackolikComprehensivePlayerDatabase
import com.example.data.datasource.MackolikExpanded84SportsDataSource
import com.example.data.datasource.ExpandedStandingRow
import com.example.data.datasource.ExpandedFixtureRow
import com.example.data.model.Sport
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

@Composable
fun MackolikStandingsView(
  modifier: Modifier = Modifier
) {
  var selectedSubTab by remember { mutableIntStateOf(0) } // 0: D3 Puan Tablosu, 1: Klasik Tablo, 2: Gol & Asist, 3: Basketbol, 4: Motor, 5: Tenis, 6: Sporcu, 7: 84 Branş

  val subTabs = listOf(
    "📊 D3 Puan Tablosu",
    "🏆 Puan Durumu",
    "👑 Gol & Asist",
    "🏀 Basketbol",
    "🏎️ Motor Sporları",
    "🎾 Tenis & Voleybol",
    "⭐ 50K+ Sporcu",
    "🌐 84 Branş (Nesine & Maçkolik)"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("📊", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Maçkolik Canlı İstatistik & Puan Merkezi",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            maxLines = 1,
            softWrap = false
          )
        }
        Surface(
          color = Color(0xFFEFF6FF),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = "Anlık Canlı Tablo",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D4ED8),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            maxLines = 1,
            softWrap = false
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Sub-Tabs Row
      ScrollableTabRow(
        selectedTabIndex = selectedSubTab,
        edgePadding = 0.dp,
        containerColor = Color(0xFFF8FAFC),
        contentColor = TealDark,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
      ) {
        subTabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedSubTab == index,
            onClick = { selectedSubTab = index },
            text = {
              Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (selectedSubTab == index) FontWeight.Black else FontWeight.Normal,
                color = if (selectedSubTab == index) TealDark else Color(0xFF64748B),
                maxLines = 1,
                softWrap = false
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      when (selectedSubTab) {
        0 -> D3LiveStandingsVisualization(initialLeague = "Trendyol Süper Lig")
        1 -> StandingsTableSection()
        2 -> TopScorersAndAssistsSection()
        3 -> BasketballLeadersSection()
        4 -> MotorsportsStandingsSection()
        5 -> TennisAndVolleyballSection()
        6 -> StandingsPlayerDirectorySection()
        7 -> Expanded84SportsSection()
      }
    }
  }
}

@Composable
private fun StandingsTableSection() {
  val leagues = listOf(
    "Trendyol Süper Lig",
    "Premier League",
    "La Liga",
    "EuroLeague",
    "NBA",
    "BSL"
  )
  var selectedLeague by remember { mutableStateOf(leagues.first()) }
  val standings = remember(selectedLeague) {
    MackolikStandingsAndOddsDataSource.getStandingsForLeague(selectedLeague)
  }

  // League Selector Chips
  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(leagues) { league ->
      val isSelected = selectedLeague == league
      FilterChip(
        selected = isSelected,
        onClick = { selectedLeague = league },
        label = {
          Text(
            text = league,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = TealDark,
          selectedLabelColor = Color.White,
          containerColor = Color(0xFFF1F5F9),
          labelColor = Color(0xFF334155)
        )
      )
    }
  }

  Spacer(modifier = Modifier.height(8.dp))

  // Table Header
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
      .padding(vertical = 6.dp, horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
    Text("TAKIM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f), maxLines = 1, softWrap = false)
    Text("O", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
    Text("G", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
    Text("B", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
    Text("M", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
    Text("AV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(26.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
    Text("P", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(26.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
    Text("FORM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(55.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
  }

  Spacer(modifier = Modifier.height(4.dp))

  // Standings Table Rows
  standings.take(10).forEach { row ->
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 5.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier.width(20.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Color(row.zoneColor)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${row.rank}",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            softWrap = false
          )
        }
      }

      Text(
        text = row.teamName,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF1E293B),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        softWrap = false,
        modifier = Modifier
          .weight(1f)
          .padding(start = 4.dp)
      )

      Text("${row.played}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("${row.won}", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("${row.drawn}", fontSize = 11.sp, color = Color(0xFF64748B), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text("${row.lost}", fontSize = 11.sp, color = Color(0xFFDC2626), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, maxLines = 1, softWrap = false)
      Text(
        text = if (row.goalDifference > 0) "+${row.goalDifference}" else "${row.goalDifference}",
        fontSize = 10.sp,
        color = Color(0xFF475569),
        modifier = Modifier.width(26.dp),
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false
      )
      Text(
        text = "${row.points}",
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF0F172A),
        modifier = Modifier.width(26.dp),
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false
      )

      Row(
        modifier = Modifier.width(55.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        row.form.takeLast(4).forEach { f ->
          val (bg, txt) = when (f) {
            "G" -> Pair(Color(0xFF10B981), "G")
            "B" -> Pair(Color(0xFF94A3B8), "B")
            else -> Pair(Color(0xFFEF4444), "M")
          }
          Box(
            modifier = Modifier
              .padding(horizontal = 1.dp)
              .size(11.dp)
              .clip(CircleShape)
              .background(bg),
            contentAlignment = Alignment.Center
          ) {
            Text(txt, fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}

@Composable
private fun TopScorersAndAssistsSection() {
  val leagues = listOf("Trendyol Süper Lig", "Premier League", "La Liga")
  var selectedLeague by remember { mutableStateOf(leagues.first()) }
  var statView by remember { mutableStateOf("Gol Krallığı") } // "Gol Krallığı" or "Asist Krallığı"

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      items(leagues) { l ->
        FilterChip(
          selected = selectedLeague == l,
          onClick = { selectedLeague = l },
          label = { Text(l, fontSize = 10.sp) },
          colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealDark, selectedLabelColor = Color.White)
        )
      }
    }
  }

  Spacer(modifier = Modifier.height(6.dp))

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    listOf("Gol Krallığı", "Asist Krallığı").forEach { tab ->
      val isSelected = statView == tab
      Surface(
        color = if (isSelected) TealDark else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .weight(1f)
          .clickable { statView = tab }
      ) {
        Text(
          text = if (tab == "Gol Krallığı") "⚽ $tab" else "🎯 $tab",
          color = if (isSelected) Color.White else Color(0xFF334155),
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(vertical = 5.dp)
        )
      }
    }
  }

  Spacer(modifier = Modifier.height(8.dp))

  if (statView == "Gol Krallığı") {
    val scorers = remember(selectedLeague) { MackolikMultiSportStatsDataSource.getTopScorers(selectedLeague) }
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
        .padding(vertical = 5.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("OYUNCU & TAKIM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
      Text("MAÇ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
      Text("GOL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
      Text("PEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
      Text("xG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(34.dp), textAlign = TextAlign.Center)
    }

    scorers.forEach { s ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("${s.rank}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (s.rank <= 3) GoldYellow else Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Column(modifier = Modifier.weight(1f)) {
          Text(s.player, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          Text(s.team, fontSize = 9.sp, color = Color(0xFF64748B))
        }
        Text("${s.matches}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(32.dp)) {
          Text("${s.goals}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF15803D), textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 1.dp))
        }
        Text("${s.penalties}", fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
        Text("${s.xg}", fontSize = 10.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold, modifier = Modifier.width(34.dp), textAlign = TextAlign.Center)
      }
    }
  } else {
    val assists = remember(selectedLeague) { MackolikMultiSportStatsDataSource.getTopAssists(selectedLeague) }
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
        .padding(vertical = 5.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("OYUNCU & TAKIM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
      Text("MAÇ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
      Text("ASİST", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
      Text("K.PAS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
    }

    assists.forEach { a ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("${a.rank}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (a.rank <= 3) Color(0xFF0284C7) else Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Column(modifier = Modifier.weight(1f)) {
          Text(a.player, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          Text(a.team, fontSize = 9.sp, color = Color(0xFF64748B))
        }
        Text("${a.matches}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
        Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(36.dp)) {
          Text("${a.assists}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0369A1), textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 1.dp))
        }
        Text("${a.keyPasses}", fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
      }
    }
  }
}

@Composable
private fun BasketballLeadersSection() {
  val tournaments = listOf("EuroLeague", "NBA")
  var selectedTour by remember { mutableStateOf("EuroLeague") }
  var statType by remember { mutableStateOf("Sayı") }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      tournaments.forEach { t ->
        FilterChip(
          selected = selectedTour == t,
          onClick = { selectedTour = t },
          label = { Text(t, fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealDark, selectedLabelColor = Color.White)
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      listOf("Sayı", "Ribaund", "Asist").forEach { st ->
        Surface(
          color = if (statType == st) Color(0xFFF97316) else Color(0xFFF1F5F9),
          shape = RoundedCornerShape(4.dp),
          modifier = Modifier.clickable { statType = st }
        ) {
          Text(
            text = st,
            color = if (statType == st) Color.White else Color(0xFF334155),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }
    }
  }

  Spacer(modifier = Modifier.height(8.dp))

  val leaders = remember(selectedTour, statType) {
    MackolikMultiSportStatsDataSource.getBasketballLeaders(selectedTour, statType)
  }

  // Header
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
      .padding(vertical = 5.dp, horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
    Text("OYUNCU & TAKIM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
    Text("MAÇ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
    Text("ORTALAMA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(65.dp), textAlign = TextAlign.End)
  }

  leaders.forEach { l ->
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("${l.rank}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Column(modifier = Modifier.weight(1f)) {
        Text(l.player, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Text(l.team, fontSize = 9.sp, color = Color(0xFF64748B))
      }
      Text("${l.gamesPlayed}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
      Surface(color = Color(0xFFFFEDD5), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(65.dp)) {
        Text(
          text = "${l.statValue}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFFC2410C),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(vertical = 2.dp)
        )
      }
    }
  }
}

@Composable
private fun MotorsportsStandingsSection() {
  val seriesList = listOf("Formula 1", "MotoGP", "WRC Ralli")
  var selectedSeries by remember { mutableStateOf("Formula 1") }
  val standings = remember(selectedSeries) {
    MackolikMultiSportStatsDataSource.getMotorsportsStandings(selectedSeries)
  }

  LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    items(seriesList) { s ->
      FilterChip(
        selected = selectedSeries == s,
        onClick = { selectedSeries = s },
        label = { Text(s, fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealDark, selectedLabelColor = Color.White)
      )
    }
  }

  Spacer(modifier = Modifier.height(8.dp))

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
      .padding(vertical = 5.dp, horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
    Text("PİLOT & TAKIM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
    Text("GAL.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
    Text("PODYUM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(42.dp), textAlign = TextAlign.Center)
    Text("PUAN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(38.dp), textAlign = TextAlign.Center)
  }

  standings.forEach { m ->
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("${m.rank}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Column(modifier = Modifier.weight(1f)) {
        Text(m.driver, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Text(m.team, fontSize = 9.sp, color = Color(0xFF64748B))
      }
      Text("${m.wins}", fontSize = 10.sp, color = Color(0xFF475569), modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
      Text("${m.podiums}", fontSize = 10.sp, color = Color(0xFF475569), modifier = Modifier.width(42.dp), textAlign = TextAlign.Center)
      Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(38.dp)) {
        Text(
          text = "${m.points}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF92400E),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(vertical = 1.dp)
        )
      }
    }
  }
}

@Composable
private fun TennisAndVolleyballSection() {
  val tennisRankings = remember { MackolikMultiSportStatsDataSource.getTennisRankings() }
  val volleyballStandings = remember { MackolikMultiSportStatsDataSource.getVolleyballStandings() }
  var subBranch by remember { mutableStateOf("ATP Tenis") } // "ATP Tenis" vs "Sultanlar Ligi"

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    listOf("ATP Tenis Sıralaması", "Voleybol Sultanlar Ligi").forEach { b ->
      val isSelected = (b.startsWith("ATP") && subBranch == "ATP Tenis") || (b.startsWith("Voleybol") && subBranch == "Sultanlar Ligi")
      Surface(
        color = if (isSelected) TealDark else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .weight(1f)
          .clickable { subBranch = if (b.startsWith("ATP")) "ATP Tenis" else "Sultanlar Ligi" }
      ) {
        Text(
          text = b,
          color = if (isSelected) Color.White else Color(0xFF334155),
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(vertical = 6.dp)
        )
      }
    }
  }

  Spacer(modifier = Modifier.height(8.dp))

  if (subBranch == "ATP Tenis") {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
        .padding(vertical = 5.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("TENİSÇİ & ÜLKE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
      Text("DEĞİŞİM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(45.dp), textAlign = TextAlign.Center)
      Text("PUAN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(45.dp), textAlign = TextAlign.End)
    }

    tennisRankings.forEach { t ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("${t.rank}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Column(modifier = Modifier.weight(1f)) {
          Text(t.player, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          Text(t.country, fontSize = 9.sp, color = Color(0xFF64748B))
        }
        Text(t.movement, fontSize = 10.sp, color = if (t.movement.contains("▲")) Color(0xFF16A34A) else Color(0xFF64748B), modifier = Modifier.width(45.dp), textAlign = TextAlign.Center)
        Text("${t.points}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(45.dp), textAlign = TextAlign.End)
      }
    }
  } else {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
        .padding(vertical = 5.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("TAKIM & SAYI LİDERİ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
      Text("O", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("G", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("M", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("P", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
    }

    volleyballStandings.forEach { v ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("${v.rank}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Column(modifier = Modifier.weight(1f)) {
          Text(v.team, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          Text("👑 ${v.topScorer}", fontSize = 9.sp, color = Color(0xFF0284C7))
        }
        Text("${v.played}", fontSize = 10.sp, color = Color(0xFF475569), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Text("${v.won}", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Text("${v.lost}", fontSize = 10.sp, color = Color(0xFFDC2626), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Text("${v.points}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(25.dp), textAlign = TextAlign.Center)
      }
    }
  }
}

@Composable
private fun StandingsPlayerDirectorySection() {
  var searchQuery by remember { mutableStateOf("") }
  var selectedSport by remember { mutableStateOf("Tümü") }
  val sportFilters = listOf("Tümü", "Futbol", "Basketbol", "Voleybol", "Tenis", "Motor Sporları", "Hentbol")

  val players = remember(searchQuery, selectedSport) {
    MackolikComprehensivePlayerDatabase.searchPlayers(
      query = searchQuery,
      sportFilter = selectedSport,
      limit = 35
    )
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Info Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
        .padding(horizontal = 10.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("⭐", fontSize = 13.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "58.450+ Tescilli Sporcu Havuzu",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0F172A)
        )
      }
      Surface(
        color = Color(0xFFDCFCE7),
        shape = RoundedCornerShape(4.dp)
      ) {
        Text(
          text = "Doğrulanmış Kadrolar",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF16A34A),
          modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Search input
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier.fillMaxWidth(),
      placeholder = { Text("50.000+ sporcu veya kulüp adı ara...", fontSize = 11.sp) },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara", tint = TealDark) },
      singleLine = true,
      shape = RoundedCornerShape(8.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF8FAFC),
        unfocusedContainerColor = Color(0xFFF8FAFC),
        focusedBorderColor = TealDark,
        unfocusedBorderColor = Color(0xFFE2E8F0)
      )
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Filter chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(sportFilters) { sport ->
        FilterChip(
          selected = selectedSport == sport,
          onClick = { selectedSport = sport },
          label = { Text(sport, fontSize = 10.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TealDark,
            selectedLabelColor = Color.White
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Player Rows Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
        .padding(vertical = 5.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
      Text("SPORCU & KULÜP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
      Text("MEVKİ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
      Text("DEĞER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(55.dp), textAlign = TextAlign.End)
      Text("PUAN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
    }

    players.forEach { p ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("${p.number}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
        Column(modifier = Modifier.weight(1f)) {
          Text(p.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
          Text("${p.team} • ${p.nationality}", fontSize = 9.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(p.position, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569), modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
        Text(p.marketValue, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488), modifier = Modifier.width(55.dp), textAlign = TextAlign.End)
        Text(String.format(java.util.Locale.US, "%.1f", p.rating), fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF16A34A), modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
      }
    }
  }
}

@Composable
private fun Expanded84SportsSection() {
  val categories = listOf(
    "🏓 Masa Tenisi",
    "🏈 NFL Amerikan Futbolu",
    "⚾ MLB Beyzbol",
    "🥋 MMA / UFC",
    "🎱 Snooker & Dart",
    "🏏 Kriket",
    "🏉 Rugby",
    "♟️ Satranç",
    "🤽 Sutopu",
    "🏸 Badminton & Padel"
  )
  var selectedCategory by remember { mutableStateOf(categories.first()) }
  val standings = remember(selectedCategory) {
    MackolikExpanded84SportsDataSource.getStandingsForCategory(selectedCategory)
  }

  val selectedSport = when {
    selectedCategory.contains("Masa Tenisi") -> Sport.TABLE_TENNIS
    selectedCategory.contains("NFL") -> Sport.AMERICAN_FOOTBALL
    selectedCategory.contains("Beyzbol") -> Sport.BASEBALL
    selectedCategory.contains("MMA") -> Sport.MMA_UFC
    selectedCategory.contains("Snooker") -> Sport.SNOOKER
    selectedCategory.contains("Kriket") -> Sport.CRICKET
    selectedCategory.contains("Rugby") -> Sport.RUGBY_UNION
    selectedCategory.contains("Satranç") -> Sport.CHESS
    selectedCategory.contains("Sutopu") -> Sport.WATER_POLO
    selectedCategory.contains("Badminton") -> Sport.BADMINTON
    else -> null
  }

  val fixtures = remember(selectedSport) {
    MackolikExpanded84SportsDataSource.getFixturesBySport(selectedSport)
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Category chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(categories) { cat ->
        val isSelected = selectedCategory == cat
        FilterChip(
          selected = isSelected,
          onClick = { selectedCategory = cat },
          label = {
            Text(
              text = cat,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GoldYellow,
            selectedLabelColor = Color(0xFF0F172A),
            containerColor = Color(0xFFF1F5F9),
            labelColor = Color(0xFF475569)
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Information banner
    Surface(
      color = Color(0xFF0F172A),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "$selectedCategory Ligi & Sıralaması",
            color = GoldYellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Nesine & Maçkolik Resmi Canlı Veri Akışı",
            color = Color(0xFF94A3B8),
            fontSize = 9.sp
          )
        }
        Surface(
          color = Color(0xFF10B981),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "CANLI TELEMETRİ",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Standings Table Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp))
        .padding(vertical = 5.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
      Text("KATILIMCI / SPORCU", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
      Text("O", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("G", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("M", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
      Text("SKOR / ELO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), modifier = Modifier.width(70.dp), textAlign = TextAlign.End)
    }

    // Standings Rows
    standings.forEach { row ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("${row.rank}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
        Column(modifier = Modifier.weight(1f)) {
          Text(row.teamOrAthlete, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
          Text("${row.countryOrCity} • ${row.detailNote}", fontSize = 9.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text("${row.played}", fontSize = 10.sp, color = Color(0xFF475569), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Text("${row.won}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Text("${row.lost}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Text(row.pointsOrScore, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0284C7), modifier = Modifier.width(70.dp), textAlign = TextAlign.End)
      }
    }

    // Fixtures Section for Selected Sport
    if (fixtures.isNotEmpty()) {
      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = "⚡ Canlı Karşılaşmalar & Simülasyon Oranları",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0F172A)
      )
      Spacer(modifier = Modifier.height(6.dp))

      fixtures.forEach { fx ->
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFF8FAFC),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = fx.tournament,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
              )
              Surface(
                color = if (fx.status == "CANLI") Color(0xFFEF4444) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = fx.status,
                  color = if (fx.status == "CANLI") Color.White else Color(0xFF334155),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = fx.participant1,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0F172A),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = fx.participant2,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0F172A),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "${fx.score1} - ${fx.score2}",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF0F172A)
                )
                Text(
                  text = fx.periodOrDetail,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF0284C7)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Odds Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White,
                modifier = Modifier.weight(1f)
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 5.dp, horizontal = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                  Text(String.format(java.util.Locale.US, "%.2f", fx.odd1), fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                }
              }

              if (fx.oddDraw > 1.0) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color.White,
                  modifier = Modifier.weight(1f)
                ) {
                  Row(
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("X", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Text(String.format(java.util.Locale.US, "%.2f", fx.oddDraw), fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                  }
                }
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White,
                modifier = Modifier.weight(1f)
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 5.dp, horizontal = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                  Text(String.format(java.util.Locale.US, "%.2f", fx.odd2), fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                }
              }
            }
          }
        }
      }
    }
  }
}
