package ch.squash.simulation.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import ch.squash.simulation.R;
import ch.squash.simulation.main.SquashActivity;
import ch.squash.simulation.main.SquashRenderer;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.common.Vector;

public final class Settings {
	// static members
	private static final String TAG = Settings.class.getSimpleName();
	private static Settings mInstance;
	private static final Object LOCK = new Object();

	// instance members
	private final SharedPreferences mSharedPrefs;
	
	private final String KEY_DRAW_MODE;
	private final String KEY_SELECT_OBECTS;
	private final String KEY_CAMERA_MODE;
	private final String KEY_RESET;

	private final String KEY_CAMERA_POSITION_X;
	private final String KEY_CAMERA_POSITION_Y;
	private final String KEY_CAMERA_POSITION_Z;
	private final String KEY_BALL_POSITION_X;
	private final String KEY_BALL_POSITION_Y;
	private final String KEY_BALL_POSITION_Z;
	private final String KEY_BALL_SPEED_X;
	private final String KEY_BALL_SPEED_Y;
	private final String KEY_BALL_SPEED_Z;
	
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

	public static String getKeyCameraPositionX(){
		return getInstance().KEY_CAMERA_POSITION_X;
	}
	public static String getKeyCameraPositionY(){
		return getInstance().KEY_CAMERA_POSITION_Y;
	}
	public static String getKeyCameraPositionZ(){
		return getInstance().KEY_CAMERA_POSITION_Z;
	}
	public static String getKeyBallPositionX(){
		return getInstance().KEY_BALL_POSITION_X;
	}
	public static String getKeyBallPositionY(){
		return getInstance().KEY_BALL_POSITION_Y;
	}
	public static String getKeyBallPositionZ(){
		return getInstance().KEY_BALL_POSITION_Z;
	}
	public static String getKeyBallSpeedX(){
		return getInstance().KEY_BALL_SPEED_X;
	}
	public static String getKeyBallSpeedY(){
		return getInstance().KEY_BALL_SPEED_Y;
	}
	public static String getKeyBallSpeedZ(){
		return getInstance().KEY_BALL_SPEED_Z;
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

		KEY_CAMERA_POSITION_X = SquashActivity.getInstance().getResources()
				.getString(R.string.key_camera_position_x);
		KEY_CAMERA_POSITION_Y = SquashActivity.getInstance().getResources()
				.getString(R.string.key_camera_position_y);
		KEY_CAMERA_POSITION_Z = SquashActivity.getInstance().getResources()
				.getString(R.string.key_camera_position_z);
		KEY_BALL_POSITION_X = SquashActivity.getInstance().getResources()
				.getString(R.string.key_ball_position_x);
		KEY_BALL_POSITION_Y = SquashActivity.getInstance().getResources()
				.getString(R.string.key_ball_position_y);
		KEY_BALL_POSITION_Z = SquashActivity.getInstance().getResources()
				.getString(R.string.key_ball_position_z);
		KEY_BALL_SPEED_X = SquashActivity.getInstance().getResources()
				.getString(R.string.key_ball_speed_x);
		KEY_BALL_SPEED_Y = SquashActivity.getInstance().getResources()
				.getString(R.string.key_ball_speed_y);
		KEY_BALL_SPEED_Z = SquashActivity.getInstance().getResources()
				.getString(R.string.key_ball_speed_z);

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
	public static Object getValue(final String key){
		final Map<String,?> map = getInstance().mSharedPrefs.getAll();
		return map.get(key);
	}
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
	
	public static IVector getCameraPosition(){
		return new Vector(
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_CAMERA_POSITION_X, "0")),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_CAMERA_POSITION_Y, "-1.5")),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_CAMERA_POSITION_Z, "2.75")));
	}

	public static IVector getBallStartPosition(){
		return new Vector(
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_POSITION_X, "-2")),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_POSITION_Y, "1")),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_POSITION_Z, "0")));
	}
	
	public static IVector getBallStartSpeed(){
		return new Vector(
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_SPEED_X, "3")),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_SPEED_Y, "1")),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_SPEED_Z, "-1")));
	}
}
