package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.FirebaseAuthManager
import com.example.service.FirestoreSyncService
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TealDark
import kotlinx.coroutines.launch

@Composable
fun AuthDialog(
  authManager: FirebaseAuthManager,
  syncService: FirestoreSyncService?,
  virtualBalance: Long,
  onDismiss: () -> Unit,
  onAuthSuccess: () -> Unit = {}
) {
  val scope = rememberCoroutineScope()
  var isRegisterMode by remember { mutableStateOf(false) }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  val currentUser = authManager.currentUser.value

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("auth_dialog"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = TealDark,
          shape = CircleShape,
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.AccountCircle,
              contentDescription = null,
              tint = GoldYellow,
              modifier = Modifier.size(24.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = if (currentUser != null && !currentUser.isAnonymous) "Hesap Bilgileri" else if (isRegisterMode) "Kayıt Ol & Bakiyeni Koru" else "Giriş Yap",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "Sanal TP ve kuponların bulutta güvende",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
      ) {
        // TP Balance Card
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFF0FDF4),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Mevcut Sanal Bakiye",
                  fontSize = 11.sp,
                  color = Color(0xFF166534)
                )
                Text(
                  text = "$virtualBalance TP",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF14532D)
                )
              }
            }
            if (currentUser != null && !currentUser.isAnonymous) {
              Surface(
                color = Color(0xFFDCFCE7),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "Buluta Bağlı",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF15803D),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (currentUser != null && !currentUser.isAnonymous) {
          // Logged In view
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Giriş yapıldı: ${currentUser.email}",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                scope.launch {
                  isLoading = true
                  syncService?.syncWalletToFirestore()
                  isLoading = false
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = TealDark),
              modifier = Modifier.fillMaxWidth()
            ) {
              if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
              } else {
                Text("🔄 Bulut Verilerini Şimdi Eşitle")
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
              onClick = {
                authManager.signOut()
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Çıkış Yap", color = Color(0xFFDC2626))
            }
          }
        } else {
          // Not Logged in form
          OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("E-posta Adresi") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64748B)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Şifre") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B)) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null,
                  tint = Color(0xFF64748B)
                )
              }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = errorMessage ?: "",
              color = Color(0xFFDC2626),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              if (email.isBlank() || password.length < 6) {
                errorMessage = "Lütfen geçerli e-posta ve en az 6 haneli şifre girin."
                return@Button
              }
              scope.launch {
                isLoading = true
                errorMessage = null
                val result = if (isRegisterMode) {
                  authManager.signUpWithEmail(email, password)
                } else {
                  authManager.signInWithEmail(email, password)
                }

                if (result.isSuccess) {
                  syncService?.pullDataFromFirestore()
                  onAuthSuccess()
                  onDismiss()
                } else {
                  errorMessage = result.exceptionOrNull()?.localizedMessage ?: "İşlem başarısız oldu."
                }
                isLoading = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TealDark),
            modifier = Modifier.fillMaxWidth()
          ) {
            if (isLoading) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
              Text(if (isRegisterMode) "Kayıt Ol & Bakiyeyi Kaydet" else "Giriş Yap")
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Google Sign-In Scaffold button
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                scope.launch {
                  isLoading = true
                  val res = authManager.signInAnonymously()
                  if (res.isSuccess) {
                    syncService?.syncWalletToFirestore()
                    onAuthSuccess()
                    onDismiss()
                  } else {
                    errorMessage = "Misafir/Google bağlantısı kurulamadı."
                  }
                  isLoading = false
                }
              }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "🌐", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Google / Hızlı Misafir İle Devam Et",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          TextButton(
            onClick = {
              isRegisterMode = !isRegisterMode
              errorMessage = null
            },
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = if (isRegisterMode) "Zaten hesabın var mı? Giriş Yap" else "Yeni misin? Ücretsiz Kayıt Ol",
              fontSize = 12.sp,
              color = TealDark,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Kapat")
      }
    }
  )
}
