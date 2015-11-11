package net.bhp.conferencetext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConferenceDateHelper {


	public static ConferenceDate getFirst() {
		return new ConferenceDate(ConferenceDate.START_YEAR, Mode.Start);
	}

	public static ConferenceDate getMostRecent() {
		return new ConferenceDate(ConferenceDate.START_YEAR, Mode.End);
	}
	
	public static Integer getYearBasedOnMonth(Integer year, Integer month) {
		return month < ConferenceMonth.April.monthNumber() ? year - 1 : year;
	}
	
	public static Set<Integer> getFullYearRange() {
		Calendar cal = GregorianCalendar.getInstance();
		return IntStream.range(ConferenceDate.START_YEAR, (getYearBasedOnMonth(cal.get(Calendar.YEAR), ConferenceMonth.getLatest(cal.get(Calendar.MONTH)).monthNumber()))).boxed().collect(Collectors.toSet());
	}
	
	public static List<ConferenceDate> generateConferenceDateRange(ConferenceDate startDate, ConferenceDate endDate) {
		Set<Integer> yearRange = IntStream.range(startDate.getIntegerYear(), endDate.getIntegerYear()).boxed().collect(Collectors.toSet());
		List<ConferenceDate> cds = new ArrayList<>();
		yearRange.stream().forEach(y -> {
			cds.add(new ConferenceDate(Collections.min(yearRange), y, ConferenceMonth.April));
			if (endDate.getMonth() == ConferenceMonth.October) {
	            cds.add(new ConferenceDate(Collections.min(yearRange), y, ConferenceMonth.October));
            }
		});
		return cds;
	}
	
	public static List<ConferenceDate> generateConferenceDateRange(Integer startYear, Integer endYear) {
		Set<Integer> yearRange = IntStream.range(startYear, endYear).boxed().collect(Collectors.toSet());
		List<ConferenceDate> cds = new ArrayList<>();
		yearRange.stream().forEach(y -> {
			cds.add(new ConferenceDate(Collections.min(yearRange), y, ConferenceMonth.April));
			cds.add(new ConferenceDate(Collections.min(yearRange), y, ConferenceMonth.October));
		});
		return cds;
	}
	
	public static List<ConferenceDate> generateAllConferenceDates() {
		return generateConferenceDateRange(getFirst(), getMostRecent());
	}
}
