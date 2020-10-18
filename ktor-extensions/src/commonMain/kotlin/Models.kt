package `fun`.sketchcode.api.lib.ktor

import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonElement


@Serializable
data class ServerErrorModel(
    val code: Int,
    val text: String,
    val data: JsonElement
)

data class ResponseScope<T>(
    val body: T?,
    val errorBody: ServerErrorModel?,
    val throwable: Throwable?
) {
    inline fun success(lambda: (body: T) -> Unit) = apply {
        if (body != null) lambda(body)
    }

    inline fun error(lambda: ServerErrorModel.() -> Unit) = apply {
        if (errorBody != null) lambda(errorBody)
    }

    inline fun failure(lambda: (Throwable) -> Unit) = apply {
        if (throwable != null) lambda(throwable)
    }
}
