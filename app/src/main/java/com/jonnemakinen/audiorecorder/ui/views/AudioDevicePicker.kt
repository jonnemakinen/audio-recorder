package com.jonnemakinen.audiorecorder.ui.views

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jonnemakinen.audiorecorder.recorder.RecorderStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioDevicePicker(modifier: Modifier = Modifier, state: RecorderStateHolder) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = state.audioDevice.name,
            onValueChange = {},
            label = { Text("Audio device") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            state.audioDevices.forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        state.selectAudioDevice(it)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
