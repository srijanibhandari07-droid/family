package com.example.data.repository

import com.example.data.ai.SystemsThinkingEngine
import com.example.data.local.BetweenUsDao
import com.example.data.model.FamilyRole
import com.example.data.model.MentalSpaceCheckInEntity
import com.example.data.model.MicroActionEntity
import com.example.data.model.ReflectionEntity
import com.example.data.model.ResponsibilityEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BetweenUsRepository(private val dao: BetweenUsDao) {

    val allReflections: Flow<List<ReflectionEntity>> = dao.getAllReflections()
    val allResponsibilities: Flow<List<ResponsibilityEntity>> = dao.getAllResponsibilities()
    val allMicroActions: Flow<List<MicroActionEntity>> = dao.getAllMicroActions()
    val recentCheckIns: Flow<List<MentalSpaceCheckInEntity>> = dao.getRecentCheckIns()

    suspend fun initializeSeedDataIfNeeded() {
        val existingResponsibilities = dao.getAllResponsibilities().first()
        if (existingResponsibilities.isEmpty()) {
            val defaultResponsibilities = listOf(
                // Student (Brainware University, Barasat PG)
                ResponsibilityEntity(ownerRole = FamilyRole.STUDENT.name, title = "B.Tech CSE Classes & Lab Work", category = "ACADEMIC", pressureLevel = 4, notes = "Understanding CS concepts with non-CS background"),
                ResponsibilityEntity(ownerRole = FamilyRole.STUDENT.name, title = "Maintaining 75%+ Attendance", category = "ACADEMIC", pressureLevel = 5, notes = "Strict university criteria, Barasat transit"),
                ResponsibilityEntity(ownerRole = FamilyRole.STUDENT.name, title = "Programming & Hackathon Projects", category = "ACADEMIC", pressureLevel = 4, notes = "Building GitHub portfolio and real apps"),
                ResponsibilityEntity(ownerRole = FamilyRole.STUDENT.name, title = "PG Self-Management & Food", category = "HOUSEHOLD", pressureLevel = 3, notes = "Living away in Barasat, managing laundry and food"),
                ResponsibilityEntity(ownerRole = FamilyRole.STUDENT.name, title = "Career & Internship Uncertainty", category = "FUTURE", pressureLevel = 4, notes = "Figuring out future direction in AI/ML"),

                // Mother (Homemaker, Kolkata home)
                ResponsibilityEntity(ownerRole = FamilyRole.MOTHER.name, title = "Daily Cooking & Kitchen Operation", category = "HOUSEHOLD", pressureLevel = 4, notes = "Managing nutrition, dietary needs for family"),
                ResponsibilityEntity(ownerRole = FamilyRole.MOTHER.name, title = "Extended Relatives & Social Ties", category = "EMOTIONAL", pressureLevel = 5, notes = "Frequent phone calls, expectations from both sides"),
                ResponsibilityEntity(ownerRole = FamilyRole.MOTHER.name, title = "Worry Over Student Living in PG", category = "EMOTIONAL", pressureLevel = 4, notes = "Concern if child is eating properly or safe"),
                ResponsibilityEntity(ownerRole = FamilyRole.MOTHER.name, title = "Household Logistics & Supplies", category = "HOUSEHOLD", pressureLevel = 3, notes = "Daily maintenance and scheduling"),

                // Father (Mechanical Engineer, HVAC servicing business)
                ResponsibilityEntity(ownerRole = FamilyRole.FATHER.name, title = "HVAC / AC Servicing Client Calls", category = "PROFESSIONAL", pressureLevel = 5, notes = "Urgent commercial and residential repair jobs"),
                ResponsibilityEntity(ownerRole = FamilyRole.FATHER.name, title = "Financial Planning & College Fees", category = "FINANCIAL", pressureLevel = 4, notes = "Cash flow, equipment maintenance, future security"),
                ResponsibilityEntity(ownerRole = FamilyRole.FATHER.name, title = "Physical Health & Stress Endurance", category = "HEALTH", pressureLevel = 4, notes = "Field visits in hot weather and physical fatigue"),
                ResponsibilityEntity(ownerRole = FamilyRole.FATHER.name, title = "Family Safety & Stability", category = "FAMILY", pressureLevel = 4, notes = "Long-term independence of the family unit")
            )
            dao.insertResponsibilities(defaultResponsibilities)
        }

        val existingActions = dao.getAllMicroActions().first()
        if (existingActions.isEmpty()) {
            val defaultActions = listOf(
                MicroActionEntity(title = "Put clothes for washing without waiting to be asked", category = "RESPONSIBILITY", targetRole = FamilyRole.MOTHER.name),
                MicroActionEntity(title = "Clean study desk and room before leaving for Barasat", category = "REPAIR", targetRole = FamilyRole.MOTHER.name),
                MicroActionEntity(title = "Call home proactively at 8:30 PM (before they call)", category = "COMMUNICATION", targetRole = FamilyRole.MOTHER.name),
                MicroActionEntity(title = "Briefly explain tomorrow's class & lab schedule calmly", category = "COMMUNICATION", targetRole = FamilyRole.FATHER.name),
                MicroActionEntity(title = "Complete 1 household errand before sitting with laptop", category = "REPAIR", targetRole = FamilyRole.FAMILY.name),
                MicroActionEntity(title = "Ask 'How was your client visit today?' before complaining", category = "COMMUNICATION", targetRole = FamilyRole.FATHER.name)
            )
            dao.insertMicroActions(defaultActions)
        }

        val existingReflections = dao.getAllReflections().first()
        if (existingReflections.isEmpty()) {
            val demo = SystemsThinkingEngine.getDemoScenario()
            val demoReflection = ReflectionEntity(
                scenarioTitle = "Return from Barasat PG to Kolkata",
                trigger = "Coming home for the weekend with heavy laundry and fatigue",
                wordsSaid = "Mother: 'You don't even know how to look after yourself.' / Student: 'I have so much college work, why does nobody understand?'",
                assumption = "Student assumed: 'They think I'm lazy.' Parents assumed: 'They take home for granted.'",
                emotion = "Defensive, overwhelmed, angry, guilty",
                actualNeed = "Reassurance that effort is acknowledged, and evidence that responsibility is shared.",
                alternativeAction = "Acknowledge the fatigue calmly and wash the dishes without defensive retorts.",
                heatScore = demo.heatScore,
                speakerRole = FamilyRole.MOTHER.name,
                responderRole = FamilyRole.STUDENT.name,
                factSummary = demo.facts.joinToString("\n• "),
                studentPressure = demo.studentPressure,
                parentPressure = demo.parentPressure,
                sharedNeed = demo.sharedUnderlyingNeed,
                isResolved = false,
                aiAssisted = true
            )
            dao.insertReflection(demoReflection)
        }
    }

    suspend fun saveReflection(reflection: ReflectionEntity): Long {
        return dao.insertReflection(reflection)
    }

    suspend fun deleteReflection(id: Long) {
        dao.deleteReflectionById(id)
    }

    suspend fun toggleMicroAction(id: Long, completed: Boolean) {
        dao.toggleMicroAction(id, completed, if (completed) System.currentTimeMillis() else null)
    }

    suspend fun addMicroAction(title: String, category: String, targetRole: String) {
        dao.insertMicroAction(
            MicroActionEntity(
                title = title,
                category = category,
                targetRole = targetRole
            )
        )
    }

    suspend fun deleteMicroAction(action: MicroActionEntity) {
        dao.deleteMicroAction(action)
    }

    suspend fun addResponsibility(role: String, title: String, category: String, pressureLevel: Int, notes: String) {
        dao.insertResponsibility(
            ResponsibilityEntity(
                ownerRole = role,
                title = title,
                category = category,
                pressureLevel = pressureLevel,
                notes = notes
            )
        )
    }

    suspend fun deleteResponsibility(item: ResponsibilityEntity) {
        dao.deleteResponsibility(item)
    }

    suspend fun logCheckIn(role: String, category: String, intensity: Int, note: String) {
        dao.insertCheckIn(
            MentalSpaceCheckInEntity(
                role = role,
                category = category,
                intensity = intensity,
                note = note
            )
        )
    }
}
