package com.erickguaillas.devtrack.data.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Estimador de calorías con visión de Groq (modelo Llama 4 Scout multimodal).
 * La API key se lee de BuildConfig.GROQ_API_KEY (defínela en local.properties).
 */
object VisionAnalyzer {

    data class CalorieResult(
        val ok: Boolean,
        val text: String
    )

    suspend fun analyze(bitmap: Bitmap, apiKey: String): CalorieResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext CalorieResult(false, "🔑 Falta la API key de Groq. Consíguela gratis en console.groq.com y ponla en local.properties: GROQ_API_KEY=gsk_...")
        }
        try {
            val b64 = bitmapToBase64(bitmap)

            val body = """
            {
              "model": "meta-llama/llama-4-scout-17b-16e-instruct",
              "max_tokens": 220,
              "messages": [{
                "role": "user",
                "content": [
                  {"type": "text", "text": "Eres nutricionista. Mira la foto de esta comida y responde SOLO así, en español, breve:\n🍽️ Qué ves: ...\n🔥 Calorías estimadas: ... kcal (rango)\n💪 Proteína: ~g · 🥑 Grasas: ~g · 🍞 Carbs: ~g\n✅ Tip de 1 línea. Si no es comida, dilo."},
                  {"type": "image_url", "image_url": {"url": "data:image/jpeg;base64,$b64"}}
                ]
              }]
            }
            """.trimIndent()

            val conn = (URL("https://api.groq.com/openai/v1/chat/completions").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $apiKey")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 30000
            }
            conn.outputStream.use { it.write(body.toByteArray()) }

            if (conn.responseCode !in 200..299) {
                val err = conn.errorStream?.bufferedReader()?.readText() ?: "Error ${conn.responseCode}"
                return@withContext CalorieResult(false, "⚠️ La IA no respondió ($err). Revisa tu API key.")
            }

            val json = conn.inputStream.bufferedReader().readText()
            // Extraer choices[0].message.content sin librerías pesadas
            val idx = json.indexOf("\"content\":")
            if (idx == -1) return@withContext CalorieResult(false, "⚠️ Respuesta inesperada de la IA.")
            val start = json.indexOf('"', idx + 10) + 1
            val end = findJsonStringEnd(json, start)
            val content = json.substring(start, end)
                .replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\")
            CalorieResult(true, content)
        } catch (e: Exception) {
            CalorieResult(false, "⚠️ Sin conexión o error: ${e.message}")
        }
    }

    private fun bitmapToBase64(src: Bitmap): String {
        val max = 720
        val scale = max.toFloat() / maxOf(src.width, src.height)
        val bmp = if (scale < 1f) Bitmap.createScaledBitmap(
            src, (src.width * scale).toInt(), (src.height * scale).toInt(), true
        ) else src
        val out = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, out)
        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }

    private fun findJsonStringEnd(s: String, from: Int): Int {
        var i = from
        while (i < s.length) {
            if (s[i] == '\\') { i += 2; continue }
            if (s[i] == '"') return i
            i++
        }
        return s.length
    }
}
