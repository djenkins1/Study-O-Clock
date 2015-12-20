package com.eo.dilan.studyoclock.database;

public class PreferenceKeys
{
	public static final String ALARM_REPEAT = "repeatAlarm";

	public static final String PREF_KEY = "STUDY_PREFS";

	public static final String ALARMING = "alarming";

	public static final String QUESTION = "onQuestion";

	public static final String TOTAL = "remaining";

	public static final String Q_LIST = "listOfQuestions";


	public static StringBuilder addNumberToEnd( StringBuilder str, long value )
	{
		if ( str.length() > 0 )
		{
			str.append( "," );
		}
		str.append( value );
		return str;
	}

}
