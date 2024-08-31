package it.insubria.fumettapp.accesso

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
//esegue il logout dell'utente dall'applicazione e lo reindirizza alla schermata di login
class Logout(private val context: Context) {
//il contesto è necessario per avviare nuove attività (come la schermata di login)

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //istanza utilizzata per eseguire operazioni di autenticazione

    fun signOutUser() {
        firebaseAuth.signOut()//esegue il logout dell'utente corrente

        val intent = Intent(context, LoginActivity::class.java)//viene creato un `Intent` per avviare la `LoginActivity`
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //indica che la nuova attività (`LoginActivity`) dovrebbe essere avviata in un nuovo task
        //cancella il task corrente e la `LoginActivity` diventa l'unica attività nel task

        context.startActivity(intent)
        //reindirizza l'utente alla schermata di login
    }
}