package ch.squash.simulation.common;

import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import ch.squash.simulation.R;
import ch.squash.simulation.main.SquashActivity;
import ch.squash.simulation.main.SquashRenderer;

public final class Settings {
	// static members
	private static final String TAG = Settings.class.getSimpleName();
	private static Settings mInstance;
	private static final Object LOCK = new Object();

	// instance members
	private final String KEY_DRAW_MODE;
	private final String KEY_SELECT_OBECTS;
	private final String KEY_CAMERA_MODE;
	private final String KEY_RESET;
	private final SharedPreferences mSharedPrefs;
	
	public static String getKeyDrawMode(){
		return getInstance().KEY_DRAW_MODE;
	}
	public static String getKeySelectObjects(){
		return getInstance().KEY_SELECT_OBECTS;
	}
	public static String getKeyCameraMode(){
		return getInstance().KEY_CAMERA_MODE;
	}
	public static String getKeyReset(){
		return getInstance().KEY_RESET;
	}
	
	private Settings() {
		mSharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(SquashActivity.getInstance());

		KEY_DRAW_MODE = SquashActivity.getInstance().getResources()
				.getString(R.string.key_draw_mode);
		KEY_SELECT_OBECTS = SquashActivity.getInstance().getResources()
				.getString(R.string.key_select_objects);
		KEY_CAMERA_MODE = SquashActivity.getInstance().getResources()
				.getString(R.string.key_camera_mode);
		KEY_RESET = SquashActivity.getInstance().getResources()
				.getString(R.string.key_reset);

		Log.i(TAG, "Settings initialized");
	}

	private static Settings getInstance() {
		synchronized (LOCK) {
			if (mInstance == null)
				mInstance = new Settings();
		}
		return mInstance;
	}

	public static void setBoolean(final String key, final boolean value) {
		final Editor editor = getInstance().mSharedPrefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	// access
	public static int getDrawMode() {
		return Integer.parseInt(getInstance().mSharedPrefs.getString(
				getInstance().KEY_DRAW_MODE, "-1"));
	}

	public static Set<String> getVisibleObjectCollections() {
		return getInstance().mSharedPrefs.getStringSet(
				getInstance().KEY_SELECT_OBECTS, new HashSet<String>());
	}

	public static void setVisibleObjectCollections(final Set<String> collections) {
		final Editor editor = getInstance().mSharedPrefs.edit();
		editor.putStringSet(getInstance().KEY_SELECT_OBECTS, collections);
		editor.commit();
	}

	public static boolean isObjectCollectionVisible(final int ocId) {
		return getInstance().mSharedPrefs.getStringSet(
				getInstance().KEY_SELECT_OBECTS, new HashSet<String>())
				.contains(Integer.toString(ocId));
	}

	public static boolean isCameraRotating() {
		return getInstance().mSharedPrefs.getString(
				getInstance().KEY_CAMERA_MODE, "0").equals("0");
	}

	public static boolean isReset() {
		return getInstance().mSharedPrefs.getBoolean(getInstance().KEY_RESET,
				false);
	}

	public static int getCameraMode() {
		return Integer.parseInt(getInstance().mSharedPrefs.getString(
				getInstance().KEY_CAMERA_MODE, "0"));
	}

	public static void setCameraMode(final int mode) {
		final Editor editor = getInstance().mSharedPrefs.edit();
		editor.putString(getInstance().KEY_CAMERA_MODE, Integer.toString(mode));
		editor.commit();
	}

	public static boolean isDrawForces() {
		return getInstance().mSharedPrefs.getStringSet(
				getInstance().KEY_SELECT_OBECTS, new HashSet<String>())
				.contains(Integer.toString(SquashRenderer.OBJECT_FORCE));
	}
}
