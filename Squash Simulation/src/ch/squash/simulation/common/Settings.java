package ch.squash.simulation.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
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
	public static void setString(final String key, final String value) {
		final Editor editor = getInstance().mSharedPrefs.edit();
		editor.putString(key, value);
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
		final IVector result;
		try{
			result = new Vector(
					Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_CAMERA_POSITION_X, 
							SquashActivity.getInstance().getResources().getString(R.string.default_camera_position_x))),
					Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_CAMERA_POSITION_Y,
							SquashActivity.getInstance().getResources().getString(R.string.default_camera_position_y))),
					Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_CAMERA_POSITION_Z, 
							SquashActivity.getInstance().getResources().getString(R.string.default_camera_position_z))));
		} catch (NumberFormatException nfe){
			Log.e(TAG, "Cannot parse float:", nfe);
			setString(getInstance().KEY_CAMERA_POSITION_X, SquashActivity.getInstance().getResources().getString(R.string.default_camera_position_x));
			setString(getInstance().KEY_CAMERA_POSITION_Y, SquashActivity.getInstance().getResources().getString(R.string.default_camera_position_y));
			setString(getInstance().KEY_CAMERA_POSITION_Z, SquashActivity.getInstance().getResources().getString(R.string.default_camera_position_z));
			Toast.makeText(SquashActivity.getInstance(), "Reset camera position", Toast.LENGTH_SHORT).show();
			return getCameraPosition();
		}
		return result;
	}

	public static IVector getBallStartPosition(){
		final IVector result; 
		try{
			result = new Vector(
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_POSITION_X,
						SquashActivity.getInstance().getResources().getString(R.string.default_ball_position_x))),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_POSITION_Y,
						SquashActivity.getInstance().getResources().getString(R.string.default_ball_position_y))),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_POSITION_Z,
						SquashActivity.getInstance().getResources().getString(R.string.default_ball_position_z))));
		} catch (NumberFormatException nfe){
			Log.e(TAG, "Cannot parse float:", nfe);
			setString(getInstance().KEY_BALL_POSITION_X, SquashActivity.getInstance().getResources().getString(R.string.default_ball_position_x));
			setString(getInstance().KEY_BALL_POSITION_Y, SquashActivity.getInstance().getResources().getString(R.string.default_ball_position_y));
			setString(getInstance().KEY_BALL_POSITION_Z, SquashActivity.getInstance().getResources().getString(R.string.default_ball_position_z));
			Toast.makeText(SquashActivity.getInstance(), "Reset ball position", Toast.LENGTH_SHORT).show();
			return getBallStartPosition();
		}
		return result;
	}
	
	public static IVector getBallStartSpeed(){
		final IVector result; 
		try{
			result = new Vector(
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_SPEED_X,
						SquashActivity.getInstance().getResources().getString(R.string.default_ball_speed_x))),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_SPEED_Y,
						SquashActivity.getInstance().getResources().getString(R.string.default_ball_speed_y))),
				Float.parseFloat(getInstance().mSharedPrefs.getString(getInstance().KEY_BALL_SPEED_Z,
						SquashActivity.getInstance().getResources().getString(R.string.default_ball_speed_z))));
		} catch (NumberFormatException nfe){
			Log.e(TAG, "Cannot parse float:", nfe);
			setString(getInstance().KEY_BALL_SPEED_X, SquashActivity.getInstance().getResources().getString(R.string.default_ball_speed_x));
			setString(getInstance().KEY_BALL_SPEED_Y, SquashActivity.getInstance().getResources().getString(R.string.default_ball_speed_y));
			setString(getInstance().KEY_BALL_SPEED_Z, SquashActivity.getInstance().getResources().getString(R.string.default_ball_speed_z));
			Toast.makeText(SquashActivity.getInstance(), "Reset ball speed", Toast.LENGTH_SHORT).show();
			return getBallStartSpeed();
		}
		return result;
	}
}
