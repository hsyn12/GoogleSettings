package com.setting.dl.google.googlesettingupdate;

import android.util.Log;

import java.util.Hashtable;

/**
 * The class for print log
 *
 * @author kesenhoo
 */
public class MyLogger {
	
	private final static boolean logFlag = true;
	
	private final static String                      TAG          = "[GSetting]";
	private final static int                         logLevel     = Log.VERBOSE;
	private static       Hashtable<String, MyLogger> sLoggerTable = new Hashtable<>();
	private              String                      mClassName;
	private static       MyLogger                    jlog;
	private static       MyLogger                    klog;
	private static final String                      HSYN         = "[hsyn] ";
	private static final String                      KESEN        = "@kesen@ ";
	
	private MyLogger(String name) {
		mClassName = name;
	}
	
	/**
	 * @param className class name
	 * @return MyLogger
	 */
	@SuppressWarnings("unused")
	private static MyLogger getLogger(String className) {
		MyLogger classLogger = sLoggerTable.get(className);
		if (classLogger == null) {
			classLogger = new MyLogger(className);
			sLoggerTable.put(className, classLogger);
		}
		return classLogger;
	}
	
	/**
	 * Purpose:Mark user one
	 *
	 * @return logger
	 */
	public static MyLogger kLog() {
		if (klog == null) {
			klog = new MyLogger(KESEN);
		}
		return klog;
	}
	
	/**
	 * Purpose:Mark user two
	 *
	 * @return logger
	 */
	public static MyLogger jLog() {
		if (jlog == null) {
			jlog = new MyLogger(HSYN);
		}
		return jlog;
	}
	
	/**
	 * Get The Current Function Name
	 *
	 * @return
	 */
	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
			return mClassName + "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":" + st.getLineNumber() + " " + st.getMethodName() + " ]";
		}
		return null;
	}
	
	/**
	 * The Log Level:i
	 *
	 * @param str
	 */
	public void i(Object str, Object... args) {
		if (logFlag) {
			if (logLevel <= Log.INFO) {
				String name = getFunctionName();
				
				try {
					
					if (name != null) {
						Log.i(TAG, name + " - " + String.format(str.toString(), args));
					}
					else {
						Log.i(TAG, str.toString());
					}
					
				}
				catch (Exception e) {
					
					this.e(e.toString());
					Log.i(TAG, str.toString());
				}
			}
		}
		
	}
	
	/**
	 * The Log Level:d
	 *
	 * @param str
	 */
	public void d(Object str, Object... args) {
		if (logFlag) {
			if (logLevel <= Log.DEBUG) {
				String name = getFunctionName();
				
				try {
					
					if (name != null) {
						Log.d(TAG, name + " - " + String.format(str.toString(), args));
					}
					else {
						Log.d(TAG, str.toString());
					}
					
				}
				catch (Exception e) {
					
					this.e(e.toString());
					Log.d(TAG, str.toString());
				}
			}
		}
	}
	
	/**
	 * The Log Level:V
	 *
	 * @param str
	 */
	public void v(Object str) {
		if (logFlag) {
			if (logLevel <= Log.VERBOSE) {
				String name = getFunctionName();
				
				try {
					
					if (name != null) {
						Log.v(TAG, name + " - " + str);
					}
					else {
						Log.v(TAG, str.toString());
					}
				}
				catch (Exception e) {
					
					this.e(e.toString());
				}
				
			}
		}
	}
	
	/**
	 * The Log Level:w
	 *
	 * @param str
	 */
	public void w(Object str, Object... args) {
		if (logFlag) {
			
			if (logLevel <= Log.WARN) {
				
				String name = getFunctionName();
				
				try {
					
					if (name != null) {
						Log.w(TAG, name + " - " + String.format(str.toString(), args));
					}
					else {
						Log.w(TAG, str.toString());
					}
					
				}
				catch (Exception e) {
					
					this.e(e.toString());
					Log.w(TAG, str.toString());
				}
			}
		}
	}
	
	/**
	 * The Log Level:e
	 *
	 * @param str str
	 */
	public void e(Object str, Object... args) {
		if (logFlag) {
			if (logLevel <= Log.ERROR) {
				
				String name = getFunctionName();
				
				try {
					
					if (name != null) {
						Log.e(TAG, name + " - " + String.format(str.toString(), args));
					}
					else {
						Log.e(TAG, str.toString());
					}
					
				}
				catch (Exception e) {
					
					this.e(e.toString());
					Log.e(TAG, str.toString());
				}
				
			}
		}
	}
	
	/**
	 * The Log Level:e
	 *
	 * @param ex
	 */
	public void e(Exception ex) {
		if (logFlag) {
			if (logLevel <= Log.ERROR) {
				Log.e(TAG, "error", ex);
			}
		}
	}
	
	/**
	 * The Log Level:e
	 *
	 * @param log
	 * @param tr
	 */
	public void e(String log, Throwable tr) {
		if (logFlag) {
			String line = getFunctionName();
			Log.e(TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName + line + ":] " + log + "\n", tr);
		}
	}
}