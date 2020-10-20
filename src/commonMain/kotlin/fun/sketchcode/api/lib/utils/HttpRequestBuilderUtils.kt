package `fun`.sketchcode.api.lib.utils

import io.ktor.client.request.*


internal fun HttpRequestBuilder.parameterNotNull(name: String, value: Any?)
        = value?.let { parameter(name, value) }
