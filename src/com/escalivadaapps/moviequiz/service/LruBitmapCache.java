package com.escalivadaapps.moviequiz.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {
	private static final int CACHE_SIZE_BYTES = 50 * 1024 * 1024;

	public LruBitmapCache(int maxSize) {
		super(maxSize);
	}

	public LruBitmapCache(Context ctx) {
		this(getCacheSize(ctx));
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}

	// Returns a cache size equal to approximately three screens worth of images.
	public static int getCacheSize(Context ctx) {
//		final DisplayMetrics displayMetrics = ctx.getResources().
//				getDisplayMetrics();
//		final int screenWidth = displayMetrics.widthPixels;
//		final int screenHeight = displayMetrics.heightPixels;
//		// 4 bytes per pixel
//		final int screenBytes = screenWidth * screenHeight * 4;
//
//		return screenBytes * 3;
		
		return CACHE_SIZE_BYTES;
	}
}
