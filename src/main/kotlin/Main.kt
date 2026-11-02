/**
 * Exp1_S2_Lisbeth_bilbao
 * Actividad formativa Semana 2 — Aplicando utilidades de Kotlin
 * Caso: Pizzería — flujos, colecciones, POO, errores, null safety y resumen
 */

import modelo.Cliente
import modelo.Pedido
import modelo.Producto
import modelo.Repartidor
import util.Calculos
import util.Inventario
import util.Validaciones
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.MalformedInputException

val consola: BufferedReader =
    BufferedReader(InputStreamReader(System.`in`, Charset.defaultCharset()))

fun main() {
    tituloPrincipal("PIZZERÍA DUOC — Semana 2")

    val inventario = mutableMapOf(
        "masa" to 20,
        "queso" to 15,
        "pepperoni" to 12,
        "tomate" to 18,
        "albahaca" to 10
    )

    val pepperoni = Producto(
        nombre = "Pepperoni",
        precio = 8990.0,
        ingredientes = listOf("masa", "queso", "pepperoni")
    )
    val margarita = Producto(
        nombre = "Margarita",
        precio = 7990.0,
        ingredientes = listOf("masa", "queso", "tomate", "albahaca")
    )
    val catalogo = listOf(pepperoni, margarita)

    val repartidores = listOf(
        Repartidor("Carlos Muñoz", "912111111", 3.2),
        Repartidor("María Soto", "912222222", 1.5),
        Repartidor("Pedro Rivas", "912333333", 4.8)
    )

    val pedidosDelDia = arrayOfNulls<Pedido>(10)
    var cantidadPedidos = 0
    var siguienteId = 2001

    // --- 1. Cliente ---
    seccion("1. Registro de cliente")
    val nombre = pedirNombre()
    val direccion = pedirDireccion()
    val telefono = pedirTelefono()
    val correo = pedirCorreo()
    val notasEntrada = pedirNotas()

    val cliente = Cliente(
        nombre = nombre,
        contacto = telefono,
        telefono = telefono,
        correo = correo,
        direccion = direccion
    )

    println()
    println("Cliente registrado correctamente")
    println()
    println("  Nombre    : ${cliente.nombre}")
    println("  Dirección : ${cliente.direccion}")
    println("  Teléfono  : ${cliente.telefono}")
    println("  Correo    : ${cliente.correo}")

    val notas: String? = notasEntrada
    notas?.takeIf { it.isNotBlank() }?.let {
        println()
        notificar("Se guardaron tus notas: \"$it\"")
    }

    // --- 2. Producto ---
    seccion("2. Elegir producto")
    catalogo.forEachIndexed { index, p ->
        println("  ${index + 1}) ${p.detallarProducto()}")
        println()
    }
    val productoElegido = pedirProducto(catalogo)
    val cantidad = pedirCantidad()

    val ingredientesUnicos: Set<String> = productoElegido.ingredientes.toSet()
    println()
    println("Pedido elegido")
    println("  Pizza         : ${productoElegido.nombre}")
    println("  Cantidad      : $cantidad")
    println("  Ingredientes  : ${ingredientesUnicos.joinToString(", ")}  (Set)")

    // --- 3. Inventario ---
    seccion("3. Verificación de inventario")
    println("Stock actual:")
    println(Inventario.formatear(inventario))
    println()

    val stock = Inventario.verificarDisponibilidad(
        inventario = inventario,
        ingredientesPedido = productoElegido.ingredientes,
        cantidadPizzas = cantidad
    )
    println(stock.mensaje)

    val pedido = Pedido(
        id = siguienteId++,
        cliente = cliente,
        productos = listOf(productoElegido),
        cantidad = cantidad,
        notas = notas
    )

    if (stock.disponible) {
        Inventario.actualizarInventario(
            inventario = inventario,
            ingredientesPedido = productoElegido.ingredientes,
            cantidadPizzas = cantidad
        )
        println()
        println("Stock actualizado:")
        println(Inventario.formatear(inventario))

        // --- 4. Totales ---
        val totales = pedido.calcularTotales()
        seccion("4. Cálculo del pedido")
        println("  Subtotal      : $${totales.subtotal.toInt()}")
        if (totales.aplicaDescuento) {
            println("  Descuento 10% : -$${totales.descuento.toInt()}")
            println("                  (más de 3 pizzas)")
        } else {
            println("  Descuento     : $0")
            println("                  (3 o menos pizzas)")
        }
        println("  Impuesto 19%  : $${totales.impuesto.toInt()}")
        println("  ----------------------------")
        println("  TOTAL         : $${totales.total.toInt()}")

        // --- 5. Repartidor ---
        val masCercano = repartidores.minByOrNull { it.distanciaKm }
        val asignado = repartidores.find { it.distanciaKm == masCercano?.distanciaKm }
        pedido.repartidor = asignado
        pedido.cambiarEstado("En preparación")

        seccion("5. Asignación de repartidor")
        asignado?.let { r ->
            println("  Repartidor : ${r.nombre}")
            println("  Distancia  : ${r.distanciaKm} km (más cercano)")
            println()
            println("  Queda reservado.")
            println("  La entrega inicia solo cuando el estado sea En camino.")
        }
        println()
        println("  Estado actual: ${pedido.estado}")

        // --- 6. Estados ---
        actualizarEstadoPedido(pedido)
    } else {
        pedido.cambiarEstado("Cancelado")
        pedido.repartidor = null
        println()
        println("Pedido NO confirmado")
        println("  Estado     : ${pedido.estado}")
        println("  Repartidor : Sin asignar")
    }

    if (cantidadPedidos < pedidosDelDia.size) {
        pedidosDelDia[cantidadPedidos] = pedido
        cantidadPedidos++
    }

    imprimirResumen(pedido)
    imprimirReporteDelDia(pedidosDelDia, cantidadPedidos)

    println()
    println("----------------------------------------")
    println("  Gracias por preferir Pizzería Duoc")
    println("----------------------------------------")
    println()
}

// ---------- Presentación en consola ----------

fun tituloPrincipal(texto: String) {
    println()
    println("========================================")
    println("  $texto")
    println("========================================")
    println()
}

fun seccion(titulo: String) {
    println()
    println("----------------------------------------")
    println("  $titulo")
    println("----------------------------------------")
    println()
}

fun notificar(mensaje: String) {
    println("  [Aviso al cliente] $mensaje")
}

fun leerEntrada(): String {
    return try {
        consola.readLine()?.trim().orEmpty()
    } catch (e: MalformedInputException) {
        println("  Error: carácter no válido. Intente de nuevo.")
        ""
    }
}

fun pedirNombre(): String {
    var nombre: String
    do {
        print("  Nombre: ")
        nombre = leerEntrada()
        if (!Validaciones.esNombreValido(nombre)) {
            println("  Error: solo letras (sin números).")
        }
    } while (!Validaciones.esNombreValido(nombre))
    return nombre
}

fun pedirDireccion(): String {
    var direccion: String
    do {
        print("  Dirección de entrega: ")
        direccion = leerEntrada()
        if (direccion.isEmpty()) {
            println("  Error: la dirección no puede estar vacía.")
        }
    } while (direccion.isEmpty())
    return direccion
}

fun pedirTelefono(): String {
    var telefono = ""
    do {
        print("  Teléfono: ")
        val telefonoIngresado = leerEntrada()
        val normalizado = Validaciones.normalizarTelefono(telefonoIngresado)

        if (telefonoIngresado.isEmpty()) {
            println("  Error: teléfono vacío.")
        } else if (!Validaciones.esTelefonoValido(normalizado)) {
            println("  Error: use 9 dígitos (ej. 912345678).")
        } else {
            telefono = normalizado
        }
    } while (telefono.isEmpty())
    return telefono
}

fun pedirCorreo(): String {
    print("  Correo: ")
    val entrada = leerEntrada()
    val (valor, ok) = Validaciones.validarCorreoODefault(entrada)
    if (!ok) {
        println("  Correo inválido → se usará: $valor")
    }
    return valor
}

fun pedirNotas(): String? {
    print("  Notas (Enter = sin notas): ")
    val entrada = leerEntrada()
    return entrada.ifBlank { null }
}

fun pedirProducto(catalogo: List<Producto>): Producto {
    while (true) {
        print("  Elija producto (1-${catalogo.size}): ")
        try {
            val opcion = leerEntrada().toInt()
            if (opcion in 1..catalogo.size) {
                return catalogo[opcion - 1]
            }
            println("  Error: opción fuera de rango.")
        } catch (e: NumberFormatException) {
            println("  Error: ingrese un número.")
        }
    }
}

fun pedirCantidad(): Int {
    while (true) {
        print("  Cantidad de pizzas: ")
        try {
            val cantidad = leerEntrada().toInt()
            if (cantidad <= 0) {
                throw IllegalArgumentException("debe ser mayor que 0")
            }
            if (cantidad > 100) {
                throw IllegalArgumentException("cantidad demasiado alta")
            }
            return cantidad
        } catch (e: NumberFormatException) {
            println("  Error: cantidad no válida.")
        } catch (e: IllegalArgumentException) {
            println("  Error: ${e.message}.")
        }
    }
}

/**
 * Actualiza el estado varias veces hasta En camino o hasta ir al resumen.
 */
fun actualizarEstadoPedido(pedido: Pedido) {
    seccion("6. Actualizar estado del pedido")
    println("  Recorrido sugerido:")
    println("  En preparación  →  Listo  →  En camino")
    println()

    while (true) {
        println("  Estado actual: ${pedido.estado}")
        println()
        println("  1) En preparación")
        println("  2) Listo")
        println("  3) En camino")
        println("  4) Ir al resumen")
        println()
        print("  Opción (1-4): ")
        val opcion = leerEntrada().toIntOrNull() ?: 4

        when (opcion) {
            1 -> {
                val anterior = pedido.estado
                if (anterior == "En preparación") {
                    println()
                    notificar("Pedido #${pedido.id}: sigue En preparación.")
                } else {
                    cambiarEstadoYNotificar(pedido, anterior, "En preparación")
                }
            }
            2 -> {
                val anterior = pedido.estado
                if (anterior == "Listo") {
                    println()
                    notificar("Pedido #${pedido.id}: ya está Listo.")
                } else {
                    cambiarEstadoYNotificar(pedido, anterior, "Listo")
                }
            }
            3 -> {
                val anterior = pedido.estado
                if (anterior != "En camino") {
                    cambiarEstadoYNotificar(pedido, anterior, "En camino")
                    iniciarEntregaSiCorresponde(pedido)
                } else {
                    println()
                    notificar("Pedido #${pedido.id}: ya va En camino.")
                }
            }
            else -> {
                println()
                println("  Pasando al resumen...")
                return
            }
        }

        println()

        if (pedido.estado == "En camino") {
            print("  ¿Ir al resumen ahora? (s/n): ")
            val irResumen = leerEntrada().lowercase()
            if (irResumen == "s" || irResumen == "si" || irResumen == "sí" || irResumen.isEmpty()) {
                println()
                println("  Pasando al resumen...")
                return
            }
            println()
        }
    }
}

fun cambiarEstadoYNotificar(pedido: Pedido, estadoAnterior: String, estadoNuevo: String) {
    pedido.cambiarEstado(estadoNuevo)
    println()
    println("  Cambio: $estadoAnterior  →  $estadoNuevo")
    pedido.cliente.nombre.let { nombreCliente ->
        notificar("$nombreCliente, su pedido #${pedido.id} ahora está: $estadoNuevo")
    }
}

fun iniciarEntregaSiCorresponde(pedido: Pedido) {
    if (pedido.estado != "En camino") return
    pedido.repartidor?.let { r ->
        println()
        println("  ${r.entregarPedido(pedido.id)}")
        notificar("Su pedido va En camino con ${r.nombre}.")
    }
}

fun imprimirResumen(pedido: Pedido) {
    val infoTotales = try {
        Calculos.calcularTotales(pedido.productos, pedido.cantidad)
    } catch (e: Exception) {
        null
    }

    val producto = pedido.productos.first()

    seccion("7. Resumen del pedido")
    println("  ID pedido   : ${pedido.id}")
    println()
    println("  Cliente")
    println("    Nombre    : ${pedido.cliente.nombre}")
    println("    Dirección : ${pedido.cliente.direccion}")
    println("    Teléfono  : ${pedido.cliente.telefono}")
    println("    Correo    : ${pedido.cliente.correo}")
    println("    Notas     : ${pedido.notasMostrar()}")
    println()
    println("  Pedido")
    println("    Producto  : ${producto.nombre}")
    println("    Precio    : $${producto.precio.toInt()}")
    println("    Cantidad  : ${pedido.cantidad}")
    infoTotales?.let { t ->
        println()
        println("  Totales")
        println("    Subtotal  : $${t.subtotal.toInt()}")
        println("    Descuento : $${t.descuento.toInt()}")
        println("    Impuesto  : $${t.impuesto.toInt()}")
        println("    TOTAL     : $${t.total.toInt()}")
    }
    println()
    println("  Estado     : ${pedido.estado}")
    println("  Repartidor : ${pedido.etiquetaRepartidor()}")
}

fun imprimirReporteDelDia(pedidos: Array<Pedido?>, cantidad: Int) {
    seccion("8. Reporte del día")
    if (cantidad == 0) {
        println("  No hay pedidos registrados.")
        return
    }

    for (i in 0 until cantidad) {
        val p = pedidos[i] ?: continue
        val prod = p.productos.firstOrNull()?.nombre ?: "N/A"
        val totalTxt = try {
            "$${p.calcularTotales().total.toInt()}"
        } catch (e: Exception) {
            "N/A"
        }
        println("  [${i + 1}] ${p.cliente.nombre}")
        println("      $prod x${p.cantidad}  |  $totalTxt  |  ${p.estado}")
        println()
    }

    val lista = pedidos.filterNotNull().take(cantidad)
    val completados = lista.filter {
        it.estado == "Listo" || it.estado == "En camino" || it.estado == "Entregado"
    }
    val pendientes = lista.filter { it.estado == "En preparación" }
    val cancelados = lista.filter { it.estado == "Cancelado" }

    println("  Pendientes  : ${pendientes.size}")
    println("  Completados : ${completados.size}")
    println("  Cancelados  : ${cancelados.size}")
}
