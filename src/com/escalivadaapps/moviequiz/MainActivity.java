package com.escalivadaapps.moviequiz;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.escalivadaapps.moviequiz.service.Level;
import com.escalivadaapps.moviequiz.service.MovieService;
import com.escalivadaapps.moviequiz.service.MovieService.MyLocalBinder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.ImageLoader;


public class MainActivity extends ActionBarActivity
implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	final static String TAG = MainActivity.class.getCanonicalName();

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	protected List<Level> levels = new ArrayList<Level>();
	protected AbsListView listView;
	protected LevelGridImageAdapter adapter;

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
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		mNavigationDrawerFragment.setMenuVisibility(false);

		Intent intent = new Intent(this, MovieService.class);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

		listView = (GridView) findViewById(R.id.grid);
		listView.setSelector(R.drawable.alpha_selector);
		listView.setDrawSelectorOnTop(true);
		adapter = new LevelGridImageAdapter(this);
		((GridView) listView).setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
				Level l = (Level) adapter.getItem(position);
				gameActivityIntent.putExtra("levelId", l.objectId);
				startActivity(gameActivityIntent);
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
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		//		FragmentManager fragmentManager = getSupportFragmentManager();
		//		fragmentManager.beginTransaction()
		//		.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
		//		.commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_game_section);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
