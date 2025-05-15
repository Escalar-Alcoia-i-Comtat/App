package org.escalaralcoiaicomtat.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.escalaralcoiaicomtat.app.data.Path
import org.escalaralcoiaicomtat.app.database.entity.PathEntity

@Dao
interface PathsDao : DataTypeDao<Path, PathEntity> {
    @Query("SELECT * FROM PathEntity")
    override suspend fun all(): List<PathEntity>

    @Query("SELECT * FROM PathEntity")
    override fun allLive(): Flow<List<PathEntity>>

    @Query("SELECT * FROM PathEntity WHERE parentSectorId=:parentSectorId")
    suspend fun findBySectorId(parentSectorId: Long): List<PathEntity>

    @Query("SELECT * FROM PathEntity WHERE id = :id")
    override suspend fun get(id: Long): PathEntity?

    @Query("SELECT COUNT(id) FROM PathEntity")
    override suspend fun count(): Int

    override suspend fun getByParentId(parentId: Long): List<PathEntity> = findBySectorId(parentId.toLong())

    override fun constructor(type: Path): PathEntity {
        return with(type) {
            PathEntity(
                id,
                Instant.fromEpochMilliseconds(timestamp),
                displayName,
                sketchId.toInt(),
                height?.toInt(),
                grade,
                ending,
                pitches,
                stringCount?.toInt(),
                paraboltCount?.toInt(),
                burilCount?.toInt(),
                pitonCount?.toInt(),
                spitCount?.toInt(),
                tensorCount?.toInt(),
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
    }
}
