package com.example.pokushai_mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment

class AllRecipesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Используем правильное имя макета
        return inflater.inflate(R.layout.fragment_result_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonBack = view.findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {

            // Если нужно принудительно применить анимацию:
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commit()

            // Запускаем анимацию возврата
            parentFragmentManager.popBackStack()
        }

    }


    private fun showRecipeDetails(recipeId: Int) {
        Toast.makeText(requireContext(), "Рецепт #$recipeId (в разработке)", Toast.LENGTH_SHORT).show()
    }
}