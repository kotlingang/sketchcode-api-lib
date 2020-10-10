@file:Suppress("EXPERIMENTAL_API_USAGE")

package `fun`.sketchcode.api.lib.ktor

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


suspend inline fun <reified T> HttpClient.request(
    scheme: String = "http", host: String = "localhost", port: Int = DEFAULT_PORT,
    path: String = "/",
    body: Any = EmptyContent,
    method: HttpMethod,
    interceptor: ResponseScope<T>.() -> Unit = {},
    block: HttpRequestBuilder.() -> Unit = {}
): ResponseScope<T> = try {
    ResponseScope(body = request<T> {
        this.method = method
        this.body = body

        url(scheme, host, port, path)
        accept(ContentType.Any)

        apply(block)
    }, errorBody = null)
} catch (re: ResponseException) {
    ResponseScope<T>(
        body = null,
        errorBody = Json.decodeFromString(re.response.readText(Charset.forName("UTF-8")))
    )
}.apply(interceptor)
