package com.sdkEw202w.demo;

import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.util.CrashHandler;
import com.sleepace.sdk.util.SdkLog;

import android.app.Application;

public class DemoApp extends Application {

	public static final String APP_TAG = "Ew202wSdk";
	public static String TOKEN = "r8xfa7hdjcm6";
	public static final String SERVER_HOST = "http://172.14.0.111:8082";
	public static final String THIED_PLATFORM = "13700";
	public static final String CHNANEL_ID = "13700";
	public static final int[][] ALARM_MUSIC = { { 31098, R.string.alarm_list_1 }, { 31099, R.string.alarm_list_2 },
	        { 31100, R.string.dididi }, { 31101, R.string.music_box }, { 31102, R.string.alarm_list_9 }, { 31103, R.string.alarm_list_7 } };
	public static final String ip = "172.14.0.111";
	public static final int port = 29010;
	// public static final int[][] SLEEPAID_MUSIC = { { 30001,
	// R.string.music_list_sea }, { 30002, R.string.music_list_sun }, { 30003,
	// R.string.music_list_dance }, { 30004, R.string.music_list_star },
	// { 30005, R.string.music_list_solo }, { 30006, R.string.music_list_rain },
	// { 30007, R.string.music_list_wind }, { 30008, R.string.music_list_summer
	// } };

	public static final int[][] SLEEPAID_MUSIC = { { 31086, R.string.music_list_You }, { 31087, R.string.music_list_Moon },
	        { 31088, R.string.music_list_Here }, { 31089, R.string.music_list_Journey }, { 31090, R.string.music_list_World },
	        { 31091, R.string.music_list_Baby }, { 31092, R.string.music_list_Lullaby }, { 31093, R.string.music_list_star },
	        { 31094, R.string.music_list_sun }, { 31095, R.string.music_list_sea }, { 31096, R.string.music_list_rain },
	        { 31097, R.string.music_list_dance } };
	private static DemoApp instance;

	private static long seqId = 0;

	public synchronized static long getSeqId() {
		seqId++;
		if (seqId >= Long.MAX_VALUE) {
			seqId = 1;
		}
		return seqId;
	}

	public static DemoApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		CrashHandler.getInstance().init(this);
		SdkLog.setLogEnable(true);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		// 低内存的时候执行
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		// 程序在内存清理的时候执行
		super.onTrimMemory(level);

	}

}
