package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Selection
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

/**
 * Flagship Mackolik Main Dashboard Component
 * Displays simulated live football (and basketball) matches with real-time betting odds,
 * live radar match statistics, in-play score alerts, and virtual wallet balance.
 */
@Composable
fun MackolikLiveDashboardComponent(
  matches: List<Match>,
  walletPoints: Long,
  selectedSelections: List<SlipSelection>,
  simulationMessage: String?,
  onSelectOdd: (SlipSelection) -> Unit,
  onOpenMatchDetail: (Match) -> Unit,
  onClaimDailyBonus: () -> Unit,
  onRechargeBalance: (Long) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedSport by remember { mutableStateOf<Sport?>(Sport.FOOTBALL) }
  var selectedLeague by remember { mutableStateOf("Tümü") }
  var activeViewTab by remember { mutableStateOf(0) } // 0: Canlı Maçlar, 1: Düşen Oranlar & Popüler, 2: Puan Durumu

  val liveMatches = remember(matches, selectedSport, selectedLeague) {
    matches.filter {
      val isLive = it.status == MatchStatus.LIVE
      val sportOk = selectedSport == null || it.sport == selectedSport
      val leagueOk = selectedLeague == "Tümü" || it.league == selectedLeague
      isLive && sportOk && leagueOk
    }
  }

  val liveFootballCount = remember(matches) {
    matches.count { it.status == MatchStatus.LIVE && it.sport == Sport.FOOTBALL }
  }
  val liveBasketballCount = remember(matches) {
    matches.count { it.status == MatchStatus.LIVE && it.sport == Sport.BASKETBALL }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("mackolik_main_dashboard")
  ) {
    // 2. Sport Selectors & Live Count Pills
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Futbol
      Surface(
        color = if (selectedSport == Sport.FOOTBALL) TealDark else Color.White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
          .weight(1f)
          .clickable { selectedSport = Sport.FOOTBALL }
      ) {
        Row(
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text("⚽", fontSize = 14.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Futbol",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selectedSport == Sport.FOOTBALL) Color.White else Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Surface(
            color = if (selectedSport == Sport.FOOTBALL) LiveRed else Color(0xFFFFE4E6),
            shape = CircleShape
          ) {
            Text(
              text = "$liveFootballCount",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = if (selectedSport == Sport.FOOTBALL) Color.White else LiveRed,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
          }
        }
      }

      // Basketbol
      Surface(
        color = if (selectedSport == Sport.BASKETBALL) TealDark else Color.White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
          .weight(1f)
          .clickable { selectedSport = Sport.BASKETBALL }
      ) {
        Row(
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text("🏀", fontSize = 14.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Basketbol",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selectedSport == Sport.BASKETBALL) Color.White else Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Surface(
            color = if (selectedSport == Sport.BASKETBALL) LiveRed else Color(0xFFFFE4E6),
            shape = CircleShape
          ) {
            Text(
              text = "$liveBasketballCount",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = if (selectedSport == Sport.BASKETBALL) Color.White else LiveRed,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
          }
        }
      }

      // Tümü
      Surface(
        color = if (selectedSport == null) TealDark else Color.White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
          .weight(0.8f)
          .clickable { selectedSport = null }
      ) {
        Row(
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text("Tümü", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedSport == null) Color.White else Color(0xFF1E293B))
        }
      }
    }

    // 3. View Switcher Tabs: Canlı Maçlar vs Düşen Oranlar vs Puan Durumu
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      val tabs = listOf("🔴 Canlı (${liveMatches.size})", "📉 Düşen Oran", "📊 Puan", "🎯 AI İstatistik")
      tabs.forEachIndexed { index, title ->
        val isSelected = activeViewTab == index
        Surface(
          color = if (isSelected) TealDark else Color(0xFFF1F5F9),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { activeViewTab = index }
        ) {
          Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF475569),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
          )
        }
      }
    }

    // 4. Content based on activeViewTab
    when (activeViewTab) {
      1 -> {
        MackolikDroppingOddsAndPopularView(
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd
        )
      }
      2 -> {
        MackolikStandingsView()
      }
      3 -> {
        DetailedMatchStatsAndAiProbabilityView(
          matches = matches,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd,
          modifier = Modifier
            .fillMaxWidth()
            .height(680.dp)
        )
      }
      else -> {
        // Live Matches Feed
        if (liveMatches.isEmpty()) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("⏳", fontSize = 32.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text("Seçilen ligde şu an canlı maç bulunmuyor.", fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
              Text("Bültendeki maçlar başlama saatleri gelince otomatik canlıya geçer.", fontSize = 11.sp, color = Color(0xFF94A3B8))
            }
          }
        } else {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            liveMatches.forEach { match ->
              LiveMatchDashboardCard(
                match = match,
                selectedSelections = selectedSelections,
                onSelectOdd = onSelectOdd,
                onOpenMatchDetail = onOpenMatchDetail
              )
            }
          }
        }
      }
    }
  }
}

/**
 * High-Density Simulated Live Match Card with Betting Odds and Live Radar
 */
@Composable
fun LiveMatchDashboardCard(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit,
  onOpenMatchDetail: (Match) -> Unit,
  modifier: Modifier = Modifier
) {
  val isBasketball = match.sport == Sport.BASKETBALL

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("live_match_card_${match.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top League & Live Clock Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (isBasketball) "🏀 ${match.league}" else "⚽ ${match.league}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
          )
          match.tvBroadcast.let { tv ->
            if (tv.isNotBlank()) {
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = tv,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF64748B),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
          }
        }

        // Live Minute Badge
        Surface(
          color = Color(0xFFFFE4E6),
          shape = RoundedCornerShape(6.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(LiveRed)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isBasketball) "CANLI ${match.minute}'" else "CANLI ${match.minute}'",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = LiveRed
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Teams & Live Score
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOpenMatchDetail(match) },
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
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (isBasketball && match.quarterScoresHome.isNotEmpty()) {
            Text(
              text = "Ç: ${match.quarterScoresHome.joinToString("-")}",
              fontSize = 9.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        // Score Box
        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Text(
            text = "${match.homeScore} - ${match.awayScore}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = GoldYellow,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
          )
        }

        // Away Team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.End
        ) {
          Text(
            text = match.awayTeam,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (isBasketball && match.quarterScoresAway.isNotEmpty()) {
            Text(
              text = "Ç: ${match.quarterScoresAway.joinToString("-")}",
              fontSize = 9.sp,
              color = Color(0xFF64748B),
              textAlign = TextAlign.End
            )
          }
        }
      }

      // Latest Event Banner if any
      match.events.firstOrNull()?.let { event ->
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          color = Color(0xFFF8FAFC),
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "${event.description}",
            fontSize = 10.sp,
            color = Color(0xFF334155),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // In-Play Betting Odds Row
      val matchResultMarket = match.markets.find {
        it.type == MarketType.MATCH_RESULT || it.type == MarketType.BASKETBALL_MS
      }
      val overUnderMarket = match.markets.find {
        it.type == MarketType.TOTAL_GOALS_25 || it.type == MarketType.BASKETBALL_TOTAL_POINTS
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // MS 1
        matchResultMarket?.selections?.getOrNull(0)?.let { sel ->
          val isSelected = selectedSelections.any { it.matchId == match.id && it.selectionId == sel.id }
          OddsBox(
            title = if (isBasketball) "1" else "MS 1",
            odd = sel.odd,
            isUp = sel.isOddsUp,
            isDown = sel.isOddsDown,
            isSelected = isSelected,
            isSuspended = matchResultMarket.isSuspended,
            onClick = {
              onSelectOdd(
                SlipSelection(
                  matchId = match.id,
                  matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                  marketType = matchResultMarket.type,
                  selectionId = sel.id,
                  selectionName = sel.name,
                  odd = sel.odd,
                  isLive = true
                )
              )
            },
            modifier = Modifier.weight(1f)
          )
        }

        // MS X (Football only)
        if (!isBasketball) {
          matchResultMarket?.selections?.getOrNull(1)?.let { sel ->
            val isSelected = selectedSelections.any { it.matchId == match.id && it.selectionId == sel.id }
            OddsBox(
              title = "MS X",
              odd = sel.odd,
              isUp = sel.isOddsUp,
              isDown = sel.isOddsDown,
              isSelected = isSelected,
              isSuspended = matchResultMarket.isSuspended,
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = matchResultMarket.type,
                    selectionId = sel.id,
                    selectionName = sel.name,
                    odd = sel.odd,
                    isLive = true
                  )
                )
              },
              modifier = Modifier.weight(1f)
            )
          }
        }

        // MS 2
        val selAway = if (isBasketball) {
          matchResultMarket?.selections?.getOrNull(1)
        } else {
          matchResultMarket?.selections?.getOrNull(2)
        }
        selAway?.let { sel ->
          val isSelected = selectedSelections.any { it.matchId == match.id && it.selectionId == sel.id }
          val isSusp = matchResultMarket?.isSuspended == true
          val mType = matchResultMarket?.type ?: MarketType.MATCH_RESULT
          OddsBox(
            title = if (isBasketball) "2" else "MS 2",
            odd = sel.odd,
            isUp = sel.isOddsUp,
            isDown = sel.isOddsDown,
            isSelected = isSelected,
            isSuspended = isSusp,
            onClick = {
              onSelectOdd(
                SlipSelection(
                  matchId = match.id,
                  matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                  marketType = mType,
                  selectionId = sel.id,
                  selectionName = sel.name,
                  odd = sel.odd,
                  isLive = true
                )
              )
            },
            modifier = Modifier.weight(1f)
          )
        }

        // 2.5 Alt / Total Points Under
        overUnderMarket?.selections?.getOrNull(0)?.let { sel ->
          val isSelected = selectedSelections.any { it.matchId == match.id && it.selectionId == sel.id }
          val isSusp = overUnderMarket?.isSuspended == true
          val mType = overUnderMarket?.type ?: MarketType.TOTAL_GOALS_25
          OddsBox(
            title = if (isBasketball) "Alt" else "2.5 Alt",
            odd = sel.odd,
            isUp = sel.isOddsUp,
            isDown = sel.isOddsDown,
            isSelected = isSelected,
            isSuspended = isSusp,
            onClick = {
              onSelectOdd(
                SlipSelection(
                  matchId = match.id,
                  matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                  marketType = mType,
                  selectionId = sel.id,
                  selectionName = sel.name,
                  odd = sel.odd,
                  isLive = true
                )
              )
            },
            modifier = Modifier.weight(1f)
          )
        }

        // 2.5 Üst / Total Points Over
        overUnderMarket?.selections?.getOrNull(1)?.let { sel ->
          val isSelected = selectedSelections.any { it.matchId == match.id && it.selectionId == sel.id }
          val isSusp = overUnderMarket?.isSuspended == true
          val mType = overUnderMarket?.type ?: MarketType.TOTAL_GOALS_25
          OddsBox(
            title = if (isBasketball) "Üst" else "2.5 Üst",
            odd = sel.odd,
            isUp = sel.isOddsUp,
            isDown = sel.isOddsDown,
            isSelected = isSelected,
            isSuspended = isSusp,
            onClick = {
              onSelectOdd(
                SlipSelection(
                  matchId = match.id,
                  matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                  marketType = mType,
                  selectionId = sel.id,
                  selectionName = sel.name,
                  odd = sel.odd,
                  isLive = true
                )
              )
            },
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Footer: Stats bar & Details Link
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (!isBasketball) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Topla Oynama: %${match.statistics.possessionHome} - %${match.statistics.possessionAway}",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "• xG: ${match.statistics.xgHome} - ${match.statistics.xgAway}",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
          }
        } else {
          match.basketballStats?.let { bs ->
            Text(
              text = "Rib: ${bs.reboundsHome}-${bs.reboundsAway} • Ast: ${bs.assistsHome}-${bs.assistsAway} • Faul: ${bs.foulsHome}-${bs.foulsAway}",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          } ?: Text("Basketbol Canlı Takip", fontSize = 10.sp, color = Color(0xFF64748B))
        }

        Text(
          text = "+${match.markets.size * 3} Bahis & Kadro ▶",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TealDark,
          modifier = Modifier.clickable { onOpenMatchDetail(match) }
        )
      }
    }
  }
}

/**
 * Compact Animated Odds Box
 */
@Composable
fun OddsBox(
  title: String,
  odd: Double,
  isUp: Boolean,
  isDown: Boolean,
  isSelected: Boolean,
  isSuspended: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val containerColor by animateColorAsState(
    targetValue = when {
      isSelected -> TealDark
      isSuspended -> Color(0xFFF1F5F9)
      else -> Color(0xFFF8FAFC)
    },
    label = "containerColor"
  )

  Surface(
    color = containerColor,
    shape = RoundedCornerShape(8.dp),
    modifier = modifier
      .clickable(enabled = !isSuspended) { onClick() }
      .border(
        width = 1.dp,
        color = if (isSelected) TealDark else Color(0xFFE2E8F0),
        shape = RoundedCornerShape(8.dp)
      )
  ) {
    Column(
      modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = title,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) GoldYellow else Color(0xFF64748B),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      if (isSuspended) {
        Text(
          text = "KAPALI",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF94A3B8)
        )
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "%.2f".format(odd),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = if (isSelected) Color.White else Color(0xFF0F172A)
          )
          if (isUp) {
            Icon(
              Icons.Default.ArrowDropUp,
              contentDescription = "Artış",
              tint = Color(0xFF16A34A),
              modifier = Modifier.size(12.dp)
            )
          } else if (isDown) {
            Icon(
              Icons.Default.ArrowDropDown,
              contentDescription = "Düşüş",
              tint = LiveRed,
              modifier = Modifier.size(12.dp)
            )
          }
        }
      }
    }
  }
}
