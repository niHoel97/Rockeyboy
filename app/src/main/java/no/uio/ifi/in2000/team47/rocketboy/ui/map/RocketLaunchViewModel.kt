package no.uio.ifi.in2000.team47.rocketboy.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribData
import no.uio.ifi.in2000.team47.rocketboy.data.trajectory.TrajectoryRepository
import no.uio.ifi.in2000.team47.rocketboy.utils.MapUtils
import kotlin.math.max

/**
 * ViewModel responsible for the rocket launch simulation logic and state.
 *
 * @param trajectoryRepository The repository to fetch trajectory data.
 */
// If using Hilt, you would annotate with @HiltViewModel and @Inject constructor
// @HiltViewModel
class RocketLaunchViewModel( // Add @Inject if using Hilt
    private val trajectoryRepository: TrajectoryRepository,
    private val gribDataList: List<GribData>
) : ViewModel() {

    private val _rocketPosition = MutableStateFlow<Point?>(null)
    val rocketPosition: StateFlow<Point?> = _rocketPosition.asStateFlow()

    private val _trajectoryPoints = MutableStateFlow<List<Point>>(emptyList())
    val trajectoryPoints: StateFlow<List<Point>> = _trajectoryPoints.asStateFlow()

    private val _cameraTarget = MutableStateFlow<CameraOptions?>(null)
    val cameraTarget: StateFlow<CameraOptions?> = _cameraTarget.asStateFlow()

    private val _isLaunching = MutableStateFlow(false)
    val isLaunching: StateFlow<Boolean> = _isLaunching.asStateFlow()

    private var animationJob: Job? = null
    private var currentTrajectory: List<Point> = emptyList()

    init {
        // Initialize with the default launch point or fetch initial data
        viewModelScope.launch {
            val initialPoint = trajectoryRepository.getInitialLaunchPoint()
            _rocketPosition.value = initialPoint
            // Set initial camera to focus on the launch point
            _cameraTarget.value = CameraOptions.Builder()
                .center(initialPoint)
                .zoom(14.0) // Initial zoom level
                .pitch(45.0) // Slight pitch for a better view
                .build()
        }
    }

    /**
     * Starts the rocket launch simulation.
     * Fetches the trajectory and animates the rocket along it.
     * @param launchPoint The specific point from which to launch. If null, uses default.
     */
    fun startLaunchSequence(launchPoint: Point? = null) {
        if (_isLaunching.value) return // Prevent multiple launches at once

        _isLaunching.value = true
        animationJob?.cancel() // Cancel any previous animation

        animationJob = viewModelScope.launch {

            val startPoint = launchPoint ?: trajectoryRepository.getInitialLaunchPoint()

            currentTrajectory = trajectoryRepository.getLaunchTrajectory(startPoint, gribDataList)
            _trajectoryPoints.value = currentTrajectory

            if (currentTrajectory.isEmpty()) {
                _isLaunching.value = false
                return@launch
            }

            // Set initial rocket position to the start of the trajectory
            _rocketPosition.value = currentTrajectory.first()

            // Animate rocket movement
            for (index in currentTrajectory.indices) {
                _rocketPosition.value = currentTrajectory[index]

                // Update camera to follow the rocket
                val cameraOptions = CameraOptions.Builder()
                    .center(currentTrajectory[index])

                // Adjust zoom and pitch based on rocket's progress (index)
                val progress = index.toFloat() / currentTrajectory.size.toFloat()
                val currentZoom = 15.0 - (progress * 7.0) // Zoom out as rocket ascends
                val currentPitch = 45.0 + (progress * 25.0) // Increase pitch

                cameraOptions
                    .zoom(max(6.0, currentZoom)) // Ensure zoom doesn't get too small
                    .pitch(max(0.0, currentPitch.coerceAtMost(70.0))) // Cap pitch

                // Optional: Make rocket point in direction of travel
                if (index < currentTrajectory.size -1 ){
                    val bearing = MapUtils.calculateBearing(currentTrajectory[index], currentTrajectory[index+1])
                    cameraOptions.bearing(bearing) // Also orient camera
                    // For rocket icon rotation, you'd need to update the PointAnnotation's iconRotate property
                    // This requires passing bearing to the View and handling it there.
                    // For now, we focus on camera bearing.
                }

                _cameraTarget.value = cameraOptions.build()
                delay(100) // Delay between animation steps (e.g., 100ms)
            }
            _isLaunching.value = false
        }
    }

    /**
     * Resets the simulation to its initial state.
     */
    fun resetSimulation(value: Point) {
        animationJob?.cancel()
        _isLaunching.value = false
        viewModelScope.launch {
            _rocketPosition.value = value
            _trajectoryPoints.value = emptyList() // Clear trajectory line
            _cameraTarget.value = CameraOptions.Builder()
                .center(value)
                .zoom(14.0)
                .pitch(45.0)
                .build()
        }
    }

    override fun onCleared() {
        super.onCleared()
        animationJob?.cancel() // Ensure coroutine is cancelled when ViewModel is cleared
    }

    fun setRocketStartPosition(point: Point) {
        _rocketPosition.value = point
    }
}