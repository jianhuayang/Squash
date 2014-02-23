package ch.squash.simulation.main;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.shapes.common.IVector;

public class SquashActivity extends Activity {
	private final static String TAG = SquashActivity.class.getSimpleName();
	private SquashView squashView;
	private TextView hudFps;
	private TextView hudBall;
	private static final int RESULT_SETTINGS = 1;
	private final static int UI_UPDATE_INTERVAL = 200; // ms
	private boolean isUpdateUi = true;

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
		hudFps = new TextView(this);
		hudFps.setTextColor(Color.RED);
		hudFps.setGravity(Gravity.RIGHT);
		hudBall = new TextView(this);
		hudBall.setTextColor(Color.BLUE);
		hudBall.setGravity(Gravity.LEFT);

		squashView = new SquashView(this);

		setContentView(squashView);
		final FrameLayout parent = (FrameLayout) squashView.getParent();
		parent.addView(hudFps);
		parent.addView(hudBall);

		updateUi();
		
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
		isUpdateUi = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		squashView.onResume();
		mInstance.updateUi();
	}

	private void updateUi() {
		isUpdateUi = true;
		
		new Thread() {
			@Override
			public void run() {
				while (isUpdateUi) {
					mInstance.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// update *ps
							hudFps.setText(String.format("%.2f", SquashRenderer.getFps()) + " fps\n" 
									+ String.format("%.2f", MovementEngine.getMps()) + " mps");
							// update ball
							final IVector location =  SquashRenderer.getSquashBall().getLocation();
							final IVector speed =  SquashRenderer.getSquashBall().getMovable().speed;
							hudBall.setText("Location:\t" + String.format("%.2f", location.getX()) + "/" + String.format("%.2f", location.getY()) + "/" + String.format("%.2f", location.getZ()) + 
									"\nSpeed:\t\t\t" + String.format("%.2f", speed.getX()) + "/" + String.format("%.2f", speed.getY()) + "/" + String.format("%.2f", speed.getZ()));
						}
					});

					try {
						Thread.sleep(UI_UPDATE_INTERVAL);
					} catch (InterruptedException e) {
						Log.e(TAG, "Error while sleepint", e);
					}
				}
			}
		}.start();
	}
}