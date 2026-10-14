package com.erickguaillas.devtrack.data

import java.util.Calendar

// ─── Entrenamiento sin gimnasio (peso corporal) ────────────
object Wellness {

    data class Routine(val dayName: String, val focus: String, val emoji: String, val exercises: List<String>)

    private val routines = listOf(
        // Calendar.MONDAY = 2 ... SUNDAY = 1 (lo ordenamos por índice dayOfWeek - 1)
        Routine("Domingo", "Descanso activo", "🧘", listOf(
            "Caminata tranquila de 30-40 min",
            "Estiramientos de todo el cuerpo · 10 min",
            "Respiración profunda 4-7-8 · 5 rondas",
            "Planifica tu semana en DevTrack 📝"
        )),
        Routine("Lunes", "Pecho + tríceps", "🔥", listOf(
            "Flexiones · 4x10-15",
            "Flexiones diamante · 3x8",
            "Fondos en silla · 3x10",
            "Flexiones inclinadas · 3x12",
            "Plancha · 3x30 seg"
        )),
        Routine("Martes", "Pierna", "🦵", listOf(
            "Sentadillas · 4x20",
            "Zancadas · 3x12 por pierna",
            "Puente de glúteo · 3x15",
            "Elevación de talones · 4x20",
            "Sentadilla isométrica en pared · 3x30 seg"
        )),
        Routine("Miércoles", "Core + abdomen", "🎯", listOf(
            "Plancha · 4x45 seg",
            "Crunches · 3x15",
            "Elevación de piernas · 3x12",
            "Plancha lateral · 3x20 seg por lado",
            "Mountain climbers · 3x30"
        )),
        Routine("Jueves", "Espalda + bíceps", "🎒", listOf(
            "Remo con mochila cargada · 4x12",
            "Superman · 3x15",
            "Curl de bíceps con mochila · 3x12",
            "Bird-dog · 3x10 por lado",
            "Aperturas con toalla · 3x15"
        )),
        Routine("Viernes", "Full body", "⚡", listOf(
            "Burpees · 3x10",
            "Flexiones · 3x12",
            "Sentadilla con salto · 3x12",
            "Plancha a codos altos · 3x10",
            "Escaladores · 3x40 seg"
        )),
        Routine("Sábado", "Cardio", "🏃", listOf(
            "Correr o trotar 20-30 min",
            "O: saltar cuerda 5x2 min",
            "O: subir escaleras 15 min",
            "Estira bien al terminar 🙌"
        ))
    )

    fun todayRoutine(): Routine = routines[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]

    /** Ejercicios del día como checklist (id estable + texto) */
    fun routineItems(): List<Pair<String, String>> =
        todayRoutine().exercises.mapIndexed { i, e -> "routine_$i" to e }

    /** Tips de cuidado corporal como checklist */
    fun tipItems(): List<Pair<String, String>> =
        bodyCareTips.mapIndexed { i, t -> "tip_$i" to t }

    val bodyCareTips = listOf(
        "💧 2 litros de agua al día — tu cerebro rinde más hidratado",
        "😴 7-8 horas de sueño: el código del día siguiente te lo agradece",
        "👀 Regla 20-20-20: cada 20 min de pantalla, mira algo a 6 metros por 20 seg",
        "🧍 Postura: cada hora levántate 2 minutos — tu espalda es tu hardware",
        "🥗 Proteína en cada comida si entrenas (huevos, pollo, atún, lentejas)",
        "🚶 10 minutos caminando después de comer = mejor digestión y energía"
    )

    // ─── Motivación ───
    val motivaciones = listOf(
        "El 90% de los juniors se rinde antes del sexto mes. Tú no asos del 90%, Erick. Tú ya tienes una app con IA en tu GitHub.",
        "Cada postulación que envías es un billete de lotería que sí tiene premio. Sigue enviando.",
        "Un año desde hoy puedes estar ganando 1.500 dólares remoto. Pero ese año empieza hoy, con esta tarea pequeña.",
        "Nadie nace sabiendo programar. Todo senior fue un junior que no se rindió. Punto.",
        "No compitas con otros devs. Compite con el Erick de ayer. ¿Qué aprendiste hoy que ayer no sabías?",
        "El código que escribes hoy sin cobrar te pagará miles de dólares mañana. Es inversión, no pérdida.",
        "Tu inglés no está mal: está en construcción. Igual que tu carrera. Ladrillo diario.",
        "La motivación te trajo aquí. La disciplina de 45 minutos diarios te llevará al empleo.",
        "Piensa en los reclutadores que te rechazan: un día les vas a escribir tú, pidiendo referencias de TU startup.",
        "El músculo crece cuando descansas. Tu cerebro igual. Haz tu rutina y viene el café. 💪"
    )

    fun randomMotivacion(): String = motivaciones.random()
}
