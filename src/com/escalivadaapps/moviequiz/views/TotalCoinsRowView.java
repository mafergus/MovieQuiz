package com.escalivadaapps.moviequiz.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.escalivadaapps.moviequiz.R;

public class TotalCoinsRowView extends OverlayRow {

	public TotalCoinsRowView(final Context context, int height) {
		super(context, height);

		LayoutInflater.from(context).inflate(R.layout.slot_view_total_coins, this);
		((TextView)findViewById(R.id.coinCount)).setText("666");
	}

}
