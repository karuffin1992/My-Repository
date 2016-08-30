package com.teamswag.course_organizer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CriteriaTable {
	public static final String NAME = "criteria";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "criteria_name";
	public static final String COLUMN_WEIGHT = "weight";
	public static final String COLUMN_COURSE_ID = "course_id";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + NAME + " ( " + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
				+ " TEXT NOT NULL, " + COLUMN_WEIGHT + " REAL DEFAULT 0, "
				+ COLUMN_COURSE_ID + " INTEGER, UNIQUE (" + COLUMN_NAME + ", "
				+ COLUMN_COURSE_ID + "));");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {

		Log.w(CriteriaTable.class.getName(), "Upgrading from version "
				+ oldVersion + " to version " + newVersion
				+ ", which will destroy all data");
		db.execSQL("DROP TABLE IF EXISTS " + NAME);
		onCreate(db);
	}

	public static String getId(String name, String courseId, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME + " WHERE "
						+ COLUMN_NAME + "=\"" + name + "\" AND "
						+ COLUMN_COURSE_ID + "=\"" + courseId + "\"", null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}
	
	public static String getWeight(String name, String courseId, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_WEIGHT + " FROM " + NAME + " WHERE "
						+ COLUMN_NAME + "=\"" + name + "\" AND "
						+ COLUMN_COURSE_ID + "=\"" + courseId + "\"", null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	public static void add(String name, String courseId, String weight,
			DatabaseHelper db) {
		ContentValues cv = new ContentValues(3);

		cv.put(COLUMN_NAME, name);
		cv.put(COLUMN_COURSE_ID, courseId);
		cv.put(COLUMN_WEIGHT, weight);
		db.getWritableDatabase().insert(NAME, null, cv);
	}

	protected static void delete(String name, String courseId,
			DatabaseHelper db) {
		String criteriaId = getId(name, courseId, db);
		ItemTable.deleteByCriteriaId(criteriaId, db);
		db.getWritableDatabase().execSQL(
				"DELETE FROM " + NAME + " WHERE " + COLUMN_NAME + "=\"" + name
						+ "\" AND " + COLUMN_COURSE_ID + "=\"" + courseId + "\"");

	}

	protected static void deleteByCourseId(String courseId, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME + " WHERE "
						+ COLUMN_COURSE_ID + "=\"" + courseId + "\"", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ItemTable.deleteByCriteriaId(cursor.getString(0), db);
			cursor.moveToNext();
		}
		cursor.close();
		
		db.getWritableDatabase().execSQL(
				"DELETE FROM " + NAME + " WHERE " + COLUMN_COURSE_ID + "=\""
						+ courseId + "\"");
	}

}
