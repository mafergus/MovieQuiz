package com.escalivadaapps.moviequiz.views;

import android.content.Context;
import android.view.LayoutInflater;

import com.escalivadaapps.moviequiz.R;

public class GameOverSlotOneView extends OverlayRow {

	public GameOverSlotOneView(final Context context, int height) {
		super(context, height);

		LayoutInflater.from(context).inflate(R.layout.slot_view_game_over, this);
	}

}
