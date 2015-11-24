package com.eo.dilan.studyoclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

//==============================================================
//PHASE 1:
//	(OPTIONAL)More visual cues for answering question correctly
//	---------
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
//PHASE 2:
//	Don't allow application to exit if alarm going off
//	shouldn't ask a question again if it has been answered correctly in this session of isAlarm
//	^---OR:	Keep a counter of questions left and add 1 to that counter when a question is answered wrong
//	cleaner looking alarm setting activity, up/down buttons for hour and minute as well as am/pm field
//	when creating first alarm set default alarm time to current time plus 5 minutes?
//	back to main menu button on every activity
//	should not be able to create an empty question
//		and should have at least one correct answer and cannot be only answer(can still have 2/3/4 correct answers)
//	should be able to remove a question
//	alarm should roll to next day if set to on and the alarm time has already happened today
//	should be able to cancel an alarm( i.e set alarm off )
//	when new alarm is created old alarm should be cancelled
//==============================================================
//	Paid features means that only premium users can use them
//==============================================================
//Phase 3:
//	(PAID?)Group questions by course/subject
//	(PAID?)Allow for alarms to ask questions by course/subject
//	(PAID?)Share questions through database on server
//	(PAID?)Allow for multiple alarms
//		both stored in database and distinguishing between alarms when broadcast is received
//	(PAID?)View all alarms activity instead of set alarm on menu
//	(PAID?)question info page showing how many times a question has been answered and how many times it was wrong
//		Keep track of right/wrongly answered questions in database on phone
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
