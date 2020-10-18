package `fun`.sketchcode.api.lib.test

import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long


suspend fun main() {
    client.getAuthCode(email, "token")

    for(i in 1..3)
        client.sendAuthCode(email, 10000)

    client.getAuthCode(email, "token") {
        error {
            println(data.jsonPrimitive.long)
        }
    }
}
