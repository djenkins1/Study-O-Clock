package com.eo.dilan.studyoclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.PreferenceKeys;

public class SettingsActivity extends AppCompatActivity
{
	private DataHelper db;

	private SharedPreferences shared;

	private boolean isLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		shared = getSharedPreferences(PreferenceKeys.PREF_KEY, Context.MODE_PRIVATE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_settings);
		Switch sw1 = (Switch ) findViewById(R.id.repeatSwitch);
		sw1.setChecked(shared.getBoolean(PreferenceKeys.ALARM_REPEAT, true));
		new LongOperation().execute();
	}

	public void resetStatsClick(View v)
	{
		if ( !isLoaded || db == null )
		{
			Toast.makeText(getApplicationContext(), "Please wait!", Toast.LENGTH_LONG).show();
			return;
		}

		db.resetQuestionStats();
		Toast.makeText(getApplicationContext(), "Question statistics reset!", Toast.LENGTH_LONG).show();
	}

	public void saveSettingsClick(View v )
	{
		Switch sw1 = (Switch ) findViewById(R.id.repeatSwitch);
		shared.edit().putBoolean(PreferenceKeys.ALARM_REPEAT, sw1.isChecked() ).apply();
		onBackPressed();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = DataHelper.instance(SettingsActivity.this.getApplicationContext() );
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			isLoaded = true;

		}
	}
}
