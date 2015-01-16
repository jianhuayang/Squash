package ch.squash.simulation.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.widget.TextView;
import android.widget.Toast;
import ch.squash.simulation.graphic.SquashRenderer;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.shapes.Ball;

@SuppressLint("ClickableViewAccessibility")
public class SquashView extends GLSurfaceView implements SurfaceHolder.Callback, OnGestureListener, OnDoubleTapListener {
	private final static String TAG = SquashView.class.getSimpleName();

	private static SquashView mInstance;

	// views
	private TextView mTxtHudFps;
	private TextView mTxtHudBall;

	private final static int UI_UPDATE_INTERVAL = 200; // ms
	
	// misc
	private boolean mIsUpdateUi = true;

    private GestureDetectorCompat mDetector; 
	
	public static SquashView getInstance(){
		return mInstance;
	}
	
	public SquashView(Context context, AttributeSet attrs)
	{
	   super(context, attrs);

		mInstance = this;
		
		// GL stuff
		setEGLContextClientVersion(2);
		setRenderer(SquashRenderer.getInstance());
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		// gesture stuff
        mDetector = new GestureDetectorCompat(context, this);
        mDetector.setOnDoubleTapListener(this);

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
						Log.e(TAG, "Error while sleeping", e);
					}
				}
			}
		}.start();
	}
	

    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
    	final float x = event.getX();
    	final float y = event.getY();
    	final float left = SquashView.getInstance().getLeft();
    	final float right = SquashView.getInstance().getRight();
    	final float top = SquashView.getInstance().getTop();
    	final float bottom = SquashView.getInstance().getBottom();
    	
    	// only do further handling if 
    	if (x >= left && x <= right &&
    			y >= top && y <= bottom) {
            this.mDetector.onTouchEvent(event);
    	}
    	
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) { 
//        Log.d(TAG,"onDown: " + event.toString()); 
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, 
            float velocityX, float velocityY) {
    	final float dx = event2.getX() - event1.getX();
    	final float dy = event2.getY() - event1.getY();

    	// y: negative means up
        Log.d(TAG, "onFling: dx=" + dx + ", dy=" + dy); // + event1.toString()+event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
//        Log.d(TAG, "onLongPress: " + event.toString()); 

    	MovementEngine.pause();
    	MovementEngine.resetMovables();
    	
    	Toast.makeText(SquashActivity.getInstance(), "Movables reset", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
//        Log.d(TAG, "onScroll: " + e1.toString()+e2.toString() + distanceX + distanceY);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
//        Log.d(TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
//        Log.d(TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
//        Log.d(TAG, "onDoubleTap: " + event.toString());
    	
    	if (MovementEngine.isRunning()) {
    		MovementEngine.pause();
    	}
    	
    	MovementEngine.setRandomDirection();
    	
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
//        Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
//        Log.d(TAG, "onSingleTapConfirmed: " + event.toString());

    	Toast.makeText(SquashActivity.getInstance(),
    			"MovementEngine " + (MovementEngine.isRunning() ? "stopped" : "started"), Toast.LENGTH_SHORT).show();
		MovementEngine.toggleRunning();
    	
        return true;
    }
}
