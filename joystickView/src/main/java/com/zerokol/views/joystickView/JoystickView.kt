package com.zerokol.views.joystickView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class JoystickView : View, Runnable {
    // Constants
    private val RAD = 57.2957795
    // Variables
    private var onJoystickMoveListener // Listener
            : OnJoystickMoveListener? = null
    private var thread: Thread? = Thread(this)
    private var loopInterval = DEFAULT_LOOP_INTERVAL
    private var xPosition = 0 // Touch x position
    private var yPosition = 0 // Touch y position
    private var centerX = 0.0 // Center view x position
    private var centerY = 0.0 // Center view y position
    private val mainCircle: Paint
    private val secondaryCircle: Paint
    private val button: Paint
    private val horizontalLine: Paint
    private val verticalLine: Paint
    private var joystickRadius = 0
    private var buttonRadius = 0
    private var lastAngle = 0
    private val lastPower = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defaultStyle: Int) : super(context, attrs, defaultStyle)

    init {
        mainCircle = Paint(Paint.ANTI_ALIAS_FLAG)
        mainCircle.color = Color.WHITE
        mainCircle.style = Paint.Style.FILL_AND_STROKE
        secondaryCircle = Paint()
        secondaryCircle.color = Color.GREEN
        secondaryCircle.style = Paint.Style.STROKE
        verticalLine = Paint()
        verticalLine.strokeWidth = 5f
        verticalLine.color = Color.RED
        horizontalLine = Paint()
        horizontalLine.strokeWidth = 2f
        horizontalLine.color = Color.BLACK
        button = Paint(Paint.ANTI_ALIAS_FLAG)
        button.color = Color.RED
        button.style = Paint.Style.FILL
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }
    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        // before measure, get the center of view
        xPosition = width / 2
        yPosition = width / 2
        val d = Math.min(xNew, yNew)
        buttonRadius = (d / 2 * 0.25).toInt()
        joystickRadius = (d / 2 * 0.75).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) { // setting the measured values to resize the view to a certain width and
// height
        val d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec))
        setMeasuredDimension(d, d)
    }

    private fun measure(measureSpec: Int): Int {
        var result = 0
        // Decode the measurement specifications.
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        result = if (specMode == MeasureSpec.UNSPECIFIED) { // Return a default size of 200 if no bounds are specified.
            200
        } else { // As you want to fill the available space
// always return the full available bounds.
            specSize
        }
        return result
    }

    override fun onDraw(canvas: Canvas) { // super.onDraw(canvas);
        centerX = width / 2.toDouble()
        centerY = height / 2.toDouble()
        // painting the main circle
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), joystickRadius.toFloat(),
                mainCircle)
        // painting the secondary circle
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), joystickRadius / 2.toFloat(),
                secondaryCircle)
        // paint lines
        canvas.drawLine(centerX.toFloat(), centerY.toFloat(), centerX.toFloat(),
                (centerY - joystickRadius).toFloat(), verticalLine)
        canvas.drawLine((centerX - joystickRadius).toFloat(), centerY.toFloat(),
                (centerX + joystickRadius).toFloat(), centerY.toFloat(),
                horizontalLine)
        canvas.drawLine(centerX.toFloat(), (centerY + joystickRadius).toFloat(),
                centerX.toFloat(), centerY.toFloat(), horizontalLine)
        // painting the move button
        canvas.drawCircle(xPosition.toFloat(), yPosition.toFloat(), buttonRadius.toFloat(), button)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        xPosition = event.x.toInt()
        yPosition = event.y.toInt()
        val abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
                + (yPosition - centerY) * (yPosition - centerY))
        if (abs > joystickRadius) {
            xPosition = ((xPosition - centerX) * joystickRadius / abs + centerX).toInt()
            yPosition = ((yPosition - centerY) * joystickRadius / abs + centerY).toInt()
        }
        invalidate()
        if (event.action == MotionEvent.ACTION_UP) {
            xPosition = centerX.toInt()
            yPosition = centerY.toInt()
            thread!!.interrupt()
            if (onJoystickMoveListener != null) onJoystickMoveListener!!.onValueChanged(angle, power,
                    direction)
        }
        if (onJoystickMoveListener != null
                && event.action == MotionEvent.ACTION_DOWN) {
            if (thread != null && thread!!.isAlive) {
                thread!!.interrupt()
            }
            thread = Thread(this)
            thread!!.start()
            if (onJoystickMoveListener != null) onJoystickMoveListener!!.onValueChanged(angle, power,
                    direction)
        }
        return true
    }

    private val angle: Int
        private get() {
            if (xPosition > centerX) {
                if (yPosition < centerY) {
                    return (Math.atan((yPosition - centerY)
                            / (xPosition - centerX))
                            * RAD + 90).toInt().also{ lastAngle = it }
                } else return if (yPosition > centerY) {
                    (Math.atan((yPosition - centerY)
                            / (xPosition - centerX)) * RAD).toInt() + 90.also { lastAngle = it }
                } else {
                    90.also { lastAngle = it }
                }
            } else if (xPosition < centerX) {
                if (yPosition < centerY) {
                    return (Math.atan((yPosition - centerY)
                            / (xPosition - centerX))
                            * RAD - 90).toInt().also{ lastAngle = it }
                } else return if (yPosition > centerY) {
                    (Math.atan((yPosition - centerY)
                            / (xPosition - centerX)) * RAD).toInt() - 90.also { lastAngle = it }
                } else {
                    -90.also { lastAngle = it }
                }
            } else {
                return if (yPosition <= centerY) {
                    0.also { lastAngle = it }
                } else {
                    if (lastAngle < 0) {
                        -180.also { lastAngle = it }
                    } else {
                        180.also { lastAngle = it }
                    }
                }
            }
        }

    private val power: Int
        private get() = (100 * Math.sqrt((xPosition - centerX)
                * (xPosition - centerX) + (yPosition - centerY)
                * (yPosition - centerY)) / joystickRadius).toInt()

    private val direction: Int
        private get() {
            if (lastPower == 0 && lastAngle == 0) {
                return 0
            }
            var a = 0
            if (lastAngle <= 0) {
                a = lastAngle * -1 + 90
            } else if (lastAngle > 0) {
                a = if (lastAngle <= 90) {
                    90 - lastAngle
                } else {
                    360 - (lastAngle - 90)
                }
            }
            var direction = ((a + 22) / 45 + 1)
            if (direction > 8) {
                direction = 1
            }
            return direction
        }

    fun setOnJoystickMoveListener(listener: OnJoystickMoveListener?,
                                  repeatInterval: Long) {
        onJoystickMoveListener = listener
        loopInterval = repeatInterval
    }

    interface OnJoystickMoveListener {
        fun onValueChanged(angle: Int, power: Int, direction: Int)
    }

    override fun run() {
        while (!Thread.interrupted()) {
            post {
                if (onJoystickMoveListener != null) onJoystickMoveListener!!.onValueChanged(angle,
                        power, direction)
            }
            try {
                Thread.sleep(loopInterval)
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    companion object {
        const val DEFAULT_LOOP_INTERVAL: Long = 100 // 100 ms
        const val FRONT = 3
        const val FRONT_RIGHT = 4
        const val RIGHT = 5
        const val RIGHT_BOTTOM = 6
        const val BOTTOM = 7
        const val BOTTOM_LEFT = 8
        const val LEFT = 1
        const val LEFT_FRONT = 2
    }
}