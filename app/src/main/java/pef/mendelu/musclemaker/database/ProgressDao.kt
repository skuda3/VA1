package pef.mendelu.musclemaker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pef.mendelu.musclemaker.model.ProgressPhoto
import pef.mendelu.musclemaker.model.WeightEntry
import java.time.LocalDate

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeight(weightEntry: WeightEntry)

    @Query("SELECT * FROM weight_entries ORDER BY date ASC")
    fun getAllWeightEntries(): Flow<List<WeightEntry>>

    @Insert
    suspend fun insertPhoto(photo: ProgressPhoto)

    @Query("SELECT * FROM progress_photos WHERE date = :date")
    fun getPhotosForDate(date: LocalDate): Flow<List<ProgressPhoto>>
}