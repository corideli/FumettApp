package it.insubria.fumettapp.accesso

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import it.insubria.fumettapp.R
//fornisce l'implementazione di un fragment che permette agli utenti di recuperare la password nel caso l'abbiano dimenticata

class PasswordDimenticataFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth//rappresenta l'istanza di autenticazione di Firebase utilizzata per l'invio dell'email di reset della password
    private lateinit var emailEditText: EditText//rappresenta il campo di input dell'email dove l'utente inserisce la sua email

    override fun onCreateView(//chiamato quando il fragment deve creare la sua interfaccia utente
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla il layout per questo fragment
        return inflater.inflate(R.layout.fragment_password_dimenticata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//chiamato subito dopo che la vista è stata creata
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()//inizializzata l'istanza di FirebaseAuth
        emailEditText = view.findViewById(R.id.et_mail)

        val resetButton = view.findViewById<View>(R.id.resetPasswordButton)
        resetButton.setOnClickListener {//impostato un listener per il bottone
            val emailAddress = emailEditText.text.toString().trim()//ottiene l'email inserita dall'utente

            if (emailAddress.isNotEmpty()) {//Controlla se l'utente ha effettivamente inserito un'email
                firebaseAuth.sendPasswordResetEmail(emailAddress)//utilizza l'istanza di FirebaseAuth per inviare un'email di reset della password
                    .addOnCompleteListener { task ->//listener che verifica se l'operazione di invio è riuscita
                        if (task.isSuccessful) {
                            Log.d(TAG, "Email inviata.")
                            Toast.makeText(context, "Email di reset inviata.", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(TAG, "Errore nell'invio dell'email.", task.exception)
                            Toast.makeText(context, "Errore nell'invio dell'email.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Inserisci un'email valida.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
