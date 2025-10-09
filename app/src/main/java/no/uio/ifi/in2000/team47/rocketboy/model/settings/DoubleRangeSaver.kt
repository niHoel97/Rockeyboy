package no.uio.ifi.in2000.team47.rocketboy.model.settings

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

val DoubleRangeSaver: Saver<ClosedFloatingPointRange<Double>, Any> = listSaver(
    save = { listOf(it.start, it.endInclusive) },
    restore = { it[0]..it[1] }
)





