package com.example.pokushai_mobile

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager



class TestRecip : AppCompatActivity() {

    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var stepAdapter: StepAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recip)


        //Тема приложения
        val layout: RecyclerView = findViewById(R.id.stepsRecyclerView)
        val topMenu: LinearLayout = findViewById(R.id.topMenu)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            layout.setBackgroundResource(R.drawable.shape_dark)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_dark)
        } else {
            // Светлая тема
            layout.setBackgroundResource(R.drawable.shape_light)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_light)
        }

        // Ищем RecyclerView
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        stepsRecyclerView = findViewById(R.id.stepsRecyclerView)

        // Энергитическая ценность
        val textViewCalories = findViewById<TextView>(R.id.textViewCalories)
        textViewCalories.text = "Калории\n305\nККАЛ"

        val textViewSquirrels = findViewById<TextView>(R.id.textViewSquirrels)
        textViewSquirrels.text = "Белки\n25\nграмм"

        val textViewFats = findViewById<TextView>(R.id.textViewFats)
        textViewFats.text = "Жиры\n20\nграмм"

        val textViewCarbohydrates = findViewById<TextView>(R.id.textViewCarbohydrates)
        textViewCarbohydrates.text = "Углеводы\n3\nграмм"

        // Количество ингридиентов
        val portions = 5
        val point0 = 50f
        val point1 = 50f
        val point2 = 0.2f
        var portionsAdditionally = portions

        // Счетчик порций
        val textPortions = findViewById<TextView>(R.id.textPortions)
        val buttonIncrease = findViewById<Button>(R.id.buttonIncrease)
        val buttonDecrease = findViewById<Button>(R.id.buttonDecrease)

        // Изначально показываем порции
        textPortions.text = "$portionsAdditionally"

        // Увеличение порций
        buttonIncrease.setOnClickListener {
            portionsAdditionally++
            val pointValues = listOf(point0, point1, point2)
            val updatedPoints = pointValues.map { it * portionsAdditionally }

            val ingredients = listOf(
                "Вода - ${updatedPoints[0]} мл",
                "Мука - ${updatedPoints[1]} г",
                "Соль - ${updatedPoints[2]} г"
            )

            textPortions.text = "$portionsAdditionally"

            ingredientsAdapter.updateIngredients(ingredients)
        }

        // Уменьшения порций
        buttonDecrease.setOnClickListener {

            if (portionsAdditionally > 1) {
                portionsAdditionally--
            } else {
                portionsAdditionally = 1
            }

            val pointValues = listOf(point0, point1, point2)
            val updatedPoints = pointValues.map { it * portionsAdditionally }

            val ingredients = listOf(
                "Вода - ${updatedPoints[0]} мл",
                "Мука - ${updatedPoints[1]} г",
                "Соль - ${updatedPoints[2]} г"
            )

            textPortions.text = "$portionsAdditionally"

            ingredientsAdapter.updateIngredients(ingredients)
        }

        // Данные для ингредиентов
        val ingredients = listOf(
            "Вода - ${point0 * portions}",
            "Мука - ${point1 * portions}",
            "Соль - ${point2 * portions}"
        )
        ingredientsAdapter = IngredientsAdapter(ingredients)

        // Шаги
        val steps = listOf(
            "Шаг 1: В ведерко хлебопечки налить теплую воду...",
            "Шаг 2: Добавить муку и растительное масло...",
            "Шаг 3: Запустить хлебопечку на режим 'подъем теста'..."
        )
        stepAdapter = StepAdapter(steps)

        // Настроим RecyclerView для ингредиентов
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientsRecyclerView.adapter = ingredientsAdapter

        // Настроим RecyclerView для шагов рецепта
        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.adapter = stepAdapter

        // Кнопка назад
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

    }
}