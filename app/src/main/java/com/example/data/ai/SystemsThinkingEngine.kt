package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.HeatLevelStatus
import com.example.data.model.HeatMeterAnalysis
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class ConflictDeconstruction(
    val isSafetyAlert: Boolean = false,
    val safetyGuidance: String? = null,
    val heatScore: Int,
    val heatStatus: HeatLevelStatus,
    val facts: List<String>,
    val studentPressure: String,
    val parentPressure: String,
    val collidingAssumption: String,
    val alternativeExplanation: String,
    val sharedUnderlyingNeed: String,
    val smallestNextAction: String,
    val mindReadingNotice: String? = null,
    val translatedStatement: String,
    val constructiveResponse: String,
    val sentimentShiftTrajectory: List<Pair<String, Int>> // Label to heat index (0-100)
)

object SystemsThinkingEngine {

    // Mind-reading & cognitive distortion triggers
    private val mindReadingPatterns = listOf(
        Regex("(?i).*they think (I'm|I am) (lazy|useless|careless|good for nothing).*"),
        Regex("(?i).*nobody (cares|understands|listens).*"),
        Regex("(?i).*(she|he|they) (hate|hates|despises) me.*"),
        Regex("(?i).*they just want to control me.*"),
        Regex("(?i).*you never (listen|care|help).*"),
        Regex("(?i).*you always (waste|ignore|forget).*"),
        Regex("(?i).*she thinks I don't care.*")
    )

    private val safetyKeywords = listOf(
        "hit me", "slapped", "beat me", "punch", "threatened to kill", "physical violence",
        "bruise", "unsafe at home", "hurting myself", "abusing me", "fear for my life"
    )

    fun checkSafetyRisk(text: String): Boolean {
        val lower = text.lowercase()
        return safetyKeywords.any { lower.contains(it) }
    }

    fun detectMindReading(text: String): String? {
        val matches = mindReadingPatterns.any { it.matches(text) }
        return if (matches || text.contains("they think", ignoreCase = true) || text.contains("she thinks", ignoreCase = true)) {
            "\"That's a conclusion. What direct evidence do you have, and what could be another explanation?\""
        } else null
    }

    suspend fun analyzeHeatQuick(
        whatHappened: String,
        whatFeeling: String,
        whatWant: String
    ): HeatMeterAnalysis = withContext(Dispatchers.Default) {
        val combined = "$whatHappened $whatFeeling $whatWant"
        var calculatedScore = 50

        val lower = combined.lowercase()
        if (lower.contains("screamed") || lower.contains("shouted") || lower.contains("furious") || lower.contains("burst") || lower.contains("hate")) {
            calculatedScore += 35
        } else if (lower.contains("angry") || lower.contains("argued") || lower.contains("upset") || lower.contains("crying")) {
            calculatedScore += 22
        } else if (lower.contains("annoyed") || lower.contains("frustrated") || lower.contains("irritated")) {
            calculatedScore += 12
        }

        if (lower.contains("phone") || lower.contains("lazy") || lower.contains("waste") || lower.contains("careless") || lower.contains("useless")) {
            calculatedScore += 10
        }

        calculatedScore = calculatedScore.coerceIn(15, 95)

        val status = when {
            calculatedScore <= 35 -> HeatLevelStatus.CALM
            calculatedScore <= 70 -> HeatLevelStatus.TENSE
            else -> HeatLevelStatus.HEATED
        }

        val mindReading = detectMindReading(combined)

        HeatMeterAnalysis(
            heatScore = calculatedScore,
            status = status,
            insightMessage = "You may be reacting to the immediate words while the other person may be reacting to accumulated pressure.",
            visibleWords = if (whatHappened.isNotBlank()) "\"$whatHappened\"" else "\"You don't care.\"",
            possiblePressure = "They may be carrying invisible worries about health, financial responsibilities, or whether you are becoming independent.",
            possibleNeed = "Evidence and reassurance that responsibilities are being managed safely.",
            myEffortFeeling = "Effort in college (programming, attendance, PG life) feels invisible or unacknowledged.",
            studentNeed = "Wanting family to understand that effort is occurring even when disorganization happens.",
            suggestedCalmResponse = "I understand why you're worried about this. I'm also finding it challenging to manage my schedule right now. Here's one specific thing I'll handle today.",
            mindReadingWarning = mindReading
        )
    }

    suspend fun deconstructConflict(
        trigger: String,
        wordsSaid: String,
        userAssumption: String,
        userEmotion: String,
        userNeed: String,
        customContext: String = ""
    ): ConflictDeconstruction = withContext(Dispatchers.IO) {
        val fullText = "$trigger $wordsSaid $userAssumption $userEmotion $userNeed $customContext"

        if (checkSafetyRisk(fullText)) {
            return@withContext ConflictDeconstruction(
                isSafetyAlert = true,
                safetyGuidance = "This situation involves indications of acute threat, violence, or severe distress. The system prioritizes safety over conflict mediation. Please reach out immediately to a trusted local mentor, emergency hotline, or safe contact.",
                heatScore = 98,
                heatStatus = HeatLevelStatus.HEATED,
                facts = listOf("Safety threshold flagged"),
                studentPressure = "Immediate safety concern",
                parentPressure = "Immediate safety concern",
                collidingAssumption = "High risk environment",
                alternativeExplanation = "Safety takes precedence over communication mediation",
                sharedUnderlyingNeed = "Physical and emotional safety",
                smallestNextAction = "Reach out to local helpline or trusted third party.",
                translatedStatement = "Please prioritize safety.",
                constructiveResponse = "I need to step outside to ensure everyone remains safe.",
                sentimentShiftTrajectory = listOf("Threat" to 98, "Boundary" to 70, "Safety" to 30)
            )
        }

        // Try calling Gemini if API key is provided
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiResult = callGeminiSystemsDeconstruction(
                    apiKey = apiKey,
                    trigger = trigger,
                    wordsSaid = wordsSaid,
                    assumption = userAssumption,
                    emotion = userEmotion,
                    need = userNeed
                )
                if (geminiResult != null) return@withContext geminiResult
            } catch (e: Exception) {
                // Fall through to deterministic systems engine
            }
        }

        // Deterministic systems engine fallback
        fallbackSystemsDeconstruction(trigger, wordsSaid, userAssumption, userEmotion, userNeed)
    }

    private suspend fun callGeminiSystemsDeconstruction(
        apiKey: String,
        trigger: String,
        wordsSaid: String,
        assumption: String,
        emotion: String,
        need: String
    ): ConflictDeconstruction? {
        val systemPrompt = """
            You are 'Between Us', an emotionally mature, neutral systems-thinking assistant for families.
            Your purpose is: 'Help people understand the pressure behind the argument.'
            Rules:
            - NEVER diagnose medically or psychologically.
            - NEVER declare who is right or wrong.
            - NEVER say 'Your mother definitely feels X'. Say 'One possible explanation is X. Only the person can confirm what they actually mean.'
            - Distinguish: FACT vs INTERPRETATION vs EMOTION vs ASSUMPTION.
            - Separate: WORDS -> EMOTION -> PRESSURE -> NEED -> ACTION.
            - Balance: 'Understanding parents does not mean agreeing; understanding child does not mean removing responsibility.'
            Return ONLY a valid JSON object with keys:
            {
              "heatScore": int (0-100),
              "facts": [string],
              "studentPressure": string,
              "parentPressure": string,
              "collidingAssumption": string,
              "alternativeExplanation": string,
              "sharedUnderlyingNeed": string,
              "smallestNextAction": string,
              "translatedStatement": string,
              "constructiveResponse": string,
              "mindReadingNotice": string
            }
        """.trimIndent()

        val userPrompt = """
            Analyze this family conflict:
            Trigger: $trigger
            Words Said: $wordsSaid
            User Assumption: $assumption
            User Emotion: $emotion
            User Need: $need
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = userPrompt)))
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.3f,
                responseMimeType = "application/json"
            )
        )

        val response = GeminiClient.service.generateContent(apiKey, request)
        val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null

        val json = JSONObject(text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim())

        val heat = json.optInt("heatScore", 75)
        val factsList = mutableListOf<String>()
        val factsArray = json.optJSONArray("facts")
        if (factsArray != null) {
            for (i in 0 until factsArray.length()) {
                factsList.add(factsArray.getString(i))
            }
        }
        if (factsList.isEmpty()) {
            factsList.add("Interaction occurred around daily expectations.")
        }

        val status = when {
            heat <= 35 -> HeatLevelStatus.CALM
            heat <= 70 -> HeatLevelStatus.TENSE
            else -> HeatLevelStatus.HEATED
        }

        return ConflictDeconstruction(
            isSafetyAlert = false,
            heatScore = heat,
            heatStatus = status,
            facts = factsList,
            studentPressure = json.optString("studentPressure", "Academic stress and adapting to independent life."),
            parentPressure = json.optString("parentPressure", "Accumulated household/business worry and future uncertainty."),
            collidingAssumption = json.optString("collidingAssumption", "Assuming criticism equates to rejection."),
            alternativeExplanation = json.optString("alternativeExplanation", "Worry expressed through sharp words because effort is invisible."),
            sharedUnderlyingNeed = json.optString("sharedUnderlyingNeed", "Both want safety, competence, and mutual respect."),
            smallestNextAction = json.optString("smallestNextAction", "Take initiative on one small responsibility without waiting to be asked."),
            mindReadingNotice = detectMindReading("$assumption $wordsSaid"),
            translatedStatement = json.optString("translatedStatement", "I am worried about whether you are managing everything okay."),
            constructiveResponse = json.optString("constructiveResponse", "I hear that you're worried. I'm working to organize my schedule, and I will handle this task now."),
            sentimentShiftTrajectory = listOf(
                "Trigger" to (heat - 15).coerceAtLeast(20),
                "Friction Words" to heat,
                "Underlying Need" to (heat - 35).coerceAtLeast(30),
                "Small Repair" to (heat - 55).coerceAtLeast(15)
            )
        )
    }

    private fun fallbackSystemsDeconstruction(
        trigger: String,
        wordsSaid: String,
        assumption: String,
        emotion: String,
        need: String
    ): ConflictDeconstruction {
        val wordsLower = wordsSaid.lowercase()
        val assumptionLower = assumption.lowercase()

        val heatScore = when {
            wordsLower.contains("never") || wordsLower.contains("always") || wordsLower.contains("useless") -> 84
            wordsLower.contains("lazy") || wordsLower.contains("phone") || wordsLower.contains("waste") -> 76
            else -> 65
        }

        val status = if (heatScore > 70) HeatLevelStatus.HEATED else HeatLevelStatus.TENSE

        val mindReading = detectMindReading("$assumption $wordsSaid")

        // Constructive possibilities - never claiming absolute facts
        val possibleParentPressure = when {
            wordsLower.contains("phone") || wordsLower.contains("lazy") ->
                "One possible explanation: Anxiety regarding college transitions and wanting visible proof of healthy habits."
            wordsLower.contains("help") || wordsLower.contains("careless") ->
                "One possible explanation: Accumulated fatigue from household/business logistics with little bandwidth to absorb disorganization."
            else ->
                "One possible explanation: General stress from work, health, or family maintenance expressing itself through frustration."
        }

        val possibleStudentPressure = "Navigating first-year B.Tech academic load, non-CS foundations, living in a PG in Barasat, and feeling effort is unrecognized."

        val collidingAssumption = if (assumption.isNotBlank()) {
            "Student assumption: \"$assumption\" colliding with Parent assumption: \"Lack of immediate responsiveness means lack of care.\""
        } else {
            "Assumption that blunt feedback implies lack of belief in the student's future."
        }

        val alternative = "They may be frightened by how little they can control your future, and reacting to visible disarray because they cannot see your internal effort."

        val sharedNeed = "Mutual safety: The parent wants reassurance that the student will thrive independently; the student wants patience while learning to self-manage."

        val smallestAction = "Before leaving or ending the conversation, complete one concrete task (wash dishes, clarify tomorrow's schedule, or tidy the room) without comment."

        val translated = when {
            wordsLower.contains("phone") ->
                "\"I am worried you are becoming less focused on your future, and I need reassurance that you can manage yourself.\""
            wordsLower.contains("look after yourself") ->
                "\"Seeing you look worn out or disordered makes me anxious that you are struggling living away from home.\""
            else ->
                "\"I feel overwhelmed by my own responsibilities right now and need support rather than added friction.\""
        }

        val constructiveReply = "I understand why this looks careless from the outside. I am finding it tricky to balance everything right now, but I value your concern. I will take care of this specific item right now."

        val facts = listOf(
            "An interaction took place regarding: ${if (trigger.isNotBlank()) trigger else "daily responsibilities"}.",
            "Specific words spoken: \"${if (wordsSaid.isNotBlank()) wordsSaid else "Unexpressed frustration"}\".",
            "Emotional tone recorded as: ${if (emotion.isNotBlank()) emotion else "Tense/Frustrated"}."
        )

        return ConflictDeconstruction(
            isSafetyAlert = false,
            heatScore = heatScore,
            heatStatus = status,
            facts = facts,
            studentPressure = possibleStudentPressure,
            parentPressure = possibleParentPressure,
            collidingAssumption = collidingAssumption,
            alternativeExplanation = alternative,
            sharedUnderlyingNeed = sharedNeed,
            smallestNextAction = smallestAction,
            mindReadingNotice = mindReading,
            translatedStatement = translated,
            constructiveResponse = constructiveReply,
            sentimentShiftTrajectory = listOf(
                "Initial Spark" to (heatScore - 20).coerceAtLeast(30),
                "Peak Reaction" to heatScore,
                "Pressure Deconstruction" to (heatScore - 30).coerceAtLeast(25),
                "Constructive Action" to 20
            )
        )
    }

    // Default Preloaded Demo Scenario (Barasat PG student returning home)
    fun getDemoScenario(): ConflictDeconstruction {
        return ConflictDeconstruction(
            isSafetyAlert = false,
            heatScore = 82,
            heatStatus = HeatLevelStatus.HEATED,
            facts = listOf(
                "Student returned home from Barasat PG after consecutive weeks of classes and lab work.",
                "Mother noted disarray and commented on self-care and phone use.",
                "Student felt academic effort and commute fatigue were dismissed.",
                "Father requested an immediate end to the argument due to his own workday stress."
            ),
            studentPressure = "B.Tech CSE coursework (attendance targets, non-CS learning curve, hackathon prep, living alone in PG, fatigue).",
            parentPressure = "Mother: Extended family calls, household cooking, worry over child's independence. Father: HVAC clients, business overhead, fatigue.",
            collidingAssumption = "Student assumption: \"They think I am lazy and don't appreciate college.\" Parent assumption: \"They take home for granted and ignore responsibilities.\"",
            alternativeExplanation = "Mother may be exhausted and worried because she cannot observe the student's daily college battles. Her criticism is an unskillful expression of anxiety.",
            sharedUnderlyingNeed = "Both parties desire reassurance that the student is safe, capable, and that the family bond remains intact despite geographical and role shifts.",
            smallestNextAction = "Tomorrow morning, handle two household chores (e.g. put laundry away, wash kitchen counter) without being asked or bringing up the argument.",
            mindReadingNotice = "\"They think I don't care.\" — That's a conclusion. What direct evidence exists, and could fatigue be the real explanation?",
            translatedStatement = "\"I am worried you are not eating properly or managing your life in the PG, and it scares me that you might falter.\"",
            constructiveResponse = "\"I know it looks chaotic when I arrive with laundry and a tired mood. I'm working hard at Brainware, and I'll clear my desk and help with dinner now.\"",
            sentimentShiftTrajectory = listOf(
                "Arrival in Kolkata" to 35,
                "Mother's Comment" to 82,
                "Father's Intervention" to 88,
                "Post-argument Pause" to 55,
                "Systemic Insight" to 28,
                "Micro-Action" to 15
            )
        )
    }
}
