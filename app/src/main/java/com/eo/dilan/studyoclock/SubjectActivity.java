package com.eo.dilan.studyoclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.Alarm;
import com.eo.dilan.studyoclock.database.Course;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.PreferenceKeys;
import com.eo.dilan.studyoclock.subject.Subject;

import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity
{
    private DataHelper db;

    private boolean isLoaded = false;

    private long alarmId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_subject);
        Intent intent = getIntent();
        if ( intent != null && intent.getExtras() != null  )
        {
            alarmId = intent.getExtras().getLong("alarm", -1);
            Log.d("Excelsior", alarmId + "");
            ((Button )findViewById( R.id.studyBtn)).setText("Save Alarm");
        }
        new LongOperation().execute();
    }

    private void saveAlarm()
    {
        String check = ((EditText) findViewById( R.id.total ) ).getText().toString();
        int value = Integer.parseInt(check);
        int index = ( ((Spinner) findViewById( R.id.courseSpin ) ).getSelectedItemPosition() - 1 );
        long id  = ( index <= 0 ? index : db.courses.get( index - 1 ).id );
        Subject sub = Subject.getSubject(this, (( Spinner ) findViewById(R.id.extra)).getSelectedItem().toString());
        db.updateAlarm( db.getAlarm(alarmId).withCorrect(value).withCourse(( int ) id).withExtra(sub.value) );
    }

    public void studyNowClick( View v )
    {
        if ( isLoaded && checkForm() )
        {
            if ( alarmId == -1 )
            {
                Intent intent = new Intent(this, QuestionActivity.class);
                handleForm(intent);
                startActivity(intent);
            }
            else
            {
                saveAlarm();
                Intent appIntent = new Intent( this, AlarmActivity.class );
                startActivity(appIntent.putExtra(PreferenceKeys.COURSE_INTENT, alarmId + ""));
            }
        }
        else if ( !isLoaded )
        {
            Toast.makeText( this, "Please wait!" , Toast.LENGTH_SHORT ).show();
        }
    }

    private boolean checkForm()
    {
        String check = ((EditText) findViewById( R.id.total ) ).getText().toString();
        try
        {
            long value = Long.parseLong( check );
            if ( value < 1 )
            {
                Toast.makeText(this, "Total must be more than zero" , Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch ( Exception e )
        {
            Toast.makeText(this, "Total must be more than zero" , Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void handleForm( Intent intent )
    {
        int index = ( ((Spinner) findViewById( R.id.courseSpin ) ).getSelectedItemPosition() - 1 );
        long id  = ( index <= 0 ? index : db.courses.get( index - 1 ).id );
        intent.putExtra(PreferenceKeys.COURSE_INTENT , id + "" );
        intent.putExtra(PreferenceKeys.EXTRA_INTENT , ((Spinner) findViewById( R.id.extra ) ).getSelectedItem().toString() );
        intent.putExtra(PreferenceKeys.TOTAL_INTENT , ((EditText) findViewById( R.id.total ) ).getText().toString() );
    }

    public void updateList( boolean isAlarm )
    {
        Alarm alarm = null;
        if ( alarmId != -1 )
        {
            alarm = db.getAlarm(alarmId);
        }
        Spinner spin = (Spinner) findViewById(R.id.courseSpin );
        Spinner spin2 = (Spinner)findViewById(R.id.extra );
        List<String> courseList = new ArrayList<>();
        courseList.add( "None" );
        courseList.add("Any");
        int index = 1;
        int found = index;
        for ( Course course: db.courses )
        {
            index++;
            courseList.add( course.title );
            if ( alarm != null && alarm.course == course.id )
            {
                found = index;
            }
        }

        if ( alarm != null && alarm.course < 1 )
        {
            found = ( alarm.course == 0 ? 1 : 0 );
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>( this, R.layout.spinner_main, courseList);
        spin.setAdapter(spinnerArrayAdapter);
        spin.setSelection(found);
        if ( isAlarm )
        {
            (( EditText ) findViewById(R.id.total)).setText( String.valueOf( alarm.correct ) );
        }

        ArrayAdapter<CharSequence> mAdaptor = ArrayAdapter
                .createFromResource(this,
                        R.array.extraItems,
                        R.layout.spinner_main);
        spin2.setAdapter(mAdaptor);
        if ( alarm != null )
        {
            index = 0;
            found = index;
            Subject subject = Subject.getSubject( alarm.extra );
            Log.d( "Look" , subject.name() );
            for ( CharSequence str : this.getResources().getTextArray(R.array.extraItems) )
            {
                if ( subject == Subject.getSubject( this, str.toString() ) )
                {
                    found = index;
                    Log.d( "Found" , str.toString() );
                }
                index++;
            }

            spin2.setSelection( found );
        }
        isLoaded = true;
    }

    private class LongOperation extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            db = DataHelper.instance(SubjectActivity.this.getApplicationContext());
            db.clearQuestionsInMem();
            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            updateList( alarmId != -1 );
        }
    }
}
