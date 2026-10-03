package com.example.service

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.TahminArenaApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Authentication Manager managing Firebase Auth and Google Sign-In state.
 */
class FirebaseAuthManager(private val context: Context) {

  private val auth: FirebaseAuth? = try {
    (context.applicationContext as? Application)?.let {
      TahminArenaApplication.initFirebase(it)
    }
    FirebaseAuth.getInstance()
  } catch (t: Throwable) {
    Log.w("FirebaseAuthManager", "FirebaseAuth not available: ${t.message}")
    null
  }

  private val _currentUser = MutableStateFlow<FirebaseUser?>(try { auth?.currentUser } catch (_: Throwable) { null })
  val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

  init {
    try {
      auth?.addAuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
      }
    } catch (e: Throwable) {
      Log.w("FirebaseAuthManager", "AuthStateListener warning: ${e.message}")
    }
  }

  val isLoggedIn: Boolean
    get() = auth?.currentUser != null

  val userDisplayName: String
    get() = auth?.currentUser?.displayName
      ?: auth?.currentUser?.email?.substringBefore("@")
      ?: "Misafir Kullanıcı"

  val userEmail: String
    get() = auth?.currentUser?.email ?: "misafir@tahminarena.com"

  suspend fun signInAnonymously(): Result<FirebaseUser> {
    val fbAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth servisi kullanılamıyor."))
    return try {
      val result = fbAuth.signInAnonymously().await()
      _currentUser.value = result.user
      if (result.user != null) {
        Result.success(result.user!!)
      } else {
        Result.failure(IllegalStateException("Kullanıcı oluşturulamadı."))
      }
    } catch (e: Exception) {
      Log.e("FirebaseAuthManager", "Anonymous sign in failed", e)
      Result.failure(e)
    }
  }

  suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> {
    val fbAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth servisi kullanılamıyor."))
    return try {
      val result = fbAuth.signInWithEmailAndPassword(email.trim(), pass.trim()).await()
      _currentUser.value = result.user
      if (result.user != null) {
        Result.success(result.user!!)
      } else {
        Result.failure(IllegalStateException("Giriş başarısız."))
      }
    } catch (e: Exception) {
      Log.e("FirebaseAuthManager", "Email sign in failed", e)
      Result.failure(e)
    }
  }

  suspend fun signUpWithEmail(email: String, pass: String): Result<FirebaseUser> {
    val fbAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth servisi kullanılamıyor."))
    return try {
      val result = fbAuth.createUserWithEmailAndPassword(email.trim(), pass.trim()).await()
      _currentUser.value = result.user
      if (result.user != null) {
        Result.success(result.user!!)
      } else {
        Result.failure(IllegalStateException("Kayıt başarısız."))
      }
    } catch (e: Exception) {
      Log.e("FirebaseAuthManager", "Sign up failed", e)
      Result.failure(e)
    }
  }

  fun getCurrentUserId(): String {
    return try {
      auth?.currentUser?.uid ?: "guest_local_user"
    } catch (_: Throwable) {
      "guest_local_user"
    }
  }

  fun signOut() {
    try {
      auth?.signOut()
    } catch (t: Throwable) {
      Log.w("FirebaseAuthManager", "SignOut warning: ${t.message}")
    }
    _currentUser.value = null
  }

  companion object {
    fun getCurrentUserId(): String {
      return try {
        FirebaseAuth.getInstance().currentUser?.uid ?: "guest_local_user"
      } catch (_: Throwable) {
        "guest_local_user"
      }
    }
  }
}
