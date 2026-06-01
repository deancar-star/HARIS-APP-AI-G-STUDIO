package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAuthScreen(
    viewModel: ParentalViewModel,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var showGoogleChooser by remember { mutableStateOf(false) }
    var showFacebookSandbox by remember { mutableStateOf(false) }
    var googleRealError by remember { mutableStateOf<String?>(null) }
    
    val context = androidx.compose.ui.platform.LocalContext.current

    val authErrorKey by viewModel.parentAuthError.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessingAuth.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = viewModel.trans("parent_auth_title"), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.setAppMode(AppMode.ROLE_SELECTION) },
                        modifier = Modifier.testTag("auth_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to role selection"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .widthIn(max = 440.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shield Secure Brand Banner
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSignUpMode) Icons.Default.AppRegistration else Icons.Default.VpnKey,
                        contentDescription = "Authentication Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = if (isSignUpMode) viewModel.trans("signup_now") else viewModel.trans("login_now"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = viewModel.trans("parent_auth_subtitle"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Error Banner
                AnimatedVisibility(
                    visible = authErrorKey != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    authErrorKey?.let { err ->
                        val displayErr = if (err == "auth_error_fields" || err == "auth_error_mismatch" || err == "auth_error_invalid") {
                            viewModel.trans(err)
                        } else {
                            err
                        }
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Error, "Error icon", modifier = Modifier.size(18.dp))
                                Text(
                                    text = displayErr,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearAuthError() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, "Dismiss error", modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Email Field
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { 
                                emailInput = it
                                viewModel.clearAuthError()
                            },
                            label = { Text(viewModel.trans("enter_email")) },
                            leadingIcon = { Icon(Icons.Default.Email, "Email Address") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input")
                        )

                        // Password Field
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { 
                                passwordInput = it
                                viewModel.clearAuthError()
                            },
                            label = { Text(viewModel.trans("enter_password")) },
                            leadingIcon = { Icon(Icons.Default.Lock, "Password Access") },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (isSignUpMode) ImeAction.Next else ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (!isSignUpMode && !isProcessing) {
                                        viewModel.loginParent(emailInput, passwordInput, onSuccess = {})
                                    }
                                }
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input")
                        )

                        // Confirm Password (Only shown in Sign Up mode)
                        AnimatedVisibility(
                            visible = isSignUpMode,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            OutlinedTextField(
                                value = confirmPasswordInput,
                                onValueChange = { 
                                    confirmPasswordInput = it
                                    viewModel.clearAuthError()
                                },
                                label = { Text(viewModel.trans("confirm_password")) },
                                leadingIcon = { Icon(Icons.Default.LockClock, "Confirm input") },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (isSignUpMode && !isProcessing) {
                                            viewModel.signupParent(emailInput, passwordInput, confirmPasswordInput, onSuccess = {})
                                        }
                                    }
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_password_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (isSignUpMode) {
                                    viewModel.signupParent(emailInput, passwordInput, confirmPasswordInput, onSuccess = {})
                                } else {
                                    viewModel.loginParent(emailInput, passwordInput, onSuccess = {})
                                }
                            },
                            enabled = !isProcessing,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_submit_button")
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSignUpMode) Icons.Default.PersonAdd else Icons.Default.Login,
                                        contentDescription = "Submit action logo"
                                    )
                                    Text(
                                        text = if (isSignUpMode) viewModel.trans("signup_now") else viewModel.trans("login_now"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        // Or separator
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                            Text(
                                text = viewModel.trans("or_continue_with"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                fontSize = 11.sp
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                        }

                        // Social Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Google Button
                            OutlinedButton(
                                onClick = {
                                    viewModel.signInWithGoogleReal(
                                        context = context,
                                        onSuccess = {},
                                        onFailed = { errMsg, suggestFallback ->
                                            googleRealError = errMsg
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f).height(46.dp).testTag("google_auth_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    // Custom beautifully color-drawn Google dots using Canvas
                                    androidx.compose.foundation.Canvas(modifier = Modifier.size(12.dp)) {
                                        drawCircle(color = androidx.compose.ui.graphics.Color(0xFFEA4335), radius = size.width / 4.2f, center = androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.28f))
                                        drawCircle(color = androidx.compose.ui.graphics.Color(0xFF4285F4), radius = size.width / 4.2f, center = androidx.compose.ui.geometry.Offset(size.width * 0.72f, size.height * 0.28f))
                                        drawCircle(color = androidx.compose.ui.graphics.Color(0xFF34A853), radius = size.width / 4.2f, center = androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.72f))
                                        drawCircle(color = androidx.compose.ui.graphics.Color(0xFFFBBC05), radius = size.width / 4.2f, center = androidx.compose.ui.geometry.Offset(size.width * 0.72f, size.height * 0.72f))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = viewModel.trans("sign_in_google"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Facebook Button
                            Button(
                                onClick = { showFacebookSandbox = true },
                                modifier = Modifier.weight(1f).height(46.dp).testTag("facebook_auth_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = androidx.compose.ui.graphics.Color(0xFF1877F2),
                                    contentColor = androidx.compose.ui.graphics.Color.White
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "f",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = viewModel.trans("sign_in_facebook"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Switch Mode Selection Prompt
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isSignUpMode) viewModel.trans("already_have_account") else viewModel.trans("no_account_yet"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        TextButton(
                            onClick = {
                                isSignUpMode = !isSignUpMode
                                viewModel.clearAuthError()
                            },
                            modifier = Modifier.testTag("auth_toggle_mode")
                        ) {
                            Text(
                                text = if (isSignUpMode) viewModel.trans("login_now") else viewModel.trans("signup_now"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Firebase secure brand note
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Shield logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = viewModel.trans("secured_by_firebase"),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // --- DIALOGS FOR SOCIAL LOGINS ---
        if (showGoogleChooser) {
            AlertDialog(
                onDismissRequest = { showGoogleChooser = false },
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            text = viewModel.trans("choose_google_account"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = viewModel.trans("continue_to_app"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = {
                                showGoogleChooser = false
                                viewModel.authWithSocial("deancarolle@gmail.com", "Dean Carolle", onSuccess = {})
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth().testTag("google_sso_primary")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("D", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Column {
                                    Text("Dean Carolle", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text("deancarolle@gmail.com", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Surface(
                            onClick = {
                                showGoogleChooser = false
                                viewModel.authWithSocial("parent.safety@gmail.com", "Haris Parent", onSuccess = {})
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth().testTag("google_sso_secondary")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(18.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("H", color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Column {
                                    Text("Haris Parent (Demo)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text("parent.safety@gmail.com", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showGoogleChooser = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showFacebookSandbox) {
            AlertDialog(
                onDismissRequest = { showFacebookSandbox = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning logo",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = viewModel.trans("fb_sandbox_title"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = viewModel.trans("fb_sandbox_desc"),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "Sandbox Developer Account: admin-dev@facebook.com",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showFacebookSandbox = false
                            viewModel.authWithSocial("admin-dev@facebook.com", "Facebook Admin", onSuccess = {})
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFF1877F2),
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(viewModel.trans("facebook_simulate"), fontSize = 12.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFacebookSandbox = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (googleRealError != null) {
            AlertDialog(
                onDismissRequest = { googleRealError = null },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "Google Sign-In Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = googleRealError ?: "",
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "To test right now without manual Google Cloud configuration or in an emulator, you can bypass this using the back-up mock selector.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            googleRealError = null
                            showGoogleChooser = true // Bypasses the blocker
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Bypass with Mock Profiles", fontSize = 12.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { googleRealError = null }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}
