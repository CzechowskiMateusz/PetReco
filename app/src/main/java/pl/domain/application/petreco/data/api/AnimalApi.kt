package pl.domain.application.petreco.data.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import pl.domain.application.petreco.data.model.AllAnimal
import pl.domain.application.petreco.data.model.AnimalTypesResponse

interface AnimalApi {
    @GET("types")
    suspend fun getAnimalTypes(
        @Header("Authorization") token: String
    ): AnimalTypesResponse

    @GET("animals")
    suspend fun getAnimalsByType(
        @Header("Authorization") token: String,
        @Query("type") type: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10
    ): AllAnimal
}