package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class ParentalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ParentalRepository
    private val db = ParentalDatabase.getDatabase(application)
    
    // Pairing Simulation State
    private val _generatedPairCode = MutableStateFlow<String?>(null)
    val generatedPairCode: StateFlow<String?> = _generatedPairCode.asStateFlow()

    private val _connectionStatus = MutableStateFlow("IDLE") // IDLE, WAITING, CONNECTED
    val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()

    fun startPairingFlow() {
        val randomCode = (100000..999999).random()
        val formattedCode = "${randomCode / 1000} ${randomCode % 1000}"
        _generatedPairCode.value = formattedCode
        _connectionStatus.value = "WAITING"
    }

    fun cancelPairing() {
        _generatedPairCode.value = null
        _connectionStatus.value = "IDLE"
    }

    fun simulateChildAppConnection(name: String, age: Int, device: String, avatar: String) {
        viewModelScope.launch {
            _connectionStatus.value = "CONNECTED"
            
            // Insert child to Database
            val childId = repository.insertChild(
                Child(
                    name = name,
                    age = age,
                    avatarEmoji = avatar,
                    isDevicePaused = false,
                    deviceModel = device,
                    batteryPercent = 100,
                    isOnline = true
                )
            )

            // Screen time configs
            repository.saveScreenTimeConfig(
                ScreenTimeConfig(
                    childId = childId,
                    dailyLimitMinutes = 120,
                    usedMinutes = 0,
                    bedtimeStartHour = 21,
                    bedtimeStartMinute = 0
                )
            )

            // Setup common app rules
            val appRules = listOf(
                AppRule(childId = childId, appName = "YouTube", packageName = "com.google.android.youtube", isBlocked = false, limitMinutes = 60),
                AppRule(childId = childId, appName = "Roblox", packageName = "com.roblox.client", isBlocked = false, limitMinutes = -1),
                AppRule(childId = childId, appName = "TikTok", packageName = "com.zhiliaoapp.musically", isBlocked = true, limitMinutes = 0),
                AppRule(childId = childId, appName = "Chrome", packageName = "com.android.chrome", isBlocked = false, limitMinutes = -1),
                AppRule(childId = childId, appName = "Facebook", packageName = "com.facebook.katana", isBlocked = true, limitMinutes = 0),
                AppRule(childId = childId, appName = "Instagram", packageName = "com.instagram.android", isBlocked = true, limitMinutes = 0),
                AppRule(childId = childId, appName = "Snapchat", packageName = "com.snapchat.android", isBlocked = true, limitMinutes = 0),
                AppRule(childId = childId, appName = "WhatsApp", packageName = "com.whatsapp", isBlocked = false, limitMinutes = 120),
                AppRule(childId = childId, appName = "Telegram", packageName = "org.telegram.messenger", isBlocked = true, limitMinutes = 0),
                AppRule(childId = childId, appName = "Discord", packageName = "com.discord", isBlocked = true, limitMinutes = 0),
                AppRule(childId = childId, appName = "X (Twitter)", packageName = "com.twitter.android", isBlocked = true, limitMinutes = 0),
                AppRule(childId = childId, appName = "Pinterest", packageName = "com.pinterest", isBlocked = false, limitMinutes = -1),
                AppRule(childId = childId, appName = "Spotify", packageName = "com.spotify.music", isBlocked = false, limitMinutes = -1),
                AppRule(childId = childId, appName = "Netflix", packageName = "com.netflix.mediaclient", isBlocked = true, limitMinutes = 0)
            )
            appRules.forEach { repository.saveAppRule(it) }

            // Setup category filters
            val categories = listOf(
                CategoryFilter(childId = childId, categoryName = "Adult Content", isBlocked = true),
                CategoryFilter(childId = childId, categoryName = "Gaming", isBlocked = false),
                CategoryFilter(childId = childId, categoryName = "Social Media", isBlocked = true),
                CategoryFilter(childId = childId, categoryName = "Gambling", isBlocked = true)
            )
            categories.forEach { repository.saveCategoryFilter(it) }

            // Setup location
            repository.saveChildLocation(
                ChildLocation(
                    childId = childId,
                    latitude = 36.7538, // Algiers center coordinates
                    longitude = 3.0588,
                    addressName = "Didouche Mourad St, Algiers"
                )
            )

            // Activity Log entry
            repository.insertActivityLog(
                ActivityLog(
                    childId = childId,
                    type = "SYSTEM",
                    detailText = "Successfully paired companion app on $device. Secure tunnel established.",
                    category = "System Pairing",
                    isBlocked = false
                )
            )

            // Auto-select the newly paired child so the UI updates to show them
            _selectedChildId.value = childId

            // Clean up state
            _generatedPairCode.value = null
            _connectionStatus.value = "IDLE"
        }
    }

    init {
        repository = ParentalRepository(db.parentalDao())
        
        // Populate demo tables, limits, and rules if empty
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // Children Stream
    val children: StateFlow<List<Child>> = repository.allChildren
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Selected Child ID
    private val _selectedChildId = MutableStateFlow<Long?>(null)
    val selectedChildId: StateFlow<Long?> = _selectedChildId.asStateFlow()

    init {
        // Automatically select the first child when list completes
        viewModelScope.launch {
            children.filter { it.isNotEmpty() }.firstOrNull()?.let { list ->
                if (_selectedChildId.value == null) {
                    _selectedChildId.value = list.first().id
                }
            }
        }
    }

    /**
     * Set selected children
     */
    fun selectChild(id: Long) {
        _selectedChildId.value = id
    }

    // Selected Child Stream
    val selectedChild: StateFlow<Child?> = combine(children, selectedChildId) { list, id ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Screen Time Config Stream
    val screenTimeConfig: StateFlow<ScreenTimeConfig?> = selectedChildId
        .flatMapLatest { id ->
            if (id != null) repository.getScreenTimeConfig(id) else flowOf(null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // App Rules Stream
    val appRules: StateFlow<List<AppRule>> = selectedChildId
        .flatMapLatest { id ->
            if (id != null) repository.getAppRulesForChild(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Web / Category Filters Stream
    val categoryFilters: StateFlow<List<CategoryFilter>> = selectedChildId
        .flatMapLatest { id ->
            if (id != null) repository.getCategoryFiltersForChild(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Activities Log Stream
    val activityLogs: StateFlow<List<ActivityLog>> = selectedChildId
        .flatMapLatest { id ->
            if (id != null) repository.getActivityLogsForChild(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Child Location Stream
    val childLocation: StateFlow<ChildLocation?> = selectedChildId
        .flatMapLatest { id ->
            if (id != null) repository.getChildLocation(id) else flowOf(null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Safety Coach Thread
    val coachMessages: StateFlow<List<CoachMessage>> = repository.allCoachMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gemini Operation States
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _coachLoading = MutableStateFlow(false)
    val coachLoading: StateFlow<Boolean> = _coachLoading.asStateFlow()

    // Location Simulation Route Points (London route coordinates)
    private val routeStages = listOf(
        Pair(51.5234, -0.1172) to "Lincoln's Inn Fields School",
        Pair(51.5215, -0.1210) to "British Museum Library",
        Pair(51.5145, -0.1250) to "Leicester Square Metro",
        Pair(51.5113, -0.1285) to "National Gallery Gym",
        Pair(51.5074, -0.1278) to "Home (Charing Cross Road)"
    )
    private var currentRouteIndex = 0

    // Emergency Panic Button (Pause all children)
    fun pauseAllDevices(paused: Boolean) {
        viewModelScope.launch {
            children.value.forEach { child ->
                repository.updateChild(child.copy(isDevicePaused = paused))
            }
        }
    }

    // Toggle selected child's device
    fun toggleDevicePause() {
        val child = selectedChild.value ?: return
        viewModelScope.launch {
            repository.updateChild(child.copy(isDevicePaused = !child.isDevicePaused))
            
            // Insert status log
            val action = if (!child.isDevicePaused) "PAUSED child's device lock screen" else "UNPAUSED device access"
            repository.insertActivityLog(
                ActivityLog(
                    childId = child.id,
                    type = "SYSTEM",
                    detailText = "Parent $action remotely from Parental Control Dashboard.",
                    category = "System Control",
                    isBlocked = false
                )
            )
        }
    }

    // Add 15 mins screen time
    fun addExtraScreenTime(minutes: Int) {
        val child = selectedChild.value ?: return
        val config = screenTimeConfig.value ?: return
        viewModelScope.launch {
            val newLimit = config.dailyLimitMinutes + minutes
            repository.saveScreenTimeConfig(config.copy(dailyLimitMinutes = newLimit))
            
            repository.insertActivityLog(
                ActivityLog(
                    childId = child.id,
                    type = "SYSTEM",
                    detailText = "Granted +$minutes minutes of extra screen time limit.",
                    category = "Limit Extension",
                    isBlocked = false
                )
            )
        }
    }

    // Toggle individual App rules
    fun toggleAppBlocked(appName: String, isBlocked: Boolean) {
        val childId = selectedChildId.value ?: return
        val rule = appRules.value.find { it.appName == appName } ?: return
        viewModelScope.launch {
            repository.updateAppRule(rule.copy(isBlocked = isBlocked))
            
            val status = if (isBlocked) "Blocked" else "Allowed"
            repository.insertActivityLog(
                ActivityLog(
                    childId = childId,
                    type = "SYSTEM",
                    detailText = "Web rules modified: App '$appName' set to $status.",
                    category = "Rule Changes",
                    isBlocked = false
                )
            )
        }
    }

    // Set daily minutes limit for app
    fun updateAppTimeLimit(appName: String, minutes: Int) {
        val childId = selectedChildId.value ?: return
        val rule = appRules.value.find { it.appName == appName } ?: return
        viewModelScope.launch {
            repository.updateAppRule(rule.copy(limitMinutes = minutes))
            
            val durationLabel = if (minutes < 0) "No Limit" else "$minutes minutes daily"
            repository.insertActivityLog(
                ActivityLog(
                    childId = childId,
                    type = "SYSTEM",
                    detailText = "App '$appName' timer adjusted to: $durationLabel.",
                    category = "Rule Changes",
                    isBlocked = false
                )
            )
        }
    }

    // Toggle category filters
    fun toggleCategoryFilter(categoryName: String, isBlocked: Boolean) {
        val childId = selectedChildId.value ?: return
        val filter = categoryFilters.value.find { it.categoryName == categoryName } ?: return
        viewModelScope.launch {
            repository.updateCategoryFilter(filter.copy(isBlocked = isBlocked))
            
            val status = if (isBlocked) "Blocked" else "Allowed"
            repository.insertActivityLog(
                ActivityLog(
                    childId = childId,
                    type = "SYSTEM",
                    detailText = "Parent changed Web filter: Category '$categoryName' is now $status.",
                    category = "Safety Filter Change",
                    isBlocked = false
                )
            )
        }
    }

    // Simulates a child changing location walks and checks into geofences
    fun simulateLocationMovement() {
        val child = selectedChild.value ?: return
        viewModelScope.launch {
            currentRouteIndex = (currentRouteIndex + 1) % routeStages.size
            val stage = routeStages[currentRouteIndex]
            val lat = stage.first.first
            val lon = stage.first.second
            val address = stage.second
            
            repository.saveChildLocation(
                ChildLocation(
                    childId = child.id,
                    latitude = lat,
                    longitude = lon,
                    addressName = address
                )
            )

            // Trigger log on interesting geo checks
            val geofenceEventText = when (address) {
                "Lincoln's Inn Fields School" -> "Geofence Check: Entered 'School' Zone."
                "Home (Charing Cross Road)" -> "Geofence Check: Safely reached 'Home' Zone."
                else -> "Location updated: Device reported position near $address."
            }

            repository.insertActivityLog(
                ActivityLog(
                    childId = child.id,
                    type = "SYSTEM",
                    detailText = geofenceEventText,
                    category = "Geolocation",
                    isBlocked = false
                )
            )
        }
    }

    // Clears all histories
    fun clearLogs() {
        val childId = selectedChildId.value ?: return
        viewModelScope.launch {
            repository.clearActivityLogs(childId)
        }
    }

    fun recordSimulatedAppUsage(appName: String, minutes: Int, packageName: String) {
        val child = selectedChild.value ?: return
        val config = screenTimeConfig.value ?: return
        viewModelScope.launch {
            val xmlUsed = config.usedMinutes + minutes
            repository.saveScreenTimeConfig(config.copy(usedMinutes = xmlUsed))
            repository.insertActivityLog(
                ActivityLog(
                    childId = child.id,
                    type = "APP",
                    detailText = "Opened $appName and used it for $minutes minutes.",
                    category = "App Usage",
                    isBlocked = false
                )
            )
        }
    }

    fun logBlockedAppAttempt(appName: String, reason: String) {
        val child = selectedChild.value ?: return
        viewModelScope.launch {
            repository.insertActivityLog(
                ActivityLog(
                    childId = child.id,
                    type = "APP",
                    detailText = "Blocked access attempt to $appName. Reason: $reason",
                    category = "Prohibited Action",
                    isBlocked = true
                )
            )
        }
    }

    fun logWebSearchAttempt(query: String, category: String, isBlocked: Boolean) {
        val child = selectedChild.value ?: return
        viewModelScope.launch {
            repository.insertActivityLog(
                ActivityLog(
                    childId = child.id,
                    type = if (isBlocked) "WEB" else "SEARCH",
                    detailText = if (isBlocked) "Blocked web access to '$query' (Prohibited: $category)" else "Searched for '$query'",
                    category = category,
                    isBlocked = isBlocked
                )
            )
        }
    }

    // Formatter helper for AI logs
    private fun formatLogsForAI(logs: List<ActivityLog>): String {
        return logs.joinToString("\n") { log ->
            val timestampLabel = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp))
            "- [$timestampLabel] [Type: ${log.type}] ${log.detailText} (Category: ${log.category}; Blocked: ${log.isBlocked})"
        }
    }

    /**
     * Quick analyze with Gemini Safety Coach
     */
    fun analyzeLogsWithGemini() {
        val child = selectedChild.value ?: return
        val logs = activityLogs.value
        
        if (logs.isEmpty()) {
            viewModelScope.launch {
                repository.addCoachMessage(
                    CoachMessage(
                        role = "model",
                        content = "There are currently no logs in my history to analyze for ${child.name}. Set some limits or search for things first so I can inspect their digital trace!"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            
            val formattedLogs = formatLogsForAI(logs)
            val parentPrompt = """
                Generate a safety analysis report for ${child.name} (Age: ${child.age}) based on these monitored search and application activity logs:
                $formattedLogs
                
                Identify emotional signs, safety warnings, and positives. Include clear, friendly parent intervention tips.
            """.trimIndent()

            val systemInstruction = """
                You are the Family Safety Coach, an AI child psychologist. Provide a structured review using:
                1. 🛡️ Safety Assessment: Red flag evaluations (Low, Medium, or High Risk) with rationale.
                2. 🌟 Positive Highlights: Praise their search queries or language/educational learnings.
                3. 💬 Practical Parent Guidelines: Offer direct, low-friction, supportive talking points.
                
                Format with clean Material markdown. Keep it concise but insightful.
            """.trimIndent()

            // Save parent request bubble (represented as action)
            repository.addCoachMessage(
                CoachMessage(
                    role = "user",
                    content = "*Requested live Gemini analysis of ${child.name}'s current safety logs*"
                )
            )

            // Make the call
            val aiResponse = GeminiService.generateContent(
                prompt = parentPrompt,
                systemInstruction = systemInstruction
            )

            repository.addCoachMessage(
                CoachMessage(
                    role = "model",
                    content = aiResponse
                )
            )

            _isAnalyzing.value = false
        }
    }

    /**
     * Sends custom coaching question to AI Advisor
     */
    fun sendCoachQuestion(parentQuery: String) {
        if (parentQuery.isBlank()) return
        val child = selectedChild.value
        val logs = activityLogs.value

        viewModelScope.launch {
            _coachLoading.value = true
            
            // Add user bubble
            repository.addCoachMessage(
                CoachMessage(
                    role = "user",
                    content = parentQuery
                )
            )

            val contextualPrompt = if (child != null && logs.isNotEmpty()) {
                val formattedLogs = formatLogsForAI(logs.take(15))
                """
                    I am the parent. My child's name is ${child.name} (Age ${child.age}).
                    Here are their recent activity logs for reference context:
                    $formattedLogs
                    
                    My question to you is: "$parentQuery"
                """.trimIndent()
            } else {
                parentQuery
            }

            val systemInstruction = """
                You are the Family Safety Coach, a friendly, authoritative child security consultant.
                The parent has asked for tips or specific settings. Give warm, actionable, research-validated advice. Avoid developer jargon. Use markdown list rules.
            """.trimIndent()

            val response = GeminiService.generateContent(
                prompt = contextualPrompt,
                systemInstruction = systemInstruction
            )

            repository.addCoachMessage(
                CoachMessage(
                    role = "model",
                    content = response
                )
            )

            _coachLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearCoachMessages()
            // Reset to introductory message
            repository.addCoachMessage(
                CoachMessage(
                    role = "model",
                    content = "Chat thread cleared. Ask me anything about child device safety, web filters, or request a profile summary above!"
                )
            )
        }
    }

    // --- MULTILINGUAL & THEME SUPPORT ---
    private val _appLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }

    private val _isDarkTheme = MutableStateFlow(false) // Default to false (Light Mode) per user preference for high visibility lavender look
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
    }

    // --- PAIRING & COMPANION ENGINE MODE ---
    private val _appMode = MutableStateFlow(AppMode.ROLE_SELECTION)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    fun setAppMode(mode: AppMode) {
        _appMode.value = mode
    }

    val pairingCode = MutableStateFlow("719-482")
    var childNameInput = MutableStateFlow("")
    var childAgeInput = MutableStateFlow("11")

    // Companion Permission Checklist State (Simulated indicators for visual feedback)
    val accessibilityEnabled = MutableStateFlow(false)
    val drawOverlaysEnabled = MutableStateFlow(false)
    val deviceAdminEnabled = MutableStateFlow(false)
    val locationPermissionEnabled = MutableStateFlow(false)

    fun toggleAccessibility(enabled: Boolean) {
        accessibilityEnabled.value = enabled
    }
    fun toggleDrawOverlays(enabled: Boolean) {
        drawOverlaysEnabled.value = enabled
    }
    fun toggleDeviceAdmin(enabled: Boolean) {
        deviceAdminEnabled.value = enabled
    }
    fun toggleLocationPermission(enabled: Boolean) {
        locationPermissionEnabled.value = enabled
    }

    fun generateNewPairingCode() {
        val rand1 = (100..999).random()
        val rand2 = (100..999).random()
        pairingCode.value = "$rand1-$rand2"
    }

    fun registerChildFromPairing(name: String, ageCode: String) {
        val parsedAge = ageCode.toIntOrNull() ?: 12
        viewModelScope.launch {
            val childId = repository.insertChild(
                Child(
                    name = name,
                    age = parsedAge,
                    avatarEmoji = if (parsedAge < 10) "👦" else "👧",
                    isDevicePaused = false,
                    deviceModel = "Companion Phone (Android)",
                    batteryPercent = 98,
                    isOnline = true
                )
            )
            // Save initial screen time rules
            repository.saveScreenTimeConfig(
                ScreenTimeConfig(
                    childId = childId,
                    dailyLimitMinutes = 150,
                    usedMinutes = 0
                )
            )
            // Save starter app lists
            val starterRules = listOf(
                AppRule(childId = childId, appName = "YouTube", packageName = "com.google.android.youtube", isBlocked = false),
                AppRule(childId = childId, appName = "Roblox", packageName = "com.roblox.client", isBlocked = true),
                AppRule(childId = childId, appName = "TikTok", packageName = "com.zhiliaoapp.musically", isBlocked = true),
                AppRule(childId = childId, appName = "Brawl Stars", packageName = "com.supercell.brawlstars", isBlocked = false),
                AppRule(childId = childId, appName = "Facebook", packageName = "com.facebook.katana", isBlocked = true),
                AppRule(childId = childId, appName = "Instagram", packageName = "com.instagram.android", isBlocked = true),
                AppRule(childId = childId, appName = "Snapchat", packageName = "com.snapchat.android", isBlocked = true),
                AppRule(childId = childId, appName = "WhatsApp", packageName = "com.whatsapp", isBlocked = false),
                AppRule(childId = childId, appName = "Telegram", packageName = "org.telegram.messenger", isBlocked = true),
                AppRule(childId = childId, appName = "Discord", packageName = "com.discord", isBlocked = true),
                AppRule(childId = childId, appName = "X (Twitter)", packageName = "com.twitter.android", isBlocked = true),
                AppRule(childId = childId, appName = "Spotify", packageName = "com.spotify.music", isBlocked = false),
                AppRule(childId = childId, appName = "Netflix", packageName = "com.netflix.mediaclient", isBlocked = true)
            )
            starterRules.forEach { repository.saveAppRule(it) }

            // Set current child to newly paired
            _selectedChildId.value = childId
            _appMode.value = AppMode.PARENT
            
            // Log the paired alert
            repository.insertActivityLog(
                ActivityLog(
                    childId = childId,
                    type = "SYSTEM",
                    detailText = "Device paired successfully and online.",
                    category = "System",
                    isBlocked = false
                )
            )
        }
    }

    // --- PARENT AUTHENTICATION STATE ---
    private val _currentParent = MutableStateFlow<ParentUser?>(null)
    val currentParent: StateFlow<ParentUser?> = _currentParent.asStateFlow()

    private val _parentAuthError = MutableStateFlow<String?>(null)
    val parentAuthError: StateFlow<String?> = _parentAuthError.asStateFlow()

    private val _isProcessingAuth = MutableStateFlow(false)
    val isProcessingAuth: StateFlow<Boolean> = _isProcessingAuth.asStateFlow()

    fun clearAuthError() {
        _parentAuthError.value = null
    }

    fun loginParent(email: String, passwordText: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _parentAuthError.value = null
            if (email.isBlank() || passwordText.isBlank()) {
                _parentAuthError.value = "auth_error_fields"
                return@launch
            }
            _isProcessingAuth.value = true
            try {
                // Network latency simulation like genuine Firebase Cloud Sync Auth
                kotlinx.coroutines.delay(600)
                val user = repository.getParentByEmail(email.trim().lowercase())
                if (user != null && user.passwordHash == passwordText) {
                    _currentParent.value = user
                    _appMode.value = AppMode.PARENT
                    onSuccess()
                } else {
                    _parentAuthError.value = "auth_error_invalid"
                }
            } catch (e: Exception) {
                _parentAuthError.value = "Auth system exception: ${e.message}"
            } finally {
                _isProcessingAuth.value = false
            }
        }
    }

    fun signupParent(email: String, passwordText: String, confirmText: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _parentAuthError.value = null
            if (email.isBlank() || passwordText.isBlank() || confirmText.isBlank()) {
                _parentAuthError.value = "auth_error_fields"
                return@launch
            }
            if (passwordText != confirmText) {
                _parentAuthError.value = "auth_error_mismatch"
                return@launch
            }
            _isProcessingAuth.value = true
            try {
                // Latency simulation for online cloud registration
                kotlinx.coroutines.delay(800)
                val sanitizedEmail = email.trim().lowercase()
                
                val existing = repository.getParentByEmail(sanitizedEmail)
                if (existing != null) {
                    _parentAuthError.value = "Account already exists with this email."
                    return@launch
                }
                
                val newUser = ParentUser(email = sanitizedEmail, passwordHash = passwordText)
                repository.registerParent(newUser)
                _currentParent.value = newUser
                _appMode.value = AppMode.PARENT
                onSuccess()
            } catch (e: Exception) {
                _parentAuthError.value = "Registration error: ${e.message}"
            } finally {
                _isProcessingAuth.value = false
            }
        }
    }

    fun authWithSocial(email: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _parentAuthError.value = null
            _isProcessingAuth.value = true
            try {
                // Latency simulation representing Google/Facebook Auth token verification
                kotlinx.coroutines.delay(1000)
                val sanitizedEmail = email.trim().lowercase()
                var user = repository.getParentByEmail(sanitizedEmail)
                if (user == null) {
                    user = ParentUser(
                        email = sanitizedEmail,
                        passwordHash = "social_oauth_verified_token",
                        name = name
                    )
                    repository.registerParent(user)
                }
                _currentParent.value = user
                _appMode.value = AppMode.PARENT
                onSuccess()
            } catch (e: Exception) {
                _parentAuthError.value = "Social Auth integration failure: ${e.message}"
            } finally {
                _isProcessingAuth.value = false
            }
        }
    }

    fun signInWithGoogleReal(
        context: android.content.Context,
        onSuccess: () -> Unit,
        onFailed: (String, Boolean) -> Unit
    ) {
        val webClientId = com.example.BuildConfig.GOOGLE_WEB_CLIENT_ID
        if (webClientId.isEmpty() || webClientId == "YOUR_GOOGLE_WEB_CLIENT_ID_PLACEHOLDER") {
            onFailed("Google Web Client ID is not configured in the project BuildConfig. Please set GOOGLE_WEB_CLIENT_ID in the AI Studio Secrets panel.", true)
            return
        }

        viewModelScope.launch {
            _parentAuthError.value = null
            _isProcessingAuth.value = true
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val getCredRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = getCredRequest
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val email = googleIdTokenCredential.id
                    val displayName = googleIdTokenCredential.displayName ?: "Parent User"
                    authWithSocial(email, displayName, onSuccess)
                } else {
                    onFailed("Invalid credential format in results.", true)
                }
            } catch (e: Exception) {
                val isDeveloperError = e.message?.contains("DEVELOPER_ERROR") == true || e.message?.contains("10:") == true
                val customErrorMsg = if (isDeveloperError) {
                    "Google Developer Error 10: This occurs because your app name, package name, or SHA-1 signing fingerprint are not yet registered in your Google Cloud / Firebase Console. Please register them so Google can verify this app's identity."
                } else {
                    e.message ?: "Google authentication request cancelled or failed."
                }
                onFailed(customErrorMsg, true)
            } finally {
                _isProcessingAuth.value = false
            }
        }
    }

    fun resetScreenTimeUsage() {
        val current = screenTimeConfig.value ?: return
        viewModelScope.launch {
            repository.saveScreenTimeConfig(current.copy(usedMinutes = 0))
        }
    }

    fun logSimulatedAppAction(packageName: String, appName: String, isBlocked: Boolean, blockReason: String?) {
        val childId = _selectedChildId.value ?: return
        viewModelScope.launch {
            val detail = if (isBlocked) {
                "Attempted to open $appName ($packageName) but was intercept-blocked: $blockReason"
            } else {
                "Launched $appName ($packageName) successfully: verified compliance."
            }
            repository.insertActivityLog(
                ActivityLog(
                    childId = childId,
                    type = "APP",
                    detailText = detail,
                    category = "Gaming/Social",
                    isBlocked = isBlocked
                )
            )
        }
    }

    fun logSimulatedWebAction(query: String, category: String, isBlocked: Boolean) {
        val childId = _selectedChildId.value ?: return
        viewModelScope.launch {
            val detail = if (isBlocked) {
                "Searched blocked contents: '$query' (Intercept Category: $category)"
            } else {
                "Searched safely: '$query' (Verified Category: $category)"
            }
            repository.insertActivityLog(
                ActivityLog(
                    childId = childId,
                    type = "SEARCH",
                    detailText = detail,
                    category = category,
                    isBlocked = isBlocked
                )
            )
        }
    }

    fun logoutParent() {
        _currentParent.value = null
        _appMode.value = AppMode.ROLE_SELECTION
    }
}

enum class AppMode {
    ROLE_SELECTION,
    PARENT_AUTH,
    PARENT,
    CHILD
}
