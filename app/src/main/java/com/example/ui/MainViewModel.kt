package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DailyMissionEntity
import com.example.data.local.UserStatistics
import com.example.data.model.GoalNotificationScope
import com.example.data.model.LiveGoalAlert
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.SocialCoupon
import com.example.data.model.Sport
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import com.example.data.model.TicketType
import com.example.data.model.UserCouponMatchInfo
import com.example.data.repository.SportsRepository
import com.example.engine.SimulationEngine
import com.example.service.BettingClosureStatus
import com.example.service.CalendarManagementService
import com.example.util.CouponDeepLinkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val simulationEngine = SimulationEngine(viewModelScope)
  val repository = SportsRepository(database, simulationEngine, viewModelScope)
  val authManager = com.example.service.FirebaseAuthManager(application)
  val firestoreSyncService = com.example.service.FirestoreSyncService(database)

  val selectedSport: StateFlow<Sport?> = repository.selectedSport
  val onlyLive: StateFlow<Boolean> = repository.onlyLive
  val slipSelections: StateFlow<List<SlipSelection>> = repository.slipSelections
  val wallet = repository.walletFlow
  val recentTransactions = repository.recentTransactionsFlow
  val tickets: StateFlow<List<Ticket>> = repository.ticketsFlow
  val socialCoupons: StateFlow<List<SocialCoupon>> = repository.socialCoupons
  val lastSimulationMessage: StateFlow<String?> = repository.lastSimulationMessage
  val lastGoalAlert: StateFlow<LiveGoalAlert?> = repository.lastGoalAlert

  private val _goalNotificationScope = MutableStateFlow(GoalNotificationScope.ONLY_COUPON)
  val goalNotificationScope: StateFlow<GoalNotificationScope> = _goalNotificationScope.asStateFlow()

  fun setGoalNotificationScope(scope: GoalNotificationScope) {
    _goalNotificationScope.value = scope
  }

  // Filter for only user's coupon matches
  private val _onlyCouponMatches = MutableStateFlow(false)
  val onlyCouponMatches: StateFlow<Boolean> = _onlyCouponMatches.asStateFlow()

  fun toggleCouponMatchesFilter() {
    _onlyCouponMatches.value = !_onlyCouponMatches.value
  }

  fun setOnlyCouponMatches(value: Boolean) {
    _onlyCouponMatches.value = value
  }

  fun cashoutTicket(ticket: Ticket) {
    viewModelScope.launch {
      val result = repository.cashoutTicket(ticket)
      if (result.isSuccess) {
        val amount = result.getOrNull() ?: 0L
        _userMessage.value = "💰 Kupon #${ticket.ticketNumber} Erken Bozduruldu! +$amount TP hesabınıza yüklendi! 🎉"
      } else {
        _userMessage.value = result.exceptionOrNull()?.message ?: "Bahis bozdurulamadı."
      }
    }
  }

  fun calculateCashoutValue(ticket: Ticket): Long {
    return repository.calculateCashoutValue(ticket)
  }

  fun isMatchInUserCoupon(matchId: String, home: String, away: String): Boolean {
    val pendingTickets = tickets.value.filter { it.status == TicketStatus.PENDING }
    return pendingTickets.any { ticket ->
      ticket.selections.any { sel ->
        sel.matchId == matchId ||
        (home.isNotBlank() && sel.matchTeams.contains(home)) ||
        (away.isNotBlank() && sel.matchTeams.contains(away))
      }
    }
  }

  fun getUserCouponMatchInfo(matchId: String, home: String, away: String): UserCouponMatchInfo? {
    val pendingTickets = tickets.value.filter { it.status == TicketStatus.PENDING }
    for (ticket in pendingTickets) {
      val sel = ticket.selections.firstOrNull {
        it.matchId == matchId ||
        (home.isNotBlank() && it.matchTeams.contains(home)) ||
        (away.isNotBlank() && it.matchTeams.contains(away))
      }
      if (sel != null) {
        return UserCouponMatchInfo(
          ticketNumber = ticket.ticketNumber,
          selectionName = sel.selectionName,
          marketName = sel.marketType.displayName,
          odd = sel.odd,
          isWinningNow = true,
          totalPotentialPoints = ticket.potentialPoints
        )
      }
    }
    return null
  }
  val leaderboard: StateFlow<List<UserStatistics>> = repository.leaderboardFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val dailyMissions: StateFlow<List<DailyMissionEntity>> = repository.balanceManager.getDailyMissionsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    simulationEngine.setApplicationContext(application)
    com.example.service.LocalNotificationManager.createNotificationChannels(application)
    viewModelScope.launch {
      repository.balanceManager.ensureLeaderboardSeeded()
      repository.balanceManager.ensureDailyMissionsSeeded()
    }
  }

  // Admin Panel visibility
  private val _isAdminPanelOpen = MutableStateFlow(false)
  val isAdminPanelOpen: StateFlow<Boolean> = _isAdminPanelOpen.asStateFlow()

  fun syncMatchesWithClock() {
    repository.syncMatchesWithClock()
  }

  fun persistMatchesNow() {
    repository.persistMatchesNow()
  }

  fun openAdminPanel() {
    _isAdminPanelOpen.value = true
  }

  fun closeAdminPanel() {
    _isAdminPanelOpen.value = false
  }

  fun updateMatchScore(matchId: String, homeScore: Int, awayScore: Int) {
    simulationEngine.updateMatchScore(matchId, homeScore, awayScore)
  }

  fun updateMatchMinute(matchId: String, minute: Int) {
    simulationEngine.updateMatchMinute(matchId, minute)
  }

  fun endMatch(matchId: String) {
    simulationEngine.endMatch(matchId)
  }

  fun startNextLiveMatch() {
    simulationEngine.startNextLiveMatch()
  }

  fun addNewMatch(home: String, away: String, league: String, sport: Sport, isHot: Boolean) {
    simulationEngine.addNewMatch(home, away, league, sport, isHot)
  }

  fun resetFixtures() {
    simulationEngine.loadInitialFixtures()
    _userMessage.value = "Tüm fikstür ve oranlar sıfırlandı."
  }

  // Week selection: 0 = Canlı / Tüm Bülten, 1 = 1. Hafta, 2 = 2. Hafta, 3 = 3. Hafta ... 38 = 38. Hafta
  private val _selectedWeek = MutableStateFlow(0)
  val selectedWeek: StateFlow<Int> = _selectedWeek.asStateFlow()

  // Date selection ISO: default to null (Tüm Aktif Maçlar) or CalendarManagementService.getCurrentDateIso()
  private val _selectedDateIso = MutableStateFlow<String?>(null)
  val selectedDateIso: StateFlow<String?> = _selectedDateIso.asStateFlow()

  fun setSelectedWeek(week: Int) {
    _selectedWeek.value = week
    _selectedDateIso.value = null
    if (week > 0) {
      repository.setOnlyLiveFilter(false)
      simulationEngine.loadWeekFixtures(week)
      _userMessage.value = "$week. Hafta Lig Maçları ve Fikstür Yüklendi."
    } else {
      simulationEngine.loadInitialFixtures()
    }
  }

  fun setSelectedDateIso(dateIso: String?) {
    _selectedDateIso.value = dateIso
    if (dateIso != null) {
      _selectedWeek.value = 0
      repository.setOnlyLiveFilter(false)
    }
  }

  fun addDrawnMatchesToBulletin(newMatches: List<Match>) {
    simulationEngine.addMatches(newMatches)
    _userMessage.value = "🎲 ${newMatches.size} Kura Eşleşmesi Canlı Bültene Eklendi! Bahis yapabilirsiniz."
  }

  // Real-time counts
  val allMatches: StateFlow<List<Match>> = repository.matches

  val liveMatchesCount: StateFlow<Int> = repository.matches.combine(repository.selectedSport) { matches, sport ->
    matches.count { it.status == MatchStatus.LIVE && (sport == null || it.sport == sport) }
  }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

  // Favorite matches
  val favoriteMatchIds: StateFlow<Set<String>> = repository.favoriteMatchIds
  private val _onlyFavorites = MutableStateFlow(false)
  val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

  fun toggleFavoriteMatch(matchId: String) {
    repository.toggleFavoriteMatch(matchId)
  }

  fun setOnlyFavoritesFilter(onlyFavs: Boolean) {
    _onlyFavorites.value = onlyFavs
  }

  // Filtered matches based on sport, live filter, week, date, and favorites
  val filteredMatches: StateFlow<List<Match>> = combine(
    repository.matches,
    repository.selectedSport,
    repository.onlyLive,
    _selectedWeek,
    _selectedDateIso,
    _onlyFavorites,
    repository.favoriteMatchIds
  ) { array ->
    @Suppress("UNCHECKED_CAST")
    val matches = array[0] as List<Match>
    val sport = array[1] as Sport?
    val liveOnly = array[2] as Boolean
    val week = array[3] as Int
    val dateIso = array[4] as String?
    val favsOnly = array[5] as Boolean
    @Suppress("UNCHECKED_CAST")
    val favIds = array[6] as Set<String>

    matches.filter { match ->
      val matchesSport = sport == null || match.sport == sport
      val matchesLive = !liveOnly || match.status == MatchStatus.LIVE
      val matchesWeek = week == 0 || match.week == week
      val matchesDate = dateIso == null || match.matchDateIso == dateIso
      val matchesFavs = !favsOnly || favIds.contains(match.id)
      matchesSport && matchesLive && matchesWeek && matchesDate && matchesFavs
    }
  }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  // Selected match for Detailed AI Analysis dialog
  private val _selectedMatchForDetail = MutableStateFlow<Match?>(null)
  val selectedMatchForDetail: StateFlow<Match?> = _selectedMatchForDetail.asStateFlow()

  // Bet slip sheet expansion
  private val _isSlipOpen = MutableStateFlow(false)
  val isSlipOpen: StateFlow<Boolean> = _isSlipOpen.asStateFlow()

  // Toast / Feedback message
  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

  // Selected stake in TP
  private val _selectedStake = MutableStateFlow(100L)
  val selectedStake: StateFlow<Long> = _selectedStake.asStateFlow()

  private val _selectedTicketType = MutableStateFlow(TicketType.KOMBINE)
  val selectedTicketType: StateFlow<TicketType> = _selectedTicketType.asStateFlow()

  fun setSportFilter(sport: Sport?) {
    repository.setSportFilter(sport)
  }

  fun setOnlyLiveFilter(onlyLive: Boolean) {
    repository.setOnlyLiveFilter(onlyLive)
  }

  fun toggleOddsSelection(selection: SlipSelection) {
    val match = repository.matches.value.firstOrNull { it.id == selection.matchId }
    val isAlreadySelected = repository.slipSelections.value.any { it.selectionId == selection.selectionId }
    if (match != null && !isAlreadySelected) {
      val closureStatus = CalendarManagementService.getClosureStatus(match)
      if (closureStatus is BettingClosureStatus.Locked5MinWindow) {
        _userMessage.value = "⚠️ '${match.homeTeam} - ${match.awayTeam}' karşılaşması başlamadan 5 dakika önce bahis alımına kapanmıştır! (5 Dk Kuralı)"
        return
      } else if (closureStatus is BettingClosureStatus.Finished) {
        _userMessage.value = "⚠️ Bu karşılaşma tamamlandığı için kupona eklenemez."
        return
      }
    }
    repository.toggleSelection(selection)
  }

  fun removeSlipItem(selectionId: String) {
    repository.removeSelection(selectionId)
  }

  fun clearSlip() {
    repository.clearSlip()
  }

  fun openSlip() {
    _isSlipOpen.value = true
  }

  fun closeSlip() {
    _isSlipOpen.value = false
  }

  fun setSelectedStake(stake: Long) {
    _selectedStake.value = stake
  }

  fun setSelectedTicketType(type: TicketType) {
    _selectedTicketType.value = type
  }

  fun openMatchDetail(match: Match) {
    _selectedMatchForDetail.value = match
    viewModelScope.launch {
      repository.balanceManager.incrementMissionProgress("mission_explore_stats", 1)
    }
  }

  fun closeMatchDetail() {
    _selectedMatchForDetail.value = null
  }

  fun copyCoupon(coupon: SocialCoupon) {
    repository.copySocialCoupon(coupon)
    _userMessage.value = "'${coupon.title}' kuponu bülteninize eklendi!"
    _isSlipOpen.value = true
  }

  fun toggleLikeCoupon(couponId: String) {
    repository.toggleLikeCoupon(couponId)
  }

  fun shareTicketToKupondas(ticket: Ticket) {
    repository.shareTicketToKupondas(ticket)
    _userMessage.value = "📢 '${ticket.ticketNumber}' kuponunuz Kupondaş topluluğunda paylaşıldı!"
  }

  fun importDeepLink(deepLink: String): Boolean {
    val parsed = CouponDeepLinkManager.parseDeepLink(deepLink)
    return if (parsed != null && parsed.selections.isNotEmpty()) {
      parsed.selections.forEach { sel ->
        repository.toggleSelection(sel)
      }
      _isSlipOpen.value = true
      _userMessage.value = "🔗 '${parsed.title}' (${parsed.selections.size} Maç, Oran: %.2f) sepete eklendi!".format(parsed.totalOdds)
      true
    } else {
      _userMessage.value = "⚠️ Geçersiz kupon bağlantısı."
      false
    }
  }

  fun placeBet() {
    viewModelScope.launch {
      val stake = _selectedStake.value
      val type = _selectedTicketType.value
      val currentSelections = repository.slipSelections.value
      val result = repository.placeBet(stake, type)
      if (result.isSuccess) {
        val ticket = result.getOrNull()
        _userMessage.value = "🎉 Kupon Onaylandı! (${ticket?.ticketNumber} - ${ticket?.potentialPoints} TP Potansiyel Kazanç)"
        _isSlipOpen.value = false

        // Update Daily Missions in Room
        try {
          repository.balanceManager.incrementMissionProgress("mission_3_bets", 1)
          val distinctSportsCount = currentSelections.map { it.marketType }.distinct().size
          if (distinctSportsCount >= 2 || currentSelections.size >= 2) {
            repository.balanceManager.incrementMissionProgress("mission_2_sports", 1)
          }
          if (ticket != null && ticket.totalOdds >= 3.0) {
            repository.balanceManager.incrementMissionProgress("mission_high_odds", 1)
          }
        } catch (_: Exception) {}

        // Sync to Firestore for multi-device cloud persistence
        try {
          firestoreSyncService.syncWalletToFirestore()
          if (ticket != null) {
            firestoreSyncService.syncTicketToFirestore(ticket)
          }
        } catch (_: Exception) {
          // Graceful fallback for offline
        }
      } else {
        _userMessage.value = result.exceptionOrNull()?.message ?: "Hata oluştu"
      }
    }
  }

  fun claimDailyMission(missionId: String) {
    viewModelScope.launch {
      val earned = repository.balanceManager.claimMissionReward(missionId)
      if (earned > 0L) {
        _userMessage.value = "🎁 Görev ödülü +$earned TP kasanıza eklendi!"
      }
    }
  }

  fun onWatchRewardedAd(rewardAmount: Long = 500L) {
    viewModelScope.launch {
      repository.balanceManager.addVirtualBalance(rewardAmount, "Ödüllü Reklam İzleme (+${rewardAmount} TP)")
      repository.balanceManager.incrementMissionProgress("mission_watch_ad", 1)
      _userMessage.value = "📺 Sponsorlu reklam tamamlandı! +$rewardAmount TP kasanıza eklendi!"
    }
  }

  fun resetBalance() {
    viewModelScope.launch {
      repository.balanceManager.resetBalance()
      _userMessage.value = "🔄 Bakiye 10.000 TP başlangıç seviyesine sıfırlandı!"
    }
  }

  fun rechargeBalance(amount: Long) {
    depositPoints(amount, "Hızlı Bakiye Yükleme")
  }

  fun deductVirtualStake(amount: Long, description: String): Boolean {
    val currentBalance = wallet.value.availablePoints
    if (currentBalance < amount) {
      _userMessage.value = "⚠️ Yetersiz bakiye! Gerekli: $amount TP, Mevcut: $currentBalance TP"
      return false
    }
    viewModelScope.launch {
      repository.balanceManager.deductStake(amount, description)
    }
    return true
  }

  fun addVirtualWinnings(amount: Long, description: String) {
    viewModelScope.launch {
      repository.balanceManager.addVirtualBalance(amount, description)
      repository.balanceManager.incrementMissionProgress("mission_win_1", 1)
      _userMessage.value = "🎉 Tebrikler! +$amount TP kasanıza eklendi ($description)"
    }
  }

  fun claimDailyBonus() {
    viewModelScope.launch {
      val result = repository.claimDailyBonus()
      if (result.isSuccess) {
        _userMessage.value = "🎁 Tebrikler! +250 TP Günlük Giriş Bonusu Cüzdanınıza Eklendi!"
      } else {
        _userMessage.value = result.exceptionOrNull()?.message ?: "Hata oluştu"
      }
    }
  }

  fun playSporToto(cost: Long, selectionsCount: Int) {
    viewModelScope.launch {
      val wallet = repository.walletFlow.value
      if (wallet.availablePoints < cost) {
        _userMessage.value = "⚠️ Yetersiz bakiye! Cüzdanınızda ${wallet.availablePoints} TP var, gereken: $cost TP."
        return@launch
      }
      val deductResult = repository.depositVirtualCurrency(-cost, "Spor Toto 15 Maç Kupon Bedeli")
      if (deductResult.isSuccess) {
        _userMessage.value = "🎉 Spor Toto 15 Maç Kuponunuz Onaylandı! ($cost TP - 38. Hafta İkramiye Havuzuna Dahil Edildi)"
      } else {
        _userMessage.value = deductResult.exceptionOrNull()?.message ?: "Hata oluştu"
      }
    }
  }

  fun depositPoints(amount: Long, method: String = "Test Yükleme") {
    viewModelScope.launch {
      val result = repository.depositVirtualCurrency(amount, method)
      if (result.isSuccess) {
        _userMessage.value = "💳 +$amount TP Sanal Bakiye Cüzdanınıza Eklendi!"
      } else {
        _userMessage.value = result.exceptionOrNull()?.message ?: "Hata oluştu"
      }
    }
  }

  fun updateMatchLiveMinute(
    matchId: String,
    minute: Int,
    extraMinute: Int = 0,
    isFinished: Boolean = false,
    homeScore: Int? = null,
    awayScore: Int? = null
  ) {
    repository.updateMatchLiveMinute(matchId, minute, extraMinute, isFinished, homeScore, awayScore)
  }

  fun triggerManualGoal(matchId: String, isHome: Boolean) {
    repository.triggerSimulationGoal(matchId, isHome)
  }

  fun dismissUserMessage() {
    _userMessage.value = null
  }

  fun clearSimulationMessage() {
    repository.clearSimulationMessage()
  }
}
