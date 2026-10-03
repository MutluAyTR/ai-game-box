package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary

@Composable
fun SportCategoriesRow(
  selectedSport: Sport?,
  onlyLive: Boolean,
  liveCount: Int = 12,
  totalCount: Int = 54,
  footballCount: Int = 38,
  basketballCount: Int = 18,
  tennisCount: Int = 9,
  onSelectSport: (Sport?) -> Unit,
  onToggleLive: (Boolean) -> Unit
) {
  val scrollState = rememberScrollState()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState)
      .padding(horizontal = 10.dp, vertical = 3.dp)
      .testTag("sport_categories_row"),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // 1. Canlı Button (with red badge like in Nesine)
    SportCategoryItem(
      title = "Canlı",
      emoji = "⏱️",
      badgeCount = liveCount,
      badgeColor = LiveRed,
      isSelected = onlyLive,
      onClick = { onToggleLive(!onlyLive) }
    )

    // 2. Tümü / Hepsi
    SportCategoryItem(
      title = "Tümü",
      emoji = "🏆",
      badgeCount = totalCount,
      badgeColor = Color(0xFF607D8B),
      isSelected = selectedSport == null && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(null)
      }
    )

    // 3. Futbol
    SportCategoryItem(
      title = "Futbol",
      emoji = "⚽",
      badgeCount = footballCount,
      badgeColor = Color(0xFF455A64),
      isSelected = selectedSport == Sport.FOOTBALL && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.FOOTBALL)
      }
    )

    // 4. Basketbol
    SportCategoryItem(
      title = "Basketbol",
      emoji = "🏀",
      badgeCount = basketballCount,
      badgeColor = Color(0xFFE65100),
      isSelected = selectedSport == Sport.BASKETBALL && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.BASKETBALL)
      }
    )

    // 6. Motor Sporları (MotoGP & Formula 1 & WRC)
    SportCategoryItem(
      title = "Motorspor",
      emoji = "🏎️",
      badgeCount = 8,
      badgeColor = Color(0xFFD32F2F),
      isSelected = selectedSport == Sport.MOTORSPORTS && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.MOTORSPORTS)
      }
    )

    // 7. Tenis
    SportCategoryItem(
      title = "Tenis",
      emoji = "🎾",
      badgeCount = tennisCount,
      badgeColor = Color(0xFF7CB342),
      isSelected = selectedSport == Sport.TENNIS && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.TENNIS)
      }
    )

    // 8. Voleybol
    SportCategoryItem(
      title = "Voleybol",
      emoji = "🏐",
      badgeCount = 6,
      badgeColor = Color(0xFF0288D1),
      isSelected = selectedSport == Sport.VOLLEYBALL && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.VOLLEYBALL)
      }
    )

    // 9. Buz Hokeyi
    SportCategoryItem(
      title = "Buz Hokeyi",
      emoji = "🏒",
      badgeCount = 10,
      badgeColor = Color(0xFF0097A7),
      isSelected = selectedSport == Sport.ICE_HOCKEY && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.ICE_HOCKEY)
      }
    )

    // 10. E-Spor
    SportCategoryItem(
      title = "E-Spor",
      emoji = "🎮",
      badgeCount = 12,
      badgeColor = Color(0xFF7B1FA2),
      isSelected = selectedSport == Sport.ESPORTS && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.ESPORTS)
      }
    )

    // 11. Hentbol
    SportCategoryItem(
      title = "Hentbol",
      emoji = "🤾",
      badgeCount = 5,
      badgeColor = Color(0xFFF57C00),
      isSelected = selectedSport == Sport.HANDBALL && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.HANDBALL)
      }
    )

    // 12. Snooker & Dart
    SportCategoryItem(
      title = "Snooker",
      emoji = "🎯",
      badgeCount = 4,
      badgeColor = Color(0xFF388E3C),
      isSelected = selectedSport == Sport.SNOOKER && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.SNOOKER)
      }
    )

    // 13. Masa Tenisi
    SportCategoryItem(
      title = "Masa Tenisi",
      emoji = "🏓",
      badgeCount = 7,
      badgeColor = Color(0xFFE91E63),
      isSelected = selectedSport == Sport.TABLE_TENNIS && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.TABLE_TENNIS)
      }
    )

    // 14. Amerikan Futbolu
    SportCategoryItem(
      title = "NFL",
      emoji = "🏈",
      badgeCount = 5,
      badgeColor = Color(0xFF795548),
      isSelected = selectedSport == Sport.AMERICAN_FOOTBALL && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.AMERICAN_FOOTBALL)
      }
    )

    // 15. Beyzbol
    SportCategoryItem(
      title = "Beyzbol",
      emoji = "⚾",
      badgeCount = 6,
      badgeColor = Color(0xFF1565C0),
      isSelected = selectedSport == Sport.BASEBALL && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.BASEBALL)
      }
    )

    // 16. MMA & UFC
    SportCategoryItem(
      title = "MMA / UFC",
      emoji = "🥋",
      badgeCount = 8,
      badgeColor = Color(0xFFC2185B),
      isSelected = selectedSport == Sport.MMA_UFC && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.MMA_UFC)
      }
    )

    // 17. Boks
    SportCategoryItem(
      title = "Boks",
      emoji = "🥊",
      badgeCount = 3,
      badgeColor = Color(0xFFB71C1C),
      isSelected = selectedSport == Sport.BOXING && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.BOXING)
      }
    )

    // 18. Formula 1
    SportCategoryItem(
      title = "Formula 1",
      emoji = "🏎️",
      badgeCount = 4,
      badgeColor = Color(0xFFE53935),
      isSelected = selectedSport == Sport.FORMULA_1 && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.FORMULA_1)
      }
    )

    // 19. Kriket
    SportCategoryItem(
      title = "Kriket",
      emoji = "🏏",
      badgeCount = 3,
      badgeColor = Color(0xFF2E7D32),
      isSelected = selectedSport == Sport.CRICKET && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.CRICKET)
      }
    )

    // 20. Rugby
    SportCategoryItem(
      title = "Rugby",
      emoji = "🏉",
      badgeCount = 4,
      badgeColor = Color(0xFF4E342E),
      isSelected = selectedSport == Sport.RUGBY_UNION && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.RUGBY_UNION)
      }
    )

    // 21. Satranç
    SportCategoryItem(
      title = "Satranç",
      emoji = "♟️",
      badgeCount = 2,
      badgeColor = Color(0xFF37474F),
      isSelected = selectedSport == Sport.CHESS && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.CHESS)
      }
    )

    // 22. Sutopu
    SportCategoryItem(
      title = "Sutopu",
      emoji = "🤽",
      badgeCount = 4,
      badgeColor = Color(0xFF00838F),
      isSelected = selectedSport == Sport.WATER_POLO && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.WATER_POLO)
      }
    )

    // 23. Badminton
    SportCategoryItem(
      title = "Badminton",
      emoji = "🏸",
      badgeCount = 5,
      badgeColor = Color(0xFF6A1B9A),
      isSelected = selectedSport == Sport.BADMINTON && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.BADMINTON)
      }
    )

    // 24. Padel
    SportCategoryItem(
      title = "Padel",
      emoji = "🎾",
      badgeCount = 4,
      badgeColor = Color(0xFF558B2F),
      isSelected = selectedSport == Sport.PADEL && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.PADEL)
      }
    )

    // 25. E-Futbol
    SportCategoryItem(
      title = "E-Futbol",
      emoji = "⚽",
      badgeCount = 8,
      badgeColor = Color(0xFF1E88E5),
      isSelected = selectedSport == Sport.E_FOOTBALL && !onlyLive,
      onClick = {
        onToggleLive(false)
        onSelectSport(Sport.E_FOOTBALL)
      }
    )
  }
}

@Composable
private fun SportCategoryItem(
  title: String,
  emoji: String,
  badgeCount: Int,
  badgeColor: Color,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) TealDark else Color.White,
    border = BorderStroke(
      1.dp,
      if (isSelected) TealDark else Color(0xFFCBD5E1)
    ),
    shadowElevation = if (isSelected) 2.dp else 0.5.dp,
    modifier = Modifier
      .clickable { onClick() }
      .testTag("sport_item_$title")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      Text(text = emoji, fontSize = 13.sp)
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
        color = if (isSelected) Color.White else Color(0xFF1E293B),
        maxLines = 1,
        softWrap = false
      )
      if (badgeCount > 0) {
        Spacer(modifier = Modifier.width(5.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) GoldYellow else badgeColor
        ) {
          Text(
            text = "$badgeCount",
            color = if (isSelected) TealDark else Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
      }
    }
  }
}
