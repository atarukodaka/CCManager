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

//////////////////////////////////////////////////////////////////
class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var timer: CountDownTimer
    lateinit var exerciseData: ExerciseData
    var state = ExerciseState()
    var sound = ExerciseSound()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        sound.onCreate(this)

        // retrieve exercise data
        val data = intent.getSerializableExtra("EXERCISE")
        if (data is ExerciseData) {
            exerciseData = data
            textEvent.text = "Event: ${exerciseData.event}"
            textStep.text = "Step: ${exerciseData.step}"
            textGrade.text = "Grade: ${exerciseData.grade}"

            Log.d("exercise", "${exerciseData.event} / ${exerciseData.step} / ${exerciseData.grade}")
            Log.d("exercise", "rep: ${exerciseData.reps}, set: ${exerciseData.sets}, interval: ${exerciseData.interval}")
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
        //status = Status.READY
        state.tag = "READY"
        state.ready = 0

        return object: CountDownTimer(6000L, 1000L) {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTick(millisUntilFinished: Long) {
                state.upReady()
                Log.d("ReadyTimer", "ready: ${state.ready}")

                if (state.ready == 1){
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
        state.tag = "RUNNING"
        state.rep = 0
        state.tick = 0
        state.upSet()

        return object: CountDownTimer((exerciseData.reps * 6 * 1000).toLong(), 1000) {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTick(millisUntilFinished: Long){
                state.upTick()

                if (state.tick > 6) { state.tick = 1 }
                Log.d("WorkoutTimer", "tick: ${state.tick}")

                if (state.tick == 1) {
                    //iRep += 1
                    state.upRep()
                    speakText(state.rep.toString())
                    beep(true)
                } else if (state.tick == 4) {
                    beep(true)
                } else {
                    beep(false)
                }
                updateUI()
            }
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onFinish() {
                if (state.set == exerciseData.sets){
                    finishExercise()
                } else {
                    speakText("interval of ${exerciseData.interval} seconds.")
                    timer = startIntervalTimer()
                }
                updateUI()
            }
        }.start()
    }

    fun startIntervalTimer() : CountDownTimer {
        state.interval = 0
        state.tag = "INTERVAL"
        //status = Status.INTERVAL

        return object: CountDownTimer(10 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                state.upInterval()
                //iInterval += 1
                state.upInterval()
                Log.d("IntervalTimer", "interval: ${state.interval}")
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

    fun finishExercise() {
        state.tag = "DONE"

        val msg = "Completed ${exerciseData.event} / ${exerciseData.step} / ${exerciseData.grade}"
        val args = Bundle()
        args.putString("message", msg)

        Log.d("finish", msg)
        val dialog = FinishedDialog()
        dialog.setArguments(args)
        dialog.show(supportFragmentManager, "NoticeDialog")
        finish()
    }
    fun updateUI() {
        textTicks.text = "Ticks: ${state.tick} / 6"
        textReps.text = "Reps: ${state.rep} / ${exerciseData.reps}"
        textSets.text = "Sets: ${state.set} / ${exerciseData.sets}"
        textInterval.text = "Interval: ${state.interval} / ${exerciseData.interval}"
        // textStatus.text = status.toString()

        textMessage.text =
            when (state.tag) {
                "RUNNING" -> { "START ! " }
                "INTERVAL" -> { "INTERVAL: ${state.interval} / ${exerciseData.interval}" }
                "READY" -> { "READY: ${6 - state.ready + 1}" }
                else ->  { "" }
            }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // Sound: delegate to ExerciseSound object
    override fun onInit(stat: Int) {
        sound.onInit(stat)

    }
    fun beep(high: Boolean) {
        sound.beep(high)
       }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speakText(text: String){
        sound.speakText(text)
    }

}
//////////////////////////////////////////////////////////////////////

data class ExerciseState (
        var tick: Int = 0, var rep: Int = 0, var set: Int = 0,
        var interval: Int = 0, var ready: Int = 0,
        var tag: String = "NOT_STARTED",
        //var status: ExerciseActivity.Status = ExerciseActivity.Status.NOT_STARTED
){
    fun upReady() { ready += 1}
    fun upSet() { set += 1 }
    fun upInterval() { interval += 1}
    fun upTick() { tick += 1 }
    fun upRep() { rep += 1 }

    //fun reset() { tick = 0; rep = 0; set = 0; interval = 0; ready = 0 }
}
class ExerciseSound {
    // Beep, Speech
    lateinit var soundPool: SoundPool
    private var textToSpeech: TextToSpeech? = null
    var beepHigh: Int = 0
    var beepLow: Int = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onCreate(obj :ExerciseActivity){
        textToSpeech = TextToSpeech(obj, obj)
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(2).build()
        beepHigh = soundPool.load(obj, R.raw.beep_high, 1)
        beepLow = soundPool.load(obj, R.raw.beep_low, 1)
    }
    fun onInit(stat: Int){
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
    /////
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