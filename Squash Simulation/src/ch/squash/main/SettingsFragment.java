package ch.squash.main;

import java.util.Dictionary;
import java.util.Hashtable;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.opengl.GLES20;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import ch.squash.R;
import ch.squash.common.Settings;

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
		ListPreference listPref = (ListPreference) findPreference(Settings
				.getKeyCameraMode());
		listPref.setEntryValues(new String[] { "-1",
				Integer.toString(GLES20.GL_LINES),
				Integer.toString(GLES20.GL_LINE_LOOP),
				Integer.toString(GLES20.GL_TRIANGLES),
				Integer.toString(GLES20.GL_POINTS) });
		listPref.setSummary(getSummary(Settings.getKeyDrawMode()));

		final MultiSelectListPreference mslp = (MultiSelectListPreference) findPreference(Settings
				.getKeySelectObjects());

		mslp.setEntries(new String[] { "Court", "Ball", "Coordinate axis",
				"Miscellaneous objects", "Forces" });
		mslp.setEntryValues(new String[] {
				Integer.toString(SquashRenderer.OBJECT_COURT),
				Integer.toString(SquashRenderer.OBJECT_BALL),
				Integer.toString(SquashRenderer.OBJECT_AXIS),
				Integer.toString(SquashRenderer.OBJECT_MISC),
				Integer.toString(SquashRenderer.OBJECT_FORCE) });

		listPref = (ListPreference) findPreference(Settings.getKeyCameraMode());
		listPref.setEntryValues(new String[] { "0", "1", "2", "3", "4" });
		listPref.setSummary(getSummary(Settings.getKeyCameraMode()));

		Log.i(TAG, "SettingsFragment created");
	}

	public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
		final Preference pref = findPreference(key);

		if (key.equals(Settings.getKeySelectObjects())) {
			for (final int i : SquashRenderer.OBJECTS)
				SquashRenderer.getInstance().setObjectVisibility(i,
						Settings.isObjectCollectionVisible(i));
		} else if (key.equals(Settings.getKeyReset()) && Settings.isReset())
			MovementEngine.resetMovables();

		pref.setSummary(getSummary(key));

		Log.i(TAG, "Setting " + key + " changed its value");
	}

	private String getSummary(final String key) {
		if (key.equals(Settings.getKeyDrawMode()))
			return DRAW_MODE_SUMMARIES.get(Settings.getDrawMode());
		else if (key.equals(Settings.getKeySelectObjects()))
			return SquashActivity.getInstance().getResources()
					.getString(R.string.summary_select_objects);
		else if (key.equals(Settings.getKeyCameraMode()))
			return CAMERA_MODE_SUMMARIES.get(Settings.getCameraMode());
		else if (key.equals(Settings.getKeyReset()))
			return SquashActivity.getInstance().getResources()
					.getString(R.string.summary_reset);

		Log.e(TAG, "Unknown key: " + key);
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (Settings.isReset())
			Settings.setBoolean(Settings.getKeyReset(), false);
	}
}
