package org.escalaralcoiaicomtat.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.escalaralcoiaicomtat.app.data.Zone
import org.escalaralcoiaicomtat.app.database.entity.ZoneEntity
import kotlin.time.Instant

@Dao
interface ZonesDao : DataTypeDao<Zone, ZoneEntity> {
    @Query("SELECT * FROM ZoneEntity")
    override suspend fun all(): List<ZoneEntity>

    @Query("SELECT * FROM ZoneEntity")
    override fun allLive(): Flow<List<ZoneEntity>>

    @Query("SELECT * FROM ZoneEntity WHERE parentAreaId=:parentAreaId")
    suspend fun findByAreaId(parentAreaId: Long): List<ZoneEntity>

    @Query("SELECT * FROM ZoneEntity WHERE id = :id")
    override suspend fun get(id: Long): ZoneEntity?

    @Query("SELECT * FROM ZoneEntity WHERE id = :id")
    override fun getLive(id: Long): Flow<ZoneEntity?>

    @Query("SELECT COUNT(id) FROM ZoneEntity")
    override suspend fun count(): Int

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
