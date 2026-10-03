package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GeminiDataIngestionWorker
import com.example.engine.IngestedSportResult
import com.example.engine.SeasonalFixtureMatch
import com.example.engine.SeasonalSchedulingEngine
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiSportsIngestionAndSchedulingDialog(
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Gemini AI Veri Aktarımı, 1: Sezonsal Fikstür Motoru

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF0F172A),
    modifier = Modifier.testTag("gemini_ingestion_scheduling_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxSize()
    ) {
      // Header
      Surface(
        color = Color(0xFF1E293B),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F3A3D)),
              contentAlignment = Alignment.Center
            ) {
              Text("🤖", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "GEMINI AI VERİ MOTORU & FİKSTÜR",
                color = GoldYellow,
                fontWeight = FontWeight.Black,
                fontSize = 13.5.sp
              )
              Text(
                text = "80+ Branş Otomatik Haritalama • Sezonsal Takvim Motoru",
                color = Color(0xFFCBD5E1),
                fontSize = 10.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(28.dp)
              .background(Color(0x33FFFFFF), CircleShape)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Tab Row
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFF1E293B),
        contentColor = GoldYellow
      ) {
        listOf(
          "🤖 Gemini Veri Aktarımı",
          "📅 Sezonsal Fikstür Motoru"
        ).forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                color = if (selectedTab == index) GoldYellow else Color(0xFF94A3B8)
              )
            }
          )
        }
      }

      Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
          0 -> GeminiDataIngestionView()
          1 -> SeasonalSchedulingView()
        }
      }
    }
  }
}

@Composable
private fun GeminiDataIngestionView() {
  val coroutineScope = rememberCoroutineScope()
  val logs by GeminiDataIngestionWorker.ingestionLogs.collectAsState(initial = emptyList())
  val isIngesting by GeminiDataIngestionWorker.isIngesting.collectAsState(initial = false)

  var selectedSport by remember { mutableStateOf("WRC Dünya Rallisi") }
  var rawTextPrompt by remember {
    mutableStateOf(
      "Thierry Neuville (Hyundai i20 N Rally1) - 242 Puan, Lider\n" +
      "Ott Tänak (Hyundai i20 N Rally1) - 200 Puan\n" +
      "Sébastien Ogier (Toyota GR Yaris) - 191 Puan\n" +
      "Elfyn Evans (Toyota GR Yaris) - 160 Puan\n" +
      "Kalle Rovanperä (Toyota GR Yaris) - 114 Puan"
    )
  }
  var lastResult by remember { mutableStateOf<IngestedSportResult?>(null) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("Branş Seçin (80+ Desteklenen Spor)", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(listOf("WRC Dünya Rallisi", "MotoGP", "Formula 1", "Trendyol Süper Lig", "EuroLeague", "Tenis ATP/WTA", "Voleybol Sultanlar", "NFL")) { sport ->
              val isSel = selectedSport == sport
              FilterChip(
                selected = isSel,
                onClick = { selectedSport = sport },
                label = { Text(sport, fontSize = 10.5.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = TealDark,
                  selectedLabelColor = Color.White,
                  containerColor = Color(0xFF0F172A),
                  labelColor = Color(0xFF94A3B8)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text("Ham Spor Metni / Bülten Verisi:", color = Color(0xFFCBD5E1), fontSize = 10.sp)
          OutlinedTextField(
            value = rawTextPrompt,
            onValueChange = { rawTextPrompt = it },
            maxLines = 4,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = TealDark,
              unfocusedBorderColor = Color(0xFF475569)
            )
          )

          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              coroutineScope.launch {
                val res = GeminiDataIngestionWorker.ingestSportDataWithGemini(selectedSport, rawTextPrompt)
                lastResult = res
              }
            },
            enabled = !isIngesting && rawTextPrompt.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = TealDark)
          ) {
            if (isIngesting) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Gemini API Veriyi Yapılandırıyor...", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Gemini API ile Veriyi Çek & Şemaya Haritala", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }

    // Ingestion Result
    lastResult?.let { res ->
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
          border = BorderStroke(1.dp, Color(0xFF10B981))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFA7F3D0), modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Haritalama Tamamlandı (${res.parsedPlayers.size} Profil)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            res.parsedPlayers.forEach { p ->
              Text("• #${p.number} ${p.name} (${p.team}) - ${p.position} [Reyting: ${p.rating}]", color = Color(0xFFD1FAE5), fontSize = 10.5.sp)
            }
          }
        }
      }
    }

    // Historical Logs
    item {
      Text("📋 Canlı Haritalama Kayıtları & İşlem Geçmişi", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
    }

    items(logs) { log ->
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(log.sport, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Surface(color = Color(0xFF065F46), shape = RoundedCornerShape(3.dp)) {
                Text(log.status, color = Color(0xFFA7F3D0), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(log.summary, color = Color(0xFF94A3B8), fontSize = 10.sp)
          }
          Text(log.timestamp, color = Color(0xFF64748B), fontSize = 9.sp)
        }
      }
    }
  }
}

@Composable
private fun SeasonalSchedulingView() {
  val allFixtures = remember { SeasonalSchedulingEngine.generateFixturesForAllSupportedSports() }
  var selectedLeague by remember { mutableStateOf("Trendyol Süper Lig") }

  val fixturesForLeague = allFixtures[selectedLeague] ?: emptyList()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sezonsal Fikstür & Maç Zamanı Algoritması", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Tarihsel lig takvimleri, derbi aralıkları ve yayın kuşakları (13:30, 16:00, 19:00, 21:45 TSİ) kural tabanlı olarak otomatik oluşturulmuştur.",
            color = Color(0xFF94A3B8),
            fontSize = 9.5.sp
          )
        }
      }
    }

    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(allFixtures.keys.toList()) { league ->
          val isSel = selectedLeague == league
          FilterChip(
            selected = isSel,
            onClick = { selectedLeague = league },
            label = { Text(league, fontSize = 10.5.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealDark,
              selectedLabelColor = Color.White,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFF94A3B8)
            )
          )
        }
      }
    }

    items(fixturesForLeague.take(25), key = { it.fixtureId }) { fix ->
      SeasonalFixtureCard(fixture = fix)
    }
  }
}

@Composable
private fun SeasonalFixtureCard(fixture: SeasonalFixtureMatch) {
  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    border = BorderStroke(1.dp, if (fixture.isDerby) GoldYellow else Color(0xFF334155)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFF334155),
            shape = RoundedCornerShape(3.dp)
          ) {
            Text("${fixture.week}. Hafta", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
          }
          if (fixture.isDerby) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(color = Color(0xFFDC2626), shape = RoundedCornerShape(3.dp)) {
              Text("DEV DERBİ", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(fixture.dateFormatted, color = Color(0xFF94A3B8), fontSize = 9.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("${fixture.homeTeam} - ${fixture.awayTeam}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.5.sp)
      }

      Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp)) {
        Text(fixture.kickoffTime, color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
      }
    }
  }
}
