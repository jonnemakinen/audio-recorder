package com.jonnemakinen.audiorecorder

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.AppTheme
import com.jonnemakinen.audiorecorder.recorder.RecordingManager

class MainActivity : ComponentActivity() {

    private val recordingManager = RecordingManager()

    private var permissionsGranted by mutableStateOf(false)

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            var permissionsGranted = true
            it.forEach {
                if (!it.value) {
                    permissionsGranted = false
                }
            }
            this.permissionsGranted = permissionsGranted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO
            )
        )
        recordingManager.initialize(this)
        setContent {
            MainContent()
        }
    }

    @Composable
    private fun MainContent() {
        AppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                RecordingView(
                    state = recordingManager.recordingState,
                    permissionsGranted = permissionsGranted
                )
            }
        }
    }
}
