package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.SportsSoccer
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
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

/**
 * Composable function for the 'Live Matches' feed that fetches and displays a list
 * of ongoing sports events using the simulated live provider.
 */
@Composable
fun LiveMatchesFeed(
  matches: List<Match>,
  selectedSelections: List<SlipSelection>,
  simulationMessage: String? = null,
  onSelectOdd: (SlipSelection) -> Unit,
  onOpenMatchDetail: (Match) -> Unit,
  onTriggerGoal: (String, Boolean) -> Unit = { _, _ -> },
  modifier: Modifier = Modifier
) {
  var selectedSport by remember { mutableStateOf<Sport?>(null) }
  val liveMatches = remember(matches, selectedSport) {
    matches.filter { it.status == MatchStatus.LIVE && (selectedSport == null || it.sport == selectedSport) }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
      .testTag("live_matches_feed")
  ) {
    // Top Live Banner & Simulation Ticker
    Surface(
      color = TealDark,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
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
                .background(LiveRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "CANLI MAÇLAR AKIŞI",
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = GoldYellow,
              letterSpacing = 1.sp
            )
          }

          Surface(
            color = LiveRed,
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = "${liveMatches.size} Canlı Maç",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        if (!simulationMessage.isNullOrEmpty()) {
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF0F474A))
              .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.ElectricBolt,
              contentDescription = null,
              tint = GoldYellow,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = simulationMessage,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color.White,
              maxLines = 1
            )
          }
        }
      }
    }

    // Sport category filters
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        FilterChip(
          selected = selectedSport == null,
          onClick = { selectedSport = null },
          label = { Text("Tüm Canlı (${matches.count { it.status == MatchStatus.LIVE }})", fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TealDark,
            selectedLabelColor = GoldYellow
          )
        )
      }
      items(Sport.values()) { sport ->
        val count = matches.count { it.status == MatchStatus.LIVE && it.sport == sport }
        FilterChip(
          selected = selectedSport == sport,
          onClick = { selectedSport = if (selectedSport == sport) null else sport },
          label = { Text("${sport.iconRes} ${sport.displayName} ($count)", fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TealDark,
            selectedLabelColor = GoldYellow
          )
        )
      }
    }

    // Live Feed Match Cards
    if (liveMatches.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("⚽", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Şu anda devam eden canlı karşılaşma bulunmuyor.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
          )
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(liveMatches, key = { it.id }) { match ->
          MatchCard(
            match = match,
            selectedSelections = selectedSelections,
            onSelectOdd = onSelectOdd,
            onOpenDetail = onOpenMatchDetail
          )
        }
      }
    }
  }
}
