package ru.security.live.data.net.response

/**
 * @author sardor
 */
data class UserResponse(
        val id: String, //40c242b3-df86-4f81-8a54-a26a22e43f4f
        val name: String? = "", //запись
        val lastName: String? = "", //Учётная
        val middleName: String? = "", //Anvics
        val avatarId: String, //5607193d-5a0f-4f80-bd48-ae066cc4f963
        val avatarUri: String? //http://10.178.3.12:3000/files/5607193d-5a0f-4f80-bd48-ae066cc4f963
)