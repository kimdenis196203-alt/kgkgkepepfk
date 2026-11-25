package com.example.studentperformance.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GradeEntityValidationTest {
    @Test
    fun acceptsGradeInFivePointRange() {
        val grade = GradeEntity(
            disciplineId = 1,
            value = 5,
            date = "2026-11-24",
            workType = "Контрольная"
        )
        assertEquals(5, grade.value)
    }

    @Test
    fun rejectsGradeOutsideFivePointRange() {
        assertThrows(IllegalArgumentException::class.java) {
            GradeEntity(
                disciplineId = 1,
                value = 6,
                date = "2026-11-24",
                workType = "Контрольная"
            )
        }
    }

    @Test
    fun rejectsBlankWorkType() {
        assertThrows(IllegalArgumentException::class.java) {
            GradeEntity(
                disciplineId = 1,
                value = 4,
                date = "2026-11-24",
                workType = " "
            )
        }
    }

    @Test
    fun rejectsWrongDateFormat() {
        assertThrows(IllegalArgumentException::class.java) {
            GradeEntity(
                disciplineId = 1,
                value = 4,
                date = "24.11.2026",
                workType = "Практическая"
            )
        }
    }
}
