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
	final private List<LevelData> levels = new ArrayList<LevelData>();

	private LayoutInflater inflater;
	private DisplayImageOptions options;

	static class LevelData {
		public String name;
		public String imageUrl;
		public boolean isLocked;
		public String objectId;

		public LevelData(String name, String imageUrl, boolean isLocked, String objectId) {
			this.name = name;
			this.imageUrl = imageUrl;
			this.isLocked = isLocked;
			this.objectId = objectId;
		}
	}

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
		this.levels.add(new LevelData(level.name, level.imageUrl, level.isLocked, level.objectId));
		notifyDataSetChanged();
	}

	public void addAll(final List<Level> levels) {
		ArrayList<LevelData> lvls = new ArrayList<LevelData>();
		for (Level l : levels) {
			lvls.add(new LevelData(l.name, l.imageUrl, l.isLocked, l.objectId));
		}
		this.levels.addAll(lvls);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_grid_image, parent, false);
			holder = new ViewHolder(context, view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LevelData l = (LevelData)getItem(position);
		holder.populate(l);

		return view;
	}

	static public int getRandomColor() {
		int color = (int) (Math.random() * 16777215);
		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		return Color.parseColor(hexColor);
	}

	static class ViewHolder {
		final Context context;
		View rootView;
		TextView text;
		int color;
		ImageView image;
		View overlay;

		public ViewHolder(final Context context, final View rootView) {
			this.context = context;
			this.text = (TextView) rootView.findViewById(R.id.text);
			Typeface font = ((MovieQuizApplication)context.getApplicationContext()).getRegularFont();
			this.text.setTypeface(font);
			this.image = (ImageView)rootView.findViewById(R.id.image);
			overlay = (ViewGroup)rootView.findViewById(R.id.overlay);
		}

		public void populate(final LevelData l) {
			this.text.setText(l.name);
			this.image.setImageResource(android.R.color.transparent);
			ImageLoader.getInstance().displayImage(l.imageUrl, this.image, 
					((MovieQuizApplication)context.getApplicationContext()).getImageOptions());
			overlay.setVisibility( l.isLocked ? View.VISIBLE : View.INVISIBLE );
		}
	}
}
