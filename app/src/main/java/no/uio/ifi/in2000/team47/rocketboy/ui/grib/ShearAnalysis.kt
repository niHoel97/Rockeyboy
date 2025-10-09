package no.uio.ifi.in2000.team47.rocketboy.ui.grib

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribData

@Composable
fun ShearAnalysis (
    modifier: Modifier = Modifier,
    gribDataList: List<GribData>,
    settings: WeatherSettingsData,
){
    Column(
        modifier = modifier
            .padding(vertical = 25.dp, horizontal = 10.dp)
            .semantics {
                contentDescription = "Shear and Wind Analysis section showing grib data and weather settings"
            },
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ){
        ShearAnalysisCard(
            modifier = Modifier.semantics {
                contentDescription = "Shear Analysis card showing shear data from the grib data"
            },
            gribDataList = gribDataList,
            settings = settings
        )
        WindAnalysisCard(
            modifier = Modifier.semantics {
                contentDescription = "Wind Analysis card showing wind data from the grib data"
            },
            gribDataList = gribDataList,
            settings = settings
        )
    }
}
