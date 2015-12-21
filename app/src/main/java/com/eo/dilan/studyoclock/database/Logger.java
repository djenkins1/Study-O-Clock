package com.eo.dilan.studyoclock.database;


import android.content.Context;
import android.util.Log;

public class Logger
{
	public static final boolean DEBUGGABLE = false;
	public static void print( Context me, String tag, String value )
	{
		if ( DEBUGGABLE )
		{
			Log.d(tag, value);
		}
	}
}
