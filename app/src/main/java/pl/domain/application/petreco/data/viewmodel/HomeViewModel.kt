package pl.domain.application.petreco.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.domain.application.petreco.data.api.RetrofitApi
import pl.domain.application.petreco.data.model.Animal
import pl.domain.application.petreco.data.utils.TokenProvider
import pl.domain.application.petreco.data.utils.fetchAnimals

class HomeViewModel : ViewModel() {

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _randomTypes = MutableStateFlow<List<String>>(emptyList())
    private var page = 1

    fun setError(message: String?) {
        _errorMessage.value = message
    }

    init {
        loadInitialAnimals()
    }

    private fun loadInitialAnimals() {
        viewModelScope.launch {
            try {
                val token = TokenProvider.fetchToken()
                val types = RetrofitApi.animalApi.getAnimalTypes(token).types.map { it.name }
                _randomTypes.value = types.shuffled().take(3)

                val fetched = fetchAnimals(token, _randomTypes.value, page)
                _animals.value = fetched.filter { it.photos.isNotEmpty() }
            } catch (e: Exception) {
                _errorMessage.value = "Błąd: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        if (_isLoading.value) return
        _isLoading.value = true
        page++
        viewModelScope.launch {
            try {
                val token = TokenProvider.fetchToken()
                val more = fetchAnimals(token, _randomTypes.value, page)
                    .filter { it.photos.isNotEmpty() }
                _animals.value = _animals.value + more
            } catch (e: Exception) {
                _errorMessage.value = "Błąd ładowania kolejnych: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
