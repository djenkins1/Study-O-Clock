package com.eo.dilan.studyoclock.subject;

import com.eo.dilan.studyoclock.R;

public enum Subject
{
    COURSE, NEW, NO_COURSE, RIGHT, WRONG, SHY, ANY_COURSE, NONE, BAD;

    public static Subject getSubject( String name )
    {
        if ( name.equals( R.string.extra_none ) )
        {
            return NONE;
        }

        if ( name.equals( R.string.extra_little ) )
        {
            return SHY;
        }

        if ( name.equals( R.string.extra_not ) )
        {
            return NEW;
        }

        if ( name.equals( R.string.extra_right))
        {
            return RIGHT;
        }

        if ( name.equals( R.string.extra_wrong))
        {
            return WRONG;
        }

        return BAD;
    }
}
