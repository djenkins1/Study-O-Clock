package com.eo.dilan.studyoclock.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Answer
{
	public static final String NAME = "answer";
	public static final String[] COLUMNS = { "id" , "question", "answer", "correct" };
	public long id;
	public long question;
	public String answer;
	public int isCorrect;

	public Answer( long id, long question, String answer, int isCorrect )
	{
		this.id = id;
		this.question = question;
		this.answer = answer;
		this.isCorrect = isCorrect;
	}

	public Answer( String answer, int isCorrect )
	{
		this.answer = answer;
		this.isCorrect = isCorrect;
	}

	public Answer withQuestion( long questionID )
	{
		this.question = questionID;
		return this;
	}

	public Answer withID( long id )
	{
		this.id = id;
		return this;
	}

	public static String sqlCreate()
	{
		StringBuilder toReturn = new StringBuilder();
		toReturn.append( "CREATE TABLE " );
		toReturn.append( NAME );
		toReturn.append( "( " );
		toReturn.append( "id INTEGER PRIMARY KEY");
		toReturn.append( ",question INTEGER");
		toReturn.append( ",answer TEXT ");
		toReturn.append( ",correct INTEGER");
		toReturn.append( ")" );
		return toReturn.toString();
	}

	public ContentValues insertValues()
	{
		ContentValues values = new ContentValues();
		values.put("answer", this.answer);
		values.put("correct", this.isCorrect);
		values.put("question", this.question);
		return values;
	}

	public static ArrayList<Answer> getAnswers( SQLiteDatabase db, long question )
	{
		ArrayList<Answer> toReturn = new ArrayList<Answer>();
		Cursor cursor = db.query( NAME , COLUMNS, "question=?" , new String[] {String.valueOf( question )}, null, null, null );
		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst())
		{
			do
			{
				toReturn.add( new Answer(
					Long.parseLong( cursor.getString( 0 ) )
					,Long.parseLong( cursor.getString( 1 ) )
					,cursor.getString( 2 )
					,Integer.parseInt( cursor.getString( 3 ) )
				) );
			}
			while ( cursor.moveToNext() );
		}
		return toReturn;
	}

	public static int numberCorrect( ArrayList<Answer> answers )
	{
		if ( answers == null )
		{
			return 0;
		}

		int toReturn = 0;
		for ( Answer answer : answers )
		{
			if ( answer.isCorrect != 0 )
				toReturn = toReturn + 1;
		}

		return toReturn;
	}
}
