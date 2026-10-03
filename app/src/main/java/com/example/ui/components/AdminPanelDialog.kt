package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

@Composable
fun AdminPanelDialog(
  matches: List<Match>,
  onUpdateScore: (matchId: String, homeScore: Int, awayScore: Int) -> Unit,
  onUpdateMinute: (matchId: String, minute: Int) -> Unit,
  onEndMatch: (matchId: String) -> Unit,
  onStartNextLiveMatch: () -> Unit,
  onAddNewMatch: (home: String, away: String, league: String, sport: Sport, isHot: Boolean) -> Unit,
  onResetFixtures: () -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Maç Yönetimi, 1: Yeni Maç Ekle, 2: DB & Sistem

  var selectedMatchId by remember {
    mutableStateOf(matches.firstOrNull { it.status == MatchStatus.LIVE }?.id ?: matches.firstOrNull()?.id ?: "")
  }

  val activeMatch = matches.find { it.id == selectedMatchId }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("admin_panel_dialog"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = Color(0xFF1E293B),
          shape = CircleShape,
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = null,
              tint = GoldYellow,
              modifier = Modifier.size(22.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Yönetici & Admin Paneli",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "Canlı Skor, 85' Kilit, Yeni Maç & Room DB",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color(0xFFF1F5F9),
          contentColor = TealDark,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Canlı Yönet", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Maç Ekle", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("DB Bilgi", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
          0 -> {
            // Live Match Control Tab
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              item {
                Text(
                  text = "YÖNETİLECEK MAÇI SEÇİN",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
              }

              // Match Selector Row
              item {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(8.dp)
                ) {
                  matches.take(8).forEach { m ->
                    val isSel = m.id == selectedMatchId
                    Surface(
                      color = if (isSel) TealDark else Color.White,
                      shape = RoundedCornerShape(6.dp),
                      border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealDark else Color(0xFFE2E8F0)),
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { selectedMatchId = m.id }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "${m.homeTeam} - ${m.awayTeam}",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isSel) Color.White else Color(0xFF1E293B)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Surface(
                            color = if (m.status == MatchStatus.LIVE) LiveRed else Color(0xFF94A3B8),
                            shape = RoundedCornerShape(4.dp)
                          ) {
                            Text(
                              text = if (m.status == MatchStatus.LIVE) "${m.minute}'" else m.status.name,
                              fontSize = 9.sp,
                              color = Color.White,
                              fontWeight = FontWeight.Bold,
                              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                          }
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = "${m.homeScore} - ${m.awayScore}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSel) GoldYellow else Color(0xFF0F172A)
                          )
                        }
                      }
                    }
                  }
                }
              }

              if (activeMatch != null) {
                item {
                  HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                  Text(
                    text = "SEÇİLİ MAÇI DÜZENLE: ${activeMatch.homeTeam} vs ${activeMatch.awayTeam}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                  )
                  Spacer(modifier = Modifier.height(6.dp))

                  // Quick Scenario Buttons (2-1 and 4-1 test as requested)
                  Text("Hızlı Senaryo Testi:", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Button(
                      onClick = { onUpdateScore(activeMatch.id, 2, 1) },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                      shape = RoundedCornerShape(6.dp),
                      modifier = Modifier.weight(1f)
                    ) {
                      Text("2 - 1 Yap (1.15 Oran)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                      onClick = { onUpdateScore(activeMatch.id, 4, 1) },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                      shape = RoundedCornerShape(6.dp),
                      modifier = Modifier.weight(1f)
                    ) {
                      Text("4 - 1 Yap (2.5 Alt Kapa)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // Score Controller
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(activeMatch.homeTeam, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                          onClick = { onUpdateScore(activeMatch.id, (activeMatch.homeScore - 1).coerceAtLeast(0), activeMatch.awayScore) }
                        ) { Text("-", fontSize = 18.sp, fontWeight = FontWeight.Black) }
                        Text("${activeMatch.homeScore}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TealDark)
                        IconButton(
                          onClick = { onUpdateScore(activeMatch.id, activeMatch.homeScore + 1, activeMatch.awayScore) }
                        ) { Text("+", fontSize = 18.sp, fontWeight = FontWeight.Black) }
                      }
                    }

                    Text("VS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF94A3B8))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(activeMatch.awayTeam, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                          onClick = { onUpdateScore(activeMatch.id, activeMatch.homeScore, (activeMatch.awayScore - 1).coerceAtLeast(0)) }
                        ) { Text("-", fontSize = 18.sp, fontWeight = FontWeight.Black) }
                        Text("${activeMatch.awayScore}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TealDark)
                        IconButton(
                          onClick = { onUpdateScore(activeMatch.id, activeMatch.homeScore, activeMatch.awayScore + 1) }
                        ) { Text("+", fontSize = 18.sp, fontWeight = FontWeight.Black) }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // Minute & 85' Lock Controllers
                  Text("Dakika & Bahis Kilidi Testi:", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    OutlinedButton(
                      onClick = { onUpdateMinute(activeMatch.id, 65) },
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text("65' Yap", fontSize = 10.sp)
                    }
                    Button(
                      onClick = { onUpdateMinute(activeMatch.id, 85) },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                      modifier = Modifier.weight(1.3f),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("85' Kilitle", fontSize = 10.sp, fontWeight = FontWeight.Black)
                      }
                    }
                    Button(
                      onClick = { onEndMatch(activeMatch.id) },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                      modifier = Modifier.weight(1.3f),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text("🏁 90' Bitir", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Button(
                    onClick = onStartNextLiveMatch,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldYellow)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("⚡ Yeni Canlı Maç Başlat (Sıradaki Fikstür)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          1 -> {
            // Add New Match Tab
            var newHome by remember { mutableStateOf("") }
            var newAway by remember { mutableStateOf("") }
            var newLeague by remember { mutableStateOf("Trendyol Süper Lig") }
            var isHotMatch by remember { mutableStateOf(true) }
            var addedSuccess by remember { mutableStateOf(false) }

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(
                text = "GERÇEK VERİ İLE YENİ MAÇ EKLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )

              OutlinedTextField(
                value = newHome,
                onValueChange = { newHome = it },
                label = { Text("Ev Sahibi Takım") },
                placeholder = { Text("Örn: Beşiktaş") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = newAway,
                onValueChange = { newAway = it },
                label = { Text("Deplasman Takımı") },
                placeholder = { Text("Örn: Trabzonspor") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = newLeague,
                onValueChange = { newLeague = it },
                label = { Text("Lig") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("🔥 Öne Çıkan Derbi / Hot Maç Yap", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Switch(
                  checked = isHotMatch,
                  onCheckedChange = { isHotMatch = it },
                  colors = SwitchDefaults.colors(checkedThumbColor = GoldYellow, checkedTrackColor = TealDark)
                )
              }

              if (addedSuccess) {
                Text("✅ Maç başarıyla veritabanına ve bültene eklendi!", color = Color(0xFF16A34A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  if (newHome.isNotBlank() && newAway.isNotBlank()) {
                    onAddNewMatch(newHome.trim(), newAway.trim(), newLeague.trim(), Sport.FOOTBALL, isHotMatch)
                    addedSuccess = true
                    newHome = ""
                    newAway = ""
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealDark),
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Fikstüre & Room DB'ye Kaydet")
              }
            }
          }

          2 -> {
            // DB & System Info Tab
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(
                text = "VERİTABANI VE SİSTEM DURUMU",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text("• Toplam Maç Kaydı: ${matches.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  Text("• Canlı Maç Sayısı: ${matches.count { it.status == MatchStatus.LIVE }}", fontSize = 12.sp, color = LiveRed, fontWeight = FontWeight.Bold)
                  Text("• Biten Maç Sayısı: ${matches.count { it.status == MatchStatus.FINISHED }}", fontSize = 12.sp, color = Color(0xFF64748B))
                  Text("• Başlamamış Maçlar: ${matches.count { it.status == MatchStatus.SCHEDULED || it.status == MatchStatus.UPCOMING }}", fontSize = 12.sp)
                  Text("• Room DB Tabloları: matches, tickets, wallet, user_balance, odds", fontSize = 11.sp, color = Color(0xFF475569))
                  Text("• Firestore Senkronizasyon: Aktif & Hazır", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedButton(
                onClick = onResetFixtures,
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFDC2626))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Veritabanı Maçlarını Sıfırla / Taze Fikstür Yükle", color = Color(0xFFDC2626), fontSize = 11.sp)
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = TealDark)
      ) {
        Text("Kapat")
      }
    }
  )
}
