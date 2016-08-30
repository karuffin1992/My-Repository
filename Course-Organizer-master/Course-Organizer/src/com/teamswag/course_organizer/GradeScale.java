package com.teamswag.course_organizer;

public class GradeScale {
	protected double  d_minus;
	protected double  d;
	protected double  d_plus;
	protected double  c_minus;
	protected double  c;
	protected double  c_plus;
	protected double  b_minus;
	protected double  b;
	protected double  b_plus;
	protected double  a_minus;
	protected double  a;
	protected double  a_plus;

	public GradeScale(double d_minus, double d, double d_plus, double c_minus,
			double c, double c_plus, double b_minus, double b, double b_plus,
			double a_minus, double a, double a_plus) {

		this.d_minus = d_minus;
		this.d = d;
		this.d_plus = d_plus;
		this.c_minus = c_minus;
		this.c = c;
		this.c_plus = c_plus;
		this.b_minus = b_minus;
		this.b = b;
		this.b_plus = b_plus;
		this.a_minus = a_minus;
		this.a = a;
		this.a_plus = a_plus;
		
	}
}
