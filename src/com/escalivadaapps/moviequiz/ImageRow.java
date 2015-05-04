package com.escalivadaapps.moviequiz;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.escalivadaapps.moviequiz.R;

public class ImageRow extends FrameLayout {
	private static final String TAG = ImageView.class.getCanonicalName();
	private int height = 0;

	public ImageRow(Context context) {
		this(context, null);
	}

	public ImageRow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageRow(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		LayoutInflater.from(context).inflate(R.layout.custom_row, this);
	}

	public ImageRow(final Context context, final int height) {
		this(context);

		this.height = height;
		this.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, this.height));

		View back = findViewById(R.id.back);
		back.setLayoutParams(new AbsoluteLayout.LayoutParams(LayoutParams.MATCH_PARENT, this.height, 0, 0));
		View front = findViewById(R.id.front);
		front.setLayoutParams(new AbsoluteLayout.LayoutParams(LayoutParams.MATCH_PARENT, this.height, 0, 0));
		View image = findViewById(R.id.image);
		image.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, this.height));

		Log.v(TAG, "height " + height);
	}

	public int getMyHeight() {
		return height;
	}
}
