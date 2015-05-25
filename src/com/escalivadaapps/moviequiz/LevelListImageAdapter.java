package com.escalivadaapps.moviequiz;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.escalivadaapps.moviequiz.service.Level;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

class LevelListImageAdapter extends BaseAdapter {
	final private Context context;
	final private List<Level> levels = new ArrayList<Level>();

	private LayoutInflater inflater;
	private DisplayImageOptions options;

	LevelListImageAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	@Override
	public int getCount() {
		return levels.size();
	}

	public void clear() {
		levels.clear();
		notifyDataSetInvalidated();
	}

	@Override
	public Object getItem(int position) {
		return levels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void add(final Level level) {
		this.levels.add(level);
		notifyDataSetChanged();
	}

	public void addAll(final List<Level> levels) {
		this.levels.addAll(levels);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_grid_image, parent, false);
			holder = new ViewHolder();
			holder.text = (TextView) view.findViewById(R.id.text);
			Typeface font = ((MovieQuizApplication)context.getApplicationContext()).getRegularFont();
			holder.text.setTypeface(font);
			holder.image = (ImageView)view.findViewById(R.id.image);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Level l = (Level)getItem(position);

		holder.text.setText(l.name);
		ImageLoader.getInstance().displayImage(l.imageUrl, holder.image);

		//		holder.text.setBackgroundColor(LevelGridImageAdapter.getRandomColor());

		//		StateListDrawable states = new StateListDrawable();
		//		states.addState(new int[] {android.R.attr.state_pressed},
		//				new ColorDrawable(LevelGridImageAdapter.getRandomColor()) );
		//		states.addState(new int[] { },
		//				new ColorDrawable(Color.TRANSPARENT) );
		//		view.setBackgroundDrawable(states);

		return view;
	}

	static public int getRandomColor() {
		int color = (int) (Math.random() * 16777215);
		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		return Color.parseColor(hexColor);
	}

	static class ViewHolder {
		TextView text;
		int color;
		ImageView image;
	}
}
