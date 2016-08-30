package com.teamswag.course_organizer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "db";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		YearTable.onCreate(db);
		SemesterTable.onCreate(db);
		CourseTable.onCreate(db);
		CriteriaTable.onCreate(db);
		GradeScaleTable.onCreate(db);
		ItemTable.onCreate(db);
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		YearTable.onUpgrade(db, oldVersion, newVersion);
		SemesterTable.onUpgrade(db, oldVersion, newVersion);
		CriteriaTable.onUpgrade(db, oldVersion, newVersion);
		GradeScaleTable.onUpgrade(db, oldVersion, newVersion);
		ItemTable.onUpgrade(db, oldVersion, newVersion);

	}

}
