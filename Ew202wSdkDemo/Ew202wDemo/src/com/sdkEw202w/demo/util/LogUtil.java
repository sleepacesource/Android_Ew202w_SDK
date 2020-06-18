package com.sdkEw202w.demo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import com.sdkEw202w.demo.DemoApp;
import com.sleepace.sdk.util.StringUtil;

import android.os.Environment;
import android.util.Log;

public class LogUtil {

	public static final String LOG_DIR = Environment.getExternalStorageDirectory() + "/" + DemoApp.APP_TAG + "/Log";

	public synchronized static void log(Object obj) {
		String msg = obj.toString();
		Log.i(DemoApp.APP_TAG, msg);
		//saveLog(StringUtil.SIMPLE_DATE_FORMAT.format(new Date())+".log", msg);
	}

	private static boolean saveLog(String filename, String log) {
		try {
			File dir = new File(LOG_DIR);
			if (!dir.exists()) {
				boolean res = dir.mkdirs();
				if (!res) {
					return false;
				}
			}

			File file = new File(dir, filename);
			FileOutputStream fos = new FileOutputStream(file, true);

			String time = StringUtil.DATE_FORMAT.format(new Date());
			log = time + "|" + log;

			fos.write(log.getBytes());
			fos.write("\r\n".getBytes());
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static String getCaller() {
		/*String className = Thread.currentThread().getStackTrace()[2].getClassName();
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		return className + "\n" + methodName + "\n" + lineNumber;*/
		
		StringBuffer sb = new StringBuffer();
		
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            sb.append(e.getClassName() + "\t" + e.getMethodName() + "\t" + e.getLineNumber());
            sb.append("\n");
        }
        
        StackTraceElement log = stackTrace[1];
        String tag = null;
        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement e = stackTrace[i];
            tag = e.getClassName() + "." + e.getMethodName();
            sb.append(tag);
            sb.append("\n");
            /*if (!e.getClassName().equals(log.getClassName())) {
                break;
            }*/
        }
        
        return sb.toString();
	}
}














