package ch.squash.simulation.main;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	private SettingsFragment mSettingsFragment;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSettingsFragment = new SettingsFragment();

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, mSettingsFragment).commit();
	}

	@Override
	protected void onPause() {
		mSettingsFragment.getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(mSettingsFragment);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSettingsFragment.getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(mSettingsFragment);
	}

}
