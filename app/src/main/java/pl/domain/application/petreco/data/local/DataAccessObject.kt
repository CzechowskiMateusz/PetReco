package pl.domain.application.petreco.data.local

import androidx.room.*
import pl.domain.application.petreco.data.model.RecognitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecognitionDao {

    @Query("SELECT * FROM recognitions WHERE userId = :userId AND recognizedAnimal = :animal AND imageUri = :imageUri LIMIT 1")
    suspend fun getRecognition(userId: String, animal: String, imageUri: String): RecognitionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecognition(recognition: RecognitionEntity)

    @Delete
    suspend fun deleteRecognition(recognition: RecognitionEntity)

    @Query("SELECT * FROM recognitions WHERE userId = :userId")
    fun getAllForUser(userId: String): Flow<List<RecognitionEntity>>
}