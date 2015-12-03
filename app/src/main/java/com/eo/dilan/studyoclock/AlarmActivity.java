package com.eo.dilan.studyoclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.Alarm;
import com.eo.dilan.studyoclock.database.DataHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmActivity extends AppCompatActivity
{
	private DataHelper db;

	private final AlarmActivity me = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_alarm);
		new LongOperation().execute();
	}

	public void setMyAlarm(View v)
	{
		cancelAlarm(v);
		TimePicker tpick = (TimePicker)findViewById(R.id.timePicker1);
		EditText text = ( EditText ) findViewById(R.id.numQuestions);
		int hour = tpick.getCurrentHour();
		int min = tpick.getCurrentMinute();
		String numText = text.getText().toString();
		if ( numText.equals("") )
		{
			Toast.makeText(getApplicationContext(), "Number correct must be filled out", Toast.LENGTH_LONG).show();
			return;
		}
		int num = Integer.parseInt( numText );
		boolean isOn = ((Switch ) findViewById(R.id.switch1)).isChecked();
		Log.d("Hour", hour + "");
		Log.d("Min", min + "");
		Log.d("To Ask", num + "");
		db.updateAlarm(db.alarms.get(0).withHour(hour).withMinute(min).withOn((isOn ? 1 : 0)).withCorrect(num));
		if (isOn)
		{
			AlarmReceiver.addAlarm(this, hour, min);
		}
		//mspin.setSelection(0);
		//mspin2.setSelection(1);
		//text.setText("");
		Intent intent = new Intent( this, MainActivity.class);
		startActivity(intent);
	}
	
	private void cancelAlarm(View v )
	{
		//update the alarm in the database to off
		//cancel the alarm in the database currently
		if ( db != null )
		{
			Alarm alarm = db.alarms.get( 0 );
			AlarmReceiver.cancelThisAlarm( me, alarm.hour, alarm.minute);
			alarm.withOn(0);
			db.updateAlarm( alarm );
		}
	}

	private void updateList(Alarm alarm )
	{
		Switch sw1 = (Switch ) findViewById(R.id.switch1);
		TimePicker tpick = (TimePicker)findViewById(R.id.timePicker1);
		EditText text = ( EditText ) findViewById(R.id.numQuestions);
		tpick.setCurrentHour(alarm.hour);
		tpick.setCurrentMinute(alarm.minute);
		text.setText( alarm.correct + "" );
		sw1.setChecked( alarm.isOn == 1 );
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = new DataHelper( me );
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			updateList( db.alarms.get(0) );
		}
	}

}
