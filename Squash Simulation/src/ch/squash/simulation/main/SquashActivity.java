package ch.squash.simulation.main;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.graphic.Shader;
import ch.squash.simulation.graphic.SquashRenderer;


public class SquashActivity extends Activity {
	// static
	private final static String TAG = SquashActivity.class.getSimpleName();
	private static SquashActivity mInstance;

	// constants
	private final static int RESULT_SETTINGS = 1;
	
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

//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.layout_main);
		SquashView.getInstance().registerViews((TextView)findViewById(R.id.txtHudFps), (TextView)findViewById(R.id.txtHudBall));

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
			SquashView.getInstance().onPause();

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
			SquashView.getInstance().onResume();
			Log.d(TAG, "SettingsActivity finished");
		}
	}

	@Override
	protected void onPause() {
		SquashView.getInstance().onPause();
		super.onPause();
		Shader.destroyShaders();	// destroy shaders so that new ones will be created
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
    public boolean onTouchEvent(MotionEvent event){ 
    	final float x = event.getX();
    	final float y = event.getY();
    	final float left = SquashView.getInstance().getLeft();
    	final float right = SquashView.getInstance().getRight();
    	final float top = SquashView.getInstance().getTop();
    	final float bottom = SquashView.getInstance().getBottom();
    	
    	// if the event was on the squashview, inform it
    	if (x >= left && x <= right &&
    			y >= top && y <= bottom) {
            SquashView.getInstance().onTouchEvent(event);
    	}
    	
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }	
}
