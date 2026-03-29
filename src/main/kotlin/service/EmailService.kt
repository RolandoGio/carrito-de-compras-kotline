package service

import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.Properties

object EmailService {

    private const val REMITENTE = "ronaldorellana20000@gmail.com"
    private const val APP_PASSWORD = "isdxvwjbfaexshgn"

    fun correoValido(correo: String): Boolean {
        return correo.contains("@") && correo.contains(".")
    }

    fun enviarFactura(destinatario: String, contenidoFactura: String): Boolean {
        if (!correoValido(destinatario)) {
            return false
        }

        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        return try {
            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(REMITENTE, APP_PASSWORD)
                }
            })

            val mensaje = MimeMessage(session).apply {
                setFrom(InternetAddress(REMITENTE))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
                subject = "Factura Electronica - Pixel Shop"
                setText(contenidoFactura)
            }

            Transport.send(mensaje)
            true
        } catch (e: Exception) {
            false
        }
    }
}