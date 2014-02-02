package ch.squash.ghosting.setting;

import ch.squash.ghosting.main.MainActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera.Size;
import android.preference.PreferenceManager;
import android.util.Log;

final public class Settings {
	private final static String TAG = Settings.class.getSimpleName();
	// constants
	public static final String VERSION = "0.1";

	// preferences
	private static SharedPreferences mPreferences = PreferenceManager
			.getDefaultSharedPreferences(MainActivity.getActivity());

	public static boolean setKeyValue(final String key, final String value){
		return setKeyValue(key, value, String.class);
	}
	public static boolean setKeyValue(final String key, final int value){
		return setKeyValue(key, value, int.class);
	}
	public static boolean setKeyValue(final String key, final Object value,
			@SuppressWarnings("rawtypes") final Class classType) {
		final Editor editor = mPreferences.edit();

		Log.d(TAG, "Writing new value for setting " + key + ": " + value);
		
		if (classType.equals(boolean.class))
			editor.putBoolean(key, (Boolean)value);
		else if (classType.equals(String.class))
			editor.putString(key, (String) value);
		else if (classType.equals(int.class))
			editor.putString(key, value.toString());
		else if (classType.equals(Size.class))
			editor.putString(key, Integer.toString(((Size) value).width) + "x"
					+ ((Size) value).height);
		else {
			Log.e(TAG,
					"Couldnt write value: unknonwn class " + classType.getSimpleName());
			return false;
		}

		editor.commit();
		return true;
	}

	public static SharedPreferences getSharedPreferences() {
		return mPreferences;
	}
	public static int getSeries(){
		return Integer.parseInt(mPreferences.getString("series", "5"));
	}
	public static int getBreak(){
		return Integer.parseInt(mPreferences.getString("break", "30"));
	}
	public static int getCornerCount(){
		return Integer.parseInt(mPreferences.getString("cornercount", "30"));
	}
	public static int getCornerTime(){
		return Integer.parseInt(mPreferences.getString("time", "60"));
	}
	public static boolean isCornersOrTime(){
		return mPreferences.getBoolean("cornersortime", true);
	}
	public static String getCorners(){
		return mPreferences.getString("corners", "10");
	}
	public static String getMinTime(){
		return mPreferences.getString("mintime", "2.5");
	}
	public static String getMaxTime(){
		return mPreferences.getString("maxtime", "3.25");
	}
	public static String getFrontTime(){
		return mPreferences.getString("fronttime", "0.75");
	}
	public static String getBackTime(){
		return mPreferences.getString("backtime", "0.5");
	}
	
	private Settings(){
		
	}
}
