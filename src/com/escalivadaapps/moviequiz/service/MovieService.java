package com.escalivadaapps.moviequiz.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.escalivadaapps.moviequiz.MovieQuizApplication;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.ImageLoader;
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
	final static public String LEVEL_BROADCAST_FILTER = "newlevelbroadcast";
	final static private int LEVEL_UPDATED_INTERVAL = 5 * 60 * 1000;
	final static private int NUM_TO_LOAD = 50;

	final public ObjectMapper mapper = new ObjectMapper();
	private final IBinder myBinder = new MyLocalBinder();

	private List<Movie> movieList = new ArrayList<Movie>();
	private List<Level> levels = new ArrayList<Level>();

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

		loadCachedLevels();
	}

	public void loadCachedLevels() {
		Log.v(TAG, "Loading cached level");
		SharedPreferences prefs = getSharedPreferences(getPackageName()+"prefs", Context.MODE_PRIVATE);
		String storedGames = prefs.getString(getPackageName()+"prefs"+"levels", null);
		if (storedGames != null && levels.isEmpty()) {
			try {
				Level[] storedLevels = mapper.readValue(storedGames, Level[].class);
				Log.v(TAG, "" + Arrays.toString(storedLevels));
				levels.clear();
				for (Level l : storedLevels) {
					levels.add(l);
					Log.v(TAG, l.toString());
				}
				sendLevelBroadcast();
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void storeCachedLevels() {
		Log.v(TAG, "Storing level games");
		SharedPreferences prefs = getSharedPreferences(getPackageName()+"prefs", Context.MODE_PRIVATE);
		try {
			String levelsJson = mapper.writeValueAsString(levels);
			Log.v(TAG, levelsJson);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(getPackageName()+"prefs"+"levels", levelsJson);
			editor.commit();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	static public interface LoadCallback<T> {
		public void onLoad(final T obj);
		public void onError();
	}

	class LoadMovieRequest {
		final private LoadCallback<Movie> callback;
		final private ParseObject movieObj;
		public LoadMovieRequest(final ParseObject movieObj, final LoadCallback<Movie> callback) {
			this.movieObj = movieObj;
			this.callback = callback;
		}
		public void load() {
			Log.v(TAG, "LoadMovieRequest#load()");
			ParseQuery<ParseObject> movieImageQuery = ParseQuery.getQuery("MovieImage");
			movieImageQuery.whereEqualTo("parent", movieObj);
			movieImageQuery.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> parseObjs, ParseException e) {
					if (e == null) {
						List<String> urls = new ArrayList<String>();
						for (final ParseObject movieImage : parseObjs) {
							urls.add(movieImage.getString("url"));
							Log.v(TAG, "LoadMovieRequest#load#done" + movieObj.getString("title") + " " + movieImage.getString("url"));
						}
						Movie m = new Movie(movieObj.getInt("mdId"), movieObj.getString("title"), urls);
						callback.onLoad(m);
					} else {
						callback.onError();
					}
				}
			});
		}
	}

	class LoadLevelRequest {
		final private LoadCallback<Level> callback;
		final private ParseObject levelObj;
		private int count = 0;
		private int completed = 0;
		private List<MovieImageData> movieImages = new ArrayList<MovieImageData>();
		public LoadLevelRequest(final ParseObject levelObj, final LoadCallback<Level> callback) { 
			this.levelObj = levelObj;
			this.callback = callback;
		}
		public void load() {
			ParseQuery<ParseObject> relationQuery = levelObj.getRelation("movieImages").getQuery();
			relationQuery.include("parent");
			relationQuery.setLimit(1000);
			relationQuery.findInBackground(new FindCallback<ParseObject>() {

				public void done(List<ParseObject> results, ParseException e) {
					if (e != null) {
						callback.onError();
					} else {
						count = results.size();

						List<MovieImageData> movieImageList = new ArrayList<MovieImageData>();
						for (ParseObject movieImage : results) {
							movieImageList.add(new MovieImageData(movieImage.getParseObject("parent").getString("title"), 
									movieImage.getParseObject("parent").getInt("mdId"), 
									movieImage.getString("url")));
						}

						final Level l = new Level(levelObj.getObjectId(), 
								levelObj.getInt("levelNumber"), 
								levelObj.getString("name"), 
								movieImageList, 
								levelObj.getBoolean("isRandom"),
								levelObj.getString("imageUrl"));
						callback.onLoad(l);
					}
				}
			});
		}
	}

	private List<Level> tempLevels = new ArrayList<Level>();

	private void loadAll() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				loadAllMovies();

				ParseQuery<ParseObject> query = ParseQuery.getQuery("Level");
				query.setLimit(NUM_TO_LOAD);
				query.findInBackground(new FindCallback<ParseObject>() {

					@Override
					public void done(List<ParseObject> parseObjs, ParseException e) {
						if (e == null) {
							Log.v(TAG, "loaded levels");
							tempLevels.clear();
							final int count = parseObjs.size();
							for (final ParseObject level : parseObjs) {

								LoadLevelRequest levelRequest = new LoadLevelRequest(level, new LoadCallback<Level>() {

									@Override
									public void onLoad(Level obj) {
										tempLevels.add(obj);
										if (tempLevels.size() == count) {
											List<Level> sortedTempLevels = new ArrayList<Level>(tempLevels);
											List<Level> sortedLevels = new ArrayList<Level>(levels);
											Collections.sort(sortedTempLevels, new Level.LevelComparator());
											Collections.sort(sortedLevels, new Level.LevelComparator());
											if (sortedTempLevels.equals(sortedLevels)) {
												Log.v(TAG, "LEVELS ARE THE SAME NOTTTTTTT SENDING BROADCAST!");
											} else {
												Log.v(TAG, "LEVELS HAVE YESSSSS CHANGED SENDING BROADCAST!!!!");
												levels = new ArrayList<Level>(tempLevels);
												storeCachedLevels();
												sendLevelBroadcast();
											}
										}
									}

									@Override
									public void onError() { 
										Log.v(TAG, "Failed to load level " + level.getString("name"));
									}
								});
								levelRequest.load();
							}
						}
					}
				});

				new Handler(Looper.getMainLooper()).postDelayed(this, LEVEL_UPDATED_INTERVAL);
			}
		});
	}

	private void sendLevelBroadcast() {
		Intent levelBroadcast = new Intent();
		levelBroadcast.setAction(LEVEL_BROADCAST_FILTER);
		sendBroadcast(levelBroadcast);
	}

	public boolean isCached(String url) {
		Bitmap bmp = ImageLoader.getInstance().getMemoryCache().get(url);
		File other = ImageLoader.getInstance().getDiskCache().get(url);
		if (bmp == null && other == null) {
			return false;
		} else {
			return true;			
		}
	}

	public List<Level> getLevels() {
		List<Level> levelsCopy = new ArrayList<Level>(this.levels);
		Collections.sort(levelsCopy, new Level.LevelComparator());
		return levelsCopy;
	}

	public Level getLevelById(String id) {
		for (Level l : levels) {
			if (l.objectId.equals(id)) {
				return l;
			}
		}
		return null;
	}

	private Random rand = new Random();

	public MovieImageData getRandomMovieImage(List<String> excludeUrls) {
		if (excludeUrls == null) {
			excludeUrls = new ArrayList<String>();
		}

		Movie randMovie = movieList.get( rand.nextInt(movieList.size()) ); 
		List<String> urls = new ArrayList<String>(randMovie.imageUrls);
		urls.removeAll(excludeUrls);
		Collections.shuffle(urls);

		return new MovieImageData(randMovie.title, randMovie.mdid, urls.get(0));
	}

	private void loadAllMovies() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Movie");
		query.whereExists("objectId");
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
					Log.v(TAG, "Loading movies got count: " + objectList.size());
					for (ParseObject movie : objectList) {
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
					Log.v(TAG, "Loading Movie Images count: " + objList.size());
					List<String> urls = new ArrayList<String>();
					for (ParseObject movieImage : objList) {
						urls.add(movieImage.getString("url"));
					}
					Movie m = new Movie(movie.getInt("mdId"), movie.getString("title"), urls);
					if (!movieList.contains(m)) {
						movieList.add(m);						
					}
				}
			}
		});
	}

}

