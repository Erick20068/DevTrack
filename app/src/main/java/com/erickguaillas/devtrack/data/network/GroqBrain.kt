package com.erickguaillas.devtrack.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cerebro del asistente: Groq (Llama 3.3 70B) conoce TODA la app
 * y decide la acción a ejecutar respondiendo un JSON simple.
 * Si no hay clave o falla la red, se usa el parser local (offline) como respaldo.
 */
object GroqBrain {

    sealed class Action {
        data class AddTask(val text: String) : Action()
        data class AddJournal(val text: String, val category: String) : Action()
        data class Navigate(val dest: String) : Action()
        object ReadTasks : Action()
        object Motivate : Action()
        data class SpotifySearch(val query: String) : Action()
        object SpotifyNext : Action()
        object SpotifyPrev : Action()
        object SpotifyPlayPause : Action()
        data class Reply(val text: String) : Action()
    }

    private fun systemPrompt(context: String) = """
Eres "Dev", el asistente de voz de DevTrack, la app de Erick Guaillas (desarrollador junior de Cuenca, Ecuador; stack Java/Spring Boot/Kotlin/Android; inglés A2 camino a B2; 20 años; busca su primer empleo dev; su meta financiera es ingresos estables y ahorro).

LA APP TIENE ESTAS SECCIONES:
- hoy: tareas del día con progreso y acceso a la bitácora
- aprender: 3 modos → flashcards (16 barajas: lenguajes, APIs/HTTP, Docker, redes, algoritmos, entrevistas, inglés A1-C1, phrasal verbs), quizzes con puntaje, y PRÁCTICA con problemas reales de código (Git, APIs, Java/Kotlin, SQL, Python/JS) y ejercicios de inglés (escritura, lectura, pronunciación)
- entrena: checklist de ejercicios del día (se marcan como tareas), checklist de cuidado corporal, y escáner de calorías por foto con visión IA
- bitacora: registro por categorías (TRABAJO, INGLES, CODIGO, APPS, CUERPO) con filtros y estadísticas
- metas: objetivos con barra de progreso
- asistente: tú

CONTEXTO ACTUAL DE ERICK:
$context

TU ROL ADICIONAL: mentor financiero básico. Conoces: regla 50/30/20, fondo de emergencia (3-6 meses), pagarse primero, interés compuesto, deuda buena vs mala, tarjetas de crédito (pagar el total), dolarización de Ecuador, ingresos freelance e impuestos/SRI, y evitas SIEMPRE estafas (nada de rendimientos garantizados, trading con dinero prestado o cripto milagrosa). Das pasos concretos y realistas para su etapa actual (ingreso bajo, primer empleo). Nunca prometes rendimientos.

REGLAS:
1. Responde SOLO un JSON en UNA línea, sin markdown, sin comillas dobles dentro de los textos.
2. Elige UNA acción:
   {"action":"add_task","text":"..."} — crear tarea nueva
   {"action":"add_journal","text":"...","category":"TRABAJO|INGLES|CODIGO|APPS|CUERPO"} — registrar actividad hecha
   {"action":"navigate","dest":"hoy|aprender|entrena|bitacora|metas|asistente"} — abrir sección
   {"action":"read_tasks"} — cuando pregunte qué debe hacer hoy
   {"action":"motivate"} — pidió motivación, ánimo o está desanimado
   {"action":"spotify_search","query":"artista o canción"} — quiere escuchar música específica (si dice pon + algo, usa eso)
   {"action":"spotify_next"} / {"action":"spotify_prev"} / {"action":"spotify_playpause"} — control de reproducción
   {"action":"reply","text":"..."} — responder cualquier otra cosa: dudas de la app, programación, inglés, estudio, DINERO y finanzas, ánimo. Máximo 45 palabras, tono cercano y directo, español, puedes llamarlo Erick. Si habla de dinero, da un paso concreto y realista.
3. Nunca inventes funciones que la app no tiene.
""".trimIndent()

    suspend fun think(userText: String, context: String, apiKey: String): Action? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null
        try {
            val body = """
            {
              "model": "llama-3.3-70b-versatile",
              "max_tokens": 160,
              "temperature": 0.5,
              "messages": [
                {"role": "system", "content": ${jsonString(systemPrompt(context))}},
                {"role": "user", "content": ${jsonString(userText)}}
              ]
            }
            """.trimIndent()

            val conn = (URL("https://api.groq.com/openai/v1/chat/completions").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $apiKey")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 12000
                readTimeout = 20000
            }
            conn.outputStream.use { it.write(body.toByteArray()) }
            if (conn.responseCode !in 200..299) return@withContext null

            val json = conn.inputStream.bufferedReader().readText()
            val content = extractContent(json) ?: return@withContext null
            parseAction(content)
        } catch (e: Exception) {
            null
        }
    }

    // ── parseo tolerante del JSON que devuelve el modelo ──
    private fun parseAction(content: String): Action {
        val c = if ('{' in content && '}' in content)
            "{${content.substringAfter('{').substringBeforeLast('}')}}"
        else content
        val action = Regex("\"action\"\\s*:\\s*\"([a-z_]+)\"").find(c)?.groupValues?.get(1) ?: return Action.Reply(content.take(180))
        val text = Regex("\"text\"\\s*:\\s*\"([^\"]*)\"").find(c)?.groupValues?.get(1)
        val category = Regex("\"category\"\\s*:\\s*\"([A-Z]+)\"").find(c)?.groupValues?.get(1) ?: "CODIGO"
        val dest = Regex("\"dest\"\\s*:\\s*\"([a-z]+)\"").find(c)?.groupValues?.get(1) ?: "hoy"
        val query = Regex("\"query\"\\s*:\\s*\"([^\"]*)\"").find(c)?.groupValues?.get(1) ?: ""

        return when (action) {
            "add_task" -> Action.AddTask(text ?: "")
            "add_journal" -> Action.AddJournal(text ?: "", category)
            "navigate" -> Action.Navigate(dest)
            "read_tasks" -> Action.ReadTasks
            "motivate" -> Action.Motivate
            "spotify_search" -> Action.SpotifySearch(if (query.isBlank()) "workout" else query)
            "spotify_next" -> Action.SpotifyNext
            "spotify_prev" -> Action.SpotifyPrev
            "spotify_playpause" -> Action.SpotifyPlayPause
            else -> Action.Reply(text ?: content.take(180))
        }
    }

    private fun jsonString(s: String): String = "\"" + s
        .replace("\\", "\\\\").replace("\"", "\\\"")
        .replace("\n", "\\n").replace("\t", " ") + "\""

    private fun extractContent(json: String): String? {
        val idx = json.indexOf("\"content\":")
        if (idx == -1) return null
        val start = json.indexOf('"', idx + 10) + 1
        var i = start
        while (i < json.length) {
            if (json[i] == '\\') { i += 2; continue }
            if (json[i] == '"') break
            i++
        }
        if (i >= json.length) return null
        return json.substring(start, i)
            .replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\")
    }
}
