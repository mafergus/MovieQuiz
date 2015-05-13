package com.escalivadaapps.moviequiz;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.escalivadaapps.moviequiz.R;
import com.escalivadaapps.moviequiz.TimerCounter.TickCallback;

public class GameTimer extends RelativeLayout {
	final private static String TAG = GameTimer.class.getCanonicalName();
	final private static int TICK_INTERVAL_MS = 200;

	private ProgressBar timerProgress;

	private long startTimeMs = 0;
	private long elapsedTimeMs = 0;
	private long totalRunTimeMs = 0;

	private TimerCounter timerCounter = null;

	private Handler h = new Handler(Looper.getMainLooper());

	private Callback callback;
	public interface Callback {
		public void onEnded(long totalRunTimeMs);
	}

	public GameTimer(Context context) {
		this(context, null);
	}
	public GameTimer(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public GameTimer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		LayoutInflater.from(context).inflate(R.layout.game_timer, this);
		timerProgress = (ProgressBar)findViewById(R.id.timerProgress);
		timerProgress.setRotation(180);
		timerProgress.setProgress(0);
	}

	public void init(long startTimeMs) {
		this.startTimeMs = startTimeMs;
		this.elapsedTimeMs = 0;
		this.totalRunTimeMs = 0;
		setProgress(0);
		timerCounter = new TimerCounter(TICK_INTERVAL_MS, new TickCallback() {

			@Override
			public void onTick(final long elapsedTimeMs) {
				h.post(new Runnable() {

					@Override
					public void run() {
						double percentage = ((double)GameTimer.this.elapsedTimeMs / (double)GameTimer.this.startTimeMs) * 100.0;
						Log.v(TAG, "onTick elapsedTimeMs: " + GameTimer.this.elapsedTimeMs + " startTimeMs: " + GameTimer.this.startTimeMs + " percentage: " + percentage);
						GameTimer.this.elapsedTimeMs += elapsedTimeMs;

						if (timerCounter.isRunning()) {
							if (GameTimer.this.elapsedTimeMs > GameTimer.this.startTimeMs) {
								if (callback != null) {
									callback.onEnded(totalRunTimeMs);
								}
								stop();
								setProgress(100);
							} else {
								updateProgress();
							}
						}
					}
				});
			}
		});
	}

	public void init(int startTimeMs, final Callback callback) {
		init(startTimeMs);
		this.callback = callback;
	}

	public void start() {
		if (timerCounter != null) {
			timerCounter.start();
		}
	}

	public void stop() {
		if (timerCounter != null) {
			timerCounter.stop();
		}
	}
	
	public void reset() {
		if (timerCounter != null) {
			init(this.startTimeMs);
		}
	}
	
	public int getProgress() {
		return timerProgress.getProgress();
	}

	public void setOnEndedCallback(final Callback callback) {
		this.callback = callback;
	}

	private void updateProgress() {
		double percentage = ((double)GameTimer.this.elapsedTimeMs / (double)GameTimer.this.startTimeMs) * 100.0;
		int perc = (int)percentage;
		timerProgress.setProgress(100-perc);
//		timerProgress.setSecondaryProgress(100-perc+5);
	}

	private void setProgress(int percent) {
		timerProgress.setProgress(100-percent);
//		timerProgress.setSecondaryProgress(100-percent+5);
	}
}
