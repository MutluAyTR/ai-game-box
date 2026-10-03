package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark

@Composable
fun LoginScreen(
  onLoginSuccess: (username: String, email: String) -> Unit,
  onContinueAsGuest: () -> Unit
) {
  var isRegisterMode by remember { mutableStateOf(false) }
  var usernameInput by remember { mutableStateOf("aycadogan") }
  var emailInput by remember { mutableStateOf("aycadogan6464@gmail.com") }
  var passwordInput by remember { mutableStateOf("123456") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            TealDark,
            Color(0xFF072325),
            Color(0xFF051719)
          )
        )
      )
      .testTag("login_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // App Brand Logo
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(GoldYellow),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "🏆", fontSize = 38.sp)
      }

      Spacer(modifier = Modifier.height(16.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "TAHMİN",
          color = GoldYellow,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "ARENA",
          color = Color.White,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Canlı İddaa & AI Veri Analizi Platformu",
        color = Color(0xFF94A3B8),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Card Form
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Text(
            text = if (isRegisterMode) "Yeni Hesap Oluştur" else "Giriş Yap",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )

          Text(
            text = if (isRegisterMode)
              "10.000 TP başlangıç hediyenizle bültene katılın."
            else
              "Tahmin Puanlarınızla sanal kuponlar yapın.",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
          )

          // Username Field
          OutlinedTextField(
            value = usernameInput,
            onValueChange = { usernameInput = it },
            label = { Text("Kullanıcı Adı", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = null, tint = TealDark, modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TealDark,
              unfocusedBorderColor = Color(0xFFCBD5E1)
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Email Field
          OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("E-Posta", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = null, tint = TealDark, modifier = Modifier.size(18.dp))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TealDark,
              unfocusedBorderColor = Color(0xFFCBD5E1)
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Password Field
          OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Şifre", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = null, tint = TealDark, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = null,
                  tint = Color(0xFF64748B),
                  modifier = Modifier.size(18.dp)
                )
              }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TealDark,
              unfocusedBorderColor = Color(0xFFCBD5E1)
            )
          )

          // Error text if present
          AnimatedVisibility(visible = errorMessage != null) {
            errorMessage?.let { msg ->
              Text(
                text = msg,
                color = Color(0xFFDC2626),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Submit Button
          Button(
            onClick = {
              if (usernameInput.isBlank() || emailInput.isBlank() || passwordInput.isBlank()) {
                errorMessage = "Lütfen tüm alanları doldurun."
              } else {
                errorMessage = null
                onLoginSuccess(usernameInput.trim(), emailInput.trim())
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("login_submit_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldYellow)
          ) {
            Text(
              text = if (isRegisterMode) "Kayıt Ol ve Başla (+10.000 TP)" else "Giriş Yap",
              color = TealDark,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Toggle Mode
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isRegisterMode) "Zaten hesabınız var mı?" else "Hesabınız yok mu?",
              fontSize = 12.sp,
              color = Color(0xFF64748B)
            )
            TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
              Text(
                text = if (isRegisterMode) "Giriş Yap" else "Kayıt Ol",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TealDark
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Guest / Direct Play Action
      Surface(
        color = Color(0xFF0F3A3D),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onContinueAsGuest() }
          .testTag("guest_login_button")
      ) {
        Row(
          modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "⚡ Giriş Yapmadan Misafir Olarak Devam Et ›",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Bu uygulama tamamen sanal TP (Tahmin Puanı) ile çalışır ve eğlence/analiz amaçlıdır.",
        color = Color(0xFF64748B),
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
