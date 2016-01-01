package com.eo.dilan.studyoclock.subject;

import android.util.Log;

import com.eo.dilan.studyoclock.database.Course;

/*
(?)Get questions that have been answered right/wrong the same number of times
 */
public abstract class StudySubject
{
    public static StudySubject getSubject( String name )
    {
        return getSubject( Subject.getSubject( name ) );
    }

    public static StudySubject getCourseSubject( long value )
    {
        if ( value == -1 )
        {
            return new NoCourseSubject();
        }

        if ( value == 0 )
        {
            return new AnyCourseSubject();
        }

        return new CourseSubject().withCourse( value );
    }

    public static StudySubject getSubject( Subject subject )
    {
        switch( subject )
        {
            case WRONG:
                return new WrongSubject();
            case RIGHT:
                return new RightSubject();
            case COURSE:
                return new CourseSubject();
            case NEW:
                return new NewSubject();
            case NO_COURSE:
                return new NoCourseSubject();
            case SHY:
                return new ShySubject();
            case ANY_COURSE:
                return new AnyCourseSubject();
            case NONE:
                return null;
            default:
                Log.d( "Default" , "Reached default in subject switch with " + subject.name() );
                return null;
        }
    }

    public abstract String getSQL();
}
