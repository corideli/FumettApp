package it.insubria.fumettapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import it.insubria.fumettapp.DatabaseHelper.Companion.COLUMN_NOME
import it.insubria.fumettapp.DatabaseHelper.Companion.TABLE_COLLANE

//permette di aggiungere o aggiornare un fumetto
class AggiungiFumettoActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper//classe per gestire le operazioni sul database SQLite
    private var fumettoId: Long = -1//variabile per conservare l'ID del fumetto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungifumetto)

        databaseHelper = DatabaseHelper(this)

        val etTitolo: EditText = findViewById(R.id.etTitolo)
        val etAutore: EditText = findViewById(R.id.etAutore)
        val etNumeroPagine: EditText = findViewById(R.id.etNumeroPagine)
        val rgStato: RadioGroup = findViewById(R.id.rgStato)
        val etCollana: EditText = findViewById(R.id.etCollana)
        val btnSalva: Button = findViewById(R.id.btnSalva)
        val btnAggiorna: Button = findViewById(R.id.btnAggiorna)
        //gli elementi dell'interfaccia utente vengono recuperati

        fumettoId = intent.getLongExtra("fumetto_id", -1)
        if (fumettoId != -1L) {
            val fumetto = databaseHelper.getFumettoById(fumettoId)//Caricamento dei dati se l'ID del fumetto è presente
            fumetto?.let {
                etTitolo.setText(it.titolo)
                etAutore.setText(it.autore)
                etNumeroPagine.setText(it.numeroPagine.toString())
                etCollana.setText(it.collana)
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
            val collana = etCollana.text.toString()

            // Controllo di validazione
            if (titolo.isEmpty() || autore.isEmpty() || numeroPagine <= 0 || collana.isEmpty()) {
                Toast.makeText(this, "Per favore, compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verifica duplicati
            val esisteFumetto = databaseHelper.esisteFumettoConDettagli(titolo, autore, numeroPagine, collana)
            if (esisteFumetto) {
                Toast.makeText(this, "Un fumetto con questi dettagli esiste già", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aggiungi la collana se non esiste
            databaseHelper.aggiungiCollanaSeNecessario(collana)

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
                stato = stato,
                collana = collana
            )
            val result = databaseHelper.insertFumetto(nuovoFumetto)
            if (result != -1L) {
                Toast.makeText(this, "Fumetto salvato con successo", Toast.LENGTH_SHORT).show()
                resetCampi()
                setResult(Activity.RESULT_OK)
            } else {
                Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
            }
        } //i dati inseriti vengono letti e usati per creare un nuovo fumetto

        btnAggiorna.setOnClickListener {
            val titolo = etTitolo.text.toString()
            val autore = etAutore.text.toString()
            val numeroPagine = etNumeroPagine.text.toString().toIntOrNull() ?: 0
            val collana = etCollana.text.toString()

            // Controllo di validazione
            if (titolo.isEmpty() || autore.isEmpty() || numeroPagine <= 0 || collana.isEmpty()) {
                Toast.makeText(this, "Per favore, compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verifica duplicati
            val esisteFumetto = databaseHelper.esisteFumettoConDettagli(titolo, autore, numeroPagine, collana, fumettoId)
            if (esisteFumetto) {
                Toast.makeText(this, "Un fumetto con questi dettagli esiste già", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
                stato = stato,
                collana = collana
            )
            val result = databaseHelper.updateFumetto(fumettoAggiornato)
            if (result > 0) {
                Toast.makeText(this, "Fumetto aggiornato con successo", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
            } else {
                Toast.makeText(this, "Errore nell'aggiornamento", Toast.LENGTH_SHORT).show()
            }
        }//vengono letti i dati dei campi e viene creato un fumetto con l'ID esistente e i nuovi valori
    }

    private fun resetCampi() {
        findViewById<EditText>(R.id.etTitolo).text.clear()
        findViewById<EditText>(R.id.etAutore).text.clear()
        findViewById<EditText>(R.id.etNumeroPagine).text.clear()
        findViewById<EditText>(R.id.etCollana).text.clear()
        findViewById<RadioGroup>(R.id.rgStato).clearCheck()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Chiude tutte le attività nella parte superiore dello stack e apre MainActivity
        startActivity(intent)
        finish() // Termina l'Activity corrente
    }


}
