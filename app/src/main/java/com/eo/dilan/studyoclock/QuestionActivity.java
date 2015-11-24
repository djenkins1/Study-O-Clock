package com.eo.dilan.studyoclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eo.dilan.studyoclock.database.*;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionActivity extends AppCompatActivity
{
	private DataHelper db;

	private int totalNeeded;

	private Vibrator vibrator;

	private final QuestionActivity me = this;

	private boolean isAlarm = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_question);
		vibrator = (Vibrator ) getSystemService( this.VIBRATOR_SERVICE);
		Intent intent = getIntent();
		if ( intent != null && intent.getExtras() != null && intent.getExtras().getString("alarm") != null )
		{
			isAlarm = true;
			long[] pattern = { 0, 200, 500 };
			vibrator.vibrate( pattern, 0);
		}
		new LongOperation().execute();
	}

	private void updateQuestion( Question question )
	{
		TextView quest = ( TextView ) findViewById(R.id.question);
		if ( question == null )
		{
			quest.setText("NO QUESTION");
			return;
		}

		quest.setText(question.question);
		ArrayList< Button > buttons = new ArrayList<>();
		buttons.add(( Button ) findViewById(R.id.button1));
		buttons.add(( Button ) findViewById(R.id.button2));
		buttons.add(( Button ) findViewById(R.id.button3));
		buttons.add(( Button ) findViewById(R.id.button4));
		int min = Math.min(buttons.size(), question.answers.size());
		Collections.shuffle(question.answers);
		for ( int i = 0; i < min; i++ )
		{
			Button button = buttons.get(i);
			button.setVisibility(View.VISIBLE);
			Answer answer = question.answers.get(i);
			boolean yes = (answer.isCorrect > 0);
			button.setText(answer.answer);
			new ClickHandler(button, this, yes);
		}

		for ( int i = min; i < buttons.size(); i++ )
		{
			buttons.get( i ).setText( "" );
			buttons.get( i ).setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//vibrator.cancel();  // cancel for example here
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		vibrator.cancel();   // or cancel here
	}

	private class ClickHandler
	{
		public ClickHandler( Button button, QuestionActivity me, boolean correct )
		{
			final QuestionActivity you = me;
			final boolean isCorrect = correct;
			button.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					//Toast.makeText(you , myText, Toast.LENGTH_SHORT).show();
					//long[] correctPattern = {0, 100, 50, 100};
					//long[] incorrect = { 0, 300, 50, 0 };
					//vibrator.vibrate( ( isCorrect ? correctPattern : incorrect ), -1); //-1 is important
					db.incrementQuestion();
					totalNeeded += ( isCorrect ? -1 : 0 );
					if ( totalNeeded == 0 && isAlarm )
					{
						vib.cancel();
						Intent intent = new Intent( you, MainActivity.class );
						you.startActivity( intent );
						return;
					}
					updateQuestion(db.getCurrentQuestion() );
				}
			});
		}
	}

	private class LongOperation extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... params)
		{
			db = new DataHelper( me );
			return null;
		}

		@Override
		protected void onPostExecute(Void param)
		{
			totalNeeded = db.alarms.get(0).correct;
			updateQuestion(db.getCurrentQuestion());
		}


	}
}
