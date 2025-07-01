package com.example.pokushai_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FoodAdapter(
    private val items: List<FoodItem>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageButton: ImageButton = view.findViewById(R.id.imageButton)
        val textView: TextView = view.findViewById(R.id.textView)

        init {
            imageButton.setOnClickListener { onClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageButton.setImageResource(item.imageResId)
        holder.textView.text = holder.itemView.context.getString(item.nameResId)
    }

    override fun getItemCount() = items.size
}

data class FoodItem(
    val originalIndex: Int,
    val imageResId: Int,
    val nameResId: Int
)