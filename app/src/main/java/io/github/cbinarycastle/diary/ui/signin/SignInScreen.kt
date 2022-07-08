package io.github.cbinarycastle.diary.ui.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import io.github.cbinarycastle.diary.R
import io.github.cbinarycastle.diary.extensions.toast

@Composable
fun SignInScreen(viewModel: SignInViewModel = viewModel()) {
    val context = LocalContext.current

    val googleSignInClient = remember(context) {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder()
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        viewModel.signInWithGoogle(task)
    }

    val user by viewModel.user.collectAsState()

    LaunchedEffect(true) {
        viewModel.signInNavigationAction.collect {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    LaunchedEffect(true) {
        viewModel.errorMessage.collect { context.toast(it) }
    }

    Box(Modifier.fillMaxSize()) {
        user?.let {
            Text(
                text = it.email,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        AndroidView(
            factory = { context ->
                SignInButton(context).apply {
                    setOnClickListener { viewModel.onSignInClick() }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}