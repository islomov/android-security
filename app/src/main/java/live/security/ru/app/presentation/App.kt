package ru.security.live.presentation

import android.app.Application
import android.support.multidex.MultiDex
import com.chibatching.kotpref.Kotpref
import io.reactivex.disposables.CompositeDisposable
import ru.security.live.data.account.AccountManagerHelper
import ru.security.live.util.ConnectivityTool
import ru.security.live.util.OurTrustCertificate
import ru.security.live.util.StatusMap
import javax.net.ssl.*
/**
 * @author sardor
 */
class App : Application() {

    companion object {
        lateinit var compositeDisposable: CompositeDisposable
    }

    override fun onCreate() {
        super.onCreate()

        // --------We have disabled Certificate check to download tiles from self signed certificate--------- //
        val trustAllCerts = arrayOf<TrustManager>(OurTrustCertificate())

        // Install the all-trusting trust manager
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

        // Create all-trusting host name verifier
        val allHostsValid = object : HostnameVerifier {
            override fun verify(hostname: String, session: SSLSession): Boolean {
                return true
            }
        }

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)

        // ------------------------------------- //


        MultiDex.install(this)
        Kotpref.init(applicationContext)
        AccountManagerHelper.init(applicationContext)
        StatusMap.init()
        ConnectivityTool.applicationContext = this
        // If we dont set user agent to application id, then map does not load, it shows blank
        //Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        compositeDisposable = CompositeDisposable()
    }
}