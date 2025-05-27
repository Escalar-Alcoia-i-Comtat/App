package org.escalaralcoiaicomtat.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.escalaralcoiaicomtat.app.data.Blocking
import org.escalaralcoiaicomtat.app.database.BlockingInterface
import org.escalaralcoiaicomtat.app.database.dao.BaseDao.EntityInterfaceImpl
import org.escalaralcoiaicomtat.app.database.entity.BlockingEntity

@Dao
interface BlockingDao : BaseDao<Blocking, BlockingEntity> {
    @Query("SELECT * FROM BlockingEntity")
    override suspend fun all(): List<BlockingEntity>

    @Query("SELECT * FROM BlockingEntity")
    override fun allLive(): Flow<List<BlockingEntity>>

    @Query("SELECT * FROM BlockingEntity WHERE pathId=:pathId")
    suspend fun getByAreaId(pathId: Long): List<BlockingEntity>

    @Query("SELECT * FROM BlockingEntity WHERE id = :id")
    override suspend fun get(id: Long): BlockingEntity?

    @Query("SELECT * FROM BlockingEntity WHERE id = :id")
    override fun getLive(id: Long): Flow<BlockingEntity?>

    @Query("SELECT COUNT(id) FROM BlockingEntity")
    override suspend fun count(): Int

    override fun constructor(type: Blocking): BlockingEntity = BlockingEntity(type)

    override fun asInterface(): BlockingInterface = BlockingInterfaceImpl(this)

    class BlockingInterfaceImpl(
        dao: BlockingDao,
    ) : EntityInterfaceImpl<Blocking, BlockingEntity>(dao), BlockingInterface {
        override suspend fun getByPathId(parentId: Long): List<Blocking> {
            return (dao as BlockingDao).getByAreaId(parentId).map { it.convert() }
        }
    }
}
