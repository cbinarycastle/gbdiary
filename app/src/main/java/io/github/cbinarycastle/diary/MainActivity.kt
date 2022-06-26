package io.github.cbinarycastle.diary

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.cbinarycastle.diary.databinding.ActivityMainBinding
import io.github.cbinarycastle.diary.extensions.launchAndRepeatWithLifecycle
import io.github.cbinarycastle.diary.signin.SignInViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<SignInViewModel>()

    private val googleSignInLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.e(e, "Google sign in failed")
            }
        }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        auth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )

        launchAndRepeatWithLifecycle {
            viewModel.signInNavigationAction.collect {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Timber.d("signInWithCredential:success")
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e(task.exception, "signInWithCredential:failure")
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        binding.email.text = user?.email
    }
}