package pef.mendelu.musclemaker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pef.mendelu.musclemaker.database.ProgressDao
import pef.mendelu.musclemaker.database.WorkoutDatabase
import pef.mendelu.musclemaker.database.WorkoutDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWorkoutDatabase(@ApplicationContext context: Context): WorkoutDatabase {
        return Room.databaseBuilder(
            context,
            WorkoutDatabase::class.java,
            "muscle_maker_database"
        ).build()
    }

    @Provides
    fun provideWorkoutDao(database: WorkoutDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideProgressDao(database: WorkoutDatabase): ProgressDao {
        return database.progressDao()
    }
}