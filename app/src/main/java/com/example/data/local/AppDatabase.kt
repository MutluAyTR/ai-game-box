package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    TicketEntity::class,
    TransactionEntity::class,
    WalletEntity::class,
    UserBalance::class,
    BetEntry::class,
    MatchEntity::class,
    LeagueEntity::class,
    OddsEntity::class,
    UserStatistics::class,
    DailyMissionEntity::class,
    Match::class,
    BasketballMatchEntity::class,
    TennisMatchEntity::class,
    Formula1RaceEntity::class
  ],
  version = 7,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun ticketDao(): TicketDao
  abstract fun walletDao(): WalletDao
  abstract fun userBalanceDao(): UserBalanceDao
  abstract fun betEntryDao(): BetEntryDao
  abstract fun matchDao(): MatchDao
  abstract fun simulatedMatchDao(): SimulatedMatchDao
  abstract fun leagueDao(): LeagueDao
  abstract fun oddsDao(): OddsDao
  abstract fun userStatisticsDao(): UserStatisticsDao
  abstract fun dailyMissionDao(): DailyMissionDao
  abstract fun basketballMatchDao(): BasketballMatchDao
  abstract fun tennisMatchDao(): TennisMatchDao
  abstract fun formula1RaceDao(): Formula1RaceDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "tahmin_arena_db"
        )
          .fallbackToDestructiveMigration(true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
