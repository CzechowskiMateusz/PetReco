package pl.domain.application.petreco.data.utils

import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PetfinderCredentials {
    const val CLIENT_ID = "Icpnsj0kWe4dZq0SZVLCxjFQNjS0m2MJlA41JZCmXXy64ocQeR"
    const val CLIENT_SECRET = "CukXFfaFPBhFIHis1MM0XG99sEFUMXgjii9bI9OX"
}

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

interface AuthApi {
    @FormUrlEncoded
    @POST("oauth2/token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("client_id") clientId: String = PetfinderCredentials.CLIENT_ID,
        @Field("client_secret") clientSecret: String = PetfinderCredentials.CLIENT_SECRET
    ): TokenResponse
}

object TokenProvider {
    private const val BASE_URL = "https://api.petfinder.com/v2/"

    private val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    suspend fun fetchToken(): String {
        val response = authApi.getAccessToken()
        return "${response.token_type} ${response.access_token}"
    }
}