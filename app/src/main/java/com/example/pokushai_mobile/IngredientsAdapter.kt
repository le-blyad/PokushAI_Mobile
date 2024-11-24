package com.example.pokushai_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientsAdapter(private var ingredients: List<String>) : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    // Метод для обновления данных
    fun updateIngredients(newIngredients: List<String>) {
        ingredients = newIngredients
        notifyDataSetChanged()  // Это перерисует RecyclerView с новыми данными
    }

    // Ваши остальные методы для адаптера
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount(): Int = ingredients.size

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ingredientText: TextView = itemView.findViewById(R.id.ingredientText)

        fun bind(ingredient: String) {
            ingredientText.text = ingredient
        }
    }
}
