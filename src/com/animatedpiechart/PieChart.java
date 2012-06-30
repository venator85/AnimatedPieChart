package com.animatedpiechart;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

public class PieChart extends View {
	private Paint[] paints;
	private float[] startAngles;
	private float[] sliceSizes;

	private float mCurrAngle = 0f;
	private int curSlice = 0;
	private boolean shouldDraw;

	private RectF bounds;
	private Rect tempBounds;

	public PieChart(Context context, float[] slices) {
		super(context);
		paints = new Paint[slices.length];
		for (int i = 0; i < paints.length; i++) {
			paints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		float total = 0.0f;
		for (int i = 0; i < slices.length; i++) {
			total += slices[i];
		}

		sliceSizes = new float[slices.length];
		for (int i = 0; i < slices.length; i++) {
			sliceSizes[i] = slices[i] / total * 360.0f;
		}

		float sliceStart = 0.0f;
		startAngles = new float[slices.length];
		for (int i = 0; i < sliceSizes.length; i++) {
			startAngles[i] = sliceStart + sliceSizes[i];
			sliceStart = startAngles[i];
		}

		bounds = new RectF();
		tempBounds = new Rect();
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		if (!shouldDraw) {
			return;
		}

		canvas.getClipBounds(tempBounds);
		bounds.set(tempBounds);

		float startAngle = 0f;
		for (int i = 0; i < curSlice; i++) {
			startAngle = (i == 0) ? 0f : startAngles[i - 1];
//			Log.e("onDraw", String.format("drawing previous slice %d: start: %f, size: %f", i, startAngle, sliceSizes[i] ));
			canvas.drawArc(bounds, startAngle, sliceSizes[i], true, paints[i]);
			if (i == (curSlice - 1)) {
				startAngle = startAngles[i];
			}
		}
//		Log.e("onDraw", String.format("drawing current slice %d: start: %f, size: %f", curSlice, startAngle, mCurrAngle - startAngle ));
		canvas.drawArc(bounds, startAngle, mCurrAngle - startAngle, true, paints[curSlice]);
		if (mCurrAngle >= startAngles[curSlice]) {
			curSlice++;
		}
	}

	public void anima() {
		Log.e("Pacman", "anima");
		curSlice = 0;

		Random random = new Random();
		for (int i = 0; i < paints.length; i++) {
			int r = random.nextInt(256);
			int g = random.nextInt(256);
			int b = random.nextInt(256);
			int color = Color.argb(0xff, r, g, b);
			paints[i].setColor(color);
		}

		shouldDraw = true;
		ValueAnimator animator = ValueAnimator.ofObject(new AngleEvaluator(), 0.0f, 360.0f);
		animator.setDuration(2000);
		animator.start();
	}

	public float getAngle(float x, float y) {

		final float mCenterX = bounds.width() / 2;
		final float mCenterY = bounds.height() / 2;

		float a = y - mCenterY;
		float c = x - mCenterX;
		Log.e("getAngle", String.format("a: %f, c: %f", a, c));

		double angle = (Math.toDegrees(Math.atan2(a, c)) + 360.0) % 360.0;

		Log.e("getAngle", String.format("angle: %f", angle));

		return (float) angle;
	}

	public boolean isOnPieChart(float x, float y) {
		// Using a bit of Pythagoras
		// inside circle if (x-center_x)**2 + (y-center_y)**2 <= radius**2:

		final float mCenterX = bounds.width() / 2;
		final float mCenterY = bounds.height() / 2;

		double distance = Math.sqrt(Math.pow(x - mCenterX, 2) + Math.pow(y - mCenterY, 2));
		Log.e("isOnPieChart", "radius: " + mCenterX);
		Log.e("isOnPieChart", "distance: " + distance);

		boolean isOnPieChart = distance <= mCenterX;
		return isOnPieChart;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {

			float x = event.getX();
			float y = event.getY();

			Log.e("onTouchEvent", String.format("touch up - x: %f, y: %f", x, y));

			boolean inChart = isOnPieChart(x, y);
			float angle = getAngle(x, y);

			int k = -1;
			for (int i = 0; i < startAngles.length; i++) {
				float startAngle = startAngles[i];
				float sliceSize = sliceSizes[i];
				float endAngle = startAngle + sliceSize;

				Log.e("onTouchEvent", String.format("slice %d, angle: %f, startAngle: %f, endAngle: %f", i, angle, startAngle, endAngle));
				if (angle >= startAngle && angle <= (startAngle + sliceSize)) {
					k = i;
					break;
				}
			}

			Toast.makeText(getContext(), "inChart: " + inChart + "\nangle: " + angle + "\nslice: " + k, Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onTouchEvent(event);
	}

	private class AngleEvaluator extends FloatEvaluator {
		@Override
		public Float evaluate(float fraction, Number startValue, Number endValue) {
			float num = (Float) super.evaluate(fraction, startValue, endValue);
			mCurrAngle = num;
			invalidate();
			return num;
		}
	}

	private String printArr(float[] v) {
		StringBuilder b = new StringBuilder();
		b.append("[");
		if (v.length > 0) {
			b.append(v[0]);
			for (int i = 1; i < v.length; i++) {
				b.append(", ");
				b.append(v[i]);
			}
		}
		b.append("]");
		return b.toString();
	}
}