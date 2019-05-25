package ru.security.live.domain.entity.enums

/**
 * @author sardor
 */
enum class Importance(val saverity: Int) {
    Normal(0),
    High(1),
    Critical(2)
}