package com.example.data.remote

import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MockLiveScoreRepository {

  suspend fun fetchLiveMatches(): Result<List<LiveMatchDto>> {
    delay(400) // realistic network latency
    val mockData = listOf(
      LiveMatchDto(
        id = "m1",
        sport = "FOOTBALL",
        league = "Trendyol Süper Lig",
        homeTeam = "Galatasaray",
        awayTeam = "Fenerbahçe",
        homeScore = 2,
        awayScore = 0,
        minute = 58,
        status = "LIVE",
        oddHome = 1.08,
        oddDraw = 12.85,
        oddAway = 21.74,
        xgHome = 2.43,
        xgAway = 1.10,
        isHot = true
      ),
      LiveMatchDto(
        id = "m2",
        sport = "FOOTBALL",
        league = "Premier League",
        homeTeam = "Arsenal",
        awayTeam = "Manchester City",
        homeScore = 1,
        awayScore = 1,
        minute = 72,
        status = "LIVE",
        oddHome = 2.65,
        oddDraw = 3.20,
        oddAway = 2.50,
        xgHome = 1.45,
        xgAway = 1.62,
        isHot = false
      ),
      LiveMatchDto(
        id = "m3",
        sport = "FOOTBALL",
        league = "Trendyol Süper Lig",
        homeTeam = "Beşiktaş",
        awayTeam = "Trabzonspor",
        homeScore = 0,
        awayScore = 0,
        minute = 33,
        status = "LIVE",
        oddHome = 2.10,
        oddDraw = 3.40,
        oddAway = 3.20,
        xgHome = 0.54,
        xgAway = 0.38,
        isHot = false
      ),
      LiveMatchDto(
        id = "m4",
        sport = "FOOTBALL",
        league = "La Liga",
        homeTeam = "Real Madrid",
        awayTeam = "Barcelona",
        homeScore = 3,
        awayScore = 2,
        minute = 84,
        status = "LIVE",
        oddHome = 1.30,
        oddDraw = 5.50,
        oddAway = 8.00,
        xgHome = 2.85,
        xgAway = 2.15,
        isHot = true
      ),
      LiveMatchDto(
        id = "m5",
        sport = "BASKETBALL",
        league = "EuroLeague",
        homeTeam = "Fenerbahçe Beko",
        awayTeam = "Panathinaikos",
        homeScore = 68,
        awayScore = 64,
        minute = 32,
        status = "LIVE",
        oddHome = 1.55,
        oddDraw = 14.00,
        oddAway = 2.30,
        xgHome = 0.0,
        xgAway = 0.0,
        isHot = false
      )
    )
    return Result.success(mockData)
  }
}
