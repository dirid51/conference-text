package net.bhp.conferencetext;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class ConferenceDate {

	public static final Integer START_YEAR = 1971;

	private String year; // four-digit year
	private ConferenceMonth month; // two-digit month - 04 or 10
	private int startYear;

	public ConferenceDate(int startYear) {
		this.startYear = startYear;
	}

	public ConferenceDate(int startYear, Mode mode) {
		this.startYear = startYear;
		if (mode == Mode.Start) {
			this.year = Integer.toString(startYear);
			this.month = ConferenceMonth.April;
		} else if (mode == Mode.End) {
			Calendar cal = GregorianCalendar.getInstance();
			this.month = ConferenceMonth.getLatest(cal.get(Calendar.MONTH));
			this.year = Integer.toString(ConferenceDateHelper.getYearBasedOnMonth(cal.get(Calendar.YEAR), this.month.monthNumber()));
		} else if (mode == Mode.Random) {
			this.month = (ConferenceMonth.values()[(int) Math.round(Math.random())]);
			Set<Integer> allYears = ConferenceDateHelper.getFullYearRange();
			this.year = Integer.toString((int)Math.round((Collections.max(allYears) - START_YEAR) * Math.random()) + START_YEAR);
		} else if (mode == Mode.None) {
			this.month = null;
			this.year = null;
		}
	}

	public ConferenceDate(int startYear, String year, ConferenceMonth month) {
		this.startYear = startYear;
		this.year = year;
		this.month = month;
	}

	public ConferenceDate(Integer startYear, Integer year, ConferenceMonth month) {
		this.startYear = startYear;
		this.year = Integer.toString(year);
		this.month = month;
	}

	public ConferenceDate(int startYear, int year, int month) {
		this.startYear = startYear;
		this.setYear(Integer.toString(year));
		this.setMonth(month);
	}

	public String getYear() {
		return year;
	}

	public Integer getIntegerYear() {
		return Integer.parseInt(year);
	}

	public void setYear(String year) {
		if (year != null && year.length() == 4 && StringUtils.isNumeric(year) && Integer.parseInt(year) >= this.startYear) {
			this.year = year;
		} else {
			this.year = null;
		}
	}

	public ConferenceMonth getMonth() {
		return month;
	}

	public Integer getIntegerMonth() {
		return month.monthNumber();
	}

	public void setMonth(int month) {
		this.month = ConferenceMonth.getLatest(month);
	}
	
	public String toDateString() {
		return this.year + "/" + this.month.monthIndicator();
	}

}
