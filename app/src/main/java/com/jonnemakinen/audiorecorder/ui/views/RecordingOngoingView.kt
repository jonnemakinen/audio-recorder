package com.jonnemakinen.audiorecorder.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jonnemakinen.audiorecorder.recorder.RecorderStateHolder

fun millisecondsToTimeString(durationInMS: Int): String {
    val durationInSeconds = durationInMS / 1000
    val builder = StringBuilder()
    val minutes = durationInSeconds / 60
    val seconds = if (durationInSeconds < 60) durationInSeconds else durationInSeconds % 60
    if (minutes < 10) {
        builder.append(0)
    }
    builder.append(minutes)
    builder.append(":")
    if (seconds < 10) {
        builder.append(0)
    }
    builder.append(seconds)
    return builder.toString()
}

@Composable
fun RecordingOngoingView(state: RecorderStateHolder, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Recording to file:")
            Text(state.filename)
            Text("Elapsed time: " + millisecondsToTimeString(state.elapsedTimeMs))
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            onClick = { state.stopRecording() }) {
            Text("Stop")
        }
    }

}

@Composable
@Preview
fun RecordingOngoingViewPreview() {
    RecordingOngoingView(state = RecorderStateHolder())
}