package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArenaNewsArticle
import com.example.data.remote.ArenaNewsRepository
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark

/**
 * Real-time 'Arena News' component that uses the Gemini API to periodically generate
 * news articles about team performance, injuries, and transfer rumors based on simulated match results.
 */
@Composable
fun RealTimeArenaNewsComponent(
  onGoToMatch: (String) -> Unit = {},
  onOpenTransferMarket: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val newsList by ArenaNewsRepository.newsFlow.collectAsState(initial = emptyList())
  val isRefreshing by ArenaNewsRepository.isRefreshing.collectAsState(initial = false)
  val categories = remember { ArenaNewsRepository.getCategories() }

  var selectedCategory by remember { mutableStateOf("Tümü") }
  var searchQuery by remember { mutableStateOf("") }
  var readingArticle by remember { mutableStateOf<ArenaNewsArticle?>(null) }

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
      containerColor = Color(0xFF1E293B),
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
              color = GoldYellow
            )
          }
          IconButton(onClick = { readingArticle = null }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF94A3B8))
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
            color = Color.White,
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
              color = Color(0xFF94A3B8)
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
            color = Color(0xFFCBD5E1),
            lineHeight = 20.sp
          )

          if (article.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              article.tags.take(3).forEach { tag ->
                Surface(
                  color = Color(0xFF0F172A),
                  shape = RoundedCornerShape(4.dp),
                  border = BorderStroke(0.6.dp, Color(0xFF334155))
                ) {
                  Text(
                    text = "#$tag",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }

          if (article.relatedMatchId != null && article.relatedMatchTeams != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
              color = Color(0xFF0F3A3D),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, TealDark),
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
                  Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("İlgili Maç: ${article.relatedMatchTeams}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(14.dp))
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { readingArticle = null },
          colors = ButtonDefaults.buttonColors(containerColor = TealDark)
        ) {
          Text("Kapat", fontWeight = FontWeight.Bold)
        }
      }
    )
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("real_time_arena_news_component")
  ) {
    // 1. Component Header with Gemini AI Live Sync Button
    Surface(
      color = Color(0xFF1E293B),
      shape = RoundedCornerShape(12.dp),
      border = BorderStroke(1.dp, Color(0xFF334155)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 10.dp),
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
            Text("⚡", fontSize = 18.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("ARENA HABER", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Surface(color = LiveRed, shape = RoundedCornerShape(4.dp)) {
                Text("GEMINI AI CANLI", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
              }
            }
            Text("Simüle maç sonuçlarından taktik, sakatlık ve transfer bülteni", color = Color(0xFF94A3B8), fontSize = 10.sp)
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              ArenaNewsRepository.refreshNews()
              Toast.makeText(context, "Gemini AI maç bülteni güncelleniyor...", Toast.LENGTH_SHORT).show()
            },
            enabled = !isRefreshing,
            modifier = Modifier.size(32.dp)
          ) {
            if (isRefreshing) {
              CircularProgressIndicator(modifier = Modifier.size(18.dp), color = GoldYellow, strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.Refresh, contentDescription = "Yenile", tint = GoldYellow, modifier = Modifier.size(20.dp))
            }
          }
        }
      }
    }

    // 2. Breaking News Marquee Ticker
    val topFlash = newsList.firstOrNull()
    if (topFlash != null) {
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFFB45309)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 4.dp)
          .clickable { readingArticle = topFlash }
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
            Text(
              text = "SON DAKİKA",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = topFlash.title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 3. Category Filter Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
    ) {
      items(categories) { cat ->
        val isSelected = selectedCategory == cat
        FilterChip(
          selected = isSelected,
          onClick = { selectedCategory = cat },
          label = {
            Text(
              text = cat,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TealDark,
            selectedLabelColor = Color.White,
            containerColor = Color(0xFF1E293B),
            labelColor = Color(0xFFCBD5E1)
          )
        )
      }
    }

    // 4. News Articles Vertical List
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .height(380.dp)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(filteredNews, key = { it.id }) { article ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          border = BorderStroke(0.8.dp, Color(0xFF334155)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { readingArticle = article }
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = article.emojiBadge, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  color = Color(0xFF0F172A),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = article.category,
                    color = GoldYellow,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                  )
                }
              }
              Text(text = article.publishedAgo, color = Color(0xFF94A3B8), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = article.title,
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 13.5.sp,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = article.summary,
              color = Color(0xFF94A3B8),
              fontSize = 11.sp,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${article.source} • ${article.author}",
                color = Color(0xFF64748B),
                fontSize = 9.5.sp
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "${article.readCount}", color = Color(0xFF64748B), fontSize = 9.5.sp)
              }
            }
          }
        }
      }
    }
  }
}
