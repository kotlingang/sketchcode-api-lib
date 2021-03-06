package `fun`.sketchcode.api.lib

import kotlinx.serialization.Serializable

@Serializable
data class TokenModel(
        val ownerHex: String,
        val hex: String,
        val generatedTime: Long,
        val firstAuth: Boolean = false
)

@Serializable
data class SendCodeParamsModel(
        val email: String,
        val code: Int
)

@Serializable
data class UserModel(
        val avatar: FileModel?,
        val bio: String?,
        val email: String? = null,
        val hex: String,
        val name: String,
        /**
         * User shortname (ex: @shortname, sketchcode.fun/users/[shortname])
         */
        val shortname: String?
)

@Serializable
data class PostModel(
        val hex: String,
        val ownerHex: String,
        val text: String?,
        val likes: Long,
        val isLiked: Boolean,
        val views: Long,
        val comments: Long,
        val attachments: List<FileModel>,
        val time: Long
)

@Serializable
data class FilterModel(
        val n: Int,
        val offset: Long?,
        val searchQuery: String?
)

@Serializable
data class UserEditParamsModel(
        val avatar: String?,
        val bio: String?,
        val shortname: String?,
        val userName: String?,
)

@Serializable
data class FileModel(
        val accessHex: String,
        val creationTime: Long,
        val extension: String,
        val hash: String,
        val ownerHex: String,
        val size: Long
)

@Serializable
data class CreatePostBodyModel(
        val attachmentHexes: List<String>?,
        val replyToPostHex: String?,
        val text: String?
)
