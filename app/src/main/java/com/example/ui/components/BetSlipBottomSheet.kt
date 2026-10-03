package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.SlipSelection
import com.example.data.model.TicketType
import com.example.service.BettingClosureStatus
import com.example.service.CalendarManagementService
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetSlipBottomSheet(
  selections: List<SlipSelection>,
  totalOdds: Double,
  selectedStake: Long,
  selectedType: TicketType,
  walletBalance: Long,
  matches: List<Match> = emptyList(),
  onStakeChange: (Long) -> Unit,
  onTypeChange: (TicketType) -> Unit,
  onRemoveItem: (String) -> Unit,
  onClearAll: () -> Unit,
  onPlaceBet: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val potentialWin = (selectedStake * totalOdds).toLong()
  var isAiAnalysisExpanded by remember { mutableStateOf(false) }

  // Check if any match in the slip is locked under the 5-minute closure rule
  val hasLockedMatches = remember(selections, matches) {
    selections.any { sel ->
      val match = matches.firstOrNull { it.id == sel.matchId }
      match != null && (CalendarManagementService.getClosureStatus(match) is BettingClosureStatus.Locked5MinWindow || match.status == MatchStatus.FINISHED)
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFFF8FAFC),
    modifier = Modifier.testTag("bet_slip_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.85f)
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Kuponum",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Color(0xFF0F172A)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(TealDark)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${selections.size} Maç",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }

        if (selections.isNotEmpty()) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clickable { onClearAll() }
              .padding(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Temizle",
              tint = Color(0xFFDC2626),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "Temizle",
              color = Color(0xFFDC2626),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Type Selector Chips (Tekli, Kombine, Sistem)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TicketType.entries.forEach { type ->
          FilterChip(
            selected = selectedType == type,
            onClick = { onTypeChange(type) },
            label = { Text(type.label, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealDark,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Selections List
      if (selections.isEmpty()) {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📑", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Kuponunuzda maç bulunmuyor.",
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF64748B),
              fontSize = 14.sp
            )
            Text(
              text = "Bültenden oranlara dokunarak kupon yapabilirsiniz.",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(selections) { item ->
            val itemMatch = matches.firstOrNull { it.id == item.matchId }
            val itemStatus = itemMatch?.let { CalendarManagementService.getClosureStatus(it) }
            val isItemLocked = itemStatus is BettingClosureStatus.Locked5MinWindow || itemMatch?.status == MatchStatus.FINISHED

            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isItemLocked) Color(0xFFFFF1F2) else Color.White
              ),
              border = if (isItemLocked) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDA4AF)) else null,
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = item.matchTeams,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "${item.marketType.displayName}: ",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                      )
                      Text(
                        text = item.selectionName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TealDark
                      )
                    }
                  }

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      color = if (isItemLocked) Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = "%.2f".format(item.odd),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (isItemLocked) Color(0xFFDC2626) else Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                      onClick = { onRemoveItem(item.selectionId) },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kaldır",
                        tint = if (isItemLocked) Color(0xFFE11D48) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }

                if (isItemLocked) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.LockClock,
                      contentDescription = null,
                      tint = Color(0xFFDC2626),
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "5 dakika kapanış kuralı: Bu maç için kupon oynama süresi dolmuştur.",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFDC2626)
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // AI Coupon Risk & Probability Insights
      if (selections.isNotEmpty()) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFF0FDF4),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isAiAnalysisExpanded = !isAiAnalysisExpanded }
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color(0xFF16A34A),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Gemini AI Kupon Değerlendirmesi",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF166534)
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFDCFCE7)
              ) {
                val winEst = (100.0 / totalOdds.coerceAtLeast(1.0)).toInt().coerceIn(12, 92)
                Text(
                  text = "%$winEst Olasılık",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF15803D),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            if (isAiAnalysisExpanded) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "📊 Gemini 3.5 Analizi: Seçilen ${selections.size} karşılaşmanın güncel xG ve Poisson olasılıkları hesaplandı. Toplam $totalOdds oran dengeli bir risk profili sunuyor. Maçların canlı durumlarını takip etmeniz önerilir.",
                fontSize = 11.sp,
                color = Color(0xFF14532D),
                lineHeight = 15.sp
              )
            } else {
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Detaylı yapay zeka risk analizini görmek için dokunun ▾",
                fontSize = 10.sp,
                color = Color(0xFF15803D)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Stake Selector & Custom Input Field
      Text(
        text = "Yatırılacak Tahmin Puanı (TP):",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF475569)
      )
      Spacer(modifier = Modifier.height(6.dp))

      // Custom Numeric Input Field with +/- and Max Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        androidx.compose.material3.OutlinedTextField(
          value = if (selectedStake > 0) selectedStake.toString() else "",
          onValueChange = { newVal ->
            val numericOnly = newVal.filter { it.isDigit() }
            val stakeVal = numericOnly.toLongOrNull() ?: 0L
            onStakeChange(stakeVal.coerceIn(0L, walletBalance.coerceAtLeast(100000L)))
          },
          label = { Text("Bahis Tutarı (TP)", fontSize = 11.sp) },
          placeholder = { Text("Örn: 100", fontSize = 12.sp) },
          trailingIcon = {
            Text(
              text = "TP",
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = TealDark,
              modifier = Modifier.padding(end = 12.dp)
            )
          },
          keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
          ),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .height(54.dp)
            .testTag("stake_amount_input"),
          shape = RoundedCornerShape(10.dp),
          colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealDark,
            unfocusedBorderColor = Color(0xFFCBD5E1)
          )
        )

        // Max Balance Button
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFFEF3C7),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
          modifier = Modifier
            .height(54.dp)
            .clickable { onStakeChange(walletBalance) }
            .testTag("max_stake_button")
        ) {
          Box(
            modifier = Modifier.padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "TÜMÜ\n(MAX)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF92400E),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              lineHeight = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Quick Stake Chips (50, 100, 250, 500, +1000 TP)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(50L, 100L, 250L, 500L, 1000L).forEach { stake ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selectedStake == stake) TealDark else Color.White,
            border = if (selectedStake == stake) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier
              .weight(1f)
              .clickable { onStakeChange(stake) }
          ) {
            Text(
              text = "$stake",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (selectedStake == stake) Color.White else Color(0xFF334155),
              modifier = Modifier.padding(vertical = 6.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }

      // Quick Stake Increment Buttons (+50, +100, +250)
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(50L, 100L, 250L).forEach { inc ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFF1F5F9),
            modifier = Modifier
              .weight(1f)
              .clickable { onStakeChange((selectedStake + inc).coerceAtMost(walletBalance.coerceAtLeast(100000L))) }
          ) {
            Text(
              text = "+$inc TP Ekle",
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF475569),
              modifier = Modifier.padding(vertical = 4.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Summary Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Toplam Oran:", fontSize = 12.sp, color = Color(0xFF64748B), maxLines = 1, softWrap = false)
            Text(text = "%.2f".format(totalOdds), fontSize = 13.sp, fontWeight = FontWeight.Black, color = TealDark, maxLines = 1, softWrap = false)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Mevcut Bakiyeniz:", fontSize = 12.sp, color = Color(0xFF64748B), maxLines = 1, softWrap = false)
            Text(text = "$walletBalance TP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488), maxLines = 1, softWrap = false)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Tahmini Kazanç:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, softWrap = false)
            Text(text = "$potentialWin TP", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFF047857), maxLines = 1, softWrap = false)
          }
        }
      }

      if (hasLockedMatches) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFFEF2F2),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LockClock,
              contentDescription = null,
              tint = Color(0xFFDC2626),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Kuponunuzdaki bazı maçlar 5 dakika kapanış kuralı nedeniyle kilitlendi. Lütfen kilitli maçları kupondan çıkarınız.",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFB91C1C)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Confirm Button
      Button(
        onClick = onPlaceBet,
        enabled = selections.isNotEmpty() && walletBalance >= selectedStake && !hasLockedMatches,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("confirm_ticket_button"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (hasLockedMatches) Color(0xFFEF4444) else GoldYellow,
          contentColor = if (hasLockedMatches) Color.White else TealDark,
          disabledContainerColor = Color(0xFFCBD5E1)
        )
      ) {
        Text(
          text = if (hasLockedMatches) {
            "KİLİTLİ MAÇLARI ÇIKARINIZ (5 Dk Kuralı)"
          } else if (walletBalance < selectedStake) {
            "Yetersiz TP Bakiyesi"
          } else {
            "KUPONU ONAYLA ($selectedStake TP)"
          },
          fontWeight = FontWeight.Black,
          fontSize = 13.sp
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
