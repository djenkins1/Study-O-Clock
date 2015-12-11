package com.eo.dilan.studyoclock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

public class DataHelper extends SQLiteOpenHelper
{
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 5;

	// Database Name
	private static final String DATABASE_NAME = "study";

	public ArrayList<Question> allQuestions;

	public ArrayList<Alarm> alarms;

	private int atQuestion = 0;

	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		SQLiteDatabase db = this.getWritableDatabase();
		allQuestions = Question.getAllQuestions(db);
		alarms = Alarm.getAlarms(db);
		Collections.shuffle( allQuestions );
		//db.close();
	}

	public void saveAllQuestions()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		for ( Question question : allQuestions )
		{
			updateQuestionNotAnswers( db, question );
		}
	}

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(Question.sqlCreate());
		db.execSQL(Answer.sqlCreate());
		db.execSQL(Alarm.sqlCreate());
		for ( Question quest : Question.getDebugs() )
		{
			addQuestion(db, quest);
		}

		addAlarm(db, Alarm.debugAlarm());

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + Question.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Answer.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Alarm.NAME );
		// Create tables again
		onCreate(db);
	}

	public Question getCurrentQuestion()
	{
		if ( allQuestions == null )
		{
			return null;
		}

		if ( atQuestion >= allQuestions.size() )
		{
			atQuestion = 0;
			Collections.shuffle( allQuestions );
		}

		if ( allQuestions.size() == 0 )
		{
			return null;
		}
		return allQuestions.get( atQuestion );
	}

	public void incrementQuestion()
	{
		atQuestion = atQuestion + 1;
	}

	public void addQuestion( SQLiteDatabase db, Question question )
	{
		question.withID(db.insert(Question.NAME, null, question.insertValues()));
		addAnswers(db, question);
	}

	public void addQuestion( Question question )
	{
		SQLiteDatabase db = this.getWritableDatabase();
		question.withID(db.insert(Question.NAME, null, question.insertValues()));
		addAnswers(db, question);
		//db.close();
	}

	public void addAnswers(SQLiteDatabase db,Question question )
	{
		for ( Answer answer : question.answers )
		{
			addAnswer(db, answer);
		}
	}

	public void addAlarm( SQLiteDatabase db , Alarm alarm )
	{
		alarm.withID(db.insert(Alarm.NAME, null, alarm.insertValues()));
	}

	public void addAnswer( SQLiteDatabase db,Answer answer)
	{
		answer.withID(db.insert(Answer.NAME, null, answer.insertValues()));
	}

	public void updateQuestion( Question question )
	{
		//Log.d("Entered update" , "updating " + question.question );
		SQLiteDatabase db = this.getWritableDatabase();
		removeAnswers(question.id);
		db.update(Question.NAME, question.insertValues(), "id = ?", new String[]{ String.valueOf(question.id) });
		addAnswers(db, question);
		//db.close();
	}

	public void updateQuestionNotAnswers( SQLiteDatabase db, Question question )
	{

		db.update(Question.NAME, question.insertValues(), "id = ?", new String[]{ String.valueOf(question.id) });
	}

	public void removeQuestion( Question question )
	{
		SQLiteDatabase db = this.getWritableDatabase();
		removeAnswers(db, question.id);
		db.execSQL(question.deleteStatement());
	}

	public void updateAlarm( Alarm alarm )
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.update(Alarm.NAME, alarm.insertValues(), "id = ?", new String[]{ String.valueOf(alarm.id) });
	}

	private void removeAnswers( SQLiteDatabase db, long qID )
	{
		String sql = "DELETE FROM " + Answer.NAME + " WHERE question=" + qID;
		db.execSQL(sql);
	}

	private void removeAnswers( long question )
	{
		removeAnswers(this.getWritableDatabase(), question);
	}

	public void setToQuestion( long qID )
	{
		atQuestion = -1;
		for ( int i = 0; i < allQuestions.size(); i++ )
		{
			if ( allQuestions.get(i).id == qID )
			{
				atQuestion = i;
				break;
			}
		}

		if ( atQuestion == -1 )
		{
			//Log.d("BAD QUESTION" , qID + "");
			//Log.d( "Null" , allQuestions.get( - 1 ).question );
		}
	}

	public void closeMe()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.close();
	}

	public void resetQuestionStats()
	{
		if ( allQuestions == null )
		{
			return;
		}

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL( Question.resetStatSQL() );
		for ( Question question : allQuestions )
		{
			question.correct = 0;
			question.wrong = 0;
		}
	}
}
