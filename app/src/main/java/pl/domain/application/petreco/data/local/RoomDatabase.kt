package pl.domain.application.petreco.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.domain.application.petreco.data.model.RecognitionEntity

@Database(entities = [RecognitionEntity::class], version = 2)
abstract class RecognitionDatabase : RoomDatabase() {

    abstract fun recognitionDao(): RecognitionDao

    companion object {
        @Volatile
        private var INSTANCE: RecognitionDatabase? = null

        fun getDatabase(context: Context): RecognitionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecognitionDatabase::class.java,
                    "recognition_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
