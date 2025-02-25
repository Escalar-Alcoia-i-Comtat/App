package database.dao

import androidx.room.Dao
import androidx.room.Query
import data.Zone
import database.entity.ZoneEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface ZonesDao : BaseDao<Zone, ZoneEntity> {
    @Query("SELECT * FROM ZoneEntity")
    override suspend fun all(): List<ZoneEntity>

    @Query("SELECT * FROM ZoneEntity")
    override fun allLive(): Flow<List<ZoneEntity>>

    @Query("SELECT * FROM ZoneEntity WHERE parentAreaId=:parentAreaId")
    suspend fun findByAreaId(parentAreaId: Long): List<ZoneEntity>

    @Query("SELECT * FROM ZoneEntity WHERE id = :id")
    override suspend fun get(id: Long): ZoneEntity?

    override suspend fun getByParentId(parentId: Long): List<ZoneEntity> = findByAreaId(parentId)

    override fun constructor(type: Zone): ZoneEntity {
        return with(type) {
            ZoneEntity(
                id,
                Instant.fromEpochMilliseconds(timestamp),
                displayName,
                image!!,
                kmz!!,
                point,
                points,
                parentAreaId
            )
        }
    }
}
