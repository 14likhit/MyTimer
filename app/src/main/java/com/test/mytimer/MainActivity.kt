package com.test.mytimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.test.mytimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    val start = 100_000L
    var timer = start
    lateinit var countDownTimer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        init()
    }

    private fun init() {

        setClickListeners()

        setTimerInputView()

    }

    private fun setClickListeners() {
        activityMainBinding.timerInputLayout.startBtn.setOnClickListener {
            startTimer()
            setTimerView()
        }

        activityMainBinding.timerCountdownLayout.exitBtn.setOnClickListener {
            exitTimer()
            setTimerInputView()
        }
    }

    private fun setTimerInputView() {
        activityMainBinding.timerCountdownLayout.timerCountdownRootCl.visibility = View.GONE
        activityMainBinding.timerInputLayout.timerInputRootCl.visibility = View.VISIBLE
        activityMainBinding.timerInputLayout.timerInputEtv.setText(timer.toString())
    }

    private fun setTimerView() {
        activityMainBinding.timerInputLayout.timerInputRootCl.visibility = View.GONE
        activityMainBinding.timerCountdownLayout.timerCountdownRootCl.visibility = View.VISIBLE
    }

    private fun startTimer(){
        countDownTimer = object : CountDownTimer(timer,1000){
            //            end of timer
            override fun onFinish() {
                Toast.makeText(this@MainActivity,"end timer",Toast.LENGTH_SHORT).show()
            }

            override fun onTick(millisUntilFinished: Long) {
                timer = millisUntilFinished
                setTextTimer()
            }

        }.start()
    }

    private fun exitTimer(){
        countDownTimer.cancel()
        timer = start
        setTextTimer()
    }

    fun setTextTimer() {
        val m = (timer / 1000) / 60
        val s = (timer / 1000) % 60

        val format = String.format("%02d:%02d", m, s)

        activityMainBinding.timerCountdownLayout.countdownTv.text = format
    }
}