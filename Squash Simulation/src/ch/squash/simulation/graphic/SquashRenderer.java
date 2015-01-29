package ch.squash.simulation.graphic;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.main.MovementEngine;
import ch.squash.simulation.shapes.common.AbstractShape;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.common.Movable;
import ch.squash.simulation.shapes.common.ShapeCollection;
import ch.squash.simulation.shapes.common.Vector;
import ch.squash.simulation.shapes.shapes.Arrow;
import ch.squash.simulation.shapes.shapes.Ball;
import ch.squash.simulation.shapes.shapes.Cube;
import ch.squash.simulation.shapes.shapes.DottedLine;
import ch.squash.simulation.shapes.shapes.DummyShape;
import ch.squash.simulation.shapes.shapes.Point;
import ch.squash.simulation.shapes.shapes.Tetrahedron;

public class SquashRenderer implements GLSurfaceView.Renderer {
	// static
	private static final String TAG = SquashRenderer.class.getSimpleName();
	private static final Object LOCK = new Object();
	private static SquashRenderer mInstance;
	
	// constants
	// dimensions
	public final static float ONE_MM = 0.001f;
	public final static float ONE_CM = 0.01f;
	public final static float COURT_LINE_WIDTH = 5 * ONE_CM;
	private final static int CYCLE_DURATION = 20000;
	// object-related
	public final static int OBJECT_COURT = 0;
	public final static int OBJECT_BALL = 1;
	public final static int OBJECT_AXIS = 2;
	public final static int OBJECT_MISC = 3;
	public final static int OBJECT_FORCE = 4;
	public final static int OBJECT_ARENA = 5;
	public final static int OBJECT_CHAIRS = 6;
	public final static int[] OBJECTS = new int[] { OBJECT_COURT, OBJECT_BALL,
			OBJECT_AXIS, OBJECT_MISC, OBJECT_FORCE, OBJECT_ARENA, OBJECT_CHAIRS };

	// matrices - camera and projection
	public float[] mViewMatrix = new float[16];
	public float[] mProjectionMatrix = new float[16];

	// objects
	private final ShapeCollection[] mObjects;
	private AbstractShape[] mCourtSolids;
	private final Ball mSquashBall;
	private final Point mLight;
	
	// misc
	private float mFps;
	private long mLastFrame;
	
	// for dynamic movement
	private float mAngleInDegrees;
	private float mOldAngle;
	public boolean setCameraRotation = true;		// set rotation on startup
	
	// light
	public final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	private final float[] mLightPosInWorldSpace = new float[4];
	public final float[] mLightPosInEyeSpace = new float[4];
	public float[] mLightModelMatrix = new float[16];	

	
	// ctor
	private SquashRenderer() {
		final IVector ballStart = Settings.getBallStartPosition();
		mSquashBall = new Ball("SquashBall", ballStart.getX(), ballStart.getY(), ballStart.getZ(), 40 * ONE_MM, 36,
				new float[] { 0, 0, 0, 1 });
		mLight = new Point("Light", 0, 0, 0);
				
		// add objects
		final ShapeCollection axis = new ShapeCollection();
		axis.addObject(new DottedLine("xAxis", -5, 0, 0, 5, 0, 0, 1f, new float[] { 1f,
				0f, 0f, 1f }), false);
		axis.addObject(new DottedLine("yAxis", 0, -5, 0, 0, 5, 0, 1f, new float[] { 0f,
				1f, 0f, 1f }), false);
		axis.addObject(new DottedLine("zAxis", 0, 0, -5, 0, 0, 5, 1f, new float[] { 0f,
				0f, 1f, 1f }), false);

		final ShapeCollection misc = new ShapeCollection();
		misc.addObject(mLight, false);
		misc.addObject(new Cube("DummyCube", 0, 0, 0, 1), false);
		misc.addObject(new Tetrahedron("DummyTetrahedron", 2, 0, 0, 2), false);
		misc.addObject(new Arrow("DummyArrow", 0, -1, -5, 0, 1, -5,
				new float[] { 1, 1, 0, 1 }), false);

		mObjects = new ShapeCollection[] {
				new ShapeCollection(ShapeCollection.OBJECT_COLLECTION_COURT),
				new ShapeCollection(mSquashBall, false), axis, misc,
				new ShapeCollection(new DummyShape(), false),
				new ShapeCollection(ShapeCollection.OBJECT_COLLECTION_ARENA),
				new ShapeCollection(ShapeCollection.OBJECT_COLLECTION_CHAIRS) };

		mCourtSolids = new AbstractShape[] { mObjects[0].getOpaqueObjects().get(0),
				mObjects[0].getOpaqueObjects().get(2),
				mObjects[0].getOpaqueObjects().get(4),
				mObjects[0].getTransparentObjects().get(0),
				mObjects[0].getTransparentObjects().get(2),
				mObjects[0].getTransparentObjects().get(4),
				mObjects[0].getTransparentObjects().get(6) };

		for (final int i : OBJECTS)
			mObjects[i].setVisibility(Settings.isObjectCollectionVisible(i));

		final ArrayList<Movable> movables = new ArrayList<Movable>();
		for (final ShapeCollection sc : mObjects)
			for (final AbstractShape igs : sc.getAllShapes())
				if (igs.isMovable())
					movables.add(igs.getMovable());
		
		MovementEngine.initialize(movables.toArray(new Movable[movables.size()]));

		Log.i(TAG, "SquashRenderer created");
	}
	
	public void reCreateArena(){
		mObjects[OBJECT_ARENA].reCreate();
		mObjects[OBJECT_CHAIRS].reCreate();
	}

	// public access
	public static SquashRenderer getInstance() {
		synchronized (LOCK) {
			if (mInstance == null)
				mInstance = new SquashRenderer();
		}
		return mInstance;
	}

	// overridden methods
	@Override
	public void onSurfaceCreated(final GL10 glUnused, final EGLConfig config) {
		// Set the background clear color to black
		GLES20.glClearColor(1, 1, 1, 0);

		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// enable transparency
		GLES20.glEnable(GL10.GL_BLEND);
		GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Position the eye in front of the origin.
		final IVector eye = new Vector(0, 0, -0.5f);

		// We are looking toward the distance
		final IVector look = new Vector(0, 0, -5);

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final IVector up = new Vector(0, 1, 0);

		Matrix.setLookAtM(mViewMatrix, 0, eye.getX(), eye.getY(), eye.getZ(), look.getX(), look.getY(),
				look.getZ(), up.getX(), up.getY(), up.getZ());

		resetCamera();
		mLastFrame = System.currentTimeMillis();

		Log.i(TAG, "Surface created");
	}

	@Override
	public void onSurfaceChanged(final GL10 glUnused, final int width, final int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the
		// same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;

		// matrix, offset, left, right, bottom, top, near, far		<--- "far" determines how far into the distance that you can see!
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 40);

		Log.i(TAG, "Surface changed");
	}
	
	@Override
	public void onDrawFrame(final GL10 glUnused) {
		final long now = System.currentTimeMillis();
		final long delta = now - mLastFrame;
		if (delta != 0)
			mFps = 0.5f * (mFps + 1000 / delta);
		mLastFrame = now;
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Do a complete rotation every 10 seconds.
		final long time = SystemClock.uptimeMillis() % CYCLE_DURATION;
		mAngleInDegrees = (360.0f / CYCLE_DURATION) * ((int) time);

		if (Settings.isCameraRotating())
			Matrix.rotateM(mViewMatrix, 0, (mAngleInDegrees - mOldAngle) * Settings.getSpeedFactor(), 0.0f,
					1.0f, 0.0f);
		else if (setCameraRotation){
			setCameraRotation = false;
			Matrix.rotateM(mViewMatrix, 0, 90 * (Settings.getCameraMode() - 1),
					0.0f, 1.0f, 0.0f);	
		}
		mOldAngle = mAngleInDegrees;

	    
        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
//        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 1.5f, 0.0f);      
        Matrix.rotateM(mLightModelMatrix, 0, mAngleInDegrees * Settings.getSpeedFactor(), 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 1.0f, 1.5f);
               
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);                        

		System.arraycopy(mLightModelMatrix, 0, mLight.mModelMatrix, 0, mLightModelMatrix.length);
		
		for (final ShapeCollection oc : mObjects)
			for (final AbstractShape object : oc.getOpaqueObjects())
				object.draw();

		GLES20.glDepthMask(false);
		for (final ShapeCollection oc : mObjects)
			for (final AbstractShape object : oc.getTransparentObjects())
				object.draw();
		GLES20.glDepthMask(true);
	}

	// own methods
	public void resetCamera() {
		Matrix.setIdentityM(mViewMatrix, 0);
		
		final IVector camPos = Settings.getCameraPosition().multiply(-1); 
		Matrix.translateM(mViewMatrix, 0, camPos.getX(), camPos.getY(), camPos.getZ());
		
		setCameraRotation = true;
	}

	public void setObjectVisibility(final int objectId, final boolean visible) {
		mObjects[objectId].setVisibility(visible);
	}

	public void setBallPosition(final IVector ballStartPosition) {
		for (final ShapeCollection s : mObjects)
			for (final AbstractShape a : s.getOpaqueObjects())
				if ("SquashBall".equals(a.tag)){
					a.moveTo(Settings.getBallStartPosition());
					return;
				}
		Log.e(TAG, "Apparently, new ball position could not be set since the ball object couldn\'t be found...");
	}
	
	public static float getFps(){
		return mInstance.mFps;
	}
	
	public static Ball getSquashBall(){
		return mInstance.mSquashBall;
	}
	
	public static AbstractShape[] getCourtSolids(){
		return mInstance.mCourtSolids;
	}
}
