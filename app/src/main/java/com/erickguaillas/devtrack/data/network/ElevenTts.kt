package com.erickguaillas.devtrack.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Voz premium (estilo JARVIS) con ElevenLabs.
 * Devuelve el audio MP3 en bytes. Si falla o no hay clave → null y se usa el TTS del sistema.
 */
object ElevenTts {

    // Modelo multilingüe (habla español con acento natural). Alternativas: eleven_turbo_v2_5 (más rápido/barato), eleven_flash_v2_5 (el más rápido)
    private const val MODEL = "eleven_multilingual_v2"

    suspend fun synthesize(text: String, apiKey: String, voiceId: String): ByteArray? =
        withContext(Dispatchers.IO) {
            if (apiKey.isBlank() || voiceId.isBlank()) return@withContext null
            try {
                val conn = (URL("https://api.elevenlabs.io/v1/text-to-speech/$voiceId")
                    .openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("xi-api-key", apiKey)
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "audio/mpeg")
                    doOutput = true
                    connectTimeout = 15000
                    readTimeout = 60000
                }

                val body = """
                {
                  "text": ${jsonString(text)},
                  "model_id": "$MODEL",
                  "voice_settings": { "stability": 0.45, "similarity_boost": 0.8, "style": 0.15, "use_speaker_boost": true }
                }
                """.trimIndent()

                conn.outputStream.use { it.write(body.toByteArray()) }
                if (conn.responseCode !in 200..299) return@withContext null
                conn.inputStream.readBytes()
            } catch (e: Exception) {
                null
            }
        }

    private fun jsonString(s: String): String = "\"" + s
        .replace("\\", "\\\\").replace("\"", "\\\"")
        .replace("\n", "\\n").replace("\r", "").replace("\t", " ") + "\""
}
