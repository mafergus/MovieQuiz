package com.escalivadaapps.moviequiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.escalivadaapps.moviequiz.Question.Callback;
import com.escalivadaapps.moviequiz.service.Level;
import com.escalivadaapps.moviequiz.service.Movie;
import com.escalivadaapps.moviequiz.service.MovieImageData;
import com.escalivadaapps.moviequiz.service.MovieService;
import com.escalivadaapps.moviequiz.service.MovieService.MyLocalBinder;
import com.escalivadaapps.moviequiz.views.CorrectAnswerRowView;
import com.escalivadaapps.moviequiz.views.NextAnswerRowView;
import com.escalivadaapps.moviequiz.views.TotalCoinsRowView;
import com.escalivadaapps.moviequiz.views.TurnCoinsRowView;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

public class GameActivity extends Activity {
	final static private String TAG = GameActivity.class.getCanonicalName();
	final static private int START_TIME_MS = 10000;
	final static private int NUM_QUESTIONS = 10;

	public static int itemHeight;

	private TextView questionText;
	private List<ImageView> images = new ArrayList<ImageView>();
	private GameTimer timer;

	private Object answerLock = new Object();
	private int correctAnswer;
	private int wrongAnswer1;
	private int wrongAnswer2;
	private int wrongAnswer3;
	private String levelId;

	private AnswerList swipelistview;
	private AnswerList overlayList;
	private ViewGroup listOverlay;
	private OverlayAdapter overlayAdapter;
	private MovieAdapter adapter;
	private ProgressDialog loading;
	private AnimationAdapter animAdapter;
	private AnimationAdapter animOverlayAdapter;

	List<Question> questions = new ArrayList<Question>();
	List<MovieImage> loadedMovies = new ArrayList<MovieImage>();

	protected MovieService movieService;
	protected ServiceConnection myConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			MyLocalBinder binder = (MyLocalBinder) service;
			movieService = binder.getService();
			loadQuestions();
		}
		public void onServiceDisconnected(ComponentName arg0) {}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_main);

		Intent intent = new Intent(this, MovieService.class);
		bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

		Typeface font = ((MovieQuizApplication)getApplicationContext()).getRegularFont();
		this.questionText = (TextView)findViewById(R.id.questionText);
		this.questionText.setTypeface(font);
		this.questionText.setTextColor(getResources().getColor(R.color.white));
		this.timer = (GameTimer)findViewById(R.id.timer);
		this.timer.init(START_TIME_MS);
		this.timer.setOnEndedCallback(new GameTimer.Callback() {

			@Override
			public void onEnded(long totalRunTimeMs) {
				onQuestionIncorrect(0);
			}
		});
		this.listOverlay = (ViewGroup)findViewById(R.id.listOverlay);

		doListStuff();

		MovieQuizApplication application = (MovieQuizApplication)getApplicationContext(); 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindService(myConnection);
	}

	@Override
	public void onBackPressed() {
		onQuestionIncorrect(0);
	}

	private Random rand = new Random();

	public int randInt(int min, int max) {

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	private void loadAnimAdapter() {
		int rand = randInt(0, 4);
		switch (rand) {
		case 0:
			animAdapter = new SwingLeftInAnimationAdapter(adapter);
			break;
		case 1:
			animAdapter = new SwingRightInAnimationAdapter(adapter);
			break;
		case 2:
			animAdapter = new SwingBottomInAnimationAdapter(adapter);
			break;
		case 3:
			animAdapter = new ScaleInAnimationAdapter(adapter);
			break;
		case 4:
			animAdapter = new AlphaInAnimationAdapter(adapter);
			break;
		default:
			animAdapter = new SwingLeftInAnimationAdapter(adapter);
			break;
		}

		animAdapter.setAbsListView(swipelistview);
		swipelistview.setAdapter(animAdapter);
		adapter.notifyDataSetChanged();
	}

	class MyAnimationListener implements AnimatorListener {
		final int position;
		final int correctAnswer;
		final View rowView;
		public MyAnimationListener(int position, int correctAnswer, final View row) { 
			this.position = position; 
			this.correctAnswer = correctAnswer;
			this.rowView = row;
		}
		@Override
		public void onAnimationStart(Animator animation) {
			if (position == 0) {
				questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);
				timer.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);
			}
		}
		@Override
		public void onAnimationEnd(Animator animation) {
			if (position == 3) {
				adapter.clear();
				onQuestionCorrect(this.correctAnswer);
			}
		}
		@Override
		public void onAnimationCancel(Animator animation) {}
		@Override
		public void onAnimationRepeat(Animator animation) {}
	}

	class OverlayAnimationListener implements AnimatorListener {
		final int position;
		public OverlayAnimationListener(int position) { this.position = position; }
		@Override
		public void onAnimationStart(Animator animation) {}
		@Override
		public void onAnimationEnd(Animator animation) {
			if (position == 3) {
				overlayList.closeOpenedItems();
				overlayList.setVisibility(View.INVISIBLE);
				loadQuestion(questions.get(0));
			}
		}
		@Override
		public void onAnimationCancel(Animator animation) {}
		@Override
		public void onAnimationRepeat(Animator animation) {}
	}

	private final static int ROW_ANIM_DURATION = 500;

	private void startListExitAnimation(final ListView list) {
		for (int i=0; i<4; i++) {
			list.setEnabled(false);
			final View rowView = list.getChildAt(i); 
			rowView.animate().setDuration(ROW_ANIM_DURATION)
			.setStartDelay(i*100)
			.alpha(0f)
			.translationX(rowView.getWidth())
			.setListener(new MyAnimationListener(i, 1, rowView));
		}
	}

	private void startOverlayExitAnimation(final ListView list) {
		for (int i=0; i<4; i++) {
			list.setEnabled(false);
			final View rowView = list.getChildAt(i); 
			rowView.animate().setDuration(ROW_ANIM_DURATION)
			.setStartDelay(i*100)
			.alpha(0f)
			.translationX(rowView.getWidth())
			.setListener(new OverlayAnimationListener(i));
		}
	}

	private void doListStuff() {
		overlayAdapter = new OverlayAdapter(this);
		overlayList = (AnswerList)findViewById(R.id.overlayList);
		overlayList.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT); // there are five swiping modes
		overlayList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
		overlayList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE);
		overlayList.setAnimationTime(50); // Animation time
		overlayList.setVisibility(View.INVISIBLE);
		swipelistview = (AnswerList)findViewById(R.id.answerList); 
		adapter = new MovieAdapter(this, R.layout.custom_row);
		//		animAdapter = new SwingLeftInAnimationAdapter(new MovieAdapter(this, R.layout.custom_row));
		animAdapter = new SwingLeftInAnimationAdapter(adapter);
		animAdapter.setAbsListView(swipelistview);
		swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(final int position, boolean toRight) {
				MovieImage data = adapter.getItem(position);
				if (data.movieId == GameActivity.this.correctAnswer) {
					startListExitAnimation(swipelistview);
				} else {
					onQuestionIncorrect(position);
				}
			}
			@Override
			public void onClosed(int position, boolean fromRight) {}
			@Override
			public void onListChanged() {
				Log.v(TAG, "onListChanged()");
				swipelistview.closeOpenedItems();
			}
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
		swipelistview.setAnimationTime(50); // Animation time
		swipelistview.setAdapter(animAdapter);
		//		animAdapter.notifyDataSetChanged();
		adapter.notifyDataSetChanged();
	}

	public void startQuestion() {
		//		for (int i=0; i<loadedMovies.size(); i++) {
		//			animAdapter.add(i, loadedMovies.get(i));
		//		}
		swipelistview.setEnabled(true);
		overlayList.setEnabled(true);
		swipelistview.closeOpenedItems();
		loadAnimAdapter();

		adapter.addAll(loadedMovies);
		loading.dismiss();

		timer.reset();
		timer.start();
	}

	public void onQuestionCorrect(int position) {
		int rand = randInt(0, 2);
		int soundResId = 0;
		if (rand == 0) {
			soundResId = R.raw.correctanswer1;
		} else if (rand == 1) {
			soundResId = R.raw.correctanswer2;
		} else if (rand == 2) {
			soundResId = R.raw.correctanswer3;
		}

		MediaPlayer mp = MediaPlayer.create(getApplicationContext(), soundResId);
		mp.start();

		swipelistview.closeOpenedItems();
		int timeLeft = timer.getProgress();
		timer.stop();
		MovieQuizApplication application = (MovieQuizApplication)getApplicationContext();
		int score = application.getPointsFromProgress(timeLeft);
		int totalPoints = application.getTotalPoints()+score;
		application.setTotalPoints(totalPoints);

		showQuestionCorrectOverlay(position, score, totalPoints);
	}

	public void onQuestionIncorrect(int position) {
		timer.stop();

		Toast.makeText(this, "GAME OVER SON", Toast.LENGTH_SHORT).show();
		int rand = randInt(0, 2);
		int soundResId = 0;
		if (rand == 0) {
			soundResId = R.raw.fail1;
		} else if (rand == 1) {
			soundResId = R.raw.fail2;
		} else if (rand == 2) {
			soundResId = R.raw.fail3;
		}
		MediaPlayer mp = MediaPlayer.create(getApplicationContext(), soundResId);
		mp.start();

		finish();
	}

	public void showQuestionCorrectOverlay(int position, int score, int totalPoints) {
		int correctRow = position;

		overlayAdapter.clear();
		if (correctRow == 0) {
			overlayAdapter.add(new CorrectAnswerRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new TurnCoinsRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new TotalCoinsRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new NextAnswerRowView(this, GameActivity.itemHeight));
		} else if (correctRow == 1) {
			overlayAdapter.add(new TurnCoinsRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new CorrectAnswerRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new TotalCoinsRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new NextAnswerRowView(this, GameActivity.itemHeight));
		} else if (correctRow == 2) {
			overlayAdapter.add(new TurnCoinsRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new TotalCoinsRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new CorrectAnswerRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new NextAnswerRowView(this, GameActivity.itemHeight));
		} else if (correctRow == 3) {
			overlayAdapter.add(new NextAnswerRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new CorrectAnswerRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new TurnCoinsRowView(this, GameActivity.itemHeight));
			overlayAdapter.add(new TotalCoinsRowView(this, GameActivity.itemHeight));
		}

		overlayList.setVisibility(View.VISIBLE);
		animOverlayAdapter = new SwingLeftInAnimationAdapter(overlayAdapter);
		animOverlayAdapter.setAbsListView(overlayList);
		overlayList.setAdapter(animOverlayAdapter);
		overlayList.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
				questions.remove(0);

				if (questions.size() == 0) {
					Toast.makeText(GameActivity.this, "You win!!!!", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					startOverlayExitAnimation(overlayList);
					questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);
					timer.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);
				}
			}
			@Override
			public void onClosed(int position, boolean fromRight) {}
			@Override
			public void onListChanged() {
				overlayList.closeOpenedItems();
			}
			@Override
			public void onMove(int position, float x) {}
			@Override
			public void onStartOpen(int position, int action, boolean right) {
				Log.d("overlaylist", String.format("onStartOpen %d - action %d", position, action));
			}
			@Override
			public void onStartClose(int position, boolean right) {
				Log.d("overlaylist", String.format("onStartClose %d", position));
			}
			@Override
			public void onClickFrontView(int position) {
				Log.d("overlaylist", String.format("onClickFrontView %d", position));
				//				swipelistview.openAnimate(position); //when you touch front view it will open
			}
			@Override
			public void onClickBackView(int position) {
				Log.d("overlaylist", String.format("onClickBackView %d", position));
				//				swipelistview.closeAnimate(position);//when you touch back view it will close
			}
			@Override
			public void onDismiss(int[] reverseSortedPositions) {}

		});
		overlayAdapter.notifyDataSetChanged();
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

		Bundle b = getIntent().getExtras();
		if (b != null) {
			String levelId = b.getString("levelId");
			if (levelId != null) {
				this.levelId = levelId;
			}
		}
	}

	private void loadQuestions() {
		loading = ProgressDialog.show(GameActivity.this, "Loading ...", "", true);

		Level l = movieService.getLevelById(levelId);
		Log.v(TAG, "loadQuesions() " + l.name);

		List<Movie> movies = new ArrayList<Movie>(l.movies);
		if (movies.size() < 10) {
			int numToAdd = NUM_QUESTIONS-movies.size();
			for (int i=0; i<numToAdd; i++) {
				movies.add(movies.get(i % movies.size()));
			}
		}

		Collections.shuffle(movies);
		qLoaded = 0;
		for (int i=0; i<NUM_QUESTIONS; i++) {
			Movie m = movies.get(i);
			List<String> images = new ArrayList<String>(m.imageUrls);
			Collections.shuffle(images);

			String img1 = images.get(0);
			MovieImageData correctAnswer = new MovieImageData(m.title, m.mdid, img1);
			MovieImageData wrongAnswer1 = movieService.getRandomMovieImage();
			MovieImageData wrongAnswer2 = movieService.getRandomMovieImage();
			MovieImageData wrongAnswer3 = movieService.getRandomMovieImage();

			Question q = new Question(m.title, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3, new Callback() {

				@Override
				public void onComplete(Question question) {
					qLoaded++;
					Log.v(TAG, "Question#onComplete() " + questions.size() + " qLoaded " + qLoaded);
					if (qLoaded == NUM_QUESTIONS) {
						loading.dismiss();
						loadQuestion(questions.get(0));
						Log.v("MNF", "LOADED ALL QUESTIONS!!! " + Arrays.toString(questions.toArray()));
					}
				}
			});
			q.load(this);
			questions.add(q);
		}
	}

	int qLoaded = 0;

	private void loadQuestion(Question question) {
		adapter.clear();
		//		animAdapter.reset();
		//		((MovieAdapter)animAdapter.getDecoratedBaseAdapter()).clear();
		loadedMovies.clear();

		MovieImage correctAnswer = new MovieImage(question.images.get(question.correctAnswer.url), question.correctAnswer.mdId);
		MovieImage wrongAnswer1 = new MovieImage(question.images.get(question.wrongAnswer1.url), question.wrongAnswer1.mdId);
		MovieImage wrongAnswer2 = new MovieImage(question.images.get(question.wrongAnswer2.url), question.wrongAnswer2.mdId);
		MovieImage wrongAnswer3 = new MovieImage(question.images.get(question.wrongAnswer3.url), question.wrongAnswer3.mdId);

		loadedMovies.add(correctAnswer);
		loadedMovies.add(wrongAnswer1);
		loadedMovies.add(wrongAnswer2);
		loadedMovies.add(wrongAnswer3);
		Collections.shuffle(loadedMovies);

		setAnswers(correctAnswer.movieId, wrongAnswer1.movieId, wrongAnswer2.movieId, wrongAnswer3.movieId);
		questionText.setText(question.title);

		startQuestion();
	}

	private void setAnswers(int mdId1, int mdId2, int mdId3, int mdId4) {
		this.correctAnswer = mdId1;
		this.wrongAnswer1 = mdId2;
		this.wrongAnswer2 = mdId3;
		this.wrongAnswer3 = mdId4;
	}

}

