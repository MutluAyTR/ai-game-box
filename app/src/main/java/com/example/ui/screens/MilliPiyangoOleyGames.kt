package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * 1. Kazı Kazan: Altın Yumurta (Milli Piyango Online)
 * 6 scratchable tiles. Scratch to reveal symbols. 3 matching symbols win!
 */
@Composable
fun KaziKazanAltinYumurtaView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  val symbols = remember { listOf("💎", "🪙", "👑", "🍀", "⭐", "💰") }
  val gridSymbols = remember { mutableStateListOf(*Array(6) { "❓" }) }
  val scratched = remember { mutableStateListOf(*Array(6) { false }) }
  var gameActive by remember { mutableStateOf(false) }
  var resultText by remember { mutableStateOf<String?>(null) }
  val ticketCost = 50L

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 14.dp, vertical = 10.dp)
      .padding(bottom = 30.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF78350F), Color(0xFF451A03))
            )
          )
          .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = "🥚 KAZI KAZAN: ALTIN YUMURTA", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp)
        Text(text = "Milli Piyango Online • 3 Aynı Sembolü Bul, 5.000 TP'ye Varan İkramiye Kazan!", color = Color(0xFFFEF08A), fontSize = 10.sp, textAlign = TextAlign.Center)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2x3 Scratch Grid
    Box(
      modifier = Modifier
        .size(width = 280.dp, height = 190.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF1E293B))
        .padding(8.dp)
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(6) { idx ->
          val isScratched = scratched[idx]
          Surface(
            color = if (isScratched) Color(0xFFFEF3C7) else Color(0xFF94A3B8),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isScratched) GoldYellow else Color(0xFFCBD5E1)),
            modifier = Modifier
              .size(80.dp)
              .clickable(enabled = gameActive && !isScratched) {
                scratched[idx] = true
                if (scratched.all { it }) {
                  gameActive = false
                  val counts = gridSymbols.groupingBy { it }.eachCount()
                  val winning = counts.entries.find { it.value >= 3 }
                  if (winning != null) {
                    val prize = when (winning.key) {
                      "🍀" -> 5000L
                      "👑" -> 2500L
                      "💎" -> 1000L
                      else -> 300L
                    }
                    onAddWinnings(prize, "Kazı Kazan Altın Yumurta")
                    resultText = "🎉 TEBRİKLER! 3 Adet ${winning.key} buldunuz! +${prize} TP kazandınız!"
                  } else {
                    resultText = "❌ Bu kartta 3 eşleşen sembol çıkmadı. Şansınızı tekrar deneyin!"
                  }
                }
              }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = if (isScratched) gridSymbols[idx] else "🪙 KAZI",
                fontSize = if (isScratched) 28.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isScratched) Color.Unspecified else Color(0xFF1E293B)
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
          if (onDeductStake(ticketCost, "Kazı Kazan Bileti")) {
            val winLikely = Random.nextDouble() > 0.40
            val chosenWinSymbol = symbols.random()
            if (winLikely) {
              val list = mutableListOf(chosenWinSymbol, chosenWinSymbol, chosenWinSymbol)
              while (list.size < 6) list.add(symbols.random())
              list.shuffle()
              for (i in 0 until 6) gridSymbols[i] = list[i]
            } else {
              val shuffled = (symbols + symbols).shuffled().take(6)
              for (i in 0 until 6) gridSymbols[i] = shuffled[i]
            }
            for (i in 0 until 6) scratched[i] = false
            resultText = null
            gameActive = true
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = TealDark),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "🎫 Yeni Kart Satın Al (50 TP)", fontWeight = FontWeight.Black, fontSize = 13.sp)
      }
    } else {
      Button(
        onClick = {
          for (i in 0 until 6) scratched[i] = true
          gameActive = false
          val counts = gridSymbols.groupingBy { it }.eachCount()
          val winning = counts.entries.find { it.value >= 3 }
          if (winning != null) {
            val prize = 1000L
            onAddWinnings(prize, "Kazı Kazan Altın Yumurta")
            resultText = "🎉 TEBRİKLER! 3 Adet ${winning.key} buldunuz! +${prize} TP kazandınız!"
          } else {
            resultText = "❌ Bu kartta 3 eşleşen sembol çıkmadı. Şansınızı tekrar deneyin!"
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "✨ Tümünü Kazı", color = TealDark, fontWeight = FontWeight.Black, fontSize = 13.sp)
      }
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

/**
 * 2. Hızlı On / 20 Top Keno (Milli Piyango Online)
 * 80 numbers, pick 5 numbers, 20 glowing balls drawn!
 */
@Composable
fun HizliOnKenoView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  val selectedNumbers = remember { mutableStateListOf<Int>() }
  val drawnBalls = remember { mutableStateListOf<Int>() }
  var isDrawing by remember { mutableStateOf(false) }
  var resultMessage by remember { mutableStateOf<String?>(null) }
  val stake = 50L

  LaunchedEffect(isDrawing) {
    if (isDrawing) {
      drawnBalls.clear()
      val pool = (1..80).shuffled()
      for (i in 0 until 20) {
        delay(120L)
        drawnBalls.add(pool[i])
      }
      isDrawing = false

      val hits = selectedNumbers.count { drawnBalls.contains(it) }
      if (hits >= 2) {
        val multiplier = when (hits) {
          5 -> 250.0
          4 -> 30.0
          3 -> 5.0
          else -> 1.5
        }
        val won = (stake * multiplier).toLong()
        onAddWinnings(won, "Hızlı On Keno Kazancı")
        resultMessage = "🎉 TEBRİKLER! $hits numara isabet etti! ($multiplier katı) +${won} TP kazandınız!"
      } else {
        resultMessage = "❌ $hits isabet sağlandı. Şansınızı tekrar deneyin!"
      }
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 36.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "🎱 HIZLI ON / KENO (20 Top Çekilişi)", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 13.sp)
          Text(text = "5 numara seçin, 20 toptan eşleşenleri yakalayın! (50 TP)", color = Color(0xFF94A3B8), fontSize = 10.sp)

          Spacer(modifier = Modifier.height(8.dp))

          // Drawn Balls Container
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = if (drawnBalls.isEmpty()) "Çekiliş Bekleniyor..." else "Çekilen: ${drawnBalls.takeLast(8).joinToString(", ")}",
              color = Color(0xFF86EFAC),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "LÜTFEN 5 ŞANSLI NUMARA SEÇİN (${selectedNumbers.size} / 5)",
        fontWeight = FontWeight.Black,
        fontSize = 11.sp,
        color = Color(0xFF334155)
      )
      Spacer(modifier = Modifier.height(6.dp))
    }

    // Number Grid (1 to 40 for clean mobile sizing)
    item {
      LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items(40) { i ->
          val num = i + 1
          val isSel = selectedNumbers.contains(num)
          val isDrawn = drawnBalls.contains(num)

          Surface(
            color = when {
              isSel && isDrawn -> Color(0xFF10B981) // Matched!
              isSel -> GoldYellow
              isDrawn -> Color(0xFF0F766E)
              else -> Color.White
            },
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier
              .size(36.dp)
              .clickable(enabled = !isDrawing) {
                if (isSel) selectedNumbers.remove(num)
                else if (selectedNumbers.size < 5) selectedNumbers.add(num)
              }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "$num",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isSel || isDrawn) Color.White else Color(0xFF0F172A)
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(12.dp))
      Button(
        onClick = {
          if (selectedNumbers.size == 5) {
            if (onDeductStake(stake, "Hızlı On Keno")) {
              resultMessage = null
              isDrawing = true
            }
          }
        },
        enabled = selectedNumbers.size == 5 && !isDrawing,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = if (isDrawing) "🎱 Toplar Çekiliyor..." else "🎱 Çekilişi Başlat (50 TP)", fontWeight = FontWeight.Black, fontSize = 13.sp)
      }

      if (resultMessage != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = Color(0xFFFEF3C7),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = resultMessage!!,
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
 * 3. Süper Çark (Oley Şans Oyunları / Dream Catcher)
 * Multipliers: 1x, 2x, 5x, 10x, 20x, 40x.
 */
@Composable
fun SuperCarkOleyView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  val multiplierSegments = listOf(1, 2, 5, 10, 20, 40)
  var selectedMultiplier by remember { mutableIntStateOf(2) }
  var isSpinning by remember { mutableStateOf(false) }
  var winningSegment by remember { mutableIntStateOf(1) }
  var outcomeText by remember { mutableStateOf<String?>(null) }
  val stake = 100L

  val spinRotation by animateFloatAsState(
    targetValue = if (isSpinning) 1800f + (winningSegment * 60f) else 0f,
    animationSpec = tween(3500),
    label = "SuperWheelSpin"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 14.dp, vertical = 10.dp)
      .padding(bottom = 30.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "🎡", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isSpinning) "ÇARK DÖNÜYOR..." else "KAZANAN: ${winningSegment}x",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
          )
          Text(text = "Oley.com Süper Çark • 40x'e varan dev çarpanlar!", color = Color(0xFFC7D2FE), fontSize = 10.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "KAZANACAK ÇARPANI SEÇ (100 TP)",
      fontWeight = FontWeight.Black,
      fontSize = 11.sp,
      color = Color(0xFF334155)
    )

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      multiplierSegments.forEach { mult ->
        val isSel = selectedMultiplier == mult
        Button(
          onClick = { selectedMultiplier = mult },
          colors = ButtonDefaults.buttonColors(containerColor = if (isSel) GoldYellow else Color.White),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, TealDark),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "${mult}x",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = if (isSel) TealDark else Color(0xFF0F172A)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Button(
      onClick = {
        if (onDeductStake(stake, "Süper Çark (${selectedMultiplier}x)")) {
          isSpinning = true
          outcomeText = null
          winningSegment = multiplierSegments.random()

          val won = winningSegment == selectedMultiplier
          if (won) {
            val winAmount = stake * selectedMultiplier
            onAddWinnings(winAmount, "Süper Çark Kazancı")
            outcomeText = "🎉 TEBRİKLER! Çark ${winningSegment}x geldi! +${winAmount} TP kazandınız!"
          } else {
            outcomeText = "🎡 Çark ${winningSegment}x geldi. Bu turda kazanamadınız."
          }
        }
      },
      enabled = !isSpinning,
      colors = ButtonDefaults.buttonColors(containerColor = TealDark),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(text = "🎡 Çarkı Döndür (100 TP)", fontWeight = FontWeight.Black, fontSize = 13.sp)
    }

    if (outcomeText != null) {
      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        color = Color(0xFFFEF3C7),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = outcomeText!!,
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

/**
 * 4. Şanslı Arabalar / Hız Pisti (Milli Piyango Online)
 * 6 formula / oval track race cars, rapid sprint to finish line.
 */
@Composable
fun SansliArabalarView(
  walletPoints: Long,
  onDeductStake: (Long, String) -> Boolean,
  onAddWinnings: (Long, String) -> Unit
) {
  val cars = remember {
    listOf(
      Triple(1, "Ferrari Kırmızı", 2.20),
      Triple(2, "Mercedes Gümüş", 3.10),
      Triple(3, "Red Bull Mavi", 3.90),
      Triple(4, "McLaren Turuncu", 5.50),
      Triple(5, "Aston Martin Yeşil", 7.80),
      Triple(6, "Porsche Siyah", 11.00)
    )
  }

  var isRacing by remember { mutableStateOf(false) }
  var selectedCar by remember { mutableStateOf(cars[0]) }
  var outcomeText by remember { mutableStateOf<String?>(null) }
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
      ) {
        Column(
          modifier = Modifier.padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "🏎️ İSTANBUL PARK HIZ PİSTİ", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Text(text = "Şanslı Arabalar Pro", color = Color(0xFF93C5FD), fontSize = 11.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Asphalt Track
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(90.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF1E293B))
              .padding(8.dp)
          ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize()) {
              cars.take(4).forEach { car ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = "${car.first}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.width(4.dp))
                  LinearProgressIndicator(
                    progress = { if (isRacing) Random.nextDouble(0.8, 1.0).toFloat() else 0.05f },
                    modifier = Modifier.weight(1f).height(5.dp),
                    color = GoldYellow,
                    trackColor = Color(0xFF334155)
                  )
                  Text(text = "🏎️", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Text(text = "ŞAMPİYON ARABANI SEÇ (100 TP)", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color(0xFF334155))
      Spacer(modifier = Modifier.height(6.dp))
    }

    items(cars.size) { idx ->
      val car = cars[idx]
      val isSel = selectedCar == car
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp)
          .clickable { selectedCar = car },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSel) Color(0xFFEFF6FF) else Color.White),
        border = if (isSel) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2563EB)) else null
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🏎️ ${car.first}. ${car.second}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
          }
          Text(text = "Oran: ${car.third}", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF2563EB))
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = {
          if (onDeductStake(stake, "Şanslı Arabalar: ${selectedCar.second}")) {
            isRacing = true
            val winner = cars.random()
            val won = winner == selectedCar
            if (won) {
              val winAmount = (stake * selectedCar.third).toLong()
              onAddWinnings(winAmount, "Şanslı Arabalar Kazancı")
              outcomeText = "🏁 1. ${winner.second}! Tebrikler, +${winAmount} TP kazandınız!"
            } else {
              outcomeText = "🏁 1. ${winner.second}! Bu yarışta kazanamadınız."
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "🏎️ Yarışı Başlat (100 TP)", fontWeight = FontWeight.Black, fontSize = 12.sp)
      }

      if (outcomeText != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = Color(0xFFFEF3C7),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = outcomeText!!,
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
