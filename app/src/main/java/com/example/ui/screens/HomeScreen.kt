package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.data.model.Ticket
import com.example.ui.components.HeroSpotlightCard
import com.example.ui.components.InteractiveMatchDetailDialog
import com.example.ui.components.LiveCouponTrackerCard
import com.example.ui.components.LiveDrawSimulatorDialog
import com.example.ui.components.LiveGoalBanner
import com.example.ui.components.MackolikLiveDashboardComponent
import com.example.ui.components.MackolikLeagueFilterRow
import com.example.ui.components.MatchCard
import com.example.ui.components.QuickActionGrid
import com.example.ui.components.RealTimeLiveScoreDashboardComponent
import com.example.ui.components.SportCategoriesRow
import com.example.service.CalendarManagementService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.ui.theme.TealDark
import com.example.ui.theme.LiveRed
import com.example.ui.theme.GoldYellow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.collectAsState
import com.example.data.model.ArenaNewsArticle
import com.example.data.remote.ArenaNewsRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.theme.GoldYellow

import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.components.MotorsportsHubDialog
import com.example.ui.components.MotorsportSeries
import com.example.ui.components.StickyBannerAdView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  matches: List<Match>,
  walletPoints: Long = 10000L,
  selectedSport: Sport?,
  onlyLive: Boolean,
  selectedWeek: Int = 0,
  selectedDateIso: String? = null,
  selectedSelections: List<SlipSelection>,
  simulationMessage: String?,
  couponGoalMessage: String? = null,
  tickets: List<Ticket> = emptyList(),
  favoriteMatchIds: Set<String> = emptySet(),
  onlyFavorites: Boolean = false,
  onlyCouponMatches: Boolean = false,
  onSelectSport: (Sport?) -> Unit,
  onToggleLive: (Boolean) -> Unit,
  onSelectWeek: (Int) -> Unit = {},
  onSelectDateIso: (String?) -> Unit = {},
  onToggleFavorite: (String) -> Unit = {},
  onToggleFavoritesFilter: (Boolean) -> Unit = {},
  onToggleCouponMatchesFilter: (Boolean) -> Unit = {},
  onCashoutTicket: (Ticket) -> Unit = {},
  onOpenCouponsScreen: () -> Unit = {},
  onImportDeepLink: (String) -> Unit = {},
  onSelectOdd: (SlipSelection) -> Unit,
  onOpenMatchDetail: (Match) -> Unit,
  onTriggerGoal: (String, Boolean) -> Unit = { _, _ -> },
  onClaimDailyBonus: () -> Unit = {},
  onRechargeBalance: (Long) -> Unit = {},
  onOpenLiveScores: () -> Unit,
  onOpenEditors: () -> Unit,
  onOpenPopularCoupons: () -> Unit,
  onOpenSimulation: () -> Unit,
  onOpenLeaderboard: () -> Unit,
  onOpenSporToto: () -> Unit,
  onOpenFinishedMatches: () -> Unit = {},
  onOpenVoiceAi: () -> Unit = {},
  onOpenLuckyWheel: () -> Unit = {},
  onOpenNews: () -> Unit = {},
  onOpenMissions: () -> Unit = {},
  onOpenSanalOyunlar: () -> Unit = {},
  onOpenRewardedAd: () -> Unit = {},
  onClearSimulationMessage: () -> Unit = {},
  onOpenLiveDraw: () -> Unit = {},
  onAddDrawnMatches: (List<Match>) -> Unit = {},
  onOpenLiveStandings: () -> Unit = {}
) {
  var selectedLeague by remember { mutableStateOf("Tümü") }
  var showImportDeepLinkDialog by remember { mutableStateOf(false) }
  var showAiWizard by remember { mutableStateOf(false) }
  var showLiveDrawDialog by remember { mutableStateOf(false) }
  var showLiveStandingsD3Dialog by remember { mutableStateOf(false) }
  var isStandingsSectionVisible by remember { mutableStateOf(false) }
  var deepLinkInput by remember { mutableStateOf("") }
  var searchQuery by remember { mutableStateOf("") }
  var isSearchExpanded by remember { mutableStateOf(false) }
  var onlyKralOran by remember { mutableStateOf(false) }
  var selectedNewsArticle by remember { mutableStateOf<ArenaNewsArticle?>(null) }

  val liveMatchesCount = remember(matches) { matches.count { it.status == com.example.data.model.MatchStatus.LIVE } }
  val footballCount = remember(matches) { matches.count { it.sport == Sport.FOOTBALL } }
  val basketballCount = remember(matches) { matches.count { it.sport == Sport.BASKETBALL } }
  val motorSportsCount = remember(matches) { matches.count { it.sport == Sport.MOTORSPORTS } }
  val tennisCount = remember(matches) { matches.count { it.sport == Sport.TENNIS } }
  val volleyballCount = remember(matches) { matches.count { it.sport == Sport.VOLLEYBALL } }
  val iceHockeyCount = remember(matches) { matches.count { it.sport == Sport.ICE_HOCKEY } }
  val handballCount = remember(matches) { matches.count { it.sport == Sport.HANDBALL } }
  val esportsCount = remember(matches) { matches.count { it.sport == Sport.ESPORTS } }

  var showMotorsportsHub by remember { mutableStateOf(false) }
  var selectedMotorsportSeries by remember { mutableStateOf(MotorsportSeries.MOTOGP) }
  var showFavoriteTeamAnalytics by remember { mutableStateOf(false) }
  var showCouponVisualizer by remember { mutableStateOf(false) }
  var showPlayerDirectoryDialog by remember { mutableStateOf(false) }
  var showGeminiIngestionDialog by remember { mutableStateOf(false) }
  var showTransferMarketDialog by remember { mutableStateOf(false) }
  var interactiveDetailMatch by remember { mutableStateOf<Match?>(null) }

  val yesterdayIsoString = com.example.service.CalendarManagementService.getYesterdayDateIso()

  val filteredMatches = remember(
    matches,
    selectedLeague,
    onlyLive,
    selectedSport,
    selectedWeek,
    selectedDateIso,
    onlyFavorites,
    favoriteMatchIds,
    onlyCouponMatches,
    tickets,
    searchQuery,
    onlyKralOran
  ) {
    val pendingTickets = tickets.filter { it.status == com.example.data.model.TicketStatus.PENDING }
    matches.filter { match ->
      val statusOk = when {
        onlyLive -> match.status == com.example.data.model.MatchStatus.LIVE
        selectedDateIso == yesterdayIsoString -> match.status == com.example.data.model.MatchStatus.FINISHED
        selectedDateIso != null -> true
        selectedWeek > 0 -> true
        else -> match.status != com.example.data.model.MatchStatus.FINISHED
      }
      val leagueOk = when (selectedLeague) {
        "Tümü" -> true
        "Hafta İçi (UEFA & Ülke)" -> com.example.engine.MackolikWeeklyFixtureEngine.isMidweekMatch(match)
        "Hafta Sonu (Ligler)" -> com.example.engine.MackolikWeeklyFixtureEngine.isWeekendMatch(match)
        else -> match.league == selectedLeague
      }
      val liveOk = !onlyLive || match.status == com.example.data.model.MatchStatus.LIVE
      val sportOk = selectedSport == null || match.sport == selectedSport
      val weekOk = selectedWeek == 0 || match.week == selectedWeek
      val dateOk = selectedDateIso == null || match.matchDateIso == selectedDateIso
      val favOk = !onlyFavorites || favoriteMatchIds.contains(match.id)
      val couponOk = !onlyCouponMatches || pendingTickets.any { t ->
        t.selections.any { sel ->
          match.id == sel.matchId || match.homeTeam in sel.matchTeams || match.awayTeam in sel.matchTeams
        }
      }
      val kralOk = !onlyKralOran || match.isKralOran
      val searchOk = searchQuery.isBlank() ||
          match.homeTeam.contains(searchQuery, ignoreCase = true) ||
          match.awayTeam.contains(searchQuery, ignoreCase = true) ||
          match.league.contains(searchQuery, ignoreCase = true) ||
          match.iddaaCode.contains(searchQuery, ignoreCase = true)
      statusOk && leagueOk && liveOk && sportOk && weekOk && dateOk && favOk && couponOk && kralOk && searchOk
    }
  }

  // Canlı Kura Simülatörü Dialog
  if (showLiveDrawDialog) {
    LiveDrawSimulatorDialog(
      onDismiss = { showLiveDrawDialog = false },
      onAddDrawnMatchesToBulletin = { drawnMatches ->
        onAddDrawnMatches(drawnMatches)
        showLiveDrawDialog = false
      }
    )
  }

  // Deep-Link Import Dialog
  if (showImportDeepLinkDialog) {
    AlertDialog(
      onDismissRequest = { showImportDeepLinkDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Link, contentDescription = null, tint = TealDark)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Deep-Link ile Kupon Yükle", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      },
      text = {
        Column {
          Text(
            text = "Kupondaş'tan veya arkadaşınızdan paylaşılan tahminarena://kupon bağlantısını girerek kuponu anında sepetinize ekleyin.",
            fontSize = 12.sp,
            color = Color(0xFF475569)
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = deepLinkInput,
            onValueChange = { deepLinkInput = it },
            placeholder = { Text("tahminarena://kupon?...", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
          )
          Spacer(modifier = Modifier.height(8.dp))
          TextButton(
            onClick = {
              deepLinkInput = "tahminarena://kupon?code=KPN-9281&title=Derbi+Bankosu&odds=3.65&author=Usta+Analist&data=W3sibWF0Y2hJZCI6Im0xIiwibWF0Y2hUZWFtcyI6IkdhbGF0YXNhcmF5IC0gRmVuZXJiYWhjZSIsIm1hcmtldFR5cGUiOiJNQVRDSF9SRVNVTFQiLCJzZWxlY3Rpb25JZCI6InNlbF8xIiwic2VsZWN0aW9uTmFtZSI6IjEiLCJvZGQiOjEuOTUsImlzTGl2ZSI6dHJ1ZX1d"
            }
          ) {
            Text("⚡ Örnek Kupon Bağlantısı Yapıştır", fontSize = 11.sp, color = TealDark, fontWeight = FontWeight.Bold)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (deepLinkInput.isNotBlank()) {
              onImportDeepLink(deepLinkInput.trim())
              showImportDeepLinkDialog = false
              deepLinkInput = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TealDark)
        ) {
          Text("Kuponu Sepete Yükle")
        }
      },
      dismissButton = {
        TextButton(onClick = { showImportDeepLinkDialog = false }) {
          Text("İptal")
        }
      }
    )
  }

  // AI Smart Coupon Wizard BottomSheet
  if (showAiWizard) {
    com.example.ui.components.AiSmartCouponDialog(
      matches = matches,
      onAddSelectionsToSlip = { list ->
        list.forEach { onSelectOdd(it) }
      },
      onDismiss = { showAiWizard = false }
    )
  }

  // Maçkolik Motor Sporları (MotoGP & WRC) Hub Dialog
  if (showMotorsportsHub) {
    MotorsportsHubDialog(
      initialSeries = selectedMotorsportSeries,
      selectedSelections = selectedSelections,
      onSelectOdd = onSelectOdd,
      onDismiss = { showMotorsportsHub = false }
    )
  }

  // Maçkolik 58.450+ Sporcu Veritabanı ve Kadro Havuzu Diyaloğu
  if (showPlayerDirectoryDialog) {
    com.example.ui.components.MackolikPlayerDirectoryDialog(
      onDismiss = { showPlayerDirectoryDialog = false }
    )
  }

  // Gemini AI 80+ Branş Veri Aktarımı & Sezonsal Fikstür Motoru
  if (showGeminiIngestionDialog) {
    com.example.ui.components.GeminiSportsIngestionAndSchedulingDialog(
      onDismiss = { showGeminiIngestionDialog = false }
    )
  }

  // Canlı Transfer Pazarı & Alım-Satım AI Modu
  if (showTransferMarketDialog) {
    com.example.ui.components.LiveTransferMarketAiHub(
      onDismiss = { showTransferMarketDialog = false }
    )
  }

  // Strict Popular Derby Filter: Football (GS-FB, Real-Barca) and Basketball ONLY!
  // Absolutely no other match branches (motorsports, tennis, volleyball, etc.)
  val eligibleDerbies = remember(matches) {
    matches.filter { match ->
      if (match.sport != Sport.FOOTBALL && match.sport != Sport.BASKETBALL) return@filter false

      val teams = "${match.homeTeam} - ${match.awayTeam}".lowercase()
      val isGsFb = teams.contains("galatasaray") && teams.contains("fenerbahçe")
      val isElClasico = teams.contains("real madrid") && teams.contains("barcelona")
      val isBjkTs = teams.contains("beşiktaş") && teams.contains("trabzonspor")
      val isBasketballDerby = match.sport == Sport.BASKETBALL && (
        teams.contains("fenerbahçe") || teams.contains("efes") || teams.contains("panathinaikos") ||
        (teams.contains("real madrid") && teams.contains("barcelona"))
      )

      isGsFb || isElClasico || isBjkTs || isBasketballDerby
    }
  }

  var selectedDerbyId by remember { mutableStateOf<String?>(null) }

  val heroMatch = remember(eligibleDerbies, selectedDerbyId, selectedSport) {
    if (selectedDerbyId != null) {
      eligibleDerbies.find { it.id == selectedDerbyId }
    } else if (selectedSport == Sport.BASKETBALL) {
      eligibleDerbies.find { it.sport == Sport.BASKETBALL && it.status == MatchStatus.LIVE }
        ?: eligibleDerbies.find { it.sport == Sport.BASKETBALL }
    } else {
      eligibleDerbies.find { it.sport == Sport.FOOTBALL && it.status == MatchStatus.LIVE && it.homeTeam.contains("Galatasaray") }
        ?: eligibleDerbies.find { it.sport == Sport.FOOTBALL && it.status == MatchStatus.LIVE }
        ?: eligibleDerbies.find { it.sport == Sport.FOOTBALL && it.homeTeam.contains("Galatasaray") }
        ?: eligibleDerbies.find { it.sport == Sport.FOOTBALL }
        ?: eligibleDerbies.firstOrNull()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("home_screen")
  ) {
    // =========================================================================
    // 1. FIXED TOP HEADER MENU (SABİT ÜST MENÜ - PROFESYONEL SPOR & FİKSTÜR NAVİGASYONU)
    // =========================================================================
    Surface(
      color = Color.White,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        // 1.1 Sport Categories & Quick Actions (Kupon Yükle + Ara)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(modifier = Modifier.weight(1f)) {
            SportCategoriesRow(
              selectedSport = selectedSport,
              onlyLive = onlyLive,
              liveCount = liveMatchesCount,
              totalCount = matches.size,
              footballCount = footballCount,
              basketballCount = basketballCount,
              tennisCount = tennisCount,
              onSelectSport = onSelectSport,
              onToggleLive = onToggleLive
            )
          }

          // Quick Kupon Yükle button
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
              .clickable { showImportDeepLinkDialog = true }
              .padding(start = 4.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.AddLink, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Kupon",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          // Search Toggle Button
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSearchExpanded || searchQuery.isNotEmpty()) TealDark else Color(0xFFF1F5F9),
            modifier = Modifier
              .clickable { isSearchExpanded = !isSearchExpanded }
              .padding(start = 4.dp)
          ) {
            Box(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Search,
                contentDescription = "Ara",
                tint = if (isSearchExpanded || searchQuery.isNotEmpty()) Color.White else Color(0xFF475569),
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }

        // 1.2 Collapsible Search Bar (Opens when "Ara" clicked or query active)
        if (isSearchExpanded || searchQuery.isNotEmpty()) {
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
              Text(
                "Takım, lig veya iddaa kodu ara (GS, FB, NBA)...",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedContainerColor = Color(0xFFF8FAFC),
              focusedContainerColor = Color.White,
              unfocusedBorderColor = Color(0xFFE2E8F0),
              focusedBorderColor = TealDark
            ),
            leadingIcon = {
              Icon(
                Icons.Default.Search,
                contentDescription = "Ara",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
              )
            },
            trailingIcon = {
              IconButton(onClick = {
                searchQuery = ""
                isSearchExpanded = false
              }) {
                Icon(
                  Icons.Default.Close,
                  contentDescription = "Kapat",
                  tint = Color(0xFF64748B),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // 1.3 Compact Date & Filter Strip (Single-line chips: Bugün, Kupondakiler, Favoriler, Kral Oran, vb.)
        val couponMatchCount = remember(tickets) {
          val p = tickets.filter { it.status == com.example.data.model.TicketStatus.PENDING }
          p.flatMap { it.selections }.size
        }

        val currentNowMs = CalendarManagementService.getCurrentTimeMillis()
        val calDate = Calendar.getInstance(CalendarManagementService.turkeyTimeZone).apply {
          timeInMillis = currentNowMs
        }
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = CalendarManagementService.turkeyTimeZone }
        val dayMonthFmt = SimpleDateFormat("dd MMM", Locale.forLanguageTag("tr-TR")).apply { timeZone = CalendarManagementService.turkeyTimeZone }

        val todayIso = isoFormat.format(calDate.time)
        val todayLabel = "Bugün (${dayMonthFmt.format(calDate.time)})"

        calDate.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayIso = isoFormat.format(calDate.time)
        val yesterdayLabel = "Dün (${dayMonthFmt.format(calDate.time)})"

        calDate.timeInMillis = currentNowMs
        calDate.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowIso = isoFormat.format(calDate.time)
        val tomorrowLabel = "Yarın (${dayMonthFmt.format(calDate.time)})"

        val fixtureDates = listOf(
          Pair(todayIso, todayLabel),
          Pair("COUPON", if (couponMatchCount > 0) "🎫 Kupondakiler ($couponMatchCount)" else "🎫 Kupondakiler"),
          Pair("KRAL", "👑 Kral Oran"),
          Pair("FAVS", if (favoriteMatchIds.isNotEmpty()) "⭐ Favoriler (${favoriteMatchIds.size})" else "⭐ Favoriler"),
          Pair(tomorrowIso, tomorrowLabel),
          Pair(yesterdayIso, yesterdayLabel),
          Pair("ALL", "📋 Tüm Bülten")
        )

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(fixtureDates.size) { idx ->
            val (dateKey, label) = fixtureDates[idx]
            val isSelected = when (dateKey) {
              "COUPON" -> onlyCouponMatches
              "KRAL" -> onlyKralOran && !onlyCouponMatches
              "FAVS" -> onlyFavorites && !onlyCouponMatches
              "ALL" -> selectedDateIso == null && !onlyLive && !onlyFavorites && !onlyKralOran && !onlyCouponMatches && selectedWeek == 0
              else -> selectedDateIso == dateKey && !onlyFavorites && !onlyKralOran && !onlyLive && !onlyCouponMatches
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) TealDark else Color(0xFFF1F5F9),
              border = BorderStroke(
                1.dp,
                if (isSelected) TealDark else Color(0xFFE2E8F0)
              ),
              modifier = Modifier.clickable {
                when (dateKey) {
                  "COUPON" -> {
                    onToggleCouponMatchesFilter(!onlyCouponMatches)
                    onlyKralOran = false
                    onToggleFavoritesFilter(false)
                    onToggleLive(false)
                  }
                  "KRAL" -> {
                    onToggleCouponMatchesFilter(false)
                    onlyKralOran = true
                    onToggleFavoritesFilter(false)
                    onToggleLive(false)
                    onSelectDateIso(null)
                    onSelectWeek(0)
                  }
                  "FAVS" -> {
                    onToggleCouponMatchesFilter(false)
                    onlyKralOran = false
                    onToggleFavoritesFilter(true)
                    onToggleLive(false)
                    onSelectDateIso(null)
                    onSelectWeek(0)
                  }
                  "ALL" -> {
                    onToggleCouponMatchesFilter(false)
                    onlyKralOran = false
                    onToggleFavoritesFilter(false)
                    onToggleLive(false)
                    onSelectDateIso(null)
                    onSelectWeek(0)
                  }
                  else -> {
                    onToggleCouponMatchesFilter(false)
                    onlyKralOran = false
                    onToggleFavoritesFilter(false)
                    onToggleLive(false)
                    onSelectDateIso(dateKey)
                  }
                }
              }
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color(0xFF334155),
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                maxLines = 1,
                softWrap = false
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 1.3.1 Haftalık Lig Fikstürü & Canlı Kura Çubuğu (Tüm 38 Hafta + Hafta İçi/Sonu)
        val all38Weeks = remember {
          listOf(Pair(0, "Tüm Fikstür")) + (1..38).map { Pair(it, "$it. Hafta") }
        }

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          // Canlı Kura Çekimi Düğmesi
          item {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1E3A8A),
              border = BorderStroke(1.dp, Color(0xFF3B82F6)),
              modifier = Modifier.clickable { showLiveDrawDialog = true }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text("🎲", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Canlı Kura Simülatörü",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
          }

          // D3 Canlı Puan Tablosu Düğmesi
          item {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isStandingsSectionVisible) Color(0xFF0F766E) else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (isStandingsSectionVisible) Color(0xFF0F766E) else Color(0xFF14B8A6)),
              modifier = Modifier.clickable { isStandingsSectionVisible = !isStandingsSectionVisible }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text("📊", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isStandingsSectionVisible) "D3 Tablo (Açık)" else "D3 Puan Tablosu",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isStandingsSectionVisible) Color.White else Color(0xFF0F766E),
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
          }

          // Hafta İçi (UEFA & Ülke Maçları) Filtresi
          item {
            val isMidweekActive = selectedLeague == "Hafta İçi (UEFA & Ülke)"
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isMidweekActive) Color(0xFF7C3AED) else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (isMidweekActive) Color(0xFF7C3AED) else Color(0xFFE2E8F0)),
              modifier = Modifier.clickable {
                selectedLeague = if (isMidweekActive) "Tümü" else "Hafta İçi (UEFA & Ülke)"
                onSelectDateIso(null)
              }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
              ) {
                Text("⭐", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "Hafta İçi (UEFA & Ülke)",
                  fontSize = 11.sp,
                  fontWeight = if (isMidweekActive) FontWeight.Bold else FontWeight.Medium,
                  color = if (isMidweekActive) Color.White else Color(0xFF475569),
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
          }

          // Hafta Sonu (Lig Maçları) Filtresi
          item {
            val isWeekendActive = selectedLeague == "Hafta Sonu (Ligler)"
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isWeekendActive) Color(0xFF0D9488) else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (isWeekendActive) Color(0xFF0D9488) else Color(0xFFE2E8F0)),
              modifier = Modifier.clickable {
                selectedLeague = if (isWeekendActive) "Tümü" else "Hafta Sonu (Ligler)"
                onSelectDateIso(null)
              }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
              ) {
                Text("🏟️", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "Hafta Sonu (Ligler)",
                  fontSize = 11.sp,
                  fontWeight = if (isWeekendActive) FontWeight.Bold else FontWeight.Medium,
                  color = if (isWeekendActive) Color.White else Color(0xFF475569),
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
          }

          // 1..38 Haftalar
          items(all38Weeks) { (wNum, wLabel) ->
            val isWSelected = selectedWeek == wNum && selectedLeague != "Hafta İçi (UEFA & Ülke)" && selectedLeague != "Hafta Sonu (Ligler)"
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isWSelected) Color(0xFF0284C7) else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (isWSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)),
              modifier = Modifier.clickable {
                if (selectedLeague == "Hafta İçi (UEFA & Ülke)" || selectedLeague == "Hafta Sonu (Ligler)") {
                  selectedLeague = "Tümü"
                }
                onSelectWeek(wNum)
                onSelectDateIso(null)
              }
            ) {
              Text(
                text = wLabel,
                fontSize = 11.sp,
                fontWeight = if (isWSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isWSelected) Color.White else Color(0xFF475569),
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
              )
            }
          }
        }

        // Sezon Sonu / Kura Çekimi Bannerı (Sabit Yatay Layout, Asla Dikey Kırılmaz)
        if (selectedWeek == 38 || selectedWeek == 0) {
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF064E3B),
            border = BorderStroke(1.dp, Color(0xFF10B981)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showLiveDrawDialog = true }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("🏆", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Column(
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = "Avrupa Kupaları Canlı Kura Simülatörü",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  maxLines = 1,
                  softWrap = false,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "UEFA Şampiyonlar Ligi, Avrupa & Konferans Ligi torbaları",
                  fontSize = 9.5.sp,
                  color = Color(0xFFA7F3D0),
                  maxLines = 1,
                  softWrap = false,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = GoldYellow,
                modifier = Modifier.clickable { showLiveDrawDialog = true }
              ) {
                Text(
                  text = "Kura Çek ➔",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF0F172A),
                  maxLines = 1,
                  softWrap = false,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // 1.4 Mackolik Lig Filtresi (Süper Lig, Şampiyonlar Ligi, Premier League...)
        MackolikLeagueFilterRow(
          selectedLeague = selectedLeague,
          onSelectLeague = { selectedLeague = it }
        )
      }
    }

    val hasPendingTickets = remember(tickets) {
      tickets.any { it.status == com.example.data.model.TicketStatus.PENDING }
    }

    // =========================================================================
    // 2. SCROLLABLE DASHBOARD & MATCHES CONTENT
    // =========================================================================
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      // 2.1 Live Goal Banner ONLY for user's coupon match!
      if (couponGoalMessage != null) {
        item {
          LiveGoalBanner(
            message = couponGoalMessage,
            onDismiss = onClearSimulationMessage
          )
        }
      }

      // 2.2 Live Coupon Tracker Widget (Only displayed if user has active pending tickets)
      if (hasPendingTickets) {
        item {
          LiveCouponTrackerCard(
            tickets = tickets,
            matches = matches,
            onCashout = onCashoutTicket,
            onNavigateToCoupons = onOpenCouponsScreen
          )
        }
      }

      // 2.3 Hero Spotlight Match Card (Nesine Derbi Vitrini)
      if (heroMatch != null) {
        if (eligibleDerbies.size > 1) {
          item {
            LazyRow(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "🔥 Derbiler:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                }
              }
              items(eligibleDerbies) { derby ->
                val isSelected = (derby.id == heroMatch.id)
                val isLive = (derby.status == MatchStatus.LIVE)
                val sportEmoji = if (derby.sport == Sport.BASKETBALL) "🏀" else "⚽"
                val shortLabel = when {
                  derby.homeTeam.contains("Galatasaray", ignoreCase = true) -> "GS - FB"
                  derby.homeTeam.contains("Real Madrid", ignoreCase = true) -> "El Clásico"
                  derby.homeTeam.contains("Fenerbahçe Beko", ignoreCase = true) -> "FB Beko - Efes"
                  else -> "${derby.homeTeam.take(4)} - ${derby.awayTeam.take(4)}"
                }
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = if (isSelected) TealDark else Color.White,
                  border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) GoldYellow else if (isLive) LiveRed.copy(alpha = 0.5f) else Color(0xFFCBD5E1)
                  ),
                  modifier = Modifier.clickable { selectedDerbyId = derby.id }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = sportEmoji,
                      fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = shortLabel,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) Color.White else Color(0xFF1E293B)
                    )
                    if (isLive) {
                      Spacer(modifier = Modifier.width(4.dp))
                      Box(
                        modifier = Modifier
                          .size(5.dp)
                          .clip(CircleShape)
                          .background(if (isSelected) GoldYellow else LiveRed)
                      )
                    }
                  }
                }
              }
            }
          }
        }

        item {
          HeroSpotlightCard(
            match = heroMatch,
            selectedSelections = selectedSelections,
            onSelectOdd = onSelectOdd,
            onOpenAiAnalysis = onOpenMatchDetail
          )
        }
      }

      // 2.3.1 D3 / Recharts Live Standings Visualization (Expandable directly in feed)
      if (isStandingsSectionVisible) {
        item {
          com.example.ui.components.D3LiveStandingsVisualization(
            initialLeague = if (selectedLeague != "Tümü" && !selectedLeague.contains("Hafta")) selectedLeague else "Trendyol Süper Lig"
          )
        }
      }

      // 2.4 Günün Maç Bülteni Başlığı
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (onlyFavorites) "⭐ Favori Karşılaşmalarım" else if (onlyLive) "🔴 Canlı Karşılaşmalar" else "📋 Günün Maç Bülteni",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "Resmi oranlar ve canlı skorlar anlık güncelleniyor",
              fontSize = 11.sp,
              color = Color(0xFF64748B),
              fontWeight = FontWeight.Medium
            )
          }
          Surface(
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
          ) {
            Text(
              text = "${filteredMatches.size} Maç",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = TealDark,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      // 7. Match Cards List or Empty Helper
    if (filteredMatches.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          shape = RoundedCornerShape(12.dp),
          colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = if (onlyFavorites) "⭐" else "🔍", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (onlyFavorites) "Henüz favori maç eklemediniz." else if (onlyLive) "Seçili filtrede canlı maç bulunmuyor." else "Filtreye uygun karşılaşma bulunamadı.",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (onlyFavorites) "Maç kartlarındaki yıldıza (☆) dokunarak favorilerinize ekleyebilirsiniz." else "Toplam ${matches.size} karşılaşma bültende mevcuttur.",
              fontSize = 11.sp,
              color = Color(0xFF64748B),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              color = TealDark,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.clickable {
                selectedLeague = "Tümü"
                onToggleFavoritesFilter(false)
                onToggleLive(false)
                onSelectSport(null)
                onSelectWeek(0)
              }
            ) {
              Text(
                text = "Tüm Maçları Göster",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              )
            }
          }
        }
      }
    } else {
      items(filteredMatches, key = { it.id }) { match ->
        MatchCard(
          match = match,
          selectedSelections = selectedSelections,
          isFavorite = favoriteMatchIds.contains(match.id),
          onToggleFavorite = onToggleFavorite,
          onSelectOdd = onSelectOdd,
          onOpenDetail = onOpenMatchDetail,
          onOpenInteractiveDetail = { interactiveDetailMatch = it },
          onOpenMotorsportsHub = { series ->
            selectedMotorsportSeries = series
            showMotorsportsHub = true
          }
        )
      }
    }

    // Secondary Tools & Utilities (Placed below match cards for clean UX)
    item {
      QuickActionGrid(
        onOpenLiveScores = onOpenLiveScores,
        onOpenEditors = onOpenEditors,
        onOpenPopularCoupons = onOpenPopularCoupons,
        onOpenSimulation = onOpenSimulation,
        onOpenLeaderboard = onOpenLeaderboard,
        onOpenSporToto = onOpenSporToto,
        onOpenFinishedMatches = onOpenFinishedMatches,
        onOpenVoiceAi = onOpenVoiceAi,
        onOpenLuckyWheel = onOpenLuckyWheel,
        onOpenNews = onOpenNews,
        onOpenAiWizard = { showAiWizard = true },
        onOpenMissions = onOpenMissions,
        onOpenRewardedAd = onOpenRewardedAd,
        onOpenFavoriteTeamAnalytics = { showFavoriteTeamAnalytics = true },
        onOpenCouponVisualizer = { showCouponVisualizer = true },
        onOpenPlayerDirectory = { showPlayerDirectoryDialog = true },
        onOpenGeminiIngestion = { showGeminiIngestionDialog = true },
        onOpenTransferMarket = { showTransferMarketDialog = true },
        onOpenLiveDraw = { showLiveDrawDialog = true },
        onOpenLiveStandings = { showLiveStandingsD3Dialog = true }
      )
    }

    item(key = "arena_news_preview_section_item") {
      ArenaNewsPreviewSection(
        onSelectArticle = { selectedNewsArticle = it },
        onViewAllNews = onOpenNews
      )
    }

    item {
      StickyBannerAdView(
        onAdClick = onOpenRewardedAd
      )
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Interactive Match Detail Dialog (Tactical Lineups, Historical H2H & Dynamic AI Predictions)
  interactiveDetailMatch?.let { match ->
    InteractiveMatchDetailDialog(
      match = match,
      selectedSelections = selectedSelections,
      onSelectOdd = onSelectOdd,
      onDismiss = { interactiveDetailMatch = null }
    )
  }

  // Favori Takım Analizi Modülü (Dashboard)
  if (showFavoriteTeamAnalytics) {
    com.example.ui.components.FavoriteTeamAnalyticsDialog(
      matches = matches,
      onSelectOdd = onSelectOdd,
      onDismiss = { showFavoriteTeamAnalytics = false }
    )
  }

  // Kupon Görselleştirici & Sosyal Medya Paylaşım Modülü
  if (showCouponVisualizer) {
    com.example.ui.components.CouponVisualizerDialog(
      ticket = tickets.lastOrNull(),
      selections = selectedSelections,
      stakePoints = 100L,
      totalOdds = if (selectedSelections.isNotEmpty()) selectedSelections.fold(1.0) { acc, s -> acc * s.odd } else 1.0,
      onDismiss = { showCouponVisualizer = false }
    )
  }

  // Arena News Article Reader Modal Dialog
  selectedNewsArticle?.let { article ->
    AlertDialog(
      onDismissRequest = { selectedNewsArticle = null },
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = article.emojiBadge, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = article.category,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TealDark
            )
          }
          IconButton(onClick = { selectedNewsArticle = null }, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF64748B))
          }
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
          Text(
            text = article.title,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = Color(0xFF0F172A),
            lineHeight = 20.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = article.content,
            fontSize = 13.sp,
            color = Color(0xFF334155),
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Kaynak: ${article.source}", fontSize = 10.sp, color = Color(0xFF64748B))
            Text(article.publishedAgo, fontSize = 10.sp, color = Color(0xFF94A3B8))
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedNewsArticle = null },
          colors = ButtonDefaults.buttonColors(containerColor = TealDark)
        ) {
          Text("Kapat")
        }
      }
    )
  }

  // D3 / Recharts Canlı Puan Tablosu Modal Dialog
  if (showLiveStandingsD3Dialog) {
    com.example.ui.components.D3LiveStandingsDialog(
      initialLeague = if (selectedLeague != "Tümü" && !selectedLeague.contains("Hafta")) selectedLeague else "Trendyol Süper Lig",
      onDismiss = { showLiveStandingsD3Dialog = false }
    )
  }
  }
}

@Composable
fun ArenaNewsPreviewSection(
  onSelectArticle: (ArenaNewsArticle) -> Unit,
  onViewAllNews: () -> Unit
) {
  val newsList by ArenaNewsRepository.newsFlow.collectAsState(initial = emptyList())
  val isRefreshing by ArenaNewsRepository.isRefreshing.collectAsState(initial = false)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .testTag("arena_news_preview_section"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Sol Başlık Bölümü (weight ile sağ butonların ezilmesi/bozulması engellendi)
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
          ) {
            Text("📰", fontSize = 16.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f, fill = false)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "ARENA HABERLER",
                color = Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, GoldYellow)
              ) {
                Text(
                  text = "GEMİNİ AI",
                  color = GoldYellow,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "Transfer, KAP, FFP & Puan Durumu",
              color = Color(0xFF94A3B8),
              fontSize = 9.5.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Sağ Aksiyon Butonları (Tek satır, asla dikey kolon şeklinde bozulmaz)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.clickable { ArenaNewsRepository.refreshNews() }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isRefreshing) "..." else "Yenile ↻",
                color = GoldYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
            }
          }

          Surface(
            color = TealDark,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, Color(0xFF14B8A6).copy(alpha = 0.4f)),
            modifier = Modifier.clickable { onViewAllNews() }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Tümü ➔",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (newsList.isEmpty()) {
        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "Haber akışı hazırlanıyor...",
              color = Color(0xFF94A3B8),
              fontSize = 11.sp
            )
          }
        }
      } else {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(horizontal = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(newsList.take(8), key = { it.id }) { article ->
            Surface(
              color = Color(0xFF1E293B),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, Color(0xFF334155)),
              modifier = Modifier
                .width(230.dp)
                .height(136.dp)
                .clickable { onSelectArticle(article) }
            ) {
              Column(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      modifier = Modifier.weight(1f),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(article.emojiBadge, fontSize = 12.sp)
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = article.category,
                        color = GoldYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = article.publishedAgo,
                      color = Color(0xFF64748B),
                      fontSize = 9.sp,
                      maxLines = 1
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  Text(
                    text = article.title,
                    color = Color.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                  )

                  Spacer(modifier = Modifier.height(3.dp))

                  Text(
                    text = article.summary,
                    color = Color(0xFFCBD5E1),
                    fontSize = 9.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 12.sp
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = article.source,
                    color = Color(0xFF64748B),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Oku ➔",
                    color = Color(0xFF38BDF8),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
