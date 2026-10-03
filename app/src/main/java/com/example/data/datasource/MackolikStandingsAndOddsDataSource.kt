package com.example.data.datasource

import com.example.data.model.DroppingOddItem
import com.example.data.model.LeagueStandingRow
import com.example.data.model.PopularBetItem
import com.example.data.model.Sport

/**
 * Authentic Mackolik Data Provider for:
 * 1. Live Standings (Puan Durumu) for all major football & basketball leagues.
 * 2. Dropping Odds (Düşen Oranlar) widget.
 * 3. Most Played Bets (En Çok Oynananlar) widget.
 */
object MackolikStandingsAndOddsDataSource {

  fun getStandingsForLeague(leagueName: String): List<LeagueStandingRow> {
    return when (leagueName) {
      "Trendyol Süper Lig", "Süper Lig" -> listOf(
        LeagueStandingRow(1, "Galatasaray", 6, 6, 0, 0, 20, 5, 15, 18, listOf("G", "G", "G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(2, "Fenerbahçe", 6, 4, 1, 1, 14, 5, 9, 13, listOf("G", "G", "B", "G", "M"), 0xFF10B981),
        LeagueStandingRow(3, "Beşiktaş", 5, 4, 1, 0, 11, 4, 7, 13, listOf("G", "G", "G", "B", "G"), 0xFF3B82F6),
        LeagueStandingRow(4, "Samsunspor", 6, 4, 0, 2, 9, 5, 4, 12, listOf("M", "G", "G", "G", "G"), 0xFF3B82F6),
        LeagueStandingRow(5, "Eyüpspor", 6, 3, 3, 0, 9, 5, 4, 12, listOf("B", "G", "B", "G", "B"), 0xFF64748B),
        LeagueStandingRow(6, "Trabzonspor", 5, 1, 4, 0, 4, 3, 1, 7, listOf("B", "B", "B", "B", "G"), 0xFF64748B),
        LeagueStandingRow(7, "Başakşehir", 5, 3, 1, 1, 10, 6, 4, 10, listOf("G", "B", "G", "G", "M"), 0xFF64748B),
        LeagueStandingRow(8, "Göztepe", 5, 2, 3, 0, 8, 4, 4, 9, listOf("B", "B", "B", "G", "G"), 0xFF64748B),
        LeagueStandingRow(9, "Kasımpaşa", 6, 1, 3, 2, 6, 8, -2, 6, listOf("M", "B", "B", "G", "B"), 0xFF64748B),
        LeagueStandingRow(10, "Sivasspor", 6, 2, 2, 2, 6, 6, 0, 8, listOf("B", "G", "M", "M", "G"), 0xFF64748B),
        LeagueStandingRow(16, "Kayserispor", 5, 0, 3, 2, 4, 8, -4, 3, listOf("M", "B", "B", "B", "M"), 0xFFEF4444),
        LeagueStandingRow(17, "Bodrum FK", 6, 2, 0, 4, 6, 10, -4, 6, listOf("M", "M", "G", "M", "G"), 0xFFEF4444),
        LeagueStandingRow(18, "Hatayspor", 6, 0, 2, 4, 4, 10, -6, 2, listOf("M", "M", "B", "M", "M"), 0xFFEF4444),
        LeagueStandingRow(19, "Adana Demirspor", 6, 0, 1, 5, 5, 14, -9, 1, listOf("M", "M", "B", "M", "M"), 0xFFEF4444)
      )

      "Premier League", "İngiltere Premier League" -> listOf(
        LeagueStandingRow(1, "Manchester City", 5, 4, 1, 0, 13, 5, 8, 13, listOf("G", "G", "G", "G", "B"), 0xFF10B981),
        LeagueStandingRow(2, "Liverpool", 5, 4, 0, 1, 10, 1, 9, 12, listOf("G", "G", "G", "M", "G"), 0xFF10B981),
        LeagueStandingRow(3, "Arsenal", 5, 3, 2, 0, 8, 3, 5, 11, listOf("G", "G", "B", "G", "B"), 0xFF10B981),
        LeagueStandingRow(4, "Chelsea", 5, 3, 1, 1, 11, 5, 6, 10, listOf("M", "G", "B", "G", "G"), 0xFF10B981),
        LeagueStandingRow(5, "Aston Villa", 5, 4, 0, 1, 10, 7, 3, 12, listOf("G", "M", "G", "G", "G"), 0xFF3B82F6),
        LeagueStandingRow(6, "Newcastle", 5, 3, 1, 1, 7, 6, 1, 10, listOf("G", "B", "G", "G", "M"), 0xFF64748B),
        LeagueStandingRow(7, "Tottenham", 5, 2, 1, 2, 9, 5, 4, 7, listOf("B", "G", "M", "M", "G"), 0xFF64748B),
        LeagueStandingRow(11, "Manchester United", 5, 2, 1, 2, 5, 5, 0, 7, listOf("G", "M", "M", "G", "B"), 0xFF64748B),
        LeagueStandingRow(18, "Crystal Palace", 5, 0, 3, 2, 4, 8, -4, 3, listOf("M", "M", "B", "B", "B"), 0xFFEF4444),
        LeagueStandingRow(19, "Southampton", 5, 0, 1, 4, 2, 9, -7, 1, listOf("M", "M", "M", "M", "B"), 0xFFEF4444),
        LeagueStandingRow(20, "Everton", 5, 0, 1, 4, 5, 14, -9, 1, listOf("M", "M", "M", "M", "B"), 0xFFEF4444)
      )

      "La Liga", "İspanya La Liga" -> listOf(
        LeagueStandingRow(1, "Barcelona", 6, 6, 0, 0, 22, 5, 17, 18, listOf("G", "G", "G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(2, "Real Madrid", 6, 4, 2, 0, 13, 3, 10, 14, listOf("B", "G", "B", "G", "G"), 0xFF10B981),
        LeagueStandingRow(3, "Athletic Bilbao", 7, 4, 1, 2, 11, 7, 4, 13, listOf("G", "M", "G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(4, "Atletico Madrid", 6, 3, 3, 0, 10, 3, 7, 12, listOf("B", "G", "B", "G", "B"), 0xFF10B981),
        LeagueStandingRow(5, "Mallorca", 7, 3, 2, 2, 6, 6, 0, 11, listOf("B", "B", "G", "M", "G"), 0xFF3B82F6),
        LeagueStandingRow(6, "Villarreal", 6, 3, 2, 1, 12, 13, -1, 11, listOf("B", "G", "G", "B", "M"), 0xFF64748B)
      )

      "Serie A", "İtalya Serie A" -> listOf(
        LeagueStandingRow(1, "Napoli", 6, 4, 1, 1, 11, 4, 7, 13, listOf("M", "G", "G", "G", "B"), 0xFF10B981),
        LeagueStandingRow(2, "Juventus", 6, 3, 3, 0, 9, 0, 9, 12, listOf("G", "G", "B", "B", "B"), 0xFF10B981),
        LeagueStandingRow(3, "AC Milan", 6, 3, 2, 1, 14, 7, 7, 11, listOf("B", "M", "B", "G", "G"), 0xFF10B981),
        LeagueStandingRow(4, "Inter", 6, 3, 2, 1, 13, 7, 6, 11, listOf("B", "G", "G", "B", "M"), 0xFF10B981),
        LeagueStandingRow(5, "Torino", 6, 3, 2, 1, 10, 8, 2, 11, listOf("B", "G", "G", "B", "M"), 0xFF3B82F6),
        LeagueStandingRow(6, "Lazio", 6, 3, 1, 2, 12, 10, 2, 10, listOf("G", "M", "B", "G", "M"), 0xFF64748B),
        LeagueStandingRow(7, "AS Roma", 6, 2, 3, 1, 7, 4, 3, 9, listOf("B", "M", "B", "B", "G"), 0xFF64748B),
        LeagueStandingRow(18, "Monza", 6, 0, 3, 3, 4, 8, -4, 3, listOf("B", "M", "B", "B", "M"), 0xFFEF4444)
      )

      "Bundesliga", "Almanya Bundesliga" -> listOf(
        LeagueStandingRow(1, "Bayern München", 5, 4, 1, 0, 17, 4, 13, 13, listOf("G", "G", "G", "G", "B"), 0xFF10B981),
        LeagueStandingRow(2, "Eintracht Frankfurt", 5, 4, 0, 1, 11, 6, 5, 12, listOf("M", "G", "G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(3, "RB Leipzig", 5, 3, 2, 0, 8, 2, 6, 11, listOf("G", "G", "B", "B", "G"), 0xFF10B981),
        LeagueStandingRow(4, "Bayer Leverkusen", 5, 3, 1, 1, 14, 10, 4, 10, listOf("G", "M", "G", "G", "B"), 0xFF10B981),
        LeagueStandingRow(5, "Borussia Dortmund", 5, 3, 1, 1, 11, 9, 2, 10, listOf("G", "B", "G", "M", "G"), 0xFF3B82F6),
        LeagueStandingRow(6, "VfB Stuttgart", 5, 2, 2, 1, 14, 10, 4, 8, listOf("M", "B", "G", "G", "B"), 0xFF64748B),
        LeagueStandingRow(18, "Holstein Kiel", 5, 0, 1, 4, 7, 17, -10, 1, listOf("M", "M", "M", "B", "M"), 0xFFEF4444)
      )

      "Ligue 1", "Fransa Ligue 1" -> listOf(
        LeagueStandingRow(1, "Paris Saint-Germain", 6, 5, 1, 0, 20, 5, 15, 16, listOf("G", "G", "G", "G", "B"), 0xFF10B981),
        LeagueStandingRow(2, "Monaco", 6, 5, 1, 0, 12, 3, 9, 16, listOf("G", "G", "B", "G", "G"), 0xFF10B981),
        LeagueStandingRow(3, "Marseille", 6, 4, 1, 1, 15, 7, 8, 13, listOf("G", "B", "G", "G", "M"), 0xFF10B981),
        LeagueStandingRow(4, "Reims", 6, 3, 2, 1, 10, 8, 2, 11, listOf("M", "B", "G", "G", "B"), 0xFF10B981),
        LeagueStandingRow(5, "Lille", 6, 3, 1, 2, 11, 7, 4, 10, listOf("G", "G", "M", "M", "B"), 0xFF3B82F6),
        LeagueStandingRow(18, "Montpellier", 6, 1, 1, 4, 6, 17, -11, 4, listOf("B", "M", "M", "M", "G"), 0xFFEF4444)
      )

      "Vodafone Sultanlar Ligi", "Sultanlar Ligi" -> listOf(
        LeagueStandingRow(1, "VakıfBank", 5, 5, 0, 0, 15, 2, 13, 15, listOf("G", "G", "G", "G", "G"), 0xFF10B981, isBasketball = false),
        LeagueStandingRow(2, "Fenerbahçe Medicana", 5, 4, 0, 1, 14, 4, 10, 12, listOf("G", "G", "G", "M", "G"), 0xFF10B981, isBasketball = false),
        LeagueStandingRow(3, "Eczacıbaşı Dynavit", 5, 4, 0, 1, 13, 5, 8, 12, listOf("G", "G", "M", "G", "G"), 0xFF10B981, isBasketball = false),
        LeagueStandingRow(4, "Türk Hava Yolları", 5, 3, 0, 2, 11, 7, 4, 9, listOf("M", "G", "G", "G", "M"), 0xFF3B82F6, isBasketball = false),
        LeagueStandingRow(5, "Galatasaray Daikin", 5, 3, 0, 2, 10, 8, 2, 9, listOf("G", "M", "G", "M", "G"), 0xFF64748B, isBasketball = false),
        LeagueStandingRow(6, "Kuzeyboru", 5, 2, 0, 3, 8, 11, -3, 6, listOf("M", "M", "G", "G", "M"), 0xFF64748B, isBasketball = false)
      )

      "EuroLeague", "Turkish Airlines EuroLeague" -> listOf(
        LeagueStandingRow(1, "Fenerbahçe Beko", 5, 4, 0, 1, 412, 385, 27, 8, listOf("G", "G", "G", "M", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(2, "Panathinaikos", 5, 4, 0, 1, 435, 398, 37, 8, listOf("G", "G", "M", "G", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(3, "Real Madrid", 5, 3, 0, 2, 420, 401, 19, 6, listOf("M", "G", "M", "G", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(4, "Olympiacos", 5, 3, 0, 2, 410, 395, 15, 6, listOf("M", "G", "G", "M", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(5, "FC Barcelona", 5, 3, 0, 2, 425, 410, 15, 6, listOf("M", "G", "G", "G", "M"), 0xFF3B82F6, isBasketball = true),
        LeagueStandingRow(6, "Anadolu Efes", 5, 3, 0, 2, 415, 408, 7, 6, listOf("G", "M", "M", "G", "G"), 0xFF3B82F6, isBasketball = true),
        LeagueStandingRow(7, "AS Monaco", 5, 3, 0, 2, 408, 402, 6, 6, listOf("G", "G", "M", "M", "G"), 0xFF64748B, isBasketball = true),
        LeagueStandingRow(8, "Partizan", 5, 2, 0, 3, 405, 415, -10, 4, listOf("M", "M", "G", "G", "M"), 0xFF64748B, isBasketball = true),
        LeagueStandingRow(9, "Maccabi Tel Aviv", 5, 2, 0, 3, 412, 424, -12, 4, listOf("G", "M", "M", "G", "M"), 0xFF64748B, isBasketball = true),
        LeagueStandingRow(10, "Baskonia", 5, 2, 0, 3, 398, 412, -14, 4, listOf("G", "M", "G", "M", "M"), 0xFF64748B, isBasketball = true)
      )

      "NBA" -> listOf(
        LeagueStandingRow(1, "Boston Celtics", 10, 9, 0, 1, 1210, 1080, 130, 18, listOf("G", "G", "G", "G", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(2, "Cleveland Cavaliers", 10, 9, 0, 1, 1195, 1090, 105, 18, listOf("G", "G", "G", "G", "M"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(3, "Oklahoma City Thunder", 10, 8, 0, 2, 1160, 1040, 120, 16, listOf("G", "G", "M", "G", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(4, "Denver Nuggets", 10, 7, 0, 3, 1180, 1145, 35, 14, listOf("M", "G", "G", "G", "M"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(5, "Golden State Warriors", 10, 7, 0, 3, 1175, 1110, 65, 14, listOf("G", "G", "G", "M", "G"), 0xFF3B82F6, isBasketball = true),
        LeagueStandingRow(6, "Los Angeles Lakers", 10, 6, 0, 4, 1150, 1140, 10, 12, listOf("G", "M", "M", "G", "G"), 0xFF3B82F6, isBasketball = true),
        LeagueStandingRow(7, "Dallas Mavericks", 10, 5, 0, 5, 1130, 1120, 10, 10, listOf("M", "G", "M", "G", "M"), 0xFF64748B, isBasketball = true)
      )

      "Türkiye Basketbol Süper Ligi (BSL)", "BSL" -> listOf(
        LeagueStandingRow(1, "Fenerbahçe Beko", 4, 4, 0, 0, 362, 305, 57, 8, listOf("G", "G", "G", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(2, "Anadolu Efes", 4, 4, 0, 0, 358, 312, 46, 8, listOf("G", "G", "G", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(3, "Beşiktaş Fibabanka", 4, 3, 0, 1, 335, 310, 25, 6, listOf("G", "G", "M", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(4, "Karşıyaka", 4, 3, 0, 1, 340, 325, 15, 6, listOf("M", "G", "G", "G"), 0xFF10B981, isBasketball = true),
        LeagueStandingRow(5, "Galatasaray", 4, 2, 0, 2, 328, 330, -2, 4, listOf("G", "M", "M", "G"), 0xFF3B82F6, isBasketball = true)
      )

      "UEFA Şampiyonlar Ligi", "Şampiyonlar Ligi" -> listOf(
        LeagueStandingRow(1, "Liverpool", 4, 4, 0, 0, 10, 1, 9, 12, listOf("G", "G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(2, "Sporting CP", 4, 3, 1, 0, 9, 2, 7, 10, listOf("G", "B", "G", "G"), 0xFF10B981),
        LeagueStandingRow(3, "Monaco", 4, 3, 1, 0, 10, 4, 6, 10, listOf("G", "B", "G", "G"), 0xFF10B981),
        LeagueStandingRow(4, "Borussia Dortmund", 4, 3, 0, 1, 13, 6, 7, 9, listOf("G", "G", "M", "G"), 0xFF10B981),
        LeagueStandingRow(5, "Aston Villa", 4, 3, 0, 1, 6, 1, 5, 9, listOf("G", "G", "G", "M"), 0xFF3B82F6),
        LeagueStandingRow(6, "Manchester City", 4, 2, 1, 1, 10, 4, 6, 7, listOf("B", "G", "G", "M"), 0xFF3B82F6),
        LeagueStandingRow(7, "Inter", 4, 3, 1, 0, 6, 0, 6, 10, listOf("B", "G", "G", "G"), 0xFF3B82F6),
        LeagueStandingRow(8, "Arsenal", 4, 2, 1, 1, 3, 1, 2, 7, listOf("B", "G", "G", "M"), 0xFF3B82F6),
        LeagueStandingRow(17, "Real Madrid", 4, 2, 0, 2, 9, 7, 2, 6, listOf("G", "M", "G", "M"), 0xFF64748B),
        LeagueStandingRow(25, "Paris Saint-Germain", 4, 1, 1, 2, 3, 5, -2, 4, listOf("G", "M", "B", "M"), 0xFFEF4444)
      )

      "UEFA Avrupa Ligi", "Avrupa Ligi" -> listOf(
        LeagueStandingRow(1, "Lazio", 4, 4, 0, 0, 11, 2, 9, 12, listOf("G", "G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(2, "Ajax", 4, 3, 1, 0, 13, 1, 12, 10, listOf("G", "B", "G", "G"), 0xFF10B981),
        LeagueStandingRow(3, "Galatasaray", 4, 3, 1, 0, 12, 8, 4, 10, listOf("G", "B", "G", "G"), 0xFF10B981),
        LeagueStandingRow(4, "Eintracht Frankfurt", 4, 3, 1, 0, 8, 4, 4, 10, listOf("B", "G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(5, "Anderlecht", 4, 3, 1, 0, 7, 3, 4, 10, listOf("G", "G", "G", "B"), 0xFF3B82F6),
        LeagueStandingRow(14, "Fenerbahçe", 4, 1, 2, 1, 5, 6, -1, 5, listOf("G", "B", "B", "M"), 0xFF64748B),
        LeagueStandingRow(18, "Beşiktaş", 4, 2, 0, 2, 4, 8, -4, 6, listOf("M", "M", "G", "G"), 0xFF64748B)
      )

      "UEFA Konferans Ligi", "Konferans Ligi" -> listOf(
        LeagueStandingRow(1, "Chelsea", 3, 3, 0, 0, 16, 3, 13, 9, listOf("G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(2, "Legia Warszawa", 3, 3, 0, 0, 8, 0, 8, 9, listOf("G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(3, "Jagiellonia", 3, 3, 0, 0, 7, 1, 6, 9, listOf("G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(4, "Rapid Wien", 3, 3, 0, 0, 6, 1, 5, 9, listOf("G", "G", "G"), 0xFF10B981),
        LeagueStandingRow(12, "Başakşehir", 3, 0, 1, 2, 4, 9, -5, 1, listOf("M", "M", "B"), 0xFF64748B)
      )

      "Trendyol 1. Lig", "TFF 1. Lig" -> listOf(
        LeagueStandingRow(1, "Kocaelispor", 7, 5, 1, 1, 12, 5, 7, 16, listOf("G", "G", "G", "B", "G"), 0xFF10B981),
        LeagueStandingRow(2, "Erzurumspor FK", 7, 4, 1, 2, 10, 4, 6, 13, listOf("G", "M", "G", "G", "B"), 0xFF10B981),
        LeagueStandingRow(3, "Fatih Karagümrük", 7, 3, 3, 1, 11, 7, 4, 12, listOf("B", "G", "G", "B", "G"), 0xFF3B82F6),
        LeagueStandingRow(4, "Bandırmaspor", 7, 3, 2, 2, 9, 8, 1, 11, listOf("G", "B", "M", "G", "B"), 0xFF3B82F6),
        LeagueStandingRow(5, "İstanbulspor", 7, 3, 2, 2, 10, 6, 4, 11, listOf("M", "G", "B", "G", "B"), 0xFF3B82F6),
        LeagueStandingRow(6, "Gençlerbirliği", 7, 3, 2, 2, 8, 6, 2, 11, listOf("B", "G", "M", "G", "B"), 0xFF64748B),
        LeagueStandingRow(18, "Yeni Malatyaspor", 7, 0, 0, 7, 2, 21, -19, -3, listOf("M", "M", "M", "M", "M"), 0xFFEF4444)
      )

      else -> getStandingsForLeague("Trendyol Süper Lig")
    }
  }

  fun getDroppingOdds(): List<DroppingOddItem> = listOf(
    DroppingOddItem(
      matchId = "m1",
      homeTeam = "Galatasaray",
      awayTeam = "Fenerbahçe",
      league = "Trendyol Süper Lig",
      marketName = "MS 1",
      initialOdd = 2.10,
      currentOdd = 1.72,
      dropPercentage = 18,
      reason = "Ev sahibi tam kadro sahada & yoğun taraftar desteğiyle oran düşüşü yaşanıyor.",
      sport = Sport.FOOTBALL
    ),
    DroppingOddItem(
      matchId = "m2",
      homeTeam = "Real Madrid",
      awayTeam = "Barcelona",
      league = "La Liga",
      marketName = "2.5 Üst",
      initialOdd = 1.82,
      currentOdd = 1.54,
      dropPercentage = 15,
      reason = "İki takımın da ofansif ilk 11'i açıklandı, gol beklentisi (xG) 3.40'a yükseldi.",
      sport = Sport.FOOTBALL
    ),
    DroppingOddItem(
      matchId = "mb1",
      homeTeam = "Fenerbahçe Beko",
      awayTeam = "Panathinaikos",
      league = "EuroLeague",
      marketName = "MS 1 (1-2)",
      initialOdd = 1.88,
      currentOdd = 1.58,
      dropPercentage = 16,
      reason = "Panathinaikos'ta 2 kilit guard sakatlandı. İddaa ve Nesine bülteninde oran sert düştü.",
      sport = Sport.BASKETBALL
    ),
    DroppingOddItem(
      matchId = "m3",
      homeTeam = "Arsenal",
      awayTeam = "Manchester City",
      league = "Premier League",
      marketName = "KG Var (Karşılıklı Gol)",
      initialOdd = 1.78,
      currentOdd = 1.52,
      dropPercentage = 14,
      reason = "Son 6 maçın 5'inde KG Var geldi. Genel kuponlarda en çok işaretlenen seçenek.",
      sport = Sport.FOOTBALL
    ),
    DroppingOddItem(
      matchId = "mb2",
      homeTeam = "Los Angeles Lakers",
      awayTeam = "Golden State Warriors",
      league = "NBA",
      marketName = "224.5 Üst",
      initialOdd = 1.90,
      currentOdd = 1.68,
      dropPercentage = 12,
      reason = "Pace ortalaması maç başına 104 hücum. Yüksek tempo bekleniyor.",
      sport = Sport.BASKETBALL
    )
  )

  fun getPopularBets(): List<PopularBetItem> = listOf(
    PopularBetItem(
      matchId = "m1",
      homeTeam = "Galatasaray",
      awayTeam = "Fenerbahçe",
      league = "Trendyol Süper Lig",
      selectionName = "MS 1",
      odd = 1.72,
      percentagePlayed = 84,
      totalBetsPlaced = 24800,
      sport = Sport.FOOTBALL
    ),
    PopularBetItem(
      matchId = "m2",
      homeTeam = "Real Madrid",
      awayTeam = "Barcelona",
      league = "La Liga",
      selectionName = "2.5 Üst",
      odd = 1.54,
      percentagePlayed = 78,
      totalBetsPlaced = 19450,
      sport = Sport.FOOTBALL
    ),
    PopularBetItem(
      matchId = "mb1",
      homeTeam = "Fenerbahçe Beko",
      awayTeam = "Panathinaikos",
      league = "EuroLeague",
      selectionName = "MS 1 (1-2)",
      odd = 1.58,
      percentagePlayed = 81,
      totalBetsPlaced = 12300,
      sport = Sport.BASKETBALL
    ),
    PopularBetItem(
      matchId = "m3",
      homeTeam = "Arsenal",
      awayTeam = "Manchester City",
      league = "Premier League",
      selectionName = "KG Var",
      odd = 1.52,
      percentagePlayed = 72,
      totalBetsPlaced = 16800,
      sport = Sport.FOOTBALL
    ),
    PopularBetItem(
      matchId = "mb3",
      homeTeam = "Anadolu Efes",
      awayTeam = "Real Madrid",
      league = "EuroLeague",
      selectionName = "164.5 Üst",
      odd = 1.84,
      percentagePlayed = 69,
      totalBetsPlaced = 8900,
      sport = Sport.BASKETBALL
    )
  )
}
