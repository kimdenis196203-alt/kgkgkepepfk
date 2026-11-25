package com.example.studentperformance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DisciplineDao {
    @Query("SELECT * FROM disciplines ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<DisciplineEntity>>

    @Query("SELECT * FROM disciplines WHERE id = :id")
    suspend fun getById(id: Long): DisciplineEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(discipline: DisciplineEntity): Long

    @Update
    suspend fun update(discipline: DisciplineEntity)

    @Delete
    suspend fun delete(discipline: DisciplineEntity)
}
