package `fun`.sketchcode.api.lib

data class AuthorizeResponseModel(
    val generatedTime: Long,
    val hex: String,
    val ownerHex: String
)

data class AuthorizeParamsModel(
    val email: String,
    val recaptchaToken: String
)

data class SendCodeParamsModel(
    val email: String,
    val code: Int
)

data class UserModel(
    val avatar: FileModel?,
    val bio: String?,
    /**
     * Null if authorized user not equal to getting user
     */
    val email: String? = null,
    val hex: String,
    val name: String,
    /**
     * User shortname (ex: @shortname, sketchcode.fun/users/[shortname])
     */
    val shortname: String?
)

data class FilterModel(
    val n: Int,
    val offset: Long?,
    val searchQuery: String?
)

data class UserEditParamsModel(
    val avatar: String,
    val bio: String,
    val shortname: String,
    val userName: String,
)

data class FileModel(
    val accessHex: String,
    val creationTime: Long,
    val extension: String,
    val hash: String,
    val ownerHex: String,
    val size: Long
)

data class CreatePostBodyModel(
    val attachmentHexes: List<String>?,
    val replyToPostHex: String?,
    val text: String?
)