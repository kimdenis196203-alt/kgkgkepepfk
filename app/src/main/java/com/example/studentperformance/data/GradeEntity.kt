package com.example.studentperformance.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grades",
    foreignKeys = [
        ForeignKey(
            entity = DisciplineEntity::class,
            parentColumns = ["id"],
            childColumns = ["disciplineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("disciplineId")]
)
data class GradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val disciplineId: Long,
    val value: Int,
    /** ISO-8601 date: yyyy-MM-dd */
    val date: String,
    val workType: String,
    val comment: String? = null
) {
    init {
        require(value in MIN_GRADE..MAX_GRADE) {
            "Оценка должна быть в диапазоне $MIN_GRADE–$MAX_GRADE"
        }
        require(date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            "Дата должна иметь формат yyyy-MM-dd"
        }
        require(workType.isNotBlank()) { "Тип работы обязателен" }
    }

    companion object {
        const val MIN_GRADE = 2
        const val MAX_GRADE = 5
    }
}
