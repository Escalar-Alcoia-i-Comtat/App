package database.dao

import androidx.room.Dao
import androidx.room.Query
import data.Sector
import database.entity.SectorEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface SectorsDao : BaseDao<Sector, SectorEntity> {
    @Query("SELECT * FROM SectorEntity")
    override suspend fun all(): List<SectorEntity>

    @Query("SELECT * FROM SectorEntity")
    override fun allLive(): Flow<List<SectorEntity>>

    @Query("SELECT * FROM SectorEntity WHERE parentZoneId=:parentZoneId")
    suspend fun findByZoneId(parentZoneId: Long): List<SectorEntity>

    @Query("SELECT * FROM SectorEntity WHERE id = :id")
    override suspend fun get(id: Long): SectorEntity?

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
