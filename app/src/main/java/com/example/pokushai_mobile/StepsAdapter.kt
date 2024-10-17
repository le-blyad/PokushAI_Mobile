package com.example.pokushai_mobile

import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater


class StepsAdapter(
    private val steps: MutableList<Step>,
    private val onRemoveStep: (Int) -> Unit
) : RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    // Создайте ViewHolder и свяжите с item_step.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.designer_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        holder.bind(step)
        holder.itemView.findViewById<Button>(R.id.deleteStepButton).setOnClickListener {
            onRemoveStep(position)
        }
    }

    override fun getItemCount(): Int = steps.size

    // Добавление нового шага
    fun addStep(step: Step) {
        steps.add(step)
        notifyItemInserted(steps.size - 1)  // Уведомляем адаптер об изменениях
    }

    // Удаление шага
    fun removeStep(position: Int) {
        steps.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, steps.size)  // Обновление порядковых номеров шагов
    }

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(step: Step) {
            // Привязка данных к элементам View
            itemView.findViewById<TextView>(R.id.stepNumber).text = "Шаг ${step.number}"
            itemView.findViewById<TextView>(R.id.stepDescription).text = step.description
        }
    }
}
