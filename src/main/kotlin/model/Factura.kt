package model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Factura(
    private val itemsComprados: List<ItemCarrito>,
    private val subtotal: Double,
    private val impuesto: Double,
    private val total: Double
) {

    fun generarTextoFactura(): String {
        val sb = StringBuilder()
        val fechaHora = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))

        sb.appendLine("==============================================")
        sb.appendLine("              FACTURA ELECTRONICA")
        sb.appendLine("                 PIXEL SHOP")
        sb.appendLine("Fecha: $fechaHora")
        sb.appendLine("==============================================")
        sb.appendLine(String.format("%-20s %5s %10s %10s", "Producto", "Cant.", "P.Unit", "Subtotal"))
        sb.appendLine("==============================================")

        for (item in itemsComprados) {
            sb.appendLine(
                String.format(
                    "%-20s %5d %10.2f %10.2f",
                    item.producto.nombre.take(20),
                    item.cantidadComprada,
                    item.producto.precio,
                    item.calcularSubtotal()
                )
            )
        }

        sb.appendLine("==============================================")
        sb.appendLine(String.format("%-26s %18.2f", "Subtotal general:", subtotal))
        sb.appendLine(String.format("%-26s %18.2f", "Impuesto:", impuesto))
        sb.appendLine(String.format("%-26s %18.2f", "Total general:", total))
        sb.appendLine("==============================================")
        sb.appendLine("Gracias por su compra.")
        sb.appendLine("==============================================")

        return sb.toString()
    }

    fun mostrarFactura() {
        println(generarTextoFactura())
    }
}