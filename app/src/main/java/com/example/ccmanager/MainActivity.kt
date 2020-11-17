package com.example.ccmanager


import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.round

////////////////////////////////////////////////////////////////////////////////
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    enum class Status { NOT_STARTED, READY, RUNNING, PAUSE, INTERVAL, DONE}

    // Timers
    lateinit var timer: CountDownTimer

    // Counters
    var iTick: Int = 0
    var iRep: Int = 0
    var iSet: Int = 0
    var iInterval: Int = 0
    var iReady: Int = 0

    var iMaxReps: Int = 2
    var iMaxSets: Int = 3
    var iMaxIntervals: Int = 10
    var iMaxReadies: Int = 6
    var status: Status = Status.NOT_STARTED

    // Beep, Speech
    lateinit var soundPool: SoundPool
    private var textToSpeech: TextToSpeech? = null
    //private var tg = ToneGenerator(android.media.AudioManager.STREAM_DTMF, ToneGenerator.MAX_VOLUME)
    var beepHigh: Int = 0
    var beepLow: Int = 0

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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)// TODO： initialize for Text To Speech
        setContentView(R.layout.activity_main)

        // sound
        textToSpeech = TextToSpeech(this, this)
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(2).build()
        beepHigh = soundPool.load(this, R.raw.beep_high, 1)
        beepLow =  soundPool.load(this, R.raw.beep_low, 1)

        //var pu_adaptor = ArrayAdapter.createFromResource(this, R.array.pu_steps, android.R.layout.simple_spinner_item )
        val step_adaptors = arrayOf (
                ArrayAdapter.createFromResource(this, R.array.ps_steps, android.R.layout.simple_spinner_dropdown_item ),
                ArrayAdapter.createFromResource(this, R.array.sq_steps, android.R.layout.simple_spinner_dropdown_item ),
                ArrayAdapter.createFromResource(this, R.array.pl_steps, android.R.layout.simple_spinner_dropdown_item ),
                ArrayAdapter.createFromResource(this, R.array.lr_steps, android.R.layout.simple_spinner_dropdown_item ),
                ArrayAdapter.createFromResource(this, R.array.br_steps, android.R.layout.simple_spinner_dropdown_item ),
                ArrayAdapter.createFromResource(this, R.array.hs_steps, android.R.layout.simple_spinner_dropdown_item )
        )

        spinnerEvents.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerSteps.setAdapter(step_adaptors[spinnerEvents.selectedItemPosition])
                Log.d("snipperevent", "pos: ${spinnerEvents.selectedItemPosition}")
                updateMaxCounters()
                updateUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
        spinnerSteps.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateMaxCounters()
                updateUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }
        spinnerGrades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateMaxCounters()
                updateUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }
    }

    fun updateMaxCounters() {
        val pos_events = spinnerEvents.selectedItemPosition
        val pos_steps = spinnerSteps.selectedItemPosition
        val pos_grades = spinnerGrades.selectedItemPosition
        val dataset = MaxSetsRepsDataset()

        iMaxSets = dataset.get_max_sets(pos_events, pos_steps, pos_grades)
        iMaxReps = dataset.get_max_reps(pos_events, pos_steps, pos_grades)
        Log.d("max sets reps", "sets: ${iMaxSets}, reps: ${iMaxReps}")
    }

    public fun buttonStart(view: View) {
        btnStart.isClickable = false
        iTick = 0
        iRep = 0
        iSet = 0
        iInterval = 0
        timer = startReadyTimer()
    }
    public fun buttonStop(view: View) {
        btnStart.isClickable = true
        stopTimer()
        //finishExercise()
    }

    /////////////////////////////////////////////////////////////////
    // timers
    fun startReadyTimer() : CountDownTimer {
        status = Status.READY
        iReady = 0
        //iSec = 0

        return object: CountDownTimer(6000L, 1000L) {
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
            override fun onTick(millisUntilFinished: Long){
                //val sec: Int = round(millisUntilFinished / 1000F).toInt()
                //iTick = 6 - sec % 6
                //Log.d("MainTimer", "[${status.toString()}] millis: ${millisUntilFinished}, sec: ${sec}, tick: ${iTick}")
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
            override fun onFinish() {
                if (iSet == iMaxSets){
                    speakText("well done ! you have done ${spinnerGrades.selectedItem.toString()}")
                    finishExercise()
                    btnStart.isClickable = true
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

    fun stopTimer(){
        if (::timer.isInitialized) { timer.cancel()  }
        btnStart.isClickable = true
        status = Status.NOT_STARTED
        updateUI()
    }
    fun finishExercise() {
        status = Status.DONE
        val event = spinnerEvents.selectedItem.toString()
        val step = spinnerSteps.selectedItem.toString()
        val grade = spinnerGrades.selectedItem.toString()

        val msg = "Completed ${event} / ${step} / ${grade}"
        val args = Bundle()
        args.putString("message", msg)

        Log.d("finish", msg)
        val dialog = FinishedDialog()
        dialog.setArguments(args)
        dialog.show(supportFragmentManager, "NoticeDialog")
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
    // Utilities
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


//////////////////////////////
class MaxSetsRepsDataset {
    val max_sets_reps = arrayOf(
            // Push Ups
            arrayOf(
                    arrayOf(arrayOf(1, 10), arrayOf(2, 25), arrayOf(3, 50)),
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
