package com.teamswag.course_organizer;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CriteriaActivity extends ListActivity implements
		OnItemLongClickListener {

	ArrayList<String> criteriaList = new ArrayList<String>();
	private ArrayAdapter<String> aa;
	private DatabaseHelper db;
	private Cursor cursor;
	private TextView yearPath;
	private TextView semesterPath;
	private TextView coursePath;
	private ListView lv;
	private String yearName;
	private String yearId;
	private String semesterName;
	private String semesterId;
	private String courseName;
	private String courseId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_criteria);

		yearPath = (TextView) findViewById(R.id.tv_criteriayear);
		semesterPath = (TextView) findViewById(R.id.tv_criteriasemester);
		coursePath = (TextView) findViewById(R.id.tv_criteriacourse);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			yearName = bundle.getString(YearTable.COLUMN_NAME);
			yearId = bundle.getString(CourseTable.COLUMN_YEAR_ID);
			semesterName = bundle.getString(SemesterTable.COLUMN_NAME);
			semesterId = bundle.getString(CourseTable.COLUMN_SEMESTER_ID);
			courseName = bundle.getString(CourseTable.COLUMN_NAME);
			courseId = bundle.getString(CriteriaTable.COLUMN_COURSE_ID);

		}

		yearPath.setText(yearName);
		semesterPath.setText(" ->" + semesterName);
		coursePath.setText(" ->" + courseName);

		db = new DatabaseHelper(this);
		populateList();

		aa = new CriteriaAdapter();
		setListAdapter(aa);

		lv = getListView();
		lv.setOnItemLongClickListener(this);

	}
	class CriteriaAdapter extends ArrayAdapter<String> {
		CriteriaAdapter() {
			super(CriteriaActivity.this, R.layout.row, R.id.tv_leftvalue,
					criteriaList);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			String criteriaWeight = CriteriaTable.getWeight(criteriaList.get(position),
					courseId, db) + "%";
			View row = super.getView(position, convertView, parent);
			TextView right = (TextView) row.findViewById(R.id.tv_rightvalue);
			right.setText(criteriaWeight);
			return row;
		}
	}

	public void returnToYear(View v) {
		Intent path = new Intent(CriteriaActivity.this, YearActivity.class);
		startActivity(path);
	}

	public void returnToSemester(View v) {
		Intent path = new Intent(CriteriaActivity.this, SemesterActivity.class);
		path.putExtra(YearTable.COLUMN_NAME, yearName);
		path.putExtra(SemesterTable.COLUMN_YEAR_ID, yearId);
		startActivity(path);
	}

	public void returnToCourse(View v) {
		Intent path = new Intent(CriteriaActivity.this, CourseActivity.class);
		path.putExtra(YearTable.COLUMN_NAME, yearName);
		path.putExtra(CourseTable.COLUMN_YEAR_ID, yearId);
		path.putExtra(SemesterTable.COLUMN_NAME, semesterName);
		path.putExtra(CourseTable.COLUMN_SEMESTER_ID, semesterId);
		startActivity(path);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent items = new Intent(CriteriaActivity.this, ItemActivity.class);

		String criteriaName = criteriaList.get(position);
		String criteriaId = CriteriaTable.getId(criteriaName, courseId, db);

		items.putExtra(YearTable.COLUMN_NAME, yearName);
		items.putExtra(CourseTable.COLUMN_YEAR_ID, yearId);
		items.putExtra(SemesterTable.COLUMN_NAME, semesterName);
		items.putExtra(CourseTable.COLUMN_SEMESTER_ID, semesterId);
		items.putExtra(CourseTable.COLUMN_NAME, courseName);
		items.putExtra(CriteriaTable.COLUMN_COURSE_ID, courseId);
		items.putExtra(CriteriaTable.COLUMN_NAME, criteriaName);
		items.putExtra(ItemTable.COLUMN_CRITERIA_ID, criteriaId);
		startActivity(items);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		final String name = criteriaList.get(position);

		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.confirm_delete);
		b.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				CriteriaTable.delete(name, courseId, db);
				populateList();
				aa.notifyDataSetChanged();
			}
		});
		b.setNegativeButton(android.R.string.cancel, null);
		b.create().show();

		return true;
	}

	public void plusCriteria(View view) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View inputLayout = inflater.inflate(R.layout.activity_inputcriteria,
				null);

		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.criteria_addcriteria);
		b.setView(inputLayout);
		final EditText criterionName = (EditText) inputLayout
				.findViewById(R.id.et_criterionname);
		criterionName.setSingleLine(true);
		final EditText criterionWeight = (EditText) inputLayout
				.findViewById(R.id.et_criterionweight);
		criterionWeight.setSingleLine(true);
		b.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						if (criterionName.getText().length() == 0)
							return;
						String name = criterionName.getText().toString();
						String weight;
						if (criterionWeight.getText().length() == 0)
							weight = "0";
						else
							weight = criterionWeight.getText().toString();
						CriteriaTable.add(name, courseId, weight, db);
						populateList();
						aa.notifyDataSetChanged();
					}
				});
		b.setNegativeButton(android.R.string.cancel, null);
		b.create().show();

	}

	private void populateList() {
		cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + CriteriaTable.COLUMN_NAME + " FROM "
						+ CriteriaTable.NAME + " WHERE "
						+ CriteriaTable.COLUMN_COURSE_ID + "=\'" + courseId
						+ "\' ORDER BY " + CriteriaTable.COLUMN_NAME + " ASC",
				null);

		criteriaList.clear();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			criteriaList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();

	}



}
