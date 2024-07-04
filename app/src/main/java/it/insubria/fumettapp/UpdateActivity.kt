/*package it.insubria.fumettapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog

class UpdateActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var authorInput: EditText
    private lateinit var pagesInput: EditText
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button

    private var id: String? = null
    private var title: String? = null
    private var author: String? = null
    private var pages: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        titleInput = findViewById(R.id.title_input2)
        authorInput = findViewById(R.id.author_input2)
        pagesInput = findViewById(R.id.pages_input2)
        updateButton = findViewById(R.id.update_button)
        deleteButton = findViewById(R.id.delete_button)

        // First we call this
        getAndSetIntentData()

        // Set actionbar title after getAndSetIntentData method
        val ab: ActionBar? = supportActionBar
        if (ab != null) {
            ab.title = title
        }

        updateButton.setOnClickListener {
            // And only then we call this
            val myDB = DatabaseHelper(this@UpdateActivity)
            val titolo = etTitolo.text.toString()
            val autore = etAutore.text.toString()
            val numeroPagine = etNumeroPagine.text.toString().toIntOrNull() ?: 0
            val stato = when (rgStato.checkedRadioButtonId) {
                R.id.rbPresente -> Stato.PRESENTE
                R.id.rbPrenotazione -> Stato.PRENOTAZIONE
                R.id.rbMancante -> Stato.MANCANTE
                else -> Stato.PRESENTE
            }
            myDB.updateFumetto(Fumetto(id = fumettoId, titolo = titolo, autore = autore, numeroPagine = numeroPagine, stato = stato))
        }
        deleteButton.setOnClickListener {
            confirmDialog()
        }
    }

    private fun getAndSetIntentData() {
        if (intent.hasExtra("id") && intent.hasExtra("title") &&
            intent.hasExtra("author") && intent.hasExtra("pages")
        ) {
            // Getting Data from Intent
            id = intent.getStringExtra("id")
            title = intent.getStringExtra("title")
            author = intent.getStringExtra("author")
            pages = intent.getStringExtra("pages")

            // Setting Intent Data
            titleInput.setText(title)
            authorInput.setText(author)
            pagesInput.setText(pages)
            Log.d("stev", "$title $author $pages")
        } else {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete $title ?")
        builder.setMessage("Are you sure you want to delete $title ?")
        builder.setPositiveButton("Yes") { _, _ ->
            val myDB = DatabaseHelper(this@UpdateActivity)
            myDB.deleteOneRow(id!!)
            finish()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.create().show()
    }
}*/