package modelo

/**
 * Clase base con atributos comunes (nombre y contacto).
 * open permite herencia hacia Cliente y Repartidor.
 */
open class Usuario(
    open val nombre: String,
    open val contacto: String
) {
    open fun describir(): String = "Usuario: $nombre | Contacto: $contacto"
}

/** Cliente de la pizzería (especialización de Usuario). */
class Cliente(
    override val nombre: String,
    override val contacto: String,
    val telefono: String,
    val correo: String,
    val direccion: String
) : Usuario(nombre, contacto) {
    override fun describir(): String =
        "Cliente: $nombre | Tel: $telefono | Correo: $correo | Dir: $direccion"
}

/**
 * Repartidor con distancia aproximada (km) para poder usar find
 * y asignar el más cercano.
 */
class Repartidor(
    override val nombre: String,
    override val contacto: String,
    val distanciaKm: Double
) : Usuario(nombre, contacto) {

    fun entregarPedido(idPedido: Int): String =
        "Repartidor $nombre inicia entrega del pedido #$idPedido"

    override fun describir(): String =
        "Repartidor: $nombre | Contacto: $contacto | Distancia: ${distanciaKm} km"
}
