package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Determines if the current API key is a valid key (not placeholder).
     */
    fun isKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotEmpty() && !key.contains("MY_GEMINI_API_KEY") && !key.contains("placeholder")
    }

    /**
     * Sends custom prompt directly to Gemini, returning response text.
     */
    suspend fun generateContent(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        if (!isKeyConfigured()) {
            Log.w(TAG, "API Key is placeholder. Simulating real response...")
            return@withContext getSimulatedResponseForPrompt(prompt)
        }

        val apiKey = BuildConfig.GEMINI_API_KEY
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"

        try {
            // Build the contents structure
            val partsObj = JSONObject().put("text", prompt)
            val contentObj = JSONObject().put("parts", JSONArray().put(partsObj))
            val contentsArr = JSONArray().put(contentObj)

            val payload = JSONObject().put("contents", contentsArr)

            // Add system instruction if specified
            if (systemInstruction != null) {
                val sysPart = JSONObject().put("text", systemInstruction)
                val sysContent = JSONObject().put("parts", JSONArray().put(sysPart))
                payload.put("systemInstruction", sysContent)
            }

            // Adjust generation config to prevent excessive length and assure good structure
            val config = JSONObject().put("temperature", 0.7)
            payload.put("generationConfig", config)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = payload.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val root = JSONObject(responseBody)
                    val candidates = root.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        if (content != null) {
                            val parts = content.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                return@withContext parts.getJSONObject(0).optString("text", "No response text found.")
                            }
                        }
                    }
                    return@withContext "No response candidate was returned from the AI model."
                } else {
                    val errMsg = "HTTP ${response.code}: ${response.message}\n${responseBody ?: ""}"
                    Log.e(TAG, "Gemini API error: $errMsg")
                    return@withContext "I encountered an API error ($errMsg).\n\n*Please ensure that your Gemini API key in AI Studio Secrets matches a valid, active API key.*"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API Call failed", e)
            return@withContext "Network Error: Could not connect to Gemini. ${e.localizedMessage}\n\n*Note: Running in offline simulation mode."
        }
    }

    /**
     * Generates extremely detailed, tailored, and helpful simulated evaluations
     * if the user lacks an actual active API key, preserving a stellar UX.
     */
    private fun getSimulatedResponseForPrompt(prompt: String): String {
        // Simple NLP dispatch against the prompt characters
        return when {
            prompt.contains("Emma", ignoreCase = true) -> {
                """
                ### 🛡️ Safety Assessment: **MEDIUM RISK**
                
                **Analysis Rationale**:
                While Emma is generally accessing positive content (like foreign language learning on Duolingo or reading educational entries on Wikipedia), her recent activity contains two critical red flags. First, she attempted to search for techniques to bypass parental filtering. Second, her search history includes a query: *"how to feel better when lonely and sad"*, which is a strong potential indicator of emotional vulnerability or micro-depressive symptoms that warrants healthy parental connection.
                
                ---
                
                ### 🌟 Positive Highlights
                - **Academic Curiosity**: Deeply engaged in language acquisition (Spanish Lesson 3 on Duolingo). 
                - **General Knowledge**: Researched scientific or factual text on Wikipedia (`wikipedia.org/wiki/List_of_dogs`).
                
                ---
                
                ### 💬 Practical Safety Advice for Parents
                1. **Addressing Sadness/Loneliness**:
                   Do not bring up the search logs directly, as this breaks trust. Instead, create a warm environment during a normal shared activity: *"Hey Emma, I've had some quiet moments lately and was thinking about how easy it is to feel a bit isolated these days. How has your week been? Is there anything on your mind?"*
                2. **Bypassing Filters**:
                   Ensure she knows the filters are to block adult web elements, not to punish her. Ask what she felt restrained by: *"If any safe sites you need are blocked, just tell me! I want you to have the freedom to study whatever you want, safely."*
                   
                ⚠️ *Note: Running in simulation mode. To enable real live Gemini 3.5 Flash calls, update the secrets in Google AI Studio.*
                """.trimIndent()
            }
            prompt.contains("Leo", ignoreCase = true) -> {
                """
                ### 🛡️ Safety Assessment: **LOW RISK**
                
                **Analysis Rationale**:
                Leo is demonstrating highly age-appropriate browsing behaviors. His activity is primarily centered around gaming tutorials, specifically *Minecraft*. The only moderate concern is a launch block on *Brawl Stars*, which was restricted by parental lock. There are zero signs of bypass attempts, cyberbullying, or access to sensitive age-inappropriate search queries.
                
                ---
                
                ### 🌟 Positive Highlights
                - **Problem Solving**: Researched Minecraft wolf taming instructions. Gaming guides encourage reading compression and step-by-step logic execution!
                - **Wholesome Entertainment**: Searched for funny, lighthearted animal media content.
                
                ---
                
                ### 💬 Practical Safety Advice for Parents
                1. **Engage with His Gaming Interests**:
                   Offer some joint play time: *"I saw you were looking up Minecraft tips! Could you show me how you tame a wolf? I'd love to learn from your world."*
                2. **Consolidate Restrictive Rules**:
                   Confirm why Brawl Stars remains capped: *"I noticed you wanted to play Brawl Stars. Let's make a deal: once we complete our reading chores, we can review if we can unlock a safe multiplayer game together."*

                ⚠️ *Note: Running in simulation mode. To enable real live Gemini 3.5 Flash calls, update the secrets in Google AI Studio.*
                """.trimIndent()
            }
            else -> {
                """
                ### 🛡️ Family Safety Coach Summary
                
                **Active Profile Profile**: General safety diagnostics.
                
                Overall, the registered profiles show a balanced usage schema. Positive educational activities are observed alongside minor restive actions. 
                
                - **Action Plan**:
                  Review screen downtime allocations on standard gaming apps.
                  Confirm bedtime scheduling is strictly applied across active tablets.
                  Initiate a weekly physical checkout where devices are charged in a central family area overnight.
                
                How can I assist you with specific parenting tips, rules reviews, or security configurations today?
                
                ⚠️ *Note: Running in simulation mode. To enable real live Gemini 3.5 Flash calls, update the secrets in Google AI Studio.*
                """.trimIndent()
            }
        }
    }
}
