package com.example.ui.components.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

data class GridRider(
  val position: Int,
  val number: Int,
  val name: String,
  val team: String,
  val bikeOrCar: String,
  val lapTime: String,
  val gap: String,
  val tyre: String, // Soft, Medium, Hard
  val topSpeed: Double,
  val points: Int,
  val teamColor: Color
)

/**
 * Specialized Motorsport & MotoGP detail view:
 * 1. Starting Grid layout with Pole position and realistic grid slots (NO football pitch!)
 * 2. Live telemetry gauges (Top speed, lean angle, RPM, throttle/brake)
 * 3. Circuit track specifications and pit-stop tyre strategies
 * 4. Head-to-Head rider telemetry comparison and AI podium predictions
 */
@Composable
fun MotorsportDetailContent(
  match: Match,
  onOpenDirectory: () -> Unit = {}
) {
  var activeSubTab by remember { mutableIntStateOf(0) } // 0: Grid & Telemetri, 1: Pist & Strateji, 2: H2H Pilotlar, 3: AI Tahmin

  val isWrc = match.league.contains("WRC", ignoreCase = true) ||
      match.league.contains("Ralli", ignoreCase = true) ||
      match.homeTeam.contains("WRC", ignoreCase = true) ||
      match.homeTeam.contains("Ogier", ignoreCase = true) ||
      match.homeTeam.contains("Neuville", ignoreCase = true) ||
      match.homeTeam.contains("Tänak", ignoreCase = true)

  val isFormula1 = !isWrc && (match.league.contains("Formula", ignoreCase = true) ||
      match.league.contains("F1", ignoreCase = true) ||
      match.homeTeam.contains("Verstappen", ignoreCase = true) ||
      match.homeTeam.contains("Norris", ignoreCase = true) ||
      match.homeTeam.contains("Leclerc", ignoreCase = true) ||
      match.homeTeam.contains("Hamilton", ignoreCase = true))

  val categoryTitle = when {
    isWrc -> "WRC Dünya Rallisi Şampiyonası"
    isFormula1 -> "Formula 1 Grand Prix"
    else -> "MotoGP Dünya Şampiyonası"
  }

  val trackTitle = when {
    isWrc -> "Etap: Rally Chile Bio Bío (Concepción • 16 Özel Etap • 306.7 km)"
    isFormula1 -> "Pist: Intercity İstanbul Park (5.245 km • 15 Viraj)"
    else -> "Pist: Misano World Circuit Marco Simoncelli (4.226 km • 16 Viraj)"
  }

  val riders = remember(match.id, isWrc, isFormula1) {
    when {
      isWrc -> listOf(
        GridRider(1, 17, "Sébastien Ogier", "Toyota Gazoo Racing WRT", "GR Yaris Rally1 Hybrid", "10:14.2", "LİDER", "Sert Çakıl", 192.4, 154, Color(0xFFEF4444)),
        GridRider(2, 11, "Thierry Neuville", "Hyundai Shell Mobis WRT", "i20 N Rally1 Hybrid", "10:15.0", "+0.8s", "Orta Çakıl", 190.8, 192, Color(0xFF38BDF8)),
        GridRider(3, 8, "Ott Tänak", "Hyundai Shell Mobis WRT", "i20 N Rally1 Hybrid", "10:16.3", "+2.1s", "Sert Çakıl", 191.2, 158, Color(0xFF38BDF8)),
        GridRider(4, 33, "Elfyn Evans", "Toyota Gazoo Racing WRT", "GR Yaris Rally1 Hybrid", "10:17.5", "+3.3s", "Orta Çakıl", 189.5, 140, Color(0xFFEF4444)),
        GridRider(5, 69, "Kalle Rovanperä", "Toyota Gazoo Racing WRT", "GR Yaris Rally1 Hybrid", "10:18.4", "+4.2s", "Yumuşak Çakıl", 193.1, 114, Color(0xFFEF4444)),
        GridRider(6, 16, "Adrien Fourmaux", "M-Sport Ford WRT", "Puma Rally1 Hybrid", "10:20.1", "+5.9s", "Sert Çakıl", 188.4, 130, Color(0xFF2563EB)),
        GridRider(7, 18, "Takamoto Katsuta", "Toyota Gazoo Racing WRT", "GR Yaris Rally1 Hybrid", "10:22.6", "+8.4s", "Orta Çakıl", 189.0, 80, Color(0xFFEF4444)),
        GridRider(8, 4, "Esapekka Lappi", "Hyundai Shell Mobis WRT", "i20 N Rally1 Hybrid", "10:25.0", "+10.8s", "Sert Çakıl", 187.9, 44, Color(0xFF38BDF8))
      )
      isFormula1 -> listOf(
        GridRider(1, 1, "Max Verstappen", "Red Bull Racing", "RB20 - Honda RBPT", "1:21.845", "POLE", "Medium", 348.5, 429, Color(0xFF1E3A8A)),
        GridRider(2, 4, "Lando Norris", "McLaren F1 Team", "MCL38 - Mercedes", "1:21.912", "+0.067s", "Medium", 346.8, 349, Color(0xFFFF8000)),
        GridRider(3, 16, "Charles Leclerc", "Scuderia Ferrari", "SF-24 - Ferrari", "1:22.015", "+0.170s", "Hard", 349.2, 319, Color(0xFFDC2626)),
        GridRider(4, 81, "Oscar Piastri", "McLaren F1 Team", "MCL38 - Mercedes", "1:22.180", "+0.335s", "Medium", 345.9, 292, Color(0xFFFF8000)),
        GridRider(5, 55, "Carlos Sainz", "Scuderia Ferrari", "SF-24 - Ferrari", "1:22.250", "+0.405s", "Hard", 347.1, 258, Color(0xFFDC2626)),
        GridRider(6, 44, "Lewis Hamilton", "Mercedes-AMG Petronas", "W15 - Mercedes", "1:22.310", "+0.465s", "Soft", 344.8, 200, Color(0xFF00D2BE))
      )
      else -> listOf(
        GridRider(1, 1, "Francesco Bagnaia", "Ducati Lenovo Team", "Desmosedici GP24", "1:44.855", "POLE", "Soft", 356.4, 317, Color(0xFFDC2626)),
        GridRider(2, 89, "Jorge Martín", "Prima Pramac Racing", "Desmosedici GP24", "1:44.933", "+0.078s", "Medium", 354.8, 341, Color(0xFF8B5CF6)),
        GridRider(3, 93, "Marc Márquez", "Gresini Racing MotoGP", "Desmosedici GP23", "1:44.979", "+0.124s", "Soft", 355.2, 282, Color(0xFF38BDF8)),
        GridRider(4, 23, "Enea Bastianini", "Ducati Lenovo Team", "Desmosedici GP24", "1:45.070", "+0.215s", "Medium", 355.8, 282, Color(0xFFDC2626)),
        GridRider(5, 31, "Pedro Acosta", "Red Bull GASGAS Tech3", "KTM RC16", "1:45.195", "+0.340s", "Hard", 352.0, 157, Color(0xFFEF4444)),
        GridRider(6, 12, "Maverick Viñales", "Aprilia Racing", "RS-GP24", "1:45.265", "+0.410s", "Medium", 351.4, 139, Color(0xFF10B981)),
        GridRider(7, 33, "Brad Binder", "Red Bull KTM Factory Racing", "KTM RC16", "1:45.310", "+0.455s", "Soft", 353.6, 165, Color(0xFFF97316)),
        GridRider(8, 20, "Fabio Quartararo", "Monster Energy Yamaha", "YZR-M1", "1:45.420", "+0.565s", "Medium", 347.8, 61, Color(0xFF1E3A8A))
      )
    }
  }

  var selectedRider by remember { mutableStateOf<GridRider?>(riders.firstOrNull()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Grand Prix Banner
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      border = BorderStroke(1.dp, GoldYellow.copy(alpha = 0.5f))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (isWrc) "🏎️" else if (isFormula1) "🏎️" else "🏍️", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(categoryTitle, color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
          }
          Text(match.league, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(trackTitle, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }

        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("HAVA / ASFALT", color = Color(0xFF94A3B8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text("26°C / 38°C", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Text("Kuru Zemin ☀️", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }

    // 2. Sub-Tab Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf(
        "🏁 Grid & Telemetri",
        "⏱️ Pist & Strateji",
        "⚔️ Pilot H2H",
        "🤖 AI Yarış Analizi"
      ).forEachIndexed { index, title ->
        FilterChip(
          selected = activeSubTab == index,
          onClick = { activeSubTab = index },
          label = { Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GoldYellow,
            selectedLabelColor = Color(0xFF0F172A),
            containerColor = Color(0xFF1E293B),
            labelColor = Color(0xFF94A3B8)
          ),
          modifier = Modifier.weight(1f)
        )
      }
    }

    when (activeSubTab) {
      0 -> StartingGridAndTelemetrySection(
        riders = riders,
        selectedRider = selectedRider,
        onSelectRider = { selectedRider = it }
      )
      1 -> CircuitAndTyreStrategySection(isWrc = isWrc, isFormula1 = isFormula1)
      2 -> PilotH2HSection(homeRider = riders[0], awayRider = riders[1])
      3 -> AiMotorsportPredictionSection(homeRider = riders[0], awayRider = riders[1])
    }
  }
}

/**
 * 🏁 Starting Grid Visualization (Asphalt track graphic, Pole position, Grid boxes)
 */
@Composable
private fun StartingGridAndTelemetrySection(
  riders: List<GridRider>,
  selectedRider: GridRider?,
  onSelectRider: (GridRider) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    // Telemetry Cockpit Gauges
    val active = selectedRider ?: riders.first()
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(active.teamColor),
              contentAlignment = Alignment.Center
            ) {
              Text("#${active.number}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(active.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text("${active.team} • ${active.bikeOrCar}", color = Color(0xFF94A3B8), fontSize = 10.sp)
            }
          }

          Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text("P${active.position} (${active.gap})", color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Telemetry Bars
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TelemetryMiniMetric(
            title = "Maksimum Hız",
            value = "${active.topSpeed} km/h",
            subtitle = "Pist Radar Rekoru",
            color = LiveRed,
            modifier = Modifier.weight(1f)
          )
          TelemetryMiniMetric(
            title = "Yatış Açısı / Viraj",
            value = "64.2°",
            subtitle = "8. Viraj Zirvesi",
            color = Color(0xFF38BDF8),
            modifier = Modifier.weight(1f)
          )
          TelemetryMiniMetric(
            title = "Pole Zamanı",
            value = active.lapTime,
            subtitle = "Sıralama Turu",
            color = GoldYellow,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Realistic Asphalt Starting Grid
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
      border = BorderStroke(1.5.dp, Color(0xFF27272A)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("🏁 RESMİ BAŞLANGIÇ GRID'İ (START ÇİZGİSİ)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
          Surface(color = Color(0xFF27272A), shape = RoundedCornerShape(4.dp)) {
            Text("POLE: P1", color = GoldYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid Rows (Staggered Grid slots: P1 left, P2 right, P3 left, P4 right...)
        riders.forEach { r ->
          val isSelected = selectedRider?.number == r.number
          Surface(
            color = if (isSelected) Color(0xFF27272A) else Color(0xFF1E293B),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0xFF334155)),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
              .clickable { onSelectRider(r) }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = if (r.position == 1) GoldYellow else Color(0xFF0F172A),
                  shape = RoundedCornerShape(4.dp),
                  modifier = Modifier.size(24.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "P${r.position}",
                      color = if (r.position == 1) Color.Black else Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp
                    )
                  }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                  modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(r.teamColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "#${r.number} ${r.name}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                  Text(
                    text = "${r.team} • ${r.tyre} Lastik",
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = r.lapTime,
                  color = GoldYellow,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
                Text(
                  text = r.gap,
                  color = if (r.position == 1) Color(0xFF10B981) else Color(0xFF94A3B8),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * ⏱️ Circuit Map & Pit-Stop Tyre Strategy Section
 */
@Composable
private fun CircuitAndTyreStrategySection(isWrc: Boolean, isFormula1: Boolean) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        val header = when {
          isWrc -> "🗺️ WRC ÖZEL ETAP VE ARA ZAMANLAR"
          isFormula1 -> "🗺️ PİST VE F1 SEKTÖR HARİTASI"
          else -> "🗺️ MOTOGP PİST VE SEKTÖR HARİTASI"
        }
        Text(header, color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))

        // Sektör / Split Zamanları Tablosu
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (isWrc) {
            SectorTimeCard("Split 1 (5.4 km)", "03:12.4", "En Hızlı", Color(0xFF10B981), Modifier.weight(1f))
            SectorTimeCard("Split 2 (11.8 km)", "06:45.1", "Mor Sektör", Color(0xFFA855F7), Modifier.weight(1f))
            SectorTimeCard("Split 3 (Finis)", "10:14.2", "Lider Derece", Color(0xFF38BDF8), Modifier.weight(1f))
          } else {
            SectorTimeCard("S1 (Sektör 1)", "28.140s", "En Hızlı", Color(0xFF10B981), Modifier.weight(1f))
            SectorTimeCard("S2 (8. Viraj)", "33.890s", "Mor Sektör", Color(0xFFA855F7), Modifier.weight(1f))
            SectorTimeCard("S3 (Son Viraj)", "22.415s", "Kişisel En İyi", Color(0xFF38BDF8), Modifier.weight(1f))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pist / Etap Özellikleri
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          if (isWrc) {
            Column {
              Text("Etap Uzunluğu: 19.72 km (SS1)", color = Color(0xFFCBD5E1), fontSize = 10.sp)
              Text("Toplam Ralli: 306.76 km (16 Etap)", color = Color(0xFFCBD5E1), fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Zemin: Sert Orman Çakılı (Gravel)", color = Color(0xFFCBD5E1), fontSize = 10.sp)
              Text("Rakım: 420m Pasifik Kıyısı", color = Color(0xFFCBD5E1), fontSize = 10.sp)
            }
          } else {
            Column {
              Text("Pist Uzunluğu: 5.245 Metre", color = Color(0xFFCBD5E1), fontSize = 10.sp)
              Text("En Uzun Düzlük: 1.141 Metre", color = Color(0xFFCBD5E1), fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Viraj Sayısı: 15 (9 Sağ, 6 Sol)", color = Color(0xFFCBD5E1), fontSize = 10.sp)
              Text("Yön: Saat Yönünün Tersi", color = Color(0xFFCBD5E1), fontSize = 10.sp)
            }
          }
        }
      }
    }

    // Lastik Stratejisi Kartı
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(if (isWrc) "🛞 WRC RALLİ LASTİK HAMURU VE HİBRİT BOOST" else "🛞 LASTİK HAMURU VE PIT STRATEJİSİ", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val title1 = when {
            isWrc -> "Sert Çakıl (Pirelli Scorpion)"
            isFormula1 -> "Set 1 (Medium C3)"
            else -> "Ön Lastik (Hard Michelin)"
          }
          val title2 = when {
            isWrc -> "Yumuşak Çakıl (2 Yedek)"
            isFormula1 -> "Set 2 (Hard C2)"
            else -> "Arka Lastik (Soft Michelin)"
          }
          TyreStatusCard(title = title1, wear = 35, color = Color(0xFFEAB308), modifier = Modifier.weight(1f))
          TyreStatusCard(title = title2, wear = 58, color = Color(0xFFEF4444), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = when {
            isWrc -> "Sabah etabında temizlenen yol avantajı: İkinci geçişte sert hamur çakıl lastiği ve 100kW hibrit boost tavsiye edilir."
            isFormula1 -> "Optimum Pit Penceresi: 22. - 26. Tur (Tek Pit-Stop Stratejisi)"
            else -> "Hava sıcaklığı nedeniyle arka lastikte yüksek aşınma bekleniyor."
          },
          color = Color(0xFF94A3B8),
          fontSize = 10.sp
        )
      }
    }
  }
}

/**
 * ⚔️ Pilot H2H Section
 */
@Composable
private fun PilotH2HSection(homeRider: GridRider, awayRider: GridRider) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    border = BorderStroke(1.dp, Color(0xFF334155))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text("⚔️ SEZON REKABETİ & BAŞA BAŞ KARŞILAŞTIRMA", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
      Spacer(modifier = Modifier.height(10.dp))

      H2HStatRow("Şampiyona Puanı", "${homeRider.points}", "${awayRider.points}")
      H2HStatRow("Pole Pozisyonu", "5", "6")
      H2HStatRow("Yarış Galibiyeti", "7", "3")
      H2HStatRow("Podyum Sayısı", "11", "12")
      H2HStatRow("Sprint Galibiyeti", "4", "5")
      H2HStatRow("Maksimum Hız Rekoru", "${homeRider.topSpeed} km/h", "${awayRider.topSpeed} km/h")
    }
  }
}

/**
 * 🤖 AI Yarış Tahmini & Risk Analizi
 */
@Composable
private fun AiMotorsportPredictionSection(homeRider: GridRider, awayRider: GridRider) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    border = BorderStroke(1.dp, GoldYellow)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("🤖", fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text("GEMINI AI YARIŞ PROJEKSİYONU", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Sıralama turlarındaki telemetri ve lastik aşınma verilerine göre ${homeRider.name}, pole avantajı ve düzlük hızındaki 356.4 km/h üstünlüğüyle yarışın en güçlü galibiyet adayıdır.",
        color = Color(0xFFCBD5E1),
        fontSize = 11.sp,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f)) {
          Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Podyum Olasılığı", color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text("%88 (Yüksek)", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Black)
          }
        }
        Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f)) {
          Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("En Hızlı Tur Oranı", color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text("1.85", color = GoldYellow, fontSize = 12.sp, fontWeight = FontWeight.Black)
          }
        }
        Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f)) {
          Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Kaza / DNF Riski", color = Color(0xFF94A3B8), fontSize = 9.sp)
            Text("%8 (Düşük)", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Black)
          }
        }
      }
    }
  }
}

@Composable
private fun TelemetryMiniMetric(title: String, value: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
  Surface(
    color = Color(0xFF1E293B),
    shape = RoundedCornerShape(8.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(title, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Medium)
      Text(value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black)
      Text(subtitle, color = Color(0xFF64748B), fontSize = 8.sp)
    }
  }
}

@Composable
private fun SectorTimeCard(sector: String, time: String, status: String, color: Color, modifier: Modifier = Modifier) {
  Surface(
    color = Color(0xFF0F172A),
    shape = RoundedCornerShape(6.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      Text(sector, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
      Text(time, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
      Text(status, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun TyreStatusCard(title: String, wear: Int, color: Color, modifier: Modifier = Modifier) {
  Surface(
    color = Color(0xFF1E293B),
    shape = RoundedCornerShape(8.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      LinearProgressIndicator(
        progress = { wear / 100f },
        color = color,
        trackColor = Color(0xFF334155),
        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
      )
      Spacer(modifier = Modifier.height(3.dp))
      Text("Aşınma Seviyesi: %$wear", color = Color(0xFF94A3B8), fontSize = 8.sp)
    }
  }
}

@Composable
private fun H2HStatRow(title: String, homeValue: String, awayValue: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(homeValue, color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Start)
    Text(title, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    Text(awayValue, color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
  }
}
