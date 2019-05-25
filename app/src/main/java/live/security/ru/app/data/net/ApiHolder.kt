package ru.security.live.data.net

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.security.live.data.account.AccountManagerHelper
import ru.security.live.data.pref.ServerPref
import ru.security.live.util.LoggingTool
import java.util.concurrent.TimeUnit

object ApiHolder {

    private val BASE_URL = "http://${ServerPref.url2}"

    fun publicApi(context: Context): PublicApi {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val chuckInterceptor = ChuckInterceptor(context)
        chuckInterceptor.showNotification(true)

        val client = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chuckInterceptor)
                .build()

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(PublicApi::class.java)
    }

    val publicApi: PublicApi by lazy {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build()

        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(PublicApi::class.java)
    }

    val privateApi: PrivateApi by lazy {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(tokenInterceptor)
                .build()

        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(PrivateApi::class.java)
    }

    private val tokenInterceptor = Interceptor {
        val request = it.request()
        val build = request.newBuilder()
        val token = AccountManagerHelper.getToken()
        val newRequest = build.addHeader("Authorization", "Token $token").build()
        LoggingTool.log("Authorization: Token:$token")
        it.proceed(newRequest)
    }
}