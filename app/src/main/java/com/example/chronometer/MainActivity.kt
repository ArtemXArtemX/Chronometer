package com.example.chronometer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button

    private val handler = Handler(Looper.getMainLooper())

    private var elapsedMs: Long = 0L
    private var startTime: Long = 0L
    private var isRunning: Boolean = false

    private val ticker = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedMs = SystemClock.elapsedRealtime() - startTime
                updateTimerText()
                handler.postDelayed(this, TICK_MS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTimer = findViewById(R.id.tvTimer)
        btnStart = findViewById(R.id.btnStart)
        btnPause = findViewById(R.id.btnPause)
        btnReset = findViewById(R.id.btnReset)

        if (savedInstanceState != null) {
            elapsedMs = savedInstanceState.getLong(KEY_ELAPSED, 0L)
            isRunning = savedInstanceState.getBoolean(KEY_RUNNING, false)
            if (isRunning) {
                startTime = SystemClock.elapsedRealtime() - elapsedMs
                handler.post(ticker)
            }
        }

        btnStart.setOnClickListener { startTimer() }
        btnPause.setOnClickListener { pauseTimer() }
        btnReset.setOnClickListener { resetTimer() }

        updateTimerText()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isRunning) {
            elapsedMs = SystemClock.elapsedRealtime() - startTime
        }
        outState.putLong(KEY_ELAPSED, elapsedMs)
        outState.putBoolean(KEY_RUNNING, isRunning)
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            startTime = SystemClock.elapsedRealtime() - elapsedMs
            handler.post(ticker)
        }
    }

    private fun pauseTimer() {
        if (isRunning) {
            elapsedMs = SystemClock.elapsedRealtime() - startTime
            isRunning = false
            handler.removeCallbacks(ticker)
            updateTimerText()
        }
    }

    private fun resetTimer() {
        isRunning = false
        handler.removeCallbacks(ticker)
        elapsedMs = 0L
        updateTimerText()
    }

    private fun updateButtons() {
        btnStart.isEnabled = !isRunning
        btnPause.isEnabled = isRunning
        btnReset.isEnabled = elapsedMs > 0L
    }

    private fun updateTimerText() {
        val totalSeconds = elapsedMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        tvTimer.text = String.format(Locale.US, "%02d:%02d", minutes, seconds)
        updateButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(ticker)
    }

    companion object {
        private const val TICK_MS = 100L
        private const val KEY_ELAPSED = "key_elapsed"
        private const val KEY_RUNNING = "key_running"
    }
}