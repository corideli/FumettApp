package it.insubria.fumettapp

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
//Aggiungere activity/fragment per l'apertura dettagli fumetto al click
class FumettoAdapter(private var fumetti: List<Fumetto>, private val onItemClick: (Fumetto) -> Unit, private val onItemLongClick: (Fumetto) -> Unit) : RecyclerView.Adapter<FumettoAdapter.FumettoViewHolder>() {
    constructor(fumetti: List<Fumetto>) : this(
        fumetti,
        {}, // Default empty onItemClick lambda
        {}  // Default empty onItemLongClick lambda
    )

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
        holder.itemView.setOnLongClickListener {
            onItemLongClick(fumetto)
            true
        }
    }


    override fun getItemCount() = fumetti.size

    fun updateFumetti(newFumetti: List<Fumetto>) {
        fumetti = newFumetti
        notifyDataSetChanged()
    }


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

