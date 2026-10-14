package com.erickguaillas.devtrack.data

/**
 * Mentor financiero de DevTrack.
 * Reglas de oro, presupuesto y lecciones para Erick (Ecuador, dolarizado).
 * No es asesoría financiera profesional: es educación y orden.
 */
object Finance {

    data class Lesson(val title: String, val body: String)

    val lessons = listOf(
        Lesson(
            "Regla 50/30/20",
            "Divide cada ingreso: 50% necesidades (arriendo, comida, transporte), 30% gustos, 20% AHORRO/INVERSIÓN. Con ingresos pequeños igual aplica: guarda el 20% primero, apenas cobras."
        ),
        Lesson(
            "Págate primero",
            "Apenas te pagan, separa tu ahorro ANTES de gastar. Si esperas 'lo que sobre', nunca sobra. $5 por cada $100 ya es un hábito ganador."
        ),
        Lesson(
            "Fondo de emergencia",
            "Antes de invertir un centavo: junta 3-6 meses de tus gastos básicos en una cuenta separada. Es tu escudo contra imprevistos y deudas malas."
        ),
        Lesson(
            "Interés compuesto",
            "Si ahorras $50/mes al 8% anual, en 10 años tienes ~$9.150 (aportaste $6.000). El tiempo es tu mayor activo: empieza joven, Erick, tienes 20 años."
        ),
        Lesson(
            "Deuda buena vs mala",
            "BUENA: la que genera ingresos o activos (curso, equipo para trabajar, negocio). MALA: la que compra consumo (celular a 24 meses, ropa, salidas). Evita la mala desde el día 1."
        ),
        Lesson(
            "Tarjeta de crédito",
            "Es una herramienta, no dinero extra. Úsala solo si puedes pagar el TOTAL cada mes. Pagar el mínimo es la trampa más cara que existe (~20-40% anual)."
        ),
        Lesson(
            "Ecuador es dolarizado 🇪🇨",
            "No hay inflación brusca como en otros países, pero tampoco devaluación que 'licúe' deudas: cada dólar vale igual. Ventaja: puedes pensar y ahorrar en USD directo."
        ),
        Lesson(
            "Ingresos freelance = ingresos de negocio",
            "Cuando cobres por Workana/Fiverr: aparta un % para impuestos y otro para meses flojos. Lleva registro (tu Bitácora sirve). Si facturas seguido, revisa RUC y facturación del SRI."
        ),
        Lesson(
            "Nunca inviertas en lo que no entiendes",
            "Ni cripto 'garantizada', ni trading con dinero prestado, ni 'rendimientos del 20% mensual'. Si suena demasiado bueno, es estafa. Regla absoluta."
        ),
        Lesson(
            "Diversifica y automatiza",
            "Cuando YA tengas fondo de emergencia: ahorro automático mensual en instrumentos simples y diversificados (bajo riesgo primero). Automatiza el ahorro para no depender de la fuerza de voluntad."
        )
    )

    fun lessonOfTheDay(): Lesson {
        val day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return lessons[day % lessons.size]
    }

    /** Plan 50/30/20 a partir de un ingreso mensual */
    fun budget(monthlyIncome: Double): String {
        val need = monthlyIncome * 0.50
        val want = monthlyIncome * 0.30
        val save = monthlyIncome * 0.20
        return "Con $${"%.0f".format(monthlyIncome)}/mes:\n" +
                "• Necesidades: $${"%.2f".format(need)}\n" +
                "• Gustos: $${"%.2f".format(want)}\n" +
                "• Ahorro/Inversión: $${"%.2f".format(save)}"
    }

    /** Siguiente paso financiero según su etapa */
    val priorityPath = listOf(
        "1️⃣ Ingreso estable (empleo o freelance recurrente)",
        "2️⃣ Fondo de emergencia (3-6 meses de gastos)",
        "3️⃣ Cero deudas malas",
        "4️⃣ Ahorro automático mensual (aunque sean $20)",
        "5️⃣ Aprender inversión básica e invertir diversificado",
        "6️⃣ Impuestos/registro en orden cuando factures seguido"
    )
}
