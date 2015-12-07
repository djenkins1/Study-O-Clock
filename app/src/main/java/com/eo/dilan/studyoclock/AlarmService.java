package com.eo.dilan.studyoclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.eo.dilan.studyoclock.database.Logger;

public class AlarmService extends Service
{

	public static boolean isAlarm = false;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
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
		if ( !isAlarm )
		{
			return;
		}
		Logger.print(this.getApplicationContext(), "Inside remove", "yes ");
		Context con = getApplicationContext();
		Intent mStartActivity = new Intent( con, MainActivity.class);
		mStartActivity.putExtra( "alarm" , "yes");
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(con, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = ( AlarmManager ) con.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);

	}
}
