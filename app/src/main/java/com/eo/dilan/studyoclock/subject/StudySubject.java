package com.eo.dilan.studyoclock.subject;

import android.util.Log;

/*
(?)Get questions that have been answered right/wrong the same number of times
 */
public abstract class StudySubject
{
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
