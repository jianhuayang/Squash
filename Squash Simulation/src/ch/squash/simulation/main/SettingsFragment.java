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
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.graphic.SquashRenderer;
import ch.squash.simulation.shapes.common.Vector;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	// static variables
	private final static String TAG = SettingsFragment.class.getSimpleName();

	// maps a number to its string representation
	private final static Dictionary<Integer, String> DRAW_MODE_SUMMARIES = new Hashtable<Integer, String>();
	private final static Dictionary<Integer, String> CAMERA_MODE_SUMMARIES = new Hashtable<Integer, String>();

	private final static Resources mResources = SquashActivity.getInstance()
			.getResources();

	// static constructor
	static {
		// fill dictionaries
		// -1 is defined as default in preferences_main.xml
		// because that is less likely to interfere with GLES20 constant values
		DRAW_MODE_SUMMARIES.put(-1,
				mResources.getString(R.string.draw_mode_default));
		DRAW_MODE_SUMMARIES.put(GLES20.GL_LINES,
				mResources.getString(R.string.draw_mode_lines));
		DRAW_MODE_SUMMARIES.put(GLES20.GL_LINE_LOOP,
				mResources.getString(R.string.draw_mode_line_loop));
		DRAW_MODE_SUMMARIES.put(GLES20.GL_TRIANGLES,
				mResources.getString(R.string.draw_mode_triangles));
		DRAW_MODE_SUMMARIES.put(GLES20.GL_POINTS,
				mResources.getString(R.string.draw_mode_points));

		CAMERA_MODE_SUMMARIES.put(0,
				mResources.getString(R.string.camera_mode_rotating));
		CAMERA_MODE_SUMMARIES.put(1,
				mResources.getString(R.string.camera_mode_backwall));
		CAMERA_MODE_SUMMARIES.put(2,
				mResources.getString(R.string.camera_mode_sidewall_left));
		CAMERA_MODE_SUMMARIES.put(3,
				mResources.getString(R.string.camera_mode_frontwall));
		CAMERA_MODE_SUMMARIES.put(4,
				mResources.getString(R.string.camera_mode_sidewall_right));
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences_main);
		addPreferencesFromResource(R.xml.preferences_world);
		addPreferencesFromResource(R.xml.preferences_arena);
		addPreferencesFromResource(R.xml.preferences_shots);
		addPreferencesFromResource(R.xml.preferences_about);

		// fill entry values etc
		ListPreference listPref = (ListPreference) findPreference(Settings
				.getKeyDrawMode());
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
		listPref.setEntryValues(new String[] { "0", "1", "2", "3", "4", "5",
				"6" });
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
		setSummary(Settings.getKeyImpactExponent());
		setSummary(Settings.getKeyCoefficientOfRollFriction());

		setSummary(Settings.getKeySeatRowsBack());
		setSummary(Settings.getKeySeatRowsFrontSide());

		// add clicklisteners for shots
		setShotOnClickListener(new Preference[] {
				findPreference(mResources.getString(R.string.key_fh_drive)),
				findPreference(mResources.getString(R.string.key_fh_short_drop)),
				findPreference(mResources.getString(R.string.key_fh_long_drop)),
				findPreference(mResources.getString(R.string.key_fh_boast)),
				findPreference(mResources.getString(R.string.key_fh_serve)),

				findPreference(mResources.getString(R.string.key_bh_drive)),
				findPreference(mResources.getString(R.string.key_bh_short_drop)),
				findPreference(mResources.getString(R.string.key_bh_long_drop)),
				findPreference(mResources.getString(R.string.key_bh_boast)),
				findPreference(mResources.getString(R.string.key_bh_serve)) });

		Log.i(TAG, "SettingsFragment created");
	}

	private void setShotOnClickListener(final Preference[] shotPreferences) {
		for (final Preference p : shotPreferences) {
			p.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				public boolean onPreferenceClick(final Preference pref) {
					// forehand shots
					if (pref.getKey().equals(
							mResources.getString(R.string.key_fh_drive))) {
						Settings.setBallStartPosition(new Vector(3, 0.75f, 4));
						Settings.setBallStartSpeed(new Vector(0.65f, 5, -45));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up FH drive", Toast.LENGTH_SHORT).show();
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_fh_short_drop))) {
						Settings.setBallStartPosition(new Vector(1.5f, 0.5f, -2));
						Settings.setBallStartSpeed(new Vector(3.1f, 2.1f, -8));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up FH short drop", Toast.LENGTH_SHORT)
								.show();
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_fh_long_drop))) {
						Settings.setBallStartPosition(new Vector(2, 0.5f, 3));
						Settings.setBallStartSpeed(new Vector(1.7f, 3, -13.75f));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up FH long drop", Toast.LENGTH_SHORT)
								.show();
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_fh_boast))) {
						Settings.setBallStartPosition(new Vector(2.5f, 0.75f,
								3.75f));
						Settings.setBallStartSpeed(new Vector(23.25f, 5, -35));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up FH boast", Toast.LENGTH_SHORT).show();
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_fh_serve))) {
						Settings.setBallStartPosition(new Vector(1.25f, 1.25f,
								0.5f));
						Settings.setBallStartSpeed(new Vector(-5.35f, 10,
								-16.5f));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up FH serve", Toast.LENGTH_SHORT).show();

						// backhand shots
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_bh_drive))) {
						Settings.setBallStartPosition(new Vector(-3, 0.75f, 4));
						Settings.setBallStartSpeed(new Vector(-0.65f, 5, -45));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up BH drive", Toast.LENGTH_SHORT).show();
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_bh_short_drop))) {
						Settings.setBallStartPosition(new Vector(-1.5f, 0.5f,
								-2));
						Settings.setBallStartSpeed(new Vector(-3.1f, 2.1f, -8));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up BH short drop", Toast.LENGTH_SHORT)
								.show();
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_bh_long_drop))) {
						Settings.setBallStartPosition(new Vector(-2, 0.5f, 3));
						Settings.setBallStartSpeed(new Vector(-1.7f, 3, -13.75f));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up BH long drop", Toast.LENGTH_SHORT)
								.show();
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_bh_boast))) {
						Settings.setBallStartPosition(new Vector(-2.5f, 0.75f,
								3.75f));
						Settings.setBallStartSpeed(new Vector(-23.25f, 5, -35));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up BH boast", Toast.LENGTH_SHORT).show();
					} else if (pref.getKey().equals(
							mResources.getString(R.string.key_bh_serve))) {
						Settings.setBallStartPosition(new Vector(-1.25f, 1.25f,
								0.5f));
						Settings.setBallStartSpeed(new Vector(5.35f, 10, -16.5f));
						Toast.makeText(SquashActivity.getInstance(),
								"Set up BH serve", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(SquashActivity.getInstance(),
								"Unknown shot '" + pref.getTitle() + "'",
								Toast.LENGTH_SHORT).show();
						Log.e(TAG,
								"Unkown shot preference key: " + pref.getKey());
						return false;
					}

					MovementEngine.resetMovables();

					return true;
				}
			});
		}
	}

	public void openPreferenceScreen(final int index) {
		getPreferenceScreen().onItemClick(null, null, index, 0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (Settings.isReset())
			Settings.setBoolean(Settings.getKeyReset(), false);
	}

	public void onSharedPreferenceChanged(
			final SharedPreferences sharedPreferences, final String key) {
		if (key.equals(Settings.getKeySelectObjects())) {
			// update selected objects
			for (final int i : SquashRenderer.OBJECTS)
				SquashRenderer.getInstance().setObjectVisibility(i,
						Settings.isObjectCollectionVisible(i));
		} else if (key.equals(Settings.getKeyReset()) && Settings.isReset()) {
			// reset moveables
			MovementEngine.resetMovables();
			Log.e(TAG, "RESET MOVABLESSSSSSSSSS");
		} else if (key.equals(Settings.getKeyBallPositionX())
				|| key.equals(Settings.getKeyBallPositionY())
				|| key.equals(Settings.getKeyBallPositionZ())) {
			// ball location has changed, reset movables
			((CheckBoxPreference) findPreference(Settings.getKeyReset()))
					.setChecked(true);
		} else if (key.equals(Settings.getKeyBallSpeedX())
				|| key.equals(Settings.getKeyBallSpeedY())
				|| key.equals(Settings.getKeyBallSpeedZ())) {
			// ball speed has changed, reset movables
			((CheckBoxPreference) findPreference(Settings.getKeyReset()))
					.setChecked(true);
		} else if (key.equals(Settings.getKeyHud())) {
			// hud visibility has changed, update ui
			if (Settings.isHudVisible()) {
				SquashView.getInstance().showHud();
			} else {
				SquashView.getInstance().hideHud();
			}
		} else if (key.equals(Settings.getKeySeatRowsBack())
				|| key.equals(Settings.getKeySeatRowsFrontSide()) 
				|| key.equals(Settings.getKeyColorSeat()) 
				|| key.equals(Settings.getKeyColorFloor()) 
				|| key.equals(Settings.getKeyColorWall())) {
			// re-create arena
			SquashRenderer.getInstance().reCreateArena();
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
			result = Settings.isMute() ? mResources
					.getString(R.string.summary_mute) : mResources
					.getString(R.string.summary_unmute);
		else if (key.equals(Settings.getKeyHud()))
			result = Settings.isHudVisible() ? mResources
					.getString(R.string.summary_hud) : mResources
					.getString(R.string.summary_no_hud);
		else if (key.equals(Settings.getKeyDrawMode()))
			result = DRAW_MODE_SUMMARIES.get(Settings.getDrawMode());
		else if (key.equals(Settings.getKeySelectObjects()))
			result = mResources.getString(R.string.summary_select_objects);
		else if (key.equals(Settings.getKeyCameraMode()))
			result = CAMERA_MODE_SUMMARIES.get(Settings.getCameraMode());
		else if (key.equals(Settings.getKeyReset()))
			result = mResources.getString(R.string.summary_reset);
		else if (key.equals(Settings.getKeyCameraPositionX())
				|| key.equals(Settings.getKeyBallPositionX())
				|| key.equals(Settings.getKeyBallSpeedX()))
			result = "x = " + Settings.getValue(key);
		else if (key.equals(Settings.getKeyCameraPositionY())
				|| key.equals(Settings.getKeyBallPositionY())
				|| key.equals(Settings.getKeyBallSpeedY()))
			result = "y = " + Settings.getValue(key);
		else if (key.equals(Settings.getKeyCameraPositionZ())
				|| key.equals(Settings.getKeyBallPositionZ())
				|| key.equals(Settings.getKeyBallSpeedZ()))
			result = "z = " + Settings.getValue(key);
		else if (key.equals(Settings.getKeySpeedFactor())
				|| key.equals(Settings.getKeyCoefficientOfRestitution())
				|| key.equals(Settings.getKeyImpactExponent())
				|| key.equals(Settings.getKeyCoefficientOfRollFriction()))
			result = Settings.getValue(key).toString();
		else if (key.equals(Settings.getKeySeatRowsBack())
				|| key.equals(Settings.getKeySeatRowsFrontSide()))
			result = Settings.getValue(key).toString() + " rows";
		else if (key.equals(Settings.getKeyColorBackground())
				|| key.equals(Settings.getKeyColorSeat())
				|| key.equals(Settings.getKeyColorFloor())
				|| key.equals(Settings.getKeyColorWall()))
			// no manual summary on color picker preferences
			result = "";
		else
			Log.e(TAG, "Unknown key: " + key);

		return result;
	}
}
