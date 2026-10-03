package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BasketballStatistics
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatistics
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.util.formatOdd
import com.example.util.formatXg
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Detailed View Component displaying specific match statistics and
 * AI-generated win probability percentages for selected games.
 *
 * Supports switching between games, multi-category sports telemetry,
 * Explainable AI probability distributions, and 1-tap bet slip integration.
 */
@Composable
fun DetailedMatchStatsAndAiProbabilityView(
  matches: List<Match>,
  initialSelectedMatchId: String? = null,
  selectedSelections: List<SlipSelection> = emptyList(),
  onSelectOdd: (SlipSelection) -> Unit = {},
  onClose: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  // Currently selected match state
  var selectedMatchId by remember(initialSelectedMatchId, matches) {
    mutableStateOf(
      initialSelectedMatchId ?: matches.firstOrNull { it.status == MatchStatus.LIVE }?.id
      ?: matches.firstOrNull()?.id ?: ""
    )
  }

  val selectedMatch = remember(selectedMatchId, matches) {
    matches.find { it.id == selectedMatchId } ?: matches.firstOrNull()
  }

  var selectedFilterSport by remember { mutableStateOf<Sport?>(null) }
  var statCategoryTab by remember { mutableIntStateOf(0) } // 0: Genel & AI, 1: Hücum & Şut, 2: Top Kontrolü & Pas, 3: Savunma & Disiplin

  val filteredGameList = remember(matches, selectedFilterSport) {
    matches.filter {
      selectedFilterSport == null || it.sport == selectedFilterSport
    }
  }

  if (selectedMatch == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFF8FAFC)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "Görüntülenecek karşılaşma bulunamadı.",
        fontSize = 14.sp,
        color = Color(0xFF64748B)
      )
    }
    return
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
      .testTag("detailed_match_stats_ai_view")
  ) {
    // 1. Top Component Navigation & Close Bar
    Surface(
      color = Color(0xFF0F172A),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(TealDark),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = GoldYellow,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "AI Kazanma Olasılığı & Detaylı İstatistik",
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = "Maçkolik Telemetrisi & Poisson AI Dağılımı",
              fontSize = 10.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        if (onClose != null) {
          IconButton(
            onClick = onClose,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Kapat",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // 2. Interactive Game Selector Carousel (Switch between Selected Games)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF1E293B))
        .padding(bottom = 8.dp)
    ) {
      // Filter row: Tümü, Futbol, Basketbol, Canlı
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Maç Seç:",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF94A3B8)
        )

        listOf(null to "Tümü", Sport.FOOTBALL to "⚽ Futbol", Sport.BASKETBALL to "🏀 Basketbol").forEach { (sport, label) ->
          val isSelected = selectedFilterSport == sport
          Surface(
            color = if (isSelected) TealDark else Color(0xFF334155),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.clickable { selectedFilterSport = sport }
          ) {
            Text(
              text = label,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else Color(0xFFCBD5E1),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      // Horizontal Games Carousel
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredGameList) { match ->
          val isCurrent = match.id == selectedMatch.id
          val isLive = match.status == MatchStatus.LIVE

          Surface(
            color = if (isCurrent) Color(0xFF0F172A) else Color(0xFF334155),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
              width = if (isCurrent) 1.5.dp else 0.5.dp,
              color = if (isCurrent) GoldYellow else Color(0xFF475569)
            ),
            modifier = Modifier
              .clickable { selectedMatchId = match.id }
              .testTag("match_selector_item_${match.id}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isLive) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(LiveRed)
                )
                Spacer(modifier = Modifier.width(4.dp))
              }
              Text(
                text = "${match.homeTeam.take(8)} vs ${match.awayTeam.take(8)}",
                fontSize = 11.sp,
                fontWeight = if (isCurrent) FontWeight.Black else FontWeight.SemiBold,
                color = if (isCurrent) Color.White else Color(0xFFE2E8F0)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = if (isCurrent) TealDark else Color(0xFF1E293B),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = if (match.status == MatchStatus.SCHEDULED) match.startTime else "${match.homeScore}-${match.awayScore}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrent) GoldYellow else Color.White,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
          }
        }
      }
    }

    // 3. Main Scrollable Content: Selected Match Header, AI Probabilities, Specific Stats
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 3.1 Hero Banner of the Selected Match
      item {
        SelectedGameHeroCard(match = selectedMatch)
      }

      // 3.2 AI-Generated Win Probability Percentages (Card 1)
      item {
        AiWinProbabilityCard(
          match = selectedMatch,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd
        )
      }

      // 3.3 Explainable AI (XAI) Factor Breakdown (Card 2)
      item {
        AiDecisionFactorsCard(match = selectedMatch)
      }

      // 3.4 Specific Match Statistics (Card 3)
      item {
        SpecificMatchStatsCard(
          match = selectedMatch,
          activeCategoryTab = statCategoryTab,
          onCategoryChange = { statCategoryTab = it }
        )
      }

      // 3.5 Quick Betting Market Strip
      item {
        SelectedMatchQuickOddsStrip(
          match = selectedMatch,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd
        )
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

/**
 * Hero Card displaying selected match status, teams, league, venue, and score.
 */
@Composable
private fun SelectedGameHeroCard(match: Match) {
  val isLive = match.status == MatchStatus.LIVE
  val isBasketball = match.sport == Sport.BASKETBALL

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // League and Status Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (isBasketball) "🏀" else "⚽",
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = match.league,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TealDark
          )
        }

        Surface(
          color = when {
            isLive -> LiveRed
            match.status == MatchStatus.FINISHED -> Color(0xFF64748B)
            else -> Color(0xFF0F172A)
          },
          shape = RoundedCornerShape(6.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (isLive) {
              Box(
                modifier = Modifier
                  .size(5.dp)
                  .clip(CircleShape)
                  .background(Color.White)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "CANLI ${match.minute}'",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            } else {
              Text(
                text = if (match.status == MatchStatus.FINISHED) "MS BİTTİ" else match.startTime,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Teams and Scoreboard
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Home Team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.Start
        ) {
          Text(
            text = match.homeTeam,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Ev Sahibi",
            fontSize = 10.sp,
            color = Color(0xFF94A3B8)
          )
        }

        // Score Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF072628))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "${match.homeScore} - ${match.awayScore}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
        }

        // Away Team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.End
        ) {
          Text(
            text = match.awayTeam,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
          )
          Text(
            text = "Deplasman",
            fontSize = 10.sp,
            color = Color(0xFF94A3B8)
          )
        }
      }

      // Halftime / Quarter breakdown
      if (isBasketball && match.quarterScoresHome.isNotEmpty() && match.quarterScoresAway.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          match.quarterScoresHome.indices.forEach { qIndex ->
            val hQ = match.quarterScoresHome.getOrElse(qIndex) { 0 }
            val aQ = match.quarterScoresAway.getOrElse(qIndex) { 0 }
            Text(
              text = "Ç${qIndex + 1}: $hQ-$aQ",
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF334155)
            )
          }
        }
      } else if (!isBasketball && (match.halfTimeHomeScore > 0 || match.halfTimeAwayScore > 0 || match.minute >= 45)) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "İlk Yarı: ${match.halfTimeHomeScore} - ${match.halfTimeAwayScore} • Hakem: ${match.referee}",
            fontSize = 10.sp,
            color = Color(0xFF64748B)
          )
        }
      }
    }
  }
}

/**
 * Flagship AI-Generated Win Probability Percentages Card.
 * Displays calculated probabilities for Home, Draw, and Away (or 2-way for Basketball),
 * confidence meters, and Poisson score expectations.
 */
@Composable
private fun AiWinProbabilityCard(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  val pred = match.aiPrediction
  val isBasketball = match.sport == Sport.BASKETBALL

  // Clean bounds
  val homePct = if (isBasketball) pred.homeWinProb.coerceIn(15, 85) else pred.homeWinProb.coerceIn(10, 80)
  val drawPct = if (isBasketball) 0 else pred.drawProb.coerceIn(5, 45)
  val awayPct = (100 - homePct - drawPct).coerceAtLeast(10)

  // Fair odds calculated from pure probabilities
  val fairOddHome = ((100.0 / homePct) * 100).roundToInt() / 100.0
  val fairOddDraw = if (drawPct > 0) ((100.0 / drawPct) * 100).roundToInt() / 100.0 else 0.0
  val fairOddAway = ((100.0 / awayPct) * 100).roundToInt() / 100.0

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("ai_win_probability_section")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = TealDark,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "AI Kazanma Olasılık Yüzdeleri",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
        }

        // Confidence Tag
        Surface(
          color = Color(0xFFF0FDF4),
          shape = RoundedCornerShape(6.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Güven: %${pred.confidence}",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF15803D)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Visual Segmented Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(14.dp)
          .clip(RoundedCornerShape(7.dp))
          .background(Color(0xFFE2E8F0))
      ) {
        // Home Segment
        Box(
          modifier = Modifier
            .weight(homePct.toFloat())
            .fillMaxSize()
            .background(TealDark)
        )
        // Draw Segment (football only)
        if (!isBasketball && drawPct > 0) {
          Box(
            modifier = Modifier
              .weight(drawPct.toFloat())
              .fillMaxSize()
              .background(Color(0xFF94A3B8))
          )
        }
        // Away Segment
        Box(
          modifier = Modifier
            .weight(awayPct.toFloat())
            .fillMaxSize()
            .background(Color(0xFFE65100))
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3 Probability Value Boxes
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // 1. Home Win Probability Box
        ProbabilityMetricPill(
          label = match.homeTeam.take(10),
          subtitle = "1 (Ev Sahibi)",
          percent = homePct,
          fairOdd = fairOddHome,
          accentColor = TealDark,
          modifier = Modifier.weight(1f)
        )

        // 2. Draw Probability Box (Football only)
        if (!isBasketball) {
          ProbabilityMetricPill(
            label = "Beraberlik",
            subtitle = "X",
            percent = drawPct,
            fairOdd = fairOddDraw,
            accentColor = Color(0xFF64748B),
            modifier = Modifier.weight(1f)
          )
        }

        // 3. Away Win Probability Box
        ProbabilityMetricPill(
          label = match.awayTeam.take(10),
          subtitle = "2 (Deplasman)",
          percent = awayPct,
          fairOdd = fairOddAway,
          accentColor = Color(0xFFE65100),
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Monte Carlo Most Probable Scores & Expected Totals
      Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "AI Skor Tahmini", fontSize = 10.sp, color = Color(0xFF64748B))
            Text(
              text = pred.predictedScore,
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = TealDark
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(30.dp)
              .background(Color(0xFFCBD5E1))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = if (isBasketball) "Toplam Sayı Beklentisi" else "xG Beklentisi", fontSize = 10.sp, color = Color(0xFF64748B))
            Text(
              text = if (isBasketball) "164.5 Üst" else "${match.statistics.xgHome.formatXg()} - ${match.statistics.xgAway.formatXg()}",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(30.dp)
              .background(Color(0xFFCBD5E1))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Simülasyon", fontSize = 10.sp, color = Color(0xFF64748B))
            Text(
              text = "100.000 İterasyon",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF059669)
            )
          }
        }
      }

      // Value Bet Recommendation Button
      val recommendedPick = if (homePct >= awayPct && homePct >= drawPct) "1" else if (awayPct > homePct) "2" else "X"
      val recOdd = when (recommendedPick) {
        "1" -> fairOddHome
        "X" -> fairOddDraw
        else -> fairOddAway
      }

      Spacer(modifier = Modifier.height(10.dp))

      Surface(
        color = Color(0xFFF0FDF4),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            onSelectOdd(
              SlipSelection(
                matchId = match.id,
                matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                marketType = MarketType.MATCH_RESULT,
                selectionId = "ai_rec_${match.id}",
                selectionName = "MS $recommendedPick",
                odd = recOdd,
                isLive = match.status == MatchStatus.LIVE
              )
            )
          }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("💡", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "AI Değerli Bahis Önerisi: MS $recommendedPick (@${recOdd})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF166534)
              )
              Text(
                text = "Model olasılığı piyasa oranına göre +%${(homePct - 40).coerceAtLeast(6)} değer içeriyor.",
                fontSize = 10.sp,
                color = Color(0xFF15803D)
              )
            }
          }

          Surface(
            color = Color(0xFF16A34A),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "+ Kupona Ekle",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProbabilityMetricPill(
  label: String,
  subtitle: String,
  percent: Int,
  fairOdd: Double,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    color = Color(0xFFF8FAFC),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = subtitle,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "%$percent",
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = accentColor
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Adil: @$fairOdd",
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF94A3B8)
      )
    }
  }
}

/**
 * Explainable AI Card: shows underlying factors driving the AI percentages
 * (Home advantage, Recent Form, H2H, xG threat index).
 */
@Composable
private fun AiDecisionFactorsCard(match: Match) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.QueryStats, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "AI Olasılık Belirleyici Faktörler",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
        }
        Text(
          text = if (isExpanded) "Gizle ▲" else "Görüşler ▼",
          fontSize = 11.sp,
          color = Color(0xFF0284C7),
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 4 Key Factors Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        DecisionFactorChip(title = "İç Saha", desc = "+%12 Etki", icon = "🏟️", modifier = Modifier.weight(1f))
        DecisionFactorChip(title = "Form Gücü", desc = "8.4 / 10", icon = "📈", modifier = Modifier.weight(1f))
        DecisionFactorChip(title = "H2H Üstünlük", desc = "%60 Galibiyet", icon = "⚔️", modifier = Modifier.weight(1f))
        DecisionFactorChip(title = "xG Farkı", desc = "+0.75 Gol", icon = "🎯", modifier = Modifier.weight(1f))
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          HorizontalDivider(color = Color(0xFFF1F5F9))
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = match.aiPrediction.tacticalAnalysis,
            fontSize = 11.sp,
            color = Color(0xFF475569),
            lineHeight = 16.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "• ${match.homeTeam}: ${match.aiPrediction.formRatingHome}",
            fontSize = 10.sp,
            color = Color(0xFF64748B)
          )
          Text(
            text = "• ${match.awayTeam}: ${match.aiPrediction.formRatingAway}",
            fontSize = 10.sp,
            color = Color(0xFF64748B)
          )
        }
      }
    }
  }
}

@Composable
private fun DecisionFactorChip(title: String, desc: String, icon: String, modifier: Modifier = Modifier) {
  Surface(
    color = Color(0xFFF8FAFC),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = icon, fontSize = 12.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
      Text(text = desc, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
  }
}

/**
 * Specific Match Statistics Card:
 * Renders categorized comparative telemetry bars for Football (possession, xG, shots, corners, discipline)
 * or Basketball (FG%, 3PT%, FT%, Rebounds, Assists, Turnovers).
 */
@Composable
private fun SpecificMatchStatsCard(
  match: Match,
  activeCategoryTab: Int,
  onCategoryChange: (Int) -> Unit
) {
  val isBasketball = match.sport == Sport.BASKETBALL
  val stats = match.statistics
  val bStats = match.basketballStats ?: BasketballStatistics()

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("specific_stats_section")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Timeline,
            contentDescription = null,
            tint = TealDark,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isBasketball) "🏀 Detaylı Basketbol İstatistikleri" else "⚽ Spesifik Maç İstatistikleri",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
        }

        Text(
          text = if (isBasketball) "EuroLeague / NBA Telemetrisi" else "Maçkolik Canlı Opta",
          fontSize = 10.sp,
          color = Color(0xFF94A3B8)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Category Switcher Tabs
      val categories = if (isBasketball) {
        listOf("Genel Şut", "Ribaund & Asist", "Savunma & Hata")
      } else {
        listOf("Tümü", "Hücum & Şut", "Pas & Hakimiyet", "Disiplin")
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        categories.forEachIndexed { index, catName ->
          val isSelected = activeCategoryTab == index
          Surface(
            color = if (isSelected) TealDark else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .clickable { onCategoryChange(index) }
          ) {
            Text(
              text = catName,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else Color(0xFF475569),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 5.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Comparative Statistics Rows
      if (isBasketball) {
        // BASKETBALL DETAILED STATS
        when (activeCategoryTab) {
          0 -> {
            ComparativeStatRow("Saha İçi İsabet (FG)", bStats.twoPointersHome, bStats.twoPointersAway, homeRatio = 0.53f)
            ComparativeStatRow("3 Sayılık İsabet (3PT)", bStats.threePointersHome, bStats.threePointersAway, homeRatio = 0.55f)
            ComparativeStatRow("Serbest Atış (FT)", bStats.freeThrowsHome, bStats.freeThrowsAway, homeRatio = 0.52f)
          }
          1 -> {
            ComparativeNumericStatRow("Toplam Ribaund", bStats.reboundsHome, bStats.reboundsAway)
            ComparativeNumericStatRow("Hücum Ribaundu", bStats.offensiveReboundsHome, bStats.offensiveReboundsAway)
            ComparativeNumericStatRow("Asistler", bStats.assistsHome, bStats.assistsAway)
          }
          else -> {
            ComparativeNumericStatRow("Top Kaybı", bStats.turnoversHome, bStats.turnoversAway, invertGood = true)
            ComparativeNumericStatRow("Top Çalma", bStats.stealsHome, bStats.stealsAway)
            ComparativeNumericStatRow("Bloklar", bStats.blocksHome, bStats.blocksAway)
            ComparativeNumericStatRow("Takım Faulü", bStats.foulsHome, bStats.foulsAway, invertGood = true)
            ComparativeNumericStatRow("Kalan Mola", bStats.timeoutsRemainingHome, bStats.timeoutsRemainingAway)
          }
        }
      } else {
        // FOOTBALL DETAILED STATS
        when (activeCategoryTab) {
          0 -> {
            // Tümü
            ComparativeNumericStatRow("Topla Oynama (%)", stats.possessionHome, stats.possessionAway)
            ComparativeStatRow("Beklenen Gol (xG)", stats.xgHome.formatXg(), stats.xgAway.formatXg(), homeRatio = (stats.xgHome / max(0.1, stats.xgHome + stats.xgAway)).toFloat())
            ComparativeNumericStatRow("Toplam Şut", stats.shotsHome, stats.shotsAway)
            ComparativeNumericStatRow("İsabetli Şut", stats.shotsOnTargetHome, stats.shotsOnTargetAway)
            ComparativeNumericStatRow("Köşe Vuruşu (Korner)", stats.cornersHome, stats.cornersAway)
            ComparativeNumericStatRow("Tehlikeli Atak", stats.dangerousAttacksHome, stats.dangerousAttacksAway)
            ComparativeNumericStatRow("Fauller", stats.foulsHome, stats.foulsAway, invertGood = true)
            ComparativeNumericStatRow("Sarı Kartlar 🟨", stats.yellowCardsHome, stats.yellowCardsAway, invertGood = true)
            ComparativeNumericStatRow("Ofsayt", stats.offsidesHome, stats.offsidesAway)
          }
          1 -> {
            // Hücum & Şut
            ComparativeStatRow("Beklenen Gol (xG)", stats.xgHome.formatXg(), stats.xgAway.formatXg(), homeRatio = (stats.xgHome / max(0.1, stats.xgHome + stats.xgAway)).toFloat())
            ComparativeNumericStatRow("Toplam Şut", stats.shotsHome, stats.shotsAway)
            ComparativeNumericStatRow("İsabetli Şut", stats.shotsOnTargetHome, stats.shotsOnTargetAway)
            ComparativeNumericStatRow("İsabetsiz Şut", (stats.shotsHome - stats.shotsOnTargetHome).coerceAtLeast(0), (stats.shotsAway - stats.shotsOnTargetAway).coerceAtLeast(0))
            ComparativeNumericStatRow("Köşe Vuruşu (Korner)", stats.cornersHome, stats.cornersAway)
            ComparativeNumericStatRow("Tehlikeli Atak", stats.dangerousAttacksHome, stats.dangerousAttacksAway)
          }
          2 -> {
            // Pas & Hakimiyet
            ComparativeNumericStatRow("Topla Oynama (%)", stats.possessionHome, stats.possessionAway)
            ComparativeStatRow("Topun Oyunda Kalma Süresi", stats.ballInPlayTime, stats.ballInPlayTime, homeRatio = 0.5f)
            ComparativeNumericStatRow("Pas İsabeti (%)", 84, 78)
            ComparativeNumericStatRow("Başarılı Paslar", 412, 365)
          }
          else -> {
            // Disiplin & Savunma
            ComparativeNumericStatRow("Fauller", stats.foulsHome, stats.foulsAway, invertGood = true)
            ComparativeNumericStatRow("Sarı Kartlar 🟨", stats.yellowCardsHome, stats.yellowCardsAway, invertGood = true)
            ComparativeNumericStatRow("Kırmızı Kartlar 🟥", stats.redCardsHome, stats.redCardsAway, invertGood = true)
            ComparativeNumericStatRow("Ofsayt", stats.offsidesHome, stats.offsidesAway)
            ComparativeNumericStatRow("Kaleci Kurtarışları", 4, 3)
          }
        }
      }
    }
  }
}

/**
 * Visual comparative bar with numerical values on sides and dynamic progress bar in the center.
 */
@Composable
private fun ComparativeNumericStatRow(
  title: String,
  homeVal: Int,
  awayVal: Int,
  invertGood: Boolean = false
) {
  val total = max(1, homeVal + awayVal)
  val homeRatio = homeVal.toFloat() / total.toFloat()
  val isHomeBetter = if (invertGood) homeVal < awayVal else homeVal > awayVal
  val isAwayBetter = if (invertGood) awayVal < homeVal else awayVal > homeVal

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "$homeVal",
        fontSize = 12.sp,
        fontWeight = if (isHomeBetter) FontWeight.Black else FontWeight.SemiBold,
        color = if (isHomeBetter) TealDark else Color(0xFF64748B)
      )

      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF334155)
      )

      Text(
        text = "$awayVal",
        fontSize = 12.sp,
        fontWeight = if (isAwayBetter) FontWeight.Black else FontWeight.SemiBold,
        color = if (isAwayBetter) Color(0xFFE65100) else Color(0xFF64748B)
      )
    }

    Spacer(modifier = Modifier.height(3.dp))

    // Comparative Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(Color(0xFFE2E8F0))
    ) {
      Box(
        modifier = Modifier
          .weight(homeRatio.coerceIn(0.05f, 0.95f))
          .fillMaxSize()
          .background(TealDark)
      )
      Box(
        modifier = Modifier
          .weight((1f - homeRatio).coerceIn(0.05f, 0.95f))
          .fillMaxSize()
          .background(Color(0xFFE65100))
      )
    }
  }
}

@Composable
private fun ComparativeStatRow(
  title: String,
  homeText: String,
  awayText: String,
  homeRatio: Float
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = homeText,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TealDark
      )

      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF334155)
      )

      Text(
        text = awayText,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFE65100)
      )
    }

    Spacer(modifier = Modifier.height(3.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(Color(0xFFE2E8F0))
    ) {
      Box(
        modifier = Modifier
          .weight(homeRatio.coerceIn(0.05f, 0.95f))
          .fillMaxSize()
          .background(TealDark)
      )
      Box(
        modifier = Modifier
          .weight((1f - homeRatio).coerceIn(0.05f, 0.95f))
          .fillMaxSize()
          .background(Color(0xFFE65100))
      )
    }
  }
}

/**
 * Quick odds selector at the bottom of the detailed view.
 * Enables the user to tap odds directly into the active betting slip.
 */
@Composable
private fun SelectedMatchQuickOddsStrip(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  val primaryMarket = match.markets.firstOrNull {
    it.type == MarketType.MATCH_RESULT || it.type == MarketType.BASKETBALL_MS
  } ?: match.markets.firstOrNull() ?: return

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "🎯 ${primaryMarket.name} Oranları",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0F172A)
        )
        Text(
          text = "Orana dokunarak kupona ekle",
          fontSize = 10.sp,
          color = Color(0xFF94A3B8)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        primaryMarket.selections.forEach { sel ->
          val isSelected = selectedSelections.any { it.selectionId == sel.id }

          Surface(
            color = if (isSelected) GoldYellow else Color(0xFFF8FAFC),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) Color(0xFFD97706) else Color(0xFFE2E8F0)
            ),
            modifier = Modifier
              .weight(1f)
              .clickable {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = primaryMarket.type,
                    selectionId = sel.id,
                    selectionName = sel.name,
                    odd = sel.odd,
                    isLive = match.status == MatchStatus.LIVE
                  )
                )
              }
          ) {
            Column(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = sel.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color(0xFF78350F) else Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = sel.odd.formatOdd(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) Color(0xFF78350F) else TealDark
              )
            }
          }
        }
      }
    }
  }
}
