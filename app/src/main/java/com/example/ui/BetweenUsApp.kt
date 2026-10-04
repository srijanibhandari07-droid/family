package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FamilyCheckInDialog
import com.example.ui.components.SafetyDialog
import com.example.ui.screens.ArgumentReplayScreen
import com.example.ui.screens.ConstellationScreen
import com.example.ui.screens.HeatMeterScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MicroActionScreen
import com.example.ui.screens.TwoWorldScreen
import com.example.ui.theme.HeatHeated
import com.example.ui.theme.IndigoPrimary

enum class AppDestination(val route: String, val title: String) {
    HOME("home", "Home"),
    HEAT_METER("heat_meter", "Heat Meter"),
    TWO_WORLD("two_world", "Two Worlds"),
    CONSTELLATION("constellation", "Work Map"),
    REPLAY("replay", "Replay"),
    MICRO_ACTIONS("micro_actions", "Repairs")
}

@Composable
fun BetweenUsApp(
    viewModel: BetweenUsViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var showCheckInDialog by remember { mutableStateOf(false) }

    val safetyAlert by viewModel.safetyAlert.collectAsStateWithLifecycle()

    if (safetyAlert != null) {
        SafetyDialog(
            guidance = safetyAlert!!,
            onDismiss = { viewModel.clearSafetyAlert() }
        )
    }

    if (showCheckInDialog) {
        FamilyCheckInDialog(
            onDismiss = { showCheckInDialog = false },
            onSubmit = { category, intensity, note ->
                viewModel.submitCheckIn(category, intensity, note)
            }
        )
    }

    // Hardware back press handler when on non-home screen
    if (currentDestination != AppDestination.HOME) {
        BackHandler {
            currentDestination = AppDestination.HOME
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                // Home
                NavigationBarItem(
                    selected = currentDestination == AppDestination.HOME,
                    onClick = { currentDestination = AppDestination.HOME },
                    icon = {
                        Icon(
                            if (currentDestination == AppDestination.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_home")
                )

                // Heat Meter (Prominent)
                NavigationBarItem(
                    selected = currentDestination == AppDestination.HEAT_METER,
                    onClick = { currentDestination = AppDestination.HEAT_METER },
                    icon = {
                        Icon(
                            if (currentDestination == AppDestination.HEAT_METER) Icons.Filled.LocalFireDepartment else Icons.Outlined.LocalFireDepartment,
                            contentDescription = "Heat Meter",
                            tint = if (currentDestination == AppDestination.HEAT_METER) HeatHeated else androidx.compose.ui.graphics.Color.Unspecified
                        )
                    },
                    label = { Text("Heated", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_heat_meter")
                )

                // Two World
                NavigationBarItem(
                    selected = currentDestination == AppDestination.TWO_WORLD,
                    onClick = { currentDestination = AppDestination.TWO_WORLD },
                    icon = {
                        Icon(
                            if (currentDestination == AppDestination.TWO_WORLD) Icons.Filled.CompareArrows else Icons.Outlined.CompareArrows,
                            contentDescription = "Two-World View"
                        )
                    },
                    label = { Text("Worlds", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_two_world")
                )

                // Invisible Work Map (Constellation)
                NavigationBarItem(
                    selected = currentDestination == AppDestination.CONSTELLATION,
                    onClick = { currentDestination = AppDestination.CONSTELLATION },
                    icon = {
                        Icon(
                            if (currentDestination == AppDestination.CONSTELLATION) Icons.Filled.Hub else Icons.Outlined.Hub,
                            contentDescription = "Invisible Work Map"
                        )
                    },
                    label = { Text("Map", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_constellation")
                )

                // Replay
                NavigationBarItem(
                    selected = currentDestination == AppDestination.REPLAY,
                    onClick = { currentDestination = AppDestination.REPLAY },
                    icon = {
                        Icon(
                            if (currentDestination == AppDestination.REPLAY) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "Argument Replay"
                        )
                    },
                    label = { Text("Replay", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_replay")
                )

                // Micro Actions
                NavigationBarItem(
                    selected = currentDestination == AppDestination.MICRO_ACTIONS,
                    onClick = { currentDestination = AppDestination.MICRO_ACTIONS },
                    icon = {
                        Icon(
                            if (currentDestination == AppDestination.MICRO_ACTIONS) Icons.Filled.SelfImprovement else Icons.Outlined.SelfImprovement,
                            contentDescription = "Repairs & Actions"
                        )
                    },
                    label = { Text("Repairs", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_repairs")
                )
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestination.HOME -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToHeatMeter = { currentDestination = AppDestination.HEAT_METER },
                    onNavigateToTwoWorld = { currentDestination = AppDestination.TWO_WORLD },
                    onNavigateToConstellation = { currentDestination = AppDestination.CONSTELLATION },
                    onNavigateToReplay = { currentDestination = AppDestination.REPLAY },
                    onNavigateToMicroActions = { currentDestination = AppDestination.MICRO_ACTIONS },
                    onOpenCheckInDialog = { showCheckInDialog = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.HEAT_METER -> {
                HeatMeterScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentDestination = AppDestination.HOME },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.TWO_WORLD -> {
                TwoWorldScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentDestination = AppDestination.HOME },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.CONSTELLATION -> {
                ConstellationScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentDestination = AppDestination.HOME },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.REPLAY -> {
                ArgumentReplayScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentDestination = AppDestination.HOME },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.MICRO_ACTIONS -> {
                MicroActionScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentDestination = AppDestination.HOME },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
