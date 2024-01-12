package github.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseAsset(
    val url: String,
    @SerialName("browser_download_url") val browserDownloadUrl: String,
    val name: String,
    @SerialName("content_type") val contentType: String,
    val size: Long
)
