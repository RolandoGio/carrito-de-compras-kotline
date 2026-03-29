package service

import model.Factura
import model.Producto
import util.PersistenciaUtil

class Tienda {

    private val inventario = PersistenciaUtil.cargarProductos()
    private val carrito = Carrito()
    private val tasaImpuesto = 0.13

    fun registrarProducto(producto: Producto): Boolean {
        val productoExistente = inventario.find {
            it.nombre.equals(producto.nombre, ignoreCase = true)
        }

        return if (productoExistente == null) {
            inventario.add(producto)
            PersistenciaUtil.guardarProductos(inventario)
            true
        } else {
            false
        }
    }

    fun hayProductos(): Boolean {
        return inventario.isNotEmpty()
    }

    fun mostrarProductos() {
        if (inventario.isEmpty()) {
            println("No hay productos registrados en la tienda.")
            return
        }

        println("=========== PRODUCTOS DISPONIBLES ===========")
        for (producto in inventario) {
            println("Nombre: ${producto.nombre}")
            println("Precio: $${"%.2f".format(producto.precio)}")
            println("Cantidad: ${producto.cantidad}")
            println("Genero: ${producto.genero}")
            println("Plataforma: ${producto.plataforma}")
            println("---------------------------------------------")
        }
    }

    fun buscarProductoPorNombre(nombre: String): Producto? {
        return inventario.find { it.nombre.equals(nombre, ignoreCase = true) }
    }

    fun agregarAlCarrito(nombre: String, cantidad: Int): Boolean {
        val producto = buscarProductoPorNombre(nombre)

        if (producto == null) {
            return false
        }

        if (cantidad <= 0) {
            return false
        }

        val cantidadYaEnCarrito = carrito.obtenerItems()
            .find { it.producto.nombre.equals(nombre, ignoreCase = true) }
            ?.cantidadComprada ?: 0

        if (cantidad + cantidadYaEnCarrito > producto.cantidad) {
            return false
        }

        return carrito.agregarProducto(producto, cantidad)
    }

    fun eliminarDelCarrito(nombre: String): Boolean {
        return carrito.eliminarProducto(nombre)
    }

    fun verCarrito() {
        carrito.mostrarCarrito()

        if (!carrito.estaVacio()) {
            println("Subtotal general: $${"%.2f".format(carrito.calcularSubtotal())}")
            println("Impuesto: $${"%.2f".format(carrito.calcularImpuesto(tasaImpuesto))}")
            println("Total general: $${"%.2f".format(carrito.calcularTotal(tasaImpuesto))}")
        }
    }

    fun confirmarCompra(): Factura? {
        if (carrito.estaVacio()) {
            return null
        }

        actualizarInventario()

        val subtotal = carrito.calcularSubtotal()
        val impuesto = carrito.calcularImpuesto(tasaImpuesto)
        val total = carrito.calcularTotal(tasaImpuesto)

        val factura = Factura(
            carrito.obtenerItems().toList(),
            subtotal,
            impuesto,
            total
        )

        PersistenciaUtil.guardarProductos(inventario)
        PersistenciaUtil.guardarFacturaEnHistorial(factura.generarTextoFactura())

        carrito.vaciarCarrito()
        return factura
    }

    fun editarProducto(
        nombreActual: String,
        nuevoNombre: String,
        nuevoPrecio: Double,
        nuevaCantidad: Int,
        nuevoGenero: String,
        nuevaPlataforma: String
    ): Boolean {
        val producto = buscarProductoPorNombre(nombreActual) ?: return false

        val nombreDuplicado = inventario.any {
            it.nombre.equals(nuevoNombre, ignoreCase = true) &&
                    !it.nombre.equals(nombreActual, ignoreCase = true)
        }

        if (nombreDuplicado) {
            return false
        }

        producto.nombre = nuevoNombre
        producto.precio = nuevoPrecio
        producto.cantidad = nuevaCantidad
        producto.genero = nuevoGenero
        producto.plataforma = nuevaPlataforma

        PersistenciaUtil.guardarProductos(inventario)
        return true
    }

    private fun actualizarInventario() {
        for (item in carrito.obtenerItems()) {
            item.producto.cantidad -= item.cantidadComprada
        }
    }
}