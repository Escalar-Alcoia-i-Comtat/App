package org.escalaralcoiaicomtat.app.database

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import org.escalaralcoiaicomtat.app.data.Entity

interface EntityInterface<Type : Entity> {
    suspend fun insert(items: List<Type>)

    suspend fun update(items: List<Type>)

    suspend fun delete(items: List<Type>)

    suspend fun all(): List<Type>

    suspend fun count(): Int

    fun allLive(): Flow<List<Type>>

    suspend fun get(id: Long): Type?

    suspend fun updateOrInsert(item: Type) {
        if (get(item.id) == null) {
            update(listOf(item))
        } else {
            insert(listOf(item))
        }
    }

    suspend fun updateOrInsert(list: List<Type>) {
        val insert = mutableListOf<Type>()
        val update = mutableListOf<Type>()
        val delete = mutableListOf<Type>()

        val entities = all()
        for (entity in list) {
            val existing = entities.find { it.id == entity.id }
            if (existing == null) {
                insert += entity
            } else {
                update += entity
            }
        }
        val entitiesIds = (insert + update).map { it.id }
        for (entity in entities) {
            if (entitiesIds.contains(entity.id)) continue
            delete += entity
        }

        Napier.d { "Inserting ${insert.size}, updating ${update.size}, deleting ${delete.size}" }

        insert(insert)
        update(update)
        delete(delete)
    }
}
