package com.example.studentperformance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentperformance.data.*
import com.example.studentperformance.ui.screens.FigmaAnalyticsScreen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Ink = Color(0xFF17201B)
private val Canvas = Color(0xFFF7F7F3)
private val Muted = Color(0xFF858D86)
private val Lime = Color(0xFFA8ED79)
private val Line = Color(0xFFE2E4DE)
private val Mint = Color(0xFFDFF7CF)
private val Blue = Color(0xFFDCEEFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = StudentRepository(StudentDatabase.getInstance(applicationContext))
        setContent { StudentApp(repository) }
    }
}

@Composable
private fun StudentApp(repository: StudentRepository) {
    var disciplines by remember { mutableStateOf<List<DisciplineEntity>>(emptyList()) }
    var grades by remember { mutableStateOf<List<GradeEntity>>(emptyList()) }
    var tab by remember { mutableStateOf(0) }
    var selected by remember { mutableStateOf<DisciplineEntity?>(null) }
    var showAdd by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(repository) {
        launch { repository.observeDisciplines().collect { disciplines = it } }
        launch { repository.observeGrades().collect { grades = it } }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Ink, onPrimary = Color.White, secondary = Lime,
            background = Canvas, surface = Color.White, onSurface = Ink
        )
    ) {
        Box(Modifier.fillMaxSize().background(Canvas)) {
            Column(Modifier.fillMaxSize()) {
                if (selected != null) {
                    DisciplineDetail(
                        discipline = selected!!,
                        grades = grades.filter { it.disciplineId == selected!!.id },
                        onBack = { selected = null },
                        onAdd = { showAdd = true }
                    )
                } else {
                    when (tab) {
                        0 -> HomeScreen(disciplines, grades, onOpen = { selected = it })
                        1 -> GradesScreen(disciplines, grades, onOpen = { id -> selected = disciplines.find { it.id == id } })
                        else -> FigmaAnalyticsScreen(disciplines, grades)
                    }
                    Spacer(Modifier.weight(1f))
                    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                        NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Главная", fontSize = 10.sp) })
                        NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Book, null) }, label = { Text("Оценки", fontSize = 10.sp) })
                        NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Analytics, null) }, label = { Text("Аналитика", fontSize = 10.sp) })
                        NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Профиль", fontSize = 10.sp) })
                    }
                }
            }
            if (selected == null) {
                FloatingActionButton(
                    onClick = { showAdd = true },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 88.dp, end = 20.dp),
                    containerColor = Ink, contentColor = Lime, shape = RoundedCornerShape(18.dp)
                ) { Icon(Icons.Default.Add, contentDescription = "Добавить оценку") }
            }
        }
        if (showAdd) {
            AddGradeDialog(
                disciplines = disciplines,
                onDismiss = { showAdd = false },
                onSave = { disciplineId, value, date, type ->
                    scope.launch {
                        repository.addGrade(disciplineId, value, date, type)
                        showAdd = false
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    disciplines: List<DisciplineEntity>,
    grades: List<GradeEntity>,
    onOpen: (DisciplineEntity) -> Unit
) {
    val average = grades.takeIf { it.isNotEmpty() }?.map { it.value }?.average()
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).background(Ink, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Text("А", color = Lime, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                }
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Eyebrow("ДОБРОЕ УТРО")
                    Text("Алексей Морозов", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
                }
                Icon(Icons.Default.NotificationsNone, null, tint = Ink, modifier = Modifier.size(25.dp))
            }
        }
        item {
            Column {
                Eyebrow("ВЕСЕННИЙ СЕМЕСТР")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Успеваемость", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Ink, modifier = Modifier.weight(1f))
                    Text(LocalDate.now().year.toString(), color = Muted, fontSize = 12.sp)
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().background(Ink, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomEnd = 28.dp, bottomStart = 8.dp)).padding(22.dp)) {
                Text("Средний балл", color = Color.White.copy(alpha = .65f), fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(average?.let { "%.2f".format(it).replace('.', ',') } ?: "—", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (average == null) "—" else "${((average / 5.0) * 100).toInt()}%", color = Lime, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("РЕЙТИНГ", color = Color.White.copy(alpha = .5f), fontSize = 9.sp)
                    }
                }
                Text("На основе всех внесённых оценок", color = Color.White.copy(alpha = .55f), fontSize = 11.sp)
            }
        }
        item {
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, null, tint = Color(0xFF477B34), modifier = Modifier.background(Mint, RoundedCornerShape(10.dp)).padding(8.dp))
                Column(Modifier.padding(start = 10.dp).weight(1f)) {
                    Text(grades.size.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
                    Text("работ с оценкой", fontSize = 10.sp, color = Muted)
                }
                Box(Modifier.width(1.dp).height(34.dp).background(Line))
                Column(Modifier.padding(start = 16.dp)) {
                    Text(disciplines.size.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
                    Text("дисциплин", fontSize = 10.sp, color = Muted)
                }
            }
        }
        item {
            Column {
                Eyebrow("ТЕКУЩИЕ РЕЗУЛЬТАТЫ")
                Text("Дисциплины", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
            }
        }
        if (disciplines.isEmpty()) item { EmptyPanel("Пока нет дисциплин", "Добавьте дисциплину, чтобы начать вести успеваемость.") }
        items(disciplines, key = { it.id }) { discipline ->
            val subjectGrades = grades.filter { it.disciplineId == discipline.id }
            val avg = subjectGrades.takeIf { it.isNotEmpty() }?.map { it.value }?.average()
            Row(
                Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(20.dp)).clickable { onOpen(discipline) }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(48.dp).background(if (discipline.id % 2L == 0L) Blue else Color(0xFF9BE66E), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Text(discipline.name.take(2).uppercase(), color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(discipline.name, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(discipline.teacherName ?: "Преподаватель не указан", color = Muted, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 6.dp)) {
                        subjectGrades.takeLast(4).forEach { GradeChip(it.value) }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(avg?.let { "%.1f".format(it).replace('.', ',') } ?: "—", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    Text("СРЕДНИЙ", color = Muted, fontSize = 8.sp)
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Eyebrow("ВАША ДИНАМИКА")
                Text("Прогресс", fontSize = 25.sp, color = Ink, fontWeight = FontWeight.ExtraBold)
                Text("Добавляйте оценки, чтобы отслеживать изменения среднего балла.", color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun GradesScreen(disciplines: List<DisciplineEntity>, grades: List<GradeEntity>, onOpen: (Long) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Eyebrow("ЖУРНАЛ"); Text("Все оценки", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Ink) }
        if (grades.isEmpty()) item { EmptyPanel("Оценок пока нет", "Добавьте первую оценку с помощью кнопки +.") }
        items(grades.sortedByDescending { it.date }, key = { it.id }) { grade ->
            val discipline = disciplines.find { it.id == grade.disciplineId }
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).clickable { onOpen(grade.disciplineId) }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                GradeChip(grade.value, large = true)
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(grade.workType, color = Ink, fontWeight = FontWeight.Bold)
                    Text("${discipline?.name ?: "Дисциплина"} · ${grade.date}", color = Muted, fontSize = 11.sp)
                    grade.comment?.let { Text(it, color = Muted, fontSize = 11.sp) }
                }
            }
        }
    }
}

@Composable
private fun DisciplineDetail(discipline: DisciplineEntity, grades: List<GradeEntity>, onBack: () -> Unit, onAdd: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("‹  Назад", color = Ink, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.height(18.dp))
            Eyebrow("ДИСЦИПЛИНА")
            Text(discipline.name, fontSize = 29.sp, color = Ink, fontWeight = FontWeight.ExtraBold)
            Text(discipline.teacherName ?: "Преподаватель не указан", color = Muted, fontSize = 13.sp)
        }
        item {
            val avg = grades.takeIf { it.isNotEmpty() }?.map { it.value }?.average()
            Row(Modifier.fillMaxWidth().background(Ink, RoundedCornerShape(22.dp)).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("Средний балл", color = Color.White.copy(alpha = .6f)); Text(avg?.let { "%.2f".format(it) } ?: "—", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold) }
                Text("${grades.size} оценок", color = Lime)
            }
        }
        item { Row(verticalAlignment = Alignment.CenterVertically) { Text("История оценок", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink, modifier = Modifier.weight(1f)); Text("+ Добавить", color = Ink, modifier = Modifier.clickable { onAdd() }) } }
        items(grades.sortedByDescending { it.date }, key = { it.id }) { grade ->
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                GradeChip(grade.value, true)
                Column(Modifier.padding(start = 12.dp)) { Text(grade.workType, color = Ink, fontWeight = FontWeight.Bold); Text(grade.date, color = Muted, fontSize = 11.sp); grade.comment?.let { Text(it, color = Muted, fontSize = 11.sp) } }
            }
        }
        if (grades.isEmpty()) item { EmptyPanel("Оценок пока нет", "Добавьте первую оценку для этой дисциплины.") }
    }
}

@Composable
private fun AnalyticsScreen(disciplines: List<DisciplineEntity>, grades: List<GradeEntity>) {
    val average = grades.takeIf { it.isNotEmpty() }?.map { it.value }?.average()
    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Eyebrow("ВАША ДИНАМИКА"); Text("Прогресс", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Ink) }
        item {
            Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp)).padding(20.dp)) {
                Text("Текущий средний балл", color = Muted, fontSize = 12.sp)
                Text(average?.let { "%.2f".format(it).replace('.', ',') } ?: "—", color = Ink, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
                Text("Количество оценок: ${grades.size}", color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    grades.takeLast(12).forEach { grade ->
                        Box(Modifier.weight(1f).fillMaxHeight(grade.value / 5f).background(Lime, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)))
                    }
                }
            }
        }
        item { Eyebrow("ПО ДИСЦИПЛИНАМ"); Text("Сравнение результатов", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink) }
        items(disciplines, key = { it.id }) { discipline ->
            val values = grades.filter { it.disciplineId == discipline.id }.map { it.value }
            val avg = values.takeIf { it.isNotEmpty() }?.average()
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(discipline.name, color = Ink, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Text(avg?.let { "%.2f".format(it).replace('.', ',') } ?: "—", color = Ink, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AddGradeDialog(
    disciplines: List<DisciplineEntity>,
    onDismiss: () -> Unit,
    onSave: (Long, Int, String, String) -> Unit
) {
    var disciplineId by remember { mutableStateOf(disciplines.firstOrNull()?.id ?: 0L) }
    var value by remember { mutableIntStateOf(5) }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var workType by remember { mutableStateOf("Контрольная") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая оценка", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (disciplines.isEmpty()) Text("Сначала добавьте дисциплину.", color = Muted)
                else {
                    Text("Дисциплина", color = Muted, fontSize = 12.sp)
                    disciplines.forEach { d ->
                        Row(Modifier.fillMaxWidth().clickable { disciplineId = d.id }, verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = disciplineId == d.id, onClick = { disciplineId = d.id })
                            Text(d.name, color = Ink)
                        }
                    }
                    Text("Оценка", color = Muted, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (2..5).forEach { n ->
                            FilterChip(selected = value == n, onClick = { value = n }, label = { Text(n.toString()) })
                        }
                    }
                    Text("Тип работы", color = Muted, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Контрольная", "Практика", "Экзамен").forEach { type ->
                            FilterChip(selected = workType == type, onClick = { workType = type }, label = { Text(type, fontSize = 10.sp) })
                        }
                    }
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Дата (ГГГГ-ММ-ДД)") }, singleLine = true)
                }
            }
        },
        confirmButton = { TextButton(onClick = { if (disciplines.isNotEmpty()) onSave(disciplineId, value, date, workType) }, enabled = disciplines.isNotEmpty()) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun Eyebrow(value: String) {
    Text(value, color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
}

@Composable
private fun GradeChip(value: Int, large: Boolean = false) {
    Box(
        Modifier.size(if (large) 46.dp else 22.dp).background(
            when (value) { 5 -> Mint; 4 -> Color(0xFFFFF0BD); else -> Color(0xFFF8D9D2) },
            RoundedCornerShape(if (large) 14.dp else 6.dp)
        ),
        contentAlignment = Alignment.Center
    ) { Text(value.toString(), color = Ink, fontSize = if (large) 19.sp else 10.sp, fontWeight = FontWeight.Bold) }
}

@Composable
private fun EmptyPanel(title: String, description: String) {
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(20.dp)).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Ink, fontWeight = FontWeight.Bold)
        Text(description, color = Muted, fontSize = 12.sp)
    }
}
