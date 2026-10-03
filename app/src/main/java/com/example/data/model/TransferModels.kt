package com.example.data.model

data class LiveTransferItem(
  val id: String,
  val playerName: String,
  val fromTeam: String,
  val toTeam: String,
  val transferFee: String,
  val feeType: String, // Bonservis, Kiralık, Bedelsiz, Opsiyonlu Kiralık
  val contractYears: Int,
  val salaryAnnual: String,
  val status: String, // "KAP Resmi Açıklaması", "Prensip Anlaşması", "İmza Atıldı", "Sağlık Kontrolü"
  val date: String,
  val nationality: String,
  val age: Int,
  val position: String,
  val rating: Double,
  val ffpImpact: String,
  val announcementSummary: String
)

data class AiTransferEvaluation(
  val playerName: String,
  val buyerTeam: String,
  val offeredFeeEurMillions: Double,
  val estimatedFairValueEurMillions: Double,
  val isAccepted: Boolean,
  val probabilityPct: Int,
  val agentFeedback: String,
  val ffpStatus: String, // "UEFA Uyumlu", "Riskli Harcama", "FFP Limit Aşımı"
  val squadSynergyScore: Int, // 0..100
  val xgContributionEstimate: String,
  val verdictTitle: String,
  val verdictExplanation: String
)

data class HistoricalTransfer(
  val id: String,
  val season: String, // e.g. "2025/2026", "2024/2025"
  val playerName: String,
  val fromClub: String,
  val toClub: String,
  val feeFormatted: String,
  val marketValueAtTime: String,
  val notes: String
)

data class TransferMarketPlayer(
  val id: String,
  val name: String,
  val club: String,
  val league: String,
  val position: String, // KL, STP, SLB, SĞB, OS, ONN, SLK, SĞK, SNT
  val age: Int,
  val rating: Double,
  val marketValueEur: String, // e.g. "€75.000.000"
  val tokenPrice: Long, // e.g. 1500L TP
  val nationality: String,
  val statusText: String, // "Satış Listesinde", "Kulüple Sözleşme Aşamasında", "Tekliflere Açık", "Serbest Kalma Maddesi"
  val xgPerMatch: String,
  val scoutingSummary: String
)

data class UserVirtualTransfer(
  val id: String,
  val player: TransferMarketPlayer,
  val tokenPaid: Long,
  val date: String,
  val contractYears: Int,
  val verdictSummary: String
)
