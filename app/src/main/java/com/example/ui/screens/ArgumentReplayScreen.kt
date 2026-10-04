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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.ai.ConflictDeconstruction
import com.example.data.model.FamilyRole
import com.example.ui.BetweenUsViewModel
import com.example.ui.components.EpistemologyPillRow
import com.example.ui.components.MindReadingAlertCard
import com.example.ui.theme.HeatCalm
import com.example.ui.theme.HeatHeated
import com.example.ui.theme.HeatTense
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletBridge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArgumentReplayScreen(
    viewModel: BetweenUsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val replayInput by viewModel.replayInputState.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val currentDeconstruction by viewModel.currentDeconstruction.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("argument_replay_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Argument Replay Studio",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("replay_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentDeconstruction()
                            scope.launch {
                                snackbarHostState.showSnackbar("Saved reflection & added micro-repair task!")
                            }
                        },
                        modifier = Modifier.testTag("save_replay_button")
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Save Reflection")
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
                .padding(16.dp)
        ) {
            // Principle Header
            EpistemologyPillRow()

            Spacer(modifier = Modifier.height(14.dp))

            // Action toolbar: Toggle manual input vs load preloaded demo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.loadPreloadedScenario()
                        showForm = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Loaded Barasat PG student demo scenario!")
                        }
                    },
                    modifier = Modifier.weight(1f).testTag("load_demo_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load Barasat Demo", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { showForm = !showForm },
                    modifier = Modifier.weight(1f).testTag("log_custom_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showForm) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (showForm) "Hide Entry Form" else "Log My Own Incident",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (showForm) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expandable Manual Entry Form
            AnimatedVisibility(visible = showForm) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "MANUAL ARGUMENT TIMELINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "No secret recording. Enter manually after everyone has stepped back.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = replayInput.scenarioTitle,
                            onValueChange = { viewModel.updateReplayInputs(scenarioTitle = it) },
                            label = { Text("Incident Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = replayInput.trigger,
                            onValueChange = { viewModel.updateReplayInputs(trigger = it) },
                            label = { Text("1. Trigger (What initiated the event?)") },
                            placeholder = { Text("e.g. Coming home from Barasat PG with heavy laundry") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = replayInput.wordsSaid,
                            onValueChange = { viewModel.updateReplayInputs(wordsSaid = it) },
                            label = { Text("2. What was actually said? (Visible words)") },
                            placeholder = { Text("e.g. 'You don't even know how to look after yourself.'") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = replayInput.userAssumption,
                            onValueChange = { viewModel.updateReplayInputs(userAssumption = it) },
                            label = { Text("3. What did I assume they meant?") },
                            placeholder = { Text("e.g. 'She thinks I am useless / doesn't care about college.'") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = replayInput.userEmotion,
                            onValueChange = { viewModel.updateReplayInputs(userEmotion = it) },
                            label = { Text("4. What emotion did I feel? (Anger, guilt, panic)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = replayInput.userNeed,
                            onValueChange = { viewModel.updateReplayInputs(userNeed = it) },
                            label = { Text("5. What did I actually need in that moment?") },
                            placeholder = { Text("e.g. Acknowledge my fatigue; patience with disorganization") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.analyzeReplay()
                                showForm = false
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isAnalyzing
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing systems collision...")
                            } else {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Deconstruct Conflict Layers", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Results Presentation
            if (currentDeconstruction != null) {
                val deconstruction = currentDeconstruction!!

                // Mind reading warning if detected
                if (deconstruction.mindReadingNotice != null) {
                    MindReadingAlertCard(notice = deconstruction.mindReadingNotice)
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 1. FACTS (Observable Events)
                DeconstructionSectionCard(
                    title = "1. FACTS (WHAT WE KNOW FOR CERTAIN)",
                    accentColor = VioletBridge,
                    testTag = "facts_section"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        deconstruction.facts.forEach { fact ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("•", color = VioletBridge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = fact,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. POSSIBLE PRESSURES (The Invisible Story)
                DeconstructionSectionCard(
                    title = "2. POSSIBLE PRESSURES (THE INVISIBLE STORY)",
                    accentColor = HeatTense,
                    testTag = "pressures_section"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column {
                            Text(
                                text = "Student Pressure (College & PG in Barasat):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = IndigoPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = deconstruction.studentPressure,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column {
                            Text(
                                text = "Parent Pressure (Household & HVAC Work):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = VioletBridge
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = deconstruction.parentPressure,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. COLLIDING ASSUMPTIONS vs REFRAMING
                DeconstructionSectionCard(
                    title = "3. COLLIDING ASSUMPTION vs REFRAMING",
                    accentColor = HeatHeated,
                    testTag = "assumptions_section"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(HeatHeated.copy(alpha = 0.08f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Collision: ${deconstruction.collidingAssumption}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "Possible Alternative Explanation:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = HeatCalm
                        )
                        Text(
                            text = deconstruction.alternativeExplanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. SHARED UNDERLYING NEED
                DeconstructionSectionCard(
                    title = "4. SHARED UNDERLYING NEED",
                    accentColor = HeatCalm,
                    testTag = "shared_need_section"
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = HeatCalm)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = deconstruction.sharedUnderlyingNeed,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. SENTIMENT SHIFT TRAJECTORY (Machine Learning / Systems Analysis)
                DeconstructionSectionCard(
                    title = "5. SENTIMENT SHIFT TRAJECTORY",
                    accentColor = IndigoPrimary,
                    testTag = "trajectory_section"
                ) {
                    Column {
                        Text(
                            text = "Tracking emotional heat as perspective shifts from reactive words to constructive clarity:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            deconstruction.sentimentShiftTrajectory.forEachIndexed { index, pair ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    pair.second > 70 -> HeatHeated.copy(alpha = 0.2f)
                                                    pair.second > 40 -> HeatTense.copy(alpha = 0.2f)
                                                    else -> HeatCalm.copy(alpha = 0.2f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${pair.second}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = when {
                                                pair.second > 70 -> HeatHeated
                                                pair.second > 40 -> HeatTense
                                                else -> HeatCalm
                                            }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pair.first,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                                if (index < deconstruction.sentimentShiftTrajectory.size - 1) {
                                    Text("→", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 6. SMALLEST NEXT ACTION (Micro-Repair)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, HeatCalm)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = HeatCalm)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "6. SMALLEST NEXT REPAIR ACTION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = HeatCalm
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "“${deconstruction.smallestNextAction}”",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Converts guilt into concrete initiative. Does not require them to apologize first.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DeconstructionSectionCard(
    title: String,
    accentColor: Color,
    testTag: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = accentColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
