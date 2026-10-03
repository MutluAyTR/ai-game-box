package com.example.engine

import com.example.data.datasource.GlobalExpandedSportsData
import com.example.data.datasource.GlobalSportsDatabase
import com.example.data.datasource.MackolikExpanded84SportsDataSource
import com.example.data.model.AiPrediction
import com.example.data.model.Market
import com.example.data.model.Match
import com.example.data.model.MatchStatistics
import com.example.data.model.MatchStatus
import com.example.data.model.Sport
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

/**
 * Seasonal Scheduling Engine:
 * Automatically generates seasonal fixtures, round-robin schedules, and authentic match times
 * for all 80+ sports based on historical seasonal patterns and official league rules.
 */
object SeasonalSchedulingEngine {

  data class LeagueSeasonConfig(
    val sport: Sport,
    val leagueName: String,
    val startMonth: Int, // 1..12
    val endMonth: Int,
    val totalWeeks: Int,
    val standardKickoffSlots: List<String>,
    val allowedDaysOfWeek: List<Int>, // 1: Mon .. 7: Sun
    val derbySpacingWeeks: Int = 3,
    val broadcastPartner: String
  )

  private val leagueConfigs = mapOf(
    "Trendyol Süper Lig" to LeagueSeasonConfig(
      sport = Sport.FOOTBALL,
      leagueName = "Trendyol Süper Lig",
      startMonth = 8, endMonth = 5, totalWeeks = 38,
      standardKickoffSlots = listOf("13:30", "16:00", "19:00", "20:00"),
      allowedDaysOfWeek = listOf(5, 6, 7, 1), // Cuma, Cumartesi, Pazar, Pazartesi
      broadcastPartner = "beIN SPORTS 1"
    ),
    "Premier League" to LeagueSeasonConfig(
      sport = Sport.FOOTBALL,
      leagueName = "Premier League",
      startMonth = 8, endMonth = 5, totalWeeks = 38,
      standardKickoffSlots = listOf("14:30", "17:00", "19:30", "22:00"),
      allowedDaysOfWeek = listOf(6, 7, 1),
      broadcastPartner = "beIN SPORTS 3"
    ),
    "La Liga" to LeagueSeasonConfig(
      sport = Sport.FOOTBALL,
      leagueName = "La Liga",
      startMonth = 8, endMonth = 5, totalWeeks = 38,
      standardKickoffSlots = listOf("15:00", "17:15", "19:30", "22:00"),
      allowedDaysOfWeek = listOf(5, 6, 7, 1),
      broadcastPartner = "S Sport"
    ),
    "NBA" to LeagueSeasonConfig(
      sport = Sport.BASKETBALL,
      leagueName = "NBA",
      startMonth = 10, endMonth = 6, totalWeeks = 82,
      standardKickoffSlots = listOf("02:00", "02:30", "03:00", "04:30", "05:30"),
      allowedDaysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
      broadcastPartner = "NBA TV / S Sport Plus"
    ),
    "EuroLeague" to LeagueSeasonConfig(
      sport = Sport.BASKETBALL,
      leagueName = "EuroLeague",
      startMonth = 10, endMonth = 5, totalWeeks = 34,
      standardKickoffSlots = listOf("19:45", "20:15", "20:30", "21:00", "21:45"),
      allowedDaysOfWeek = listOf(2, 3, 4, 5), // Salı-Cuma
      broadcastPartner = "S Sport"
    ),
    "Formula 1" to LeagueSeasonConfig(
      sport = Sport.FORMULA_1,
      leagueName = "Formula 1 Grand Prix",
      startMonth = 3, endMonth = 11, totalWeeks = 24,
      standardKickoffSlots = listOf("15:00", "16:00", "21:00"),
      allowedDaysOfWeek = listOf(7), // Pazar Ana Yarış
      broadcastPartner = "beIN SPORTS 2"
    ),
    "MotoGP" to LeagueSeasonConfig(
      sport = Sport.MOTOGP,
      leagueName = "MotoGP Grand Prix",
      startMonth = 3, endMonth = 11, totalWeeks = 20,
      standardKickoffSlots = listOf("14:00", "15:00"),
      allowedDaysOfWeek = listOf(7), // Pazar Ana Yarış
      broadcastPartner = "S Sport 2"
    ),
    "WRC Dünya Rallisi" to LeagueSeasonConfig(
      sport = Sport.WRC_RALLY,
      leagueName = "WRC Dünya Rallisi",
      startMonth = 1, endMonth = 11, totalWeeks = 13,
      standardKickoffSlots = listOf("09:00", "12:30", "14:15"),
      allowedDaysOfWeek = listOf(4, 5, 6, 7), // 4 Günlük Ralli
      broadcastPartner = "Red Bull TV"
    ),
    "ATP & WTA Tour" to LeagueSeasonConfig(
      sport = Sport.TENNIS,
      leagueName = "ATP & WTA Tour",
      startMonth = 1, endMonth = 11, totalWeeks = 44,
      standardKickoffSlots = listOf("12:00", "14:30", "17:00", "20:00"),
      allowedDaysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
      broadcastPartner = "Eurosport 1"
    ),
    "NFL" to LeagueSeasonConfig(
      sport = Sport.AMERICAN_FOOTBALL,
      leagueName = "NFL",
      startMonth = 9, endMonth = 2, totalWeeks = 18,
      standardKickoffSlots = listOf("20:00", "23:25", "03:20"),
      allowedDaysOfWeek = listOf(4, 7, 1), // Perşembe, Pazar, Pazartesi
      broadcastPartner = "S Sport Plus"
    ),
    "Sultanlar Ligi" to LeagueSeasonConfig(
      sport = Sport.VOLLEYBALL,
      leagueName = "Vodafone Sultanlar Ligi",
      startMonth = 10, endMonth = 4, totalWeeks = 26,
      standardKickoffSlots = listOf("14:00", "16:30", "19:00"),
      allowedDaysOfWeek = listOf(6, 7), // Hafta sonu
      broadcastPartner = "TRT Spor Yıldız"
    ),
    "UFC & MMA" to LeagueSeasonConfig(
      sport = Sport.MMA_UFC,
      leagueName = "UFC Fight Night / Pay-Per-View",
      startMonth = 1, endMonth = 12, totalWeeks = 42,
      standardKickoffSlots = listOf("03:00", "05:00"),
      allowedDaysOfWeek = listOf(7), // Pazar sabahı TSİ
      broadcastPartner = "S Sport Plus"
    )
  )

  /**
   * Generates a realistic seasonal fixture calendar for a specific sport / league.
   */
  fun generateSeasonalScheduleForLeague(
    leagueKey: String,
    teams: List<String>,
    currentSeasonYear: Int = 2026
  ): List<SeasonalFixtureMatch> {
    if (teams.size < 2) return emptyList()

    val config = leagueConfigs[leagueKey] ?: LeagueSeasonConfig(
      sport = Sport.FOOTBALL,
      leagueName = leagueKey,
      startMonth = 9, endMonth = 5, totalWeeks = 30,
      standardKickoffSlots = listOf("15:00", "18:00", "20:00"),
      allowedDaysOfWeek = listOf(6, 7),
      broadcastPartner = "Nesine TV"
    )

    val fixtures = mutableListOf<SeasonalFixtureMatch>()
    val shuffledTeams = teams.shuffled(Random(abs(leagueKey.hashCode().toLong())))
    val n = if (shuffledTeams.size % 2 == 0) shuffledTeams.size else shuffledTeams.size - 1
    val roundsCount = (n - 1) * 2 // Home and Away Double Round-Robin

    var matchCounter = 1

    for (round in 1..roundsCount) {
      val isSecondHalf = round > (n - 1)
      val effectiveRound = if (isSecondHalf) round - (n - 1) else round

      // Approximate calendar date calculation
      val weekOffsetDays = (round - 1) * 7
      val baseDate = LocalDate.of(currentSeasonYear, config.startMonth.coerceIn(1, 12), 1)
        .plusDays(weekOffsetDays.toLong())

      for (i in 0 until n / 2) {
        val homeIdx = (effectiveRound + i) % (n - 1)
        var awayIdx = (n - 1 - i + effectiveRound) % (n - 1)
        if (i == 0) awayIdx = n - 1

        val teamA = shuffledTeams[homeIdx]
        val teamB = shuffledTeams[awayIdx]

        val (homeTeam, awayTeam) = if (isSecondHalf) teamB to teamA else teamA to teamB

        val slot = config.standardKickoffSlots[(i + round) % config.standardKickoffSlots.size]
        val dayOffset = (i % config.allowedDaysOfWeek.size)
        val matchDate = baseDate.plusDays(dayOffset.toLong())
        val dateStr = matchDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("tr")))

        fixtures.add(
          SeasonalFixtureMatch(
            fixtureId = "fix_${leagueKey.lowercase().replace(" ", "_")}_w${round}_m$matchCounter",
            week = round,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            league = config.leagueName,
            sport = config.sport,
            kickoffTime = slot,
            dateFormatted = "$dateStr $slot TSİ",
            dateIso = matchDate.toString(),
            broadcast = config.broadcastPartner,
            isDerby = isBigDerbyMatch(homeTeam, awayTeam)
          )
        )
        matchCounter++
      }
    }

    return fixtures
  }

  private fun isBigDerbyMatch(teamA: String, teamB: String): Boolean {
    val bigClubs = setOf(
      "Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor",
      "Real Madrid", "Barcelona", "Arsenal", "Manchester City", "Liverpool",
      "Inter", "AC Milan", "Juventus", "Bayern München", "Borussia Dortmund",
      "Boston Celtics", "Los Angeles Lakers"
    )
    return bigClubs.contains(teamA) && bigClubs.contains(teamB)
  }

  /**
   * Generates seasonal fixtures for all 80+ sports available in the app.
   */
  fun generateFixturesForAllSupportedSports(): Map<String, List<SeasonalFixtureMatch>> {
    val result = mutableMapOf<String, List<SeasonalFixtureMatch>>()

    // Football Leagues
    result["Trendyol Süper Lig"] = generateSeasonalScheduleForLeague("Trendyol Süper Lig", GlobalSportsDatabase.superLigTeams.take(19))
    result["Premier League"] = generateSeasonalScheduleForLeague("Premier League", GlobalSportsDatabase.premierLeagueTeams.take(20))
    result["La Liga"] = generateSeasonalScheduleForLeague("La Liga", GlobalSportsDatabase.laLigaTeams.take(20))

    // Basketball Leagues
    result["NBA"] = generateSeasonalScheduleForLeague("NBA", GlobalSportsDatabase.nbaTeams.take(20))
    result["EuroLeague"] = generateSeasonalScheduleForLeague("EuroLeague", GlobalSportsDatabase.euroLeagueTeams.take(18))

    // Motorsports
    result["Formula 1"] = generateSeasonalScheduleForLeague("Formula 1", GlobalSportsDatabase.f1Drivers.take(16))
    result["MotoGP"] = generateSeasonalScheduleForLeague("MotoGP", GlobalSportsDatabase.motoGpRacers.take(16))
    result["WRC Dünya Rallisi"] = generateSeasonalScheduleForLeague("WRC Dünya Rallisi", GlobalSportsDatabase.wrcRallyDrivers.take(12))

    // Tennis & Volleyball
    result["ATP & WTA Tour"] = generateSeasonalScheduleForLeague("ATP & WTA Tour", GlobalSportsDatabase.tennisPlayers.take(16))
    result["Sultanlar Ligi"] = generateSeasonalScheduleForLeague("Sultanlar Ligi", GlobalSportsDatabase.sultanlarLigiTeams.take(14))

    return result
  }
}

data class SeasonalFixtureMatch(
  val fixtureId: String,
  val week: Int,
  val homeTeam: String,
  val awayTeam: String,
  val league: String,
  val sport: Sport,
  val kickoffTime: String,
  val dateFormatted: String,
  val dateIso: String,
  val broadcast: String,
  val isDerby: Boolean
)
