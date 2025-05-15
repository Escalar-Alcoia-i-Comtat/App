package org.escalaralcoiaicomtat.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.data.Area
import kotlin.uuid.Uuid

@Entity
data class AreaEntity(
    @PrimaryKey override val id: Long,
    override val timestamp: Instant,
    val displayName: String,
    val image: Uuid,
) : DatabaseEntity<Area> {
    override suspend fun convert(): Area = Area(
        id,
        timestamp.toEpochMilliseconds(),
        displayName,
        image,
    )
}
