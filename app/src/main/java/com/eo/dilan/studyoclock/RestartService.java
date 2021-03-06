package com.eo.dilan.studyoclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.eo.dilan.studyoclock.database.Alarm;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.PreferenceKeys;

public class RestartService extends Service
{
	private DataHelper db;

	private SharedPreferences shared;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		shared = getSharedPreferences(PreferenceKeys.PREF_KEY, Context.MODE_PRIVATE);
		new LongOperation().execute();
		return START_NOT_STICKY;
	}

	private void handleAlarms()
	{
		if ( db.alarms != null )
		{
			for ( Alarm alarm : db.alarms )
			{
				if ( alarm.isOn == 1 )
				{
                    if ( shared.getBoolean( PreferenceKeys.ALARM_REPEAT, true ) )
                    {
                        alarm.setForTomorrow(this.getApplicationContext(), (int) (alarm.id) - 1);
                    }
                    else
                    {
                        boolean val = alarm.setForToday(this.getApplicationContext(), (int) (alarm.id) - 1);
                        if ( !val )
                        {
                            db.updateAlarm( alarm.withOn( 0 ) );
                        }
                    }
				}
			}
		}
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = DataHelper.instance(RestartService.this.getApplicationContext() );
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			handleAlarms();
			if ( shared.getBoolean( PreferenceKeys.ALARMING , false ) )
			{
				Context con = getApplicationContext();
				Intent mStartActivity = new Intent( con, MainActivity.class);
				mStartActivity.putExtra( "alarm" , "yes");
				int value = shared.getInt(PreferenceKeys.ALARM_KEY, -1);
				mStartActivity.putExtra( PreferenceKeys.ALARM_KEY , value);
				int mPendingIntentId = -1;
				PendingIntent mPendingIntent = PendingIntent.getActivity(con, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager mgr = ( AlarmManager ) con.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000, mPendingIntent);
			}
			RestartService.this.stopSelf();
		}
	}
}
