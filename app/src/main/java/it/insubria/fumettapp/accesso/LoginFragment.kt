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

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegistrazione.setOnClickListener {
            // Naviga verso il fragment di registrazione
            // Presupponendo che tu abbia un fragment di registrazione
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment()) // Sostituisci RegisterFragment con il fragment di registrazione corretto
                .addToBackStack(null)
                .commit()
        }

        binding.btnAccedi.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val pass = binding.editTextTextPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        Toast.makeText(context, "Utente non registrato, verifica le credenziali", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Non sono ammessi campi vuoti!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPswDim.setOnClickListener {
            // Naviga verso il fragment di recupero password
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PasswordDimenticataFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}
