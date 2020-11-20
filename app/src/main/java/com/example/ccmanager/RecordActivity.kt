package com.example.ccmanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_record.*
import java.io.BufferedReader
import java.io.File

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val rec = RecordController(this)
        //rec.addRecord("2011/1/1, Pull, vertical\n")
        //rec.addRecord("2011/1/2, Push, wall\n")
        rec.loadRecords().forEach() {
            val tv = TextView(this)
            tv.text = it
            layoutRecords.addView(tv)
        }
    }

    public fun buttonBack(view: View){
        finish()
    }
}
///////////////////////////////////////////////////////////
class RecordController (var context: Context) {
    class Item {

    }
    var fileName: String = "records2.csv"

    fun addRecord(contents: String){
        context.openFileOutput(fileName, Context.MODE_APPEND).use {
            it.write(contents.toByteArray())
            Log.d("RecordController", "add: ${contents}")
        }
    }
    fun loadRecords() : ArrayList<String> {
        val readFile = File(context.filesDir, fileName)
        val records = ArrayList<String>()

        val lines: List<String> = readFile.readLines()
        lines.forEach {
            records.add(it)
            Log.d("RecordController", "load: ${it}")
        }
        return records
    }
}