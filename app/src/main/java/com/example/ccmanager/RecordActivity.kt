package com.example.ccmanager

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_record.*

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        //
        // show records
        var text = ""
        val records: SharedPreferences = getSharedPreferences("records", Context.MODE_PRIVATE)
        val map = records.all
        records.all.forEach {
            val textView = TextView(this)
            textView.text = "${it.key}: ${it.value}"
            textView.setTextIsSelectable(true)
            layoutRecords.addView(textView)
        }

    }

    public fun buttonBack(view: View){
        finish()
    }
}