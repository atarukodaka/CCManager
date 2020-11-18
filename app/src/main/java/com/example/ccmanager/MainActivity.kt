package com.example.ccmanager

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

////////////////////////////////////////////////////////////////////////////////
class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ar = arrayOf(R.array.ps_steps,R.array.sq_steps, R.array.pl_steps, R.array.lr_steps,  R.array.br_steps,  R.array.hs_steps)
        val stepAdaptors = arrayOfNulls<ArrayAdapter<CharSequence>>(ar.size)

        for ((index, elem) in ar.withIndex() ) {
            stepAdaptors[index] = ArrayAdapter.createFromResource(this, elem, android.R.layout.simple_dropdown_item_1line)
        }
        val context = this

        spinnerEvents.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerSteps.setAdapter(stepAdaptors[spinnerEvents.selectedItemPosition])
                //spinnerSteps.setAdapter(ArrayAdapter.createFromResource(context, arrayOf("asdf", "foo"), android.R.layout.simple_dropdown_item_1line))
                Log.d("snipperevent", "pos: ${spinnerEvents.selectedItemPosition}")
                //val data = createExerciseDataFromSpinners()
                //textMaxReps.text = data.reps.toString()
                //textMaxSets.text = data.sets.toString()
                updateUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerSteps.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerGrades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }
    fun updateUI(){
        val data = createExerciseDataFromSpinners()
        textMaxReps.text = "${data.reps.toString()} reps"
        textMaxSets.text = "${data.sets.toString()} sets"
    }
    /*
    fun updateMaxCounters() {
        val pos_events = spinnerEvents.selectedItemPosition
        val pos_steps = spinnerSteps.selectedItemPosition
        val pos_grades = spinnerGrades.selectedItemPosition
        val dataset = MaxSetsRepsDataset()

        //iMaxSets = dataset.get_max_sets(pos_events, pos_steps, pos_grades)
        //iMaxReps = dataset.get_max_reps(pos_events, pos_steps, pos_grades)
       //Log.d("max sets reps", "sets: ${iMaxSets}, reps: ${iMaxReps}")
    }

     */

    public fun buttonStart(view: View) {
        val exerciseData = createExerciseDataFromSpinners()

        val intent: Intent = Intent(this, ExerciseActivity::class.java)
        intent.putExtra("EXERCISE", exerciseData)
        startActivity(intent)
    }
    fun createExerciseDataFromSpinners () : ExerciseData {
        val event = spinnerEvents.selectedItem.toString()
        val step = spinnerSteps.selectedItem.toString()
        val grade = spinnerGrades.selectedItem.toString()
        val interval :Int = editTextInterval.text.toString().toIntOrNull() ?: 0

        val pos_events = spinnerEvents.selectedItemPosition
        val pos_steps = spinnerSteps.selectedItemPosition
        val pos_grades = spinnerGrades.selectedItemPosition

        //val dataset = MaxSetsRepsDataset()
        //val sets :Int = dataset.get_max_sets(pos_events, pos_steps, pos_grades)
        //val reps : Int = dataset.get_max_reps(pos_events, pos_steps, pos_grades)

        val vol: ExerciseVolumnData? = DatasetController().find_volumn(pos_events, pos_steps, pos_grades)
        val sets: Int = vol?.sets ?: 0
        val reps: Int = vol?.reps ?: 0

        return ExerciseData(event, step, grade, interval, sets, reps)
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////

data class ExerciseData (
    var event: String,
    var step: String,
    var grade: String,

    var interval: Int = 10,
    var sets: Int = 3,
    var reps: Int = 10,
) : java.io.Serializable


data class Volumn (var reps: Int, var sets: Int)
data class ExcerciseStep( var number: Int, var name: String,
                         var beginnerVolumn: Volumn, var intermidiateVolumn: Volumn, var seniorVolumn: Volumn )

data class ExerciseEventData(val id: Int, val name: String, val abbr: String)
data class ExerciseStepData(val id: Int, val event: Int, val number: Int, val name: String)
data class ExerciseVolumnData(val id: Int, val event: Int, val step: Int, val grade: Int, val sets: Int, val reps: Int)

class DatasetController {
    val events = arrayOf (
            ExerciseEventData(0, "Push Ups", "PS"),
            ExerciseEventData(1, "Squats", "SQ"),
            ExerciseEventData(2, "Pull Ups", "PU"),
            ExerciseEventData(3, "Leg Raises", "LR"),
            ExerciseEventData(4, "Bridges", "BR"),
            ExerciseEventData(5, "Handstand Pushups", "HS")
    )
    val volumns = arrayOf(
            ExerciseVolumnData(0,0,0,0,1,10),
            ExerciseVolumnData(1,0,0,1,2,25),
            ExerciseVolumnData(2,0,0,2,3,50),
            ExerciseVolumnData(3,0,1,0,1,10),
            ExerciseVolumnData(4,0,1,1,2,20),
            ExerciseVolumnData(5,0,1,2,3,40),
            ExerciseVolumnData(6,0,2,0,1,10),
            ExerciseVolumnData(7,0,2,1,2,15),
            ExerciseVolumnData(8,0,2,2,3,30),
            ExerciseVolumnData(9,0,3,0,1,8),
            ExerciseVolumnData(10,0,3,1,2,12),
            ExerciseVolumnData(11,0,3,2,2,25),
            ExerciseVolumnData(12,0,4,0,1,5),
            ExerciseVolumnData(13,0,4,1,2,10),
            ExerciseVolumnData(14,0,4,2,2,20),
            ExerciseVolumnData(15,0,5,0,1,5),
            ExerciseVolumnData(16,0,5,1,2,10),
            ExerciseVolumnData(17,0,5,2,2,20),
            ExerciseVolumnData(18,1,0,0,1,10),
            ExerciseVolumnData(19,1,0,1,2,25),
            ExerciseVolumnData(20,1,0,2,3,50),
            ExerciseVolumnData(21,1,1,0,1,10),
            ExerciseVolumnData(22,1,1,1,2,20),
            ExerciseVolumnData(23,1,1,2,3,40),
            ExerciseVolumnData(24,1,2,0,1,10),
            ExerciseVolumnData(25,1,2,1,2,15),
            ExerciseVolumnData(26,1,2,2,3,30),
            ExerciseVolumnData(27,1,3,0,1,8),
            ExerciseVolumnData(28,1,3,1,2,12),
            ExerciseVolumnData(29,1,3,2,2,25),
            ExerciseVolumnData(30,1,4,0,1,5),
            ExerciseVolumnData(31,1,4,1,2,10),
            ExerciseVolumnData(32,1,4,2,2,20),
            ExerciseVolumnData(33,1,5,0,1,5),
            ExerciseVolumnData(34,1,5,1,2,10),
            ExerciseVolumnData(35,1,5,2,2,20),
            ExerciseVolumnData(36,2,0,0,1,10),
            ExerciseVolumnData(37,2,0,1,2,25),
            ExerciseVolumnData(38,2,0,2,3,50),
            ExerciseVolumnData(39,2,1,0,1,10),
            ExerciseVolumnData(40,2,1,1,2,20),
            ExerciseVolumnData(41,2,1,2,3,40),
            ExerciseVolumnData(42,2,2,0,1,10),
            ExerciseVolumnData(43,2,2,1,2,15),
            ExerciseVolumnData(44,2,2,2,3,30),
            ExerciseVolumnData(45,2,3,0,1,8),
            ExerciseVolumnData(46,2,3,1,2,12),
            ExerciseVolumnData(47,2,3,2,2,25),
            ExerciseVolumnData(48,2,4,0,1,5),
            ExerciseVolumnData(49,2,4,1,2,10),
            ExerciseVolumnData(50,2,4,2,2,20),
            ExerciseVolumnData(51,2,5,0,1,5),
            ExerciseVolumnData(52,2,5,1,2,10),
            ExerciseVolumnData(53,2,5,2,2,20),
            ExerciseVolumnData(54,3,0,0,1,10),
            ExerciseVolumnData(55,3,0,1,2,25),
            ExerciseVolumnData(56,3,0,2,3,50),
            ExerciseVolumnData(57,3,1,0,1,10),
            ExerciseVolumnData(58,3,1,1,2,20),
            ExerciseVolumnData(59,3,1,2,3,40),
            ExerciseVolumnData(60,3,2,0,1,10),
            ExerciseVolumnData(61,3,2,1,2,15),
            ExerciseVolumnData(62,3,2,2,3,30),
            ExerciseVolumnData(63,3,3,0,1,8),
            ExerciseVolumnData(64,3,3,1,2,12),
            ExerciseVolumnData(65,3,3,2,2,25),
            ExerciseVolumnData(66,3,4,0,1,5),
            ExerciseVolumnData(67,3,4,1,2,10),
            ExerciseVolumnData(68,3,4,2,2,20),
            ExerciseVolumnData(69,3,5,0,1,5),
            ExerciseVolumnData(70,3,5,1,2,10),
            ExerciseVolumnData(71,3,5,2,2,20),
            ExerciseVolumnData(72,3,0,0,1,10),
            ExerciseVolumnData(73,3,0,1,2,25),
            ExerciseVolumnData(74,3,0,2,3,50),
            ExerciseVolumnData(75,3,1,0,1,10),
            ExerciseVolumnData(76,3,1,1,2,20),
            ExerciseVolumnData(77,3,1,2,3,40),
            ExerciseVolumnData(78,3,2,0,1,10),
            ExerciseVolumnData(79,3,2,1,2,15),
            ExerciseVolumnData(80,3,2,2,3,30),
            ExerciseVolumnData(81,3,3,0,1,8),
            ExerciseVolumnData(82,3,3,1,2,12),
            ExerciseVolumnData(83,3,3,2,2,25),
            ExerciseVolumnData(84,3,4,0,1,5),
            ExerciseVolumnData(85,3,4,1,2,10),
            ExerciseVolumnData(86,3,4,2,2,20),
            ExerciseVolumnData(87,3,5,0,1,5),
            ExerciseVolumnData(88,3,5,1,2,10),
            ExerciseVolumnData(89,3,5,2,2,20),
            ExerciseVolumnData(90,3,0,0,1,10),
            ExerciseVolumnData(91,3,0,1,2,25),
            ExerciseVolumnData(92,3,0,2,3,50),
            ExerciseVolumnData(93,3,1,0,1,10),
            ExerciseVolumnData(94,3,1,1,2,20),
            ExerciseVolumnData(95,3,1,2,3,40),
            ExerciseVolumnData(96,3,2,0,1,10),
            ExerciseVolumnData(97,3,2,1,2,15),
            ExerciseVolumnData(98,3,2,2,3,30),
            ExerciseVolumnData(99,3,3,0,1,8),
            ExerciseVolumnData(100,3,3,1,2,12),
            ExerciseVolumnData(101,3,3,2,2,25),
            ExerciseVolumnData(102,3,4,0,1,5),
            ExerciseVolumnData(103,3,4,1,2,10),
            ExerciseVolumnData(104,3,4,2,2,20),
            ExerciseVolumnData(105,3,5,0,1,5),
            ExerciseVolumnData(106,3,5,1,2,10),
            ExerciseVolumnData(107,3,5,2,2,20),

            )

    fun find_volumn (event: Int, step: Int, grade: Int) : ExerciseVolumnData? {
        volumns.forEach {
            if (it.event == event && it.step == step && it.grade == grade) return it
        }
        Log.d("find_volumn", "not found: ${event}/${step}/${grade}")
        return null
    }
}
/*
//////////////////////////////
class MaxSetsRepsDataset {
    val max_sets_reps = arrayOf(
            // Push Ups
            arrayOf(
                    arrayOf(arrayOf(2, 3), arrayOf(2, 25), arrayOf(3, 50)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 15), arrayOf(3, 30)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(2, 25)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(2, 20)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(2, 20)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(2, 20)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(2, 20)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(2, 20)),
                    arrayOf(arrayOf(1, 5), arrayOf(2, 10), arrayOf(2, 20))
            ),
            arrayOf(
                    arrayOf(arrayOf(1, 10), arrayOf(2, 25), arrayOf(3, 50)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 15), arrayOf(3, 30)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40))
            ),
            arrayOf(
                    arrayOf(arrayOf(1, 10), arrayOf(2, 25), arrayOf(3, 50)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 15), arrayOf(3, 30)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40))
            ),
            arrayOf(
                    arrayOf(arrayOf(1, 10), arrayOf(2, 25), arrayOf(3, 50)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 15), arrayOf(3, 30)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40))
            ),
            arrayOf(
                    arrayOf(arrayOf(1, 10), arrayOf(2, 25), arrayOf(3, 50)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 15), arrayOf(3, 30)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40)),
                    arrayOf(arrayOf(1, 10), arrayOf(2, 20), arrayOf(3, 40))
            )
    )
    fun get_max_sets(event: Int, set: Int, grade: Int) : Int {
        return max_sets_reps[event][set][grade][0]
    }

    fun get_max_reps(event: Int, set: Int, grade: Int) : Int {
        return max_sets_reps[event][set][grade][1]
    }

}


 */