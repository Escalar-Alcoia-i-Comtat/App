package org.escalaralcoiaicomtat.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.data.Area
import org.escalaralcoiaicomtat.app.database.entity.AreaEntity

@Dao
interface AreasDao : BaseDao<Area, AreaEntity> {
    @Query("SELECT * FROM AreaEntity")
    override suspend fun all(): List<AreaEntity>

    @Query("SELECT * FROM AreaEntity")
    override fun allLive(): Flow<List<AreaEntity>>

    @Query("SELECT * FROM AreaEntity WHERE id = :id")
    override suspend fun get(id: Long): AreaEntity?

    override suspend fun getByParentId(parentId: Long): List<AreaEntity> {
        throw UnsupportedOperationException("Areas do not have parents.")
    }

    override fun constructor(type: Area): AreaEntity {
        return with(type) {
            AreaEntity(id, Instant.fromEpochMilliseconds(timestamp), displayName, image!!)
        }
    }
}
