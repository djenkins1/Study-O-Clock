package com.eo.dilan.studyoclock.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eo.dilan.studyoclock.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Alarm
{
	public static final String NAME = "alarm";
	public static final String[] COLUMNS = { "id" , "hour", "minute", "correct", "ison" };
	public long id;
	public int hour;
	public int minute;
	public int correct;
	public int isOn;

	public Alarm()
	{
		this( 0, 0, 0);
	}

	public Alarm( int hour, int minute, int correct )
	{
		this.withHour( hour ).withMinute( minute ).withCorrect( correct ).withOn( 0 ).withID(-1);
	}

	public Alarm withHour( int hour )
	{
		this.hour = hour;
		return this;
	}

	public Alarm withMinute(int minute)
	{
		this.minute = minute;
		return this;
	}

	public Alarm withID( long id )
	{
		this.id = id;
		return this;
	}

	public Alarm withCorrect( int correct )
	{
		this.correct = correct;
		return this;
	}

	public Alarm withOn( int on )
	{
		this.isOn = on;
		return this;
	}

	public static String sqlCreate()
	{
		StringBuilder toReturn = new StringBuilder();
		toReturn.append( "CREATE TABLE " );
		toReturn.append( NAME );
		toReturn.append( "( " );
		toReturn.append( "id INTEGER PRIMARY KEY");
		toReturn.append( ",hour INTEGER");
		toReturn.append( ",minute INTEGER");
		toReturn.append( ",correct INTEGER");
		toReturn.append( ",ison INTEGER");
		toReturn.append( ")" );
		return toReturn.toString();
	}

	public ContentValues insertValues()
	{
		ContentValues values = new ContentValues();
		values.put("hour", this.hour);
		values.put("minute", this.minute);
		values.put("correct", this.correct);
		values.put("ison", this.isOn);
		return values;
	}
	public static String sqlSelectAll()
	{
		return "SELECT * FROM " + NAME;
	}

	public static ArrayList<Alarm> getAlarms( SQLiteDatabase db )
	{
		ArrayList<Alarm> toReturn = new ArrayList<>();
		Cursor cursor = db.rawQuery(sqlSelectAll(), null);
		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst())
		{
			do
			{
				Alarm alarm = new Alarm();
				alarm.withID( Long.parseLong( cursor.getString( 0 )));
				alarm.withHour(Integer.parseInt(cursor.getString(1)));
				alarm.withMinute(Integer.parseInt(cursor.getString(2)));
				alarm.withCorrect(Integer.parseInt(cursor.getString(3)));
				alarm.withOn(Integer.parseInt(cursor.getString(4)));
				toReturn.add( alarm );
			}
			while ( cursor.moveToNext() );
		}
		return toReturn;
	}

	public static Alarm debugAlarm()
	{
		GregorianCalendar now = new GregorianCalendar();
		return new Alarm().withHour( now.get(Calendar.HOUR) ).withMinute( now.get(Calendar.MINUTE) ).withCorrect( 1 ).withOn( 0 );
	}

	public void setForTomorrow( Activity from, int id  )
	{
		AlarmReceiver.addAlarm(from, this.hour, this.minute , id);
	}
}
