package com.teamswag.course_organizer;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.database.Cursor;

public class GpaCalculator {
	final static double A_plus = 4.00;
	final static double A = 4.00;
	final static double A_minus = 3.70;
	final static double B_plus = 3.30;
	final static double B = 3.00;
	final static double B_minus = 2.70;
	final static double C_plus = 2.30;
	final static double C = 2.00;
	final static double C_minus = 1.70;
	final static double D_plus = 1.30;
	final static double D = 1.00;
	final static double D_minus = 0.70;
	final static double F = 0;

	@SuppressLint("DefaultLocale")
	public static String getGPA(DatabaseHelper db) {
		double qualityPoints = 0;
		int totalHours = 0;
		ArrayList<Course> list = getCourseList(db);
		if (list.size() == 0)
			return "";

		for (int i = 0; i < list.size(); i++) {
			Course course = list.get(i);
			if (course.grade.equals(""))
				continue;

			totalHours += course.creditHours;
			qualityPoints += getPoints(course.grade, course.creditHours);
		}
		if (totalHours == 0)
			return "";

		String gpa = String.format("%.2f", qualityPoints / totalHours);

		return gpa;

	}

	private static double getPoints(String grade, int creditHours) {
		if ((grade == "F")) {
			return F * creditHours;
		} else if (grade == "D-") {
			return D_minus * creditHours;
		} else if (grade == "D") {
			return D * creditHours;
		} else if (grade == "D+") {
			return D_plus * creditHours;
		} else if (grade == "C-") {
			return C_minus * creditHours;
		} else if (grade == "C") {
			return C * creditHours;
		} else if (grade == "C+") {
			return C_plus * creditHours;
		} else if (grade == "B-") {
			return B_minus * creditHours;
		} else if (grade == "B") {
			return B * creditHours;
		} else if (grade == "B+") {
			return B_plus * creditHours;
		} else if (grade == "A-") {
			return A_minus * creditHours;
		} else if (grade == "A") {
			return A * creditHours;
		} else {
			return A_plus * creditHours;
		}
	}

	private static ArrayList<Course> getCourseList(DatabaseHelper db) {
		ArrayList<Course> list = new ArrayList<Course>();

		Cursor cursor = db.getReadableDatabase().rawQuery(
				"SELECT " + CourseTable.COLUMN_ID + ", "
						+ CourseTable.COLUMN_CREDIT_HOURS + " FROM "
						+ CourseTable.NAME, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String courseId = cursor.getString(0);
			GradeScale gs = GradeScaleTable.getGradeScale(courseId, db);
			String grade = GradeCalculator.getGrade(courseId, gs, db);
			list.add(new Course(courseId, cursor.getString(1), grade));
			cursor.moveToNext();
		}
		cursor.close();

		return list;
	}
}

class Course {
	protected int creditHours;
	protected String id;
	protected String grade;

	public Course(String courseId, String creditHours, String grade) {
		this.id = courseId;
		this.creditHours = Integer.parseInt(creditHours);
		this.grade = grade;

	}
}