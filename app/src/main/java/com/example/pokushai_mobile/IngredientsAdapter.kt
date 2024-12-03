package com.example.pokushai_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientsAdapter(private var ingredients: List<Pair<String, String>>) :
    RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    fun updateIngredients(newIngredients: List<Pair<String, String>>) {
        ingredients = newIngredients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient.first, ingredient.second)
    }

    override fun getItemCount(): Int = ingredients.size

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.ingredientName)
        private val quantityText: TextView = itemView.findViewById(R.id.ingredientQuantity)

        fun bind(name: String, quantity: String) {
            nameText.text = name
            quantityText.text = quantity
        }
    }
}
