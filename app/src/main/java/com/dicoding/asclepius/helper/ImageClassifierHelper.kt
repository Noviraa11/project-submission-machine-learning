package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageClassifierHelper(context: Context) {
    private var interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(context))
    }

    @Throws(IOException::class)
    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("cancer_classification.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyImage(bitmap: Bitmap): Pair<String, Float> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val tensorImage = TensorImage.fromBitmap(resizedBitmap)
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 2), org.tensorflow.lite.DataType.FLOAT32)
        interpreter.run(tensorImage.buffer, outputBuffer.buffer.rewind())
        val confidences = outputBuffer.floatArray
        val cancerConfidence = confidences[0]
        val nonCancerConfidence = confidences[1]
        return if (cancerConfidence > nonCancerConfidence) {
            "Cancer detected" to cancerConfidence
        } else {
            "No cancer detected" to nonCancerConfidence
        }
    }
}
