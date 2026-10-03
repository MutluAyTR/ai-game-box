package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserStatistics
import com.example.ui.components.LeaderboardComponent
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
  leaderboard: List<UserStatistics>,
  currentUserId: String = "default_user",
  onBack: () -> Unit = {}
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = GoldYellow
            )
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Column {
              Text(
                text = "Liderlik Sıralaması (Leaderboard)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
              )
              Text(
                text = "Sanal TP Kazanç Sıralaması • Room Veritabanı",
                fontSize = 11.sp,
                color = Color(0xFFB0BEC5)
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Geri",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TealDark),
        modifier = Modifier.testTag("leaderboard_top_bar")
      )
    },
    containerColor = Color(0xFFF1F5F9),
    modifier = Modifier.testTag("leaderboard_screen")
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      item {
        LeaderboardComponent(
          leaderboard = leaderboard,
          currentUserId = currentUserId,
          modifier = Modifier.padding(top = 8.dp)
        )
      }

      item {
        Spacer(modifier = Modifier.height(90.dp))
      }
    }
  }
}
