package com.example.studentperformance.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentperformance.data.DisciplineEntity
import com.example.studentperformance.data.GradeEntity
import java.util.Locale

private val AnalyticsInk = Color(0xFF17201B)
private val AnalyticsMuted = Color(0xFF7A817C)
private val AnalyticsLime = Color(0xFFA8ED79)
private val AnalyticsBorder = Color(0xFFE2E5DF)

@Composable
fun FigmaAnalyticsScreen(disciplines: List<DisciplineEntity>, grades: List<GradeEntity>) {
    var targetText by remember { mutableStateOf("4.8") }
    var selectedId by remember(disciplines) { mutableStateOf(disciplines.firstOrNull()?.id ?: 0L) }
    var menuExpanded by remember { mutableStateOf(false) }
    val selectedDiscipline = disciplines.find { it.id == selectedId }
    val subjectGrades = grades.filter { it.disciplineId == selectedId }
    val target = targetText.replace(',', '.').toDoubleOrNull()?.coerceIn(2.0, 5.0) ?: 4.8
    val needed = target * (subjectGrades.size + 1) - subjectGrades.sumOf { it.value }
    val achievable = subjectGrades.isNotEmpty() && needed <= 5.0
    val nextGrade = kotlin.math.ceil(needed).toInt().coerceAtLeast(2)
    val recent = grades.sortedBy { it.date }.takeLast(8)
    val average = grades.takeIf { it.isNotEmpty() }?.map { it.value }?.average()

    Column(Modifier.fillMaxSize().background(Color(0xFFF7F7F3))) {
        Column(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
                Text("ВАША ДИНАМИКА", color = AnalyticsMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Text("Прогресс", color = AnalyticsInk, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).background(Color.White, RoundedCornerShape(22.dp)).padding(20.dp)) {
                Text("Изменение среднего балла", color = AnalyticsMuted, fontSize = 12.sp)
                Text(average?.let { String.format(Locale("ru", "RU"), "%.2f", it) } ?: "—", color = AnalyticsInk, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text("Последние оценки", color = AnalyticsMuted, fontSize = 11.sp)
                Spacer(Modifier.height(14.dp))
                if (recent.isEmpty()) {
                    Box(Modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.Center) {
                        Text("Добавьте оценки, чтобы увидеть динамику", color = AnalyticsMuted, fontSize = 12.sp)
                    }
                } else {
                    Canvas(Modifier.fillMaxWidth().height(112.dp)) {
                        val left = 8f
                        val right = size.width - 8f
                        val top = 8f
                        val bottom = size.height - 8f
                        for (line in 0..3) {
                            val y = top + (bottom - top) * line / 3f
                            drawLine(AnalyticsBorder, Offset(left, y), Offset(right, y), 1f)
                        }
                        val points = recent.mapIndexed { index, grade ->
                            val x = if (recent.size == 1) (left + right) / 2f else left + (right - left) * index / (recent.size - 1)
                            Offset(x, bottom - (grade.value - 2f) / 3f * (bottom - top))
                        }
                        if (points.size > 1) {
                            val path = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                points.drop(1).forEach { lineTo(it.x, it.y) }
                            }
                            drawPath(path, AnalyticsInk, style = Stroke(width = 4f))
                        }
                        points.forEach { drawCircle(AnalyticsLime, 6f, it); drawCircle(AnalyticsInk, 2.5f, it) }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("2", color = AnalyticsMuted, fontSize = 9.sp)
                        Text("3", color = AnalyticsMuted, fontSize = 9.sp)
                        Text("4", color = AnalyticsMuted, fontSize = 9.sp)
                        Text("5", color = AnalyticsMuted, fontSize = 9.sp)
                    }
                }
            }
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text("Целевой результат", color = AnalyticsInk, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Рассчитайте оценку для достижения желаемого среднего балла", color = AnalyticsMuted, fontSize = 11.sp)
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).background(Color.White, RoundedCornerShape(22.dp)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Дисциплина", color = AnalyticsMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Box {
                    OutlinedButton(onClick = { menuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedDiscipline?.name ?: "Нет дисциплин", color = AnalyticsInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        disciplines.forEach { item ->
                            DropdownMenuItem(text = { Text(item.name) }, onClick = { selectedId = item.id; menuExpanded = false })
                        }
                    }
                }
                Text("Желаемый средний балл", color = AnalyticsMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { value -> if (value.length <= 4 && value.all { it.isDigit() || it == '.' || it == ',' }) targetText = value },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("из 5") }
                )
                HorizontalDivider(color = AnalyticsBorder)
                val current = subjectGrades.takeIf { it.isNotEmpty() }?.map { it.value }?.average()
                Text("Текущий средний: " + (current?.let { String.format(Locale("ru", "RU"), "%.2f", it) } ?: "—"), color = AnalyticsMuted, fontSize = 12.sp)
                when {
                    subjectGrades.isEmpty() -> Text("Добавьте первую оценку по этой дисциплине — расчёт появится автоматически.", color = AnalyticsMuted, fontSize = 12.sp)
                    achievable -> {
                        Text("Для достижения цели нужна оценка не ниже ${nextGrade}", color = AnalyticsInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Расчёт учитывает ${subjectGrades.size} оценок по дисциплине.", color = AnalyticsMuted, fontSize = 11.sp)
                    }
                    else -> {
                        Text("Цель недостижима одной следующей оценкой", color = Color(0xFFC3533D), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Даже оценка 5 не позволит получить заданный средний балл за одну работу.", color = AnalyticsMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
