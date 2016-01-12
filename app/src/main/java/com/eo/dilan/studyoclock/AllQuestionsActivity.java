package com.eo.dilan.studyoclock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.Alarm;
import com.eo.dilan.studyoclock.database.Course;
import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.PreferenceKeys;
import com.eo.dilan.studyoclock.database.Question;

import java.util.ArrayList;
import java.util.List;

public class AllQuestionsActivity extends AppCompatActivity {
	private DataHelper db;

	private final AllQuestionsActivity me = this;

    //true if the activity is meant to show all the alarms or false otherwise
    private boolean isAlarm = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_all_questions);
        Intent intent = getIntent();
        if ( intent != null && intent.getExtras() != null  )
        {
            if (intent.getExtras().getString("alarm") != null)
            {
                isAlarm = true;
            }
        }
		new LongOperation().execute();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db != null) {
			db.clearQuestionsInMem();
		}

	}

	private void updateList(final ArrayList<Question> questions) {
		final ListView listview = (ListView) findViewById(R.id.listview);

		final ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < questions.size(); ++i) {
			list.add(questions.get(i).question);
		}
		final QuestionArrayAdapter adapter = new QuestionArrayAdapter(this, list, questions);
		FrameLayout footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.footer_res, null);
        FrameLayout headerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.header_question, null);
		listview.addFooterView(footerLayout);
        listview.addHeaderView( headerLayout );
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                final float[] from = new float[3],
                        to = new float[3];

                final int color = ((ColorDrawable) view.getBackground()).getColor();
                Color.colorToHSV(Color.parseColor("#5CCCCC"), to);
                Color.colorToHSV(color, from);

                ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
                anim.setDuration(100);

                final float[] hsv = new float[3];
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // Transition along each axis of HSV (hue, saturation, value)
                        hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                        hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                        hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                        view.setBackgroundColor(Color.HSVToColor(hsv));
                    }
                });

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator arg0) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator arg0) {
                    }

                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        view.setBackgroundColor(color);
                        long quest = questions.get(position - 1).id;
                        Intent appIntent = new Intent(me, AddQuestionActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putLong("question", quest);
                        appIntent.putExtras(mBundle);
                        startActivity(appIntent);
                    }
                });

                anim.start();

            }

        });
	}

    private void updateListAsAlarm( final List<Alarm> alarms )
    {
        final ListView listview = (ListView) findViewById(R.id.listview);

        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < alarms.size(); ++i)
        {
            //list.add(alarms.get(i).getTime() );
            Log.d("COURSE", alarms.get(i).course + "");
            list.add( db.getCourse(new Course().withID(alarms.get(i).course)).title );
        }

        final AlarmArrayAdapter adapter = new AlarmArrayAdapter(this, list, alarms);
        FrameLayout footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.footer_alarm, null);
        FrameLayout headerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.header_alarm, null);
        listview.addHeaderView(headerLayout);
        listview.addFooterView(footerLayout);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                final float[] from = new float[3],
                        to = new float[3];

                final int color = ((ColorDrawable) view.getBackground()).getColor();
                Color.colorToHSV(Color.parseColor("#5CCCCC"), to);
                Color.colorToHSV(color, from);

                ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
                anim.setDuration(100);

                final float[] hsv = new float[3];
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // Transition along each axis of HSV (hue, saturation, value)
                        hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                        hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                        hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                        view.setBackgroundColor(Color.HSVToColor(hsv));
                    }
                });

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator arg0) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator arg0) {
                    }

                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        view.setBackgroundColor(color);
                        long id = alarms.get( position - 1 ).id;
                        Intent appIntent = new Intent( me, AlarmActivity.class );
                        startActivity( appIntent.putExtra(PreferenceKeys.COURSE_INTENT , id + "") );
                    }
                });

                anim.start();

            }

        });
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
            if ( isAlarm )
            {
                updateListAsAlarm( db.alarms );
            }
            else
            {
                updateList(db.getAllQuestions());
            }
		}
	}

	public void onClickAddQ(View v)
	{
		Intent intent = new Intent( this , AddQuestionActivity.class );
		startActivity( intent );
	}

    public void onClickAddA( View v )
    {
        Intent intent = new Intent( this , AlarmActivity.class );
        startActivity( intent );
    }
}
