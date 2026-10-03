package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.MackolikLeagueDataSource
import com.example.ui.theme.TealDark

@Composable
fun MackolikLeagueFilterRow(
  selectedLeague: String,
  onSelectLeague: (String) -> Unit
) {
  val leagues = MackolikLeagueDataSource.getAllLeagues()

  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .testTag("mackolik_league_filter_row"),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    items(leagues) { league ->
      val isSelected = selectedLeague == league
      FilterChip(
        selected = isSelected,
        onClick = { onSelectLeague(league) },
        label = {
          Text(
            text = league,
            fontSize = 12.sp,
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
