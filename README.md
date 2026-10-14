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

- [ ] **v2**: Asistente conectado a Groq API (respuestas con IA generativa)
- [ ] Widget de tareas en pantalla de inicio
- [ ] Sistema de rachas (streaks) con 🔥
- [ ] Más barajas de repaso (Python, Docker, Git)
- [ ] Estadísticas semanales en la bitácora
- [ ] Publicación en Play Store

## 🎤 Comandos de voz (v1)

```
"agregar tarea repasar java"
"postulé a Anyone AI"          → guarda en Bitácora (💼 Trabajo)
"estudié inglés 30 minutos"    → guarda en Bitácora (🗣️ Inglés)
"estuve programando la app"    → guarda en Bitácora (💻 Código)
"abre repaso" / "abre metas"   → navega a la sección
"qué tengo hoy"                → lee tus tareas pendientes en voz alta
```

---
*Proyecto de portafolio — código limpio, arquitectura por capas (data / ui).*
