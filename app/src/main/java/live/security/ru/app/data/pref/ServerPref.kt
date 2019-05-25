package ru.security.live.data.pref

import com.chibatching.kotpref.KotprefModel
/**
 * @author sardor
 */
object ServerPref : KotprefModel() {
    var url1 by stringPref("10.178.3.34")
    var url2 by stringPref("10.178.3.34:9001")
    var token by stringPref(default = "")
}