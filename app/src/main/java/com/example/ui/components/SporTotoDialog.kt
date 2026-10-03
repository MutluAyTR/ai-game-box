package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import kotlin.random.Random

data class TotoMatch(
  val id: Int,
  val homeTeam: String,
  val awayTeam: String,
  val league: String,
  val date: String
)

val DEFAULT_TOTO_FIXTURE = listOf(
  TotoMatch(1, "Galatasaray", "Fenerbahçe", "Süper Lig", "25 Eyl 20:00"),
  TotoMatch(2, "Beşiktaş", "Trabzonspor", "Süper Lig", "25 Eyl 20:00"),
  TotoMatch(3, "Başakşehir", "Kasımpaşa", "Süper Lig", "26 Eyl 17:00"),
  TotoMatch(4, "Samsunspor", "Göztepe", "Süper Lig", "26 Eyl 17:00"),
  TotoMatch(5, "Antalyaspor", "Sivasspor", "Süper Lig", "26 Eyl 20:00"),
  TotoMatch(6, "Alanyaspor", "Rizespor", "Süper Lig", "27 Eyl 16:00"),
  TotoMatch(7, "Eyüpspor", "Gaziantep FK", "Süper Lig", "27 Eyl 19:00"),
  TotoMatch(8, "Konyaspor", "Kayserispor", "Süper Lig", "27 Eyl 20:00"),
  TotoMatch(9, "Arsenal", "Chelsea", "Premier League", "26 Eyl 19:30"),
  TotoMatch(10, "Liverpool", "Manchester City", "Premier League", "27 Eyl 18:30"),
  TotoMatch(11, "Real Madrid", "Barcelona", "La Liga", "27 Eyl 22:00"),
  TotoMatch(12, "Atletico Madrid", "Sevilla", "La Liga", "26 Eyl 22:00"),
  TotoMatch(13, "Inter", "Juventus", "Serie A", "27 Eyl 21:45"),
  TotoMatch(14, "Bayern Münih", "Dortmund", "Bundesliga", "26 Eyl 19:30"),
  TotoMatch(15, "PSG", "Marsilya", "Ligue 1", "27 Eyl 21:45")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SporTotoDialog(
  walletBalance: Long,
  onPlayToto: (cost: Long, selectionsCount: Int) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  // Selections: Map of matchId -> Set of choices ("1", "X", "2")
  val selections = remember { mutableStateMapOf<Int, Set<String>>() }

  // Calculation of columns: product of choices count for all 15 matches
  val completedMatches = selections.count { it.value.isNotEmpty() }
  val totalColumns = if (completedMatches == 15) {
    selections.values.fold(1L) { acc, set -> acc * set.size }
  } else {
    0L
  }
  val costPerColumn = 5L
  val totalCost = totalColumns * costPerColumn

  fun toggleChoice(matchId: Int, choice: String) {
    val current = selections[matchId] ?: emptySet()
    val updated = if (current.contains(choice)) {
      current - choice
    } else {
      current + choice
    }
    if (updated.isEmpty()) {
      selections.remove(matchId)
    } else {
      selections[matchId] = updated
    }
  }

  fun fillRandom() {
    val choices = listOf("1", "X", "2")
    DEFAULT_TOTO_FIXTURE.forEach { match ->
      if (!selections.containsKey(match.id) || selections[match.id]?.isEmpty() == true) {
        val randomChoice = choices[Random.nextInt(choices.size)]
        selections[match.id] = setOf(randomChoice)
      }
    }
  }

  fun clearAll() {
    selections.clear()
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFFF8FAFC),
    modifier = Modifier.testTag("spor_toto_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.92f)
    ) {
      // Header
      Surface(
        color = TealDark,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = GoldYellow,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "15 MAÇ",
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = TealDark,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Spor Toto Bülteni",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = Color.White
              )
              Text(
                text = "38. Hafta İkramiye Havuzu: 2.500.000 TP",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
          }
        }
      }

      // Quick Tools Row
      Surface(
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "$completedMatches / 15 Maç Dolduruldu",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (completedMatches == 15) Color(0xFF059669) else Color(0xFFE11D48)
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
              onClick = { fillRandom() },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(32.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = TealDark)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Rastgele Doldur", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealDark)
            }

            IconButton(
              onClick = { clearAll() },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Temizle", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      // 15 Matches List
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        itemsIndexed(DEFAULT_TOTO_FIXTURE) { index, match ->
          val currentPicks = selections[match.id] ?: emptySet()

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Match Index
              Box(
                modifier = Modifier
                  .size(26.dp)
                  .clip(CircleShape)
                  .background(if (currentPicks.isNotEmpty()) TealDark else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${match.id}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = if (currentPicks.isNotEmpty()) GoldYellow else Color(0xFF64748B)
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              // Match Info
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${match.homeTeam} - ${match.awayTeam}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E293B),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${match.league} • ${match.date}",
                  fontSize = 10.sp,
                  color = Color(0xFF94A3B8)
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              // 1 - X - 2 Option Buttons
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TotoOptionButton(
                  label = "1",
                  isSelected = currentPicks.contains("1"),
                  onClick = { toggleChoice(match.id, "1") }
                )
                TotoOptionButton(
                  label = "X",
                  isSelected = currentPicks.contains("X"),
                  onClick = { toggleChoice(match.id, "X") }
                )
                TotoOptionButton(
                  label = "2",
                  isSelected = currentPicks.contains("2"),
                  onClick = { toggleChoice(match.id, "2") }
                )
              }
            }
          }
        }
      }

      // Bottom Checkout Bar
      Surface(
        color = Color.White,
        shadowElevation = 12.dp,
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
            Column {
              Text(
                text = "Toplam Kolon: $totalColumns",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
              )
              Text(
                text = "$totalCost TP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TealDark
              )
            }

            Button(
              onClick = {
                if (completedMatches == 15 && totalCost > 0) {
                  onPlayToto(totalCost, completedMatches)
                }
              },
              enabled = completedMatches == 15 && totalCost > 0 && walletBalance >= totalCost,
              colors = ButtonDefaults.buttonColors(
                containerColor = GoldYellow,
                contentColor = TealDark,
                disabledContainerColor = Color(0xFFE2E8F0),
                disabledContentColor = Color(0xFF94A3B8)
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .height(46.dp)
                .testTag("play_spor_toto_button")
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (completedMatches < 15) "15 Maçı Tamamla" else "Kuponu Oyna ($totalCost TP)",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
            }
          }

          if (walletBalance < totalCost) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "⚠️ Yetersiz bakiye! Cüzdanınızda $walletBalance TP bulunmaktadır.",
              fontSize = 11.sp,
              color = Color(0xFFDC2626),
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TotoOptionButton(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(width = 32.dp, height = 32.dp)
      .clip(RoundedCornerShape(6.dp))
      .background(if (isSelected) TealDark else Color(0xFFF1F5F9))
      .border(
        width = 1.dp,
        color = if (isSelected) GoldYellow else Color(0xFFCBD5E1),
        shape = RoundedCornerShape(6.dp)
      )
      .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = FontWeight.Black,
      color = if (isSelected) GoldYellow else Color(0xFF334155)
    )
  }
}
