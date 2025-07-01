package com.example.pokushai_mobile

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var interpreter: Interpreter
    private val switches = mutableListOf<Switch>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadModel() // Загружаем модель
        initUIElements(view)
    }

    private fun loadModel() {
        try {
            val modelFile = requireContext().assets.openFd("model.tflite")
            val startOffset = modelFile.startOffset
            val length = modelFile.length
            val fileChannel = FileInputStream(modelFile.fileDescriptor).channel
            val mappedByteBuffer: MappedByteBuffer = fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                startOffset,
                length
            )
            interpreter = Interpreter(mappedByteBuffer)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка загрузки модели: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createSwitches(container: LinearLayout) {
        container.removeAllViews()
        switches.clear()

        for (i in 0 until 96) {
            val switch = Switch(requireContext()).apply {
                id = View.generateViewId()
                val resourceName = "ingredient$i"
                val resourceId = resources.getIdentifier(resourceName, "string", requireContext().packageName)

                text = if (resourceId != 0) getString(resourceId) else "Ингредиент $i"
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

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun initUIElements(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnClickListener { searchView.isIconified = false }

        val container = view.findViewById<LinearLayout>(R.id.switches_container)
        createSwitches(container)

        view.findViewById<Button>(R.id.deleteCheck)?.setOnClickListener {
            switches.forEach { it.isChecked = false }
        }

        view.findViewById<Button>(R.id.searchRecipes)?.setOnClickListener {
            // Проверяем количество выбранных ингредиентов
            val selectedCount = switches.count { it.isChecked }

            if (selectedCount < 5) {
                Toast.makeText(
                    requireContext(),
                    "Пожалуйста, выберите минимум 5 ингредиентов",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Подготовка данных
            val selectedIngredients = switches.map { it.isChecked }.toBooleanArray()
            val inputArray = Array(1) { FloatArray(96) }
            switches.forEachIndexed { index, switch ->
                inputArray[0][index] = if (switch.isChecked) 1.0f else 0.0f
            }

            val outputArray = Array(1) { FloatArray(50) }
            interpreter.run(inputArray, outputArray)
            val result = outputArray[0]

            // Создание фрагмента с результатами
            val resultFragment = ResultRecipesFragment().apply {
                arguments = Bundle().apply {
                    putBooleanArray("switchValues", selectedIngredients)
                    putFloatArray("result", result)
                }
            }

            // Навигация между фрагментами
            FragmentNavigator.navigateForward(
                parentFragmentManager,
                R.id.fragment_container,
                resultFragment
            )

            FragmentNavigator.navigateForward(
                parentFragmentManager,
                R.id.fragment_container,
                ResultRecipesFragment().apply {
                    arguments = Bundle().apply {
                        putBooleanArray("switchValues", selectedIngredients)
                        putFloatArray("result", result)
                    }
                }
            )
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

    override fun onDestroy() {
        super.onDestroy()
        if (::interpreter.isInitialized) {
            interpreter.close()
        }
    }
}