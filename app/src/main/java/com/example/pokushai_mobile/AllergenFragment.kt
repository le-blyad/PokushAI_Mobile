package com.example.pokushai_mobile

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import androidx.fragment.app.Fragment

class AllergenFragment : Fragment() {

    private lateinit var container: LinearLayout
    private val allergenSwitches = mutableListOf<Switch>()
    private var allergenListener: OnAllergensSelectedListener? = null

    interface OnAllergensSelectedListener {
        fun onAllergensSelected(allergenIndices: Set<Int>)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAllergensSelectedListener) {
            allergenListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_allergen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view.findViewById(R.id.allergen_container)
        createAllergenSwitches()

        view.findViewById<Button>(R.id.save_button).setOnClickListener {
            saveSelectedAllergens()
        }
    }

    private fun createAllergenSwitches() {
        container.removeAllViews()
        allergenSwitches.clear()

        for (i in 0 until 96) {
            val switch = Switch(requireContext()).apply {
                id = View.generateViewId()
                val resourceName = "ingredient$i"
                val resourceId = resources.getIdentifier(resourceName, "string", requireContext().packageName)

                text = if (resourceId != 0) getString(resourceId) else "Ингредиент $i"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(100, 0.dpToPx(), 100, 0.dpToPx())
                    setPadding(0, 15.dpToPx(), 0, 15.dpToPx())
                }
            }
            container.addView(switch)
            allergenSwitches.add(switch)
        }
    }

    private fun saveSelectedAllergens() {
        val selectedIndices = allergenSwitches
            .mapIndexed { index, switch -> index to switch.isChecked }
            .filter { it.second }
            .map { it.first }
            .toSet()

        allergenListener?.onAllergensSelected(selectedIndices)
        parentFragmentManager.popBackStack()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}