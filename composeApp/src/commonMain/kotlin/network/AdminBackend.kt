package network

import data.Area
import data.DataType
import data.DataTypeWithGPX
import data.DataTypeWithImage
import data.DataTypeWithKMZ
import data.DataTypeWithParent
import data.DataTypeWithPoint
import data.DataTypeWithPoints
import data.DataTypes
import data.Path
import data.Sector
import data.Zone
import data.generic.Builder
import data.generic.LatLng
import data.generic.PitchInfo
import data.generic.Point
import database.DatabaseInterface
import database.SettingsKeys
import database.byType
import database.parentInterface
import database.settings
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.core.PlatformFile
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import network.response.data.DataResponseType
import network.response.data.UpdateResponseData
import utils.append
import utils.appendOrRemove
import utils.appendSerializable

object AdminBackend : Backend() {
    /**
     * Checks whether the given key is the correct one.
     */
    suspend fun validateApiKey(apiKey: String): Boolean {
        val request = client.submitFormWithBinaryData(
            url = URLBuilder(baseUrl)
                .appendPathSegments("area")
                .buildString(),
            formData = emptyList()
        ) {
            bearerAuth(apiKey)
        }
        // Since we are not properly making the request, it will return BadRequest, but if it does,
        // it means that the key is correct.
        return request.status == HttpStatusCode.BadRequest
    }

    private suspend fun <DT: DataType> delete(
        item: DT,
        type: DataTypes<DT>,
    ) {
        val token = settings.getStringOrNull(SettingsKeys.API_KEY)
        checkNotNull(token) { "There isn't any stored token." }

        val int = DatabaseInterface.byType(type)
        val stored = int.get(item.id)
        requireNotNull(stored) { "Could not find the $type in the database." }

        delete(
            DataResponseType.serializer(),
            type.path, item.id,
            requestBuilder = {
                bearerAuth(token)
            },
        )

        int.delete(listOf(stored))
    }

    private suspend fun <DT: DataType> patch(
        item: DT,
        type: DataTypes<DT>,
        serializer: KSerializer<DT>,
        progress: (suspend (current: Long, total: Long) -> Unit)?,
        image: PlatformFile? = null,
        kmz: PlatformFile? = null,
        gpx: PlatformFile? = null,
    ): DT? {
        val token = settings.getStringOrNull(SettingsKeys.API_KEY)
        checkNotNull(token) { "There isn't any stored token." }

        val int = DatabaseInterface.byType(type)
        val stored = int.get(item.id)
        if (stored == item) {
            Napier.i { "Tried to patch an unmodified $type." }
            return null
        }
        requireNotNull(stored) { "Could not find the $type in the database." }
        require(item.id > 0) { "The $type must already exist in order to patch it." }

        require(item is DataTypeWithImage || image == null) { "Cannot pass an image to a data type that doesn't support it." }
        val imageBytes = image?.readBytes()

        require(item is DataTypeWithKMZ || kmz == null) { "Cannot pass a kmz to a data type that doesn't support it." }
        val kmzBytes = kmz?.readBytes()

        require(item is DataTypeWithGPX || gpx == null) { "Cannot pass a gpx to a data type that doesn't support it." }
        val gpxBytes = gpx?.readBytes()

        return try {
            submitForm(
                UpdateResponseData.serializer(serializer),
                type.path, item.id,
                progress = progress,
                requestBuilder = {
                    bearerAuth(token)
                },
            ) {
                if (stored.displayName != item.displayName) append("displayName", item.displayName)

                if (item is DataTypeWithParent && type.parentDataType != null) {
                    stored as DataTypeWithParent
                    val parentDataType = type.parentDataType
                    if (stored.parentId != item.parentId) {
                        append(parentDataType.path, item.parentId)
                    }
                }

                if (item is DataTypeWithPoint) {
                    stored as DataTypeWithPoint
                    if (stored.point != item.point) {
                        appendOrRemove("point", item.point, LatLng.serializer())
                    }
                }

                if (item is DataTypeWithPoints) {
                    stored as DataTypeWithPoints
                    if (stored.points != item.points) {
                        appendOrRemove("points", item.points, ListSerializer(Point.serializer()))
                    }
                }

                if (imageBytes != null) {
                    append("image", image, imageBytes)
                }
                if (kmzBytes != null) {
                    append("kmz", kmz, kmzBytes)
                }
                if (gpxBytes != null) {
                    append("gpx", gpx, gpxBytes)
                }

                if (item is Sector) {
                    stored as Sector
                    if (stored.kidsApt != item.kidsApt) append("kidsApt", item.kidsApt)
                    if (stored.sunTime != item.sunTime) append("sunTime", item.sunTime.name)
                    if (stored.weight != item.weight) append("weight", item.weight)
                    if (stored.walkingTime != item.walkingTime) appendOrRemove("walkingTime", item.walkingTime)
                    if (stored.tracks != item.tracks) {
                        if (item.tracks == null) {
                            append("tracks", "")
                        } else {
                            append("tracks", item.tracks.joinToString("\n") { "${it.type};${it.url}" })
                        }
                    }
                }
                if (item is Path) {
                    stored as Path
                    if (stored.sketchId != item.sketchId) append("sketchId", item.sketchId.toInt())

                    if (stored.height != item.height) appendOrRemove("height", item.height?.toInt())
                    if (stored.grade != item.grade) appendOrRemove("grade", item.gradeValue)
                    if (stored.ending != item.ending) appendOrRemove("ending", item.ending?.name)

                    if (stored.pitches != item.pitches) appendOrRemove("pitches", item.pitches, ListSerializer(PitchInfo.serializer()))

                    if (stored.stringCount != item.stringCount) appendOrRemove("stringCount", item.stringCount?.toInt())
                    if (stored.paraboltCount != item.paraboltCount) appendOrRemove("paraboltCount", item.paraboltCount?.toInt())
                    if (stored.burilCount != item.burilCount) appendOrRemove("burilCount", item.burilCount?.toInt())
                    if (stored.pitonCount != item.pitonCount) appendOrRemove("pitonCount", item.pitonCount?.toInt())
                    if (stored.spitCount != item.spitCount) appendOrRemove("spitCount", item.spitCount?.toInt())
                    if (stored.tensorCount != item.tensorCount) appendOrRemove("tensorCount", item.tensorCount?.toInt())

                    if (stored.nutRequired != item.nutRequired) appendOrRemove("crackerRequired", item.nutRequired)
                    if (stored.friendRequired != item.friendRequired) appendOrRemove("friendRequired", item.friendRequired)
                    if (stored.lanyardRequired != item.lanyardRequired) appendOrRemove("lanyardRequired", item.lanyardRequired)
                    if (stored.nailRequired != item.nailRequired) appendOrRemove("nailRequired", item.nailRequired)
                    if (stored.pitonRequired != item.pitonRequired) appendOrRemove("pitonRequired", item.pitonRequired)
                    if (stored.stapesRequired != item.stapesRequired) appendOrRemove("stapesRequired", item.stapesRequired)

                    if (stored.showDescription != item.showDescription) appendOrRemove("showDescription", item.showDescription)
                    if (stored.description != item.description) appendOrRemove("description", item.description)

                    if (stored.builder != item.builder) appendOrRemove("builder", item.builder, Builder.serializer())
                    if (stored.reBuilders != item.reBuilders) appendOrRemove("reBuilder", item.reBuilders, ListSerializer(Builder.serializer()))

                    // TODO: Upload and remove images
                }
            }.element.also { int.update(listOf(it)) }
        } catch (_: IllegalArgumentException) {
            Napier.w { "Nothing to update" }
            item
        }
    }

    private suspend fun <DT: DataType> create(
        item: DT,
        type: DataTypes<DT>,
        serializer: KSerializer<DT>,
        progress: (suspend (current: Long, total: Long) -> Unit)?,
        image: PlatformFile? = null,
        kmz: PlatformFile? = null,
        gpx: PlatformFile? = null,
    ): DT {
        val token = settings.getStringOrNull(SettingsKeys.API_KEY)
        checkNotNull(token) { "There isn't any stored token." }

        val int = DatabaseInterface.byType(type)
        require(item.id == 0L) { "The $type must not already exist." }

        require(item !is DataTypeWithImage || image != null) { "An image is required." }
        val imageBytes = image?.readBytes()

        require(item !is DataTypeWithKMZ || kmz != null) { "A KMZ is required." }
        val kmzBytes = kmz?.readBytes()

        require(item is DataTypeWithGPX || gpx == null) { "Cannot pass a gpx to a data type that doesn't support it." }
        val gpxBytes = gpx?.readBytes()

        // Verify that a parent is set if required
        if (item is DataTypeWithParent) {
            require(type.parentDataType == null || item.parentId != 0L) {
                "A parent must be passed to types that support it."
            }
            requireNotNull(int.parentInterface(type).get(item.parentId)) {
                "Could not find the set item's parent."
            }
        }

        // Validate the display name field
        require(item.displayName.isNotBlank())

        return submitForm(
            UpdateResponseData.serializer(serializer),
            type.path,
            progress = progress,
            requestBuilder = {
                bearerAuth(token)
            },
        ) {
            append("displayName", item.displayName)
            append("webUrl", "https://escalaralcoiaicomtat.org")

            if (item is DataTypeWithParent) {
                val parentDataType = type.parentDataType!!
                append(parentDataType.path, item.parentId)
            }

            if (item is DataTypeWithPoint) {
                item.point?.let {
                    appendSerializable("point", it, LatLng.serializer())
                }
            }

            if (imageBytes != null) {
                append("image", image, imageBytes)
            }
            if (kmzBytes != null) {
                append("kmz", kmz, kmzBytes)
            }
            if (gpxBytes != null) {
                append("gpx", gpx, gpxBytes)
            }

            if (item is Sector) {
                append("kidsApt", item.kidsApt)
                append("sunTime", item.sunTime.name)
                append("weight", item.weight)
                if (item.walkingTime != null) {
                    append("walkingTime", item.walkingTime)
                }
                if (item.tracks != null) {
                    append("tracks", item.tracks.joinToString("\n") { "${it.type};${it.url}" })
                }
            }
            if (item is Path) {
                append("sketchId", item.sketchId.toInt())

                if (item.height != null) append("height", item.height.toInt())
                if (item.gradeValue != null) append("grade", item.gradeValue)
                if (item.ending != null) append("ending", item.ending.name)

                if (item.pitches != null) appendSerializable("pitches", item.pitches, ListSerializer(PitchInfo.serializer()))

                if (item.stringCount != null) append("stringCount", item.stringCount.toInt())
                if (item.paraboltCount != null) append("paraboltCount", item.paraboltCount.toInt())
                if (item.burilCount != null) append("burilCount", item.burilCount.toInt())
                if (item.pitonCount != null) append("pitonCount", item.pitonCount.toInt())
                if (item.spitCount != null) append("spitCount", item.spitCount.toInt())
                if (item.tensorCount != null) append("tensorCount", item.tensorCount.toInt())

                append("crackerRequired", item.nutRequired)
                append("friendRequired", item.friendRequired)
                append("lanyardRequired", item.lanyardRequired)
                append("nailRequired", item.nailRequired)
                append("pitonRequired", item.pitonRequired)
                append("stapesRequired", item.stapesRequired)

                append("showDescription", item.showDescription)
                if (item.description != null) append("description", item.description)

                if (item.builder != null) appendSerializable("builder", item.builder, Builder.serializer())
                if (item.reBuilders != null) appendSerializable("reBuilder", item.reBuilders, ListSerializer(Builder.serializer()))

                // TODO: Upload and remove images
            }
        }.element.also { int.insert(listOf(it)) }
    }

    suspend fun patch(
        area: Area,
        image: PlatformFile?,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Area? = patch(area, DataTypes.Area, Area.serializer(), progress, image)

    suspend fun patch(
        zone: Zone,
        image: PlatformFile?,
        kmz: PlatformFile?,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Zone? = patch(zone, DataTypes.Zone, Zone.serializer(), progress, image, kmz = kmz)

    suspend fun patch(
        sector: Sector,
        image: PlatformFile?,
        gpx: PlatformFile?,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Sector? = patch(sector, DataTypes.Sector, Sector.serializer(), progress, image, gpx = gpx)

    suspend fun patch(
        path: Path,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Path? = patch(path, DataTypes.Path, Path.serializer(), progress)

    suspend fun delete(area: Area) = delete(area, DataTypes.Area)
    suspend fun delete(zone: Zone) = delete(zone, DataTypes.Zone)
    suspend fun delete(sector: Sector) = delete(sector, DataTypes.Sector)
    suspend fun delete(path: Path) = delete(path, DataTypes.Path)

    suspend fun create(
        area: Area,
        image: PlatformFile?,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Area = create(area, DataTypes.Area, Area.serializer(), progress, image)

    suspend fun create(
        zone: Zone,
        image: PlatformFile?,
        kmz: PlatformFile?,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Zone = create(zone, DataTypes.Zone, Zone.serializer(), progress, image, kmz = kmz)

    suspend fun create(
        sector: Sector,
        image: PlatformFile?,
        gpx: PlatformFile?,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Sector = create(sector, DataTypes.Sector, Sector.serializer(), progress, image, gpx = gpx)

    suspend fun create(
        path: Path,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Path = create(path, DataTypes.Path, Path.serializer(), progress)

}
