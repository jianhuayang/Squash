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
	private RelativeLayout mLayout;

	private GestureDetectorCompat mDetector;

	private boolean mShowingUi = true;

	private final static int ARENA_SETTINGS_INDEX = 5;

	private final static int WORLD_SETTINGS_INDEX = 5;

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

		setContentView(R.layout.layout_main);
		SquashView.getInstance().registerViews(
				(TextView) findViewById(R.id.txtHudFps),
				(TextView) findViewById(R.id.txtHudBall));

		mLayout = (RelativeLayout) findViewById(R.id.layout);

		// gesture stuff
		mDetector = new GestureDetectorCompat(this, this);
		mDetector.setOnDoubleTapListener(this);

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
		int openMenuIndex = -1;

		if (item.getItemId() == R.id.menu_settings_world) {
			openMenuIndex = WORLD_SETTINGS_INDEX;
		} else if (item.getItemId() == R.id.menu_settings_arena) {
			openMenuIndex = ARENA_SETTINGS_INDEX;
		} else if (item.getItemId() != R.id.menu_settings_main) {
			return true;
		}

		SquashView.getInstance().onPause();

		final Intent intent = new Intent(this, SettingsActivity.class);
		if (openMenuIndex != -1) {
			intent.putExtra("openMenuIndex", openMenuIndex);
		}
		
		startActivityForResult(intent, RESULT_SETTINGS);
		Log.d(TAG, "SettingsActivity started");

		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_SETTINGS) {
			SquashView.getInstance().onResume();
			Log.d(TAG, "SettingsActivity finished");
		}
	}

	@Override
	protected void onPause() {
		SquashView.getInstance().onPause();
		super.onPause();
		Shader.destroyShaders(); // destroy shaders so that new ones will be
									// created
	}

	@Override
	protected void onResume() {
		super.onResume();
		SquashView.getInstance().onResume();
	}

	public static SquashActivity getInstance() {
		return mInstance;
	}

	private boolean mIgnoringEvent;

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

		MovementEngine.pause();
		MovementEngine.resetMovables();

		Toast.makeText(SquashActivity.getInstance(), "Movables reset",
				Toast.LENGTH_SHORT).show();
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

		if (MovementEngine.isRunning()) {
			MovementEngine.pause();
		}

		MovementEngine.setRandomDirection();

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

		Toast.makeText(
				SquashActivity.getInstance(),
				"MovementEngine "
						+ (MovementEngine.isRunning() ? "stopped" : "started"),
				Toast.LENGTH_SHORT).show();
		MovementEngine.toggleRunning();

		return true;
	}
}
