package com.example.service

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.BetEntry
import com.example.data.local.TicketEntity
import com.example.data.local.UserBalance
import com.example.data.local.WalletEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Sync service between local Room SQLite Database and Google Cloud Firestore.
 * Ensures the user's virtual TP wallet, bet history, and placed tickets are persisted in the cloud.
 */
class FirestoreSyncService(
  private val database: AppDatabase
) {

  private val firestore: FirebaseFirestore? by lazy {
    try {
      FirebaseFirestore.getInstance()
    } catch (t: Throwable) {
      Log.w("FirestoreSyncService", "FirebaseFirestore not available: ${t.message}")
      null
    }
  }

  private val auth: FirebaseAuth? by lazy {
    try {
      FirebaseAuth.getInstance()
    } catch (t: Throwable) {
      Log.w("FirestoreSyncService", "FirebaseAuth not available: ${t.message}")
      null
    }
  }

  /**
   * Syncs the current local wallet state and balance to Firestore.
   */
  suspend fun syncWalletToFirestore(): Boolean = withContext(Dispatchers.IO) {
    val fs = firestore ?: return@withContext false
    try {
      val currentUser = auth?.currentUser
      val userId = currentUser?.uid ?: "guest_local_user"

      val wallet = database.walletDao().getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
      val userBalance = database.userBalanceDao().getUserBalance(userId) ?: UserBalance(
        userId = userId,
        balance = wallet.availablePoints,
        lockedBalance = wallet.lockedPoints,
        totalWon = wallet.lifetimeWon,
        totalLost = wallet.lifetimeLost
      )

      val walletData = hashMapOf(
        "userId" to userId,
        "email" to (currentUser?.email ?: "guest@tahminarena.com"),
        "availablePoints" to wallet.availablePoints,
        "lockedPoints" to wallet.lockedPoints,
        "lifetimeWon" to wallet.lifetimeWon,
        "lifetimeLost" to wallet.lifetimeLost,
        "lastUpdated" to System.currentTimeMillis()
      )

      fs.collection("users")
        .document(userId)
        .collection("wallet")
        .document("current_state")
        .set(walletData, SetOptions.merge())
        .await()

      Log.d("FirestoreSyncService", "Successfully synced wallet to Firestore for $userId")
      true
    } catch (e: Exception) {
      Log.w("FirestoreSyncService", "Failed to sync wallet to Firestore: ${e.message}")
      false
    }
  }

  /**
   * Syncs a newly created or settled ticket to Firestore.
   */
  suspend fun syncTicketToFirestore(ticket: TicketEntity): Boolean = withContext(Dispatchers.IO) {
    val fs = firestore ?: return@withContext false
    try {
      val currentUser = auth?.currentUser
      val userId = currentUser?.uid ?: "guest_local_user"

      val ticketData = hashMapOf(
        "ticketNumber" to ticket.ticketNumber,
        "type" to ticket.type,
        "stakePoints" to ticket.stakePoints,
        "totalOdds" to ticket.totalOdds,
        "potentialPoints" to ticket.potentialPoints,
        "status" to ticket.status,
        "createdAt" to ticket.createdAt,
        "selectionsJson" to ticket.selectionsJson,
        "syncedAt" to System.currentTimeMillis()
      )

      fs.collection("users")
        .document(userId)
        .collection("tickets")
        .document(ticket.ticketNumber)
        .set(ticketData, SetOptions.merge())
        .await()

      Log.d("FirestoreSyncService", "Successfully synced ticket #${ticket.ticketNumber} to Firestore")
      true
    } catch (e: Exception) {
      Log.w("FirestoreSyncService", "Failed to sync ticket to Firestore: ${e.message}")
      false
    }
  }

  /**
   * Restores user balance and bet history from Firestore upon login.
   */
  suspend fun pullDataFromFirestore(): Boolean = withContext(Dispatchers.IO) {
    val fs = firestore ?: return@withContext false
    try {
      val currentUser = auth?.currentUser ?: return@withContext false
      val userId = currentUser.uid

      val docSnapshot = fs.collection("users")
        .document(userId)
        .collection("wallet")
        .document("current_state")
        .get()
        .await()

      if (docSnapshot.exists()) {
        val availablePoints = docSnapshot.getLong("availablePoints") ?: 10000L
        val lockedPoints = docSnapshot.getLong("lockedPoints") ?: 0L
        val lifetimeWon = docSnapshot.getLong("lifetimeWon") ?: 0L
        val lifetimeLost = docSnapshot.getLong("lifetimeLost") ?: 0L

        database.walletDao().insertOrUpdateWallet(
          WalletEntity(
            id = 1,
            availablePoints = availablePoints,
            lockedPoints = lockedPoints,
            lifetimeWon = lifetimeWon,
            lifetimeLost = lifetimeLost
          )
        )

        database.userBalanceDao().insertOrUpdateBalance(
          UserBalance(
            userId = userId,
            balance = availablePoints,
            lockedBalance = lockedPoints,
            totalWon = lifetimeWon,
            totalLost = lifetimeLost,
            lastUpdated = System.currentTimeMillis()
          )
        )
        Log.d("FirestoreSyncService", "Restored wallet balance $availablePoints TP from Firestore for $userId")
        true
      } else {
        // First time cloud user, upload local balance
        syncWalletToFirestore()
        true
      }
    } catch (e: Exception) {
      Log.w("FirestoreSyncService", "Failed to pull data from Firestore: ${e.message}")
      false
    }
  }

  /**
   * Syncs a Ticket model object to Firestore
   */
  suspend fun syncTicketToFirestore(ticket: com.example.data.model.Ticket): Boolean = withContext(Dispatchers.IO) {
    val fs = firestore ?: return@withContext false
    try {
      val currentUser = auth?.currentUser
      val userId = currentUser?.uid ?: "guest_local_user"

      val ticketData = hashMapOf(
        "ticketNumber" to ticket.ticketNumber,
        "type" to ticket.type.name,
        "stakePoints" to ticket.stakePoints,
        "totalOdds" to ticket.totalOdds,
        "potentialPoints" to ticket.potentialPoints,
        "status" to ticket.status.name,
        "createdAt" to ticket.createdAt,
        "syncedAt" to System.currentTimeMillis()
      )

      fs.collection("users")
        .document(userId)
        .collection("tickets")
        .document(ticket.ticketNumber)
        .set(ticketData, SetOptions.merge())
        .await()

      Log.d("FirestoreSyncService", "Successfully synced ticket #${ticket.ticketNumber} to Firestore")
      true
    } catch (e: Exception) {
      Log.w("FirestoreSyncService", "Failed to sync ticket to Firestore: ${e.message}")
      false
    }
  }

  companion object {
    suspend fun syncWalletToFirestore(userId: String, balance: Long): Boolean = withContext(Dispatchers.IO) {
      try {
        val firestore = FirebaseFirestore.getInstance()
        val walletData = hashMapOf(
          "userId" to userId,
          "availablePoints" to balance,
          "lastUpdated" to System.currentTimeMillis()
        )
        firestore.collection("users")
          .document(userId)
          .collection("wallet")
          .document("current_state")
          .set(walletData, SetOptions.merge())
          .await()
        true
      } catch (e: Throwable) {
        false
      }
    }

    suspend fun syncTicketToFirestore(userId: String, ticket: com.example.data.model.Ticket): Boolean = withContext(Dispatchers.IO) {
      try {
        val firestore = FirebaseFirestore.getInstance()
        val ticketData = hashMapOf(
          "ticketNumber" to ticket.ticketNumber,
          "type" to ticket.type.name,
          "stakePoints" to ticket.stakePoints,
          "totalOdds" to ticket.totalOdds,
          "potentialPoints" to ticket.potentialPoints,
          "status" to ticket.status.name,
          "createdAt" to ticket.createdAt,
          "syncedAt" to System.currentTimeMillis()
        )
        firestore.collection("users")
          .document(userId)
          .collection("tickets")
          .document(ticket.ticketNumber)
          .set(ticketData, SetOptions.merge())
          .await()
        true
      } catch (e: Throwable) {
        false
      }
    }
  }
}
