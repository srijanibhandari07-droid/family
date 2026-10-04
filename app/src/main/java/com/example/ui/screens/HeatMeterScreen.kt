package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BetweenUsViewModel
import com.example.ui.components.BreathingPauseDialog
import com.example.ui.components.HeatGauge
import com.example.ui.components.MindReadingAlertCard
import com.example.ui.theme.HeatCalm
import com.example.ui.theme.HeatHeated
import com.example.ui.theme.HeatPause
import com.example.ui.theme.HeatTense
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletBridge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatMeterScreen(
    viewModel: BetweenUsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heatState by viewModel.heatMeterState.collectAsStateWithLifecycle()
    var selectedActionTab by remember { mutableStateOf<String?>(null) } // "UNDERSTAND" or "RESPOND"

    if (heatState.isPauseActive) {
        BreathingPauseDialog(onDismiss = { viewModel.setPauseActive(false) })
    }

    Scaffold(
        modifier = modifier.testTag("heat_meter_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Heat Meter",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("heat_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (heatState.analysis != null) {
                        IconButton(
                            onClick = {
                                viewModel.resetHeatMeter()
                                selectedActionTab = null
                            },
                            modifier = Modifier.testTag("heat_reset_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Form")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (heatState.analysis == null) {
                // 3 Short Questions Input Flow
                Text(
                    text = "BEFORE YOU RESPOND",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.3.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = HeatHeated
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Name what is happening to clear the friction.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Question 1: What happened?
                OutlinedTextField(
                    value = heatState.whatHappened,
                    onValueChange = {
                        viewModel.updateHeatInputs(
                            whatHappened = it,
                            whatFeeling = heatState.whatFeeling,
                            whatWant = heatState.whatWant
                        )
                    },
                    label = { Text("1. What happened? (Visible words or trigger)") },
                    placeholder = { Text("e.g. Mom said: 'You only know how to sit with your phone, you don't care.'") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_what_happened"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Question 2: What am I feeling?
                OutlinedTextField(
                    value = heatState.whatFeeling,
                    onValueChange = {
                        viewModel.updateHeatInputs(
                            whatHappened = heatState.whatHappened,
                            whatFeeling = it,
                            whatWant = heatState.whatWant
                        )
                    },
                    label = { Text("2. What am I feeling right now?") },
                    placeholder = { Text("e.g. Defensive, exhausted from college labs, furious, misunderstood.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_what_feeling"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Question 3: What do I want right now?
                OutlinedTextField(
                    value = heatState.whatWant,
                    onValueChange = {
                        viewModel.updateHeatInputs(
                            whatHappened = heatState.whatHappened,
                            whatFeeling = heatState.whatFeeling,
                            whatWant = it
                        )
                    },
                    label = { Text("3. What do I want right now?") },
                    placeholder = { Text("e.g. I want them to acknowledge my effort, and leave me alone for an hour.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_what_want"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.calculateHeatMeter() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_heat_meter"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeatHeated),
                    enabled = !heatState.isAnalyzing
                ) {
                    if (heatState.isAnalyzing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deconstructing friction...")
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calculate Heat & Deconstruct",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick prefill helper for realistic Barasat PG argument
                OutlinedButton(
                    onClick = {
                        viewModel.updateHeatInputs(
                            whatHappened = "Mother said: 'You only know how to sit with your phone. You don't care about this family.'",
                            whatFeeling = "Angry and guilty. I've been grinding code at Brainware and living alone in PG.",
                            whatWant = "I want them to see I am trying, and stop assuming I'm lazy."
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("heat_prefill_button")
                ) {
                    Text("Prefill Example: Barasat PG Weekend Argument", fontSize = 12.sp)
                }

            } else {
                // Analysis Result Presentation
                val analysis = heatState.analysis!!

                HeatGauge(
                    score = analysis.heatScore,
                    status = analysis.status
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = IndigoPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "“${analysis.insightMessage}”",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mind reading warning if detected
                if (analysis.mindReadingWarning != null) {
                    MindReadingAlertCard(notice = analysis.mindReadingWarning)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 3 Core Post-Heat Options: [PAUSE] [UNDERSTAND] [RESPOND]
                Text(
                    text = "NEXT COMPOSURE PATH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.setPauseActive(true) },
                        modifier = Modifier.weight(1f).testTag("action_pause_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HeatPause)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PAUSE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { selectedActionTab = "UNDERSTAND" },
                        modifier = Modifier.weight(1f).testTag("action_understand_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedActionTab == "UNDERSTAND") VioletBridge else VioletBridge.copy(alpha = 0.75f)
                        )
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("UNDERSTAND", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { selectedActionTab = "RESPOND" },
                        modifier = Modifier.weight(1f).testTag("action_respond_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedActionTab == "RESPOND") IndigoPrimary else IndigoPrimary.copy(alpha = 0.75f)
                        )
                    ) {
                        Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RESPOND", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Detail Box for UNDERSTAND
                AnimatedVisibility(visible = selectedActionTab == "UNDERSTAND" || selectedActionTab == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VioletBridge.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "WORDS vs INVISIBLE PRESSURE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = VioletBridge
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            DeconstructionRow(label = "Visible Words:", content = analysis.visibleWords, labelColor = HeatHeated)
                            Spacer(modifier = Modifier.height(10.dp))
                            DeconstructionRow(label = "Possible Pressure (Their Side):", content = analysis.possiblePressure, labelColor = HeatTense)
                            Spacer(modifier = Modifier.height(10.dp))
                            DeconstructionRow(label = "Possible Need (Their Side):", content = analysis.possibleNeed, labelColor = HeatCalm)
                            Spacer(modifier = Modifier.height(10.dp))
                            DeconstructionRow(label = "Student Side (My Reality):", content = analysis.myEffortFeeling, labelColor = IndigoPrimary)
                            Spacer(modifier = Modifier.height(10.dp))
                            DeconstructionRow(label = "Student Need:", content = analysis.studentNeed, labelColor = VioletBridge)

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Note: This is one possible interpretation. Only the person can confirm what they actually mean.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Detail Box for RESPOND
                AnimatedVisibility(visible = selectedActionTab == "RESPOND") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = IndigoPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GROUNDED CALM RESPONSE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.1.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = IndigoPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Preserves their underlying concern while maintaining your boundaries and dignity:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(IndigoPrimary.copy(alpha = 0.1f))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = "“${analysis.suggestedCalmResponse}”",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Why this works: It acknowledges the valid concern without agreeing that you are careless, and gives immediate proof of responsibility.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DeconstructionRow(
    label: String,
    content: String,
    labelColor: Color
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = labelColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}
