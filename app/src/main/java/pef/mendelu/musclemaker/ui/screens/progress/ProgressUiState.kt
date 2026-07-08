package pef.mendelu.musclemaker.ui.screens.progress

import pef.mendelu.musclemaker.model.ProgressPhoto
import pef.mendelu.musclemaker.model.WeightEntry
import java.time.LocalDate

data class ProgressUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weightHistory: List<WeightEntry> = emptyList(),
    val photosForSelectedDate: List<ProgressPhoto> = emptyList(),
    val isDatePickerVisible: Boolean = false
)