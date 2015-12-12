package com.eo.dilan.studyoclock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.Question;

import java.util.ArrayList;

public class AllQuestionsActivity extends AppCompatActivity
{
	private DataHelper db;

	private final AllQuestionsActivity me = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_all_questions);
		new LongOperation().execute();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		db.closeMe();
		Intent intent = new Intent( this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void updateList( final ArrayList<Question> questions )
	{
		final ListView listview = (ListView) findViewById(R.id.listview);

		final ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < questions.size(); ++i)
		{
			list.add( questions.get( i ).question );
		}
		final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter( this, list, questions );
		//final StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.list_viewer, list);
		FrameLayout footerLayout = (FrameLayout ) getLayoutInflater().inflate(R.layout.footer_res,null);
		listview.addFooterView(footerLayout);
		listview.setAdapter(adapter);

		//btnPostYourEnquiry = (Button ) footerLayout.findViewById(R.id.btnGetMoreResults);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView< ? > parent, final View view, int position, long id)
			{
				//final String item = ( String ) parent.getItemAtPosition(position);
				Logger.print(me.getApplicationContext(),"Question Clicked", questions.get(position).question );
				long quest = questions.get(position).id;
				Intent appIntent = new Intent(me, AddQuestionActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putLong("question", quest );
				appIntent.putExtras(mBundle);
				db.closeMe();
				startActivity(appIntent);
			}

		});
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
			updateList(db.allQuestions );
		}
	}

	public void onClickAddQ(View v)
	{
		Intent intent = new Intent( this , AddQuestionActivity.class );
		startActivity( intent );
	}
}
