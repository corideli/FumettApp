package it.insubria.fumettapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class LibraryActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var collanaAdapter: CollanaAdapter
    private lateinit var fumettoAdapter: FumettoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                    val intent = Intent(this, PreferitiActivity::class.java)
                    startActivity(intent)
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

        collanaAdapter = CollanaAdapter(emptyList()) { collana ->
            mostraFumettiPerCollana(collana)
        }
        recyclerView.adapter = collanaAdapter

        // Carica le collane dal database
        loadCollaneFromDatabase()
    }
    private fun loadCollaneFromDatabase() {
        val collane = databaseHelper.getAllCollane()
        collanaAdapter.updateCollane(collane)
    }

    private fun mostraFumettiPerCollana(collana: String) {
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
    }

    private fun loadFumettiFromDatabase() {
        val fumetti = databaseHelper.getAllFumetti()
        if (::fumettoAdapter.isInitialized) {
            fumettoAdapter.updateFumetti(fumetti)
        } else {
            fumettoAdapter = FumettoAdapter(fumetti,
                onItemClick = { fumetto ->
                    // Gestisci il click semplice qui, se necessario
                },
                onItemLongClick = { fumetto ->
                    mostraDialogoOpzioni(fumetto)
                }
            )
            recyclerView.adapter = fumettoAdapter
        }
    }

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
    }

    private fun modificaFumetto(fumetto: Fumetto) {
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
            loadFumettiFromDatabase()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val REQUEST_CODE_ADD = 1
        private const val REQUEST_CODE_EDIT = 2
    }

}