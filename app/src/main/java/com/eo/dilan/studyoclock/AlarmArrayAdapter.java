package com.eo.dilan.studyoclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.eo.dilan.studyoclock.database.Alarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmArrayAdapter extends ArrayAdapter<String>
{
	private final AllQuestionsActivity context;
	private final List<Alarm> alarms;
    private final List<String> courses;

	public AlarmArrayAdapter(AllQuestionsActivity context, ArrayList<String> values, List<Alarm> alarms)
	{
		super(context, -1, values);
		this.context = context;
		this.alarms = alarms;
        this.courses = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
        final Alarm alarm = alarms.get( position );
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_viewer_alarm, parent, false);
		TextView timeView = (TextView ) rowView.findViewById(R.id.myTime );
		TextView courseView = (TextView)rowView.findViewById(R.id.myCourse);
		final Switch onView = (Switch)rowView.findViewById(R.id.myOn);
		onView.setOnClickListener( new View.OnClickListener(){
			public void onClick( View v )
			{
				context.toggleAlarm( alarm, onView.isChecked() );
			}

		});
        timeView.setText( alarm.getTime() );
        courseView.setText( courses.get( position ) );
        onView.setChecked( alarm.isOn != 0 );
		return rowView;
	}
} 

