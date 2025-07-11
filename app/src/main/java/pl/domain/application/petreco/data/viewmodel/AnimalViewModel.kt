package pl.domain.application.petreco.data.viewmodel

import android.R
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.domain.application.petreco.data.utils.RecognizeAnimal
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import pl.domain.application.petreco.data.local.RecognitionDatabase
import pl.domain.application.petreco.data.model.RecognitionEntity

class RecoViewModel : ViewModel() {

    private val _selectedBitmap = mutableStateOf<Bitmap?>(null)
    val selectedBitmap: State<Bitmap?> get() = _selectedBitmap

    private val _recognizedAnimal = mutableStateOf("null")
    val recognizedAnimal: State<String> get() = _recognizedAnimal

    private val _recognitionConfidence = mutableStateOf<Float?>(null)
    val recognitionConfidence: State<Float?> get() = _recognitionConfidence

    private val _showLoading = mutableStateOf(false)
    val showLoading: State<Boolean> get() = _showLoading

    private val _currentImageUri = mutableStateOf("")
    val currentImageUri: State<String> = _currentImageUri

    private val _isFavorite = mutableStateOf(false)
    val isFavorite: State<Boolean> get() = _isFavorite

    fun setisFavorite(b: Boolean) {
        _isFavorite.value = b
    }
    fun setCurrentImageUri(uri: String) {
        _currentImageUri.value = uri
    }

    fun recognizeFromBitmap(context: Context, bitmap: Bitmap, imageUri: String = "", userId: String) {
        _showLoading.value = true
        viewModelScope.launch {
            val (label, confidence) = withContext(Dispatchers.Default) {
                RecognizeAnimal.fromBitmap(context, bitmap)
            }
            _selectedBitmap.value = bitmap
            _recognizedAnimal.value = label
            _recognitionConfidence.value = confidence
            _showLoading.value = false
            setCurrentImageUri(imageUri)
            checkIfFavorite(context, userId)

        }
    }

    fun checkIfFavorite(context: Context, userId: String) {
        val imageUriValue = currentImageUri.value
        val animalValue = recognizedAnimal.value

        if (imageUriValue.isBlank() || animalValue.isBlank() || userId.isBlank()) {
            _isFavorite.value = false
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val existing = RecognitionDatabase.getDatabase(context).recognitionDao()
                .getRecognition(userId, animalValue, imageUriValue)
            withContext(Dispatchers.Main) {
                _isFavorite.value = existing != null
            }
        }
    }

    fun saveFavorite(context: Context, userId: String) {
        val animal = _recognizedAnimal.value
        val confidence = _recognitionConfidence.value ?: return
        val bitmapUri = currentImageUri.value.ifBlank { return }
        if (userId.isBlank()) return

        val recognition = RecognitionEntity(
            userId = userId,
            imageUri = bitmapUri,
            recognizedAnimal = animal,
            confidence = confidence
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                RecognitionDatabase.getDatabase(context).recognitionDao().insertRecognition(recognition)
                withContext(Dispatchers.Main) {
                    _isFavorite.value = true
                }
            } catch (e: Exception) {
                Log.e("RecoDebug", "Insert error", e)
            }
        }
    }

    fun deleteFavorite(context: Context, userId: String) {
        val animal = _recognizedAnimal.value
        val confidence = _recognitionConfidence.value ?: return
        val bitmapUri = currentImageUri.value.ifBlank { return }
        if (userId.isBlank()) return

        val recognition = RecognitionEntity(
            userId = userId,
            imageUri = bitmapUri,
            recognizedAnimal = animal,
            confidence = confidence
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                RecognitionDatabase.getDatabase(context).recognitionDao().deleteRecognition(recognition)
                withContext(Dispatchers.Main) {
                    _isFavorite.value = false
                }
            } catch (e: Exception) {
                Log.e("RecoDebug", "Delete failed", e)
            }
        }
    }

    fun setrecognizedAnimal(value: String) {
        _recognizedAnimal.value = value
    }

    fun setrecognitionConfidence(value: Float?) {
        _recognitionConfidence.value = value
    }

    fun setLoading(value: Boolean) {
        _showLoading.value = value
    }

    fun reset() {
        _selectedBitmap.value = null
        _recognizedAnimal.value = "Nie rozpoznano"
        _recognitionConfidence.value = null
        _showLoading.value = false
    }

}




