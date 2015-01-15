package ch.squash.simulation.main;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;
import ch.squash.simulation.graphic.SquashRenderer;

public class SquashView extends GLSurfaceView implements SurfaceHolder.Callback {
	private final static String TAG = SquashView.class.getSimpleName();

	public SquashView(final Context context) {
		super(context);

		setEGLContextClientVersion(2);

		setRenderer(SquashRenderer.getInstance());

		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		Log.i(TAG, "SquashView created");
	}

	@Override
	public void onPause() {
		super.onPause();
		MovementEngine.pause();
	}

//	public boolean onTouchEvent(final MotionEvent event) {
//		mGestureDetector.onTouchEvent(event);
		
//		if (mLongPress){
//			// only reset longpress flag if it was completed (if the action is UP)
//			mLongPress = event.getAction() != MotionEvent.ACTION_UP;
//		} else if (event.getAction() == MotionEvent.ACTION_UP){
//	    	Toast.makeText(SquashActivity.getInstance(),
//	    			"MovementEngine " + (MovementEngine.isRunning() ? "stopped" : "started"), Toast.LENGTH_SHORT).show();
//			MovementEngine.toggleRunning();
//		}
		
//		return true;
//	}
}
