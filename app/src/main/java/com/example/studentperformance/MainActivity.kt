package com.example.studentperformance

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

/**
 * Минимальная точка входа проекта. Пользовательские экраны будут реализованы
 * в следующих спринтах; текущий спринт посвящён модели и локальному хранению.
 */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message = TextView(this).apply {
            text = "Личный кабинет успеваемости студента\n\nСпринты 1–2: требования и локальная база данных готовы."
            textSize = 18f
            setPadding(32, 48, 32, 32)
        }
        setContentView(message)
    }
}
