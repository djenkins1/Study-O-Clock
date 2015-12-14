package com.eo.dilan.studyoclock.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertBuilder
{
	public AlertBuilder( final String title, final String message, final Context context, final DialogInterface.OnClickListener yesClick, final DialogInterface.OnClickListener noClick )
	{
		new AlertDialog.Builder(context)
				.setTitle( title )
				.setMessage( message )
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, yesClick)
				.setNegativeButton(android.R.string.no, noClick ).show();
	}
}
