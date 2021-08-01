package com.custom.managecalls

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.custom.managecalls.listeners.OnSlideDownDismissTouchListener
import kotlinx.android.synthetic.main.activity_incoming_call.*


class IncomingCallActivity : AppCompatActivity() {

    var vibrator: Vibrator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        fabIncomingCall.slideUp()
//        fabIncomingCall.setOnClickListener {
//            when (it.tag) {
//                "slideUp" -> {
//                    it.tag = "slideDown"
//                    it.slideUp(800)
//                }
//                "slideDown" -> {
//                    vibrator?.cancel()
//                    it.tag = "slideUp"
//                    it.slideDown(800)
//                }
//                else -> {
//                    it.tag = "slideUp"
//                    // Get instance of Vibrator from current Context
//
//                }
//            }
//        }

        fabIncomingCall.setOnTouchListener(object: OnSlideDownDismissTouchListener(fabIncomingCall, null,
            "1212121212",
            object : DismissCallbacks {
                override fun onDismiss(view: View?, token: Any?) {

                }

                override fun canDismiss(token: Any?): Boolean {
                    return true
                }


            }){

        })
    }


    fun View.slideUp(duration: Int = 1100) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(
                            700,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator?.vibrate(1000)
                }
                this@slideUp.slideDown(800)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        this.startAnimation(animate)
    }

    fun View.slideDown(duration: Int = 1100) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
        animate.duration = duration.toLong()
        animate.fillAfter = true
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                this@slideDown.slideUp(800)
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        this.startAnimation(animate)
    }
}
