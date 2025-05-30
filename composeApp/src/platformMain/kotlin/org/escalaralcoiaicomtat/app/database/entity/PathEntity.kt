package org.escalaralcoiaicomtat.app.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.data.generic.Builder
import org.escalaralcoiaicomtat.app.data.generic.Ending
import org.escalaralcoiaicomtat.app.data.generic.GradeValue
import org.escalaralcoiaicomtat.app.data.generic.PitchInfo
import kotlin.uuid.Uuid

@Entity(
    indices = [Index("parentSectorId", unique = false)],
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
    val grade: GradeValue? = null,
    val aidGrade: GradeValue? = null,
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

    val images: List<Uuid>? = null,

    val parentSectorId: Long
) : DatabaseEntity<Path> {
    override suspend fun convert(): Path = Path(
        id,
        timestamp.toEpochMilliseconds(),
        displayName,
        sketchId.toUInt(),
        height?.toUInt(),
        grade,
        aidGrade,
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
