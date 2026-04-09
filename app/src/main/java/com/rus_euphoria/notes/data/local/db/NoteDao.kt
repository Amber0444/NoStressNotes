package com.rus_euphoria.notes.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun observeAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE uid = :uid LIMIT 1")
    suspend fun getByUid(uid: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NoteEntity)

    @Query("DELETE FROM notes WHERE uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("DELETE FROM notes")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(entities: List<NoteEntity>) {
        clear()
        entities.forEach { upsert(it) }
    }
}
