package com.jonnemakinen.audiorecorder.recorder

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.provider.MediaStore
import android.util.Log

@SuppressLint("MissingPermission")
class AudioRecorder(private val context: Context,
                    private val sampleRate: Int,
                    audioSource: AudioSource,
                    private val channelCount: Int,
                    preferredDevice: AudioDeviceInfo?,
                    private val folderPath: String,
                    private val outputFilename: String,
                    private val listener: Listener
                    ) {
    private var audioRecordingThread: Thread? = null
    private val wavWriter: WavWriter
    private val audioRecord: AudioRecord

    private var doStop = false
    private var lastUpdatedElapsedTime = -9999

    interface Listener {
        fun onStateChanged(state: RecordingState)
        fun onRecordingTimeChanged(elapsedTimeMs: Int)
    }

    init {
        Log.d(TAG, "Creating audio recorder with:")
        Log.d(TAG, "Sample rate: $sampleRate")
        Log.d(TAG, "Channel count: $channelCount")
        Log.d(TAG, "Audio source: $audioSource")
        Log.d(TAG, "Preferred device: " + preferredDevice?.id)
        val audioInputFormat = AudioFormat.Builder().apply {
            setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            setSampleRate(sampleRate)
            when (channelCount) {
                2 -> setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
                1 -> setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                else -> {
                    val channelIndexMask = (1 shl channelCount) - 1
                    setChannelIndexMask(channelIndexMask)
                }
            }

        }.build()

        val source = when (audioSource) {
            AudioSource.Default -> MediaRecorder.AudioSource.DEFAULT
            AudioSource.Camcorder -> MediaRecorder.AudioSource.CAMCORDER
            AudioSource.Mic -> MediaRecorder.AudioSource.MIC
            AudioSource.Unprocessed -> MediaRecorder.AudioSource.UNPROCESSED
            AudioSource.VoiceCommunication -> MediaRecorder.AudioSource.VOICE_COMMUNICATION
            AudioSource.VoiceRecognition -> MediaRecorder.AudioSource.VOICE_RECOGNITION
        }
        audioRecord =
            AudioRecord.Builder().setAudioSource(source).setAudioFormat(audioInputFormat).build()
        audioRecord.preferredDevice = preferredDevice
        wavWriter = WavWriter(
            bitsPerSample = 16,
            channelCount = channelCount,
            sampleRate = sampleRate,
            context = context
        )
    }

    private fun saveWavFile() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, outputFilename + ".wav")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav")
            put(MediaStore.Audio.AudioColumns.RELATIVE_PATH, folderPath)
        }
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(contentUri, contentValues)
        if (uri != null) {
            contentResolver.openOutputStream(uri)?.let {
                wavWriter.writeToStream(it)
                it.close()
            }
        }
    }

    fun start() {
        if (audioRecordingThread == null) {
            audioRecordingThread = Thread({
                doStop = false
                audioRecord.startRecording()
                while (!doStop) {
                    val sizeInBytes = channelCount * 2 * 1024
                    val audioBuffer = ByteArray(sizeInBytes)
                    audioRecord.read(audioBuffer, 0, sizeInBytes)
                    wavWriter.addData(audioBuffer)
                    val elapsedTimeMs = wavWriter.dataWrittenInMs
                    if (elapsedTimeMs - ELAPSED_TIME_INTERVAL > lastUpdatedElapsedTime) {
                        listener.onRecordingTimeChanged(elapsedTimeMs.toInt())
                        lastUpdatedElapsedTime = elapsedTimeMs.toInt()
                    }
                }
                audioRecord.stop()
                audioRecord.release()
                saveWavFile()
                listener.onStateChanged(RecordingState.Idle)
            }, "AudioRecordingThread").apply { start() }
            listener.onStateChanged(RecordingState.Recording)
        }
    }

    fun stop() {
        if (!doStop) {
            listener.onStateChanged(RecordingState.Saving)
            doStop = true
        }
    }
    companion object {
        private const val TAG = "AudioRecorder"
        private const val ELAPSED_TIME_INTERVAL = 500
    }
}