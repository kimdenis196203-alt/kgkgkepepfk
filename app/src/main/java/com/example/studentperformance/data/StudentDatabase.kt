package com.example.studentperformance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DisciplineEntity::class, GradeEntity::class],
    version = 1,
    exportSchema = true
)
abstract class StudentDatabase : RoomDatabase() {
    abstract fun disciplineDao(): DisciplineDao
    abstract fun gradeDao(): GradeDao

    companion object {
        @Volatile
        private var INSTANCE: StudentDatabase? = null

        fun getInstance(context: Context): StudentDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    "student-performance.db"
                )
                    .addCallback(DatabaseSeeder.callback)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
