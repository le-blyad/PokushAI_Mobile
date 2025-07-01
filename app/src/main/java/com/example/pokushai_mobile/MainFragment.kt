package com.example.pokushai_mobile

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class MainFragment : Fragment(R.layout.fragment_main) {

    private val switches = mutableListOf<Switch>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUIElements(view)
    }

    private fun createSwitches(container: LinearLayout) {
        container.removeAllViews()
        switches.clear()

        for (i in 0 until 96) {
            val switch = Switch(requireContext()).apply {
                id = View.generateViewId()

                // Получаем строку из ресурсов по имени
                val resourceName = "ingredient$i"
                val resourceId = resources.getIdentifier(resourceName, "string", requireContext().packageName)

                if (resourceId != 0) {
                    text = getString(resourceId)
                } else {
                    text = "Ингредиент $i" // Запасной вариант
                }

                // Устанавливаем стиль через код
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                setSwitchTextAppearance(requireContext(), R.style.MyCustomSwitch)

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(100, 0.dpToPx(), 100, 0.dpToPx())
                    setPadding(0, 15.dpToPx(), 0, 15.dpToPx())
                }
            }

            container.addView(switch)
            switches.add(switch)
        }
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun initUIElements(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnClickListener { searchView.isIconified = false }

        val container = view.findViewById<LinearLayout>(R.id.switches_container)

        // Создаем свитчи вместо сбора существующих
        createSwitches(container)

        view.findViewById<Button>(R.id.deleteCheck)?.setOnClickListener {
            switches.forEach { switch -> switch.isChecked = false }
        }

        view.findViewById<Button>(R.id.searchRecipes)?.setOnClickListener {
            // Формируем список выбранных ингредиентов
            val selectedIngredients = switches
                .filter { it.isChecked }
                .joinToString { it.text.toString() }

            Toast.makeText(
                requireContext(),
                "Выбрано: $selectedIngredients",
                Toast.LENGTH_SHORT
            ).show()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterSwitches(newText)
                return true
            }
        })
    }

    private fun filterSwitches(query: String?) {
        val queryText = query?.lowercase() ?: ""
        switches.forEach { switch ->
            switch.visibility = if (switch.text.toString().lowercase().contains(queryText)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}