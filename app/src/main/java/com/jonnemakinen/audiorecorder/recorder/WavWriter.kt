package com.jonnemakinen.audiorecorder.recorder

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavWriter(
    context: Context,
    private val channelCount: Int,
    private val sampleRate: Int,
    private val bitsPerSample: Int
) {
    private val tempFile = File.createTempFile("temp.wav", null, context.cacheDir)
    private val tempOutputStream = FileOutputStream(tempFile)

    private var totalWrittenData: Int = 0
    private val bytesPerSecond = sampleRate * channelCount * bitsPerSample / 8
    val dataWrittenInMs: Long
        get() = 1000L * totalWrittenData / bytesPerSecond

    fun addData(data: ByteArray) {
        tempOutputStream.write(data)
        totalWrittenData += data.size
    }

    fun writeToStream(outputStream: OutputStream) {
        tempOutputStream.close()
        val inputStream = FileInputStream(tempFile)
        outputStream.write(createWavHeader(totalWrittenData))
        val copyBuffer = ByteArray(2048)
        while (true) {
            val bytesRead = inputStream.read(copyBuffer, 0, copyBuffer.size)
            if (bytesRead == -1) break
            outputStream.write(copyBuffer, 0, bytesRead)
        }
        inputStream.close()
        tempFile.delete()
        outputStream.close()
    }


    private fun createWavHeader(totalSize: Int): ByteArray {
        val header = ByteBuffer.allocateDirect(44)
        header.order(ByteOrder.BIG_ENDIAN)
        header.putInt(0x52494646)
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(totalSize + 36)
        header.order(ByteOrder.BIG_ENDIAN)
        header.putInt(0x57415645)
        header.putInt(0x666d7420)
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(16)
        header.putShort(1.toShort())
        header.putShort(channelCount.toShort())
        header.putInt(sampleRate)
        header.putInt(sampleRate * channelCount * bitsPerSample / 8)
        header.putShort((channelCount * bitsPerSample / 8).toShort())
        header.putShort(bitsPerSample.toShort())
        header.order(ByteOrder.BIG_ENDIAN)
        header.putInt(0x64617461)
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(totalSize)
        val headerArray = ByteArray(44)
        header.rewind()
        header.get(headerArray)
        return headerArray
    }
}