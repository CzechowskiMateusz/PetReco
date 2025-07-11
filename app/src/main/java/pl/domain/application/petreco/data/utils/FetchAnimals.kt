package pl.domain.application.petreco.data.utils

import pl.domain.application.petreco.data.api.RetrofitApi
import pl.domain.application.petreco.data.model.Animal

suspend fun fetchAnimals(token: String, types: List<String>, page: Int): List<Animal> {
    val all = mutableListOf<Animal>()
    for (type in types) {
        val response = RetrofitApi.animalApi.getAnimalsByType(token, type, page, limit = 10)
        all += response.animals
    }
    return all.shuffled()
}
