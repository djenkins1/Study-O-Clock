package com.eo.dilan.studyoclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

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
		appIntent.putExtras(mBundle);
		context.startActivity( appIntent );
		WakeLocker.release();
	}
}
