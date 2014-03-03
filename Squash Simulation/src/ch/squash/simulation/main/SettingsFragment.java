package ch.squash.simulation.main;

import java.util.Dictionary;
import java.util.Hashtable;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.opengl.GLES20;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	private final static String TAG = SettingsFragment.class.getSimpleName();

	private static final Dictionary<Integer, String> DRAW_MODE_SUMMARIES = new Hashtable<Integer, String>();
	private static final Dictionary<Integer, String> CAMERA_MODE_SUMMARIES = new Hashtable<Integer, String>();
	
	static {
		DRAW_MODE_SUMMARIES.put(-1, "Use default draw mode");
		DRAW_MODE_SUMMARIES.put(GLES20.GL_LINES, "Draw lines");
		DRAW_MODE_SUMMARIES.put(GLES20.GL_LINE_LOOP, "Draw looping lines");
		DRAW_MODE_SUMMARIES.put(GLES20.GL_TRIANGLES, "Draw triangles");
		DRAW_MODE_SUMMARIES.put(GLES20.GL_POINTS, "Draw points");
		
		CAMERA_MODE_SUMMARIES.put(0, "Rotating camera");
		CAMERA_MODE_SUMMARIES.put(1, "Backwall camera");
		CAMERA_MODE_SUMMARIES.put(2, "Sidewall camera left");
		CAMERA_MODE_SUMMARIES.put(3, "Frontwall camera");
		CAMERA_MODE_SUMMARIES.put(4, "Siedwall camera right");
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// fill entry values etc
		ListPreference listPref = (ListPreference) findPreference(Settings.getKeyDrawMode());
		listPref.setEntryValues(new String[] { "-1",
				Integer.toString(GLES20.GL_LINES),
				Integer.toString(GLES20.GL_LINE_LOOP),
				Integer.toString(GLES20.GL_TRIANGLES),
				Integer.toString(GLES20.GL_POINTS) });
		listPref.setSummary(getSummary(Settings.getKeyDrawMode()));

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
		listPref.setSummary(getSummary(Settings.getKeyCameraMode()));

		findPreference(Settings.getKeyCameraPositionX()).setSummary(getSummary(Settings.getKeyCameraPositionX()));
		findPreference(Settings.getKeyCameraPositionY()).setSummary(getSummary(Settings.getKeyCameraPositionY()));
		findPreference(Settings.getKeyCameraPositionZ()).setSummary(getSummary(Settings.getKeyCameraPositionZ()));
		findPreference(Settings.getKeyBallPositionX()).setSummary(getSummary(Settings.getKeyBallPositionX()));
		findPreference(Settings.getKeyBallPositionY()).setSummary(getSummary(Settings.getKeyBallPositionY()));
		findPreference(Settings.getKeyBallPositionZ()).setSummary(getSummary(Settings.getKeyBallPositionZ()));
		findPreference(Settings.getKeyBallSpeedX()).setSummary(getSummary(Settings.getKeyBallSpeedX()));
		findPreference(Settings.getKeyBallSpeedY()).setSummary(getSummary(Settings.getKeyBallSpeedY()));
		findPreference(Settings.getKeyBallSpeedZ()).setSummary(getSummary(Settings.getKeyBallSpeedZ()));

		findPreference(Settings.getKeySpeedFactor()).setSummary(getSummary(Settings.getKeySpeedFactor()));
		findPreference(Settings.getKeyCoefficientOfRestitution()).setSummary(getSummary(Settings.getKeyCoefficientOfRestitution()));

		Log.i(TAG, "SettingsFragment created");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (Settings.isReset())
			Settings.setBoolean(Settings.getKeyReset(), false);
	}

	public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
		final Preference pref = findPreference(key);

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
		}
		
		pref.setSummary(getSummary(key));

		Log.i(TAG, "Setting " + key + " changed its value");
	}

	private String getSummary(final String key) {
		String result = null;
		if (key.equals(Settings.getKeyDrawMode()))
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
		else if (key.equals(Settings.getKeySpeedFactor()) || key.equals(Settings.getKeyCoefficientOfRestitution()))
			result = Settings.getValue(key).toString();
		else
			Log.e(TAG, "Unknown key: " + key);
		
		return result;
	}
}
