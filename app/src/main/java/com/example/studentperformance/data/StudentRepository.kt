package com.example.studentperformance.data

import kotlinx.coroutines.flow.Flow

class StudentRepository(private val database: StudentDatabase) {
    private val disciplines = database.disciplineDao()
    private val grades = database.gradeDao()

    fun observeDisciplines(): Flow<List<DisciplineEntity>> = disciplines.observeAll()
    fun observeGrades(): Flow<List<GradeEntity>> = grades.observeAll()
    fun observeGrades(disciplineId: Long): Flow<List<GradeEntity>> =
        grades.observeForDiscipline(disciplineId)

    suspend fun getDiscipline(id: Long) = disciplines.getById(id)
    suspend fun getGrade(id: Long) = grades.getById(id)

    suspend fun addDiscipline(name: String, teacherName: String? = null): Long {
        require(name.isNotBlank()) { "Название дисциплины обязательно" }
        return disciplines.insert(
            DisciplineEntity(name = name.trim(), teacherName = teacherName?.trim()?.ifBlank { null })
        )
    }

    suspend fun updateDiscipline(discipline: DisciplineEntity) {
        require(discipline.name.isNotBlank()) { "Название дисциплины обязательно" }
        disciplines.update(discipline.copy(name = discipline.name.trim()))
    }

    suspend fun deleteDiscipline(discipline: DisciplineEntity) = disciplines.delete(discipline)

    suspend fun addGrade(
        disciplineId: Long,
        value: Int,
        date: String,
        workType: String,
        comment: String? = null
    ): Long = grades.insert(
        GradeEntity(
            disciplineId = disciplineId,
            value = value,
            date = date,
            workType = workType.trim(),
            comment = comment?.trim()?.ifBlank { null }
        )
    )

    suspend fun updateGrade(grade: GradeEntity) = grades.update(grade)
    suspend fun deleteGrade(grade: GradeEntity) = grades.delete(grade)

    fun average(grades: List<GradeEntity>): Double? =
        grades.takeIf { it.isNotEmpty() }?.map { it.value }?.average()
}
