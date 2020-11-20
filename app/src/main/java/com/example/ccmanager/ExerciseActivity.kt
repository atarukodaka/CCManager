package com.example.ccmanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_exercise.*
import java.lang.Math.round
import java.sql.Time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//////////////////////////////////////////////////////////////////
class ExerciseActivity : AppCompatActivity() {
    companion object {
       // var sound :SoundController = SoundController()
    }
    lateinit var timer: CountDownTimer
    lateinit var exerciseData: ExerciseData
    var state = ExerciseState()
    var sound = SoundController()
    var millisResume : Long = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        //sound.initialize(this)
        sound.initialize(this)
        //sound.speakText("test")

        // retrieve exercise data
        val data = intent.getSerializableExtra("EXERCISE")
        if (data is ExerciseData) {
            exerciseData = data
            textEvent.text = exerciseData.event
            textStep.text = exerciseData.step
            textGrade.text = exerciseData.grade

            Log.d("exercise", "${exerciseData.event} / ${exerciseData.step} / ${exerciseData.grade}")
            Log.d("exercise", "rep: ${exerciseData.reps}, set: ${exerciseData.sets}, interval: ${exerciseData.interval}")
        }
        //timer = startReadyTimer()
        timer = startExerciseTimer()
    }
    ///////////////////////////
    // onClick
    public fun buttonStop(view: View) { // cancel timer and back to main activity
        if (::timer.isInitialized) timer.cancel()
        finish()
    }
    public fun buttonPauseResume(view: View) {
        btnPauseResume.text = resources.getString(R.string.Pause)
        when (state.tag){
            "PAUSE" -> timer = startExerciseTimer(millisResume)
            "FINISHED" -> timer = startExerciseTimer()
            else -> state.tag = "PAUSING"
        }
    }

    /////////////////////////////////////////////////////////////////
    fun startExerciseTimer(millis: Long = (6 + (exerciseData.reps * 6 + exerciseData.interval ) * exerciseData.sets - exerciseData.interval ).toLong()* 1000L) : CountDownTimer {
        Log.d("ExerciseTimer", "millis: ${millis}")

        return object: CountDownTimer(millis, 1000L) {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTick(millisUntilFinished: Long) {
                //textDebug.text = sound.textToSpeech.language.toString()
                if (state.tag == "PAUSING") {
                    state.tag = "PAUSE"
                    btnPauseResume.text = resources.getString(R.string.Resume)
                    cancel()
                    millisResume = millisUntilFinished
                } else {
                    val readyCount = 6
                    val tickCount = 6
                    val remaining_sec: Int = round(millisUntilFinished.toDouble() / 1000).toInt()
                    val total_sec = readyCount + (exerciseData.reps * 6 + exerciseData.interval) * exerciseData.sets - exerciseData.interval
                    val elapse_sec = total_sec - remaining_sec - readyCount

                    var tone: String = "low"
                    if (elapse_sec < 0) {
                        state.tag = "READY"
                        state.ready = elapse_sec * -1

                        val text: String =
                            if (state.ready == readyCount -1) exerciseData.step.toString()
                            else state.ready.toString()
                        sound.speakText(text)
                    } else {
                        state.tag = "RUNNING"
                        state.set = (elapse_sec / (6 * exerciseData.reps + exerciseData.interval)).toInt() + 1
                        val mod = (elapse_sec % (6 * exerciseData.reps + exerciseData.interval))

                        if (mod < 6 * exerciseData.reps) {
                            state.tag = "RUNNING"
                            state.tick = (mod % 6) + 1
                            state.rep = (mod / 6).toInt() + 1
                            if (state.tick == 1) {
                                if (state.rep == 1){
                                    sound.speakText("set ${state.set} start")
                                } else {
                                    sound.speakText(state.rep.toString())
                                }
                                tone = "high"
                            }
                        } else {
                            state.tag = "INTERVAL"
                            state.interval = exerciseData.interval + 1 - (mod - 6 * exerciseData.reps + 1)

                            if (state.interval == exerciseData.interval) {
                                tone = "high"
                                sound.speakText("set ${state.set} done. interval of ${exerciseData.interval} seconds.")
                            } else if (state.interval == 5) {
                                sound.speakText("5 secounds to go.")
                            } else if (state.interval <= 3) {
                                sound.speakText(state.interval.toString())
                            }
                         }
                    }
                    sound.beep(tone)

                    val msg = "[${state.tag}] remaining_sec: ${remaining_sec}, elapse_sec: ${elapse_sec}, set: ${state.set}, interval: ${state.interval}, rep: ${state.rep}, tick: ${state.tick}"
                    textMessage.text = msg
                    Log.d("ExerciseTimer", msg)
                }
                updateUI()
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onFinish() {
                btnStop.text = resources.getString(R.string.Back)
                btnPauseResume.text = resources.getString(R.string.Restart)
                state.tag = "FINISHED"

                Log.d("ExerciseTimer", "Finished: ${state.tag}")
                writeRecord()
                sound.speakText("all sets finished. well done.")
                updateUI()
            }
        }.start()
    }

    fun updateUI() {
        textMessage.text = state.tag
        textTicks.text = "Ticks: ${state.tick} / 6"
        textReady.text = "Ready: ${state.ready }"
        textMaxReps.text = "Reps: ${state.rep} / ${exerciseData.reps}"
        textMaxSets.text = "Sets: ${state.set} / ${exerciseData.sets}"
        textInterval.text = "Interval: ${state.interval} / ${exerciseData.interval}"
    }
    fun writeRecord() {
        // record
        val sp: SharedPreferences = getSharedPreferences("records", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        val cur = LocalDateTime.now()
        //val datetimePattern = "yyyy-MM-DD HH:mm:ss"

        //val formatter = DateTimeFormatter.ofPattern(datetimePattern)
        //val formatted_time = cur.format(formatter)
        val formatted_time = cur.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        //val formatted_time = cur.toString()
        //val step_short: String = resources.getStringArray(R.array.push_steps_short)[exerciseData.step]
        val msg = "${exerciseData.event}/${exerciseData.step}/${exerciseData.sets}x${exerciseData.reps}"
        Log.d("ExerciseActity", "${formatted_time}: ${msg}")
        editor.putString(formatted_time, msg)
        editor.apply()

    }
}
//////////////////////////////////////////////////////////////////////
data class ExerciseState (
        var tick: Int = 0, var rep: Int = 0, var set: Int = 0,
        var interval: Int = 0, var ready: Int = 0,
        var tag: String = "NOT_STARTED"
)