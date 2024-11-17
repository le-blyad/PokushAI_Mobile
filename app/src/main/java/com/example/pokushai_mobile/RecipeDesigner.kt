package com.example.pokushai_mobile

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast



class RecipeDesigner: AppCompatActivity() {

    private lateinit var stepsAdapter: StepsAdapter
    private val steps = mutableListOf<Step>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_designer)


        val layout: LinearLayout = findViewById(R.id.stepsBackground)

        // Проверяем текущую тему приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            layout.setBackgroundResource(R.drawable.shape_dark)
        } else {
            // Светлая тема
            layout.setBackgroundResource(R.drawable.shape_light)
        }

        // Настройка RecyclerView
        stepsAdapter = StepsAdapter(steps) { position ->
            stepsAdapter.removeStep(position)
        }
        val stepsRecyclerView: RecyclerView = findViewById(R.id.stepsRecyclerView)
        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.adapter = stepsAdapter

        // Добавление первого шага
        addStep()

        // Кнопка добавления нового шага
        findViewById<Button>(R.id.addStepButton).setOnClickListener {
            addStep()
        }

        // Кнопка сохранения
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveSteps()
        }
    }

    private fun addStep() {
        val newStep = Step(number = steps.size + 1)
        stepsAdapter.addStep(newStep)
    }

    private fun saveSteps() {
        // Логика для сохранения шагов
        Toast.makeText(this, "Шаги сохранены", Toast.LENGTH_SHORT).show()
    }
}
