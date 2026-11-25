package com.example.studentperformance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disciplines")
data class DisciplineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val teacherName: String? = null
)
