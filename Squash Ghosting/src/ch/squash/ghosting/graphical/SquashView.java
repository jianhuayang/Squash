package ch.squash.ghosting.graphical;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import ch.squash.ghosting.setting.Settings;

public class SquashView extends GLSurfaceView {
    private final SquashRenderer mRenderer;
    private static SquashView mInstance;
    
    private final static String TAG = SquashView.class.getSimpleName();
    
	private final static String CORNER_10 = "10";
	private final static String CORNER_6 = "6";
	private final static String CORNER_FRONT = "front";
	private final static String CORNER_BACK = "back";
	private final static String CORNER_LEFT_LEG = "left leg";
	private final static String CORNER_RIGHT_LEG = "right leg";
	private final static String CORNER_DEFENSIVE = "defensive";
	private final static String CORNER_MIDDLE = "middle";
    
    public SquashView(final Context context) {
        super(context);
        mInstance = this;
        mRenderer = new SquashRenderer();
        setRenderer(mRenderer);
        
        Log.i(TAG, "SquashView created");
    }

    public static void setOverallCornerVisibility(){
    	final String cornerLayout = Settings.getCorners();
    	if ("custom".equals(cornerLayout)){
    		return;
    	}
    	
    	setCornerVisible(0, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_6) || cornerLayout.equals(CORNER_FRONT) || cornerLayout.equals(CORNER_LEFT_LEG) || cornerLayout.equals(CORNER_DEFENSIVE));
    	setCornerVisible(1, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_6) || cornerLayout.equals(CORNER_FRONT) || cornerLayout.equals(CORNER_RIGHT_LEG) || cornerLayout.equals(CORNER_DEFENSIVE));
    	setCornerVisible(2, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_FRONT) || cornerLayout.equals(CORNER_RIGHT_LEG) || cornerLayout.equals(CORNER_MIDDLE));
    	setCornerVisible(3, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_FRONT) || cornerLayout.equals(CORNER_LEFT_LEG) || cornerLayout.equals(CORNER_MIDDLE));
    	setCornerVisible(4, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_6) || cornerLayout.equals(CORNER_FRONT) || cornerLayout.equals(CORNER_BACK) || cornerLayout.equals(CORNER_RIGHT_LEG) || cornerLayout.equals(CORNER_MIDDLE));
    	setCornerVisible(5, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_6) || cornerLayout.equals(CORNER_FRONT) || cornerLayout.equals(CORNER_BACK) || cornerLayout.equals(CORNER_LEFT_LEG) || cornerLayout.equals(CORNER_MIDDLE));
    	setCornerVisible(6, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_BACK) || cornerLayout.equals(CORNER_RIGHT_LEG) || cornerLayout.equals(CORNER_MIDDLE));
    	setCornerVisible(7, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_BACK) || cornerLayout.equals(CORNER_LEFT_LEG) || cornerLayout.equals(CORNER_MIDDLE));
    	setCornerVisible(8, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_6) || cornerLayout.equals(CORNER_BACK) || cornerLayout.equals(CORNER_LEFT_LEG) || cornerLayout.equals(CORNER_DEFENSIVE));
    	setCornerVisible(9, cornerLayout.equals(CORNER_10) || cornerLayout.equals(CORNER_6) || cornerLayout.equals(CORNER_BACK) || cornerLayout.equals(CORNER_RIGHT_LEG) || cornerLayout.equals(CORNER_DEFENSIVE));
    }
    
	public static void setCornerVisible(final int corner, final boolean visible) {
		mInstance.mRenderer.setCornerVisible(corner, visible);
	}
	public static boolean getCornerVisible(final int corner) {
		return mInstance.mRenderer.getCornerVisible(corner);
	}
	public static void setCornerColor(final int corner, final int color){
		setCornerColor(corner, (float)((color & 0xff0000) >> 16) / 255, (float)((color & 0xff00) >> 8) / 255, (float)((color & 0xff)) / 255, 1);
	}
	public static void setCornerColor(final int corner, final float red, final float green, final float blue, final float alpha){
		mInstance.mRenderer.setCornerColor(mInstance, corner, red, green, blue, alpha);
	}
	public static void clearCorners() {
		for (int i = 0; i < 10; i++){
			setCornerVisible(i, false);
		}
	}
	public static void setCornerLineWhite(final int corner, final boolean white){
		mInstance.mRenderer.setCornerLineWhite(corner, white);
	}
}