package ch.squash.simulation.main;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.TextView;
import ch.squash.simulation.graphic.SquashRenderer;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.shapes.Ball;

public class SquashView extends GLSurfaceView implements SurfaceHolder.Callback {
	private final static String TAG = SquashView.class.getSimpleName();

	private static SquashView mInstance;

	// views
	private TextView mTxtHudFps;
	private TextView mTxtHudBall;

	private final static int UI_UPDATE_INTERVAL = 200; // ms
	
	// misc
	private boolean mIsUpdateUi = true;

	
	public static SquashView getInstance(){
		return mInstance;
	}
	
	public SquashView(Context context, AttributeSet attrs)
	{
	   super(context, attrs);

		mInstance = this;
		
		setEGLContextClientVersion(2);

		setRenderer(SquashRenderer.getInstance());

		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		Log.i(TAG, "SquashView created");
	}
	
	public void registerViews(final TextView hudFps, final TextView hudBall){
		mTxtHudFps = hudFps;
		mTxtHudBall = hudBall;
		
		updateUi();
	}

	@Override
	public void onPause() {
		super.onPause();
		MovementEngine.pause();
		
		mIsUpdateUi = false;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mInstance.updateUi();
	}

	private void updateUi() {
		mIsUpdateUi = true;
		
		new Thread() {
			@Override
			public void run() {
				while (mIsUpdateUi) {
					SquashActivity.getInstance().runOnUiThread(new Runnable() {
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
							mTxtHudBall.setText(locationString + "\n" + speedString);
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
