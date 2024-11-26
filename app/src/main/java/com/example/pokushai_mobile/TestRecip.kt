package com.example.pokushai_mobile

import android.content.res.ColorStateList
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager



class TestRecip : AppCompatActivity() {

    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recip)

        // Заглавное фото
        val mainPhoto = findViewById<ImageView>(R.id.mainPhoto)
        mainPhoto.setImageResource(R.drawable.nophotostep)

        // Заголовок
        val title = findViewById<TextView>(R.id.title)
        title.text = "Название блюда"

        // Энергитическая ценность
        val calories = 100
        val proteins = 1
        val fats = 1
        val carbohydrates = 1

        val textViewCalories = findViewById<TextView>(R.id.textViewCalories)
        textViewCalories.text = "$calories\nкКал"

        val textViewSquirrels = findViewById<TextView>(R.id.textViewSquirrels)
        textViewSquirrels.text = "$proteins г"

        val textViewFats = findViewById<TextView>(R.id.textViewFats)
        textViewFats.text = "$fats г"

        val textViewCarbohydrates = findViewById<TextView>(R.id.textViewCarbohydrates)
        textViewCarbohydrates.text = "$carbohydrates г"


        // Ищем RecyclerView
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        stepsRecyclerView = findViewById(R.id.stepsRecyclerView)

        // Количество ингридиентов
        val portions = 1
        val point0 = 1f
        val point1 = 1f
        val point2 = 1f
        var portionsAdditionally = portions

        // Счетчик порций
        val textPortions = findViewById<TextView>(R.id.textPortions)

        // Изначально показываем порции
        textPortions.text = "$portionsAdditionally"

        // Увеличение порций
        val buttonIncrease = findViewById<Button>(R.id.buttonIncrease)
        buttonIncrease.setOnClickListener {
            portionsAdditionally++
            val pointValues = listOf(point0, point1, point2)
            val updatedPoints = pointValues.map { (it * portionsAdditionally).toInt() }

            val ingredients = listOf(
                "Ингредиент 1" to "${updatedPoints[0]} ед.изм",
                "Ингредиент 2" to "${updatedPoints[1]} ед.изм",
                "Ингредиент 3" to "${updatedPoints[2]} ед.изм"
            )

            textPortions.text = "$portionsAdditionally"
            ingredientsAdapter.updateIngredients(ingredients)
        }

        // Уменьшение порций
        val buttonDecrease = findViewById<Button>(R.id.buttonDecrease)
        buttonDecrease.setOnClickListener {
            if (portionsAdditionally > 1) {
                portionsAdditionally--
            }

            val pointValues = listOf(point0, point1, point2)
            val updatedPoints = pointValues.map { (it * portionsAdditionally).toInt() }

            val ingredients = listOf(
                "Ингредиент 1" to "${updatedPoints[0]} ед.изм",
                "Ингредиент 2" to "${updatedPoints[1]} ед.изм",
                "Ингредиент 3" to "${updatedPoints[2]} ед.изм"
            )

            textPortions.text = "$portionsAdditionally"
            ingredientsAdapter.updateIngredients(ingredients)
        }

        // Начальные ингредиенты
        val ingredients = listOf(
            "Ингредиент 1" to "${(point0 * portions).toInt()} ед.изм",
            "Ингредиент 2" to "${(point1 * portions).toInt()} ед.изм",
            "Ингредиент 3" to "${(point2 * portions).toInt()} ед.изм"
        )
        ingredientsAdapter = IngredientsAdapter(ingredients)

        // Шаги
        val steps = listOf(
            StepAdapter.Step("Это текст для первого шага...", R.drawable.nophotostep),
            StepAdapter.Step("Это текст для второго шага...", R.drawable.nophotostep),
            StepAdapter.Step("Это текст для третьего шага...", R.drawable.nophotostep)
        )

        val stepAdapter = StepAdapter(steps)

        // Настроим RecyclerView для ингредиентов
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientsRecyclerView.adapter = ingredientsAdapter

        // Настройка RecyclerView для шагов
        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.adapter = stepAdapter


        // Кнопка назад
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        //Тема приложения
        val layout: RecyclerView = findViewById(R.id.stepsRecyclerView)
        val topMenu: LinearLayout = findViewById(R.id.topMenu)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            val color = ContextCompat.getColor(this, R.color.nutritionalValueDark)

            layout.setBackgroundResource(R.drawable.shape_dark)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_dark)

            textViewCalories.setBackgroundResource(R.drawable.dark_radius)
            textViewSquirrels.setBackgroundResource(R.drawable.dark_radius)
            textViewFats.setBackgroundResource(R.drawable.dark_radius)
            textViewCarbohydrates.setBackgroundResource(R.drawable.dark_radius)

            buttonIncrease.backgroundTintList = ColorStateList.valueOf(color)
            buttonDecrease.backgroundTintList = ColorStateList.valueOf(color)
        } else {
            // Светлая тема
            val color = ContextCompat.getColor(this, R.color.nutritionalValueLight)

            layout.setBackgroundResource(R.drawable.shape_light)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_light)

            textViewCalories.setBackgroundResource(R.drawable.light_radius)
            textViewSquirrels.setBackgroundResource(R.drawable.light_radius)
            textViewFats.setBackgroundResource(R.drawable.light_radius)
            textViewCarbohydrates.setBackgroundResource(R.drawable.light_radius)

            buttonIncrease.backgroundTintList = ColorStateList.valueOf(color)
            buttonDecrease.backgroundTintList = ColorStateList.valueOf(color)
        }

    }
}