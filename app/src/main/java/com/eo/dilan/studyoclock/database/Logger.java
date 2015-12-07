package com.eo.dilan.studyoclock.database;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class Logger
{
	public static void print( Context me, String tag, String value )
	{
		boolean DEBUGGABLE = (me.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		if ( DEBUGGABLE )
		{
			Log.d(tag, value);
		}
	}
}
