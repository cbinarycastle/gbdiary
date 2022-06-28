package io.github.cbinarycastle.diary.signin

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import io.github.cbinarycastle.diary.R
import io.github.cbinarycastle.diary.databinding.ActivityMainBinding
import io.github.cbinarycastle.diary.extensions.launchAndRepeatWithLifecycle
import io.github.cbinarycastle.diary.extensions.toast

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<SignInViewModel>()

    private val googleSignInLauncher = registerForActivityResult(StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        viewModel.signInWithGoogle(task)
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@SignInActivity
            viewModel = this@SignInActivity.viewModel
        }
        setContentView(binding.root)

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
            viewModel.errorMessage.collect { this@SignInActivity.toast(it) }
        }
    }
}