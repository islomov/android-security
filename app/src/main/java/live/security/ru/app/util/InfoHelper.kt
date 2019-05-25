package ru.security.live.util

import android.content.Context
import android.content.Intent
import android.net.Uri
/**
 * @author sardor
 */
class InfoHelper {
    companion object {
        fun phoneCall(context: Context) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            context.startActivity(intent)
        }

        fun openLicense(context: Context) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(licenseUrl)
            context.startActivity(intent)
        }

        fun openSite(context: Context) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(companyUrl)
            context.startActivity(intent)
        }

        fun emailSupport(context: Context) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$supportEmail")
            context.startActivity(intent)
        }
    }
}