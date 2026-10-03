package com.example.data.datasource

import com.example.data.model.MarketType
import com.example.data.model.SlipSelection
import com.example.data.model.SocialCoupon

object VerifiedAnalystsDataSource {

  fun getVerifiedAnalystsCoupons(): List<SocialCoupon> {
    val now = System.currentTimeMillis()
    val list = mutableListOf<SocialCoupon>()

    // 1. Rıdvan Dilmen (Günün Süper Lig Derbi Bankosu - 19:00 TSİ)
    list.add(
      SocialCoupon(
        id = "sc_ridvan_dilmen",
        authorName = "Rıdvan Dilmen",
        authorTitle = "Usta Yorumcu & Baş Editör",
        authorAvatarEmoji = "⚽",
        title = "Günün Süper Lig & Derbi Bankosu",
        selections = listOf(
          SlipSelection("m_feat_1", "Galatasaray - Fenerbahçe", MarketType.MATCH_RESULT, "m1_ms1", "MS 1", 1.85, true),
          SlipSelection("m_feat_5", "Beşiktaş - Trabzonspor", MarketType.TOTAL_GOALS_25, "m5_ov25", "2.5 Üst", 1.72, true)
        ),
        totalOdds = 3.18,
        stake = 500,
        likeCount = 1420,
        copyCount = 856,
        isEditor = true,
        winRate = 84,
        matchStartTimeTs = now + 40 * 60 * 1000L,
        formattedKickoff = "19:00 TSİ",
        category = "Süper Lig"
      )
    )

    // 2. Güntekin Onay
    list.add(
      SocialCoupon(
        id = "sc_guntekin_onay",
        authorName = "Güntekin Onay",
        authorTitle = "Derbi & Taktik Uzmanı",
        authorAvatarEmoji = "🎙️",
        title = "Süper Lig Taktiksel Derbi Kombinesi",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.BOTH_TEAMS_SCORE, "m1_btts_yes", "KG Var", 1.58, true),
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.MATCH_RESULT, "m5_ms1", "MS 1", 2.05, true)
        ),
        totalOdds = 3.24,
        stake = 400,
        likeCount = 980,
        copyCount = 512,
        isEditor = true,
        winRate = 81,
        matchStartTimeTs = now + 40 * 60 * 1000L,
        formattedKickoff = "20:00 TSİ",
        category = "Süper Lig"
      )
    )

    // 3. Erman Toroğlu (SÜRESİ DOLMUŞ / KAPALI KUPON)
    list.add(
      SocialCoupon(
        id = "sc_erman_toroglu",
        authorName = "Erman Toroğlu",
        authorTitle = "Hakem & Disiplin Analisti",
        authorAvatarEmoji = "🟨",
        title = "Yüksek Tempolu Sert Karşılaşmalar Kuponu",
        selections = listOf(
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.TOTAL_GOALS_25, "m2_ov25", "2.5 Üst", 1.52, true),
          SlipSelection("m4", "Inter - Juventus", MarketType.TOTAL_GOALS_25, "m4_un25", "2.5 Alt", 1.78, true)
        ),
        totalOdds = 2.71,
        stake = 350,
        likeCount = 1120,
        copyCount = 640,
        isEditor = true,
        winRate = 79,
        matchStartTimeTs = now - 30 * 60 * 1000L, // Süresi 30 dk önce doldu
        formattedKickoff = "18:00 TSİ (KAPANDI)",
        category = "Avrupa"
      )
    )

    // 4. Uğur Meleke
    list.add(
      SocialCoupon(
        id = "sc_ugur_meleke",
        authorName = "Uğur Meleke",
        authorTitle = "Taktik & xG Veri Analisti",
        authorAvatarEmoji = "📊",
        title = "xG Değeri Tavan Yapan Avrupa Maçları",
        selections = listOf(
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.BOTH_TEAMS_SCORE, "m3_btts_yes", "KG Var", 1.62, true),
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.TOTAL_GOALS_25, "m7_ov25", "2.5 Üst", 1.45, true)
        ),
        totalOdds = 2.35,
        stake = 600,
        likeCount = 1650,
        copyCount = 940,
        isEditor = true,
        winRate = 86,
        matchStartTimeTs = now + 15 * 60 * 1000L,
        formattedKickoff = "19:30 TSİ",
        category = "Avrupa"
      )
    )

    // 5. Serdar Ali Çelikler
    list.add(
      SocialCoupon(
        id = "sc_serdar_ali",
        authorName = "Serdar Ali Çelikler",
        authorTitle = "Kırmızı Çizgi Başyazarı",
        authorAvatarEmoji = "🔥",
        title = "Kırmızı Çizgi Derbi Sürpriz Kuponu",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.MATCH_RESULT, "m1_msx", "MS X (Beraberlik)", 3.40, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.TOTAL_GOALS_25, "m8_ov25", "2.5 Üst", 1.65, true)
        ),
        totalOdds = 5.61,
        stake = 250,
        likeCount = 1890,
        copyCount = 1120,
        isEditor = true,
        winRate = 77,
        matchStartTimeTs = now + 2 * 60 * 1000L, // 2 dk kaldı (5 dk kuralı ile kapandı)
        formattedKickoff = "19:02 TSİ (5 Dk Kuralı)",
        category = "Süper Lig"
      )
    )

    // 6. Mehmet Demirkol
    list.add(
      SocialCoupon(
        id = "sc_mehmet_demirkol",
        authorName = "Mehmet Demirkol",
        authorTitle = "Sokrates & Spor Yazarı",
        authorAvatarEmoji = "✍️",
        title = "Organize Hücumlar & Gol Kuponu",
        selections = listOf(
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.BOTH_TEAMS_SCORE, "m2_btts_yes", "KG Var", 1.45, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.MATCH_RESULT, "m8_ms1", "MS 1", 1.75, true)
        ),
        totalOdds = 2.54,
        stake = 500,
        likeCount = 1340,
        copyCount = 760,
        isEditor = true,
        winRate = 83,
        matchStartTimeTs = now + 75 * 60 * 1000L,
        formattedKickoff = "21:00 TSİ",
        category = "Avrupa"
      )
    )

    // 7. Ali Ece
    list.add(
      SocialCoupon(
        id = "sc_ali_ece",
        authorName = "Ali Ece",
        authorTitle = "Premier Lig & Rock'n Roll Futbol",
        authorAvatarEmoji = "🎸",
        title = "Premier Lig Tutkusu & Gol Festivali",
        selections = listOf(
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.TOTAL_GOALS_25, "m3_ov25", "2.5 Üst", 1.70, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.BOTH_TEAMS_SCORE, "m8_btts_yes", "KG Var", 1.55, true)
        ),
        totalOdds = 2.64,
        stake = 450,
        likeCount = 1580,
        copyCount = 890,
        isEditor = true,
        winRate = 80,
        matchStartTimeTs = now + 35 * 60 * 1000L,
        formattedKickoff = "19:45 TSİ",
        category = "Avrupa"
      )
    )

    // 8. Ilgaz Çınar
    list.add(
      SocialCoupon(
        id = "sc_ilgaz_cinar",
        authorName = "Ilgaz Çınar",
        authorTitle = "Scout & Genç Yetenek Analizi",
        authorAvatarEmoji = "🔍",
        title = "Scout Gözüyle Süper Lig Kombinesi",
        selections = listOf(
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.MATCH_RESULT, "m5_ms1", "MS 1", 2.05, true),
          SlipSelection("m9", "Atletico Madrid - Sevilla", MarketType.MATCH_RESULT, "m9_ms1", "MS 1", 1.60, true)
        ),
        totalOdds = 3.28,
        stake = 300,
        likeCount = 870,
        copyCount = 490,
        isEditor = true,
        winRate = 82,
        matchStartTimeTs = now + 90 * 60 * 1000L,
        formattedKickoff = "21:15 TSİ",
        category = "Süper Lig"
      )
    )

    // 9. Tümer Metin
    list.add(
      SocialCoupon(
        id = "sc_tumer_metin",
        authorName = "Tümer Metin",
        authorTitle = "Eski Milli Futbolcu",
        authorAvatarEmoji = "⭐",
        title = "Yıldız Oyuncular & Bitiricilik Kuponu",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.TOTAL_GOALS_25, "m1_ov25", "2.5 Üst", 1.72, true),
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.MATCH_RESULT, "m7_ms1", "MS 1", 1.55, true)
        ),
        totalOdds = 2.67,
        stake = 350,
        likeCount = 760,
        copyCount = 420,
        isEditor = true,
        winRate = 78,
        matchStartTimeTs = now + 4 * 60 * 1000L, // 4 dk kaldı -> Bahis Kapandı testi!
        formattedKickoff = "19:15 TSİ (KAPANDI)",
        category = "Süper Lig"
      )
    )

    // 10. Nihat Kahveci
    list.add(
      SocialCoupon(
        id = "sc_nihat_kahveci",
        authorName = "Nihat Kahveci",
        authorTitle = "La Liga & Süper Lig Gurmesi",
        authorAvatarEmoji = "🎯",
        title = "İspanya ve Türkiye Özel İkili",
        selections = listOf(
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.MATCH_RESULT, "m2_ms1", "MS 1", 2.15, true),
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.BOTH_TEAMS_SCORE, "m1_btts_yes", "KG Var", 1.58, true)
        ),
        totalOdds = 3.40,
        stake = 500,
        likeCount = 2100,
        copyCount = 1350,
        isEditor = true,
        winRate = 85,
        matchStartTimeTs = now + 65 * 60 * 1000L,
        formattedKickoff = "20:45 TSİ",
        category = "Avrupa"
      )
    )

    // 11. Sinan Engin
    list.add(
      SocialCoupon(
        id = "sc_sinan_engin",
        authorName = "Sinan Engin",
        authorTitle = "Beyaz Futbol Duayeni",
        authorAvatarEmoji = "🦅",
        title = "Beşiktaş & Süper Lig Ağır Toplar Kuponu",
        selections = listOf(
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.MATCH_RESULT, "m5_ms1", "MS 1", 2.05, true),
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.TOTAL_GOALS_25, "m1_ov25", "2.5 Üst", 1.72, true)
        ),
        totalOdds = 3.53,
        stake = 400,
        likeCount = 1450,
        copyCount = 890,
        isEditor = true,
        winRate = 76,
        matchStartTimeTs = now + 80 * 60 * 1000L,
        formattedKickoff = "21:00 TSİ",
        category = "Süper Lig"
      )
    )

    // 12. Ahmet Çakar
    list.add(
      SocialCoupon(
        id = "sc_ahmet_cakar",
        authorName = "Ahmet Çakar",
        authorTitle = "Eski FIFA Hakemi & Yorumcu",
        authorAvatarEmoji = "🟥",
        title = "Tansiyonu Yüksek Maçlar Tahmini",
        selections = listOf(
          SlipSelection("m4", "Inter - Juventus", MarketType.MATCH_RESULT, "m4_ms1", "MS 1", 2.10, true),
          SlipSelection("m9", "Atletico Madrid - Sevilla", MarketType.TOTAL_GOALS_25, "m9_un25", "2.5 Alt", 1.82, true)
        ),
        totalOdds = 3.82,
        stake = 300,
        likeCount = 990,
        copyCount = 520,
        isEditor = true,
        winRate = 75,
        matchStartTimeTs = now + 55 * 60 * 1000L,
        formattedKickoff = "20:30 TSİ",
        category = "Avrupa"
      )
    )

    // 13. Önder Özen
    list.add(
      SocialCoupon(
        id = "sc_onder_ozen",
        authorName = "Önder Özen",
        authorTitle = "Teknik Direktör Gözüyle",
        authorAvatarEmoji = "📋",
        title = "Taktik Disiplin & Düşük Skor Kombini",
        selections = listOf(
          SlipSelection("m4", "Inter - Juventus", MarketType.TOTAL_GOALS_25, "m4_un25", "2.5 Alt", 1.78, true),
          SlipSelection("m9", "Atletico Madrid - Sevilla", MarketType.MATCH_RESULT, "m9_ms1", "MS 1", 1.60, true)
        ),
        totalOdds = 2.85,
        stake = 500,
        likeCount = 1240,
        copyCount = 730,
        isEditor = true,
        winRate = 84,
        matchStartTimeTs = now + 45 * 60 * 1000L,
        formattedKickoff = "20:15 TSİ",
        category = "Avrupa"
      )
    )

    // 14. Metin Tekin
    list.add(
      SocialCoupon(
        id = "sc_metin_tekin",
        authorName = "Metin Tekin",
        authorTitle = "Sarı Fırtına & Usta Analist",
        authorAvatarEmoji = "⚡",
        title = "Kanat Akınları & Gol Kombinesi",
        selections = listOf(
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.TOTAL_GOALS_25, "m8_ov25", "2.5 Üst", 1.65, true),
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.BOTH_TEAMS_SCORE, "m3_btts_yes", "KG Var", 1.62, true)
        ),
        totalOdds = 2.67,
        stake = 450,
        likeCount = 1180,
        copyCount = 680,
        isEditor = true,
        winRate = 80,
        matchStartTimeTs = now + 30 * 60 * 1000L,
        formattedKickoff = "19:45 TSİ",
        category = "Avrupa"
      )
    )

    // 15. Eray Erollu
    list.add(
      SocialCoupon(
        id = "sc_eray_erollu",
        authorName = "Eray Erollu",
        authorTitle = "İddaa Bülteni Başyazarı",
        authorAvatarEmoji = "💰",
        title = "Günün İddaa Bülten Kasası",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.MATCH_RESULT, "m1_ms1", "MS 1", 1.85, true),
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.TOTAL_GOALS_25, "m7_ov25", "2.5 Üst", 1.45, true)
        ),
        totalOdds = 2.68,
        stake = 1000,
        likeCount = 3100,
        copyCount = 2450,
        isEditor = true,
        winRate = 87,
        matchStartTimeTs = now + 40 * 60 * 1000L,
        formattedKickoff = "20:00 TSİ",
        category = "Popüler"
      )
    )

    // 16. Barış Ertül
    list.add(
      SocialCoupon(
        id = "sc_baris_ertul",
        authorName = "Barış Ertül",
        authorTitle = "Radyospor & Oran Avcısı",
        authorAvatarEmoji = "📻",
        title = "Değerli Oranlar Banko Kuponu",
        selections = listOf(
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.BOTH_TEAMS_SCORE, "m2_btts_yes", "KG Var", 1.45, true),
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.BOTH_TEAMS_SCORE, "m5_btts_yes", "KG Var", 1.68, true)
        ),
        totalOdds = 2.44,
        stake = 750,
        likeCount = 2280,
        copyCount = 1620,
        isEditor = true,
        winRate = 85,
        matchStartTimeTs = now + 50 * 60 * 1000L,
        formattedKickoff = "20:15 TSİ",
        category = "Popüler"
      )
    )

    // 17. Genco Boran
    list.add(
      SocialCoupon(
        id = "sc_genco_boran",
        authorName = "Genco Boran",
        authorTitle = "Banko & Sistem Kuponları",
        authorAvatarEmoji = "📑",
        title = "Sistem & Yüksek Kazançlı İkili",
        selections = listOf(
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.MATCH_RESULT, "m3_msx", "MS X", 3.25, true),
          SlipSelection("m4", "Inter - Juventus", MarketType.MATCH_RESULT, "m4_ms1", "MS 1", 2.10, true)
        ),
        totalOdds = 6.83,
        stake = 200,
        likeCount = 1420,
        copyCount = 890,
        isEditor = true,
        winRate = 83,
        matchStartTimeTs = now + 85 * 60 * 1000L,
        formattedKickoff = "21:00 TSİ",
        category = "Avrupa"
      )
    )

    // 18. İsmail Şenol
    list.add(
      SocialCoupon(
        id = "sc_ismail_senol",
        authorName = "İsmail Şenol",
        authorTitle = "EuroLeague Baş Spikeri",
        authorAvatarEmoji = "🏀",
        title = "EuroLeague Özel Sayı & Galibiyet Kuponu",
        selections = listOf(
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true),
          SlipSelection("m11", "Real Madrid - Anadolu Efes", MarketType.MATCH_RESULT, "m11_ms1", "MS 1", 1.65, true)
        ),
        totalOdds = 2.77,
        stake = 600,
        likeCount = 1950,
        copyCount = 1200,
        isEditor = true,
        winRate = 89,
        matchStartTimeTs = now + 60 * 60 * 1000L,
        formattedKickoff = "20:30 TSİ",
        category = "EuroLeague"
      )
    )

    // 19. İhsan Bayülken
    list.add(
      SocialCoupon(
        id = "sc_ihsan_bayulken",
        authorName = "İhsan Bayülken",
        authorTitle = "EuroLeague Koçu & Yorumcu",
        authorAvatarEmoji = "⛹️",
        title = "Pota Altı & Taktik Savunma Kuponu",
        selections = listOf(
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.TOTAL_GOALS_25, "m6_un", "162.5 Alt", 1.82, true),
          SlipSelection("m11", "Real Madrid - Anadolu Efes", MarketType.TOTAL_GOALS_25, "m11_ov", "165.5 Üst", 1.78, true)
        ),
        totalOdds = 3.24,
        stake = 500,
        likeCount = 1320,
        copyCount = 810,
        isEditor = true,
        winRate = 87,
        matchStartTimeTs = now + 90 * 60 * 1000L,
        formattedKickoff = "21:15 TSİ",
        category = "EuroLeague"
      )
    )

    // 20. Kaan Kural
    list.add(
      SocialCoupon(
        id = "sc_kaan_kural",
        authorName = "Kaan Kural",
        authorTitle = "NBA & Basketbol Gurusu",
        authorAvatarEmoji = "👓",
        title = "Modern Basketbol İstatistik Kombinesi",
        selections = listOf(
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true),
          SlipSelection("m10", "Jannik Sinner - Carlos Alcaraz", MarketType.MATCH_RESULT, "m10_ms1", "MS 1", 1.82, true)
        ),
        totalOdds = 3.06,
        stake = 700,
        likeCount = 2650,
        copyCount = 1840,
        isEditor = true,
        winRate = 88,
        matchStartTimeTs = now + 20 * 60 * 1000L,
        formattedKickoff = "19:35 TSİ",
        category = "EuroLeague"
      )
    )

    // 21. Şevket Furkan Erbay
    list.add(
      SocialCoupon(
        id = "sc_furkan_erbay",
        authorName = "Şevket Furkan Erbay",
        authorTitle = "Tenis & Grand Slam Başyazarı",
        authorAvatarEmoji = "🎾",
        title = "ATP Dünya Turu Final Setleri Bankosu",
        selections = listOf(
          SlipSelection("m10", "Jannik Sinner - Carlos Alcaraz", MarketType.MATCH_RESULT, "m10_ms1", "MS 1", 1.82, true),
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.TOTAL_GOALS_25, "m2_ov25", "2.5 Üst", 1.52, true)
        ),
        totalOdds = 2.77,
        stake = 550,
        likeCount = 1430,
        copyCount = 920,
        isEditor = true,
        winRate = 88,
        matchStartTimeTs = now + 35 * 60 * 1000L,
        formattedKickoff = "19:50 TSİ",
        category = "Tenis"
      )
    )

    // 22. Emre Yazıcılar
    list.add(
      SocialCoupon(
        id = "sc_emre_yazicilar",
        authorName = "Emre Yazıcılar",
        authorTitle = "Kort & Servis Kırma Analisti",
        authorAvatarEmoji = "🏆",
        title = "Torino ATP Sert Kort Özel İkili",
        selections = listOf(
          SlipSelection("m10", "Jannik Sinner - Carlos Alcaraz", MarketType.BOTH_TEAMS_SCORE, "m10_tiebreak", "Tie-Break Olur", 1.70, true),
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true)
        ),
        totalOdds = 2.86,
        stake = 400,
        likeCount = 890,
        copyCount = 540,
        isEditor = true,
        winRate = 86,
        matchStartTimeTs = now + 45 * 60 * 1000L,
        formattedKickoff = "20:05 TSİ",
        category = "Tenis"
      )
    )

    // 23. MonteCarlo_AI_Bot
    list.add(
      SocialCoupon(
        id = "sc_monte_carlo_ai",
        authorName = "MonteCarlo_AI_Bot",
        authorTitle = "100.000 Maç Simülasyon Motoru",
        authorAvatarEmoji = "🤖",
        title = "100.000 Simülasyonda %91 Başarı xG Kuponu",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.TOTAL_GOALS_25, "m1_ov25", "2.5 Üst", 1.72, true),
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.TOTAL_GOALS_25, "m2_ov25", "2.5 Üst", 1.52, true),
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.TOTAL_GOALS_25, "m7_ov25", "2.5 Üst", 1.45, true)
        ),
        totalOdds = 3.79,
        stake = 800,
        likeCount = 3890,
        copyCount = 2810,
        isEditor = true,
        winRate = 91,
        matchStartTimeTs = now + 30 * 60 * 1000L,
        formattedKickoff = "19:45 TSİ",
        category = "AI Botları"
      )
    )

    // 24. DeepStats_xG_Engine
    list.add(
      SocialCoupon(
        id = "sc_deepstats_xg",
        authorName = "DeepStats_xG_Engine",
        authorTitle = "Derin Öğrenme Beklenen Gol Modeli",
        authorAvatarEmoji = "🧠",
        title = "Derin Öğrenme xG Pozitif Sapma Kuponu",
        selections = listOf(
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.BOTH_TEAMS_SCORE, "m3_btts_yes", "KG Var", 1.62, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.MATCH_RESULT, "m8_ms1", "MS 1", 1.75, true)
        ),
        totalOdds = 2.84,
        stake = 650,
        likeCount = 2410,
        copyCount = 1760,
        isEditor = true,
        winRate = 90,
        matchStartTimeTs = now + 40 * 60 * 1000L,
        formattedKickoff = "20:00 TSİ",
        category = "AI Botları"
      )
    )

    // 25. Opta_Neural_Predictor
    list.add(
      SocialCoupon(
        id = "sc_opta_neural",
        authorName = "Opta_Neural_Predictor",
        authorTitle = "Büyük Veri & Form Matrisi",
        authorAvatarEmoji = "🌐",
        title = "Opta Matrisi: En Yüksek Güvenilirlik Kombini",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.MATCH_RESULT, "m1_ms1", "MS 1", 1.85, true),
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true)
        ),
        totalOdds = 3.11,
        stake = 900,
        likeCount = 3120,
        copyCount = 2240,
        isEditor = true,
        winRate = 88,
        matchStartTimeTs = now + 25 * 60 * 1000L,
        formattedKickoff = "19:40 TSİ",
        category = "AI Botları"
      )
    )

    // 26. Derbi_Matrix_Bot
    list.add(
      SocialCoupon(
        id = "sc_derbi_matrix",
        authorName = "Derbi_Matrix_Bot",
        authorTitle = "Derbi & Büyük Maç Olasılık Motoru",
        authorAvatarEmoji = "⚡",
        title = "Süper Lig & El Clasico Özel AI Kombinesi",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.BOTH_TEAMS_SCORE, "m1_btts_yes", "KG Var", 1.58, true),
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.BOTH_TEAMS_SCORE, "m2_btts_yes", "KG Var", 1.45, true)
        ),
        totalOdds = 2.29,
        stake = 1000,
        likeCount = 2950,
        copyCount = 2100,
        isEditor = true,
        winRate = 89,
        matchStartTimeTs = now + 22 * 60 * 1000L,
        formattedKickoff = "19:35 TSİ",
        category = "AI Botları"
      )
    )

    // 27. ValueOdd_Scanner
    list.add(
      SocialCoupon(
        id = "sc_value_odd",
        authorName = "ValueOdd_Scanner",
        authorTitle = "Değerli Oran & Arbitraj Dedektörü",
        authorAvatarEmoji = "📈",
        title = "Piyasa Hatalı Oran Dedektör Kuponu",
        selections = listOf(
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.MATCH_RESULT, "m5_ms1", "MS 1", 2.05, true),
          SlipSelection("m4", "Inter - Juventus", MarketType.TOTAL_GOALS_25, "m4_un25", "2.5 Alt", 1.78, true)
        ),
        totalOdds = 3.65,
        stake = 500,
        likeCount = 1870,
        copyCount = 1340,
        isEditor = true,
        winRate = 86,
        matchStartTimeTs = now + 50 * 60 * 1000L,
        formattedKickoff = "20:10 TSİ",
        category = "AI Botları"
      )
    )

    // 28. Ozan German
    list.add(
      SocialCoupon(
        id = "sc_ozan_german",
        authorName = "Ozan German",
        authorTitle = "Basketbol Tahmin Uzmanı",
        authorAvatarEmoji = "⛹️‍♂️",
        title = "EuroLeague Şampiyonluk Adayları Kuponu",
        selections = listOf(
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true),
          SlipSelection("m11", "Real Madrid - Anadolu Efes", MarketType.MATCH_RESULT, "m11_ms1", "MS 1", 1.65, true)
        ),
        totalOdds = 2.77,
        stake = 400,
        likeCount = 1100,
        copyCount = 720,
        isEditor = true,
        winRate = 86,
        matchStartTimeTs = now + 70 * 60 * 1000L,
        formattedKickoff = "20:30 TSİ",
        category = "EuroLeague"
      )
    )

    // 29. Fethi Aytuna
    list.add(
      SocialCoupon(
        id = "sc_fethi_aytuna",
        authorName = "Fethi Aytuna",
        authorTitle = "İtalya Serie A & Avrupa Uzmanı",
        authorAvatarEmoji = "🇮🇹",
        title = "Serie A & La Liga Klasikleri",
        selections = listOf(
          SlipSelection("m4", "Inter - Juventus", MarketType.MATCH_RESULT, "m4_ms1", "MS 1", 2.10, true),
          SlipSelection("m9", "Atletico Madrid - Sevilla", MarketType.TOTAL_GOALS_25, "m9_un25", "2.5 Alt", 1.82, true)
        ),
        totalOdds = 3.82,
        stake = 300,
        likeCount = 760,
        copyCount = 480,
        isEditor = true,
        winRate = 81,
        matchStartTimeTs = now + 60 * 60 * 1000L,
        formattedKickoff = "20:20 TSİ",
        category = "Avrupa"
      )
    )

    // 30. Murat Fevzi Tanırlı
    list.add(
      SocialCoupon(
        id = "sc_murat_fevzi",
        authorName = "Murat Fevzi Tanırlı",
        authorTitle = "Hakem & Kart İstatistikleri",
        authorAvatarEmoji = "📏",
        title = "Derbi Disiplin & Gol Bahisleri",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.BOTH_TEAMS_SCORE, "m1_btts_yes", "KG Var", 1.58, true),
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.TOTAL_GOALS_25, "m7_ov25", "2.5 Üst", 1.45, true)
        ),
        totalOdds = 2.29,
        stake = 600,
        likeCount = 1430,
        copyCount = 980,
        isEditor = true,
        winRate = 84,
        matchStartTimeTs = now + 30 * 60 * 1000L,
        formattedKickoff = "19:45 TSİ",
        category = "Süper Lig"
      )
    )

    // 31. Berkuk Ünyay
    list.add(
      SocialCoupon(
        id = "sc_berkuk_unyay",
        authorName = "Berkuk Ünyay",
        authorTitle = "İngiltere Premier Lig Gurmesi",
        authorAvatarEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
        title = "Ada Futbolu Zirve Mücadelesi",
        selections = listOf(
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.MATCH_RESULT, "m3_ms1", "MS 1", 2.45, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.TOTAL_GOALS_25, "m8_ov25", "2.5 Üst", 1.65, true)
        ),
        totalOdds = 4.04,
        stake = 350,
        likeCount = 1120,
        copyCount = 670,
        isEditor = true,
        winRate = 82,
        matchStartTimeTs = now + 45 * 60 * 1000L,
        formattedKickoff = "20:00 TSİ",
        category = "Avrupa"
      )
    )

    // 32. Senih Yurga
    list.add(
      SocialCoupon(
        id = "sc_senih_yurga",
        authorName = "Senih Yurga",
        authorTitle = "Gol Bahisleri & 2.5 Üst Uzmanı",
        authorAvatarEmoji = "⚽",
        title = "Günün 2.5 Üst Gol Yağmuru Kuponu",
        selections = listOf(
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.TOTAL_GOALS_25, "m2_ov25", "2.5 Üst", 1.52, true),
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.TOTAL_GOALS_25, "m7_ov25", "2.5 Üst", 1.45, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.TOTAL_GOALS_25, "m8_ov25", "2.5 Üst", 1.65, true)
        ),
        totalOdds = 3.64,
        stake = 700,
        likeCount = 2890,
        copyCount = 2050,
        isEditor = true,
        winRate = 85,
        matchStartTimeTs = now + 35 * 60 * 1000L,
        formattedKickoff = "19:50 TSİ",
        category = "Popüler"
      )
    )

    // 33. Kerim Can Özdal
    list.add(
      SocialCoupon(
        id = "sc_kerim_can",
        authorName = "Kerim Can Özdal",
        authorTitle = "Fransa & Şampiyonlar Ligi Uzmanı",
        authorAvatarEmoji = "🇫🇷",
        title = "Avrupa Kupaları Form Kombinesi",
        selections = listOf(
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.BOTH_TEAMS_SCORE, "m3_btts_yes", "KG Var", 1.62, true),
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.MATCH_RESULT, "m2_ms1", "MS 1", 2.15, true)
        ),
        totalOdds = 3.48,
        stake = 400,
        likeCount = 890,
        copyCount = 570,
        isEditor = true,
        winRate = 80,
        matchStartTimeTs = now + 50 * 60 * 1000L,
        formattedKickoff = "20:10 TSİ",
        category = "Avrupa"
      )
    )

    // 34. Burak Şen
    list.add(
      SocialCoupon(
        id = "sc_burak_sen",
        authorName = "Burak Şen",
        authorTitle = "İlk Yarı & Karşılıklı Gol Uzmanı",
        authorAvatarEmoji = "⏱️",
        title = "Hızlı Başlayan Maçlar & İY Gol Kombini",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.BOTH_TEAMS_SCORE, "m1_btts_yes", "KG Var", 1.58, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.BOTH_TEAMS_SCORE, "m8_btts_yes", "KG Var", 1.55, true)
        ),
        totalOdds = 2.45,
        stake = 600,
        likeCount = 1670,
        copyCount = 1140,
        isEditor = true,
        winRate = 83,
        matchStartTimeTs = now + 20 * 60 * 1000L,
        formattedKickoff = "19:35 TSİ",
        category = "Süper Lig"
      )
    )

    // 35. Murat Murathanoğlu
    list.add(
      SocialCoupon(
        id = "sc_murat_murathanoglu",
        authorName = "Murat Murathanoğlu",
        authorTitle = "Efsane Basketbol Spikeri",
        authorAvatarEmoji = "🏀",
        title = "EuroLeague Klasikleri & Parke Heyecanı",
        selections = listOf(
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true),
          SlipSelection("m11", "Real Madrid - Anadolu Efes", MarketType.TOTAL_GOALS_25, "m11_ov", "165.5 Üst", 1.78, true)
        ),
        totalOdds = 2.99,
        stake = 500,
        likeCount = 1380,
        copyCount = 890,
        isEditor = true,
        winRate = 84,
        matchStartTimeTs = now + 40 * 60 * 1000L,
        formattedKickoff = "20:00 TSİ",
        category = "EuroLeague"
      )
    )

    // 36. SureBet_AI
    list.add(
      SocialCoupon(
        id = "sc_surebet_ai",
        authorName = "SureBet_AI",
        authorTitle = "Düşük Riskli Banko Seçici",
        authorAvatarEmoji = "🛡️",
        title = "Kasa Katlama Garantili 2'li Banko",
        selections = listOf(
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.TOTAL_GOALS_25, "m7_ov25", "2.5 Üst", 1.45, true),
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.TOTAL_GOALS_25, "m2_ov25", "2.5 Üst", 1.52, true)
        ),
        totalOdds = 2.20,
        stake = 1500,
        likeCount = 4200,
        copyCount = 3450,
        isEditor = true,
        winRate = 92,
        matchStartTimeTs = now + 30 * 60 * 1000L,
        formattedKickoff = "19:45 TSİ",
        category = "Popüler"
      )
    )

    // 37. Gol_Makinesi_AI
    list.add(
      SocialCoupon(
        id = "sc_gol_makinesi_ai",
        authorName = "Gol_Makinesi_AI",
        authorTitle = "KG Var & Üst Algoritması",
        authorAvatarEmoji = "⚽",
        title = "Karşılıklı Gol Var xG %89 İkili",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.BOTH_TEAMS_SCORE, "m1_btts_yes", "KG Var", 1.58, true),
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.BOTH_TEAMS_SCORE, "m3_btts_yes", "KG Var", 1.62, true)
        ),
        totalOdds = 2.56,
        stake = 800,
        likeCount = 2950,
        copyCount = 2120,
        isEditor = true,
        winRate = 88,
        matchStartTimeTs = now + 35 * 60 * 1000L,
        formattedKickoff = "19:50 TSİ",
        category = "AI Botları"
      )
    )

    // 38. Orkun Çolakoğlu
    list.add(
      SocialCoupon(
        id = "sc_orkun_colakoglu",
        authorName = "Orkun Çolakoğlu",
        authorTitle = "Basketbol İstatistik & Tempo Analisti",
        authorAvatarEmoji = "📊",
        title = "Yüksek Tempolu EuroLeague Maçları",
        selections = listOf(
          SlipSelection("m11", "Real Madrid - Anadolu Efes", MarketType.MATCH_RESULT, "m11_ms1", "MS 1", 1.65, true),
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true)
        ),
        totalOdds = 2.77,
        stake = 450,
        likeCount = 980,
        copyCount = 610,
        isEditor = true,
        winRate = 83,
        matchStartTimeTs = now + 55 * 60 * 1000L,
        formattedKickoff = "20:15 TSİ",
        category = "EuroLeague"
      )
    )

    // 39. Can İşbakan
    list.add(
      SocialCoupon(
        id = "sc_can_isbakan",
        authorName = "Can İşbakan",
        authorTitle = "EuroLeague & TBL Analisti",
        authorAvatarEmoji = "⛹️",
        title = "EuroLeague Çift Maç Haftası Kombini",
        selections = listOf(
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true),
          SlipSelection("m10", "Jannik Sinner - Carlos Alcaraz", MarketType.MATCH_RESULT, "m10_ms1", "MS 1", 1.82, true)
        ),
        totalOdds = 3.06,
        stake = 500,
        likeCount = 890,
        copyCount = 570,
        isEditor = true,
        winRate = 82,
        matchStartTimeTs = now + 45 * 60 * 1000L,
        formattedKickoff = "20:00 TSİ",
        category = "EuroLeague"
      )
    )

    // 40. Erman Kunter
    list.add(
      SocialCoupon(
        id = "sc_erman_kunter",
        authorName = "Erman Kunter",
        authorTitle = "Eski Milli Başantrenör",
        authorAvatarEmoji = "🎖️",
        title = "Disiplinli Savunmalar & Alt Tercihleri",
        selections = listOf(
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.TOTAL_GOALS_25, "m6_un", "162.5 Alt", 1.82, true),
          SlipSelection("m4", "Inter - Juventus", MarketType.TOTAL_GOALS_25, "m4_un25", "2.5 Alt", 1.78, true)
        ),
        totalOdds = 3.24,
        stake = 400,
        likeCount = 750,
        copyCount = 480,
        isEditor = true,
        winRate = 85,
        matchStartTimeTs = now + 50 * 60 * 1000L,
        formattedKickoff = "20:10 TSİ",
        category = "EuroLeague"
      )
    )

    // 41. Kortun Efendisi
    list.add(
      SocialCoupon(
        id = "sc_kortun_efendisi",
        authorName = "Kortun Efendisi",
        authorTitle = "Tenis Canlı Algoritması",
        authorAvatarEmoji = "🎾",
        title = "ATP Turin Zirvesi Özel Teklisi",
        selections = listOf(
          SlipSelection("m10", "Jannik Sinner - Carlos Alcaraz", MarketType.MATCH_RESULT, "m10_ms1", "MS 1", 1.82, true)
        ),
        totalOdds = 1.82,
        stake = 1200,
        likeCount = 1890,
        copyCount = 1420,
        isEditor = true,
        winRate = 84,
        matchStartTimeTs = now + 25 * 60 * 1000L,
        formattedKickoff = "19:40 TSİ",
        category = "Tenis"
      )
    )

    // 42. Tenis_Set_Matriksi
    list.add(
      SocialCoupon(
        id = "sc_tenis_matriks",
        authorName = "Tenis_Set_Matriksi",
        authorTitle = "Tie-Break & Set Skorları Uzmanı",
        authorAvatarEmoji = "🎾",
        title = "Büyük Maç Uzun Ralliler Kuponu",
        selections = listOf(
          SlipSelection("m10", "Jannik Sinner - Carlos Alcaraz", MarketType.TOTAL_GOALS_25, "m10_sets", "2.5 Set Üst", 2.10, true),
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.TOTAL_GOALS_25, "m7_ov25", "2.5 Üst", 1.45, true)
        ),
        totalOdds = 3.05,
        stake = 600,
        likeCount = 1250,
        copyCount = 840,
        isEditor = true,
        winRate = 86,
        matchStartTimeTs = now + 40 * 60 * 1000L,
        formattedKickoff = "19:55 TSİ",
        category = "Tenis"
      )
    )

    // 43. Şampiyonlar_Ligi_Botu
    list.add(
      SocialCoupon(
        id = "sc_ucl_bot",
        authorName = "Şampiyonlar_Ligi_Botu",
        authorTitle = "UEFA İstatistik & Kadro Analisti",
        authorAvatarEmoji = "🌟",
        title = "Devler Ligi Yıldızlar Karması Kuponu",
        selections = listOf(
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.MATCH_RESULT, "m2_ms1", "MS 1", 2.15, true),
          SlipSelection("m3", "Arsenal - Manchester City", MarketType.TOTAL_GOALS_25, "m3_ov25", "2.5 Üst", 1.70, true)
        ),
        totalOdds = 3.66,
        stake = 750,
        likeCount = 2840,
        copyCount = 1960,
        isEditor = true,
        winRate = 89,
        matchStartTimeTs = now + 35 * 60 * 1000L,
        formattedKickoff = "19:50 TSİ",
        category = "AI Botları"
      )
    )

    // 44. Canlı_Momentum_AI
    list.add(
      SocialCoupon(
        id = "sc_canli_momentum_ai",
        authorName = "Canlı_Momentum_AI",
        authorTitle = "Canlı Baskı İndeksi & Anlık Oranlar",
        authorAvatarEmoji = "⚡",
        title = "Hücum Baskısı %70 Üstü Maçlar Kuponu",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.TOTAL_GOALS_25, "m1_ov25", "2.5 Üst", 1.72, true),
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.BOTH_TEAMS_SCORE, "m5_btts_yes", "KG Var", 1.68, true)
        ),
        totalOdds = 2.89,
        stake = 850,
        likeCount = 3150,
        copyCount = 2380,
        isEditor = true,
        winRate = 90,
        matchStartTimeTs = now + 28 * 60 * 1000L,
        formattedKickoff = "19:45 TSİ",
        category = "AI Botları"
      )
    )

    // 45. EuroLeague_Guru_AI
    list.add(
      SocialCoupon(
        id = "sc_euroleague_guru",
        authorName = "EuroLeague_Guru_AI",
        authorTitle = "Basketbol Sayı Baremi Tahmincisi",
        authorAvatarEmoji = "🤖",
        title = "EuroLeague Çift Haneli Fark Kombinesi",
        selections = listOf(
          SlipSelection("m6", "Fenerbahçe Beko - Panathinaikos", MarketType.MATCH_RESULT, "m6_ms1", "MS 1", 1.68, true),
          SlipSelection("m11", "Real Madrid - Anadolu Efes", MarketType.MATCH_RESULT, "m11_ms1", "MS 1", 1.65, true)
        ),
        totalOdds = 2.77,
        stake = 500,
        likeCount = 1840,
        copyCount = 1250,
        isEditor = true,
        winRate = 87,
        matchStartTimeTs = now + 42 * 60 * 1000L,
        formattedKickoff = "20:00 TSİ",
        category = "EuroLeague"
      )
    )

    // 46. İlkYarı_Dehası
    list.add(
      SocialCoupon(
        id = "sc_ilkyari_dehasi",
        authorName = "İlkYarı_Dehası",
        authorTitle = "İlk Yarı Sonucu & İY 0.5 Üst Uzmanı",
        authorAvatarEmoji = "⏱️",
        title = "İlk 45 Dakikada Gol Olur Kombini",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.TOTAL_GOALS_25, "m1_iy_gol", "İY 0.5 Üst", 1.38, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.TOTAL_GOALS_25, "m8_iy_gol", "İY 0.5 Üst", 1.35, true)
        ),
        totalOdds = 1.86,
        stake = 1200,
        likeCount = 1920,
        copyCount = 1480,
        isEditor = true,
        winRate = 83,
        matchStartTimeTs = now + 24 * 60 * 1000L,
        formattedKickoff = "19:40 TSİ",
        category = "Popüler"
      )
    )

    // 47. Skor_Tahmincisi_Bot
    list.add(
      SocialCoupon(
        id = "sc_skor_tahmincisi",
        authorName = "Skor_Tahmincisi_Bot",
        authorTitle = "Poisson Dağılımlı Skor Modeli",
        authorAvatarEmoji = "🎯",
        title = "Yüksek Oranlı 2 Skor Tahmini",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.MATCH_RESULT, "m1_ms1", "MS 1", 1.85, true),
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.MATCH_RESULT, "m5_ms1", "MS 1", 2.05, true)
        ),
        totalOdds = 3.79,
        stake = 400,
        likeCount = 1640,
        copyCount = 980,
        isEditor = true,
        winRate = 85,
        matchStartTimeTs = now + 38 * 60 * 1000L,
        formattedKickoff = "19:55 TSİ",
        category = "AI Botları"
      )
    )

    // 48. Süper_Lig_Panteri
    list.add(
      SocialCoupon(
        id = "sc_super_lig_panteri",
        authorName = "Süper_Lig_Panteri",
        authorTitle = "Anadolu Takımları Sürpriz Avcısı",
        authorAvatarEmoji = "🐆",
        title = "Süper Lig Sürpriz & Değer Kuponu",
        selections = listOf(
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.BOTH_TEAMS_SCORE, "m5_btts_yes", "KG Var", 1.68, true),
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.TOTAL_GOALS_25, "m1_ov25", "2.5 Üst", 1.72, true)
        ),
        totalOdds = 2.89,
        stake = 450,
        likeCount = 1140,
        copyCount = 760,
        isEditor = true,
        winRate = 79,
        matchStartTimeTs = now + 32 * 60 * 1000L,
        formattedKickoff = "19:48 TSİ",
        category = "Süper Lig"
      )
    )

    // 49. Korner_Kart_Avcısı
    list.add(
      SocialCoupon(
        id = "sc_korner_kart",
        authorName = "Korner_Kart_Avcısı",
        authorTitle = "Maç İçi Dinamik İstatistik Uzmanı",
        authorAvatarEmoji = "🚩",
        title = "Korner & Kart Dinamikleri Bankosu",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.TOTAL_GOALS_25, "m1_corn_ov", "9.5 Korner Üst", 1.65, true),
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.TOTAL_GOALS_25, "m2_corn_ov", "9.5 Korner Üst", 1.62, true)
        ),
        totalOdds = 2.67,
        stake = 500,
        likeCount = 1380,
        copyCount = 890,
        isEditor = true,
        winRate = 81,
        matchStartTimeTs = now + 44 * 60 * 1000L,
        formattedKickoff = "20:00 TSİ",
        category = "Avrupa"
      )
    )

    // 50. Ceyla Büyükuzun
    list.add(
      SocialCoupon(
        id = "sc_ceyla_buyukuzun",
        authorName = "Ceyla Büyükuzun",
        authorTitle = "beIN SPORTS Moderatörü",
        authorAvatarEmoji = "📺",
        title = "Haftanın Maçları & Stüdyo Kombinesi",
        selections = listOf(
          SlipSelection("m1", "Galatasaray - Fenerbahçe", MarketType.MATCH_RESULT, "m1_ms1", "MS 1", 1.85, true),
          SlipSelection("m8", "Liverpool - Chelsea", MarketType.TOTAL_GOALS_25, "m8_ov25", "2.5 Üst", 1.65, true)
        ),
        totalOdds = 3.05,
        stake = 500,
        likeCount = 1420,
        copyCount = 920,
        isEditor = true,
        winRate = 79,
        matchStartTimeTs = now + 36 * 60 * 1000L,
        formattedKickoff = "19:52 TSİ",
        category = "Süper Lig"
      )
    )

    // 51. Mustafa Doğan
    list.add(
      SocialCoupon(
        id = "sc_mustafa_dogan",
        authorName = "Mustafa Doğan",
        authorTitle = "Almanya Bundesliga Analisti",
        authorAvatarEmoji = "🇩🇪",
        title = "Bundesliga & Şampiyonlar Ligi Bankosu",
        selections = listOf(
          SlipSelection("m7", "Bayern Münih - Dortmund", MarketType.TOTAL_GOALS_25, "m7_ov25", "2.5 Üst", 1.45, true),
          SlipSelection("m2", "Real Madrid - Barcelona", MarketType.BOTH_TEAMS_SCORE, "m2_btts_yes", "KG Var", 1.45, true)
        ),
        totalOdds = 2.10,
        stake = 800,
        likeCount = 1290,
        copyCount = 950,
        isEditor = true,
        winRate = 79,
        matchStartTimeTs = now + 48 * 60 * 1000L,
        formattedKickoff = "20:05 TSİ",
        category = "Avrupa"
      )
    )

    // 52. Sezer Kenar
    list.add(
      SocialCoupon(
        id = "sc_sezer_kenar",
        authorName = "Sezer Kenar",
        authorTitle = "Güney Amerika & Libertadores",
        authorAvatarEmoji = "🌎",
        title = "Ateşli Karşılaşmalar & Yüksek Oran",
        selections = listOf(
          SlipSelection("m5", "Beşiktaş - Trabzonspor", MarketType.MATCH_RESULT, "m5_ms1", "MS 1", 2.05, true),
          SlipSelection("m4", "Inter - Juventus", MarketType.MATCH_RESULT, "m4_ms1", "MS 1", 2.10, true)
        ),
        totalOdds = 4.31,
        stake = 300,
        likeCount = 890,
        copyCount = 540,
        isEditor = true,
        winRate = 78,
        matchStartTimeTs = now + 75 * 60 * 1000L,
        formattedKickoff = "20:30 TSİ",
        category = "Avrupa"
      )
    )

    return list
  }
}
