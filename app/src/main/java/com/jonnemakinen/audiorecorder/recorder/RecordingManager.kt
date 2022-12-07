package com.jonnemakinen.audiorecorder.recorder

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecordingManager() {

    private var audioSource: AudioSource = AudioSource.Default
        set(value) {
            field = value
            recordingState = recordingState.copy(audioSource = value)
        }

    private var sampleRate: Int = 48000
        set(value) {
            field = value
            recordingState = recordingState.copy(sampleRate = value)
        }

    private var channelCount: Int = 2
        set(value) {
            field = value
            recordingState = recordingState.copy(channelCount = value)
        }

    private var audioDevice: AudioDevice = AudioDevice(name = "Default", deviceId = -1)
        set(value) {
            field = value
            recordingState = recordingState.copy(audioDevice = value)
        }

    var recordingState by mutableStateOf(RecorderStateHolder(
        sampleRate = sampleRate,
        channelCount = channelCount,
        audioSource = audioSource,
        audioDevice = AudioDevice(name = "Default", deviceId = -1),
        startRecording = { startRecording() },
        stopRecording = { stopRecording() },
        setAudioSource = { audioSource = it },
        setSampleRate = { sampleRate = it },
        setChannelCount = { channelCount = it },
        selectAudioDevice = { audioDevice = it }
    ))

    private var audioRecording: AudioRecorder? = null
    private lateinit var audioManager: AudioManager

    lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioDevices = ArrayList<AudioDevice>()
        audioDevices.add(AudioDevice(name = "Default", deviceId = -1))
        audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS).forEach {
            audioDevices.add(
                AudioDevice(
                    name = deviceTypeToString(it.type) + " " + it.id,
                    deviceId = it.id
                )
            )
        }
        recordingState = recordingState.copy(audioDevices = audioDevices)
    }

    private val recordingListener = object : AudioRecorder.Listener {
        override fun onStateChanged(state: RecordingState) {
            CoroutineScope(Dispatchers.Main).launch {
                var newState = recordingState.copy(state = state)
                if (state == RecordingState.Idle) {
                    newState = newState.copy(elapsedTimeMs = 0)
                }
                recordingState = newState
            }
        }

        override fun onRecordingTimeChanged(elapsedTimeMs: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                recordingState = recordingState.copy(elapsedTimeMs = elapsedTimeMs)
            }
        }
    }

    private fun startRecording() {
        if (audioRecording == null) {
            val filename = generateFilename()
            recordingState = recordingState.copy(recording = true, filename = filename)
            val preferredDevice = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
                .find { it.id == audioDevice.deviceId }
            audioRecording = AudioRecorder(
                context = context,
                sampleRate = sampleRate,
                audioSource = audioSource,
                channelCount = channelCount,
                preferredDevice = preferredDevice,
                outputFilename = filename,
                folderPath = Environment.DIRECTORY_RECORDINGS,
                listener = recordingListener
            ).apply {
                start()
            }
        }
    }

    private fun stopRecording() {
        recordingState = recordingState.copy(recording = false)
        audioRecording?.stop()
        audioRecording = null
    }

    private fun generateFilename(): String {
        val time = SimpleDateFormat("yyyyMMdd-HHmmss").format(Calendar.getInstance().time)
        return time + "-" + recordingState.channelCount + "ch-" + recordingState.audioSource + "-" + recordingState.sampleRate
    }

    private fun deviceTypeToString(type: Int): String {
        when (type) {
            AudioDeviceInfo.TYPE_UNKNOWN -> return "TYPE_UNKNOWN"
            AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> return "TYPE_BUILTIN_EARPIECE"
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> return "TYPE_BUILTIN_SPEAKER"
            AudioDeviceInfo.TYPE_WIRED_HEADSET -> return "TYPE_WIRED_HEADSET"
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> return "TYPE_WIRED_HEADPHONES"
            AudioDeviceInfo.TYPE_LINE_ANALOG -> return "TYPE_LINE_ANALOG"
            AudioDeviceInfo.TYPE_LINE_DIGITAL -> return "TYPE_LINE_DIGITAL"
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> return "TYPE_BLUETOOTH_SCO"
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> return "TYPE_BLUETOOTH_A2DP"
            AudioDeviceInfo.TYPE_HDMI -> return "TYPE_HDMI"
            AudioDeviceInfo.TYPE_HDMI_ARC -> return "TYPE_HDMI_ARC"
            AudioDeviceInfo.TYPE_USB_DEVICE -> return "TYPE_USB_DEVICE"
            AudioDeviceInfo.TYPE_USB_ACCESSORY -> return "TYPE_USB_ACCESSORY"
            AudioDeviceInfo.TYPE_DOCK -> return "TYPE_DOCK"
            AudioDeviceInfo.TYPE_FM -> return "TYPE_FM"
            AudioDeviceInfo.TYPE_BUILTIN_MIC -> return "TYPE_BUILTIN_MIC"
            AudioDeviceInfo.TYPE_FM_TUNER -> return "TYPE_FM_TUNER"
            AudioDeviceInfo.TYPE_TV_TUNER -> return "TYPE_TV_TUNER"
            AudioDeviceInfo.TYPE_TELEPHONY -> return "TYPE_TELEPHONY"
            AudioDeviceInfo.TYPE_AUX_LINE -> return "TYPE_AUX_LINE"
            AudioDeviceInfo.TYPE_IP -> return "TYPE_IP"
            AudioDeviceInfo.TYPE_BUS -> return "TYPE_BUS"
            AudioDeviceInfo.TYPE_USB_HEADSET -> return "TYPE_USB_HEADSET"
            AudioDeviceInfo.TYPE_HEARING_AID -> return "TYPE_HEARING_AID"
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER_SAFE -> return "TYPE_BUILTIN_SPEAKER_SAFE"
            AudioDeviceInfo.TYPE_REMOTE_SUBMIX -> return "TYPE_REMOTE_SUBMIX"
            AudioDeviceInfo.TYPE_BLE_HEADSET -> return "TYPE_BLE_HEADSET"
            AudioDeviceInfo.TYPE_BLE_SPEAKER -> return "TYPE_BLE_SPEAKER"
            AudioDeviceInfo.TYPE_HDMI_EARC -> return "TYPE_HDMI_EARC"
            AudioDeviceInfo.TYPE_BLE_BROADCAST -> return "TYPE_BLE_BROADCAST"

            else -> return "NOT_DEFINED"
        }

    }

}