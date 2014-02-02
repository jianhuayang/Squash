package ch.squash.simulation.shapes.shapes;

import android.opengl.GLES20;
import ch.squash.simulation.shapes.common.AbstractShape;

public class Cube extends AbstractShape {
	public Cube(final String tag, final float x, final float y, final float z, final float edge){
		super(tag, x, y, z, getVertices(edge), null);

		initialize(GLES20.GL_TRIANGLES, SolidType.NONE, null);
	}
	
	private static float[] getVertices(final float edge){
		final float halfEdge = edge / 2;
		// X, Y, Z
		return new float[]{
				// Front face
				0 - halfEdge, 0 + halfEdge, 0 + halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 + halfEdge, 0 + halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 + halfEdge, 0 + halfEdge,
				
				// Right face
				0 + halfEdge, 0 + halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 + halfEdge, 0 - halfEdge,
				0 + halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 - halfEdge, 0 - halfEdge,
				0 + halfEdge, 0 + halfEdge, 0 - halfEdge,
				
				// Back face
				0 + halfEdge, 0 + halfEdge, 0 - halfEdge,
				0 + halfEdge, 0 - halfEdge, 0 - halfEdge,
				0 - halfEdge, 0 + halfEdge, 0 - halfEdge,
				0 + halfEdge, 0 - halfEdge, 0 - halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 - halfEdge,
				0 - halfEdge, 0 + halfEdge, 0 - halfEdge,
				
				// Left face
				0 - halfEdge, 0 + halfEdge, 0 - halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 - halfEdge,
				0 - halfEdge, 0 + halfEdge, 0 + halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 - halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 - halfEdge, 0 + halfEdge, 0 + halfEdge,
				
				// Top face
				0 - halfEdge, 0 + halfEdge, 0 - halfEdge,
				0 - halfEdge, 0 + halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 + halfEdge, 0 - halfEdge,
				0 - halfEdge, 0 + halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 + halfEdge, 0 + halfEdge,
				0 + halfEdge, 0 + halfEdge, 0 - halfEdge,
				
				// Bottom face
				0 + halfEdge, 0 - halfEdge, 0 - halfEdge,
				0 + halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 - halfEdge,
				0 + halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 + halfEdge,
				0 - halfEdge, 0 - halfEdge, 0 - halfEdge,
		};
	}

	@Override
	protected float[] getColorData(final float[] color) {
		// R, G, B, A
		return new float[] 	{				
			// Front face (red)
			1.0f, 0.0f, 0.0f, 1.0f,				
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,				
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			
			// Right face (green)
			0.0f, 1.0f, 0.0f, 1.0f,				
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,				
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			
			// Back face (blue)
			0.0f, 0.0f, 1.0f, 1.0f,				
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,				
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			
			// Left face (yellow)
			1.0f, 1.0f, 0.0f, 1.0f,				
			1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f,				
			1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f,
			
			// Top face (cyan)
			0.0f, 1.0f, 1.0f, 1.0f,				
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,				
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			
			// Bottom face (magenta)
			1.0f, 0.0f, 1.0f, 1.0f,				
			1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 1.0f,				
			1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 1.0f
		};
	}
}
