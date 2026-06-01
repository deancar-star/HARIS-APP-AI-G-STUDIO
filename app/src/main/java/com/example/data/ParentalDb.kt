package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentalDao {
    // Child Table
    @Query("SELECT * FROM children")
    fun getAllChildren(): Flow<List<Child>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child): Long

    @Update
    suspend fun updateChild(child: Child)

    @Delete
    suspend fun deleteChild(child: Child)

    // ScreenTimeConfig Table
    @Query("SELECT * FROM screen_time_configs WHERE childId = :childId")
    fun getScreenTimeConfig(childId: Long): Flow<ScreenTimeConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenTimeConfig(config: ScreenTimeConfig)

    // AppRules Table
    @Query("SELECT * FROM app_rules WHERE childId = :childId")
    fun getAppRulesForChild(childId: Long): Flow<List<AppRule>>

    @Query("SELECT * FROM app_rules WHERE childId = :childId AND packageName = :packageName")
    suspend fun getAppRuleByPkg(childId: Long, packageName: String): AppRule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppRule(rule: AppRule)

    @Update
    suspend fun updateAppRule(rule: AppRule)

    // CategoryFilter Table
    @Query("SELECT * FROM category_filters WHERE childId = :childId")
    fun getCategoryFiltersForChild(childId: Long): Flow<List<CategoryFilter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryFilter(filter: CategoryFilter)

    @Update
    suspend fun updateCategoryFilter(filter: CategoryFilter)

    // ActivityLogs Table
    @Query("SELECT * FROM activity_logs WHERE childId = :childId ORDER BY timestamp DESC LIMIT 100")
    fun getActivityLogsForChild(childId: Long): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLog)

    @Query("DELETE FROM activity_logs WHERE childId = :childId")
    suspend fun clearActivityLogs(childId: Long)

    // Location Table
    @Query("SELECT * FROM child_locations WHERE childId = :childId")
    fun getChildLocation(childId: Long): Flow<ChildLocation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChildLocation(location: ChildLocation)

    // CoachMessage Table
    @Query("SELECT * FROM coach_messages ORDER BY timestamp ASC")
    fun getAllCoachMessages(): Flow<List<CoachMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoachMessage(message: CoachMessage)

    @Query("DELETE FROM coach_messages")
    suspend fun clearCoachMessages()

    // ParentUser Table
    @Query("SELECT * FROM parent_users WHERE email = :email LIMIT 1")
    suspend fun getParentUser(email: String): ParentUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParentUser(user: ParentUser)
}

@Database(
    entities = [
        Child::class,
        ScreenTimeConfig::class,
        AppRule::class,
        CategoryFilter::class,
        ActivityLog::class,
        ChildLocation::class,
        CoachMessage::class,
        ParentUser::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ParentalDatabase : RoomDatabase() {
    abstract fun parentalDao(): ParentalDao

    companion object {
        @Volatile
        private var INSTANCE: ParentalDatabase? = null

        fun getDatabase(context: Context): ParentalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParentalDatabase::class.java,
                    "parental_safety_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
