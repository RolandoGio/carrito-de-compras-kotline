package util

import model.Producto
import java.io.File

object PersistenciaUtil {

    private const val ARCHIVO_PRODUCTOS = "productos.txt"
    private const val ARCHIVO_FACTURAS = "facturas_historial.txt"

    fun cargarProductos(): MutableList<Producto> {
        val productos = mutableListOf<Producto>()
        val archivo = File(ARCHIVO_PRODUCTOS)

        if (!archivo.exists()) {
            return productos
        }

        return try {
            archivo.forEachLine { linea ->
                if (linea.isNotBlank()) {
                    val partes = linea.split("|")

                    if (partes.size == 5) {
                        val nombre = partes[0]
                        val precio = partes[1].toDoubleOrNull()
                        val cantidad = partes[2].toIntOrNull()
                        val genero = partes[3]
                        val plataforma = partes[4]

                        if (precio != null && cantidad != null) {
                            productos.add(
                                Producto(nombre, precio, cantidad, genero, plataforma)
                            )
                        } else {
                            LoggerUtil.registrarError(
                                "PersistenciaUtil.cargarProductos",
                                "Linea invalida en productos.txt: $linea"
                            )
                        }
                    } else {
                        LoggerUtil.registrarError(
                            "PersistenciaUtil.cargarProductos",
                            "Formato incorrecto en productos.txt: $linea"
                        )
                    }
                }
            }
            productos
        } catch (e: Exception) {
            LoggerUtil.registrarExcepcion("PersistenciaUtil.cargarProductos", e)
            mutableListOf()
        }
    }

    fun guardarProductos(productos: List<Producto>) {
        val archivo = File(ARCHIVO_PRODUCTOS)

        try {
            archivo.printWriter().use { writer ->
                productos.forEach { producto ->
                    writer.println(
                        "${producto.nombre}|${producto.precio}|${producto.cantidad}|${producto.genero}|${producto.plataforma}"
                    )
                }
            }
        } catch (e: Exception) {
            LoggerUtil.registrarExcepcion("PersistenciaUtil.guardarProductos", e)
        }
    }

    fun guardarFacturaEnHistorial(facturaTexto: String) {
        val archivo = File(ARCHIVO_FACTURAS)

        try {
            archivo.appendText(facturaTexto)
            archivo.appendText("\n\n")
        } catch (e: Exception) {
            LoggerUtil.registrarExcepcion("PersistenciaUtil.guardarFacturaEnHistorial", e)
        }
    }
}