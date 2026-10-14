package com.erickguaillas.devtrack.ui.asistente

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.erickguaillas.devtrack.BuildConfig
import com.erickguaillas.devtrack.R
import com.erickguaillas.devtrack.data.DevRepository
import com.erickguaillas.devtrack.data.Wellness
import com.erickguaillas.devtrack.data.network.ElevenTts
import com.erickguaillas.devtrack.data.network.GroqBrain
import com.erickguaillas.devtrack.databinding.FragmentAsistenteBinding
import com.erickguaillas.devtrack.ui.SpotifyMedia
import com.erickguaillas.devtrack.ui.VoiceCommands
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class AsistenteFragment : Fragment(R.layout.fragment_asistente) {

    private var _b: FragmentAsistenteBinding? = null
    private val b get() = _b!!
    private lateinit var repo: DevRepository
    private var tts: TextToSpeech? = null
    private var recognizer: SpeechRecognizer? = null
    private var mediaPlayer: MediaPlayer? = null
    private val voiceCache: File by lazy { File(requireContext().cacheDir, "devtrack_voice.mp3") }

    private val micPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) startListening() else speak("Necesito el micrófono para escucharte, Erick.") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentAsistenteBinding.bind(view)
        repo = DevRepository(requireContext())

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.let { configureVoice(it) }
        }

        b.btnMic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) startListening()
            else micPermission.launch(Manifest.permission.RECORD_AUDIO)
        }

        b.btnMotivate.setOnClickListener { motivate() }
        b.btnSpotify.setOnClickListener { openSpotify("workout motivation") }

        b.tvVoiceBadge.text = if (BuildConfig.ELEVENLABS_API_KEY.isBlank())
            "🎙️ Voz del sistema activa · configura ELEVENLABS_API_KEY para la voz JARVIS"
        else
            "🎙️ Voz JARVIS activada (ElevenLabs) · respaldo automático al sistema"

        // Controles de reproducción ⏮ ⏯ ⏭
        b.btnNext.setOnClickListener { mediaWithPermission { SpotifyMedia.next(requireContext()) } }
        b.btnPlayPause.setOnClickListener { mediaWithPermission { SpotifyMedia.playPause(requireContext()) } }
        b.btnPrev.setOnClickListener { mediaWithPermission { SpotifyMedia.prev(requireContext()) } }
    }

    // ─── Escucha ───
    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            Toast.makeText(context, "Reconocimiento de voz no disponible en este dispositivo", Toast.LENGTH_LONG).show()
            return
        }
        b.tvStatus.text = "🎤 Escuchando..."
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(requireContext()).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle) {
                    val heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    b.tvStatus.text = "🎙️ Toca el micrófono y habla"
                    if (heard != null) {
                        b.tvHeard.text = "Entendí: \"$heard\""
                        think(heard)
                    }
                }
                override fun onError(error: Int) {
                    b.tvStatus.text = "🎙️ Toca el micrófono y habla"
                    b.tvHeard.text = "No te escuché bien ($error). Intenta de nuevo."
                }
                override fun onReadyForSpeech(p: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(v: Float) {}
                override fun onBufferReceived(b: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(p: Bundle?) {}
                override fun onEvent(t: Int, p: Bundle?) {}
            })
            startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-EC")
            })
        }
    }

    /** Primero Groq (entiende todo); si falla/sin clave → parser local offline */
    private fun think(heard: String) {
        if (BuildConfig.GROQ_API_KEY.isNotBlank()) {
            b.tvStatus.text = "🤖 Pensando..."
            lifecycleScope.launch {
                val context = buildString {
                    append(repo.tasksSummary()).append(". ").append(repo.goalsSummary())
                    append(". Ejercicios en Entrena: ").append(repo.habitsSummary("EXERCISE"))
                    append(". Hábitos de cuidado: ").append(repo.habitsSummary("CARE"))
                    append(". Bitácora reciente: ").append(repo.weekSummaryText())
                }
                val action = GroqBrain.think(heard, context, BuildConfig.GROQ_API_KEY)
                b.tvStatus.text = "🎙️ Toca el micrófono y habla"
                if (action != null) execute(action) else handleLocal(heard)
            }
        } else {
            handleLocal(heard)
        }
    }

    private fun execute(a: GroqBrain.Action) {
        when (a) {
            is GroqBrain.Action.AddTask -> {
                lifecycleScope.launch { repo.addTask(a.text) }
                speak("Listo, tarea agregada: ${a.text}")
            }
            is GroqBrain.Action.AddHabit -> {
                lifecycleScope.launch { repo.addHabit(a.text, a.type) }
                speak(if (a.type == "CARE") "Hábito de cuidado agregado: ${a.text}" else "Ejercicio agregado a Entrena: ${a.text}")
            }
            is GroqBrain.Action.AddJournal -> {
                lifecycleScope.launch { repo.addJournal(a.text, a.category) }
                speak("Registrado en tu bitácora. Buen trabajo.")
            }
            is GroqBrain.Action.Navigate -> {
                val dest = when (a.dest) {
                    "aprender" -> R.id.nav_aprender to "Aprender"
                    "entrena" -> R.id.nav_entrena to "Entrena"
                    "bitacora" -> R.id.nav_bitacora to "Bitácora"
                    "metas" -> R.id.nav_metas to "Metas"
                    "asistente" -> R.id.nav_asistente to "Asistente"
                    else -> R.id.nav_today to "Hoy"
                }
                speak("Abriendo ${dest.second}")
                try { findNavController().navigate(dest.first) } catch (_: Exception) {}
            }
            GroqBrain.Action.ReadTasks -> lifecycleScope.launch {
                speak(repo.pendingTaskTitles())
            }
            GroqBrain.Action.Motivate -> motivate()
            is GroqBrain.Action.SpotifySearch -> {
                speak("Abriendo ${a.query} en Spotify.")
                openSpotify(a.query)
            }
            GroqBrain.Action.SpotifyNext -> mediaWithPermission {
                if (SpotifyMedia.next(requireContext())) speak("Siguiente canción.")
                else speak("No encontré Spotify sonando. Pon play primero.")
            }
            GroqBrain.Action.SpotifyPrev -> mediaWithPermission {
                if (SpotifyMedia.prev(requireContext())) speak("Volvemos a la anterior.")
                else speak("No encontré Spotify sonando.")
            }
            GroqBrain.Action.SpotifyPlayPause -> mediaWithPermission {
                if (SpotifyMedia.playPause(requireContext())) speak("Hecho.")
                else speak("No encontré Spotify sonando.")
            }
            is GroqBrain.Action.Reply -> speak(a.text)
        }
    }

    // ─── Respaldo offline (sin internet / sin clave) ───
    private fun handleLocal(heard: String) {
        when (val cmd = VoiceCommands.parse(heard)) {
            is VoiceCommands.Command.AddTask -> {
                lifecycleScope.launch { repo.addTask(cmd.text) }
                speak("Listo, tarea agregada: ${cmd.text}")
            }
            is VoiceCommands.Command.AddHabit -> {
                lifecycleScope.launch { repo.addHabit(cmd.text, cmd.type) }
                speak(if (cmd.type == "CARE") "Hábito agregado: ${cmd.text}" else "Ejercicio agregado a Entrena: ${cmd.text}")
            }
            is VoiceCommands.Command.AddJournal -> {
                lifecycleScope.launch { repo.addJournal(cmd.text, cmd.category) }
                speak("Registrado en tu bitácora. Buen trabajo.")
            }
            is VoiceCommands.Command.Navigate -> {
                speak("Abriendo ${cmd.destName}")
                try { findNavController().navigate(cmd.destId) } catch (_: Exception) {}
            }
            VoiceCommands.Command.ReadTasks -> lifecycleScope.launch {
                speak(repo.pendingTaskTitles())
            }
            VoiceCommands.Command.Motivate -> motivate()
            is VoiceCommands.Command.SpotifyQuery -> {
                speak("Abriendo ${cmd.query} en Spotify.")
                openSpotify(cmd.query)
            }
            VoiceCommands.Command.SpotifyNext -> mediaWithPermission {
                if (SpotifyMedia.next(requireContext())) speak("Siguiente canción.")
                else speak("No encontré Spotify sonando.")
            }
            VoiceCommands.Command.SpotifyPrev -> mediaWithPermission {
                if (SpotifyMedia.prev(requireContext())) speak("Canción anterior.")
                else speak("No encontré Spotify sonando.")
            }
            VoiceCommands.Command.SpotifyPlayPause -> mediaWithPermission {
                if (SpotifyMedia.playPause(requireContext())) speak("Hecho.")
                else speak("No encontré Spotify sonando.")
            }
            VoiceCommands.Command.Unknown -> speak(
                "No entendí, Erick. Prueba con: agregar tarea, postulé a, estudié inglés, entrené, abre repaso, motívame, pon AC DC, o siguiente canción."
            )
        }
    }

    // ─── Motivación + AC/DC 🔥 ───
    private fun motivate() {
        speak(Wellness.randomMotivacion())
        Handler(Looper.getMainLooper()).postDelayed({ openSpotify("AC/DC", autoPlay = true) }, 2600)
    }

    /** Voz lo más humana/grave posible (estilo Jarvis) con el TTS del sistema */
    private fun configureVoice(t: TextToSpeech) {
        try {
            t.language = Locale("es", "US")
            val spanishVoices = t.voices?.filter { it.locale.language == "es" } ?: emptyList()
            // Heurística: voces masculinas suelen contener "esd", "male" o "-m-"
            val male = spanishVoices.firstOrNull {
                it.name.contains("esd", true) || it.name.contains("male", true) || it.name.contains("m-", true)
            }
            val chosen = male ?: spanishVoices.maxByOrNull { it.quality }
            chosen?.let { t.voice = it }
            t.setPitch(0.88f)      // más grave = más "Jarvis"
            t.setSpeechRate(0.98f) // pausado y claro
        } catch (_: Exception) {
            t.language = Locale("es", "ES")
            t.setPitch(0.9f)
        }
    }

    private fun mediaWithPermission(block: () -> Unit) {
        if (!SpotifyMedia.hasAccess(requireContext())) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permiso de notificaciones 🎵")
                .setMessage("Para que yo controle Spotify (siguiente, pausa, play), activa el acceso a notificaciones para DevTrack. Solo se usa para leer la sesión de música — nada más.")
                .setPositiveButton("Activar") { _, _ ->
                    startActivity(SpotifyMedia.accessIntent())
                }
                .setNegativeButton("Ahora no", null)
                .show()
        } else block()
    }

    private fun openSpotify(query: String, autoPlay: Boolean = true) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("spotify:search:${Uri.encode(query)}")).apply {
                setPackage("com.spotify.android")
            })
        } catch (e: Exception) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/${Uri.encode(query)}")))
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(context, "Instala Spotify o abre tu app de música", Toast.LENGTH_LONG).show()
                return
            }
        }
        // Intento de reproducción automática: espera a que Spotify abra y manda PLAY
        if (autoPlay && SpotifyMedia.hasAccess(requireContext())) {
            Handler(Looper.getMainLooper()).postDelayed({
                SpotifyMedia.play(requireContext())
            }, 4000)
            Handler(Looper.getMainLooper()).postDelayed({
                SpotifyMedia.dispatch(requireContext(), android.view.KeyEvent.KEYCODE_MEDIA_PLAY)
            }, 7000) // segundo intento por si la sesión tarda
        }
    }

    // ─── Voz: ElevenLabs (JARVIS) con respaldo al TTS del sistema ───
    private fun speak(text: String) {
        b.tvHeard.text = text
        val key = BuildConfig.ELEVENLABS_API_KEY
        if (key.isBlank()) {
            systemSpeak(text)
            return
        }
        lifecycleScope.launch {
            val audio = ElevenTts.synthesize(text, key, BuildConfig.ELEVENLABS_VOICE_ID)
            if (audio != null && audio.isNotEmpty()) playAudio(audio) else systemSpeak(text)
        }
    }

    private fun playAudio(bytes: ByteArray) {
        try {
            tts?.stop()
            voiceCache.writeBytes(bytes)
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(voiceCache.absolutePath)
                setOnPreparedListener { it.start() }
                setOnErrorListener { _, _, _ ->
                    systemSpeak("No pude reproducir la voz premium, sigo con la del sistema.")
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            systemSpeak("Error de audio, usando voz del sistema.")
        }
    }

    private fun systemSpeak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "devtrack_tts")
    }

    override fun onDestroyView() {
        recognizer?.destroy()
        mediaPlayer?.release(); mediaPlayer = null
        cacheFileCleanup()
        tts?.stop(); tts?.shutdown()
        super.onDestroyView()
        _b = null
    }

    private fun cacheFileCleanup() {
        try { if (voiceCache.exists()) voiceCache.delete() } catch (_: Exception) {}
    }
}
