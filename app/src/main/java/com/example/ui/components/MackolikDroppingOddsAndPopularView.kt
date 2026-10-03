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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.MackolikStandingsAndOddsDataSource
import com.example.data.model.MarketType
import com.example.data.model.SlipSelection
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

@Composable
fun MackolikDroppingOddsAndPopularView(
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: Düşen Oranlar, 1: En Çok Oynananlar

  val droppingOdds = remember { MackolikStandingsAndOddsDataSource.getDroppingOdds() }
  val popularBets = remember { MackolikStandingsAndOddsDataSource.getPopularBets() }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Tab selector: Düşen Oranlar vs En Çok Oynananlar
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFFF8FAFC),
        contentColor = TealDark,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = TealDark,
            height = 3.dp
          )
        },
        divider = {}
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("📉", fontSize = 13.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Düşen Oranlar",
                fontSize = 12.sp,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🔥", fontSize = 13.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "En Çok Oynananlar",
                fontSize = 12.sp,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (selectedTab == 0) {
        // Düşen Oranlar Listesi
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          droppingOdds.forEach { item ->
            val isSelected = selectedSelections.any { it.matchId == item.matchId }
            Surface(
              color = if (isSelected) Color(0xFFE6FFFA) else Color(0xFFF8FAFC),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onSelectOdd(
                    SlipSelection(
                      matchId = item.matchId,
                      matchTeams = "${item.homeTeam} - ${item.awayTeam}",
                      marketType = MarketType.MATCH_RESULT,
                      selectionId = "sel_${item.marketName}",
                      selectionName = item.marketName,
                      odd = item.currentOdd,
                      isLive = true
                    )
                  )
                }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = item.league,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      color = Color(0xFFFFE4E6),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "▼ %${item.dropPercentage} Düşüş",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = LiveRed,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(
                    text = "${item.homeTeam} - ${item.awayTeam}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = item.reason,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Odds Box
                Surface(
                  color = if (isSelected) TealDark else Color(0xFFE2E8F0),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.padding(start = 4.dp)
                ) {
                  Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = item.marketName,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) GoldYellow else Color(0xFF334155)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "${item.initialOdd}",
                        fontSize = 9.sp,
                        color = if (isSelected) Color.LightGray else Color(0xFF94A3B8),
                        textDecoration = TextDecoration.LineThrough
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "${item.currentOdd}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.White else Color(0xFF0F172A)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        // En Çok Oynananlar Listesi
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          popularBets.forEach { item ->
            val isSelected = selectedSelections.any { it.matchId == item.matchId }
            Surface(
              color = if (isSelected) Color(0xFFE6FFFA) else Color(0xFFF8FAFC),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onSelectOdd(
                    SlipSelection(
                      matchId = item.matchId,
                      matchTeams = "${item.homeTeam} - ${item.awayTeam}",
                      marketType = MarketType.MATCH_RESULT,
                      selectionId = "sel_${item.selectionName}",
                      selectionName = item.selectionName,
                      odd = item.odd,
                      isLive = true
                    )
                  )
                }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = item.league,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      color = Color(0xFFFEF3C7),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "🔥 %${item.percentagePlayed} Tercih",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(
                    text = "${item.homeTeam} - ${item.awayTeam}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                  Text(
                    text = "${item.totalBetsPlaced} kuponda yer aldı",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                  color = if (isSelected) TealDark else Color(0xFFE2E8F0),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = item.selectionName,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) GoldYellow else Color(0xFF334155)
                    )
                    Text(
                      text = "${item.odd}",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Black,
                      color = if (isSelected) Color.White else Color(0xFF0F172A)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
