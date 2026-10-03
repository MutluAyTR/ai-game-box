package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary

/**
 * Screen dedicated to Finished Matches (Biten Karşılaşmalar) with Real-Time Authenticity,
 * sport-specific timing, and AI Control Mechanism breakdown.
 */
@Composable
fun FinishedMatchesScreen(
  matches: List<Match>,
  onOpenMatchDetail: (Match) -> Unit
) {
  var selectedSportFilter by remember { mutableIntStateOf(0) } // 0: Tümü, 1: Futbol, 2: Basketbol, 3: Tenis

  val finishedMatches = matches.filter { it.status == MatchStatus.FINISHED }
  val filteredMatches = when (selectedSportFilter) {
    1 -> finishedMatches.filter { it.sport == Sport.FOOTBALL }
    2 -> finishedMatches.filter { it.sport == Sport.BASKETBALL }
    3 -> finishedMatches.filter { it.sport == Sport.TENNIS }
    else -> finishedMatches
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("finished_matches_screen")
  ) {
    // Header Banner
    item {
      Surface(
        color = TealDark,
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
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
              Text(
                text = "BİTEN KARŞILAŞMALAR",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "Futbol, Basketbol, Tenis gerçek süreler ve AI kontrol kayıtları",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Surface(
              color = Color(0xFF0F3A3D),
              shape = RoundedCornerShape(12.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = "AI Kontrollü",
                  tint = GoldYellow,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "AI Kontrollü",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldYellow,
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
          }
        }
      }
    }

    // Sport Filter Tab Row
    item {
      ScrollableTabRow(
        selectedTabIndex = selectedSportFilter,
        containerColor = Color.White,
        edgePadding = 12.dp
      ) {
        Tab(
          selected = selectedSportFilter == 0,
          onClick = { selectedSportFilter = 0 },
          text = { Text("Tümü (${finishedMatches.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedSportFilter == 1,
          onClick = { selectedSportFilter = 1 },
          text = { Text("⚽ Futbol (${finishedMatches.count { it.sport == Sport.FOOTBALL }})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedSportFilter == 2,
          onClick = { selectedSportFilter = 2 },
          text = { Text("🏀 Basketbol (${finishedMatches.count { it.sport == Sport.BASKETBALL }})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedSportFilter == 3,
          onClick = { selectedSportFilter = 3 },
          text = { Text("🎾 Tenis (${finishedMatches.count { it.sport == Sport.TENNIS }})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(10.dp))
    }

    if (filteredMatches.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🏁", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Henüz bu kategoride biten karşılaşma bulunmuyor.",
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF64748B),
              fontSize = 13.sp
            )
          }
        }
      }
    } else {
      items(filteredMatches) { match ->
        FinishedMatchCard(match = match, onClick = { onOpenMatchDetail(match) })
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
private fun FinishedMatchCard(
  match: Match,
  onClick: () -> Unit
) {
  val sportIcon = when (match.sport) {
    Sport.BASKETBALL -> Icons.Default.SportsBasketball
    Sport.TENNIS -> Icons.Default.SportsTennis
    else -> Icons.Default.SportsSoccer
  }

  val durationText = when (match.sport) {
    Sport.FOOTBALL -> "90+${match.extraTimeMinutes}' Tamamlandı"
    Sport.BASKETBALL -> "40' (4 Çeyrek) Tamamlandı"
    Sport.TENNIS -> "${match.minute}' (3 Set) Tamamlandı"
    else -> "Tamamlandı"
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .clickable { onClick() }
      .testTag("finished_match_card_${match.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: League & Sport Badge & Duration
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = sportIcon,
            contentDescription = match.sport.name,
            tint = TealPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = match.league,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
        }

        Surface(
          color = Color(0xFFF1F5F9),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = "Süre",
              tint = Color(0xFF475569),
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = durationText,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF475569)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Match Score Board
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Home Team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = match.homeTeam,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          if (match.statistics.xgHome > 0) {
            Text(
              text = "xG ${"%.2f".format(match.statistics.xgHome)}",
              fontSize = 10.sp,
              color = Color(0xFF0D9488)
            )
          }
        }

        // Score Pill
        Surface(
          color = TealDark,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Text(
            text = "${match.homeScore} - ${match.awayScore}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          )
        }

        // Away Team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = match.awayTeam,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          if (match.statistics.xgAway > 0) {
            Text(
              text = "xG ${"%.2f".format(match.statistics.xgAway)}",
              fontSize = 10.sp,
              color = Color(0xFF0D9488)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // AI Accuracy & Verification Banner
      Surface(
        color = Color(0xFFF0FDF4),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "AI Kontrol",
              tint = Color(0xFF059669),
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "AI Kontrol: Tahmin ${match.aiPrediction.predictedScore} (%${match.aiPrediction.confidence} Doğruluk)",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF065F46)
            )
          }

          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Detay",
            tint = Color(0xFF059669),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
