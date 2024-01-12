package github.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    val url: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("assets_url") val assetsUrl: String,
    @SerialName("upload_url") val uploadUrl: String,
    @SerialName("tarball_url") val tarballUrl: String,
    @SerialName("zipball_url") val zipballUrl: String,
    val id: Long,
    @SerialName("node_id") val nodeId: String,
    @SerialName("tag_name") val tagName: String,
    val name: String,
    val body: String,
    val draft: Boolean,
    val prerelease: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("published_at") val publishedAt: String,
    val assets: List<ReleaseAsset>
)
