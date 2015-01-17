package ch.squash.simulation.main;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	// reference to the preference fragment
	private SettingsFragment mSettingsFragment;

	// determines whether a specific submenu should be opened in onresume
	private boolean mOpenMenuIndex = true;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create preference fragment
		mSettingsFragment = new SettingsFragment();

		// start editing preferences with mSettingsFragment which listenes for
		// changes
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, mSettingsFragment).commit();
	}

	@Override
	protected void onPause() {
		// no need to keep listening for changes
		mSettingsFragment.getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(mSettingsFragment);

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// start listening for changes
		mSettingsFragment.getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(mSettingsFragment);

		// if we could open a submenu and have an index to open, do it
		if (mOpenMenuIndex
				&& getIntent().getIntExtra("openMenuIndex", -1) != -1) {
			// open submenu
			mSettingsFragment.openPreferenceScreen(getIntent().getIntExtra(
					"openMenuIndex", -1));

			// don't open submenu in the future
			// (eg. after re-opening since we might (want to) be on another
			// preference screen)
			mOpenMenuIndex = false;
		}
	}
}
