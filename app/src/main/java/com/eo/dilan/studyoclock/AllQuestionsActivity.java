package com.eo.dilan.studyoclock;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.eo.dilan.studyoclock.database.DataHelper;
import com.eo.dilan.studyoclock.database.Logger;
import com.eo.dilan.studyoclock.database.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		Intent intent = new Intent( this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private class StableArrayAdapter extends ArrayAdapter<String>
	{

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects)
		{
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i)
			{
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position)
		{
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}

	}

	private void updateList( final ArrayList<Question> questions )
	{
		final ListView listview = (ListView) findViewById(R.id.listview);

		final ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < questions.size(); ++i)
		{
			list.add( questions.get( i ).question );
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.list_viewer, list);
		listview.setAdapter(adapter);

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

}
