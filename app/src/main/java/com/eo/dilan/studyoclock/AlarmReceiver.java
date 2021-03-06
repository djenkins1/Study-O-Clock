package com.eo.dilan.studyoclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.PreferenceKeys;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends WakefulBroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		WakeLocker.acquire(context);
		Intent appIntent = new Intent(context, MainActivity.class);
		appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle mBundle = new Bundle();
		mBundle.putString("alarm", "yes" );
		mBundle.putInt(PreferenceKeys.ALARM_KEY ,intent.getExtras().getInt( PreferenceKeys.ALARM_KEY , -1 ) );
		appIntent.putExtras(mBundle);
		context.startActivity( appIntent );
		WakeLocker.release();
	}

	public static void cancelThisAlarm( Activity me, int hour, int minute, int id )
	{
		Intent intent = new Intent(me, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(me, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		sender.cancel();
	}

	public static boolean addAlarm( Context me, int hour, int minute, int id , boolean rollTomorrow )
	{
        boolean toReturn = true;
		Logger.print(me.getApplicationContext(), "Entered: ", "addAlarm");
		AlarmManager am = (AlarmManager ) me.getSystemService(Context.ALARM_SERVICE);
		GregorianCalendar later = new GregorianCalendar();
		GregorianCalendar now = new GregorianCalendar();
		later.set(Calendar.HOUR_OF_DAY, hour);
		later.set(Calendar.MINUTE, minute);
		later.set(Calendar.SECOND, 0);
		if ( later.compareTo( now ) != 1 && rollTomorrow )
		{
			Logger.print(me.getApplicationContext(), "Set Alarm", "Setting alarm to tomorrow");
			later.add(Calendar.DATE , 1);
            toReturn = true;
		}
        else if ( later.compareTo( now ) == 1 && !rollTomorrow )
        {
            toReturn = false;
        }

		Intent intent = new Intent(me, AlarmReceiver.class);
		intent.putExtra( PreferenceKeys.ALARM_KEY , id );
		PendingIntent sender = PendingIntent.getBroadcast(me, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Logger.print(me.getApplicationContext(), "Time: ", later.getTimeInMillis() + "");
		Logger.print(me.getApplicationContext(), "Current: ", new GregorianCalendar().getTimeInMillis() + "");
		am.set(AlarmManager.RTC_WAKEUP, later.getTimeInMillis(), sender);
        return toReturn;
	}

    public static boolean addAlarm( Context me, int hour, int minute, int id )
    {
        return addAlarm( me, hour, minute, id , true );
    }
}
