package com.eo.dilan.studyoclock.subject;

import android.content.Context;

import com.eo.dilan.studyoclock.R;

public enum Subject
{
    COURSE(0), NEW(1), NO_COURSE(2), RIGHT(3), WRONG(4), ANY_COURSE(5), NONE(6), BAD(-1);

    public final int value;

    Subject ( int value )
    {
        this.value = value;
    }

    public static Subject getSubject( Context context, String name )
    {
        if ( name.equals( context.getString(R.string.extra_none)) )
        {
            return NONE;
        }

        if ( name.equals(context.getString(R.string.extra_not) ))
        {
            return NEW;
        }

        if ( name.equals(context.getString(R.string.extra_right)))
        {
            return RIGHT;
        }

        if (name.equals(context.getString( R.string.extra_wrong)))
        {
            return WRONG;
        }

        return BAD;
    }

    public static Subject getSubject( int value )
    {
        for ( Subject subject : values() )
        {
            if ( value == subject.value )
            {
                return subject;
            }
        }

        return BAD;
    }
}
