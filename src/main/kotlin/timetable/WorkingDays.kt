package timetable

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

val workingDays = DayOfWeek
    .values()
    .dropLast(2)
    .map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
