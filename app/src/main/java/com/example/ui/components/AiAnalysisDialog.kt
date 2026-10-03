package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.example.engine.GeminiAnalysisService
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.ui.components.MotorsportsHubDialog
import com.example.ui.components.MotorsportSeries
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.util.formatXg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAnalysisDialog(
  match: Match,
  allMatches: List<Match> = emptyList(),
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit,
  onTriggerGoal: (String, Boolean) -> Unit = { _, _ -> },
  onUpdateMatchState: ((matchId: String, minute: Int, extraMinute: Int, isFinished: Boolean, homeScore: Int, awayScore: Int) -> Unit)? = null,
  onOpenVoiceAi: () -> Unit = {},
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableIntStateOf(0) }
  var showMotorsportHubFromDetail by remember { mutableStateOf(false) }

  // Synchronized dynamic minute and score states
  val matchIsAlreadyFinished = match.status == MatchStatus.FINISHED ||
      (match.sport == Sport.FOOTBALL && match.minute >= 90 && match.currentExtraMinute >= (if (match.extraTimeMinutes > 0) match.extraTimeMinutes else 3)) ||
      (match.sport == Sport.BASKETBALL && match.minute >= 40) ||
      (match.sport == Sport.TENNIS && match.minute >= 90) ||
      (match.sport == Sport.ICE_HOCKEY && match.minute >= 60)
  var isMatchFinished by remember(match.id, match.status, match.minute) {
    mutableStateOf(matchIsAlreadyFinished)
  }
  var dynamicLiveMinute by remember(match.id, match.minute) {
    mutableIntStateOf(if (isMatchFinished) 90 else if (match.minute > 0) match.minute else 42)
  }
  var dynamicExtraMinute by remember(match.id, match.currentExtraMinute) {
    mutableIntStateOf(match.currentExtraMinute)
  }
  var dynamicHomeScore by remember(match.id, match.homeScore) {
    mutableIntStateOf(match.homeScore)
  }
  var dynamicAwayScore by remember(match.id, match.awayScore) {
    mutableIntStateOf(match.awayScore)
  }

  // React dynamically to real-time WebSocket match progress
  LaunchedEffect(match.status, match.minute, match.currentExtraMinute, match.homeScore, match.awayScore) {
    val finished = match.status == MatchStatus.FINISHED ||
        (match.sport == Sport.FOOTBALL && match.minute >= 90 && match.currentExtraMinute >= (if (match.extraTimeMinutes > 0) match.extraTimeMinutes else 3)) ||
        (match.sport == Sport.BASKETBALL && match.minute >= 40) ||
        (match.sport == Sport.TENNIS && match.minute >= 90) ||
        (match.sport == Sport.ICE_HOCKEY && match.minute >= 60)
    if (finished) {
      isMatchFinished = true
    }
    dynamicLiveMinute = if (finished) 90 else if (match.minute > 0) match.minute else dynamicLiveMinute
    dynamicExtraMinute = match.currentExtraMinute
    dynamicHomeScore = match.homeScore
    dynamicAwayScore = match.awayScore
  }

  val isMotorsport = match.sport == Sport.MOTORSPORTS ||
      match.league.contains("MotoGP", ignoreCase = true) ||
      match.league.contains("WRC", ignoreCase = true)
  val series = if (match.league.contains("WRC", ignoreCase = true)) MotorsportSeries.WRC else MotorsportSeries.MOTOGP

  if (showMotorsportHubFromDetail) {
    MotorsportsHubDialog(
      initialSeries = series,
      selectedSelections = selectedSelections,
      onSelectOdd = onSelectOdd,
      onDismiss = { showMotorsportHubFromDetail = false }
    )
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFFF8FAFC),
    modifier = Modifier.testTag("ai_analysis_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.92f)
    ) {
      // Header Bar
      Surface(color = TealDark, modifier = Modifier.fillMaxWidth()) {
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
            Text(
              text = match.league,
              color = GoldYellow,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = onOpenVoiceAi,
                modifier = Modifier
                  .size(28.dp)
                  .background(Color(0x33FFFFFF), CircleShape)
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Sesli AI Asistanı",
                  tint = GoldYellow,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Kapat",
                  tint = Color.White
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Teams and Score
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = match.homeTeam,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = Color.White,
              modifier = Modifier.weight(1f)
            )

            // Score Pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF072628))
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = if (isMotorsport) "🏁 H2H" else "$dynamicHomeScore - $dynamicAwayScore",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }

            Text(
              text = match.awayTeam,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = Color.White,
              textAlign = TextAlign.End,
              modifier = Modifier.weight(1f)
            )
          }

          if (isMotorsport) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showMotorsportHubFromDetail = true }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  modifier = Modifier.weight(1f).padding(end = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = if (series == MotorsportSeries.WRC) "🏎️" else "🏍️", fontSize = 16.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "${series.title} Maçkolik Yarış Merkezi",
                      color = GoldYellow,
                      fontWeight = FontWeight.Black,
                      fontSize = 12.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Text(
                      text = "Puan Tablosu, Sezon Fikstürü, Etaplar & Rehber",
                      color = Color(0xFFCBD5E1),
                      fontSize = 10.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }
                Surface(
                  color = GoldYellow,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = "Aç ➔",
                    color = Color(0xFF78350F),
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }

          if (match.status == MatchStatus.LIVE || isMatchFinished || dynamicLiveMinute > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(if (isMatchFinished) Color(0xFF10B981) else LiveRed)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isMatchFinished) {
                  "MAÇ SONUCU (MS: $dynamicHomeScore - $dynamicAwayScore)"
                } else if (dynamicLiveMinute >= 90 && dynamicExtraMinute > 0) {
                  "CANLI 90+$dynamicExtraMinute'"
                } else if (dynamicLiveMinute >= 90) {
                  "CANLI 90'"
                } else {
                  "CANLI ${dynamicLiveMinute}'"
                },
                color = if (isMatchFinished) Color(0xFF6EE7B7) else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Maçkolik & İddaa Multi-Tab Navigation
      androidx.compose.material3.ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        edgePadding = 12.dp
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("🏟️ 2D Canlı Simülatör", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("🎯 AI Olasılık & İstatistik", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("🏢 5 Operatör Oran Kıyas", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          text = { Text("İddaa Oranları (${match.markets.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 4,
          onClick = { selectedTab = 4 },
          text = { Text("Canlı Olaylar & İstatistik", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 5,
          onClick = { selectedTab = 5 },
          text = { Text("Kadro & H2H", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 6,
          onClick = { selectedTab = 6 },
          text = { Text("🤖 Gemini Analizi", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 7,
          onClick = { selectedTab = 7 },
          text = { Text("📈 D3 Baskı & Trend", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 8,
          onClick = { selectedTab = 8 },
          text = { Text("📊 Pro Dashboard", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
      }

      // Tab Content
      when (selectedTab) {
        0 -> androidx.compose.foundation.lazy.LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(12.dp)
        ) {
          item {
            MultiSport2DSimulator(
              match = match.copy(
                homeScore = dynamicHomeScore,
                awayScore = dynamicAwayScore,
                minute = dynamicLiveMinute,
                currentExtraMinute = dynamicExtraMinute,
                status = if (isMatchFinished) MatchStatus.FINISHED else MatchStatus.LIVE
              ),
              onTriggerGoal = { id, isHome ->
                if (isHome) dynamicHomeScore++ else dynamicAwayScore++
                onTriggerGoal(id, isHome)
                onUpdateMatchState?.invoke(id, dynamicLiveMinute, dynamicExtraMinute, isMatchFinished, dynamicHomeScore, dynamicAwayScore)
              },
              onMinuteUpdated = { min, extraMin, finished, hScore, aScore ->
                dynamicLiveMinute = min
                dynamicExtraMinute = extraMin
                isMatchFinished = finished
                dynamicHomeScore = hScore
                dynamicAwayScore = aScore
                onUpdateMatchState?.invoke(match.id, min, extraMin, finished, hScore, aScore)
              }
            )
          }
        }
        1 -> DetailedMatchStatsAndAiProbabilityView(
          matches = if (allMatches.isNotEmpty()) allMatches else listOf(match),
          initialSelectedMatchId = match.id,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd,
          onClose = onDismiss
        )
        2 -> BettingProvidersComparisonView(
          match = match,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd
        )
        3 -> ComprehensiveBettingScreen(
          match = match,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd
        )
        4 -> StatsAndEventsTabContent(match = match)
        5 -> MackolikLineupsAndH2HView(match = match)
        6 -> AiTabContent(match = match, onSelectOdd = onSelectOdd)
        7 -> androidx.compose.foundation.lazy.LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
        ) {
          item {
            MatchPressureTrendVisualization(match = match)
          }
        }
        8 -> MatchAnalyticsDashboard(
          match = match,
          selectedSelections = selectedSelections,
          onSelectOdd = onSelectOdd,
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
        )
      }
    }
  }
}

@Composable
private fun AiTabContent(
  match: Match,
  onSelectOdd: (SlipSelection) -> Unit = {}
) {
  val pred = match.aiPrediction
  val coroutineScope = rememberCoroutineScope()
  var geminiInsight by remember { mutableStateOf<String?>(null) }
  var isGeminiLoading by remember { mutableStateOf(false) }

  LaunchedEffect(match.id) {
    isGeminiLoading = true
    geminiInsight = GeminiAnalysisService.generateDeepMatchAnalysis(match)
    isGeminiLoading = false
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 0. Pre-Match AI Insight Powered by Gemini
    item {
      PreMatchAiInsightCard(
        match = match,
        onAddRecommendationToSlip = { pickName, oddVal ->
          onSelectOdd(
            SlipSelection(
              matchId = match.id,
              matchTeams = "${match.homeTeam} - ${match.awayTeam}",
              marketType = com.example.data.model.MarketType.MATCH_RESULT,
              selectionId = "ai_rec_${match.id}",
              selectionName = pickName,
              odd = oddVal,
              isLive = (match.status == MatchStatus.LIVE)
            )
          )
        }
      )
    }

    // Reusable AI Match Flow Insight Component
    item {
      AiMatchFlowInsightComponent(match = match)
    }

    // 0. Gemini Deep Analytical AI Card
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Gemini AI",
                tint = Color(0xFF059669),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Gemini 2.5 Flash Derin Analiz",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color(0xFF065F46)
              )
            }

            IconButton(
              onClick = {
                coroutineScope.launch {
                  isGeminiLoading = true
                  geminiInsight = GeminiAnalysisService.generateDeepMatchAnalysis(match)
                  isGeminiLoading = false
                }
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Yenile",
                tint = Color(0xFF059669),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          if (isGeminiLoading) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = Color(0xFF059669)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Yapay zeka canlı taktiksel raporu oluşturuyor...",
                fontSize = 11.sp,
                color = Color(0xFF047857),
                fontWeight = FontWeight.Medium
              )
            }
          } else {
            Text(
              text = geminiInsight ?: "Analiz hazırlanıyor...",
              fontSize = 12.sp,
              color = Color(0xFF064E3B),
              lineHeight = 17.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
    item {
      // 1. Probabilities Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Poisson & Monte Carlo Olasılık Dağılımı",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color(0xFF1E293B)
            )
            Text(
              text = "Güven: %${pred.confidence}",
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = Color(0xFF0D9488)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 3 Probability Bars
          ProbabilityBar(label = match.homeTeam, percent = pred.homeWinProb, color = TealDark)
          Spacer(modifier = Modifier.height(8.dp))
          ProbabilityBar(label = "Beraberlik", percent = pred.drawProb, color = Color(0xFF64748B))
          Spacer(modifier = Modifier.height(8.dp))
          ProbabilityBar(label = match.awayTeam, percent = pred.awayWinProb, color = Color(0xFFE65100))
        }
      }
    }

    item {
      // 2. Expected Goals (xG) and Score Prediction Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "AI Skor Tahmini & Beklenen Gol (xG)",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF1E293B)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Tahmini Skor", fontSize = 11.sp, color = Color(0xFF64748B))
              Text(
                text = pred.predictedScore,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TealDark
              )
            }

            Box(
              modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .background(Color(0xFFE2E8F0))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "xG Beklentisi", fontSize = 11.sp, color = Color(0xFF64748B))
              Text(
                text = "${"%.2f".format(match.statistics.xgHome)} vs ${"%.2f".format(match.statistics.xgAway)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D9488)
              )
            }
          }
        }
      }
    }

    item {
      // 3. Tactical Narrative Explanation
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Yapay Zeka Taktiksel Görüşü",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = pred.tacticalAnalysis,
            fontSize = 12.sp,
            color = Color(0xFF475569),
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "• ${pred.formRatingHome}",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
          Text(
            text = "• ${pred.formRatingAway}",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }
      }
    }
  }
}

@Composable
private fun ProbabilityBar(label: String, percent: Int, color: Color) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
      Text(text = "%$percent", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { percent / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = color,
      trackColor = Color(0xFFE2E8F0),
    )
  }
}

@Composable
private fun StatsAndEventsTabContent(
  match: Match
) {
  val stats = match.statistics

  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    if (match.status == MatchStatus.LIVE) {
      item {
        AiMatchFlowInsightComponent(match = match)
      }
    }

    item {
      // Mackolik Match Overview Card (Stadyum, Hakem, İY Skoru, Ek Süre)
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "İlk Yarı Sonucu", fontSize = 12.sp, color = Color(0xFF64748B))
            Text(
              text = "${match.halfTimeHomeScore} - ${match.halfTimeAwayScore}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = TealDark
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Hakem", fontSize = 12.sp, color = Color(0xFF64748B))
            Text(text = match.referee, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF1E293B))
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Stadyum", fontSize = 12.sp, color = Color(0xFF64748B))
            Text(text = match.stadium, fontSize = 11.sp, color = Color(0xFF1E293B))
          }
          if (match.extraTimeMinutes > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Eklenen Süre (Uzatma)", fontSize = 12.sp, color = Color(0xFF64748B))
              Text(
                text = "+${match.extraTimeMinutes} dakika",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = LiveRed
              )
            }
          }
        }
      }
    }

    item {
      // D3-inspired data visualization component for match possession, shot accuracy, and pressure trends
      D3MatchTrendVisualization(match = match)
    }

    item {
      // Match Statistics Table
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "📊 Maçkolik Canlı Karşılaşma İstatistikleri",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.height(10.dp))

          StatRow("Topla Oynama", "${stats.possessionHome}%", "${stats.possessionAway}%")
          StatRow("Beklenen Gol (xG)", stats.xgHome.formatXg(), stats.xgAway.formatXg())
          StatRow("Toplam Şut", "${stats.shotsHome}", "${stats.shotsAway}")
          StatRow("İsabetli Şut", "${stats.shotsOnTargetHome}", "${stats.shotsOnTargetAway}")
          StatRow("Köşe Vuruşu (Korner)", "${stats.cornersHome}", "${stats.cornersAway}")
          StatRow("Sarı Kart 🟨", "${stats.yellowCardsHome}", "${stats.yellowCardsAway}")
          StatRow("Kırmızı Kart 🟥", "${stats.redCardsHome}", "${stats.redCardsAway}")
          StatRow("Ofsayt", "${stats.offsidesHome}", "${stats.offsidesAway}")
          StatRow("Faul", "${stats.foulsHome}", "${stats.foulsAway}")
          StatRow("Tehlikeli Atak", "${stats.dangerousAttacksHome}", "${stats.dangerousAttacksAway}")
          StatRow("Topun Oyunda Kalma Süresi", stats.ballInPlayTime, stats.ballInPlayTime)
        }
      }
    }

    item {
      // Timeline Events
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Canlı Zaman Akışı & Olaylar",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.height(8.dp))

          if (match.events.isEmpty()) {
            Text(
              text = "Henüz önemli bir olay gerçekleşmedi.",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          } else {
            match.events.forEach { ev ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (ev.type == EventType.GOAL) LiveRed else Color(0xFFE2E8F0))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${ev.minute}'",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (ev.type == EventType.GOAL) Color.White else Color(0xFF1E293B)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = ev.description,
                  fontSize = 12.sp,
                  color = Color(0xFF334155),
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StatRow(title: String, homeVal: String, awayVal: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = homeVal, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TealDark)
    Text(text = title, fontSize = 11.sp, color = Color(0xFF64748B))
    Text(text = awayVal, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TealDark)
  }
}

@Composable
private fun AllMarketsTabContent(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(match.markets) { market ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = market.name,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            market.selections.forEach { sel ->
              val isSelected = selectedSelections.any { it.selectionId == sel.id }
              OddCell(
                selection = sel,
                isSelected = isSelected,
                isSuspended = market.isSuspended,
                modifier = Modifier.weight(1f),
                onClick = {
                  onSelectOdd(
                    SlipSelection(
                      matchId = match.id,
                      matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                      marketType = market.type,
                      selectionId = sel.id,
                      selectionName = sel.name,
                      odd = sel.odd,
                      isLive = match.status == MatchStatus.LIVE
                    )
                  )
                }
              )
            }
          }
        }
      }
    }
  }
}
