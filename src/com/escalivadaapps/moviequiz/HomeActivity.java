package com.escalivadaapps.moviequiz;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.escalivadaapps.moviequiz.service.Level;
import com.escalivadaapps.moviequiz.service.MovieService;
import com.escalivadaapps.moviequiz.service.MovieService.MyLocalBinder;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends Activity {
	final static String TAG = HomeActivity.class.getCanonicalName();
	final static public short GAME_REQUEST = 111;

	protected List<Level> levels = new ArrayList<Level>();
	protected AbsListView listView;
	protected LevelListImageAdapter adapter;

	protected MovieService movieService;
	protected ServiceConnection myConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			MyLocalBinder binder = (MyLocalBinder) service;
			movieService = binder.getService();
			if (!levels.equals(movieService.getLevels())) {
				levels.clear();
				adapter.clear();
				levels.addAll(movieService.getLevels());
				adapter.addAll(levels);
			}
		}
		public void onServiceDisconnected(ComponentName arg0) {}
	};

	private LevelBroadcastReceiver levelBroadcastReceiver;
	private IntentFilter levelBroadcastIntentFilter;

	private class LevelBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (movieService != null) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						Log.v("MNF", "ARE EQUAL? " + (levels.equals(movieService.getLevels()) ? "TRUE" : "FALSE") );
						if (!levels.equals(movieService.getLevels())) {
							levels.clear();
							adapter.clear();
							levels.addAll(movieService.getLevels());
							adapter.addAll(levels);
						}
						Log.v("MNF", "GOT LEVELS BROADCAST!");
					}
				});
			}
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		Intent intent = new Intent(this, MovieService.class);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

		((TextView)findViewById(R.id.titleText)).setTypeface(Typeface.createFromAsset(getAssets(), "deftone_stylus.ttf"));

		listView = (ListView) findViewById(R.id.levelList);
		listView.setSelector(R.drawable.alpha_selector);
//		listView.setDrawSelectorOnTop(true);
		adapter = new LevelListImageAdapter(this);
		((ListView) listView).setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent gameActivityIntent = new Intent(HomeActivity.this, GameActivity.class);
				Level l = (Level) adapter.getItem(position);
				gameActivityIntent.putExtra("levelId", l.objectId);
				startActivityForResult(gameActivityIntent, GAME_REQUEST);
			}
		});

		levelBroadcastReceiver = new LevelBroadcastReceiver();
		levelBroadcastIntentFilter = new IntentFilter(MovieService.LEVEL_BROADCAST_FILTER);
		registerReceiver(levelBroadcastReceiver, levelBroadcastIntentFilter);
	}

	//	private void showAll() {
	//		List<Level> levels = movieService.getLevels();
	//		for (Level l : levels) {
	//			List<Movie> movies = l.movies;
	//			for (Movie m : movies) {
	//				List<String> urls = m.imageUrls;
	//				for (String s : urls) {
	//					Log.v(TAG, "" + movieService.isCached(s) + " level " + l.levelId + " " + l.name + " " + m.title + " " + s);
	//				}
	//			}
	//		}
	//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(levelBroadcastReceiver);
		unbindService(myConnection);
		ImageLoader.getInstance().stop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GAME_REQUEST) {
			if (resultCode == GameActivity.RESULT_CODE_LEVEL_UNLOCKED) {
				MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.unlock);
				mp.start();
				Toast.makeText(this, "LEVEL UNLCOKED " + data.getExtras().getString("levelId"), Toast.LENGTH_LONG).show();
			}
		}
	}

}
