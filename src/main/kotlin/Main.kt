2import model.Producto
import service.Tienda
import util.LoggerUtil
import service.EmailService

fun main() {
    val tienda = Tienda()
    var salir = false

    while (!salir) {
        mostrarMenu()

        when (leerEntero("Seleccione una opcion: ")) {
            1 -> {
                println("===== REGISTRAR PRODUCTO =====")
                val nombre = leerTextoNoVacio("Ingrese el nombre del videojuego: ")
                val precio = leerDoublePositivo("Ingrese el precio: ")
                val cantidad = leerEnteroPositivo("Ingrese la cantidad disponible: ")
                val genero = leerTextoNoVacio("Ingrese el genero: ")
                val plataforma = leerTextoNoVacio("Ingrese la plataforma (PC, PS5, Xbox, Switch, etc.): ")

                val producto = Producto(nombre, precio, cantidad, genero, plataforma)

                val registrado = tienda.registrarProducto(producto)
                if (registrado) {
                    println("Producto registrado correctamente.")
                } else {
                    println("Ya existe un producto con ese nombre.")
                    LoggerUtil.registrarError("main", "Intento de registrar producto duplicado: $nombre")
                }
            }

            2 -> {
                println("===== VER PRODUCTOS =====")
                tienda.mostrarProductos()
            }

            3 -> {
                println("===== AGREGAR AL CARRITO =====")

                if (!tienda.hayProductos()) {
                    println("No hay productos registrados en la tienda.")
                    LoggerUtil.registrarError("main", "Se intento agregar al carrito sin productos registrados.")
                } else {
                    val nombre = leerTextoNoVacio("Ingrese el nombre del producto que desea agregar: ")
                    val cantidad = leerEnteroPositivo("Ingrese la cantidad a comprar: ")

                    val agregado = tienda.agregarAlCarrito(nombre, cantidad)
                    if (agregado) {
                        println("Producto agregado al carrito correctamente.")
                    } else {
                        println("No se pudo agregar el producto. Verifique nombre y cantidad disponible.")
                        LoggerUtil.registrarError(
                            "main",
                            "No se pudo agregar al carrito el producto '$nombre' con cantidad $cantidad"
                        )
                    }
                }
            }

            4 -> {
                println("===== ELIMINAR DEL CARRITO =====")
                val nombre = leerTextoNoVacio("Ingrese el nombre del producto que desea eliminar: ")

                val eliminado = tienda.eliminarDelCarrito(nombre)
                if (eliminado) {
                    println("Producto eliminado del carrito.")
                } else {
                    println("Ese producto no esta en el carrito.")
                    LoggerUtil.registrarError("main", "Intento de eliminar producto no existente en carrito: $nombre")
                }
            }

            5 -> {
                println("===== VER CARRITO =====")
                tienda.verCarrito()
            }

            6 -> {
                println("===== CONFIRMAR COMPRA =====")
                val factura = tienda.confirmarCompra()

                if (factura == null) {
                    println("No se puede confirmar la compra porque el carrito esta vacio.")
                    LoggerUtil.registrarError("main", "Se intento confirmar una compra con el carrito vacio.")
                } else {
                    println("Compra realizada correctamente.")
                    factura.mostrarFactura()

                    val correoDestino = leerCorreoValido("Ingrese el correo electronico para enviar la factura: ")
                    val enviada = EmailService.enviarFactura(correoDestino, factura.generarTextoFactura())

                    if (enviada) {
                        println("Factura enviada correctamente al correo: $correoDestino")
                    } else {
                        println("No se pudo enviar la factura por correo.")
                        LoggerUtil.registrarError("main", "Fallo el envio de la factura al correo: $correoDestino")
                    }

                    println("Puede seguir comprando si lo desea.")
                }
            }

            7 -> {
                println("===== EDITAR PRODUCTO =====")

                if (!tienda.hayProductos()) {
                    println("No hay productos registrados en la tienda.")
                    LoggerUtil.registrarError("main", "Se intento editar un producto sin inventario registrado.")
                } else {
                    val nombreActual = leerTextoNoVacio("Ingrese el nombre del producto que desea editar: ")

                    if (tienda.buscarProductoPorNombre(nombreActual) == null) {
                        println("No existe un producto con ese nombre.")
                        LoggerUtil.registrarError("main", "Intento de editar producto inexistente: $nombreActual")
                    } else {
                        println("Ingrese los nuevos datos del producto.")
                        val nuevoNombre = leerTextoNoVacio("Nuevo nombre: ")
                        val nuevoPrecio = leerDoublePositivo("Nuevo precio: ")
                        val nuevaCantidad = leerEnteroNoNegativo("Nueva cantidad disponible: ")
                        val nuevoGenero = leerTextoNoVacio("Nuevo genero: ")
                        val nuevaPlataforma = leerTextoNoVacio("Nueva plataforma (PC, PS5, Xbox, Switch, etc.): ")

                        val editado = tienda.editarProducto(
                            nombreActual,
                            nuevoNombre,
                            nuevoPrecio,
                            nuevaCantidad,
                            nuevoGenero,
                            nuevaPlataforma
                        )

                        if (editado) {
                            println("Producto editado correctamente.")
                        } else {
                            println("No se pudo editar el producto. Puede que el nuevo nombre ya exista.")
                            LoggerUtil.registrarError("main", "No se pudo editar el producto: $nombreActual")
                        }
                    }
                }
            }

            8 -> {
                println("Gracias por usar Pixel Shop.")
                salir = true
            }

            else -> {
                println("Opcion invalida. Intente nuevamente.")
                LoggerUtil.registrarError("main", "El usuario ingreso una opcion invalida de menu.")
            }
        }

        println()
    }
}

fun mostrarMenu() {
    println("========== PIXEL SHOP ==========")
    println("1. Registrar producto")
    println("2. Ver productos")
    println("3. Agregar producto al carrito")
    println("4. Eliminar producto del carrito")
    println("5. Ver carrito")
    println("6. Confirmar compra")
    println("7. Editar producto")
    println("8. Salir")
    println("================================")
}

fun leerTextoNoVacio(mensaje: String): String {
    while (true) {
        print(mensaje)
        val entrada = readln().trim()

        if (entrada.isEmpty()) {
            println("La entrada no puede estar vacia.")
            LoggerUtil.registrarError("leerTextoNoVacio", "El usuario dejo una entrada vacia.")
            continue
        }

        if (entrada.contains("|")) {
            println("No puede usar el caracter | en este campo.")
            LoggerUtil.registrarError("leerTextoNoVacio", "El usuario ingreso el caracter prohibido |")
            continue
        }

        return entrada
    }
}

fun leerEntero(mensaje: String): Int {
    while (true) {
        print(mensaje)
        val entrada = readln().trim()

        try {
            return entrada.toInt()
        } catch (e: NumberFormatException) {
            println("Debe ingresar un numero entero valido.")
            LoggerUtil.registrarError("leerEntero", "Entrada invalida para entero: '$entrada'")
        }
    }
}

fun leerEnteroPositivo(mensaje: String): Int {
    while (true) {
        val numero = leerEntero(mensaje)

        if (numero > 0) {
            return numero
        }

        println("Debe ingresar un numero mayor que 0.")
        LoggerUtil.registrarError("leerEnteroPositivo", "Se ingreso un entero no positivo: $numero")
    }
}

fun leerDoublePositivo(mensaje: String): Double {
    while (true) {
        print(mensaje)
        val entrada = readln().trim()

        try {
            val numero = entrada.toDouble()
            if (numero > 0) {
                return numero
            }

            println("Debe ingresar un numero mayor que 0.")
            LoggerUtil.registrarError("leerDoublePositivo", "Se ingreso un decimal no positivo: '$entrada'")
        } catch (e: NumberFormatException) {
            println("Debe ingresar un numero decimal valido.")
            LoggerUtil.registrarError("leerDoublePositivo", "Entrada invalida para decimal: '$entrada'")
        }
    }
}

fun leerCorreoValido(mensaje: String): String {
    while (true) {
        print(mensaje)
        val correo = readln().trim()

        if (EmailService.correoValido(correo)) {
            return correo
        }

        println("Debe ingresar un correo valido.")
        LoggerUtil.registrarError("leerCorreoValido", "Correo invalido ingresado: '$correo'")
    }
}

fun leerEnteroNoNegativo(mensaje: String): Int {
    while (true) {
        val numero = leerEntero(mensaje)

        if (numero >= 0) {
            return numero
        }

        println("Debe ingresar un numero mayor o igual que 0.")
        LoggerUtil.registrarError("leerEnteroNoNegativo", "Se ingreso un entero negativo: $numero")
    }
}