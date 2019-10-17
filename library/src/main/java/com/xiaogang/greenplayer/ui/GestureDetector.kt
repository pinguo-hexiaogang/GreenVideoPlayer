package com.xiaogang.greenplayer.ui

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class GestureDetector(private val view: View, private val listener: TapListener) :
    GestureDetector.SimpleOnGestureListener() {
    interface TapListener {
        fun onSingleTapUp(): Boolean
        fun onDoubleTapUp(): Boolean
    }

    private val detector = GestureDetector(view.context, this)

    init {
        view.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return listener.onSingleTapUp()
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        return listener.onDoubleTapUp()
    }
}