package com.eo.dilan.studyoclock.subject;

import com.eo.dilan.studyoclock.database.Course;

/*
Get questions from a specific course
---all questions with course of id provided
 */
public class CourseSubject extends StudySubject
{
    private long course;

    public CourseSubject()
    {
        this.course = -1;
    }

    public String getSQL()
    {
        //TODO: use course provided to get sql needed
        return null;
    }

    public CourseSubject withCourse( long value )
    {
        this.course = value;
        return this;
    }
}
