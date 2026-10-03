package com.example.data.datasource

data class ScorerRow(
  val rank: Int,
  val player: String,
  val team: String,
  val matches: Int,
  val goals: Int,
  val penalties: Int,
  val assists: Int,
  val xg: Double,
  val minutesPerGoal: Int
)

data class AssistRow(
  val rank: Int,
  val player: String,
  val team: String,
  val matches: Int,
  val assists: Int,
  val keyPasses: Int
)

data class BasketballLeaderRow(
  val rank: Int,
  val player: String,
  val team: String,
  val statValue: Double,
  val statType: String,
  val gamesPlayed: Int
)

data class MotorsportStandingRow(
  val rank: Int,
  val driver: String,
  val team: String,
  val points: Int,
  val wins: Int,
  val podiums: Int
)

data class TennisRankingRow(
  val rank: Int,
  val player: String,
  val country: String,
  val points: Int,
  val movement: String
)

data class VolleyballStandingRow(
  val rank: Int,
  val team: String,
  val played: Int,
  val won: Int,
  val lost: Int,
  val points: Int,
  val topScorer: String
)

object MackolikMultiSportStatsDataSource {

  fun getTopScorers(league: String): List<ScorerRow> {
    return when (league) {
      "Trendyol Süper Lig", "Süper Lig" -> listOf(
        ScorerRow(1, "Ciro Immobile", "Beşiktaş", 6, 7, 2, 1, 6.2, 74),
        ScorerRow(2, "Mauro Icardi", "Galatasaray", 6, 6, 1, 2, 5.8, 86),
        ScorerRow(3, "Edin Džeko", "Fenerbahçe", 6, 5, 0, 2, 4.9, 98),
        ScorerRow(4, "Simon Banza", "Trabzonspor", 5, 4, 1, 0, 3.8, 102),
        ScorerRow(5, "Krzysztof Piątek", "Başakşehir", 5, 4, 0, 1, 3.5, 110),
        ScorerRow(6, "Youssef En-Nesyri", "Fenerbahçe", 6, 3, 0, 1, 3.1, 142),
        ScorerRow(7, "Barış Alper Yılmaz", "Galatasaray", 6, 3, 0, 1, 2.9, 155),
        ScorerRow(8, "Rafa Silva", "Beşiktaş", 6, 3, 0, 4, 2.7, 168)
      )
      "Premier League" -> listOf(
        ScorerRow(1, "Erling Haaland", "Manchester City", 5, 10, 1, 0, 8.4, 45),
        ScorerRow(2, "Cole Palmer", "Chelsea", 5, 6, 1, 4, 5.1, 72),
        ScorerRow(3, "Mohamed Salah", "Liverpool", 5, 5, 1, 4, 4.8, 88),
        ScorerRow(4, "Nicolas Jackson", "Chelsea", 5, 4, 0, 2, 3.9, 105),
        ScorerRow(5, "Bukayo Saka", "Arsenal", 5, 3, 0, 5, 3.2, 140)
      )
      "La Liga" -> listOf(
        ScorerRow(1, "Robert Lewandowski", "Barcelona", 6, 7, 2, 2, 6.5, 75),
        ScorerRow(2, "Kylian Mbappé", "Real Madrid", 6, 5, 3, 1, 5.4, 102),
        ScorerRow(3, "Raphinha", "Barcelona", 6, 5, 0, 3, 4.2, 105),
        ScorerRow(4, "Vinicius Junior", "Real Madrid", 6, 3, 1, 4, 3.6, 170),
        ScorerRow(5, "Lamine Yamal", "Barcelona", 6, 3, 0, 5, 3.1, 172)
      )
      "Bundesliga" -> listOf(
        ScorerRow(1, "Harry Kane", "Bayern Münih", 5, 8, 3, 4, 7.1, 55),
        ScorerRow(2, "Omar Marmoush", "Frankfurt", 5, 6, 1, 3, 5.2, 73),
        ScorerRow(3, "Serhou Guirassy", "Dortmund", 4, 4, 0, 1, 3.6, 85)
      )
      "Serie A" -> listOf(
        ScorerRow(1, "Mateo Retegui", "Atalanta", 6, 7, 1, 1, 5.9, 72),
        ScorerRow(2, "Marcus Thuram", "Inter", 6, 5, 0, 1, 4.5, 98),
        ScorerRow(3, "Dušan Vlahović", "Juventus", 6, 4, 1, 1, 4.1, 128)
      )
      else -> getTopScorers("Trendyol Süper Lig")
    }
  }

  fun getTopAssists(league: String): List<AssistRow> {
    return when (league) {
      "Trendyol Süper Lig", "Süper Lig" -> listOf(
        AssistRow(1, "Dušan Tadić", "Fenerbahçe", 6, 5, 19),
        AssistRow(2, "Rafa Silva", "Beşiktaş", 6, 4, 15),
        AssistRow(3, "Dries Mertens", "Galatasaray", 6, 4, 14),
        AssistRow(4, "Gabriel Sara", "Galatasaray", 6, 3, 13),
        AssistRow(5, "Edin Višća", "Trabzonspor", 5, 3, 11),
        AssistRow(6, "Fred", "Fenerbahçe", 5, 2, 9),
        AssistRow(7, "Arthur Masuaku", "Beşiktaş", 6, 2, 8)
      )
      "Premier League" -> listOf(
        AssistRow(1, "Bukayo Saka", "Arsenal", 5, 5, 17),
        AssistRow(2, "Cole Palmer", "Chelsea", 5, 4, 14),
        AssistRow(3, "Mohamed Salah", "Liverpool", 5, 4, 13),
        AssistRow(4, "Kevin De Bruyne", "Manchester City", 4, 3, 12)
      )
      "La Liga" -> listOf(
        AssistRow(1, "Lamine Yamal", "Barcelona", 6, 5, 18),
        AssistRow(2, "Raphinha", "Barcelona", 6, 3, 15),
        AssistRow(3, "Álex Baena", "Villarreal", 6, 3, 13),
        AssistRow(4, "Vinicius Junior", "Real Madrid", 6, 4, 12)
      )
      else -> getTopAssists("Trendyol Süper Lig")
    }
  }

  fun getBasketballLeaders(tournament: String, statType: String = "Sayı"): List<BasketballLeaderRow> {
    return when (tournament) {
      "EuroLeague" -> when (statType) {
        "Sayı" -> listOf(
          BasketballLeaderRow(1, "Kendrick Nunn", "Panathinaikos", 21.4, "Sayı Ort.", 5),
          BasketballLeaderRow(2, "Nigel Hayes-Davis", "Fenerbahçe Beko", 18.8, "Sayı Ort.", 5),
          BasketballLeaderRow(3, "Shane Larkin", "Anadolu Efes", 17.5, "Sayı Ort.", 5),
          BasketballLeaderRow(4, "Facundo Campazzo", "Real Madrid", 16.2, "Sayı Ort.", 5),
          BasketballLeaderRow(5, "Mike James", "AS Monaco", 16.0, "Sayı Ort.", 5)
        )
        "Ribaund" -> listOf(
          BasketballLeaderRow(1, "Walter Tavares", "Real Madrid", 8.2, "Ribaund Ort.", 5),
          BasketballLeaderRow(2, "Nikola Milutinov", "Olympiacos", 7.8, "Ribaund Ort.", 5),
          BasketballLeaderRow(3, "Nicolo Melli", "Fenerbahçe Beko", 6.8, "Ribaund Ort.", 5)
        )
        else -> listOf(
          BasketballLeaderRow(1, "Facundo Campazzo", "Real Madrid", 7.4, "Asist Ort.", 5),
          BasketballLeaderRow(2, "Nick Calathes", "AS Monaco", 6.8, "Asist Ort.", 5),
          BasketballLeaderRow(3, "Wade Baldwin IV", "Fenerbahçe Beko", 5.6, "Asist Ort.", 5)
        )
      }
      "NBA" -> when (statType) {
        "Sayı" -> listOf(
          BasketballLeaderRow(1, "Luka Dončić", "Dallas Mavericks", 33.2, "Sayı Ort.", 10),
          BasketballLeaderRow(2, "Shai Gilgeous-Alexander", "Oklahoma City", 31.0, "Sayı Ort.", 10),
          BasketballLeaderRow(3, "Giannis Antetokounmpo", "Milwaukee Bucks", 30.5, "Sayı Ort.", 10),
          BasketballLeaderRow(4, "Jayson Tatum", "Boston Celtics", 28.8, "Sayı Ort.", 10),
          BasketballLeaderRow(5, "Anthony Davis", "LA Lakers", 27.2, "Sayı Ort.", 10)
        )
        "Ribaund" -> listOf(
          BasketballLeaderRow(1, "Domantas Sabonis", "Sacramento Kings", 13.8, "Ribaund Ort.", 10),
          BasketballLeaderRow(2, "Nikola Jokić", "Denver Nuggets", 12.6, "Ribaund Ort.", 10),
          BasketballLeaderRow(3, "Anthony Davis", "LA Lakers", 11.9, "Ribaund Ort.", 10)
        )
        else -> listOf(
          BasketballLeaderRow(1, "Tyrese Haliburton", "Indiana Pacers", 11.2, "Asist Ort.", 10),
          BasketballLeaderRow(2, "Trae Young", "Atlanta Hawks", 10.6, "Asist Ort.", 10),
          BasketballLeaderRow(3, "Nikola Jokić", "Denver Nuggets", 9.8, "Asist Ort.", 10)
        )
      }
      else -> listOf(
        BasketballLeaderRow(1, "Errick McCollum", "Karşıyaka", 19.5, "Sayı Ort.", 4),
        BasketballLeaderRow(2, "Shane Larkin", "Anadolu Efes", 18.2, "Sayı Ort.", 4),
        BasketballLeaderRow(3, "Nigel Hayes-Davis", "Fenerbahçe Beko", 17.0, "Sayı Ort.", 4)
      )
    }
  }

  fun getMotorsportsStandings(series: String): List<MotorsportStandingRow> {
    return when (series) {
      "Formula 1", "F1" -> listOf(
        MotorsportStandingRow(1, "Max Verstappen", "Red Bull Racing", 331, 7, 11),
        MotorsportStandingRow(2, "Lando Norris", "McLaren", 279, 3, 10),
        MotorsportStandingRow(3, "Charles Leclerc", "Ferrari", 245, 2, 9),
        MotorsportStandingRow(4, "Oscar Piastri", "McLaren", 237, 2, 7),
        MotorsportStandingRow(5, "Carlos Sainz", "Ferrari", 190, 1, 5),
        MotorsportStandingRow(6, "Lewis Hamilton", "Mercedes", 174, 2, 4)
      )
      "MotoGP" -> listOf(
        MotorsportStandingRow(1, "Jorge Martin", "Prima Pramac Ducati", 341, 3, 11),
        MotorsportStandingRow(2, "Francesco Bagnaia", "Ducati Lenovo", 317, 7, 11),
        MotorsportStandingRow(3, "Marc Márquez", "Gresini Racing", 282, 2, 8),
        MotorsportStandingRow(4, "Enea Bastianini", "Ducati Lenovo", 281, 2, 7),
        MotorsportStandingRow(5, "Brad Binder", "Red Bull KTM", 165, 0, 1)
      )
      "WRC Ralli" -> listOf(
        MotorsportStandingRow(1, "Thierry Neuville", "Hyundai Shell Mobis", 192, 2, 5),
        MotorsportStandingRow(2, "Ott Tänak", "Hyundai Shell Mobis", 158, 1, 4),
        MotorsportStandingRow(3, "Sébastien Ogier", "Toyota Gazoo Racing", 154, 3, 5),
        MotorsportStandingRow(4, "Elfyn Evans", "Toyota Gazoo Racing", 140, 0, 4)
      )
      else -> getMotorsportsStandings("Formula 1")
    }
  }

  fun getTennisRankings(): List<TennisRankingRow> {
    return listOf(
      TennisRankingRow(1, "Jannik Sinner", "İtalya 🇮🇹", 11180, "—"),
      TennisRankingRow(2, "Carlos Alcaraz", "İspanya 🇪🇸", 7010, "▲ 1"),
      TennisRankingRow(3, "Alexander Zverev", "Almanya 🇩🇪", 6875, "▼ 1"),
      TennisRankingRow(4, "Novak Djokovic", "Sırbistan 🇷🇸", 5560, "—"),
      TennisRankingRow(5, "Daniil Medvedev", "Bireysel 🌐", 5475, "—"),
      TennisRankingRow(6, "Andrey Rublev", "Bireysel 🌐", 4645, "—"),
      TennisRankingRow(7, "Taylor Fritz", "ABD 🇺🇸", 4060, "▲ 5")
    )
  }

  fun getVolleyballStandings(): List<VolleyballStandingRow> {
    return listOf(
      VolleyballStandingRow(1, "Fenerbahçe Medicana", 6, 6, 0, 18, "Melissa Vargas (26.4 s/m)"),
      VolleyballStandingRow(2, "VakıfBank", 6, 5, 1, 16, "Marina Markova (22.8 s/m)"),
      VolleyballStandingRow(3, "Eczacıbaşı Dynavit", 6, 5, 1, 15, "Tijana Bošković (25.1 s/m)"),
      VolleyballStandingRow(4, "Galatasaray Daikin", 6, 4, 2, 12, "İlkin Aydın (18.6 s/m)"),
      VolleyballStandingRow(5, "Türk Hava Yolları", 6, 3, 3, 9, "Kiera Van Ryk (19.4 s/m)")
    )
  }
}
