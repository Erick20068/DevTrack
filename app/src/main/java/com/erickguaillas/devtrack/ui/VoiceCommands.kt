package com.erickguaillas.devtrack.ui

import com.erickguaillas.devtrack.R
import java.text.Normalizer

/** Intérprete de comandos de voz en español (sin IA, funciona offline) */
object VoiceCommands {

    sealed class Command {
        data class AddTask(val text: String) : Command()
        data class AddJournal(val text: String, val category: String) : Command()
        data class Navigate(val destId: Int, val destName: String) : Command()
        object ReadTasks : Command()
        object Motivate : Command()
        object Spotify : Command()
        object Unknown : Command()
    }

    fun parse(raw: String): Command {
        val t = normalize(raw)

        return when {
            // Agregar tarea
            t.startsWith("agregar tarea") || t.startsWith("agrega tarea") ||
            t.startsWith("nueva tarea") || t.startsWith("anade tarea") || t.startsWith("añade tarea") ->
                Command.AddTask(raw.substringAfter("tarea", "").trim().ifBlank { raw })

            // Bitácora por verbos
            t.startsWith("postul") -> Command.AddJournal(raw, "TRABAJO")
            t.startsWith("aplique") || t.startsWith("aplique a") -> Command.AddJournal(raw, "TRABAJO")
            t.startsWith("estudi") && (t.contains("ingles")) -> Command.AddJournal(raw, "INGLES")
            t.startsWith("estudi") -> Command.AddJournal(raw, "INGLES")
            t.startsWith("estuve program") || t.startsWith("programe") || t.startsWith("codee") ->
                Command.AddJournal(raw, "CODIGO")
            t.startsWith("entren") && !t.startsWith("abre") && !t.startsWith("abrir") ->
                Command.AddJournal(raw, "CUERPO")

            // Navegación
            t.contains("repaso") || t.contains("aprender") || t.contains("flashcard") || t.contains("quiz") ->
                Command.Navigate(R.id.nav_aprender, "Repaso")
            t.contains("entrena") || t.contains("ejercicio") || t.contains("rutina") || t.contains("caloria") ->
                Command.Navigate(R.id.nav_entrena, "Entrena")
            t.contains("bitacora") || t.contains("registro") || t.contains("diario") ->
                Command.Navigate(R.id.nav_bitacora, "Bitácora")
            t.contains("meta") || t.contains("objetivo") ->
                Command.Navigate(R.id.nav_metas, "Metas")
            t.startsWith("abre") || t.startsWith("abrir") && t.contains("tarea") ->
                Command.Navigate(R.id.nav_today, "Hoy")

            // Lectura de tareas
            t.contains("que tengo hoy") || t.contains("mis tareas") || t.contains("tareas pendientes") ->
                Command.ReadTasks

            // Motivación
            t.contains("motiva") || t.contains("animo") || t.contains("frase") || t.contains("desanimado") ||
            t.contains("triste") || t.contains("cansado") ->
                Command.Motivate

            // Música
            t.contains("musica") || t.contains("spotify") || t.contains("cancion") || t.contains("pon algo") ->
                Command.Spotify

            else -> Command.Unknown
        }
    }

    private fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "") // quita tildes
            .trim()
}
