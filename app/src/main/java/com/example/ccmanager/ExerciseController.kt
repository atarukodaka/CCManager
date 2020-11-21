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

        }
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
    fun select_steps_by_event(event: Event) : ArrayList<Step>{
        val arr = arrayListOf<Step>()

        steps.forEach { step ->
            if (step.event == event) arr.add(step)
        }
        return arr
    }
    fun find_volumn(event_no: Int, step_no: Int, grade_no: Int) : Volumn {
        return find_volumn(events[event_no], find_step(event_no, step_no), grades[grade_no])

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
