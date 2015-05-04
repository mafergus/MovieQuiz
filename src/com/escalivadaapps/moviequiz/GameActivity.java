package com.escalivadaapps.moviequiz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GameActivity extends Activity {
	final static private String TAG = GameActivity.class.getCanonicalName();
	final static private int START_TIME_MS = 10000;

	public static int itemHeight;

	private TextView questionText;
	private List<ImageView> images = new ArrayList<ImageView>();
	private GameTimer timer;

	private Object answerLock = new Object();
	private int correctAnswer;
	private int wrongAnswer1;
	private int wrongAnswer2;
	private int wrongAnswer3;

	private AnswerList swipelistview;
	private ViewGroup listOverlay;
	private MovieAdapter adapter;
	private ProgressDialog loading;
	private AnimationAdapter animAdapter;

	List<MovieImage> loadedMovies = new ArrayList<MovieImage>();

	public class DownloadTask extends AsyncTask<String, Integer, Drawable> {
		private int movieId;

		public DownloadTask(int movieId) {
			this.movieId = movieId;
		}

		@Override
		protected Drawable doInBackground(String... arg0) {
			return downloadImage(arg0[0]);
		}

		protected void onPostExecute(Drawable image) {
			//			setImage(imageView, image);
			Log.v("DownloadTask", "onPostExecute movidId " + movieId);
			loadedMovies.add(new MovieImage(image, movieId));
			if (loadedMovies.size() == 4) {
				startQuestion();
			}
		}

		private Drawable downloadImage(String _url) {
			//Prepare to download image
			URL url;        
			BufferedOutputStream out;
			InputStream in;
			BufferedInputStream buf;

			try {
				url = new URL(_url);
				in = url.openStream();
				buf = new BufferedInputStream(in);
				Bitmap bMap = BitmapFactory.decodeStream(buf);
				if (in != null) {
					in.close();
				}
				if (buf != null) {
					buf.close();
				}

				return new BitmapDrawable(bMap);

			} catch (Exception e) {
				Log.e("Error reading file", e.toString());
			}

			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_main);

		this.questionText = (TextView)findViewById(R.id.questionText);
		this.questionText.setTextColor(getResources().getColor(R.color.white));
		this.timer = (GameTimer)findViewById(R.id.timer);
		this.timer.init(START_TIME_MS);
		this.listOverlay = (ViewGroup)findViewById(R.id.listOverlay);

		doListStuff();

		MovieQuizApplication application = (MovieQuizApplication)getApplicationContext(); 
		int points = application.getPointsFromProgress(50);
		showQuestionCorrectOverlay(points, application.getTotalPoints());
		//		loadQuestion();
	}

	private void doListStuff() {
		swipelistview = (AnswerList)findViewById(R.id.answerList); 
		adapter = new MovieAdapter(this, R.layout.custom_row);
		animAdapter = new SwingLeftInAnimationAdapter(adapter);
		animAdapter.setAbsListView(swipelistview);

		swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
				MovieImage data = adapter.getItem(position);
				if (data.movieId == GameActivity.this.correctAnswer) {
					onQuestionCorrect();
				} else {
					onQuestionIncorrect();
				}
			}
			@Override
			public void onClosed(int position, boolean fromRight) {}
			@Override
			public void onListChanged() {}
			@Override
			public void onMove(int position, float x) {}
			@Override
			public void onStartOpen(int position, int action, boolean right) {
				Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
			}
			@Override
			public void onStartClose(int position, boolean right) {
				Log.d("swipe", String.format("onStartClose %d", position));
			}
			@Override
			public void onClickFrontView(int position) {
				Log.d("swipe", String.format("onClickFrontView %d", position));

				//				swipelistview.openAnimate(position); //when you touch front view it will open
			}
			@Override
			public void onClickBackView(int position) {
				Log.d("swipe", String.format("onClickBackView %d", position));

				//				swipelistview.closeAnimate(position);//when you touch back view it will close
			}
			@Override
			public void onDismiss(int[] reverseSortedPositions) {}

		});

		//These are the swipe listview settings. you can change these
		//setting as your requirement 
		swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT); // there are five swiping modes
		swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
		swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE);
		swipelistview.setAnimationTime(300); // Animation time
		swipelistview.setAdapter(animAdapter);
		adapter.notifyDataSetChanged();
	}

	public void startQuestion() {
		adapter.addAll(loadedMovies);
		loadedMovies.clear();
		loading.dismiss();

		timer.start();
	}

	public void onQuestionCorrect() {
		int timeLeft = timer.getProgress();
		MovieQuizApplication application = (MovieQuizApplication)getApplicationContext();
		int score = application.getPointsFromProgress(timeLeft);
		int totalPoints = application.getTotalPoints()+score;
		application.setTotalPoints(totalPoints);

		showQuestionCorrectOverlay(score, totalPoints);
	}

	public void onQuestionIncorrect() {

	}

	public void showQuestionCorrectOverlay(int score, int totalPoints) {
		adapter.add(new MovieImage(getResources().getDrawable(R.drawable.ic_launcher), 1));
		adapter.add(new MovieImage(getResources().getDrawable(R.drawable.ic_launcher), 2));
		adapter.add(new MovieImage(getResources().getDrawable(R.drawable.ic_launcher), 3));
		adapter.add(new MovieImage(getResources().getDrawable(R.drawable.ic_launcher), 4));


		ViewGroup slot1 = (ViewGroup)listOverlay.findViewById(R.id.overlayChild1);
		ViewGroup slot2 = (ViewGroup)listOverlay.findViewById(R.id.overlayChild2);
		ViewGroup slot3 = (ViewGroup)listOverlay.findViewById(R.id.overlayChild3);
		ViewGroup slot4 = (ViewGroup)listOverlay.findViewById(R.id.overlayChild4);

		int correctRow = 2;

		ViewGroup totalCoins = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.total_coins_slot_view, null);
		((TextView)totalCoins.findViewById(R.id.coinCount)).setText("666");
		totalCoins.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
				ViewGroup.LayoutParams.MATCH_PARENT));

		ViewGroup turnCoins = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.turn_coins_slot_view, null);
		turnCoins.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
				ViewGroup.LayoutParams.MATCH_PARENT));
		
		ImageView imvCorrect = new ImageView(this);
		imvCorrect.setImageResource(R.drawable.correct_draft);

		ImageView imNext = new ImageView(this);
		imNext.setImageResource(R.drawable.next_draft);
		imNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(GameActivity.this, "Loading next questioin", Toast.LENGTH_SHORT).show();
			}
		});

		if (correctRow == 0) {
			slot1.addView(imvCorrect);
			slot2.addView(turnCoins);
			slot3.addView(totalCoins);
			slot4.addView(imNext);
		} else if (correctRow == 1) {
			slot1.addView(turnCoins);
			slot2.addView(imvCorrect);
			slot3.addView(totalCoins);
			slot4.addView(imNext);
		} else if (correctRow == 2) {
			slot1.addView(turnCoins);
			slot2.addView(totalCoins);
			slot3.addView(imvCorrect);
			slot4.addView(imNext);
		} else if (correctRow == 3) {
			slot3.addView(imNext);
			slot4.addView(imvCorrect);
			slot1.addView(turnCoins);
			slot2.addView(totalCoins);
		} 
		//		
		//		for (int i=0; i<adapter.getCount(); i++) {
		//			View v = adapter.getView(i, null, swipelistview);
		//			ViewGroup overlay = (ViewGroup)v.findViewById(R.id.imageOverlay);
		//			if (i == 0) {
		//				Log.v(TAG, "setting row 0");
		//				CorrectRowView crw = new CorrectRowView(GameActivity.this);
		//				overlay.addView(crw);
		//			} else {
		//				Log.v(TAG, "setting row " + i);
		////				overlay.setBackgroundColor(getResources().getColor(R.color.red));
		//			}
		//		}
	}

	private ViewGroup getSlotFromIndex(int index) {
		switch (index) {
		case 0:
			return (ViewGroup)listOverlay.findViewById(R.id.overlayChild1);
		case 1:
			return (ViewGroup)listOverlay.findViewById(R.id.overlayChild2);
		case 2:
			return (ViewGroup)listOverlay.findViewById(R.id.overlayChild3);
		case 3:
			return (ViewGroup)listOverlay.findViewById(R.id.overlayChild4);
		default:
			return null;
		}
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	@Override
	protected void onResume() {
		super.onResume();

		correctAnswer = 0;
		wrongAnswer1 = 0;
		wrongAnswer2 = 0;
		wrongAnswer3 = 0;
	}

	private void loadQuestion() {
		loading = ProgressDialog.show(GameActivity.this, "Loading ...", "", true);

		adapter.clear();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Movie");
		query.countInBackground(new CountCallback() {
			public void done(int count, ParseException e) {
				if (e == null && count > 0) {
					final Random r = new Random();
					for (int i=0; i<4; i++) {
						final int rand = r.nextInt(count);
						loadRandomMovieImage(rand);
					}
				} else {
					Log.v("GameActivity", "Couldn't get movie count");
				}
			}
		});
	}

	private void loadRandomMovieImage(final int rand) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Movie");
		query.setSkip(rand);
		query.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(final ParseObject parseObject, ParseException e) {
				if (e == null) {
					String str = "Got random movie " + parseObject.getString("title") + " rand " + rand + " url " + parseObject.get("mdId");
					Log.v("GameActivity", str);

					synchronized (answerLock) {
						populateAnswer(parseObject);
					}
					ParseQuery<ParseObject> imageQuery = ParseQuery.getQuery("MovieImage");
					imageQuery.whereEqualTo("parent", parseObject);
					imageQuery.findInBackground(new FindCallback<ParseObject>() {

						@Override
						public void done(List<ParseObject> objList, ParseException arg1) {
							if (arg1 == null) {
								Collections.shuffle(objList);
								ParseObject movieImage = objList.get(0);
								Log.v("GameActivity", parseObject.getString("title") + " " + movieImage.getString("url"));
								new DownloadTask(parseObject.getInt("mdId")).execute(movieImage.getString("url"));
							}
						}
					});
				} else {
					Log.e(TAG, "Couldn't get Movie rand " + rand);
				}
			}
		});
	}

	private void populateAnswer(final ParseObject po) {
		int id = po.getInt("mdId");
		if (correctAnswer == 0) {
			correctAnswer = id;
			questionText.setText("" + po.getString("title"));
		} else if (wrongAnswer1 == 0) {
			wrongAnswer1 = id;
		} else if (wrongAnswer2 == 0) {
			wrongAnswer2 = id;
		} else if (wrongAnswer3 == 0) {
			wrongAnswer3 = id;
		}
	}
}

