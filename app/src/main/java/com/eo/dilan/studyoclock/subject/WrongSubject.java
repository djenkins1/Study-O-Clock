package com.eo.dilan.studyoclock.subject;

import com.eo.dilan.studyoclock.database.Question;

/*
Get questions that have been answered wrong a lot
--- ( wrong - right ) > some value
*/
public class WrongSubject extends StudySubject
{
    public String getSQL()
    {
        StringBuilder toReturn = new StringBuilder();
        return toReturn.append( "SELECT * FROM " ).append( Question.NAME ).append( " WHERE (correct-wrong)<=0 ").toString();
    }
}
