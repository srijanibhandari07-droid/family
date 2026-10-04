package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ConflictDeconstruction
import com.example.data.ai.SystemsThinkingEngine
import com.example.data.local.AppDatabase
import com.example.data.model.FamilyRole
import com.example.data.model.HeatLevelStatus
import com.example.data.model.HeatMeterAnalysis
import com.example.data.model.MentalSpaceCheckInEntity
import com.example.data.model.MicroActionEntity
import com.example.data.model.ReflectionEntity
import com.example.data.model.ResponsibilityEntity
import com.example.data.repository.BetweenUsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HeatMeterUiState(
    val whatHappened: String = "",
    val whatFeeling: String = "",
    val whatWant: String = "",
    val isAnalyzing: Boolean = false,
    val analysis: HeatMeterAnalysis? = null,
    val isPauseActive: Boolean = false,
    val breathingSecondsLeft: Int = 45
)

data class ReplayInputState(
    val scenarioTitle: String = "PG Weekend Discussion",
    val trigger: String = "",
    val wordsSaid: String = "",
    val userAssumption: String = "",
    val userEmotion: String = "",
    val userNeed: String = "",
    val speakerRole: FamilyRole = FamilyRole.MOTHER,
    val responderRole: FamilyRole = FamilyRole.STUDENT
)

class BetweenUsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BetweenUsRepository

    val allReflections: StateFlow<List<ReflectionEntity>>
    val allResponsibilities: StateFlow<List<ResponsibilityEntity>>
    val allMicroActions: StateFlow<List<MicroActionEntity>>
    val recentCheckIns: StateFlow<List<MentalSpaceCheckInEntity>>

    private val _activeRole = MutableStateFlow(FamilyRole.STUDENT)
    val activeRole: StateFlow<FamilyRole> = _activeRole.asStateFlow()

    private val _heatMeterState = MutableStateFlow(HeatMeterUiState())
    val heatMeterState: StateFlow<HeatMeterUiState> = _heatMeterState.asStateFlow()

    private val _replayInputState = MutableStateFlow(ReplayInputState())
    val replayInputState: StateFlow<ReplayInputState> = _replayInputState.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _currentDeconstruction = MutableStateFlow<ConflictDeconstruction?>(null)
    val currentDeconstruction: StateFlow<ConflictDeconstruction?> = _currentDeconstruction.asStateFlow()

    private val _selectedReflection = MutableStateFlow<ReflectionEntity?>(null)
    val selectedReflection: StateFlow<ReflectionEntity?> = _selectedReflection.asStateFlow()

    private val _safetyAlert = MutableStateFlow<String?>(null)
    val safetyAlert: StateFlow<String?> = _safetyAlert.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = BetweenUsRepository(database.betweenUsDao())

        allReflections = repository.allReflections.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allResponsibilities = repository.allResponsibilities.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allMicroActions = repository.allMicroActions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        recentCheckIns = repository.recentCheckIns.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            // Set initial demo deconstruction for quick exploration
            _currentDeconstruction.value = SystemsThinkingEngine.getDemoScenario()
        }
    }

    fun switchActiveRole(role: FamilyRole) {
        _activeRole.value = role
    }

    // --- Heat Meter Flow ---
    fun updateHeatInputs(whatHappened: String, whatFeeling: String, whatWant: String) {
        _heatMeterState.value = _heatMeterState.value.copy(
            whatHappened = whatHappened,
            whatFeeling = whatFeeling,
            whatWant = whatWant
        )
    }

    fun calculateHeatMeter() {
        val state = _heatMeterState.value
        _heatMeterState.value = state.copy(isAnalyzing = true)
        viewModelScope.launch {
            val analysis = SystemsThinkingEngine.analyzeHeatQuick(
                state.whatHappened,
                state.whatFeeling,
                state.whatWant
            )
            _heatMeterState.value = _heatMeterState.value.copy(
                isAnalyzing = false,
                analysis = analysis
            )
        }
    }

    fun resetHeatMeter() {
        _heatMeterState.value = HeatMeterUiState()
    }

    fun setPauseActive(active: Boolean) {
        _heatMeterState.value = _heatMeterState.value.copy(isPauseActive = active)
    }

    // --- Replay & Deconstruction Flow ---
    fun updateReplayInputs(
        scenarioTitle: String = _replayInputState.value.scenarioTitle,
        trigger: String = _replayInputState.value.trigger,
        wordsSaid: String = _replayInputState.value.wordsSaid,
        userAssumption: String = _replayInputState.value.userAssumption,
        userEmotion: String = _replayInputState.value.userEmotion,
        userNeed: String = _replayInputState.value.userNeed,
        speakerRole: FamilyRole = _replayInputState.value.speakerRole,
        responderRole: FamilyRole = _replayInputState.value.responderRole
    ) {
        _replayInputState.value = _replayInputState.value.copy(
            scenarioTitle = scenarioTitle,
            trigger = trigger,
            wordsSaid = wordsSaid,
            userAssumption = userAssumption,
            userEmotion = userEmotion,
            userNeed = userNeed,
            speakerRole = speakerRole,
            responderRole = responderRole
        )
    }

    fun analyzeReplay() {
        val input = _replayInputState.value
        _isAnalyzing.value = true
        viewModelScope.launch {
            val deconstruction = SystemsThinkingEngine.deconstructConflict(
                trigger = input.trigger,
                wordsSaid = input.wordsSaid,
                userAssumption = input.userAssumption,
                userEmotion = input.userEmotion,
                userNeed = input.userNeed
            )
            _isAnalyzing.value = false
            if (deconstruction.isSafetyAlert) {
                _safetyAlert.value = deconstruction.safetyGuidance
            }
            _currentDeconstruction.value = deconstruction
        }
    }

    fun loadPreloadedScenario() {
        val demo = SystemsThinkingEngine.getDemoScenario()
        _currentDeconstruction.value = demo
        _replayInputState.value = ReplayInputState(
            scenarioTitle = "Return from Barasat PG to Kolkata",
            trigger = "Coming home for the weekend with heavy laundry and fatigue",
            wordsSaid = "Mother: 'You don't even know how to look after yourself.' / Student: 'I have so much college work, why does nobody understand?'",
            userAssumption = "They think I am lazy and don't care about the family.",
            userEmotion = "Defensive, overwhelmed, angry, guilty",
            userNeed = "Validation that academic effort is acknowledged, and patience with disorganization.",
            speakerRole = FamilyRole.MOTHER,
            responderRole = FamilyRole.STUDENT
        )
    }

    fun saveCurrentDeconstruction() {
        val deconstruction = _currentDeconstruction.value ?: return
        val input = _replayInputState.value
        viewModelScope.launch {
            val reflection = ReflectionEntity(
                scenarioTitle = input.scenarioTitle.ifBlank { "Argument Reflection" },
                trigger = input.trigger.ifBlank { "Family discussion" },
                wordsSaid = input.wordsSaid.ifBlank { deconstruction.facts.firstOrNull() ?: "Disagreement" },
                assumption = input.userAssumption.ifBlank { deconstruction.collidingAssumption },
                emotion = input.userEmotion.ifBlank { "Tense" },
                actualNeed = input.userNeed.ifBlank { deconstruction.sharedUnderlyingNeed },
                alternativeAction = deconstruction.smallestNextAction,
                heatScore = deconstruction.heatScore,
                speakerRole = input.speakerRole.name,
                responderRole = input.responderRole.name,
                factSummary = deconstruction.facts.joinToString("\n• "),
                studentPressure = deconstruction.studentPressure,
                parentPressure = deconstruction.parentPressure,
                sharedNeed = deconstruction.sharedUnderlyingNeed,
                isResolved = false,
                aiAssisted = true
            )
            repository.saveReflection(reflection)
            // Also suggest a micro action
            repository.addMicroAction(
                title = deconstruction.smallestNextAction,
                category = "REPAIR",
                targetRole = input.speakerRole.name
            )
        }
    }

    fun selectReflection(reflection: ReflectionEntity?) {
        _selectedReflection.value = reflection
    }

    fun deleteReflection(id: Long) {
        viewModelScope.launch {
            repository.deleteReflection(id)
            if (_selectedReflection.value?.id == id) {
                _selectedReflection.value = null
            }
        }
    }

    // --- Micro Action System ---
    fun toggleMicroAction(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleMicroAction(id, completed)
        }
    }

    fun addMicroAction(title: String, category: String, targetRole: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addMicroAction(title, category, targetRole)
        }
    }

    fun deleteMicroAction(action: MicroActionEntity) {
        viewModelScope.launch {
            repository.deleteMicroAction(action)
        }
    }

    // --- Responsibilities / Invisible Work Map ---
    fun addResponsibility(role: String, title: String, category: String, pressureLevel: Int, notes: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addResponsibility(role, title, category, pressureLevel, notes)
        }
    }

    fun deleteResponsibility(item: ResponsibilityEntity) {
        viewModelScope.launch {
            repository.deleteResponsibility(item)
        }
    }

    // --- Check-Ins ---
    fun submitCheckIn(category: String, intensity: Int, note: String) {
        viewModelScope.launch {
            repository.logCheckIn(
                role = _activeRole.value.name,
                category = category,
                intensity = intensity,
                note = note
            )
        }
    }

    fun clearSafetyAlert() {
        _safetyAlert.value = null
    }
}
