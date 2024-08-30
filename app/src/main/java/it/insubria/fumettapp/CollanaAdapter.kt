package it.insubria.fumettapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//classe che gestisce una lista di `collane`, che è una lista di stringhe rappresentanti i nomi delle collane di fumetti
class CollanaAdapter(
    private var collane: List<String>,//lista di stringhe che rappresenta i dati da visualizzare nel `RecyclerView`
    private val onItemClick: (String) -> Unit//funzione lambda che viene eseguita quando un elemento del `RecyclerView` viene cliccato
) : RecyclerView.Adapter<CollanaAdapter.CollanaViewHolder>() {

    inner class CollanaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//rappresenta la vista per un singolo elemento del `RecyclerView`
        val textView: TextView = itemView.findViewById(R.id.tvCollana)//mostra il nome della collana
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollanaViewHolder {//metodo chiamato quando il `RecyclerView` ha bisogno di creare un nuovo `ViewHolder`
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collana, parent, false)//viene utilizzato per creare una nuova vista
        return CollanaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollanaViewHolder, position: Int) {//viene chiamato per associare i dati alla vista
        val collana = collane[position]//recupera il nome della collana dall'elenco utilizzando la posizione corrente.
        holder.textView.text = collana //imposta il testo del `TextView` all'interno del `ViewHolder` con il nome della collana corrente
        holder.itemView.setOnClickListener { onItemClick(collana) }//imposta un listener per il click sull'elemento corrente
    }

    override fun getItemCount(): Int {//ritorna il numero di elementi nella lista `collane`
        return collane.size
    }

    fun updateCollane(newCollane: List<String>) {//consente di aggiornare la lista delle `collane` con una nuova lista
        collane = newCollane
        notifyDataSetChanged()
    }
}
