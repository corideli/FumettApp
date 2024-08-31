package it.insubria.fumettapp.accesso

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import it.insubria.fumettapp.R
import it.insubria.fumettapp.databinding.FragmentRegisterBinding
//questo fragment permette agli utenti di registrarsi tramite email e password usando Firebase Authentication
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding//variabile utilizzata per accedere agli elementi del layout in modo sicuro e tipizzato
    private lateinit var firebaseAuth: FirebaseAuth//variabile per gestire l'autenticazione con Firebase

    override fun onCreateView(//chiamato per creare e restituire la vista gerarchica associata al fragment
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        //inizializza il binding utilizzando il layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//chiamato subito dopo che la vista è stata creata e utilizzato per configurare elementi dell'interfaccia utente e la logica del fragment
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()//inizializza l'istanza di FirebaseAuth per l'autenticazione

        binding.btnAccesso.setOnClickListener {//configura un listener per il pulsante di accesso
            // Naviga verso il LoginFragment
            parentFragmentManager.beginTransaction()//crea una nuova transazione di fragment
                .replace(R.id.fragment_container, LoginFragment())//sostituisce l'attuale fragment con `LoginFragment`
                .addToBackStack(null)//aggiunge la transazione alla backstack
                .commit()//applica la transazione
        }

        binding.Registrati.setOnClickListener {
            val email = binding.Email.text.toString()//ottiene l'email inserita dall'utente
            val pass = binding.password.text.toString()
            val confirmPass = binding.psdConferma.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {//controlla se la password e la conferma corrispondono
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {//tenta di registrare un nuovo utente con l'email e la password specificate
                        if (it.isSuccessful) {
                            val intent = Intent(activity, LoginFragment::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "La password non corrisponde", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Non sono ammessi campi vuoti!", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestione dei padding per edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->//configura un listener per adattare i padding del layout principale (`R.id.main`) in base agli insets delle system bars
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)//imposta i padding della vista in modo che il contenuto non venga coperto dalle barre di sistema
            insets
        }
    }
}
