package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import com.example.data.model.ArenaNewsArticle
import com.example.data.remote.ArenaNewsRepository
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaNewsScreen(
  onGoToMatch: (String) -> Unit = {}
) {
  val context = LocalContext.current
  val newsList by ArenaNewsRepository.newsFlow.collectAsState(initial = emptyList())
  val isRefreshing by ArenaNewsRepository.isRefreshing.collectAsState(initial = false)
  val categories = remember { ArenaNewsRepository.getCategories() }

  var selectedCategory by remember { mutableStateOf("Tümü") }
  var searchQuery by remember { mutableStateOf("") }
  var readingArticle by remember { mutableStateOf<ArenaNewsArticle?>(null) }
  var showTransferMarketDialog by remember { mutableStateOf(false) }

  if (showTransferMarketDialog) {
    com.example.ui.components.LiveTransferMarketAiHub(
      onDismiss = { showTransferMarketDialog = false }
    )
  }

  val filteredNews = remember(newsList, selectedCategory, searchQuery) {
    newsList.filter { article ->
      val matchesCat = selectedCategory == "Tümü" || article.category == selectedCategory
      val matchesQuery = searchQuery.isBlank() ||
          article.title.contains(searchQuery, ignoreCase = true) ||
          article.summary.contains(searchQuery, ignoreCase = true) ||
          article.tags.any { it.contains(searchQuery, ignoreCase = true) }
      matchesCat && matchesQuery
    }
  }

  // Article Reader Modal Dialog
  readingArticle?.let { article ->
    AlertDialog(
      onDismissRequest = { readingArticle = null },
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = article.emojiBadge, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = article.category,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TealDark
            )
          }
          IconButton(onClick = { readingArticle = null }, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF64748B))
          }
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
          Text(
            text = article.title,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = Color(0xFF0F172A),
            lineHeight = 22.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${article.source} • ${article.publishedAgo}",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
            Text(
              text = "${article.readTimeMinutes} dk okuma",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = TealDark
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = article.content,
            fontSize = 13.sp,
            color = Color(0xFF334155),
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Tags row
          if (article.tags.isNotEmpty()) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              article.tags.take(3).forEach { tag ->
                Surface(
                  color = Color(0xFFF1F5F9),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "#$tag",
                    fontSize = 10.sp,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }

          // Related match action if available
          if (article.relatedMatchId != null && article.relatedMatchTeams != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
              color = Color(0xFFECFDF5),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  readingArticle = null
                  onGoToMatch(article.relatedMatchId)
                }
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(text = "Haberle İlgili Maç:", fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                    Text(text = article.relatedMatchTeams, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF065F46))
                  }
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            Toast.makeText(context, "Haber bağlantısı kopyalandı!", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TealDark)
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Paylaş")
        }
      },
      dismissButton = {
        TextButton(onClick = { readingArticle = null }) {
          Text("Kapat")
        }
      }
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("arena_news_screen")
  ) {
    // 1. Header Banner (Sleek, Compact & Responsive Maçkolik Style)
    item {
      Surface(
        color = TealDark,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F3A3D)),
              contentAlignment = Alignment.Center
            ) {
              Text("📰", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "ARENA HABER",
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp,
                  color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFDC2626))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = "CANLI",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }
              Text(
                text = "Transfer KAP • xG Analiz • Günlük Spor Bülteni",
                fontSize = 10.sp,
                color = Color(0xFFCBD5E1)
              )
            }
          }

          Surface(
            color = Color(0xFF0F3A3D),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, GoldYellow.copy(alpha = 0.6f)),
            modifier = Modifier.clickable { ArenaNewsRepository.refreshNews() }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isRefreshing) {
                CircularProgressIndicator(
                  modifier = Modifier.size(11.dp),
                  color = GoldYellow,
                  strokeWidth = 2.dp
                )
              } else {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = "Gemini AI",
                  tint = GoldYellow,
                  modifier = Modifier.size(13.dp)
                )
              }
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isRefreshing) "Derliyor..." else "AI Yenile",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = GoldYellow
              )
            }
          }
        }
      }
    }

    // 2. Breaking Live Flash News Ticker (Son Dakika Kayan Yazılar - Smooth Marquee)
    item {
      BreakingNewsMarqueeTicker()
    }

    // 2.1 Canlı Transfer Dönemi & Alım-Satım AI Modu Hızlı Erişim Kartı
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, GoldYellow.copy(alpha = 0.8f)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .clickable { showTransferMarketDialog = true }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B)),
              contentAlignment = Alignment.Center
            ) {
              Text("✈️", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("CANLI TRANSFER & AI ALIM-SATIM", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Surface(color = LiveRed, shape = RoundedCornerShape(4.dp)) {
                  Text("KAP AKTİF", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                }
              }
              Text("Kulüp alım-satım simülatörü, bonservisler ve transfer geçmişi", color = Color(0xFFCBD5E1), fontSize = 9.5.sp)
            }
          }

          Surface(
            color = TealDark,
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Aç", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }

    // AI Günlük Spor Bülteni & Özet Kartı
    item {
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🤖", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("AI GÜNLÜK SPOR BÜLTENİ", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF0F172A))
            }
            Surface(
              color = Color(0xFFDCFCE7),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text("Bugün Güncellendi", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              color = Color(0xFFF8FAFC),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text("👑 Gol Krallığı", fontSize = 9.sp, color = Color(0xFF64748B))
                Text("Immobile (7 Gol)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                Text("Takipçi: Dzeko (5)", fontSize = 8.sp, color = Color(0xFF94A3B8))
              }
            }

            Surface(
              color = Color(0xFFF8FAFC),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text("📉 Düşme Barajı", fontSize = 9.sp, color = Color(0xFF64748B))
                Text("42 Puan (xG)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFDC2626))
                Text("Monte Carlo Analizi", fontSize = 8.sp, color = Color(0xFF94A3B8))
              }
            }

            Surface(
              color = Color(0xFFF8FAFC),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text("💰 Mali / FFP", fontSize = 9.sp, color = Color(0xFF64748B))
                Text("Limitler Onaylandı", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D9488))
                Text("TFF Harcama Raporu", fontSize = 8.sp, color = Color(0xFF94A3B8))
              }
            }
          }
        }
      }
    }

    // 2. Search & Filter Bar
    item {
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Haber, takım veya oyuncu ara...", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = "Ara", tint = Color(0xFF94A3B8))
            },
            trailingIcon = {
              if (searchQuery.isNotBlank()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Temizle", tint = Color(0xFF94A3B8))
                }
              }
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedBorderColor = Color(0xFFE2E8F0),
              focusedBorderColor = TealDark
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
          ) {
            items(categories) { cat ->
              val isSelected = selectedCategory == cat
              FilterChip(
                selected = isSelected,
                onClick = { selectedCategory = cat },
                label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = TealDark,
                  selectedLabelColor = Color.White,
                  containerColor = Color(0xFFF1F5F9),
                  labelColor = Color(0xFF334155)
                )
              )
            }
          }
        }
      }
    }

    // 3. News Articles List
    if (filteredNews.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Newspaper, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Aramanıza uygun haber bulunamadı.", fontSize = 13.sp, color = Color(0xFF64748B))
          }
        }
      }
    } else {
      items(filteredNews, key = { it.id }) { article ->
        NewsArticleCard(
          article = article,
          onReadClick = { readingArticle = article },
          onGoToMatch = { article.relatedMatchId?.let { onGoToMatch(it) } }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun NewsArticleCard(
  article: ArenaNewsArticle,
  onReadClick: () -> Unit,
  onGoToMatch: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clickable { onReadClick() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
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
              .background(Color(0xFFE0F2FE)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = article.emojiBadge, fontSize = 15.sp)
          }
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = article.category,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = TealDark,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = article.publishedAgo,
          fontSize = 11.sp,
          color = Color(0xFF94A3B8)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = article.title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Color(0xFF0F172A),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = article.summary,
        fontSize = 12.sp,
        color = Color(0xFF475569),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "${article.readCount}", fontSize = 11.sp, color = Color(0xFF94A3B8))
          Spacer(modifier = Modifier.width(10.dp))
          Text(text = "Yazar: ${article.author}", fontSize = 10.sp, color = Color(0xFF64748B))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (article.relatedMatchId != null) {
            TextButton(
              onClick = onGoToMatch,
              modifier = Modifier.height(30.dp),
              contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
              Text("Maç Oranları", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealDark)
            }
          }

          TextButton(
            onClick = onReadClick,
            modifier = Modifier.height(30.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
          ) {
            Text("Devamını Oku →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
          }
        }
      }
    }
  }
}

@Composable
private fun BreakingNewsMarqueeTicker() {
  val flashHeadlines = remember {
    listOf(
      "⚡ KAP: Mauro Icardi sözleşmesi 2 yıl uzatıldı!",
      "👑 Ciro Immobile 7 golle Süper Lig gol krallığı zirvesinde",
      "💰 TFF Harcama Limitleri resmi olarak onaylandı",
      "🏀 Panathinaikos - Fenerbahçe Beko maçı bu akşam 21:15'te",
      "📉 Monte Carlo xG Simülasyonu: Küme düşme barajı 42 puan",
      "🎯 Rıdvan Dilmen: 'Orta sahayı kazanan derbiyi koparır'",
      "🏎️ F1 Singapur GP'de pole pozisyonu Lando Norris'in"
    )
  }

  val combinedTickerText = remember(flashHeadlines) {
    flashHeadlines.joinToString("   •••   ")
  }

  val infiniteTransition = rememberInfiniteTransition(label = "marquee_anim")
  val offsetX by infiniteTransition.animateFloat(
    initialValue = 350f,
    targetValue = -1200f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 28000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ticker_offset"
  )

  Surface(
    color = Color(0xFF0F172A),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        color = Color(0xFFDC2626),
        shape = RoundedCornerShape(4.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(Color.White)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "SON DAKİKA",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        modifier = Modifier
          .weight(1f)
          .clipToBounds()
      ) {
        Text(
          text = combinedTickerText,
          color = Color(0xFFE2E8F0),
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          softWrap = false,
          modifier = Modifier.offset(x = offsetX.dp)
        )
      }
    }
  }
}

