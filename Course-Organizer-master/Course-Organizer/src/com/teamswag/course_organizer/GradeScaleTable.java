package com.teamswag.course_organizer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GradeScaleTable {

	public static final String NAME = "grade_scale";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_COURSE_ID = "course_id";
	public static final String COLUMN_A_PLUS = "A_plus", COLUMN_A = "A",
			COLUMN_A_MINUS = "A_minus", COLUMN_B_PLUS = "B_plus",
			COLUMN_B = "B", COLUMN_B_MINUS = "B_minus",
			COLUMN_C_PLUS = "C_plus", COLUMN_C = "C",
			COLUMN_C_MINUS = "C_minus", COLUMN_D_PLUS = "D_plus",
			COLUMN_D = "D", COLUMN_D_MINUS = "D_minus";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + NAME + " ( " + COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_A_PLUS
				+ " REAL DEFAULT 97, " + COLUMN_A + " REAL DEFAULT 93, "
				+ COLUMN_A_MINUS + " REAL DEFAULT 90, " + COLUMN_B_PLUS
				+ " REAL DEFAULT 87, " + COLUMN_B + " REAL DEFAULT 83, "
				+ COLUMN_B_MINUS + " REAL DEFAULT 80, " + COLUMN_C_PLUS
				+ " REAL DEFAULT 77, " + COLUMN_C + " REAL DEFAULT 73, "
				+ COLUMN_C_MINUS + " REAL DEFAULT 70, " + COLUMN_D_PLUS
				+ " REAL DEFAULT 67, " + COLUMN_D + " REAL DEFAULT 63, "
				+ COLUMN_D_MINUS + " REAL DEFAULT 60, " + COLUMN_COURSE_ID
				+ " TEXT NOT NULL);");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {

		Log.w(YearTable.class.getName(), "Upgrading from version " + oldVersion
				+ " to version " + newVersion + ", which will destroy all data");
		db.execSQL("DROP TABLE IF EXISTS " + NAME);
		onCreate(db);
	}

	public static String getId(String courseId, DatabaseHelper db) {
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + COLUMN_ID + " FROM " + NAME + " WHERE "
						+ COLUMN_COURSE_ID + "=\"" + courseId + "\"", null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	public static void add(String courseId, String aPlus, String a,
			String aMinus, String bPlus, String b, String bMinus, String cPlus,
			String c, String cMinus, String dPlus, String d, String dMinus,
			DatabaseHelper db) {
		ContentValues cv = new ContentValues(13);

		cv.put(COLUMN_COURSE_ID, courseId);
		cv.put(COLUMN_A_PLUS, aPlus);
		cv.put(COLUMN_A, a);
		cv.put(COLUMN_A_MINUS, aMinus);
		cv.put(COLUMN_B_PLUS, bPlus);
		cv.put(COLUMN_B, b);
		cv.put(COLUMN_B_MINUS, bMinus);
		cv.put(COLUMN_C_PLUS, cPlus);
		cv.put(COLUMN_C, c);
		cv.put(COLUMN_C_MINUS, cMinus);
		cv.put(COLUMN_D_PLUS, dPlus);
		cv.put(COLUMN_D, d);
		cv.put(COLUMN_D_MINUS, dMinus);
		db.getWritableDatabase().insert(NAME, null, cv);
	}

	protected static GradeScale getGradeScale(String courseId, DatabaseHelper db) {
		GradeScale gs;
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + GradeScaleTable.COLUMN_D_MINUS + ", "
						+ GradeScaleTable.COLUMN_D + ", "
						+ GradeScaleTable.COLUMN_D_PLUS + ", "
						+ GradeScaleTable.COLUMN_C_MINUS + ", "
						+ GradeScaleTable.COLUMN_C + ", "
						+ GradeScaleTable.COLUMN_C_PLUS + ", "
						+ GradeScaleTable.COLUMN_B_MINUS + ", "
						+ GradeScaleTable.COLUMN_B + ", "
						+ GradeScaleTable.COLUMN_B_PLUS + ", "
						+ GradeScaleTable.COLUMN_A_MINUS + ", "
						+ GradeScaleTable.COLUMN_A + ", "
						+ GradeScaleTable.COLUMN_A_PLUS + " FROM "
						+ GradeScaleTable.NAME + " WHERE "
						+ GradeScaleTable.COLUMN_COURSE_ID + "=\"" + courseId
						+ "\"", null);

		cursor.moveToFirst();
		double d_minus = cursor.getDouble(0);
		double d = cursor.getDouble(1);
		double d_plus = cursor.getDouble(2);
		double c_minus = cursor.getDouble(3);
		double c = cursor.getDouble(4);
		double c_plus = cursor.getDouble(5);
		double b_minus = cursor.getDouble(6);
		double b = cursor.getDouble(7);
		double b_plus = cursor.getDouble(8);
		double a_minus = cursor.getDouble(9);
		double a = cursor.getDouble(10);
		double a_plus = cursor.getDouble(11);

		gs = new GradeScale(d_minus, d, d_plus, c_minus, c, c_plus, b_minus, b,
				b_plus, a_minus, a, a_plus);

		return gs;

	}

	protected static void delete(String courseId, DatabaseHelper db) {
		db.getWritableDatabase().execSQL(
				"DELETE FROM " + NAME + " WHERE " + COLUMN_COURSE_ID + "=\""
						+ courseId + "\"");

	}

}
