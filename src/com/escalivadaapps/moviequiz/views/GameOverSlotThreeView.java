package com.escalivadaapps.moviequiz.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.escalivadaapps.moviequiz.R;

public class GameOverSlotThreeView extends OverlayRow {
	
	public GameOverSlotThreeView(Context context, int height) {
		super(context, height);
		
		LayoutInflater.from(context).inflate(R.layout.slot_view_turn_coins, this);
	}

}
