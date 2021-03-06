package com.eo.dilan.studyoclock;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eo.dilan.studyoclock.database.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionArrayAdapter extends ArrayAdapter<String>
{
	private final Context context;
	private final List<Question> questions;

	public QuestionArrayAdapter(Context context, ArrayList<String> values, List<Question> questions)
	{
		super(context, -1, values);
		this.context = context;
		this.questions = questions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_viewer, parent, false);
		TextView textView = (TextView ) rowView.findViewById(R.id.myLine );
		TextView correct = (TextView)rowView.findViewById(R.id.myCorrect);
		TextView wrong = (TextView)rowView.findViewById(R.id.myWrong);
		textView.setText(  questions.get( position ).question );
		correct.setText(questions.get( position ).correct + "" );
		correct.setTextColor(Color.GREEN);
		wrong.setText(questions.get(position).wrong + "");
		wrong.setTextColor(Color.RED);
		return rowView;
	}
} 

