package ru.security.live.domain.entity

/**
 * @author sardor
 */
data class FilterEventTypeItem(
        val id: String,
        val name: String,
        var isChecked: Boolean = false
) {

    fun toggle() {
        isChecked = !isChecked
    }

    //нужно чтобы убрать отметки у чайлд типов событий когда у родителя убирается отметка
    var parentId = ""
}