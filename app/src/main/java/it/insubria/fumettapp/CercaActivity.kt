package it.insubria.fumettapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CercaActivity : AppCompatActivity() {


    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FumettoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cerca)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val etSearch: EditText = findViewById(R.id.etSearch)
        val btnSearch: Button = findViewById(R.id.btnSearch)

        btnSearch.setOnClickListener {
            val searchTerm = etSearch.text.toString()
            // Esegui la ricerca nel database
            val searchResults = databaseHelper.searchFumetti(searchTerm)
            // Aggiorna l'Adapter con i risultati della ricerca
            adapter.updateFumetti(searchResults)
        }

        // Inizializza l'Adapter con una lista vuota (o vuoi mostrare tutti i fumetti inizialmente)
        adapter = FumettoAdapter(emptyList())
        recyclerView.adapter = adapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = 0
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_favorites -> {
                    val intent = Intent(this, PreferitiActivity::class.java)
                    startActivity(intent)
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
