package ru.security.live.data.net

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import ru.security.live.data.net.request.LoginRequest
import ru.security.live.data.net.response.LoginResponse
import ru.security.live.data.net.response.SettingsResponse
/**
 * @author sardor
 */
interface PublicApi {

    @POST("/authentication/login")
    fun login(@Body body: LoginRequest): Single<LoginResponse>


    @GET
    fun getSettings(@Url url: String): Single<SettingsResponse>
}