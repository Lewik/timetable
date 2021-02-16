package timetable

val lessonSet1: (String, String) -> List<Lesson> = { group: String, baseTeacher: String ->
    listOf(
        List(1) { Lesson("Изо", baseTeacher, group) },
        List(4) { Lesson("Мат", baseTeacher, group) },
        List(1) { Lesson("Муз", "MT", group) },
        List(5) { Lesson("Грам", baseTeacher, group) },
        List(2) { Lesson("Окр", baseTeacher, group) },
        List(4) { Lesson("Пис", baseTeacher, group) },
        List(1) { Lesson("Тех", baseTeacher, group) },
        List(3) { Lesson("Физ", "${group.first()}PT", group) },
    )
        .flatten()
}
val lessonSet2 = { group: String, baseTeacher: String ->
    listOf(
        List(2) { Lesson("Англ", "${group.first()}ET", group) },
        List(1) { Lesson("Изо", baseTeacher, group) },
        List(4) { Lesson("Мат", baseTeacher, group) },
        List(1) { Lesson("Муз", "MT", group) },
        List(2) { Lesson("Окр", baseTeacher, group) },
        List(5) { Lesson("Рус", baseTeacher, group) },
        List(1) { Lesson("Тех", baseTeacher, group) },
        List(3) { Lesson("Физ", "${group.first()}PT", group) },
        List(4) { Lesson("Чтен", baseTeacher, group) },
    )
        .flatten()
}
val lessonSet3 = { group: String, baseTeacher: String ->
    listOf(
        List(2) { Lesson("Англ", "${group.first()}ET", group) },
        List(1) { Lesson("Изо", baseTeacher, group) },
        List(4) { Lesson("Мат", baseTeacher, group) },
        List(1) { Lesson("Муз", "MT", group) },
        List(2) { Lesson("Окр", baseTeacher, group) },
        List(5) { Lesson("Рус", baseTeacher, group) },
        List(1) { Lesson("Тех", baseTeacher, group) },
        List(3) { Lesson("Физ", "${group.first()}PT", group) },
        List(3) { Lesson("Чтен", baseTeacher, group) },
        List(1) { Lesson("Орксэ", baseTeacher, group) },
    )
        .flatten()
}
val groups = mapOf(
    "1а" to lessonSet1("1а", "1а" + "T"),
    "1б" to lessonSet1("1б", "1б" + "T"),
    "1в" to lessonSet1("1в", "1в" + "T"),
    "1г" to lessonSet1("1г", "1г" + "T"),

    "2а" to lessonSet2("2а", "2а" + "T"),
    "2б" to lessonSet2("2б", "2б" + "T"),
    "2в" to lessonSet2("2в", "2в" + "T"),
    "2г" to lessonSet2("2г", "2г" + "T"),

    "3а" to lessonSet2("3а", "3а" + "T"),
    "3б" to lessonSet2("3б", "3б" + "T"),
    "3в" to lessonSet2("3в", "3в" + "T"),
    "3г" to lessonSet2("3г", "3г" + "T"),

    "4а" to lessonSet3("4а", "4а" + "T"),
    "4б" to lessonSet3("4б", "4б" + "T"),
    "4в" to lessonSet3("4в", "4в" + "T"),
)
