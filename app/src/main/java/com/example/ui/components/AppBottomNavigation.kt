package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

enum class MainTab(val label: String, val icon: ImageVector) {
  BULTEN("Home", Icons.Default.Home),
  CANLI("Matches", Icons.Default.SportsSoccer),
  BITEN("Biten", Icons.Default.Assessment),
  KUPONDAS("Kupondaş", Icons.Default.Group),
  KUPONLARIM("Bet Slip", Icons.Default.ReceiptLong),
  HABERLER("Haberler", Icons.Default.Newspaper),
  PROFIL("Profile", Icons.Default.Person)
}

/**
 * Clean, fixed BottomNavigationBar built with Material3 to replace legacy navigation elements.
 * Features dedicated items for 'Home', 'Matches', 'Bet Slip' (with selection badge), and 'Profile'.
 */
@Composable
fun AppBottomNavigation(
  currentTab: MainTab,
  slipCount: Int,
  slipTotalOdds: Double,
  onSelectTab: (MainTab) -> Unit,
  onOpenSlip: () -> Unit
) {
  Surface(
    color = Color.White,
    shadowElevation = 8.dp,
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .testTag("material3_bottom_navigation_bar")
  ) {
    NavigationBar(
      containerColor = Color.White,
      tonalElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = TealDark,
        selectedTextColor = TealDark,
        indicatorColor = TealDark.copy(alpha = 0.12f),
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B)
      )

      // 1. Home
      val isHomeSelected = currentTab == MainTab.BULTEN
      NavigationBarItem(
        selected = isHomeSelected,
        onClick = { onSelectTab(MainTab.BULTEN) },
        icon = {
          Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = "Home",
            fontSize = 12.sp,
            fontWeight = if (isHomeSelected) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = itemColors,
        modifier = Modifier.testTag("nav_item_home")
      )

      // 2. Matches
      val isMatchesSelected = currentTab == MainTab.CANLI || currentTab == MainTab.BITEN
      NavigationBarItem(
        selected = isMatchesSelected,
        onClick = { onSelectTab(MainTab.CANLI) },
        icon = {
          Icon(
            imageVector = Icons.Default.SportsSoccer,
            contentDescription = "Matches",
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = "Matches",
            fontSize = 12.sp,
            fontWeight = if (isMatchesSelected) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = itemColors,
        modifier = Modifier.testTag("nav_item_matches")
      )

      // 3. Bet Slip (With active selections count badge)
      val isSlipSelected = currentTab == MainTab.KUPONLARIM
      NavigationBarItem(
        selected = isSlipSelected,
        onClick = { onOpenSlip() },
        icon = {
          BadgedBox(
            badge = {
              if (slipCount > 0) {
                Badge(
                  containerColor = GoldYellow,
                  contentColor = TealDark
                ) {
                  Text(
                    text = "$slipCount",
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                  )
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.ReceiptLong,
              contentDescription = "Bet Slip",
              modifier = Modifier.size(24.dp)
            )
          }
        },
        label = {
          Text(
            text = "Bet Slip",
            fontSize = 12.sp,
            fontWeight = if (isSlipSelected) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = itemColors,
        modifier = Modifier.testTag("nav_item_bet_slip")
      )

      // 4. Profile
      val isProfileSelected = currentTab == MainTab.PROFIL
      NavigationBarItem(
        selected = isProfileSelected,
        onClick = { onSelectTab(MainTab.PROFIL) },
        icon = {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = "Profile",
            fontSize = 12.sp,
            fontWeight = if (isProfileSelected) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = itemColors,
        modifier = Modifier.testTag("nav_item_profile")
      )
    }
  }
}
