package pl.domain.application.petreco.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.support.image.TensorImage
import pl.domain.application.petreco.ml.AnimalModel

object RecognizeAnimal {
    fun loadLabels(context: Context): List<String> {
        return context.assets.open("labels.txt").bufferedReader().useLines { lines ->
            lines.toList()
        }
    }

    fun fromBitmap(context: Context, bitmap: Bitmap): Pair<String, Float> {
        return try {
            val model = AnimalModel.newInstance(context)

            val inputSize = 224
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

            val tensorImage = TensorImage.fromBitmap(resizedBitmap)
            tensorImage.load(resizedBitmap)

            val outputs = model.process(tensorImage.tensorBuffer)
            val outputBuffer = outputs.outputFeature0AsTensorBuffer
            val rawScores = outputBuffer.floatArray

            val scores = softmax(rawScores)

            val labels = loadLabels(context)
            val topK = scores.withIndex()
                .sortedByDescending { it.value }
                .take(5)
                .map { labels[it.index] to it.value }

            topK.forEach { Log.d("PREDICT", "${it.first}: ${it.second}") }

            val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1

            model.close()

            if (maxIndex != -1) {
                labels[maxIndex] to scores[maxIndex]
            } else {
                "null" to 0f
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Błąd rozpoznawania" to 0f
        }
    }

    fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: 0f
        val exps = logits.map { Math.exp((it - maxLogit).toDouble()) } // stabilizacja
        val sumExps = exps.sum()
        return exps.map { (it / sumExps).toFloat() }.toFloatArray()
    }

}
