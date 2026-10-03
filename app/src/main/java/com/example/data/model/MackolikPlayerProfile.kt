package com.example.data.model

/**
 * Maçkolik & Nesine Resmi Sporcu Veritabanı Modeli.
 * Futbol, Basketbol, Voleybol, Tenis, Motor Sporları ve Hentbol
 * branşlarındaki 50.000+ sporcunun detaylı bilgilerini taşır.
 */
data class MackolikPlayerProfile(
  val id: String,
  val name: String,
  val team: String,
  val sport: String, // Futbol, Basketbol, Voleybol, Tenis, Motor Sporları, Hentbol
  val league: String,
  val number: Int,
  val position: String, // GK, DEF, MID, FWD, PG, SG, SF, PF, C, Pasör, Smaçör, Orta Oyuncu, Libero, Pilot, Raket vb.
  val rating: Double,
  val nationality: String,
  val age: Int,
  val marketValue: String,
  val isCaptain: Boolean = false,
  val statsSummary: String = "",
  val goals: Int = 0,
  val assists: Int = 0,
  val points: Int = 0,
  val rebounds: Int = 0,
  val yellowCards: Int = 0,
  val redCards: Int = 0,
  val matchCount: Int = 20,
  val photoPlaceholderColor: Long = 0xFF0D9488
) {
  fun toPlayerLineup(): PlayerLineup {
    return PlayerLineup(
      number = number,
      name = name,
      position = position,
      rating = rating,
      isCaptain = isCaptain,
      yellowCards = yellowCards,
      redCards = redCards,
      goals = goals,
      marketValue = marketValue
    )
  }
}
