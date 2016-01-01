package com.eo.dilan.studyoclock.subject;

/*
Get questions that haven't been answered before
--- zero right/wrong stats
 */
public class NewSubject extends StudySubject
{
    public String getSQL()
    {
        return " AND correct=0 AND wrong=0";
    }
}
