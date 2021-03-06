package com.eo.dilan.studyoclock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class Question
{
	public static final String NAME = "question";
	public long id;
	public String question;
	public ArrayList<Answer> answers;
	public int correct;
	public int wrong;
    public Course course;

	public static final int QUESTION_FINE = 9;
	public static final int QUESTION_EMPTY = 10;
	public static final int NOT_ENOUGH_ANSWERS = 11;
	public static final int NOT_ENOUGH_CORRECT = 12;

	public Question( long id, String question, ArrayList<Answer> answers )
	{
		this.id = id;
		this.question = question;
		this.answers = new ArrayList<>();
		this.withAnswers( answers );
        this.course = new Course().withID( -1 );
	}

	public Question withCorrect( int i)
	{
		this.correct = i;
		return this;
	}

	public Question withWrong( int i)
	{
		this.wrong = i;
		return this;
	}

	public static String deleteAllStatement()
	{
		return "DELETE FROM " + NAME;
	}

	public Question incrementAmount( Context c, boolean isCorrect )
	{
		if ( isCorrect )
		{
			this.correct++;
			Logger.print( c, "Incremented correct", "question " + this.id + " now has " + this.correct );
			return this;
		}

		this.wrong++;
		Logger.print(c, "Incremented wrong", "question " + this.id + " now has " + this.wrong);
		return this;
	}

	public Question( String question )
	{
		this(0, question, null);
	}

	public Question withAnswer( Answer answer )
	{
		this.answers.add(answer.withQuestion(this.id));
		return this;
	}

	public Question withAnswers( ArrayList<Answer> answers )
	{
		if ( answers == null )
		{
			return this;
		}

		for ( Answer answer : answers )
		{
			this.withAnswer( answer );
		}
		return this;
	}

	public Question withQuestion(String question )
	{
		this.question = question;
		return this;
	}

	public Question withID( long id )
	{
		this.id = id;
		for( Answer answer : answers )
		{
			answer.withQuestion(id);
		}
		return this;
	}

    public Question withCourse( Course course )
    {
        this.course = course;
        return this;
    }

	public Question withAnswersClear()
	{
		this.answers.clear();
		return this;
	}

    public static String alterSQL()
    {
        return "ALTER TABLE " + Question.NAME + " ADD course INTEGER DEFAULT(-1)";
    }

	public static String sqlCreate()
	{
		StringBuilder toReturn = new StringBuilder();
		toReturn.append( "CREATE TABLE IF NOT EXISTS " );
		toReturn.append( NAME );
		toReturn.append( "( " );
		toReturn.append( "id INTEGER PRIMARY KEY");
		toReturn.append(",question TEXT ");
		toReturn.append(",correct INTEGER");
		toReturn.append(",wrong INTEGER");
        toReturn.append(",course INTEGER");
		toReturn.append(")");
		return toReturn.toString();
	}

	public ContentValues insertValues()
	{
		ContentValues values = new ContentValues();
		values.put("question", this.question);
		values.put("correct", this.correct);
		values.put("wrong", this.wrong);
        values.put("course", this.course.id);
		return values;
	}

	public static String sqlSelectAll()
	{
		return "SELECT * FROM " + NAME;
	}

	public static String sqlSelectNotID( long qID )
	{
		return "SELECT * FROM " + NAME + " WHERE id !=" + qID + " ORDER BY RANDOM()";
	}

	public static Question getQuestion( SQLiteDatabase db, long id, boolean loadAnswers )
	{
		String stmt = "SELECT * FROM " + NAME + " WHERE id=" + id;
		Cursor cursor = db.rawQuery( stmt , new String[] {} );
		if (cursor.getCount() <= 0)
		{
			return null;
		}

		cursor.moveToFirst();
		Question question = fromCursor( cursor );
		if ( question != null && loadAnswers )
		{
			question.withAnswers(Answer.getAnswers(db, question.id));
		}
		return question;
	}

	public static ArrayList<Question> getAllQuestions(SQLiteDatabase db)
	{
		ArrayList<Question> toReturn = new ArrayList<>();
		Cursor cursor = db.rawQuery(sqlSelectAll(), null);
		if (cursor != null && cursor.moveToFirst())
		{
			do
			{
				Question question = fromCursor( cursor );
				toReturn.add(question);
			}
			while ( cursor.moveToNext() );
		}
		return toReturn;
	}

	public static Question fromCursor( Cursor cursor )
	{
		Question question = new Question( cursor.getString( 1 ));
		question.withID(Long.parseLong(cursor.getString( 0 )));
		question.withCorrect(Integer.parseInt(cursor.getString(2)));
		question.withWrong(Integer.parseInt(cursor.getString(3)));
        question.withCourse( new Course().withID( Long.parseLong( cursor.getString(4) ) ));
		return question;
	}

    public static String getSqlForCourse( long courseID )
    {
        StringBuilder toReturn = new StringBuilder();
        if ( courseID != -1 )
        {
            toReturn.append( "SELECT * FROM " );
            toReturn.append( Question.NAME );
            toReturn.append( " WHERE course=" );
            toReturn.append( courseID );
        }
        else
        {
            Log.d("BAD COURSE", courseID + "");
        }
        return toReturn.toString();
    }

	public static ArrayList<Question> getDebugs()
	{
		ArrayList<Question> toReturn = new ArrayList<Question>();
		/*
		Question quest = new Question( "I think I found myself a ___");
		quest.withAnswer( new Answer( "virus" , 0 ) );
		quest.withAnswer( new Answer( "train" , 0 ) );
		quest.withAnswer(new Answer("cheerleader", 1));
		quest.withAnswer( new Answer( "weeknd" , 0 ) );
		toReturn.add(quest);
		quest = new Question( "On the other side of ___");
		quest.withAnswer( new Answer( "a street I knew" , 1 ) );
		quest.withAnswer(new Answer("my streetcar", 0));
		toReturn.add(quest);
		*/

		for ( int i = 0; i < 10; i++ )
		{
			Question quest = new Question( "What is 5 + " + i);
			quest.withAnswer( new Answer( i + "" , 0 ));
			quest.withAnswer( new Answer( ( i + 2 ) + "" , 0 ));
			quest.withAnswer( new Answer( ( i * 3 ) + "" , 0 ));
			quest.withAnswer( new Answer( ( i + 5) + "" , 1 ));
			if ( quest.isValidQuestion() == QUESTION_FINE )
			{
				toReturn.add(quest);
			}
		}

		return toReturn;
	}

	public int isValidQuestion()
	{
		if ( question == null || question.equals( "" ))
			return QUESTION_EMPTY;
		if ( answers == null || answers.size() < 2 )
			return NOT_ENOUGH_ANSWERS;
		if ( Answer.numberCorrect(answers) < 1 )
			return NOT_ENOUGH_CORRECT;

		return QUESTION_FINE;
	}

	public String deleteStatement()
	{
		return "DELETE FROM " + NAME + " WHERE id=" + this.id;
	}

	public static String resetStatSQL()
	{
		return "UPDATE " + NAME + " SET correct=0,wrong=0";
	}
}

