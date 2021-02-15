package timetable

import kotlinx.serialization.Serializable
import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.domain.variable.PlanningVariable
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator
import org.optaplanner.core.api.score.stream.ConstraintCollectors
import org.optaplanner.core.api.score.stream.ConstraintFactory
import org.optaplanner.core.api.score.stream.ConstraintProvider
import org.optaplanner.core.api.score.stream.Joiners
import org.optaplanner.core.api.solver.SolverManager
import org.optaplanner.core.api.solver.SolverStatus
import org.optaplanner.core.config.solver.SolverConfig
import java.time.DayOfWeek
import java.time.Duration
import java.time.format.TextStyle
import java.util.*


@Target(AnnotationTarget.CLASS)
annotation class NoArg

@Serializable
@NoArg
@PlanningEntity
data class Lesson(
    val subject: String,
    val teacher: String,
    val studentGroup: String,
    @PlanningVariable(valueRangeProviderRefs = ["timeslotRange"])
    var timeslot: TimeSlot? = null,
    @PlanningId
    val id: String = UUID.randomUUID().toString(),
)

@Serializable
data class TimeSlot(
    val day: String,
    val number: Int,
)


@NoArg
@PlanningSolution
data class TimeTable(

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeslotRange")
    var timeslotList: List<TimeSlot>,

    @PlanningEntityCollectionProperty
    var lessonList: List<Lesson>,

    @PlanningScore
    var score: HardSoftScore? = null,

    // Ignored by OptaPlanner, used by the UI to display solve or stop solving button
    var solverStatus: SolverStatus? = null,
)

@Serializable
data class UiTimeTable(
    val lessonList: List<Lesson>,
) {
    companion object {
        fun createFrom(timeTable: TimeTable) = UiTimeTable(
            lessonList = timeTable.lessonList
        )
    }
}

val workingDays = DayOfWeek
    .values()
    .dropLast(2)
    .map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }

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

class TimeTableConstraintProvider : ConstraintProvider {

    override fun defineConstraints(constraintFactory: ConstraintFactory) = with(constraintFactory) {
        arrayOf(
            //HARD
//            from(Lesson::class.java)
//                .groupBy ({it}, { lesson: Lesson -> lesson.studentGroup })
//                .join(Lesson::class.java,
//                    Joiners.equal(Lesson::studentGroup),
//                    Joiners.equal { lesson: Lesson -> lesson.timeslot?.day },
//                    Joiners.equal { lesson: Lesson -> lesson.timeslot?.day }
//                )
//            fromUniquePair(Lesson::class.java,
//                Joiners.equal(Lesson::studentGroup),
//                Joiners.equal { lesson: Lesson -> lesson.timeslot?.day }
//            )
//                .filter { lesson1: Lesson, lesson2: Lesson ->
//                    abs((lesson1.timeslot!!.number) - (lesson2.timeslot!!.number)) != 1
//                }
//                .penalize("Student no gaps", HardSoftScore.ONE_HARD),

//            fromUniquePair(Lesson::class.java,
//                Joiners.equal(Lesson::timeslot),
//                Joiners.equal(Lesson::teacher)
//            )
//                .penalize("Teacher conflict", HardSoftScore.ONE_HARD),

            fromUniquePair(Lesson::class.java,
                Joiners.equal(Lesson::timeslot),
                Joiners.equal(Lesson::studentGroup)
            )
                .penalize("Student group conflict", HardSoftScore.ONE_HARD),

            from(Lesson::class.java)
                .filter { it.timeslot != null }
                .groupBy({ it.studentGroup }, { it.timeslot!!.day }, ConstraintCollectors.toList())
                .filter { group, day, list ->
                    list.any { it.timeslot!!.number != 1 }
                }
                .penalize(
                    "Student must have first lesson",
                    HardSoftScore.ONE_HARD
                ) { group, day, list -> list.count { it.timeslot!!.number != 1 } },

            //SOFT
//            from(Lesson::class.java)
//                .join(Lesson::class.java,
//                    Joiners.equal(Lesson::teacher),
//                    Joiners.equal { lesson: Lesson -> lesson.timeslot?.day }
//                )
//                .filter { lesson1: Lesson, lesson2: Lesson ->
//                    abs((lesson1.timeslot!!.number) - (lesson2.timeslot!!.number)) == 1
//                }
//                .reward("Teacher time efficiency", HardSoftScore.ONE_SOFT)
        )
    }

//    fun studentGroupSubjectVariety(constraintFactory: ConstraintFactory): Constraint {
//        // A student group dislikes sequential lessons on the same subject.
//        return constraintFactory
//            .from(Lesson::class.java)
//            .join(Lesson::class.java,
//                Joiners.equal(Lesson::subject),
//                Joiners.equal(Lesson::studentGroup),
//                Joiners.equal { lesson: Lesson -> lesson.timeslot?.day })
//            .filter { lesson1: Lesson, lesson2: Lesson ->
//                abs((lesson1.timeslot?.number ?: 0) - (lesson2.timeslot?.number ?: 0)) <= 1
//            }
//            .penalize("Student group subject variety", HardSoftScore.ONE_SOFT)
//    }

}

const val SINGLETON_TIME_TABLE_ID = 1L


fun main(args: Array<String>) {
    val timeSlots = workingDays
        .flatMap { day ->
            (1..8).map { number ->
                TimeSlot(day, number)
            }
        }

    val getPerClassLessons = { group: String, baseTeacher: String ->
        listOf(
            List(4) { Lesson("Math", baseTeacher, group) },
            List(4) { Lesson("Rus", baseTeacher, group) },
            List(1) { Lesson("HLang", baseTeacher, group) },
            List(2) { Lesson("Eng", "${group.first()}ET", group) },
            List(3) { Lesson("Phis", "${group.first()}PT", group) },
            List(2) { Lesson("Env", baseTeacher, group) },
            List(1) { Lesson("Tech", baseTeacher, group) },
            List(1) { Lesson("Iso", baseTeacher, group) },
            List(1) { Lesson("Mus", "MT", group) },
        )
            .flatten()
    }

    val groups = listOf("a", "b", "c", "d")
        .flatMap { letter ->
            (1..4).map { number ->
                "$number$letter"
            }
        }
        .dropLast(1)
    println("Groups: ${groups.size}")


    val lessons = groups
        .flatMap { group ->
            getPerClassLessons(group, "${group}T")
        }

    val problem = TimeTable(
        timeslotList = timeSlots,
        lessonList = lessons
    )

    val solverConfig = SolverConfig().apply {
        solutionClass = TimeTable::class.java
        entityClassList = listOf(
            Lesson::class.java
        )
//        withEasyScoreCalculatorClass(TimeTableEasyScoreCalculator::class.java)
        withConstraintProviderClass(TimeTableConstraintProvider::class.java)
        withTerminationSpentLimit(Duration.ofSeconds(5))
    }

    val manager = SolverManager.create<TimeTable, Long>(solverConfig)
    println("Start")
//    val job = manager.solveAndListen(
//        SINGLETON_TIME_TABLE_ID,
//        { solution },
//        {
//            println(it)
//        },
//        {
//            println("BEST $it")
//        },
//        { a, b ->
//            System.err.println(a)
//            System.err.println(b)
//        }
//    )
    val job = manager.solve(SINGLETON_TIME_TABLE_ID, problem)

    val solution = job.finalBestSolution
    println("End")

    val uiTimeTable = UiTimeTable.createFrom(solution)

//    println(Json { prettyPrint = true }.encodeToString(uiTimeTable))


    val rowWidth = 6
    println(solution.score)
    print("Table".padEnd(rowWidth))
    print("|")


    val lessonList = solution
        .lessonList

    workingDays
        .forEach { dayOfWeek ->
            (1..8).forEach a@{ slot ->
                if (lessonList.none { it.timeslot!!.number == slot }) return@a
                print("$dayOfWeek($slot)".padEnd(rowWidth))
                print("|")
            }
            print("      ")
        }

    println()




    groups
        .sorted()
        .forEach { studentGroup ->
            print(studentGroup.padEnd(rowWidth))
            print("|")
            workingDays
                .forEach { dayOfWeek ->
                    (1..8).forEach a@{ slot ->
                        if (lessonList.none { it.timeslot!!.number == slot }) return@a

                        lessonList
                            .filter { it.studentGroup == studentGroup && it.timeslot?.day == dayOfWeek && it.timeslot?.number == slot }
//                            .joinToString { "${it.subject}/${it.teacher}" }
                            .joinToString { "${it.subject}" }
                            .padEnd(rowWidth)
                            .also { print(it) }
                        print("|")
                    }
                    print("      ")
                }

            println()
        }


}

