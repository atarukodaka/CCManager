package com.example.ccmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

////////////////////////////////////////////////////////////////////////////////
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

          val step_adaptors = arrayOf(
            ArrayAdapter.createFromResource(
                this,
                R.array.ps_steps,
                android.R.layout.simple_spinner_dropdown_item
            ),
            ArrayAdapter.createFromResource(
                this,
                R.array.sq_steps,
                android.R.layout.simple_spinner_dropdown_item
            ),
            ArrayAdapter.createFromResource(
                this,
                R.array.pl_steps,
                android.R.layout.simple_spinner_dropdown_item
            ),
            ArrayAdapter.createFromResource(
                this,
                R.array.lr_steps,
                android.R.layout.simple_spinner_dropdown_item
            ),
            ArrayAdapter.createFromResource(
                this,
                R.array.br_steps,
                android.R.layout.simple_spinner_dropdown_item
            ),
            ArrayAdapter.createFromResource(
                this,
                R.array.hs_steps,
                android.R.layout.simple_spinner_dropdown_item
            )
        )

        spinnerEvents.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerSteps.setAdapter(step_adaptors[spinnerEvents.selectedItemPosition])
                Log.d("snipperevent", "pos: ${spinnerEvents.selectedItemPosition}")
                updateMaxCounters()
                //updateUI()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerSteps.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateMaxCounters()
                //updateUI()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerGrades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateMaxCounters()
                //updateUI()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun updateMaxCounters() {
        val pos_events = spinnerEvents.selectedItemPosition
        val pos_steps = spinnerSteps.selectedItemPosition
        val pos_grades = spinnerGrades.selectedItemPosition
        val dataset = MaxSetsRepsDataset()

        //iMaxSets = dataset.get_max_sets(pos_events, pos_steps, pos_grades)
        //iMaxReps = dataset.get_max_reps(pos_events, pos_steps, pos_grades)
       //Log.d("max sets reps", "sets: ${iMaxSets}, reps: ${iMaxReps}")
    }

    public fun buttonStart(view: View) {
        val event = spinnerEvents.selectedItem.toString()
        val step = spinnerSteps.selectedItem.toString()
        val grade = spinnerGrades.selectedItem.toString()
        val interval :Int = editTextInterval.text.toString().toIntOrNull() ?: 0

        val pos_events = spinnerEvents.selectedItemPosition
        val pos_steps = spinnerSteps.selectedItemPosition
        val pos_grades = spinnerGrades.selectedItemPosition
        val dataset = MaxSetsRepsDataset()

        val sets :Int = dataset.get_max_sets(pos_events, pos_steps, pos_grades)
        val reps : Int = dataset.get_max_reps(pos_events, pos_steps, pos_grades)

        val exerciseData = ExerciseData(event, step, grade, interval, reps, sets)

        val intent: Intent = Intent(this, ExerciseActivity::class.java)
        intent.putExtra("EXERCISE", exerciseData)
        startActivity(intent)
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////

data class ExerciseData (
    var event: String,
    var step: String,
    var grade: String,

    var interval: Int = 10,
    var reps: Int = 10,
    var sets: Int = 3
) : java.io.Serializable

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
