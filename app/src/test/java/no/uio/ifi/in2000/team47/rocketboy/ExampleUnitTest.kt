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
import no.uio.ifi.in2000.team47.rocketboy.model.settings.coordinatesInRange
import no.uio.ifi.in2000.team47.rocketboy.model.settings.formatCoordinates
import no.uio.ifi.in2000.team47.rocketboy.utils.MapUtils
import com.mapbox.geojson.Point

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

class MapUtilsTest {

    @Test
    fun `calculateBearing returns 0 for due North`() {
        val from = Point.fromLngLat(0.0, 0.0)
        val to = Point.fromLngLat(0.0, 1.0)
        assertEquals(0.0, MapUtils.calculateBearing(from, to), 1.0)
    }

    @Test
    fun `calculateBearing returns 90 for due East`() {
        val from = Point.fromLngLat(0.0, 0.0)
        val to = Point.fromLngLat(1.0, 0.0)
        assertEquals(90.0, MapUtils.calculateBearing(from, to), 1.0)
    }

    @Test
    fun `calculateBearing returns 180 for due South`() {
        val from = Point.fromLngLat(0.0, 0.0)
        val to = Point.fromLngLat(0.0, -1.0)
        assertEquals(180.0, MapUtils.calculateBearing(from, to), 1.0)
    }

    @Test
    fun `calculateBearing returns 270 for due West`() {
        val from = Point.fromLngLat(0.0, 0.0)
        val to = Point.fromLngLat(-1.0, 0.0)
        assertEquals(270.0, MapUtils.calculateBearing(from, to), 1.0)
    }

    @Test
    fun `calculateBearing result is always in 0 to 360 range`() {
        val pairs = listOf(
            Pair(Point.fromLngLat(10.0, 59.0), Point.fromLngLat(11.0, 60.0)),
            Pair(Point.fromLngLat(5.0, 60.0), Point.fromLngLat(3.0, 58.0)),
            Pair(Point.fromLngLat(0.0, 0.0), Point.fromLngLat(-5.0, 5.0))
        )
        for ((from, to) in pairs) {
            val bearing = MapUtils.calculateBearing(from, to)
            assertTrue("Bearing should be >= 0", bearing >= 0.0)
            assertTrue("Bearing should be < 360", bearing < 360.0)
        }
    }

    @Test
    fun `calculateDistance returns 0 for same point`() {
        val point = Point.fromLngLat(10.73, 59.94)
        assertEquals(0.0, MapUtils.calculateDistance(point, point), 0.01)
    }

    @Test
    fun `calculateDistance for 1 degree latitude at equator is approximately 111194 meters`() {
        val from = Point.fromLngLat(0.0, 0.0)
        val to = Point.fromLngLat(0.0, 1.0)
        assertEquals(111194.0, MapUtils.calculateDistance(from, to), 1000.0)
    }

    @Test
    fun `calculateDistance is symmetric`() {
        val a = Point.fromLngLat(10.73, 59.94)
        val b = Point.fromLngLat(5.32, 60.37)
        val distAB = MapUtils.calculateDistance(a, b)
        val distBA = MapUtils.calculateDistance(b, a)
        assertEquals(distAB, distBA, 0.001)
    }

    @Test
    fun `calculateDistance is always non-negative`() {
        val from = Point.fromLngLat(10.73, 59.94)
        val to = Point.fromLngLat(5.32, 60.37)
        assertTrue(MapUtils.calculateDistance(from, to) >= 0.0)
    }
}

class SettingsFunctionsExtendedTest {

    @Test
    fun `coordinatesInRange returns true for valid Norwegian coordinates`() {
        assertTrue(coordinatesInRange(59.94, 10.73))
    }

    @Test
    fun `coordinatesInRange returns false for latitude below minimum`() {
        assertFalse(coordinatesInRange(55.0, 10.73))
    }

    @Test
    fun `coordinatesInRange returns false for latitude above maximum`() {
        assertFalse(coordinatesInRange(65.0, 10.73))
    }

    @Test
    fun `coordinatesInRange returns false for longitude below minimum`() {
        assertFalse(coordinatesInRange(59.94, -2.0))
    }

    @Test
    fun `coordinatesInRange returns false for longitude above maximum`() {
        assertFalse(coordinatesInRange(59.94, 15.0))
    }

    @Test
    fun `coordinatesInRange returns true for exact boundary values`() {
        assertTrue(coordinatesInRange(55.35, -1.45))
        assertTrue(coordinatesInRange(64.25, 14.45))
    }

    @Test
    fun `formatCoordinates returns correctly formatted string`() {
        assertEquals("59.9432, 10.7369", formatCoordinates(59.943233, 10.736938))
    }

    @Test
    fun `formatCoordinates handles negative values`() {
        assertEquals("-1.2345, -9.8765", formatCoordinates(-1.2345, -9.8765))
    }

    @Test
    fun `formatCoordinates rounds to 4 decimal places`() {
        assertEquals("1.2346, 2.8765", formatCoordinates(1.23456789, 2.87654321))
    }

    @Test
    fun `parseCoordinatesInput handles DMS with South latitude`() {
        val input = "10°30'00.0\"S 10°00'00.0\"E"
        val result = parseCoordinatesInput(input)
        assertTrue("South latitude should be negative", result.lat < 0)
        assertEquals(-(10 + 30.0 / 60), result.lat, 0.0001)
    }

    @Test
    fun `parseCoordinatesInput handles DMS with West longitude`() {
        val input = "59°00'00.0\"N 01°30'00.0\"W"
        val result = parseCoordinatesInput(input)
        assertTrue("West longitude should be negative", result.lon < 0)
        assertEquals(-1.5, result.lon, 0.0001)
    }
}






