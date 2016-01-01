package com.eo.dilan.studyoclock.subject;

import android.content.Context;
import android.content.res.Resources;

import com.eo.dilan.studyoclock.R;

public enum Subject
{
    COURSE, NEW, NO_COURSE, RIGHT, WRONG, ANY_COURSE, NONE, BAD;

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
}
