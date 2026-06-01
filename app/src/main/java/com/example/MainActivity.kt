package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ParentalViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val language by viewModel.appLanguage.collectAsStateWithLifecycle()
            val layoutDirection = if (language.isRtl) androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr

            MyApplicationTheme(darkTheme = isDarkTheme) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val mode by viewModel.appMode.collectAsStateWithLifecycle()
                        
                        when (mode) {
                            AppMode.ROLE_SELECTION -> {
                                RoleSelectionScreen(viewModel = viewModel)
                            }
                            AppMode.PARENT_AUTH -> {
                                ParentAuthScreen(viewModel = viewModel)
                            }
                            AppMode.PARENT -> {
                                ParentalDashboardScreen(viewModel = viewModel)
                            }
                            AppMode.CHILD -> {
                                ChildCompanionDashboard(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
