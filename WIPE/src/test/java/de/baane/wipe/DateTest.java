package de.baane.wipe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import de.baane.wipe.util.DateUtil;

public class DateTest {
	
	/**
	 * Check if Date is 7 day ago or behind Wednesday.
	 */
	@Test
	public void testWednesdayPast() {
		printTestMethod("Wednesday Past");
		
		Calendar saveDate = Calendar.getInstance();
		saveDate.set(2014, 7, 12);
		System.out.println("SaveDate:: "+saveDate.getTime());
		
		Calendar loginDate = Calendar.getInstance();
		
		loginDate.set(2014, 7, 16);
		System.out.println("LoginDate:: "+loginDate.getTime());
		assertTrue(DateUtil.resetIds(saveDate.getTime(), loginDate.getTime()));
		
		loginDate.set(2014, 8, 12);
		System.out.println("LoginDate:: "+loginDate.getTime());
		assertTrue(DateUtil.resetIds(saveDate.getTime(), loginDate.getTime()));
	}
	
	/**
	 * Check if Wednesday is SaveDate. Do not reset SaveDate.
	 */
	@Test
	public void testWednesdaySaveDate() {
		printTestMethod("Wednesday SaveDate");
		
		Calendar saveDate = Calendar.getInstance();
		saveDate.set(2014, 7, 13, 7, 0); // 7 a.m. on Wednesday
		System.out.println("SaveDate:: "+saveDate.getTime());
		
		Calendar loginDate = Calendar.getInstance();
		loginDate.set(2014, 7, 13, 10, 0); // 10 a.m. on Wednesday
		System.out.println("LoginDate:: "+loginDate.getTime());
		
		assertFalse(DateUtil.resetIds(saveDate.getTime(), loginDate.getTime()));
	}
	
	/**
	 * Reset on 6 a.m. on Wednesday. Check time before that on same Wednesday.
	 */
	@Test
	public void testWednesdayMidnight() {
		printTestMethod("Wednesday Midnight");
		
		Calendar saveDate = Calendar.getInstance();
		saveDate.set(2014, 11, 10, 1, 0); // 1 a.m. on Wednesday
		System.out.println("SaveDate:: "+saveDate.getTime());
		
		
		Calendar loginDate = Calendar.getInstance();
		loginDate.set(2014, 11, 10, 8, 0); // 8 a.m. on Wednesday
		System.out.println("LoginDate:: "+loginDate.getTime());
		
		assertTrue(DateUtil.resetIds(saveDate.getTime(), loginDate.getTime()));
	}
	/**
	 * Lunch Time on Wednesday.
	 */
	@Test
	public void testWednesdayLunch() {
		printTestMethod("Wednesday lunch time");
		
		Calendar saveDate = Calendar.getInstance();
		saveDate.set(2015, 4, 20, 2, 0); // 2 a.m. on Wednesday
		System.out.println("SaveDate:: "+saveDate.getTime());
		
		
		Calendar loginDate = Calendar.getInstance();
		loginDate.set(2015, 4, 20, 13, 0); // 1 p.m. on Wednesday
		System.out.println("LoginDate:: "+loginDate.getTime());
		
		assertTrue(DateUtil.resetIds(saveDate.getTime(), loginDate.getTime()));
	}

	/**
	 * Check if last save date is before last Wednesday.
	 */
	@Test
	public void testSaveDateBeforeWednesday() {
		printTestMethod("Save Date Before Wednesday");
		
		Calendar saveDate = Calendar.getInstance();
		saveDate.set(2014, 8, 30);
		System.out.println("SaveDate:: "+saveDate.getTime());
		
		
		Calendar loginDate = Calendar.getInstance();
		loginDate.set(2014, 9, 2);
		System.out.println("LoginDate:: "+loginDate.getTime());
		
		assertTrue(DateUtil.resetIds(saveDate.getTime(), loginDate.getTime()));
	}

	@Test
	public void testTuesdayEvening() {
		printTestMethod("Tuesday Evening");
		
		Calendar saveDate = Calendar.getInstance();
		saveDate.set(2014, 11, 9, 23, 0);
		System.out.println("SaveDate:: "+saveDate.getTime());
		
		
		Calendar loginDate = Calendar.getInstance();
		loginDate.set(2014, 11, 10, 7, 0);
		System.out.println("LoginDate:: "+loginDate.getTime());
		
		assertTrue(DateUtil.resetIds(saveDate.getTime(), loginDate.getTime()));
	}

	@Test
	public void testTuesdayAfternoon() {
		printTestMethod("Tuesday Afternoon");
		
		Calendar saveDate = Calendar.getInstance();
		saveDate.set(2014, 11, 2, 14, 0);
		System.out.println("SaveDate:: "+saveDate.getTime());
		
		
		Calendar loginDate = Calendar.getInstance();
		loginDate.set(2014, 11, 3, 23, 0);
		System.out.println("LoginDate:: "+loginDate.getTime());
		
		assertTrue(DateUtil.resetIds(saveDate.getTime(), loginDate.getTime()));
	}
	
	private void printTestMethod(String method) {
		System.out.println();
		System.out.println("++++++++++++++++++++++++++++++++");
		System.out.println("+"+method+"+");
		System.out.println("++++++++++++++++++++++++++++++++");
	}
	
}
