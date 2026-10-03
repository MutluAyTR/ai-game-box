package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Selection
import com.example.data.model.SlipSelection
import com.example.service.BettingClosureStatus
import com.example.service.CalendarManagementService
import com.example.data.model.Sport
import com.example.ui.components.MotorsportSeries
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OddsGreen
import com.example.ui.theme.OddsRed
import com.example.ui.theme.TealDark

@Composable
fun MatchCard(
  match: Match,
  selectedSelections: List<SlipSelection>,
  isFavorite: Boolean = false,
  onToggleFavorite: (String) -> Unit = {},
  onSelectOdd: (SlipSelection) -> Unit,
  onOpenDetail: (Match) -> Unit,
  onOpenInteractiveDetail: (Match) -> Unit = {},
  onOpenMotorsportsHub: (MotorsportSeries) -> Unit = {}
) {
  var isExpanded by remember { mutableStateOf(false) }
  val msMarket = match.markets.find { it.type == MarketType.MATCH_RESULT || it.type == MarketType.BASKETBALL_MS || it.type == MarketType.MOTORSPORTS_WINNER }
  val tg25Market = match.markets.find { it.type == MarketType.TOTAL_GOALS_25 || it.type == MarketType.BASKETBALL_TOTAL_POINTS }
  val kgMarket = match.markets.find { it.type == MarketType.BOTH_TEAMS_SCORE || it.type == MarketType.BASKETBALL_HANDICAP }

  val closureStatus = remember(match.id) { CalendarManagementService.getClosureStatus(match) }
  val isLiveLocked85 = match.status == MatchStatus.LIVE && match.minute >= 85
  val isBettingClosed = closureStatus is BettingClosureStatus.Locked5MinWindow ||
      match.status == MatchStatus.FINISHED ||
      isLiveLocked85

  val isMotorsports = match.sport == Sport.MOTORSPORTS ||
      match.league.contains("MotoGP", ignoreCase = true) ||
      match.league.contains("WRC", ignoreCase = true)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 5.dp)
      .clickable { onOpenDetail(match) }
      .testTag("match_card_${match.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // 1. Header Row: Sport Icon + League Name + Status/Time + Star Favorite
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Text(text = match.sport.iconRes, fontSize = 13.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = match.league,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          when (match.status) {
            MatchStatus.LIVE -> {
              Surface(
                color = LiveRed,
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
                      .background(Color.White)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  val minuteStr = if (match.minute >= 90 && match.currentExtraMinute > 0) "90+${match.currentExtraMinute}'" else "${match.minute}'"
                  Text(
                    text = minuteStr,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    softWrap = false
                  )
                }
              }
            }
            MatchStatus.FINISHED -> {
              Surface(
                color = Color(0xFF64748B),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "MS (Bitti)",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  softWrap = false,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            else -> {
              Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "${match.startTime} TSİ",
                  color = Color(0xFF475569),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  softWrap = false,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          Box(
            modifier = Modifier
              .size(32.dp)
              .clickable { onToggleFavorite(match.id) }
              .testTag("fav_btn_${match.id}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isFavorite) "⭐" else "☆",
              fontSize = 16.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // 2. Info Badges Bar: İddaa Kodu, MBS, Kral Oran, TV Kanalı, Kapanma Durumu (Scrollable so it NEVER wraps or breaks layout)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // İddaa Kodu
        Surface(
          color = Color(0xFFF1F5F9),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "#${match.iddaaCode}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }

        // MBS
        Surface(
          color = if (match.mbs == 1) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "MBS ${match.mbs}",
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = if (match.mbs == 1) Color(0xFFB45309) else Color(0xFF475569),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }

        // Kral Oran
        if (match.isKralOran) {
          Surface(
            color = Color(0xFFFEF9C3),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "👑 KRAL ORAN",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF854D0E),
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
        }

        // TV Kanalı
        if (match.tvBroadcast.isNotBlank()) {
          Surface(
            color = Color(0xFFE0F2FE),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "📺 ${match.tvBroadcast}",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0369A1),
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
        }

        // Popüler Bahis Yüzdesi
        if (match.popularBetPercentage > 0) {
          Surface(
            color = Color(0xFFFEE2E2),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "🔥 %${match.popularBetPercentage}",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFB91C1C),
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
        }

        // Bahis Kapanma Penceresi
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = when (closureStatus) {
            is BettingClosureStatus.Open -> Color(0xFFF0FDF4)
            is BettingClosureStatus.Locked5MinWindow -> Color(0xFFFEF2F2)
            is BettingClosureStatus.LiveInPlay -> Color(0xFFFFFBEB)
            is BettingClosureStatus.Finished -> Color(0xFFF1F5F9)
          }
        ) {
          Text(
            text = CalendarManagementService.getClosureWindowLabel(match),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = when (closureStatus) {
              is BettingClosureStatus.Open -> Color(0xFF166534)
              is BettingClosureStatus.Locked5MinWindow -> Color(0xFFDC2626)
              is BettingClosureStatus.LiveInPlay -> Color(0xFFB45309)
              is BettingClosureStatus.Finished -> Color(0xFF64748B)
            },
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 3. Teams & Scores Section
      Column(modifier = Modifier.fillMaxWidth()) {
        // Home Team Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = match.homeTeam,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.width(8.dp))
          if (isMotorsports) {
            Surface(
              color = if (match.status == MatchStatus.LIVE) Color(0xFFDC2626) else if (match.status == MatchStatus.FINISHED) Color(0xFF0F766E) else Color(0xFF1E293B),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = if (match.status == MatchStatus.LIVE) "LİDER" else if (match.status == MatchStatus.FINISHED) "GALİP" else "POLE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          } else if (match.status != MatchStatus.SCHEDULED) {
            Text(
              text = "${match.homeScore}",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = if (match.status == MatchStatus.LIVE) LiveRed else Color(0xFF0F172A)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Away Team Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = match.awayTeam,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.width(8.dp))
          if (isMotorsports) {
            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = if (match.status == MatchStatus.LIVE) "+0.2s" else if (match.status == MatchStatus.FINISHED) "2." else "P2",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          } else if (match.status != MatchStatus.SCHEDULED) {
            Text(
              text = "${match.awayScore}",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = if (match.status == MatchStatus.LIVE) LiveRed else Color(0xFF0F172A)
            )
          }
        }
      }

      // Motorsports Hub Banner (MotoGP & WRC Fikstür, Puan Durumu & Detayları)
      if (isMotorsports) {
        val series = if (match.league.contains("WRC", ignoreCase = true)) MotorsportSeries.WRC else MotorsportSeries.MOTOGP
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenMotorsportsHub(series) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f).padding(end = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = if (series == MotorsportSeries.WRC) "🏎️" else "🏍️", fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${series.title}: Puan Durumu & Fikstür",
                color = GoldYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Surface(
              color = Color(0xFF0D9488),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "Maçkolik ➔",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 5. Odds Section - MS 1 - X - 2 (Cleanly stacked, zero overflow)
      if (msMarket != null && msMarket.selections.isNotEmpty()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          msMarket.selections.take(3).forEachIndexed { idx, sel ->
            val label = when (idx) {
              0 -> "MS 1"
              1 -> if (msMarket.selections.size > 2) "MS X" else "MS 2"
              else -> "MS 2"
            }
            val isSelected = selectedSelections.any { it.selectionId == sel.id }
            OddCell(
              label = label,
              selection = sel,
              isSelected = isSelected,
              isSuspended = msMarket.isSuspended || isBettingClosed,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = MarketType.MATCH_RESULT,
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

      // 6. Secondary Odds Section: 2.5 Alt / Üst & KG Var / Yok
      if (tg25Market != null && kgMarket != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          tg25Market.selections.take(2).forEachIndexed { idx, sel ->
            val isSelected = selectedSelections.any { it.selectionId == sel.id }
            val label = if (idx == 0) "2.5 Alt" else "2.5 Üst"
            OddCell(
              label = label,
              selection = sel,
              isSelected = isSelected,
              isSuspended = tg25Market.isSuspended || isBettingClosed,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = MarketType.TOTAL_GOALS_25,
                    selectionId = sel.id,
                    selectionName = sel.name,
                    odd = sel.odd,
                    isLive = match.status == MatchStatus.LIVE
                  )
                )
              }
            )
          }
          kgMarket.selections.take(2).forEachIndexed { idx, sel ->
            val isSelected = selectedSelections.any { it.selectionId == sel.id }
            val label = if (idx == 0) "KG Var" else "KG Yok"
            OddCell(
              label = label,
              selection = sel,
              isSelected = isSelected,
              isSuspended = kgMarket.isSuspended || isBettingClosed,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = MarketType.BOTH_TEAMS_SCORE,
                    selectionId = sel.id,
                    selectionName = "KG ${sel.name}",
                    odd = sel.odd,
                    isLive = match.status == MatchStatus.LIVE
                  )
                )
              }
            )
          }
        }
      }

      // Expandable Extra Markets
      if (isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))
        match.markets.filter {
          it.type != MarketType.MATCH_RESULT &&
              it.type != MarketType.TOTAL_GOALS_25 &&
              it.type != MarketType.BOTH_TEAMS_SCORE
        }.forEach { market ->
          Text(
            text = market.name,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            market.selections.forEach { sel ->
              val isSelected = selectedSelections.any { it.selectionId == sel.id }
              OddCell(
                label = sel.name,
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

      Spacer(modifier = Modifier.height(6.dp))

      // Footer: Expand and 6-Operator Comparison Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isExpanded) "▲ Oranları Kapat" else "▼ Tüm Oranlar & 6 Operatör Kıyası (${match.markets.size * 2 + 18})",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TealDark,
          modifier = Modifier
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 4.dp)
        )
      }
    }
  }
}

@Composable
fun OddCell(
  selection: Selection,
  isSelected: Boolean,
  isSuspended: Boolean,
  modifier: Modifier = Modifier,
  label: String = selection.name.replace(Regex("\\s*\\(.*\\)"), "").trim(),
  onClick: () -> Unit
) {
  val bgColor by animateColorAsState(
    targetValue = when {
      isSelected -> GoldYellow
      isSuspended -> Color(0xFFF1F5F9)
      else -> Color(0xFFF8FAFC)
    },
    label = "oddCellBg"
  )

  val borderColor = when {
    isSelected -> GoldYellow
    selection.isOddsUp -> OddsGreen
    selection.isOddsDown -> OddsRed
    else -> Color(0xFFE2E8F0)
  }

  Box(
    modifier = modifier
      .height(46.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(8.dp))
      .clickable(enabled = !isSuspended) { onClick() }
      .padding(horizontal = 4.dp, vertical = 3.dp),
    contentAlignment = Alignment.Center
  ) {
    if (isSuspended) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = "Askıda",
        tint = Color(0xFF94A3B8),
        modifier = Modifier.size(14.dp)
      )
    } else {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = label,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (isSelected) TealDark else Color(0xFF64748B),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "%.2f".format(selection.odd),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = if (isSelected) TealDark else Color(0xFF0F172A),
            maxLines = 1
          )
          if (selection.isOddsUp) {
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "↑", color = OddsGreen, fontSize = 9.sp, fontWeight = FontWeight.Black)
          } else if (selection.isOddsDown) {
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "↓", color = OddsRed, fontSize = 9.sp, fontWeight = FontWeight.Black)
          }
        }
      }
    }
  }
}
