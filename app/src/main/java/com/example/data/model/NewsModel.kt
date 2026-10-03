package com.example.data.model

data class ArenaNewsArticle(
  val id: String,
  val title: String,
  val summary: String,
  val content: String,
  val category: String, // Süper Lig, Şampiyonlar Ligi, Transfer, Basketbol, Tenis
  val source: String,
  val author: String,
  val publishedAgo: String,
  val readTimeMinutes: Int = 3,
  val readCount: Int = 1450,
  val relatedMatchId: String? = null,
  val relatedMatchTeams: String? = null,
  val emojiBadge: String = "📰",
  val tags: List<String> = emptyList()
)
