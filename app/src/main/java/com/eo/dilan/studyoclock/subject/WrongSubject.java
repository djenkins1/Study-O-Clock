package com.eo.dilan.studyoclock.subject;

/*
Get questions that have been answered wrong a lot
--- ( wrong - right ) > some value
*/
public class WrongSubject extends StudySubject
{
    public String getSQL()
    {
        return  " AND (correct-wrong)<=0 ";
    }
}
