package service

import model.ItemCarrito
import model.Producto

class Carrito {
    private val items = mutableListOf<ItemCarrito>()

    fun agregarProducto(producto: Producto, cantidad: Int): Boolean{
        if (cantidad <= 0) {
            return false
        }

        val itemExistente = items.find {
            it.producto.nombre.equals(producto.nombre, ignoreCase = true)
        }

        return if (itemExistente != null) {
            itemExistente.cantidadComprada += cantidad
            true
        } else {
            items.add(ItemCarrito(producto, cantidad))
            true
        }
    }

    fun eliminarProducto(nombreProducto: String): Boolean {
        val itemAEliminar = items.find {
            it.producto.nombre.equals(nombreProducto, ignoreCase = true)
        }

        return if (itemAEliminar != null) {
            items.remove(itemAEliminar)
            true
        } else {
            false
        }
    }

    fun mostrarCarrito() {
        if (items.isEmpty()) {
            println("El carrito está vacío.")
            return
        }

        println("===== CARRITO DE COMPRAS =====")
        for (item in items) {
            println("Producto: ${item.producto.nombre}")
            println("Cantidad: ${item.cantidadComprada}")
            println("Precio unitario: $${item.producto.precio}")
            println("Subtotal: $${item.calcularSubtotal()}")
            println("------------------------------")
        }
    }

    fun calcularSubtotal(): Double {
        return items.sumOf { it.calcularSubtotal() }
    }

    fun calcularImpuesto(tasa: Double): Double {
        return calcularSubtotal() * tasa
    }

    fun calcularTotal(tasa: Double): Double {
        return calcularSubtotal() + calcularImpuesto(tasa)
    }

    fun estaVacio(): Boolean {
        return items.isEmpty()
    }

    fun vaciarCarrito() {
        items.clear()
    }

    fun obtenerItems(): List<ItemCarrito> {
        return items
    }
}