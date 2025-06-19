package com.akatsuki.newsum.common.util;

import java.time.LocalDateTime;

public class DateUtil {

	public static String localDateTimeToDateString(LocalDateTime localDateTime) {
		return localDateTime.toLocalDate().toString();
	}

	public static LocalDateTime beforeDaysFromToday(int days) {
		return LocalDateTime.now().minusDays(days).withHour(0).withMinute(0).withSecond(0).withNano(0);
	}
}
