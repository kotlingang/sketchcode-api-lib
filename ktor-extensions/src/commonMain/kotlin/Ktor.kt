@file:Suppress("EXPERIMENTAL_API_USAGE")

package `fun`.sketchcode.api.lib.ktor

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext


suspend inline fun <reified T> HttpClient.request(
    scheme: String = "http", host: String = "localhost", port: Int = DEFAULT_PORT,
    path: String = "/",
    body: Any = EmptyContent,
    method: HttpMethod,
    coroutineContext: CoroutineContext,
    interceptor: ResponseScope<T>.() -> Unit = {},
    crossinline block: HttpRequestBuilder.() -> Unit = {},
): ResponseScope<T> = withContext(coroutineContext) {
    requestPrivate<T>(scheme, host, port, path, body, method, block)
}.apply(interceptor)

suspend inline fun <reified T> HttpClient.requestPrivate(
    scheme: String, host: String, port: Int,
    path: String,
    body: Any = EmptyContent,
    method: HttpMethod,
    block: HttpRequestBuilder.() -> Unit = {}
): ResponseScope<T> = try {
    ResponseScope(body = request<T> {
        this.method = method
        this.body = body

        url(scheme, host, port, path)
        accept(ContentType.Any)

        apply(block)
    }, errorBody = null, throwable = null)
} catch (re: ResponseException) {
    ResponseScope(
        body = null,
        throwable = null,
        errorBody = json.decodeFromString<ServerErrorModel>(re.response.readText(Charset.forName("UTF-8")))
    )
} catch (t: Throwable) {
    ResponseScope(
        body = null,
        errorBody = null,
        throwable = t
    )
}
