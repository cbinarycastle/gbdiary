package com.casoft.gbdiary.ui.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.casoft.gbdiary.extensions.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton

@Composable
fun SignInScreen(viewModel: SignInViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val signInClient = viewModel.googleSignInClient

    val googleSignInLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        viewModel.signInWithGoogle(task)
    }

    val account by viewModel.account.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.signInNavigationAction.collect {
            googleSignInLauncher.launch(signInClient.signInIntent)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.errorMessage.collect { context.toast(it) }
    }

    Box(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Text(
            text = if (account == null) "로그인 실패" else "로그인 성공",
            modifier = Modifier.align(Alignment.Center),
        )
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