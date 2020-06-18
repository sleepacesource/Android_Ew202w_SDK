package com.sdkEw202w.demo.util;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import com.sleepace.sdk.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtill {

	public static boolean isAM(int hour24, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour24);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.get(Calendar.AM_PM) == Calendar.AM;
	}

	public static boolean HourIs24(Context context) {
		boolean b = DateFormat.is24HourFormat(context);
		return b;
	}

	public static byte getHour24(int hour12, int minute, int apm) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.AM_PM, apm);
		cal.set(Calendar.HOUR, hour12);
		cal.set(Calendar.MINUTE, minute);
		byte hour24 = (byte) cal.get(Calendar.HOUR_OF_DAY);

		if (hour24 == 12) {
			hour24 = 0;
		} else if (hour24 == 0) {
			hour24 = 12;
		}

		return hour24;
	}

	public static int getHour12(int hour24) {

		if (hour24 == 0 || hour24 == 12) {
			return 12;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour24);
		return calendar.get(Calendar.HOUR);
	}

	public static String formatMusicTime(int duration) {
		int minute = duration / 60;
		int second = duration % 60;
		return formatMinute(minute, second);
	}

	public static String formatMinute(int hour, int minute) {
		return String.format("%02d:%02d", hour, minute);
	}

	public static String formatMinute(int minute) {
		return String.format("%02d", minute);
	}

	/**
	 * 根据时区获取 对应的 calendar
	 * 
	 * @param tzone
	 * @return
	 */
	public static Calendar getCalendar(float tzone) {
		Calendar c = Calendar.getInstance();
		TimeZone tz = null;
		if (tzone == -100) {
			tzone = getTimeZone();
			tz = TimeZone.getTimeZone(getTimeZone(tzone));
		} else {
			tz = TimeZone.getTimeZone(getTimeZone(tzone));
		}

		c.setTimeZone(tz);
		c.setMinimalDaysInFirstWeek(1);
		c.setFirstDayOfWeek(Calendar.MONDAY);
		// c.setFirstDayOfWeek(Calendar.SUNDAY);

		return c;
	}

	/**
	 * 描述：获取手机TimeZone时区，单位 小时 包含了 夏时令 和 冬时令的 偏移
	 * 
	 * @return
	 */
	public static float getTimeZone() {
		TimeZone tz = TimeZone.getDefault();

		// String s = "TimeZone:"+tz.getDisplayName(false,
		// TimeZone.SHORT)+",seqid:" +tz.getID();

		Calendar calendar = Calendar.getInstance();
		float f = (tz.getRawOffset() + calendar.get(Calendar.DST_OFFSET)) / 1000f / 60f / 60f;

		// LogUtil.showMsg(TAG+" getTimeZone s:"+s+",f:" +f+",str:" +
		// StringUtil.DF_P_2.format(f));

		return (int) (f * 100) / 100f;
	}


	
	/**
	 * 描述：根据时区的值，获取String 时区值
	 * 
	 * @param tzone
	 * @return
	 */
	public static String getTimeZone(float tzone) {
		String timezone = "";
		int zone = (int) tzone;
		int mode = (int) ((tzone * 100) % 100);
		if (mode == 0) {// 整数
			if (tzone >= 0) {
				timezone = "GMT+" + zone;
			} else if (tzone == -100) {
				timezone = "";
			} else {
				timezone = "GMT" + zone;
			}
			// LogUtil.showMsg(TAG+" getTimeZone tzone:" + tzone+",str:" +
			// timezone);
		} else {

			int minute = (int) (60 * (mode / 100f));

			if (tzone >= 0) {
				timezone = "GMT+" + StringUtil.DF_2.format(zone) + ":"
						+ StringUtil.DF_2.format(minute);
			} else if (tzone == -100) {
				timezone = "";
			} else {
				timezone = "GMT" + StringUtil.DF_2.format(zone) + ":"
						+ StringUtil.DF_2.format(minute);
			}

			// LogUtil.showMsg(TAG+" getTimeZone tzone:" + tzone+",str:" +
			// timezone);
		}
		return timezone;
	}
	

	/**
	 *
	 * <h3>获取当前时间的 时间戳</h3>
	 * <ul>
	 *   <li>单位为S</li>
	 * </ul>
	 * @return
	 */
	public static int getCurrentTimeInt()
	{
		Calendar c = getCalendar(-100);
		return (int)(c.getTimeInMillis()/1000);
	}


}
