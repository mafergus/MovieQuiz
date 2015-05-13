package com.escalivadaapps.moviequiz.views;

import android.content.Context;
import android.view.LayoutInflater;

import com.escalivadaapps.moviequiz.R;

public class CorrectAnswerRowView extends OverlayRow {

	public CorrectAnswerRowView(final Context context, int height) {
		super(context, height);

		LayoutInflater.from(context).inflate(R.layout.slot_view_correct, this);
	}
}
