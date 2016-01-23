package com.eo.dilan.studyoclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.Alarm;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.PreferenceKeys;

public class AlarmActivity extends AppCompatActivity
{
	private DataHelper db;

	private final AlarmActivity me = this;

    private long alarmId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_alarm);
        Intent intent = getIntent();
        if ( intent != null && intent.getExtras() != null  )
        {
            if (intent.getExtras().getString(PreferenceKeys.COURSE_INTENT) != null)
            {
                alarmId = Long.parseLong(intent.getExtras().getString(PreferenceKeys.COURSE_INTENT));
                Log.d("ALARM", intent.getExtras().getString(PreferenceKeys.COURSE_INTENT));
            }
        }
		new LongOperation().execute();
	}

	public void setMyAlarm( View v )
	{
		setMyAlarm( v , false);
	}

	private void setMyAlarm(View v, boolean gotoSubject)
	{
		if ( db == null )
		{
			Toast.makeText(getApplicationContext(), "Please wait!", Toast.LENGTH_LONG).show();
			return;
		}
		cancelAlarm(v);
		TimePicker tpick = ( TimePicker ) findViewById(R.id.timePicker1);
		//EditText text = ( EditText ) findViewById(R.id.numQuestions);
		int hour = tpick.getCurrentHour();
		int min = tpick.getCurrentMinute();
		/*
		String numText = text.getText().toString();
		if ( numText.equals("") )
		{
			Toast.makeText(getApplicationContext(), "Number correct must be filled out", Toast.LENGTH_LONG).show();
			return;
		}

		try
		{
			Integer.parseInt(numText);
		} catch ( Exception e )
		{
			Toast.makeText(getApplicationContext(), "Number correct must be a number", Toast.LENGTH_LONG).show();
			return;
		}

		int num = Integer.parseInt(numText);
		if ( num <= 0 )
		{
			Toast.makeText(getApplicationContext(), "Number correct must be positive", Toast.LENGTH_LONG).show();
			return;
		}

		if ( num > 100 )
		{
			Toast.makeText(getApplicationContext(), "Number correct cannot be more than 100", Toast.LENGTH_LONG).show();
			return;
		}
		*/

		boolean isOn = (( Switch ) findViewById(R.id.switch1)).isChecked();
		Logger.print(this.getApplicationContext(), "Hour", hour + "");
		Logger.print(this.getApplicationContext(), "Min", min + "");
		boolean isVibrate = ((Switch) findViewById(R.id.vibrateSwitch)).isChecked();

		if ( alarmId != -1 )
		{
			Log.d( "Alarm update" , alarmId + " was updated" );
			db.updateAlarm( db.getAlarm(alarmId).withHour(hour).withMinute(min).withOn( isOn ).withSound( !isVibrate ) );
		}
		else
		{
			Alarm alarm = new Alarm();
			db.addAlarm(alarm.withHour(hour).withMinute(min).withOn(isOn).withSound( !isVibrate ));
			Log.d("Alarm create", alarm.id + " was created");
			alarmId = alarm.id;
		}

		if ( isOn )
		{
			AlarmReceiver.addAlarm(this.getApplicationContext(), hour, min, (int)alarmId );
		}

		Intent intent;
		if ( gotoSubject )
		{
			Log.d( "Subject" , alarmId + "" );
			intent = new Intent(this, SubjectActivity.class);
			intent.putExtra("alarm", alarmId );
		}
		else
		{
			Log.d( "All Alarms" , "MORE");
			intent = new Intent(this, AllQuestionsActivity.class);
			intent.putExtra("alarm", "TRUE");
		}
		startActivity(intent);
	}

	public void setMyOptions( View v)
	{
		setMyAlarm( v, true );
	}
	
	private void cancelAlarm(View v )
	{
		//update the alarm in the database to off
		//cancel the alarm in the database currently
		if ( db != null )
		{
			Alarm alarm = db.getAlarm(alarmId);
            AlarmReceiver.cancelThisAlarm( me, alarm.hour, alarm.minute, (int) alarmId );
			alarm.withOn(0);
			db.updateAlarm( alarm );
		}
	}

	private void updateList(Alarm alarm )
	{
		Switch sw1 = (Switch ) findViewById(R.id.switch1);
		Switch vSwitch = (Switch ) findViewById(R.id.vibrateSwitch);
		TimePicker tpick = (TimePicker)findViewById(R.id.timePicker1);
		//EditText text = ( EditText ) findViewById(R.id.numQuestions);
		//text.setText( alarm.correct + "" );
		tpick.setCurrentHour(alarm.hour);
		tpick.setCurrentMinute(alarm.minute);
		vSwitch.setChecked( alarm.sound == -1 );
		sw1.setChecked(alarm.isOn == 1);
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
            updateList(db.getAlarm(alarmId));
		}
	}

}
