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
import org.optaplanner.core.api.solver.SolverStatus
import java.util.*

@Serializable
@NoArg
@PlanningEntity
class Lesson(
    val subject: String,
    val teacher: String,
    val studentGroup: String,
    @PlanningVariable(valueRangeProviderRefs = ["timeslotRange"])
    var timeslot: TimeSlot? = null,
    @PlanningId
    val id: String = UUID.randomUUID().toString(),
)

@Serializable
class TimeSlot(
    val day: String,
    val number: Int,
)

@NoArg
@PlanningSolution
class TimeTable(

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
