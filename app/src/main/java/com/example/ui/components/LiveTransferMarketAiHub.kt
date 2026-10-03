package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.UserBalanceManager
import com.example.data.model.AiTransferEvaluation
import com.example.data.model.HistoricalTransfer
import com.example.data.model.LiveTransferItem
import com.example.data.model.TransferMarketPlayer
import com.example.data.model.UserVirtualTransfer
import com.example.data.remote.TransferMarketRepository
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.launch

/**
 * Gerçek Canlı Transfer Dönemi, Alım-Satım AI Modu ve Transfer Geçmişi Bileşeni
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTransferMarketAiHub(
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Canlı KAP & Transferler, 1: Alım-Satım AI Modu, 2: Transfer Geçmişi

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF0F172A),
    modifier = Modifier.testTag("transfer_market_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxSize()
    ) {
      // 1. Header
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
              Text("✈️", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "CANLI TRANSFER DÖNEMİ",
                  color = GoldYellow,
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  color = LiveRed,
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "AÇIK",
                    color = Color.White,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "KAP Bildirimleri • Alım-Satım AI Modu • Bonservis Raporları",
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

      // 2. Navigation Tab Row
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFF1E293B),
        contentColor = GoldYellow
      ) {
        listOf(
          "🌟 Oyuncu Pazarı",
          "🔥 Canlı KAP",
          "🤖 Alım-Satım AI",
          "🏆 Sanal Kadrom"
        ).forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                color = if (selectedTab == index) GoldYellow else Color(0xFF94A3B8)
              )
            }
          )
        }
      }

      // 3. Tab Contents
      Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
          0 -> AvailablePlayersTransferMarketTab()
          1 -> LiveTransfersAndKapTab()
          2 -> BuySellAiTradingModeTab()
          3 -> UserVirtualSquadTab()
        }
      }
    }
  }
}

@Composable
private fun AvailablePlayersTransferMarketTab() {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val db = remember { AppDatabase.getDatabase(context) }
  val balanceManager = remember { UserBalanceManager(db) }
  val wallet by db.walletDao().getWalletFlow().collectAsState(initial = null)
  val userBalance = wallet?.availablePoints ?: 10000L

  val allPlayers by TransferMarketRepository.availablePlayersFlow.collectAsState(initial = emptyList())
  var searchQuery by remember { mutableStateOf("") }
  var selectedPosition by remember { mutableStateOf("Tümü") }
  var selectedPlayerForTransfer by remember { mutableStateOf<TransferMarketPlayer?>(null) }
  var tokenOfferInput by remember { mutableStateOf("") }

  val filteredPlayers = remember(allPlayers, searchQuery, selectedPosition) {
    allPlayers.filter { player ->
      val matchesPos = when (selectedPosition) {
        "Tümü" -> true
        "Forvet / SNT" -> player.position == "SNT"
        "Kanat" -> player.position in listOf("SLK", "SĞK")
        "Orta Saha" -> player.position in listOf("OS", "ONN")
        "Savunma" -> player.position in listOf("STP", "SLB", "SĞB")
        "Kaleci" -> player.position == "KL"
        else -> true
      }
      val matchesSearch = searchQuery.isBlank() ||
          player.name.contains(searchQuery, true) ||
          player.club.contains(searchQuery, true) ||
          player.league.contains(searchQuery, true)

      matchesPos && matchesSearch
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // User Token Balance & Market Stats Banner
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, GoldYellow)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F3A3D)),
              contentAlignment = Alignment.Center
            ) {
              Text("🪙", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("SANAL TRANSFER BÜTÇESİ", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text("$userBalance TP", color = GoldYellow, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
          }

          Surface(
            color = Color(0xFF0B132B),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.8.dp, Color(0xFF38BDF8))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Pazarda ${allPlayers.size} Oyuncu", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Search input
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Oyuncu veya kulüp ara...", color = Color(0xFF64748B), fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = TealDark,
          unfocusedBorderColor = Color(0xFF334155),
          focusedContainerColor = Color(0xFF1E293B),
          unfocusedContainerColor = Color(0xFF1E293B)
        )
      )
    }

    // Position Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
      ) {
        items(listOf("Tümü", "Forvet / SNT", "Kanat", "Orta Saha", "Savunma", "Kaleci")) { pos ->
          val isSelected = selectedPosition == pos
          FilterChip(
            selected = isSelected,
            onClick = { selectedPosition = pos },
            label = { Text(pos, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealDark,
              selectedLabelColor = Color.White,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFFCBD5E1)
            )
          )
        }
      }
    }

    // Players list
    items(filteredPlayers, key = { it.id }) { player ->
      AvailablePlayerCard(
        player = player,
        userBalance = userBalance,
        onInitiateTransfer = {
          selectedPlayerForTransfer = player
          tokenOfferInput = player.tokenPrice.toString()
        }
      )
    }
  }

  // Transfer Request Dialog
  selectedPlayerForTransfer?.let { player ->
    AlertDialog(
      onDismissRequest = { selectedPlayerForTransfer = null },
      containerColor = Color(0xFF1E293B),
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Sanal Transfer Talebi", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 16.sp)
          IconButton(onClick = { selectedPlayerForTransfer = null }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF94A3B8))
          }
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(player.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                  Text("${player.club} • ${player.position} • ${player.age} Yaş", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
                Surface(
                  color = Color(0xFF065F46),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text("★ ${player.rating}", color = Color(0xFFA7F3D0), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Piyasa Değeri: ${player.marketValueEur} • xG Katkısı: ${player.xgPerMatch}", color = Color(0xFF38BDF8), fontSize = 10.sp)
              Text(player.scoutingSummary, color = Color(0xFFCBD5E1), fontSize = 9.5.sp)
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Mevcut Bakiyeniz:", color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text("$userBalance TP", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          Text("Teklif Edilecek TP Miktarı (Önerilen: ${player.tokenPrice} TP)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          OutlinedTextField(
            value = tokenOfferInput,
            onValueChange = { tokenOfferInput = it.filter { ch -> ch.isDigit() } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = TealDark,
              unfocusedBorderColor = Color(0xFF475569)
            )
          )

          val parsedBid = tokenOfferInput.toLongOrNull() ?: 0L
          if (parsedBid > userBalance) {
            Text("⚠️ Yetersiz bakiye! Teklifiniz mevcut TP bakiyenizden büyük olamaz.", color = Color(0xFFEF4444), fontSize = 10.sp)
          }
        }
      },
      confirmButton = {
        val parsedBid = tokenOfferInput.toLongOrNull() ?: 0L
        val canConfirm = parsedBid > 0 && parsedBid <= userBalance
        Button(
          onClick = {
            coroutineScope.launch {
              val deducted = balanceManager.deductStake(parsedBid, "Sanal Transfer: ${player.name} (${player.club})")
              if (deducted) {
                TransferMarketRepository.executeVirtualTransfer(player, parsedBid, userBalance)
                Toast.makeText(context, "🎉 ${player.name} sanal kadronuza katıldı! (-$parsedBid TP)", Toast.LENGTH_LONG).show()
                selectedPlayerForTransfer = null
              } else {
                Toast.makeText(context, "Transfer işlemi başarısız oldu (Yetersiz bakiye).", Toast.LENGTH_SHORT).show()
              }
            }
          },
          enabled = canConfirm,
          colors = ButtonDefaults.buttonColors(containerColor = TealDark)
        ) {
          Text("Transferi İmzala (-$parsedBid TP)", fontWeight = FontWeight.Black)
        }
      },
      dismissButton = {
        Button(
          onClick = { selectedPlayerForTransfer = null },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
        ) {
          Text("Vazgeç")
        }
      }
    )
  }
}

@Composable
private fun AvailablePlayerCard(
  player: TransferMarketPlayer,
  userBalance: Long,
  onInitiateTransfer: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    border = BorderStroke(1.dp, Color(0xFF334155)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          val posColor = when (player.position) {
            "KL" -> Color(0xFFEAB308)
            "STP", "SLB", "SĞB" -> Color(0xFF2563EB)
            "OS", "ONN" -> Color(0xFF059669)
            else -> Color(0xFFDC2626)
          }
          Surface(
            color = posColor,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = player.position,
              color = Color.White,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "★ ${player.rating}",
              color = GoldYellow,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "${player.age} Yaş • ${player.nationality}",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(text = player.marketValueEur, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Text(text = "${player.tokenPrice} TP", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = player.name,
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp
      )
      Text(
        text = "${player.club} • ${player.league} • Durum: ${player.statusText}",
        color = Color(0xFFCBD5E1),
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = player.scoutingSummary,
        color = Color(0xFF94A3B8),
        fontSize = 10.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Beklenen Katkı: ${player.xgPerMatch}",
          color = Color(0xFFA7F3D0),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )

        Button(
          onClick = onInitiateTransfer,
          colors = ButtonDefaults.buttonColors(containerColor = if (userBalance >= player.tokenPrice) TealDark else Color(0xFF475569)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Transfer Et (${player.tokenPrice} TP)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun UserVirtualSquadTab() {
  val userTransfers by TransferMarketRepository.userVirtualTransfersFlow.collectAsState(initial = emptyList())

  if (userTransfers.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🏆", fontSize = 42.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Henüz Sanal Transfer Yapılmadı", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          "'Oyuncu Pazarı' sekmesinden sanal TP bakiyenizi kullanarak yıldız oyuncuları kadronuza katabilirsiniz.",
          color = Color(0xFF94A3B8),
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )
      }
    }
  } else {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
          border = BorderStroke(1.dp, Color(0xFF059669))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("SANAL TRANSFER EDİLEN OYUNCULAR", color = Color(0xFFA7F3D0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text("${userTransfers.size} Oyuncu Kadroda", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
            Text(
              "Toplam: ${userTransfers.sumOf { it.tokenPaid }} TP",
              color = GoldYellow,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      items(userTransfers, key = { it.id }) { item ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = TealDark, shape = RoundedCornerShape(4.dp)) {
                  Text(item.player.position, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(item.player.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
              }
              Text("-${item.tokenPaid} TP", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${item.player.club} • ${item.player.age} Yaş • Değer: ${item.player.marketValueEur}", color = Color(0xFF94A3B8), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.verdictSummary, color = Color(0xFFA7F3D0), fontSize = 10.sp)
          }
        }
      }
    }
  }
}

@Composable
private fun LiveTransfersAndKapTab() {
  val transfers by TransferMarketRepository.liveTransfersFlow.collectAsState(initial = emptyList())
  var filterCategory by remember { mutableStateOf("Tümü") }

  val filtered = remember(transfers, filterCategory) {
    if (filterCategory == "Tümü") transfers
    else transfers.filter { it.fromTeam.contains(filterCategory, true) || it.toTeam.contains(filterCategory, true) }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Top Ticker Info
    item {
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("TFF & FIFA Resmi Transfer Tescil Dönemi", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
            Text("Kamuoyu Aydınlatma Platformu (KAP) ve kulüp resmi bildirimleri anlık olarak sisteme düşmektedir.", color = Color(0xFF94A3B8), fontSize = 9.5.sp)
          }
        }
      }
    }

    // Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
      ) {
        items(listOf("Tümü", "Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor", "Avrupa")) { club ->
          val isSelected = filterCategory == club
          FilterChip(
            selected = isSelected,
            onClick = { filterCategory = club },
            label = { Text(club, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealDark,
              selectedLabelColor = Color.White,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFFCBD5E1)
            )
          )
        }
      }
    }

    // Transfer Items List
    items(filtered, key = { it.id }) { transfer ->
      LiveTransferCard(transfer = transfer)
    }
  }
}

@Composable
private fun LiveTransferCard(transfer: LiveTransferItem) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    border = BorderStroke(1.dp, Color(0xFF334155)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFF065F46),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = transfer.status,
              color = Color(0xFFA7F3D0),
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = transfer.date, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }

        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, GoldYellow)
        ) {
          Text(
            text = transfer.transferFee,
            color = GoldYellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Player and Route
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = transfer.playerName,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
          )
          Text(
            text = "${transfer.position} • ${transfer.age} Yaş • ${transfer.nationality}",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFF334155),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = transfer.fromTeam,
              color = Color.White,
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = TealDark,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = transfer.toTeam,
              color = Color.White,
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Financial & Contract Details
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("Sözleşme & Maaş", fontSize = 9.sp, color = Color(0xFF94A3B8))
          Text("${transfer.contractYears} Yıllık (${transfer.salaryAnnual})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("Finansal Fair Play", fontSize = 9.sp, color = Color(0xFF94A3B8))
          Text(transfer.ffpImpact, color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = transfer.announcementSummary,
        color = Color(0xFFCBD5E1),
        fontSize = 11.sp,
        lineHeight = 15.sp
      )
    }
  }
}

/**
 * 🤖 ALIM - SATIM AI MODU (Interactive Transfer Sandbox & Negotiation Evaluation)
 */
@Composable
private fun BuySellAiTradingModeTab() {
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val isEvaluating by TransferMarketRepository.isGeneratingAiTransfer.collectAsState(initial = false)

  var isSellingMode by remember { mutableStateOf(false) } // false: Satın Al, true: Sat
  var selectedClub by remember { mutableStateOf("Galatasaray") }
  var otherClub by remember { mutableStateOf("Napoli") }
  var targetPlayer by remember { mutableStateOf("Victor Osimhen") }
  var offeredFeeMillions by remember { mutableStateOf("75") }
  var offeredSalaryMillions by remember { mutableStateOf("6.0") }
  var lastAiEvaluation by remember { mutableStateOf<AiTransferEvaluation?>(null) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Mode Switcher: Alım vs Satım
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          color = if (!isSellingMode) TealDark else Color.Transparent,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .clickable {
              isSellingMode = false
              targetPlayer = "Victor Osimhen"
              otherClub = "Napoli"
              offeredFeeMillions = "75"
            }
        ) {
          Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = if (!isSellingMode) Color.White else Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Oyuncu Alım Modu", color = if (!isSellingMode) Color.White else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }

        Surface(
          color = if (isSellingMode) Color(0xFFDC2626) else Color.Transparent,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .clickable {
              isSellingMode = true
              targetPlayer = "Barış Alper Yılmaz"
              otherClub = "Aston Villa"
              offeredFeeMillions = "28"
            }
        ) {
          Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = if (isSellingMode) Color.White else Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Oyuncu Satış Modu", color = if (isSellingMode) Color.White else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    // Interactive Configuration Card
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = if (isSellingMode) "💰 OYUNCU SATIŞ VE GELİR STRATEJİSİ" else "🌟 YILDIZ OYUNCU TRANSFER TALEBİ",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          // Clubs Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(if (isSellingMode) "Satıcı Kulübünüz" else "Alıcı Kulübünüz", fontSize = 10.sp, color = Color(0xFF94A3B8))
              OutlinedTextField(
                value = selectedClub,
                onValueChange = { selectedClub = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White,
                  focusedBorderColor = TealDark,
                  unfocusedBorderColor = Color(0xFF475569)
                )
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(if (isSellingMode) "Talip Olan Kulüp" else "Hedef Kulüp", fontSize = 10.sp, color = Color(0xFF94A3B8))
              OutlinedTextField(
                value = otherClub,
                onValueChange = { otherClub = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White,
                  focusedBorderColor = TealDark,
                  unfocusedBorderColor = Color(0xFF475569)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Player Name
          Text("Hedef Oyuncu Adı", fontSize = 10.sp, color = Color(0xFF94A3B8))
          OutlinedTextField(
            value = targetPlayer,
            onValueChange = { targetPlayer = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = GoldYellow,
              unfocusedBorderColor = Color(0xFF475569)
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Fee & Salary Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Teklif Bonservis (€M)", fontSize = 10.sp, color = Color(0xFF94A3B8))
              OutlinedTextField(
                value = offeredFeeMillions,
                onValueChange = { offeredFeeMillions = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White,
                  focusedBorderColor = TealDark,
                  unfocusedBorderColor = Color(0xFF475569)
                )
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text("Yıllık Maaş (€M)", fontSize = 10.sp, color = Color(0xFF94A3B8))
              OutlinedTextField(
                value = offeredSalaryMillions,
                onValueChange = { offeredSalaryMillions = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White,
                  focusedBorderColor = TealDark,
                  unfocusedBorderColor = Color(0xFF475569)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Submit AI Evaluation Button
          Button(
            onClick = {
              val fee = offeredFeeMillions.toDoubleOrNull() ?: 15.0
              val sal = offeredSalaryMillions.toDoubleOrNull() ?: 3.0
              coroutineScope.launch {
                val eval = TransferMarketRepository.evaluateAiPlayerNegotiation(
                  playerName = targetPlayer,
                  buyerTeam = if (isSellingMode) otherClub else selectedClub,
                  sellerTeam = if (isSellingMode) selectedClub else otherClub,
                  offeredFeeMillions = fee,
                  offeredSalaryMillions = sal,
                  isSellingMode = isSellingMode
                )
                lastAiEvaluation = eval
              }
            },
            enabled = !isEvaluating && targetPlayer.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (isSellingMode) Color(0xFFDC2626) else TealDark)
          ) {
            if (isEvaluating) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Gemini AI Pazarlığı Simüle Ediyor...", fontWeight = FontWeight.Bold, color = Color.White)
            } else {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Gemini AI ile Transferi Değerlendir & Simüle Et", fontWeight = FontWeight.Black, color = Color.White)
            }
          }
        }
      }
    }

    // AI Negotiation Result Card
    lastAiEvaluation?.let { eval ->
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          border = BorderStroke(1.5.dp, if (eval.isAccepted) Color(0xFF10B981) else Color(0xFFF59E0B))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                color = if (eval.isAccepted) Color(0xFF065F46) else Color(0xFF78350F),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = eval.verdictTitle,
                  color = if (eval.isAccepted) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Text(
                text = "Kabul İhtimali: %${eval.probabilityPct}",
                color = GoldYellow,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = eval.verdictExplanation,
              color = Color.White,
              fontSize = 12.5.sp,
              fontWeight = FontWeight.SemiBold,
              lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Agent & Club Feedback
            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("🗣️ Menajer & Kulüp Yanıtı:", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(eval.agentFeedback, color = Color(0xFFCBD5E1), fontSize = 11.sp)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics Summary Grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Column(
                modifier = Modifier
                  .weight(1f)
                  .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                  .padding(8.dp)
              ) {
                Text("Finansal Fair Play", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
                Text(eval.ffpStatus, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
              }

              Column(
                modifier = Modifier
                  .weight(1f)
                  .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                  .padding(8.dp)
              ) {
                Text("Kadro Uyumu", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
                Text("${eval.squadSynergyScore} / 100", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
              }

              Column(
                modifier = Modifier
                  .weight(1f)
                  .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                  .padding(8.dp)
              ) {
                Text("Beklenen xG Etkisi", fontSize = 8.5.sp, color = Color(0xFF94A3B8))
                Text(eval.xgContributionEstimate, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
              }
            }
          }
        }
      }
    }
  }
}

/**
 * 📜 TRANSFER GEÇMİŞİ TAB (Historical Verified Transfers & Club Records)
 */
@Composable
private fun HistoricalTransfersTab() {
  val history = remember { TransferMarketRepository.getHistoricalTransfers() }

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
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.History, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Tescilli Resmi Transfer Geçmişi ve Bonservis Rekorları", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.5.sp)
            Text("Kulüplerin geçmiş dönemlerde tamamladığı rekor bonservisli oyuncu hareketleri.", color = Color(0xFF94A3B8), fontSize = 9.5.sp)
          }
        }
      }
    }

    items(history, key = { it.id }) { item ->
      HistoricalTransferCard(item = item)
    }
  }
}

@Composable
private fun HistoricalTransferCard(item: HistoricalTransfer) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    border = BorderStroke(1.dp, Color(0xFF334155)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = Color(0xFF334155),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(text = item.season, color = Color(0xFFCBD5E1), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }

        Surface(
          color = Color(0xFF0F172A),
          shape = RoundedCornerShape(4.dp),
          border = BorderStroke(1.dp, GoldYellow)
        ) {
          Text(text = item.feeFormatted, color = GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = item.playerName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        Text(text = "${item.fromClub} → ${item.toClub}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(text = item.notes, color = Color(0xFF94A3B8), fontSize = 10.sp, lineHeight = 14.sp)
    }
  }
}
