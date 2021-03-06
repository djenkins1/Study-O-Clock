package com.eo.dilan.studyoclock;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.AlertBuilder;
import com.eo.dilan.studyoclock.database.Answer;
import com.eo.dilan.studyoclock.database.Course;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Question;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionActivity extends AppCompatActivity
{
	private DataHelper db;

	private long qID;

	private final AddQuestionActivity me = this;

	private boolean populated = false;

	private ArrayList<EditText> edits;

	private ArrayList<CheckBox> checks;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_add_question);
		readViews();
		addClickToTexts();
		Intent intent = getIntent();
		qID = -1;
		findViewById(R.id.delQuestBtn).setEnabled(false);
		if ( intent != null && intent.getExtras() != null && intent.getExtras().getLong("question", -1) != -1 )
		{
			qID = intent.getExtras().getLong("question", -1);
		}

        new LongOperation().execute();
	}

	private void readViews()
	{
		edits = new ArrayList<>();
		edits.add( (EditText)findViewById(R.id.question) );
		edits.add( (EditText)findViewById(R.id.button1) );
		edits.add((EditText) findViewById(R.id.button2));
		edits.add((EditText) findViewById(R.id.button3));
		edits.add((EditText) findViewById(R.id.button4));

		checks = new ArrayList<>();
		checks.add((CheckBox) findViewById(R.id.chk1));
		checks.add(( CheckBox ) findViewById(R.id.chk2));
		checks.add((CheckBox) findViewById(R.id.chk3));
		checks.add((CheckBox) findViewById(R.id.chk4));
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if ( db != null )
		{
			db.clearQuestionsInMem();
		}
	}

	public void removeQuestion(View v )
	{
		if ( qID != -1 && db != null)
		{
			new AlertBuilder( "Remove Question" , "Do you really wish to remove this question?" ,this,new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					db.removeQuestion(db.getCurrentQuestion());
					Toast.makeText(getApplicationContext(), "Question removed!", Toast.LENGTH_LONG).show();
					Intent intent = new Intent( AddQuestionActivity.this , MainActivity.class );
					startActivity(intent);
				}
			} , null );

		}
	}

	private void addClickToTexts()
	{
		//make so that empty answers cannot be checked
		//make so that when answer is edited checkbox gets checked to false
		for ( int i = 1; i < edits.size(); i++ )
		{
			final CheckBox check = checks.get( i - 1 );
			final EditText edit = edits.get( i );
			check.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if ( edit.getText().toString().trim().equals( "" ) )
					{
						check.setChecked( false );
					}
				}
			});

			edit.addTextChangedListener( new TextWatcher()
			{
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
				{

				}

				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
				{

				}

				public void afterTextChanged(Editable editable)
				{
					check.setChecked( false );
				}
			});
		}
	}

	public void addQuestion(View v)
	{
		//get the text in each of the edit text fields then clear them
		ArrayList<EditText> texts = edits;
		ArrayList<CheckBox > boxes = checks;

		String qText = texts.get( 0 ).getText().toString().trim();
		Question question;
		if ( populated && qID != -1 )
		{
			question = db.getCurrentQuestion().withAnswersClear().withQuestion( qText );
		}
		else if ( !populated && qID == -1)
		{
			question = new Question( qText );
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_LONG).show();
			return;
		}

		for ( int i = 1; i < texts.size(); i++ )
		{
			boolean value = boxes.get( i - 1 ).isChecked();
			String text = texts.get( i ).getText().toString().trim();
			if ( !text.equals( "" ) )
			{
				question.withAnswer( new Answer( text, ( value ? 1 : 0 ) ) );
			}
		}

        Spinner spin = (Spinner)findViewById( R.id.courseSpin );
        int index = spin.getSelectedItemPosition() - 2;
        if ( index >= 0 ) {
            question.withCourse(db.courses.get(index));
        }
        else
        {
            question.withCourse( new Course() );
        }


		int error = question.isValidQuestion();
		switch( error )
		{
			case Question.QUESTION_FINE:
				break;
			case Question.QUESTION_EMPTY:
				Toast.makeText(getApplicationContext(), "Question field cannot be empty!", Toast.LENGTH_LONG).show();
				return;
			case Question.NOT_ENOUGH_ANSWERS:
				Toast.makeText(getApplicationContext(), "Must have at least 2 answers!", Toast.LENGTH_LONG).show();
				return;
			case Question.NOT_ENOUGH_CORRECT:
				Toast.makeText(getApplicationContext(), "Must have at least 1 correct answer!", Toast.LENGTH_LONG).show();
				return;
			default:
				Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
				return;
		}

		if ( qID == -1 )
		{
			Toast.makeText(getApplicationContext(), "Question added!", Toast.LENGTH_LONG).show();
			db.addQuestion(question);
			Intent intent = new Intent( this , AllQuestionsActivity.class );
			startActivity(intent);
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Question updated!", Toast.LENGTH_LONG).show();
			db.updateQuestion( question );
			Intent intent = new Intent( this , AllQuestionsActivity.class );
			startActivity(intent);
		}
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = DataHelper.instance(me.getApplicationContext() );
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
            if ( qID != -1 ) {
                db.setToQuestion(qID);
                updateList(db.getCurrentQuestion());
            }
            else
            {
                updateCourse(null);
            }
		}
	}

	private void updateList( Question question )
	{
		ArrayList<EditText> texts = new ArrayList<>();
		texts.add( (EditText)findViewById(R.id.question) );
		texts.add( (EditText)findViewById(R.id.button1) );
		texts.add( (EditText)findViewById(R.id.button2) );
		texts.add( (EditText)findViewById(R.id.button3) );
		texts.add( (EditText)findViewById(R.id.button4) );

		ArrayList<CheckBox > boxes = new ArrayList<>();
		boxes.add( (CheckBox)findViewById(R.id.chk1) );
		boxes.add( (CheckBox)findViewById(R.id.chk2) );
		boxes.add( (CheckBox)findViewById(R.id.chk3) );
		boxes.add((CheckBox) findViewById(R.id.chk4));
		texts.get(0).setText(question.question);
		for ( int i = 1; i < texts.size(); i++ )
		{
			if ( i > question.answers.size() )
			{
				texts.get( i ).setText("");
				boxes.get( i - 1 ).setChecked(false);
				break;
			}
			Answer answer = question.answers.get( i - 1 );
			texts.get( i ).setText( answer.answer );
			boxes.get( i - 1 ).setChecked(false);
			if ( answer.isCorrect != 0 )
			{
				boxes.get( i - 1 ).setChecked(true);
			}
		}

		Button button = (Button) findViewById(R.id.addQuestBtn);
		button.setText("Edit Question");
		findViewById(R.id.delQuestBtn).setEnabled(true);
        updateCourse(question.course);
        populated = true;

	}

    private void updateCourse( Course atMe )
    {
        Spinner spin = (Spinner)findViewById( R.id.courseSpin );
        List<String> courseList = new ArrayList<>();
        courseList.add("None");
        courseList.add( "New Course");
        int index = 0;
        int atPlace = courseList.size();
        for ( Course course: db.courses )
        {
            if ( course.equals( atMe ) )
            {
                index = atPlace;
            }
            courseList.add( course.title );
            atPlace++;
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>( this, R.layout.spinner_main, courseList);
        spin.setAdapter(spinnerArrayAdapter);
        spin.setSelection(index);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( position == 1 )
                {
                    popupAddCourse();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    private void popupAddCourse()
    {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this, R.style.AppCompatAlertDialogStyle );
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        alertDialogBuilder.setView( promptsView );
        alertDialogBuilder.setCancelable( false ).setPositiveButton("Create",
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog,int id)
                {
                    Course course = new Course().withID( -2 ).withTitle( userInput.getText().toString() );
                    db.addCourse( course );
                    updateCourse( course );
                }
            })
            .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button b = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        b.setBackgroundDrawable(getResources().getDrawable(R.drawable.apptheme_btn_default_holo_dark));
        b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        b.setBackgroundDrawable(getResources().getDrawable(R.drawable.apptheme_btn_default_holo_dark));
    }
}
