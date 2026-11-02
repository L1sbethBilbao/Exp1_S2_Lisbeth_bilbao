package modelo

import util.Calculos
import util.ResultadoTotales

/**
 * Pedido con cliente, productos, cantidad, estado, notas y repartidor opcional.
 * Si el pedido se cancela por stock, repartidor queda null (sin asignar).
 */
class Pedido(
    val id: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    var cantidad: Int,
    var estado: String = "En preparación",
    val notas: String? = null,
    var repartidor: Repartidor? = null
) {
    fun calcularTotales(): ResultadoTotales = Calculos.calcularTotales(productos, cantidad)

    fun cambiarEstado(nuevoEstado: String) {
        estado = nuevoEstado
    }

    fun etiquetaRepartidor(): String = repartidor?.nombre ?: "Sin asignar"

    fun notasMostrar(): String = notas?.takeIf { it.isNotBlank() } ?: "Sin notas"
}
