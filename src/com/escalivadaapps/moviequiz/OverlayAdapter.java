package com.escalivadaapps.moviequiz;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.escalivadaapps.moviequiz.MovieAdapter.NewsHolder;
import com.escalivadaapps.moviequiz.views.OverlayRow;
import com.escalivadaapps.moviequiz.views.TotalCoinsRowView;

public class OverlayAdapter extends BaseAdapter {
	final private Context context;

	private List<OverlayRow> rows = new ArrayList<OverlayRow>();

	public OverlayAdapter(final Context context) {
		this.context = context;
	}

	public void add(final OverlayRow row) {
		this.rows.add(row);
		notifyDataSetChanged();
	}
	
	public void clear() {
		rows.clear();
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return rows.size();
	}

	@Override
	public Object getItem(int position) {
		return rows.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return rows.get(position);
	}

}
