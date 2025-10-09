package no.uio.ifi.in2000.team47.rocketboy

import android.util.Log
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Data
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Details
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Instant
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Next_1_hours
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Summary
import no.uio.ifi.in2000.team47.rocketboy.model.ratings.rate
import no.uio.ifi.in2000.team47.rocketboy.model.settings.parseCoordinatesInput
import no.uio.ifi.in2000.team47.rocketboy.model.settings.submitChanges
import org.junit.Test
import org.junit.Before
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.model.ratings.getBadFactors
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribDataSource
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribRepository
import no.uio.ifi.in2000.team47.rocketboy.data.locationforecast.LocationforecastDataSource
import no.uio.ifi.in2000.team47.rocketboy.data.locationforecast.LocationforecastRepository
import no.uio.ifi.in2000.team47.rocketboy.data.network.HttpClientProvider
import no.uio.ifi.in2000.team47.rocketboy.model.grib.GribResponse

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class RatingUnitTest{
    @Test
    fun true_rate_test(){
        val rating: Double = rate(0.0,0.0,0.0,0.0,10.0)
        assertTrue(10.0 == rating)
    }

    @Test
    fun false_rate_test(){
        val rating: Double = rate(0.0,0.0,0.0,3.0,10.0)
        assertFalse(10.0 == rating)
    }
}

class ParseCoordinatesInputTest {

    @Test
    fun `should parse decimal coordinates with space`() {
        val input = "59.943233 10.736938"
        val result = parseCoordinatesInput(input)
        assertEquals(59.943233, result.lat, 0.000001)
        assertEquals(10.736938, result.lon, 0.000001)
        assertEquals(60, result.alt) // default altitude
    }

    @Test
    fun `should parse decimal coordinates with comma and custom altitude`() {
        val input = "59.91, 10.75 120"
        val result = parseCoordinatesInput(input)
        assertEquals(59.91, result.lat, 0.000001)
        assertEquals(10.75, result.lon, 0.000001)
        assertEquals(120, result.alt)
    }

    @Test
    fun `should parse DMS coordinates correctly`() {
        val input = "59°56'36.1\"N 10°43'05.1\"E"
        val result = parseCoordinatesInput(input)

        val expectedLat = 59 + 56.0/60 + 36.1/3600
        val expectedLon = 10 + 43.0/60 + 5.1/3600

        assertEquals(expectedLat, result.lat, 0.0001)
        assertEquals(expectedLon, result.lon, 0.0001)
        assertEquals(60, result.alt) // default
    }

    @Test
    fun `should throw exception for invalid format`() {
        val invalidInput = "this is not coordinates"
        assertThrows(IllegalArgumentException::class.java) {
            parseCoordinatesInput(invalidInput)
        }
    }
}

class FindingBadFactors{

    @Test
    fun `should detect bad temperature`() {
        val details = Details(airTemperature = -50.0)
        val data = Data(
            instant = Instant(details),
            next1Hours = Next_1_hours(
                summary = Summary("notImportant"),
                details = Details()
            )
        )

        val settings = WeatherSettingsData(
            tempMin = 0.0, tempMax = 30.0,
            humidityMin = 0.0, humidityMax = 100.0,
            windMin = 0.0, windMax = 20.0,
            precipitationMin = 0.0, precipitationMax = 5.0,
            fogMin = 0.0, fogMax = 100.0,
            dewMin = -10.0, dewMax = 20.0,
            cloudLowMax = 100.0, cloudMediumMax = 100.0, cloudHighMax = 100.0,
            cloudLowMin = 0.0, cloudMediumMin = 0.0, cloudHighMin = 0.0,
            shearMin = 0.0, shearMax = 100.0,
            id = 1
        )

        val badFactors = getBadFactors(data, settings)
        assertTrue("Should include Temperature", badFactors.contains("Temperature"))
    }


    @Test
    fun `should detect all factors are good`() {
        val details = Details(
            airTemperature = 20.0,
            relativeHumidity = 50.0,
            windSpeed = 10.0,
            dewPointTemperature = 10.0,
            fogAreaFraction = 5.0,
            cloudAreaFractionLow = 10.0,
            cloudAreaFractionMedium = 10.0,
            cloudAreaFractionHigh = 10.0,
            precipitationAmount = 2.0
        )

        val data = Data(
            instant = Instant(details),
            next1Hours = Next_1_hours(
                summary = Summary("notImportant"),
                details = Details()
            )
        )

        val settings = WeatherSettingsData(
            tempMin = 0.0, tempMax = 30.0,
            humidityMin = 0.0, humidityMax = 100.0,
            windMin = 0.0, windMax = 20.0,
            precipitationMin = 0.0, precipitationMax = 5.0,
            fogMin = 0.0, fogMax = 100.0,
            dewMin = -10.0, dewMax = 20.0,
            cloudLowMax = 100.0, cloudMediumMax = 100.0, cloudHighMax = 100.0,
            cloudLowMin = 0.0, cloudMediumMin = 0.0, cloudHighMin = 0.0,
            shearMin = 0.0, shearMax = 100.0,
            id = 4
        )

        val badFactors = getBadFactors(data, settings)
        assertTrue("Should not include any bad factors", badFactors.isEmpty())
    }

    @Test
    fun `should detect multiple bad factors`() {
        val details = Details(
            airTemperature = -50.0,
            relativeHumidity = 120.0,
            windSpeed = 50.0,
            dewPointTemperature = 25.0,
            fogAreaFraction = 110.0,
            cloudAreaFractionLow = 110.0,
            cloudAreaFractionMedium = 110.0,
            cloudAreaFractionHigh = 110.0,
        )

        val data = Data(
            instant = Instant(details),
            next1Hours = Next_1_hours(
                summary = Summary("notImportant"),
                details = Details()
            )
        )

        val settings = WeatherSettingsData(
            tempMin = 0.0, tempMax = 30.0,
            humidityMin = 0.0, humidityMax = 100.0,
            windMin = 0.0, windMax = 20.0,
            precipitationMin = 0.0, precipitationMax = 5.0,
            fogMin = 0.0, fogMax = 100.0,
            dewMin = -10.0, dewMax = 20.0,
            cloudLowMax = 100.0, cloudMediumMax = 100.0, cloudHighMax = 100.0,
            cloudLowMin = 0.0, cloudMediumMin = 0.0, cloudHighMin = 0.0,
            shearMin = 0.0, shearMax = 100.0,
            id = 5
        )

        val badFactors = getBadFactors(data, settings)
        assertTrue("Should include multiple bad factors", badFactors.size > 1)
        assertTrue("Should include Temperature", badFactors.contains("Temperature"))
        assertTrue("Should include Humidity", badFactors.contains("Humidity"))
        assertTrue("Should include Wind Speed", badFactors.contains("Wind Speed"))
        assertTrue("Should include Dew Point", badFactors.contains("Dew Point"))
        assertTrue("Should include Fog", badFactors.contains("Fog"))
        assertTrue("Should include Cloud fraction", badFactors.contains("Cloud fraction"))
    }
}

class GribResponseTest {

    private val gribDataSource = GribDataSource(HttpClientProvider.client)
    private val gribRepository = GribRepository(gribDataSource)

    @Test
    fun `should fetch Grib data successfully`() = runBlocking {

        val result = gribRepository.fetchGrib(59.94, 10.73)

        assertTrue("The fetchGrib call should succeed", result.isSuccess)
    }
}






