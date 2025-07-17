package com.example.pokushai_mobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class ResultRecipesFragment : Fragment() {

    private lateinit var dishIngredients: Map<Int, List<Int>>
    private val hiddenAllergens by lazy {
        (activity as? MainActivity)?.getHiddenAllergens() ?: emptySet()
    }

    private val allFoodItems = listOf(
        FoodItem(0, R.drawable.belya, R.string.recip0),
        FoodItem(1, R.drawable.bll, R.string.recip1),
        FoodItem(2, R.drawable.blinmyas, R.string.recip2),
        FoodItem(3, R.drawable.bor, R.string.recip3),
        FoodItem(4, R.drawable.grbt, R.string.recip4),
        FoodItem(5, R.drawable.grech, R.string.recip5),
        FoodItem(6, R.drawable.zkart, R.string.recip6),
        FoodItem(7, R.drawable.zapek, R.string.recip7),
        FoodItem(8, R.drawable.pure, R.string.recip8),
        FoodItem(9, R.drawable.krfc, R.string.recip9),
        FoodItem(10, R.drawable.kotl, R.string.recip10),
        FoodItem(11, R.drawable.supkur, R.string.recip11),
        FoodItem(12, R.drawable.lagm, R.string.recip12),
        FoodItem(13, R.drawable.mak, R.string.recip13),
        FoodItem(14, R.drawable.manka, R.string.recip14),
        FoodItem(15, R.drawable.manni, R.string.recip15),
        FoodItem(16, R.drawable.medov, R.string.recip16),
        FoodItem(17, R.drawable.moroz, R.string.recip17),
        FoodItem(18, R.drawable.myus, R.string.recip18),
        FoodItem(19, R.drawable.myaspi, R.string.recip19),
        FoodItem(20, R.drawable.nagg, R.string.recip20),
        FoodItem(21, R.drawable.nap, R.string.recip21),
        FoodItem(22, R.drawable.ovro, R.string.recip22),
        FoodItem(23, R.drawable.ovsyank, R.string.recip23),
        FoodItem(24, R.drawable.okro, R.string.recip24),
        FoodItem(25, R.drawable.oladii, R.string.recip25),
        FoodItem(26, R.drawable.oml, R.string.recip26),
        FoodItem(27, R.drawable.pb, R.string.recip27),
        FoodItem(28, R.drawable.pk, R.string.recip28),
        FoodItem(29, R.drawable.plm, R.string.recip29),
        FoodItem(30, R.drawable.peche, R.string.recip30),
        FoodItem(31, R.drawable.ponch, R.string.recip31),
        FoodItem(32, R.drawable.rass, R.string.recip32),
        FoodItem(33, R.drawable.salatgr, R.string.recip33),
        FoodItem(34, R.drawable.salatcez, R.string.recip34),
        FoodItem(35, R.drawable.solyank, R.string.recip35),
        FoodItem(36, R.drawable.svt, R.string.recip36),
        FoodItem(37, R.drawable.goroh, R.string.recip37),
        FoodItem(38, R.drawable.subgrech, R.string.recip38),
        FoodItem(39, R.drawable.supgrib, R.string.recip39),
        FoodItem(40, R.drawable.supovosh, R.string.recip40),
        FoodItem(41, R.drawable.supfrik, R.string.recip41),
        FoodItem(42, R.drawable.sirnik, R.string.recip42),
        FoodItem(43, R.drawable.tk, R.string.recip43),
        FoodItem(44, R.drawable.frikk, R.string.recip44),
        FoodItem(45, R.drawable.stf, R.string.recip45),
        FoodItem(46, R.drawable.sharl, R.string.recip46),
        FoodItem(47, R.drawable.shaurm, R.string.recip47),
        FoodItem(48, R.drawable.ekler, R.string.recip48),
        FoodItem(49, R.drawable.glzz, R.string.recip49)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDishIngredients()

        val backButton = view.findViewById<ImageButton>(R.id.buttonBack)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val itemsToShow = if (arguments?.containsKey("result") == true) {
            val result = arguments?.getFloatArray("result") ?: floatArrayOf()
            if (result.isNotEmpty()) {
                val top3Indices = getTopNIndices(result, 3)
                top3Indices.map { index -> allFoodItems[index] }
            } else {
                allFoodItems
            }
        } else {
            allFoodItems
        }

        // Фильтрация рецептов с аллергенами
        val filteredItems = itemsToShow.filter { dish ->
            val ingredients = dishIngredients[dish.id] ?: emptyList()
            !ingredients.any { it in hiddenAllergens }
        }

        Log.d("FilterDebug", "Hidden allergens: $hiddenAllergens")
        Log.d("FilterDebug", "Filtered items count: ${filteredItems.size}/${itemsToShow.size}")

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = FoodAdapter(filteredItems) { recipeId ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecipePrescriptionFragment().apply {
                    arguments = Bundle().apply { putInt("recipe_id", recipeId) }
                })
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getTopNIndices(array: FloatArray, n: Int): List<Int> {
        return array.mapIndexed { index, value -> index to value }
            .sortedByDescending { it.second }
            .take(n)
            .map { it.first }
    }

    private fun loadDishIngredients() {
        try {
            val inputStream = requireContext().assets.open("dishes_ingredients.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<DishIngredients>>() {}.type
            val ingredientsList: List<DishIngredients> = Gson().fromJson(jsonString, type)
            dishIngredients = ingredientsList.associate { it.id to it.ingredients }

            Log.d("IngredientDebug", "Loaded ${ingredientsList.size} dishes")
            ingredientsList.forEach { dish ->
                if (dish.ingredients.any { it in hiddenAllergens }) {
                    Log.d("IngredientDebug", "Dish ${dish.id} contains hidden allergens")
                }
            }
        } catch (e: Exception) {
            Log.e("IngredientError", "Error loading ingredients", e)
            dishIngredients = emptyMap()
        }
    }

    private data class DishIngredients(val id: Int, val ingredients: List<Int>)
}