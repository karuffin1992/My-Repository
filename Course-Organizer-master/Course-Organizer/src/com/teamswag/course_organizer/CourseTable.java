package com.teamswag.course_organizer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CourseTable {
	public static final String NAME = "course";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "course_name";
	public static final String COLUMN_YEAR_ID = "year_id";
	public static final String COLUMN_SEMESTER_ID = "semester_id";
	public static final String COLUMN_CREDIT_HOURS = "credit_hours";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + NAME + " ( " + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
				+ " TEXT NOT NULL, " + COLUMN_CREDIT_HOURS + " TEXT NOT NULL, "
				+ COLUMN_YEAR_ID + " INTEGER, " + COLUMN_SEMESTER_ID
				+ " TEXT NOT NULL, UNIQUE (" + COLUMN_NAME + ", "
				+ COLUMN_SEMESTER_ID + "));");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {

		Log.w(YearTable.class.getName(), "Upgrading from version " + oldVersion
				+ " to version " + newVersion + ", which will destroy all data");
		db.execSQL("DROP TABLE IF EXISTS " + NAME);
		onCreate(db);
	}

	public static String getId(String name, String semesterId, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME + " WHERE "
						+ COLUMN_NAME + "=\"" + name + "\" AND "
						+ COLUMN_SEMESTER_ID + "=\"" + semesterId + "\"", null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	public static String getWeight(String name, String semesterId,
			DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME + " WHERE "
						+ COLUMN_NAME + "=\"" + name + "\" AND "
						+ COLUMN_SEMESTER_ID + "=\"" + semesterId + "\"", null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	public static void add(String name, String creditHours, String semesterId, String yearId,
			DatabaseHelper db) {
		ContentValues cv = new ContentValues(4);

		cv.put(COLUMN_NAME, name);
		cv.put(COLUMN_CREDIT_HOURS, creditHours);
		cv.put(COLUMN_SEMESTER_ID, semesterId);
		cv.put(COLUMN_YEAR_ID, yearId);
		db.getWritableDatabase().insert(NAME, null, cv);
	}

	protected static void delete(String name, String semesterId,
			DatabaseHelper db) {
		String courseId = getId(name, semesterId, db);
		CriteriaTable.deleteByCourseId(courseId, db);
		GradeScaleTable.delete(courseId, db);
		db.getWritableDatabase().execSQL(
				"DELETE FROM " + NAME + " WHERE " + COLUMN_ID + "=\""
						+ courseId + "\"");

	}

	protected static void deleteBySemesterId(String semesterId,
			DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME + " WHERE "
						+ COLUMN_SEMESTER_ID + "=\"" + semesterId + "\"", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String courseId = cursor.getString(0);
			CriteriaTable.deleteByCourseId(courseId, db);
			GradeScaleTable.delete(courseId, db);
			cursor.moveToNext();
		}
		cursor.close();
		db.getWritableDatabase().execSQL(
				"DELETE FROM " + CourseTable.NAME + " WHERE "
						+ COLUMN_SEMESTER_ID + "=\"" + semesterId + "\"");
	}

	protected static void deleteByYearId(String yearId, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME + " WHERE "
						+ COLUMN_YEAR_ID + "=\"" + yearId + "\"", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String courseId = cursor.getString(0);
			CriteriaTable.deleteByCourseId(courseId, db);
			GradeScaleTable.delete(courseId, db);
			cursor.moveToNext();
		}
		cursor.close();

		db.getWritableDatabase().execSQL(
				"DELETE FROM " + NAME + " WHERE " + COLUMN_YEAR_ID + "="
						+ yearId);
	}
}
