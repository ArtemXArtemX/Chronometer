package com.example.chronometer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private var isRunning: Boolean = false

    private val ticker = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedMs += TICK_MS
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

        btnStart.setOnClickListener { startTimer() }
        btnPause.setOnClickListener { pauseTimer() }
        btnReset.setOnClickListener { resetTimer() }

        updateTimerText()
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(ticker)
        }
    }

    private fun pauseTimer() {
        isRunning = false
        handler.removeCallbacks(ticker)
    }

    private fun resetTimer() {
        isRunning = false
        handler.removeCallbacks(ticker)
        elapsedMs = 0L
        updateTimerText()
    }

    private fun updateTimerText() {
        val totalSeconds = elapsedMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        tvTimer.text = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(ticker)
    }

    companion object {
        private const val TICK_MS = 1000L
    }
}