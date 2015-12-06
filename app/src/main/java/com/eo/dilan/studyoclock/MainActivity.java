package com.eo.dilan.studyoclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity
{
	Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent serv = new Intent( getApplicationContext() , AlarmService.class );
		getApplicationContext().startService( serv );
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
