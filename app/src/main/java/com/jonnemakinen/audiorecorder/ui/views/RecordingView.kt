package com.jonnemakinen.audiorecorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jonnemakinen.audiorecorder.recorder.RecorderStateHolder
import com.jonnemakinen.audiorecorder.recorder.RecordingState
import com.jonnemakinen.audiorecorder.ui.views.RecordingOngoingView
import com.jonnemakinen.audiorecorder.ui.views.RecordingSettingsView


@Composable
fun RecordingView(state: RecorderStateHolder) {
    Box(modifier = Modifier.fillMaxSize()) {

        if (state.state == RecordingState.Recording) {
            RecordingOngoingView(state = state)
        } else if (state.state == RecordingState.Idle) {
            RecordingSettingsView(recordingState = state)
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

    }
}

@Composable
@Preview
fun RecordingViewPreview() {
    RecordingView(RecorderStateHolder())
}