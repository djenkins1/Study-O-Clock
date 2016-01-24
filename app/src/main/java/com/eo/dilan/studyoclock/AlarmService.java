package com.eo.dilan.studyoclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.PreferenceKeys;

public class AlarmService extends Service
{
	private SharedPreferences shared;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		shared = getSharedPreferences(PreferenceKeys.PREF_KEY, Context.MODE_PRIVATE);
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onTaskRemoved(Intent rootIntent)
	{
		super.onTaskRemoved(rootIntent);
		boolean isAlarm = getSharedPreferences(PreferenceKeys.PREF_KEY, Context.MODE_PRIVATE).getBoolean( PreferenceKeys.ALARMING , false );
		if ( !isAlarm )
		{
			DataHelper.instance(this.getApplicationContext() ).closeMe();
			return;
		}

		Context con = getApplicationContext();
		Intent mStartActivity = new Intent( con, MainActivity.class);
		mStartActivity.putExtra( "alarm" , "yes");
		int value = shared.getInt(PreferenceKeys.ALARM_KEY, -1);
		mStartActivity.putExtra( PreferenceKeys.ALARM_KEY , value);
		int mPendingIntentId = -1;
		PendingIntent mPendingIntent = PendingIntent.getActivity(con, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = ( AlarmManager ) con.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
	}
}
