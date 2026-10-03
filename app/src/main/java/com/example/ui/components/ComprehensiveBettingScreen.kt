package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Market
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.service.BettingClosureStatus
import com.example.service.CalendarManagementService
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

enum class MarketCategory(val label: String) {
  ALL("Tüm Bahisler"),
  MAIN("Popüler / Sonuç"),
  PLAYER("Oyuncu Bahisleri"),
  HANDICAP("Handikap"),
  FIRST_HALF("İlk Yarı"),
  GOALS("Alt / Üst"),
  CORNERS("Korner"),
  CARDS("Kartlar"),
  SPECIAL("Özel / Ek Süre")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComprehensiveBettingScreen(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  var selectedCategory by remember { mutableStateOf(MarketCategory.ALL) }
  val closureStatus = remember(match.id) { CalendarManagementService.getClosureStatus(match) }
  val isLiveLocked85 = match.status == MatchStatus.LIVE && match.minute >= 85
  val isBettingClosed = closureStatus is BettingClosureStatus.Locked5MinWindow ||
      match.status == MatchStatus.FINISHED ||
      isLiveLocked85

  val filteredMarkets = when (selectedCategory) {
    MarketCategory.ALL -> match.markets
    MarketCategory.MAIN -> match.markets.filter {
      it.type == MarketType.MATCH_RESULT || it.type == MarketType.DOUBLE_CHANCE || it.type == MarketType.BOTH_TEAMS_SCORE
    }
    MarketCategory.PLAYER -> match.markets.filter {
      it.type == MarketType.PLAYER_ANYTIME_GOAL || it.type == MarketType.PLAYER_FIRST_GOAL || it.type == MarketType.PLAYER_SHOT_ON_TARGET
    }
    MarketCategory.HANDICAP -> match.markets.filter {
      it.type == MarketType.HANDICAP_RESULT
    }
    MarketCategory.FIRST_HALF -> match.markets.filter {
      it.type == MarketType.FIRST_HALF_RESULT || it.type == MarketType.FIRST_HALF_GOALS
    }
    MarketCategory.GOALS -> match.markets.filter {
      it.type == MarketType.TOTAL_GOALS_25 || it.type == MarketType.TOTAL_GOALS_15 || it.type == MarketType.TOTAL_GOALS_35 || it.type == MarketType.BOTH_TEAMS_SCORE
    }
    MarketCategory.CORNERS -> match.markets.filter {
      it.type == MarketType.TOTAL_CORNERS
    }
    MarketCategory.CARDS -> match.markets.filter {
      it.type == MarketType.TOTAL_CARDS
    }
    MarketCategory.SPECIAL -> match.markets.filter {
      it.type == MarketType.NEXT_GOAL
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .testTag("comprehensive_betting_screen"),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    if (isLiveLocked85) {
      item {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFFEF2F2),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LockClock,
              contentDescription = null,
              tint = Color(0xFFDC2626),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "85' Dakika: Canlı Bahis Kilitlendi",
                fontWeight = FontWeight.Black,
                color = Color(0xFFB91C1C),
                fontSize = 12.sp
              )
              Text(
                text = "Nesine ve TFF standartları gereği 85. dakikadan sonra canlı bülten kilitlenir.",
                color = Color(0xFFDC2626),
                fontSize = 11.sp
              )
            }
          }
        }
      }
    } else if (closureStatus is BettingClosureStatus.Locked5MinWindow) {
      item {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFFEF2F2),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LockClock,
              contentDescription = null,
              tint = Color(0xFFDC2626),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Bahis Alımı Kilitlendi (5 Dk Kuralı)",
                fontWeight = FontWeight.Black,
                color = Color(0xFFB91C1C),
                fontSize = 12.sp
              )
              Text(
                text = "Maç başlamadan 5 dakika önce bültendeki tüm oranlar kilitlenir. Yeni oran seçilemez.",
                color = Color(0xFFDC2626),
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // 1. Mackolik Match Context Header (İY skoru, Hakem, Ek Süre, Stadyum, Yayın)
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B2E))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (match.status == MatchStatus.LIVE) LiveRed else Color(0xFF94A3B8))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (match.status == MatchStatus.LIVE) "CANLI ${match.minute}'" else match.startTime,
                color = if (match.status == MatchStatus.LIVE) LiveRed else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
              if (match.extraTimeMinutes > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  color = Color(0x33EF4444),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "+${match.extraTimeMinutes}' Ek Süre",
                    color = Color(0xFFFCA5A5),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }
            }

            Text(
              text = "İY: ${match.halfTimeHomeScore} - ${match.halfTimeAwayScore}",
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "🏟️ ${match.stadium}",
              color = Color(0xFF94A3B8),
              fontSize = 11.sp
            )
            Text(
              text = "📺 ${match.tvBroadcast}",
              color = GoldYellow,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "⚖️ Hakem: ${match.referee}",
              color = Color(0xFF94A3B8),
              fontSize = 11.sp
            )
            Text(
              text = "☁️ ${match.weather}",
              color = Color(0xFF94A3B8),
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // 2. Market Categories Filter Chips (Tümü, İlk Yarı, Kartlar, Korner...)
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(MarketCategory.values()) { cat ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = {
              Text(
                text = cat.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealDark,
              selectedLabelColor = Color.White,
              containerColor = Color.White,
              labelColor = Color(0xFF334155)
            )
          )
        }
      }
    }

    // 3. Markets list
    if (filteredMarkets.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = Color(0xFF94A3B8),
              modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Bu kategoride şu anda açık iddaa bahsi bulunmuyor.",
              color = Color(0xFF64748B),
              fontSize = 12.sp
            )
          }
        }
      }
    } else {
      items(filteredMarkets) { market ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (market.type) {
                  MarketType.FIRST_HALF_RESULT, MarketType.FIRST_HALF_GOALS -> Icons.Default.Timer
                  MarketType.TOTAL_CORNERS -> Icons.Default.BookmarkBorder
                  MarketType.TOTAL_CARDS -> Icons.Default.Style
                  MarketType.MATCH_RESULT, MarketType.DOUBLE_CHANCE -> Icons.Default.SportsSoccer
                  else -> Icons.Default.LocalAtm
                }
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = TealDark,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = market.name,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color(0xFF0F172A)
                )
              }

              if (market.isSuspended) {
                Surface(
                  color = Color(0xFFFFEDD5),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "ASKIDA",
                    color = Color(0xFFC2410C),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Odds grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              market.selections.forEach { sel ->
                val isSelected = selectedSelections.any { it.selectionId == sel.id }
                OddCell(
                  selection = sel,
                  isSelected = isSelected,
                  isSuspended = market.isSuspended || isBettingClosed,
                  modifier = Modifier.weight(1f),
                  onClick = {
                    onSelectOdd(
                      SlipSelection(
                        matchId = match.id,
                        matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                        marketType = market.type,
                        selectionId = sel.id,
                        selectionName = sel.name,
                        odd = sel.odd,
                        isLive = match.status == MatchStatus.LIVE
                      )
                    )
                  }
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
