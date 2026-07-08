package pef.mendelu.musclemaker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey
    val date: LocalDate,
    val weight: Float
)