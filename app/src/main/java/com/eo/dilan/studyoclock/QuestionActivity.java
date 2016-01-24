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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.Alarm;
import com.eo.dilan.studyoclock.database.Answer;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.PreferenceKeys;
import com.eo.dilan.studyoclock.database.Question;
import com.eo.dilan.studyoclock.subject.StudySubject;
import com.eo.dilan.studyoclock.subject.Subject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionActivity extends AppCompatActivity
{
	private DataHelper db;

	private int totalNeeded;

	private Cursor cursor;

	private Vibrator vibrator;

    private MediaPlayer player;

    private boolean playsSound = false;

	private final QuestionActivity me = this;

	private boolean isAlarm = false;

	private SharedPreferences shared;

	private StringBuilder questionsWrong = new StringBuilder("");

    private StudySubject extra;

    private StudySubject course;

    private int totalIntent;

	private long alarmId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		shared = getSharedPreferences(PreferenceKeys.PREF_KEY, Context.MODE_PRIVATE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_study);
		vibrator = (Vibrator ) getSystemService( VIBRATOR_SERVICE);
		Intent intent = getIntent();

		questionsWrong = new StringBuilder( shared.getString( PreferenceKeys.Q_LIST , "" ) );
		if ( intent != null && intent.getExtras() != null  )
		{
            if ( intent.getExtras().getString( "alarm" ) != null )
			{
				alarmId = intent.getExtras().getInt( PreferenceKeys.ALARM_KEY , -1 );
                shared.edit().putBoolean(PreferenceKeys.ALARMING, true).putInt( PreferenceKeys.ALARM_KEY, (int)alarmId ).commit();
                isAlarm = true;
            }
            else if ( intent.getExtras().getString( PreferenceKeys.COURSE_INTENT ) != null
                    && intent.getExtras().getString(PreferenceKeys.EXTRA_INTENT ) != null
                    && intent.getExtras().getString(PreferenceKeys.TOTAL_INTENT ) != null
					)
            {
                extra = StudySubject.getSubject(this, intent.getExtras().getString(PreferenceKeys.EXTRA_INTENT));
                course = StudySubject.getCourseSubject(Long.parseLong(intent.getExtras().getString(PreferenceKeys.COURSE_INTENT)));
                totalIntent = Integer.parseInt( intent.getExtras().getString( PreferenceKeys.TOTAL_INTENT ) );
            }
		}
		else
		{
			Toast.makeText(this.getApplicationContext(), "Press back to stop studying" , Toast.LENGTH_LONG ).show();
		}

		new LongOperation().execute();
	}

	private void updateQuestion( Question question )
	{
        TextView quest = ( TextView ) findViewById(R.id.question);
		if ( question == null )
		{
            quest.setText("NO QUESTION");
            if ( course != null )
            {
                Toast.makeText( this, "No questions match the options provided." , Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent( this , SubjectActivity.class );
                startActivity( intent );
            }

			return;
		}

        ArrayList< Button > buttons = new ArrayList<>();
        buttons.add((Button) findViewById(R.id.button1));
        buttons.add(( Button ) findViewById(R.id.button2));
        buttons.add((Button) findViewById(R.id.button3));
        buttons.add((Button) findViewById(R.id.button4));
		quest.setText(question.question);

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
	public void onBackPressed()
    {
		if ( isAlarm || course != null )
		{
            Toast.makeText( this, "There are questions remaining" , Toast.LENGTH_SHORT ).show();
			return;
		}

		Intent intent = new Intent( me, ScoreActivity.class );
		intent.putExtra(PreferenceKeys.Q_LIST, questionsWrong.toString());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		me.startActivity(intent);
	}

	@Override
	public void onPause()
	{
		super.onPause();
        if ( playsSound && player != null && player.isPlaying() )
        {
            player.pause();
        }
	}

    public void onWindowFocusChanged (boolean hasFocus)
    {

        if(hasFocus && playsSound && player != null )
        {

            if (!player.isPlaying())
            {
                player.start();
            }

        }
        else
        {

            if ( playsSound && player != null && player.isPlaying())
            {
                player.pause();
            }
        }
    }

    public void onResume()
    {
        super.onResume();
        if ( playsSound && player != null )
        {
            player.start();
        }
    }

	@Override
	public void onDestroy()
	{
		super.onDestroy();
        cancelSoundAndVibrate();

		if ( cursor != null )
		{
			cursor.close();
			cursor = null;
		}
		else
		{
			Logger.print( this, "Cursor BAD" , "more" );
		}

		if ( isAlarm && totalNeeded > 0 && db.getCurrentQuestion() != null )
		{
			shared.edit().putBoolean(PreferenceKeys.ALARMING, true).putLong(PreferenceKeys.QUESTION, db.getCurrentQuestion().id).putInt(PreferenceKeys.TOTAL , totalNeeded ).commit();
			return;
		}
		else if ( isAlarm )
		{
			Logger.print(this.getApplicationContext(), "Reached here", "alarm off");
			return;
		}
		shared.edit().putBoolean( PreferenceKeys.ALARMING , false).remove(PreferenceKeys.QUESTION).remove(PreferenceKeys.TOTAL).remove(PreferenceKeys.Q_LIST ).commit();
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
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

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
				view.setBackgroundResource(R.drawable.apptheme_btn_default_holo_dark);
				handleQuestionEnd( isCorrect );
			}
		});

		anim.start();
	}

	private void handleQuestionEnd( final boolean isCorrect )
	{
		if ( !isCorrect )
		{
			questionsWrong = PreferenceKeys.addNumberToEnd( questionsWrong, db.getCurrentQuestion().id );
		}

		db.saveQuestion(db.getCurrentQuestion().incrementAmount(this.getApplicationContext(), isCorrect));
		db.incrementQuestion();
		totalNeeded += ( isCorrect ? -1 : 1 );

		if ( totalNeeded == 0 && isAlarm )
		{
            cancelSoundAndVibrate();
			shared.edit().putBoolean( PreferenceKeys.ALARMING , false).remove(PreferenceKeys.QUESTION).remove(PreferenceKeys.Q_LIST).remove(PreferenceKeys.TOTAL).commit();
            gotoWrongActivity();
			return;
		}
        else if ( totalNeeded == 0 && course != null )
        {
            gotoWrongActivity();
            return;
        }

		updateQuestion(db.getCurrentQuestionLoader(cursor, 5));
	}

    private void cancelSoundAndVibrate()
    {
        if ( vibrator != null )
        {
            vibrator.cancel();
        }

        if ( player != null && player.isPlaying() )
        {
            player.stop();
            player.reset();
            player.release();
            player = null;

        }
    }

    private void gotoWrongActivity()
    {
        Intent intent = new Intent( me, ScoreActivity.class );
        intent.putExtra( PreferenceKeys.Q_LIST , questionsWrong.toString() );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        me.startActivity(intent);
    }

	private void showNeeded()
	{
		TextView v = (TextView)findViewById(R.id.remains);
		if ( ( !isAlarm && course == null ) || totalNeeded <= 0 )
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
					animateMyView(v, isCorrect );
				}
			});
		}
	}

    private void startSounds()
    {
        try {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if ( alert == null )
            {
                // alert is null, using backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if(alert == null)
                {
                    // alert backup is null, using 2nd backup
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    if ( alert == null )
                    {
                        throw new IOException( "Could not load uri for any backup sounds" );
                    }
                }
            }

            player = new MediaPlayer();
            player.setDataSource(QuestionActivity.this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if ( audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL && audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                player.setAudioStreamType(AudioManager.STREAM_ALARM);
                player.setLooping(true);
                player.prepare();
                player.start();
                playsSound = true;
            }
            else
            {
                startVibrate();
            }
        }
        catch( IOException e )
        {
            Log.d( "PROBLEM" , "PROBLEM WITH " + e.getMessage() );
            startVibrate();
        }
    }

    private void startVibrate()
    {
        long[] pattern = {0, 200, 500};
        vibrator.vibrate(pattern, 0);
    }

	private void loadCursor()
	{
		/*
		long id = shared.getLong(PreferenceKeys.QUESTION, -1);
		int rem = shared.getInt(PreferenceKeys.TOTAL, -1);
		if ( id != -1 && rem != -1 )
		{
			db.setToQuestion(id);
			cursor = db.loadQuestions( id , 4 );
		}
		else
		*/
		if ( course != null )
		{
			cursor =  db.getCursorOfQuestionSubjects( course, extra );
			cursor = db.loadQuestions(cursor, 5);
		}
		else
		{
			cursor = db.loadQuestions( -1 , 5 );
		}
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = DataHelper.instance(me.getApplicationContext() );
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

			Alarm alarm = db.getAlarm( alarmId );
            if ( isAlarm )
			{
				totalNeeded = alarm.correct;
				extra = StudySubject.getSubject( Subject.getSubject(alarm.extra) );
				course = StudySubject.getCourseSubject( alarm.course );
				Log.d( "EXTRA" , ( extra != null ? extra.toString() : "NULL" ) );
				Log.d( "COURSE" , ( course != null ? course.toString() : "NULL" ) );
				Log.d( "TOTAL" , totalNeeded + "");
				if ( alarm.sound != -1 )
                {
                    startSounds();
                }
                else
                {
                    startVibrate();
                }
			}
            else if ( course != null )
            {
                totalNeeded = totalIntent;
            }

			if ( shared.getBoolean(PreferenceKeys.ALARM_REPEAT, true) )
			{
				alarm.setForTomorrow(me.getApplicationContext(), 0);
			}
			else
			{
				db.updateAlarm( alarm.withOn(0) );
			}

			new CursorOperation().execute();

		}
	}

	private class CursorOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			loadCursor();
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			updateQuestion(db.getCurrentQuestionLoader(cursor, 5));
		}
	}

}
