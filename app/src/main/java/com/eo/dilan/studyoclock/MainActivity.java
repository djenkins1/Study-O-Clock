package com.eo.dilan.studyoclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

//==============================================================
//	(DONE)Probably should get rid of vibration cue for right/wrong answer if isAlarm
//	(DONE)get the question's info in AddQuestion if question was passed in bundle
//	(DONE)randomize question order and answer order
//	(DONE)need another activity for adding questions
//	(DONE)need to be able to see if question is answered correctly
//	(DONE)need to have database setup for questions
//	(DONEneed to be able to go off at alarm time
//	(DONE)need to be able to move into question asking phase on alarm going off and app open
//	(DONEshow all questions activity, with click on edit?
//	(DONE)need to be able to store alarm time/question amount, only one row at a time
//	(DONE)need alarm setting activity that shows current alarm and number of correct questions needed
//		(DONE)have way to set alarm from empty, just need to fill in from database
//		(DONE)also should have a default alarm set in database for current time when created
//	(DONE)need to keep ringing until enough correct answers
//		(DONE)for now ingore wrong answers, they don't do anything
//==============================================================

public class MainActivity extends AppCompatActivity
{
	Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);
		vibrator = (Vibrator ) getSystemService( this.VIBRATOR_SERVICE);
		Intent intent = getIntent();
		if ( intent != null && intent.getExtras() != null && intent.getExtras().getString("alarm") != null )
		{
			Intent appIntent = new Intent(this, QuestionActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putString("alarm", "yes" );
			appIntent.putExtras(mBundle);
			startActivity(appIntent);
		}

		//DataHelper db = new DataHelper(this);

	}

	public void onClickShowQ(View v)
	{
		//vibrator.vibrate( 500 );
		Intent intent = new Intent( this , QuestionActivity.class );
		startActivity( intent );
	}

	public void onClickAddQ(View v)
	{
		Intent intent = new Intent( this , AddQuestionActivity.class );
		startActivity( intent );
	}

	public void onClickAddAlarm(View v)
	{
		Intent intent = new Intent( this , AlarmActivity.class );
		startActivity( intent );
		//addAlarm(22, 50);
	}

	public void allQuestionsClick(View v)
	{
		Intent intent = new Intent( this , AllQuestionsActivity.class );
		startActivity( intent );
	}


}
