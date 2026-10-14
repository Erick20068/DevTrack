package com.erickguaillas.devtrack.ui.asistente

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.erickguaillas.devtrack.R
import com.erickguaillas.devtrack.data.DevRepository
import com.erickguaillas.devtrack.data.Wellness
import com.erickguaillas.devtrack.databinding.FragmentAsistenteBinding
import com.erickguaillas.devtrack.ui.VoiceCommands
import kotlinx.coroutines.launch
import java.util.Locale

class AsistenteFragment : Fragment(R.layout.fragment_asistente) {

    private var _b: FragmentAsistenteBinding? = null
    private val b get() = _b!!
    private lateinit var repo: DevRepository
    private var tts: TextToSpeech? = null
    private var recognizer: SpeechRecognizer? = null

    private val micPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) startListening() else speak("Necesito el micrófono para escucharte, Erick.") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentAsistenteBinding.bind(view)
        repo = DevRepository(requireContext())

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
                tts?.setPitch(1.05f)
            }
        }

        b.btnMic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) startListening()
            else micPermission.launch(Manifest.permission.RECORD_AUDIO)
        }

        b.btnMotivate.setOnClickListener { speak(Wellness.randomMotivacion()) }
        b.btnSpotify.setOnClickListener { openSpotify() }
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
                        handle(heard)
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
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-EC")
            })
        }
    }

    // ─── Ejecutar comando ───
    private fun handle(heard: String) {
        when (val cmd = VoiceCommands.parse(heard)) {
            is VoiceCommands.Command.AddTask -> {
                lifecycleScope.launch { repo.addTask(cmd.text) }
                speak("Listo, tarea agregada: ${cmd.text}")
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
            VoiceCommands.Command.Motivate -> speak(Wellness.randomMotivacion())
            VoiceCommands.Command.Spotify -> {
                speak("Abriendo Spotify. A romperla, Erick.")
                openSpotify()
            }
            VoiceCommands.Command.Unknown -> speak(
                "No entendí ese comando. Prueba con: agregar tarea, postulé a, estudié inglés, entrené, abre repaso, motívame, o pon música."
            )
        }
    }

    // ─── Spotify ───
    private fun openSpotify() {
        val query = "workout motivation"
        try {
            // App de Spotify instalada
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("spotify:search:$query")).apply {
                setPackage("com.spotify.android")
            })
        } catch (e: Exception) {
            // Sin app o no respondió → web
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/workout%20motivation")))
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(context, "Instala Spotify o abre tu app de música", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun speak(text: String) {
        b.tvHeard.text = text
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "devtrack_tts")
    }

    override fun onDestroyView() {
        recognizer?.destroy()
        tts?.stop(); tts?.shutdown()
        super.onDestroyView()
        _b = null
    }
}
