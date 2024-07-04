package it.insubria.fumettapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LibraryActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FumettoAdapter
    private lateinit var btnAggiungi: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnAggiungi = findViewById(R.id.btnAggiungi)
        btnAggiungi.setOnClickListener {
            val intent = Intent(this, AggiungiFumettoActivity::class.java)
            startActivity(intent)
        }

        // Carica i dati dal database e aggiorna l'Adapter
        loadFumettiFromDatabase()
    }

    private fun loadFumettiFromDatabase() {
        val fumetti = databaseHelper.getAllFumetti()
        adapter = FumettoAdapter(fumetti) { fumetto ->
            val intent = Intent(this, AggiungiFumettoActivity::class.java)
            intent.putExtra("fumetto_id", fumetto.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

}