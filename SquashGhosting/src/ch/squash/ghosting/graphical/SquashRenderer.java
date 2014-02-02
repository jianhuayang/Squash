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
	private int courtLineVertexCount = 0;
	private ShortBuffer courtFillIndices;
	private int courtFillVertexCount = 0;

	private ShortBuffer[] cornerIndices;
	private FloatBuffer[] cornerVertices;
	private int[] cornerVertexCount;
	private boolean[] cornerVisible;
	private float[][] cornerColor;
	
	private final int cornerCorners = 50;

	private float _width = 320f;
	private float _height = 480f;

	public void setCornerVisible(View view, int corner, boolean visible) {
		cornerVisible[corner] = visible;
	}
	public boolean getCornerVisible(int corner) {
		return cornerVisible[corner];
	}
	public void setCornerColor(View view, int corner, float r, float g, float b, float a){
		cornerColor[corner] = new float[]{r, g, b, a};
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// preparation
		gl.glMatrixMode(GL10.GL_PROJECTION);
		float size = .01f * (float) Math.tan(Math.toRadians(45.0) / 2);
		float ratio = _width / _height;
		// perspective:
		gl.glFrustumf(-size, size, -size / ratio, size / ratio, 0.01f, 100.0f);
		// orthographic:
		gl.glViewport(0, 0, (int) _width, (int) _height);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		// define the color we want to be displayed as the "clipping wall"
		gl.glClearColor(1f, 1f, 1f, 0f);

		// enable the differentiation of which side may be visible
		gl.glEnable(GL10.GL_CULL_FACE);
		// which is the front? the one which is drawn counter clockwise
		gl.glFrontFace(GL10.GL_CCW);
		// which one should NOT be drawn
		gl.glCullFace(GL10.GL_BACK);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		drawCourtLines();
		drawCorners();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		_width = w;
		_height = h;
		gl.glViewport(0, 0, w, h);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// clear the color buffer and the depth buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// general init
		gl.glLoadIdentity();
		// gl.glTranslatef(0.0f, -1f, -1.0f + -1.5f * i);
		gl.glTranslatef(0f, 0.02f, -1.65f);

		gl.glLineWidth(255f);

		// corners
		for (int i = 0; i < cornerVertices.length; i++) {
			if (!cornerVisible[i])
				continue;

			gl.glColor4f(cornerColor[i][0], cornerColor[i][1], cornerColor[i][2], cornerColor[i][3]);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cornerVertices[i]);
			gl.glDrawElements(GL10.GL_TRIANGLES, cornerVertexCount[i],
					GL10.GL_UNSIGNED_SHORT, cornerIndices[i]);
		}

		// lines
		gl.glColor4f(0.8f, 0f, 0f, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, courtLineVertices);
		gl.glDrawElements(GL10.GL_LINE_LOOP, courtLineVertexCount,
				GL10.GL_UNSIGNED_SHORT, courtLineIndices);

		// fill
		gl.glColor4f(0.85f, 0.73f, 0.47f, 1f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, courtLineVertices);
		gl.glDrawElements(GL10.GL_TRIANGLES, courtFillVertexCount,
				GL10.GL_UNSIGNED_SHORT, courtFillIndices);
	}

	private void drawCourtLines() {
		float factor = 0.18f;

		float[] coords = { -3.2f * factor, -4.26f * factor, 0f * factor,
				0f * factor, -4.26f * factor, 0f * factor, 3.2f * factor,
				-4.26f * factor, 0f * factor,

				-3.2f * factor, -1.6f * factor, 0f * factor, -1.6f * factor,
				-1.6f * factor, 0f * factor, 1.6f * factor, -1.6f * factor,
				0f * factor, 3.2f * factor, -1.6f * factor, 0f * factor,

				-3.2f * factor, 0f * factor, 0f * factor, -1.6f * factor,
				0f * factor, 0f * factor, 0f * factor, 0f * factor,
				0f * factor, 1.6f * factor, 0f * factor, 0f * factor,
				3.2f * factor, 0f * factor, 0f * factor,

				-3.2f * factor, 5.49f * factor, 0f * factor, 3.2f * factor,
				5.49f * factor, 0f * factor,

				-3.2f * factor, 5.49f * factor, 0.48f * factor, 3.2f * factor,
				5.49f * factor, 0.48f * factor,

				-3.2f * factor, 5.49f * factor, 1.78f * factor, 3.2f * factor,
				5.49f * factor, 1.78f * factor,

				-3.2f * factor, -4.26f * factor, 2.13f * factor, 3.2f * factor,
				-4.26f * factor, 2.13f * factor,

				-3.2f * factor, 5.49f * factor, 4.57f * factor, 3.2f * factor,
				5.49f * factor, 4.57f * factor, };

		short[] indices = new short[] {
				// floor
				0, 3, 4, 8, 7, 3, 12, 13, 11, 10, 5, 6, 11, 2, 0, 1, 9, 8, 10,
				7 };
		
		short[] indices2 = new short[]{0, 13, 12, 0, 2, 13};

		courtLineVertexCount = indices.length;
		courtFillVertexCount = indices2.length;

		ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		courtLineVertices = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		courtLineIndices = ibb.asShortBuffer();

		ByteBuffer iibb = ByteBuffer.allocateDirect(indices2.length * 2);
		iibb.order(ByteOrder.nativeOrder());
		courtFillIndices = iibb.asShortBuffer();
		
		courtLineVertices.put(coords);
		courtLineIndices.put(indices);
		courtFillIndices.put(indices2);

		courtLineVertices.position(0);
		courtLineIndices.position(0);
		courtFillIndices.position(0);
	}

	private float[] getCornerCoordinates(float centerX, float centerY,
			float radius, float factor) {
		float[] coords = new float[(cornerCorners + 1) * 3];
		coords[0] = factor * centerX;
		coords[1] = factor * centerY;
		coords[2] = factor * 0;

		float degrees = 360f / cornerCorners;
		
		for (int j = 0; j < cornerCorners; j++) {
			coords[3 * j + 0 + 3] = factor
					* (centerX + (float) (radius * Math.cos((degrees / 2 + j * degrees)
							/ 180 * Math.PI)));
			coords[3 * j + 1 + 3] = factor
					* (centerY + (float) (radius * Math.sin((degrees / 2 + j * degrees)
							/ 180 * Math.PI)));
			coords[3 * j + 2 + 3] = factor * 0;
		}

		return coords;
	}

	private void drawCorners() {
		cornerIndices = new ShortBuffer[10];
		cornerVertexCount = new int[10];
		cornerVertices = new FloatBuffer[10];
		cornerColor = new float[10][];

		float factor = 0.18f;
		float radius = 0.5f;

		short[] indices = new short[cornerCorners * 3];

		for (short i = 0; i < cornerCorners; i++){
			indices[3 * i + 1] = (short)(i + 1);
			if (indices[3 * i + 1] >= cornerCorners + 1)
				indices[3 * i + 1] -= cornerCorners;
			indices[3 * i + 2] = (short)(i + 2);
			if (indices[3 * i + 2] >= cornerCorners + 1)
				indices[3 * i + 2] -= cornerCorners;
		}
		
		float[] vertices = null;

		for (int i = 0; i < 10; i++) {
			// int, bool
			cornerVertexCount[i] = indices.length;
			cornerColor[i] = new float[]{0f, 0f, 1f, 0.5f};

			// indices
			ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
			ibb.order(ByteOrder.nativeOrder());
			cornerIndices[i] = ibb.asShortBuffer();
			cornerIndices[i].put(indices);
			cornerIndices[i].position(0);

			// vertices
			switch (i) {
			case 0:
				vertices = getCornerCoordinates(3.2f - radius, 5.49f - radius,
						radius, factor);
				break;
			case 1:
				vertices = getCornerCoordinates(-3.2f + radius, 5.49f - radius,
						radius, factor);
				break;
			case 2:
				vertices = getCornerCoordinates(3.2f - radius, 1.6f + radius, radius,
						factor);
				break;
			case 3:
				vertices = getCornerCoordinates(-3.2f + radius, 1.6f + radius, radius,
						factor);
				break;
			case 4:
				vertices = getCornerCoordinates(3.2f - radius, 0f, radius,
						factor);
				break;
			case 5:
				vertices = getCornerCoordinates(-3.2f + radius, 0f, radius,
						factor);
				break;
			case 6:
				vertices = getCornerCoordinates(3.2f - radius, -1.6f, radius,
						factor);
				break;
			case 7:
				vertices = getCornerCoordinates(-3.2f + radius, -1.6f, radius,
						factor);
				break;
			case 8:
				vertices = getCornerCoordinates(3.2f - radius, -4.26f + radius,
						radius, factor);
				break;
			case 9:
				vertices = getCornerCoordinates(-3.2f + radius,
						-4.26f + radius, radius, factor);
				break;
			}

			// float has 4 bytes, coordinate * 4 bytes
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			cornerVertices[i] = vbb.asFloatBuffer();

			cornerVertices[i].put(vertices);
			cornerVertices[i].position(0);
		}
	}

	public SquashRenderer(){
		cornerVisible = new boolean[10];
	}
}