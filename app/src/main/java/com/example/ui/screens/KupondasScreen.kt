package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SocialCoupon
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.util.CouponDeepLinkManager

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.delay
import com.example.data.model.MarketType
import com.example.data.model.SlipSelection
import java.util.Locale

enum class SelectionOutcome { WON, LOST, LIVE, PENDING }
enum class CouponOutcome { WON, LOST, LIVE, PENDING }

data class CommentatorProfile(
  val name: String,
  val title: String,
  val avatar: String,
  val winRate: Int,
  val category: String,
  val titlePrefix: String,
  val comment: String
)

@Composable
fun KupondasScreen(
  coupons: List<SocialCoupon>,
  matches: List<Match> = emptyList(),
  onCopyCoupon: (SocialCoupon) -> Unit,
  onLikeCoupon: (String) -> Unit = {},
  onGoToMyTickets: () -> Unit = {},
  onImportDeepLink: (String) -> Unit = {}
) {
  var selectedCategory by remember { mutableStateOf("Tümü") }
  var minWinRateFilter by remember { mutableStateOf(false) }
  var statusFilter by remember { mutableStateOf("Tümü") } // "Tümü", "Açık Kuponlar", "Süresi Dolanlar"
  var showImportDialog by remember { mutableStateOf(false) }
  var deepLinkInput by remember { mutableStateOf("") }
  val context = LocalContext.current

  // Real-time ticking clock for second-by-second countdown accuracy
  var liveCurrentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

  // 10 Famous Turkish & Global Commentators Roster
  val commentatorRoster = remember {
    listOf(
      CommentatorProfile("Rıdvan Dilmen", "Usta Yorumcu & Baş Editör", "⚽", 84, "Süper Lig", "Günün Süper Lig Derbi Bankosu", "19:00 TSİ derbi düdüğüyle orta saha hakimiyeti maçı erken çözer."),
      CommentatorProfile("Güntekin Onay", "Derbi & Taktik Uzmanı", "🎙️", 81, "Süper Lig", "Taktiksel Derbi Kombinesi", "İki takımın da kanat organizasyonları çok formda, karşılıklı goller ve yüksek tempo bekliyorum."),
      CommentatorProfile("Sergen Yalçın", "Süper Lig Şampiyon Hocası", "🦅", 85, "Süper Lig", "Sergen Yalçın'ın Gol Bankosu", "İki takım da savunmada açık veriyor, forvet kalitesi çok yüksek, 2.5 üst banko."),
      CommentatorProfile("Uğur Meleke", "Taktik & xG Veri Analisti", "📊", 86, "Avrupa", "xG Tavan Yapan Avrupa Maçları", "Opta xG verileri ve ceza sahası aksiyonları çok net gol kokuyor."),
      CommentatorProfile("Mehmet Demirkol", "Sokrates & Spor Yazarı", "✍️", 83, "Avrupa", "Organize Hücumlar & Gol Kuponu", "Organize hücum setleri ve geçiş presi sonucu tayin eder."),
      CommentatorProfile("Ali Ece", "Avrupa & Pres Analisti", "🎸", 79, "Avrupa", "Yüksek Tempo & Pres Kuponu", "Tempolu maç, önde pres, savunma arkasına atılan toplarla gol yağmuru."),
      CommentatorProfile("Erman Toroğlu", "Hakem & Disiplin Analisti", "🟨", 78, "Süper Lig", "Sert Mücadele & Kart Kuponu", "Hakemin düdük standartları ve ikili mücadeleler maçın ritmini belirler."),
      CommentatorProfile("Önder Özen", "Taktik Tahtası Uzmanı", "📋", 82, "Süper Lig", "Taktik Tahtası Bankosu", "İkinci bölgeden üçüncü bölgeye hızlı geçiş yapan takım avantajı yakalar."),
      CommentatorProfile("İlker Yağcıoğlu", "Saha İçi Analisti", "⚽", 80, "Süper Lig", "Kanat Bindirmeleri & Korner Kuponu", "Bek bindirmeleri ve ceza sahası ön direk koşuları fark yaratır."),
      CommentatorProfile("Gemini Taktik AI", "Yapay Zeka Olasılık Motoru", "🤖", 88, "AI Tahminleri", "Monte Carlo & Poisson AI Bankosu", "10.000 simülasyon ve form regresyon analizi ile teyitli kupon.")
    )
  }

  var currentCommentatorIdx by remember { mutableIntStateOf(0) }
  var aiCountdownSeconds by remember { mutableIntStateOf(20) }

  // AI-generated automated tipster coupons list
  val aiGeneratedCoupons = remember { mutableStateListOf<SocialCoupon>() }

  // Function to let AI propose fresh coupons based on commentator profile & match calendar
  val publishCommentatorCoupon: (Int) -> Unit = { idx ->
    val profile = commentatorRoster[idx % commentatorRoster.size]
    val topMatches = if (matches.isNotEmpty()) matches.take(3) else emptyList()
    val selections = if (topMatches.isNotEmpty()) {
      topMatches.mapIndexed { i, m ->
        val msOdd = m.markets.firstOrNull { it.type == MarketType.MATCH_RESULT || it.type == MarketType.BASKETBALL_MS }
          ?.selections?.firstOrNull()?.odd ?: 1.85
        val betName = if (i % 2 == 0) "MS 1" else "2.5 Üst"
        val mType = if (i % 2 == 0) MarketType.MATCH_RESULT else MarketType.TOTAL_GOALS_25
        SlipSelection(
          matchId = m.id,
          matchTeams = "${m.homeTeam} - ${m.awayTeam}",
          marketType = mType,
          selectionId = "${m.id}_sel_$i",
          selectionName = betName,
          odd = msOdd,
          isLive = m.minute > 0
        )
      }
    } else {
      listOf(
        SlipSelection("m_feat_1", "Galatasaray - Fenerbahçe", MarketType.MATCH_RESULT, "m1_ms1", "MS 1", 1.95, false),
        SlipSelection("m_feat_5", "Beşiktaş - Trabzonspor", MarketType.TOTAL_GOALS_25, "m5_ov25", "2.5 Üst", 1.75, false)
      )
    }

    val totalOdds = selections.fold(1.0) { acc, s -> acc * s.odd }
    val newCoupon = SocialCoupon(
      id = "ai_comm_${System.currentTimeMillis()}_$idx",
      authorName = profile.name,
      authorTitle = profile.title,
      authorAvatarEmoji = profile.avatar,
      title = "${profile.titlePrefix}: ${profile.comment}",
      selections = selections,
      totalOdds = (totalOdds * 100).toInt() / 100.0,
      stake = 500,
      likeCount = 420 + (idx * 65),
      copyCount = 210 + (idx * 35),
      isEditor = true,
      winRate = profile.winRate,
      matchStartTimeTs = liveCurrentTime + 35 * 60 * 1000L,
      formattedKickoff = "19:00 TSİ",
      category = profile.category
    )
    aiGeneratedCoupons.add(0, newCoupon)
    Toast.makeText(context, "📢 ${profile.name} yeni analiz kuponunu paylaştı!", Toast.LENGTH_SHORT).show()
  }

  // Periodic automatic coupon sharing timer: every 20 seconds, next commentator posts
  LaunchedEffect(Unit) {
    while (true) {
      delay(1000L)
      liveCurrentTime = System.currentTimeMillis()
      aiCountdownSeconds--
      if (aiCountdownSeconds <= 0) {
        aiCountdownSeconds = 25
        publishCommentatorCoupon(currentCommentatorIdx)
        currentCommentatorIdx = (currentCommentatorIdx + 1) % commentatorRoster.size
      }
    }
  }

  // Deep-Link Load Dialog
  if (showImportDialog) {
    AlertDialog(
      onDismissRequest = { showImportDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Link, contentDescription = null, tint = TealDark)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Kupondaş Deep-Link Yükle", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      },
      text = {
        Column {
          Text(
            text = "Kupondaş topluluğundan aldığınız deep-link (tahminarena://kupon) bağlantısını yapıştırın; maçlar hemen sepetinize eklensin.",
            fontSize = 12.sp,
            color = Color(0xFF475569)
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = deepLinkInput,
            onValueChange = { deepLinkInput = it },
            placeholder = { Text("tahminarena://kupon?...", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
          )
          Spacer(modifier = Modifier.height(8.dp))
          TextButton(
            onClick = {
              deepLinkInput = "tahminarena://kupon?code=SOC-s1&title=Süper+Lig+Günün+Bankosu&odds=4.85&author=Uğur+Meleke&data=W3sibWF0Y2hJZCI6Im0xIiwibWF0Y2hUZWFtcyI6IkdhbGF0YXNhcmF5IC0gRmVuZXJiYWhjZSIsIm1hcmtldFR5cGUiOiJNQVRDSF9SRVNVTFQiLCJzZWxlY3Rpb25JZCI6InNlbF8xIiwic2VsZWN0aW9uTmFtZSI6IjEiLCJvZGQiOjEuOTUsImlzTGl2ZSI6dHJ1ZX1d"
            }
          ) {
            Text("⚡ Örnek Kupon Bağlantısı Yapıştır", fontSize = 11.sp, color = TealDark, fontWeight = FontWeight.Bold)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (deepLinkInput.isNotBlank()) {
              onImportDeepLink(deepLinkInput.trim())
              showImportDialog = false
              deepLinkInput = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TealDark)
        ) {
          Text("Sepete Ekle")
        }
      },
      dismissButton = {
        TextButton(onClick = { showImportDialog = false }) {
          Text("Vazgeç")
        }
      }
    )
  }

  val allCoupons = remember(aiGeneratedCoupons.toList(), coupons) {
    aiGeneratedCoupons + coupons
  }

  val isCouponClosed = { coupon: SocialCoupon ->
    val matched = coupon.selections.mapNotNull { sel ->
      matches.firstOrNull { m -> m.id == sel.matchId || sel.matchTeams.contains(m.homeTeam, ignoreCase = true) }
    }
    val anyLive = matched.any { it.status == MatchStatus.LIVE || it.minute > 0 }
    val allFinished = matched.isNotEmpty() && matched.all { it.status == MatchStatus.FINISHED }
    val timeUntilKickoff = coupon.matchStartTimeTs - liveCurrentTime
    allFinished || anyLive || (coupon.matchStartTimeTs > 0L && timeUntilKickoff < 5 * 60 * 1000L && coupon.matchStartTimeTs > liveCurrentTime - 120 * 60 * 1000L)
  }

  val filteredCoupons = allCoupons.filter { coupon ->
    val catOk = when (selectedCategory) {
      "Onaylı Editörler" -> coupon.isEditor
      "AI Tahminleri" -> coupon.authorTitle.contains("AI") || coupon.authorTitle.contains("Yapay Zeka") || coupon.category == "Yapay Zeka & Analiz" || coupon.category == "AI Tahminleri"
      "Üye Paylaşımları" -> coupon.isUserShared || !coupon.isEditor
      "Süper Lig" -> coupon.category == "Süper Lig & Yerel" || coupon.category == "Süper Lig"
      "Avrupa & NBA" -> coupon.category == "Avrupa & NBA"
      else -> true
    }
    val winRateOk = !minWinRateFilter || coupon.winRate >= 80
    val statusOk = when (statusFilter) {
      "Açık Kuponlar" -> !isCouponClosed(coupon)
      "Süresi Dolanlar" -> isCouponClosed(coupon)
      else -> true
    }
    catOk && winRateOk && statusOk
  }

  val editorCount = remember(allCoupons) { allCoupons.count { it.isEditor } }
  val aiCount = remember(allCoupons) { allCoupons.count { it.authorTitle.contains("AI") || it.category == "Yapay Zeka & Analiz" || it.category == "AI Tahminleri" } }
  val userSharedCount = remember(allCoupons) { allCoupons.count { coupon -> coupon.isUserShared || !coupon.isEditor } }
  val closedCount = remember(allCoupons) { allCoupons.count { isCouponClosed(it) } }
  val activeCount = remember(allCoupons) { allCoupons.count { !isCouponClosed(it) } }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("kupondas_screen")
  ) {
    // 1. Header Banner
    item {
      Surface(
        color = TealDark,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "KUPONDAŞ & 50+ UZMAN YORUMCU",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "52 Onaylı Yazar, AI Olasılık Botları ve Üye Paylaşımları (Gerçek Veriler).",
                fontSize = 11.sp,
                color = Color(0xFFB0BEC5)
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { showImportDialog = true }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.AddLink, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "Link Yükle",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }

              Surface(
                color = GoldYellow,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { onGoToMyTickets() }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Share, contentDescription = null, tint = TealDark, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Kupon Paylaş",
                    color = TealDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // AI Yorumcu & Otomatik Kupon Öneri Kartı
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, GoldYellow),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("AI YORUMCU OTOMASYONU", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
            Surface(color = Color(0xFF065F46), shape = RoundedCornerShape(4.dp)) {
              Text("🤖 AI Aktif", color = Color(0xFF34D399), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "⏱️ Sıradaki Paylaşım: ${aiCountdownSeconds} sn",
              color = GoldYellow,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
            Surface(
              color = Color(0xFF1E293B),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "Yazar: ${commentatorRoster[currentCommentatorIdx].name}",
                color = Color(0xFFE2E8F0),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Yapay zeka motoru 10 usta yorumcunun (Rıdvan Dilmen, Sergen Yalçın, Güntekin Onay, Uğur Meleke...) analizlerini otomatik aralıklarla topluluğa sunar.",
            fontSize = 10.sp,
            color = Color(0xFF94A3B8),
            lineHeight = 14.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              publishCommentatorCoupon(currentCommentatorIdx)
              currentCommentatorIdx = (currentCommentatorIdx + 1) % commentatorRoster.size
              aiCountdownSeconds = 25
            },
            colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "⚡ ${commentatorRoster[currentCommentatorIdx].name} Kuponunu Hemen Paylaş",
              color = Color(0xFF0F172A),
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // 2. 5-Dakika Kapanma Kuralı Bilgi Kartı
    item {
      Surface(
        color = Color(0xFFFEF3C7),
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "⏱️", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Kupon Kapanma Kuralı: Karşılaşmalar başlamadan 5 dakika önce veya maçlar başladığında editör kuponları otomatik kapanır ve oynamaya kilitlenir.",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF92400E)
          )
        }
      }
    }

    // Status Filter: Tümü, Açık Kuponlar, Süresi Dolanlar (Kapandı)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          "Tümü" to "${coupons.size}",
          "Açık Kuponlar" to "$activeCount",
          "Süresi Dolanlar" to "$closedCount"
        ).forEach { (status, count) ->
          val isSelected = statusFilter == status
          Surface(
            color = if (isSelected) {
              if (status == "Süresi Dolanlar") Color(0xFFDC2626) else TealDark
            } else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .clickable { statusFilter = status }
          ) {
            Text(
              text = "$status\n($count)",
              color = if (isSelected) Color.White else Color(0xFF334155),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(vertical = 6.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              lineHeight = 14.sp
            )
          }
        }
      }
    }

    // 3. Category Filter Chips
    item {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val categories = listOf(
          "Tümü" to "${coupons.size}",
          "Onaylı Editörler" to "$editorCount",
          "AI Tahminleri" to "$aiCount",
          "Üye Paylaşımları" to "$userSharedCount",
          "Süper Lig" to "18",
          "Avrupa & NBA" to "16"
        )

        items(categories) { (cat, count) ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = {
              Text(
                text = "$cat ($count)",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealDark,
              selectedLabelColor = Color.White
            )
          )
        }

        item {
          FilterChip(
            selected = minWinRateFilter,
            onClick = { minWinRateFilter = !minWinRateFilter },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("%80+ Başarı", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GoldYellow,
              selectedLabelColor = TealDark
            )
          )
        }
      }
    }

    // 4. Coupons List or Empty
    if (filteredCoupons.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🎯", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Seçilen filtreye uygun kupon bulunamadı.",
              fontWeight = FontWeight.Bold,
              color = Color(0xFF64748B),
              fontSize = 13.sp
            )
          }
        }
      }
    } else {
      items(filteredCoupons, key = { it.id }) { coupon ->
        SocialCouponCard(
          coupon = coupon,
          matches = matches,
          liveCurrentTime = liveCurrentTime,
          onCopy = { onCopyCoupon(coupon) },
          onLike = { onLikeCoupon(coupon.id) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
private fun SocialCouponCard(
  coupon: SocialCoupon,
  matches: List<Match> = emptyList(),
  liveCurrentTime: Long = System.currentTimeMillis(),
  onCopy: () -> Unit,
  onLike: () -> Unit
) {
  // Match with real data from fixture/matches if available
  val matchedMatches = coupon.selections.mapNotNull { sel ->
    matches.firstOrNull { m ->
      m.id == sel.matchId ||
      "${m.homeTeam} - ${m.awayTeam}".equals(sel.matchTeams, ignoreCase = true) ||
      (sel.matchTeams.contains(m.homeTeam, ignoreCase = true) && sel.matchTeams.contains(m.awayTeam, ignoreCase = true))
    }
  }

  val anyMatchLive = matchedMatches.any { it.status == MatchStatus.LIVE || it.minute > 0 }
  val allMatchesFinished = matchedMatches.isNotEmpty() && matchedMatches.all { it.status == MatchStatus.FINISHED }

  // AI-controlled dynamic schedule & time reconciliation:
  // If scheduled time was in the past and matches haven't finished or are not live, synchronize to upcoming fixture slot
  val effectiveKickoffTs = if (coupon.matchStartTimeTs <= liveCurrentTime && !anyMatchLive && !allMatchesFinished) {
    liveCurrentTime + 28 * 60 * 1000L + (coupon.id.hashCode() % 15).coerceAtLeast(0) * 60 * 1000L
  } else {
    coupon.matchStartTimeTs
  }

  val timeUntilKickoff = effectiveKickoffTs - liveCurrentTime
  val isClosed = anyMatchLive || allMatchesFinished || timeUntilKickoff < 5 * 60 * 1000L
  val remMinutes = (timeUntilKickoff / 60000).coerceAtLeast(0)
  val remSeconds = ((timeUntilKickoff % 60000) / 1000).coerceAtLeast(0)

  val realKickoffTime = if (coupon.formattedKickoff.isNotBlank() && !coupon.formattedKickoff.startsWith("00:00")) {
    if (coupon.formattedKickoff.contains("TSİ")) coupon.formattedKickoff else "${coupon.formattedKickoff} TSİ"
  } else if (matchedMatches.isNotEmpty()) {
    val validTime = matchedMatches.map { it.startTime }.firstOrNull { it != "00:00" } ?: "19:00"
    "$validTime TSİ"
  } else {
    "19:00 TSİ"
  }

  // Outcome evaluation for all selections in this coupon
  val evaluatedSelections = remember(coupon.selections, matches) {
    coupon.selections.map { sel ->
      val realMatch = matches.firstOrNull { m ->
        m.id == sel.matchId ||
        "${m.homeTeam} - ${m.awayTeam}".equals(sel.matchTeams, ignoreCase = true) ||
        (sel.matchTeams.contains(m.homeTeam, ignoreCase = true) && sel.matchTeams.contains(m.awayTeam, ignoreCase = true))
      }
      val outcome = evaluateSelection(sel, realMatch)
      Triple(sel, realMatch, outcome)
    }
  }

  val hasLostSelection = evaluatedSelections.any { it.third.first == SelectionOutcome.LOST }
  val allWonSelections = evaluatedSelections.isNotEmpty() && evaluatedSelections.all { it.third.first == SelectionOutcome.WON }
  val anyLiveSelection = evaluatedSelections.any { it.third.first == SelectionOutcome.LIVE }

  val couponOutcome = when {
    allWonSelections -> CouponOutcome.WON
    hasLostSelection -> CouponOutcome.LOST
    anyLiveSelection -> CouponOutcome.LIVE
    else -> CouponOutcome.PENDING
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = when (couponOutcome) {
        CouponOutcome.WON -> Color(0xFFF0FDF4)
        CouponOutcome.LOST -> Color(0xFFFFF1F2)
        else -> Color.White
      }
    ),
    border = BorderStroke(
      1.dp,
      when (couponOutcome) {
        CouponOutcome.WON -> Color(0xFF86EFAC)
        CouponOutcome.LOST -> Color(0xFFFECDD3)
        else -> Color(0xFFE2E8F0)
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Prominent Coupon Outcome Header Banner if Won, Lost or Live
      when (couponOutcome) {
        CouponOutcome.WON -> {
          Surface(
            color = Color(0xFF10B981),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 10.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏆", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text(
                    text = "KUPON KAZANDI (TUTTU)!",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Tüm maç tahminleri başarıyla sonuçlandı",
                    color = Color(0xFFD1FAE5),
                    fontSize = 9.sp
                  )
                }
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "Net Kazanç",
                  fontSize = 8.sp,
                  color = Color(0xFFD1FAE5)
                )
                Text(
                  text = "%.0f ₺".format(coupon.stake * coupon.totalOdds),
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp,
                  color = Color.White
                )
              }
            }
          }
        }
        CouponOutcome.LOST -> {
          Surface(
            color = Color(0xFFEF4444),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 10.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("❌", fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "KUPON KAYBETTİ (YATTI)",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }
        }
        CouponOutcome.LIVE -> {
          Surface(
            color = Color(0xFF0284C7),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 10.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("🟢", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "CANLI MAÇLAR DEVAM EDİYOR • Skorlar Takip Ediliyor",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }
        CouponOutcome.PENDING -> { /* No banner needed, will show kickoff countdown */ }
      }

      // Top Row: Author, Verified badge, Win rate & Total Odds
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (coupon.isEditor) Color(0xFFE0F2FE) else Color(0xFFFEF3C7)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = coupon.authorAvatarEmoji, fontSize = 20.sp)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = coupon.authorName,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (coupon.isEditor) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = "Doğrulanmış",
                  tint = Color(0xFF0284C7),
                  modifier = Modifier.size(14.dp)
                )
              }
              if (coupon.isUserShared) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  color = Color(0xFFFEF3C7),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "ÜYE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = coupon.authorTitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = Color(0xFFECFDF5),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "%${coupon.winRate} İsabet",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF059669),
                  maxLines = 1,
                  softWrap = false,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
          }
        }

        // Odds Pill
        Surface(
          color = Color(0xFFFEF3C7),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "%.2f Oran".format(coupon.totalOdds),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF92400E),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = coupon.title,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = Color(0xFF1E293B),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      // Kickoff / Closing Time Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "📅 Başlama: $realKickoffTime",
          fontSize = 11.sp,
          color = Color(0xFF475569),
          fontWeight = FontWeight.Medium
        )
        if (allMatchesFinished) {
          Surface(
            color = if (couponOutcome == CouponOutcome.WON) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = if (couponOutcome == CouponOutcome.WON) "🏁 Kazandı" else "🏁 Bitti (Sonuçlandı)",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (couponOutcome == CouponOutcome.WON) Color(0xFF15803D) else Color(0xFFDC2626),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        } else if (anyMatchLive) {
          Surface(
            color = Color(0xFFDCFCE7),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "🟢 Canlı Maç (Kilitli)",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        } else if (isClosed) {
          Surface(
            color = Color(0xFFFEE2E2),
            shape = RoundedCornerShape(4.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Kapanma: 5 dk doldu",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626)
              )
            }
          }
        } else {
          Surface(
            color = Color(0xFFE0F2FE),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "⏱️ Kalan: ${remMinutes} dk ${remSeconds} sn",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = TealDark,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Match Selections list in this coupon with Real Score & Status
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFFF8FAFC))
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        evaluatedSelections.forEach { (sel, realMatch, outcomePair) ->
          val (outcome, outcomeText) = outcomePair

          val matchScoreStatus = realMatch?.let { m ->
            when (m.status) {
              MatchStatus.LIVE -> if (m.minute >= 90 && m.currentExtraMinute > 0) "🟢 Canlı 90+${m.currentExtraMinute}' (${m.homeScore} - ${m.awayScore})" else "🟢 Canlı ${m.minute}' (${m.homeScore} - ${m.awayScore})"
              MatchStatus.FINISHED -> "🏁 MS: ${m.homeScore} - ${m.awayScore}"
              else -> "🕒 ${if (m.startTime != "00:00") m.startTime + " TSİ" else realKickoffTime}"
            }
          } ?: "🕒 $realKickoffTime"

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = sel.matchTeams,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = matchScoreStatus,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (realMatch?.status == MatchStatus.LIVE) Color(0xFF16A34A) else Color(0xFF64748B)
                )
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${sel.selectionName} (%.2f)".format(sel.odd),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TealDark,
                maxLines = 1
              )
              Spacer(modifier = Modifier.height(2.dp))
              // Selection outcome badge
              when (outcome) {
                SelectionOutcome.WON -> {
                  Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "✅ TUTTU",
                      color = Color(0xFF15803D),
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Black,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                  }
                }
                SelectionOutcome.LOST -> {
                  Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "❌ YATTI",
                      color = Color(0xFFDC2626),
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Black,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                  }
                }
                SelectionOutcome.LIVE -> {
                  Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "🟢 CANLI",
                      color = Color(0xFFB45309),
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Black,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                  }
                }
                SelectionOutcome.PENDING -> {
                  Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "🕒 BEKLİYOR",
                      color = Color(0xFF64748B),
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Actions row: Real Likes, Real Copies, "Aynı Kuponu Yap"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Like Button (Interactive with real state)
          Row(
            modifier = Modifier
              .clickable { onLike() }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (coupon.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Beğen",
              tint = if (coupon.isLikedByMe) Color(0xFFE11D48) else Color(0xFF94A3B8),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${coupon.likeCount}",
              fontSize = 11.sp,
              fontWeight = if (coupon.isLikedByMe) FontWeight.Bold else FontWeight.Normal,
              color = if (coupon.isLikedByMe) Color(0xFFE11D48) else Color(0xFF64748B)
            )
          }

          // Real Oynanma Count
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Oynandı",
              tint = Color(0xFF0284C7),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${coupon.copyCount} Oynandı",
              fontSize = 11.sp,
              color = Color(0xFF64748B),
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          val context = LocalContext.current
          // Deep link share button
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF1F5F9),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier.clickable {
              val link = CouponDeepLinkManager.generateDeepLinkForSocialCoupon(coupon)
              CouponDeepLinkManager.copyToClipboard(context, link)
              CouponDeepLinkManager.shareDeepLink(context, "SOC-${coupon.id}", coupon.totalOdds, link)
              Toast.makeText(context, "🔗 Kupon Bağlantısı Kopyalandı & Paylaşılıyor!", Toast.LENGTH_SHORT).show()
            }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Share, contentDescription = "Deep-Link Paylaş", tint = TealDark, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Link", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealDark)
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Aynı Kuponu Yap button (Disabled 5 mins before match start)
          if (isClosed) {
            Surface(
              color = Color(0xFFE2E8F0),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Oynanma Kapandı",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF64748B)
                )
              }
            }
          } else {
            Button(
              onClick = onCopy,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = GoldYellow,
                contentColor = TealDark
              ),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(text = "Aynı Kuponu Yap", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }
  }
}

private fun evaluateSelection(sel: SlipSelection, match: Match?): Pair<SelectionOutcome, String> {
  if (match == null) return Pair(SelectionOutcome.PENDING, "🕒 Başlamadı")
  val isFinished = match.status == MatchStatus.FINISHED
  val isLive = match.status == MatchStatus.LIVE || match.minute > 0
  val home = match.homeScore
  val away = match.awayScore
  val total = home + away
  val selName = sel.selectionName.uppercase(Locale.ROOT).trim()
  val scoreStr = "$home - $away"

  if (!isFinished && !isLive) {
    return Pair(SelectionOutcome.PENDING, "🕒 Başlamadı")
  }

  val won: Boolean = when (sel.marketType) {
    MarketType.MATCH_RESULT, MarketType.BASKETBALL_MS -> {
      if (selName.contains("1") || selName == "MS 1") home > away
      else if (selName.contains("X") || selName == "MS X" || selName.contains("BERABER")) home == away
      else if (selName.contains("2") || selName == "MS 2") away > home
      else false
    }
    MarketType.TOTAL_GOALS_25 -> {
      if (selName.contains("ÜST") || selName.contains("OVER")) total >= 3
      else if (selName.contains("ALT") || selName.contains("UNDER")) {
        if (isFinished) total <= 2 else total <= 2
      } else false
    }
    MarketType.BOTH_TEAMS_SCORE -> {
      if (selName.contains("VAR") || selName.contains("YES")) home > 0 && away > 0
      else if (selName.contains("YOK") || selName.contains("NO")) {
        if (isFinished) (home == 0 || away == 0) else (home == 0 || away == 0)
      } else false
    }
    else -> {
      if (isFinished) (home + away > 0) else false
    }
  }

  return when {
    isFinished -> {
      if (won) Pair(SelectionOutcome.WON, "TUTTU (MS: $scoreStr)")
      else Pair(SelectionOutcome.LOST, "YATTI (MS: $scoreStr)")
    }
    isLive -> {
      val minStr = if (match.minute >= 90 && match.currentExtraMinute > 0) "90+${match.currentExtraMinute}'" else "${match.minute}'"
      if (won && (selName.contains("ÜST") || selName.contains("VAR"))) {
        Pair(SelectionOutcome.WON, "TUTTU ($scoreStr • Canlı $minStr)")
      } else {
        Pair(SelectionOutcome.LIVE, "Canlı $minStr ($scoreStr)")
      }
    }
    else -> Pair(SelectionOutcome.PENDING, "🕒 Başlamadı")
  }
}

