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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Question;

import java.util.ArrayList;

public class AllQuestionsActivity extends AppCompatActivity {
	private DataHelper db;

	private final AllQuestionsActivity me = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_all_questions);
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
		final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, list, questions);
		FrameLayout footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.footer_res, null);
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
						long quest = questions.get(position).id;
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
			updateList( db.getAllQuestions() );
		}
	}

	public void onClickAddQ(View v)
	{
		Intent intent = new Intent( this , AddQuestionActivity.class );
		startActivity( intent );
	}
}
