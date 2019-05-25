package ru.security.live.data.pref

import com.chibatching.kotpref.KotprefModel
/**
 * @author sardor
 */
object UserPref : KotprefModel() {
    var id by stringPref(default = "")
    var name by stringPref(default = "")
    var token by stringPref(default = "")
    var lastName by stringPref(default = "")
    var middleName by stringPref(default = "")
    var avatarId by stringPref(default = "")
    var avatarUri by stringPref(default = "")
}