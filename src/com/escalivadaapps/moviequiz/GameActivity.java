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
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.escalivadaapps.moviequiz.Question.Callback;
import com.escalivadaapps.moviequiz.service.Level;
import com.escalivadaapps.moviequiz.service.Movie;
import com.escalivadaapps.moviequiz.service.MovieImageData;
import com.escalivadaapps.moviequiz.service.MovieService;
import com.escalivadaapps.moviequiz.service.MovieService.MyLocalBinder;
import com.escalivadaapps.moviequiz.views.CorrectAnswerSlotOneView;
import com.escalivadaapps.moviequiz.views.GameOverSlotFourView;
import com.escalivadaapps.moviequiz.views.GameOverSlotOneView;
import com.escalivadaapps.moviequiz.views.GameOverSlotThreeView;
import com.escalivadaapps.moviequiz.views.GameOverSlotTwoView;
import com.escalivadaapps.moviequiz.views.LevelPassedSlotFour;
import com.escalivadaapps.moviequiz.views.LevelPassedSlotOne;
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
	final static private int START_TIME_MS = 8000;
	final static private int NUM_QUESTIONS = 10;
	final static public int RESULT_CODE_LEVEL_UNLOCKED = 1212345;

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
				MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.alarmclock_fade_boosted);
				mp.start();
				startListExitAnimation(swipelistview, new AnimationCallback() {

					@Override
					public void onAnimationEnded() {
						onQuestionIncorrect(0);						
					}
				});
			}
		});
		this.listOverlay = (ViewGroup)findViewById(R.id.listOverlay);

		doListStuff();
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
		final AnimationCallback callback;
		public MyAnimationListener(int position, final AnimationCallback callback) { 
			this.position = position; 
			this.callback = callback;
		}
		@Override
		public void onAnimationStart(Animator animation) {}
		@Override
		public void onAnimationEnd(Animator animation) {
			if (position == 3) {
				callback.onAnimationEnded();
			}
		}
		@Override
		public void onAnimationCancel(Animator animation) {}
		@Override
		public void onAnimationRepeat(Animator animation) {}
	}

	private final static int ROW_ANIM_DURATION = 500;

	private void startListExitAnimation(final ListView list, final AnimationCallback callback) {
		list.setEnabled(false);
		for (int i=0; i<list.getCount(); i++) {
			final View rowView = list.getChildAt(i); 
			rowView.animate().setDuration(ROW_ANIM_DURATION)
			.setStartDelay(i*100)
			.alpha(0f)
			.translationX(rowView.getWidth())
			.setListener(new MyAnimationListener(i, callback));
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
		animAdapter = new SwingLeftInAnimationAdapter(adapter);
		animAdapter.setAbsListView(swipelistview);
		swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(final int position, boolean toRight) {
				timer.stop();
				MovieImage data = adapter.getItem(position);
				if (data.movieId == GameActivity.this.correctAnswer) {
					startListExitAnimation(swipelistview, new AnimationCallback() {

						@Override
						public void onAnimationEnded() {
							adapter.clear();
							onQuestionCorrect(correctAnswer);							
						}
					});
				} else {
					startListExitAnimation(swipelistview, new AnimationCallback() {

						@Override
						public void onAnimationEnded() {
							adapter.clear();
							onQuestionIncorrect(position);
						}
					});
				}
				questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);
				timer.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);
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
		swipelistview.setEnabled(true);
		overlayList.setEnabled(true);
		swipelistview.closeOpenedItems();
		loadAnimAdapter();
		questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);
		timer.setVisibility(View.VISIBLE);
		timer.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);

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
			soundResId = R.raw.correctanswer1;
		} else if (rand == 2) {
			soundResId = R.raw.correctanswer1;
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

		showQuestionIncorrectOverlay();
	}

	public interface AnimationCallback {
		public void onAnimationEnded();
	}

	public void showQuestionIncorrectOverlay() {

		overlayAdapter.clear();
		overlayAdapter.add(new GameOverSlotOneView(this, GameActivity.itemHeight));
		overlayAdapter.add(new GameOverSlotTwoView(this, GameActivity.itemHeight));
		overlayAdapter.add(new GameOverSlotThreeView(this, GameActivity.itemHeight));
		overlayAdapter.add(new GameOverSlotFourView(this, GameActivity.itemHeight));

		questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);
		questionText.setText(getString(R.string.fail_text));

		overlayList.setVisibility(View.VISIBLE);
		animOverlayAdapter = new SwingLeftInAnimationAdapter(overlayAdapter);
		animOverlayAdapter.setAbsListView(overlayList);
		overlayList.setAdapter(animOverlayAdapter);
		overlayList.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(final int position, boolean toRight) {
				startListExitAnimation(overlayList, new AnimationCallback() {

					@Override
					public void onAnimationEnded() {
						overlayList.closeOpenedItems();
						overlayList.setVisibility(View.INVISIBLE);
						finish();
						overridePendingTransition(0, android.R.anim.fade_out);
						if (position == 0) {
							Intent gameIntent = new Intent(GameActivity.this, GameActivity.class);
							gameIntent.putExtra("levelId", levelId);
							GameActivity.this.startActivity(gameIntent);
							overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						}
					}
				});
				questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);
				timer.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);
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
			public void onStartOpen(int position, int action, boolean right) {}
			@Override
			public void onStartClose(int position, boolean right) {}
			@Override
			public void onClickFrontView(int position) {}
			@Override
			public void onClickBackView(int position) {}
			@Override
			public void onDismiss(int[] reverseSortedPositions) {}

		});
		overlayAdapter.notifyDataSetChanged();
	}

	public void showQuestionCorrectOverlay(int position, int score, int totalPoints) {
		int correctRow = position;

		int roundNum = NUM_QUESTIONS - questions.size() + 1;
		questionText.setText("Round " + roundNum + "/" + NUM_QUESTIONS + " complete!");
		questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);
		//		timer.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);

		overlayAdapter.clear();
		overlayAdapter.add(new CorrectAnswerSlotOneView(this, GameActivity.itemHeight));
		overlayAdapter.add(new ImageRow(this, GameActivity.itemHeight, new ColorDrawable(getResources().getColor(R.color.FlatRed))));
		overlayAdapter.add(new ImageRow(this, GameActivity.itemHeight, new ColorDrawable(getResources().getColor(R.color.FlatBlue))));
		overlayAdapter.add(new ImageRow(this, GameActivity.itemHeight, new ColorDrawable(getResources().getColor(R.color.FlatGreen))));

		overlayList.setVisibility(View.VISIBLE);
		animOverlayAdapter = new SwingLeftInAnimationAdapter(overlayAdapter);
		animOverlayAdapter.setAbsListView(overlayList);
		overlayList.setAdapter(animOverlayAdapter);
		overlayList.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
				questions.remove(0);

				if (questions.size() == 0) {
					showGameWon();
				} else {
					startListExitAnimation(overlayList, new AnimationCallback() {

						@Override
						public void onAnimationEnded() {
							overlayList.closeOpenedItems();
							overlayList.setVisibility(View.INVISIBLE);
							loadQuestion(questions.get(0));	
							questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);
							timer.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);
						}
					});
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

	private void showGameWon() {
		startListExitAnimation(overlayList, new AnimationCallback() {

			@Override
			public void onAnimationEnded() {
				MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.level_passed);
				mp.start();

				overlayList.closeOpenedItems();
				questionText.setText(String.format(getString(R.string.level_passed), movieService.getLevelById(levelId).levelId));
				questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(1f);

				overlayAdapter.clear();
				overlayAdapter.add(new LevelPassedSlotOne(GameActivity.this, GameActivity.itemHeight));
				overlayAdapter.add(new GameOverSlotTwoView(GameActivity.this, GameActivity.itemHeight));
				overlayAdapter.add(new ImageRow(GameActivity.this, GameActivity.itemHeight, new ColorDrawable(getResources().getColor(R.color.FlatRed))));
				overlayAdapter.add(new LevelPassedSlotFour(GameActivity.this, GameActivity.itemHeight));

				overlayList.setVisibility(View.VISIBLE);
				overlayList.setEnabled(true);
				animOverlayAdapter = new SwingLeftInAnimationAdapter(overlayAdapter);
				animOverlayAdapter.setAbsListView(overlayList);
				overlayList.setAdapter(animOverlayAdapter);
				overlayList.setSwipeListViewListener(new BaseSwipeListViewListener() {
					@Override
					public void onOpened(int position, boolean toRight) {
						Log.v("MNF", "onOpened");
						
						startListExitAnimation(overlayList, new AnimationCallback() {
							
							@Override
							public void onAnimationEnded() {
								Intent result = new Intent();
								result.putExtra("levelId", levelId);
								setResult(RESULT_CODE_LEVEL_UNLOCKED, result);
								finish();
								overridePendingTransition(0, android.R.anim.fade_out);
							}
						});
					}
					@Override
					public void onClosed(int position, boolean fromRight) {
						Log.v("MNF", "onClosed");
					}
					@Override
					public void onListChanged() {
						Log.v("MNF", "onListChanged");
						overlayList.closeOpenedItems(); 
					}
					@Override
					public void onMove(int position, float x) {
						Log.v("MNF", "onMove");
					}
					@Override
					public void onStartOpen(int position, int action, boolean right) {
						Log.v("MNF", "onStartOpen");
					}
					@Override
					public void onStartClose(int position, boolean right) {
						Log.v("MNF", "onStartClose");
					}
					@Override
					public void onClickFrontView(int position) {
						Log.v("MNF", "onClickFrontView");
					}
					@Override
					public void onClickBackView(int position) {
						Log.v("MNF", "onClickBackView");
					}
					@Override
					public void onDismiss(int[] reverseSortedPositions) {
						Log.v("MNF", "onDismiss");
					}

				});
				overlayAdapter.notifyDataSetChanged();
			}
		});
		questionText.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);
		timer.animate().setDuration(ROW_ANIM_DURATION).alpha(0f);
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
		qLoaded = 0;
		loading = ProgressDialog.show(GameActivity.this, "Loading ...", "", true, true);

		Level l = movieService.getLevelById(levelId);
		Log.v(TAG, "loadQuesions() " + l.name);

		List<Movie> movies = l.getMovieList();
		List<MovieImageData> levelImages = new ArrayList<MovieImageData>(l.movieImages);
		Collections.shuffle(levelImages);
		Collections.shuffle(movies);

		for (int i=0; i<NUM_QUESTIONS; i++) {
			Movie m = movies.get(i);
			List<String> movieImageUrls = new ArrayList<String>(m.imageUrls);
			Collections.shuffle(movieImageUrls);
			List<MovieImageData> movieImages = new ArrayList<MovieImageData>(l.movieImages);
			Collections.shuffle(movieImages);

			List<String> urls = new ArrayList<String>();
			MovieImageData correctAnswer = new MovieImageData(m.title, m.mdid, movieImageUrls.get(0));
			urls.add(correctAnswer.url);

			MovieImageData wrongAnswer1;
			MovieImageData wrongAnswer2;
			MovieImageData wrongAnswer3;

			if (l.isRandom) {
				wrongAnswer1 = movieService.getRandomMovieImage(urls);
				urls.add(wrongAnswer1.url);
				wrongAnswer2 = movieService.getRandomMovieImage(urls);
				urls.add(wrongAnswer2.url);
				wrongAnswer3 = movieService.getRandomMovieImage(urls);
			} else {
				List<MovieImageData> toRemove = new ArrayList<MovieImageData>();
				for (String imgUrl : m.imageUrls) {
					toRemove.add(new MovieImageData(m.title, m.mdid, imgUrl));
				}
				movieImages.remove(toRemove);

				wrongAnswer1 = movieImages.get(0);
				wrongAnswer2 = movieImages.get(1);
				wrongAnswer3 = movieImages.get(2);
			}

			Question q = new Question(correctAnswer.movieTitle, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3, new Callback() {

				@Override
				public void onComplete(Question question) {
					qLoaded++;
					Log.v(TAG, "Question#onComplete() " + questions.size() + " qLoaded " + qLoaded);
					if (qLoaded == NUM_QUESTIONS) {
						loading.dismiss();
						loadQuestion(questions.get(0));
						Log.v("MNF", "LOADED ALL QUESTIONS!!! " + Arrays.toString(questions.toArray()));
					}
				}});
			q.load(this);
			questions.add(q);
		}
	}

	int qLoaded = 0;

	private void loadQuestion(Question question) {
		adapter.clear();
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

