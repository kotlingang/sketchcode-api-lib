@file:Suppress("EXPERIMENTAL_API_USAGE")

package `fun`.sketchcode.api.lib

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
        block: HttpRequestBuilder.() -> Unit = {}
): ResponseScope<T> {
    return try {
        ResponseScope(body = request<T> {
            url(scheme, host, port, path)
            this.method = method
            this.body = body
            apply(block)
        }, errorBody = null)
    } catch (rpe: RedirectResponseException) {
        ResponseScope(
                body = null,
                errorBody = Json.decodeFromString(
                        rpe.response.readText(Charset.forName("UTF-8"))
                )
        )
    } catch (cre: ClientRequestException) {
        ResponseScope(
                body = null,
                errorBody = Json.decodeFromString(
                        cre.response.readText(Charset.forName("UTF-8"))
                )
        )
    } catch (sre: ServerResponseException) {
        ResponseScope(
                body = null,
                errorBody = Json.parse(
                        ServerErrorModel.serializer(),
                        sre.response.readText(Charset.forName("UTF-8"))
                )
        )
    }
}