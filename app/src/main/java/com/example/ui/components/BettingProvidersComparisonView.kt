package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.BettingProvider
import com.example.data.engine.UnifiedBettingDataEngine
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.SlipSelection
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

/**
 * 5 Yasal Bahis Operatörü Oran Karşılaştırma & Editör Paneli
 * Nesine, Bilyoner, Misli, Oley, İddaa ve Maçkolik verilerini kıyaslar.
 */
@Composable
fun BettingProvidersComparisonView(
  match: Match,
  selectedSelections: List<SlipSelection> = emptyList(),
  onSelectOdd: (SlipSelection) -> Unit = {}
) {
  val comparisons = UnifiedBettingDataEngine.getComparisonForMatch(match)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .padding(14.dp)
  ) {
    // 1. Header Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CorporateFare, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "5 BAHİS OPERATÖRÜ ORAN & EDİTÖR KIYASI",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              letterSpacing = 0.5.sp
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Nesine (Kral Oran), Bilyoner (Tribün), Misli (Süper Oran), Oley (Sosyal Trend) ve Maçkolik resmi oran karşılaştırması.",
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 2. Comparison Cards
    items(comparisons) { item ->
      val prov = item.provider
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(prov.badgeColor).copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Provider Title Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = prov.logoIcon, fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = prov.providerName,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = Color(prov.badgeColor)
              )
            }
            Surface(
              color = Color(prov.badgeColor).copy(alpha = 0.12f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = prov.specialFeature,
                color = Color(prov.badgeColor),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Odds Table (MS 1 - MS X - MS 2)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // MS 1
            OddsButton(
              label = "MS 1",
              odd = item.homeOdd,
              isSpecial = prov == BettingProvider.NESINE || prov == BettingProvider.MISLI,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = MarketType.MATCH_RESULT,
                    selectionId = "${match.id}_1",
                    selectionName = "1",
                    odd = item.homeOdd,
                    isLive = match.status == com.example.data.model.MatchStatus.LIVE
                  )
                )
              }
            )

            // MS X
            OddsButton(
              label = "MS X",
              odd = item.drawOdd,
              isSpecial = false,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = MarketType.MATCH_RESULT,
                    selectionId = "${match.id}_X",
                    selectionName = "X",
                    odd = item.drawOdd,
                    isLive = match.status == com.example.data.model.MatchStatus.LIVE
                  )
                )
              }
            )

            // MS 2
            OddsButton(
              label = "MS 2",
              odd = item.awayOdd,
              isSpecial = prov == BettingProvider.MISLI,
              modifier = Modifier.weight(1f),
              onClick = {
                onSelectOdd(
                  SlipSelection(
                    matchId = match.id,
                    matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                    marketType = MarketType.MATCH_RESULT,
                    selectionId = "${match.id}_2",
                    selectionName = "2",
                    odd = item.awayOdd,
                    isLive = match.status == com.example.data.model.MatchStatus.LIVE
                  )
                )
              }
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 2.5 Alt / Üst
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Row(
              modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("2.5 Üst:", fontSize = 10.sp, color = Color(0xFF64748B))
              Text("%.2f".format(item.over25Odd), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
            Row(
              modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("KG Var:", fontSize = 10.sp, color = Color(0xFF64748B))
              Text("%.2f".format(item.bttsOdd), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Editor Pick / Comment & Popular Bet %
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.editorComment,
                fontSize = 11.sp,
                color = Color(0xFF334155),
                lineHeight = 15.sp
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              color = Color(0xFFE2E8F0),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "%${item.popularBetPercentage} Oynandı",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun OddsButton(
  label: String,
  odd: Double,
  isSpecial: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    color = if (isSpecial) Color(0xFFFEF3C7) else Color(0xFFF8FAFC),
    shape = RoundedCornerShape(8.dp),
    border = BorderStroke(1.dp, if (isSpecial) GoldYellow else Color(0xFFE2E8F0)),
    modifier = modifier.clickable { onClick() }
  ) {
    Column(
      modifier = Modifier.padding(vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, fontSize = 9.sp, color = if (isSpecial) Color(0xFFB45309) else Color(0xFF64748B), fontWeight = FontWeight.Bold)
      Text(
        text = "%.2f".format(odd),
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = if (isSpecial) Color(0xFFB45309) else Color(0xFF0F172A)
      )
    }
  }
}
