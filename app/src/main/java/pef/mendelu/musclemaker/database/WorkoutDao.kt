package pef.mendelu.musclemaker.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pef.mendelu.musclemaker.model.Workout
import pef.mendelu.musclemaker.model.Exercise

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY id DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM exercises WHERE workoutOwnerId = :workoutId")
    fun getExercisesForWorkout(workoutId: Long): Flow<List<Exercise>>

    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExercise(exercise: Exercise)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkoutById(workoutId: Long): Flow<Workout?>

    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    fun getExerciseById(exerciseId: Long): Flow<Exercise?>

    @Query("UPDATE workouts SET hasVideo = :hasVideo WHERE id = :workoutId")
    suspend fun updateWorkoutHasVideo(workoutId: Long, hasVideo: Boolean)

    @Query("UPDATE workouts SET isCompleted = :isCompleted WHERE id = :workoutId")
    suspend fun updateWorkoutCompletedStatus(workoutId: Long, isCompleted: Boolean)
}