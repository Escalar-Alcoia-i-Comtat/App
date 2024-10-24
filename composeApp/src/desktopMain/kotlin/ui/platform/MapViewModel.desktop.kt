package ui.platform

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import build.BuildKonfig
import cache.storageProvider
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.api.staticmap.v1.models.StaticPolylineAnnotation
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import map.MapData
import map.kmz.KMZLoader
import map.placemark.Polygon
import network.createHttpClient
import utils.asJavaFile

actual class MapViewModel actual constructor() : ViewModel() {
    private val httpClient = createHttpClient {
        install(HttpCache) {
            val cacheFile = storageProvider.cacheDirectory + "map_cache"
            publicStorage(FileStorage(cacheFile.asJavaFile))
        }
    }

    private val _progress = MutableStateFlow<Float?>(null)
    val progress: StateFlow<Float?>
        get() = _progress.asStateFlow()

    private val _mapImage = MutableStateFlow<ByteArray?>(null)
    val mapImage: StateFlow<ByteArray?>
        get() = _mapImage.asStateFlow()

    fun loadMap(kmzUUID: String, layoutSize: IntSize) {
        viewModelScope.launch(Dispatchers.IO) {
            Napier.i { "Loading KMZ $kmzUUID..." }
            val mapData = KMZLoader.loadKMZ(kmzUUID)

            val (mapUUID, staticMap) = createMap(mapData, layoutSize)
            loadMap(mapUUID, staticMap)
        }
    }

    private fun createMap(mapData: MapData, layoutSize: IntSize): Pair<String, MapboxStaticMap> {
        val uuidBuilder = StringBuilder("")

        mapData.styles.let { Napier.d { "There are ${it.size} styles loaded." } }

        val staticMap = MapboxStaticMap.builder()
            .accessToken(BuildKonfig.MAPBOX_ACCESS_TOKEN!!)
            .styleId(StaticMapCriteria.OUTDOORS_STYLE)
            .also { uuidBuilder.append(StaticMapCriteria.OUTDOORS_STYLE) }
            .cameraAuto(true)
            .also { uuidBuilder.append(";auto") }
            .width(layoutSize.width)
            .also { uuidBuilder.append(";${layoutSize.width}") }
            .height(layoutSize.height)
            .also { uuidBuilder.append(";${layoutSize.height}") }
            .staticPolylineAnnotations(
                mapData.placemarks
                    .filterIsInstance<Polygon>()
                    // Log markers count
                    .also { Napier.d { "There are ${it.size} polygons in map." } }
                    .map { polygon ->
                        val pointsHashCode = polygon.coordinates
                            .joinToString(";") { (lat, lon) -> "$lat,$lon" }
                            .hashCode()
                        uuidBuilder.append(";${POLYLINE_PRECISION}")
                        uuidBuilder.append(";$pointsHashCode")

                        StaticPolylineAnnotation.builder()
                            .polyline(
                                PolylineUtils.encode(
                                    polygon.coordinates.map { (lat, lon) ->
                                        Point.fromLngLat(
                                            lat,
                                            lon
                                        )
                                    },
                                    POLYLINE_PRECISION
                                )
                            )
                            .build()
                    }
            )
            .staticMarkerAnnotations(
                mapData.placemarks
                    // Take only points
                    .filterIsInstance<map.placemark.Point>()
                    // Sort from top to bottom to display ordered
                    .sortedByDescending { it.longitude }
                    // Log markers count
                    .also { Napier.d { "There are ${it.size} markers in map." } }
                    .map { point ->
                        uuidBuilder.append(";${point.latitude},${point.longitude}")

                        // TODO: icon from image
                        StaticMarkerAnnotation.builder()
                            .lnglat(Point.fromLngLat(point.latitude, point.longitude))
                            .name(StaticMapCriteria.SMALL_PIN)
                            .build()
                    }
            )
            .build()
        val mapUUID = uuidBuilder.toString()

        return mapUUID to staticMap
    }

    private suspend fun loadMap(mapUUID: String, map: MapboxStaticMap) = try {
        _progress.emit(null)
        val imageUrl = map.url().toString()
            // Replace problematic characters by URL encoding
            .replace("[", "%5B")
            .replace("]", "%5D")
        val imageUid = mapUUID.hashCode().toString()

        Napier.v { "Fetching map image ($imageUid): $imageUrl" }
        val result = httpClient.get(imageUrl) {
            onDownload { bytesSentTotal, contentLength ->
                contentLength ?: return@onDownload
                Napier.v { "Map image download progress: $bytesSentTotal / $contentLength" }
                _progress.emit((bytesSentTotal.toDouble() / contentLength.toDouble()).toFloat())
            }
        }
        if (result.status.value in 200..299) {
            Napier.v { "Map downloaded successfully. Reading body..." }
            val bytes = result.bodyAsBytes()
            Napier.v { "Emitting response bytes to UI..." }
            _mapImage.emit(bytes)
        } else {
            Napier.e { "Could not load map image. Status: ${result.status}" }
            // TODO: Notify user
        }
    } finally {
        _progress.emit(null)
    }

    companion object {
        private const val POLYLINE_PRECISION = 5
    }
}
// Chrome // API
// https://api.mapbox.com/styles/v1/mapbox/outdoors-v11/static/pin-s(-0.490355,38.713750),pin-s(-0.490235,38.713137),pin-s(-0.489554,38.712806),pin-s(-0.489915,38.712436),pin-s(-0.489550,38.712308),pin-s(-0.489976,38.712130),pin-s(-0.488739,38.711557),pin-s(-0.488311,38.711464),pin-s(-0.488166,38.711268),pin-s(-0.489334,38.711157),pin-s(-0.489559,38.711125),pin-s(-0.489163,38.711002),pin-s(-0.489304,38.710871),pin-s(-0.487903,38.710825),pin-s(-0.488154,38.710797),pin-s(-0.489447,38.710709),pin-s(-0.489384,38.710356),pin-s(-0.487242,38.710238),pin-s(-0.489733,38.709519),pin-s(-0.486737,38.709405),pin-s(-0.489954,38.708357),pin-s(-0.486522,38.707649),pin-s(-0.489262,38.707584),pin-s(-0.489268,38.707266),pin-s(-0.489300,38.707019),path(wggkF~v~AD%3FNGLKJELKRILCFI%3FOI%3FGFMFO@G@KHQDMLEF@R),path(kbgkFjs~AFAHEDENOFKLIDGCKECGCC@CDCFEDEHCFGHKHAH%3FH),path(c%60gkF%60q~A@NZDLWYIQJ),path(y~fkFpq~Ap@L@[m@IEV),path(qugkFdh~AEEG@AB%3FLCHDNFBFCAK@MAM),path(yzgkFhn~AHEHAAMEMI%3FEBALBJ@D),path(g%7BgkFdl~AJC%3FSICCB@V),path(szgkFhk~APQHAHDL@NM@MBEFAFBPDNABMCOOAUFQNSNO@CIODSTFJ),path(qogkF%7Ca~AzCR@q@wCOEl@),path(a_hkF%60u~AR%3FRBZ%3FR%3FBKEGY@M@[%3FS%3F@J),path(o%60hkFxr~An@GL@%5CFDQSAWAu@H%3FH),path(oahkFls~ASKOe@QSYGEBFx@%3Fb@Q%5CAd@T%5Cn@c@Pe@F[@O),path(qehkF%60w~ACW_@Fi@B_@Bm@DOAI%3F%3FRFF%5C@X%3FVIf@EZAXG),path(wwgkFzq~AFHJIBGIMEDGN),path(yvgkFlq~AH%3FFHLJHKIISMQ@DJ),path(qugkFhr~ATHN@BMEAOCMGEN),path(_tgkFrr~Ad@AZB@U]Ce@@%3FT),path(qwgkFxp~AIGGDANFHDGDO),path(sxgkFvr~AH%3F%3FIBMBKGEGRAT),path(ywgkFzr~APHTDNCAQe@AOG%3FN),path(apgkF~s~AzAr@lAd@l@IKo@sBe@%7D@u@Yz@)/auto/600x180?access_token=pk.eyJ1IjoiYXJueW1pbmVyeiIsImEiOiJja2x0ODduMnAxNmJmMnBsbGpxdzBjN3N5In0.QfEgY1VWtAd6HWj-Q64PKQ
// https://api.mapbox.com/styles/v1/mapbox/outdoors-v11/static/pin-s(-0.490355,38.713750),pin-s(-0.490235,38.713137),pin-s(-0.489554,38.712806),pin-s(-0.489915,38.712436),pin-s(-0.489550,38.712308),pin-s(-0.489976,38.712130),pin-s(-0.488739,38.711557),pin-s(-0.488311,38.711464),pin-s(-0.488166,38.711268),pin-s(-0.489334,38.711157),pin-s(-0.489559,38.711125),pin-s(-0.489163,38.711002),pin-s(-0.489304,38.710871),pin-s(-0.487903,38.710825),pin-s(-0.488154,38.710797),pin-s(-0.489447,38.710709),pin-s(-0.489384,38.710356),pin-s(-0.487242,38.710238),pin-s(-0.489733,38.709519),pin-s(-0.486737,38.709405),pin-s(-0.489954,38.708357),pin-s(-0.486522,38.707649),pin-s(-0.489262,38.707584),pin-s(-0.489268,38.707266),pin-s(-0.489300,38.707019),path(wggkF~v~AD%3FNGLKJELKRILCFI%3FOI%3FGFMFO@G@KHQDMLEF@R),path(kbgkFjs~AFAHEDENOFKLIDGCKECGCC@CDCFEDEHCFGHKHAH%3FH),path(c%60gkF%60q~A@NZDLWYIQJ),path(y~fkFpq~Ap@L@[m@IEV),path(qugkFdh~AEEG@AB%3FLCHDNFBFCAK@MAM),path(yzgkFhn~AHEHAAMEMI%3FEBALBJ@D),path(g%7BgkFdl~AJC%3FSICCB@V),path(szgkFhk~APQHAHDL@NM@MBEFAFBPDNABMCOOAUFQNSNO@CIODSTFJ),path(qogkF%7Ca~AzCR@q@wCOEl@),path(a_hkF%60u~AR%3FRBZ%3FR%3FBKEGY@M@[%3FS%3F@J),path(o%60hkFxr~An@GL@%5CFDQSAWAu@H%3FH),path(oahkFls~ASKOe@QSYGEBFx@%3Fb@Q%5CAd@T%5Cn@c@Pe@F[@O),path(qehkF%60w~ACW_@Fi@B_@Bm@DOAI%3F%3FRFF%5C@X%3FVIf@EZAXG),path(wwgkFzq~AFHJIBGIMEDGN),path(yvgkFlq~AH%3FFHLJHKIISMQ@DJ),path(qugkFhr~ATHN@BMEAOCMGEN),path(_tgkFrr~Ad@AZB@U]Ce@@%3FT),path(qwgkFxp~AIGGDANFHDGDO),path(sxgkFvr~AH%3F%3FIBMBKGEGRAT),path(ywgkFzr~APHTDNCAQe@AOG%3FN),path(apgkF~s~AzAr@lAd@l@IKo@sBe@%7D@u@Yz@)/auto/600x180?access_token=pk.eyJ1IjoiYXJueW1pbmVyeiIsImEiOiJja2x0ODduMnAxNmJmMnBsbGpxdzBjN3N5In0.QfEgY1VWtAd6HWj-Q64PKQ
