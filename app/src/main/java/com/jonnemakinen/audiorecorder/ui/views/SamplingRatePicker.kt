package com.jonnemakinen.audiorecorder.ui.views

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jonnemakinen.audiorecorder.recorder.RecorderStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleRatePicker(modifier: Modifier = Modifier, state: RecorderStateHolder) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = "" + state.sampleRate,
            onValueChange = {},
            label = { Text("Sample rate") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            state.sampleRates.forEach {
                DropdownMenuItem(
                    text = { Text(""+it) },
                    onClick = {
                        state.setSampleRate(it)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
