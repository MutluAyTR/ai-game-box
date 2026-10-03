package com.example.ui.components.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.PlayerLineup
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlin.math.hypot
import kotlin.math.roundToInt

/**
 * Position slot model for tactical pitch layout
 */
data class TacticalSlot(
  val relX: Float,
  val relY: Float,
  val roleName: String
)

/**
 * TacticalPitchView:
 * Visualizes team formations dynamically based on player positions (e.g., 4-4-2, 3-5-2, 4-3-3, 4-2-3-1, etc.)
 * with interactive drag-and-drop support for administrative tactical planning.
 */
@Composable
fun TacticalPitchView(
  formation: String,
  teamName: String,
  players: List<PlayerLineup>,
  isHome: Boolean = true,
  isAdministrativePlanning: Boolean = false,
  onFormationChanged: (String) -> Unit = {},
  onPlayerMoved: (PlayerLineup, Offset) -> Unit = { _, _ -> },
  onPlayerSwapped: (PlayerLineup, PlayerLineup) -> Unit = { _, _ -> },
  onPlayerClick: (PlayerLineup) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var currentFormation by remember(formation) { mutableStateOf(formation) }
  var isPlanningMode by remember(isAdministrativePlanning) { mutableStateOf(isAdministrativePlanning) }
  var showTacticalGrid by remember { mutableStateOf(false) }
  var tacticalStyle by remember { mutableStateOf("DENGELİ") } // DENGELİ, YÜKSEK PRES, ALÇAK BLOK, GENİŞ KANAT
  var saveSuccessBanner by remember { mutableStateOf(false) }

  // Current order of players (which can be swapped via drag-and-drop)
  var currentPlayers by remember(players) { mutableStateOf(players) }

  // Custom dragged offsets per player number
  val customOffsets = remember { mutableStateMapOf<Int, Offset>() }

  // Currently actively dragged player number
  var draggingPlayerNumber by remember { mutableStateOf<Int?>(null) }
  var activeDragDelta by remember { mutableStateOf(Offset.Zero) }
  var dropCandidateNumber by remember { mutableStateOf<Int?>(null) }

  val supportedFormations = listOf("4-4-2", "3-5-2", "4-3-3", "4-2-3-1", "3-4-3", "5-3-2", "4-1-4-1")

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("tactical_pitch_view")
  ) {
    // 1. Administrative Tactical Planning Bar
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      border = BorderStroke(1.dp, if (isPlanningMode) GoldYellow else Color(0xFF334155)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isPlanningMode) Icons.Default.Edit else Icons.Default.Shield,
              contentDescription = null,
              tint = if (isPlanningMode) GoldYellow else Color(0xFF38BDF8),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = if (isPlanningMode) "🛠️ YÖNETİCİ TAKTİK TAHTASI" else "📋 TAKTİK DİZİLİŞ GÖRÜNÜMÜ",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = if (isPlanningMode) "Oyuncuları sürükleyip bırakarak takas edin veya pozisyon kaydırın" else "$teamName • $currentFormation Formasyonu",
                color = if (isPlanningMode) GoldYellow else Color(0xFF94A3B8),
                fontSize = 9.sp
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (isPlanningMode) "Planlama" else "İzleme",
              color = if (isPlanningMode) GoldYellow else Color(0xFF64748B),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Switch(
              checked = isPlanningMode,
              onCheckedChange = { isPlanningMode = it },
              modifier = Modifier.testTag("admin_planning_toggle"),
              colors = SwitchDefaults.colors(
                checkedThumbColor = GoldYellow,
                checkedTrackColor = TealDark,
                uncheckedThumbColor = Color(0xFF64748B),
                uncheckedTrackColor = Color(0xFF1E293B)
              )
            )
          }
        }

        // Formations Chip Selector
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Diziliş:",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
          supportedFormations.forEach { form ->
            val isSelected = currentFormation == form
            FilterChip(
              selected = isSelected,
              onClick = {
                currentFormation = form
                customOffsets.clear()
                onFormationChanged(form)
              },
              label = {
                Text(
                  text = form,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GoldYellow,
                selectedLabelColor = Color(0xFF0F172A),
                containerColor = Color(0xFF1E293B),
                labelColor = Color.White
              ),
              border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0xFF334155)),
              modifier = Modifier.height(28.dp)
            )
          }
        }

        // Administrative Planning Controls (visible in planning mode)
        if (isPlanningMode) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Preset 1: Yüksek Pres
            Surface(
              color = if (tacticalStyle == "YÜKSEK PRES") Color(0xFFEF4444) else Color(0xFF1E293B),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.clickable {
                tacticalStyle = "YÜKSEK PRES"
                // Push players slightly higher up the pitch
                customOffsets.clear()
                currentPlayers.take(11).forEachIndexed { idx, p ->
                  if (idx > 0) {
                    customOffsets[p.number] = Offset(0f, 30f)
                  }
                }
              }
            ) {
              Text(
                text = "⚔️ Yüksek Pres",
                color = Color.White,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            // Preset 2: Alçak Blok
            Surface(
              color = if (tacticalStyle == "ALÇAK BLOK") Color(0xFF3B82F6) else Color(0xFF1E293B),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.clickable {
                tacticalStyle = "ALÇAK BLOK"
                // Drop players deeper down
                customOffsets.clear()
                currentPlayers.take(11).forEachIndexed { idx, p ->
                  if (idx > 0) {
                    customOffsets[p.number] = Offset(0f, -30f)
                  }
                }
              }
            ) {
              Text(
                text = "🛡️ Alçak Blok",
                color = Color.White,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            // Preset 3: Geniş Kanatlar
            Surface(
              color = if (tacticalStyle == "GENİŞ KANAT") Color(0xFF10B981) else Color(0xFF1E293B),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.clickable {
                tacticalStyle = "GENİŞ KANAT"
                customOffsets.clear()
              }
            ) {
              Text(
                text = "⚡ Geniş Kanatlar",
                color = Color.White,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            // Grid toggle
            Surface(
              color = if (showTacticalGrid) TealDark else Color(0xFF1E293B),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.clickable { showTacticalGrid = !showTacticalGrid }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.GridOn, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (showTacticalGrid) "Izgara Açık" else "Taktik Izgara",
                  color = Color.White,
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Reset
            Surface(
              color = Color(0xFF334155),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.clickable {
                customOffsets.clear()
                tacticalStyle = "DENGELİ"
                currentPlayers = players
              }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFE2E8F0), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Sıfırla", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Save Tactic
            Surface(
              color = Color(0xFF059669),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.clickable {
                saveSuccessBanner = true
              }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Kaydet", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Save confirmation toast/banner
        AnimatedVisibility(visible = saveSuccessBanner) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
              .background(Color(0xFF065F46), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Check, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Taktik diziliş ve oyuncu pozisyonları başarıyla uygulandı!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(
              onClick = { saveSuccessBanner = false },
              modifier = Modifier.size(18.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White, modifier = Modifier.size(12.dp))
            }
          }
        }
      }
    }

    // 2. THE TACTICAL PITCH WITH DYNAMIC FORMATIONS & DRAG-AND-DROP
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
      border = BorderStroke(2.dp, if (isPlanningMode) GoldYellow else Color(0xFF059669)),
      modifier = Modifier
        .fillMaxWidth()
        .height(425.dp)
    ) {
      BoxWithConstraints(
        modifier = Modifier
          .fillMaxSize()
          .clip(RoundedCornerShape(16.dp))
      ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Pitch Drawing Canvas (Stripes, Lines, Boxes, Tactical Channels)
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // 1. Lush Grass Stripes
          val stripes = 8
          val stripeHeight = h / stripes
          for (i in 0 until stripes) {
            val color = if (i % 2 == 0) Color(0xFF065F46) else Color(0xFF047857)
            drawRect(color = color, topLeft = Offset(0f, i * stripeHeight), size = Size(w, stripeHeight))
          }

          // 2. Outer Border
          drawRect(
            color = Color(0x66FFFFFF),
            topLeft = Offset(12f, 12f),
            size = Size(w - 24f, h - 24f),
            style = Stroke(2.2f)
          )

          // 3. Penalty Box (Top Goal Area)
          drawRect(
            color = Color(0x55FFFFFF),
            topLeft = Offset(w * 0.22f, 12f),
            size = Size(w * 0.56f, 75f),
            style = Stroke(2f)
          )
          // 6-yard box
          drawRect(
            color = Color(0x55FFFFFF),
            topLeft = Offset(w * 0.36f, 12f),
            size = Size(w * 0.28f, 30f),
            style = Stroke(1.5f)
          )
          // Penalty spot & arc
          drawCircle(color = Color(0x88FFFFFF), radius = 3.5f, center = Offset(w / 2, 58f))

          // 4. Center Line (at the bottom of tactical half-pitch)
          drawLine(
            color = Color(0x88FFFFFF),
            start = Offset(12f, h - 12f),
            end = Offset(w - 12f, h - 12f),
            strokeWidth = 3f
          )
          // Center circle arc
          drawCircle(
            color = Color(0x66FFFFFF),
            radius = 52f,
            center = Offset(w / 2, h - 12f),
            style = Stroke(2f)
          )
          // Center spot
          drawCircle(color = Color(0x99FFFFFF), radius = 3.5f, center = Offset(w / 2, h - 12f))

          // 5. Tactical Grid Overlay (When toggled on)
          if (showTacticalGrid) {
            drawLine(
              color = Color(0x44FACC15),
              start = Offset(w * 0.25f, 12f),
              end = Offset(w * 0.25f, h - 12f),
              strokeWidth = 1.5f
            )
            drawLine(
              color = Color(0x44FACC15),
              start = Offset(w * 0.40f, 12f),
              end = Offset(w * 0.40f, h - 12f),
              strokeWidth = 1.5f
            )
            drawLine(
              color = Color(0x44FACC15),
              start = Offset(w * 0.60f, 12f),
              end = Offset(w * 0.60f, h - 12f),
              strokeWidth = 1.5f
            )
            drawLine(
              color = Color(0x44FACC15),
              start = Offset(w * 0.75f, 12f),
              end = Offset(w * 0.75f, h - 12f),
              strokeWidth = 1.5f
            )
          }
        }

        // Tactical Slots based on the formation
        val slots = remember(currentFormation) { getTacticalSlots(currentFormation) }
        val startingEleven = currentPlayers.take(11)

        val slotPositions = remember(slots, widthPx, heightPx) {
          slots.mapIndexed { idx, slot ->
            Offset(
              x = slot.relX * widthPx,
              y = slot.relY * heightPx
            )
          }
        }

        startingEleven.forEachIndexed { index, player ->
          val slot = if (index < slots.size) slots[index] else TacticalSlot(0.5f, 0.5f, player.position)
          val baseSlotOffset = if (index < slotPositions.size) slotPositions[index] else Offset(widthPx / 2, heightPx / 2)
          val customOffset = customOffsets[player.number] ?: Offset.Zero

          val isBeingDragged = draggingPlayerNumber == player.number
          val isDropCandidate = dropCandidateNumber == player.number

          val effectiveX = if (isBeingDragged) {
            baseSlotOffset.x + customOffset.x + activeDragDelta.x
          } else {
            baseSlotOffset.x + customOffset.x
          }

          val effectiveY = if (isBeingDragged) {
            baseSlotOffset.y + customOffset.y + activeDragDelta.y
          } else {
            baseSlotOffset.y + customOffset.y
          }

          val nodeWidthDp = 78.dp
          val nodeHeightDp = 72.dp
          val nodeWidthPx = with(density) { nodeWidthDp.toPx() }
          val nodeHeightPx = with(density) { nodeHeightDp.toPx() }

          val leftDp = with(density) { (effectiveX - nodeWidthPx / 2).coerceIn(0f, widthPx - nodeWidthPx).toDp() }
          val topDp = with(density) { (effectiveY - nodeHeightPx / 2).coerceIn(0f, heightPx - nodeHeightPx).toDp() }

          Box(
            modifier = Modifier
              .offset(x = leftDp, y = topDp)
              .size(width = nodeWidthDp, height = nodeHeightDp)
              .zIndex(if (isBeingDragged) 100f else if (isDropCandidate) 50f else 1f)
              .graphicsLayer {
                scaleX = if (isBeingDragged) 1.22f else if (isDropCandidate) 1.12f else 1f
                scaleY = if (isBeingDragged) 1.22f else if (isDropCandidate) 1.12f else 1f
                shadowElevation = if (isBeingDragged) 18f else 0f
              }
              .then(
                if (isPlanningMode) {
                  Modifier.pointerInput(player.number) {
                    detectDragGestures(
                      onDragStart = {
                        draggingPlayerNumber = player.number
                        activeDragDelta = Offset.Zero
                      },
                      onDrag = { change, dragAmount ->
                        change.consume()
                        activeDragDelta += dragAmount

                        // Detect proximity to other players for swap candidate
                        val currentDragCenter = baseSlotOffset + customOffset + activeDragDelta
                        var closestCandidate: Int? = null
                        var minDistance = Float.MAX_VALUE

                        startingEleven.forEachIndexed { otherIdx, otherPlayer ->
                          if (otherPlayer.number != player.number && otherIdx < slotPositions.size) {
                            val otherBase = slotPositions[otherIdx] + (customOffsets[otherPlayer.number] ?: Offset.Zero)
                            val dist = hypot(currentDragCenter.x - otherBase.x, currentDragCenter.y - otherBase.y)
                            if (dist < 110f && dist < minDistance) { // within approx 40dp threshold
                              minDistance = dist
                              closestCandidate = otherPlayer.number
                            }
                          }
                        }
                        dropCandidateNumber = closestCandidate
                      },
                      onDragEnd = {
                        val candidateNum = dropCandidateNumber
                        if (candidateNum != null) {
                          // Swap players in the starting lineup!
                          val currentList = currentPlayers.toMutableList()
                          val idxA = currentList.indexOfFirst { it.number == player.number }
                          val idxB = currentList.indexOfFirst { it.number == candidateNum }
                          if (idxA != -1 && idxB != -1) {
                            val temp = currentList[idxA]
                            currentList[idxA] = currentList[idxB]
                            currentList[idxB] = temp
                            currentPlayers = currentList
                            onPlayerSwapped(player, currentList[idxA])
                          }
                          // Clear custom offsets of swapped players
                          customOffsets.remove(player.number)
                          customOffsets.remove(candidateNum)
                        } else {
                          // Free-form repositioning
                          val existing = customOffsets[player.number] ?: Offset.Zero
                          val finalOffset = existing + activeDragDelta
                          customOffsets[player.number] = finalOffset
                          onPlayerMoved(player, finalOffset)
                        }

                        draggingPlayerNumber = null
                        activeDragDelta = Offset.Zero
                        dropCandidateNumber = null
                      },
                      onDragCancel = {
                        draggingPlayerNumber = null
                        activeDragDelta = Offset.Zero
                        dropCandidateNumber = null
                      }
                    )
                  }
                } else {
                  Modifier
                }
              )
          ) {
            TacticalPitchInteractivePlayerNode(
              player = player,
              roleName = slot.roleName,
              isHome = isHome,
              isPlanningMode = isPlanningMode,
              isBeingDragged = isBeingDragged,
              isDropCandidate = isDropCandidate,
              onClick = {
                onPlayerClick(player)
              }
            )
          }
        }

        // Bottom Pitch Info Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(Color(0x99000000))
            .padding(horizontal = 12.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "⚽ $teamName ($currentFormation)",
            color = GoldYellow,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = if (isPlanningMode) "✋ Oyuncuyu sürükleyip başka oyuncunun üstüne bırakın" else "11 Oyuncunun Tamamı Sahada",
            color = if (isPlanningMode) Color(0xFF38BDF8) else Color(0xFF94A3B8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

/**
 * Player node on the tactical pitch with badge, role indicator, and drag feedback
 */
@Composable
private fun TacticalPitchInteractivePlayerNode(
  player: PlayerLineup,
  roleName: String,
  isHome: Boolean,
  isPlanningMode: Boolean,
  isBeingDragged: Boolean,
  isDropCandidate: Boolean,
  onClick: () -> Unit
) {
  val jerseyColor = if (isHome) Color(0xFFDC2626) else Color(0xFF2563EB)
  val haloColor by animateColorAsState(
    targetValue = when {
      isBeingDragged -> GoldYellow
      isDropCandidate -> Color(0xFF38BDF8)
      player.isCaptain -> GoldYellow
      else -> Color.White
    },
    animationSpec = tween(150),
    label = "haloColor"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxSize()
      .clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(jerseyColor)
        .border(
          width = if (isBeingDragged || isDropCandidate) 2.5.dp else 1.5.dp,
          color = haloColor,
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "${player.number}",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 11.sp
      )

      // Captain armband indicator
      if (player.isCaptain) {
        Surface(
          color = GoldYellow,
          shape = CircleShape,
          modifier = Modifier
            .size(10.dp)
            .align(Alignment.TopEnd)
        ) {
          Text(
            text = "C",
            color = Color.Black,
            fontSize = 6.5.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
          )
        }
      }

      // Drag handle icon overlay when in planning mode
      if (isPlanningMode && !isBeingDragged) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .align(Alignment.BottomEnd)
            .background(Color(0xCC000000), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.DragHandle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(7.dp)
          )
        }
      }
    }

    // Role Tag (KL, STP, SLB, SĞB, ÖNL, OS, SNT, etc.)
    Surface(
      color = when (roleName) {
        "KL" -> Color(0xFFEAB308)
        "STP", "SLB", "SĞB" -> Color(0xFF3B82F6)
        "ÖNL", "OS", "ON", "SLK", "SĞK", "KNT" -> Color(0xFF10B981)
        "SNT", "FV" -> Color(0xFFEF4444)
        else -> Color(0xFF64748B)
      },
      shape = RoundedCornerShape(3.dp),
      modifier = Modifier.padding(top = 1.5.dp)
    ) {
      Text(
        text = roleName,
        color = Color.White,
        fontSize = 7.5.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(horizontal = 3.dp, vertical = 0.5.dp)
      )
    }

    // Player Display Name (High-contrast, clearly legible on tactical pitch)
    Surface(
      color = Color(0xF50A1128),
      shape = RoundedCornerShape(5.dp),
      border = BorderStroke(0.8.dp, Color(0x99FFFFFF)),
      modifier = Modifier
        .padding(top = 2.dp)
        .widthIn(min = 40.dp, max = 74.dp)
    ) {
      Text(
        text = getTacticalPlayerDisplayName(player.name),
        color = Color.White,
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
      )
    }

    // Rating or Swap Hint
    if (isDropCandidate) {
      Text(
        text = "🔄 Takas",
        color = Color(0xFF38BDF8),
        fontSize = 7.5.sp,
        fontWeight = FontWeight.Black
      )
    } else {
      Text(
        text = "★ ${player.rating}",
        color = GoldYellow,
        fontSize = 7.5.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}

/**
 * Returns a formatted player display name that fits cleanly on the tactical pitch without getting truncated
 */
private fun getTacticalPlayerDisplayName(fullName: String): String {
  val clean = fullName.trim()
  if (clean.length <= 9) return clean
  val parts = clean.split("\\s+".toRegex())
  return when {
    parts.size == 1 -> parts[0]
    parts.size == 2 -> {
      if (parts[1].length <= 8) parts[1] else "${parts[0].first()}. ${parts[1]}"
    }
    else -> "${parts[0].first()}. ${parts.last()}"
  }
}

/**
 * Calculates dynamic tactical pitch coordinates based on team formation.
 * Generates exact (relX, relY) relative slots for the starting 11 players.
 */
private fun getTacticalSlots(formation: String): List<TacticalSlot> {
  return when (formation) {
    "4-4-2" -> listOf(
      TacticalSlot(0.50f, 0.10f, "KL"),
      // DEF (4)
      TacticalSlot(0.16f, 0.28f, "SLB"),
      TacticalSlot(0.38f, 0.26f, "STP"),
      TacticalSlot(0.62f, 0.26f, "STP"),
      TacticalSlot(0.84f, 0.28f, "SĞB"),
      // MID (4)
      TacticalSlot(0.16f, 0.52f, "SLK"),
      TacticalSlot(0.38f, 0.50f, "OS"),
      TacticalSlot(0.62f, 0.50f, "OS"),
      TacticalSlot(0.84f, 0.52f, "SĞK"),
      // FWD (2)
      TacticalSlot(0.38f, 0.76f, "SNT"),
      TacticalSlot(0.62f, 0.76f, "SNT")
    )

    "3-5-2" -> listOf(
      TacticalSlot(0.50f, 0.10f, "KL"),
      // DEF (3)
      TacticalSlot(0.24f, 0.27f, "STP"),
      TacticalSlot(0.50f, 0.25f, "STP"),
      TacticalSlot(0.76f, 0.27f, "STP"),
      // MID (5)
      TacticalSlot(0.12f, 0.48f, "KNT"),
      TacticalSlot(0.35f, 0.44f, "ÖNL"),
      TacticalSlot(0.50f, 0.55f, "OS"),
      TacticalSlot(0.65f, 0.44f, "ÖNL"),
      TacticalSlot(0.88f, 0.48f, "KNT"),
      // FWD (2)
      TacticalSlot(0.38f, 0.78f, "SNT"),
      TacticalSlot(0.62f, 0.78f, "SNT")
    )

    "4-3-3" -> listOf(
      TacticalSlot(0.50f, 0.10f, "KL"),
      // DEF (4)
      TacticalSlot(0.16f, 0.28f, "SLB"),
      TacticalSlot(0.38f, 0.26f, "STP"),
      TacticalSlot(0.62f, 0.26f, "STP"),
      TacticalSlot(0.84f, 0.28f, "SĞB"),
      // MID (3)
      TacticalSlot(0.28f, 0.48f, "OS"),
      TacticalSlot(0.50f, 0.44f, "ÖNL"),
      TacticalSlot(0.72f, 0.48f, "OS"),
      // FWD (3)
      TacticalSlot(0.18f, 0.74f, "SLK"),
      TacticalSlot(0.50f, 0.78f, "SNT"),
      TacticalSlot(0.82f, 0.74f, "SĞK")
    )

    "4-2-3-1" -> listOf(
      TacticalSlot(0.50f, 0.10f, "KL"),
      // DEF (4)
      TacticalSlot(0.16f, 0.28f, "SLB"),
      TacticalSlot(0.38f, 0.26f, "STP"),
      TacticalSlot(0.62f, 0.26f, "STP"),
      TacticalSlot(0.84f, 0.28f, "SĞB"),
      // CDM (2)
      TacticalSlot(0.36f, 0.44f, "ÖNL"),
      TacticalSlot(0.64f, 0.44f, "ÖNL"),
      // CAM (3)
      TacticalSlot(0.18f, 0.62f, "SLK"),
      TacticalSlot(0.50f, 0.60f, "ON"),
      TacticalSlot(0.82f, 0.62f, "SĞK"),
      // ST (1)
      TacticalSlot(0.50f, 0.80f, "SNT")
    )

    "3-4-3" -> listOf(
      TacticalSlot(0.50f, 0.10f, "KL"),
      // DEF (3)
      TacticalSlot(0.24f, 0.27f, "STP"),
      TacticalSlot(0.50f, 0.25f, "STP"),
      TacticalSlot(0.76f, 0.27f, "STP"),
      // MID (4)
      TacticalSlot(0.14f, 0.50f, "KNT"),
      TacticalSlot(0.38f, 0.48f, "OS"),
      TacticalSlot(0.62f, 0.48f, "OS"),
      TacticalSlot(0.86f, 0.50f, "KNT"),
      // FWD (3)
      TacticalSlot(0.20f, 0.74f, "SLK"),
      TacticalSlot(0.50f, 0.78f, "SNT"),
      TacticalSlot(0.80f, 0.74f, "SĞK")
    )

    "5-3-2" -> listOf(
      TacticalSlot(0.50f, 0.10f, "KL"),
      // DEF (5)
      TacticalSlot(0.12f, 0.32f, "SLB"),
      TacticalSlot(0.30f, 0.26f, "STP"),
      TacticalSlot(0.50f, 0.24f, "STP"),
      TacticalSlot(0.70f, 0.26f, "STP"),
      TacticalSlot(0.88f, 0.32f, "SĞB"),
      // MID (3)
      TacticalSlot(0.28f, 0.50f, "OS"),
      TacticalSlot(0.50f, 0.46f, "ÖNL"),
      TacticalSlot(0.72f, 0.50f, "OS"),
      // FWD (2)
      TacticalSlot(0.38f, 0.76f, "SNT"),
      TacticalSlot(0.62f, 0.76f, "SNT")
    )

    "4-1-4-1" -> listOf(
      TacticalSlot(0.50f, 0.10f, "KL"),
      // DEF (4)
      TacticalSlot(0.16f, 0.28f, "SLB"),
      TacticalSlot(0.38f, 0.26f, "STP"),
      TacticalSlot(0.62f, 0.26f, "STP"),
      TacticalSlot(0.84f, 0.28f, "SĞB"),
      // CDM (1)
      TacticalSlot(0.50f, 0.42f, "ÖNL"),
      // MID (4)
      TacticalSlot(0.16f, 0.60f, "SLK"),
      TacticalSlot(0.38f, 0.58f, "OS"),
      TacticalSlot(0.62f, 0.58f, "OS"),
      TacticalSlot(0.84f, 0.60f, "SĞK"),
      // ST (1)
      TacticalSlot(0.50f, 0.80f, "SNT")
    )

    else -> {
      // Dynamic fallback for any arbitrary formation format (e.g. "5-4-1", "4-4-1-1")
      val parts = formation.split("-").mapNotNull { it.toIntOrNull() }
      if (parts.isNotEmpty() && parts.sum() == 10) {
        val result = mutableListOf<TacticalSlot>()
        result.add(TacticalSlot(0.50f, 0.10f, "KL"))

        val totalLines = parts.size
        val lineSpacing = 0.70f / totalLines

        parts.forEachIndexed { lineIdx, count ->
          val y = 0.24f + lineIdx * lineSpacing
          val role = when (lineIdx) {
            0 -> "DEF"
            totalLines - 1 -> "FWD"
            else -> "MID"
          }
          val xSpacing = 1.0f / (count + 1)
          for (c in 1..count) {
            result.add(TacticalSlot(c * xSpacing, y, role))
          }
        }
        result
      } else {
        // Safe 4-3-3 fallback
        listOf(
          TacticalSlot(0.50f, 0.10f, "KL"),
          TacticalSlot(0.16f, 0.28f, "SLB"),
          TacticalSlot(0.38f, 0.26f, "STP"),
          TacticalSlot(0.62f, 0.26f, "STP"),
          TacticalSlot(0.84f, 0.28f, "SĞB"),
          TacticalSlot(0.28f, 0.48f, "OS"),
          TacticalSlot(0.50f, 0.44f, "ÖNL"),
          TacticalSlot(0.72f, 0.48f, "OS"),
          TacticalSlot(0.18f, 0.74f, "SLK"),
          TacticalSlot(0.50f, 0.78f, "SNT"),
          TacticalSlot(0.82f, 0.74f, "SĞK")
        )
      }
    }
  }
}
