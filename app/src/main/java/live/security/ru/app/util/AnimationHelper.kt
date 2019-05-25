package ru.security.live.util

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation

/**
 * @author sardor
 */
class AnimationHelper {
    companion object {
        fun rotate90Left(view: View, listener: AnimationEndListener) {
            val rotate = RotateAnimation(-90f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotate.duration = 200
            rotate.interpolator = LinearInterpolator()
            rotate.fillAfter = true
            rotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    listener.onAnimationEnd(animation)
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })

            view.startAnimation(rotate)
        }

        fun rotate90Right(view: View, listener: AnimationEndListener) {
            val rotate = RotateAnimation(0f, -90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotate.duration = 200
            rotate.interpolator = LinearInterpolator()
            rotate.fillAfter = true
            rotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    listener.onAnimationEnd(animation)
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })

            view.startAnimation(rotate)
        }
    }
}