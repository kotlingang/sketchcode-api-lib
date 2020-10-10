package `fun`.sketchcode.api.lib.ktor

import kotlinx.serialization.Serializable


@Serializable
data class ServerErrorModel(
    val code: Int,
    val text: String
)

data class ResponseScope<T>(
    val body: T?,
    val errorBody: ServerErrorModel?
) {
    inline fun success(lambda: (body: T) -> Unit) = apply {
        if (body != null) lambda(body)
    }

    inline fun error(lambda: ServerErrorModel.() -> Unit) = apply {
        if (errorBody != null) lambda(errorBody)
    }
}
