package com.dqqdo.dobase;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AUTHER：Tyrion
 * DATE  ：2015/11/17
 * DESCRIPTION: 日志输出工具类
 */
public class DoLog {
	
	// 是否打印日志的总开关
	private final static boolean isDebug = true;
//	private final static boolean isDebug = false;
	// log输出tag
	private final static String TAG = "dqqdo";


	public static boolean isDebug() {
		return isDebug;
	}

	/**
	 * debug级别 log输出函数
	 * @param logstr log内容
	 */
	public static void d(String logstr){
		if(isDebug){
			Log.d(TAG, logstr);
		}
	}

	/**
	 * debug级别 log输出函数
	 * @param logstr log内容
	 */
	public static void d(String tag, String logstr){
		if(isDebug){
			Log.d(tag, logstr);
		}
	}

	
	/**
	 * info 级别的日志
	 * @param logstr
	 */
	public static void i(String logstr){
		if(isDebug){
			Log.i(TAG, logstr);
		}
	}
	
	/**
	 * error级别 log输出函数
	 * @param logstr log内容
	 */
	public static void e(String logstr){
		if(isDebug){
			Log.e(TAG, logstr);
		}
		
	}
	
	/**
	 * error级别 log输出函数
	 * @param logstr log内容
	 */
	public static void e(Object logstr){
		if(isDebug){
			Log.e(TAG, logstr.toString());
		}
		
	}
	
	
	/**
	 * 打印debug 时间描述
	 * @param des 描述信息
	 */
	@SuppressLint("SimpleDateFormat")
	public static void logDebugTime(String des) {

		long longSystemTime = System.currentTimeMillis();

		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd-hh:mm:ss");
		String formatDate = sDateFormat.format(new Date(longSystemTime));
		
		DoLog.d("debug: des =" + des + "时间:" + formatDate + "  毫秒数:" + longSystemTime);
		
	}
	
}
