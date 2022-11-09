//package com.casoft.gbdiary.ui.lock
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import com.casoft.gbdiary.util.biometricAuthenticate
//
//@Composable
//fun ScreenLock(
//    password: String?,
//    biometricAuthenticationEnabled: Boolean,
//) {
//    val context = LocalContext.current
//
//    LaunchedEffect(biometricAuthenticationEnabled) {
//        if (biometricAuthenticationEnabled) {
//            context.biometricAuthenticate()
//        }
//    }
//
//    Box(Modifier.fillMaxSize()) {
//        if (password != null) {
//
//        }
//    }
//}