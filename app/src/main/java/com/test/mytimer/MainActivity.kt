package com.test.mytimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.test.mytimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

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
            setTimerInputView()

        }
    }

    private fun setTimerInputView() {
        activityMainBinding.timerCountdownLayout.timerCountdownRootCl.visibility = View.GONE
        activityMainBinding.timerInputLayout.timerInputRootCl.visibility = View.VISIBLE
    }

    private fun setTimerView() {
        activityMainBinding.timerInputLayout.timerInputRootCl.visibility = View.GONE
        activityMainBinding.timerCountdownLayout.timerCountdownRootCl.visibility = View.VISIBLE
    }

    private fun startTimer(){
        val time = activityMainBinding.timerInputLayout.timerInputEtv.text
    }

    private fun exitTimer(){

    }
}