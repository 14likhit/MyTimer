package com.test.mytimer.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder

enum class TimerState { RUNNING, TERMINATED }

class TimerService : Service() {

    companion object {
        var state = TimerState.TERMINATED
    }

    private lateinit var timer: CountDownTimer

    private val foreGroundId = 55
    private var secondsRemaining: Long = 0
    private var setTime: Long = 0
    private lateinit var showTime: String

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                "PLAY" -> {
                    playTimer(
                        intent.getLongExtra("setTime", 0L)
                    )
                }
                "TERMINATE" -> terminateTimer()
            }
        }
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        if (::timer.isInitialized) {
            timer.cancel()
            state = TimerState.TERMINATED
        }
        TimerNotification.removeNotification()
        stopSelf()
    }

    private fun playTimer(setTime: Long) {

        this.setTime = setTime
        secondsRemaining = setTime
        startForeground(foreGroundId, TimerNotification.createNotification(this, setTime))

        timer = object : CountDownTimer(secondsRemaining, 1000) {
            override fun onFinish() {
                state = TimerState.TERMINATED
                val minutesUntilFinished = setTime / 1000 / 60
                val secondsInMinuteUntilFinished = ((setTime / 1000) - minutesUntilFinished * 60)
                val secondsStr = secondsInMinuteUntilFinished.toString()
                val showTime =
                    "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
                TimerNotification.removeNotification()
            }

            override fun onTick(millisUntilFinished: Long) {
                TimerNotification.updateUntilFinished(millisUntilFinished + (1000 - (millisUntilFinished % 1000)) - 1000)
                secondsRemaining = millisUntilFinished
                updateCountdownUI()
            }
        }.start()

        state = TimerState.RUNNING
    }


    private fun terminateTimer() {
        if (::timer.isInitialized) {
            timer.cancel()
            state = TimerState.TERMINATED
            TimerNotification.removeNotification()
            stopSelf()
        }
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = (secondsRemaining / 1000) / 60
        val secondsInMinuteUntilFinished = ((secondsRemaining / 1000) - minutesUntilFinished * 60)
        val secondsStr = secondsInMinuteUntilFinished.toString()
        showTime =
            "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
        //Update Notification
        TimerNotification.updateTimeLeft(this, showTime)
    }

}