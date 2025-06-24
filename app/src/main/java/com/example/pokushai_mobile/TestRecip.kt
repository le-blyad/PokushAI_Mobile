package com.example.pokushai_mobile

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.InputStreamReader


class TestRecip : AppCompatActivity() {

    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var recipe: Recipe
    private var portionsAdditionally = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recip)

        val recipeId = intent.getIntExtra("recipe_id", -1)

        // Загружаем данные рецептов из JSON
        val recipes = loadRecipes()
        recipe = recipes.find { it.id == recipeId } ?: return

        // Устанавливаем начальное количество порций
        portionsAdditionally = recipe.portion

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
        ingredientsAdapter = IngredientsAdapter(recipe.ingredients.map { ingredient ->
            val initialValue = if (ingredient.weight != null) {
                "${ingredient.weight} ${ingredient.amount}"
            } else {
                ingredient.amount
            }
            ingredient.name to initialValue
        })
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientsRecyclerView.adapter = ingredientsAdapter

        // Настраиваем RecyclerView для шагов
        stepsRecyclerView = findViewById(R.id.stepsRecyclerView)
        val stepAdapter = StepAdapter(recipe.steps.map { step ->
            StepAdapter.Step(step.description, resources.getIdentifier(step.image, "drawable", packageName))
        })
        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.adapter = stepAdapter

        // Кнопки изменения порций
        val buttonIncrease = findViewById<Button>(R.id.buttonIncrease)
        val buttonDecrease = findViewById<Button>(R.id.buttonDecrease)

        buttonIncrease.setOnClickListener {
            portionsAdditionally++
            updateIngredients()
            textPortions.text = "$portionsAdditionally"
        }

        buttonDecrease.setOnClickListener {
            if (portionsAdditionally > 1) {
                portionsAdditionally--
                updateIngredients()
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

    private fun updateIngredients() {
        val updatedIngredients = recipe.ingredients.map { ingredient ->
            // Вычисляем коэффициент относительно базового количества порций
            val coefficient = portionsAdditionally.toDouble() / recipe.portion

            // Обрабатываем разные типы ингредиентов
            val displayValue = when {
                // Для ингредиентов с числовым весом
                ingredient.weight?.toDoubleOrNull() != null -> {
                    val scaledValue = ingredient.weight.toDouble() * coefficient
                    "${scaledValue.toInt()} ${ingredient.amount}"
                }
                // Для ингредиентов "по вкусу"
                else -> ingredient.amount
            }
            ingredient.name to displayValue
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

    // Адаптер для ингредиентов
    inner class IngredientsAdapter(
        private var items: List<Pair<String, String>>
    ) : RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
            val ingredientQuantity: TextView = itemView.findViewById(R.id.ingredientQuantity) // Исправлено на ingredientQuantity
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ingredient, parent, false) // Убедитесь что имя файла совпадает
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (name, quantity) = items[position]
            holder.ingredientName.text = name
            holder.ingredientQuantity.text = quantity
        }

        override fun getItemCount(): Int = items.size

        fun updateIngredients(newItems: List<Pair<String, String>>) {
            items = newItems
            notifyDataSetChanged()
        }
    }

    // Модели данных
    data class RecipeResponse(
        val recipes: List<Recipe>
    )

    data class Recipe(
        val id: Int,
        val name: String,
        val image: String,
        val calories: Int,
        val proteins: Int,
        val fats: Int,
        val carbohydrates: Int,
        val portion: Int, // базовое количество порций
        val ingredients: List<Ingredient>,
        val steps: List<Step>
    )

    data class Ingredient(
        val name: String,
        val weight: String?, // может быть null (для "по вкусу")
        val amount: String
    )

    data class Step(
        val step_number: Int,
        val image: String,
        val description: String
    )
}