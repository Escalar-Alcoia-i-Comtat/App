package org.escalaralcoiaicomtat.app.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.data.generic.BlockingRecurrenceYearly
import org.escalaralcoiaicomtat.app.data.generic.BlockingTypes

@Entity(
    indices = [Index("pathId", unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = PathEntity::class,
            parentColumns = ["id"],
            childColumns = ["pathId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class BlockingEntity(
    @PrimaryKey override val id: Long,
    override val timestamp: Instant,
    val type: BlockingTypes,
    val recurrence: BlockingRecurrenceYearly? = null,
    val endDate: LocalDateTime? = null,
    val pathId: Int,
) : DatabaseEntity<Blocking> {
    constructor(blocking: Blocking) : this(
        blocking.id,
        blocking.timestamp.let(Instant::fromEpochMilliseconds),
        blocking.type,
        blocking.recurrence,
        blocking.endDate,
        blocking.pathId
    )

    override suspend fun convert(): Blocking {
        return Blocking(id, timestamp.toEpochMilliseconds(), type, recurrence, endDate, pathId)
    }
}
