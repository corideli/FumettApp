package it.insubria.fumettapp

import android.annotation.SuppressLint
import android.app.Activity
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
            startActivityForResult(intent, REQUEST_CODE_ADD)
        }

        // Carica i dati dal database e aggiorna l'Adapter
        loadFumettiFromDatabase()
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
        if (::adapter.isInitialized) {
            adapter.updateFumetti(fumetti)
        } else {
            adapter = FumettoAdapter(fumetti) { fumetto ->
                val intent = Intent(this, AggiungiFumettoActivity::class.java)
                intent.putExtra("fumetto_id", fumetto.id)
                startActivityForResult(intent, REQUEST_CODE_EDIT)
            }
            recyclerView.adapter = adapter
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD = 1
        private const val REQUEST_CODE_EDIT = 2
    }

}