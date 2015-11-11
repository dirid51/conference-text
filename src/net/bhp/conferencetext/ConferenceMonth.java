package net.bhp.conferencetext;

import java.util.Calendar;

public enum ConferenceMonth {
	April ("04", 3),
	October ("10", 9);
	
	private final String monthIndicator;
	private final int monthNumber;
	
	ConferenceMonth(String monthIndicator, int monthNumber) {
		this.monthIndicator = monthIndicator;
		this.monthNumber = monthNumber;
	}
	
	public String monthIndicator() {
		return this.monthIndicator;
	}
	
	public int monthNumber() {
		return this.monthNumber;
	}
	
	public static ConferenceMonth getLatest(int currentMonth) {
		if (currentMonth < Calendar.APRIL || currentMonth > Calendar.OCTOBER) {
			return October;
		} else {
			return April;
		}
	}
}
