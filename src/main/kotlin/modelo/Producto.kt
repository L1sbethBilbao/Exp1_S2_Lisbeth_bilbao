package modelo

/**
 * Producto (pizza) con nombre, precio e ingredientes necesarios por unidad.
 */
class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: List<String>
) {
    fun detallarProducto(): String {
        val lista = ingredientes.joinToString(", ")
        return "$nombre  —  $${precio.toInt()}\n     Ingredientes: $lista"
    }
}
