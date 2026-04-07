package com.example.specialkeyboard

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var tvAppName: TextView
    private lateinit var tvTagline: TextView
    private lateinit var tvLoading: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tvAppName = findViewById(R.id.tvAppName)
        tvTagline = findViewById(R.id.tvTagline)
        tvLoading = findViewById(R.id.tvLoading)

        startAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2800)
    }

    private fun startAnimations() {
        // App name fade + slide up
        tvAppName.alpha = 0f
        tvAppName.translationY = 80f
        tvAppName.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(1000)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Tagline fade slightly later
        tvTagline.alpha = 0f
        tvTagline.translationY = 50f
        tvTagline.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(350)
            .setDuration(900)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Loading text pulse
        val scaleX = ObjectAnimator.ofFloat(tvLoading, "scaleX", 1f, 1.08f, 1f)
        scaleX.duration = 900
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleX.repeatMode = ValueAnimator.RESTART
        scaleX.start()

        val scaleY = ObjectAnimator.ofFloat(tvLoading, "scaleY", 1f, 1.08f, 1f)
        scaleY.duration = 900
        scaleY.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatMode = ValueAnimator.RESTART
        scaleY.start()

        // Animated dots for loading text
        startLoadingDotsAnimation()
    }

    private fun startLoadingDotsAnimation() {
        val handler = Handler(Looper.getMainLooper())
        val baseText = "Loading"

        val runnable = object : Runnable {
            var dots = 0

            override fun run() {
                dots = (dots + 1) % 4
                tvLoading.text = baseText + ".".repeat(dots)
                handler.postDelayed(this, 400)
            }
        }

        handler.post(runnable)
    }
}