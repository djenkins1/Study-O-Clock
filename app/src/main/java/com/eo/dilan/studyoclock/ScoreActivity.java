package com.eo.dilan.studyoclock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.PreferenceKeys;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity
{
    private SharedPreferences shared;

    private DataHelper db;

    private String[] qList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_score);
        shared = getSharedPreferences(PreferenceKeys.PREF_KEY, Context.MODE_PRIVATE);
        Intent intent = getIntent();
        if ( intent.getExtras() != null && intent.getExtras().getString( PreferenceKeys.Q_LIST ) != null )
        {
            qList = intent.getExtras().getString( PreferenceKeys.Q_LIST ).split( "," );
            if ( qList.length == 1 && qList[ 0 ].equals( "" ) )
            {
                qList = new String[0];
            }
            TextView counter = (TextView) findViewById(R.id.counter);
            counter.setText( counter.getText().toString().replace( "?" , qList.length + "" ));
            new LongOperation().execute();
        }
        else
        {
            Logger.print(this, "BAD" , "Question list empty" );
            startActivity( new Intent( this, MainActivity.class ) );
        }
    }

    @Override
    public void onBackPressed()
    {
        handleFinish( null );
    }

    private void updateList()
    {
        final ListView listview = (ListView) findViewById(R.id.listview);
        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < db.allQuestions.size(); ++i)
        {
            list.add(db.allQuestions.get(i).question);
        }

        final QuestionArrayAdapter adapter = new QuestionArrayAdapter(this, list, db.allQuestions );
        FrameLayout footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.footer_score_res, null);
        listview.addFooterView(footerLayout);
        listview.setAdapter(adapter);
    }

    public void handleFinish( View v )
    {
        Intent intent = new Intent( this, MainActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class LongOperation extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... params)
        {
            db = DataHelper.instance(ScoreActivity.this.getApplicationContext());
            db.clearQuestionsInMem();

            for ( String str : qList )
            {
                try
                {
                    long id = Long.parseLong(str);
                    db.setToQuestion(id, false );
                }
                catch( NumberFormatException e )
                {
                    continue;
                }
            }
            return null;
        }

        protected void onPostExecute(Void param)
        {
            updateList();
        }
    }
}
