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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.DataHelper;

public class MainActivity extends AppCompatActivity
{

	private DataHelper db;

	private boolean canRun = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent serv = new Intent( getApplicationContext() , AlarmService.class );
		getApplicationContext().startService(serv);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);
		new LongOperation().execute();
		addUnicodeToButtons();
	}

	private void addUnicodeToButtons( )
	{
		TextView btn = (TextView)findViewById(R.id.studyIcon);
		btn.setText( new StringBuilder( "\uD83D\uDCD6" ) );
		btn = (TextView)findViewById(R.id.questIcon);
		btn.setText(new StringBuilder("\u270E"));
		btn = (TextView)findViewById(R.id.alarmIcon);
		btn.setText(new StringBuilder("\uD83D\uDD50"));
		btn = (TextView)findViewById(R.id.settingIcon);
		btn.setText(new StringBuilder("\u2600"));
	}

    public void handleClick( View v )
    {
        switch( v.getId() )
        {
            case R.id.settingsBtn:
                clickSettings(v);
                break;
            case R.id.setAlarmBtn:
                onClickAddAlarm(v);
                break;
            case R.id.viewQuestionBtn:
                allQuestionsClick(v);
                break;
            case R.id.seeQuestionBtn:
                onClickShowQ(v);
                break;
            default:
                break;
        }
    }

    public void runOnClick( final View view )
    {
		final float[] from = new float[3],
				to =   new float[3];

		final int color = ((ColorDrawable)view.getBackground()).getColor();
		Color.colorToHSV(Color.parseColor("#5CCCCC"), to);
		Color.colorToHSV( color , from);

		ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
		anim.setDuration(100);

		final float[] hsv  = new float[3];
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
				view.setBackgroundColor( color );
				handleClick(view);
			}
		});

		anim.start();
    }

	public void clickSettings(View v )
	{
		if ( !canRun )
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent( this , SettingsActivity.class );
		startActivity(intent);
	}

	public void onClickShowQ(View v)
	{
		if ( !canRun )
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
			return;
		}

		if ( db != null && !db.areThereQuestions() )
		{
			Toast.makeText(getApplicationContext(), "You need to add questions in order to study", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent( this , SubjectActivity.class );
		startActivity( intent );
	}

	@Override
	public void onBackPressed()
	{
		return;
	}

	public void onClickAddAlarm(View v)
	{
		if ( !canRun )
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent( this , AlarmActivity.class );
		startActivity(intent);
	}

	public void allQuestionsClick(View v)
	{
		if ( !canRun )
		{
			Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent( this , AllQuestionsActivity.class );
		startActivity( intent );
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = DataHelper.instance(MainActivity.this.getApplicationContext());
			db.clearQuestionsInMem();
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			canRun = true;
			Intent intent = getIntent();
			if ( intent != null && intent.getExtras() != null && intent.getExtras().getString("alarm") != null && db != null && db.areThereQuestions() )
			{
				Intent appIntent = new Intent(MainActivity.this, QuestionActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("alarm", "yes" );
				appIntent.putExtras(mBundle);
				startActivity(appIntent);
			}

		}


	}
}
