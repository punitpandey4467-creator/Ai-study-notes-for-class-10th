package com.example.data

import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GeminiApiClient
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudyNotesRepository {
    suspend fun processNotes(notes: String, option: StudyOption): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please set your GEMINI_API_KEY in the AI Studio Secrets panel.")
            )
        }

        val promptText = "${option.promptInstruction}$notes"
        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = promptText)))
            ),
            systemInstruction = Content(
                parts = listOf(
                    Part(
                        text = "You are an expert AI study tutor and academic assistant. Your objective is to help students learn effectively. Format your output clearly with structured markdown headers, bullet points, and clean spacing."
                    )
                )
            )
        )

        try {
            val response = GeminiApiClient.service.generateContent(apiKey, request)
            val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!candidateText.isNullOrBlank()) {
                Result.success(candidateText.trim())
            } else {
                val errorMsg = response.error?.message ?: "No response received from Gemini API."
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            val friendlyMessage = when {
                e.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                    "Network error. Please check your internet connection and try again."
                e.message?.contains("400", ignoreCase = true) == true || e.message?.contains("API key not valid", ignoreCase = true) == true ->
                    "Invalid API key. Please check your GEMINI_API_KEY in the AI Studio Secrets panel."
                e.message?.contains("429", ignoreCase = true) == true || e.message?.contains("RESOURCE_EXHAUSTED", ignoreCase = true) == true ->
                    "Gemini API rate limit reached. Please wait a moment and try again."
                else -> e.message ?: "An unexpected error occurred while contacting Gemini API."
            }
            Result.failure(Exception(friendlyMessage))
        }
    }
}
