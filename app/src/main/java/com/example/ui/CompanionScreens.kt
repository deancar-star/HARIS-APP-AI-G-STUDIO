package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AlertRed
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.data.*

val OkGreen = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    viewModel: ParentalViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // App Preferences Selector (Theme & Language Toggle Menu)
        AppPreferencesMenu(
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .statusBarsPadding(),
            iconColor = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // App Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FamilyRestroom,
                    contentDescription = "Haris Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = viewModel.trans("app_name"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = viewModel.trans("tagline"),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // High Visibility Language and Theme selector banner
            InlineLanguageAndThemePicker(
                viewModel = viewModel,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = viewModel.trans("who_using"),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 10.dp)
            )

            // Double Role Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Parent Card
                Card(
                    onClick = { viewModel.setAppMode(AppMode.PARENT_AUTH) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("role_select_parent"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Parent controller logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewModel.trans("i_am_parent"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = viewModel.trans("parent_desc"),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Forward Icon",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Child Card
                Card(
                    onClick = { viewModel.setAppMode(AppMode.CHILD) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("role_select_child"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = "Child companion logo",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = viewModel.trans("i_am_child"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = viewModel.trans("child_desc"),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Forward Icon",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PrivacyTip,
                    contentDescription = "GDPR compliant icon",
                    tint = OkGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = viewModel.trans("regulation_compliant"),
                    fontSize = 11.sp,
                    color = OkGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildCompanionDashboard(
    viewModel: ParentalViewModel,
    modifier: Modifier = Modifier
) {
    val kids by viewModel.children.collectAsStateWithLifecycle()
    
    val nameInput by viewModel.childNameInput.collectAsStateWithLifecycle()
    val ageInput by viewModel.childAgeInput.collectAsStateWithLifecycle()

    val accessibility by viewModel.accessibilityEnabled.collectAsStateWithLifecycle()
    val overlay by viewModel.drawOverlaysEnabled.collectAsStateWithLifecycle()
    val admin by viewModel.deviceAdminEnabled.collectAsStateWithLifecycle()
    val location by viewModel.locationPermissionEnabled.collectAsStateWithLifecycle()

    var showBypassDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    // Simulation lab state handles
    val selectedChildId by viewModel.selectedChildId.collectAsStateWithLifecycle()
    val selectedChild by viewModel.selectedChild.collectAsStateWithLifecycle()
    val screenTimeConfig by viewModel.screenTimeConfig.collectAsStateWithLifecycle()
    val appRules by viewModel.appRules.collectAsStateWithLifecycle()
    val categoryFilters by viewModel.categoryFilters.collectAsStateWithLifecycle()

    var simulationTab by remember { mutableStateOf(0) } // 0: Permissions, 1: Apps, 2: Web Browser
    var simulatedSearchQuery by remember { mutableStateOf("") }
    var searchedResultText by remember { mutableStateOf<String?>(null) }
    var searchedResultBlocked by remember { mutableStateOf(false) }
    var searchedResultCategory by remember { mutableStateOf("") }

    var simulatedAppOpenedName by remember { mutableStateOf<String?>(null) }
    var simulatedAppOpenedPackage by remember { mutableStateOf<String?>(null) }
    var activeLockReason by remember { mutableStateOf<String?>(null) }
    var activeLockTitle by remember { mutableStateOf<String?>(null) }
    var showAppOpenedContent by remember { mutableStateOf<String?>(null) }

    val launchAppSimulation = { rule: AppRule ->
        val isSingleBlocked = rule.isBlocked
        val isDevPaused = selectedChild?.isDevicePaused == true
        val limitMinutes = screenTimeConfig?.dailyLimitMinutes ?: 120
        val usedMinutes = screenTimeConfig?.usedMinutes ?: 0
        val isExceeded = usedMinutes >= limitMinutes

        simulatedAppOpenedName = rule.appName
        simulatedAppOpenedPackage = rule.packageName

        if (isDevPaused) {
            activeLockTitle = "DEVICE PAUSED"
            activeLockReason = "Your parent has paused this device temporarily to help you focus."
            showAppOpenedContent = "LOCKED"
        } else if (isExceeded) {
            activeLockTitle = "DAILY LIMIT EXCEEDED"
            activeLockReason = "You have used up your screen time budget for today. Good job spending offline time!"
            showAppOpenedContent = "LOCKED"
        } else if (isSingleBlocked) {
            activeLockTitle = "APPLICATION RESTRICTED"
            activeLockReason = "${rule.appName} is blocked by your parent under active security filters."
            showAppOpenedContent = "LOCKED"
        } else {
            activeLockTitle = null
            activeLockReason = null
            showAppOpenedContent = "SUCCESS"
        }

        viewModel.logSimulatedAppAction(
            packageName = rule.packageName,
            appName = rule.appName,
            isBlocked = isDevPaused || isExceeded || isSingleBlocked,
            blockReason = when {
                isDevPaused -> "Remote Pause Lock"
                isExceeded -> "Daily Screentime Exhausted"
                isSingleBlocked -> "Restricted Package Filter"
                else -> null
            }
        )
    }

    val executeWebSearchSimulation = { query: String ->
        if (query.isNotBlank()) {
            var matchedCategory = "Safe Search"
            val lowerQuery = query.lowercase()
            if (lowerQuery.contains("hack") || lowerQuery.contains("bypass") || lowerQuery.contains("proxy") || lowerQuery.contains("unrestricted")) {
                matchedCategory = "Adult Content"
            } else if (lowerQuery.contains("casino") || lowerQuery.contains("poker") || lowerQuery.contains("betting") || lowerQuery.contains("jackpot")) {
                matchedCategory = "Gambling"
            } else if (lowerQuery.contains("roblox") || lowerQuery.contains("game") || lowerQuery.contains("brawl")) {
                matchedCategory = "Gaming"
            } else if (lowerQuery.contains("tiktok") || lowerQuery.contains("instagram") || lowerQuery.contains("facebook")) {
                matchedCategory = "Social Media"
            }

            val filter = categoryFilters.find { it.categoryName == matchedCategory }
            val isBlocked = filter?.isBlocked == true

            searchedResultText = if (isBlocked) {
                "Access blocked to category '$matchedCategory'. Query '$query' contains restricted search terms or patterns."
            } else {
                "Connected to secure gateway. Query successfully parsed of category '$matchedCategory'."
            }
            searchedResultBlocked = isBlocked
            searchedResultCategory = matchedCategory

            viewModel.logSimulatedWebAction(
                query = query,
                category = matchedCategory,
                isBlocked = isBlocked
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Secured Shield",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = viewModel.trans("shield_title"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    // Preferences Selector: Theme & Language Toggle Menu
                    AppPreferencesMenu(
                        viewModel = viewModel,
                        iconColor = MaterialTheme.colorScheme.primary
                    )

                    // Bypass back to parent mode
                    TextButton(
                        onClick = { showBypassDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(imageVector = Icons.Default.LockReset, contentDescription = "Parent Bypass", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(viewModel.trans("bypass_text"), fontSize = 11.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Heartbeat state badge
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (kids.isNotEmpty()) OkGreen.copy(alpha = 0.12f) else AlertRed.copy(alpha = 0.12f)
                    ),
                    border = BorderStroke(1.dp, if (kids.isNotEmpty()) OkGreen else AlertRed),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (kids.isNotEmpty()) OkGreen else AlertRed)
                        )
                        Text(
                            text = if (kids.isNotEmpty()) "Service Monitored & Secured" else "Awaiting Pairing Code Input",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (kids.isNotEmpty()) OkGreen else AlertRed
                        )
                    }
                }

                if (kids.isEmpty()) {
                    // Render Pairing Wizard Screen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Link Child's Device",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = "Enter the 6-digit dynamic pairing code generated on the Parent Dashboard to establish an encrypted, real-time control socket.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            var pairingCodeInput by remember { mutableStateOf("") }
                            var isError by remember { mutableStateOf(false) }

                            OutlinedTextField(
                                value = pairingCodeInput,
                                onValueChange = {
                                    if (it.length <= 7) {
                                        pairingCodeInput = it
                                        isError = false
                                    }
                                },
                                label = { Text("6-Digit Code (e.g. 719-482)") },
                                isError = isError,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = "Key Icon") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { viewModel.childNameInput.value = it },
                                label = { Text("Child's First Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = ageInput,
                                onValueChange = { viewModel.childAgeInput.value = it },
                                label = { Text("Child's Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (pairingCodeInput.isBlank() || nameInput.isBlank()) {
                                        isError = true
                                    } else {
                                        viewModel.registerChildFromPairing(nameInput, ageInput)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("submit_pairing_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.BluetoothConnected, contentDescription = "Confirm Pairing")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Connect to Family Suite")
                            }
                        }
                    }
                } else {
                    // Companion is Active & Linked!
                    val currentChild = selectedChild ?: kids.lastOrNull()
                    
                    // 1. Selector for Children Simulations (if more than one child exists)
                    if (kids.size > 1 && currentChild != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(currentChild.avatarEmoji, fontSize = 16.sp)
                                    }
                                    Column {
                                        Text("Active Simulation Profile", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${currentChild.name} (Age ${currentChild.age})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                                
                                var userExpanded by remember { mutableStateOf(false) }
                                Box {
                                    Button(
                                        onClick = { userExpanded = true },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                                    ) {
                                        Text("Switch Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                    
                                    DropdownMenu(
                                        expanded = userExpanded,
                                        onDismissRequest = { userExpanded = false }
                                    ) {
                                        kids.forEach { child ->
                                            DropdownMenuItem(
                                                text = { Text("${child.avatarEmoji} ${child.name} (Age ${child.age})") },
                                                onClick = {
                                                    viewModel.selectChild(child.id)
                                                    userExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Tab Selection Row
                    TabRow(
                        selectedTabIndex = simulationTab,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[simulationTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        Tab(
                            selected = simulationTab == 0,
                            onClick = { simulationTab = 0 },
                            text = { Text("Permissions", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = simulationTab == 1,
                            onClick = { simulationTab = 1 },
                            text = { Text("Apps Simulator", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = simulationTab == 2,
                            onClick = { simulationTab = 2 },
                            text = { Text("Web Simulator", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    if (currentChild != null) {
                        when (simulationTab) {
                            0 -> {
                                // Status Information Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "Companion Device Status",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        
                                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Hardware Target Profile:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(currentChild.deviceModel, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        }

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Emergency Lock Status:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                text = if (currentChild.isDevicePaused) "PAUSED (BLOCKED)" else "ALLOWED (SAFE)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (currentChild.isDevicePaused) AlertRed else OkGreen
                                            )
                                        }

                                        screenTimeConfig?.let { time ->
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Bedtime Lockdown:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("${String.format("%02d", time.bedtimeStartHour)}:${String.format("%02d", time.bedtimeStartMinute)} PM - ${String.format("%02d", time.bedtimeEndHour)}:${String.format("%02d", time.bedtimeEndMinute)} AM", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }

                                // Permissions switch cards
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "COMPASS PERMISSIONS MONITORING",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                    )

                                    PermissionCard(
                                        title = "Accessibility Service Agent",
                                        description = "Monitors app opening boundaries to block restricted apps instantly.",
                                        isEnabled = accessibility,
                                        onToggle = { viewModel.toggleAccessibility(it) }
                                    )

                                    PermissionCard(
                                        title = "System Alert Overlay Control",
                                        description = "Draws the 'Time Is Up' or 'Blocked' lock screen directly on top of forbidden apps.",
                                        isEnabled = overlay,
                                        onToggle = { viewModel.toggleDrawOverlays(it) }
                                    )

                                    PermissionCard(
                                        title = "Device Policy Administrator",
                                        description = "Enables core tamper-protection preventing unauthorized child uninstalls.",
                                        isEnabled = admin,
                                        onToggle = { viewModel.toggleDeviceAdmin(it) }
                                    )

                                    PermissionCard(
                                        title = "Background Location Tracing",
                                        description = "Transmits accurate regional GPS coordinates back to parent, even when device is passive.",
                                        isEnabled = location,
                                        onToggle = { viewModel.toggleLocationPermission(it) }
                                    )
                                }
                            }

                            1 -> {
                                // APPS SIMULATOR TAB
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Live screentime progress budget
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Screentime Remaining Today", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                screenTimeConfig?.let { time ->
                                                    Text(
                                                        text = "${time.usedMinutes} / ${time.dailyLimitMinutes} Mins Used", 
                                                        fontWeight = FontWeight.Bold, 
                                                        color = if (time.usedMinutes >= time.dailyLimitMinutes) AlertRed else OkGreen, 
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }
                                            
                                            val usedVal = screenTimeConfig?.usedMinutes ?: 0
                                            val limitVal = screenTimeConfig?.dailyLimitMinutes ?: 1
                                            LinearProgressIndicator(
                                                progress = (usedVal.toFloat() / limitVal.toFloat()).coerceIn(0f, 1f),
                                                color = if (usedVal >= limitVal) AlertRed else MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { viewModel.addExtraScreenTime(15) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Add 15 Mins", fontSize = 10.sp)
                                                }

                                                OutlinedButton(
                                                    onClick = { 
                                                        viewModel.resetScreenTimeUsage()
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Reset Usage", fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }

                                    Text(
                                        text = "SIMULATED APP LAUNCHER PANEL",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                    )

                                    Text(
                                        text = "Tap on any application to simulate launching it on the child's phone. This tests our package filters and daily screentime boundaries instantly.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )

                                    // Apps Listing loop
                                    if (appRules.isEmpty()) {
                                        Card(
                                            modifier = Modifier.fillMaxWidth().height(100.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Text("Loading launcher app rule tables...", color = Color.Gray, fontSize = 12.sp)
                                            }
                                        }
                                    } else {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            appRules.forEach { rule ->
                                                val isSingleBlocked = rule.isBlocked
                                                val isDevPaused = currentChild.isDevicePaused
                                                val isExceeded = screenTimeConfig?.let { it.usedMinutes >= it.dailyLimitMinutes } ?: false
                                                val isAppBlockedOverall = isSingleBlocked || isDevPaused || isExceeded

                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { launchAppSimulation(rule) },
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                                                    ),
                                                    border = BorderStroke(1.dp, if (isAppBlockedOverall) AlertRed.copy(alpha = 0.4f) else OkGreen.copy(alpha = 0.4f))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(36.dp)
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(if (isAppBlockedOverall) AlertRed.copy(alpha = 0.12f) else OkGreen.copy(alpha = 0.12f)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    text = rule.appName.take(1),
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (isAppBlockedOverall) AlertRed else OkGreen,
                                                                    fontSize = 16.sp
                                                                )
                                                            }
                                                            Column {
                                                                Text(rule.appName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                                Text(rule.packageName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            }
                                                        }

                                                        // Action labels
                                                        Card(
                                                            shape = RoundedCornerShape(6.dp),
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = if (isAppBlockedOverall) AlertRed.copy(alpha = 0.1f) else OkGreen.copy(alpha = 0.1f)
                                                            )
                                                        ) {
                                                            Text(
                                                                text = if (isAppBlockedOverall) "BLOCKED" else "ALLOWED",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isAppBlockedOverall) AlertRed else OkGreen,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            2 -> {
                                // WEB SEARCH/BROWSING SIMULATOR TAB
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text(
                                                text = "Simulated Safe Web Browser",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            
                                            Text(
                                                text = "Type search terms or tap query presets to evaluate filtering rules locally. Blocked domains trigger an immediate warning panel.",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 15.sp
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = simulatedSearchQuery,
                                                    onValueChange = { simulatedSearchQuery = it },
                                                    placeholder = { Text("Search or type URL...") },
                                                    singleLine = true,
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                
                                                Button(
                                                    onClick = { executeWebSearchSimulation(simulatedSearchQuery) },
                                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                                ) {
                                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                                }
                                            }
                                        }
                                    }

                                    // Search suggestions list
                                    Text(
                                        text = "SEARCH SUGGESTION PRESETS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )

                                    val suggestions = listOf(
                                        "Volcanic eruption facts & history" to "Education",
                                        "Download Roblox cheat hacks and bypass proxy" to "Gaming",
                                        "Casino poker betting real cash jackpot" to "Gambling",
                                        "Bypass home parental lock with unrestricted proxy" to "Adult Content",
                                        "Create new TikTok account with custom birthday" to "Social Media",
                                        "Learn fun Kotlin programming lessons" to "Safe Search"
                                    )

                                    suggestions.forEach { pair ->
                                        val queryText = pair.first
                                        val categoryName = pair.second
                                        val isCategoryBlocked = categoryFilters.find { it.categoryName == categoryName }?.isBlocked == true

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { 
                                                    simulatedSearchQuery = queryText
                                                    executeWebSearchSimulation(queryText) 
                                                },
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1.5f).padding(end = 8.dp)) {
                                                    Text(queryText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, lineHeight = 15.sp)
                                                    Text("Mapped Category: $categoryName", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                
                                                Card(
                                                    shape = RoundedCornerShape(4.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isCategoryBlocked) AlertRed.copy(alpha = 0.1f) else OkGreen.copy(alpha = 0.1f)
                                                    ),
                                                    modifier = Modifier.wrapContentSize()
                                                ) {
                                                    Text(
                                                        text = if (isCategoryBlocked) "BLOCKED" else "SAFE",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isCategoryBlocked) AlertRed else OkGreen,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Display Search Results Window
                                    searchedResultText?.let { result ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (searchedResultBlocked) AlertRed.copy(alpha = 0.08f) else OkGreen.copy(alpha = 0.08f)
                                            ),
                                            border = BorderStroke(1.5.dp, if (searchedResultBlocked) AlertRed else OkGreen)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        Icon(
                                                            imageVector = if (searchedResultBlocked) Icons.Default.Warning else Icons.Default.CheckCircle,
                                                            contentDescription = null,
                                                            tint = if (searchedResultBlocked) AlertRed else OkGreen,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Text(
                                                            text = if (searchedResultBlocked) "INTERCEPTED DIRECTORY RULE" else "SAFE MATCH CARRIED",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (searchedResultBlocked) AlertRed else OkGreen
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = { searchedResultText = null },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                                
                                                Text(
                                                    text = result,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Educational section explaining how we bypass sandboxing
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Info, contentDescription = "Explainer", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text("Algerian Market Compliance Info", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            text = "This companion uses an Android AccessibilityService to detect foregound running packages. Locally on this device, it references our SQLite database rules. If the launched package package name is marked as blocked in Rule Settings, it fires a SYSTEM_ALERT_WINDOW to trap the background interface until unlocked.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // PIN Override bypass dialog
            if (showBypassDialog) {
                AlertDialog(
                    onDismissRequest = { showBypassDialog = false },
                    title = { Text("Parent Override Verification") },
                    text = {
                        Column {
                            Text("Enter your 4-Digit Parent Security PIN to toggle back to Admin Controller mode.")
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = enteredPin,
                                onValueChange = { enteredPin = it },
                                label = { Text("Parent PIN (Pass: 1234)") },
                                isError = pinError,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            if (pinError) {
                                Text("Invalid bypass PIN. Try 1234.", color = AlertRed, fontSize = 11.sp)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (enteredPin == "1234" || enteredPin == "0000") {
                                    viewModel.setAppMode(AppMode.PARENT)
                                    showBypassDialog = false
                                    enteredPin = ""
                                    pinError = false
                                } else {
                                    pinError = true
                                }
                            }
                        ) {
                            Text("Verify & Exit")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showBypassDialog = false; enteredPin = ""; pinError = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Simulated Lock Screen Overlay
            if (showAppOpenedContent == "LOCKED") {
                AlertDialog(
                    onDismissRequest = { showAppOpenedContent = null },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Access Locked",
                            tint = AlertRed,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = {
                        Text(
                            text = activeLockTitle ?: "TIME LIMIT EXCEEDED",
                            color = AlertRed,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "We intercepted an attempt to open ${simulatedAppOpenedName ?: "App"}.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = activeLockReason ?: "Access is restricted under active parental policies.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showAppOpenedContent = null },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                        ) {
                            Text("Return to Companion Suite", color = Color.White)
                        }
                    }
                )
            }

            // Simulated Successful App Open
            if (showAppOpenedContent == "SUCCESS") {
                AlertDialog(
                    onDismissRequest = { showAppOpenedContent = null },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "App Running",
                            tint = OkGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "SIMULATED RUNNING: ${simulatedAppOpenedName ?: "App"}",
                            color = OkGreen,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Package: ${simulatedAppOpenedPackage ?: "com.app"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "This application launch fully conforms to active parental policies. No limits or bedtime rules were violated.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showAppOpenedContent = null },
                            colors = ButtonDefaults.buttonColors(containerColor = OkGreen)
                        ) {
                            Text("Exit App Simulation", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
        border = BorderStroke(
            width = 1.dp,
            color = if (isEnabled) OkGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isEnabled) OkGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isEnabled) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                    contentDescription = "Status symbol",
                    tint = if (isEnabled) OkGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 14.sp)
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = OkGreen,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
