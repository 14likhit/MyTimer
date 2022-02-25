package com.test.mytimer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.test.mytimer.databinding.ActivityMainBinding
import com.test.mytimer.service.TimerNotification
import com.test.mytimer.utils.BROADCAST_ACTION_TIMER
import com.test.mytimer.utils.BROADCAST_TIMER_SECONDS_REMAINING

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    val start = 600_00L
    var timer = start
    lateinit var countDownTimer: CountDownTimer

    lateinit var notiTimer: TimerNotification.Builder

    val timerBroadCastReciver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BROADCAST_ACTION_TIMER -> {
                    if(intent.hasExtra(BROADCAST_TIMER_SECONDS_REMAINING)){
                        //handleTimer
                            val timer = intent.getStringExtra(BROADCAST_TIMER_SECONDS_REMAINING)
                        activityMainBinding.timerCountdownLayout.countdownTv.text = timer
                        setTimerView()
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        init()
    }

    private fun init() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
            timerBroadCastReciver,
            IntentFilter(BROADCAST_ACTION_TIMER)
        )

        setClickListeners()

        setTimerInputView()

    }

    private fun setClickListeners() {
        activityMainBinding.timerInputLayout.startBtn.setOnClickListener {
            setTimerView()
            startTimerService()
        }

        activityMainBinding.timerCountdownLayout.exitBtn.setOnClickListener {
            exitTimerService()
            setTimerInputView()
        }
    }

    private fun setTimerInputView() {
        activityMainBinding.timerCountdownLayout.timerCountdownRootCl.visibility = View.GONE
        activityMainBinding.timerInputLayout.timerInputRootCl.visibility = View.VISIBLE
        val minutesUntilFinished = (timer / 1000 - 1) / 60
        val secondsInMinuteUntilFinished = ((timer / 1000 - 1) - minutesUntilFinished * 60)
        val secondsStr = secondsInMinuteUntilFinished.toString()
        val showTime =
            "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
        activityMainBinding.timerInputLayout.timerInputEtv.setText(showTime)
    }

    private fun startTimerService() {
        notiTimer = TimerNotification.Builder(this)
            .setSmallIcon(R.drawable.ic_timer)
            .setColor(R.color.purple_200)
            .setShowWhen(false)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(getPendingIntent())
            .setOnTickListener {
                //activityMainBinding.timerCountdownLayout.countdownTv.text = it.toString()
            }
            .setOnFinishListener {
                Toast.makeText(this, "timer finished", Toast.LENGTH_SHORT).show()
            }
            .setContentTitle("Timer :)")

        notiTimer.play(start)
    }

    private fun exitTimerService() {
        notiTimer.terminate()
    }

    private fun setTimerView() {
        activityMainBinding.timerInputLayout.timerInputRootCl.visibility = View.GONE
        activityMainBinding.timerCountdownLayout.timerCountdownRootCl.visibility = View.VISIBLE
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timer, 1000) {
            //            end of timer
            override fun onFinish() {
                Toast.makeText(this@MainActivity, "end timer", Toast.LENGTH_SHORT).show()
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
        val minutesUntilFinished = (timer / 1000 - 1) / 60
        val secondsInMinuteUntilFinished = ((timer / 1000 - 1) - minutesUntilFinished * 60)
        val secondsStr = secondsInMinuteUntilFinished.toString()
        val showTime =
            "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"


        activityMainBinding.timerCountdownLayout.countdownTv.text = showTime
    }

    private fun getPendingIntent(): PendingIntent {
        return Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    }
}