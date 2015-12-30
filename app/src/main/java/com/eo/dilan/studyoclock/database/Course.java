package com.eo.dilan.studyoclock.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//TODO
//course spinner in add question
//load up courses in add question
//associate course id with text in dataHelper
//add courses to spinner in subject activity, along with other options of no course/any course
//take into account the options selected when going into study session

//add course selector from subject activity to alarm activity and save that somehow in database
public class Course
{
    public static final String NAME = "Course";

    public String title;

    public long id;

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
        if ( this.getClass() != other.getClass() )
        {
            return false;
        }

        return ( this.id == ((Course) other ).id );
    }

    public ContentValues insertValues()
    {
        ContentValues values = new ContentValues();
        values.put("answer", this.title);
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

    public static List<Course> getAllCourses(SQLiteDatabase db)
    {
        List<Course> toReturn = new ArrayList<>();
        Cursor cursor = db.rawQuery( sqlSelectAll(), null);
        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                toReturn.add( fromCursor( cursor ) );
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
