package ch.squash.simulation.shapes.shapes;

import android.opengl.GLES20;
import ch.squash.simulation.shapes.common.AbstractShape;
import ch.squash.simulation.shapes.common.SolidType;

public class Chair extends AbstractShape {
	private final static int EDGE_COUNT = 288;
	
	public Chair(final String tag, final float x, final float y, final float z, final float length, final float width, final float height, final float[] color) {
		super(tag, x, y, z, getVertices(length, width, height), color);

		initialize(GLES20.GL_TRIANGLES, SolidType.OTHER, null);
	}

	private static float[] getVertices(final float length, final float width, final float height) {
		final float[] edges = new float[3 * EDGE_COUNT];
		
		final float[] stick = getStick((length + width) / 2 / 10, height / 3);
		final float[] seat = getSeat(length, width, height / 3, height / 10);
		final float[] back = getBack(length, width, height * 13 / 30, height, height / 10);
		
		System.arraycopy(stick, 0, edges, 0, stick.length);
		System.arraycopy(seat, 0, edges, stick.length, seat.length);
		System.arraycopy(back, 0, edges, stick.length + seat.length, back.length);
		
		return edges;
	}

	private static float[] getBack(final float length, final float width, final float startHeight, final float endHeight, final float thick){
		final float[] edges = new float[90];
		
		final float halfLength = length / 2;
		final float halfWidth = width / 2;
		final float startLength = thick - halfLength;

		// front
		edges[ 0] = -halfWidth;
		edges[ 1] = startHeight;
		edges[ 2] = startLength;
		edges[ 3] = halfWidth;
		edges[ 4] = startHeight;
		edges[ 5] = startLength;
		edges[ 6] = halfWidth;
		edges[ 7] = endHeight;
		edges[ 8] = startLength;

		edges[ 9] = halfWidth;
		edges[10] = endHeight;
		edges[11] = startLength;
		edges[12] = -halfWidth;
		edges[13] = endHeight;
		edges[14] = startLength;
		edges[15] = -halfWidth;
		edges[16] = startHeight;
		edges[17] = startLength;

		// right
		edges[18] = halfWidth;
		edges[19] = startHeight;
		edges[20] = startLength;
		edges[21] = halfWidth;
		edges[22] = startHeight;
		edges[23] = -halfLength;
		edges[24] = halfWidth;
		edges[25] = endHeight;
		edges[26] = -halfLength;

		edges[27] = halfWidth;
		edges[28] = endHeight;
		edges[29] = -halfLength;
		edges[30] = halfWidth;
		edges[31] = endHeight;
		edges[32] = startLength;
		edges[33] = halfWidth;
		edges[34] = startHeight;
		edges[35] = startLength;

		// back
		edges[36] = halfWidth;
		edges[37] = startHeight;
		edges[38] = -halfLength;
		edges[39] = -halfWidth;
		edges[40] = startHeight;
		edges[41] = -halfLength;
		edges[42] = -halfWidth;
		edges[43] = endHeight;
		edges[44] = -halfLength;
		
		edges[45] = -halfWidth;
		edges[46] = endHeight;
		edges[47] = -halfLength;
		edges[48] = halfWidth;
		edges[49] = endHeight;
		edges[50] = -halfLength;
		edges[51] = halfWidth;
		edges[52] = startHeight;
		edges[53] = -halfLength;

		// left
		edges[54] = -halfWidth;
		edges[55] = startHeight;
		edges[56] = -halfLength;
		edges[57] = -halfWidth;
		edges[58] = startHeight;
		edges[59] = startLength;
		edges[60] = -halfWidth;
		edges[61] = endHeight;
		edges[62] = startLength;
		
		edges[63] = -halfWidth;
		edges[64] = endHeight;
		edges[65] = startLength;
		edges[66] = -halfWidth;
		edges[67] = endHeight;
		edges[68] = -halfLength;
		edges[69] = -halfWidth;
		edges[70] = startHeight;
		edges[71] = -halfLength;

		// top
		edges[72] = -halfWidth;
		edges[73] = endHeight;
		edges[74] = startLength;
		edges[75] = halfWidth;
		edges[76] = endHeight;
		edges[77] = startLength;
		edges[78] = halfWidth;
		edges[79] = endHeight;
		edges[80] = -halfLength;
		
		edges[81] = halfWidth;
		edges[82] = endHeight;
		edges[83] = -halfLength;
		edges[84] = -halfWidth;
		edges[85] = endHeight;
		edges[86] = -halfLength;
		edges[87] = -halfWidth;
		edges[88] = endHeight;
		edges[89] = startLength;
		
		return edges;
	}
	
	private static float[] getSeat(final float length, final float width, final float height, final float thick){
		final float[] edges = new float[108];
		
		final float halfLength = length / 2;
		final float halfWidth = width / 2;
		
		// bottom
		edges[ 0] = halfWidth;
		edges[ 1] = height;
		edges[ 2] = halfLength;
		edges[ 3] = -halfWidth;
		edges[ 4] = height;
		edges[ 5] = halfLength;
		edges[ 6] = -halfWidth;
		edges[ 7] = height;
		edges[ 8] = -halfLength;

		edges[ 9] = -halfWidth;
		edges[10] = height;
		edges[11] = -halfLength;
		edges[12] = halfWidth;
		edges[13] = height;
		edges[14] = -halfLength;
		edges[15] = halfWidth;
		edges[16] = height;
		edges[17] = halfLength;

		// front
		edges[18] = -halfWidth;
		edges[19] = height;
		edges[20] = halfLength;
		edges[21] = halfWidth;
		edges[22] = height;
		edges[23] = halfLength;
		edges[24] = halfWidth;
		edges[25] = height + thick;
		edges[26] = halfLength;

		edges[27] = halfWidth;
		edges[28] = height + thick;
		edges[29] = halfLength;
		edges[30] = -halfWidth;
		edges[31] = height + thick;
		edges[32] = halfLength;
		edges[33] = -halfWidth;
		edges[34] = height;
		edges[35] = halfLength;

		// right
		edges[36] = halfWidth;
		edges[37] = height;
		edges[38] = halfLength;
		edges[39] = halfWidth;
		edges[40] = height;
		edges[41] = -halfLength;
		edges[42] = halfWidth;
		edges[43] = height + thick;
		edges[44] = -halfLength;
		
		edges[45] = halfWidth;
		edges[46] = height + thick;
		edges[47] = -halfLength;
		edges[48] = halfWidth;
		edges[49] = height + thick;
		edges[50] = halfLength;
		edges[51] = halfWidth;
		edges[52] = height;
		edges[53] = halfLength;

		// back
		edges[54] = halfWidth;
		edges[55] = height;
		edges[56] = -halfLength;
		edges[57] = -halfWidth;
		edges[58] = height;
		edges[59] = -halfLength;
		edges[60] = -halfWidth;
		edges[61] = height + thick;
		edges[62] = -halfLength;
		
		edges[63] = -halfWidth;
		edges[64] = height + thick;
		edges[65] = -halfLength;
		edges[66] = halfWidth;
		edges[67] = height + thick;
		edges[68] = -halfLength;
		edges[69] = halfWidth;
		edges[70] = height;
		edges[71] = -halfLength;

		// bottom
		edges[72] = -halfWidth;
		edges[73] = height;
		edges[74] = -halfLength;
		edges[75] = -halfWidth;
		edges[76] = height;
		edges[77] = halfLength;
		edges[78] = -halfWidth;
		edges[79] = height + thick;
		edges[80] = halfLength;
		
		edges[81] = -halfWidth;
		edges[82] = height + thick;
		edges[83] = halfLength;
		edges[84] = -halfWidth;
		edges[85] = height + thick;
		edges[86] = -halfLength;
		edges[87] = -halfWidth;
		edges[88] = height;
		edges[89] = -halfLength;

		// top
		edges[90] = -halfWidth;
		edges[91] = height + thick;
		edges[92] = halfLength;
		edges[93] = halfWidth;
		edges[94] = height + thick;
		edges[95] = halfLength;
		edges[96] = halfWidth;
		edges[97] = height + thick;
		edges[98] = -halfLength;
		
		edges[ 99] = halfWidth;
		edges[100] = height + thick;
		edges[101] = -halfLength;
		edges[102] = -halfWidth;
		edges[103] = height + thick;
		edges[104] = -halfLength;
		edges[105] = -halfWidth;
		edges[106] = height + thick;
		edges[107] = halfLength;
		
		return edges;
	}
	
	private static float[] getStick(final float width, final float height){
		final float[] edges = new float[90];
		final float halfWidth = width / 2;

		// front
		edges[ 0] = -halfWidth;
		edges[ 1] = 0;
		edges[ 2] = halfWidth;
		edges[ 3] = halfWidth;
		edges[ 4] = 0;
		edges[ 5] = halfWidth;
		edges[ 6] = halfWidth;
		edges[ 7] = height;
		edges[ 8] = halfWidth;

		edges[ 9] = halfWidth;
		edges[10] = height;
		edges[11] = halfWidth;
		edges[12] = -halfWidth;
		edges[13] = height;
		edges[14] = halfWidth;
		edges[15] = -halfWidth;
		edges[16] = 0;
		edges[17] = halfWidth;

		// right
		edges[18] = halfWidth;
		edges[19] = 0;
		edges[20] = halfWidth;
		edges[21] = halfWidth;
		edges[22] = 0;
		edges[23] = -halfWidth;
		edges[24] = halfWidth;
		edges[25] = height;
		edges[26] = -halfWidth;

		edges[27] = halfWidth;
		edges[28] = height;
		edges[29] = -halfWidth;
		edges[30] = halfWidth;
		edges[31] = height;
		edges[32] = halfWidth;
		edges[33] = halfWidth;
		edges[34] = 0;
		edges[35] = halfWidth;

		// back
		edges[36] = halfWidth;
		edges[37] = 0;
		edges[38] = -halfWidth;
		edges[39] = -halfWidth;
		edges[40] = 0;
		edges[41] = -halfWidth;
		edges[42] = -halfWidth;
		edges[43] = height;
		edges[44] = -halfWidth;
		
		edges[45] = -halfWidth;
		edges[46] = height;
		edges[47] = -halfWidth;
		edges[48] = halfWidth;
		edges[49] = height;
		edges[50] = -halfWidth;
		edges[51] = halfWidth;
		edges[52] = 0;
		edges[53] = -halfWidth;

		// left
		edges[54] = -halfWidth;
		edges[55] = 0;
		edges[56] = -halfWidth;
		edges[57] = -halfWidth;
		edges[58] = 0;
		edges[59] = halfWidth;
		edges[60] = -halfWidth;
		edges[61] = height;
		edges[62] = halfWidth;
		
		edges[63] = -halfWidth;
		edges[64] = height;
		edges[65] = halfWidth;
		edges[66] = -halfWidth;
		edges[67] = height;
		edges[68] = -halfWidth;
		edges[69] = -halfWidth;
		edges[70] = 0;
		edges[71] = -halfWidth;
		
		// bottom
		edges[72] = halfWidth;
		edges[73] = 0;
		edges[74] = halfWidth;
		edges[75] = -halfWidth;
		edges[76] = 0;
		edges[77] = halfWidth;
		edges[78] = -halfWidth;
		edges[79] = 0;
		edges[80] = -halfWidth;
		
		edges[81] = -halfWidth;
		edges[82] = 0;
		edges[83] = -halfWidth;
		edges[84] = halfWidth;
		edges[85] = 0;
		edges[86] = -halfWidth;
		edges[87] = halfWidth;
		edges[88] = 0;
		edges[89] = halfWidth;
		
		return edges;
	}

	@Override
	protected float[] getColorData(final float[] color) {
		final float[] result = new float[EDGE_COUNT * 3 * color.length];

		for (int i = 0; i < result.length / color.length; i++)
			System.arraycopy(color, 0, result, i * color.length, color.length);
		return result;
	}
}
