package pl.domain.application.petreco.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recognitions")
data class RecognitionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val imageUri: String,
    val recognizedAnimal: String,
    val confidence: Float
)

