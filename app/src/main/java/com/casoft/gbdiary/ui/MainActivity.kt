package com.casoft.gbdiary.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            GBDiaryApp(finishActivity = this::finish)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkExistingSignedInUser()
    }

    override fun onResume() {
        super.onResume()
        viewModel.queryPurchases()
    }
}