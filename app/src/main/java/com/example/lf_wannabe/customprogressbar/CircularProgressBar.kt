package com.example.lf_wannabe.customprogressbar

import android.content.Context
import android.content.ReceiverCallNotAllowedException
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View

/**
 * Created by lf_wannabe on 13/10/2017.
 */
class CircularProgressBar : View {

    private lateinit var selectedBarPaint: Paint
    private lateinit var unselectedBarPaint: Paint
    private var intervalAngle = 8f
    private var partialAngle = 64f
    private var strokeWidth = 12f
    private var icon: Drawable? = null
    private var alertColor: Int ?= null

    private var viewRectF: RectF = RectF()
    private var iconRectF: RectF = RectF()

    var progress: Int = 0
        set(value) {
            var part = (360/(intervalAngle+partialAngle)).toInt()
            for (i in 1 until part+1){
                progressIcon(value)
                if(value <= 0) {
                    field = 0
                    break;
                } else if(value <= 100/part*i) {
                    field = i;
                    break;
                }
            }
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {

        var typedArray: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CP, 0, 0)
        var selectedColor: Int
        var unselectedColor: Int

        try {
            progress = typedArray.getInt(R.styleable.CP_progress, 0)
            strokeWidth = typedArray.getDimension(R.styleable.CP_progressbar_stroke_width, 12f)
            intervalAngle = typedArray.getFloat(R.styleable.CP_progressbar_interval, 8f)
            typedArray.getFloat(R.styleable.CP_progressbar_partial, 5f).let {
                partialAngle = 360/it - intervalAngle
            }

            icon = typedArray.getDrawable(R.styleable.CP_progressbar_icon_src)
            var iconWidth = typedArray.getDimension(R.styleable.CP_progressbar_icon_width, 40f)
            var iconHeight = typedArray.getDimension(R.styleable.CP_progressbar_icon_height, 40f)
            iconRectF.set(0f, 0f, iconWidth, iconHeight)

            selectedColor = typedArray.getColor(R.styleable.CP_progressbar_selected_color, Color.GRAY)
            unselectedColor = typedArray.getColor(R.styleable.CP_progressbar_unselected_color, Color.BLACK)
            alertColor = typedArray.getColor(R.styleable.CP_progressbar_alert_color, Color.RED)

        } finally {
            typedArray.recycle()
        }

        selectedBarPaint = Paint();
        selectedBarPaint.color = selectedColor
        selectedBarPaint.style = Paint.Style.STROKE
        selectedBarPaint.strokeWidth = strokeWidth

        unselectedBarPaint = Paint();
        unselectedBarPaint.color = unselectedColor
        unselectedBarPaint.style = Paint.Style.STROKE
        unselectedBarPaint.strokeWidth = strokeWidth

        progressIcon(progress)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        icon?. let {
            it.setBounds((viewRectF.centerX() - iconRectF.width()/2).toInt(),
                    (viewRectF.centerY() - iconRectF.height()/2).toInt(),
                    (viewRectF.centerX() - iconRectF.width()/2).toInt() + iconRectF.width().toInt(),
                    (viewRectF.centerY() - iconRectF.height()/2).toInt() + iconRectF.height().toInt())
            it.draw(canvas)
        }

        for ( i in 0 until (360/(intervalAngle+partialAngle)).toInt()){
            canvas!!.drawArc(viewRectF,
                    -90f + intervalAngle/2 + (intervalAngle+partialAngle)*i,
                    partialAngle,
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

        viewRectF.set(0 + strokeWidth/2, 0 + strokeWidth/2, min - strokeWidth/2, min - strokeWidth/2)
    }

    private fun progressIcon(value: Int) {
        if(value <= 1) {
            icon?.setColorFilter(alertColor!!, PorterDuff.Mode.SRC_ATOP)
        } else {
            icon?.setColorFilter(alertColor!!, PorterDuff.Mode.DST)
        }
    }
}