package com.escalivadaapps.moviequiz;

import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MovieQuizApplication extends Application {
	final private static String TOTAL_POINTS_KEY = "total_coins_key";
	private int totalPoints = 0;
	private Typeface fontRegular;
	private Typeface fontBold;
	private Typeface scoreFont;

	@Override
	public void onCreate() {
		super.onCreate();

		loadTotalPoints();

		//		fontBold = Typeface.createFromAsset(getAssets(), "JosefinSans-Bold.ttf");
		//		fontRegular = Typeface.createFromAsset(getAssets(), "JosefinSans-Regular.ttf");
		//		scoreFont = Typeface.createFromAsset(getAssets(), "open_24_display.ttf");
	}

	//Should be some sort of S-curve function
	public int getPointsFromProgress(int progress) {
		if (progress < 100 && progress > 90) {
			return 5;
		} else if (progress < 90 && progress > 75) {
			return 4;
		} else if (progress < 75 && progress > 25) {
			return 3;
		} else if (progress < 25 && progress > 10) {
			return 2;
		} else if (progress < 10 && progress > 0) { 
			return 1;
		} else { 
			return 0;
		}
	}

	public void loadTotalPoints() {
		SharedPreferences prefs = getSharedPreferences(getPackageName()+"prefs", Context.MODE_PRIVATE);
		totalPoints = prefs.getInt(TOTAL_POINTS_KEY, 0);
	}

	public void saveTotalPoints() {
		SharedPreferences prefs = getSharedPreferences(getPackageName()+"prefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(TOTAL_POINTS_KEY, totalPoints);
		editor.commit();
	}

	public int getTotalPoints() { 
		return totalPoints;
	}

	public void setTotalPoints(int points) {
		totalPoints = points;
		saveTotalPoints();
	}

	public Typeface getRegularFont() {
		return fontRegular;
	}

	public Typeface getBoldFont() {
		return fontBold;
	}

	public Typeface getScoreFont() {
		return scoreFont;
	}

	private static String uniqueID = null;
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

	public synchronized static String id(Context context) {
		if (uniqueID == null) {
			SharedPreferences sharedPrefs = context.getSharedPreferences(
					PREF_UNIQUE_ID, Context.MODE_PRIVATE);
			uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
			if (uniqueID == null) {
				uniqueID = UUID.randomUUID().toString();
				Editor editor = sharedPrefs.edit();
				editor.putString(PREF_UNIQUE_ID, uniqueID);
				editor.commit();
			}
		}
		return uniqueID;
	}

}
