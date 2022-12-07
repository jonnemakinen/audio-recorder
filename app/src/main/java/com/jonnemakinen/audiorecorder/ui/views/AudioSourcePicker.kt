package com.jonnemakinen.audiorecorder.ui.views

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jonnemakinen.audiorecorder.recorder.RecorderStateHolder
import com.jonnemakinen.audiorecorder.recorder.AudioSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSourcePicker(modifier: Modifier = Modifier, state: RecorderStateHolder) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = state.audioSource.name,
            onValueChange = {},
            label = { Text("Audio source") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            AudioSource.values().forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        state.setAudioSource(it)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
