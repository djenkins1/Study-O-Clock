package com.eo.dilan.studyoclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eo.dilan.studyoclock.database.Logger;

public class RebootReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Logger.print( context, "Received" , "Reboot message");
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
		{
			Intent serviceIntent = new Intent( context, RestartService.class );
			context.startService(serviceIntent);
		}
	}
}
