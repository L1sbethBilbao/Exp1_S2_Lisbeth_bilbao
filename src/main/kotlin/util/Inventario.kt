package util

/**
 * Inventario con Map (ingrediente -> cantidad) y verificación con when.
 */
object Inventario {

    data class ResultadoStock(
        val disponible: Boolean,
        val faltantes: List<String>,
        val mensaje: String
    )

    /**
     * Calcula cuántas unidades de cada ingrediente se necesitan
     * (1 unidad de cada ingrediente por pizza).
     */
    fun cantidadNecesaria(ingredientes: List<String>, cantidadPizzas: Int): Map<String, Int> {
        val necesario = mutableMapOf<String, Int>()
        for (ing in ingredientes) {
            necesario[ing] = (necesario[ing] ?: 0) + cantidadPizzas
        }
        return necesario
    }

    /**
     * Usa when para clasificar el resultado de disponibilidad.
     * Si faltan ingredientes, indica cuáles y cómo reabastecer.
     */
    fun verificarDisponibilidad(
        inventario: Map<String, Int>,
        ingredientesPedido: List<String>,
        cantidadPizzas: Int
    ): ResultadoStock {
        val necesario = cantidadNecesaria(ingredientesPedido, cantidadPizzas)
        val faltantes = mutableListOf<String>()
        val reabastecer = mutableListOf<String>()

        for ((ing, req) in necesario) {
            val hay = inventario[ing] ?: 0
            if (hay < req) {
                faltantes.add("$ing (hay $hay, se necesitan $req)")
                reabastecer.add("agregar al menos ${req - hay} unidades de $ing")
            }
        }

        val estadoClave = when {
            faltantes.isEmpty() -> "OK"
            faltantes.size == 1 -> "FALTA_UNO"
            else -> "FALTAN_VARIOS"
        }

        val mensaje = when (estadoClave) {
            "OK" -> "OK — hay stock para $cantidadPizzas pizza(s)."
            "FALTA_UNO", "FALTAN_VARIOS" -> {
                val faltanLineas = faltantes.joinToString("\n") { "  - $it" }
                val reponerLineas = reabastecer.joinToString("\n") { "  - $it" }
                "SIN STOCK suficiente.\nFaltan:\n$faltanLineas\nReabastecer:\n$reponerLineas"
            }
            else -> "No se pudo evaluar el inventario."
        }

        return ResultadoStock(faltantes.isEmpty(), faltantes, mensaje)
    }

    /**
     * Actualiza el inventario descontando lo usado. Solo llamar si hay stock.
     */
    fun actualizarInventario(
        inventario: MutableMap<String, Int>,
        ingredientesPedido: List<String>,
        cantidadPizzas: Int
    ) {
        val necesario = cantidadNecesaria(ingredientesPedido, cantidadPizzas)
        for ((ing, req) in necesario) {
            val actual = inventario[ing] ?: 0
            if (actual - req < 0) {
                throw IllegalArgumentException("Cantidad negativa en inventario para $ing")
            }
            inventario[ing] = actual - req
        }
    }

    fun formatear(inventario: Map<String, Int>): String =
        inventario.entries.joinToString("\n") { "  - ${it.key}: ${it.value}" }
}
