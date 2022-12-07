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

@Composable
fun RecordingSettingsView(recordingState: RecorderStateHolder, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp),
            text = "Files will be saved in the Recordings folder"
        )
        Column(modifier = Modifier.align(Alignment.Center)) {

            val gridModifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
            AudioSourcePicker(
                state = recordingState, modifier = gridModifier
            )
            SampleRatePicker(
                state = recordingState, modifier = gridModifier
            )
            AudioDevicePicker(
                state = recordingState, modifier = gridModifier
            )
            ChannelCountPicker(state = recordingState, modifier = gridModifier)

        }
        Button(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 30.dp),
            onClick = { recordingState.startRecording() }) {
            Text("Start")
        }
    }

}

@Composable
@Preview
fun RecordingSettingsViewPreview() {
    RecordingSettingsView(RecorderStateHolder())
}