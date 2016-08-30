package com.teamswag.course_organizer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class YearTable {
	public static final String NAME = "year";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "year_name";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + NAME + " (" + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
				+ " INTEGER UNIQUE);");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {

		Log.w(YearTable.class.getName(), "Upgrading from version " + oldVersion
				+ " to version " + newVersion + ", which will destroy all data");
		db.execSQL("DROP TABLE IF EXISTS " + NAME);
		onCreate(db);
	}
	
	public static String getId(String name, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME
						+ " WHERE " + COLUMN_NAME + "=" + name,
				null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}
	public static void add(String name, DatabaseHelper db) {
		ContentValues cv = new ContentValues(1);
		cv.put(COLUMN_NAME, name);
		db.getWritableDatabase().insert(NAME, null, cv);
	}
	
	protected static void delete(String name, DatabaseHelper db) {
		String yearId = getId(name, db);
		SemesterTable.deleteByYearId(yearId, db);
		db.getWritableDatabase().execSQL("DELETE FROM " + NAME + " WHERE "
						+ COLUMN_ID + "=" + yearId);
		
	}
}
