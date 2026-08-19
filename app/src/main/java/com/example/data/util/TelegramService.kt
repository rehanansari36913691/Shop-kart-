package com.example.data.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TelegramService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun sendNotification(botToken: String, chatId: String, messageHtml: String): Boolean {
        if (botToken.isBlank() || chatId.isBlank()) {
            Log.d("TelegramService", "Bot token or Chat ID not configured. Simulated notification logged: $messageHtml")
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.telegram.org/bot$botToken/sendMessage"
                val json = JSONObject().apply {
                    put("chat_id", chatId)
                    put("text", messageHtml)
                    put("parse_mode", "HTML")
                }

                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val success = response.isSuccessful
                    if (!success) {
                        Log.w("TelegramService", "Telegram API returned HTTP ${response.code}: ${response.body?.string()}")
                    }
                    success
                }
            } catch (e: Exception) {
                Log.e("TelegramService", "Failed to dispatch Telegram notification: ${e.message}", e)
                false
            }
        }
    }
}
