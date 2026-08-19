package com.example

import com.example.data.StudyOption
import com.example.ui.StudyNotesViewModel
import com.example.ui.StudyUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StudyNotesViewModelTest {

    private lateinit var viewModel: StudyNotesViewModel

    @Before
    fun setup() {
        viewModel = StudyNotesViewModel()
    }

    @Test
    fun initialState_isIdleWithDefaultOption() {
        assertEquals("", viewModel.notesInput.value)
        assertEquals(StudyOption.SUMMARIZE, viewModel.selectedOption.value)
        assertFalse(viewModel.isProUnlocked.value)
        assertFalse(viewModel.showPremiumDialog.value)
        assertTrue(viewModel.uiState.value is StudyUiState.Idle)
    }

    @Test
    fun changingNotes_updatesNotesInput() {
        val sample = "Photosynthesis creates glucose and oxygen."
        viewModel.onNotesChanged(sample)
        assertEquals(sample, viewModel.notesInput.value)
    }

    @Test
    fun selectingFreeOption_updatesSelectedOption() {
        viewModel.onOptionSelected(StudyOption.EXPLAIN_CONCEPT)
        assertEquals(StudyOption.EXPLAIN_CONCEPT, viewModel.selectedOption.value)
    }

    @Test
    fun selectingMcqWhenLocked_opensPremiumDialog() {
        viewModel.onOptionSelected(StudyOption.GENERATE_QUIZ)
        assertTrue(viewModel.showPremiumDialog.value)
    }

    @Test
    fun unlockingPro_enablesMcqOptionAndDismissesDialog() {
        viewModel.unlockPro()
        assertTrue(viewModel.isProUnlocked.value)
        assertFalse(viewModel.showPremiumDialog.value)
        assertEquals(StudyOption.GENERATE_QUIZ, viewModel.selectedOption.value)
    }

    @Test
    fun loadingSample_populatesNotesInput() {
        val sample = viewModel.sampleNotes.first()
        viewModel.loadSample(sample)
        assertEquals(sample.text, viewModel.notesInput.value)
    }

    @Test
    fun clearingInput_resetsNotesText() {
        viewModel.onNotesChanged("Some notes")
        viewModel.clearInput()
        assertEquals("", viewModel.notesInput.value)
    }

    @Test
    fun generateWithEmptyInput_showsErrorMessage() {
        viewModel.clearInput()
        viewModel.generate()
        assertTrue(viewModel.uiState.value is StudyUiState.Error)
        val errorState = viewModel.uiState.value as StudyUiState.Error
        assertTrue(errorState.message.contains("Please enter or paste your study notes"))
    }
}
