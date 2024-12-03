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
import com.google.gson.Gson
import java.io.InputStreamReader

class TestRecip : AppCompatActivity() {

    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private var portionsAdditionally = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recip)

        val recipeId = intent.getIntExtra("recipe_id", -1)

        // Загружаем данные рецептов из JSON
        val recipes = loadRecipes()
        val recipe = recipes.find { it.id == recipeId } ?: return

        // Заполняем данные рецепта
        val mainPhoto = findViewById<ImageView>(R.id.mainPhoto)
        val title = findViewById<TextView>(R.id.title)
        val textViewCalories = findViewById<TextView>(R.id.textViewCalories)
        val textViewSquirrels = findViewById<TextView>(R.id.textViewSquirrels)
        val textViewFats = findViewById<TextView>(R.id.textViewFats)
        val textViewCarbohydrates = findViewById<TextView>(R.id.textViewCarbohydrates)
        val textPortions = findViewById<TextView>(R.id.textPortions)

        mainPhoto.setImageResource(resources.getIdentifier(recipe.image, "drawable", packageName))
        title.text = recipe.name
        textViewCalories.text = "${recipe.calories}\nкКал"
        textViewSquirrels.text = "${recipe.proteins} г"
        textViewFats.text = "${recipe.fats} г"
        textViewCarbohydrates.text = "${recipe.carbohydrates} г"
        textPortions.text = "$portionsAdditionally"

        // Настраиваем RecyclerView для ингредиентов
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        ingredientsAdapter = IngredientsAdapter(recipe.ingredients.map { it.name to it.amount })
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientsRecyclerView.adapter = ingredientsAdapter

        // Настраиваем RecyclerView для шагов
        stepsRecyclerView = findViewById(R.id.stepsRecyclerView)
        val stepAdapter = StepAdapter(recipe.steps.map { StepAdapter.Step(it.description, resources.getIdentifier(it.image, "drawable", packageName)) })
        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.adapter = stepAdapter

        // Кнопки изменения порций
        val buttonIncrease = findViewById<Button>(R.id.buttonIncrease)
        val buttonDecrease = findViewById<Button>(R.id.buttonDecrease)

        buttonIncrease.setOnClickListener {
            portionsAdditionally++
            updateIngredients(recipe)
            textPortions.text = "$portionsAdditionally"
        }

        buttonDecrease.setOnClickListener {
            if (portionsAdditionally > 1) {
                portionsAdditionally--
                updateIngredients(recipe)
                textPortions.text = "$portionsAdditionally"
            }
        }

        // Кнопка "Назад"
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Настройка темы
        setupTheme()
    }

    private fun loadRecipes(): List<Recipe> {
        val inputStream = assets.open("recipes.json")
        val reader = InputStreamReader(inputStream)
        val recipeResponse = Gson().fromJson(reader, RecipeResponse::class.java)
        return recipeResponse.recipes
    }


    private fun updateIngredients(recipe: Recipe) {
        val updatedIngredients = recipe.ingredients.map {
            it.name to "${(it.amount.toInt() * portionsAdditionally)} г"
        }
        ingredientsAdapter.updateIngredients(updatedIngredients)
    }

    private fun setupTheme() {
        val layout: RecyclerView = findViewById(R.id.stepsRecyclerView)
        val topMenu: LinearLayout = findViewById(R.id.topMenu)
        val textViewCalories = findViewById<TextView>(R.id.textViewCalories)
        val textViewSquirrels = findViewById<TextView>(R.id.textViewSquirrels)
        val textViewFats = findViewById<TextView>(R.id.textViewFats)
        val textViewCarbohydrates = findViewById<TextView>(R.id.textViewCarbohydrates)
        val buttonIncrease = findViewById<Button>(R.id.buttonIncrease)
        val buttonDecrease = findViewById<Button>(R.id.buttonDecrease)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
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
