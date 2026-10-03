package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AiPrediction
import com.example.data.model.Market
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatistics
import com.example.data.model.MatchStatus
import com.example.data.model.Selection
import com.example.data.model.Sport
import com.example.engine.MackolikWeeklyFixtureEngine
import com.example.service.CalendarManagementService
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

data class DrawnGroup(
  val groupName: String,
  val teams: MutableList<String> = mutableListOf()
)

data class DrawnKnockoutMatch(
  val matchTitle: String,
  val homeTeam: String,
  val awayTeam: String,
  val roundName: String = "Son 16 Turu"
)

enum class DrawTournamentType(val displayName: String, val badge: String) {
  CHAMPIONS_LEAGUE("UEFA Şampiyonlar Ligi", "⭐"),
  EUROPA_LEAGUE("UEFA Avrupa Ligi", "🏆"),
  CONFERENCE_LEAGUE("UEFA Konferans Ligi", "🌍"),
  TURKISH_CUP("Ziraat Türkiye Kupası", "🇹🇷")
}

/**
 * Authentic UEFA Champions League Studio Live Draw Simulator.
 * Exactly styled after the official UEFA draw ceremony:
 * - Left: Giant gleaming silver Champions League Trophy ("Big Ears")
 * - Center: Chibi presenters in sharp navy suits holding starball & team card
 * - Center: 4 Transparent glass bowls for POT 1, POT 2, POT 3, POT 4 on neon stage table
 * - Right: Glowing vertical digital LED scoreboard displaying qualified teams
 */
@Composable
fun LiveDrawSimulatorDialog(
  onDismiss: () -> Unit,
  onAddDrawnMatchesToBulletin: (List<Match>) -> Unit
) {
  var selectedTournament by remember { mutableStateOf(DrawTournamentType.CHAMPIONS_LEAGUE) }
  var currentPotIndex by remember { mutableIntStateOf(1) } // 1, 2, 3, 4
  var isDrawing by remember { mutableStateOf(false) }
  var lastDrawnTeam by remember { mutableStateOf<String?>("REAL MADRID") }
  var lastDrawnMessage by remember { mutableStateOf("Kura çekimini başlatmak için 'Topu Çek' butonuna basınız.") }
  var showCardRevealBanner by remember { mutableStateOf(true) }

  // 8 Groups: A through H (or 4 for Turkish Cup / Conference)
  val groups = remember(selectedTournament) {
    mutableStateListOf(
      DrawnGroup("Grup A"), DrawnGroup("Grup B"),
      DrawnGroup("Grup C"), DrawnGroup("Grup D"),
      DrawnGroup("Grup E"), DrawnGroup("Grup F"),
      DrawnGroup("Grup G"), DrawnGroup("Grup H")
    )
  }

  val pots = remember(selectedTournament) {
    when (selectedTournament) {
      DrawTournamentType.CHAMPIONS_LEAGUE -> MackolikWeeklyFixtureEngine.uefaPots
      DrawTournamentType.EUROPA_LEAGUE -> MackolikWeeklyFixtureEngine.europaPots
      DrawTournamentType.CONFERENCE_LEAGUE -> MackolikWeeklyFixtureEngine.conferencePots
      DrawTournamentType.TURKISH_CUP -> mapOf(
        1 to listOf("Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor"),
        2 to listOf("Başakşehir", "Samsunspor", "Göztepe", "Kasımpaşa"),
        3 to listOf("Sivasspor", "Antalyaspor", "Konyaspor", "Alanyaspor"),
        4 to listOf("Kocaelispor", "Sakaryaspor", "Amed SK", "Bursaspor")
      )
    }
  }

  val remainingTeamsInPot = remember(currentPotIndex, selectedTournament, groups.map { it.teams.size }) {
    val drawnSet = groups.flatMap { it.teams }.toSet()
    (pots[currentPotIndex] ?: emptyList()).filter { !drawnSet.contains(it) }.toMutableList()
  }

  // Country protection helper
  fun getTeamCountry(team: String): String {
    return when {
      team in listOf("Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor", "Başakşehir", "Samsunspor", "Göztepe", "Kasımpaşa", "Kocaelispor", "Sakaryaspor", "Amed SK", "Bursaspor") -> "TR"
      team in listOf("Real Madrid", "Barcelona", "Atletico Madrid", "Real Sociedad", "Athletic Bilbao", "Girona") -> "ES"
      team in listOf("Manchester City", "Arsenal", "Liverpool", "Aston Villa", "Manchester United", "Tottenham", "Chelsea") -> "EN"
      team in listOf("Bayern München", "Borussia Dortmund", "Bayer Leverkusen", "RB Leipzig", "VfB Stuttgart", "Eintracht Frankfurt") -> "DE"
      team in listOf("Inter", "Juventus", "Atalanta", "AC Milan", "Bologna", "AS Roma", "Lazio") -> "IT"
      team in listOf("Paris Saint-Germain", "AS Monaco", "Brest", "Lille", "Lyon") -> "FR"
      team in listOf("Sporting CP", "Benfica", "FC Porto") -> "PT"
      team in listOf("PSV Eindhoven", "Feyenoord", "Ajax") -> "NL"
      else -> "OTHER"
    }
  }

  fun drawNextTeam() {
    isDrawing = true
    if (remainingTeamsInPot.isEmpty()) {
      if (currentPotIndex < 4) {
        currentPotIndex++
        lastDrawnMessage = "${currentPotIndex}. Torbaya geçildi! Çekilişe devam edebilirsiniz."
      } else {
        lastDrawnMessage = "🎉 Tüm torbaların kura çekimi başarıyla tamamlandı!"
      }
      isDrawing = false
      return
    }

    val team = remainingTeamsInPot.random()
    remainingTeamsInPot.remove(team)
    lastDrawnTeam = team
    showCardRevealBanner = true

    // Find valid group with Country Protection rule
    val teamCountry = getTeamCountry(team)
    var assigned = false
    val maxPerGroup = if (selectedTournament == DrawTournamentType.TURKISH_CUP) 2 else 4

    for (group in groups) {
      if (group.teams.size < maxPerGroup) {
        val sameCountryExists = group.teams.any { getTeamCountry(it) == teamCountry && teamCountry != "OTHER" }
        if (!sameCountryExists) {
          group.teams.add(team)
          lastDrawnMessage = "🟢 $team çekildi ve ${group.groupName}'ye yerleşti!"
          assigned = true
          break
        }
      }
    }

    if (!assigned) {
      val availableGroup = groups.firstOrNull { it.teams.size < maxPerGroup }
      if (availableGroup != null) {
        availableGroup.teams.add(team)
        lastDrawnMessage = "🟡 $team çekildi ve ${availableGroup.groupName}'ye yerleştirildi (İstisnai Eşleşme)."
      }
    }

    if (remainingTeamsInPot.isEmpty() && currentPotIndex < 4) {
      currentPotIndex++
    }
    isDrawing = false
  }

  fun autoDrawAll() {
    isDrawing = true
    for (pot in 1..4) {
      currentPotIndex = pot
      val potTeams = (pots[pot] ?: emptyList()).toMutableList()
      potTeams.shuffle()
      val maxPerGroup = if (selectedTournament == DrawTournamentType.TURKISH_CUP) 2 else 4

      for (team in potTeams) {
        val teamCountry = getTeamCountry(team)
        var placed = false

        for (group in groups) {
          if (group.teams.size < pot) {
            val sameCountry = group.teams.any { getTeamCountry(it) == teamCountry && teamCountry != "OTHER" }
            if (!sameCountry) {
              group.teams.add(team)
              placed = true
              break
            }
          }
        }

        if (!placed) {
          val fallback = groups.firstOrNull { it.teams.size < pot } ?: groups.firstOrNull { it.teams.size < maxPerGroup }
          fallback?.teams?.add(team)
        }
      }
    }

    lastDrawnTeam = groups.firstOrNull()?.teams?.firstOrNull() ?: "REAL MADRID"
    showCardRevealBanner = true
    lastDrawnMessage = "🏆 Kura Çekimi Tamamlandı! Gruplar ve Hafta İçi Eşleşmeleri hazırlandı."
    isDrawing = false
  }

  // Animation values for glowing stadium neon lights & trophy sparkle
  val infiniteTransition = rememberInfiniteTransition(label = "studio_lights")
  val neonGlow by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "neon_glow"
  )

  val trophySparkle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "trophy_sparkle"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      color = Color(0xFF060B18),
      shape = RoundedCornerShape(20.dp),
      modifier = Modifier
        .fillMaxWidth(0.98f)
        .fillMaxHeight(0.96f)
        .border(1.5.dp, Color(0xFF00D4FF).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
        .shadow(24.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0xFF0284C7).copy(alpha = 0.4f))
        .testTag("live_draw_simulator_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xFF030816),
                Color(0xFF07142E),
                Color(0xFF0B1F48),
                Color(0xFF040A1A)
              )
            )
          )
      ) {
        // =========================================================================
        // TOP BAR: TOURNAMENT SELECTOR & CLOSE
        // =========================================================================
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
                .size(34.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))),
              contentAlignment = Alignment.Center
            ) {
              Text("⭐", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "UEFA CHAMPIONS LEAGUE",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = Color.White,
                letterSpacing = 0.6.sp,
                maxLines = 1,
                softWrap = false
              )
              Text(
                text = "Resmi Canlı Kura Simülatörü & Torba Çekilişi",
                fontSize = 10.sp,
                color = Color(0xFF38BDF8),
                maxLines = 1,
                softWrap = false
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFFCBD5E1))
          }
        }

        // Tournament Tabs Row
        TabRow(
          selectedTabIndex = selectedTournament.ordinal,
          containerColor = Color(0xFF0A1630),
          contentColor = Color(0xFF00D4FF),
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTournament.ordinal]),
              color = Color(0xFF00D4FF),
              height = 3.dp
            )
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          DrawTournamentType.values().forEach { t ->
            val isSelected = selectedTournament == t
            Tab(
              selected = isSelected,
              onClick = {
                selectedTournament = t
                currentPotIndex = 1
                groups.forEach { it.teams.clear() }
                lastDrawnTeam = null
                lastDrawnMessage = "${t.displayName} kura çekimi hazırlandı."
              },
              text = {
                Text(
                  text = "${t.badge} ${t.displayName}",
                  fontSize = 10.5.sp,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                  color = if (isSelected) Color(0xFF00D4FF) else Color(0xFF94A3B8),
                  maxLines = 1,
                  softWrap = false
                )
              }
            )
          }
        }

        // =========================================================================
        // MAIN SCROLLABLE CEREMONY STAGE (EXACTLY MATCHING THE USER REFERENCE IMAGE)
        // =========================================================================
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
        ) {
          // 1. GRAND UEFA CEREMONY ARENA STAGE (Trophy + 3D Presenters + 4 Glass Bowls + Digital LED Screen)
          item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0xFF00D4FF).copy(alpha = 0.35f)),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF061026)),
              border = BorderStroke(1.5.dp, Color(0xFF00D4FF).copy(alpha = 0.8f))
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(
                    Brush.verticalGradient(
                      listOf(
                        Color(0xFF071738),
                        Color(0xFF0A2254),
                        Color(0xFF040F26)
                      )
                    )
                  )
                  .padding(10.dp)
              ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                  // Stage Overhead: UEFA Logo Banner & Stadium Spotlights
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text("⭐", fontSize = 14.sp)
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "UEFA DRAW CEREMONY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        softWrap = false
                      )
                    }

                    // Active Pot Indicator Pill
                    Surface(
                      color = Color(0xFF0C2B64),
                      shape = RoundedCornerShape(12.dp),
                      border = BorderStroke(1.dp, Color(0xFF00D4FF))
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Box(
                          modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                          text = "AKTİF: POT $currentPotIndex",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Black,
                          color = Color.White,
                          maxLines = 1,
                          softWrap = false
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // =====================================================================
                  // TRIPLE STAGE COMPOSITION:
                  // [Left: Silver Trophy] + [Center: Presenters & 4 Bowls] + [Right: LED Screen]
                  // =====================================================================
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(240.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                  ) {
                    // LEFT: Giant Gleaming Silver Champions League Trophy ("Big Ears")
                    Box(
                      modifier = Modifier
                        .width(72.dp)
                        .fillMaxHeight(),
                      contentAlignment = Alignment.BottomCenter
                    ) {
                      BigEarsSilverTrophyView(
                        sparkleProgress = trophySparkle,
                        modifier = Modifier.fillMaxSize()
                      )
                    }

                    // CENTER: 3D Cute Chibi UEFA Presenters & The 4 Glass Bowls
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp),
                      contentAlignment = Alignment.BottomCenter
                    ) {
                      CeremonyStageWithPresentersAndBowls(
                        currentPotIndex = currentPotIndex,
                        isDrawing = isDrawing,
                        lastDrawnTeam = lastDrawnTeam,
                        neonGlow = neonGlow,
                        onSelectPot = { potNum -> currentPotIndex = potNum },
                        modifier = Modifier.fillMaxSize()
                      )
                    }

                    // RIGHT: Digital Glowing Vertical LED Screen (Team Roster Display)
                    Box(
                      modifier = Modifier
                        .width(96.dp)
                        .fillMaxHeight(),
                      contentAlignment = Alignment.BottomCenter
                    ) {
                      DigitalLedBoardScreen(
                        selectedTournament = selectedTournament,
                        drawnGroups = groups,
                        modifier = Modifier.fillMaxSize()
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // =====================================================================
                  // REVEALED TEAM CARD BANNER (Held up by Presenter)
                  // =====================================================================
                  AnimatedVisibility(
                    visible = showCardRevealBanner && lastDrawnTeam != null,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -20 })
                  ) {
                    Surface(
                      color = Color(0xFF04102B),
                      shape = RoundedCornerShape(10.dp),
                      border = BorderStroke(1.5.dp, Color(0xFF00D4FF)),
                      shadowElevation = 8.dp,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text("⭐", fontSize = 18.sp)
                          Spacer(modifier = Modifier.width(8.dp))
                          Column {
                            Text(
                              text = "UEFA DRAW • ÇEKİLEN TAKIM",
                              fontSize = 9.sp,
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFF38BDF8),
                              maxLines = 1,
                              softWrap = false
                            )
                            Text(
                              text = lastDrawnTeam?.uppercase() ?: "",
                              fontSize = 15.sp,
                              fontWeight = FontWeight.Black,
                              color = Color.White,
                              letterSpacing = 0.5.sp,
                              maxLines = 1,
                              softWrap = false,
                              overflow = TextOverflow.Ellipsis
                            )
                          }
                        }

                        Surface(
                          color = Color(0xFF0284C7),
                          shape = RoundedCornerShape(6.dp)
                        ) {
                          Text(
                            text = "POT $currentPotIndex",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            softWrap = false
                          )
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  // Status Message
                  Text(
                    text = lastDrawnMessage,
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  // =====================================================================
                  // CONTROL DECK (TOPU ÇEK, TÜMÜNÜ ÇEK, SIFIRLA)
                  // =====================================================================
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    // 1. Draw Ball (Kura Çek)
                    Button(
                      onClick = { drawNextTeam() },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4FF)),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_draw_ball")
                    ) {
                      Text("⭐", fontSize = 14.sp)
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Topu Çek (Kura)",
                        color = Color(0xFF040A1A),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                      )
                    }

                    // 2. Auto Draw All (Hızlı Otomatik)
                    Button(
                      onClick = { autoDrawAll() },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_auto_draw")
                    ) {
                      Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(5.dp))
                      Text(
                        text = "Tümünü Çek (Hızlı)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                      )
                    }

                    // 3. Reset Button
                    Surface(
                      color = Color(0xFF1E293B),
                      shape = RoundedCornerShape(10.dp),
                      border = BorderStroke(1.dp, Color(0xFF475569)),
                      modifier = Modifier
                        .size(44.dp)
                        .clickable {
                          groups.forEach { it.teams.clear() }
                          currentPotIndex = 1
                          lastDrawnTeam = null
                          lastDrawnMessage = "Kura sıfırlandı. Yeniden başlayabilirsiniz."
                        }
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sıfırla", tint = Color.White, modifier = Modifier.size(20.dp))
                      }
                    }
                  }
                }
              }
            }
          }

          // 2. FORMED GROUPS & MATCHUPS SECTION
          item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏆", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Oluşan UEFA Grupları ve Eşleşmeleri",
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  color = Color.White,
                  maxLines = 1,
                  softWrap = false
                )
              }

              Surface(
                color = Color(0xFF0F265C),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFF1D4ED8))
              ) {
                Text(
                  text = "8 Grup • 32 Takım",
                  color = Color(0xFF93C5FD),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          // Groups list
          items(groups) { group ->
            Card(
              colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1938)),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, Color(0xFF1E3A8A)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00D4FF))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = group.groupName,
                      color = Color(0xFF00D4FF),
                      fontWeight = FontWeight.Black,
                      fontSize = 12.sp,
                      maxLines = 1,
                      softWrap = false
                    )
                  }
                  Text(
                    text = "${group.teams.size}/4 Takım",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    maxLines = 1,
                    softWrap = false
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (group.teams.isEmpty()) {
                  Text(
                    text = "Henüz takım çekilmedi...",
                    color = Color(0xFF475569),
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    maxLines = 1,
                    softWrap = false
                  )
                } else {
                  group.teams.forEachIndexed { idx, team ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        color = Color(0xFF1E293B),
                        shape = CircleShape,
                        border = BorderStroke(0.5.dp, Color(0xFF38BDF8)),
                        modifier = Modifier.size(17.dp)
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Text(
                            text = "${idx + 1}",
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                          )
                        }
                      }
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = team,
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        modifier = Modifier.weight(1f)
                      )
                      Surface(
                        color = Color(0xFF0C2B64),
                        shape = RoundedCornerShape(4.dp)
                      ) {
                        Text(
                          text = "POT ${idx + 1}",
                          fontSize = 8.5.sp,
                          color = Color(0xFF38BDF8),
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                          maxLines = 1,
                          softWrap = false
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(12.dp))
          }
        }

        // =========================================================================
        // BOTTOM ACTION: EXPORT DRAWN MATCHES TO BULLETIN
        // =========================================================================
        Surface(
          color = Color(0xFF050B18),
          shadowElevation = 8.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.padding(12.dp)) {
            Button(
              onClick = {
                val generatedMatches = mutableListOf<Match>()
                val todayIso = CalendarManagementService.getCurrentDateIso()
                var codeCounter = 50001

                groups.forEachIndexed { gIdx, group ->
                  if (group.teams.size >= 2) {
                    for (i in 0 until group.teams.size step 2) {
                      if (i + 1 < group.teams.size) {
                        val tHome = group.teams[i]
                        val tAway = group.teams[i + 1]
                        val mId = "drawn_${selectedTournament.name.lowercase()}_${gIdx}_${i}"

                        val match = Match(
                          id = mId,
                          sport = Sport.FOOTBALL,
                          league = selectedTournament.displayName,
                          homeTeam = tHome,
                          awayTeam = tAway,
                          homeScore = 0,
                          awayScore = 0,
                          minute = 0,
                          status = MatchStatus.SCHEDULED,
                          markets = listOf(
                            Market(
                              id = "${mId}_ms",
                              type = MarketType.MATCH_RESULT,
                              name = "Maç Sonucu",
                              selections = listOf(
                                Selection("${mId}_1", "${mId}_ms", "MS 1", 2.15),
                                Selection("${mId}_x", "${mId}_ms", "MS X", 3.25),
                                Selection("${mId}_2", "${mId}_ms", "MS 2", 2.80)
                              )
                            ),
                            Market(
                              id = "${mId}_25",
                              type = MarketType.TOTAL_GOALS_25,
                              name = "2.5 Gol Alt/Üst",
                              selections = listOf(
                                Selection("${mId}_ov", "${mId}_25", "2.5 Üst", 1.72),
                                Selection("${mId}_un", "${mId}_25", "2.5 Alt", 1.88)
                              )
                            )
                          ),
                          statistics = MatchStatistics(xgHome = 1.65, xgAway = 1.35),
                          aiPrediction = AiPrediction(
                            homeWinProb = 48, drawProb = 26, awayWinProb = 26,
                            predictedScore = "2 - 1", confidence = 82,
                            xgSummary = "Kura eşleşmesinde taktiksel hücum üstünlüğü.",
                            tacticalAnalysis = "${selectedTournament.displayName} Kura Eşleşmesi.",
                            formRatingHome = "G-G-B", formRatingAway = "G-B-G"
                          ),
                          events = emptyList(),
                          startTime = if (selectedTournament == DrawTournamentType.CHAMPIONS_LEAGUE) "22:00" else "19:45",
                          isHot = true,
                          stadium = "${tHome} Stadyumu",
                          tvBroadcast = "TRT 1 / Tabii Spor",
                          week = 1,
                          matchDate = "Hafta İçi (Kura Maçı)",
                          matchDateIso = todayIso,
                          iddaaCode = (codeCounter++).toString(),
                          mbs = 1,
                          isKralOran = true,
                          popularBetPercentage = 85
                        )
                        generatedMatches.add(match)
                      }
                    }
                  }
                }

                if (generatedMatches.isNotEmpty()) {
                  onAddDrawnMatchesToBulletin(generatedMatches)
                  onDismiss()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_export_drawn_matches")
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Kura Eşleşmelerini Bültene Aktar ve Bahis Oyna",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 12.5.sp,
                maxLines = 1,
                softWrap = false
              )
            }
          }
        }
      }
    }
  }
}

/**
 * 1. Big Ears Trophy ("Kupa"):
 * Drawn with high-spec metallic chrome gradients, dual curved handles, engraved text, and glowing neon base.
 */
@Composable
fun BigEarsSilverTrophyView(
  sparkleProgress: Float,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    // Blue glowing pedestal light
    drawCircle(
      brush = Brush.radialGradient(
        listOf(Color(0xFF00E5FF).copy(alpha = 0.45f), Color.Transparent),
        center = Offset(w * 0.5f, h * 0.95f),
        radius = w * 0.7f
      ),
      radius = w * 0.7f,
      center = Offset(w * 0.5f, h * 0.95f)
    )

    // Trophy Pedestal (Square tiered base)
    val baseTop = h * 0.88f
    drawRect(
      brush = Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))),
      topLeft = Offset(w * 0.15f, baseTop),
      size = Size(w * 0.7f, h * 0.08f)
    )
    drawRect(
      color = Color(0xFF00D4FF),
      topLeft = Offset(w * 0.15f, baseTop),
      size = Size(w * 0.7f, 2.dp.toPx()),
      style = Stroke(width = 1.dp.toPx())
    )

    // Metallic chrome trophy cup body
    val chromeBrush = Brush.horizontalGradient(
      listOf(
        Color(0xFF94A3B8),
        Color(0xFFF1F5F9),
        Color(0xFFCBD5E1),
        Color.White,
        Color(0xFF94A3B8),
        Color(0xFF64748B)
      )
    )

    val cupPath = Path().apply {
      moveTo(w * 0.28f, h * 0.24f) // Left rim
      lineTo(w * 0.72f, h * 0.24f) // Right rim
      cubicTo(w * 0.74f, h * 0.52f, w * 0.62f, h * 0.72f, w * 0.54f, h * 0.78f) // Right curve down to stem
      lineTo(w * 0.46f, h * 0.78f) // Stem base
      cubicTo(w * 0.38f, h * 0.72f, w * 0.26f, h * 0.52f, w * 0.28f, h * 0.24f) // Left curve up
      close()
    }
    drawPath(path = cupPath, brush = chromeBrush)

    // Trophy Rim Ring
    drawOval(
      brush = Brush.horizontalGradient(listOf(Color(0xFFCBD5E1), Color.White, Color(0xFF94A3B8))),
      topLeft = Offset(w * 0.26f, h * 0.22f),
      size = Size(w * 0.48f, h * 0.05f)
    )

    // Left "Big Ear" Handle
    val leftHandle = Path().apply {
      moveTo(w * 0.28f, h * 0.26f)
      cubicTo(w * 0.02f, h * 0.22f, w * 0.02f, h * 0.60f, w * 0.34f, h * 0.68f)
      cubicTo(w * 0.12f, h * 0.58f, w * 0.12f, h * 0.30f, w * 0.28f, h * 0.28f)
      close()
    }
    drawPath(
      path = leftHandle,
      brush = Brush.horizontalGradient(listOf(Color.White, Color(0xFFCBD5E1), Color(0xFF64748B)))
    )

    // Right "Big Ear" Handle
    val rightHandle = Path().apply {
      moveTo(w * 0.72f, h * 0.26f)
      cubicTo(w * 0.98f, h * 0.22f, w * 0.98f, h * 0.60f, w * 0.66f, h * 0.68f)
      cubicTo(w * 0.88f, h * 0.58f, w * 0.88f, h * 0.30f, w * 0.72f, h * 0.28f)
      close()
    }
    drawPath(
      path = rightHandle,
      brush = Brush.horizontalGradient(listOf(Color(0xFF64748B), Color(0xFFCBD5E1), Color.White))
    )

    // Trophy Stem & Base Tier
    drawRect(
      brush = chromeBrush,
      topLeft = Offset(w * 0.44f, h * 0.78f),
      size = Size(w * 0.12f, h * 0.07f)
    )
    drawOval(
      brush = chromeBrush,
      topLeft = Offset(w * 0.32f, h * 0.84f),
      size = Size(w * 0.36f, h * 0.045f)
    )

    // Specular light sheen across trophy
    val sheenX = w * (0.35f + sparkleProgress * 0.3f)
    drawLine(
      color = Color.White.copy(alpha = 0.7f),
      start = Offset(sheenX, h * 0.26f),
      end = Offset(sheenX - 8.dp.toPx(), h * 0.70f),
      strokeWidth = 3.dp.toPx(),
      cap = StrokeCap.Round
    )
  }
}

/**
 * 2. Center Stage with 3D Cute Chibi UEFA Presenters & 4 Transparent Glass Bowls.
 * Exactly like in the image: Presenters in navy suits, glass bowls labeled POT 1, POT 2, POT 3, POT 4.
 */
@Composable
fun CeremonyStageWithPresentersAndBowls(
  currentPotIndex: Int,
  isDrawing: Boolean,
  lastDrawnTeam: String?,
  neonGlow: Float,
  onSelectPot: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier) {
    // 1. Chibi Presenters Illustration Row (Standing behind the stage table)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(130.dp)
        .align(Alignment.TopCenter)
        .padding(top = 10.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.Bottom
    ) {
      // Presenter 1 (Left - Holding the Team Card)
      ChibiPresenterAvatar(
        hairColor = Color(0xFF1E1B18),
        suitColor = Color(0xFF0C1938),
        expression = "😊",
        isHoldingCard = true,
        teamCardText = lastDrawnTeam?.take(10) ?: "UEFA",
        isRaisingBall = false
      )

      // Presenter 2 (Center - Excitedly Raising Starball up!)
      ChibiPresenterAvatar(
        hairColor = Color(0xFF261C14),
        suitColor = Color(0xFF0F224D),
        expression = "😄",
        isHoldingCard = false,
        teamCardText = "",
        isRaisingBall = true
      )

      // Presenter 3 (Right - Assisting with Pot 3)
      ChibiPresenterAvatar(
        hairColor = Color(0xFF5D4037),
        suitColor = Color(0xFF0C1938),
        expression = "😃",
        isHoldingCard = false,
        teamCardText = "",
        isRaisingBall = false
      )
    }

    // 2. The Futuristic Stage Table with 4 Transparent Glass Bowls (POT 1, POT 2, POT 3, POT 4)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
    ) {
      // 4 Glass Bowls sitting on top of the stage table
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        for (potNum in 1..4) {
          val isActive = currentPotIndex == potNum
          GlassDrawBowlItem(
            potNumber = potNum,
            isActive = isActive,
            isDrawing = isDrawing && isActive,
            neonGlow = neonGlow,
            onClick = { onSelectPot(potNum) },
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Neon LED Stage Base Table with UEFA Emblem
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(34.dp)
          .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xFF0A2254),
                Color(0xFF05122E)
              )
            )
          )
          .border(
            BorderStroke(1.5.dp, Color(0xFF00D4FF).copy(alpha = neonGlow)),
            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("⭐", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "UEFA CHAMPIONS LEAGUE",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp,
            maxLines = 1,
            softWrap = false
          )
        }
      }
    }
  }
}

/**
 * 3. Individual Glass Bowl (POT 1, POT 2, POT 3, POT 4):
 * Semi-transparent glass bowl with rim highlights, glowing pedestal, and starballs inside.
 */
@Composable
fun GlassDrawBowlItem(
  potNumber: Int,
  isActive: Boolean,
  isDrawing: Boolean,
  neonGlow: Float,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bowlBorderColor = if (isActive) Color(0xFF00E5FF) else Color(0xFF38BDF8).copy(alpha = 0.4f)
  val bowlGlowAlpha = if (isActive) neonGlow else 0.2f

  Column(
    modifier = modifier
      .padding(horizontal = 2.dp)
      .clickable { onClick() },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Glass Bowl Canvas with Starballs
    Box(
      modifier = Modifier
        .size(46.dp, 36.dp)
        .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0x3300D4FF),
              Color(0x660A2254)
            )
          )
        )
        .border(
          BorderStroke(1.2.dp, bowlBorderColor),
          RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      // Multiple starballs inside bowl
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("⭐", fontSize = 9.sp)
        Text("⚽", fontSize = 11.sp)
        Text("⭐", fontSize = 9.sp)
      }
    }

    // Pedestal Plate (POT Label)
    Surface(
      color = if (isActive) Color(0xFF0052CC) else Color(0xFF0A1838),
      shape = RoundedCornerShape(3.dp),
      border = BorderStroke(1.dp, if (isActive) Color(0xFF00E5FF) else Color(0xFF1E3A8A)),
      modifier = Modifier.padding(top = 2.dp)
    ) {
      Text(
        text = "POT $potNumber",
        fontSize = 8.sp,
        fontWeight = FontWeight.Black,
        color = if (isActive) Color(0xFF00E5FF) else Color(0xFF94A3B8),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
        maxLines = 1,
        softWrap = false
      )
    }
  }
}

/**
 * 4. Stylized Chibi Presenter Avatar:
 * Cute anime delegate in navy suit holding team card / starball.
 */
@Composable
fun ChibiPresenterAvatar(
  hairColor: Color,
  suitColor: Color,
  expression: String,
  isHoldingCard: Boolean,
  teamCardText: String,
  isRaisingBall: Boolean,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Raised Starball in hand (if center presenter)
    if (isRaisingBall) {
      Surface(
        color = Color.White,
        shape = CircleShape,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0xFF00D4FF)),
        modifier = Modifier.size(20.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text("⭐", fontSize = 11.sp)
        }
      }
      Spacer(modifier = Modifier.height(2.dp))
    }

    // Chibi Head & Slick Hair
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(CircleShape)
        .background(Color(0xFFFFDFBA)), // Anime skin tone
      contentAlignment = Alignment.Center
    ) {
      // Hair top cap
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(18.dp)
          .align(Alignment.TopCenter)
          .background(hairColor)
      )
      // Face Expression
      Text(
        text = expression,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 6.dp)
      )
    }

    // Navy Suit with Tie
    Box(
      modifier = Modifier
        .size(32.dp, 28.dp)
        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        .background(suitColor),
      contentAlignment = Alignment.TopCenter
    ) {
      // White shirt collar + Navy Tie
      Row(
        modifier = Modifier.padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(3.dp, 8.dp)
            .background(Color(0xFF00D4FF)) // Cyan tie
        )
      }
    }

    // Held Team Card (Presenter 1)
    if (isHoldingCard) {
      Surface(
        color = Color(0xFF040A1A),
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(1.dp, Color.White),
        modifier = Modifier
          .width(54.dp)
          .padding(top = 2.dp)
      ) {
        Text(
          text = teamCardText,
          fontSize = 7.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          textAlign = TextAlign.Center,
          maxLines = 1,
          softWrap = false,
          modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
        )
      }
    }
  }
}

/**
 * 5. Right Side Vertical Digital LED Screen:
 * Glowing blue stadium monitor showing UEFA logo and list of qualified clubs with badges.
 */
@Composable
fun DigitalLedBoardScreen(
  selectedTournament: DrawTournamentType,
  drawnGroups: List<DrawnGroup>,
  modifier: Modifier = Modifier
) {
  val teamsList = remember(selectedTournament) {
    listOf(
      Pair("Real Madrid", "👑"),
      Pair("Manchester City", "🔵"),
      Pair("Bayern München", "🔴"),
      Pair("Barcelona", "🔴🔵"),
      Pair("PSG", "🔵🔴"),
      Pair("Liverpool", "🔴"),
      Pair("Inter", "🔵⚫"),
      Pair("Borussia Dortmund", "🟡")
    )
  }

  Surface(
    color = Color(0xFF071B42),
    shape = RoundedCornerShape(8.dp),
    border = BorderStroke(1.5.dp, Color(0xFF00D4FF).copy(alpha = 0.8f)),
    shadowElevation = 6.dp,
    modifier = modifier
      .padding(start = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(4.dp)
    ) {
      // Screen Header with UEFA star
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("⭐", fontSize = 8.sp)
        Spacer(modifier = Modifier.width(2.dp))
        Text(
          text = "UEFA CL",
          fontSize = 8.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF00D4FF),
          maxLines = 1,
          softWrap = false
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(1.dp)
          .background(Color(0xFF00D4FF))
          .padding(vertical = 2.dp)
      )

      Spacer(modifier = Modifier.height(2.dp))

      // Team list rows
      teamsList.forEach { (team, badge) ->
        val isDrawn = drawnGroups.any { g -> g.teams.contains(team) }
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = badge, fontSize = 7.sp)
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = team.take(10),
            fontSize = 7.5.sp,
            fontWeight = if (isDrawn) FontWeight.Black else FontWeight.Bold,
            color = if (isDrawn) Color(0xFF00E5FF) else Color(0xFFE2E8F0),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false
          )
        }
      }
    }
  }
}
