package com.escalivadaapps.moviequiz.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

public class OverlayRow extends RelativeLayout {
	private int height = 0;

	public OverlayRow(Context context) {
		this(context, null);
	}
	
	public OverlayRow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public OverlayRow(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public OverlayRow(final Context context, final int height) {
		this(context);

		this.height = height;
		this.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, this.height));
	}

	public int getMyHeight() {
		return height;
	}

}
