package timetable

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.api.score.stream.ConstraintFactory
import org.optaplanner.core.api.score.stream.ConstraintProvider
import org.optaplanner.core.api.score.stream.Joiners

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

            fromUniquePair(Lesson::class.java,
                Joiners.equal(Lesson::timeslot),
                Joiners.equal(Lesson::teacher)
            )
                .penalize("Teacher conflict", HardSoftScore.ONE_HARD),

            fromUniquePair(Lesson::class.java,
                Joiners.equal(Lesson::timeslot),
                Joiners.equal(Lesson::studentGroup)
            )
                .penalize("Student group conflict", HardSoftScore.ONE_HARD),


//            from(Lesson::class.java)
//                .groupBy({ it.studentGroup }, { it.timeslot!!.day }, ConstraintCollectors.toList())
//                .filter { group, day, list ->
//                    list.any { it.timeslot!!.number != 1 }
//                }
//                .penalize(
//                    "Student must have first lesson",
//                    HardSoftScore.ONE_HARD
//                ),

//            from(Lesson::class.java)
//                .groupBy({ it.studentGroup }, { it.timeslot!!.day }, ConstraintCollectors.toList())
//                .filter { group, day, list ->
//                    list.any { it.timeslot!!.number == 1 }
//                }
//                .reward(
//                    "Student must have first lesson",
//                    HardSoftScore.ONE_HARD
//                ),


            fromUniquePair(Lesson::class.java,
                Joiners.equal(Lesson::studentGroup),
                Joiners.equal { lesson: Lesson -> lesson.timeslot?.day },
                Joiners.filtering { a, b -> a.timeslot != null && b.timeslot != null && a.timeslot!!.number - b.timeslot!!.number == 2 }

            )
                .ifNotExists(
                    Lesson::class.java,
                    Joiners.filtering { a, b, c ->
                        c.timeslot != null && c.timeslot!!.number == b.timeslot!!.number + 1
                    }
                )
                .penalize("Student no gaps", HardSoftScore.ONE_HARD),


//            from(Lesson::class.java)
//                .filter { it.timeslot!!.number != 1 }
//                .ifNotExistsOther(
//                    Lesson::class.java,
//                    Joiners.equal(Lesson::studentGroup),
//                    Joiners.equal { lesson: Lesson -> lesson.timeslot?.day },
//                    Joiners.equal(
//                        { a -> a.timeslot!!.number },
//                        { b -> (b.timeslot?.number ?: 0) + 1 }
//                    ),
//                )
//
//                .penalize(
//                    "Student no gaps",
//                    HardSoftScore.ONE_HARD
//                ),

//            from(Lesson::class.java)
//                .groupBy({ it.studentGroup }, { it.timeslot!!.day }, ConstraintCollectors.toList())
//                .filter { group, day, list ->
//                    val numbers = list.map { it.timeslot!!.number }
//                    (numbers.minOrNull()!!..numbers.maxOrNull()!!)
//                        .minus(numbers)
//                        .isNotEmpty()
//                }
//                .penalize(
//                    "Student no gaps",
//                    HardSoftScore.ONE_HARD
//                ),

//            from(Lesson::class.java)
//                .groupBy({ it.studentGroup }, { it.timeslot!!.day }, ConstraintCollectors.toList())
//                .filter { group, day, list ->
//                    val numbers = list.map { it.timeslot!!.number }
//                    (numbers.minOrNull()!!..numbers.maxOrNull()!!)
//                        .minus(numbers)
//                        .isEmpty()
//                }
//                .reward(
//                    "Student no gaps",
//                    HardSoftScore.ONE_HARD
//                ),

//            from(Lesson::class.java)
//                .filter { lesson ->
//                    groups
//                        .getValue(lesson.studentGroup)
//                        .none { it.id == lesson.id }
//                }
//                .penalize(
//                    "Student must have their scheduled lessons",
//                    HardSoftScore.ONE_HARD
//                ),
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
