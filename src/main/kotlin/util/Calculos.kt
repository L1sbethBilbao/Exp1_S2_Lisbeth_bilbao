package util

import modelo.Producto

data class ResultadoTotales(
    val subtotal: Double,
    val descuento: Double,
    val impuesto: Double,
    val total: Double,
    val aplicaDescuento: Boolean
)

object Calculos {
    const val IMPUESTO = 0.19
    const val DESCUENTO = 0.10

    /**
     * Calcula subtotal, descuento 10% si cantidad > 3, impuesto 19% sobre
     * (subtotal - descuento) y total final.
     */
    fun calcularTotales(productos: List<Producto>, cantidad: Int): ResultadoTotales {
        if (cantidad <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor que 0")
        }
        val precioUnitario = productos.sumOf { it.precio }
        val subtotal = precioUnitario * cantidad
        val aplicaDescuento = cantidad > 3
        val descuento = if (aplicaDescuento) subtotal * DESCUENTO else 0.0
        val base = subtotal - descuento
        val impuesto = base * IMPUESTO
        val total = base + impuesto
        return ResultadoTotales(subtotal, descuento, impuesto, total, aplicaDescuento)
    }
}
