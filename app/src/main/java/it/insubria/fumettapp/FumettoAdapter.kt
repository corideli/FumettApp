package it.insubria.fumettapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
//collega i dati dei fumetti a una RecyclerView
class FumettoAdapter(private var fumetti: List<Fumetto>, private val onItemClick: (Fumetto) -> Unit, private val onItemLongClick: (Fumetto) -> Unit) : RecyclerView.Adapter<FumettoAdapter.FumettoViewHolder>() {
    //accetta una lista di fumetti, una lambda per gestire i click sugli elementi, e una lambda per gestire i long click
    constructor(fumetti: List<Fumetto>) : this(
        fumetti,
        {}, // Default empty onItemClick lambda
        {}  // Default empty onItemLongClick lambda
    ) //fornisce costruttori che passano lambdas vuote

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FumettoViewHolder {//creazione della vista per ciascun elemento della lista
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_fumetto, parent, false)
        return FumettoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FumettoViewHolder, position: Int) {//viene chiamato dal RecyclerView per associare i dati di un fumetto specifico (in base alla posizione) a un `FumettoViewHolder`
        val fumetto = fumetti[position]
        holder.bind(fumetto)
        holder.itemView.setOnClickListener {
            onItemClick(fumetto)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(fumetto)
            true
        }//associa l'azione di click e long click alle relative lambdas
    }

    override fun getItemCount() = fumetti.size
    //restituisce il numero di elementi nella lista `fumetti`, per informare il RecyclerView di quanti elementi deve visualizzare

    fun updateFumetti(newFumetti: List<Fumetto>) {
        fumetti = newFumetti
        notifyDataSetChanged()
    }
    //aggiorna la lista dei fumetti visualizzata con una nuova lista e lo notifica al RecyclerView

    class FumettoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//rappresenta la vista di un singolo fumetto all'interno del RecyclerView
        private val titoloFumetto: TextView = itemView.findViewById(R.id.titoloFumetto)
        private val autoreFumetto: TextView = itemView.findViewById(R.id.autoreFumetto)
        private val numeroPagineFumetto: TextView = itemView.findViewById(R.id.numeroPagineFumetto)
        private val statoPallino: View = itemView.findViewById(R.id.statoPallino)

        fun bind(fumetto: Fumetto) {//associa i dati di un oggetto `Fumetto` alle viste all'interno del ViewHolder
            titoloFumetto.text = fumetto.titolo
            autoreFumetto.text = fumetto.autore
            numeroPagineFumetto.text = fumetto.numeroPagine.toString()
            statoPallino.setBackgroundColor(//per rappresentare visivamente lo stato
                when (fumetto.stato) {
                    Stato.PRESENTE -> ContextCompat.getColor(itemView.context, R.color.color_present)
                    Stato.PRENOTAZIONE -> ContextCompat.getColor(itemView.context, R.color.color_reserved)
                    Stato.MANCANTE -> ContextCompat.getColor(itemView.context, R.color.color_missing)
                }
            )
        }
    }
}

