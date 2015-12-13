package com.eo.dilan.studyoclock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eo.dilan.studyoclock.database.Answer;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.PreferenceKeys;
import com.eo.dilan.studyoclock.database.Question;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionActivity extends AppCompatActivity
{
	private DataHelper db;

	private int totalNeeded;

	private Cursor cursor;

	private Vibrator vibrator;

	private final QuestionActivity me = this;

	private boolean isAlarm = false;

	private SharedPreferences shared;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		shared = getSharedPreferences(PreferenceKeys.PREF_KEY, Context.MODE_PRIVATE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_question);
		vibrator = (Vibrator ) getSystemService( this.VIBRATOR_SERVICE);
		Intent intent = getIntent();

		if ( intent != null && intent.getExtras() != null && intent.getExtras().getString("alarm") != null )
		{
			shared.edit().putBoolean(PreferenceKeys.ALARMING, true).commit();
			isAlarm = true;
			long[] pattern = { 0, 200, 500 };
			vibrator.vibrate( pattern, 0);
		}

		new LongOperation().execute();
	}

	private void updateQuestion( Question question )
	{
		TextView quest = ( TextView ) findViewById(R.id.question);
		if ( question == null )
		{
			quest.setText("NO QUESTION");
			return;
		}

		quest.setText(question.question);
		ArrayList< Button > buttons = new ArrayList<>();
		buttons.add(( Button ) findViewById(R.id.button1));
		buttons.add(( Button ) findViewById(R.id.button2));
		buttons.add(( Button ) findViewById(R.id.button3));
		buttons.add(( Button ) findViewById(R.id.button4));
		int min = Math.min(buttons.size(), question.answers.size());
		Collections.shuffle(question.answers);
		for ( int i = 0; i < min; i++ )
		{
			Button button = buttons.get(i);
			button.setVisibility(View.VISIBLE);
			Answer answer = question.answers.get(i);
			boolean yes = (answer.isCorrect > 0);
			button.setText(answer.answer);
			new ClickHandler(button, yes);
		}

		for ( int i = min; i < buttons.size(); i++ )
		{
			buttons.get( i ).setText( "" );
			buttons.get( i ).setVisibility(View.GONE);
		}

		showNeeded();
	}

	@Override
	public void onBackPressed(){
		if ( isAlarm )
		{
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if ( db != null )
		{
			//db.saveAllQuestions();
		}

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if ( vibrator != null )
		{
			vibrator.cancel();
		}

		if ( cursor != null )
		{
			cursor.close();
			cursor = null;
		}
		else
		{
			Logger.print( this, "Cursor BAD" , "more" );
		}

		if ( db != null )
		{
			//db.saveAllQuestions();
		}

		if ( isAlarm && totalNeeded > 0 )
		{
			shared.edit().putBoolean(PreferenceKeys.ALARMING, true).putLong(PreferenceKeys.QUESTION, db.getCurrentQuestion().id).putInt(PreferenceKeys.TOTAL , totalNeeded ).commit();
			return;
		}
		else if ( isAlarm )
		{
			Logger.print(this.getApplicationContext(), "Reached here", "alarm off");
			return;
		}
		shared.edit().putBoolean( PreferenceKeys.ALARMING , false).remove(PreferenceKeys.QUESTION).remove(PreferenceKeys.TOTAL).commit();
	}

	private void animateMyView(final View view, final boolean isCorrect )
	{
		final float[] from = new float[3],
				to =   new float[3];

		String color = ( isCorrect ? "green" :  "red" );
		Color.colorToHSV(Color.parseColor("white"), from);   // from white
		Color.colorToHSV(Color.parseColor( color ), to);     // to red

		ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
		anim.setDuration(500);                              // for 300 ms

		final float[] hsv  = new float[3];                  // transition color
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
			@Override public void onAnimationUpdate(ValueAnimator animation) {
				// Transition along each axis of HSV (hue, saturation, value)
				hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
				hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
				hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

				view.setBackgroundColor(Color.HSVToColor(hsv));
			}
		});

		anim.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator arg0)
			{
			}

			@Override
			public void onAnimationRepeat(Animator arg0)
			{
			}

			@Override
			public void onAnimationEnd(Animator arg0)
			{
				view.setBackgroundResource(android.R.drawable.btn_default);
				handleQuestionEnd( isCorrect );
			}
		});

		anim.start();
	}

	private void handleQuestionEnd( final boolean isCorrect )
	{
		final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		db.getCurrentQuestion().incrementAmount( this.getApplicationContext(), isCorrect );
		db.incrementQuestion();
		totalNeeded += ( isCorrect ? -1 : 1 );
		if ( totalNeeded == 0 && isAlarm )
		{
			vib.cancel();
			shared.edit().putBoolean( PreferenceKeys.ALARMING , false).remove(PreferenceKeys.QUESTION).remove(PreferenceKeys.TOTAL).commit();
			Intent intent = new Intent( me, MainActivity.class );
			me.startActivity( intent );
			return;
		}
		//updateQuestion(db.getCurrentQuestion() );
		updateQuestion(db.getCurrentQuestionLoader( cursor, 5 ));
	}

	private void showNeeded()
	{
		TextView v = (TextView)findViewById(R.id.remains);
		if ( !isAlarm || totalNeeded <= 0 )
		{
			v.setText( "" );
			return;
		}
		v.setText( totalNeeded + ":" );
	}

	private class ClickHandler
	{
		public ClickHandler( Button button, boolean correct )
		{
			final boolean isCorrect = correct;
			button.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					animateMyView(v , isCorrect );
				}
			});
		}
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = DataHelper.instance(me.getApplicationContext() );
			long id = shared.getLong(PreferenceKeys.QUESTION, -1);
			int rem = shared.getInt(PreferenceKeys.TOTAL, -1);
			if ( id != -1 && rem != -1 )
			{
				db.setToQuestion(id);
				cursor = db.loadQuestions( id , 4 );
			}
			else
			{
				cursor = db.loadQuestions( -1 , 5 );
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			long id = shared.getLong(PreferenceKeys.QUESTION, -1);
			int rem = shared.getInt(PreferenceKeys.TOTAL, -1);
			if ( id != -1 && rem != -1 )
			{
				totalNeeded = rem;
			}
			else if ( isAlarm )
			{
				totalNeeded = db.alarms.get(0).correct;
			}

			if ( shared.getBoolean(PreferenceKeys.ALARM_REPEAT, true) )
			{
				db.alarms.get(0).setForTomorrow(me.getApplicationContext(), 0);
			}

			updateQuestion(db.getCurrentQuestionLoader( cursor, 5 ));
		}


	}
}
