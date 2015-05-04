package com.escalivadaapps.moviequiz;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.escalivadaapps.moviequiz.R;
import com.escalivadaapps.moviequiz.TimerCounter.TickCallback;

public class TimerCounterTextView extends RelativeLayout {

	private TimerCounter timerCounter = new TimerCounter(1000);
	private long startTimeNs;
	private long timeToCountMs;
	private long timeLeftMs;
	private long totalRunTimeMs = 0;

	private TextView leftText;
	private TextView point;
	private TextView rightText;

	private Callback callback;

	private final Object lock = new Object();

	public interface Callback {
		public void onEnded(long totalRunTimeMs);
	}

	public TimerCounterTextView(Context context) {
		this(context, null);
	}

	public TimerCounterTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TimerCounterTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.timer_counter_text_view, this);
		this.leftText = (TextView)findViewById(R.id.timerCounterTextViewLeft);
		this.rightText = (TextView)findViewById(R.id.timerCounterTextViewRight);
		this.point = (TextView)findViewById(R.id.timerCounterTextViewPoint);
	}

	public void init(long intervalMs, long startTimeMs, final Callback callback) {
		setOnEndedCallback(callback);
		this.timeToCountMs = startTimeMs;
		timerCounter = new TimerCounter(intervalMs, new TickCallback() {

			@Override
			synchronized public void onTick(long elapsedTimeMs) {
				synchronized (lock) {
					if (timerCounter.isRunning()) {
						timeLeftMs -= elapsedTimeMs;
						totalRunTimeMs += elapsedTimeMs;
						setTextColor(getContext().getResources().getColor((timeLeftMs < 4000 ? R.color.Red : R.color.white)));
						if (timeLeftMs < 0) {
							callback.onEnded(totalRunTimeMs);
							stop();
							setDisplay(0);
							setTextColor(getContext().getResources().getColor(R.color.white));
						} else {
							setDisplay(timeLeftMs);
						}
					}
				}
			}
		});
		setDisplay(timeToCountMs);
	}

	public void setOnEndedCallback(final Callback callback) {
		this.callback = callback;
	}

	synchronized public void start() {
		synchronized (lock) {
			setDisplay(timeToCountMs);
			timeLeftMs = timeToCountMs;
			timerCounter.start();
		}
	}

	synchronized public void stop() {
		totalRunTimeMs = 0;
		timerCounter.stop();
	}

	synchronized public void reset() {
		synchronized (lock) {
			totalRunTimeMs = 0;
			timeLeftMs = timeToCountMs;
			setDisplay(timeLeftMs);
		}
	}

	synchronized public void addTime(long timeMs) {
		synchronized (lock) {
			timeLeftMs += timeMs;
			setDisplay(timeLeftMs);
		}
	}

	synchronized public void setTime(long timeMs) {
		synchronized (lock) {
			timeLeftMs = timeMs;
			setDisplay(timeLeftMs);
		}
	}

	public void setDisplay(long timeMs) {
		int hours = (int) (timeMs / (1000 * 60 * 60));
		timeMs -= (hours * 1000 * 60 * 60);
		int minutes = (int) (timeMs / (1000 * 60));
		timeMs -= (minutes * 1000 * 60);
		int seconds = (int) (timeMs / 1000);
		timeMs -= (seconds * 1000);
		int miliseconds = (int) (timeMs);

		String hoursStr = "" + (hours == 0 ? "" : hours);
		String minutesStr = "" + (minutes == 0 ? "" : String.format("%02d", minutes));
		String secondsStr = String.format("%02d", seconds);
		String milisecondsStr = String.format("%2d", miliseconds/10);

		leftText.setText("" + hoursStr + minutesStr + secondsStr);
		rightText.setText("" + milisecondsStr);
	}

	public void setTextColor(int color) {
		point.setTextColor(color);
		leftText.setTextColor(color);
		rightText.setTextColor(color);
	}

	public void setTypeface(final Typeface typeface) {
		point.setTypeface(typeface);
		leftText.setTypeface(typeface);
		rightText.setTypeface(typeface);
	}

	public void setTextSize(float sp) {
		point.setTextSize(sp);
		leftText.setTextSize(sp);
		rightText.setTextSize(sp);
	}

	public boolean isRunning() {
		return timerCounter.isRunning();
	}

}
