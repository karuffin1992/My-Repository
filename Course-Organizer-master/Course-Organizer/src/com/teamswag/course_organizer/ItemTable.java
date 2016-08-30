package com.teamswag.course_organizer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ItemTable {
	public static final String NAME = "item";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "item_name";
	public static final String COLUMN_CRITERIA_ID = "criteria_id";
	public static final String COLUMN_GRADE = "grade";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + NAME + " ( " + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
				+ " TEXT NOT NULL, " + COLUMN_CRITERIA_ID + " INTEGER, "
				+ COLUMN_GRADE + " REAL DEFAULT 0, UNIQUE (" + COLUMN_NAME + ", "
				+ COLUMN_CRITERIA_ID + "));");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {

		Log.w(YearTable.class.getName(), "Upgrading from version " + oldVersion
				+ " to version " + newVersion + ", which will destroy all data");
		db.execSQL("DROP TABLE IF EXISTS " + NAME);
		onCreate(db);
	}
	
	public static String getId(String name, String criteriaId, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME + " WHERE "
						+ COLUMN_NAME + "=\'" + name + "\' AND "
						+ COLUMN_CRITERIA_ID + "=\'" + criteriaId + "\'", null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	public static void add(String name, String criteriaId, String grade,
			DatabaseHelper db) {
		ContentValues cv = new ContentValues(3);

		cv.put(COLUMN_NAME, name);
		cv.put(COLUMN_CRITERIA_ID, criteriaId);
		cv.put(COLUMN_GRADE, grade);
		db.getWritableDatabase().insert(NAME, null, cv);
	}

	protected static void delete(String name, String criteriaId,
			DatabaseHelper db) {
		db.getWritableDatabase().execSQL(
				"DELETE FROM " + NAME + " WHERE " + COLUMN_NAME + "=\'" + name
						+ "\' AND " + COLUMN_CRITERIA_ID + "=\'" + criteriaId + "\'");

	}

	protected static void deleteByCriteriaId(String criteriaId, DatabaseHelper db) {
		db.getWritableDatabase().execSQL(
				"DELETE FROM " + NAME + " WHERE " + COLUMN_CRITERIA_ID + "=\'"
						+ criteriaId + "\'");
	}

	public static String getGrade(String name, String criteriaId, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_GRADE + " FROM " + NAME + " WHERE "
						+ COLUMN_NAME + "=\'" + name + "\' AND "
						+ COLUMN_CRITERIA_ID + "=\'" + criteriaId + "\'", null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}
}
