package com.jonnemakinen.audiorecorder.recorder

data class RecorderStateHolder(
    val state: RecordingState = RecordingState.Idle,
    val filename: String = "",
    val elapsedTimeMs: Int = 0,
    val channelCounts: List<Int> = listOf(1,2,3,4,5,6),
    val channelCount: Int = 3,
    val setChannelCount: (Int) -> Unit = {},
    val sampleRates: List<Int> = listOf(8000, 16000, 24000, 32000, 44100, 48000),
    val audioDevices: List<AudioDevice> = listOf(),
    val audioDevice: AudioDevice = AudioDevice(),
    val selectAudioDevice: (AudioDevice) -> Unit = {},
    val recording: Boolean = false,
    val startRecording: () -> Unit = {},
    val stopRecording: () -> Unit = {},
    val recordingDuration: Int = 0,
    val setAudioSource: (AudioSource) -> Unit = { },
    val audioSource: AudioSource = AudioSource.Default,
    val sampleRate: Int = 48000,
    val setSampleRate: (Int) -> Unit = {},
    )