package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class ParentalRepository(private val dao: ParentalDao) {

    val allChildren: Flow<List<Child>> = dao.getAllChildren()

    fun getScreenTimeConfig(childId: Long): Flow<ScreenTimeConfig?> = dao.getScreenTimeConfig(childId)

    fun getAppRulesForChild(childId: Long): Flow<List<AppRule>> = dao.getAppRulesForChild(childId)

    fun getCategoryFiltersForChild(childId: Long): Flow<List<CategoryFilter>> = dao.getCategoryFiltersForChild(childId)

    fun getActivityLogsForChild(childId: Long): Flow<List<ActivityLog>> = dao.getActivityLogsForChild(childId)

    fun getChildLocation(childId: Long): Flow<ChildLocation?> = dao.getChildLocation(childId)

    val allCoachMessages: Flow<List<CoachMessage>> = dao.getAllCoachMessages()

    suspend fun insertChild(child: Child): Long = dao.insertChild(child)

    suspend fun updateChild(child: Child) = dao.updateChild(child)

    suspend fun deleteChild(child: Child) = dao.deleteChild(child)

    suspend fun saveScreenTimeConfig(config: ScreenTimeConfig) = dao.insertScreenTimeConfig(config)

    suspend fun saveAppRule(rule: AppRule) = dao.insertAppRule(rule)

    suspend fun updateAppRule(rule: AppRule) = dao.updateAppRule(rule)

    suspend fun saveCategoryFilter(filter: CategoryFilter) = dao.insertCategoryFilter(filter)

    suspend fun updateCategoryFilter(filter: CategoryFilter) = dao.updateCategoryFilter(filter)

    suspend fun insertActivityLog(log: ActivityLog) = dao.insertActivityLog(log)

    suspend fun clearActivityLogs(childId: Long) = dao.clearActivityLogs(childId)

    suspend fun saveChildLocation(location: ChildLocation) = dao.insertChildLocation(location)

    suspend fun addCoachMessage(message: CoachMessage) = dao.insertCoachMessage(message)

    suspend fun clearCoachMessages() = dao.clearCoachMessages()

    suspend fun getParentByEmail(email: String): ParentUser? = dao.getParentUser(email)

    suspend fun registerParent(user: ParentUser) = dao.insertParentUser(user)

    // Seeds initial kids and safety state if empty
    suspend fun prepopulateIfEmpty() {
        val children = dao.getAllChildren().first()
        if (children.isEmpty()) {
            // Seed Kid 1: Emma
            val emmaId = dao.insertChild(
                Child(
                    name = "Emma",
                    age = 12,
                    avatarEmoji = "👧",
                    isDevicePaused = false,
                    deviceModel = "Emma's Pixel 7a",
                    batteryPercent = 78,
                    isOnline = true
                )
            )

            dao.insertScreenTimeConfig(
                ScreenTimeConfig(
                    childId = emmaId,
                    dailyLimitMinutes = 120,
                    usedMinutes = 85,
                    bedtimeStartHour = 21,
                    bedtimeStartMinute = 30
                )
            )

            // App limits for Emma
            val emmaApps = listOf(
                AppRule(childId = emmaId, appName = "YouTube", packageName = "com.google.android.youtube", isBlocked = false, limitMinutes = 60),
                AppRule(childId = emmaId, appName = "Roblox", packageName = "com.roblox.client", isBlocked = true, limitMinutes = 30),
                AppRule(childId = emmaId, appName = "TikTok", packageName = "com.zhiliaoapp.musically", isBlocked = true, limitMinutes = 0),
                AppRule(childId = emmaId, appName = "Chrome", packageName = "com.android.chrome", isBlocked = false, limitMinutes = -1),
                AppRule(childId = emmaId, appName = "Facebook", packageName = "com.facebook.katana", isBlocked = true, limitMinutes = 0),
                AppRule(childId = emmaId, appName = "Instagram", packageName = "com.instagram.android", isBlocked = true, limitMinutes = 0),
                AppRule(childId = emmaId, appName = "Snapchat", packageName = "com.snapchat.android", isBlocked = true, limitMinutes = 0),
                AppRule(childId = emmaId, appName = "WhatsApp", packageName = "com.whatsapp", isBlocked = false, limitMinutes = 120),
                AppRule(childId = emmaId, appName = "Telegram", packageName = "org.telegram.messenger", isBlocked = true, limitMinutes = 0),
                AppRule(childId = emmaId, appName = "Discord", packageName = "com.discord", isBlocked = true, limitMinutes = 0),
                AppRule(childId = emmaId, appName = "X (Twitter)", packageName = "com.twitter.android", isBlocked = true, limitMinutes = 0),
                AppRule(childId = emmaId, appName = "Pinterest", packageName = "com.pinterest", isBlocked = false, limitMinutes = -1),
                AppRule(childId = emmaId, appName = "Spotify", packageName = "com.spotify.music", isBlocked = false, limitMinutes = -1),
                AppRule(childId = emmaId, appName = "Netflix", packageName = "com.netflix.mediaclient", isBlocked = true, limitMinutes = 0),
                AppRule(childId = emmaId, appName = "Duolingo", packageName = "com.duolingo", isBlocked = false, limitMinutes = -1)
            )
            emmaApps.forEach { dao.insertAppRule(it) }

            // Category limits for Emma
            val emmaCategories = listOf(
                CategoryFilter(childId = emmaId, categoryName = "Adult Content", isBlocked = true),
                CategoryFilter(childId = emmaId, categoryName = "Gaming", isBlocked = false),
                CategoryFilter(childId = emmaId, categoryName = "Social Media", isBlocked = true),
                CategoryFilter(childId = emmaId, categoryName = "Gambling", isBlocked = true)
            )
            emmaCategories.forEach { dao.insertCategoryFilter(it) }

            // Activity Logs for Emma
            val emmaLogs = listOf(
                ActivityLog(childId = emmaId, type = "WEB", detailText = "Accessed wikipedia.org/wiki/List_of_dogs", category = "Education", isBlocked = false, timestamp = System.currentTimeMillis() - 500000),
                ActivityLog(childId = emmaId, type = "WEB", detailText = "Blocked search attempt for 'bypass home filters and parental locks'", category = "Adult Content", isBlocked = true, timestamp = System.currentTimeMillis() - 400000),
                ActivityLog(childId = emmaId, type = "SEARCH", detailText = "Searched: 'how to feel better when lonely and sad'", category = "Social Media", isBlocked = false, timestamp = System.currentTimeMillis() - 300000),
                ActivityLog(childId = emmaId, type = "APP", detailText = "Launched Roblox and played for 30 minutes (Limit Reached)", category = "Gaming", isBlocked = true, timestamp = System.currentTimeMillis() - 150000),
                ActivityLog(childId = emmaId, type = "APP", detailText = "Blocked access to TikTok (Strictly Blocked by Rule)", category = "Social Media", isBlocked = true, timestamp = System.currentTimeMillis() - 100000),
                ActivityLog(childId = emmaId, type = "WEB", detailText = "Accessed duolingo.com - Spanish Lesson 3", category = "Education", isBlocked = false, timestamp = System.currentTimeMillis() - 20000)
            )
            emmaLogs.forEach { dao.insertActivityLog(it) }

            // Location for Emma
            dao.insertChildLocation(
                ChildLocation(
                    childId = emmaId,
                    latitude = 51.5234, // Lincoln's Inn Fields, London (sample location)
                    longitude = -0.1172,
                    addressName = "Lincoln's Inn Fields Library"
                )
            )


            // Seed Kid 2: Leo
            val leoId = dao.insertChild(
                Child(
                    name = "Leo",
                    age = 8,
                    avatarEmoji = "👦",
                    isDevicePaused = false,
                    deviceModel = "Leo's Galaxy Tab",
                    batteryPercent = 92,
                    isOnline = true
                )
            )

            dao.insertScreenTimeConfig(
                ScreenTimeConfig(
                    childId = leoId,
                    dailyLimitMinutes = 60,
                    usedMinutes = 15,
                    bedtimeStartHour = 20,
                    bedtimeStartMinute = 0
                )
            )

            val leoApps = listOf(
                AppRule(childId = leoId, appName = "YouTube Kids", packageName = "com.google.android.apps.youtube.kids", isBlocked = false, limitMinutes = 30),
                AppRule(childId = leoId, appName = "Minecraft", packageName = "com.mojang.minecraftpe", isBlocked = false, limitMinutes = 60),
                AppRule(childId = leoId, appName = "Brawl Stars", packageName = "com.supercell.brawlstars", isBlocked = true, limitMinutes = 0),
                AppRule(childId = leoId, appName = "TikTok Kids", packageName = "com.zhiliaoapp.musically.kids", isBlocked = true, limitMinutes = 0),
                AppRule(childId = leoId, appName = "Roblox", packageName = "com.roblox.client", isBlocked = true, limitMinutes = 0),
                AppRule(childId = leoId, appName = "Instagram", packageName = "com.instagram.android", isBlocked = true, limitMinutes = 0),
                AppRule(childId = leoId, appName = "Facebook", packageName = "com.facebook.katana", isBlocked = true, limitMinutes = 0),
                AppRule(childId = leoId, appName = "Snapchat", packageName = "com.snapchat.android", isBlocked = true, limitMinutes = 0),
                AppRule(childId = leoId, appName = "WhatsApp", packageName = "com.whatsapp", isBlocked = false, limitMinutes = 30),
                AppRule(childId = leoId, appName = "Spotify Kids", packageName = "com.spotify.music.kids", isBlocked = false, limitMinutes = -1)
            )
            leoApps.forEach { dao.insertAppRule(it) }

            val leoCategories = listOf(
                CategoryFilter(childId = leoId, categoryName = "Adult Content", isBlocked = true),
                CategoryFilter(childId = leoId, categoryName = "Gaming", isBlocked = false),
                CategoryFilter(childId = leoId, categoryName = "Social Media", isBlocked = true),
                CategoryFilter(childId = leoId, categoryName = "Gambling", isBlocked = true)
            )
            leoCategories.forEach { dao.insertCategoryFilter(it) }

            val leoLogs = listOf(
                ActivityLog(childId = leoId, type = "APP", detailText = "Launched Minecraft (Story Mode)", category = "Gaming", isBlocked = false, timestamp = System.currentTimeMillis() - 200000),
                ActivityLog(childId = leoId, type = "WEB", detailText = "Accessed minecraft.net/guide", category = "Gaming", isBlocked = false, timestamp = System.currentTimeMillis() - 120000),
                ActivityLog(childId = leoId, type = "APP", detailText = "Blocked launch of Brawl Stars (Strictly Blocked)", category = "Gaming", isBlocked = true, timestamp = System.currentTimeMillis() - 50000),
                ActivityLog(childId = leoId, type = "SEARCH", detailText = "Searched: 'cute cat videos funny'", category = "Video Streaming", isBlocked = false, timestamp = System.currentTimeMillis() - 10000)
            )
            leoLogs.forEach { dao.insertActivityLog(it) }

            dao.insertChildLocation(
                ChildLocation(
                    childId = leoId,
                    latitude = 51.5074, // London Charing Cross
                    longitude = -0.1278,
                    addressName = "Home (Charing Cross Road)"
                )
            )

            // Seed Safety Coach First Intro
            dao.insertCoachMessage(
                CoachMessage(
                    role = "model",
                    content = "Hello Parent! I'm your **Family Safety Coach**, powered by Gemini 3.5. I scan your children's live histories and alert feeds to identify visual patterns, digital risks (like loneliness indicators or cyberbullying), and highlights of positive study habits. \n\nSelect a child from the dropdown at the top, then tap **'Analyze Activity with Gemini'** to receive a structured safety summary, or ask me any question directly below!"
                )
            )
        } else {
            // Live upgrade path for existing demo schemas
            dao.insertCoachMessage(
                CoachMessage(
                    role = "model",
                    content = "Welcome back parent! Premium safety database refreshed. Added dynamic control layers for Discord, Snapchat, Instagram, Facebook, X, Pinterest, Spotify, and Netflix to give you comprehensive control over all apps!"
                )
            )
            for (child in children) {
                val existingRules = dao.getAppRulesForChild(child.id).first()
                if (existingRules.size <= 5) {
                    val defaultNewApps = if (child.name == "Emma") {
                        listOf(
                            AppRule(childId = child.id, appName = "Facebook", packageName = "com.facebook.katana", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "Instagram", packageName = "com.instagram.android", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "Snapchat", packageName = "com.snapchat.android", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "WhatsApp", packageName = "com.whatsapp", isBlocked = false, limitMinutes = 120),
                            AppRule(childId = child.id, appName = "Telegram", packageName = "org.telegram.messenger", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "Discord", packageName = "com.discord", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "X (Twitter)", packageName = "com.twitter.android", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "Pinterest", packageName = "com.pinterest", isBlocked = false, limitMinutes = -1),
                            AppRule(childId = child.id, appName = "Spotify", packageName = "com.spotify.music", isBlocked = false, limitMinutes = -1),
                            AppRule(childId = child.id, appName = "Netflix", packageName = "com.netflix.mediaclient", isBlocked = true, limitMinutes = 0)
                        )
                    } else {
                        listOf(
                            AppRule(childId = child.id, appName = "TikTok Kids", packageName = "com.zhiliaoapp.musically.kids", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "Roblox", packageName = "com.roblox.client", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "Instagram", packageName = "com.instagram.android", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "Facebook", packageName = "com.facebook.katana", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "Snapchat", packageName = "com.snapchat.android", isBlocked = true, limitMinutes = 0),
                            AppRule(childId = child.id, appName = "WhatsApp", packageName = "com.whatsapp", isBlocked = false, limitMinutes = 30),
                            AppRule(childId = child.id, appName = "Spotify Kids", packageName = "com.spotify.music.kids", isBlocked = false, limitMinutes = -1)
                        )
                    }
                    for (app in defaultNewApps) {
                        if (dao.getAppRuleByPkg(child.id, app.packageName) == null) {
                            dao.insertAppRule(app)
                        }
                    }
                }
            }
        }
    }
}
