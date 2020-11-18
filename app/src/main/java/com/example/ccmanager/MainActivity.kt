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
