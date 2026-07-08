package pef.mendelu.musclemaker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pef.mendelu.musclemaker.database.WorkoutDao
import pef.mendelu.musclemaker.model.Exercise
import pef.mendelu.musclemaker.model.ProgressPhoto
import pef.mendelu.musclemaker.model.WeightEntry
import pef.mendelu.musclemaker.model.Workout

@Database(
    entities = [Workout::class, Exercise::class, WeightEntry::class, ProgressPhoto::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun progressDao(): ProgressDao
}