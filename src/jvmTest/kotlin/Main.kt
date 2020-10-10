package `fun`.sketchcode.api.lib.test

import `fun`.sketchcode.api.lib.SketchcodeClient


const val email = "sokolsokolinskiy@gmail.com"
val client = SketchcodeClient()

suspend fun main() {
    client.getAuthCode(email, "token")
    print("Enter auth code: ")

    client.sendAuthCode(email, readLine()!!.toInt()) {
        success {
            println("Authorized. $it")
        }
        error {
            println("Error: $text")
        }
    }
}
