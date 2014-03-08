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
import ch.squash.simulation.shapes.shapes.Ball;

public class SquashActivity extends Activity {
	// static
	private final static String TAG = SquashActivity.class.getSimpleName();
	private static SquashActivity mInstance;

	// constants
	private final static int RESULT_SETTINGS = 1;
	private final static int UI_UPDATE_INTERVAL = 200; // ms
	
	// views
	private SquashView mSquashView;
	private TextView mTxtHudFps;
	private TextView mTxtHudBall;
	
	// misc
	private boolean mIsUpdateUi = true;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInstance = this;

		// ensure the camera is not rotating on startup
//		if (Settings.getCameraMode() == 0)
//			Settings.setCameraMode(1);

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
		mTxtHudFps = new TextView(this);
		mTxtHudFps.setTextColor(Color.RED);
		mTxtHudFps.setGravity(Gravity.RIGHT);
		mTxtHudBall = new TextView(this);
		mTxtHudBall.setTextColor(Color.BLUE);
		mTxtHudBall.setGravity(Gravity.LEFT);

		mSquashView = new SquashView(this);

		setContentView(mSquashView);
		final FrameLayout parent = (FrameLayout) mSquashView.getParent();
		parent.addView(mTxtHudFps);
		parent.addView(mTxtHudBall);

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
			mSquashView.onPause();

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
			mSquashView.onResume();
			Log.d(TAG, "SettingsActivity finished");
		}
	}

	@Override
	protected void onPause() {
		mSquashView.onPause();
		super.onPause();
		mIsUpdateUi = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSquashView.onResume();
		mInstance.updateUi();
	}

	private void updateUi() {
		mIsUpdateUi = true;
		
		new Thread() {
			@Override
			public void run() {
				while (mIsUpdateUi) {
					mInstance.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// update *ps
							mTxtHudFps.setText(String.format("%.2f", SquashRenderer.getFps()) + " fps\n" 
									+ String.format("%.2f", MovementEngine.getMps()) + " mps");
							// update ball
							final Ball ball = SquashRenderer.getSquashBall();
							final IVector location =  ball.getLocation();
							final String locationString = "Location:\t" + String.format("%.2f", location.getX()) + "/" + String.format("%.2f", location.getY()) + "/" + String.format("%.2f", location.getZ());
							final IVector speed =  ball.getMovable().speed;
							final String speedString = "Speed:\t\t\t" + String.format("%.2f", speed.getX()) + "/" + String.format("%.2f", speed.getY()) + "/" + String.format("%.2f", speed.getZ());
							final String energyString1 = "EPot=\t\t\t\t" + String.format("%.2f", ball.getMovable().getPotentialEnergy()) + 
									"\t\tEKinLin=" + String.format("%.2f", ball.getMovable().getKineticLinearEnergy()); 
							final String energyString2 = "EKinRot=\t" + String.format("%.2f", ball.getMovable().getKineticRotationalEnergy()) + 
									"\t\tETherm=" + String.format("%.2f", ball.getMovable().getThermicEnergy());
							final String energyString3 = "ETot=\t\t\t\t" + String.format("%.2f", ball.getMovable().getTotalEnergy());
							
							mTxtHudBall.setText(locationString + "\n" + speedString + "\n" + energyString1 + "\n" + energyString2 + "\n" + energyString3);
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
	
	public static SquashActivity getInstance() {
		return mInstance;
	}
}
