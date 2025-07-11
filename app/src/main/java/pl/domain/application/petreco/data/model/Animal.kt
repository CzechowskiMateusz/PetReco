package pl.domain.application.petreco.data.model

data class AllAnimal(
    val animals: List<Animal>
)

data class Animal(
    val id: Int,
    val name: String,
    val type: String?,
    val species: String?,
    val age: String?,
    val gender: String?,
    val description: String?,
    val breeds: Breed?,
    val photos: List<Photo> = emptyList()
)

data class Photo(
    val medium: String,
    val large: String
)

data class Breed(
    val primary: String?,
    val secondary: String?,
    val mixed: Boolean,
    val unknown: Boolean
)