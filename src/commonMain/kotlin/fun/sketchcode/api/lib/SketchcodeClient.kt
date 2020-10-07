package `fun`.sketchcode.api.lib

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*

class SketchcodeClient(private val baseUrl: String = "https://sketchcode.fun/api") {

    private val client = HttpClient() {
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
    suspend fun getAuthCode(email: String, recaptchaToken: String) =
            client.request<Unit>(method = HttpMethod.Get) {
                url("$baseUrl/users/tokens/code")
                contentType(ContentType.Application.Json)
                body = AuthorizeParamsModel(email, recaptchaToken)
            }

    /**
     * Verify authorization code.
     * @param code - Received code from mail.
     * @param email - Email to which was sent a code.
     * @return Authorization token
     */
    suspend fun sendAuthCode(code: Int, email: String) =
            client.request<AuthorizeParamsModel>(method = HttpMethod.Post) {
                url("$baseUrl/users/tokens")
                contentType(ContentType.Application.Json)
                body = SendCodeParamsModel(email, code)
            }

    /**
     * Get info about token.
     * @param tokenHex - token in hex
     */
    suspend fun getToken(tokenHex: String) =
            client.request<AuthorizeResponseModel>(method = HttpMethod.Get) {
                url("$baseUrl/users/tokens")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
            }

    /**
     * Get user by [userId].
     * @param tokenHex - token in hex
     * @param userId - User id
     */
    suspend fun getUser(tokenHex: String, userId: Long) =
            client.request<UserModel>(method = HttpMethod.Get) {
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
            searchQuery: String?
    ) = client.request<List<UserModel>>(method = HttpMethod.Get) {
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
            tokenHex: String
    ) = client.request<Unit>(method = HttpMethod.Patch) {
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
            tokenHex: String
    ) = client.request<Unit>(method = HttpMethod.Post) {
        url("$baseUrl/wall")
        contentType(ContentType.Application.Json)
        header("tokenHex", tokenHex)
        body = CreatePostBodyModel(attachmentHexes, replyToPostHex, text)
    }

    /**
     * Follow post. If post was already likes, there won't be any error.
     * @param postHex - Post unique hex
     * @param tokenHex - Authorization token hex.
     */
    suspend fun followPost(postHex: String, tokenHex: String) =
            client.request<Unit>(method = HttpMethod.Post) {
                url("$baseUrl/wall/likes/$postHex")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
            }

    /**
     * Unfollow post. If post was already likes, there won't be any error.
     * @param postHex - Post unique hex
     * @param tokenHex - Authorization token hex.
     */
    suspend fun unfollowPost(postHex: String, tokenHex: String) =
            client.request<Unit>(method = HttpMethod.Delete) {
                url("$baseUrl/wall/likes/$postHex")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
            }

    /**
     * @param fileBytes - File content bytes
     * @param tokenHex = Authorization token hex
     */
    suspend fun uploadFile(fileBytes: ByteArray, tokenHex: String) =
            client.request<String>(method = HttpMethod.Post) {
                url("$baseUrl/files")
                contentType(ContentType.Application.Json)
                header("tokenHex", tokenHex)
                body = fileBytes
            }


}