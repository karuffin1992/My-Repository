package com.teamswag.course_organizer;

import java.util.ArrayList;

import android.database.Cursor;

public class GradeCalculator {

	private static double getScore(ArrayList<Criteria> list, DatabaseHelper db) {
		double score = 0;
		double totalWeight = 0;

		for (int i = 0; i < list.size(); i++) {
			double subtotal = 0;

			Criteria criteria = list.get(i);
			Cursor cursor = db.getReadableDatabase().rawQuery(
					"SELECT " + ItemTable.COLUMN_GRADE + " FROM "
							+ ItemTable.NAME + " WHERE "
							+ ItemTable.COLUMN_CRITERIA_ID + "=\'"
							+ criteria.id + "\'", null);

			int count = cursor.getCount();
			if (count <= 0)
				continue;

			double weight = Double.parseDouble(criteria.weight);
			totalWeight += weight;

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				subtotal += Double.parseDouble(cursor.getString(0));
				cursor.moveToNext();
			}
			score += subtotal / count * weight;
		}

		return (score / totalWeight);
	}

	private static ArrayList<Criteria> getCriteriaList(String courseId,
			DatabaseHelper db) {
		ArrayList<Criteria> list = new ArrayList<Criteria>();
		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + CriteriaTable.COLUMN_NAME + ", "
						+ CriteriaTable.COLUMN_WEIGHT + ", "
						+ CriteriaTable.COLUMN_ID + " FROM "
						+ CriteriaTable.NAME + " WHERE "
						+ CriteriaTable.COLUMN_COURSE_ID + "=\'" + courseId
						+ "\'", null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			list.add(new Criteria(cursor.getString(0), cursor.getString(1),
					cursor.getString(2)));
			cursor.moveToNext();
		}
		cursor.close();

		return list;
	}

	public static String getGrade(String courseId, GradeScale gs,
			DatabaseHelper db) {
		ArrayList<Criteria> list = getCriteriaList(courseId, db);
		if (list.isEmpty())
			return "";
		double score = getScore(list, db);
		if (score < 0)
			return "";

		double d_minus = gs.d_minus;
		double d = gs.d;
		double d_plus = gs.d_plus;
		double c_minus = gs.c_minus;
		double c = gs.c;
		double c_plus = gs.c_plus;
		double b_minus = gs.b_minus;
		double b = gs.b;
		double b_plus = gs.b_plus;
		double a_minus = gs.a_minus;
		double a = gs.a;
		double a_plus = gs.a_plus;

		if (score < d_minus) {
			return "F";
		} else if (score < d) {
			return "D-";
		} else if (score < d_plus) {
			return "D";
		} else if (score < c_minus) {
			return "D+";
		} else if (score < c) {
			return "C-";
		} else if (score < c_plus) {
			return "C";
		} else if (score < b_minus) {
			return "C+";
		} else if (score < b) {
			return "B-";
		} else if (score < b_plus) {
			return "B";
		} else if (score < a_minus) {
			return "B+";
		} else if (score < a) {
			return "A-";
		} else if (score < a_plus) {
			return "A";
		} else
			return "A+";
	}
}

class Criteria {

	protected String name;
	protected String weight;
	protected String id;

	public Criteria(String name, String weight, String id) {
		this.name = name;
		this.weight = weight;
		this.id = id;
	}
}
