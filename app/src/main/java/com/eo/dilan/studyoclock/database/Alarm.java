package com.eo.dilan.studyoclock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eo.dilan.studyoclock.AlarmReceiver;
import com.eo.dilan.studyoclock.subject.Subject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Alarm
{
	public static final String NAME = "alarm";
	public long id;
	public int hour;
	public int minute;
	public int correct;
	public int isOn;
    public int course;
    public int extra;
    public int sound;

	public Alarm()
	{
		this( 0, 0, 5);
	}

	public Alarm( int hour, int minute, int correct )
	{
		this.withHour( hour ).withMinute( minute ).withCorrect( correct ).withOn( 0 ).withID(-1).withExtra( Subject.NONE.value ).withCourse( 0 ).withSound(-1);
	}

    public Alarm withCourse( int course )
    {
        this.course = course;
        return this;
    }

    public Alarm withSound( int sound )
    {
        this.sound = sound;
        return this;
    }

    public Alarm withExtra( int extra )
    {
        this.extra = extra;
        return this;
    }

	public Alarm withSound( boolean isOn )
	{
		return withSound(( isOn ? 0 : -1 ) );
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

	public Alarm withOn( boolean on )
	{
		return withOn( ( on ? 1 : 0 ) );
	}

    public String getTime()
    {
        StringBuilder toReturn = new StringBuilder();
        int time = ( isNight( hour ) && hour != 12 ? hour - 12 : hour );
        time = ( time == 0 ? 12 : time );
        toReturn.append( ( time < 10 ? "0" : "" ) );
        toReturn.append( time );
        toReturn.append( ":");
        toReturn.append( ( minute < 10 ? "0" : "" ) );
        toReturn.append( minute );
        toReturn.append( " " );
        toReturn.append( ( isNight( hour ) ? "PM" : "AM" ) );
        return toReturn.toString();
    }

    private boolean isNight( int time )
    {
        return ( time >= 12 );
    }

	public static String sqlCreate()
	{
		StringBuilder toReturn = new StringBuilder();
		toReturn.append( "CREATE TABLE IF NOT EXISTS " );
		toReturn.append( NAME );
		toReturn.append( "( " );
		toReturn.append( "id INTEGER PRIMARY KEY");
		toReturn.append( ",hour INTEGER");
		toReturn.append( ",minute INTEGER");
		toReturn.append( ",correct INTEGER");
		toReturn.append( ",ison INTEGER");
        toReturn.append( ",course INTEGER");
        toReturn.append( ",extra INTEGER");
        toReturn.append( ",sound INTEGER");
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
        values.put( "course" , this.course );
        values.put( "extra" , this.extra );
        values.put( "sound" , this.sound );
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
		if (cursor != null && cursor.moveToFirst())
		{
			do
			{
				Alarm alarm = new Alarm();
                int index = 0;
				alarm.withID( Long.parseLong( cursor.getString( index++ )));
				alarm.withHour(Integer.parseInt(cursor.getString(index++)));
				alarm.withMinute(Integer.parseInt(cursor.getString(index++)));
				alarm.withCorrect(Integer.parseInt(cursor.getString(index++)));
				alarm.withOn(Integer.parseInt(cursor.getString(index++)));
                alarm.withCourse(Integer.parseInt(cursor.getString(index++)));
                alarm.withExtra(Integer.parseInt(cursor.getString(index++)));
                alarm.withSound(Integer.parseInt(cursor.getString(index++)));
				toReturn.add( alarm );
			}
			while ( cursor.moveToNext() );
			cursor.close();
		}
		return toReturn;
	}

	public static Alarm debugAlarm()
	{
		GregorianCalendar now = new GregorianCalendar();
		return new Alarm().withHour( now.get(Calendar.HOUR_OF_DAY) ).withMinute( now.get(Calendar.MINUTE) ).withCorrect( 5 ).withOn( 0 ).withSound( -1 );
	}

	public boolean setForTomorrow( Context from, int id  )
	{
		return AlarmReceiver.addAlarm(from, this.hour, this.minute , id);
	}

    public boolean setForToday( Context from, int id )
    {
        return ( !AlarmReceiver.addAlarm(from, this.hour, this.minute , id, false) );
    }
}
