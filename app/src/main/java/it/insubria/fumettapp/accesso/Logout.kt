package it.insubria.fumettapp.accesso

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

class Logout(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signOutUser() {
        firebaseAuth.signOut()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}