package com.zerokol.joystick.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Joystick extends View implements Runnable {
	// Constants
	private final double RAD = 57.2957795;
	public final static long DEFAULT_LOOP_INTERVAL = 100;
	// Variables
	private OnJoystickMoveListener onJoystickMoveListener; // Listener
	private Thread thread = new Thread(this);
	private long loopInterval = DEFAULT_LOOP_INTERVAL;
	private int xPosition = 0; // Touch x position
	private int yPosition = 0; // Touch y position
	private double centerX = 0; // Center view x position
	private double centerY = 0; // Center view y position
	private Paint mainCircle;
	private Paint secondaryCircle;
	private Paint button;
	private Paint horizontalLine;
	private Paint verticalLine;
	private int joystickRadius;
	private int buttonRadius;

	public Joystick(Context context) {
		super(context);
	}

	public Joystick(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Joystick);
		initJoystickView(a);
		a.recycle();
	}

	public Joystick(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Joystick);
		initJoystickView(a);
		a.recycle();
	}

	protected void initJoystickView(TypedArray tyArr) {
		Resources r = this.getResources();

		mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainCircle
				.setColor(tyArr.getColor(R.styleable.Joystick_mainCircleColor,
						r.getColor(R.color.white)));
		mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

		secondaryCircle = new Paint();
		secondaryCircle.setColor(tyArr.getColor(
				R.styleable.Joystick_secondaryCircleColor,
				r.getColor(R.color.green)));
		secondaryCircle.setStyle(Paint.Style.STROKE);

		verticalLine = new Paint();
		verticalLine.setStrokeWidth(5);
		verticalLine.setColor(tyArr.getColor(
				R.styleable.Joystick_secondaryCircleColor,
				r.getColor(R.color.red)));

		horizontalLine = new Paint();
		horizontalLine.setStrokeWidth(2);
		horizontalLine.setColor(tyArr.getColor(
				R.styleable.Joystick_secondaryCircleColor,
				r.getColor(R.color.black)));

		button = new Paint(Paint.ANTI_ALIAS_FLAG);
		button.setColor(tyArr.getColor(
				R.styleable.Joystick_secondaryCircleColor,
				r.getColor(R.color.red)));
		button.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onFinishInflate() {
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// setting the measured values to resize the view to a certain width and
		// height
		int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

		setMeasuredDimension(d, d);

		// before measure, get the center of view
		xPosition = (int) getWidth() / 2;
		yPosition = (int) getWidth() / 2;

		buttonRadius = (int) (d / 2 * 0.25);
		joystickRadius = (int) (d / 2 * 0.75);
	}

	private int measure(int measureSpec) {
		int result = 0;

		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			// As you want to fill the available space
			// always return the full available bounds.
			result = specSize;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		centerX = (getWidth()) / 2;
		centerY = (getHeight()) / 2;

		// painting the main circle
		canvas.drawCircle((int) centerX, (int) centerY, joystickRadius,
				mainCircle);
		// painting the secondary circle
		canvas.drawCircle((int) centerX, (int) centerY, joystickRadius / 2,
				secondaryCircle);
		// paint lines
		canvas.drawLine((float) centerX, (float) centerY, (float) centerX,
				(float) (centerY - joystickRadius), verticalLine);
		canvas.drawLine((float) (centerX - joystickRadius), (float) centerY,
				(float) (centerX + joystickRadius), (float) centerY,
				horizontalLine);
		canvas.drawLine((float) centerX, (float) (centerY + joystickRadius),
				(float) centerX, (float) centerY, horizontalLine);

		// painting the move button
		canvas.drawCircle(xPosition, yPosition, buttonRadius, button);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		xPosition = (int) event.getX();
		yPosition = (int) event.getY();
		double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
				+ (yPosition - centerY) * (yPosition - centerY));
		if (abs > joystickRadius) {
			xPosition = (int) ((xPosition - centerX) * joystickRadius / abs + centerX);
			yPosition = (int) ((yPosition - centerY) * joystickRadius / abs + centerY);
		}
		invalidate();
		if (event.getAction() == MotionEvent.ACTION_UP) {
			xPosition = (int) centerX;
			yPosition = (int) centerY;
			onJoystickMoveListener.onValueChanged(getAngle(), getPower());
			thread.interrupt();
		}
		if (onJoystickMoveListener != null
				&& event.getAction() == MotionEvent.ACTION_DOWN) {
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
			thread = new Thread(this);
			thread.start();
			onJoystickMoveListener.onValueChanged(getAngle(), getPower());
		}
		return true;
	}

	public int getAngle() {
		if (xPosition > centerX) {
			if (yPosition < centerY) {
				return (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX))
						* RAD + 90);
			} else if (yPosition > centerY) {
				return (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX)) * RAD) + 90;
			} else {
				return 90;
			}
		} else if (xPosition < centerX) {
			if (yPosition < centerY) {
				return (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX))
						* RAD - 90);
			} else if (yPosition > centerY) {
				return (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX)) * RAD) - 90;
			} else {
				return 90;
			}
		} else {
			if (yPosition <= centerY) {
				return 0;
			} else {
				return 180;
			}
		}
	}

	public int getPower() {
		return (int) (100 * Math.sqrt((xPosition - centerX)
				* (xPosition - centerX) + (yPosition - centerY)
				* (yPosition - centerY)) / joystickRadius);
	}

	public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
			long repeatInterval) {
		this.onJoystickMoveListener = listener;
		this.loopInterval = repeatInterval;
	}

	public static interface OnJoystickMoveListener {
		public void onValueChanged(int angle, int power);
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			post(new Runnable() {
				public void run() {
					onJoystickMoveListener.onValueChanged(getAngle(),
							getPower());
				}
			});
			try {
				Thread.sleep(loopInterval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}