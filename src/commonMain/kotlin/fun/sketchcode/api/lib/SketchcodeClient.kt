package `fun`.sketchcode.api.lib

import `fun`.sketchcode.api.lib.ktor.ResponseScope
import `fun`.sketchcode.api.lib.ktor.json
import `fun`.sketchcode.api.lib.ktor.request
import `fun`.sketchcode.api.lib.utils.parameterNotNull
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


class SketchcodeClient(
    private val logging: Boolean = false,
    private val baseUrl: String = "https://api.sketchcode.fun",
) {
    private val apiVersion = 1

    private val client = HttpClient {
        if(logging) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }
        install(DefaultRequest) {
            header("Api-Version", apiVersion)
            contentType(ContentType.Application.Json)
        }
        addDefaultResponseValidation()
    }

    /**
     * Get authorization code for creating token. Code sends to email.
     * @param email - User email. Max - 350 symbols.
     * @param recaptchaToken - Recaptcha verification token
     */
    suspend fun getAuthCode(email: String, recaptchaToken: String, interceptor: ResponseScope<Unit>.() -> Unit = {})
            = client.request(method = HttpMethod.Get, interceptor = interceptor) {
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
    suspend fun sendAuthCode(email: String, code: Int, interceptor: ResponseScope<TokenModel>.() -> Unit = {})
            = client.request(method = HttpMethod.Post, interceptor = interceptor) {
        url("$baseUrl/users/tokens")

        parameter("email", email)
        parameter("code", code)
    }

    /**
     * Get info about token.
     * @param tokenHex - token in hex
     */
    suspend fun getToken(tokenHex: String, interceptor: ResponseScope<TokenModel>.() -> Unit = {})
            = client.request(method = HttpMethod.Get, interceptor = interceptor) {
        url("$baseUrl/users/tokens")
        header("tokenHex", tokenHex)
    }

    /**
     * Get user by [userHex].
     * @param tokenHex - token in hex
     * @param userHex - User hex
     */
    suspend fun getUser(
            tokenHex: String? = null, userHex: String? = null, interceptor: ResponseScope<UserModel>.() -> Unit = {}
    ) = client.request(method = HttpMethod.Get, interceptor = interceptor) {
        url("$baseUrl/users/${userHex ?: ""}")
        header("tokenHex", tokenHex)
    }

    /**
     * Get list of users.
     * @param limit - Number of rows to load
     * @param offset - Number from what load starts
     * @param searchQuery - Search string query
     */
    suspend fun searchUsers(
            limit: Int? = null,
            offset: Long? = null,
            searchQuery: String? = null,
            interceptor: ResponseScope<List<UserModel>>.() -> Unit = {}
    ) = client.request(method = HttpMethod.Get, interceptor = interceptor) {
        url("$baseUrl/users/search")

        parameterNotNull("limit", limit)
        parameterNotNull("offset", offset)
        parameterNotNull("searchQuery", searchQuery)
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
            avatar: String? = null,
            bio: String? = null,
            shortname: String? = null,
            userName: String? = null,
            tokenHex: String? = null,
            interceptor: ResponseScope<Unit>.() -> Unit = {}
    ) = client.request(method = HttpMethod.Patch, interceptor = interceptor) {
        url("$baseUrl/users")
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
            tokenHex: String,
            attachmentHexes: List<String>? = null,
            replyToPostHex: String? = null,
            text: String? = null,
            interceptor: ResponseScope<Unit>.() -> Unit = {}
    ) = client.request(method = HttpMethod.Post, interceptor = interceptor) {
        url("$baseUrl/wall")
        header("tokenHex", tokenHex)
        body = CreatePostBodyModel(attachmentHexes, replyToPostHex, text)
    }

    /**
     * Get user's posts
     * You should specify at least [tokenHex] or [userHex]
     */
    suspend fun getUserPosts(
            tokenHex: String? = null,
            userHex: String? = null,
            limit: Int? = null,
            offset: Long? = null,
            interceptor: ResponseScope<List<PostModel>>.() -> Unit = {}
    ) = client.request(method = HttpMethod.Get, interceptor = interceptor) {
        url("$baseUrl/wall/${userHex ?: ""}")
        header("tokenHex", tokenHex)

        parameterNotNull("limit", limit)
        parameterNotNull("offset", offset)
    }

    /**
     * Like post. If post was already likes, there won't be any error.
     * @param postHex - Post unique hex
     * @param tokenHex - Authorization token hex.
     */
    suspend fun likePost(postHex: String, tokenHex: String, interceptor: ResponseScope<Unit>.() -> Unit = {}) =
            client.request(method = HttpMethod.Post, interceptor = interceptor) {
                url("$baseUrl/wall/likes/$postHex")
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
                header("tokenHex", tokenHex)
            }

    /**
     * @param fileBytes - File content bytes
     * @param tokenHex = Authorization token hex
     */
    suspend fun uploadFile(fileBytes: ByteArray, tokenHex: String, interceptor: ResponseScope<String>.() -> Unit = {}) =
            client.request(method = HttpMethod.Post, interceptor = interceptor) {
                url("$baseUrl/files")
                header("tokenHex", tokenHex)
                body = fileBytes
            }
}