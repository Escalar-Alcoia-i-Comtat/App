package org.escalaralcoiaicomtat.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.escalaralcoiaicomtat.app.data.Sector
import org.escalaralcoiaicomtat.app.database.entity.SectorEntity
import kotlin.time.Instant

@Dao
interface SectorsDao : DataTypeDao<Sector, SectorEntity> {
    @Query("SELECT * FROM SectorEntity")
    override suspend fun all(): List<SectorEntity>

    @Query("SELECT * FROM SectorEntity")
    override fun allLive(): Flow<List<SectorEntity>>

    @Query("SELECT * FROM SectorEntity WHERE parentZoneId=:parentZoneId")
    suspend fun findByZoneId(parentZoneId: Long): List<SectorEntity>

    @Query("SELECT * FROM SectorEntity WHERE id = :id")
    override suspend fun get(id: Long): SectorEntity?

    @Query("SELECT * FROM SectorEntity WHERE id = :id")
    override fun getLive(id: Long): Flow<SectorEntity?>

    @Query("SELECT COUNT(id) FROM SectorEntity")
    override suspend fun count(): Int

    override suspend fun getByParentId(parentId: Long): List<SectorEntity> = findByZoneId(parentId.toLong())

    override fun constructor(type: Sector): SectorEntity {
        return with(type) {
            SectorEntity(
                id,
                Instant.fromEpochMilliseconds(timestamp),
                displayName,
                image!!,
                gpx,
                tracks,
                kidsApt,
                weight,
                walkingTime,
                point,
                sunTime,
                parentZoneId
            )
        }
    }
}
