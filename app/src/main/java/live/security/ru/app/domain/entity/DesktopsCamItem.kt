package ru.security.live.domain.entity

/**
 * @author sardor
 */
data class DesktopsCamItem(
        val id: String = "",
        val name: String = "",
        val cameras: ArrayList<String> = ArrayList()// id камер в данной раскладке(десктопе)
) : DesktopsItem(name)