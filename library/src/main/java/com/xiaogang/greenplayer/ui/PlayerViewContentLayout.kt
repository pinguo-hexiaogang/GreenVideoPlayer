package com.xiaogang.greenplayer.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlin.math.abs

class PlayerViewContentLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    companion object {
        /**
         * 保持比例，不超过父控件
         */
        const val FILL_TYPE_FIT = 0
        /**
         * 放弃比例
         */
        const val FILL_TYPE_FILL = 1
        private const val RATION_TOLENRENCE = 0.01F
    }

    /**
     * 视频宽高比
     */
    var videoRation: Float = 1f
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    /**
     * 缩放模式
     */
    var fillType: Int = FILL_TYPE_FIT
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        if (videoRation < 0) return
        if (abs(width * 1f / height - videoRation) < RATION_TOLENRENCE) {
            return
        }
        val viewRation = width * 1f / height
        var newWidth = 0
        var newHeight = 0
        when (fillType) {
            FILL_TYPE_FIT -> {
                if (videoRation > viewRation) {
                    newWidth = width
                    newHeight = (width / videoRation).toInt()
                } else {
                    newHeight = height
                    newWidth = (newHeight * videoRation).toInt()
                }
            }
            FILL_TYPE_FILL -> {
                newWidth = width
                newHeight = height
            }
        }
        val newWidthSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY)
        val newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
        super.onMeasure(newWidthSpec, newHeightSpec)
    }

}