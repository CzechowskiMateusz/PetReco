package pl.domain.application.petreco.data.model

data class AnimalTypesResponse(
    val types: List<AnimalType>
)

data class AnimalType(
    val name: String
)
