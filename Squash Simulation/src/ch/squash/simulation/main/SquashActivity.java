package ch.squash.simulation.main;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.graphic.Shader;
import ch.squash.simulation.graphic.SquashRenderer;

public class SquashActivity extends Activity implements OnGestureListener,
		OnDoubleTapListener {
	// static
	private final static String TAG = SquashActivity.class.getSimpleName();
	private static SquashActivity mInstance;

	// constants
	private final static int RESULT_SETTINGS = 1;

	// misc
	private RelativeLayout mLayout; // reference to the layout in order to loop
									// over children

	private GestureDetectorCompat mDetector; // handles complex gestures

	private boolean mIgnoringEvent;	// true if the current event is being ignored

	private boolean mShowingUi = true; // true if the UI (buttons and title bar)
										// are currently visible

	// sub-menu indices
	private final static int WORLD_SETTINGS_INDEX = 8;
	private final static int ARENA_SETTINGS_INDEX = 9;
	private final static int SHOTS_SETTINGS_INDEX = 10;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInstance = this;

		// ensure that if nothing would be drawn, ball court and forces are
		// drawn instead
		if (Settings.getVisibleObjectCollections().size() == 0) {
			final Set<String> ss = new HashSet<String>();
			ss.add(Integer.toString(SquashRenderer.OBJECT_COURT));
			ss.add(Integer.toString(SquashRenderer.OBJECT_BALL));
			ss.add(Integer.toString(SquashRenderer.OBJECT_FORCE));
			Settings.setVisibleObjectCollections(ss);
		}

		// inflate layout
		setContentView(R.layout.layout_main);

		// register views
		SquashView.getInstance().registerViews(
				(TextView) findViewById(R.id.txtHudFps),
				(TextView) findViewById(R.id.txtHudBall));
		if (Settings.isHudVisible()) {
			SquashView.getInstance().showHud();
		} else {
			SquashView.getInstance().hideHud();
		}
		
		// retrieve reference to layout
		mLayout = (RelativeLayout) findViewById(R.id.layout);

		// initialize gesture detection
		mDetector = new GestureDetectorCompat(this, this);
		mDetector.setOnDoubleTapListener(this);

		// hide the ui initially
//		toggleUi();

		Log.i(TAG, "SquashActivity created");
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// inflate settings menu
		getMenuInflater().inflate(R.menu.settings, menu);

		Log.d(TAG, "Menu inflated");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		int openMenuIndex = -1;

		// detect which menu entry has been touched
		if (item.getItemId() == R.id.menu_settings_world) {
			openMenuIndex = WORLD_SETTINGS_INDEX;
		} else if (item.getItemId() == R.id.menu_settings_arena) {
			openMenuIndex = ARENA_SETTINGS_INDEX;
		} else if (item.getItemId() == R.id.menu_settings_shots) {
			openMenuIndex = SHOTS_SETTINGS_INDEX;
		} else if (item.getItemId() != R.id.menu_settings_main) {
			// no valid entry has been touched!
			Log.e(TAG, "Unknown menu entry: " + item.getItemId());
			return false;
		}

		// create new intent which opens settings
		final Intent intent = new Intent(this, SettingsActivity.class);

		// if the user didn't touch the main menu entry, tell the settings
		// activity to open a submenu
		if (openMenuIndex != -1) {
			intent.putExtra("openMenuIndex", openMenuIndex);
		}

		// launch activity
		startActivityForResult(intent, RESULT_SETTINGS);
		
		Log.d(TAG, "SettingsActivity started");

		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		SquashView.getInstance().onPause();
		
		//destroy shaders so that new ones will be created
		Shader.destroyShaders();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SquashView.getInstance().onResume();
	}

	public static SquashActivity getInstance() {
		return mInstance;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final float x = event.getX();
		final float y = event.getY();
		final float left = SquashView.getInstance().getLeft();
		final float right = SquashView.getInstance().getRight();
		final float top = SquashView.getInstance().getTop();
		final float bottom = SquashView.getInstance().getBottom();

		// if the squashview is full screen, dont ignore anything
		if (!mShowingUi) {
			mIgnoringEvent = false;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mIgnoringEvent = x >= left && x <= right && y >= top && y <= bottom ? false
					: true;
		}

		if (!mIgnoringEvent) {
			this.mDetector.onTouchEvent(event);
		} else {
			Log.w(TAG, "ignoring event " + event.getAction());
		}

		// Be sure to call the superclass implementation
		return super.onTouchEvent(event);
	}

	public void toggleUi() {
		for (int i = 0; i < mLayout.getChildCount(); i++) {
			if (mLayout.getChildAt(i).getTag() != null
					&& mLayout.getChildAt(i).getTag().equals("dontHide")) {
				continue;
			}
			mLayout.getChildAt(i).setVisibility(
					mShowingUi ? View.GONE : View.VISIBLE);
		}

		if (mShowingUi) {
			getActionBar().hide();
		} else {
			getActionBar().show();
		}

		mShowingUi = !mShowingUi;
	}

	@Override
	public boolean onDown(MotionEvent event) {
		// Log.d(TAG,"onDown: " + event.toString());
		return true;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2,
			float velocityX, float velocityY) {
		final float dx = event2.getX() - event1.getX();
		final float dy = event2.getY() - event1.getY();

		if (Math.abs(dy) > 0.2f * SquashView.getInstance().getHeight()) {
			Log.d(TAG, "SwipeUp/Down");
			SquashActivity.getInstance().toggleUi();
		}

		Log.d(TAG, "onFling: dx=" + dx + ", dy=" + dy); // +
														// event1.toString()+event2.toString());
		return true;
	}

	@Override
	public void onLongPress(MotionEvent event) {
		// Log.d(TAG, "onLongPress: " + event.toString());

		resetMovables(null);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// Log.d(TAG, "onScroll: " + e1.toString()+e2.toString() + distanceX +
		// distanceY);
		return true;
	}

	@Override
	public void onShowPress(MotionEvent event) {
		// Log.d(TAG, "onShowPress: " + event.toString());
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		// Log.d(TAG, "onSingleTapUp: " + event.toString());
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		// Log.d(TAG, "onDoubleTap: " + event.toString());

		randomizeMovables(null);
		
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		// Log.d(TAG, "onDoubleTapEvent: " + event.toString());
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		// Log.d(TAG, "onSingleTapConfirmed: " + event.toString());

		startStopMovementEngine(null);
		
		return true;
	}
	
	public void showControls(final View view) {
		Toast.makeText(this, "CONTROLS NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
	}
	
	public void randomizeMovables(final View view) {
		if (MovementEngine.isRunning()) {
			MovementEngine.pause();
		}

		MovementEngine.setRandomDirection();
	}
	
	public void startStopMovementEngine(final View view) {
		Toast.makeText(
				SquashActivity.getInstance(),
				"MovementEngine "
						+ (MovementEngine.isRunning() ? "stopped" : "started"),
				Toast.LENGTH_SHORT).show();
		MovementEngine.toggleRunning();
	}
	
	public void resetMovables(final View view) {
		MovementEngine.pause();
		MovementEngine.resetMovables();
	}
}
