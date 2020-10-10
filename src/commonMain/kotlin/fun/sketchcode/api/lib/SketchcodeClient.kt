package `fun`.sketchcode.api.lib

import `fun`.sketchcode.api.lib.ktor.ResponseScope
import `fun`.sketchcode.api.lib.ktor.request
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*


class SketchcodeClient(private val baseUrl: String = "https://api.sketchcode.fun/") {

    private val client = HttpClient {
//        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL
//        }
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        addDefaultResponseValidation()
    }

    /**
     * Get authorization code for creating token. Code sends to email.
     * @param email - User email. Max - 350 symbols.
     * @param recaptchaToken - Recaptcha verification token
     */
    suspend fun getAuthCode(email: String, recaptchaToken: String, interceptor: ResponseScope<Unit>.() -> Unit = {}) =
            client.request(method = HttpMethod.Get, interceptor = interceptor) {
                url("$baseUrl/users/tokens/code")

                parameter("email", email)
                parameter("recaptchaToken", recaptchaToken)
            }

    /**
     * Verify authorization code.
     * @param code - Received code from mail.
     * @param email - Email to which was sent a code.
     * @return Authorization token
     */
    suspend fun sendAuthCode(email: String, code: Int, interceptor: ResponseScope<TokenModel>.() -> Unit = {}) =
            client.request(method = HttpMethod.Post, interceptor = interceptor) {
                url("$baseUrl/users/tokens")
                contentType(ContentType.Application.Json)

                parameter("email", email)
                parameter("code", code)
            }

    /**
     * Get info about token.
     * @param tokenHex - token in hex
     */
    suspend fun getToken(tokenHex: String, interceptor: ResponseScope<TokenModel>.() -> Unit = {}) =
            client.request(method = HttpMethod.Get, interceptor = interceptor) {
                url("$baseUrl/users/tokens")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
            }

    /**
     * Get user by [userId].
     * @param tokenHex - token in hex
     * @param userId - User id
     */
    suspend fun getUser(tokenHex: String, userId: Long, interceptor: ResponseScope<UserModel>.() -> Unit = {}) =
            client.request(method = HttpMethod.Get, interceptor = interceptor) {
                url("$baseUrl/users/$userId")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
            }

    /**
     * Get list of users.
     * @param n - Number of rows to load
     * @param offset - Number from what load starts
     * @param searchQuery - Search string query
     */
    suspend fun getUsers(
            n: Int,
            offset: Long?,
            searchQuery: String?,
            interceptor: ResponseScope<List<UserModel>>.() -> Unit = {}
    ) = client.request(method = HttpMethod.Get, interceptor = interceptor) {
        url("$baseUrl/users")
        contentType(ContentType.Application.Json)
        body = FilterModel(n, offset, searchQuery)
    }

    /**
     * Edit user.
     * @param avatar - uploaded image hex.
     * @param bio - User description (max 300 symbols).
     * @param shortname - User shortname (ex: @shortname, sketchcode.fun/users/[shortname]),
     * max - 20 symbols.
     * @param userName - User name (max 50 symbols).
     * @param tokenHex - user authorization hex
     */
    suspend fun editUser(
            avatar: String,
            bio: String,
            shortname: String,
            userName: String,
            tokenHex: String,
            interceptor: ResponseScope<Unit>.() -> Unit = {}
    ) = client.request<Unit>(method = HttpMethod.Patch, interceptor = interceptor) {
        url("$baseUrl/users")
        contentType(ContentType.Application.Json)
        header("tokenHex", tokenHex)
        body = UserEditParamsModel(avatar, bio, shortname, userName)
    }

    /**
     * Create post.
     * [attachmentHexes] can be null if [text] not null and vice versa.
     * @param attachmentHexes - list of file hexes (max - 6 attachments).
     * @param replyToPostHex - post to which you respond.
     * @param text - post content text (max length - 15895).
     */
    suspend fun createPost(
            attachmentHexes: List<String>? = null,
            replyToPostHex: String? = null,
            text: String? = null,
            tokenHex: String,
            interceptor: ResponseScope<Unit>.() -> Unit = {}
    ) = client.request(method = HttpMethod.Post, interceptor = interceptor) {
        url("$baseUrl/wall")
        contentType(ContentType.Application.Json)
        header("tokenHex", tokenHex)
        body = CreatePostBodyModel(attachmentHexes, replyToPostHex, text)
    }

    /**
     * Like post. If post was already likes, there won't be any error.
     * @param postHex - Post unique hex
     * @param tokenHex - Authorization token hex.
     */
    suspend fun likePost(postHex: String, tokenHex: String, interceptor: ResponseScope<Unit>.() -> Unit = {}) =
            client.request(method = HttpMethod.Post, interceptor = interceptor) {
                url("$baseUrl/wall/likes/$postHex")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
            }

    /**
     * Unlike post. If post was already likes, there won't be any error.
     * @param postHex - Post unique hex
     * @param tokenHex - Authorization token hex.
     */
    suspend fun unlikePost(postHex: String, tokenHex: String, interceptor: ResponseScope<Unit>.() -> Unit = {}) =
            client.request(method = HttpMethod.Delete, interceptor = interceptor) {
                url("$baseUrl/wall/likes/$postHex")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
            }

    /**
     * @param fileBytes - File content bytes
     * @param tokenHex = Authorization token hex
     */
    suspend fun uploadFile(fileBytes: ByteArray, tokenHex: String, interceptor: ResponseScope<String>.() -> Unit = {}) =
            client.request(method = HttpMethod.Post, interceptor = interceptor) {
                url("$baseUrl/files")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
                body = fileBytes
            }


}