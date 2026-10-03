package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GoalNotificationScope
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.data.model.TicketStatus
import com.example.data.model.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.components.AdminPanelDialog
import com.example.ui.components.AiAnalysisDialog
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AuthDialog
import com.example.ui.components.BetSlipBottomSheet
import com.example.ui.components.GoalCelebrationOverlay
import com.example.ui.components.LeaderboardDialog
import com.example.ui.components.LiveGoalBanner
import com.example.ui.components.LuckyWheelDialog
import com.example.ui.components.MainTab
import com.example.ui.components.SporTotoDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.components.VoiceAiPredictionDialog
import com.example.ui.screens.ArenaNewsScreen
import com.example.ui.screens.BetHistoryScreen
import com.example.ui.screens.DailyMissionsScreen
import com.example.ui.screens.FinishedMatchesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KupondasScreen
import com.example.ui.screens.KuponlarimScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.LiveScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfilScreen
import com.example.ui.screens.SanalOyunlarScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.components.AdMobInterstitialAdDialog
import com.example.ui.components.RewardedAdDialog
import com.example.ui.theme.TahminArenaTheme

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Handle deep-link intent if app is opened via URL
    intent?.data?.let { uri ->
      viewModel.importDeepLink(uri.toString())
    }

    com.example.service.CalendarManagementService.initContext(this)
    viewModel.syncMatchesWithClock()

    setContent {
      TahminArenaTheme {
        TahminArenaApp(viewModel = viewModel)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    com.example.service.CalendarManagementService.onAppResume()
    viewModel.syncMatchesWithClock()
  }

  override fun onPause() {
    super.onPause()
    com.example.service.CalendarManagementService.persistCurrentTime()
    viewModel.persistMatchesNow()
  }

  override fun onStop() {
    super.onStop()
    com.example.service.CalendarManagementService.persistCurrentTime()
    viewModel.persistMatchesNow()
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    intent.data?.let { uri ->
      viewModel.importDeepLink(uri.toString())
    }
  }
}

@Composable
fun TahminArenaApp(viewModel: MainViewModel) {
  var isLoggedIn by remember { mutableStateOf(true) }
  var currentUserProfile by remember {
    mutableStateOf(
      UserProfile(
        username = "aycadogan",
        email = "aycadogan6464@gmail.com",
        fullName = "Ayça Doğan",
        avatarUrl = "⚡"
      )
    )
  }

  var currentTab by remember { mutableStateOf(MainTab.BULTEN) }
  var isVoiceAiOpen by remember { mutableStateOf(false) }
  var isLuckyWheelOpen by remember { mutableStateOf(false) }
  var isSporTotoOpen by remember { mutableStateOf(false) }
  var isLeaderboardOpen by remember { mutableStateOf(false) }
  var isBetHistoryOpen by remember { mutableStateOf(false) }
  var isSettingsOpen by remember { mutableStateOf(false) }
  var isDailyMissionsOpen by remember { mutableStateOf(false) }
  var isSanalOyunlarOpen by remember { mutableStateOf(false) }
  var isNotificationSettingsOpen by remember { mutableStateOf(false) }
  var isRewardedAdOpen by remember { mutableStateOf(false) }
  var isPostBetInterstitialOpen by remember { mutableStateOf(false) }
  var isLiveDrawOpen by remember { mutableStateOf(false) }
  var isLiveStandingsOpen by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }

  val matches by viewModel.allMatches.collectAsState()
  val selectedSport by viewModel.selectedSport.collectAsState()
  val onlyLive by viewModel.onlyLive.collectAsState()
  val favoriteMatchIds by viewModel.favoriteMatchIds.collectAsState()
  val onlyFavorites by viewModel.onlyFavorites.collectAsState()
  val slipSelections by viewModel.slipSelections.collectAsState()
  val wallet by viewModel.wallet.collectAsState()
  val transactions by viewModel.recentTransactions.collectAsState()
  val tickets by viewModel.tickets.collectAsState()
  val leaderboard by viewModel.leaderboard.collectAsState()
  val dailyMissions by viewModel.dailyMissions.collectAsState()
  val socialCoupons by viewModel.socialCoupons.collectAsState()
  val isSlipOpen by viewModel.isSlipOpen.collectAsState()
  val selectedStake by viewModel.selectedStake.collectAsState()
  val selectedTicketType by viewModel.selectedTicketType.collectAsState()
  val selectedMatchDetail by viewModel.selectedMatchForDetail.collectAsState()
  val userMessage by viewModel.userMessage.collectAsState()
  val simulationMessage by viewModel.lastSimulationMessage.collectAsState()
  val lastGoalAlert by viewModel.lastGoalAlert.collectAsState()
  val goalNotificationScope by viewModel.goalNotificationScope.collectAsState()
  val onlyCouponMatches by viewModel.onlyCouponMatches.collectAsState()
  val selectedWeek by viewModel.selectedWeek.collectAsState()
  val selectedDateIso by viewModel.selectedDateIso.collectAsState()
  val isAdminPanelOpen by viewModel.isAdminPanelOpen.collectAsState()
  var isAuthDialogOpen by remember { mutableStateOf(false) }

  // Check if live goal alert belongs to a match in user's coupon or favorites
  val isGoalInUserCoupon = remember(lastGoalAlert, tickets) {
    val alert = lastGoalAlert ?: return@remember false
    viewModel.isMatchInUserCoupon(alert.matchId, alert.homeTeam, alert.awayTeam)
  }

  val userCouponInfo = remember(lastGoalAlert, tickets) {
    val alert = lastGoalAlert ?: return@remember null
    viewModel.getUserCouponMatchInfo(alert.matchId, alert.homeTeam, alert.awayTeam)
  }

  val isGoalInFavorites = remember(lastGoalAlert, favoriteMatchIds) {
    val alert = lastGoalAlert ?: return@remember false
    favoriteMatchIds.contains(alert.matchId)
  }

  val isGoalNotificationAllowed = when (goalNotificationScope) {
    GoalNotificationScope.ONLY_COUPON -> isGoalInUserCoupon
    GoalNotificationScope.COUPON_AND_FAVORITES -> isGoalInUserCoupon || isGoalInFavorites
    GoalNotificationScope.ALL_MATCHES -> true
    GoalNotificationScope.MUTED -> false
  }

  // couponGoalMessage is shown ONLY when allowed by user's coupon preference
  val couponGoalMessage = if (lastGoalAlert != null && isGoalNotificationAllowed) {
    lastGoalAlert!!.rawMessage
  } else null

  val totalOdds = viewModel.repository.calculateTotalOdds()

  val context = androidx.compose.ui.platform.LocalContext.current

  // Handle Goal Push Notification - Only for user's coupon / permitted scope!
  LaunchedEffect(lastGoalAlert) {
    val alert = lastGoalAlert ?: return@LaunchedEffect
    if (isGoalNotificationAllowed) {
      com.example.service.LocalNotificationManager.notifyGoal(
        context = context,
        matchTitle = "${alert.homeTeam} - ${alert.awayTeam}",
        minute = alert.minute,
        scorer = alert.scorerName,
        newScore = "${alert.homeScore} - ${alert.awayScore}"
      )
    }
  }

  // Ticking Turkey Time (TSİ) Clock & Checking Auto Day/Season Rollover & Match Kickoffs
  LaunchedEffect(Unit) {
    while (true) {
      com.example.service.SeasonRollOverManager.updateTurkeyClock()
      viewModel.syncMatchesWithClock()
      kotlinx.coroutines.delay(1000)
    }
  }

  // Handle Toast / Snackbar messages
  LaunchedEffect(userMessage) {
    userMessage?.let {
      snackbarHostState.showSnackbar(
        message = it,
        duration = SnackbarDuration.Short
      )
      viewModel.dismissUserMessage()
    }
  }

  // Connect Virtual 22-match round engine to wallet
  LaunchedEffect(Unit) {
    com.example.service.VirtualRoundManager.onCreditWinnings = { amount, description ->
      viewModel.addVirtualWinnings(amount, description)
    }
  }

  // If user is not logged in, show Login Screen
  if (!isLoggedIn) {
    LoginScreen(
      onLoginSuccess = { username, email ->
        currentUserProfile = UserProfile(
          username = username,
          email = email,
          fullName = username.replaceFirstChar { it.uppercase() },
          avatarUrl = "⚡"
        )
        isLoggedIn = true
      },
      onContinueAsGuest = {
        isLoggedIn = true
      }
    )
    return
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      val turkeyTimeText by com.example.service.SeasonRollOverManager.turkeyTimeFormatted.collectAsState()
      val currentSeasonWeek by com.example.service.SeasonRollOverManager.currentSeasonWeek.collectAsState()

      TopHeaderBar(
        walletPoints = wallet.availablePoints,
        turkeyTimeText = turkeyTimeText,
        seasonWeekText = "${currentSeasonWeek}. Hafta",
        onClaimDaily = { viewModel.claimDailyBonus() },
        onOpenProfile = { currentTab = MainTab.PROFIL },
        onOpenVoiceAssistant = { isVoiceAiOpen = true },
        onOpenLuckyWheel = { isLuckyWheelOpen = true },
        onOpenSettings = { isSettingsOpen = true },
        onOpenNotificationSettings = { isNotificationSettingsOpen = true },
        onOpenAdminPanel = { viewModel.openAdminPanel() },
        onOpenLiveDraw = { isLiveDrawOpen = true },
        onOpenLiveStandings = { isLiveStandingsOpen = true }
      )
    },
    bottomBar = {
      AppBottomNavigation(
        currentTab = currentTab,
        slipCount = slipSelections.size,
        slipTotalOdds = totalOdds,
        onSelectTab = { currentTab = it },
        onOpenSlip = { viewModel.openSlip() }
      )
    },
    snackbarHost = {
      SnackbarHost(hostState = snackbarHostState)
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        MainTab.BULTEN -> {
          HomeScreen(
            matches = matches,
            walletPoints = wallet.availablePoints,
            selectedSport = selectedSport,
            onlyLive = onlyLive,
            selectedWeek = selectedWeek,
            selectedDateIso = selectedDateIso,
            selectedSelections = slipSelections,
            simulationMessage = simulationMessage,
            couponGoalMessage = couponGoalMessage,
            tickets = tickets,
            favoriteMatchIds = favoriteMatchIds,
            onlyFavorites = onlyFavorites,
            onlyCouponMatches = onlyCouponMatches,
            onSelectSport = { viewModel.setSportFilter(it) },
            onToggleLive = { viewModel.setOnlyLiveFilter(it) },
            onSelectWeek = { viewModel.setSelectedWeek(it) },
            onSelectDateIso = { viewModel.setSelectedDateIso(it) },
            onToggleFavorite = { viewModel.toggleFavoriteMatch(it) },
            onToggleFavoritesFilter = { viewModel.setOnlyFavoritesFilter(it) },
            onToggleCouponMatchesFilter = { viewModel.toggleCouponMatchesFilter() },
            onCashoutTicket = { viewModel.cashoutTicket(it) },
            onOpenCouponsScreen = { currentTab = MainTab.KUPONLARIM },
            onImportDeepLink = { viewModel.importDeepLink(it) },
            onSelectOdd = { viewModel.toggleOddsSelection(it) },
            onOpenMatchDetail = { viewModel.openMatchDetail(it) },
            onTriggerGoal = { id, isHome -> viewModel.triggerManualGoal(id, isHome) },
            onClaimDailyBonus = { viewModel.claimDailyBonus() },
            onRechargeBalance = { viewModel.depositPoints(it) },
            onOpenLiveScores = {
              viewModel.setOnlyLiveFilter(true)
              currentTab = MainTab.CANLI
            },
            onOpenEditors = { currentTab = MainTab.KUPONDAS },
            onOpenPopularCoupons = { currentTab = MainTab.KUPONDAS },
            onOpenSimulation = {
              viewModel.setOnlyLiveFilter(true)
              currentTab = MainTab.CANLI
            },
            onOpenLeaderboard = { isLeaderboardOpen = true },
            onOpenSporToto = { isSporTotoOpen = true },
            onOpenFinishedMatches = { currentTab = MainTab.BITEN },
            onOpenVoiceAi = { isVoiceAiOpen = true },
            onOpenLuckyWheel = { isLuckyWheelOpen = true },
            onOpenNews = { currentTab = MainTab.HABERLER },
            onOpenMissions = { isDailyMissionsOpen = true },
            onOpenSanalOyunlar = { isSanalOyunlarOpen = true },
            onOpenRewardedAd = { isRewardedAdOpen = true },
            onClearSimulationMessage = { viewModel.clearSimulationMessage() },
            onAddDrawnMatches = { viewModel.addDrawnMatchesToBulletin(it) },
            onOpenLiveDraw = { isLiveDrawOpen = true },
            onOpenLiveStandings = { isLiveStandingsOpen = true }
          )
        }

        MainTab.CANLI -> {
          LiveScreen(
            matches = matches,
            selectedSelections = slipSelections,
            simulationMessage = couponGoalMessage,
            favoriteMatchIds = favoriteMatchIds,
            onToggleFavorite = { viewModel.toggleFavoriteMatch(it) },
            onSelectOdd = { viewModel.toggleOddsSelection(it) },
            onOpenMatchDetail = { viewModel.openMatchDetail(it) }
          )
        }

        MainTab.BITEN -> {
          FinishedMatchesScreen(
            matches = matches,
            onOpenMatchDetail = { viewModel.openMatchDetail(it) }
          )
        }

        MainTab.KUPONDAS -> {
          KupondasScreen(
            coupons = socialCoupons,
            matches = matches,
            onCopyCoupon = { viewModel.copyCoupon(it) },
            onLikeCoupon = { viewModel.toggleLikeCoupon(it) },
            onGoToMyTickets = { currentTab = MainTab.KUPONLARIM },
            onImportDeepLink = { viewModel.importDeepLink(it) }
          )
        }

        MainTab.KUPONLARIM -> {
          KuponlarimScreen(
            tickets = tickets,
            matches = matches,
            onShareToKupondas = { ticket ->
              viewModel.shareTicketToKupondas(ticket)
              currentTab = MainTab.KUPONDAS
            },
            onReplayTicket = { ticket ->
              ticket.selections.forEach { viewModel.toggleOddsSelection(it) }
              viewModel.openSlip()
            },
            onCashoutTicket = { viewModel.cashoutTicket(it) },
            onOpenDetailedBetHistory = { isBetHistoryOpen = true }
          )
        }

        MainTab.HABERLER -> {
          ArenaNewsScreen(
            onGoToMatch = { matchId ->
              matches.find { it.id == matchId }?.let { m ->
                viewModel.openMatchDetail(m)
              }
            }
          )
        }

        MainTab.PROFIL -> {
          ProfilScreen(
            userProfile = currentUserProfile,
            walletEntity = wallet,
            tickets = tickets,
            transactions = transactions,
            onClaimDaily = { viewModel.claimDailyBonus() },
            onDeposit = { amount -> viewModel.depositPoints(amount) },
            onOpenBetHistory = { isBetHistoryOpen = true },
            onOpenLeaderboard = { isLeaderboardOpen = true },
            onOpenSettings = { isSettingsOpen = true },
            onOpenAuthDialog = { isAuthDialogOpen = true },
            onOpenAdminPanel = { viewModel.openAdminPanel() },
            onLogout = { isLoggedIn = false }
          )
        }
      }
    }
  }

  // Active Bet Slip Bottom Sheet
  if (isSlipOpen) {
    BetSlipBottomSheet(
      selections = slipSelections,
      totalOdds = totalOdds,
      selectedStake = selectedStake,
      selectedType = selectedTicketType,
      walletBalance = wallet.availablePoints,
      matches = matches,
      onStakeChange = { viewModel.setSelectedStake(it) },
      onTypeChange = { viewModel.setSelectedTicketType(it) },
      onRemoveItem = { viewModel.removeSlipItem(it) },
      onClearAll = { viewModel.clearSlip() },
      onPlaceBet = {
        viewModel.placeBet()
        isPostBetInterstitialOpen = true
      },
      onDismiss = { viewModel.closeSlip() }
    )
  }

  // Match AI Analysis and Detail Bottom Sheet Modal
  selectedMatchDetail?.let { match ->
    val liveMatch = matches.find { it.id == match.id } ?: match
    AiAnalysisDialog(
      match = liveMatch,
      allMatches = matches,
      selectedSelections = slipSelections,
      onSelectOdd = { viewModel.toggleOddsSelection(it) },
      onTriggerGoal = { id, isHome -> viewModel.triggerManualGoal(id, isHome) },
      onUpdateMatchState = { matchId, minute, extraMinute, isFinished, homeScore, awayScore ->
        viewModel.updateMatchLiveMinute(matchId, minute, extraMinute, isFinished, homeScore, awayScore)
      },
      onOpenVoiceAi = { isVoiceAiOpen = true },
      onDismiss = { viewModel.closeMatchDetail() }
    )
  }

  // Voice-Powered AI Match Prediction Assistant Dialog
  if (isVoiceAiOpen) {
    VoiceAiPredictionDialog(
      matches = matches,
      onDismiss = { isVoiceAiOpen = false }
    )
  }

  // Lucky Wheel Dialog
  if (isLuckyWheelOpen) {
    LuckyWheelDialog(
      onDismiss = { isLuckyWheelOpen = false },
      onReward = { amount ->
        viewModel.depositPoints(amount, "Şans Çarkı Ödülü")
      }
    )
  }

  // Spor Toto 15 Maç Bülteni Dialog
  if (isSporTotoOpen) {
    SporTotoDialog(
      walletBalance = wallet.availablePoints,
      onPlayToto = { cost, count ->
        viewModel.playSporToto(cost, count)
        isSporTotoOpen = false
      },
      onDismiss = { isSporTotoOpen = false }
    )
  }

  // Leaderboard Top 10 Dialog & Room Leaderboard
  if (isLeaderboardOpen) {
    Dialog(
      onDismissRequest = { isLeaderboardOpen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      LeaderboardScreen(
        leaderboard = leaderboard,
        onBack = { isLeaderboardOpen = false }
      )
    }
  }

  // Full Screen Bet History (Room DB past records with outcomes)
  if (isBetHistoryOpen) {
    Dialog(
      onDismissRequest = { isBetHistoryOpen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      BetHistoryScreen(
        tickets = tickets,
        matches = matches,
        onBack = { isBetHistoryOpen = false },
        onShareTicket = { ticket ->
          viewModel.shareTicketToKupondas(ticket)
          isBetHistoryOpen = false
          currentTab = MainTab.KUPONDAS
        },
        onReplayTicket = { ticket ->
          ticket.selections.forEach { viewModel.toggleOddsSelection(it) }
          isBetHistoryOpen = false
          viewModel.openSlip()
        }
      )
    }
  }

  // Settings Screen Dialog (Now with Virtual Currency Wallet Management)
  if (isSettingsOpen) {
    Dialog(
      onDismissRequest = { isSettingsOpen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      SettingsScreen(
        walletPoints = wallet.availablePoints,
        goalNotificationScope = goalNotificationScope,
        onSelectGoalNotificationScope = { viewModel.setGoalNotificationScope(it) },
        onRechargeBalance = { viewModel.rechargeBalance(it) },
        onClaimDailyBonus = { viewModel.claimDailyBonus() },
        onWatchRewardedAd = { isRewardedAdOpen = true },
        onResetBalance = { viewModel.resetBalance() },
        onOpenMissions = { isDailyMissionsOpen = true },
        onOpenSanalOyunlar = { isSanalOyunlarOpen = true },
        onBack = { isSettingsOpen = false },
        onLogout = {
          isSettingsOpen = false
          isLoggedIn = false
        }
      )
    }
  }

  // Dedicated Notification Settings Panel Dialog (Only Coupon & Tracked Matches)
  if (isNotificationSettingsOpen) {
    com.example.ui.components.NotificationSettingsDialog(
      currentScope = goalNotificationScope,
      onSaveScope = { viewModel.setGoalNotificationScope(it) },
      onDismiss = { isNotificationSettingsOpen = false }
    )
  }

  // Daily Missions Screen (Room DB)
  if (isDailyMissionsOpen) {
    Dialog(
      onDismissRequest = { isDailyMissionsOpen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      DailyMissionsScreen(
        missions = dailyMissions,
        walletPoints = wallet.availablePoints,
        onClaimReward = { viewModel.claimDailyMission(it) },
        onBack = { isDailyMissionsOpen = false }
      )
    }
  }

  // Sanal Oyunlar & İddaa Screen (Virtual Football, Horses, Zeplin, Spor Toto)
  if (isSanalOyunlarOpen) {
    Dialog(
      onDismissRequest = { isSanalOyunlarOpen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      SanalOyunlarScreen(
        walletPoints = wallet.availablePoints,
        onDeductStake = { amount, desc -> viewModel.deductVirtualStake(amount, desc) },
        onAddWinnings = { amount, desc -> viewModel.addVirtualWinnings(amount, desc) },
        onBack = { isSanalOyunlarOpen = false }
      )
    }
  }

  // Multi-Ad Rewarded Video Ad Simulation Dialog
  if (isRewardedAdOpen) {
    RewardedAdDialog(
      rewardAmount = 500L,
      onDismiss = { isRewardedAdOpen = false },
      onRewardClaimed = { reward ->
        viewModel.onWatchRewardedAd(reward)
        isRewardedAdOpen = false
      }
    )
  }

  // Google AdMob Post-Betting Interstitial Ad Unit
  if (isPostBetInterstitialOpen) {
    AdMobInterstitialAdDialog(
      onDismiss = { isPostBetInterstitialOpen = false }
    )
  }

  // Canlı Kura Simülatörü Dialog
  if (isLiveDrawOpen) {
    com.example.ui.components.LiveDrawSimulatorDialog(
      onDismiss = { isLiveDrawOpen = false },
      onAddDrawnMatchesToBulletin = { drawnList ->
        viewModel.addDrawnMatchesToBulletin(drawnList)
        isLiveDrawOpen = false
      }
    )
  }

  // D3 / Recharts Canlı Puan Tablosu Dialog
  if (isLiveStandingsOpen) {
    com.example.ui.components.D3LiveStandingsDialog(
      initialLeague = "Trendyol Süper Lig",
      onDismiss = { isLiveStandingsOpen = false }
    )
  }

  // Admin Panel Dialog
  if (isAdminPanelOpen) {
    AdminPanelDialog(
      matches = matches,
      onUpdateScore = { matchId, h, a -> viewModel.updateMatchScore(matchId, h, a) },
      onUpdateMinute = { matchId, min -> viewModel.updateMatchMinute(matchId, min) },
      onEndMatch = { matchId -> viewModel.endMatch(matchId) },
      onStartNextLiveMatch = { viewModel.startNextLiveMatch() },
      onAddNewMatch = { home, away, league, sport, isHot -> viewModel.addNewMatch(home, away, league, sport, isHot) },
      onResetFixtures = { viewModel.resetFixtures() },
      onDismiss = { viewModel.closeAdminPanel() }
    )
  }

  // Cloud Auth & Sync Dialog
  if (isAuthDialogOpen) {
    AuthDialog(
      authManager = viewModel.authManager,
      syncService = viewModel.firestoreSyncService,
      virtualBalance = wallet.availablePoints,
      onDismiss = { isAuthDialogOpen = false },
      onAuthSuccess = {
        isAuthDialogOpen = false
      }
    )
  }

  // Dynamic Goal Celebration Animation Overlay (Confetti + Glow)
  // CRITICAL: Only celebrate goals if permitted by user's coupon preference (default: ONLY_COUPON)
  var dismissedGoalKey by remember { mutableStateOf<String?>(null) }
  val currentGoalKey = lastGoalAlert?.let { "${it.matchId}_${it.minute}_${it.homeScore}_${it.awayScore}" }

  val activeGoalEvent = if (lastGoalAlert != null && isGoalNotificationAllowed && currentGoalKey != dismissedGoalKey) {
    lastGoalAlert!!.rawMessage
  } else null

  GoalCelebrationOverlay(
    goalMessage = activeGoalEvent,
    couponInfo = userCouponInfo,
    onDismiss = {
      dismissedGoalKey = currentGoalKey
      viewModel.clearSimulationMessage()
    }
  )
}
