package com.teamswag.course_organizer;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class YearActivity extends ListActivity implements
		OnItemLongClickListener {

	private ArrayList<String> yearList = new ArrayList<String>();
	private ArrayAdapter<String> aa;
	private DatabaseHelper db;
	private Cursor cursor;
	private ListView lv;
	private TextView gpa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_year);

		db = new DatabaseHelper(this);
		populateList();

		aa = new ArrayAdapter<String>(this, R.layout.row2, R.id.tv_row,
				yearList);
		setListAdapter(aa);
		lv = getListView();
		lv.setOnItemLongClickListener(this);
		gpa = (TextView) findViewById(R.id.tv_gpa);
		gpa.setText(GpaCalculator.getGPA(db) + " GPA");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(YearActivity.this, SemesterActivity.class);

		String name = yearList.get(position);
		String yearId = YearTable.getId(name, db);

		intent.putExtra(YearTable.COLUMN_NAME, name);
		intent.putExtra(SemesterTable.COLUMN_YEAR_ID, yearId);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		final String name = yearList.get(position);

		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.confirm_delete);
		b.setPositiveButton(R.string.delete,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						YearTable.delete(name, db);
						populateList();
						aa.notifyDataSetChanged();
						gpa.setText(GpaCalculator.getGPA(db) + " GPA");
					}
				});
		b.setNegativeButton(android.R.string.cancel, null);
		b.create().show();

		return true;
	}

	public void plusYear(View view) {

		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.year_addyear);
		final EditText input = new EditText(this);
		input.setSingleLine(true);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setHint(R.string.year_name_hint);
		b.setView(input);
		b.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						if (input.getText().length() == 0)
							return;

						String year = Integer.toString(Math.abs(Integer.valueOf(
								input.getText().toString())));
						YearTable.add(year, db);

						populateList();
						aa.notifyDataSetChanged();
					}
				});
		b.setNegativeButton(android.R.string.cancel, null);
		b.create().show();

	}

	private void populateList() {
		cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + YearTable.COLUMN_NAME + " FROM " + YearTable.NAME
						+ " ORDER BY " + YearTable.COLUMN_NAME + " DESC", null);

		yearList.clear();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			yearList.add(String.valueOf(cursor.getInt(0)));
			cursor.moveToNext();
		}
		cursor.close();

	}
}
