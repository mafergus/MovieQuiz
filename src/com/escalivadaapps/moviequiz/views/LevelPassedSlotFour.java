package com.escalivadaapps.moviequiz.views;

import com.escalivadaapps.moviequiz.R;

import android.content.Context;
import android.view.LayoutInflater;

public class LevelPassedSlotFour extends OverlayRow {

	public LevelPassedSlotFour(final Context context, int height) {
		super(context, height);

		LayoutInflater.from(context).inflate(R.layout.level_passed_slot_four, this);
	}

}
