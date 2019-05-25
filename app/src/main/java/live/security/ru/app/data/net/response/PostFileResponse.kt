package ru.security.live.data.net.response
/**
 * @author sardor
 */
data class PostFileResponse(
        val imageInfo: ImageInfo
) {
    data class ImageInfo(
            val id: String, // 7c054527-aae2-40b5-bbce-d262f7b25ba7
            val uri: String // http://10.178.3.12:3000/files/7c054527-aae2-40b5-bbce-d262f7b25ba7
    )
}