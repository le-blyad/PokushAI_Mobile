package com.example.pokushai_mobile

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.InputStreamReader

class RecipePrescriptionFragment : Fragment() {

    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var stepAdapter: StepAdapter
    private lateinit var recipe: Recipe
    private var portionsAdditionally = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_prescription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageButton>(R.id.buttonBack)
        backButton.setOnClickListener {
            FragmentNavigator.navigateBack(parentFragmentManager)
        }

        FragmentNavigator.setupBackPressHandler(this)

        val recipeId = arguments?.getInt("recipe_id", -1) ?: -1
        val recipes = loadRecipes()
        recipe = recipes.find { it.id == recipeId } ?: return
        portionsAdditionally = recipe.portion

        val mainPhoto = view.findViewById<ImageView>(R.id.mainPhoto)
        val title = view.findViewById<TextView>(R.id.title)
        val textViewCalories = view.findViewById<TextView>(R.id.textViewCalories)
        val textViewSquirrels = view.findViewById<TextView>(R.id.textViewSquirrels)
        val textViewFats = view.findViewById<TextView>(R.id.textViewFats)
        val textViewCarbohydrates = view.findViewById<TextView>(R.id.textViewCarbohydrates)
        val textPortions = view.findViewById<TextView>(R.id.textPortions)

        mainPhoto.setImageResource(resources.getIdentifier(recipe.image, "drawable", requireContext().packageName))
        title.text = recipe.name
        textViewCalories.text = "${recipe.calories}\nкКал"
        textViewSquirrels.text = "${recipe.proteins} г"
        textViewFats.text = "${recipe.fats} г"
        textViewCarbohydrates.text = "${recipe.carbohydrates} г"
        textPortions.text = "$portionsAdditionally"

        // Инициализация адаптера ингредиентов
        ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView)
        ingredientsAdapter = IngredientsAdapter().apply {
            submitList(recipe.ingredients.map { ingredient ->
                val initialValue = if (ingredient.weight != null) {
                    "${ingredient.weight} ${ingredient.amount}"
                } else {
                    ingredient.amount
                }
                ingredient.name to initialValue
            })
        }

        ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientsAdapter
            isNestedScrollingEnabled = false
        }

        // Инициализация адаптера шагов
        stepsRecyclerView = view.findViewById(R.id.stepsRecyclerView)
        stepAdapter = StepAdapter().apply {
            submitList(recipe.steps.mapIndexed { index, step ->
                StepAdapter.Step(
                    "Шаг ${index + 1}",
                    step.description,
                    resources.getIdentifier(step.image, "drawable", requireContext().packageName)
                )
            })
        }

        stepsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stepAdapter
            isNestedScrollingEnabled = false
        }

        val buttonIncrease = view.findViewById<Button>(R.id.buttonIncrease)
        val buttonDecrease = view.findViewById<Button>(R.id.buttonDecrease)

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

        setupTheme(view)
    }

    private fun loadRecipes(): List<Recipe> {
        val inputStream = requireContext().assets.open("recipes.json")
        val reader = InputStreamReader(inputStream)
        val recipeResponse = Gson().fromJson(reader, RecipeResponse::class.java)
        return recipeResponse.recipes
    }

    private fun updateIngredients() {
        val updatedIngredients = recipe.ingredients.map { ingredient ->
            val coefficient = portionsAdditionally.toDouble() / recipe.portion

            val displayValue = when {
                ingredient.weight?.toDoubleOrNull() != null -> {
                    val scaledValue = ingredient.weight.toDouble() * coefficient
                    "${scaledValue.toInt()} ${ingredient.amount}"
                }
                else -> ingredient.amount
            }
            ingredient.name to displayValue
        }
        ingredientsAdapter.submitList(updatedIngredients) // Используем правильный метод обновления
    }

    private fun setupTheme(view: View) {
        val layout: RecyclerView = view.findViewById(R.id.stepsRecyclerView)
        val topMenu: LinearLayout = view.findViewById(R.id.topMenu)
        val textViewCalories = view.findViewById<TextView>(R.id.textViewCalories)
        val textViewSquirrels = view.findViewById<TextView>(R.id.textViewSquirrels)
        val textViewFats = view.findViewById<TextView>(R.id.textViewFats)
        val textViewCarbohydrates = view.findViewById<TextView>(R.id.textViewCarbohydrates)
        val buttonIncrease = view.findViewById<Button>(R.id.buttonIncrease)
        val buttonDecrease = view.findViewById<Button>(R.id.buttonDecrease)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            val color = ContextCompat.getColor(requireContext(), R.color.nutritionalValueDark)
            layout.setBackgroundResource(R.drawable.shape_dark)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_dark)
            textViewCalories.setBackgroundResource(R.drawable.dark_radius)
            textViewSquirrels.setBackgroundResource(R.drawable.dark_radius)
            textViewFats.setBackgroundResource(R.drawable.dark_radius)
            textViewCarbohydrates.setBackgroundResource(R.drawable.dark_radius)
            buttonIncrease.backgroundTintList = ColorStateList.valueOf(color)
            buttonDecrease.backgroundTintList = ColorStateList.valueOf(color)
        } else {
            val color = ContextCompat.getColor(requireContext(), R.color.nutritionalValueLight)
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

    // Адаптер для шагов
    class StepAdapter : RecyclerView.Adapter<StepAdapter.ViewHolder>() {

        private var steps = listOf<Step>()

        data class Step(
            val stepNumber: String,
            val text: String,
            val imageResId: Int
        )

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val stepText: TextView = itemView.findViewById(R.id.stepText)
            val stepImage: ImageView = itemView.findViewById(R.id.stepImage)
            val numberStep: TextView = itemView.findViewById(R.id.numberStep)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_step, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val step = steps[position]
            holder.stepText.text = step.text
            holder.stepImage.setImageResource(step.imageResId)
            holder.numberStep.text = step.stepNumber
        }

        override fun getItemCount(): Int = steps.size

        fun submitList(newSteps: List<Step>) {
            steps = newSteps
            notifyDataSetChanged()
        }
    }

    // Адаптер для ингредиентов
    class IngredientsAdapter : RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

        private var ingredients = listOf<Pair<String, String>>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
            val ingredientQuantity: TextView = itemView.findViewById(R.id.ingredientQuantity)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ingredient, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (name, quantity) = ingredients[position]
            holder.ingredientName.text = name
            holder.ingredientQuantity.text = quantity
        }

        override fun getItemCount(): Int = ingredients.size

        fun submitList(newIngredients: List<Pair<String, String>>) {
            ingredients = newIngredients
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
        val portion: Int,
        val ingredients: List<Ingredient>,
        val steps: List<Step>
    )

    data class Ingredient(
        val name: String,
        val weight: String?,
        val amount: String
    )

    data class Step(
        val step_number: Int,
        val image: String,
        val description: String
    )
}