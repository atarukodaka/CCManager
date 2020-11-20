package com.example.ccmanager

import android.content.Context
import android.content.res.Resources
import android.util.Log

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

data class ExerciseVolumnData(val event: Int, val step: Int, val grade: Int, val sets: Int, val reps: Int)

data class Event (var id: Int, var name: String, var abbr: String, var steps: Array<Step>)
data class Step ( var id: Int, var name: String, var short: String, var grades: Array<Grade>)
data class Grade(var id: Int, var name: String, var volumn: Volumn)
data class Volumn(var sets: Int, var reps: Int)
data class Exercise(var event: Event, var step: Step, var grade: Grade)


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
