package com.eo.dilan.studyoclock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.AlertBuilder;
import com.eo.dilan.studyoclock.database.DataHelper;

public class MainActivity extends AppCompatActivity
{

	private DataHelper db;

	private boolean canRun = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent serv = new Intent( getApplicationContext() , AlarmService.class );
		getApplicationContext().startService(serv);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);
		new LongOperation().execute();
		addUnicodeToButtons();
	}

	private void addUnicodeToButtons( )
	{
		//viewQuestionBtn
		//setAlarmBtn
		//seeQuestionBtn
		//settingsBtn
		Button btn = (Button)findViewById(R.id.viewQuestionBtn);
		btn.setText( new StringBuilder( "\u21F6 " ).append( btn.getText()) );
		btn = (Button)findViewById(R.id.seeQuestionBtn);
		btn.setText( new StringBuilder( "\uD83D\uDCD6 " ).append( btn.getText()) );
		btn = (Button)findViewById(R.id.setAlarmBtn);
		btn.setText( new StringBuilder( "\uD83D\uDD50 " ).append( btn.getText()) );
		btn = (Button)findViewById(R.id.settingsBtn);
		btn.setText( new StringBuilder( "\u2261 " ).append( btn.getText()) );

	}

	public void clickSettings(View v )
	{
		if ( !canRun )
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent( this , SettingsActivity.class );
		startActivity(intent);
	}

	public void onClickShowQ(View v)
	{
		if ( !canRun )
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
			return;
		}

		if ( db != null && !db.areThereQuestions() )
		{
			Toast.makeText(getApplicationContext(), "You need to add questions in order to study", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent( this , QuestionActivity.class );
		startActivity( intent );
	}

	@Override
	public void onBackPressed()
	{
		return;
	}

	public void onClickAddAlarm(View v)
	{
		if ( !canRun )
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent( this , AlarmActivity.class );
		startActivity(intent);
	}

	public void allQuestionsClick(View v)
	{
		if ( !canRun )
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent( this , AllQuestionsActivity.class );
		startActivity( intent );
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = DataHelper.instance(MainActivity.this.getApplicationContext());
			db.clearQuestionsInMem();
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			canRun = true;
			Intent intent = getIntent();
			if ( intent != null && intent.getExtras() != null && intent.getExtras().getString("alarm") != null && db != null && db.areThereQuestions() )
			{
				Intent appIntent = new Intent(MainActivity.this, QuestionActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("alarm", "yes" );
				appIntent.putExtras(mBundle);
				startActivity(appIntent);
			}

		}


	}
}
