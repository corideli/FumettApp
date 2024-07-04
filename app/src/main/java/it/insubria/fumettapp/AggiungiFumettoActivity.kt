/*package it.insubria.fumettapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddActivity : AppCompatActivity() {

    private lateinit var dbHelper: ComicDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = ComicDatabaseHelper(this)

        // Esempio di aggiunta di un fumetto
        val newComic =
            Comic(title = "Batman", author = "Bob Kane", year = 1939, genre = "Superhero", pages = 182)
        dbHelper.addComic(newComic)

        // Esempio di recupero di tutti i fumetti
        val comics = dbHelper.getAllComics()
        comics.forEach {
            println("Comic: ${it.title}, Author: ${it.author}")

            enableEdgeToEdge()
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}*/
package it.insubria.fumettapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AggiungiFumettoActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var fumettoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungifumetto)

        databaseHelper = DatabaseHelper(this)

        val etTitolo: EditText = findViewById(R.id.etTitolo)
        val etAutore: EditText = findViewById(R.id.etAutore)
        val etNumeroPagine: EditText = findViewById(R.id.etNumeroPagine)
        val rgStato: RadioGroup = findViewById(R.id.rgStato)
        val btnSalva: Button = findViewById(R.id.btnSalva)
        val btnAggiorna: Button = findViewById(R.id.btnAggiorna)

        fumettoId = intent.getLongExtra("fumetto_id", -1)
        if (fumettoId != -1L) {
            val fumetto = databaseHelper.getFumettoById(fumettoId)
            fumetto?.let {
                etTitolo.setText(it.titolo)
                etAutore.setText(it.autore)
                etNumeroPagine.setText(it.numeroPagine.toString())
                when (it.stato) {
                    Stato.PRESENTE -> rgStato.check(R.id.rbPresente)
                    Stato.PRENOTAZIONE -> rgStato.check(R.id.rbPrenotazione)
                    Stato.MANCANTE -> rgStato.check(R.id.rbMancante)
                }
                btnSalva.visibility = View.GONE
                btnAggiorna.visibility = View.VISIBLE
            }
        }

        btnSalva.setOnClickListener {
            val titolo = etTitolo.text.toString()
            val autore = etAutore.text.toString()
            val numeroPagine = etNumeroPagine.text.toString().toIntOrNull() ?: 0
            val stato = when (rgStato.checkedRadioButtonId) {
                R.id.rbPresente -> Stato.PRESENTE
                R.id.rbPrenotazione -> Stato.PRENOTAZIONE
                R.id.rbMancante -> Stato.MANCANTE
                else -> Stato.PRESENTE
            }

            val nuovoFumetto = Fumetto(
                titolo = titolo,
                autore = autore,
                numeroPagine = numeroPagine,
                stato = stato
            )
            val result = databaseHelper.insertFumetto(nuovoFumetto)
            if (result != -1L) {
                Toast.makeText(this, "Fumetto salvato con successo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
            }

            finish()
        }

        btnAggiorna.setOnClickListener {
            val titolo = etTitolo.text.toString()
            val autore = etAutore.text.toString()
            val numeroPagine = etNumeroPagine.text.toString().toIntOrNull() ?: 0
            val stato = when (rgStato.checkedRadioButtonId) {
                R.id.rbPresente -> Stato.PRESENTE
                R.id.rbPrenotazione -> Stato.PRENOTAZIONE
                R.id.rbMancante -> Stato.MANCANTE
                else -> Stato.PRESENTE
            }

            val fumettoAggiornato = Fumetto(
                id = fumettoId,
                titolo = titolo,
                autore = autore,
                numeroPagine = numeroPagine,
                stato = stato
            )
            val result = databaseHelper.updateFumetto(fumettoAggiornato)
            if (result > 0) {
                Toast.makeText(this, "Fumetto aggiornato con successo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Errore nell'aggiornamento", Toast.LENGTH_SHORT).show()
            }

            // Tornare alla MainActivity o visualizzare un messaggio di successo
            finish()
        }
    }
}
