package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.TicketEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.WalletEntity
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.SocialCoupon
import com.example.data.model.Sport
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import com.example.data.model.TicketType
import com.example.engine.SimulationEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

class SportsRepository(
  private val database: AppDatabase,
  private val simulationEngine: SimulationEngine,
  private val scope: CoroutineScope
) {

  val balanceManager = com.example.data.local.UserBalanceManager(database)
  val betRepository: BetRepository = BetRepositoryImpl(database, balanceManager)
  val leaderboardFlow: kotlinx.coroutines.flow.Flow<List<com.example.data.local.UserStatistics>> = balanceManager.getLeaderboardFlow()

  val matches: StateFlow<List<Match>> = simulationEngine.matches
  val lastSimulationMessage: StateFlow<String?> = simulationEngine.lastSimulationMessage
  val lastGoalAlert: StateFlow<com.example.data.model.LiveGoalAlert?> = simulationEngine.lastGoalAlert

  // Active Bet Slip state
  private val _slipSelections = MutableStateFlow<List<SlipSelection>>(emptyList())
  val slipSelections: StateFlow<List<SlipSelection>> = _slipSelections.asStateFlow()

  // Selected Sport filter
  private val _selectedSport = MutableStateFlow<Sport?>(null)
  val selectedSport: StateFlow<Sport?> = _selectedSport.asStateFlow()

  // Only Live toggle
  private val _onlyLive = MutableStateFlow(false)
  val onlyLive: StateFlow<Boolean> = _onlyLive.asStateFlow()

  // Wallet
  val walletFlow: StateFlow<WalletEntity> = database.walletDao().getWalletFlow()
    .combine(_slipSelections) { entity, _ ->
      entity ?: WalletEntity(id = 1, availablePoints = 10000L)
    }
    .stateIn(
      scope = scope,
      started = SharingStarted.Eagerly,
      initialValue = WalletEntity(id = 1, availablePoints = 10000L)
    )

  val recentTransactionsFlow = database.walletDao().getRecentTransactionsFlow()
    .stateIn(scope, SharingStarted.Lazily, emptyList())

  val ticketsFlow: StateFlow<List<Ticket>> = database.ticketDao().getAllTicketsFlow()
    .combine(matches) { entities, _ ->
      entities.map { mapEntityToTicket(it) }
    }
    .stateIn(scope, SharingStarted.Lazily, emptyList())

  // Social / Kupondaş feed
  private val _socialCoupons = MutableStateFlow<List<SocialCoupon>>(emptyList())
  val socialCoupons: StateFlow<List<SocialCoupon>> = _socialCoupons.asStateFlow()

  // Favorite match IDs
  private val _favoriteMatchIds = MutableStateFlow<Set<String>>(emptySet())
  val favoriteMatchIds: StateFlow<Set<String>> = _favoriteMatchIds.asStateFlow()

  fun toggleFavoriteMatch(matchId: String) {
    val current = _favoriteMatchIds.value
    _favoriteMatchIds.value = if (current.contains(matchId)) {
      current - matchId
    } else {
      current + matchId
    }
  }

  init {
    initWalletIfNeeded()
    initSocialCoupons()
    syncFixturesToRoom()
    startSettlementObserver()
    startMatchAutoPersistence()
  }

  private fun startMatchAutoPersistence() {
    scope.launch(Dispatchers.IO) {
      matches.collect { currentList ->
        if (currentList.isNotEmpty()) {
          val entities = currentList.map { m ->
            com.example.data.local.MatchEntity(
              id = m.id,
              sport = m.sport.name,
              league = m.league,
              homeTeam = m.homeTeam,
              awayTeam = m.awayTeam,
              homeScore = m.homeScore,
              awayScore = m.awayScore,
              minute = m.minute,
              status = m.status.name,
              startTime = m.startTime,
              matchDate = m.matchDate,
              matchDateIso = m.matchDateIso,
              week = m.week,
              isHot = m.isHot,
              referee = m.referee,
              stadium = m.stadium,
              weather = m.weather,
              tvBroadcast = m.tvBroadcast,
              halfTimeHomeScore = m.halfTimeHomeScore,
              halfTimeAwayScore = m.halfTimeAwayScore,
              extraTimeMinutes = m.extraTimeMinutes
            )
          }
          database.matchDao().insertMatches(entities)
        }
      }
    }
  }

  private fun syncFixturesToRoom() {
    scope.launch(Dispatchers.IO) {
      val count = database.matchDao().getMatchCount()
      if (count == 0) {
        val currentMatches = matches.value
        if (currentMatches.isNotEmpty()) {
          val entities = currentMatches.map { m ->
            com.example.data.local.MatchEntity(
              id = m.id,
              sport = m.sport.name,
              league = m.league,
              homeTeam = m.homeTeam,
              awayTeam = m.awayTeam,
              homeScore = m.homeScore,
              awayScore = m.awayScore,
              minute = m.minute,
              status = m.status.name,
              startTime = m.startTime,
              matchDate = m.matchDate,
              matchDateIso = m.matchDateIso,
              week = m.week,
              isHot = m.isHot,
              referee = m.referee,
              stadium = m.stadium,
              weather = m.weather,
              tvBroadcast = m.tvBroadcast,
              halfTimeHomeScore = m.halfTimeHomeScore,
              halfTimeAwayScore = m.halfTimeAwayScore,
              extraTimeMinutes = m.extraTimeMinutes
            )
          }
          database.matchDao().insertMatches(entities)
        }
      } else {
        val savedEntities = database.matchDao().getAllMatches()
        val currentMatches = matches.value.toMutableList()
        val savedMap = savedEntities.associateBy { it.id }
        for (i in currentMatches.indices) {
          val m = currentMatches[i]
          val saved = savedMap[m.id]
          if (saved != null) {
            currentMatches[i] = m.copy(
              homeScore = saved.homeScore,
              awayScore = saved.awayScore,
              minute = saved.minute,
              status = try { MatchStatus.valueOf(saved.status) } catch (_: Exception) { m.status }
            )
          }
        }
        simulationEngine.loadMatches(currentMatches)
      }
      simulationEngine.syncAllMatchesWithCurrentTime()
    }
  }

  fun persistMatchesNow() {
    scope.launch(Dispatchers.IO) {
      val currentMatches = matches.value
      if (currentMatches.isNotEmpty()) {
        val entities = currentMatches.map { m ->
          com.example.data.local.MatchEntity(
            id = m.id,
            sport = m.sport.name,
            league = m.league,
            homeTeam = m.homeTeam,
            awayTeam = m.awayTeam,
            homeScore = m.homeScore,
            awayScore = m.awayScore,
            minute = m.minute,
            status = m.status.name,
            startTime = m.startTime,
            matchDate = m.matchDate,
            matchDateIso = m.matchDateIso,
            week = m.week,
            isHot = m.isHot,
            referee = m.referee,
            stadium = m.stadium,
            weather = m.weather,
            tvBroadcast = m.tvBroadcast,
            halfTimeHomeScore = m.halfTimeHomeScore,
            halfTimeAwayScore = m.halfTimeAwayScore,
            extraTimeMinutes = m.extraTimeMinutes
          )
        }
        database.matchDao().insertMatches(entities)

        // Also persist to Room entity 'Match' (SimulatedMatchDao) as per data architecture specification
        val simulatedMatchEntities = currentMatches.map { m ->
          val ms1Odd = m.markets.firstOrNull { it.type == com.example.data.model.MarketType.MATCH_RESULT }
            ?.selections?.firstOrNull { it.name.contains("1") }?.odd ?: 2.05
          val msXOdd = m.markets.firstOrNull { it.type == com.example.data.model.MarketType.MATCH_RESULT }
            ?.selections?.firstOrNull { it.name.contains("X") }?.odd ?: 3.20
          val ms2Odd = m.markets.firstOrNull { it.type == com.example.data.model.MarketType.MATCH_RESULT }
            ?.selections?.firstOrNull { it.name.contains("2") }?.odd ?: 2.80
          val overOdd = m.markets.firstOrNull { it.type == com.example.data.model.MarketType.TOTAL_GOALS_25 }
            ?.selections?.firstOrNull { it.name.contains("Üst") }?.odd ?: 1.85
          val underOdd = m.markets.firstOrNull { it.type == com.example.data.model.MarketType.TOTAL_GOALS_25 }
            ?.selections?.firstOrNull { it.name.contains("Alt") }?.odd ?: 1.85

          com.example.data.local.Match(
            id = m.id,
            homeTeam = m.homeTeam,
            awayTeam = m.awayTeam,
            homeScore = m.homeScore,
            awayScore = m.awayScore,
            status = m.status.name,
            homeOdds = ms1Odd,
            drawOdds = msXOdd,
            awayOdds = ms2Odd,
            over25Odds = overOdd,
            under25Odds = underOdd,
            sport = m.sport.name,
            league = m.league,
            minute = m.minute,
            startTime = m.startTime,
            matchDateIso = m.matchDateIso,
            lastUpdated = System.currentTimeMillis()
          )
        }
        database.simulatedMatchDao().insertMatches(simulatedMatchEntities)
      }
    }
  }

  fun syncMatchesWithClock() {
    simulationEngine.syncAllMatchesWithCurrentTime()
    persistMatchesNow()
  }

  private fun initWalletIfNeeded() {
    scope.launch(Dispatchers.IO) {
      val existing = database.walletDao().getWallet()
      if (existing == null) {
        val initial = WalletEntity(
          id = 1,
          availablePoints = 10000L,
          lockedPoints = 0L,
          lifetimeWon = 0L,
          lifetimeLost = 0L
        )
        database.walletDao().insertOrUpdateWallet(initial)
        database.walletDao().insertTransaction(
          TransactionEntity(
            type = "WELCOME_BONUS",
            amount = 10000L,
            balanceAfter = 10000L,
            description = "Hoş Geldin Bonusu (TahminArena Başlangıç Bakiyesi) 🎁",
            createdAt = System.currentTimeMillis()
          )
        )
      }
    }
  }

  private fun initSocialCoupons() {
    _socialCoupons.value = com.example.data.datasource.VerifiedAnalystsDataSource.getVerifiedAnalystsCoupons()
  }

  fun toggleLikeCoupon(couponId: String) {
    val current = _socialCoupons.value.toMutableList()
    val index = current.indexOfFirst { it.id == couponId }
    if (index != -1) {
      val coupon = current[index]
      val newLiked = !coupon.isLikedByMe
      val newCount = if (newLiked) coupon.likeCount + 1 else (coupon.likeCount - 1).coerceAtLeast(0)
      current[index] = coupon.copy(isLikedByMe = newLiked, likeCount = newCount)
      _socialCoupons.value = current
    }
  }

  fun shareTicketToKupondas(ticket: Ticket) {
    val current = _socialCoupons.value.toMutableList()
    val newCoupon = SocialCoupon(
      id = "user_shared_${System.currentTimeMillis()}",
      authorName = "Ayça Doğan (Siz)",
      authorTitle = "Üye Tahmincisi ⭐",
      authorAvatarEmoji = "👑",
      title = "Üye Paylaşımı (${ticket.selections.size} Maçlık Kupon)",
      selections = ticket.selections,
      totalOdds = ticket.totalOdds,
      stake = ticket.stakePoints,
      likeCount = 1,
      copyCount = 0,
      isEditor = false,
      winRate = 79,
      matchStartTimeTs = System.currentTimeMillis() + 35 * 60 * 1000L,
      formattedKickoff = "Bugün 20:00 TSİ",
      isUserShared = true,
      isLikedByMe = true,
      category = "Kupondaş Üye"
    )
    current.add(0, newCoupon)
    _socialCoupons.value = current
  }

  fun setSportFilter(sport: Sport?) {
    _selectedSport.value = sport
  }

  fun setOnlyLiveFilter(onlyLive: Boolean) {
    _onlyLive.value = onlyLive
  }

  fun toggleSelection(selection: SlipSelection) {
    val current = _slipSelections.value.toMutableList()
    // If exact selection exists, remove it
    val existingIndex = current.indexOfFirst { it.selectionId == selection.selectionId }
    if (existingIndex != -1) {
      current.removeAt(existingIndex)
    } else {
      // If another selection for the same match and market exists, replace it
      current.removeAll { it.matchId == selection.matchId && it.marketType == selection.marketType }
      current.add(selection)
    }
    _slipSelections.value = current
  }

  fun removeSelection(selectionId: String) {
    _slipSelections.value = _slipSelections.value.filterNot { it.selectionId == selectionId }
  }

  fun clearSlip() {
    _slipSelections.value = emptyList()
  }

  fun copySocialCoupon(coupon: SocialCoupon) {
    _slipSelections.value = coupon.selections
    // Increment copy count
    val current = _socialCoupons.value.toMutableList()
    val index = current.indexOfFirst { it.id == coupon.id }
    if (index != -1) {
      val found = current[index]
      current[index] = found.copy(copyCount = found.copyCount + 1)
      _socialCoupons.value = current
    }
  }

  fun calculateTotalOdds(): Double {
    val list = _slipSelections.value
    if (list.isEmpty()) return 0.0
    var total = 1.0
    list.forEach { total *= it.odd }
    return (total * 100).roundToInt() / 100.0
  }

  suspend fun placeBet(stake: Long, ticketType: TicketType): Result<Ticket> {
    val selections = _slipSelections.value
    if (selections.isEmpty()) {
      return Result.failure(Exception("Kuponda en az 1 maç olmalıdır."))
    }

    // 5-minute pre-match closure validation
    val validationResult = com.example.service.CalendarManagementService.validateSelectionsForBetting(
      selections = selections,
      allMatches = matches.value
    )
    if (validationResult.isFailure) {
      return Result.failure(validationResult.exceptionOrNull() ?: Exception("Kupon kapanış süresi dolmuştur."))
    }

    val betResult = betRepository.placeBet(
      selections = selections,
      stake = stake,
      ticketType = ticketType
    )

    if (betResult.isFailure) {
      return Result.failure(betResult.exceptionOrNull() ?: Exception("Bahis onaylanamadı."))
    }

    val bet = betResult.getOrThrow()
    clearSlip()

    val createdTicket = Ticket(
      id = bet.id,
      ticketNumber = bet.betId,
      type = ticketType,
      stakePoints = bet.stakeAmount,
      totalOdds = bet.totalOdds,
      potentialPoints = bet.potentialReturn,
      status = TicketStatus.PENDING,
      createdAt = bet.placedAt,
      selections = selections
    )

    return Result.success(createdTicket)
  }

  fun calculateCashoutValue(ticket: Ticket): Long {
    if (ticket.status != TicketStatus.PENDING || ticket.selections.isEmpty()) return 0L
    val currentMatches = matches.value

    var anyLive = false
    var anyLost = false
    var allScheduled = true
    var liveScoreRatioSum = 0.0

    for (sel in ticket.selections) {
      val match = currentMatches.firstOrNull { it.id == sel.matchId }
        ?: currentMatches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && it.status != MatchStatus.FINISHED }
        ?: currentMatches.firstOrNull { it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams }

      if (match == null || match.status == MatchStatus.SCHEDULED || match.status == MatchStatus.UPCOMING) {
        // Pre-match selection: neutral 0.50 baseline weight
        liveScoreRatioSum += 0.50
        continue
      }

      allScheduled = false

      if (match.status == MatchStatus.FINISHED) {
        val won = when (sel.marketType) {
          MarketType.MATCH_RESULT, MarketType.BASKETBALL_MS, MarketType.MOTORSPORTS_WINNER -> {
            when {
              sel.selectionName.contains("1") -> match.homeScore > match.awayScore
              sel.selectionName.contains("2") -> match.awayScore > match.homeScore
              else -> match.homeScore == match.awayScore
            }
          }
          MarketType.TOTAL_GOALS_25 -> {
            val total = match.homeScore + match.awayScore
            if (sel.selectionName.contains("Üst", ignoreCase = true)) total > 2 else total < 3
          }
          MarketType.BOTH_TEAMS_SCORE -> {
            val both = match.homeScore > 0 && match.awayScore > 0
            if (sel.selectionName.contains("Var", ignoreCase = true)) both else !both
          }
          MarketType.DOUBLE_CHANCE -> {
            when {
              sel.selectionName.contains("1-X") -> match.homeScore >= match.awayScore
              sel.selectionName.contains("1-2") -> match.homeScore != match.awayScore
              else -> match.awayScore >= match.homeScore
            }
          }
          else -> true
        }

        if (!won) {
          anyLost = true
          break
        } else {
          liveScoreRatioSum += 1.0 // 100% won secured
        }
        continue
      }

      // In-play LIVE match
      anyLive = true
      val isWinningLive = when (sel.marketType) {
        MarketType.MATCH_RESULT, MarketType.BASKETBALL_MS, MarketType.MOTORSPORTS_WINNER -> {
          when {
            sel.selectionName.contains("1") -> match.homeScore > match.awayScore
            sel.selectionName.contains("2") -> match.awayScore > match.homeScore
            else -> match.homeScore == match.awayScore
          }
        }
        MarketType.TOTAL_GOALS_25 -> {
          val total = match.homeScore + match.awayScore
          if (sel.selectionName.contains("Üst", ignoreCase = true)) total >= 3 else total < 3
        }
        MarketType.BOTH_TEAMS_SCORE -> {
          val both = match.homeScore > 0 && match.awayScore > 0
          if (sel.selectionName.contains("Var", ignoreCase = true)) both else true
        }
        MarketType.DOUBLE_CHANCE -> {
          when {
            sel.selectionName.contains("1-X") -> match.homeScore >= match.awayScore
            sel.selectionName.contains("1-2") -> match.homeScore != match.awayScore
            else -> match.awayScore >= match.homeScore
          }
        }
        else -> true
      }

      val isDrawing = match.homeScore == match.awayScore
      val maxMin = when (match.sport) {
        Sport.BASKETBALL -> 40.0
        Sport.MOTORSPORTS -> 25.0
        else -> 90.0
      }
      val minuteProgress = (match.minute.coerceIn(1, maxMin.toInt()) / maxMin).coerceIn(0.05, 1.0)

      val matchFactor = if (isWinningLive) {
        // Automatically rises as live minute advances while team is in winning position
        0.55 + (minuteProgress * 0.40)
      } else if (isDrawing && (sel.selectionName.contains("1") || sel.selectionName.contains("2"))) {
        // Automatically decreases below half as time runs out in a draw
        (0.50 - (minuteProgress * 0.28)).coerceAtLeast(0.12)
      } else {
        // Opponent is leading: falls substantially below half stake
        (0.38 - (minuteProgress * 0.30)).coerceAtLeast(0.06)
      }

      liveScoreRatioSum += matchFactor
    }

    if (anyLost) return 0L

    // KURAL 1: "maç başlamadan önce bozdurmak isteyenler için kupon için yatırdığı tp nin yarısı olacak"
    if (allScheduled && !anyLive) {
      return (ticket.stakePoints / 2L).coerceAtLeast(1L)
    }

    // KURAL 2: "sonra maç başladığında kupon daki maç durumuna göre artıp azalacak otamatik"
    val averageFactor = (liveScoreRatioSum / ticket.selections.size.toDouble()).coerceIn(0.05, 1.0)

    val calculatedCashout = if (averageFactor >= 0.50) {
      // Değer kupondaki lehte skora göre yukarı tırmanır
      val progressAboveHalf = (averageFactor - 0.50) / 0.50
      val halfStake = ticket.stakePoints / 2.0
      val maxPossible = ticket.potentialPoints * 0.95
      (halfStake + (maxPossible - halfStake) * progressAboveHalf).toLong()
    } else {
      // Değer kupondaki aleyhte skora göre yarı yatırılanın altına düşer
      val progressBelowHalf = (0.50 - averageFactor) / 0.50
      val halfStake = ticket.stakePoints / 2.0
      val minPossible = (ticket.stakePoints * 0.10).coerceAtLeast(1.0)
      (halfStake - (halfStake - minPossible) * progressBelowHalf).toLong()
    }

    return calculatedCashout.coerceIn(
      (ticket.stakePoints * 0.05).toLong().coerceAtLeast(1L),
      (ticket.potentialPoints * 0.98).toLong()
    )
  }

  suspend fun cashoutTicket(ticket: Ticket): Result<Long> {
    if (ticket.status != TicketStatus.PENDING) {
      return Result.failure(Exception("Bu kupon zaten sonuçlanmıştır."))
    }
    val cashoutAmount = calculateCashoutValue(ticket)
    if (cashoutAmount <= 0) {
      return Result.failure(Exception("Bu kupon için şu an bahis bozdurma teklifi bulunmuyor."))
    }

    // Update ticket in database
    database.ticketDao().updateTicketStatus(ticket.id, "WON")

    // Update betEntry
    val betEntry = database.betEntryDao().getBetEntryById(ticket.ticketNumber)
    if (betEntry != null) {
      database.betEntryDao().updateBetEntry(betEntry.copy(status = "WON", resolvedAt = System.currentTimeMillis()))
    }

    // Credit cashout TP to wallet
    balanceManager.addWinnings(
      amount = cashoutAmount,
      description = "Erken Bahis Bozdur (Kupon #${ticket.ticketNumber}) 💰"
    )
    balanceManager.recordBetResultInStats(
      userId = "default_user",
      isWon = true,
      tpWon = cashoutAmount
    )

    return Result.success(cashoutAmount)
  }

  suspend fun claimDailyBonus(): Result<Long> {
    return balanceManager.claimDailyReward(250L)
  }

  suspend fun depositVirtualCurrency(amount: Long, method: String): Result<Long> {
    if (amount <= 0) return Result.failure(Exception("Geçerli bir yükleme tutarı giriniz."))
    balanceManager.addVirtualBalance(amount, "Sanal TP Yükleme ($method) 💳 (+${amount} TP)")
    return Result.success(amount)
  }

  private fun startSettlementObserver() {
    scope.launch(Dispatchers.IO) {
      matches.collect { currentMatches ->
        betRepository.evaluatePendingBets(currentMatches)
      }
    }
  }

  private fun mapEntityToTicket(entity: TicketEntity): Ticket {
    val selections = mutableListOf<SlipSelection>()
    try {
      val array = JSONArray(entity.selectionsJson)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        selections.add(
          SlipSelection(
            matchId = obj.getString("matchId"),
            matchTeams = obj.getString("matchTeams"),
            marketType = MarketType.valueOf(obj.getString("marketType")),
            selectionId = obj.getString("selectionId"),
            selectionName = obj.getString("selectionName"),
            odd = obj.getDouble("odd"),
            isLive = obj.optBoolean("isLive", false)
          )
        )
      }
    } catch (_: Exception) {}

    return Ticket(
      id = entity.id,
      ticketNumber = entity.ticketNumber,
      type = try { TicketType.valueOf(entity.type) } catch (_: Exception) { TicketType.KOMBINE },
      stakePoints = entity.stakePoints,
      totalOdds = entity.totalOdds,
      potentialPoints = entity.potentialPoints,
      status = try { TicketStatus.valueOf(entity.status) } catch (_: Exception) { TicketStatus.PENDING },
      createdAt = entity.createdAt,
      selections = selections
    )
  }

  fun updateMatchLiveMinute(
    matchId: String,
    minute: Int,
    extraMinute: Int = 0,
    isFinished: Boolean = false,
    homeScore: Int? = null,
    awayScore: Int? = null
  ) {
    simulationEngine.updateMatchLiveMinute(matchId, minute, extraMinute, isFinished, homeScore, awayScore)
  }

  fun triggerSimulationGoal(matchId: String, isHome: Boolean) {
    simulationEngine.triggerGoalForMatch(matchId, isHome)
  }

  fun clearSimulationMessage() {
    simulationEngine.clearSimulationMessage()
  }
}
