package pef.mendelu.musclemaker.database

import kotlinx.coroutines.flow.Flow
import pef.mendelu.musclemaker.model.Exercise
import pef.mendelu.musclemaker.model.ProgressPhoto
import pef.mendelu.musclemaker.model.WeightEntry
import pef.mendelu.musclemaker.model.Workout
import java.time.LocalDate

interface IWorkoutRepository {
    fun getAllWorkouts(): Flow<List<Workout>>
    fun getExercisesForWorkout(workoutId: Long): Flow<List<Exercise>>
    fun getWorkoutById(workoutId: Long): Flow<Workout?>
    fun getExerciseById(exerciseId: Long): Flow<Exercise?>
    suspend fun deleteWorkout(workout: Workout)
    suspend fun insertWorkout(workout: Workout): Long
    suspend fun updateWorkout(workout: Workout)
    suspend fun upsertExercise(exercise: Exercise)
    suspend fun deleteExercise(exercise: Exercise)
    suspend fun updateWorkoutHasVideo(workoutId: Long, hasVideo: Boolean)
    suspend fun updateWorkoutCompletedStatus(workoutId: Long, isCompleted: Boolean)
    fun getAllWeightEntries(): Flow<List<WeightEntry>>
    suspend fun upsertWeight(weightEntry: WeightEntry)
    fun getPhotosForDate(date: LocalDate): Flow<List<ProgressPhoto>>
    suspend fun insertPhoto(photo: ProgressPhoto)
}