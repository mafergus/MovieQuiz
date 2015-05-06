package com.escalivadaapps.moviequiz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.escalivadaapps.moviequiz.MovieQuizApplication;
import com.escalivadaapps.moviequiz.R;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MovieService extends Service {
	final static private String TAG = MovieService.class.getCanonicalName();
	final static private int NUM_TO_LOAD = 1000;

	private final IBinder myBinder = new MyLocalBinder();

	private Map< MovieData, List<String> > movieMap = new HashMap<MovieData, List<String> >();
	private List<Level> levels = new ArrayList<Level>();

	private ImageLoader imageLoader;
	private RequestQueue requestQueue;
	private LruBitmapCache bitmapCache;

	@Override
	public IBinder onBind(Intent arg0) {
		return myBinder;
	}

	public class MyLocalBinder extends Binder {
		public MovieService getService() {
			return MovieService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand");
		return Service.START_NOT_STICKY;
	}

	public MovieService() {}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "onCreate()");

		final SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE); 
		boolean hasLoadedQuestions = prefs.getBoolean("hasLoadedQuestions", false);

		Parse.initialize(this, "UFj25hlpaoyfnC2w9sPUcxynB1tfxEtoP2uexm9W", "IkFZkVf6t72EfabEUa2YFMSXj8zY1dEiZXn61m49");
		//		ParseFacebookUtils.initialize("525166447618020");
		ParseUser.enableAutomaticUser();
		ParseUser.logInInBackground(MovieQuizApplication.id(getApplicationContext()), "", new LogInCallback() {

			@Override
			public void done(ParseUser arg0, ParseException arg1) {
				if (arg0 == null) {
					Log.v("MNF", "exception logging in " + arg1.toString());
					ParseUser user = new ParseUser();
					user.setUsername(MovieQuizApplication.id(getApplicationContext()));
					user.setPassword("");
					user.put("displayName", "Anonymous User");

					user.signUpInBackground(new SignUpCallback() {
						public void done(ParseException e) {
							if (e == null) {
								Log.v("MNF", "new user created");
								loadAll();
								// Hooray! Let them use the app now.
							} else {
								// Sign up didn't succeed. Look at the ParseException
								// to figure out what went wrong
								Log.v("MNF", "sign up failed");
							}
						}
					});
				} else {
					Log.v("MNF", "Parse User logged in " + arg0.getUsername());
					loadAll();
				}
			}
		});

		bitmapCache = new LruBitmapCache(this);
		requestQueue = Volley.newRequestQueue(getApplicationContext());
		imageLoader = new ImageLoader(requestQueue, bitmapCache);
	}

	private void loadAll() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Level");
		query.setLimit(NUM_TO_LOAD);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> parseObjs, ParseException e) {
				if (e == null) {
					Log.v(TAG, "loaded levels");
					for (final ParseObject level : parseObjs) {
						level.getRelation("movies").getQuery().findInBackground(new FindCallback<ParseObject>() {
							public void done(List<ParseObject> results, ParseException e) {
								if (e != null) {
									// There was an error
								} else {
									final List<Movie> movies = new ArrayList<Movie>();
									for (final ParseObject movie : results) {
										ParseQuery<ParseObject> movieImageQuery = ParseQuery.getQuery("MovieImage");
										movieImageQuery.whereEqualTo("parent", movie);
										movieImageQuery.findInBackground(new FindCallback<ParseObject>() {

											@Override
											public void done(List<ParseObject> parseObjs, ParseException e) {
												if (e == null) {
													List<String> urls = new ArrayList<String>();
													for (final ParseObject movieImage : parseObjs) {
														urls.add(movieImage.getString("url"));
														Log.v(TAG, "" + movie.getString("title") + " " + movieImage.getString("url"));
														ImageRequest imgRequest = new ImageRequest(movieImage.getString("url"), 
																new Listener<Bitmap>() {

															@Override
															public void onResponse(Bitmap bmp) {
																Log.v(TAG, "loaded bmp " + movie.getString("title"));
																bitmapCache.putBitmap(movieImage.getString("url"), bmp);
															}
														}, 0, 0, null, null);
														requestQueue.add(imgRequest);
													}
													Movie m = new Movie(movie.getInt("mdid"), movie.getString("title"), urls);
													movies.add(m);
													requestQueue.start();
												}
											}
										});
									}
									Level l = new Level(level.getInt("levelNumber"), level.getString("name"), movies);
									levels.add(l);
								}
							}
						});
					}
				}
			}
		});
	}
	
	public boolean isCached(String url) {
		return (bitmapCache.get(url) == null ? false : true);
	}
	
	public List<Level> getLevels() {
		return levels;
	}

	public Map<Integer, Drawable> getMovieQuestions(int count) {
		Map<Integer, Drawable> movieMap = new HashMap<Integer, Drawable>();
		Bitmap d1 = BitmapFactory.decodeResource(getResources(), R.drawable.frame1);
		BitmapDrawable bd1 = new BitmapDrawable(d1);
		Bitmap d2 = BitmapFactory.decodeResource(getResources(), R.drawable.frame2);
		BitmapDrawable bd2 = new BitmapDrawable(d2);
		Bitmap d3 = BitmapFactory.decodeResource(getResources(), R.drawable.frame3);
		BitmapDrawable bd3 = new BitmapDrawable(d3);
		Bitmap d4 = BitmapFactory.decodeResource(getResources(), R.drawable.frame4);
		BitmapDrawable bd4 = new BitmapDrawable(d4);

		movieMap.put(1, bd1);
		movieMap.put(2, bd2);
		movieMap.put(3, bd3);
		movieMap.put(4, bd4);

		return movieMap;
	}

	private void loadQuestion() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Movie");
		query.countInBackground(new CountCallback() {
			public void done(int count, ParseException e) {
				if (e == null) {
					int reps = count/1000 + 1;
					for (int i=0; i<reps; i++) {
						loadMovies(NUM_TO_LOAD);
					}
				} else {
					Log.v("GameActivity", "Couldn't get movie count");
				}
			}
		});
	}

	private void loadMovies(int count) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Movie");
		query.setLimit(count);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objectList, ParseException e) {
				if (e == null) {
					for (ParseObject movie : objectList) {
						if (!movieMap.containsKey(movie)) {
							List<String> movieImages = new ArrayList<String>();
							movieMap.put(new MovieData(movie.getInt("mdId")), movieImages);
						}
						loadMovieImages(movie);
					}
				} else {
					Log.v(TAG, "couldn't load movies");
				}
			}
		});
	}

	private void loadMovieImages(final ParseObject movie) {
		ParseQuery<ParseObject> imageQuery = ParseQuery.getQuery("MovieImage");
		imageQuery.whereEqualTo("parent", movie);
		imageQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objList, ParseException arg1) {
				if (arg1 == null) {
					for (ParseObject movieImage : objList) {
						imageLoader.get(movieImage.getString("url"), null);
					}
				}
			}
		});
	}

	//	private void loadRandomMovieImage(final int rand) {
	//		ParseQuery<ParseObject> query = ParseQuery.getQuery("Movie");
	//		query.setSkip(rand);
	//		query.getFirstInBackground(new GetCallback<ParseObject>() {
	//
	//			@Override
	//			public void done(final ParseObject parseObject, ParseException e) {
	//				if (e == null) {
	//					String str = "Got random movie " + parseObject.getString("title") + " rand " + rand + " url " + parseObject.get("mdId");
	//					Log.v("GameActivity", str);
	//
	//					ParseQuery<ParseObject> imageQuery = ParseQuery.getQuery("MovieImage");
	//					imageQuery.whereEqualTo("parent", parseObject);
	//					imageQuery.findInBackground(new FindCallback<ParseObject>() {
	//
	//						@Override
	//						public void done(List<ParseObject> objList, ParseException arg1) {
	//							if (arg1 == null) {
	//								Collections.shuffle(objList);
	//								ParseObject movieImage = objList.get(0);
	//								Log.v("GameActivity", parseObject.getString("title") + " " + movieImage.getString("url"));
	//								new DownloadTask(parseObject.getInt("mdId")).execute(movieImage.getString("url"));
	//							}
	//						}
	//					});
	//				} else {
	//					Log.e(TAG, "Couldn't get Movie rand " + rand);
	//				}
	//			}
	//		});
	//	}


}
