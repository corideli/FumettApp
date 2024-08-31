package it.insubria.fumettapp.accesso

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import it.insubria.fumettapp.MainActivity
import it.insubria.fumettapp.R
import it.insubria.fumettapp.databinding.FragmentLoginBinding
//frammento che gestisce la logica di autenticazione dell'utente tramite Firebase
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding//riferimento all'istanza di Firebase Authentication, utilizzata per gestire l'autenticazione degli utenti
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(//creazione della vista del frammento
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()//inizializza l'istanza di Firebase Authentication

        binding.btnRegistrazione.setOnClickListener {//imposta un listener per il bottone di registrazione. Quando cliccato, naviga al `RegisterFragment` utilizzando una transazione di frammenti
            // Naviga verso il fragment di registrazione
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment()) // Sostituisci RegisterFragment con il fragment di registrazione corretto
                .addToBackStack(null)
                .commit()
        }

        binding.btnAccedi.setOnClickListener {//imposta un listener per il bottone di accesso
            val email = binding.editTextTextEmailAddress.text.toString()//recupera l'email e la password inseriti dall'utente
            val pass = binding.editTextTextPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { //tenta di autenticare l'utente con Firebase
                    if (it.isSuccessful) {
                        val intent = Intent(activity, MainActivity::class.java)//se l'autenticazione ha successo, avvia `MainActivity`
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        Toast.makeText(context, "Utente non registrato, verifica le credenziali", Toast.LENGTH_SHORT).show()
                    }//se fallisce, mostra un `Toast` con un messaggio di errore
                }
            } else {
                Toast.makeText(context, "Non sono ammessi campi vuoti!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPswDim.setOnClickListener {//imposta un listener per il bottone di recupero password. Quando cliccato, naviga al `PasswordDimenticataFragment
            // Naviga verso il fragment di recupero password
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PasswordDimenticataFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onStart() {//se esiste un utente autenticato, l'applicazione avvia `MainActivity`
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}
