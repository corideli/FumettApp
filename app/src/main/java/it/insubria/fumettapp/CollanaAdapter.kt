package it.insubria.fumettapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CollanaAdapter(
    private var collane: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CollanaAdapter.CollanaViewHolder>() {

    inner class CollanaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvCollana)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollanaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collana, parent, false)
        return CollanaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollanaViewHolder, position: Int) {
        val collana = collane[position]
        holder.textView.text = collana
        holder.itemView.setOnClickListener { onItemClick(collana) }
    }

    override fun getItemCount(): Int {
        return collane.size
    }

    fun updateCollane(newCollane: List<String>) {
        collane = newCollane
        notifyDataSetChanged()
    }
}
