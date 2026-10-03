package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.util.formatOdd
import kotlin.random.Random

/**
 * 1. Sanal Basketbol Ligi (EuroLeague & BSL - Nesine / Bilyoner)
 */
@Composable
fun SanalBasketbolView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  var homeScore by remember { mutableIntStateOf(68) }
  var awayScore by remember { mutableIntStateOf(65) }
  var quarter by remember { mutableStateOf("Çeyrek 3") }
  var resultMessage by remember { mutableStateOf<String?>(null) }
  val stake = 100L

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 36.dp)
  ) {
    item {
      // Basketball Court Scoreboard
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFF854D0E), Color(0xFF451A03))
              )
            )
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(color = Color(0xFFDC2626), shape = RoundedCornerShape(4.dp)) {
              Text(
                text = "● CANLI BASKETBOL",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "$quarter • 04:18",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Teams & Score
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "🟡", fontSize = 24.sp)
              Text(text = "Fenerbahçe Beko", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow)
            ) {
              Text(
                text = "$homeScore - $awayScore",
                color = GoldYellow,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "🔵", fontSize = 24.sp)
              Text(text = "Anadolu Efes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Radar: Nigel Hayes-Davis köşe üçlüğü deniyor... Basket!",
            color = Color(0xFFFEF08A),
            fontSize = 10.sp,
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "HIZLI BASKETBOL MARKETLERİ",
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        color = Color(0xFF334155)
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Markets
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          "MS 1 (FB)" to 1.74,
          "MS 2 (Efes)" to 2.10
        ).forEach { (name, odd) ->
          Button(
            onClick = {
              if (onDeductStake(stake, "Sanal Basketbol: $name")) {
                val won = Random.nextBoolean()
                if (won) {
                  val winAmount = (stake * odd).toLong()
                  onAddWinnings(winAmount, "Sanal Basketbol Kazancı")
                  resultMessage = "🎉 $name isabetli! +${winAmount} TP kazandınız!"
                } else {
                  resultMessage = "❌ $name bahsi sonuçlanamadı."
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealDark),
            modifier = Modifier.weight(1f)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = name, fontSize = 11.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
              Text(text = odd.formatOdd(), fontSize = 12.sp, color = TealDark, fontWeight = FontWeight.Black)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          "164.5 Alt" to 1.88,
          "164.5 Üst" to 1.88
        ).forEach { (name, odd) ->
          Button(
            onClick = {
              if (onDeductStake(stake, "Sanal Basketbol: $name")) {
                val won = Random.nextBoolean()
                if (won) {
                  val winAmount = (stake * odd).toLong()
                  onAddWinnings(winAmount, "Sanal Basketbol Kazancı")
                  resultMessage = "🎉 $name isabetli! +${winAmount} TP kazandınız!"
                } else {
                  resultMessage = "❌ $name bahsi sonuçlanamadı."
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF64748B)),
            modifier = Modifier.weight(1f)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = name, fontSize = 11.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
              Text(text = odd.formatOdd(), fontSize = 12.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.Black)
            }
          }
        }
      }

      if (resultMessage != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = Color(0xFFDCFCE7),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = resultMessage!!,
            color = Color(0xFF15803D),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    }
  }
}

/**
 * 2. Sanal Tazı Yarışı (Greyhound Racing - Nesine / Bilyoner)
 */
@Composable
fun SanalTaziYarisiView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  val greyhounds = remember {
    listOf(
      Triple(1, "Şimşek", 2.10),
      Triple(2, "Fırtına", 3.20),
      Triple(3, "Roket", 4.40),
      Triple(4, "Rüzgar", 6.00),
      Triple(5, "Kasırga", 8.50),
      Triple(6, "Ateş", 12.00)
    )
  }

  var isRunning by remember { mutableStateOf(false) }
  val runProgress by animateFloatAsState(
    targetValue = if (isRunning) 1f else 0f,
    animationSpec = tween(3000),
    label = "GreyhoundRun"
  )
  var winnerDog by remember { mutableStateOf<Triple<Int, String, Double>?>(null) }
  var selectedDog by remember { mutableStateOf(greyhounds[0]) }
  var resultText by remember { mutableStateOf<String?>(null) }
  val stake = 100L

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 36.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "🐕 Londra Hipodromu 480m Sprint", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = "Tazı Yarışı 4", color = Color.White, fontSize = 11.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Sand Track
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(90.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF78350F))
              .padding(8.dp)
          ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize()) {
              greyhounds.take(4).forEach { dog ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = "${dog.first}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.width(4.dp))
                  LinearProgressIndicator(
                    progress = { if (isRunning) (runProgress * Random.nextDouble(0.9, 1.0)).toFloat().coerceIn(0f, 1f) else 0.05f },
                    modifier = Modifier.weight(1f).height(5.dp),
                    color = GoldYellow,
                    trackColor = Color(0xFF451A03)
                  )
                  Text(text = "🐕", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Text(text = "TAZI SEÇİMİ (100 TP)", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF334155))
      Spacer(modifier = Modifier.height(6.dp))
    }

    items(greyhounds.size) { idx ->
      val dog = greyhounds[idx]
      val isSelected = selectedDog == dog
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp)
          .clickable { selectedDog = dog },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE6FFFA) else Color.White),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, TealDark) else null
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier.size(22.dp).clip(CircleShape).background(Color(0xFF0F766E)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "${dog.first}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = dog.second, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
          }
          Text(text = "Ganyan: ${dog.third.formatOdd()}", fontWeight = FontWeight.Black, fontSize = 12.sp, color = TealDark)
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = {
          if (onDeductStake(stake, "Sanal Tazı: ${selectedDog.second}")) {
            isRunning = true
            winnerDog = greyhounds.random()
            val won = winnerDog == selectedDog
            if (won) {
              val winAmount = (stake * selectedDog.third).toLong()
              onAddWinnings(winAmount, "Tazı Yarışı Kazancı")
              resultText = "🏁 Kazanan: ${winnerDog?.second}! Tebrikler, +${winAmount} TP kazandınız!"
            } else {
              resultText = "🏁 Kazanan: ${winnerDog?.second}! Bu koşuda kazanamadınız."
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "🐕 Tazı Koşusunu Başlat (100 TP)", fontWeight = FontWeight.Black, fontSize = 12.sp)
      }

      if (resultText != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = Color(0xFFFEF3C7),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = resultText!!,
            color = Color(0xFF92400E),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    }
  }
}

/**
 * 3. Mayın Tarlası / Mines (Nesine Sanal Oyunlar)
 */
@Composable
fun MayinTarlasiView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  var gameActive by remember { mutableStateOf(false) }
  var currentMultiplier by remember { mutableDoubleStateOf(1.00) }
  var pickedCount by remember { mutableIntStateOf(0) }
  var gameOverMessage by remember { mutableStateOf<String?>(null) }
  val tilesState = remember { mutableStateListOf(*Array(25) { false }) } // revealed
  val minesIndices = remember { mutableStateListOf<Int>() }
  val stake = 100L

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(14.dp)
      .padding(bottom = 28.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header Dashboard
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "💣 MAYIN TARLASI (3 Mayın)", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
          Text(text = "Her elmas çarpanı katlar!", color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow)
        ) {
          Text(
            text = String.format(java.util.Locale.US, "%.2fx", currentMultiplier),
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 5x5 Grid
    Box(
      modifier = Modifier
        .size(280.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF1E293B))
        .padding(6.dp)
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items(25) { index ->
          val isRevealed = tilesState[index]
          val isMine = minesIndices.contains(index)
          Surface(
            color = when {
              isRevealed && isMine -> Color(0xFFDC2626)
              isRevealed -> Color(0xFF059669)
              else -> Color(0xFF334155)
            },
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
              .size(50.dp)
              .clickable(enabled = gameActive && !isRevealed) {
                tilesState[index] = true
                if (isMine) {
                  gameActive = false
                  gameOverMessage = "💥 Mayına bastınız! Bahis kaybedildi."
                } else {
                  pickedCount++
                  currentMultiplier = 1.00 + (pickedCount * 0.45)
                }
              }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = when {
                  isRevealed && isMine -> "💣"
                  isRevealed -> "💎"
                  else -> "❓"
                },
                fontSize = 18.sp
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (!gameActive) {
      Button(
        onClick = {
          if (onDeductStake(stake, "Mayın Tarlası Oyunu")) {
            for (i in 0 until 25) tilesState[i] = false
            minesIndices.clear()
            val rndIndices = (0 until 25).shuffled().take(3)
            minesIndices.addAll(rndIndices)
            pickedCount = 0
            currentMultiplier = 1.00
            gameOverMessage = null
            gameActive = true
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = TealDark),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "🎮 Oyunu Başlat (100 TP)", fontWeight = FontWeight.Black, fontSize = 13.sp)
      }
    } else {
      Button(
        onClick = {
          gameActive = false
          val won = (stake * currentMultiplier).toLong()
          onAddWinnings(won, "Mayın Tarlası Kazancı")
          gameOverMessage = "🎉 Tebrikler! ${String.format(java.util.Locale.US, "%.2fx", currentMultiplier)} ile bozdurdunuz: +${won} TP kazandınız!"
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        val curWin = (stake * currentMultiplier).toLong()
        Text(text = "💰 KASAYI BOZDUR (+${curWin} TP)", fontWeight = FontWeight.Black, fontSize = 13.sp)
      }
    }

    if (gameOverMessage != null) {
      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = gameOverMessage!!,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = Color(0xFF0F172A),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(10.dp)
        )
      }
    }
  }
}

/**
 * 4. Penaltı Atışları / Penalty Shootout (Nesine Sanal Oyunlar)
 */
@Composable
fun PenaltiAtislariView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  var streak by remember { mutableIntStateOf(0) }
  var currentMultiplier by remember { mutableDoubleStateOf(1.00) }
  var gameActive by remember { mutableStateOf(false) }
  var lastResultText by remember { mutableStateOf<String?>(null) }
  val stake = 100L

  val targetSpots = listOf(
    "↖ Sol Üst", "⬆ Orta", "↗ Sağ Üst",
    "↙ Sol Alt", "↘ Sağ Alt"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(14.dp)
      .padding(bottom = 28.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Goal Post Canvas representation
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF14532D), Color(0xFF064E3B))
            )
          )
          .padding(12.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "🧤 🥅 ⚽", fontSize = 34.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "PENALTI ATIŞI • ÇARPAN: ${String.format(java.util.Locale.US, "%.2fx", currentMultiplier)}",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
          Text(
            text = "Seri Gol: $streak • Bir köşe seçip şutunu çek!",
            color = Color(0xFF86EFAC),
            fontSize = 11.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "KÖŞENİ SEÇ VE ŞUTUNU ÇEK",
      fontWeight = FontWeight.Black,
      fontSize = 12.sp,
      color = Color(0xFF334155)
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Top 3 Corners
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      targetSpots.take(3).forEach { spot ->
        Button(
          onClick = {
            if (!gameActive) {
              if (onDeductStake(stake, "Penaltı Atışı")) {
                gameActive = true
              } else return@Button
            }
            val scored = Random.nextDouble() > 0.35
            if (scored) {
              streak++
              currentMultiplier *= 1.85
              lastResultText = "⚽ GOL! $spot köşesine mükemmel vuruş! Çarpan: ${String.format(java.util.Locale.US, "%.2fx", currentMultiplier)}"
            } else {
              gameActive = false
              streak = 0
              currentMultiplier = 1.00
              lastResultText = "🧤 KALECİ KURTARDI! $spot kurtarıldı, bahis kaybedildi."
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TealDark),
          modifier = Modifier.weight(1f)
        ) {
          Text(text = spot, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), textAlign = TextAlign.Center)
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Bottom 2 Corners
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      targetSpots.drop(3).forEach { spot ->
        Button(
          onClick = {
            if (!gameActive) {
              if (onDeductStake(stake, "Penaltı Atışı")) {
                gameActive = true
              } else return@Button
            }
            val scored = Random.nextDouble() > 0.35
            if (scored) {
              streak++
              currentMultiplier *= 1.85
              lastResultText = "⚽ GOL! $spot köşesine mükemmel vuruş! Çarpan: ${String.format(java.util.Locale.US, "%.2fx", currentMultiplier)}"
            } else {
              gameActive = false
              streak = 0
              currentMultiplier = 1.00
              lastResultText = "🧤 KALECİ KURTARDI! $spot kurtarıldı, bahis kaybedildi."
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TealDark),
          modifier = Modifier.weight(1f)
        ) {
          Text(text = spot, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), textAlign = TextAlign.Center)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (gameActive && streak > 0) {
      Button(
        onClick = {
          gameActive = false
          val won = (stake * currentMultiplier).toLong()
          onAddWinnings(won, "Penaltı Kazancı")
          lastResultText = "🎉 Tebrikler! ${streak} seri gol ile +${won} TP kazandınız!"
          streak = 0
          currentMultiplier = 1.00
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        val curWin = (stake * currentMultiplier).toLong()
        Text(text = "💰 KASAYI BOZDUR (+${curWin} TP)", fontWeight = FontWeight.Black, fontSize = 13.sp)
      }
    }

    if (lastResultText != null) {
      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        color = Color(0xFFFEF3C7),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = lastResultText!!,
          color = Color(0xFF92400E),
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(10.dp)
        )
      }
    }
  }
}

data class VirtualHorseItem(
  val number: Int,
  val name: String,
  val jockey: String,
  val odds: Double,
  val color: Color
)

/**
 * 5. Sanal At Yarışı (TJK Veliefendi 1200m)
 */
@Composable
fun SanalAtYarisiView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  val horses = remember {
    listOf(
      VirtualHorseItem(1, "Bold Pilot", "H. Karataş", 2.20, Color(0xFFDC2626)),
      VirtualHorseItem(2, "Gazi Fırtınası", "S. Kaya", 3.40, Color(0xFF2563EB)),
      VirtualHorseItem(3, "Rüzgarın Kızı", "A. Çelik", 4.50, Color(0xFF059669)),
      VirtualHorseItem(4, "Şampiyon", "G. Kocakaya", 6.80, Color(0xFFD97706)),
      VirtualHorseItem(5, "Karayel", "M. Kaya", 9.00, Color(0xFF7C3AED)),
      VirtualHorseItem(6, "Torok", "Ö. Yıldırım", 14.50, Color(0xFF475569))
    )
  }

  var isRacing by remember { mutableStateOf(false) }
  val raceProgress by animateFloatAsState(
    targetValue = if (isRacing) 1f else 0f,
    animationSpec = tween(4000),
    label = "HorseRace"
  )
  var winnerHorse by remember { mutableStateOf<VirtualHorseItem?>(null) }
  var selectedHorse by remember { mutableStateOf(horses[0]) }
  var raceResultText by remember { mutableStateOf<String?>(null) }
  val stake = 100L

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 36.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "🐎 Veliefendi Hipodromu 1200m Çim", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = "Koşu 6 • Ganyan", color = Color.White, fontSize = 11.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Turf Track
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(95.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF14532D))
              .padding(8.dp)
          ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize()) {
              horses.take(4).forEach { horse ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = "${horse.number}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.width(4.dp))
                  LinearProgressIndicator(
                    progress = { if (isRacing) (raceProgress * Random.nextDouble(0.85, 1.0)).toFloat().coerceIn(0f, 1f) else 0.05f },
                    modifier = Modifier.weight(1f).height(5.dp),
                    color = horse.color,
                    trackColor = Color(0xFF052E16)
                  )
                  Text(text = "🏇", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Text(text = "ATINI SEÇ & GANYAN OYNA (100 TP)", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF334155))
      Spacer(modifier = Modifier.height(6.dp))
    }

    items(horses.size) { index ->
      val horse = horses[index]
      val isSelected = selectedHorse == horse
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 3.dp)
          .clickable { selectedHorse = horse },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE6FFFA) else Color.White),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, TealDark) else null
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier.size(22.dp).clip(CircleShape).background(horse.color),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "${horse.number}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(text = horse.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
              Text(text = "Jokey: ${horse.jockey}", fontSize = 10.sp, color = Color(0xFF64748B))
            }
          }

          Text(
            text = "Ganyan: ${horse.odds}",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = TealDark
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = {
          if (onDeductStake(stake, "Sanal At Yarışı: ${selectedHorse.name}")) {
            isRacing = true
            winnerHorse = horses.random()
            val won = winnerHorse == selectedHorse
            if (won) {
              val wonAmount = (stake * selectedHorse.odds).toLong()
              onAddWinnings(wonAmount, "Sanal At Yarışı Ganyan Kazancı")
              raceResultText = "🏁 1. ${winnerHorse?.name}! Tebrikler, +${wonAmount} TP kazandınız!"
            } else {
              raceResultText = "🏁 1. ${winnerHorse?.name}! Bu koşuda kazanamadınız."
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "🏇 Koşuyu Başlat (100 TP)", fontWeight = FontWeight.Black, fontSize = 12.sp)
      }

      if (raceResultText != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = Color(0xFFFEF3C7),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = raceResultText!!,
            color = Color(0xFF92400E),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    }
  }
}

/**
 * 6. Zeplin / Aviator (Crash)
 */
@Composable
fun ZeplinCrashView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  var multiplier by remember { mutableDoubleStateOf(1.00) }
  var isFlying by remember { mutableStateOf(false) }
  var hasCrashed by remember { mutableStateOf(false) }
  var hasCashedOut by remember { mutableStateOf(false) }
  var resultText by remember { mutableStateOf<String?>(null) }
  val stake = 100L

  LaunchedEffect(isFlying) {
    if (isFlying) {
      hasCrashed = false
      hasCashedOut = false
      multiplier = 1.00
      val crashPoint = Random.nextDouble(1.15, 4.80)

      while (multiplier < crashPoint && isFlying) {
        kotlinx.coroutines.delay(120L)
        multiplier += 0.05
        if (multiplier >= crashPoint) {
          hasCrashed = true
          isFlying = false
          if (!hasCashedOut) {
            resultText = "💥 Zeplin ${String.format(java.util.Locale.US, "%.2f", crashPoint)}x seviyesinde patladı!"
          }
        }
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(14.dp)
      .padding(bottom = 28.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(190.dp),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = if (hasCrashed) "💥" else "🚀",
            fontSize = 40.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = String.format(java.util.Locale.US, "%.2fx", multiplier),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = if (hasCrashed) Color(0xFFEF4444) else if (hasCashedOut) Color(0xFF10B981) else GoldYellow
          )
          Text(
            text = if (isFlying) "Tırmanıyor..." else if (hasCrashed) "PATLADI!" else "Uçuşa Hazır",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (!isFlying) {
      Button(
        onClick = {
          if (onDeductStake(stake, "Zeplin Bahsi")) {
            isFlying = true
            resultText = null
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = TealDark),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "🚀 Uçuşu Başlat (100 TP)", fontWeight = FontWeight.Black, fontSize = 13.sp)
      }
    } else {
      Button(
        onClick = {
          if (!hasCashedOut && !hasCrashed) {
            hasCashedOut = true
            isFlying = false
            val winAmount = (stake * multiplier).toLong()
            onAddWinnings(winAmount, "Zeplin Bozdurma")
            resultText = "🎉 Tebrikler! ${String.format(java.util.Locale.US, "%.2f", multiplier)}x ile bozdurdunuz: +${winAmount} TP!"
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        val currentWin = (stake * multiplier).toLong()
        Text(text = "💰 ŞİMDİ BOZDUR (+${currentWin} TP)", fontWeight = FontWeight.Black, fontSize = 13.sp)
      }
    }

    if (resultText != null) {
      Spacer(modifier = Modifier.height(12.dp))
      Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = resultText!!,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = Color(0xFF0F172A),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(10.dp)
        )
      }
    }
  }
}

/**
 * 7. Spor Toto 15'li Bülteni (Nesine Spor Toto)
 */
@Composable
fun SporToto15View(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  val matches = remember {
    listOf(
      "Galatasaray - Fenerbahçe", "Beşiktaş - Trabzonspor", "Başakşehir - Sivasspor",
      "Kasımpaşa - Samsunspor", "Göztepe - Alanyaspor", "Antalyaspor - Rizespor",
      "Konyaspor - Kayserispor", "Gaziantep - Bodrumspor", "Eyüpspor - Hatayspor",
      "Real Madrid - Barcelona", "Arsenal - Manchester City", "Liverpool - Chelsea",
      "Inter - Juventus", "Bayern Münih - Dortmund", "PSG - Marsilya"
    )
  }

  val selections = remember { mutableStateListOf(*Array(15) { "1" }) }
  var ticketMessage by remember { mutableStateOf<String?>(null) }
  val cost = 50L

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 36.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "📋 SPOR TOTO 15. HAFTA", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Text(text = "Devreden Havuz: 15.450.000 TP", color = Color(0xFF86EFAC), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              val options = listOf("1", "X", "2")
              for (i in 0 until 15) selections[i] = options.random()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(text = "Rastgele Doldur", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
    }

    items(matches.size) { idx ->
      val match = matches[idx]
      Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${idx + 1}. $match",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
          )

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("1", "X", "2").forEach { opt ->
              val isSel = selections[idx] == opt
              Surface(
                color = if (isSel) TealDark else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                  .clickable { selections[idx] = opt }
                  .padding(1.dp)
              ) {
                Text(
                  text = opt,
                  color = if (isSel) Color.White else Color(0xFF334155),
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = {
          if (onDeductStake(cost, "Spor Toto 15 Kolon")) {
            val correctHits = Random.nextInt(9, 15)
            ticketMessage = "Kolonunuz onaylandı! Simülasyon Sonucu: $correctHits / 15 İsabet sağlandı."
            if (correctHits >= 13) {
              val bonus = 25000L
              onAddWinnings(bonus, "Spor Toto İkramiyesi")
              ticketMessage += " 🏆 Büyük ikramiye payı kazandınız: +${bonus} TP!"
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "Kolonu Onayla ($cost TP)", fontWeight = FontWeight.Black, fontSize = 12.sp)
      }

      if (ticketMessage != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = Color(0xFFFEF3C7),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = ticketMessage!!,
            color = Color(0xFF92400E),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    }
  }
}

