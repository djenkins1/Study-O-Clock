package com.eo.dilan.studyoclock;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.eo.dilan.studyoclock.database.Alarm;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;

public class RestartService extends Service
{
	private DataHelper db;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Logger.print(this.getApplicationContext(), "Restart" , "Entered on start");
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
					alarm.setForTomorrow( this.getApplicationContext() , (int)(alarm.id) - 1 );
				}
			}
		}
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = new DataHelper( RestartService.this.getApplicationContext() );
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			handleAlarms();
		}
	}
}
