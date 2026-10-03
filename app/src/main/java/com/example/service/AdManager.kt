package com.example.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AdNetwork(val displayName: String, val badgeColorHex: Long, val tag: String) {
  ADMOB("Google AdMob", 0xFF4285F4, "AdMob"),
  UNITY_ADS("Unity Ads", 0xFF1E293B, "Unity"),
  APPLOVIN("AppLovin MAX", 0xFFE11D48, "MAX"),
  IRONSOURCE("ironSource", 0xFF0D9488, "ironSource")
}

data class AdSystemState(
  val activeNetwork: AdNetwork = AdNetwork.ADMOB,
  val totalImpressions: Int = 124,
  val rewardedWatched: Int = 12,
  val totalRewardedTpEarned: Long = 6000L,
  val fillRatePercent: Double = 99.4,
  val simulatedEcpm: Double = 14.85
)

/**
 * Multi-Ad mediation and rewarded ad engine.
 * Supports Google AdMob, Unity Ads, AppLovin MAX, and ironSource simulation.
 */
object AdManager {

  private val _state = MutableStateFlow(AdSystemState())
  val state: StateFlow<AdSystemState> = _state.asStateFlow()

  fun setActiveNetwork(network: AdNetwork) {
    _state.value = _state.value.copy(activeNetwork = network)
  }

  fun recordImpression() {
    _state.value = _state.value.copy(
      totalImpressions = _state.value.totalImpressions + 1
    )
  }

  fun recordRewardedAdWatched(rewardTp: Long = 500L) {
    _state.value = _state.value.copy(
      rewardedWatched = _state.value.rewardedWatched + 1,
      totalRewardedTpEarned = _state.value.totalRewardedTpEarned + rewardTp,
      totalImpressions = _state.value.totalImpressions + 1
    )
  }
}
