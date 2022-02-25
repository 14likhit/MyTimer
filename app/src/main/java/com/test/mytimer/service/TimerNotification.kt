package com.test.mytimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

interface Timer {
    fun play(context: Context, timeMillis: Long)
    fun terminate(context: Context)
}

typealias onFinishListener = () -> Unit
typealias onTickListener = (Long) -> Unit

object TimerNotification : Timer {

    private var notiIcon: Int? = null
    private var notiTitle: CharSequence = ""
    private var showWhen = false
    private var notiColor = 0x66FFFFFF
    private var notificationPriority = NotificationCompat.PRIORITY_LOW
    private var isAutoCancel = false
    private var isOnlyAlertOnce = true
    private var isControlMode = false
    private var contentPendingIntent: PendingIntent? = null
    private var playBtnIcon: Int? = null
    private var pauseBtnIcon: Int? = null
    private var stopBtnIcon: Int? = null
    private var finishListener: onFinishListener? = null
    private var tickListener: onTickListener? = null

    private lateinit var channelId: String
    private lateinit var notificationManager: NotificationManagerCompat

    private var setStartTime by Delegates.notNull<Long>()

    override fun play(context: Context, timeMillis: Long) {
        if (TimerService.state == TimerState.RUNNING) return

        val playIntent = Intent(context, TimerService::class.java).apply {
            action = "PLAY"
            putExtra("setTime", timeMillis)
        }
        ContextCompat.startForegroundService(context, playIntent)
    }

    override fun terminate(context: Context) {
        if (!::notificationManager.isInitialized && TimerService.state == TimerState.TERMINATED) return

        val terminateIntent = Intent(context, TimerService::class.java).apply {
            action = "TERMINATE"
        }
        ContextCompat.startForegroundService(context, terminateIntent)
    }

    fun createNotification(context: Context, setTime: Long): Notification {
        channelId = "${context.packageName}.timer"
        notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "timer",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { setShowBadge(false) }
            notificationManager.createNotificationChannel(channel)
        }

        this.setStartTime = setTime

        val minutesUntilFinished = (setTime / 1000 - 1) / 60
        val secondsInMinuteUntilFinished = ((setTime / 1000 - 1) - minutesUntilFinished * 60)
        val secondsStr = secondsInMinuteUntilFinished.toString()
        val showTime =
            "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"

        return playStateNotification(context, showTime)
    }

    fun updateTimeLeft(context: Context, timeLeft: String) =
        notificationManager.notify(55, playStateNotification(context, timeLeft))

    fun updateUntilFinished(millisUntilFinished: Long) = tickListener?.invoke(millisUntilFinished)

    fun removeNotification() = notificationManager.cancelAll()

    private fun baseNotificationBuilder(context: Context, timeLeft: String) =
        NotificationCompat.Builder(context, channelId).apply {
            notiIcon?.let { setSmallIcon(it) }
            setContentTitle(notiTitle)
            setContentText(timeLeft)
            setShowWhen(showWhen)
            color = notiColor
            priority = notificationPriority
            setAutoCancel(isAutoCancel)
            setOnlyAlertOnce(isOnlyAlertOnce)
            contentPendingIntent?.let { setContentIntent(it) }
        }

    private fun playStateNotification(context: Context, timeLeft: String): Notification =
        baseNotificationBuilder(context, timeLeft).build()

    private fun getPlayPendingIntent(
        context: Context
    ): PendingIntent {
        val playIntent = Intent(context, TimerService::class.java).apply {
            action = "PLAY"
            putExtra("setTime", setStartTime)
        }

        return PendingIntent.getService(context, 29, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    class Builder(private val context: Context) {

        fun setSmallIcon(icon: Int): Builder {
            notiIcon = icon
            return this
        }

        fun setContentTitle(title: CharSequence): Builder {
            notiTitle = title
            return this
        }

        fun setShowWhen(show: Boolean): Builder {
            showWhen = show
            return this
        }

        fun setColor(color: Int): Builder {
            notiColor = color
            return this
        }

        fun setPriority(priority: Int): Builder {
            notificationPriority = priority
            return this
        }

        fun setAutoCancel(autoCancel: Boolean): Builder {
            isAutoCancel = autoCancel
            return this
        }

        fun setOnlyAlertOnce(onlyAlertOnce: Boolean): Builder {
            isOnlyAlertOnce = onlyAlertOnce
            return this
        }

        fun setControlMode(controlMode: Boolean): Builder {
            isControlMode = controlMode
            return this
        }

        fun setContentIntent(intent: PendingIntent): Builder {
            contentPendingIntent = intent
            return this
        }

        fun setPlayButtonIcon(icon: Int): Builder {
            playBtnIcon = icon
            return this
        }

        fun setPauseButtonIcon(icon: Int): Builder {
            pauseBtnIcon = icon
            return this
        }

        fun setStopButtonIcon(icon: Int): Builder {
            stopBtnIcon = icon
            return this
        }

        fun setOnFinishListener(listener: onFinishListener): Builder {
            finishListener = listener
            return this
        }

        fun setOnTickListener(listener: onTickListener): Builder {
            tickListener = listener
            return this
        }

        fun play(timeMillis: Long) = play(context, timeMillis)

        fun terminate() = terminate(context)
    }
}