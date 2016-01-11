package com.eo.dilan.studyoclock.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//TODO
//(!!!)write the sql for all the subjects
//---works for (any course and none extra)
//---need to test for others
//
//TODO: ADD ALARM
//go to the subject activity for setting the alarm, save the alarm values in preferences until the user adds the alarm
//replace the number of questions box with a toggle box for vibrate/sound in alarm activity
//start integrating the code for the alarm activity to use a id value passed in for alarm to edit/add alarm if not passed
//
//TODO: PASS TO STUDY
//use the course and extra values in the database for the alarm going off
//
//TODO: ALL ALARMS
//need to put in course title for each alarm in the all questions activity for alarms
//need to pass along id of the alarm chosen in the all questions activity for alarms
//need to take into account the id passed in the intent if passed for add alarm activity
//alarm should show 12 for midnight when hour is 0 on all alarms getTime



public class Course
{
    public static final String NAME = "Course";

    public String title;

    public long id;

    public Course()
    {
        this.id = -1;
        this.title = "";
    }

    public Course withTitle( String title )
    {
        this.title = title;
        return this;
    }

    public static String sqlSelectAll()
    {
        return "SELECT * FROM " + NAME + " ORDER BY title ASC";
    }

    public Course withID( long id )
    {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals( Object other )
    {
        if ( other == null )
        {
            return false;
        }

        if ( this.getClass() != other.getClass() )
        {
            return false;
        }

        return ( this.id == ((Course) other ).id );
    }

    public ContentValues insertValues()
    {
        ContentValues values = new ContentValues();
        values.put("title", this.title);
        return values;
    }

    public static String sqlCreate()
    {
        StringBuilder toReturn = new StringBuilder();
        toReturn.append( "CREATE TABLE IF NOT EXISTS " );
        toReturn.append( NAME );
        toReturn.append( "( " );
        toReturn.append( "id INTEGER PRIMARY KEY");
        toReturn.append( ",title TEXT");
        toReturn.append(")");
        return toReturn.toString();
    }

    public static List<Course> getDebugList()
    {
        List<Course> toReturn = new ArrayList<>();
        toReturn.add( new Course().withTitle( "Computer Science"));
        toReturn.add( new Course().withTitle( "Art History"));
        return toReturn;
    }

    public static List<Course> getAllCourses(SQLiteDatabase db)
    {
        List<Course> toReturn = new ArrayList<>();
        Cursor cursor = db.rawQuery( sqlSelectAll(), null);
        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                toReturn.add(fromCursor(cursor));
                Log.d( "Data", toReturn.get(toReturn.size() - 1).title);
            }
            while ( cursor.moveToNext() );
        }
        return toReturn;
    }

    public static Course fromCursor( Cursor cursor )
    {
        Course course = new Course();
        course.withID(Long.parseLong(cursor.getString(0)));
        course.withTitle(cursor.getString(1) );
        return course;
    }
}
