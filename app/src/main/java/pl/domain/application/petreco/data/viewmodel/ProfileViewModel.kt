package pl.domain.application.petreco.data.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.domain.application.petreco.data.local.RecognitionDatabase
import pl.domain.application.petreco.data.model.RecognitionEntity

class ProfileViewModel(application: Application, private val userId: String) : AndroidViewModel(application) {

    private val dao = RecognitionDatabase.getDatabase(application).recognitionDao()

    val recognitions: StateFlow<List<RecognitionEntity>> =
        dao.getAllForUser(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteFavourite(context: Context, recognition: RecognitionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                RecognitionDatabase.getDatabase(context).recognitionDao().deleteRecognition(recognition)
            } catch (e: Exception) {
                Log.e("ProfileVM", "Delete error", e)
            }
        }
    }
}
