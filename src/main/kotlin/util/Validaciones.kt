package util

object Validaciones {
    /** Default del enunciado S2 para correo inválido. El teléfono reintenta como en S1. */
    const val CORREO_DEFAULT = "correo@invalido.com"

    /** Celular Chile: 9 dígitos (ej. 912345678). */
    fun normalizarTelefono(texto: String): String {
        var digitos = texto.filter { it.isDigit() }
        if (digitos.startsWith("56") && digitos.length >= 11) {
            digitos = digitos.removePrefix("56")
        }
        if (digitos.startsWith("0") && digitos.length >= 9) {
            digitos = digitos.removePrefix("0")
        }
        return digitos
    }

    fun esTelefonoValido(digitos: String): Boolean =
        digitos.length == 9 && digitos.all { it.isDigit() }

    /** Validación simple de correo (contiene @ y un punto después). */
    fun esCorreoValido(correo: String): Boolean {
        val t = correo.trim()
        val at = t.indexOf('@')
        return at > 0 && t.indexOf('.', at) > at + 1 && !t.contains(" ")
    }

    fun validarCorreoODefault(entrada: String): Pair<String, Boolean> {
        val t = entrada.trim()
        return if (esCorreoValido(t)) {
            t to true
        } else {
            CORREO_DEFAULT to false
        }
    }

    /**
     * Nombre de cliente: no vacío, solo letras/espacios/tildes,
     * sin números ni símbolos, y al menos una letra.
     */
    fun esNombreValido(texto: String): Boolean {
        val t = texto.trim()
        if (t.isEmpty()) return false
        if (!t.any { it.isLetter() }) return false
        return t.all { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' }
    }
}
