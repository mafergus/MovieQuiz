package com.escalivadaapps.moviequiz.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.escalivadaapps.moviequiz.R;

public class CorrectRowView extends ImageView {

	public CorrectRowView(Context context) {
		this(context, null);
	}

	public CorrectRowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CorrectRowView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		this.setImageResource(R.drawable.correct_draft);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.setLayoutParams(params);
	}

}
