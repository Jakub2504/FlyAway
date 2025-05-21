package com.example.flyaway
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.test.assertEquals

// Interfaz de la API
interface HotelAvailabilityApi {
    @GET("hotels/{group_id}/availability")
    suspend fun getHotelAvailability(@Path("group_id") groupId: String): HotelAvailabilityResponse
}

// Modelo de respuesta
data class HotelAvailabilityResponse(
    val group_id: String = BuildConfig.GROUP_ID,
    val availability: List<HotelAvailability>
)

data class HotelAvailability(
    val hotel_id: String,
    val rooms_available: Int
)

// Test de integración
class HotelAvailabilityApiIntegrationTest {

    private val api: HotelAvailabilityApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL) // Reemplaza con la URL base de tu API
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(HotelAvailabilityApi::class.java)

    @Test
    fun test() = runBlocking {
        // Llamar al endpoint real
        val response = api.getHotelAvailability("123")

        // Verificar resultados
        assertEquals("123", response.group_id)
        assert(response.availability.isNotEmpty())
    }
}
