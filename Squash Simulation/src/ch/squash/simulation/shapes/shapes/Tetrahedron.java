package ch.squash.simulation.shapes.shapes;

import android.opengl.GLES20;
import ch.squash.simulation.shapes.common.AbstractShape;

public class Tetrahedron extends AbstractShape {
	
	public Tetrahedron(final String tag, final float x, final float y, final float z, final float edge){
		super(tag, x, y, z, getVertices(edge), null);

		initialize(GLES20.GL_TRIANGLES, SolidType.NONE, null);
	}
	
	private static float[] getVertices(final float edge){
		final float halfEdge = edge / 2;
		final float radius = (float) (Math.sqrt(3) / 3 * edge);
		final float bottomHalf = (float) (Math.sqrt(Math.pow(radius, 2) - Math.pow(halfEdge, 2)));
		final float topHalf = 2 * bottomHalf;
		
		// X, Y, Z
		return new float[]{
				// Front face
				0 - halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 + halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 + 0,		  0 + topHalf, 0 + 0,
				
				// Right face
				0 + halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 + 0,		  0 - bottomHalf, 0 - topHalf,
				0 + 0, 		  0 + topHalf, 0 + 0,

				// Left face
				0 + 0,		  0 + topHalf, 0 + 0,
				0 + 0,		  0 - bottomHalf, 0 - topHalf,
				0 - halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				
				// Bottom face
				0 + halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 - halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 + 0,		  0 - bottomHalf, 0 - topHalf,
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
			
			// Right face (green)
			0.0f, 1.0f, 0.0f, 1.0f,				
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			
			// Left face (blue)
			0.0f, 0.0f, 1.0f, 1.0f,				
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			
			// Bottom face (white)
			1.0f, 1.0f, 1.0f, 1.0f,				
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f
		};
	}
}
