package com.eo.dilan.studyoclock.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eo.dilan.studyoclock.subject.StudySubject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class DataHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_NAME = "study";

	public List< Question > allQuestions;

	public List< Alarm > alarms;

	public List< Course > courses;

	private int atQuestion = 0;

	private SQLiteDatabase db;

	private static DataHelper singleton;

	public static boolean reloadAlarms = false;

	public synchronized static DataHelper instance(Context context)
	{
		if ( singleton == null )
		{
			singleton = new DataHelper(context);
		}
		else if ( reloadAlarms )
		{
			Log.d( "RELOAD" , "ALARM");
			reloadAlarms = false;
			singleton.alarms = Alarm.getAlarms( singleton.db );
		}
		return singleton;
	}

	public void closeMe()
	{
		singleton = null;
		alarms = null;
		allQuestions = null;
		this.close();
	}

	private DataHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = this.getWritableDatabase();
		allQuestions = new Vector<>();
		alarms = Alarm.getAlarms(db);
		courses = Course.getAllCourses(db);
	}

	public Alarm getAlarm(long id)
	{
		for ( Alarm alarm : alarms )
		{
			if ( alarm.id == id )
			{
				return alarm;
			}
		}
		return Alarm.debugAlarm();
	}

	public Course getCourse(Course lookFor)
	{
		if ( lookFor.id == -1 )
		{
			return new Course().withID(lookFor.id).withTitle("None");
		} else if ( lookFor.id == 0 )
		{
			return new Course().withID(lookFor.id).withTitle("Any");
		}

		for ( Course course : courses )
		{
			if ( lookFor.equals(course) )
			{
				return course;
			}
		}
		return new Course().withID(lookFor.id).withTitle("BAD");
	}

	public ArrayList< Question > getAllQuestions()
	{
		return Question.getAllQuestions(db);
	}

	public void saveQuestion(Question question)
	{
		updateQuestionNotAnswers(db, question);
	}

	private void createTables(SQLiteDatabase db)
	{
		db.execSQL(Question.sqlCreate());
		db.execSQL(Answer.sqlCreate());
		db.execSQL(Alarm.sqlCreate());
		db.execSQL(Course.sqlCreate());
	}

	public void onCreate(SQLiteDatabase db)
	{
		createTables(db);
		for ( Question quest : Question.getDebugs() )
		{
			addQuestion(db, quest);
		}

		for ( Course course : Course.getDebugList() )
		{
			if ( course != null )
			{
				addCourse(db, course);
			}
		}

		addAlarm(db, Alarm.debugAlarm());

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		createTables(db);
		if ( newVersion == 2 )
		{
			db.execSQL(Question.alterSQL());
		} else
		{
			// Drop older table if existed
			db.execSQL("DROP TABLE IF EXISTS " + Question.NAME);
			db.execSQL("DROP TABLE IF EXISTS " + Answer.NAME);
			db.execSQL("DROP TABLE IF EXISTS " + Alarm.NAME);
			db.execSQL("DROP TABLE IF EXISTS " + Course.NAME);
			// Create tables again
			onCreate(db);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + Question.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Answer.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Alarm.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Course.NAME);
		// Create tables again
		onCreate(db);
	}

	public Question getCurrentQuestionLoader(Cursor cursor, int limit)
	{
		if ( allQuestions == null )
		{
			return null;
		}

		if ( atQuestion >= allQuestions.size() )
		{
			loadQuestions(cursor, limit);
			if ( atQuestion >= allQuestions.size() )
			{
				atQuestion = 0;
				Collections.shuffle(allQuestions);
			}
		}

		if ( allQuestions.size() == 0 )
		{
			return null;
		}
		return allQuestions.get(atQuestion);
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
			Collections.shuffle(allQuestions);
		}

		if ( allQuestions.size() == 0 )
		{
			return null;
		}
		return allQuestions.get(atQuestion);
	}

	public void incrementQuestion()
	{
		atQuestion = atQuestion + 1;
	}

	public void addQuestion(SQLiteDatabase db, Question question)
	{
		question.withID(db.insert(Question.NAME, null, question.insertValues()));
		addAnswers(db, question);
	}

	public void loadTitleForCourse(Course courseWithID)
	{
		int index = courses.indexOf(courseWithID);
		if ( index != -1 )
		{
			courseWithID.withTitle(courses.get(index).title);
		}
	}

	public void addCourse(SQLiteDatabase db, Course course)
	{
		course.withID(db.insert(Course.NAME, null, course.insertValues()));
	}

	public void addCourse(Course course)
	{
		addCourse(this.db, course);
		courses.add(course);
	}

	public void addQuestion(Question question)
	{
		addQuestion(db, question);
	}

	public void addAnswers(SQLiteDatabase db, Question question)
	{
		for ( Answer answer : question.answers )
		{
			addAnswer(db, answer);
		}
	}

	public void addAlarm(SQLiteDatabase db, Alarm alarm)
	{
		alarm.withID(db.insert(Alarm.NAME, null, alarm.insertValues()));
	}

	public void addAlarm(Alarm alarm)
	{
		addAlarm( this.db, alarm );
		if ( alarms != null )
		{
			alarms.add( alarm );
			reloadAlarms = true;
		}
		else
		{
			Log.d( "Error" , "Trying to add alarm with alarms array null");
		}
	}

	public void addAnswer( SQLiteDatabase db,Answer answer)
	{
		answer.withID(db.insert(Answer.NAME, null, answer.insertValues()));
	}

	public void updateQuestion( Question question )
	{
		removeAnswers(question.id);
		updateQuestionNotAnswers( db , question );
		addAnswers(db, question);
	}

	public void updateQuestionNotAnswers( SQLiteDatabase db, Question question )
	{
		if ( question != null )
		{
			db.update(Question.NAME, question.insertValues(), "id = ?", new String[]{ String.valueOf(question.id) });
		}
	}

	public void removeQuestion( Question question )
	{
		removeAnswers(db, question.id);
		db.execSQL(question.deleteStatement());
	}

	public void removeAllQuestions()
	{
		clearQuestionsInMem();
		if ( checkOpen() )
		{
			removeAllAnswers();
			db.execSQL(Question.deleteAllStatement());
		}
	}

	private boolean checkOpen()
	{
		return ( db != null && db.isOpen() );
	}

	public boolean areThereQuestions()
	{
		if ( checkOpen() )
		{
			Cursor cursor = db.rawQuery("SELECT 1 FROM " + Question.NAME + " LIMIT 1", new String[]{});
			boolean toReturn = ( cursor != null && cursor.getCount() == 1 );
			if ( cursor != null )
			{
				cursor.close();
			}
			return toReturn;
		}
		return false;
	}

	private void removeAllAnswers()
	{
		if ( checkOpen() )
		{
			db.execSQL(Answer.deleteAllStatement());
		}
	}

	public void updateAlarm( Alarm alarm )
	{
		if ( checkOpen() )
		{
			db.update(Alarm.NAME, alarm.insertValues(), "id = ?", new String[]{String.valueOf(alarm.id)});
			reloadAlarms = true;
		}
	}

	private void removeAnswers( SQLiteDatabase db, long qID )
	{
		if ( checkOpen() )
		{
			String sql = "DELETE FROM " + Answer.NAME + " WHERE question=" + qID;
			db.execSQL(sql);
		}
	}

	public void clearQuestionsInMem()
	{
		allQuestions.clear();
	}

	private void removeAnswers( long question )
	{
		if ( checkOpen() )
		{
			removeAnswers(db, question);
		}
	}

	public void setToQuestion( long qID )
	{
		setToQuestion( qID, true );
	}

	public void setToQuestion( long qID, boolean loadAnswers )
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
			Question question = Question.getQuestion(db, qID, loadAnswers);
			if ( question != null )
			{
				allQuestions.add(question);
				atQuestion = allQuestions.size() - 1;
			}
		}
	}

	public Cursor loadQuestions( long qID, int limit )
	{
		Cursor cursor = getCursorOfQuestionsNotID( qID );
        if ( cursor != null )
        {
            cursor.moveToFirst();
        }
		return loadFromCursor( cursor, limit );
	}

	public Cursor loadQuestions( Cursor cursor, int limit )
	{
        if ( cursor != null )
        {
            cursor.moveToFirst();
        }
		return loadFromCursor( cursor, limit );
	}

	private Cursor loadFromCursor( Cursor cursor, int limit )
	{
		int count = 0;
		if ( cursor != null )
		{
			do
			{
				if ( count >= limit )
				{
					break;
				}

				if ( cursor.isAfterLast() )
				{
					break;
				}

				count++;
				Question question = Question.fromCursor(cursor);
				if ( question != null )
				{
					question.withAnswers(Answer.getAnswers(db, question.id));
					allQuestions.add(question);
				}
			}
			while ( cursor.moveToNext() );
		}

		return cursor;
	}

	public Cursor getCursorOfQuestionsNotID( long qID )
	{
		return db.rawQuery( Question.sqlSelectNotID( qID ) , new String[]{} );
	}

    public Cursor getCursorOfQuestionSubjects( StudySubject courseSubject, StudySubject extraSubject )
    {
        StringBuilder query = new StringBuilder( courseSubject.getSQL() );
        if ( extraSubject != null )
        {
            query.append( extraSubject.getSQL() );
        }
        Log.d("QUERY", query.toString());
        return db.rawQuery( query.toString(), new String[]{} );
    }

	public void resetQuestionStats()
	{
		if ( allQuestions == null )
		{
			return;
		}

		if ( checkOpen() )
		{
			db.execSQL(Question.resetStatSQL());
		}
		clearQuestionsInMem();
	}
}
