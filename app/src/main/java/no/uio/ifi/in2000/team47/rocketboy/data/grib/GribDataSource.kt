package no.uio.ifi.in2000.team47.rocketboy.data.grib

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team47.rocketboy.model.grib.GribResponse

class GribDataSource(private val httpClient: HttpClient) {
    suspend fun getGribData(url: String): Result<GribResponse> {
        return try {
            val response: HttpResponse = httpClient.get(url)
            if (!response.status.isSuccess()) {
                return Result.failure(Exception("HTTP Error: ${response.status}"))
            }
            val responseBody = response.bodyAsText()
            val gribResponse = Json.decodeFromString<GribResponse>(responseBody)
            Result.success(gribResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

