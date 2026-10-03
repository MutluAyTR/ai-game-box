package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.util.formatOdd
import com.example.util.formatXg

@Composable
fun HeroSpotlightCard(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit,
  onOpenAiAnalysis: (Match) -> Unit,
  onTriggerGoal: (String, Boolean) -> Unit = { _, _ -> }
) {
  val msMarket = match.markets.find { it.type == MarketType.MATCH_RESULT }
  val isLiveLocked85 = match.status == MatchStatus.LIVE && match.minute >= 85
  val isMarketSuspended = (msMarket?.isSuspended ?: false) || isLiveLocked85

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 10.dp, vertical = 4.dp)
      .testTag("hero_spotlight_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = TealDark),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0xFF0F474A),
              Color(0xFF082729)
            )
          )
        )
        .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Top banner: League, Live indicator & Analysis button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (match.status == MatchStatus.LIVE) LiveRed else GoldYellow)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = if (match.status == MatchStatus.LIVE) "CANLI DERBİ • ${match.league}" else "GÜNÜN DERBİSİ • TSİ ${match.startTime}",
              color = GoldYellow,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              letterSpacing = 0.5.sp
            )
          }

          Surface(
            color = Color(0x33FFFFFF),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable { onOpenAiAnalysis(match) }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (match.tvBroadcast.isNotBlank()) "📺 ${match.tvBroadcast}" else "Detay ➔",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Teams & Center Score Card (Compact single row)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Home Team
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = match.homeTeam,
              fontWeight = FontWeight.Bold,
              fontSize = 13.5.sp,
              color = Color.White,
              textAlign = TextAlign.Center,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (match.stadium.isNotBlank()) {
              Text(
                text = match.stadium.split("(").first().trim(),
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB0BEC5),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          // Center Score & Minute
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 6.dp)
          ) {
            // Minute pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (match.status == MatchStatus.LIVE) LiveRed else Color(0xFF1E293B))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (match.status == MatchStatus.LIVE) "${match.minute}'" else if (match.status == MatchStatus.FINISHED) "MS" else "TSİ ${match.startTime}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
            }

            // Score
            Text(
              text = if (match.status == MatchStatus.SCHEDULED) "vs" else "${match.homeScore} - ${match.awayScore}",
              fontWeight = FontWeight.Black,
              fontSize = if (match.status == MatchStatus.SCHEDULED) 15.sp else 19.sp,
              color = Color.White
            )
          }

          // Away Team
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = match.awayTeam,
              fontWeight = FontWeight.Bold,
              fontSize = 13.5.sp,
              color = Color.White,
              textAlign = TextAlign.Center,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = if (match.sport == Sport.BASKETBALL) "Basketbol" else "Futbol",
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFFB0BEC5)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Odds Buttons: Supports both 3-way (Football) and 2-way (Basketball)
        val mainMarket = msMarket ?: match.markets.firstOrNull()
        if (mainMarket != null && mainMarket.selections.size >= 3 && match.sport == Sport.FOOTBALL) {
          val sel1 = mainMarket.selections[0]
          val selX = mainMarket.selections[1]
          val sel2 = mainMarket.selections[2]

          val is1Selected = selectedSelections.any { it.selectionId == sel1.id }
          val isXSelected = selectedSelections.any { it.selectionId == selX.id }
          val is2Selected = selectedSelections.any { it.selectionId == sel2.id }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            HeroOddButton(
              topLabel = "MS 1",
              odd = sel1.odd,
              isSelected = is1Selected,
              isSuspended = isMarketSuspended,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = mainMarket.type,
                    selectionId = sel1.id,
                    selectionName = "MS 1",
                    odd = sel1.odd,
                    isLive = match.status == MatchStatus.LIVE
                  )
                )
              }
            )

            HeroOddButton(
              topLabel = "MS X",
              odd = selX.odd,
              isSelected = isXSelected,
              isSuspended = isMarketSuspended,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = mainMarket.type,
                    selectionId = selX.id,
                    selectionName = "MS X",
                    odd = selX.odd,
                    isLive = match.status == MatchStatus.LIVE
                  )
                )
              }
            )

            HeroOddButton(
              topLabel = "MS 2",
              odd = sel2.odd,
              isSelected = is2Selected,
              isSuspended = isMarketSuspended,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = mainMarket.type,
                    selectionId = sel2.id,
                    selectionName = "MS 2",
                    odd = sel2.odd,
                    isLive = match.status == MatchStatus.LIVE
                  )
                )
              }
            )
          }
        } else if (mainMarket != null && mainMarket.selections.size >= 2) {
          // Basketball (2-way: 1 & 2)
          val sel1 = mainMarket.selections[0]
          val sel2 = mainMarket.selections[1]

          val is1Selected = selectedSelections.any { it.selectionId == sel1.id }
          val is2Selected = selectedSelections.any { it.selectionId == sel2.id }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            HeroOddButton(
              topLabel = "MS 1",
              odd = sel1.odd,
              isSelected = is1Selected,
              isSuspended = isMarketSuspended,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = mainMarket.type,
                    selectionId = sel1.id,
                    selectionName = "MS 1",
                    odd = sel1.odd,
                    isLive = match.status == MatchStatus.LIVE
                  )
                )
              }
            )

            HeroOddButton(
              topLabel = "MS 2",
              odd = sel2.odd,
              isSelected = is2Selected,
              isSuspended = isMarketSuspended,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = mainMarket.type,
                    selectionId = sel2.id,
                    selectionName = "MS 2",
                    odd = sel2.odd,
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

/**
 * Compact, balanced betting button with crisp alignment.
 */
@Composable
fun HeroOddButton(
  topLabel: String,
  odd: Double,
  isSelected: Boolean,
  isSuspended: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val bgColor by animateColorAsState(
    targetValue = when {
      isSelected -> GoldYellow
      isSuspended -> Color(0xFF2E3D40)
      else -> Color(0xFF1B4E52)
    },
    label = "heroOddBg"
  )

  val labelColor = when {
    isSelected -> TealDark
    else -> Color(0xFFB0BEC5)
  }

  val oddColor = when {
    isSelected -> TealDark
    else -> GoldYellow
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .border(
        width = 1.dp,
        color = if (isSelected) GoldYellow else Color(0x33FFFFFF),
        shape = RoundedCornerShape(8.dp)
      )
      .clickable(enabled = !isSuspended) { onClick() }
      .padding(vertical = 5.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    if (isSuspended) {
      Text(
        text = "KİLİTLİ 🔒",
        color = Color(0xFFFFB74D),
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      )
    } else {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = topLabel,
          color = labelColor,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          maxLines = 1
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = odd.formatOdd(),
          color = oddColor,
          fontWeight = FontWeight.Black,
          fontSize = 13.sp,
          maxLines = 1
        )
      }
    }
  }
}
