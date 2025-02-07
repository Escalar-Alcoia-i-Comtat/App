package database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import data.Path
import data.generic.Builder
import data.generic.Ending
import data.generic.PitchInfo
import kotlinx.datetime.Instant

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = SectorEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentSectorId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class PathEntity(
    @PrimaryKey override val id: Long,
    override val timestamp: Instant,
    val displayName: String,

    val sketchId: Int,

    val height: Int? = null,
    val gradeValue: String? = null,
    val ending: Ending? = null,
    val pitches: List<PitchInfo>? = null,

    val stringCount: Int? = null,

    val paraboltCount: Int? = null,
    val burilCount: Int? = null,
    val pitonCount: Int? = null,
    val spitCount: Int? = null,
    val tensorCount: Int? = null,

    val nutRequired: Boolean,
    val friendRequired: Boolean,
    val lanyardRequired: Boolean,
    val nailRequired: Boolean,
    val pitonRequired: Boolean,
    val stapesRequired: Boolean,

    val showDescription: Boolean,
    val description: String? = null,

    val builder: Builder? = null,
    val reBuilders: List<Builder>? = null,

    val images: List<String>? = null,

    val parentSectorId: Long
) : DatabaseEntity<Path> {
    override suspend fun convert(): Path = Path(
        id,
        timestamp.toEpochMilliseconds(),
        displayName,
        sketchId.toUInt(),
        height?.toUInt(),
        gradeValue,
        ending,
        pitches,
        stringCount?.toUInt(),
        paraboltCount?.toUInt(),
        burilCount?.toUInt(),
        pitonCount?.toUInt(),
        spitCount?.toUInt(),
        tensorCount?.toUInt(),
        nutRequired,
        friendRequired,
        lanyardRequired,
        nailRequired,
        pitonRequired,
        stapesRequired,
        showDescription,
        description,
        builder,
        reBuilders,
        images,
        parentSectorId
    )
}
