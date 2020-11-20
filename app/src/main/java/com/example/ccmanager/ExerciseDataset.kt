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

class DatasetController (var context: Context) {
    fun createExerciseData(event_id: Int, step_id: Int, grade_id: Int, interval: Int, sets: Int, reps: Int){
        //val event = context.resources.getStringArray(R.s)
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
