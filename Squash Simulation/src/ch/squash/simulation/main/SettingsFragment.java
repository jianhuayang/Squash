package ch.squash.simulation.main;

import java.util.Dictionary;
import java.util.Hashtable;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.graphic.SquashRenderer;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	// static variables
	private final static String TAG = SettingsFragment.class.getSimpleName();

	// maps a number to its string representation
	private static final Dictionary<Integer, String> DRAW_MODE_SUMMARIES = new Hashtable<Integer, String>();
	private static final Dictionary<Integer, String> CAMERA_MODE_SUMMARIES = new Hashtable<Integer, String>();
	
	// static constructor
	static {
		final Resources res = SquashActivity.getInstance().getResources();
		
		// fill dictionaries
		// -1 is defined as default in preferences_main.xml
		// because that is less likely to interfere with GLES20 constant values
		DRAW_MODE_SUMMARIES.put(-1, res.getString(R.string.draw_mode_default));
		DRAW_MODE_SUMMARIES.put(GLES20.GL_LINES, res.getString(R.string.draw_mode_lines));
		DRAW_MODE_SUMMARIES.put(GLES20.GL_LINE_LOOP, res.getString(R.string.draw_mode_line_loop));
		DRAW_MODE_SUMMARIES.put(GLES20.GL_TRIANGLES, res.getString(R.string.draw_mode_triangles));
		DRAW_MODE_SUMMARIES.put(GLES20.GL_POINTS, res.getString(R.string.draw_mode_points));
		
		CAMERA_MODE_SUMMARIES.put(0, res.getString(R.string.camera_mode_rotating));
		CAMERA_MODE_SUMMARIES.put(1, res.getString(R.string.camera_mode_backwall));
		CAMERA_MODE_SUMMARIES.put(2, res.getString(R.string.camera_mode_sidewall_left));
		CAMERA_MODE_SUMMARIES.put(3, res.getString(R.string.camera_mode_frontwall));
		CAMERA_MODE_SUMMARIES.put(4, res.getString(R.string.camera_mode_sidewall_right));
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences_main);
		addPreferencesFromResource(R.xml.preferences_world);
		addPreferencesFromResource(R.xml.preferences_arena);
		
		// fill entry values etc
		ListPreference listPref = (ListPreference) findPreference(Settings.getKeyDrawMode());
		listPref.setEntryValues(new String[] { "-1",
				Integer.toString(GLES20.GL_LINES),
				Integer.toString(GLES20.GL_LINE_LOOP),
				Integer.toString(GLES20.GL_TRIANGLES),
				Integer.toString(GLES20.GL_POINTS) });
		setSummary(Settings.getKeyDrawMode());

		final MultiSelectListPreference mslp = (MultiSelectListPreference) findPreference(Settings
				.getKeySelectObjects());

		mslp.setEntries(new String[] { "Court", "Ball", "Coordinate axis",
				"Miscellaneous objects", "Forces", "Arena", "Chairs" });
		mslp.setEntryValues(new String[] {
				Integer.toString(SquashRenderer.OBJECT_COURT),
				Integer.toString(SquashRenderer.OBJECT_BALL),
				Integer.toString(SquashRenderer.OBJECT_AXIS),
				Integer.toString(SquashRenderer.OBJECT_MISC),
				Integer.toString(SquashRenderer.OBJECT_FORCE),
				Integer.toString(SquashRenderer.OBJECT_ARENA),
				Integer.toString(SquashRenderer.OBJECT_CHAIRS) });

		listPref = (ListPreference) findPreference(Settings.getKeyCameraMode());
		listPref.setEntryValues(new String[] { "0", "1", "2", "3", "4", "5", "6" });
		setSummary(Settings.getKeyCameraMode());

		setSummary(Settings.getKeyMute());
		setSummary(Settings.getKeyHud());

		setSummary(Settings.getKeyCameraPositionX());
		setSummary(Settings.getKeyCameraPositionY());
		setSummary(Settings.getKeyCameraPositionZ());
		setSummary(Settings.getKeyBallPositionX());
		setSummary(Settings.getKeyBallPositionY());
		setSummary(Settings.getKeyBallPositionZ());
		setSummary(Settings.getKeyBallSpeedX());
		setSummary(Settings.getKeyBallSpeedY());
		setSummary(Settings.getKeyBallSpeedZ());

		setSummary(Settings.getKeySpeedFactor());
		setSummary(Settings.getKeyCoefficientOfRestitution());
		setSummary(Settings.getKeyCoefficientOfRollFriction());

		Log.i(TAG, "SettingsFragment created");
	}

	public void openPreferenceScreen(final int index){
		getPreferenceScreen().onItemClick( null, null, index, 0 ); 
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (Settings.isReset())
			Settings.setBoolean(Settings.getKeyReset(), false);
	}

	public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
		if (key.equals(Settings.getKeySelectObjects())) {
			for (final int i : SquashRenderer.OBJECTS)
				SquashRenderer.getInstance().setObjectVisibility(i,
						Settings.isObjectCollectionVisible(i));
		} else if (key.equals(Settings.getKeyReset()) && Settings.isReset())
			MovementEngine.resetMovables();
		else if (key.equals(Settings.getKeyCameraMode()))
			SquashRenderer.getInstance().setCameraRotation = true;
		else if (key.equals(Settings.getKeyCameraPositionX()) || key.equals(Settings.getKeyCameraPositionY()) || key.equals(Settings.getKeyCameraPositionZ()))
			SquashRenderer.getInstance().resetCamera();
		else if (key.equals(Settings.getKeyBallPositionX()) || key.equals(Settings.getKeyBallPositionY()) || key.equals(Settings.getKeyBallPositionZ())){
			SquashRenderer.getInstance().setBallPosition(Settings.getBallStartPosition());
			((CheckBoxPreference)findPreference(Settings.getKeyReset())).setChecked(true);
		}
		else if (key.equals(Settings.getKeyBallSpeedX()) || key.equals(Settings.getKeyBallSpeedY()) || key.equals(Settings.getKeyBallSpeedZ())){
			((CheckBoxPreference)findPreference(Settings.getKeyReset())).setChecked(true);
			MovementEngine.resetMovables();
		} else if (key.equals(Settings.getKeyHud())){
			if (Settings.isHudVisible()) {
				SquashView.getInstance().showHud();
			} else {
				SquashView.getInstance().hideHud();
			}
		}

		setSummary(key);
		
		Log.i(TAG, "Setting " + key + " changed its value");
	}
	
	private void setSummary(final String key) {
		findPreference(key).setSummary(getSummary(key));
	}

	private String getSummary(final String key) {
		String result = null;
		if (key.equals(Settings.getKeyMute()))
			result = Settings.isMute() ?
					SquashActivity.getInstance().getResources().getString(R.string.summary_mute) : 
						SquashActivity.getInstance().getResources().getString(R.string.summary_unmute);
		else if (key.equals(Settings.getKeyHud()))
			result = Settings.isHudVisible() ?
					SquashActivity.getInstance().getResources().getString(R.string.summary_hud) : 
						SquashActivity.getInstance().getResources().getString(R.string.summary_no_hud);
		else if (key.equals(Settings.getKeyDrawMode()))
			result = DRAW_MODE_SUMMARIES.get(Settings.getDrawMode());
		else if (key.equals(Settings.getKeySelectObjects()))
			result = SquashActivity.getInstance().getResources()
					.getString(R.string.summary_select_objects);
		else if (key.equals(Settings.getKeyCameraMode()))
			result = CAMERA_MODE_SUMMARIES.get(Settings.getCameraMode());
		else if (key.equals(Settings.getKeyReset()))
			result = SquashActivity.getInstance().getResources()
					.getString(R.string.summary_reset);
		else if (key.equals(Settings.getKeyCameraPositionX()) || key.equals(Settings.getKeyBallPositionX()) || key.equals(Settings.getKeyBallSpeedX()))
			result = "x = " + Settings.getValue(key);
		else if (key.equals(Settings.getKeyCameraPositionY()) || key.equals(Settings.getKeyBallPositionY()) || key.equals(Settings.getKeyBallSpeedY()))
			result = "y = " + Settings.getValue(key);
		else if (key.equals(Settings.getKeyCameraPositionZ()) || key.equals(Settings.getKeyBallPositionZ()) || key.equals(Settings.getKeyBallSpeedZ()))
			result = "z = " + Settings.getValue(key);
		else if (key.equals(Settings.getKeySpeedFactor()) || key.equals(Settings.getKeyCoefficientOfRestitution()) || key.equals(Settings.getKeyCoefficientOfRollFriction()))
			result = Settings.getValue(key).toString();
		else
			Log.e(TAG, "Unknown key: " + key);
		
		return result;
	}
}
