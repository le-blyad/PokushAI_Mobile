package com.example.pokushai_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StepAdapter(private val steps: List<Step>) :
    RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    data class Step(val text: String, val imageResId: Int)

    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepText: TextView = itemView.findViewById(R.id.stepText)
        val stepImage: ImageView = itemView.findViewById(R.id.stepImage)
        val numberStep: TextView = itemView.findViewById(R.id.numberStep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        holder.stepText.text = step.text
        holder.stepImage.setImageResource(step.imageResId)
        holder.numberStep.text = "Шаг ${position + 1}"
    }

    override fun getItemCount(): Int = steps.size
}

