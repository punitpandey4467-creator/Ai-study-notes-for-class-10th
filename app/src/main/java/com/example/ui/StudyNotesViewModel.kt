package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StudyNotesRepository
import com.example.data.StudyOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface StudyUiState {
    data object Idle : StudyUiState
    data object Loading : StudyUiState
    data class Success(val content: String, val option: StudyOption) : StudyUiState
    data class Error(val message: String) : StudyUiState
}

data class SampleNote(
    val title: String,
    val text: String
)

class StudyNotesViewModel(
    private val repository: StudyNotesRepository = StudyNotesRepository()
) : ViewModel() {

    private val _notesInput = MutableStateFlow("")
    val notesInput: StateFlow<String> = _notesInput.asStateFlow()

    private val _selectedOption = MutableStateFlow(StudyOption.SUMMARIZE)
    val selectedOption: StateFlow<StudyOption> = _selectedOption.asStateFlow()

    private val _isProUnlocked = MutableStateFlow(false)
    val isProUnlocked: StateFlow<Boolean> = _isProUnlocked.asStateFlow()

    private val _showPremiumDialog = MutableStateFlow(false)
    val showPremiumDialog: StateFlow<Boolean> = _showPremiumDialog.asStateFlow()

    private val _uiState = MutableStateFlow<StudyUiState>(StudyUiState.Idle)
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    val sampleNotes = listOf(
        SampleNote(
            title = "Biology: Cellular Respiration",
            text = "Cellular respiration is the biochemical pathway by which cells convert glucose and oxygen into ATP, carbon dioxide, and water. It consists of three primary stages: 1) Glycolysis in the cytoplasm (anaerobic, generates 2 ATP and 2 NADH), 2) Krebs Cycle (Citric Acid Cycle) in the mitochondrial matrix (produces 2 ATP, 6 NADH, 2 FADH2), and 3) Oxidative Phosphorylation / Electron Transport Chain on the inner mitochondrial membrane (produces ~28-32 ATP via ATP synthase). Oxygen acts as the final electron acceptor."
        ),
        SampleNote(
            title = "Physics: Newton's Laws",
            text = "Newton's Three Laws of Motion govern classical mechanics: 1) First Law (Inertia): An object at rest stays at rest, and an object in motion continues at constant velocity unless acted on by a net external force. 2) Second Law: Force equals mass times acceleration (F = ma). Net force directly accelerates an object in the direction of the force. 3) Third Law (Action-Reaction): For every action, there is an equal and opposite reaction. Forces always occur in matched interaction pairs acting on two distinct bodies."
        ),
        SampleNote(
            title = "History: Industrial Revolution",
            text = "The Industrial Revolution began in Britain during the mid-18th century (around 1760-1840). Key drivers included the mechanization of textiles via the spinning jenny and power loom, the invention of James Watt's improved steam engine, and abundant domestic coal and iron reserves. It caused rapid urbanization, the rise of factory wage labor, expansion of global trade, and profound social transformations including new labor laws and economic philosophies."
        )
    )

    fun onNotesChanged(newText: String) {
        _notesInput.value = newText
    }

    fun onOptionSelected(option: StudyOption) {
        if (option.isPremium && !_isProUnlocked.value) {
            _showPremiumDialog.value = true
        } else {
            _selectedOption.value = option
        }
    }

    fun openPremiumDialog() {
        _showPremiumDialog.value = true
    }

    fun dismissPremiumDialog() {
        _showPremiumDialog.value = false
    }

    fun unlockPro() {
        _isProUnlocked.value = true
        _selectedOption.value = StudyOption.GENERATE_QUIZ
        _showPremiumDialog.value = false
    }

    fun loadSample(note: SampleNote) {
        _notesInput.value = note.text
    }

    fun clearInput() {
        _notesInput.value = ""
    }

    fun clearResult() {
        _uiState.value = StudyUiState.Idle
    }

    fun generate() {
        val option = _selectedOption.value
        if (option.isPremium && !_isProUnlocked.value) {
            _showPremiumDialog.value = true
            return
        }

        val currentNotes = _notesInput.value.trim()
        if (currentNotes.isBlank()) {
            _uiState.value = StudyUiState.Error("Please enter or paste your study notes first.")
            return
        }

        _uiState.value = StudyUiState.Loading

        viewModelScope.launch {
            val result = repository.processNotes(currentNotes, option)
            result.fold(
                onSuccess = { text ->
                    _uiState.value = StudyUiState.Success(content = text, option = option)
                },
                onFailure = { error ->
                    _uiState.value = StudyUiState.Error(
                        message = error.message ?: "Failed to generate response. Please try again."
                    )
                }
            )
        }
    }
}
