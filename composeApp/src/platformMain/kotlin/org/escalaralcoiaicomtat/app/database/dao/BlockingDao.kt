package org.escalaralcoiaicomtat.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.database.entity.BlockingEntity

@Dao
interface BlockingDao : BaseDao<Blocking, BlockingEntity> {
    @Query("SELECT * FROM BlockingEntity")
    override suspend fun all(): List<BlockingEntity>

    @Query("SELECT * FROM BlockingEntity")
    override fun allLive(): Flow<List<BlockingEntity>>

    @Query("SELECT * FROM BlockingEntity WHERE id = :id")
    override suspend fun get(id: Long): BlockingEntity?

    override fun constructor(type: Blocking): BlockingEntity = BlockingEntity(type)
}
