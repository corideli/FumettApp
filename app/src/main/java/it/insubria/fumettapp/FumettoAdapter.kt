package it.insubria.fumettapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FumettoAdapter(private val fumetti: List<Fumetto>, private val onItemClick: (Fumetto) -> Unit) : RecyclerView.Adapter<FumettoAdapter.FumettoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FumettoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_fumetto, parent, false)
        return FumettoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FumettoViewHolder, position: Int) {
        val fumetto = fumetti[position]
        holder.bind(fumetto)
        holder.itemView.setOnClickListener {
            onItemClick(fumetto)
        }
    }

    override fun getItemCount() = fumetti.size

    class FumettoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titoloFumetto: TextView = itemView.findViewById(R.id.titoloFumetto)
        private val autoreFumetto: TextView = itemView.findViewById(R.id.autoreFumetto)
        private val numeroPagineFumetto: TextView = itemView.findViewById(R.id.numeroPagineFumetto)
        private val statoPallino: View = itemView.findViewById(R.id.statoPallino)

        fun bind(fumetto: Fumetto) {
            titoloFumetto.text = fumetto.titolo
            autoreFumetto.text = fumetto.autore
            numeroPagineFumetto.text = fumetto.numeroPagine.toString()
            statoPallino.setBackgroundColor(
                when (fumetto.stato) {
                    Stato.PRESENTE -> ContextCompat.getColor(itemView.context, R.color.color_present)
                    Stato.PRENOTAZIONE -> ContextCompat.getColor(itemView.context, R.color.color_reserved)
                    Stato.MANCANTE -> ContextCompat.getColor(itemView.context, R.color.color_missing)
                }
            )
        }
    }
}

