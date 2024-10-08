package it.insubria.fumettapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.insubria.fumettapp.accesso.Logout
//gestisce la visualizzazione e l'interazione con una libreria di fumetti organizzata per "collane"
class LibraryActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper//un'istanza di `DatabaseHelper` per interagire con il database locale
    private lateinit var recyclerView: RecyclerView//RecyclerView` utilizzato per mostrare la lista delle collane o dei fumetti
    private lateinit var collanaAdapter: CollanaAdapter//Adapter per gestire la visualizzazione delle collane e dei fumetti nel `RecyclerView`
    private lateinit var fumettoAdapter: FumettoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        databaseHelper = DatabaseHelper(this)//creata un'istanza di `DatabaseHelper`
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)//RecyclerView` viene configurato

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_logout -> {
                    Logout(this).signOutUser()
                    true
                }
                R.id.nav_add -> {
                    val intent = Intent(this, AggiungiFumettoActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_ADD)
                    true
                }
                else -> false
            }
        }

        //creato un adapter per la lista delle collane, e assegnato al `RecyclerView`
        collanaAdapter = CollanaAdapter(emptyList(),
            onItemClick = { collana ->
                mostraFumettiPerCollana(collana)
            },
            onItemLongClick = { collana ->
                mostraDialogoOpzioniCollana(collana)
            }
        )
        recyclerView.adapter = collanaAdapter

        // Carica le collane dal database
        loadCollaneFromDatabase()
    }
    private fun loadCollaneFromDatabase() { //recupera tutte le collane dal database e aggiorna l'adapter
        val collane = databaseHelper.getAllCollane()
        collanaAdapter.updateCollane(collane)
    }

    private fun mostraFumettiPerCollana(collana: String) { //recupera i fumetti associati a quella collana
        val fumetti = databaseHelper.getFumettiPerCollana(collana)
        fumettoAdapter = FumettoAdapter(fumetti, onItemClick = { fumetto ->
            // Gestisci il click semplice qui, se necessario
        },
            onItemLongClick = { fumetto ->
                mostraDialogoOpzioni(fumetto)
            }
        )
        recyclerView.adapter = fumettoAdapter
    }
    override fun onResume() {
        super.onResume()
        // Assicurati che l'item corretto sia selezionato
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = 0 // Imposta selectedItemId a 0 per deselezionare tutti gli item
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD, REQUEST_CODE_EDIT -> {
                    // Aggiorna la lista dei fumetti
                    loadFumettiFromDatabase()
                }
            }
        }
    }//gestisce il risultato delle attività per l'aggiunta o modifica di un fumetto

    private fun loadFumettiFromDatabase() {
        val fumetti = databaseHelper.getAllFumetti()
        if (::fumettoAdapter.isInitialized) {
            fumettoAdapter.updateFumetti(fumetti)
        } else {
            fumettoAdapter = FumettoAdapter(fumetti,
                onItemClick = { fumetto ->
                },
                onItemLongClick = { fumetto ->
                    mostraDialogoOpzioni(fumetto)
                }
            )
            recyclerView.adapter = fumettoAdapter
        }
    }//carica tutti i fumetti dal database e aggiorna il `fumettoAdapter`

    private fun mostraDialogoOpzioni(fumetto: Fumetto) {
        val options = arrayOf("Modifica", "Elimina")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Scegli un'opzione")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> modificaFumetto(fumetto)
                1 -> confermaEliminazioneFumetto(fumetto)
            }
        }
        builder.show()
    }//opzioni per modificare o eliminare un fumetto

    private fun mostraDialogoOpzioniCollana(collana: String) {
        val options = arrayOf("Modifica Collana", "Elimina Collana")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Scegli un'opzione")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> modificaCollana(collana)
                1 -> confermaEliminazioneCollana(collana)
            }
        }
        builder.show()
    }

    private fun modificaFumetto(fumetto: Fumetto) {//apre AggiungiFumettoActivity
        val intent = Intent(this, AggiungiFumettoActivity::class.java)
        intent.putExtra("fumetto_id", fumetto.id)
        startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    private fun confermaEliminazioneFumetto(fumetto: Fumetto) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Conferma eliminazione")
        builder.setMessage("Sei sicuro di voler eliminare questo fumetto?")
        builder.setPositiveButton("Sì") { dialog, which ->
            databaseHelper.deleteFumetto(fumetto.id)
            mostraFumettiPerCollana(fumetto.collana) // Ricarica i fumetti della stessa collana
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }//conferma per eliminare un fumetto

    private fun modificaCollana(collana: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Modifica Collana")

        val input = EditText(this)
        input.setText(collana)
        builder.setView(input)

        builder.setPositiveButton("Salva") { dialog, which ->
            val nuovoNome = input.text.toString().trim()
            if (nuovoNome.isNotEmpty()) {
                val successo = databaseHelper.updateCollanaNome(collana, nuovoNome)
                if (successo) {
                    loadCollaneFromDatabase() // Aggiorna la lista delle collane
                    Toast.makeText(this, "Nome collana aggiornato!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Errore nell'aggiornamento del nome.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Il nome della collana non può essere vuoto.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Annulla") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }


    private fun confermaEliminazioneCollana(collana: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Conferma eliminazione")
        builder.setMessage("Sei sicuro di voler eliminare questa collana e tutti i fumetti al suo interno?")
        builder.setPositiveButton("Sì") { dialog, which ->
            databaseHelper.deleteCollanaEAssociati(collana)
            loadCollaneFromDatabase() // Aggiorna la lista delle collane
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }


    override fun onBackPressed() {//gestisce il comportamento del tasto "Indietro"
        if (::fumettoAdapter.isInitialized && recyclerView.adapter == fumettoAdapter) {
            // Se il fumettoAdapter è attualmente mostrato, torna al collanaAdapter
            recyclerView.adapter = collanaAdapter
        } else {
            // Altrimenti, chiama il comportamento predefinito del tasto indietro
            super.onBackPressed()
        }
    }


    companion object {
        private const val REQUEST_CODE_ADD = 1
        private const val REQUEST_CODE_EDIT = 2
    }
    //contiene costanti utilizzate per identificare le richieste di aggiunta o modifica di un fumetto

}