package ch.squash.ghosting.graphical;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.view.View;

public class SquashRenderer implements GLSurfaceView.Renderer {
	private ShortBuffer courtLineIndices;
	private FloatBuffer courtLineVertices;
	private ShortBuffer courtFillIndices;
	private int lineVertexCount;
	private int fillVertexCount;

	private ShortBuffer[] cornerIndices;
	private FloatBuffer[] cornerVertices;
	private int cornerVertexCount;
	private boolean[] cornerVisible;
	private float[][] cornerColor;
	private ShortBuffer[] cornerLineIndices;
	private FloatBuffer[] cornerLineVertices;
	private int cornerLineVertexCount;

	private final static int CORNER_CORNERS = 50;			// amount of vertices of the "round" corners 
	private final static float CORNER_RADIUS = 0.65f;

	private float mWidth = 320f;
	private float mHeight = 480f;

	private final static float DRAW_FACTOR = 0.18f;

	public void setCornerVisible(final View view, final int corner,
			final boolean visible) {
		cornerVisible[corner] = visible;
	}

	public boolean getCornerVisible(final int corner) {
		return cornerVisible[corner];
	}

	public void setCornerColor(final View view, final int corner,
			final float red, final float green, final float blue,
			final float alpha) {
		cornerColor[corner] = new float[] { red, green, blue, alpha };
	}

	@Override
	public void onSurfaceCreated(final GL10 glObject, final EGLConfig config) {
		// preparation
		glObject.glMatrixMode(GL10.GL_PROJECTION);
		final float size = .01f * (float) Math.tan(Math.toRadians(45.0) / 2);
		final float ratio = mWidth / mHeight;
		// perspective:
		glObject.glFrustumf(-size, size, -size / ratio, size / ratio, 0.01f,
				100.0f);
		// orthographic:
		glObject.glViewport(0, 0, (int) mWidth, (int) mHeight);
		glObject.glMatrixMode(GL10.GL_MODELVIEW);
		glObject.glEnable(GL10.GL_DEPTH_TEST);

		// define the color we want to be displayed as the "clipping wall"
		glObject.glClearColor(1f, 1f, 1f, 0f);

		// enable the differentiation of which side may be visible
		glObject.glEnable(GL10.GL_CULL_FACE);
		// which is the front? the one which is drawn counter clockwise
		glObject.glFrontFace(GL10.GL_CCW);
		// which one should NOT be drawn
		glObject.glCullFace(GL10.GL_BACK);

		glObject.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		drawCourtLines();
		drawCorners();
	}

	@Override
	public void onSurfaceChanged(final GL10 glObject, final int width,
			final int height) {
		mWidth = width;
		mHeight = height;
		glObject.glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(final GL10 glObject) {
		// clear the color buffer and the depth buffer
		glObject.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// general init
		glObject.glLoadIdentity();
		// gl.glTranslatef(0.0f, -1f, -1.0f + -1.5f * i);
		glObject.glTranslatef(0f, 0.02f, -1.65f);

		glObject.glLineWidth(255f);

		// corners
		for (int i = 0; i < cornerVertices.length; i++) {
			if (!cornerVisible[i]){
				continue;
			}

			glObject.glColor4f(0, 0, 0, 0);
			glObject.glVertexPointer(3, GL10.GL_FLOAT, 0, cornerLineVertices[i]);
			glObject.glDrawElements(GL10.GL_LINE_LOOP, cornerLineVertexCount,
					GL10.GL_UNSIGNED_SHORT, cornerLineIndices[i]);
			
			glObject.glColor4f(cornerColor[i][0], cornerColor[i][1],
					cornerColor[i][2], cornerColor[i][3]);
			glObject.glVertexPointer(3, GL10.GL_FLOAT, 0, cornerVertices[i]);
			glObject.glDrawElements(GL10.GL_TRIANGLES, cornerVertexCount,
					GL10.GL_UNSIGNED_SHORT, cornerIndices[i]);
		}

		// lines
		glObject.glColor4f(0.7f, 0f, 0f, 1f);
		glObject.glVertexPointer(3, GL10.GL_FLOAT, 0, courtLineVertices);
		glObject.glDrawElements(GL10.GL_LINE_LOOP, lineVertexCount,
				GL10.GL_UNSIGNED_SHORT, courtLineIndices);

		// fill
		glObject.glColor4f(1f, 0.9f, 0.6f, 1f);
		glObject.glVertexPointer(3, GL10.GL_FLOAT, 0, courtLineVertices);
		glObject.glDrawElements(GL10.GL_TRIANGLES, fillVertexCount,
				GL10.GL_UNSIGNED_SHORT, courtFillIndices);
	}

	private void drawCourtLines() {
		final float[] coords = { 
				-3.2f * DRAW_FACTOR,	-4.26f * DRAW_FACTOR,	0f * DRAW_FACTOR,
				0f * DRAW_FACTOR,		-4.26f * DRAW_FACTOR,	0f * DRAW_FACTOR,
				3.2f * DRAW_FACTOR,		-4.26f * DRAW_FACTOR,	0f * DRAW_FACTOR,

				-3.2f * DRAW_FACTOR,	-1.6f * DRAW_FACTOR,	0f * DRAW_FACTOR,
				-1.6f * DRAW_FACTOR,	-1.6f * DRAW_FACTOR,	0f * DRAW_FACTOR,
				1.6f * DRAW_FACTOR,		-1.6f * DRAW_FACTOR,	0f * DRAW_FACTOR,
				3.2f * DRAW_FACTOR,		-1.6f * DRAW_FACTOR,	0f * DRAW_FACTOR,

				-3.2f * DRAW_FACTOR,	0f * DRAW_FACTOR,		0f * DRAW_FACTOR,
				-1.6f * DRAW_FACTOR,	0f * DRAW_FACTOR, 		0f * DRAW_FACTOR,
				0f * DRAW_FACTOR, 		0f * DRAW_FACTOR,		0f * DRAW_FACTOR, 
				1.6f * DRAW_FACTOR,		0f * DRAW_FACTOR,		0f * DRAW_FACTOR,
				3.2f * DRAW_FACTOR, 	0f * DRAW_FACTOR,		0f * DRAW_FACTOR,

				-3.2f * DRAW_FACTOR,	5.49f * DRAW_FACTOR,	0f * DRAW_FACTOR,
				3.2f * DRAW_FACTOR,		5.49f * DRAW_FACTOR,	0f * DRAW_FACTOR,

				-3.2f * DRAW_FACTOR,	5.49f * DRAW_FACTOR,	0.48f * DRAW_FACTOR,
				3.2f * DRAW_FACTOR,		5.49f * DRAW_FACTOR,	0.48f * DRAW_FACTOR,

				-3.2f * DRAW_FACTOR,	5.49f * DRAW_FACTOR,	1.78f * DRAW_FACTOR,
				3.2f * DRAW_FACTOR,		5.49f * DRAW_FACTOR,	1.78f * DRAW_FACTOR,

				-3.2f * DRAW_FACTOR,	-4.26f * DRAW_FACTOR,	2.13f * DRAW_FACTOR,
				3.2f * DRAW_FACTOR,		-4.26f * DRAW_FACTOR,	2.13f * DRAW_FACTOR,

				-3.2f * DRAW_FACTOR,	5.49f * DRAW_FACTOR,	4.57f * DRAW_FACTOR,
				3.2f * DRAW_FACTOR,		5.49f * DRAW_FACTOR,	4.57f * DRAW_FACTOR, };

		final short[] indices = new short[] {
				// floor
				0, 3, 4, 8, 7, 3, 12, 13, 11, 10, 5, 6, 11, 2, 0, 1, 9, 8, 10,
				7 };

		final short[] indices2 = new short[] { 0, 13, 12, 0, 2, 13 };

		lineVertexCount = indices.length;
		fillVertexCount = indices2.length;

		final ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		courtLineVertices = vbb.asFloatBuffer();

		final ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		courtLineIndices = ibb.asShortBuffer();

		final ByteBuffer iibb = ByteBuffer.allocateDirect(indices2.length * 2);
		iibb.order(ByteOrder.nativeOrder());
		courtFillIndices = iibb.asShortBuffer();

		courtLineVertices.put(coords);
		courtLineIndices.put(indices);
		courtFillIndices.put(indices2);

		courtLineVertices.position(0);
		courtLineIndices.position(0);
		courtFillIndices.position(0);
	}

	private float[] getCornerCoordinates(final float centerX,
			final float centerY, final float radius, final float factor) {
		final float[] coords = new float[(CORNER_CORNERS + 1) * 3];

		coords[0] = factor * centerX;
		coords[1] = factor * centerY;
		coords[2] = factor * 0;

		final float degrees = 360f / CORNER_CORNERS;

		for (int j = 0; j < CORNER_CORNERS; j++) {
			coords[3 * j + 0 + 3] = factor
					* (centerX + (float) (radius * Math.cos((degrees / 2 + j
							* degrees)
							/ 180 * Math.PI)));
			coords[3 * j + 1 + 3] = factor
					* (centerY + (float) (radius * Math.sin((degrees / 2 + j
							* degrees)
							/ 180 * Math.PI)));
			coords[3 * j + 2 + 3] = factor * 0;
		}

		return coords;
	}

	private void drawCorners() {
		cornerIndices = new ShortBuffer[10];
		cornerVertices = new FloatBuffer[10];
		cornerColor = new float[10][];
		
		cornerLineIndices = new ShortBuffer[10];
		cornerLineVertices = new FloatBuffer[10];

		short[] indices = new short[CORNER_CORNERS * 3];
		short[] lineIndices = new short[CORNER_CORNERS];
		for (short i = 0; i < CORNER_CORNERS; i++) {
			indices[3 * i + 1] = (short) (i + 1);
			if (indices[3 * i + 1] >= CORNER_CORNERS + 1)
				indices[3 * i + 1] -= CORNER_CORNERS;
			indices[3 * i + 2] = (short) (i + 2);
			if (indices[3 * i + 2] >= CORNER_CORNERS + 1)
				indices[3 * i + 2] -= CORNER_CORNERS;
			lineIndices[i] = (short) (i + 1);
		}
		
		cornerVertexCount = indices.length;
		cornerLineVertexCount = lineIndices.length;
		
		float[] vertices = null;
		float[] lineVertices = null;
		final float[] blueCornerColor = new float[] { 0f, 0f, 1f, 0.5f };

		for (int i = 0; i < 10; i++) {
			// int, bool
			cornerColor[i] = blueCornerColor;

			// indices
			final ByteBuffer ibb = ByteBuffer
					.allocateDirect(indices.length * 2);
			ibb.order(ByteOrder.nativeOrder());
			cornerIndices[i] = ibb.asShortBuffer();
			cornerIndices[i].put(indices);
			cornerIndices[i].position(0);
			
			final ByteBuffer bb = ByteBuffer.allocateDirect(lineIndices.length * 2);
			bb.order(ByteOrder.nativeOrder());
			cornerLineIndices[i] = bb.asShortBuffer();
			cornerLineIndices[i].put(lineIndices);
			cornerLineIndices[i].position(0);

			// vertices
			switch (i) {
			case 0:
				vertices = getCornerCoordinates(3.2f - CORNER_RADIUS, 5.49f - CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				lineVertices = getCornerCoordinates(3.2f - CORNER_RADIUS, 5.49f - CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				break;
			case 1:
				vertices = getCornerCoordinates(-3.2f + CORNER_RADIUS, 5.49f - CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				lineVertices = getCornerCoordinates(-3.2f + CORNER_RADIUS, 5.49f - CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				break;
			case 2:
				vertices = getCornerCoordinates(3.2f - CORNER_RADIUS, 1.6f + CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				lineVertices = getCornerCoordinates(3.2f - CORNER_RADIUS, 1.6f + CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				break;
			case 3:
				vertices = getCornerCoordinates(-3.2f + CORNER_RADIUS, 1.6f + CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				lineVertices = getCornerCoordinates(-3.2f + CORNER_RADIUS, 1.6f + CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				break;
			case 4:
				vertices = getCornerCoordinates(3.2f - CORNER_RADIUS, 0f, CORNER_RADIUS,
						DRAW_FACTOR);
				lineVertices = getCornerCoordinates(3.2f - CORNER_RADIUS, 0f, CORNER_RADIUS,
						DRAW_FACTOR);
				break;
			case 5:
				vertices = getCornerCoordinates(-3.2f + CORNER_RADIUS, 0f, CORNER_RADIUS,
						DRAW_FACTOR);
				lineVertices = getCornerCoordinates(-3.2f + CORNER_RADIUS, 0f, CORNER_RADIUS,
						DRAW_FACTOR);
				break;
			case 6:
				vertices = getCornerCoordinates(3.2f - CORNER_RADIUS, -1.6f, CORNER_RADIUS,
						DRAW_FACTOR);
				lineVertices = getCornerCoordinates(3.2f - CORNER_RADIUS, -1.6f, CORNER_RADIUS,
						DRAW_FACTOR);
				break;
			case 7:
				vertices = getCornerCoordinates(-3.2f + CORNER_RADIUS, -1.6f, CORNER_RADIUS,
						DRAW_FACTOR);
				lineVertices = getCornerCoordinates(-3.2f + CORNER_RADIUS, -1.6f, CORNER_RADIUS,
						DRAW_FACTOR);
				break;
			case 8:
				vertices = getCornerCoordinates(3.2f - CORNER_RADIUS, -4.26f + CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				lineVertices = getCornerCoordinates(3.2f - CORNER_RADIUS, -4.26f + CORNER_RADIUS,
						CORNER_RADIUS, DRAW_FACTOR);
				break;
			case 9:
				vertices = getCornerCoordinates(-3.2f + CORNER_RADIUS,
						-4.26f + CORNER_RADIUS, CORNER_RADIUS, DRAW_FACTOR);
				lineVertices = getCornerCoordinates(-3.2f + CORNER_RADIUS,
						-4.26f + CORNER_RADIUS, CORNER_RADIUS, DRAW_FACTOR);
				break;
			default:
			}

			// float has 4 bytes, coordinate * 4 bytes
			final ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			cornerVertices[i] = vbb.asFloatBuffer();
			cornerVertices[i].put(vertices);
			cornerVertices[i].position(0);

			final ByteBuffer vb = ByteBuffer.allocateDirect(lineVertices.length * 4);
			vb.order(ByteOrder.nativeOrder());
			cornerLineVertices[i] = vb.asFloatBuffer();
			cornerLineVertices[i].put(lineVertices);
			cornerLineVertices[i].position(0);
		}
	}

	public SquashRenderer() {
		cornerVisible = new boolean[10];
	}
}