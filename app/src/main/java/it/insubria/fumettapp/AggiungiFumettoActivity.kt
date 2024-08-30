package it.insubria.fumettapp

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
            }
        }//i dati inseriti vengono letti e usati per creare un nuovo fumetto

        btnAggiorna.setOnClickListener {
            val titolo = etTitolo.text.toString()
            val autore = etAutore.text.toString()
            val numeroPagine = etNumeroPagine.text.toString().toIntOrNull() ?: 0
            val collana = etCollana.text.toString()
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
                finish()
            } else {
                Toast.makeText(this, "Errore nell'aggiornamento", Toast.LENGTH_SHORT).show()
            }
        }//vengono letti i dati dei campi e viene creato un fumetto con l'ID esistente e i nuovi valori
    }
}
