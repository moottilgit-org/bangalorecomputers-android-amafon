package com.atoz.johnnysapp.store.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Pref {
	private static String USERID = "USERID";
	private static String FULLNAME = "FULLNAME";
	private static String EMAIL = "EMAIL";
	private static String MOBILE = "MOBILE";
	private static String PASSWORD = "PASSWORD";
	public static String IS_LOGGED_IN = "IS_LOGGED_IN";

	private static String UPI_ID = "UPI_ID";
	private static String UPI_URI = "UPI_URI";
	
	public static Pref prefObj= null;
	Context context;
	SharedPreferences prefs;

	public static Pref init(Context _context) {
		if(prefObj==null) {
			prefObj= new Pref(_context);
		}
		return prefObj;
	}

	public Pref(Context _context) {
		context= _context;
		prefs= PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	public String getString(String prefKey, String defaultVal) {
		return prefs.getString(prefKey, defaultVal).trim();
	}

	public boolean getBoolean(String prefKey, boolean defaultVal) {
		return prefs.getBoolean(prefKey, defaultVal);
	}

	public int getInt(String prefKey, int defaultVal) {
	    return prefs.getInt(prefKey, defaultVal);
	}

	public long getLong(String prefKey, long defaultVal) {
		try { return prefs.getLong(prefKey, defaultVal); }
		catch(Exception e) { return defaultVal; }
	}

	public float getFloat(String prefKey, float defaultVal) {
		try { return prefs.getFloat(prefKey, defaultVal); }
		catch(Exception e) { return defaultVal; }
	}

	public void setString(String prefKey, String value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(prefKey, value.trim());
		editor.apply();
	}

	public void setInt(String prefKey, int value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(prefKey, value);
		editor.apply();
	}

	public void setLong(String prefKey, Long value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(prefKey, value);
		editor.apply();
	}

	public void setFloat(String prefKey, float value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat(prefKey, value);
		editor.apply();
	}

	public void setBoolean(String prefKey, boolean value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(prefKey, value);
		editor.apply();
	}

	public static String getFullName(Context context) {
		try {
			String value= Pref.init(context).getString(FULLNAME, "");
			return value.isEmpty()?"":Encrypter.decryptContent(value);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public static void setFullName(Context context, String value) {
		try {
			Pref.init(context).setString(FULLNAME, Encrypter.encryptContent(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getEmail(Context context) {
		try {
			String value= Pref.init(context).getString(EMAIL, "");
			return value.isEmpty()?"":Encrypter.decryptContent(value);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public static void setEmail(Context context, String value) {
		try {
			Pref.init(context).setString(EMAIL, Encrypter.encryptContent(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getMobile(Context context) {
		try {
			String value= PhoneNumberCleaner.format(Pref.init(context).getString(MOBILE, ""));
			return value.isEmpty()?"":Encrypter.decryptContent(value);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public static void setMobile(Context context, String value) {
		try {
			Pref.init(context).setString(MOBILE, Encrypter.encryptContent(PhoneNumberCleaner.format(value)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getPassword(Context context) {
		try {
			String value= Pref.init(context).getString(PASSWORD, "");
			return value.isEmpty()?"":Encrypter.decryptContent(value);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public static void setPassword(Context context, String value) {
		try {
			Pref.init(context).setString(PASSWORD, Encrypter.encryptContent(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setUID(Context context, int value) {
		try {
			Pref.init(context).setInt(USERID, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static int getUID(Context context) {
		try {
			return Pref.init(context).getInt(USERID, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return 3;
		}
	}


	public static void setUPI(Context context, String value) {
		try {
			Pref.init(context).setString(UPI_ID, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getUPI(Context context) {
		try {
			return Pref.init(context).getString(UPI_ID, "");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}


	public static void setUPIText(Context context, String value) {
		try {
			Pref.init(context).setString(UPI_URI, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getUPIText(Context context) {
		try {
			return Pref.init(context).getString(UPI_URI, "");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}

