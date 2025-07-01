package com.example.pokushai_mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ResultRecipesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageButton>(R.id.buttonBack)
        setupBackButton(backButton)

        // Получаем данные из аргументов
        val result = arguments?.getFloatArray("result") ?: run {
            Toast.makeText(requireContext(), "Данные рецептов не загружены", Toast.LENGTH_SHORT).show()
            return
        }

        // Получаем индексы топ-3 рецептов
        val top3Indices = getTopNIndices(result, 3)

        // Собираем все элементы интерфейса
        val recipeViews = (0 until 50).map { i ->
            Triple(
                view.findViewById<ImageButton>(resources.getIdentifier("imageButton$i", "id", requireContext().packageName)),
                view.findViewById<TextView>(resources.getIdentifier("textView$i", "id", requireContext().packageName)),
                view.findViewById<View>(resources.getIdentifier("recipeContainer$i", "id", requireContext().packageName))
            )
        }

        // Сначала скрываем все элементы
        recipeViews.forEach { (imageButton, textView, container) ->
            imageButton?.visibility = View.GONE
            textView?.visibility = View.GONE
            container?.visibility = View.GONE
        }

        // Показываем топ-3 рецепта
        if (top3Indices.isNotEmpty()) {
            top3Indices.forEachIndexed { position, index ->
                val (imageButton, textView, container) = recipeViews[index]

                // Показываем контейнер рецепта (или отдельные элементы)
                container?.visibility = View.VISIBLE
                imageButton?.visibility = View.VISIBLE
                textView?.visibility = View.VISIBLE

                textView?.text = "${textView?.text}"
            }
        }
    }

    private fun getTopNIndices(array: FloatArray, n: Int): List<Int> {
        // Создаем список пар (индекс, значение) и сортируем по убыванию значений
        return array.withIndex()
            .sortedByDescending { it.value }
            .take(n)
            .map { it.index }
    }

    private fun setupBackButton(backButton: ImageButton) {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}