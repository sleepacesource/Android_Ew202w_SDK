package com.sdkEw202w.demo;

import com.sdkEw202w.demo.util.CrashHandler;
import com.sleepace.sdk.util.SdkLog;

import android.app.Application;

public class DemoApp extends Application {

	public static final String APP_TAG = "Ew202wSdk";
	public static String TOKEN = "test01";
	public static final String SERVER_HOST = "http://120.24.68.136:8091";
	public static final String THIED_PLATFORM = "53500";
	public static final String CHNANEL_ID = "53500";
	
	public static final int[][] ALARM_MUSIC = { { 31143, R.string.alarm_list_1 }, { 31144, R.string.alarm_list_2 },
	        { 31145, R.string.dididi }, { 31146, R.string.music_box }, { 31147, R.string.alarm_list_9 }, { 31148, R.string.alarm_list_7 } };
	// public static final int[][] SLEEPAID_MUSIC = { { 30001,
	// R.string.music_list_sea }, { 30002, R.string.music_list_sun }, { 30003,
	// R.string.music_list_dance }, { 30004, R.string.music_list_star },
	// { 30005, R.string.music_list_solo }, { 30006, R.string.music_list_rain },
	// { 30007, R.string.music_list_wind }, { 30008, R.string.music_list_summer
	// } };

	public static final int[][] SLEEPAID_MUSIC = { { 31131, R.string.music_list_You }, { 31132, R.string.music_list_Moon },
	        { 31133, R.string.music_list_Here }, { 31134, R.string.music_list_Journey }, { 31135, R.string.music_list_World },
	        { 31136, R.string.music_list_Baby }, { 31137, R.string.music_list_Lullaby }, { 31138, R.string.music_list_star },
	        { 31139, R.string.music_list_sun }, { 31140, R.string.music_list_sea }, { 31141, R.string.music_list_rain },
	        { 31142, R.string.music_list_dance } };
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
		SdkLog.setLogEnable(true);
		SdkLog.setSaveLog(true, "log.txt");
		CrashHandler.getInstance().init(this);
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
