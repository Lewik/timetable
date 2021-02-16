package timetable

fun printSolution(solution: TimeTable) {
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
        .keys
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
                            .joinToString { it.subject }
                            .padEnd(rowWidth)
                            .also { print(it) }
                        print("|")
                    }
                    print("      ")
                }

            println()
        }
}
