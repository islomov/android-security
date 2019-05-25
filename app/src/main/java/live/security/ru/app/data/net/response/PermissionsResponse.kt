package ru.security.live.data.net.response
/**
 * @author sardor
 */
data class PermissionsResponse(
        val id: String, // 70a7b3af-cf7c-4b67-a7c8-6a26c8877ab8
        val code: String, // Events
        val name: String, // Реестр Событий
        val permissions: List<Permission>
) {
    data class Permission(
            val id: String, // 8d7b795b-00ac-4a28-9c00-4cc71357acd1
            val contextType: Int, // 1
            val code: String, // User_Event_CRUD
            val name: String // Создание пользовательского события
    )
}