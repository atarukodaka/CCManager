package com.example.ccmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

////////////////////////////////////////////////////////////////////////////////
class MainActivity : AppCompatActivity() {
    val spFilename = "ccmanager_preference"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // debug
        //val t_step: String = DatasetController(this).step_name(0, 0)

        // task

        // shared preference: interval
        val sp: SharedPreferences = getSharedPreferences(spFilename, Context.MODE_PRIVATE)
        //val editor:SharedPreferences.Editor =  sp.edit()
        val interval: Int = sp.getInt("interval", 30)
        //Log.d("main", "key interval null? : ${interval == null}")
        editTextInterval.setText(interval.toString())
        editTextInterval.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val editor: SharedPreferences.Editor = sp.edit()
                val edittext_interval: Int =  view.text.toString().toInt() // TODO: overflow
                editor.putInt("interval", edittext_interval)
                editor.apply()
                Log.d("main/sp", "applied: interval = ${edittext_interval}")
            }
            false
        }

        val res_id = resources.getIdentifier("push_steps", "array", getPackageName())
        val arr = resources.getStringArray(res_id)
        Log.d("main", "res id: ${res_id}, data: ${arr[0]}")

        val ar = arrayOf(R.array.push_steps, R.array.sq_steps, R.array.pull_steps, R.array.leg_steps, R.array.br_steps, R.array.hspu_steps)
        val stepAdaptors = arrayOfNulls<ArrayAdapter<CharSequence>>(ar.size)

        for ((index, elem) in ar.withIndex() ) {
            stepAdaptors[index] = ArrayAdapter.createFromResource(this, elem, android.R.layout.simple_dropdown_item_1line)
        }

        spinnerEvents.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerSteps.setAdapter(stepAdaptors[spinnerEvents.selectedItemPosition])
                Log.d("snipperevent", "pos: ${spinnerEvents.selectedItemPosition}")
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
        val ctrl = ExerciseController(this)
        val volumn = ctrl.find_volumn(spinnerEvents.selectedItemPosition,
                spinnerSteps.selectedItemPosition,
                spinnerGrades.selectedItemPosition)

        Log.d("MainActivity", "volumn: ${volumn.sets} x ${volumn.reps}")
        textMaxReps.text = "${volumn.reps.toString()} reps"
        textMaxSets.text = "${volumn.sets.toString()} sets"
    }

    // onClickListener
    public fun buttonStart(view: View) {
        val intent: Intent = Intent(this, ExerciseActivity::class.java)
        intent.putExtra("event_number", spinnerEvents.selectedItemPosition)
        intent.putExtra("step_number", spinnerSteps.selectedItemPosition)
        intent.putExtra("grade_number", spinnerGrades.selectedItemPosition)
        intent.putExtra("interval", editTextInterval.text.toString().toIntOrNull() ?: 0)

        startActivity(intent)
    }
    public fun buttonRecords(view: View){
        val intent: Intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////
