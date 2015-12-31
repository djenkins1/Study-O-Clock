package com.eo.dilan.studyoclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.eo.dilan.studyoclock.database.Course;
import com.eo.dilan.studyoclock.database.DataHelper;

import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity
{
    private DataHelper db;

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
        Intent intent = new Intent( this , QuestionActivity.class );
        startActivity(intent);
    }

    public void updateList()
    {
        Spinner spin = (Spinner) findViewById(R.id.courseSpin );
        Spinner spin2 = (Spinner)findViewById(R.id.extra );
        List<String> courseList = new ArrayList<>();
        courseList.add( "None" );
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
