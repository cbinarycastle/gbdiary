package io.github.cbinarycastle.diary

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.cbinarycastle.diary.databinding.ActivityMainBinding
import io.github.cbinarycastle.diary.extensions.launchAndRepeatWithLifecycle
import io.github.cbinarycastle.diary.signin.SignInViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<SignInViewModel>()

    private val googleSignInLauncher = registerForActivityResult(StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        viewModel.signInWithGoogle(task)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.viewModel
        }
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

        launchAndRepeatWithLifecycle {
            viewModel.errorMessage.collect {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkSignedIn()
    }
}