package com.example

import com.example.data.datasource.MackolikLeagueDataSource
import com.example.data.model.MarketType
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.service.BettingClosureStatus
import com.example.service.CalendarManagementService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarManagementServiceTest {

  @Test
  fun testKickoffTimestampCalculation() {
    val sampleMatch = MackolikLeagueDataSource.getInitialMackolikMatches().first()
    val kickoff = CalendarManagementService.getKickoffTimestamp(sampleMatch)
    assertTrue(kickoff > 0)
  }

  @Test
  fun testFiveMinuteClosureRuleValidation() {
    val sampleMatch = MackolikLeagueDataSource.getInitialMackolikMatches().first()
    val kickoff = CalendarManagementService.getKickoffTimestamp(sampleMatch)

    // 4 minutes before kickoff (within 5-minute closure window)
    val timeInsideWindow = kickoff - (4 * 60 * 1000L)
    val isClosed4Min = CalendarManagementService.isBettingClosed(
      match = sampleMatch.copy(status = MatchStatus.SCHEDULED),
      currentTimeMs = timeInsideWindow
    )
    assertTrue("Should be closed when 4 minutes remaining to kickoff", isClosed4Min)

    // 6 minutes before kickoff (outside 5-minute closure window)
    val timeOutsideWindow = kickoff - (6 * 60 * 1000L)
    val isOpen6Min = CalendarManagementService.isBettingClosed(
      match = sampleMatch.copy(status = MatchStatus.SCHEDULED),
      currentTimeMs = timeOutsideWindow
    )
    assertFalse("Should be open when 6 minutes remaining to kickoff", isOpen6Min)
  }

  @Test
  fun testClosureStatusReturnsCorrectType() {
    val sampleMatch = MackolikLeagueDataSource.getInitialMackolikMatches().first()
    val kickoff = CalendarManagementService.getKickoffTimestamp(sampleMatch)

    val closedStatus = CalendarManagementService.getClosureStatus(
      match = sampleMatch.copy(status = MatchStatus.SCHEDULED),
      currentTimeMs = kickoff - (3 * 60 * 1000L)
    )
    assertTrue(closedStatus is BettingClosureStatus.Locked5MinWindow)

    val openStatus = CalendarManagementService.getClosureStatus(
      match = sampleMatch.copy(status = MatchStatus.SCHEDULED),
      currentTimeMs = kickoff - (20 * 60 * 1000L)
    )
    assertTrue(openStatus is BettingClosureStatus.Open)
  }

  @Test
  fun testValidateSelectionsForBetting() {
    val allMatches = MackolikLeagueDataSource.getInitialMackolikMatches()
    val scheduledMatch = allMatches.first { it.status == MatchStatus.SCHEDULED }
    val kickoff = CalendarManagementService.getKickoffTimestamp(scheduledMatch)

    val openSelection = SlipSelection(
      matchId = scheduledMatch.id,
      matchTeams = "${scheduledMatch.homeTeam} - ${scheduledMatch.awayTeam}",
      marketType = MarketType.MATCH_RESULT,
      selectionId = "sel_1",
      selectionName = "MS 1",
      odd = 1.85,
      isLive = false
    )

    // Valid when currentTime is well before kickoff
    val validResult = CalendarManagementService.validateSelectionsForBetting(
      selections = listOf(openSelection),
      allMatches = allMatches,
      currentTimeMs = kickoff - (30 * 60 * 1000L)
    )
    assertTrue(validResult.isSuccess)

    // Invalid when currentTime is inside the 5-minute window
    val invalidResult = CalendarManagementService.validateSelectionsForBetting(
      selections = listOf(openSelection),
      allMatches = allMatches,
      currentTimeMs = kickoff - (3 * 60 * 1000L)
    )
    assertTrue(invalidResult.isFailure)
  }

  @Test
  fun testFixtureClustersContainExpectedDates() {
    val clusters = CalendarManagementService.fixtureClusters
    assertTrue(clusters.isNotEmpty())

    val todayCluster = clusters.find { it.id == "cluster_today" }
    assertNotNull(todayCluster)
    assertEquals("Bugün (Canlı Akış)", todayCluster!!.title)
    assertTrue(todayCluster.dates.isNotEmpty())

    val week2Cluster = clusters.find { it.id == "cluster_week2" }
    assertNotNull(week2Cluster)
    assertEquals("2. Hafta (+1 Hafta)", week2Cluster!!.title)
    assertTrue(week2Cluster.dates.isNotEmpty())
  }
}
