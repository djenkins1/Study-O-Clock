package com.eo.dilan.studyoclock.subject;

import com.eo.dilan.studyoclock.database.Question;

/*
Get questions without a course
---all questions with course of invalid signifier( -1 or 0 )
 */
public class NoCourseSubject extends StudySubject
{
    public String getSQL()
    {
        StringBuilder toReturn = new StringBuilder();
        toReturn.append( "SELECT * FROM ");
        toReturn.append( Question.NAME );
        toReturn.append( " WHERE course=-1 ");
        return toReturn.toString();
    }
}
