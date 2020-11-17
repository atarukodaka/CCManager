package com.example.ccmanager

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_exercise.*
import java.util.*

data class ExerciseState (
    var tick: Int = 0, var rep: Int = 0, var set: Int = 0,
    var interval: Int = 0, var ready: Int = 0,
    var status: ExerciseActivity.Status = ExerciseActivity.Status.NOT_STARTED
){
    fun tickUp() { tick += 1}
    fun tickReset() { tick = 0}
    fun repUp() { rep += 1}
}
class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    enum class Status { NOT_STARTED, READY, RUNNING, PAUSE, INTERVAL, DONE }

    // Timers
    lateinit var timer: CountDownTimer

    // Counters
    var iTick: Int = 0
    var iRep: Int = 0
    var iSet: Int = 0
    var iInterval: Int = 0
    var iReady: Int = 0

    var iMaxReps: Int = 1
    var iMaxSets: Int = 1
    var iMaxIntervals: Int = 1
    var iMaxReadies: Int = 6
    var status: Status = Status.NOT_STARTED

    // Beep, Speech
    lateinit var soundPool: SoundPool
    private var textToSpeech: TextToSpeech? = null

    var beepHigh: Int = 0
    var beepLow: Int = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // sound
        textToSpeech = TextToSpeech(this, this)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(2).build()
        beepHigh = soundPool.load(this, R.raw.beep_high, 1)
        beepLow = soundPool.load(this, R.raw.beep_low, 1)

        // retrieve exercise data
        val exerciseData = intent.getSerializableExtra("EXERCISE")
        if (exerciseData is ExerciseData) {
            textEvent.text = "${exerciseData.event} / ${exerciseData.step} / ${exerciseData.grade}"
            iMaxReps = exerciseData.reps
            iMaxSets = exerciseData.sets
            iMaxIntervals = exerciseData.interval

            textEvent.text = "Event: ${exerciseData.event}"
            textStep.text = "Step: ${exerciseData.step}"
            textGrade.text = "Grade: ${exerciseData.grade}"

            Log.d("exercise", "${exerciseData.event} / ${exerciseData.step} / ${exerciseData.grade}")
            Log.d("exercise", "rep: ${iMaxReps}, set: ${iMaxSets}, interval: ${iMaxIntervals}")
        }
        timer = startReadyTimer()
    }

    // onClick
    public fun buttonStop(view: View) {
        if (::timer.isInitialized) timer.cancel()
        finish()
    }
    /////////////////////////////////////////////////////////////////
    // timers
    fun startReadyTimer() : CountDownTimer {
        status = Status.READY
        iReady = 0
        //iSec = 0

        return object: CountDownTimer(6000L, 1000L) {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTick(millisUntilFinished: Long) {
                // val sec: Int = round(millisUntilFinished / 1000F).toInt()
                iReady += 1
                Log.d("ReadyTimer", "ready: ${iReady}")
                //Log.d("ReadyTimer", "[${status.toString()}] millis: ${millisUntilFinished}, sec: ${iSec}, ready: ${iReady}")

                if (iReady == 1){
                    beep(true)
                    speakText("ready")
                } else {
                    beep(false)
                }
                updateUI()
            }
            override fun onFinish() {
                timer = startWorkoutTimer()
                updateUI()
            }
        }.start()
    }
    fun startWorkoutTimer() : CountDownTimer {
        status = Status.RUNNING
        iRep = 0
        iSet += 1
        iTick = 0

        return object: CountDownTimer((iMaxReps * 6 * 1000).toLong(), 1000) {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTick(millisUntilFinished: Long){
                iTick += 1
                if (iTick > 6) { iTick = 1 }
                Log.d("WorkoutTimer", "tick: ${iTick}")

                if (iTick == 1) {
                    iRep += 1
                    speakText(iRep.toString())
                    beep(true)
                } else if (iTick == 4) {
                    beep(true)
                } else {
                    beep(false)
                }
                updateUI()
            }
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onFinish() {
                if (iSet == iMaxSets){

                    finishExercise()
                    //btnStart.isClickable = true
                } else {
                    //status = Status.INTERVAL
                    speakText("interval of ${iMaxIntervals} seconds.")
                    timer = startIntervalTimer()
                }
                updateUI()
            }
        }.start()
    }

    fun startIntervalTimer() : CountDownTimer {
        iInterval = 0
        status = Status.INTERVAL

        return object: CountDownTimer(10 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                iInterval += 1
                Log.d("IntervalTimer", "interval: ${iInterval}")
                beep(false)
                updateUI()
            }
            override fun onFinish() {
                timer = startReadyTimer()
                updateUI()
            }
        }.start()
    }
    /////////////////////////////////////

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun finishExercise() {
        status = Status.DONE
        //val event = spinnerEvents.selectedItem.toString()
        //val step = spinnerSteps.selectedItem.toString()
        //val grade = spinnerGrades.selectedItem.toString()

        //speakText("well done ! you have done ${current_grade()} grade of ${current_step()} in ${current_event()}.")

        //val msg = "Completed ${event} / ${step} / ${grade}"
        val msg = "FINISHED"
        val args = Bundle()
        args.putString("message", msg)

        Log.d("finish", msg)
        val dialog = FinishedDialog()
        dialog.setArguments(args)
        dialog.show(supportFragmentManager, "NoticeDialog")
        finish()
    }
    fun updateUI() {
        textTicks.text = "Ticks: ${iTick} / 6"
        textReps.text = "Reps: ${iRep} / ${iMaxReps}"
        textSets.text = "Sets: ${iSet} / ${iMaxSets}"
        textInterval.text = "Interval: ${iInterval} / ${iMaxIntervals}"
        textStatus.text = status.toString()

        if (status == Status.RUNNING) {
            textMessage.text = "START !"
        } else if (status == Status.INTERVAL) {
            textMessage.text = "INTERVAL: ${iInterval} / ${iMaxIntervals}"
        } else if (status == Status.READY ){
            textMessage.text = "READY: ${iMaxReadies - iReady + 1}"
        } else {
            textMessage.text = "START!"
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Sound
    override fun onInit(stat: Int) {
        if (stat == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val locale = Locale.US
                if (tts.isLanguageAvailable(locale) > TextToSpeech.LANG_AVAILABLE) {
                    tts.language = locale
                } else {
                    Log.d("tts", "set language failed")
                }
            }
        } else {
            Log.d("tts", "tts init failed")
        }
    }

    fun beep(high: Boolean) {
        //val tone = if (high) { ToneGenerator.TONE_CDMA_ONE_MIN_BEEP } else { ToneGenerator.TONE_PROP_BEEP }
        //tg.startTone(tone)
        var tone = if (high) { beepHigh } else { beepLow }
        soundPool.play(tone, 1.0f, 1.0f, 0, 0, 1.0f)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speakText(text: String){
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }

}
//////////////////////////////////////////////////////////////////////
