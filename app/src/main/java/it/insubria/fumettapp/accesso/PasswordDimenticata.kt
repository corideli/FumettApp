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

class PasswordDimenticata : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla il layout per questo fragment
        return inflater.inflate(R.layout.fragment_password_dimenticata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Trova l'EditText per l'email
        emailEditText = view.findViewById(R.id.et_mail)

        // Imposta il listener per il bottone di reset (presupponendo che tu abbia un bottone nel layout)
        val resetButton = view.findViewById<View>(R.id.resetPasswordButton)
        resetButton.setOnClickListener {
            val emailAddress = emailEditText.text.toString().trim()

            if (emailAddress.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
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
