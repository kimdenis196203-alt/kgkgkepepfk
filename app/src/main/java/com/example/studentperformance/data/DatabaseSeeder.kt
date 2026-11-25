package com.example.studentperformance.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseSeeder {
    val callback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL("INSERT INTO disciplines (name, teacherName) VALUES ('Математика', 'Иванова И.И.')")
            db.execSQL("INSERT INTO disciplines (name, teacherName) VALUES ('История', NULL)")
            db.execSQL("INSERT INTO disciplines (name, teacherName) VALUES ('Программирование', 'Петров П.П.')")
            db.execSQL(
                "INSERT INTO grades (disciplineId, value, date, workType, comment) " +
                    "VALUES (1, 5, '2026-09-12', 'Контрольная', 'Первый тест')"
            )
            db.execSQL(
                "INSERT INTO grades (disciplineId, value, date, workType, comment) " +
                    "VALUES (1, 4, '2026-09-19', 'Практическая', NULL)"
            )
            db.execSQL(
                "INSERT INTO grades (disciplineId, value, date, workType, comment) " +
                    "VALUES (3, 5, '2026-09-20', 'Лабораторная', 'Отлично')"
            )
        }
    }
}
