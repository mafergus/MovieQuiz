package com.escalivadaapps.moviequiz.views;

import com.escalivadaapps.moviequiz.R;

import android.content.Context;
import android.view.LayoutInflater;

public class LevelPassedSlotOne extends OverlayRow {

	public LevelPassedSlotOne(final Context context, int height) {
		super(context, height);

		LayoutInflater.from(context).inflate(R.layout.level_passed_slot_one, this);
	}

}
