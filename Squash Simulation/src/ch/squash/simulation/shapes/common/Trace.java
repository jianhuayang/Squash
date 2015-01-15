package ch.squash.simulation.shapes.common;

import ch.squash.simulation.graphic.ShaderType;
import android.opengl.GLES20;
import android.util.Log;

public class Trace extends AbstractShape {
	// constant
	private final static String TAG = Trace.class.getSimpleName();
	private final static float MIN_DISTANCE_OF_POINTS = 0.01f;
	private final static int MAX_TRACE_SEGMENTS = 50;
	
	// data
	private final IVector[] mPoints = new IVector[MAX_TRACE_SEGMENTS + 1];
	private final float[] mColor;
	
	// control
	private boolean isReset;
	private int mTraceIndex;

	// misc
	private Trace mNextTrace;

	public Trace(final String tag, final float[] color) {
		super(tag, 0, 0, 0, ShaderType.NO_LIGHT);
		
		mColor = color.clone();
		
		initialize(new float[MAX_TRACE_SEGMENTS * 2 * 3 * 2], getColorData(color), new float[0], GLES20.GL_LINES, SolidType.NONE, null);
	}

	public void addPoint(final IVector point){		
		if (mNextTrace != null && !mNextTrace.isReset){
			mNextTrace.addPoint(point);
			return;
		}
		
		if (mTraceIndex == 0){
			// always add new point into empty list
			mPoints[mTraceIndex++] = point;
			return;
		}
		else if (mTraceIndex == mPoints.length){
			// see if there's a re-usable trace
			if (mNextTrace == null){
				// start new trace if current list is full
				mNextTrace = new Trace(tag, mColor);
				Log.w(TAG, "Adding new trace to " + tag);
			}else{
				// reuse trace
				mNextTrace.isReset = false;
				Log.w(TAG, "Re-using old trace of " + tag);
			}
			mNextTrace.addPoint(point);
		}
		else if (AbstractShape.getPointPointDistance(mPoints[mTraceIndex - 1].getDirection(), point.getDirection()) >= MIN_DISTANCE_OF_POINTS)
			// add new point to current list if it's enough far away from last point in list
			mPoints[mTraceIndex] = point;
		else
			// return if nothing else must be done
			return;
		
		final float[] data = new float[]{ mPoints[mTraceIndex -1].getX(), mPoints[mTraceIndex -1].getY(),
				mPoints[mTraceIndex -1].getZ(), point.getX(), point.getY(), point.getZ() };
		
		mPositions.position(6 * (mTraceIndex++));
		mPositions.put(data);
		mPositions.position(0);
	}

	public void reset(){
		if (mNextTrace != null && !mNextTrace.isReset)
			mNextTrace.reset();
		
		mPositions.position(0);
		mPositions.put(new float[MAX_TRACE_SEGMENTS * 2 * 3 * 2]);
		mPositions.position(0);
		mTraceIndex = 0;
		
		isReset = true;
	}
	
	private float[] getColorData(final float[] color) {
		final float[] result = new float[(MAX_TRACE_SEGMENTS + 2) * 2 * color.length];

		for (int i = 0; i < result.length / color.length; i++)
			System.arraycopy(color, 0, result, i * color.length, color.length);
		return result;
	}
	
	@Override
	public void draw() {
		super.draw();
		
		if (mNextTrace != null && !mNextTrace.isReset)
			mNextTrace.draw();
	}

	@Override
	protected String getShapeTag() {
		return TAG;
	}
}
