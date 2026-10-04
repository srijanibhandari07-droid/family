package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.FamilyRole
import com.example.ui.BetweenUsViewModel
import com.example.ui.theme.HeatCalm
import com.example.ui.theme.HeatHeated
import com.example.ui.theme.HeatTense
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletBridge

data class WorldItem(
    val title: String,
    val subtitle: String,
    val pressureLevel: String,
    val isVisibleToOther: Boolean = false
)

data class GapCollision(
    val title: String,
    val myAssumption: String,
    val theirAssumption: String,
    val possibleAlternativeMy: String,
    val possibleAlternativeTheir: String,
    val underlyingSharedGoal: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoWorldScreen(
    viewModel: BetweenUsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = The Gap, 1 = My World, 2 = Their World

    val myWorldItems = remember {
        listOf(
            WorldItem("B.Tech CSE Classes & Labs", "Understanding technical logic with non-CS background", "High", isVisibleToOther = false),
            WorldItem("75% Attendance Benchmark", "Barasat PG commute and strict academic rules", "Critical", isVisibleToOther = false),
            WorldItem("Programming & Hackathons", "Building projects, self-teaching Python/DSA", "High", isVisibleToOther = false),
            WorldItem("Living Away in PG", "Managing meals, laundry, and solitude in Barasat", "Medium", isVisibleToOther = false),
            WorldItem("Career Uncertainty", "Will this degree lead to independence?", "High", isVisibleToOther = false),
            WorldItem("Occasional Disorganization", "Fatigue turning into procrastination or mess", "Visible", isVisibleToOther = true)
        )
    }

    val theirWorldItems = remember {
        listOf(
            WorldItem("Mother: Extended Relatives Calls", "Managing calls, social expectations from both sides", "High", isVisibleToOther = false),
            WorldItem("Mother: Household & Cooking", "Cooking 3 meals, grocery maintenance, health", "Constant", isVisibleToOther = true),
            WorldItem("Mother: Worry About PG Life", "Is the child eating? Is health stable?", "High", isVisibleToOther = false),
            WorldItem("Father: HVAC Client Pressures", "Deadlines, summer peak load, maintenance contracts", "High", isVisibleToOther = false),
            WorldItem("Father: Financial Planning", "Tuition, household cash flow, business expenses", "High", isVisibleToOther = false),
            WorldItem("Parental Fear of Independence", "Will the student be self-sufficient and responsible?", "High", isVisibleToOther = false)
        )
    }

    val collisions = remember {
        listOf(
            GapCollision(
                title = "Phone & Laptop Screen Time",
                myAssumption = "“They think I am lazy and wasting time when I am working or decompressing.”",
                theirAssumption = "“They only care about the phone and don't care about our effort.”",
                possibleAlternativeMy = "They may be anxious because they don't see the coding or study progress happening inside the screen.",
                possibleAlternativeTheir = "The student may be exhausted by cognitive load and unfamiliar programming concepts, seeking a safe mental break.",
                underlyingSharedGoal = "Both desire evidence of a thriving future and mutual care."
            ),
            GapCollision(
                title = "Coming Home With Laundry & Disarray",
                myAssumption = "“They attack me the moment I enter; they don't appreciate my commute from Barasat.”",
                theirAssumption = "“They treat this house like a hotel where others do all the chores.”",
                possibleAlternativeMy = "Mother is already exhausted by domestic chores and interprets mess as an extra burden placed on her.",
                possibleAlternativeTheir = "Student is drained after weeks of managing alone in PG and longs for home to be a sanctuary.",
                underlyingSharedGoal = "Both want home to feel like a place of rest, not friction."
            ),
            GapCollision(
                title = "Silence vs Inquiries",
                myAssumption = "“They are interrogating me because they don't trust my competence.”",
                theirAssumption = "“They are hiding things or drifting away from the family.”",
                possibleAlternativeMy = "Questions are an awkward parental attempt to confirm the student is safe living independently.",
                possibleAlternativeTheir = "Student doesn't want to worry them about non-CS difficulties or college stress.",
                underlyingSharedGoal = "Both want reassurance that the other person is okay."
            )
        )
    }

    Scaffold(
        modifier = modifier.testTag("two_world_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Two-World View",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("two_world_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Header summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, VioletBridge.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CompareArrows, contentDescription = null, tint = VioletBridge)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SEEING THE COLLISION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = VioletBridge
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Most family friction occurs in 'The Gap' — where one person's internal reality is invisible to the other person.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("THE GAP", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("MY WORLD", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("THEIR WORLD", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // THE GAP
                    Text(
                        text = "WHERE ASSUMPTIONS COLLIDE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = HeatTense
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Marked strictly as POSSIBILITIES, not absolute facts. Only the other person can confirm what they actually feel.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    collisions.forEach { collision ->
                        GapCollisionCard(collision = collision)
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                1 -> {
                    // MY WORLD (Student)
                    Text(
                        text = "MY WORLD: COLLEGE & PG LIFE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What is taking your mental space that parents cannot see directly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    myWorldItems.forEach { item ->
                        WorldItemCard(item = item, accentColor = IndigoPrimary, icon = Icons.Default.School)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                2 -> {
                    // THEIR WORLD (Mother & Father)
                    Text(
                        text = "THEIR WORLD: HOUSEHOLD & HVAC RESPONSIBILITIES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = VioletBridge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What is taking their mental space that is rarely talked about during arguments.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    theirWorldItems.forEach { item ->
                        WorldItemCard(
                            item = item,
                            accentColor = VioletBridge,
                            icon = if (item.title.contains("Mother")) Icons.Default.Home else Icons.Default.Business
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GapCollisionCard(
    collision: GapCollision
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("gap_card_${collision.title}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = collision.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HeatTense.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "THE GAP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = HeatTense
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two Colliding Assumptions Side by Side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(IndigoPrimary.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Student Assumption",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = IndigoPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = collision.myAssumption,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VioletBridge.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Parent Assumption",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = VioletBridge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = collision.theirAssumption,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Alternative Explanations
            Text(
                text = "POSSIBLE ALTERNATIVE EXPLANATIONS:",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = HeatCalm
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "• For Parents: ${collision.possibleAlternativeMy}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "• For Student: ${collision.possibleAlternativeTheir}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Shared Underlying Goal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Shared Need: ${collision.underlyingSharedGoal}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun WorldItemCard(
    item: WorldItem,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (item.isVisibleToOther) HeatCalm.copy(alpha = 0.15f)
                                else HeatHeated.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.isVisibleToOther) "VISIBLE TO THEM" else "INVISIBLE LOAD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = if (item.isVisibleToOther) HeatCalm else HeatHeated
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
