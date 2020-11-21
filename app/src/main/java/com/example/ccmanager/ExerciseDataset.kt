package com.example.ccmanager

import android.content.Context
import android.content.res.Resources
import android.util.Log

//data class ExerciseTaskSerializable (var event_number: Int, var step_number: Int, var grade_number: Int, var sets: Int, var reps: Int, var interval: Int): java.io.Serializable
class ExerciseTask (var event: Event, var step: Step, var grade: Grade, var volumn: Volumn)


data class ExerciseData (
        var event_id: Int,
        var event: String,
        var step_id: Int,
        var step: String,
        var grade_id: Int,
        var grade: String,

        var interval: Int = 10,
        var sets: Int = 3,
        var reps: Int = 10,
) : java.io.Serializable

data class ExerciseVolumnData(val event: Int, val step: Int, val grade: Int, val sets: Int, val reps: Int) : java.io.Serializable

data class Event (var number: Int, var name: String, var short: String)
data class Step (var number: Int, var name: String, var short: String, var event: Event)
data class Grade(var number: Int, var name: String, var short: String)
//data class Volumn(var sets: Int, var reps: Int)
//data class Volumn(var sets: Int, var reps: Int)
data class Volumn(var event: Event, var step: Step, var grade: Grade, var sets: Int, var reps: Int)

class ExerciseController (context: Context) {
    lateinit var events: Array<Event>
    lateinit var steps: Array<Step>
    lateinit var grades: Array<Grade>
    lateinit var volumns: Array<Volumn>

    init {
        events = arrayOf(
                Event(1, "Push Ups", "push"),
                Event(2, "Squats", "sq"),
                Event(3, "Pull Ups", "pull"),
                Event(4, "Leg Raises", "leg"),
                Event(5, "Bridges", "br"),
                Event(6, "Handstand PUs", "hspu"),
        )
        steps = arrayOf(
                Step(1, "Wall Pushup", "wall", events[0]),
                Step(2, "Incline Pushup", "incl", events[0]),
                Step(3, "Kneeling Pushup", "knee", events[0]),
                Step(4, "Half Pushup", "incl", events[0]),
                Step(5, "Full Pushup", "incl", events[0]),
                Step(6, "Close Pushup", "incl", events[0]),
                Step(7, "Incline Pushup", "incl", events[0]),
                Step(8, "Incline Pushup", "incl", events[0]),
                Step(9, "Incline Pushup", "incl", events[0]),
                Step(10, "Incline Pushup", "incl", events[0]),
        )
        grades = arrayOf(
                Grade(1, "Beginner", "beg"),
                Grade(2, "Intermidiate", "int"),
                Grade(3, "Senior", "snr"),

        )
        volumns = arrayOf(
                Volumn(events[0], steps[0], grades[0],2, 3),
                Volumn(events[0], steps[0], grades[1],2, 20),
                Volumn(events[0], steps[0], grades[2],3, 50),

                Volumn(events[0], steps[1], grades[0],2, 3),
                Volumn(events[0], steps[1], grades[1],2, 20),
                Volumn(events[0], steps[1], grades[2],3, 50),

                Volumn(events[0], steps[2], grades[0],2, 3),
                Volumn(events[0], steps[2], grades[1],2, 20),
                Volumn(events[0], steps[2], grades[2],3, 50),
        )
    }
    fun create_task (event_number: Int, step_number: Int, grade_number: Int) : ExerciseTask {
        val event = events[event_number]
        val step = steps[step_number]
        val grade =  grades[grade_number]
        val volumn = find_volumn(event, step, grade)
        return ExerciseTask(event, step, grade, volumn)
    }
    fun find_volumn(event_no: Int, step_no: Int, grade_no: Int) : Volumn {
        //try {
            val event = events[event_no] // TODO: overflow check
            val step = steps[event_no * 10 + step_no]
            val grade = grades[grade_no]

            return find_volumn(event, step, grade)
        //} catch (e: ArrayIndexOutOfBoundsException) {
        //    Log.d("ExerciseDataset", "ERROR: ${e.toString()}")
        //    return null
        //}

    }
    fun find_volumn(event: Event, step: Step, grade: Grade) : Volumn {

        volumns.forEach {
            if (it.event == event && it.step == step && it.grade == grade){
                return it
            }
        }
        throw Exception("volumn not found")
    }
}

class DatasetController (var context: Context) {
    fun createExerciseData(event_id: Int, step_id: Int, grade_id: Int, interval: Int, sets: Int, reps: Int){
        //val event = context.resources.getStringArray(R.s)
    }
    fun event_name(id: Int){
        context.resources.getStringArray(R.array.events)[id]    // TODO: over flow check
    }
    fun step_name(event_id: Int, step_id: Int) : String {
        //resources.getIdentifier("push_steps", "array", getPackageName())
        val step_abbr: String = context.resources.getStringArray(R.array.events_abbr)[event_id] // TODO: overflow
        val resource_id: Int = context.resources.getIdentifier("${step_abbr.toLowerCase()}_steps", "array", context.getPackageName())
        val arr: Array<String> = context.resources.getStringArray(resource_id)
        return arr[step_id]
    }
    fun find_volumn  (event: Int, step: Int, grade: Int) : ExerciseVolumnData? {
        Log.d("ExerciseDataset", "given/event: ${event}, step: ${step}, grade: ${grade}")
        val index: Int = event * 30 + step * 3 + grade
        val arr: Array<String> = context.resources.getStringArray(R.array.grade_volumns)
        // TODO: array size check

        Log.d("ExerciseDataset", "index: ${index}, size: ${arr.size}")
        val str: String = arr[index]
        Log.d("ExerciseDataset", "string in array: ${str}")

        val data = str.split(",")
        //val data: Array<Int> = str.split(",")
        val sets: Int = data[3].toInt()
        val reps: Int = data[4].toInt()

        Log.d("ExerciseDataset", "dataset: ${event}, ${step}, ${grade}, ${sets}, ${reps}")
        return ExerciseVolumnData(event, step, grade, sets, reps)
    }
}
