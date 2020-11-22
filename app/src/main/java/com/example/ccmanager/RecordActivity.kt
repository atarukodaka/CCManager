package com.example.ccmanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.ccmanager.RecordController.Companion.datetimeFormatter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_record.*
import java.io.BufferedReader
import java.io.File
import java.nio.file.Files.exists
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecordActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        updateUI()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUI() {
        val rec = RecordController(this)
        var strRecords: String = ""
        rec.loadRecords().forEach() {
            //val tv = TextView(this)

            strRecords += "${it.datetime.format(RecordController.datetimeFormatter)}: ${it.task.event.name} s${it.task.step.number} ${it.task.step.name} ${it.task.volumn.sets}x${it.task.volumn.reps}\n"

            //tv.text = "${it.datetime.format(RecordController.datetimeFormatter)}: ${ex.event} s${ex.step_id+1} ${ex.step} ${ex.sets}x${ex.reps}"
            //layoutRecords.addView(tv)
        }
        textAllRecords.text = strRecords

        // Todays Summary
        var strTodaySummary: String = ""
        rec.loadRecords().forEach() {
            if (true) { // TODO

                strTodaySummary += "${it.task.event.name} s${it.task.step.number} ${it.task.step.name} ${it.task.volumn.sets}x${it.task.volumn.reps}\n"
                //val tv = TextView(this)
                //val ex = it.exerciseData
                //tv.text = "${ex.event} s${ex.step_id+1} ${ex.step} ${ex.sets}x${ex.reps}"
                //layoutTodaySummary.addView(tv)

                // textView.setTextIsSelectable(true)
            }
        }
        textTodaySummary.text = strTodaySummary
    }

    public fun buttonBack(view: View){
        finish()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    public fun buttonClear(view: View){
        // TODO: confirm dialog
        RecordController(this).clearRecords()
        layoutRecords.removeAllViews()
        layoutTodaySummary.removeAllViews()
        updateUI()
    }
    public fun buttonShare(view: View) {
        // TODO: open dialog
    }
}
///////////////////////////////////////////////////////////
class RecordController (var context: Context) {
    class Item (var datetime: LocalDateTime, var task: ExerciseTask)

    private var fileName: String = "records.csv"
    private var recordFile: File = File(context.filesDir, fileName)
    companion object {
        var datetimePattern: String = "yyyy-MM-dd HH:mm:ss"
        var datetimeFormatter = DateTimeFormatter.ofPattern(datetimePattern)
    }

    //fun addRecord(contents: String){
    fun addRecord(item: Item){
        addRecord(item.datetime, item.task)
    }
    fun addRecord(datetime: LocalDateTime, task: ExerciseTask){

        //context.openFileOutput(fileName, Context.MODE_APPEND).use {
        //val formatter = DateTimeFormatter.ofPattern(datetimePattern)
        val formatted = datetime.format(datetimeFormatter)

        val contents = arrayOf<String>(formatted,
                task.event.number.toString(), task.step.number.toString(), task.grade.number.toString()
        ).joinToString(",")
        recordFile.appendText(contents + "\n")
            //it.write(contents.toByteArray())
        Log.d("RecordController", "add: ${contents}")
    }

    fun loadRecords() : ArrayList<Item> {
        //val readFile = File(context.filesDir, fileName)
        val records = ArrayList<Item>()
        if (recordFile.exists()) {
            val lines: List<String> = recordFile.readLines()
            lines.forEach {
                //records.add(it)
                Log.d("RecordController", "load: ${it}")

                try {
                    val arr = it.split(',')
                    val dateTime = LocalDateTime.parse(arr[0], DateTimeFormatter.ofPattern(datetimePattern))
                    //val exerciseData = ExerciseData(arr[1].toInt(), arr[2], arr[3].toInt(), arr[4], arr[5].toInt(), arr[6], 0, arr[7].toInt(), arr[8].toInt()) // TODO: array overflow
                    val task = ExerciseController(context).create_task(arr[1].toInt(), arr[2].toInt(), arr[3].toInt())
                    records.add(Item(dateTime, task))
                } catch (e: RuntimeException) {
                    Log.d("RecordActivity", "ERROR: ${e.toString()}")
                    // TODO: catch error on loading records
                }

            }
        }

        return records
    }
    fun clearRecords () {
        recordFile.delete()
    }
}