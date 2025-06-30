package com.example.pokushai_mobile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

class MainFragment : Fragment(R.layout.fragment_main) {

    private val switches = mutableListOf<Switch>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация UI элементов
        initUIElements(view)
    }

    private fun initUIElements(view: View) {
        // Инициализация SearchView
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnClickListener { searchView.isIconified = false }

        // Находим контейнер со Switch элементами
        val container = view.findViewById<LinearLayout>(R.id.switches_container)

        // Собираем все Switch в список
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child is Switch) {
                switches.add(child)
            }
        }

        // Кнопка очистки
        view.findViewById<Button>(R.id.deleteCheck)?.setOnClickListener {
            for (switch in switches) {
                switch.isChecked = false
            }
        }

        // Кнопка перехода
        view.findViewById<Button>(R.id.buttonNext2)?.setOnClickListener {
            Toast.makeText(requireContext(), "Поиск рецептов (в разработке)", Toast.LENGTH_SHORT).show()
        }

        // Фильтрация при поиске
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterSwitches(newText)
                return true
            }
        })
    }

    private fun filterSwitches(query: String?) {
        switches.forEach { switch ->
            val switchText = switch.text.toString().lowercase()
            if (query.isNullOrEmpty() || switchText.contains(query.lowercase())) {
                switch.visibility = View.VISIBLE
            } else {
                switch.visibility = View.GONE
            }
        }
    }
}