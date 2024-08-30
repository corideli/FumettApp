package it.insubria.fumettapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.insubria.fumettapp.accesso.Logout
//gestisce l'interfaccia utente e le funzionalità per visualizzare e gestire una lista di fumetti mancanti
class DesideriActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper//istanza della classe `DatabaseHelper`, che gestisce le operazioni CRUD sul database SQLite
    private lateinit var recyclerView: RecyclerView//per visualizzare la lista di fumetti
    private lateinit var fumettoAdapter: FumettoAdapter//adapter per gestire l'interfaccia tra la lista di fumetti e il `RecyclerView`

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desideri)

        databaseHelper = DatabaseHelper(this)//crea un'istanza per accedere al database
        recyclerView = findViewById(R.id.recyclerView)//configura il `RecyclerView` con un layout manager lineare
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = 0
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

        loadFumettiMancantiFromDatabase()
    }

    private fun loadFumettiMancantiFromDatabase() {//carica i fumetti mancanti dal database
        val fumetti = databaseHelper.getFumettiMancanti()
        if (::fumettoAdapter.isInitialized) {
            fumettoAdapter.updateFumetti(fumetti)//aggiorna la lista di fumetti
        } else {
            fumettoAdapter = FumettoAdapter(fumetti,
                onItemClick = { fumetto ->
                },
                onItemLongClick = { fumetto ->
                    mostraDialogoOpzioni(fumetto)
                }
            )
            recyclerView.adapter = fumettoAdapter
        }//crea l'adattatore con i fumetti mancanti e lo associa al `RecyclerView`
    }

    override fun onResume() {
        super.onResume()
        // Ricarica i fumetti mancanti dal database quando l'attività riprende
        loadFumettiMancantiFromDatabase()

        // Assicura che l'item corretto sia selezionato
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = 0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD, REQUEST_CODE_EDIT -> {
                    // Ricarica i fumetti mancanti dal database
                    loadFumettiMancantiFromDatabase()
                }
            }
        }
    }

    private fun mostraDialogoOpzioni(fumetto: Fumetto) {//due opzioni quando si tiene premuto su un fumetto
        val options = arrayOf("Modifica", "Elimina")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Scegli un'opzione")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> modificaFumetto(fumetto) //apre AggiungiFumettoActivity
                1 -> confermaEliminazioneFumetto(fumetto) //conferma l'eliminazione
            }
        }
        builder.show()
    }

    private fun modificaFumetto(fumetto: Fumetto) {//avvia AggiungiFumettoActivity
        val intent = Intent(this, AggiungiFumettoActivity::class.java)
        intent.putExtra("fumetto_id", fumetto.id)
        startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    private fun confermaEliminazioneFumetto(fumetto: Fumetto) {//elimina il fumetto dal database e aggiorna la lista
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Conferma eliminazione")
        builder.setMessage("Sei sicuro di voler eliminare questo fumetto?")
        builder.setPositiveButton("Sì") { dialog, which ->
            databaseHelper.deleteFumetto(fumetto.id)
            loadFumettiMancantiFromDatabase()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {//contiene costanti che vengono utilizzate per identificare le richieste di aggiunta o modifica di un fumetto
        private const val REQUEST_CODE_ADD = 1
        private const val REQUEST_CODE_EDIT = 2
    }
}
