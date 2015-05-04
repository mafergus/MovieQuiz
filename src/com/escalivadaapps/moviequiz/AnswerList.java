package com.escalivadaapps.moviequiz;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.fortysevendeg.swipelistview.SwipeListView;

public class AnswerList extends SwipeListView {
	private final static String TAG = AnswerList.class.getCanonicalName();

	public int itemHeight = 0;
	
	public AnswerList(Context context) {
		this(context, null);
	}
	
	public AnswerList(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public AnswerList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int height = bottom-top;
		Log.v(TAG, "onLayout height " + height);
		itemHeight = height / 4;
		
//		MovieAdapter adapter = (MovieAdapter)getAdapter();
//		adapter.itemHeight = itemHeight;
		GameActivity.itemHeight = itemHeight;
		invalidateViews();
	}

}
