package com.example.ccmanager

import android.content.Context
import android.content.res.Resources
import android.util.Log

class ExerciseTask (var event: Event, var step: Step, var grade: Grade, var volumn: Volumn)

data class Event (var number: Int, var name: String, var short: String)
data class Step (var number: Int, var name: String, var short: String, var event: Event)
data class Grade(var number: Int, var name: String, var short: String)
data class Volumn(var event: Event, var step: Step, var grade: Grade, var sets: Int, var reps: Int)

class ExerciseController (context: Context) {
    var events = arrayListOf<Event>()
    var steps = arrayListOf<Step>()
    var grades = arrayListOf<Grade>()
    var volumns = arrayListOf<Volumn>()

    init {
        val res = context.resources
        val events_short = res.getStringArray(R.array.events_short)

        res.getStringArray(R.array.events).forEachIndexed { index, s ->
            val short_name = events_short[index]
            Log.d("ExerciseController", "Register Event: ${index}: ${s} / ${short_name}")
            events.add(Event(index+1, s, short_name))
        }
        // steps
        val arr = arrayOf(R.array.push_steps, R.array.sq_steps, R.array.pull_steps, R.array.leg_steps, R.array.br_steps, R.array.hspu_steps)

        events.forEach { event ->
            //  val res_id = resources.getIdentifier("push_steps", "array", getPackageName())
            val res_id = res.getIdentifier("${event.short}_steps", "array", context.getPackageName() )

            res.getStringArray(res_id).forEachIndexed { i, s ->
                Log.d("ExerciseController", "Register Step: ${i}: ${s} on ${event.name}")
                steps.add(Step(i+1, s, "foo", event))
            }
        }
        // grades
        res.getStringArray(R.array.grades).forEachIndexed { index, s ->
            grades.add(Grade(index+1, s, "foo"))
        }
        // volumns
        events.forEach { event ->
            val res_id = res.getIdentifier("${event.short}_volumns", "array", context.getPackageName() )

            res.getStringArray(res_id).forEachIndexed { i, s ->
                Log.d("ExerciseController", "Register Volumn: ${i}: ${s} on ${event.name}")
                val arr = s.split(",")
                val step = find_step(event.number-1, i)
                volumns.add(Volumn(event, step, grades[0], arr[0].toInt(), arr[1].toInt()))
                volumns.add(Volumn(event, step, grades[1], arr[2].toInt(), arr[3].toInt()))
                volumns.add(Volumn(event, step, grades[2], arr[4].toInt(), arr[5].toInt()))
            }
        }
        context.resources.getStringArray(R.array.push_volumns).forEachIndexed { index, s ->
            val arr = s.split(",")
//            volumns.add(Volumn(events[0], steps[index], grades[0], arr[0].toInt(), arr[1].toInt()))
//            volumns.add(Volumn(events[0], steps[index], grades[1], arr[2].toInt(), arr[3].toInt()))
//            volumns.add(Volumn(events[0], steps[index], grades[2], arr[4].toInt(), arr[5].toInt()))
        }
        /*
        val __events = arrayListOf(
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

         */
    }
    fun create_task (event_number: Int, step_number: Int, grade_number: Int) : ExerciseTask {
        val event = events[event_number]
        val step = steps[step_number]
        val grade =  grades[grade_number]
        val volumn = find_volumn(event, step, grade)
        return ExerciseTask(event, step, grade, volumn)
    }
    fun find_step(event_no: Int, step_no: Int) : Step {
        val event = events[event_no]
        steps.forEach {
            if (it.event == event && it.number == step_no){
                return it
            }
        }
        return steps[0]  // TODO: DEBUG ONLY
    }
    fun find_volumn(event_no: Int, step_no: Int, grade_no: Int) : Volumn {
        return find_volumn(events[event_no], find_step(event_no, step_no), grades[grade_no])
        //try {
        /*
            val event = events[event_no] // TODO: overflow check
            val step = steps[event_no * 10 + step_no]
            val grade = grades[grade_no]

            return find_volumn(event, step, grade)

         */
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
        //throw Exception("volumn not found: ${event.number}/${step.number}/${grade.number}") // TODO:
        Log.d("ExerciseDataset", "Volumn not found: ${event.number} / ${step.number} / ${grade.number}")
        return volumns[0]  // TODO: just for debug
    }
}
/*
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


 */