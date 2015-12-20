package com.eo.dilan.studyoclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.PreferenceKeys;

public class ScoreActivity extends AppCompatActivity
{
    private SharedPreferences shared;

    private DataHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_score);
        shared = getSharedPreferences(PreferenceKeys.PREF_KEY, Context.MODE_PRIVATE);
    }
}
