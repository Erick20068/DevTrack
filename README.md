# DevTrack 🚀

**Tu bitácora de programador** — app Android nativa en Kotlin para organizar tu crecimiento como desarrollador: tareas diarias, repaso de teoría por lenguaje, registro de actividad, metas y asistente de voz.

Creada por [Erick Guaillas](https://github.com/erickguaillasest-dev) · Cuenca, Ecuador 🇪🇨

## ✨ Funciones

| Módulo | Qué hace |
|---|---|
| 🏠 **Hoy** | Tareas del día: añadir, completar, progreso diario |
| 🃏 **Repaso** | Flashcards por lenguaje (Java, Kotlin, JavaScript, SQL) + baraja de **entrevistas técnicas** |
| 📓 **Bitácora** | Registro con categorías: 💼 Trabajo · 🗣️ Inglés · 💻 Código · 📱 Apps |
| 🎯 **Metas** | Objetivos con barra de progreso |
| 🎙️ **Asistente de voz** | Habla: "agregar tarea...", "postulé a...", "estudié inglés...", "abre repaso", "qué tengo hoy" — con respuesta por voz (TTS) |

## 🛠️ Stack

- Kotlin · XML + Material Components 3
- Room (SQLite) · LiveData · ViewBinding
- Navigation Component (single-activity)
- SpeechRecognizer + TextToSpeech (asistente de voz local, funciona **offline**)

## 🚀 Cómo correrla

1. Abre la carpeta `DevTrack` en **Android Studio**
2. Espera el *Gradle Sync* (primera vez descarga dependencias)
3. Run ▶ en tu dispositivo o emulador (min SDK 26)

## 🗺️ Roadmap

- [x] **v2**: Asistente conectado a Groq IA — responde cualquier pregunta sobre la app, tus tareas y metas (Llama 3.3 70B), además de los comandos locales offline
- [x] **v2**: Control de Spotify por voz — *"siguiente canción"*, *"pausa"*, *"pon AC/DC"* ⏮ ⏯ ⏭
- [x] **v2**: *"Motívame"* = frase motivadora + AC/DC sonando 🔥
- [x] **v3**: Checklists de entrenamiento y cuidado corporal con progreso diario
- [x] **v3**: Modo **Práctica**: problemas reales de código (Git, APIs, Java/Kotlin, SQL, Python/JS) y ejercicios de inglés (escritura, lectura, pronunciación)
- [x] **v3**: +6 barajas (APIs, Docker, Algoritmos, Redes, Phrasal verbs) y +4 quizzes · Bitácora con filtros y estadísticas · Consejo financiero diario + mentor financiero con IA
- [x] **v3**: Voz JARVIS opcional con ElevenLabs + autoplay de Spotify
- [ ] Widget de tareas en pantalla de inicio
- [ ] Sistema de rachas (streaks) con 🔥
- [ ] Más barajas de repaso (Python avanzado, Docker, Kubernetes)
- [ ] Estadísticas semanales en la bitácora
- [ ] Publicación en Play Store

## 🎤 Comandos de voz

```
"agregar tarea repasar java"
"postulé a Anyone AI"          → guarda en Bitácora (💼 Trabajo)
"estudié inglés 30 minutos"    → guarda en Bitácora (🗣️ Inglés)
"estuve programando la app"    → guarda en Bitácora (💻 Código)
"abre repaso" / "abre metas"   → navega a la sección
"qué tengo hoy"                → lee tus tareas pendientes en voz alta
"motívame"                     → frase motivadora + AC/DC en Spotify 🔥
"pon AC/DC" / "pon salsa"      → busca y abre eso en Spotify
"siguiente canción" / "pausa" / "dale play" → controla Spotify* 
```
\* Requiere activar **Acceso a notificaciones** una vez (la app te guía con un diálogo).

## 🧠 Asistente con IA (Groq)

Con una clave gratis de [console.groq.com](https://console.groq.com) en `local.properties`:

```properties
GROQ_API_KEY=gsk_tu_clave
```

El asistente entiende lenguaje natural y **conoce toda la app + tus tareas y metas actuales**. Sin clave, usa el modo offline con comandos fijos.

## 🎙️ Voz JARVIS (ElevenLabs) — opcional

Por defecto usa el **TTS del sistema** (voz grave estilo Jarvis). Para voz humana real:

1. Crea cuenta gratis en [elevenlabs.io](https://elevenlabs.io) → **Profile → API key**
2. Añade a `local.properties`:
```properties
ELEVENLABS_API_KEY=sk_tu_clave
ELEVENLABS_VOICE_ID=onwK4e9ZLuTAKqWW03F9
```
3. Si la clave falla o no hay internet, **cae automáticamente al TTS del sistema** (nunca se queda mudo)

| Voz | ID | Estilo |
|---|---|---|
| Daniel | `onwK4e9ZLuTAKqWW03F9` | Británica grave — **estilo Jarvis** (default) |
| Adam | `pNInz6obpgDQGcFmaJgB` | Americana profunda |
| Arnold | `VR6AewLTigWG4xSOukaG` | Muy grave |
| Josh | `TxGEqnHWrfWFTfGW9XjX` | Joven grave |

Modelo: `eleven_multilingual_v2` (habla español natural). Alternativas más rápidas/baratas: `eleven_turbo_v2_5`, `eleven_flash_v2_5`.
💡 Tier gratis de ElevenLabs: ~10.000 caracteres/mes (≈10 min de audio).

## 🧠 Notas técnicas (gotchas aprendidas)

1. **BottomNavigationView max = 5 items.** Con 6 pestañas: `IllegalArgumentException: Maximum number of items supported by BottomNavigationView is 5` al inflar `activity_main.xml`. → Bitácora se abre desde el botón "Ver mi bitácora completa" en la pantalla **Hoy** (destino sigue en `nav_graph.xml`, solo no está en el menú).
2. **compileSdk vs librerías.** Este equipo tiene instaladas las plataformas 34, 36.1 y 37.1 (no la 35). Usamos `compileSdk = 34`, por eso `androidx.core:core-ktx` va en **1.13.1** (la 1.15.0 exige SDK 35 y rompe `checkDebugAarMetadata`).
3. **Comillas dentro de atributos XML.** `android:text="... \"algo\" ..."` rompe el parser → usar `« »` o `&quot;`.
4. **API keys fuera del repo.** `GROQ_API_KEY` vive en `local.properties` (ignorado por git) y llega a la app vía `BuildConfig.GROQ_API_KEY`.
5. **Control de Spotify** requiere `DevNotificationListener` declarado en el manifest + permiso de acceso a notificaciones concedido por el usuario; sin él `getActiveSessions` devuelve vacío.

---
*Proyecto de portafolio — código limpio, arquitectura por capas (data / ui).*
