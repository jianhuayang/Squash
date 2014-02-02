package ch.squash.main;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import ch.squash.common.Settings;
import ch.squash.shapes.common.AbstractShape;
import ch.squash.shapes.common.IVector;
import ch.squash.shapes.common.Movable;
import ch.squash.shapes.common.ShapeCollection;
import ch.squash.shapes.common.Vector;
import ch.squash.shapes.shapes.Arrow;
import ch.squash.shapes.shapes.Ball;
import ch.squash.shapes.shapes.Cube;
import ch.squash.shapes.shapes.DottedLine;
import ch.squash.shapes.shapes.DummyShape;
import ch.squash.shapes.shapes.Tetrahedron;

public class SquashRenderer implements GLSurfaceView.Renderer {
	private static final String TAG = SquashRenderer.class.getSimpleName();

	// static access
	private static SquashRenderer mInstance;
	private final static Object LOCK = new Object();

	// matrices - camera and projection
	public float[] mViewMatrix = new float[16];
	public float[] mProjectionMatrix = new float[16];

	// handles
	public int mMVPMatrixHandle;
	public int mMVMatrixHandle;
	public int mPositionHandle;
	public int mColorHandle;
	private int mPerVertexProgramHandle;
	public int mPointProgramHandle;

	private final static int CYCLE_DURATION = 20000;
	
	// dimensions
	public final static float ONE_MM = 0.001f;
	public final static float ONE_CM = 0.01f;
	public final static float COURT_LINE_WIDTH = 5 * ONE_CM;

	// object-related
	public final static int OBJECT_COURT = 0;
	public final static int OBJECT_BALL = 1;
	public final static int OBJECT_AXIS = 2;
	public final static int OBJECT_MISC = 3;
	public final static int OBJECT_FORCE = 4;
	public final static int[] OBJECTS = new int[] { OBJECT_COURT, OBJECT_BALL,
			OBJECT_AXIS, OBJECT_MISC, OBJECT_FORCE };

	private final ShapeCollection[] mObjects;
	public AbstractShape[] courtSolids;

	// for dynamic movement
	public float angleInDegrees;
	private float oldAngle;
	public float mDeltaX;
	public float mDeltaY;

	public static SquashRenderer getInstance() {
		synchronized (LOCK) {
			if (mInstance == null)
				mInstance = new SquashRenderer();
		}
		return mInstance;
	}
	
	public SquashRenderer() {
		// add objects
		final ShapeCollection axis = new ShapeCollection();
		axis.addObject(new DottedLine("xAxis", -5, 0, 0, 5, 0, 0, 1f, new float[] { 1f,
				0f, 0f, 1f }), false);
		axis.addObject(new DottedLine("yAxis", 0, -5, 0, 0, 5, 0, 1f, new float[] { 0f,
				1f, 0f, 1f }), false);
		axis.addObject(new DottedLine("zAxis", 0, 0, -5, 0, 0, 5, 1f, new float[] { 0f,
				0f, 1f, 1f }), false);

		final ShapeCollection misc = new ShapeCollection();
		misc.addObject(new Cube("DummyCube", 0, 2, 0, 2), false);
		misc.addObject(new Tetrahedron("DummyTetrahedron", 2, 0, 0, 2), false);
		misc.addObject(new Arrow("DummyArrow", 0, -1, -5, 0, 1, -5,
				new float[] { 1, 1, 0, 1 }), false);

		mObjects = new ShapeCollection[] {
				new ShapeCollection(ShapeCollection.OBJECT_COLLECTION_COURT),
				new ShapeCollection(new Ball("SquashBall", -2, 2, -2, 40 * ONE_MM, 36,
						new float[] { 0, 0, 0, 1 }), false), axis, misc,
				new ShapeCollection(new DummyShape(), false) };

		courtSolids = new AbstractShape[] { mObjects[0].getOpaqueObjects().get(0),
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
//		movementEngine = new MovementEngine(
//				movables.toArray(new Movable[movables.size()]));

		Log.i(TAG, "SquashRenderer created");
	}

	private String getVertexShader() {
		return    "uniform mat4 u_MVPMatrix;      \n"
				+ "attribute vec4 a_Position;     \n"
				+ "attribute vec4 a_Color;        \n"
				+ "varying vec4 v_Color;          \n"
				+ "void main()                   \n"
				+ "{                             \n"
				+ "   v_Color = a_Color;          \n"
				+ "   gl_Position = u_MVPMatrix   \n"
				+ "               * a_Position;   \n"
				+ "}                             \n";
	}

	protected String getFragmentShader() {
		return    "precision mediump float;       \n"
				+ "varying vec4 v_Color;          \n"
				+ "void main()                   \n"
				+ "{                             \n"
				+ "   gl_FragColor = v_Color;     \n"
				+ "}                             \n";
	}

	// overriding methods
	@Override
	public void onSurfaceCreated(final GL10 glUnused, final EGLConfig config) {
		// Set the background clear color to black.
		GLES20.glClearColor(0, 0, 0, 0);
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

		final String vertexShader = getVertexShader();
		final String fragmentShader = getFragmentShader();

		final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER,
				vertexShader);
		final int fragmentShaderHandle = compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		mPerVertexProgramHandle = createAndLinkProgram(vertexShaderHandle,
				fragmentShaderHandle, new String[] { "a_Position", "a_Color" });

		// Define a simple shader program for our point.
		final String pointVertexShader = "uniform mat4 u_MVPMatrix;      \n"
				+ "attribute vec4 a_Position;     \n"
				+ "void main()                    \n"
				+ "{                              \n"
				+ "   gl_Position = u_MVPMatrix   \n"
				+ "               * a_Position;   \n"
				+ "   gl_PointSize = 5.0;         \n"
				+ "}                              \n";

		final String pointFragmentShader = "precision mediump float;       \n"
				+ "void main()                    \n"
				+ "{                              \n"
				+ "   gl_FragColor = vec4(1.0,    \n"
				+ "   1.0, 1.0, 1.0);             \n"
				+ "}                              \n";

		final int pointVertexShaderHandle = compileShader(
				GLES20.GL_VERTEX_SHADER, pointVertexShader);
		final int pointFragmentShaderHandle = compileShader(
				GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
		mPointProgramHandle = createAndLinkProgram(pointVertexShaderHandle,
				pointFragmentShaderHandle, new String[] { "a_Position" });

		resetCamera();

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

		// matrix, offset, left, right, bottom, top, near, far
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 20);

		Log.i(TAG, "Surface changed");
	}

	@Override
	public void onDrawFrame(final GL10 glUnused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Do a complete rotation every 10 seconds.
		final long time = SystemClock.uptimeMillis() % CYCLE_DURATION;
		angleInDegrees = (360.0f / CYCLE_DURATION) * ((int) time);

		if (Settings.isCameraRotating())
			Matrix.rotateM(mViewMatrix, 0, angleInDegrees - oldAngle, 0.0f,
					1.0f, 0.0f);
		else {
			resetCamera();
			Matrix.rotateM(mViewMatrix, 0, 90 * (Settings.getCameraMode() - 1),
					0.0f, 1.0f, 0.0f);
		}
		oldAngle = angleInDegrees;

		// Set our per-vertex lighting program.
		GLES20.glUseProgram(mPerVertexProgramHandle);

		// Set program handles for cube drawing.
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle,
				"u_MVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle,
				"u_MVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle,
				"a_Position");
		mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle,
				"a_Color");

		for (final ShapeCollection oc : mObjects)
			for (final AbstractShape object : oc.getOpaqueObjects())
				object.draw();

		GLES20.glDepthMask(false);
		for (final ShapeCollection oc : mObjects)
			for (final AbstractShape object : oc.getTransparentObjects())
				object.draw();
		GLES20.glDepthMask(true);
//		for (GlShape object : courtSolids)
//			object.draw();

		GLES20.glUseProgram(mPointProgramHandle);

		mDeltaX = 0.0f;
		mDeltaY = 0.0f;
	}

	// gl methods
	private int compileShader(final int shaderType, final String shaderSource) {
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) {
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS,
					compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {
				Log.e(TAG,
						"Error compiling shader: "
								+ GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0) {
			Log.e(TAG, "Error creating shader");
			return -1;
		}

		return shaderHandle;
	}

	private int createAndLinkProgram(final int vertexShaderHandle,
			final int fragmentShaderHandle, final String[] attributes) {
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) {
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);

			// Bind attributes
			if (attributes != null) {
				final int size = attributes.length;
				for (int i = 0; i < size; i++) {
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}
			}

			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS,
					linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) {
				Log.e(TAG,
						"Error compiling program: "
								+ GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0) {
			Log.e(TAG, "Error creating program");
			return -1;
		}

		return programHandle;
	}

	// own methods
	private void resetCamera() {
		Matrix.setIdentityM(mViewMatrix, 0);
//		 Matrix.translateM(mViewMatrix, 0, 0, -3f, -9f);
		Matrix.translateM(mViewMatrix, 0, 0, -1.5f, -2.75f);
	}

	public void setObjectVisibility(final int objectId, final boolean visible) {
		mObjects[objectId].setVisibility(visible);
	}
}
