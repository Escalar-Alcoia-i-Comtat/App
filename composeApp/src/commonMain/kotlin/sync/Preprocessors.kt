package sync

import data.Area
import data.Path
import data.Sector
import data.Zone

object Preprocessors {
    val areaPreprocessor: Preprocessor<Area> = Preprocessor { it }
    val zonePreprocessor: Preprocessor<Zone> = Preprocessor { it }
    val sectorPreprocessor: Preprocessor<Sector> = Preprocessor { it }
    val pathPreprocessor: Preprocessor<Path> = Preprocessor { path ->
        path.copy(
            // Fix builder
            builder = path.builder
                .takeIf { !it?.name.isNullOrBlank() || !it?.date.isNullOrBlank() }
        )
    }
}
