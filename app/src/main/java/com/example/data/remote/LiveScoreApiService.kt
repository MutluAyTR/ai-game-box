package com.example.data.remote

import com.example.data.model.MarketType
import com.example.data.model.Sport
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class LiveMatchDto(
  @param:Json(name = "id") val id: String,
  @param:Json(name = "sport") val sport: String,
  @param:Json(name = "league") val league: String,
  @param:Json(name = "home_team") val homeTeam: String,
  @param:Json(name = "away_team") val awayTeam: String,
  @param:Json(name = "home_score") val homeScore: Int,
  @param:Json(name = "away_score") val awayScore: Int,
  @param:Json(name = "minute") val minute: Int,
  @param:Json(name = "status") val status: String, // LIVE, FINISHED, SCHEDULED
  @param:Json(name = "odd_home") val oddHome: Double,
  @param:Json(name = "odd_draw") val oddDraw: Double,
  @param:Json(name = "odd_away") val oddAway: Double,
  @param:Json(name = "xg_home") val xgHome: Double,
  @param:Json(name = "xg_away") val xgAway: Double,
  @param:Json(name = "is_hot") val isHot: Boolean = false
)

interface LiveScoreApiService {
  @GET("api/v1/matches/live")
  suspend fun getLiveMatches(
    @Query("sport") sport: String? = null
  ): Response<List<LiveMatchDto>>

  @GET("api/v1/matches/{id}")
  suspend fun getMatchDetail(
    @Path("id") id: String
  ): Response<LiveMatchDto>
}

object MockRetrofitClient {
  private val retrofit by lazy {
    Retrofit.Builder()
      .baseUrl("https://mock.tahminarena.local/")
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
  }

  val apiService: LiveScoreApiService by lazy {
    retrofit.create(LiveScoreApiService::class.java)
  }
}
