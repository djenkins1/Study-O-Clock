package com.eo.dilan.studyoclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.Course;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.PreferenceKeys;

import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity
{
    private DataHelper db;

    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_subject);
        new LongOperation().execute();
    }

    public void studyNowClick( View v )
    {
        if ( isLoaded && checkForm() ) {
            Intent intent = new Intent(this, QuestionActivity.class);
            handleForm( intent );
            startActivity(intent);
        }
        else if ( !isLoaded )
        {
            Toast.makeText( this, "Please wait!" , Toast.LENGTH_SHORT );
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
                Toast.makeText(this, "Total must be at least one" , Toast.LENGTH_SHORT);
                return false;
            }
        }
        catch ( Exception e )
        {
            return false;
        }
        return true;
    }

    private void handleForm( Intent intent )
    {
        intent.putExtra(PreferenceKeys.COURSE_INTENT , ( ((Spinner) findViewById( R.id.courseSpin ) ).getSelectedItemPosition() - 1 ) + "" );
        intent.putExtra(PreferenceKeys.EXTRA_INTENT , ((Spinner) findViewById( R.id.extra ) ).getSelectedItem().toString() );
        intent.putExtra(PreferenceKeys.TOTAL_INTENT , ((EditText) findViewById( R.id.total ) ).getText().toString() );
    }

    public void updateList()
    {
        Spinner spin = (Spinner) findViewById(R.id.courseSpin );
        Spinner spin2 = (Spinner)findViewById(R.id.extra );
        List<String> courseList = new ArrayList<>();
        courseList.add( "None" );
        courseList.add( "Any" );
        for ( Course course: db.courses )
        {
            courseList.add( course.title );
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>( this, R.layout.spinner_main, courseList);
        spin.setAdapter( spinnerArrayAdapter);

        ArrayAdapter<CharSequence> madaptor = ArrayAdapter
                .createFromResource(this,
                        R.array.extraItems,
                        R.layout.spinner_main);
        spin2.setAdapter(madaptor);
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
            updateList();
        }
    }
}
