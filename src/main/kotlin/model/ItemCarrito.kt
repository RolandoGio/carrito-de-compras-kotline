package model

data class ItemCarrito(
    val producto: Producto,
    var cantidadComprada: Int
) {
    fun calcularSubtotal(): Double {
        return producto.precio * cantidadComprada
    }
}