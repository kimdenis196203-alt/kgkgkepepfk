package com.example.studentperformance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    @Query("SELECT * FROM grades WHERE disciplineId = :disciplineId ORDER BY date DESC, id DESC")
    fun observeForDiscipline(disciplineId: Long): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE id = :id")
    suspend fun getById(id: Long): GradeEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(grade: GradeEntity): Long

    @Update
    suspend fun update(grade: GradeEntity)

    @Delete
    suspend fun delete(grade: GradeEntity)

    @Query("DELETE FROM grades WHERE id = :id")
    suspend fun deleteById(id: Long)
}
