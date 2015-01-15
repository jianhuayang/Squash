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
}
