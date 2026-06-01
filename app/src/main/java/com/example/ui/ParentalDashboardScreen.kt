package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentalDashboardScreen(
    viewModel: ParentalViewModel,
    modifier: Modifier = Modifier
) {
    val kids by viewModel.children.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedChildId.collectAsStateWithLifecycle()
    val activeChild by viewModel.selectedChild.collectAsStateWithLifecycle()
    
    val timeConfig by viewModel.screenTimeConfig.collectAsStateWithLifecycle()
    val appRules by viewModel.appRules.collectAsStateWithLifecycle()
    val webFilters by viewModel.categoryFilters.collectAsStateWithLifecycle()
    val logs by viewModel.activityLogs.collectAsStateWithLifecycle()
    val activeLocation by viewModel.childLocation.collectAsStateWithLifecycle()
    val coachMessages by viewModel.coachMessages.collectAsStateWithLifecycle()
    
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val coachLoading by viewModel.coachLoading.collectAsStateWithLifecycle()

    val pairCode by viewModel.generatedPairCode.collectAsStateWithLifecycle()
    val pairingStatus by viewModel.connectionStatus.collectAsStateWithLifecycle()
    var showPairingDialog by remember { mutableStateOf(false) }

    var activeTab by remember { mutableStateOf(0) } // 0: Overview, 1: Rules, 2: Location, 3: AI Safety Coach
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = viewModel.trans("app_title"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = viewModel.trans("app_subtitle"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Preferences Selector: Theme & Language Toggle Menu
                    AppPreferencesMenu(
                        viewModel = viewModel,
                        iconColor = MaterialTheme.colorScheme.primary
                    )

                    // Switch Role Device Selector Button for Simulator
                    IconButton(
                        onClick = { viewModel.setAppMode(AppMode.ROLE_SELECTION) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Roles",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Add / Pair Device Dialog Button via existing outer pairing components
                    IconButton(
                        onClick = { 
                            viewModel.startPairingFlow()
                            showPairingDialog = true 
                        },
                        modifier = Modifier.testTag("pair_device_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Pair Child Companion App",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Panic Pause Button
                    var showPanicDialog by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { showPanicDialog = true },
                        modifier = Modifier.testTag("panic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Emergency Pause Screen Locker",
                            tint = AlertRed
                        )
                    }
                    
                    if (showPanicDialog) {
                        AlertDialog(
                            onDismissRequest = { showPanicDialog = false },
                            title = { Text("Emergency Remote Lockdown") },
                            text = { Text("Do you wish to instantly PAUSE ALL children's devices? This blocks screen access on all connected devices until manually unlocked.") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.pauseAllDevices(true)
                                        showPanicDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                                ) {
                                    Text("PAUSE ALL NOW", color = Color.White)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showPanicDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = { Icon(if (activeTab == 0) Icons.Default.Person else Icons.Default.Person, contentDescription = "Overview") },
                        label = { Text(viewModel.trans("overview"), fontSize = 11.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Rules") },
                        label = { Text(viewModel.trans("rules"), fontSize = 11.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") },
                        label = { Text(viewModel.trans("location"), fontSize = 11.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium) }
                    )
                    NavigationBarItem(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        icon = { Icon(Icons.Default.Shield, contentDescription = "AI Coach") },
                        label = { Text(viewModel.trans("ai_coach"), fontSize = 11.sp, fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Medium) }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (showPairingDialog) {
            var childName by remember { mutableStateOf("") }
            var childAgeText by remember { mutableStateOf("") }
            var selectedDevice by remember { mutableStateOf("Samsung Galaxy S23") }
            val deviceModels = listOf("Samsung Galaxy S23", "Google Pixel 8 Pro", "Xiaomi Redmi 12", "Xiaomi POCO X6 Pro")
            var selectedAvatarIndex by remember { mutableStateOf(0) }
            val avatars = listOf("👧", "👦", "🧒", "🎒", "🌟")
            
            AlertDialog(
                onDismissRequest = { 
                    viewModel.cancelPairing()
                    showPairingDialog = false 
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Pairing Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pair Child Companion App",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Download the Haris Companion App on the child's device, open it, and display this dynamic pairing code on screen:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = pairCode ?: "--- ---",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            strokeWidth = 1.5.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Awaiting Firebase RTDB connection...",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Companion App Simulator (Cloud Proxy):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = childName,
                                onValueChange = { childName = it },
                                label = { Text("Child's Name") },
                                placeholder = { Text("e.g. Yacine") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("pair_child_name_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = childAgeText,
                                onValueChange = { childAgeText = it },
                                label = { Text("Child's Age") },
                                placeholder = { Text("e.g. 10") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("pair_child_age_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            Text("Select Avatar Emoji:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                avatars.forEachIndexed { index, avatar ->
                                    val selected = index == selectedAvatarIndex
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (selected) MaterialTheme.colorScheme.primaryContainer 
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            )
                                            .clickable { selectedAvatarIndex = index }
                                            .border(
                                                width = if (selected) 2.dp else 0.dp,
                                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(avatar, fontSize = 20.sp)
                                    }
                                }
                            }
                        }

                        item {
                            Text("Select Child Device Brand:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(deviceModels) { model ->
                                    val selected = model == selectedDevice
                                    FilterChip(
                                        selected = selected,
                                        onClick = { selectedDevice = model },
                                        label = { Text(model, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val age = childAgeText.toIntOrNull() ?: 10
                            val name = if (childName.isNotBlank()) childName else "Yacine"
                            viewModel.simulateChildAppConnection(
                                name = name,
                                age = age,
                                device = selectedDevice,
                                avatar = avatars[selectedAvatarIndex]
                              )
                              showPairingDialog = false
                        },
                        enabled = childName.isNotBlank() && childAgeText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Simulate Companion Setup", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            viewModel.cancelPairing()
                            showPairingDialog = false 
                        }
                    ) {
                        Text("Cancel Setting Up")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Child Selector Bar
            if (kids.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    items(kids) { child ->
                        val isSelected = child.id == selectedId
                        OutlinedCard(
                            onClick = { viewModel.selectChild(child.id) },
                            modifier = Modifier
                                .width(120.dp)
                                .testTag("child_selector_${child.name.lowercase()}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(child.avatarEmoji, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = child.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${child.age} yrs",
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                      )
                                  }
                              }
                          }
                      }
                      
                      item {
                          // A stylish border card for pairing a new device!
                          OutlinedCard(
                              onClick = { 
                                  viewModel.startPairingFlow()
                                  showPairingDialog = true 
                              },
                              modifier = Modifier
                                  .width(135.dp)
                                  .testTag("pair_new_device_card"),
                              shape = RoundedCornerShape(16.dp),
                              border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                              colors = CardDefaults.outlinedCardColors(
                                  containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                              )
                          ) {
                              Row(
                                  modifier = Modifier
                                      .padding(8.dp)
                                      .fillMaxWidth()
                                      .height(36.dp),
                                  verticalAlignment = Alignment.CenterVertically
                              ) {
                                  Box(
                                      modifier = Modifier
                                          .size(28.dp)
                                          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                                      contentAlignment = Alignment.Center
                                  ) {
                                      Icon(
                                          imageVector = Icons.Default.Add,
                                          contentDescription = "Pair Device",
                                          tint = MaterialTheme.colorScheme.primary,
                                          modifier = Modifier.size(16.dp)
                                      )
                                  }
                                  Spacer(modifier = Modifier.width(6.dp))
                                  Column {
                                      Text(
                                          text = "+ Pair Phone",
                                          fontWeight = FontWeight.Bold,
                                          fontSize = 11.sp,
                                          color = MaterialTheme.colorScheme.primary
                                      )
                                      Text(
                                          text = "Add Companion App",
                                          fontSize = 8.sp,
                                          color = MaterialTheme.colorScheme.onSurfaceVariant,
                                          maxLines = 1,
                                          overflow = TextOverflow.Ellipsis
                                      )
                                  }
                              }
                          }
                      }
                  }
              }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

            if (activeChild == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val currentChild = activeChild!!
                
                // Active Child Content Window
                Box(modifier = Modifier.weight(1f)) {
                    when (activeTab) {
                        0 -> OverviewTab(
                            child = currentChild,
                            timeConfig = timeConfig,
                            logs = logs,
                            activeLocation = activeLocation,
                            viewModel = viewModel,
                            onNavigateToRules = { activeTab = 1 },
                            onNavigateToLocation = { activeTab = 2 }
                        )
                        1 -> RulesTab(
                            child = currentChild,
                            timeConfig = timeConfig,
                            appRules = appRules,
                            webRules = webFilters,
                            viewModel = viewModel
                        )
                        2 -> LocationTab(
                            child = currentChild,
                            location = activeLocation,
                            viewModel = viewModel
                        )
                        3 -> SafetyCoachTab(
                            child = currentChild,
                            logs = logs,
                            messages = coachMessages,
                            isAnalyzing = isAnalyzing,
                            isLoadingResponse = coachLoading,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// OVERVIEW TAB
// ==========================================
@Composable
fun OverviewTab(
    child: Child,
    timeConfig: ScreenTimeConfig?,
    logs: List<ActivityLog>,
    activeLocation: ChildLocation?,
    viewModel: ParentalViewModel,
    onNavigateToRules: () -> Unit,
    onNavigateToLocation: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // High Density child device details header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Initials Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(LavenderContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = child.name.take(2).uppercase(),
                            color = OnLavenderContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (child.deviceModel.lowercase().contains("phone") || child.deviceModel.lowercase().contains("ipad")) {
                                "${child.name}'s ${child.deviceModel}"
                            } else {
                                "${child.name}'s Device"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 18.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(if (child.isOnline) SafeGreen else Color.Gray, CircleShape)
                            )
                            Text(
                                text = if (child.isOnline) "Online • Active now" else "Offline • Rest mode",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Fine-lined Quick notification actions
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { /* Soft feedback action */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onNavigateToRules() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Device Settings",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }



        // Split Screen Time Progress Outlined Card (from the Tailwind HTML pattern)
        item {
            val used = timeConfig?.usedMinutes ?: 0
            val limit = timeConfig?.dailyLimitMinutes ?: 120
            val ratio = if (limit > 0) used.toFloat() / limit.toFloat() else 1f
            val ratioPercent = (ratio * 100).toInt().coerceIn(0, 100)
            
            val hrsUsed = used / 60
            val minsUsed = used % 60
            val usedLabel = if (hrsUsed > 0) "${hrsUsed}h ${minsUsed}m" else "${minsUsed}m"
                     val hrsLimit = limit / 60
            val minsLimit = limit % 60
            val limitLabel = if (hrsLimit > 0) "${hrsLimit}h limit" else "${minsLimit}m limit"

            val primaryColorVal = MaterialTheme.colorScheme.primary
            val outlineColorVal = MaterialTheme.colorScheme.outline
            val alertRedColorVal = AlertRed

            OutlinedCard(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, outlineColorVal),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left Text & horizontal mini bar
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Screen Time Today",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = usedLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = if (used >= limit) alertRedColorVal else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "/ $limitLabel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = primaryColorVal,
                                    modifier = Modifier.padding(bottom = 5.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            // Mini horizontal indicator block
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(outlineColorVal.copy(alpha = 0.2f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio.coerceIn(0f, 1f))
                                        .fillMaxHeight()
                                        .background(if (used >= limit) alertRedColorVal else primaryColorVal)
                                )
                            }
                        }

                        // Right circular canvas ring chart
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(76.dp)
                        ) {
                            Canvas(modifier = Modifier.size(68.dp)) {
                                drawArc(
                                    color = outlineColorVal.copy(alpha = 0.22f),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 6.dp.toPx())
                                )
                                drawArc(
                                    color = if (used >= limit) alertRedColorVal else primaryColorVal,
                                    startAngle = -90f,
                                    sweepAngle = (ratio * 360f).coerceIn(0f, 360f),
                                    useCenter = false,
                                    style = Stroke(width = 6.dp.toPx())
                                )
                            }
                            Text(
                                text = "$ratioPercent%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic Screen allowance incremental controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add or subtract daily limit:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = { viewModel.addExtraScreenTime(-15) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text("-15 Min", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { viewModel.addExtraScreenTime(15) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text("+15 Min", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Modern 2-column Grid of interactive lock action controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // LOCK / UNLOCK DEVICE
                val paused = child.isDevicePaused
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (paused) PinkContainer else LavenderContainer)
                        .clickable { viewModel.toggleDevicePause() }
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (paused) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (paused) OnPinkContainer else OnLavenderContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (paused) "Unlock Device" else "Lock Device",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (paused) OnPinkContainer else OnLavenderContainer
                        )
                        Text(
                            text = if (paused) "Device is locked" else "Instant lockout",
                            fontSize = 10.sp,
                            color = if (paused) OnPinkContainer.copy(alpha = 0.7f) else OnLavenderContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                // EMERGENCY REMOTE PAUSE / LOCKDOWN
                var showPanicDialog by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PinkContainer)
                        .clickable { showPanicDialog = true }
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = OnPinkContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Pause All Devices",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = OnPinkContainer
                        )
                        Text(
                            text = "Suspend all activity",
                            fontSize = 10.sp,
                            color = OnPinkContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                if (showPanicDialog) {
                    AlertDialog(
                        onDismissRequest = { showPanicDialog = false },
                        title = { Text("Emergency Family Lockdown") },
                        text = { Text("Do you wish to instantly PAUSE ALL children's devices? This blocks screen access on all connected devices until manually unlocked.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.pauseAllDevices(true)
                                    showPanicDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                            ) {
                                Text("PAUSE ALL NOW", color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showPanicDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }

        // Quick Telemetry and Geolocation info
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Device battery status
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Battery5Bar,
                                contentDescription = "Battery",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Battery Status", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("${child.batteryPercent}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = if (child.isOnline) "Device Active" else "Device Offline",
                            fontSize = 10.sp,
                            color = if (child.isOnline) SafeGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Bedtime Tracker
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Bedtime schedule",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Quiet Hours", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val hr = timeConfig?.bedtimeStartHour ?: 21
                        val min = timeConfig?.bedtimeStartMinute ?: 30
                        val formattedTime = String.format("%02d:%02d", hr, min)
                        Text("$formattedTime PM", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Instant lock filters", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Last Location summary panel matching the HTML footer location banner
        item {
            activeLocation?.let { loc ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                        .clickable { onNavigateToLocation() }
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "LAST LOCATION SIGNAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = loc.addressName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Arrived recently • Battery ${child.batteryPercent}%",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Navigate to location detailed map",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Monitored Feeds & Alerts Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ALERTS & WEB FEED",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Configure Rules",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToRules() }
                )
            }
        }

        // Unified list card container mimicking the HTML overflow-hidden listing block
        if (logs.isEmpty()) {
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No proxy logs captured yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        logs.take(6).forEachIndexed { index, log ->
                            val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
                            val timeLabel = formatter.format(Date(log.timestamp))
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* Feed Row Tap */ }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Dynamic letter-box visual badges
                                val initialLetter = log.detailText.trimStart().firstOrNull()?.uppercaseChar()?.toString() ?: "A"
                                val (badgeBg, badgeText) = if (log.isBlocked) {
                                    Pair(Color(0xFFFF8A80).copy(alpha = 0.15f), Color(0xFFC62828))
                                } else {
                                    when (log.category) {
                                        "Gaming" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
                                        "Social" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
                                        "Education" -> Pair(Color(0xFFFFFDE7), Color(0xFFF57F17))
                                        else -> Pair(LavenderContainer, OnLavenderContainer)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(badgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initialLetter,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeText,
                                        fontSize = 15.sp
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = log.detailText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (log.isBlocked) AlertRed else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = log.category,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "•",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = timeLabel,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (log.isBlocked) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AlertRed)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("BLOCKED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            if (index < logs.take(6).size - 1) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// RULES & LIMITS CONFIGURATION
// ==========================================
@Composable
fun RulesTab(
    child: Child,
    timeConfig: ScreenTimeConfig?,
    appRules: List<AppRule>,
    webRules: List<CategoryFilter>,
    viewModel: ParentalViewModel
) {
    var configSubTab by remember { mutableStateOf(0) } // 0: Apps, 1: Web Categories, 2: Bedtime

    Column(modifier = Modifier.fillMaxSize()) {
        // Sub tabs selection with sharp bottom border resembling HTML design
        TabRow(
            selectedTabIndex = configSubTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(selected = configSubTab == 0, onClick = { configSubTab = 0 }) {
                Text("Apps Limit", modifier = Modifier.padding(14.dp), fontSize = 13.sp, fontWeight = if (configSubTab == 0) FontWeight.Bold else FontWeight.Medium)
            }
            Tab(selected = configSubTab == 1, onClick = { configSubTab = 1 }) {
                Text("Web Filtering", modifier = Modifier.padding(14.dp), fontSize = 13.sp, fontWeight = if (configSubTab == 1) FontWeight.Bold else FontWeight.Medium)
            }
            Tab(selected = configSubTab == 2, onClick = { configSubTab = 2 }) {
                Text("Schedule", modifier = Modifier.padding(14.dp), fontSize = 13.sp, fontWeight = if (configSubTab == 2) FontWeight.Bold else FontWeight.Medium)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (configSubTab) {
                0 -> { // APPS RULES
                    item {
                        OutlinedCard(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Toggle the switches below to completely block specific apps, or select deep-limit timers to restrict daily consumption for ${child.name}.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (appRules.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No monitored apps defined for this device.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(appRules) { rule ->
                            OutlinedCard(
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(rule.appName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(rule.packageName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = if (rule.isBlocked) "Blocked" else "Allowed",
                                                color = if (rule.isBlocked) AlertRed else SafeGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Switch(
                                                checked = !rule.isBlocked,
                                                onCheckedChange = { isAllowed ->
                                                    viewModel.toggleAppBlocked(rule.appName, !isAllowed)
                                                },
                                                modifier = Modifier.testTag("app_switch_${rule.appName.lowercase()}")
                                            )
                                        }
                                    }

                                    if (!rule.isBlocked) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Daily limit allowed:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            
                                            // Select limits
                                            var expanded by remember { mutableStateOf(false) }
                                            val minutesOptions = listOf(-1, 15, 30, 45, 60, 120)
                                            val currentLimitLabel = if (rule.limitMinutes < 0) "Unlimited" else "${rule.limitMinutes} min"

                                            Box {
                                                OutlinedButton(
                                                    onClick = { expanded = true },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                                ) {
                                                    Text(currentLimitLabel, fontSize = 12.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }

                                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                                    minutesOptions.forEach { mins ->
                                                        val label = if (mins < 0) "Unlimited" else "$mins minutes"
                                                        DropdownMenuItem(
                                                            text = { Text(label) },
                                                            onClick = {
                                                                viewModel.updateAppTimeLimit(rule.appName, mins)
                                                                expanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> { // WEB CATEGORIES FILTERS
                    item {
                        OutlinedCard(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Our active proxy blocks child search attempts and web browser visits matching blocked categories below instantly.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(webRules) { filter ->
                        OutlinedCard(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(filter.categoryName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = if (filter.isBlocked) "Blocked from child devices" else "Uncensored access enabled",
                                        fontSize = 11.sp,
                                        color = if (filter.isBlocked) AlertRed else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Switch(
                                    checked = filter.isBlocked,
                                    onCheckedChange = { isBlocked ->
                                        viewModel.toggleCategoryFilter(filter.categoryName, isBlocked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AlertRed,
                                        checkedTrackColor = AlertRed.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("filter_switch_${filter.categoryName.replace(" ", "_").lowercase()}")
                                )
                            }
                        }
                    }
                }
                2 -> { // SCHEDULED HOURS
                    item {
                        OutlinedCard(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Quiet Hours / Bedtime Routine", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "When Bedtime arrives, the child's screens lock immediately to encourage healthy rest cycles. Bedtime starts automatically at 9:30 PM and blocks access until 7:00 AM.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Daily Bedtime Lockout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Locks out screens", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("9:30 PM - 7:00 AM", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// LOCATION TRACKING SIGNALS SCREEN
// ==========================================
@Composable
fun LocationTab(
    child: Child,
    location: ChildLocation?,
    viewModel: ParentalViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location Stats Card
        OutlinedCard(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Current Coordinates", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = location?.addressName ?: "Identifying Signal...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (location != null) {
                            Text(
                                text = String.format("Lat: %.5f, Lon: %.5f", location.latitude, location.longitude),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Button(
                    onClick = { viewModel.simulateLocationMovement() },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("simulate_location_button")
                ) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Walk", fontSize = 12.sp)
                }
            }
        }

        // Custom stylized canvas map representation (No heavy Google Play APIs required!)
        OutlinedCard(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val pathColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                val primaryColor = MaterialTheme.colorScheme.primary
                val amberColor = AccentAmber

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Drawing abstract street shapes / lines representing a modern tactical map layout
                    // Grid lines
                    for (x in 0..w.toInt() step 120) {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.25f),
                            start = Offset(x.toFloat(), 0f),
                            end = Offset(x.toFloat(), h)
                        )
                    }
                    for (y in 0..h.toInt() step 120) {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.25f),
                            start = Offset(0f, y.toFloat()),
                            end = Offset(w, y.toFloat())
                        )
                    }

                    // Main road vectors
                    drawLine(color = pathColor, start = Offset(w * 0.1f, h * 0.2f), end = Offset(w * 0.9f, h * 0.2f), strokeWidth = 14f)
                    drawLine(color = pathColor, start = Offset(w * 0.5f, h * 0.1f), end = Offset(w * 0.5f, h * 0.9f), strokeWidth = 16f)
                    drawLine(color = pathColor, start = Offset(w * 0.2f, h * 0.8f), end = Offset(w * 0.8f, h * 0.8f), strokeWidth = 14f)
                    drawLine(color = pathColor, start = Offset(w * 0.1f, h * 0.5f), end = Offset(w * 0.9f, h * 0.5f), strokeWidth = 12f)
                    
                    // Geofence Safe Zones (School & Home Circles)
                    // School Center
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.08f),
                        radius = 120f,
                        center = Offset(w * 0.35f, h * 0.25f)
                    )
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.5f),
                        radius = 6f,
                        center = Offset(w * 0.35f, h * 0.25f)
                    )

                    // Home Center
                    drawCircle(
                        color = SafeGreen.copy(alpha = 0.08f),
                        radius = 140f,
                        center = Offset(w * 0.7f, h * 0.7f)
                    )
                    drawCircle(
                        color = SafeGreen.copy(alpha = 0.5f),
                        radius = 6f,
                        center = Offset(w * 0.7f, h * 0.7f)
                    )
                }

                // Place Geofence Labels on map overlay
                Box(modifier = Modifier
                    .offset(x = 30.dp, y = 50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("School Zone", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Box(modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-40).dp, y = (-85).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9)) // light green
                    .padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("Home Safety Zone", color = SafeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Child Pulsing Pin (dynamically mapped near school, center, or home depending on address)
                if (location != null) {
                    val relativeOffset = when (location.addressName) {
                        "Lincoln's Inn Fields School" -> Alignment.CenterStart
                        "British Museum Library" -> Alignment.TopCenter
                        "Leicester Square Metro" -> Alignment.Center
                        "National Gallery Gym" -> Alignment.BottomCenter
                        "Home (Charing Cross Road)" -> Alignment.BottomEnd
                        else -> Alignment.Center
                    }

                    // Pulse effect
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseRadius by infiniteTransition.animateFloat(
                        initialValue = 18f,
                        targetValue = 44f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "radius"
                    )
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "alpha"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 60.dp, vertical = 80.dp),
                        contentAlignment = relativeOffset
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Pulsing Ring
                            Box(
                                modifier = Modifier
                                    .size(pulseRadius.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha),
                                        CircleShape
                                    )
                            )

                            // Main Pin Button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(child.avatarEmoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// AI SAFETY COACH TERMINAL WITH GEMINI
// ==========================================
@Composable
fun SafetyCoachTab(
    child: Child,
    logs: List<ActivityLog>,
    messages: List<CoachMessage>,
    isAnalyzing: Boolean,
    isLoadingResponse: Boolean,
    viewModel: ParentalViewModel
) {
    val listState = rememberLazyListState()
    var textInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Auto-scroll on new chats
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Upper Quick Diagnostic Panel
        OutlinedCard(
            modifier = Modifier.padding(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${child.name}'s Safety Audit", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Scans histories for red flags instantly", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { viewModel.analyzeLogsWithGemini() },
                        enabled = !isAnalyzing && !isLoadingResponse,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("gemini_analyze_button")
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isAnalyzing) "Analyzing..." else "Analyze Logs", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Status Rationale Badges based on logs
                val hasRedFlags = logs.any { it.isBlocked && it.category == "Adult Content" }
                val hasMediumFlags = logs.any { it.detailText.contains("sad") || it.detailText.contains("lonely") }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Risk Level:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    val (lbl, color) = when {
                        hasRedFlags -> Pair("HIGH RISK - Requires attention", AlertRed)
                        hasMediumFlags -> Pair("MEDIUM RISK - Emotional flag", AccentAmber)
                        else -> Pair("LOW RISK - Wholesome", SafeGreen)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.12f))
                            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(lbl, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Live Chat Messages Logger list
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
            
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(messages) { msg ->
                    val isAssistant = msg.role == "model"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isAssistant) Arrangement.Start else Arrangement.End
                    ) {
                        OutlinedCard(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isAssistant) 4.dp else 16.dp,
                                bottomEnd = if (isAssistant) 16.dp else 4.dp
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isAssistant) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isAssistant) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.widthIn(max = 290.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isAssistant) "🤖 Family Coach" else "👤 You",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAssistant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = msg.content,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = if (isAssistant) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                if (isLoadingResponse) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.widthIn(max = 240.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Text("Gemini Safety Coach typing...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips Carousel
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val suggestions = listOf(
                "Is Emma isolated?",
                "Draft clean Screen Time rules",
                "Explain how Roblox timers work",
                "How to talk about sadness searches?"
            )
            items(suggestions) { query ->
                OutlinedCard(
                    onClick = { viewModel.sendCoachQuestion(query) },
                    shape = CircleShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.testTag("ai_suggestion_chip")
                ) {
                    Text(
                        text = query,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Chat Input Bar with a flat bottom border styling
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Clear chat history
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Conversation", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Ask active safety questions...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        maxLines = 2,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (textInput.isNotBlank()) {
                                    viewModel.sendCoachQuestion(textInput)
                                    textInput = ""
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (textInput.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable(enabled = textInput.isNotBlank()) {
                                viewModel.sendCoachQuestion(textInput)
                                textInput = ""
                                focusManager.clearFocus()
                            }
                            .testTag("chat_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (textInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

}
