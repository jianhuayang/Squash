package ch.squash.simulation.main;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class SquashView extends GLSurfaceView implements SurfaceHolder.Callback {
	private final static String TAG = SquashView.class.getSimpleName();

	// vars for touchevents
	private float mPreviousX;
	private float mPreviousY;
	private final float mDensity;

	@Override
	public void onPause() {
		super.onPause();
		MovementEngine.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		MovementEngine.resume();
	}

	public SquashView(final Context context) {
		super(context);

		setEGLContextClientVersion(2);

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		((SquashActivity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		mDensity = displayMetrics.density;

		setRenderer(SquashRenderer.getInstance());

		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		Log.i(TAG, "SquashView created");
	}

	public boolean onTouchEvent(final MotionEvent event) {
		if (event == null)
			return super.onTouchEvent(event);
		else {
			final float x = event.getX();
			final float y = event.getY();

			if (event.getAction() == MotionEvent.ACTION_MOVE
					&& SquashRenderer.getInstance() != null) {
				final float deltaX = (x - mPreviousX) / mDensity / 2f;
				final float deltaY = (y - mPreviousY) / mDensity / 2f;

				SquashRenderer.getInstance().mDeltaX += deltaX;
				SquashRenderer.getInstance().mDeltaY += deltaY;
			}

			mPreviousX = x;
			mPreviousY = y;

			return true;
		}
	}

}