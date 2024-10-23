package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f
    private var buttonWidth: Float = 0f
    private var buttonHeight: Float = 0f
    private var downloadButtonColor: Int = 0
    private var loadingButtonColor: Int = 0
    private var buttonTextDownload: String = ""
    private var buttonTextLoading: String = ""
    private var circleAngle = 0f

    private val buttonPaint = Paint().apply {
        color = downloadButtonColor
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
    }

    private val circlePaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private var buttonState: ButtonState = ButtonState.Completed

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            downloadButtonColor = getColor(R.styleable.LoadingButton_buttonColor1, 0)
            loadingButtonColor = getColor(R.styleable.LoadingButton_buttonColor2, 0)
            buttonTextDownload = getString(R.styleable.LoadingButton_buttonText1) ?: ""
            buttonTextLoading = getString(R.styleable.LoadingButton_buttonText2) ?: ""
            textPaint.color = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            circlePaint.color = getColor(R.styleable.LoadingButton_circleColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (buttonState == ButtonState.Completed) {
            setButtonWidth(widthSize)
            setButtonHeight(heightSize)

            buttonPaint.color = downloadButtonColor

            // Draw the download button background as a rectangle
            canvas.drawRect(0f, 0f, widthSize, buttonHeight, buttonPaint)

            // Calculate the x and y position for the text
            val xPos = buttonWidth / 2
            val yPos = (buttonHeight / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

            // Draw the text on the button
            canvas.drawText(buttonTextDownload, xPos, yPos, textPaint)
        } else {
            buttonPaint.color = downloadButtonColor

            // Draw the download button background as a rectangle
            canvas.drawRect(0f, 0f, widthSize, buttonHeight, buttonPaint)

            buttonPaint.color = loadingButtonColor
            // Draw the loading button background as a rectangle
            canvas.drawRect(0f, 0f, buttonWidth, buttonHeight, buttonPaint)

            // Calculate the x and y position for the text
            val xPos = widthSize / 2
            val yPos = (heightSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

            // Draw the text on the button
            canvas.drawText(buttonTextLoading, xPos, yPos, textPaint)

            // Draw the circular indicator
            val circleSize = widthSize / 18
            val radius = circleSize / 2f - circlePaint.strokeWidth / 2
            canvas.drawArc(
                (widthSize / 4) * 3 - radius,
                (heightSize / 2) - radius,
                (widthSize / 4) * 3 + radius,
                (heightSize / 2) + radius,
                0f,
                circleAngle,
                true,
                circlePaint
            )
        }
    }

    private fun setButtonWidth(width: Float) {
        buttonWidth = width
        invalidate() // Request to redraw the view
    }

    private fun setButtonHeight(height: Float) {
        buttonHeight = height
        invalidate() // Request to redraw the view
    }

    private fun setCircleAngle(angle: Float) {
        circleAngle = angle
        invalidate() // Request to redraw the view
    }

    fun startAnimation() {
        buttonState = ButtonState.Loading
        setButtonWidth(0f)
        setButtonHeight(heightSize)

        val widthAnimator = ValueAnimator.ofFloat(0f, widthSize)
        widthAnimator.addUpdateListener { animation ->
            setButtonWidth(animation.animatedValue as Float)
        }

        val circleAnimator = ValueAnimator.ofFloat(0f, 360f)
        circleAnimator.repeatCount = ValueAnimator.INFINITE
        circleAnimator.addUpdateListener { animation ->
            setCircleAngle(animation.animatedValue as Float)
        }

        widthAnimator.duration = 2000
        circleAnimator.duration = 2000

        widthAnimator.start()
        circleAnimator.start()
    }

    fun completeAnimation() {
        buttonState = ButtonState.Completed
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }

}