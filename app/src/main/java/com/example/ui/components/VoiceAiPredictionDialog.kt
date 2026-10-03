package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.engine.GeminiAnalysisService
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Voice-Powered AI Match Prediction Assistant with Gemini AI & Voiced (TTS) Responses.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceAiPredictionDialog(
  matches: List<Match>,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var isListening by remember { mutableStateOf(false) }
  var isProcessingGemini by remember { mutableStateOf(false) }
  var isSpeakingResponse by remember { mutableStateOf(false) }
  var spokenQuery by remember { mutableStateOf("") }
  var aiResponseText by remember { mutableStateOf("") }
  var statusMessage by remember { mutableStateOf("Mikrofon butonuna basıp konuşun...") }

  // Text-To-Speech Engine Setup
  var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }
  var isTtsReady by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    var tts: TextToSpeech? = null
    tts = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        val result = tts?.setLanguage(Locale.forLanguageTag("tr-TR"))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          tts?.setLanguage(Locale.US)
        }
        isTtsReady = true
      }
    }
    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String?) {
        isSpeakingResponse = true
      }
      override fun onDone(utteranceId: String?) {
        isSpeakingResponse = false
      }
      override fun onError(utteranceId: String?) {
        isSpeakingResponse = false
      }
    })
    ttsInstance = tts

    onDispose {
      try {
        tts.stop()
        tts.shutdown()
      } catch (_: Exception) {}
    }
  }

  fun speakText(text: String) {
    if (isTtsReady && text.isNotBlank()) {
      isSpeakingResponse = true
      val params = Bundle().apply {
        putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ai_prediction_${System.currentTimeMillis()}")
      }
      ttsInstance?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "prediction_utterance")
    }
  }

  fun stopSpeaking() {
    ttsInstance?.stop()
    isSpeakingResponse = false
  }

  // Voice Recognizer logic
  fun handleRecognizedSpeech(text: String) {
    spokenQuery = text
    statusMessage = "Gemini AI analiz ediyor..."
    isProcessingGemini = true

    coroutineScope.launch {
      val answer = GeminiAnalysisService.answerVoiceQuery(text, matches)
      aiResponseText = answer
      isProcessingGemini = false
      statusMessage = "Gemini yanıtı seslendiriyor..."
      speakText(answer)
    }
  }

  // Speech Recognizer
  var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

  DisposableEffect(Unit) {
    val recognizer = if (SpeechRecognizer.isRecognitionAvailable(context)) {
      SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
          override fun onReadyForSpeech(params: Bundle?) {
            isListening = true
            statusMessage = "Dinliyorum... (Örn: 'Galatasaray maçını kim kazanır?')"
          }
          override fun onBeginningOfSpeech() {}
          override fun onRmsChanged(rmsdB: Float) {}
          override fun onBufferReceived(buffer: ByteArray?) {}
          override fun onEndOfSpeech() {
            isListening = false
            statusMessage = "Ses işleniyor..."
          }
          override fun onError(error: Int) {
            isListening = false
            statusMessage = "Ses algılanamadı, lütfen tekrar deneyin veya örnek soru seçin."
          }
          override fun onResults(results: Bundle?) {
            isListening = false
            val matchesList = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = matchesList?.firstOrNull() ?: ""
            if (recognizedText.isNotBlank()) {
              handleRecognizedSpeech(recognizedText)
            }
          }
          override fun onPartialResults(partialResults: Bundle?) {}
          override fun onEvent(eventType: Int, params: Bundle?) {}
        })
      }
    } else null

    speechRecognizer = recognizer

    onDispose {
      try {
        recognizer?.destroy()
      } catch (_: Exception) {}
    }
  }

  // Permission Launcher
  val audioPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Hangi maçın tahminini istiyorsunuz?")
      }
      try {
        speechRecognizer?.startListening(intent)
      } catch (_: Exception) {
        statusMessage = "Mikrofon başlatılamadı."
      }
    } else {
      statusMessage = "Mikrofon izni verilmedi. Aşağıdaki hazır soruları kullanabilirsiniz."
    }
  }

  fun startListening() {
    stopSpeaking()
    audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
  }

  val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isListening) 1.25f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  ModalBottomSheet(
    onDismissRequest = {
      stopSpeaking()
      onDismiss()
    },
    sheetState = sheetState,
    containerColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFFEFF6FF),
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Gemini Asistan",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Sesli Gemini AI Tahmin Asistanı",
              fontWeight = FontWeight.Black,
              fontSize = 15.sp,
              color = TealDark
            )
            Text(
              text = "Konuşarak maç tahmini isteyin, sesli dinleyin",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        IconButton(onClick = {
          stopSpeaking()
          onDismiss()
        }) {
          Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF64748B))
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Animated Microphone Core Button
      Box(
        modifier = Modifier
          .size(88.dp)
          .scale(if (isListening) pulseScale else 1f)
          .clip(CircleShape)
          .background(
            if (isListening) Brush.radialGradient(listOf(Color(0xFFE11D48), Color(0xFFBE123C)))
            else Brush.radialGradient(listOf(TealPrimary, TealDark))
          )
          .clickable {
            if (isListening) {
              speechRecognizer?.stopListening()
              isListening = false
            } else {
              startListening()
            }
          }
          .testTag("voice_prediction_mic_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
          contentDescription = "Mikrofon",
          tint = Color.White,
          modifier = Modifier.size(42.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Status text
      Text(
        text = statusMessage,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isListening) Color(0xFFE11D48) else Color(0xFF475569),
        textAlign = TextAlign.Center
      )

      if (spokenQuery.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          color = Color(0xFFF1F5F9),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "🗣️ \"$spokenQuery\"",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Gemini Response Card
      if (isProcessingGemini) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          CircularProgressIndicator(color = TealPrimary, modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Gemini Flash verileri ve xG korelasyonunu hesaplıyor...",
            fontSize = 12.sp,
            color = Color(0xFF64748B)
          )
        }
      } else if (aiResponseText.isNotBlank()) {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
          modifier = Modifier.fillMaxWidth()
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
                  contentDescription = "Gemini",
                  tint = Color(0xFF059669),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Gemini Sesli Yanıtı",
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp,
                  color = Color(0xFF065F46)
                )
              }

              // Voice playback button
              Surface(
                color = if (isSpeakingResponse) Color(0xFFDCFCE7) else Color.White,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier.clickable {
                  if (isSpeakingResponse) stopSpeaking() else speakText(aiResponseText)
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = if (isSpeakingResponse) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Seslendir",
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isSpeakingResponse) "Durdur" else "Yeniden Dinle",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = aiResponseText,
              fontSize = 13.sp,
              color = Color(0xFF1E293B),
              lineHeight = 20.sp,
              fontWeight = FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Quick Speech Prompts
      Text(
        text = "Veya hızlı soru seçebilirsiniz:",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF64748B),
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(8.dp))

      val sampleQueries = listOf(
        "Galatasaray - Fenerbahçe derbisi ne olur?",
        "Arsenal - Manchester City maçı için AI tahmini nedir?",
        "EuroLeague Fenerbahçe Beko maçında tempo nasıl?",
        "Bugünün en banko 2.5 Üst maçı hangisi?"
      )

      sampleQueries.forEach { query ->
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFF8FAFC),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { handleRecognizedSpeech(query) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "💬", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = query,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF334155)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
