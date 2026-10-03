package com.example

import com.example.data.model.MarketType
import com.example.engine.AiOddsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testInitialMarketsGeneration() {
    val (markets, prediction) = AiOddsEngine.generateInitialMarkets(
      matchId = "test_match_1",
      homeElo = 1750.0,
      awayElo = 1500.0,
      homeXgExp = 1.9,
      awayXgExp = 1.1
    )

    assertTrue(markets.isNotEmpty())
    val msMarket = markets.find { it.type == MarketType.MATCH_RESULT }
    assertNotNull(msMarket)
    assertEquals(3, msMarket?.selections?.size)

    val sel1 = msMarket?.selections?.get(0)?.odd ?: 0.0
    val sel2 = msMarket?.selections?.get(2)?.odd ?: 0.0

    assertTrue(sel1 >= 1.01)
    assertTrue(sel2 >= 1.01)
    // Home team has higher Elo & xG, so home win odd should be lower than away win odd
    assertTrue(sel1 < sel2)

    assertTrue(prediction.homeWinProb > prediction.awayWinProb)
    assertTrue(prediction.confidence in 1..99)
  }

  @Test
  fun testMackolikFixtureGeneratorMatchesAndUniqueness() {
    val matches = com.example.data.datasource.MackolikFixtureGenerator.generateAllMatches()
    val sept26Matches = matches.filter { it.matchDateIso == "2026-09-26" }
    // Must have at least 2000 matches for 26.09.2026
    assertTrue("Sept 26 matches count should be >= 2000, was: ${sept26Matches.size}", sept26Matches.size >= 2000)

    // No team plays twice on 26.09.2026
    val allTeams = sept26Matches.flatMap { listOf(it.homeTeam, it.awayTeam) }
    val uniqueTeams = allTeams.toSet()
    assertEquals("Every team must play at most once on 26.09.2026", allTeams.size, uniqueTeams.size)

    // Check future weeks
    val week2Matches = matches.filter { it.matchDateIso == "2026-10-03" }
    assertTrue(week2Matches.isNotEmpty())

    val week3Matches = matches.filter { it.matchDateIso == "2026-10-10" }
    assertTrue(week3Matches.isNotEmpty())
  }
}


