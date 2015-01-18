package ch.squash.simulation.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import ch.squash.simulation.R;
import ch.squash.simulation.graphic.SquashRenderer;
import ch.squash.simulation.main.SquashActivity;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.common.Vector;

public final class Settings {
	// constant values
	private final static String TAG = Settings.class.getSimpleName();
	private final static SharedPreferences mSharedPrefs = PreferenceManager
			.getDefaultSharedPreferences(SquashActivity.getInstance());
	private final static Resources mResources = SquashActivity.getInstance()
			.getResources();

	/**************
	 * KEY ACCESS *
	 **************/
	public static String getKeyDrawMode() {
		return mResources.getString(R.string.key_draw_mode);
	}

	public static String getKeySelectObjects() {
		return mResources.getString(R.string.key_select_objects);
	}

	public static String getKeyCameraMode() {
		return mResources.getString(R.string.key_camera_mode);
	}

	public static String getKeyMute() {
		return mResources.getString(R.string.key_mute);
	}

	public static String getKeyHud() {
		return mResources.getString(R.string.key_hud);
	}

	public static String getKeyReset() {
		return mResources.getString(R.string.key_reset);
	}

	public static String getKeyCameraPositionX() {
		return mResources.getString(R.string.key_camera_position_x);
	}

	public static String getKeyCameraPositionY() {
		return mResources.getString(R.string.key_camera_position_y);
	}

	public static String getKeyCameraPositionZ() {
		return mResources.getString(R.string.key_camera_position_z);
	}

	public static String getKeyBallPositionX() {
		return mResources.getString(R.string.key_ball_position_x);
	}

	public static String getKeyBallPositionY() {
		return mResources.getString(R.string.key_ball_position_y);
	}

	public static String getKeyBallPositionZ() {
		return mResources.getString(R.string.key_ball_position_z);
	}

	public static String getKeyBallSpeedX() {
		return mResources.getString(R.string.key_ball_speed_x);
	}

	public static String getKeyBallSpeedY() {
		return mResources.getString(R.string.key_ball_speed_y);
	}

	public static String getKeyBallSpeedZ() {
		return mResources.getString(R.string.key_ball_speed_z);
	}

	public static String getKeySpeedFactor() {
		return mResources.getString(R.string.key_speed_factor);
	}

	public static String getKeyCoefficientOfRestitution() {
		return mResources.getString(R.string.key_coefficient_of_restitution);
	}

	public static String getKeyCoefficientOfRollFriction() {
		return mResources.getString(R.string.key_coefficient_of_roll_friction);
	}

	/**********
	 * SETTER *
	 **********/
	public static void setBoolean(final String key, final boolean value) {
		final Editor editor = mSharedPrefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void setString(final String key, final String value) {
		final Editor editor = mSharedPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void setVisibleObjectCollections(final Set<String> collections) {
		final Editor editor = mSharedPrefs.edit();
		editor.putStringSet(getKeySelectObjects(), collections);
		editor.commit();
	}

	public static void setCameraMode(final int mode) {
		final Editor editor = mSharedPrefs.edit();
		editor.putString(getKeyCameraMode(), Integer.toString(mode));
		editor.commit();
	}

	public static void setBallStartSpeed(final IVector speed) {
		final Editor editor = mSharedPrefs.edit();
		editor.putString(getKeyBallSpeedX(), Float.toString(speed.getX()));
		editor.putString(getKeyBallSpeedY(), Float.toString(speed.getY()));
		editor.putString(getKeyBallSpeedZ(), Float.toString(speed.getZ()));
		editor.commit();
	}

	/**********
	 * GETTER *
	 **********/
	public static Object getValue(final String key) {
		final Map<String, ?> map = mSharedPrefs.getAll();
		return map.get(key);
	}

	public static boolean isReset() {
		return mSharedPrefs.getBoolean(getKeyReset(), false);
	}

	public static boolean isMute() {
		return mSharedPrefs.getBoolean(getKeyMute(), false);
	}

	public static boolean isHudVisible() {
		return mSharedPrefs.getBoolean(getKeyHud(), true);
	}

	public static int getDrawMode() {
		// draw mode is stored as a string because it's options are strings
		return Integer.parseInt(mSharedPrefs.getString(getKeyDrawMode(), "-1"));
	}

	public static Set<String> getVisibleObjectCollections() {
		return mSharedPrefs.getStringSet(getKeySelectObjects(),
				new HashSet<String>());
	}

	public static boolean isObjectCollectionVisible(final int ocId) {
		return getVisibleObjectCollections().contains(Integer.toString(ocId));
	}

	public static boolean isDrawForces() {
		return isObjectCollectionVisible(SquashRenderer.OBJECT_FORCE);
	}

	public static int getCameraMode() {
		// camera mode is stored as a string because it's options are strings
		return Integer
				.parseInt(mSharedPrefs.getString(getKeyCameraMode(), "0"));
	}

	public static boolean isCameraRotating() {
		return getCameraMode() == 0;
	}

	public static IVector getCameraPosition() {
		IVector result;
		try {
			result = new Vector(Float.parseFloat(mSharedPrefs.getString(
					getKeyCameraPositionX(),
					mResources.getString(R.string.default_camera_position_x))),

			Float.parseFloat(mSharedPrefs.getString(getKeyCameraPositionY(),
					mResources.getString(R.string.default_camera_position_y))),

			Float.parseFloat(mSharedPrefs.getString(getKeyCameraPositionZ(),
					mResources.getString(R.string.default_camera_position_z))));
		} catch (NumberFormatException nfe) {
			Log.e(TAG, "Cannot parse float", nfe);
			setString(getKeyCameraPositionX(),
					mResources.getString(R.string.default_camera_position_x));
			setString(getKeyCameraPositionY(),
					mResources.getString(R.string.default_camera_position_y));
			setString(getKeyCameraPositionZ(),
					mResources.getString(R.string.default_camera_position_z));
			Toast.makeText(SquashActivity.getInstance(),
					"Reset camera position", Toast.LENGTH_SHORT).show();
			result = getCameraPosition();
		}
		return result;
	}

	public static IVector getBallStartPosition() {
		IVector result;
		try {
			result = new Vector(
					Float.parseFloat(mSharedPrefs.getString(
							getKeyBallPositionX(),
							mResources
									.getString(R.string.default_ball_position_x))),
					Float.parseFloat(mSharedPrefs.getString(
							getKeyBallPositionY(),
							mResources
									.getString(R.string.default_ball_position_y))),
					Float.parseFloat(mSharedPrefs.getString(
							getKeyBallPositionZ(),
							mResources
									.getString(R.string.default_ball_position_z))));
		} catch (NumberFormatException nfe) {
			Log.e(TAG, "Cannot parse float", nfe);
			setString(getKeyBallPositionX(),
					mResources.getString(R.string.default_ball_position_x));
			setString(getKeyBallPositionY(),
					mResources.getString(R.string.default_ball_position_y));
			setString(getKeyBallPositionZ(),
					mResources.getString(R.string.default_ball_position_z));
			Toast.makeText(SquashActivity.getInstance(), "Reset ball position",
					Toast.LENGTH_SHORT).show();
			result = getBallStartPosition();
		}
		return result;
	}

	public static IVector getBallStartSpeed() {
		IVector result;
		try {
			result = new Vector(
					Float.parseFloat(mSharedPrefs
							.getString(getKeyBallSpeedX(), mResources
									.getString(R.string.default_ball_speed_x))),
					Float.parseFloat(mSharedPrefs
							.getString(getKeyBallSpeedY(), mResources
									.getString(R.string.default_ball_speed_y))),
					Float.parseFloat(mSharedPrefs
							.getString(getKeyBallSpeedZ(), mResources
									.getString(R.string.default_ball_speed_z))));
		} catch (NumberFormatException nfe) {
			Log.e(TAG, "Cannot parse float", nfe);
			setString(getKeyBallSpeedX(),
					mResources.getString(R.string.default_ball_speed_x));
			setString(getKeyBallSpeedY(),
					mResources.getString(R.string.default_ball_speed_y));
			setString(getKeyBallSpeedZ(),
					mResources.getString(R.string.default_ball_speed_z));
			Toast.makeText(SquashActivity.getInstance(), "Reset ball speed",
					Toast.LENGTH_SHORT).show();
			result = getBallStartSpeed();
		}
		return result;
	}

	public static float getSpeedFactor() {
		return Float.parseFloat(mSharedPrefs.getString(getKeySpeedFactor(),
				"1f"));
	}

	public static float getCoefficientOfRestitution() {
		return Float.parseFloat(mSharedPrefs.getString(
				getKeyCoefficientOfRestitution(), "0.75f"));
	}

	public static float getCoefficientOfRollFriction() {
		return Float.parseFloat(mSharedPrefs.getString(
				getKeyCoefficientOfRollFriction(), "0.25f"));
	}
}
