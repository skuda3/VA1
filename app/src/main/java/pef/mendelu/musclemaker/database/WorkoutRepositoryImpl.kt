package pef.mendelu.musclemaker.database

import kotlinx.coroutines.flow.Flow
import pef.mendelu.musclemaker.model.Exercise
import pef.mendelu.musclemaker.model.ProgressPhoto
import pef.mendelu.musclemaker.model.WeightEntry
import pef.mendelu.musclemaker.model.Workout
import java.time.LocalDate
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val progressDao: ProgressDao
) : IWorkoutRepository {
    override fun getAllWorkouts(): Flow<List<Workout>> = workoutDao.getAllWorkouts()
    override fun getExercisesForWorkout(workoutId: Long): Flow<List<Exercise>> = workoutDao.getExercisesForWorkout(workoutId)
    override fun getWorkoutById(workoutId: Long): Flow<Workout?> = workoutDao.getWorkoutById(workoutId)
    override fun getExerciseById(exerciseId: Long): Flow<Exercise?> = workoutDao.getExerciseById(exerciseId)
    override suspend fun insertWorkout(workout: Workout): Long = workoutDao.insertWorkout(workout)
    override suspend fun updateWorkout(workout: Workout) = workoutDao.updateWorkout(workout)
    override suspend fun upsertExercise(exercise: Exercise) = workoutDao.upsertExercise(exercise)
    override suspend fun deleteExercise(exercise: Exercise) = workoutDao.deleteExercise(exercise)
    override suspend fun deleteWorkout(workout: Workout) = workoutDao.deleteWorkout(workout)
    override suspend fun updateWorkoutHasVideo(workoutId: Long, hasVideo: Boolean) = workoutDao.updateWorkoutHasVideo(workoutId, hasVideo)
    override suspend fun updateWorkoutCompletedStatus(workoutId: Long, isCompleted: Boolean) { workoutDao.updateWorkoutCompletedStatus(workoutId, isCompleted) }
    override fun getAllWeightEntries(): Flow<List<WeightEntry>> = progressDao.getAllWeightEntries()
    override suspend fun upsertWeight(weightEntry: WeightEntry) = progressDao.upsertWeight(weightEntry)
    override fun getPhotosForDate(date: LocalDate): Flow<List<ProgressPhoto>> = progressDao.getPhotosForDate(date)
    override suspend fun insertPhoto(photo: ProgressPhoto) = progressDao.insertPhoto(photo)
}