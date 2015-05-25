package com.escalivadaapps.moviequiz;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MovieAdapter extends ArrayAdapter<MovieImage> {
	private final static String TAG = MovieAdapter.class.getCanonicalName();

	List<MovieImage> data; 
	Context context;
	public int itemHeight;

	public MovieAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);

		this.data = new ArrayList<MovieImage>();
		this.context = context;
	}

	public void add(final MovieImage po) {
		data.add(po);
		Log.v(TAG, "add MovieData id " + po.movieId + " count " + data.size());
		notifyDataSetChanged();
	}

	public void addAll(List<MovieImage> movies) {
		for (MovieImage md : movies) {
			data.add(md);
		}
		notifyDataSetChanged();
	}

	@Override
	public MovieImage getItem(int position) {
		return data.get(position);
	}

	public void clear() {
		data.clear();
		notifyDataSetInvalidated();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		NewsHolder holder = null;
		View row = convertView;
		holder = null;
		if(row == null) {
			row = new ImageRow(context, GameActivity.itemHeight);
			holder = new NewsHolder();
			holder.image = (ImageView)row.findViewById(R.id.image);
			holder.overlay = (RelativeLayout)row.findViewById(R.id.imageOverlay);
			holder.back = (ImageView)row.findViewById(R.id.back);
			row.setTag(holder);
		}
		else {
			holder = (NewsHolder)row.getTag();
		}

		ImageRow imageRow = ((ImageRow)row);

		MovieImage itemdata = data.get(position);
		holder.image.setImageBitmap(itemdata.drawable);
		holder.overlay.setBackgroundDrawable(context.getResources().getDrawable(getDrawableResId(position)));
		holder.back.setBackgroundDrawable(context.getResources().getDrawable(getDrawableResId(position)));
		Log.v(TAG, "position " + position + " height " + imageRow.getMyHeight());

		return row;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	static class NewsHolder{
		public ImageView image;
		public ImageView back;
		public RelativeLayout overlay;
	}

	public int getDrawableResId(int index) {
		switch (index) {
		case 0:
			return R.drawable.yellowoverlay;
		case 1:
			return R.drawable.blueoverlay;
		case 2:
			return R.drawable.redoverlay;
		case 3:
			return R.drawable.greenoverlay;
		default:
			return android.R.color.transparent;
		}
	}


}




