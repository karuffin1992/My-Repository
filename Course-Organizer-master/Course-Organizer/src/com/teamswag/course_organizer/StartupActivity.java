package com.teamswag.course_organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

public class StartupActivity extends Activity {

	private DatabaseHelper db;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		db = new DatabaseHelper(this);
		cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + YearTable.COLUMN_NAME + " FROM " + YearTable.NAME,
				null);
		if (cursor.getCount() != 0) {
			Intent intent = new Intent(StartupActivity.this, YearActivity.class);
			startActivity(intent);
			cursor.close();
			finish();
		}
	}

	public void plusYear(View view) {

		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.year_addyear);
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		b.setView(input);
		b.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						if (input.getText().length() == 0)
							return;
						YearTable.add(input.getText().toString(), db);
						Intent intent = new Intent(StartupActivity.this,
								YearActivity.class);
						startActivity(intent);
					}
				});
		b.setNegativeButton(android.R.string.cancel, null);
		b.create().show();
	}

}
