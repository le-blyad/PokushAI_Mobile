package com.example.pokushai_mobile

import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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



        val layout: RecyclerView = findViewById(R.id.stepsRecyclerView)
        val topMenu: LinearLayout = findViewById(R.id.topMenu)
        val buttonIncrease = findViewById<Button>(R.id.buttonIncrease)
        val buttonDecrease = findViewById<Button>(R.id.buttonDecrease)

        //Тема приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            val color = ContextCompat.getColor(this, R.color.nutritionalValueDark)

            layout.setBackgroundResource(R.drawable.shape_dark)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_dark)

            buttonIncrease.backgroundTintList = ColorStateList.valueOf(color)
            buttonDecrease.backgroundTintList = ColorStateList.valueOf(color)
        } else {
            // Светлая тема
            val color = ContextCompat.getColor(this, R.color.nutritionalValueLight)

            layout.setBackgroundResource(R.drawable.shape_light)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_light)

            buttonIncrease.backgroundTintList = ColorStateList.valueOf(color)
            buttonDecrease.backgroundTintList = ColorStateList.valueOf(color)
        }

        // Ищем RecyclerView
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        stepsRecyclerView = findViewById(R.id.stepsRecyclerView)

        // Энергитическая ценность
        val textViewCalories = findViewById<TextView>(R.id.textViewCalories)
        textViewCalories.text = "Калории\n305\nкКал"

        val textViewSquirrels = findViewById<TextView>(R.id.textViewSquirrels)
        textViewSquirrels.text = "Белки\n25\nг"

        val textViewFats = findViewById<TextView>(R.id.textViewFats)
        textViewFats.text = "Жиры\n20\nг"

        val textViewCarbohydrates = findViewById<TextView>(R.id.textViewCarbohydrates)
        textViewCarbohydrates.text = "Углеводы\n3\nг"

        // Количество ингридиентов
        val portions = 5
        val point0 = 50f
        val point1 = 50f
        val point2 = 0.2f
        var portionsAdditionally = portions

        // Счетчик порций
        val textPortions = findViewById<TextView>(R.id.textPortions)

        // Изначально показываем порции
        textPortions.text = "$portionsAdditionally"

        // Увеличение порций
        buttonIncrease.setOnClickListener {
            portionsAdditionally++
            val pointValues = listOf(point0, point1, point2)
            val updatedPoints = pointValues.map { it * portionsAdditionally }

            val ingredients = listOf(
                "Вода ${updatedPoints[0]} мл",
                "Мука ${updatedPoints[1]} г",
                "Соль ${updatedPoints[2]} г"
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
                "Вода ${updatedPoints[0]} мл",
                "Мука ${updatedPoints[1]} г",
                "Соль ${updatedPoints[2]} г"
            )

            textPortions.text = "$portionsAdditionally"

            ingredientsAdapter.updateIngredients(ingredients)
        }

        // Данные для ингредиентов
        val ingredients = listOf(
            "Вода ${point0 * portions} мл",
            "Мука ${point1 * portions} г",
            "Соль ${point2 * portions}г "
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