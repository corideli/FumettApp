package it.insubria.fumettapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.insubria.fumettapp.accesso.Logout

//classe progettata per consentire agli utenti di cercare fumetti nel database e visualizzarli in una lista tramite un `RecyclerView
class CercaActivity : AppCompatActivity() {


    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FumettoAdapter//istanza di `FumettoAdapter`, un adattatore utilizzato per collegare i dati dei fumetti al `RecyclerView`

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cerca)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val etSearch: EditText = findViewById(R.id.etSearch)
        val btnSearch: Button = findViewById(R.id.btnSearch)

        btnSearch.setOnClickListener {
            val searchTerm = etSearch.text.toString()//viene letto il testo inserito
            // Esegui la ricerca nel database
            val searchResults = databaseHelper.searchFumetti(searchTerm)//viene chiamato per cercare i fumetti che corrispondono alla ricerca
            // Aggiorna l'Adapter con i risultati della ricerca
            adapter.updateFumetti(searchResults)
        }

        // Inizializza l'Adapter con una lista vuota
        adapter = FumettoAdapter(emptyList())
        recyclerView.adapter = adapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = 0
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->//listener impostato per gestire i clic sugli elementi della barra di navigazione
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
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // Assicurati che l'item corretto sia selezionato
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = 0
    }
}
