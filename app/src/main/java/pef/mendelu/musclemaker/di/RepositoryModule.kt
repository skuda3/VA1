package pef.mendelu.musclemaker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pef.mendelu.musclemaker.database.IWorkoutRepository
import pef.mendelu.musclemaker.database.WorkoutRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): IWorkoutRepository
}