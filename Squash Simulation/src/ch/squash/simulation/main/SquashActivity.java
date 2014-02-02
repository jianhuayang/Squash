package ch.squash.simulation.main;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;

public class SquashActivity extends Activity {
	private final static String TAG = SquashActivity.class.getSimpleName();
	private SquashView squashView;
	private static final int RESULT_SETTINGS = 1;

	// static access
	private static SquashActivity mInstance;

	public static SquashActivity getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInstance = this;

		// ensure the camera is not rotating on startup
		if (Settings.getCameraMode() == 0)
			Settings.setCameraMode(1);

		// ensure that if nothing would be drawn, ball court and forces are
		// drawn instead
		if (Settings.getVisibleObjectCollections().size() == 0) {
			final Set<String> ss = new HashSet<String>();
			ss.add(Integer.toString(SquashRenderer.OBJECT_COURT));
			ss.add(Integer.toString(SquashRenderer.OBJECT_BALL));
			ss.add(Integer.toString(SquashRenderer.OBJECT_FORCE));
			Settings.setVisibleObjectCollections(ss);
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		squashView = new SquashView(this);

		setContentView(squashView);

		Log.i(TAG, "SquashActivity created");
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		Log.d(TAG, "Menu inflated");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == R.id.menu_settings) {
			squashView.onPause();

			final Intent i = new Intent(this, SettingsActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			Log.d(TAG, "SettingsActivity started");
		}

		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_SETTINGS) {
			squashView.onResume();
			Log.d(TAG, "SettingsActivity finished");
		}
	}

	@Override
	protected void onPause() {
		squashView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		squashView.onResume();
	}
}