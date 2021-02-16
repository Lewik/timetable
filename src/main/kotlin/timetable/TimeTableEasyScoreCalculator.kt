package timetable

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator

class TimeTableEasyScoreCalculator : EasyScoreCalculator<TimeTable, HardSoftScore> {
    override fun calculateScore(timeTable: TimeTable): HardSoftScore {
        val lessonList = timeTable.lessonList
        var hardScore = 0

        val nulls = lessonList.count { it.timeslot == null }
        repeat(nulls) { hardScore-- }

        if (nulls == 0) {
            lessonList.forEach { a ->
                lessonList.forEach { b ->
                    if (a.id != b.id) {
                        if (a.timeslot != null && a.timeslot == b.timeslot) {
                            if (a.teacher == b.teacher) {
//                                hardScore--
                            }
                            if (a.studentGroup == b.studentGroup) {
                                hardScore--
                            }
                        }
                    }
                }
            }

            val lessonsGroups = lessonList
                .groupBy { it.studentGroup }

            lessonsGroups
                .forEach { (group, groupLessons) ->
                    val groupLessonsByDay = groupLessons
                        .groupBy { it.timeslot!!.day }

                    if (workingDays.minus(groupLessonsByDay.keys).isNotEmpty()) {
                        hardScore--
                    }

                    groupLessonsByDay
                        .forEach { (day, dayGroupLesions) ->
                            if (dayGroupLesions.isEmpty()) {
                                hardScore--
                            }
                            val numbers = dayGroupLesions.map { it.timeslot!!.number }
                            if (1 !in numbers) {
                                hardScore--
                            }
                            val lastNumber = numbers.maxOrNull()!!
                            if ((1..lastNumber)
                                    .toSet()
                                    .minus(numbers).isNotEmpty()
                            ) {
                                hardScore--
                            }
                        }
                }
        }

        val softScore = 0
        // Soft constraints are only implemented in the "complete" implementation
        return HardSoftScore.of(hardScore, softScore)
    }
}
