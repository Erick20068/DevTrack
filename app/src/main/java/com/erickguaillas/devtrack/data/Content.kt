package com.erickguaillas.devtrack.data

// ─── Modelos ───────────────────────────────────────────────
data class Flashcard(val front: String, val back: String)

data class Deck(val title: String, val emoji: String, val cards: List<Flashcard>) {
    val label get() = "$emoji $title"
}

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class Quiz(val title: String, val emoji: String, val questions: List<QuizQuestion>) {
    val label get() = "$emoji $title"
}

// ─── Contenido ─────────────────────────────────────────────
object Content {

    val decks: List<Deck> = listOf(

        Deck("Java", "☕", listOf(
            Flashcard("¿Qué es la JVM?", "Java Virtual Machine: ejecuta el bytecode de Java. Gracias a ella Java corre en cualquier SO — \"write once, run anywhere\"."),
            Flashcard("JDK vs JRE vs JVM", "JDK = kit de desarrollo (compilador + herramientas). JRE = entorno de ejecución. JVM = la máquina virtual que corre el código."),
            Flashcard("¿Qué es una clase?", "Una plantilla/molde que define atributos y métodos para crear objetos."),
            Flashcard("¿Qué es un objeto?", "Una instancia de una clase. Si 'Auto' es la clase, 'miAuto' es un objeto."),
            Flashcard("== vs .equals()", "== compara REFERENCIAS (¿son el mismo objeto?). .equals() compara CONTENIDO. Con Strings usa siempre .equals()."),
            Flashcard("¿Qué es la herencia?", "Una clase hija adquiere atributos y métodos de la clase padre con 'extends'. Ej: class Perro extends Animal."),
            Flashcard("public vs private vs protected", "public: visible en todas partes. private: solo dentro de su clase. protected: clase + paquete + subclases."),
            Flashcard("¿Qué hace 'static'?", "El miembro pertenece a la CLASE, no a una instancia. Se usa sin crear objetos: Math.max(1, 2).")
        )),

        Deck("Kotlin", "🟣", listOf(
            Flashcard("val vs var", "val = inmutable (solo lectura, como final). var = mutable (puede reasignarse). Prefiere val siempre que puedas."),
            Flashcard("¿Qué es null safety?", "El sistema de tipos que evita el NullPointerException: una variable SOLO acepta null si la marcas con '?': var nombre: String? = null"),
            Flashcard("¿Qué es una data class?", "Clase pensada para datos: Kotlin genera gratis equals(), hashCode(), toString() y copy(). Ej: data class User(val nombre: String)"),
            Flashcard("Operador Elvis (?:)", "Da un valor por defecto cuando algo es null: val largo = nombre?.length ?: 0"),
            Flashcard("Llamada segura (?.)", "Llama al método SOLO si el objeto no es null: usuario?.saludar()"),
            Flashcard("¿Qué son las corrutinas?", "Hilos ligeros para código asíncrono sin bloquear. Se marcan con 'suspend' y se lanzan con launch/async."),
            Flashcard("String templates", "Interpolación directa: \"Hola \$nombre, tienes \${edad + 1} años\""),
            Flashcard("Función de extensión", "Añade funciones a clases existentes sin heredarlas: fun String.gritar() = this.uppercase()")
        )),

        Deck("JavaScript", "🟨", listOf(
            Flashcard("let vs const vs var", "const: no se reasigna (úselo por defecto). let: scope de bloque. var: scope de función — EVÍTALO, es legacy."),
            Flashcard("=== vs ==", "=== compara valor Y tipo (úsalo siempre). == hace conversión de tipos: 1 == '1' es true ⚠️"),
            Flashcard("¿Qué es una Promesa?", "Objeto para operaciones asíncronas con 3 estados: pending, fulfilled, rejected."),
            Flashcard("async / await", "Azúcar sobre promesas: permite escribir código asíncrono como si fuera secuencial."),
            Flashcard("map vs forEach", "map transforma y RETORNA un nuevo array. forEach solo itera (no retorna nada)."),
            Flashcard("null vs undefined", "undefined: la variable existe pero sin valor asignado. null: ausencia de valor INTENCIONAL."),
            Flashcard("Arrow functions", "Sintaxis corta: const suma = (a, b) => a + b. No tienen su propio 'this'."),
            Flashcard("JSON.parse / JSON.stringify", "parse: texto JSON → objeto. stringify: objeto → texto JSON.")
        )),

        Deck("TypeScript", "🔷", listOf(
            Flashcard("¿Qué es TypeScript?", "JavaScript + tipado estático. Compila a JS puro. Detecta errores ANTES de ejecutar."),
            Flashcard("interface vs type", "Ambos definen la forma de un objeto. Las interfaces pueden re-abrirse y extenderse; las type son más flexibles con uniones."),
            Flashcard("any vs unknown", "any desactiva el chequeo de tipos (evítalo). unknown te obliga a verificar el tipo antes de usarlo."),
            Flashcard("¿Qué significa '?' en una propiedad?", "Que es opcional: interface User { nombre: string; apodo?: string }"),
            Flashcard("Genéricos <T>", "Tipos parametrizados: function primero<T>(lista: T[]): T — una función sirve para cualquier tipo sin perder seguridad.")
        )),

        Deck("Python", "🐍", listOf(
            Flashcard("Característica clave de Python", "La indentación ES la sintaxis (define bloques). Interpretado, tipado dinámico, legible."),
            Flashcard("list vs tuple", "list = mutable [1, 2, 3]. tuple = inmutable (1, 2, 3)."),
            Flashcard("¿Qué es un diccionario?", "Pares clave:valor. persona = {\"nombre\": \"Erick\", \"edad\": 20} → persona[\"nombre\"]"),
            Flashcard("f-string", "Interpolación: nombre = \"Erick\"; print(f\"Hola {nombre}\")"),
            Flashcard("¿Qué es 'self'?", "Referencia a la instancia dentro de los métodos de una clase."),
            Flashcard("List comprehension", "\nCrea listas en una línea: dobles = [x * 2 for x in numeros]"),
            Flashcard("¿Qué es pip?", "El gestor de paquetes de Python: pip install requests"),
            Flashcard("__init__", "El método constructor de una clase: se ejecuta al crear el objeto.")
        )),

        Deck("C#", "💠", listOf(
            Flashcard("¿Qué es .NET?", "La plataforma de Microsoft sobre la que corre C# (web con ASP.NET, desktop, móvil, juegos con Unity)."),
            Flashcard("get; set;", "Propiedades autoimplementadas: public string Nombre { get; set; }"),
            Flashcard("¿Qué es LINQ?", "Consultas integradas al lenguaje sobre colecciones: numeros.Where(n => n > 5).Select(...)"),
            Flashcard("class vs struct", "class: tipo de REFERENCIA. struct: tipo de VALOR (se copia al asignar)."),
            Flashcard("async Task / await", "Programación asíncrona: public async Task<string> GetDatos() { ... await ... }")
        )),

        Deck("SQL", "🗄️", listOf(
            Flashcard("SELECT", "Recupera datos: SELECT nombre, precio FROM productos;  (* trae todo)"),
            Flashcard("WHERE", "Filtra filas: SELECT * FROM ventas WHERE total > 100;"),
            Flashcard("INNER JOIN vs LEFT JOIN", "INNER: solo las coincidencias de AMBAS tablas. LEFT: TODAS las de la izquierda + coincidencias (o NULL)."),
            Flashcard("GROUP BY", "Agrupa filas para agregaciones: SELECT cliente, SUM(total) FROM ventas GROUP BY cliente;"),
            Flashcard("HAVING vs WHERE", "WHERE filtra ANTES de agrupar; HAVING filtra DESPUÉS (sobre grupos): HAVING SUM(total) > 1000"),
            Flashcard("PRIMARY KEY", "Identificador único y NO nulo de cada fila."),
            Flashcard("FOREIGN KEY", "Campo que apunta a la PK de otra tabla — mantiene la integridad referencial."),
            Flashcard("ORDER BY", "Ordena resultados: ORDER BY fecha DESC (mayor a menor).")
        )),

        Deck("HTML & CSS", "🌐", listOf(
            Flashcard("¿Qué es HTML?", "Lenguaje de MARCADO: define la estructura y el contenido de una página web."),
            Flashcard("¿Qué es CSS?", "Define la PRESENTACIÓN: colores, fuentes, espaciado, layout."),
            Flashcard("div vs span", "div: contenedor de BLOQUE (ocupa todo el ancho). span: en LÍNEA (solo su contenido)."),
            Flashcard("class vs id", "class: reutilizable en varios elementos (.boton). id: ÚNICO por página (#titulo)."),
            Flashcard("¿Qué es Flexbox?", "Sistema de layout unidimensional: display: flex + justify-content + align-items."),
            Flashcard("Box model", "Todo elemento es una caja: content → padding → border → margin."),
            Flashcard("HTML semántico", "Etiquetas con significado: header, nav, main, article, footer — mejoran SEO y accesibilidad.")
        )),

        Deck("Git", "🌿", listOf(
            Flashcard("git init vs git clone", "init: crea un repo nuevo. clone: copia uno remoto a tu máquina."),
            Flashcard("git add", "Manda cambios al área de preparación (staging): git add . o git add archivo"),
            Flashcard("git commit", "Guarda una foto del código con mensaje: git commit -m \"feat: login\""),
            Flashcard("git push vs git pull", "push: sube tus commits al remoto. pull: descarga y fusiona cambios del remoto."),
            Flashcard("¿Qué es una rama?", "Línea independiente de desarrollo: git switch -c feature/login. Se fusiona con git merge."),
            Flashcard("git status / git log", "status: qué cambió. log: historial de commits."),
            Flashcard(".gitignore", "Archivo que lista qué NO se sube al repo (contraseñas, node_modules, build/).")
        )),

        Deck("Android", "🤖", listOf(
            Flashcard("¿Qué es una Activity?", "Una pantalla de tu app. Punto de entrada visual con su ciclo de vida (onCreate, onStart...)."),
            Flashcard("¿Qué es un Fragment?", "Porción reutilizable de UI que vive DENTRO de una Activity."),
            Flashcard("¿Qué es el AndroidManifest?", "El documento de identidad de la app: declara Activities, permisos y configuración."),
            Flashcard("¿Qué es un Intent?", "Mensaje para lanzar componentes o pasar datos entre pantallas."),
            Flashcard("RecyclerView", "Lista eficiente que RECICLA las vistas al hacer scroll — no carga mil filas en memoria."),
            Flashcard("ViewBinding", "Acceso tipado y seguro a las vistas del XML sin findViewById."),
            Flashcard("Room", "Capa de abstracción sobre SQLite: entidades, DAOs y consultas verificadas en compilación."),
            Flashcard("LiveData", "Contenedor de datos observable que respeta el ciclo de vida (evita crashes por actualizaciones a pantallas muertas).")
        )),

        // ─── ENTREVISTAS ───
        Deck("Entrevistas 🎯", "💼", listOf(
            Flashcard("\"Háblame de ti\"", "Estructura de 3 golpes (1 min): quién eres → qué sabes (stack) → tu proyecto estrella (MovilDilo) → qué buscas. Ensayada, NO improvisada."),
            Flashcard("\"¿Por qué contratarte sin experiencia?\"", "\"Ya construí solo lo que en equipos hacen 3 personas: app, API y base de datos. Me falta experiencia en empresa, no experiencia construyendo software.\""),
            Flashcard("\"¿Tu mayor debilidad?\"", "Real + acción: \"Mi inglés es A2, por eso estudio todos los días camino a B2\". Nunca finjas una fortaleza falsa."),
            Flashcard("Pregunta técnica: ¿interfaz vs clase abstracta?", "Interfaz: solo contratos (sin estado). Abstracta: puede tener estado y métodos ya implementados. Una clase: muchas interfaces, UNA herencia."),
            Flashcard("\"¿Expectativa salarial?\" (Ecuador, junior)", "\"Priorizo aprender; estoy en el rango de mercado junior: \$600-900 según responsabilidades.\""),
            Flashcard("Coding challenge: invierte un string", "Practica hasta que salga solo: str.reversed() no vale — con bucle: for (i = len-1; i >= 0; i--). FizzBuzz igual."),
            Flashcard("\"¿Trabajas en equipo?\"", "Sí + evidencia: Git, ramas, pull requests, code reviews en mis proyectos colaborativos."),
            Flashcard("\"¿Tienes preguntas para nosotros?\"", "SIEMPRE ten 2-3: \"¿Cómo es un día típico del equipo?\" \"¿Cómo miden el éxito de un junior en los primeros 3 meses?\" \"¿Qué stack usan a diario?\"")
        )),

        // ─── INGLÉS A1 → C1 ───
        Deck("Inglés A1 🌱", "🌱", listOf(
            Flashcard("house", "casa 🏠 — The house is big. (La casa es grande)"),
            Flashcard("to eat", "comer — I eat breakfast at 8. (Desayuno a las 8)"),
            Flashcard("family", "familia — My family lives in Cuenca."),
            Flashcard("big / small", "grande / pequeño — A big dog. A small cat."),
            Flashcard("I am / you are", "yo soy/estoy · tú eres/estás — I am a developer. You are my friend."),
            Flashcard("please / thank you", "por favor / gracias — A coffee, please. Thank you!"),
            Flashcard("good morning", "buenos días — Good morning, team!"),
            Flashcard("water", "agua — I drink water every day."),
            Flashcard("friend", "amigo/a — She is my friend."),
            Flashcard("to like", "gustar — I like programming. (Me gusta programar)")
        )),

        Deck("Inglés A2 🌿", "🌿", listOf(
            Flashcard("job", "trabajo — My job is remote. (Mi trabajo es remoto)"),
            Flashcard("usually", "usualmente — I usually study at night."),
            Flashcard("to learn", "aprender — I want to learn English."),
            Flashcard("difficult / easy", "difícil / fácil — The test was not difficult."),
            Flashcard("to work", "trabajar — I work with Java and Kotlin."),
            Flashcard("always / sometimes", "siempre / a veces — I always push my code. I sometimes work late."),
            Flashcard("yesterday / tomorrow", "ayer / mañana — I applied yesterday. I will study tomorrow."),
            Flashcard("to apply for a job", "postular a un empleo — I applied for a job today! 🎉"),
            Flashcard("week / month", "semana / mes — This week I study Java."),
            Flashcard("how much / how many", "cuánto (incontable) / cuántos (contable) — How many tasks? How much time?")
        )),

        Deck("Inglés B1 🍀", "🍀", listOf(
            Flashcard("to achieve", "lograr — I achieved my first interview. (Logré mi primera entrevista)"),
            Flashcard("skill", "habilidad — Programming is a useful skill."),
            Flashcard("interview", "entrevista — I have a job interview tomorrow. 💼"),
            Flashcard("to improve", "mejorar — I improve my English every day."),
            Flashcard("meeting", "reunión — The meeting is at 10 am."),
            Flashcard("deadline", "fecha límite — The deadline is Friday."),
            Flashcard("salary", "salario — The salary is good for a junior role."),
            Flashcard("to hire", "contratar — They hired 3 developers."),
            Flashcard("experience", "experiencia — I have experience with Android apps."),
            Flashcard("to look for", "buscar — I'm looking for my first job.")
        )),

        Deck("Inglés B2 🌳", "🌳", listOf(
            Flashcard("goal", "objetivo/meta — My goal is to work remotely."),
            Flashcard("however", "sin embargo — It's hard. However, I won't quit."),
            Flashcard("to manage", "gestionar/dirigir — I manage my time with DevTrack."),
            Flashcard("environment", "entorno (laboral) — A good work environment matters."),
            Flashcard("achievement", "logro — Building an app was my biggest achievement."),
            Flashcard("meanwhile", "mientras tanto — I'll freelance. Meanwhile, I'll study."),
            Flashcard("to deal with", "lidiar con — Developers deal with bugs daily. 🐛"),
            Flashcard("reliable", "confiable/fiable — A reliable developer delivers on time."),
            Flashcard("remote work", "trabajo remoto — Remote work changed my life."),
            Flashcard("to keep up with", "mantenerse al día con — I keep up with new technologies.")
        )),

        Deck("Inglés C1 🌟", "🌟", listOf(
            Flashcard("to thrive", "prosperar — Junior devs thrive with good mentorship."),
            Flashcard("leverage", "aprovechar/palancar — I leverage AI tools to code faster."),
            Flashcard("regardless of", "independientemente de — Keep coding regardless of the outcome."),
            Flashcard("to tackle", "abordar (un problema) — We tackled the bug as a team."),
            Flashcard("expertise", "pericia/experiencia profunda — She has expertise in backend."),
            Flashcard("concise", "conciso — Keep your resume concise and clear."),
            Flashcard("nuanced", "matizado — His answer was nuanced, not black and white."),
            Flashcard("to streamline", "optimizar/agilizar — CI/CD streamlines our deployments."),
            Flashcard("compelling", "convincente — Make your portfolio compelling."),
            Flashcard("endeavor", "emprendimiento/esfuerzo — Learning English is a worthy endeavor.")
        )),

        Deck("APIs & HTTP", "🔌", listOf(
            Flashcard("¿Qué es una API?", "Interfaz que permite que dos programas se comuniquen: tú pides (request), ella responde (response). Como un mesero entre tu app y la cocina (el servidor)."),
            Flashcard("Métodos HTTP", "GET (leer), POST (crear), PUT/PATCH (actualizar), DELETE (borrar). Son las 4 acciones base de una API REST."),
            Flashcard("Códigos de estado", "200 OK · 201 Creado · 400 Petición inválida · 401 No autorizado · 403 Prohibido · 404 No encontrado · 500 Error del servidor."),
            Flashcard("¿Qué es REST?", "Estilo de API sobre HTTP: recursos con URL (ej. /usuarios/5), sin estado, y usa los métodos HTTP para operar."),
            Flashcard("JSON", "Formato de intercambio de datos: {\"nombre\": \"Erick\", \"edad\": 20}. Es el idioma que hablan casi todas las APIs."),
            Flashcard("Headers y Body", "Headers: metadatos (Authorization, Content-Type). Body: los datos que envías (ej. el JSON del nuevo usuario)."),
            Flashcard("¿Qué es un token JWT?", "Credencial firmada que el servidor entrega al hacer login; el cliente la manda en cada petición para autenticarse."),
            Flashcard("REST vs GraphQL", "REST: varios endpoints, cada uno devuelve una estructura fija. GraphQL: un solo endpoint y pides exactamente los campos que necesitas.")
        )),

        Deck("Docker", "🐳", listOf(
            Flashcard("¿Qué es Docker?", "Empaqueta tu app con TODAS sus dependencias en un contenedor: funciona igual en tu PC, en el servidor y en el de tu compañero."),
            Flashcard("Contenedor vs máquina virtual", "El contenedor comparte el kernel del SO (liviano, arranca en segundos). La VM virtualiza hardware entero (pesada)."),
            Flashcard("¿Qué es una imagen?", "La plantilla de solo lectura para crear contenedores (como la clase). El contenedor es la instancia en ejecución."),
            Flashcard("Dockerfile", "Archivo de receta: FROM (base) → COPY (código) → RUN (instalar) → CMD (comando de arranque)."),
            Flashcard("docker compose", "Orquesta varios contenedores a la vez (app + base de datos) con un solo archivo YAML y un `docker compose up`."),
            Flashcard("Volúmenes", "Almacenamiento que sobrevive al contenedor: sin él, la base de datos se borra al reiniciar.")
        )),

        Deck("Algoritmos", "🧮", listOf(
            Flashcard("Big-O notation", "Mide cómo crece el tiempo según la entrada: O(1) constante, O(log n) búsqueda binaria, O(n) un bucle, O(n²) doble bucle. Menos es mejor."),
            Flashcard("Búsqueda binaria", "Buscar en una lista ORDENADA partiendo a la mitad cada vez: O(log n). Si hay 1.000 elementos, encuentra en ~10 pasos."),
            Flashcard("Ordenamiento rápido (concepto)", "QuickSort: elige un pivote, divide menores/mayores y ordena recursivamente. Promedio O(n log n)."),
            Flashcard("Recursividad", "Una función que se llama a sí misma con un caso base que la detiene. Ej: factorial(3) = 3 * factorial(2)."),
            Flashcard("FIFO vs LIFO", "Cola (FIFO): el primero que entra sale primero. Pila (LIFO): el último que entra sale primero (como deshacer Ctrl+Z)."),
            Flashcard("Hash map (diccionario)", "Estructura clave→valor con búsqueda O(1) promedio: HashMap en Java, dict en Python, objeto en JS."),
            Flashcard("Complejidad espacial", "Cuánta memoria extra usa el algoritmo. A veces sacrificas memoria para ganar velocidad (y viceversa).")
        )),

        Deck("Redes & Web", "🌍", listOf(
            Flashcard("¿Qué pasa al escribir una URL?", "DNS traduce el dominio a IP → conexión TCP → handshake TLS (HTTPS) → petición HTTP → el servidor responde el HTML → el navegador lo renderiza."),
            Flashcard("DNS", "El 'directorio telefónico' de internet: convierte google.com en 142.250.0.1."),
            Flashcard("HTTP vs HTTPS", "HTTPS es HTTP cifrado con TLS: nadie en el camino puede leer ni alterar los datos. SIEMPRE usa HTTPS."),
            Flashcard("Cliente vs servidor", "El cliente (tu app/navegador) pide; el servidor procesa y responde. Tú ya hiciste los dos en MovilDilo."),
            Flashcard("hosting y despliegue", "Publicar tu app en un servidor: Railway, Render, Vercel (free tier) → tu API deja de vivir en tu PC."),
            Flashcard("Puertos", "80 = HTTP, 443 = HTTPS, 3306 = MySQL, 8080 = servidor de desarrollo. Como puertas numeradas del mismo edificio.")
        )),

        Deck("Phrasal verbs 🇬🇧", "🧩", listOf(
            Flashcard("to give up", "rendirse — Never give up on your dreams. (Nunca te rindas)"),
            Flashcard("to keep on", "seguir, continuar — Keep on practicing every day."),
            Flashcard("to look for", "buscar — I'm looking for a job."),
            Flashcard("to find out", "descubrir, averiguar — I found out how the bug happened."),
            Flashcard("to run out of", "quedarse sin — We ran out of time in the sprint."),
            Flashcard("to set up", "configurar, montar — I set up my dev environment."),
            Flashcard("to work out", "funcionar / hacer ejercicio — My code works out now. / I work out every day."),
            Flashcard("to deal with", "lidiar con — Devs deal with bugs every day.")
        ))
    )

    // ─── QUIZZES INTERACTIVOS ──────────────────────────────

    val quizzes: List<Quiz> = listOf(

        Quiz("Quiz Java ☕", "☕", listOf(
            QuizQuestion("¿Con qué método arranca todo programa en Java?",
                listOf("start()", "main()", "run()", "init()"), 1,
                "public static void main(String[] args) es el punto de entrada."),
            QuizQuestion("¿Cómo declaras una CONSTANTE en Java?",
                listOf("const int X = 5", "final int X = 5", "static int X = 5", "let X = 5"), 1,
                "Java usa 'final'. 'const' existe pero está reservada y NO se usa."),
            QuizQuestion("¿Cuánto imprime System.out.println(7 % 3)?",
                listOf("2", "3", "1", "0"), 2,
                "% es el resto de la división: 7 / 3 = 2 con resto 1."),
            QuizQuestion("¿Cuál NO es un tipo primitivo en Java?",
                listOf("int", "boolean", "double", "String"), 3,
                "String es una CLASE; los primitivos son 8: int, double, boolean, char, byte, short, long, float."),
            QuizQuestion("Forma correcta de crear una lista dinámica:",
                listOf("new List<>()", "new ArrayList<>()", "List[] = {}", "array.new()"), 1,
                "ArrayList es la implementación: List<String> l = new ArrayList<>();")
        )),

        Quiz("Quiz Kotlin 🟣", "🟣", listOf(
            QuizQuestion("¿Cómo declaras una variable INMUTABLE?",
                listOf("var nombre", "val nombre", "let nombre", "dim nombre"), 1,
                "val = solo lectura. var = mutable. Prefiere val."),
            QuizQuestion("Si nombre = \"Erick\", ¿qué imprime println(\"Hola \$nombre\")?",
                listOf("Hola \$nombre", "Hola Erick", "Hola nombre", "Error"), 1,
                "Los string templates sustituyen \$variable por su valor."),
            QuizQuestion("¿Cómo declaras un String que PUEDE ser null?",
                listOf("String!", "?String", "String?", "null String"), 2,
                "El '?' al final del tipo lo hace nullable: var s: String? = null"),
            QuizQuestion("fun suma(a: Int, b: Int) = a + b — ¿qué retorna?",
                listOf("Unit", "Int", "Double", "No retorna"), 1,
                "Kotlin infiere el tipo de retorno: Int."),
            QuizQuestion("usuario?.saludar() — ¿cuándo se ejecuta saludar()?",
                listOf("Siempre", "Solo si usuario no es null", "Solo si es null", "Nunca"), 1,
                "?. es la llamada segura: con null no pasa nada (ni error).")
        )),

        Quiz("Quiz JavaScript 🟨", "🟨", listOf(
            QuizQuestion("¿Cuál forma es la recomendada para declarar una constante?",
                listOf("var", "let", "const", "static"), 2,
                "const no permite reasignación. Es el default recomendado."),
            QuizQuestion("typeof null devuelve...",
                listOf("\"null\"", "\"undefined\"", "\"object\"", "\"number\""), 2,
                "Famoso bug histórico de JS: typeof null === \"object\"."),
            QuizQuestion("[1, 2, 3].map(x => x * 2) devuelve:",
                listOf("[1, 2, 3]", "[2, 4, 6]", "6", "undefined"), 1,
                "map transforma cada elemento y retorna un NUEVO array."),
            QuizQuestion("console.log(2 + \"2\") muestra:",
                listOf("4", "22", "\"4\"", "NaN"), 1,
                "Con +, si un lado es string, concatena: \"2\" + \"2\" → \"22\"."),
            QuizQuestion("¿Cómo conviertes un texto JSON en objeto?",
                listOf("JSON.toObj()", "JSON.stringify()", "JSON.parse()", "Object.fromJSON()"), 2,
                "JSON.parse(texto) → objeto. JSON.stringify(objeto) → texto.")
        )),

        Quiz("Quiz Python 🐍", "🐍", listOf(
            QuizQuestion("¿Cómo se define una función en Python?",
                listOf("function f():", "def f():", "fun f()", "fn f() =>"), 1,
                "def nombre(): con indentación para el cuerpo."),
            QuizQuestion("len(\"hola\") devuelve:",
                listOf("3", "4", "5", "Error"), 1,
                "len cuenta caracteres: h-o-l-a = 4."),
            QuizQuestion("¿Cuál de estas estructuras ES mutable?",
                listOf("tuple", "string", "list", "int"), 2,
                "La lista SÍ puede cambiar tras crearse; tuplas y strings no."),
            QuizQuestion("print(10 // 3) imprime:",
                listOf("3.33", "3", "4", "3.0"), 1,
                "// es división ENTERA: descarta los decimales."),
            QuizQuestion("d = {\"a\": 1}; ¿cómo obtienes el 1?",
                listOf("d(a)", "d.get(\"a\") o d[\"a\"]", "d->a", "d.a"), 1,
                "Los diccionarios se acceden por clave con corchetes o .get().")
        )),

        Quiz("Quiz SQL 🗄️", "🗄️", listOf(
            QuizQuestion("¿Qué cláusula filtra filas antes de agrupar?",
                listOf("FILTER", "HAVING", "WHERE", "LIMIT"), 2,
                "WHERE filtra filas. HAVING filtra grupos ya agregados."),
            QuizQuestion("¿Cómo cuentas todas las filas de 'clientes'?",
                listOf("SUM(*) clientes", "COUNT(*) FROM clientes", "SELECT COUNT(*) FROM clientes", "TOTAL clientes"), 2,
                "SELECT COUNT(*) FROM clientes;"),
            QuizQuestion("Quieres TODOS los clientes aunque no tengan ventas. Usas:",
                listOf("INNER JOIN", "LEFT JOIN", "CROSS JOIN", "UNION"), 1,
                "LEFT JOIN trae toda la tabla izquierda + coincidencias (o NULLs)."),
            QuizQuestion("Ordenar productos del más caro al más barato:",
                listOf("ORDER BY precio DESC", "ORDER BY precio ASC", "SORT precio DOWN", "GROUP BY precio"), 0,
                "ORDER BY ... DESC = de mayor a menor."),
            QuizQuestion("Una PRIMARY KEY debe ser:",
                listOf("única y no nula", "numérica", "un texto", "opcional"), 0,
                "PK = identificador ÚNICO y NO NULO de cada fila.")
        )),

        // ─── QUIZZES DE INGLÉS ───
        Quiz("Quiz Inglés A1 🌱", "🌱", listOf(
            QuizQuestion("Elige la traducción de: \"Hola, ¿cómo estás?\"",
                listOf("Hello, who are you?", "Hello, how are you?", "Hi, how old are you?", "Hello, where are you?"), 1,
                "how = cómo · who = quién · how old = qué edad."),
            QuizQuestion("Completa: \"I ___ a developer.\"",
                listOf("is", "are", "am", "be"), 2,
                "I → am · you/we/they → are · he/she/it → is"),
            QuizQuestion("El plural de \"child\" es:",
                listOf("childs", "children", "childes", "childrens"), 1,
                "child → children es un plural IRREGULAR."),
            QuizQuestion("Completa: \"She ___ coffee every morning.\"",
                listOf("drink", "drinks", "drinking", "drank"), 1,
                "Tercera persona en presente simple lleva -s: she drinks."),
            QuizQuestion("¿Qué significa \"thank you\"?",
                listOf("por favor", "de nada", "gracias", "perdón"), 2,
                "thank you = gracias · please = por favor · you're welcome = de nada.")
        )),

        Quiz("Quiz Inglés A2 🌿", "🌿", listOf(
            QuizQuestion("El pasado de \"go\" es:",
                listOf("goed", "gone", "went", "going"), 2,
                "go → went (irregular). gone es el participio."),
            QuizQuestion("Completa: \"I have lived here ___ 2020.\"",
                listOf("for", "since", "from", "at"), 1,
                "SINCE + punto en el tiempo (2020, Monday). FOR + duración (3 years)."),
            QuizQuestion("Completa: \"There ___ many options.\"",
                listOf("is", "am", "are", "be"), 2,
                "options es plural → there are."),
            QuizQuestion("Completa: \"___ you ever been to Quito?\"",
                listOf("Did", "Have", "Do", "Are"), 1,
                "Present perfect para experiencias de vida: Have you ever..."),
            QuizQuestion("El comparativo de \"good\" es:",
                listOf("gooder", "more good", "better", "best"), 2,
                "good → better (comparativo) → best (superlativo). Irregular.")
        )),

        Quiz("Quiz Inglés B1 🍀", "🍀", listOf(
            QuizQuestion("Completa: \"If I ___ more time, I would study more.\"",
                listOf("have", "had", "will have", "would have"), 1,
                "Segundo condicional: If + pasado, would + infinitivo."),
            QuizQuestion("Completa: \"I'm looking forward ___ meeting you.\"",
                listOf("for", "to", "at", "-"), 1,
                "looking forward TO + verbo-ing. El 'to' aquí es preposición."),
            QuizQuestion("Completa: \"He told me ___ the bug in production.\"",
                listOf("about", "of", "on", "for"), 0,
                "tell someone ABOUT something."),
            QuizQuestion("Completa: \"I've worked here ___ three years.\"",
                listOf("since", "for", "during", "from"), 1,
                "FOR + duración (three years). SINCE + punto inicial."),
            QuizQuestion("Completa: \"The app ___ by our team last month.\"",
                listOf("built", "was built", "is built", "has built"), 1,
                "Pasiva en pasado: was/were + participio. built = participio de build.")
        )),

        Quiz("Quiz Inglés B2 🌳", "🌳", listOf(
            QuizQuestion("Completa: \"Despite ___ tired, he finished the sprint.\"",
                listOf("be", "being", "been", "to be"), 1,
                "Despite + verbo-ing: despite being tired."),
            QuizQuestion("Completa: \"By the time you arrive, I ___ the deploy.\"",
                listOf("will finish", "will have finished", "finished", "am finishing"), 1,
                "Futuro perfecto para acciones completadas ANTES de un punto futuro."),
            QuizQuestion("Completa: \"Hardly ___ I started when the server broke.\"",
                listOf("have", "had", "did", "was"), 1,
                "Hardly had + sujeto + participio: estructura formal con inversión."),
            QuizQuestion("Completa: \"You'd better ___ the tests before pushing.\"",
                listOf("to run", "running", "run", "ran"), 2,
                "had better + infinitivo SIN 'to'."),
            QuizQuestion("Completa: \"I wish I ___ more about Kubernetes.\"",
                listOf("know", "knew", "had known", "would know"), 1,
                "I wish + pasado = deseo sobre el presente.")
        )),

        Quiz("Quiz APIs & HTTP 🔌", "🔌", listOf(
            QuizQuestion("¿Qué método HTTP se usa para CREAR un recurso?",
                listOf("GET", "POST", "PATCH", "HEAD"), 1,
                "POST crea. GET lee, PUT/PATCH actualizan, DELETE borra."),
            QuizQuestion("¿Qué significa el código 401?",
                listOf("No encontrado", "No autorizado", "Error del servidor", "Creado"), 1,
                "401 = no autenticado. 403 = autenticado pero sin permiso."),
            QuizQuestion("¿Para qué sirve un token JWT?",
                listOf("Comprimir respuestas", "Autenticar al usuario en cada petición", "Guardar imágenes", "Encriptar la base de datos"), 1,
                "Se envía en el header Authorization: Bearer <token>."),
            QuizQuestion("Una API REST usa principalmente...",
                listOf("Solo POST", "Recursos con URL + métodos HTTP", "Archivos CSV", "Sockets binarios"), 1,
                "Ej: GET /usuarios/5 — el recurso es la URL, la acción es el método."),
            QuizQuestion("¿Qué formato de datos devuelven casi todas las APIs?",
                listOf("PDF", "XML obligatorio", "JSON casi siempre", "CSV"), 2,
                "JSON {clave: valor} es el estándar de facto.")
        )),

        Quiz("Quiz Git 🌿", "🌿", listOf(
            QuizQuestion("¿Qué comando deshace el último commit MANTENIENDO los cambios?",
                listOf("git reset --hard HEAD~1", "git reset --soft HEAD~1", "git delete last", "git undo"), 1,
                "--soft quita el commit y deja los cambios en staging. --hard los borra."),
            QuizQuestion("¿Qué hace `git branch -M main`?",
                listOf("Crea una rama llamada M", "Renombra la rama actual a main", "Borra main", "Sube a main"), 1,
                "-M renombra forzando (la rama por defecto pasó de master a main)."),
            QuizQuestion("Quieres traer los cambios del remoto y fusionarlos:",
                listOf("git push", "git pull", "git commit", "git clone"), 1,
                "git pull = git fetch + git merge."),
            QuizQuestion("¿Dónde vive el archivo que EXCLUYE cosas del repo?",
                listOf("git.config", ".gitignore", "exclude.txt", "README.md"), 1,
                ".gitignore: contraseñas, build/, node_modules."),
            QuizQuestion("Acabas de escribir código y quieres ver qué cambió:",
                listOf("git log", "git status", "git push", "git init"), 1,
                "git status muestra archivos modificados/nuevos.")
        )),

        Quiz("Quiz Algoritmos 🧮", "🧮", listOf(
            QuizQuestion("La búsqueda binaria requiere que la lista esté...",
                listOf("Vacía", "Ordenada", "Sin duplicados", "En JSON"), 1,
                "Solo así puedes descartar la mitad en cada paso: O(log n)."),
            QuizQuestion("¿Qué complejidad tiene un doble bucle sobre n elementos?",
                listOf("O(1)", "O(n)", "O(n²)", "O(log n)"), 2,
                "Por cada elemento recorres todos los demás: n × n."),
            QuizQuestion("Una pila (stack) es...",
                listOf("FIFO", "LIFO", "Aleatoria", "Ordenada"), 1,
                "LIFO: el último en entrar es el primero en salir (Ctrl+Z)."),
            QuizQuestion("¿Qué agrega un 'caso base' en una función recursiva?",
                listOf("Más memoria", "La condición que detiene la recursión", "Un bucle infinito", "Velocidad"), 1,
                "Sin caso base, la recursión nunca termina (StackOverflow)."),
            QuizQuestion("Buscar una clave en un HashMap tiene complejidad:",
                listOf("O(n)", "O(log n)", "O(1) promedio", "O(n²)"), 2,
                "Por eso los diccionarios/objetos son tan rápidos para buscar.")
        )),

        Quiz("Quiz Verbos irregulares 🇬🇧", "📚", listOf(
            QuizQuestion("go → went → ...",
                listOf("goed", "gone", "going", "went"), 1,
                "go / went / GONE (participio para have gone)."),
            QuizQuestion("¿Cómo se dice 'yo hice' (do en pasado)?",
                listOf("doed", "did", "done", "doing"), 1,
                "do / did / done. 'I did my homework.'"),
            QuizQuestion("Él tomó el bus (take):",
                listOf("He taked the bus", "He took the bus", "He taken the bus", "He taking the bus"), 1,
                "take / took / taken."),
            QuizQuestion("¿Qué verbo NO es irregular?",
                listOf("eat", "write", "work", "sing"), 2,
                "work → worked (regular). Los demás tienen forma propia: ate, wrote, sang."),
            QuizQuestion("Ella compró un libro (buy):",
                listOf("She buyed a book", "She bought a book", "She buy a book", "She boughting"), 1,
                "buy / bought / bought.")
        ))
    )

    // ─── PRÁCTICA: problemas reales de código + ejercicios de inglés ───
    data class PracticeItem(
        val id: String,
        val prompt: String,
        val hint: String,
        val solution: String
    )

    data class PracticePack(val title: String, val emoji: String, val items: List<PracticeItem>) {
        val label get() = "$emoji $title"
    }

    val practicePacks: List<PracticePack> = listOf(

        PracticePack("Práctica · Git", "🌿", listOf(
            PracticeItem("p_git_1",
                "Clona un repositorio de GitHub y muéstrame los comandos en orden.",
                "Necesitas la URL del repo (botón verde Code → HTTPS).",
                "git clone https://github.com/Erick20068/DevTrack.git\ncd DevTrack\ngit log --oneline   # ver el historial"),
            PracticeItem("p_git_2",
                "Crea una rama llamada 'feature/login', haz un cambio y súbela al remoto.",
                "Después de commitear, el primer push de una rama nueva necesita -u.",
                "git switch -c feature/login\ngit add .\ngit commit -m \"feat: pantalla login\"\ngit push -u origin feature/login"),
            PracticeItem("p_git_3",
                "Hiciste un commit con un mensaje equivocado. ¿Cómo lo corriges sin crear otro commit?",
                "Puedes editar el último commit con --amend (solo si no lo has subido).",
                "git commit --amend -m \"mensaje correcto\""),
            PracticeItem("p_git_4",
                "Revisa la diferencia entre tu working directory y el último commit.",
                "El comando clásico para ver diferencias es git diff.",
                "git diff          # cambios sin preparar\ngit diff --staged # cambios ya en staging"),
            PracticeItem("p_git_5",
                "Únete a un repo existente de tu equipo sin copiar todo de nuevo.",
                "Necesitas traer el remoto y luego hacer pull.",
                "git remote add origin <url>\ngit pull origin main")
        )),

        PracticePack("Práctica · APIs", "🔌", listOf(
            PracticeItem("p_api_1",
                "Explica con tus palabras qué es una API REST y da un ejemplo de endpoint.",
                "Piensa en recursos, URLs y métodos HTTP.",
                "Una API REST expone recursos en URLs y permite operar con métodos HTTP.\nEj: GET /productos → lista productos. POST /productos → crea uno.\nEn MovilDilo tu API tenía endpoints así para inventario y ventas."),
            PracticeItem("p_api_2",
                "¿Qué diferencia hay entre 401 y 403? Escribe un caso de cada uno.",
                "Uno es 'no sé quién eres', el otro es 'sé quién eres pero no puedes'.",
                "401 Unauthorized: token ausente o inválido (no estás autenticado).\n403 Forbidden: autenticado pero sin permiso (ej: un bodeguero intentando borrar usuarios)."),
            PracticeItem("p_api_3",
                "Diseña en texto los endpoints para un sistema de tareas.",
                "Piensa CRUD: crear, listar, actualizar, borrar.",
                "GET    /tareas          → listar\nGET    /tareas/{id}     → detalle\nPOST   /tareas          → crear\nPUT    /tareas/{id}     → actualizar\nDELETE /tareas/{id}     → eliminar\nHeader: Authorization: Bearer <token>"),
            PracticeItem("p_api_4",
                "¿Qué le mandarías al servidor para crear un usuario? Escribe el JSON.",
                "Campos mínimos + no envíes la contraseña en texto plano al log.",
                "POST /usuarios\n{\n  \"nombre\": \"Erick\",\n  \"email\": \"erick@mail.com\",\n  \"password\": \"(se hashea en el servidor)\"\n}")
        )),

        PracticePack("Práctica · Java & Kotlin", "☕", listOf(
            PracticeItem("p_java_1",
                "Escribe una función que invierta un String SIN usar reverse().",
                "Recorre desde el último carácter hasta el primero.",
                "fun invertir(s: String): String {\n    val sb = StringBuilder()\n    for (i in s.length - 1 downTo 0) sb.append(s[i])\n    return sb.toString()\n}"),
            PracticeItem("p_java_2",
                "¿Qué diferencia hay entre == y .equals() en Java? Da un ejemplo.",
                "Uno compara referencias, el otro contenido.",
                "== compara si son el MISMO objeto en memoria.\n.equals() compara el contenido.\nString a = new String(\"hi\"); String b = new String(\"hi\");\na == b → false ; a.equals(b) → true"),
            PracticeItem("p_kotlin_1",
                "Escribe una data class Usuario con nombre y email, y crea una copia con otro nombre.",
                "data class + .copy()",
                "data class Usuario(val nombre: String, val email: String)\n\nval u1 = Usuario(\"Erick\", \"e@mail.com\")\nval u2 = u1.copy(nombre = \"Santiago\")"),
            PracticeItem("p_kotlin_2",
                "Filtra una lista de números y quédate solo con los pares, sumándoles 10.",
                "Combina filter y map.",
                "val nums = listOf(1, 2, 3, 4, 5, 6)\nval paresMas10 = nums.filter { it % 2 == 0 }.map { it + 10 }\n// [12, 14, 16]"),
            PracticeItem("p_java_3",
                "¿Qué es NullPointerException y cómo lo evitas en Kotlin?",
                "Kotlin tiene un sistema de tipos que distingue nullable de no-nullable.",
                "NPE: intentar usar una referencia null (usar .length de un String null).\nEn Kotlin: marca nullable con ? (String?), usa ?. para llamadas seguras, ?: para valor por defecto, y !! solo si estás 100% seguro.")
        )),

        PracticePack("Práctica · SQL & Datos", "🗄️", listOf(
            PracticeItem("p_sql_1",
                "Escribe la consulta: los 5 productos más caros.",
                "ORDER BY + DESC + LIMIT.",
                "SELECT * FROM productos\nORDER BY precio DESC\nLIMIT 5;"),
            PracticeItem("p_sql_2",
                "Lista los clientes SIN ventas registradas.",
                "Piensa en LEFT JOIN con IS NULL, o NOT IN.",
                "SELECT c.* FROM clientes c\nLEFT JOIN ventas v ON v.cliente_id = c.id\nWHERE v.id IS NULL;"),
            PracticeItem("p_sql_3",
                "Total vendido por cada cliente, solo los que superan $1000.",
                "GROUP BY + SUM + HAVING (no WHERE).",
                "SELECT cliente_id, SUM(total) AS total\nFROM ventas\nGROUP BY cliente_id\nHAVING SUM(total) > 1000;")
        )),

        PracticePack("Práctica · Python & JS", "🐍", listOf(
            PracticeItem("p_py_1",
                "Cuenta cuántas veces aparece cada palabra en una frase (Python).",
                "Un diccionario + split().",
                "frase = \"hola mundo hola\"\nconteo = {}\nfor palabra in frase.split():\n    conteo[palabra] = conteo.get(palabra, 0) + 1\nprint(conteo)  # {'hola': 2, 'mundo': 1}"),
            PracticeItem("p_js_1",
                "Escribe una función JS que reciba un array y devuelva solo los mayores de 18.",
                "Array.prototype.filter",
                "const adultos = (edades) => edades.filter(e => e > 18);\n\nadultos([12, 20, 18, 25]); // [20, 25]"),
            PracticeItem("p_py_2",
                "¿Qué es un decorador en Python? Da un ejemplo simple.",
                "Una función que envuelve a otra para añadir comportamiento.",
                "def loguear(f):\n    def wrapper(*args):\n        print(\"Ejecutando...\")\n        return f(*args)\n    return wrapper\n\n@loguear\ndef saludar(nombre):\n    print(f\"Hola {nombre}\")"),
            PracticeItem("p_js_2",
                "¿Qué es el DOM y cómo cambias el texto de un elemento?",
                "document.getElementById / querySelector.",
                "El DOM es el árbol de objetos que representa el HTML.\nconst titulo = document.querySelector('#titulo');\ntitulo.textContent = 'Nuevo texto';")
        )),

        PracticePack("Práctica · Inglés: escritura", "✍️", listOf(
            PracticeItem("p_en_w1",
                "Escribe 3 oraciones presentándote como desarrollador (en inglés).",
                "Usa: I'm / I work with / I'm looking for.",
                "Hi, I'm Erick. I'm a junior software developer from Ecuador.\nI work with Java, Kotlin and Spring Boot.\nI'm looking for my first remote job where I can grow."),
            PracticeItem("p_en_w2",
                "Describe tu proyecto más importante en 40 palabras (inglés).",
                "Estructura: qué es → qué tecnología → qué problema resuelve.",
                "I built an Android app called MovilDilo to manage inventory and sales for small businesses. It uses Kotlin, a Spring Boot REST API, MySQL and even an AI voice assistant. It helped shop owners track their stock in real time."),
            PracticeItem("p_en_w3",
                "Escribe un email corto respondiendo a 'Tell us about yourself'.",
                "Saludo + experiencia + motivación + cierre.",
                "Dear hiring team,\nI am a junior software developer from Ecuador with strong Java and Kotlin skills. I have built a complete inventory system with a REST API and AI integration. I am eager to learn and contribute to your team.\nBest regards,\nErick Guaillas"),
            PracticeItem("p_en_w4",
                "Traduce: 'Ayer postulé a cinco trabajos y hoy practiqué inglés por una hora.'",
                "Pasado simple: applied / practiced.",
                "Yesterday I applied to five jobs and today I practiced English for one hour."),
            PracticeItem("p_en_w5",
                "Escribe 3 preguntas que harías en una entrevista técnica (inglés).",
                "Empieza con: What / How / Could.",
                "What does a typical day look like for a junior developer here?\nHow do you measure success in the first three months?\nCould you tell me more about the team and the tech stack?")
        )),

        PracticePack("Práctica · Inglés: lectura & pronunciación", "🎧", listOf(
            PracticeItem("p_en_r1",
                "LEE en voz alta y responde: 'Remote work changed my life. I wake up early, study English for an hour, then apply to jobs online. In the afternoon, I build my app. It is hard but exciting.' — ¿Qué hace en la mañana?",
                "Busca las actividades después de 'I'.",
                "Se levanta temprano, estudia inglés una hora y postula a trabajos online.\nPronunciación: 'early' = /ér-li/ (no 'erli'), 'exciting' = /ek-sáitin/."),
            PracticeItem("p_en_r2",
                "LEE y responde: 'A good developer does not know everything. A good developer knows how to find the answer, ask good questions and keep learning every day.' — ¿Qué distingue a un buen dev?",
                "No es saberse todo.",
                "No saberlo todo, sino saber buscar respuestas, hacer buenas preguntas y aprender cada día."),
            PracticeItem("p_en_p1",
                "Pronunciación: repite 5 veces 'I would like to improve my English skills.'",
                "would like = /wud láik/. Una los sonidos sin pausas.",
                "Enfócate en:\n• 'would' (la 'l' es muda): /wud/\n• 'improve': /im-prúuv/\n• 'skills': la 'k' suena /skils/"),
            PracticeItem("p_en_p2",
                "Pronuncia estas palabras de programación: 'queue', 'suite', 'chaos', 'integer'.",
                "Ojo: varias NO se pronuncian como se escriben.",
                "queue = /kiú/ · suite = /suít/ · chaos = /kéos/ · integer = /ín-te-yer/\nTruco: escúchalas en Google Translate y repite en voz alta 3 veces."),
            PracticeItem("p_en_r3",
                "LEE y tradúcelo al español: 'Technical interviews test how you think, not just what you know. Explain your reasoning out loud.'",
                "reasoning = razonamiento.",
                "Las entrevistas técnicas evalúan cómo piensas, no solo lo que sabes. Explica tu razonamiento en voz alta.")
        ))
    )

    /** Total de ejercicios de práctica (para progreso global) */
    val totalPractice: Int get() = practicePacks.sumOf { it.items.size }
}
