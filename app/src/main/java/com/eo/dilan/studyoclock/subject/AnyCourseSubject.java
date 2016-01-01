package com.eo.dilan.studyoclock.subject;


import com.eo.dilan.studyoclock.database.Question;

public class AnyCourseSubject extends StudySubject
{
    public String getSQL()
    {
        return Question.sqlSelectAll();
    }
}
