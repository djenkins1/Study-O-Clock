package com.eo.dilan.studyoclock.subject;

import com.eo.dilan.studyoclock.database.Question;

/*
Get questions that have been answered right most of the time
--- ( wrong - right ) < some value
*/
public class RightSubject extends StudySubject
{
    public String getSQL()
    {
        return ( " AND (correct-wrong)>0 ");
    }
}
