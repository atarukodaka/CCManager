package com.example.ccmanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_exercise.*
import java.lang.Math.round

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
            textEvent.text = "${exerciseData.event}"
            textStep.text = "${exerciseData.step}"
            textGrade.text = "${exerciseData.grade}"

            //sound.speakText("do set of ${exerciseData.sets} with repitition of ${exerciseData.reps}")
            Log.d("exercise", "${exerciseData.event} / ${exerciseData.step} / ${exerciseData.grade}")
            Log.d("exercise", "rep: ${exerciseData.reps}, set: ${exerciseData.sets}, interval: ${exerciseData.interval}")
        }

        //timer = startReadyTimer()
        timer = startExerciseTimer()
    }
    // onClick
    public fun buttonStop(view: View) {
        if (::timer.isInitialized) timer.cancel()
        finish()
    }
    public fun buttonPauseResume(view: View) {
        btnPauseResume.text = "PAUSE"
        when (state.tag){
            "PAUSE" -> startExerciseTimer(millisResume)
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
                    cancel()
                    millisResume = millisUntilFinished
                } else {
                    val remaining_sec: Int = round(millisUntilFinished.toDouble() / 1000).toInt()
                    val total_sec = 6 + (exerciseData.reps * 6 + exerciseData.interval) * exerciseData.sets - exerciseData.interval
                    val elapse_sec = total_sec - remaining_sec - 6

                    var tone: String = "low"
                    if (elapse_sec < 0) {
                        state.tag = "READY"
                        state.ready = elapse_sec * -1
                        if (state.ready == 3) sound.speakText("get ready")
                    } else {
                        state.tag = "RUNNING"
                        state.set = (elapse_sec / (6 * exerciseData.reps + exerciseData.interval)).toInt() + 1
                        val mod = (elapse_sec % (6 * exerciseData.reps + exerciseData.interval))

                        if (mod < 6 * exerciseData.reps) {
                            state.tag = "RUNNING"
                            state.tick = (mod % 6) + 1
                            state.rep = (mod / 6).toInt() + 1
                            if (state.tick == 1) {
                                sound.speakText(state.rep.toString())
                                tone = "high"
                            }
                        } else {
                            state.tag = "INTERVAL"
                            state.interval = mod - 6 * exerciseData.reps + 1
                            if (state.interval == 1) sound.speakText("interval of ${exerciseData.interval} seconds.")
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
                state.tag = "FINISHED"
                sound.speakText("finished. well done.")
            }
        }.start()
    }

    /////////////////////////////////////
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun finishExercise() {
        sound.speakText("Finished. Well done.")
        finish()

        val msg = "Completed ${exerciseData.event} / ${exerciseData.step} / ${exerciseData.grade}"
        val args = Bundle()
        args.putString("message", msg)

        Log.d("finish", msg)
        //val dialog = FinishedDialog()
        //dialog.setArguments(args)
        //dialog.show(supportFragmentManager, "NoticeDialog")
        //finish()
    }
    fun updateUI() {
        textMessage.text = state.tag
        textTicks.text = "Ticks: ${state.tick} / 6"
        textReady.text = "Ready: ${state.ready }"
        textReps.text = "Reps: ${state.rep} / ${exerciseData.reps}"
        textSets.text = "Sets: ${state.set} / ${exerciseData.sets}"
        textInterval.text = "Interval: ${state.interval} / ${exerciseData.interval}"
    }
}
//////////////////////////////////////////////////////////////////////
data class ExerciseState (
        var tick: Int = 0, var rep: Int = 0, var set: Int = 0,
        var interval: Int = 0, var ready: Int = 0,
        var tag: String = "NOT_STARTED"
){
    fun incrReady() { ready += 1}
    fun incrSet() { set += 1 }
    fun incrInterval() { interval += 1}
    fun incrTick() { tick += 1 }
    fun incrRep() { rep += 1 }
    //fun reset() { tick = 0; rep = 0; set = 0; interval = 0; ready = 0 }
}