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

---
*Proyecto de portafolio — código limpio, arquitectura por capas (data / ui).*
