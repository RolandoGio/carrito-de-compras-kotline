package util

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LoggerUtil {

    private val archivoLog = "errores_log.txt"
    private val formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun registrarError(origen: String, mensaje: String) {
        val fechaHora = LocalDateTime.now().format(formatoFecha)
        val linea = "$fechaHora | ERROR | $origen | $mensaje\n"

        File(archivoLog).appendText(linea)
    }

    fun registrarExcepcion(origen: String, e: Exception) {
        val fechaHora = LocalDateTime.now().format(formatoFecha)
        val linea = "$fechaHora | EXCEPCION | $origen | ${e.message}\n"

        File(archivoLog).appendText(linea)
    }
}