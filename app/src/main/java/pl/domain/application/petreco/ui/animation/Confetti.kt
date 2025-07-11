package pl.domain.application.petreco.ui.animation

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import android.widget.RelativeLayout

fun startConfettiAnimation(activity: Activity, parentView: ViewGroup) {
    val colors = listOf(
        Color.parseColor("#A8E6CF"), // Mint Green
        Color.parseColor("#81C784"), // Light Green
        Color.parseColor("#D0F8CE"), // Pale Green
        Color.parseColor("#66BB6A"), // Medium Green
        Color.parseColor("#388E3C"), // Forest Green
        Color.parseColor("#C8E6C9"), // Soft Green
        Color.parseColor("#AED581"), // Yellow-Green
        Color.parseColor("#B2FF59")  // Lime Accent
    )

    val confettiCount = 100

    repeat(confettiCount) {
        val confetti = View(activity).apply {
            setBackgroundColor(colors.random())
            layoutParams = RelativeLayout.LayoutParams(20, 20)
            x = (0..parentView.width).random().toFloat()
            y = -50f
        }

        parentView.addView(confetti)

        val fallDuration = (1000..2500).random().toLong()
        val fallAnimator = ObjectAnimator.ofFloat(confetti, "translationY", -50f, parentView.height + 100f).apply {
            duration = fallDuration
            interpolator = AccelerateDecelerateInterpolator()
        }

        val rotationAnimator = ObjectAnimator.ofFloat(confetti, "rotation", 0f, (360..1440).random().toFloat()).apply {
            duration = fallDuration
        }

        AnimatorSet().apply {
            playTogether(fallAnimator, rotationAnimator)
            doOnEnd {
                parentView.removeView(confetti)
            }
            start()
        }
    }
}