package no.uio.ifi.in2000.team47.rocketboy.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribData
import no.uio.ifi.in2000.team47.rocketboy.data.trajectory.TrajectoryRepository

class RocketLaunchViewModelFactory(
    private val trajectoryRepository: TrajectoryRepository,
    private val gribDataList: List<GribData>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RocketLaunchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RocketLaunchViewModel(trajectoryRepository, gribDataList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



