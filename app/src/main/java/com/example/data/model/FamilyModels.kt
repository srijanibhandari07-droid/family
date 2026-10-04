package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FamilyRole(val displayName: String, val contextDescription: String) {
    STUDENT("Student", "1st Year B.Tech CSE AI/ML (Barasat PG, Non-CS background)"),
    MOTHER("Mother", "Homemaker & Emotional Anchor (Household, Relatives, Well-being)"),
    FATHER("Father", "Mechanical Engineer / HVAC Business (Clients, Finance, Health)"),
    FAMILY("Family", "Shared household unit responsibilities"),
    OTHER("Family Member", "Household member with individual pressures")
}

enum class HeatLevelStatus(val minScore: Int, val maxScore: Int, val title: String, val subtitle: String) {
    CALM(0, 35, "Calm", "Ground is steady. Good window for constructive sharing."),
    TENSE(36, 70, "Tense", "Pressure accumulating. Assumptions are active."),
    HEATED(71, 100, "Heated", "Immediate reactive threshold. Step back before speaking.")
}

@Entity(tableName = "reflections")
data class ReflectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val scenarioTitle: String,
    val trigger: String,
    val wordsSaid: String,
    val assumption: String,
    val emotion: String,
    val actualNeed: String,
    val alternativeAction: String,
    val heatScore: Int,
    val speakerRole: String,
    val responderRole: String,
    val factSummary: String,
    val studentPressure: String,
    val parentPressure: String,
    val sharedNeed: String,
    val isResolved: Boolean = false,
    val aiAssisted: Boolean = true
)

@Entity(tableName = "responsibilities")
data class ResponsibilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerRole: String, // STUDENT, MOTHER, FATHER
    val title: String,
    val category: String, // ACADEMIC, HOUSEHOLD, PROFESSIONAL, EMOTIONAL, HEALTH, FINANCIAL
    val pressureLevel: Int = 3, // 1 to 5
    val isShared: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "micro_actions")
data class MicroActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // REPAIR, RESPONSIBILITY, COMMUNICATION
    val targetRole: String, // MOTHER, FATHER, FAMILY
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(tableName = "mental_space_checkins")
data class MentalSpaceCheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String,
    val category: String, // College, Work, Money, Health, Household, Relationships, Future, Nothing specific
    val intensity: Int = 3, // 1 to 5
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class HeatMeterAnalysis(
    val heatScore: Int,
    val status: HeatLevelStatus,
    val insightMessage: String,
    val visibleWords: String,
    val possiblePressure: String,
    val possibleNeed: String,
    val myEffortFeeling: String,
    val studentNeed: String,
    val suggestedCalmResponse: String,
    val mindReadingWarning: String? = null
)

data class TwoWorldGapItem(
    val id: String,
    val myWorldFacet: String,
    val theirWorldFacet: String,
    val collidingAssumption: String,
    val possibleAlternative: String,
    val sharedUnderlyingGoal: String
)

data class ConstellationNode(
    val id: String,
    val label: String,
    val role: FamilyRole,
    val pressureWeight: Float, // 0.2f to 1.0f
    val category: String
)

data class ConstellationLink(
    val sourceId: String,
    val targetId: String,
    val tensionNote: String
)
