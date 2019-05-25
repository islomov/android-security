package ru.security.live.data.net.response
/**
 * @author sardor
 */
data class UserEventResponse(
        val id: String, // 60c17307-dc69-4593-b47f-ea029d690933
        val info: String, // тест
        val description: String, // тест
        val locationId: String, // a2e20fcc-f889-4373-910c-7eee7c90b74c
        val images: List<Image>?
) {
    data class Image(
            val id: String, // 149d39e1-90fb-4f2c-80a7-4cdf14b0d9f2
            val uri: String // http://10.178.3.12:3000/files/149d39e1-90fb-4f2c-80a7-4cdf14b0d9f2
    )
}