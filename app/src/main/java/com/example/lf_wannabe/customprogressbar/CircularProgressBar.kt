package com.example.lf_wannabe.customprogressbar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.View

/**
 * Created by lf_wannabe on 13/10/2017.
 */
class CircularProgressBar : View {

    private var intervalAngle = 8f
    private var progressAngle = 64f
    private var strokeWidth: Float = resources.getDimension(R.dimen.default_stroke_width)
    private var color: Int = Color.BLACK
    private var unselectedColor: Int = Color.WHITE
    private var startAngle:Float = -90f
    private lateinit var rectF: RectF
    private lateinit var selectedBarPaint: Paint
    private lateinit var unselectedBarPaint: Paint


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    var progress: Int = 0
        set(value) {
            if(value<=20) field = 1
            else if (value<=40) field = 2
            else if (value<=60) field = 3
            else if (value<=80) field = 4
            else field = 5

            invalidate()
        }

    private fun init(context: Context, attrs: AttributeSet?) {
        rectF = RectF()
        var typedArray: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CP, 0, 0)

        try {
            progress = typedArray.getInt(R.styleable.CP_progress, progress)
            strokeWidth  = typedArray.getDimension(R.styleable.CP_progressbar_width,strokeWidth)
            color = typedArray.getColor(R.styleable.CP_progressbar_color, color)
            unselectedColor = typedArray.getColor(R.styleable.CP_progressbar_unselectedcolor, unselectedColor)
            intervalAngle = typedArray.getFloat(R.styleable.CP_progressbar_interval, intervalAngle)
            progressAngle = typedArray.getFloat(R.styleable.CP_progressbar_partial, progressAngle)
        } finally {
            typedArray.recycle()
        }

        selectedBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        selectedBarPaint.color = color
        selectedBarPaint.style = Paint.Style.STROKE
        selectedBarPaint.strokeWidth = strokeWidth

        unselectedBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        unselectedBarPaint.color = unselectedColor
        unselectedBarPaint.style = Paint.Style.STROKE
        unselectedBarPaint.strokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var angle: Float = 360f * progress / 100

        for ( i in 0 until (360/(intervalAngle+progressAngle)).toInt()){
            canvas!!.drawArc(rectF,
                    startAngle + intervalAngle/2 + (intervalAngle+progressAngle)*i,
                    progressAngle,
                    false,
                    if(i<progress) selectedBarPaint else unselectedBarPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width  = getDefaultSize(suggestedMinimumWidth , widthMeasureSpec)
        val min = Math.min(width, height)
        setMeasuredDimension(min, min)

        rectF.set(0 + strokeWidth/2, 0 + strokeWidth/2, min - strokeWidth/2, min - strokeWidth/2)
    }
}