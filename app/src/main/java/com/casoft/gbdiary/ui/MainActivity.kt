package com.casoft.gbdiary.ui

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationEnabled.collect { notificationEnabled ->
                    if (notificationEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permission = android.Manifest.permission.POST_NOTIFICATIONS
                        val permissionGranted = ContextCompat.checkSelfPermission(
                            this@MainActivity, permission
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!permissionGranted) {
                            viewModel.disableNotification()
                        }
                    }
                }
            }
        }

        setContent {
            GBDiaryApp(finishActivity = this::finish)
        }
    }

    override fun onStart() {
        super.onStart()
        with(viewModel) {
            checkInactivityTimeout()
            checkExistingSignedInUser()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.queryPurchases()
    }

    override fun onStop() {
        super.onStop()
        viewModel.turnToInactivity()
    }
}