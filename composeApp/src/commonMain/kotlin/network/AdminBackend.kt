package network

import data.Area
import data.DataType
import data.DataTypeWithImage
import data.DataTypeWithParent
import data.DataTypeWithPoint
import data.DataTypes
import database.DatabaseInterface
import database.SettingsKeys
import database.byType
import database.settings
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.extension
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.defaultForFileExtension
import kotlinx.serialization.KSerializer
import network.response.data.UpdateResponseData

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

    suspend fun <DT: DataType> patch(
        item: DT,
        type: DataTypes<DT>,
        serializer: KSerializer<DT>,
        image: PlatformFile?,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
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

        return patch(
            UpdateResponseData.serializer(serializer),
            type.path, item.id,
            progress = progress,
            requestBuilder = {
                bearerAuth(token)
            },
        ) {
            if (stored.displayName != item.displayName) append("displayName", item.displayName)

            if (item is DataTypeWithParent) {
                stored as DataTypeWithParent
                if (stored.parentId != item.parentId) append("parentId", item.parentId)
            }

            if (item is DataTypeWithPoint) {
                stored as DataTypeWithPoint
                if (stored.point != item.point) append("parentId", item.parentId)
            }

            if (imageBytes != null) {
                append(
                    "image",
                    imageBytes,
                    Headers.build {
                        append(HttpHeaders.ContentType, ContentType.defaultForFileExtension(image.extension).toString())
                        append(HttpHeaders.ContentDisposition, "filename=\"${image.name}\"")
                    },
                )
            }
        }.element.also { int.update(listOf(it)) }
    }

    suspend fun patchArea(
        area: Area,
        image: PlatformFile?,
        progress: (suspend (current: Long, total: Long) -> Unit)? = null,
    ): Area? = patch(area, DataTypes.Area, Area.serializer(), image, progress)
}
