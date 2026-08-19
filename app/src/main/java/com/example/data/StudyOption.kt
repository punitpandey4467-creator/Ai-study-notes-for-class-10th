package com.example.data

enum class StudyOption(
    val title: String,
    val subtitle: String,
    val isPremium: Boolean,
    val priceText: String,
    val promptInstruction: String
) {
    SUMMARIZE(
        title = "Summarize",
        subtitle = "Key takeaways & outline",
        isPremium = false,
        priceText = "Free",
        promptInstruction = "Provide a structured, easy-to-read summary of these study notes. Format with clear headings: \n- 📌 Executive Summary (2-3 concise sentences)\n- 🔑 Key Bullet Points & Core Takeaways\n- 💡 Important Definitions or Formulas (if any)\n\nNotes:\n"
    ),
    EXPLAIN_CONCEPT(
        title = "Explain Concept",
        subtitle = "Intuitive breakdown & analogy",
        isPremium = false,
        priceText = "Free",
        promptInstruction = "Explain the fundamental concepts in these study notes in simple, crystal-clear terms suitable for high comprehension. Structure the response with: \n- 🧠 Core Concept Explained (Simple terms)\n- 🔍 Real-World Analogy (An intuitive everyday metaphor)\n- 🪜 Step-by-Step Breakdown\n- ⚠️ Common Misconceptions or Pitfalls\n\nNotes:\n"
    ),
    GENERATE_QUIZ(
        title = "MCQ Quiz",
        subtitle = "MCQ & Practice • ₹200",
        isPremium = true,
        priceText = "₹200",
        promptInstruction = "Create a high-yield study quiz based directly on these notes to test active recall. Include: \n- 📝 3 Multiple-Choice Questions (MCQs) (with options A, B, C, D and explicitly clearly indicated correct answers with brief 1-line rationales)\n- 🎯 2 Short-Answer / Conceptual Questions with model answers for self-testing\n\nNotes:\n"
    )
}

