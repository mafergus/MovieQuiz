package com.escalivadaapps.moviequiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.escalivadaapps.moviequiz.service.MovieImageData;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class Question {
	private static final String TAG = Question.class.getCanonicalName();

	private int remaining;
	final Map<String, Bitmap> images = new HashMap<String, Bitmap>();
	final private Callback callback;

	final public String title;
	final public MovieImageData correctAnswer;
	final public MovieImageData wrongAnswer1;
	final public MovieImageData wrongAnswer2;
	final public MovieImageData wrongAnswer3;

	public interface Callback { 
		public void onComplete(final Question question);
	}

	public Question(String title,
			MovieImageData correctAnswer, 
			MovieImageData wrongAnswer1, 
			MovieImageData wrongAnswer2, 
			MovieImageData wrongAnswer3, Callback callback) {

		this.title = title;
		this.correctAnswer = correctAnswer;
		this.wrongAnswer1 = wrongAnswer1;
		this.wrongAnswer2 = wrongAnswer2;
		this.wrongAnswer3 = wrongAnswer3;

		List<String> imageList = new ArrayList<String>();
		imageList.add(this.correctAnswer.url);
		imageList.add(this.wrongAnswer1.url);
		imageList.add(this.wrongAnswer2.url);
		imageList.add(this.wrongAnswer3.url);
		Collections.shuffle(imageList);

		images.put(imageList.get(0), null);
		images.put(imageList.get(1), null);
		images.put(imageList.get(2), null);
		images.put(imageList.get(3), null);
		this.callback = callback;
		remaining = images.size();
	}

	public void load(final Context context) {
		Log.v(TAG, "load()");
		Set<String> keys = images.keySet();
		for (String url : keys) {
			NonViewAware imageAware = new NonViewAware(new ImageSize(500, 281), ViewScaleType.CROP); // don't pass URI here
			ImageLoader.getInstance().displayImage(url, imageAware, 
					((MovieQuizApplication)context.getApplicationContext()).getImageOptions(), 
					new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					Log.v(TAG, "onLoadingStarted()");
				}
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					Log.v(TAG, "onLoadingFailed()");
				}
				@Override
				public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
					Log.v(TAG, "onLoadingComplete " + imageUri);

					new Handler(Looper.getMainLooper()).post(new Runnable() {

						@Override
						public void run() {
							images.put(imageUri, loadedImage);
							remaining--;
							if (remaining == 0) {
								Log.v(TAG, "loaded all images should trigger callback for " + correctAnswer.mdId);
								callback.onComplete(Question.this);
							}
						}
					});
				}
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					Log.v(TAG, "onLoadingCancelled()");
				}
			}, null);
		}
	}

	public boolean isLoaded() {
		return (remaining == 0);
	}

	@Override
	public String toString() {
		return "Question Title: " + title + " CA: " + correctAnswer.toString() + "\n"
				+ " WR1: " + wrongAnswer1.toString() + "\n" + " WR2: " + wrongAnswer2.toString() + "\n"
				+ " WR3: " + wrongAnswer3.toString() + "\n\n";
	}

}
