package pt.noticias.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Response(
    var status: String? = null
)