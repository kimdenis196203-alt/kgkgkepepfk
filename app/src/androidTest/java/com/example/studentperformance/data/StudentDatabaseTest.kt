package com.example.studentperformance.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StudentDatabaseTest {
    private lateinit var database: StudentDatabase
    private lateinit var repository: StudentRepository

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, StudentDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = StudentRepository(database)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun disciplineAndGradeSupportCrudAndCascadeDelete() = runBlocking {
        val disciplineId = repository.addDiscipline("Физика")
        val gradeId = repository.addGrade(
            disciplineId = disciplineId,
            value = 4,
            date = "2026-11-24",
            workType = "Лабораторная",
            comment = "Работа принята"
        )

        val created = repository.getGrade(gradeId)
        assertNotNull(created)
        assertEquals(4, created?.value)

        repository.updateGrade(requireNotNull(created).copy(value = 5))
        assertEquals(5, repository.getGrade(gradeId)?.value)

        val currentDiscipline = requireNotNull(repository.getDiscipline(disciplineId))
        repository.deleteDiscipline(currentDiscipline)

        assertNull(repository.getDiscipline(disciplineId))
        assertNull(repository.getGrade(gradeId))
        assertEquals(emptyList<GradeEntity>(), repository.observeGrades(disciplineId).first())
    }

    @Test
    fun recordsRemainAfterDatabaseIsClosedAndReopened() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "persistence-test.db"
        context.deleteDatabase(databaseName)

        val firstOpen = Room.databaseBuilder(
            context,
            StudentDatabase::class.java,
            databaseName
        ).allowMainThreadQueries().build()

        val firstRepository = StudentRepository(firstOpen)
        val disciplineId = firstRepository.addDiscipline("Химия")
        val gradeId = firstRepository.addGrade(
            disciplineId = disciplineId,
            value = 5,
            date = "2026-11-24",
            workType = "Практическая"
        )
        firstOpen.close()

        val secondOpen = Room.databaseBuilder(
            context,
            StudentDatabase::class.java,
            databaseName
        ).allowMainThreadQueries().build()

        try {
            val restored = StudentRepository(secondOpen).getGrade(gradeId)
            assertNotNull(restored)
            assertEquals("Химия", StudentRepository(secondOpen).getDiscipline(disciplineId)?.name)
            assertEquals(5, restored?.value)
        } finally {
            secondOpen.close()
            context.deleteDatabase(databaseName)
        }
    }
}
