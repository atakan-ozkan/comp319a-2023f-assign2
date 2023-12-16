package com.example.fitlifein30days

import android.content.Context
import org.xmlpull.v1.XmlPullParser
import java.io.IOException

class WorkoutModelService (context: Context){
    private val workoutsForMale = listOf(
        WorkoutModel(R.drawable.benchpressman, "Bench Press"),
        WorkoutModel(R.drawable.bicepsman, "Biceps Workout"),
        WorkoutModel(R.drawable.deadliftman, "Deadlift"),
        WorkoutModel(R.drawable.declinedumbleman, "Decline Dumbbell Press"),
        WorkoutModel(R.drawable.dumbellbackrowman, "Dumbbell Back Row"),
        WorkoutModel(R.drawable.hanginglegraisesman, "Hanging Leg Raises"),
        WorkoutModel(R.drawable.legpullinsman, "Leg Pull-Ins"),
        WorkoutModel(R.drawable.lateralraiseman, "Lateral Raise"),
        WorkoutModel(R.drawable.pushupman, "Push Up"),
        WorkoutModel(R.drawable.skulltricepsman, "Skull Crusher Triceps"),
        WorkoutModel(R.drawable.squatman, "Squat"),
        WorkoutModel(R.drawable.standshoulderpressman, "Standing Shoulder Press"),
        WorkoutModel(R.drawable.stitwistman, "Seated Twist")
    )

    private val workoutsForFemale = listOf(
        WorkoutModel(R.drawable.abdominalwoman, "Abdominal Workout"),
        WorkoutModel(R.drawable.cobralatpulldownwoman, "Cobra Lat Pulldown"),
        WorkoutModel(R.drawable.invertedplankwoman, "Inverted Plank"),
        WorkoutModel(R.drawable.legstretchwoman, "Leg Stretching"),
        WorkoutModel(R.drawable.lowerabswoman, "Lower Abs Workout"),
        WorkoutModel(R.drawable.pushupwoman, "Push Up"),
        WorkoutModel(R.drawable.sidelegraisewoman, "Side Leg Raise"),
        WorkoutModel(R.drawable.singlelegdeadliftwoman, "Single Leg Deadlift"),
        WorkoutModel(R.drawable.squatwoman, "Squat")
    )

    private val thirtyDaySchedule: Map<String, List<WorkoutModel>>

    init {
        val weeklySchedule = parseWeeklyWorkoutSchedule(context)
        thirtyDaySchedule = createThirtyDaySchedule(weeklySchedule)
    }

    fun getWorkoutSchedule(day: Int, gender: String): List<WorkoutModel>?{
        return thirtyDaySchedule["Day$day-$gender"]
    }

    private fun createThirtyDaySchedule(weeklySchedule: Map<String, List<WorkoutModel>>): Map<String, List<WorkoutModel>> {
        val thirtyDaySchedule = mutableMapOf<String, List<WorkoutModel>>()
        for (day in 1..30) {
            val weekDay = (day - 1) % 7 + 1
            for (gender in listOf("Male", "Female")) {
                val weeklyKey = "Day$weekDay-$gender"
                val thirtyDayKey = "Day$day-$gender"
                weeklySchedule[weeklyKey]?.let {
                    thirtyDaySchedule[thirtyDayKey] = it
                }
            }
        }
        return thirtyDaySchedule
    }

    private fun parseWeeklyWorkoutSchedule(context: Context): Map<String, List<WorkoutModel>> {
        val schedule = mutableMapOf<String, MutableList<WorkoutModel>>()

        try {
            val parser = context.resources.getXml(R.xml.weekly_workout_schedule)
            var eventType = parser.eventType
            var day = ""
            var gender = ""
            var tempWorkoutList = listOf<WorkoutModel>()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "day" -> day = "Day${parser.getAttributeValue(null, "number")}"
                            "gender" ->
                            {
                                gender = parser.getAttributeValue(null, "type")
                                tempWorkoutList = if (gender.equals("Male", ignoreCase = true)){
                                    workoutsForMale
                                } else{
                                    workoutsForFemale
                                }

                            }
                            "workout" -> {
                                val workoutName = parser.getAttributeValue(null, "name")
                                tempWorkoutList.find { it.title.equals(workoutName, ignoreCase = true) }?.let {
                                    val key = "$day-$gender"
                                    schedule.computeIfAbsent(key) { mutableListOf() }
                                        .add(it)
                                }
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return schedule
    }
}