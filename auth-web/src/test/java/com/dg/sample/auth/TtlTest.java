package com.dg.sample.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

public class TtlTest {

	@Test
	public void testExpiration() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, 2017);
		cal.set(Calendar.MONTH, 12);
		cal.set(Calendar.DAY_OF_MONTH, 13);
		cal.set(Calendar.HOUR_OF_DAY, 14);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		// Move one day ahead (to the 14h)
		Calendar calExpected = GregorianCalendar.getInstance();
		calExpected.setTimeInMillis(cal.getTimeInMillis());
		calExpected.set(Calendar.DAY_OF_MONTH, 14);
		
		// Add 24h to the initial time
		cal.add(Calendar.HOUR_OF_DAY, 24);
		
		assertThat(calExpected.getTimeInMillis() == cal.getTimeInMillis(), is(true));
	}
}
