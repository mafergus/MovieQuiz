package com.escalivadaapps.moviequiz;

import android.os.Handler;
import android.os.Looper;

public class TimerCounter {
	private float startTime;
	private float lastUpdateTime;
	
	private long intervalMs;
	private TickCallback tickCallback;
	
	private TimerRunnable runnable = new TimerRunnable();
	
	private boolean isRunning = false;
	
	public interface TickCallback {
		public void onTick(long elapsedTimeMs);
	}

	private class TimerRunnable implements Runnable {

		@Override
		public void run() {
			float time = System.nanoTime();
			float timeElapsed = time - lastUpdateTime;
			lastUpdateTime = time;
			long timeElapsedMs = (long) (timeElapsed / 1000000);
			if (tickCallback != null && isRunning) {
				tickCallback.onTick(timeElapsedMs);
			}

			new Handler(Looper.getMainLooper()).postDelayed(this, intervalMs);
		}
	}
	
	public TimerCounter(long intervalMs) {
		this(intervalMs, null);
	}
	
	public TimerCounter(long intervalMs, final TickCallback tickCallback) {
		this.intervalMs = intervalMs;
		this.tickCallback = tickCallback;
	}
	
	public void start() {
		isRunning = true;
		lastUpdateTime = System.nanoTime();
		new Handler(Looper.getMainLooper()).postDelayed(runnable, intervalMs);
	}
	
	public void stop() {
		isRunning = false;
		new Handler(Looper.getMainLooper()).removeCallbacks(runnable);
	}
	
	public boolean isRunning() { 
		return isRunning;
	}
	
}
