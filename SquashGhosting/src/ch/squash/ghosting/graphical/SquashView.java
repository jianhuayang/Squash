package ch.squash.ghosting.graphical;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import ch.squash.ghosting.setting.Settings;

public class SquashView extends GLSurfaceView {
    private SquashRenderer mRenderer;
    private static SquashView mInstance;
    
    private final static String TAG = SquashView.class.getSimpleName();
    
    public SquashView(Context context) {
        super(context);
        mInstance = this;
        mRenderer = new SquashRenderer();
        setRenderer(mRenderer);
        
        Log.i(TAG, "SquashView created");
    }

    public static void setOverallCornerVisibility(){
    	String cornerLayout = Settings.getCorners();
    	if (cornerLayout.equals("custom"))
    		return;

    	setCornerVisible(0, cornerLayout.equals("10") || cornerLayout.equals("6") || cornerLayout.equals("front") || cornerLayout.equals("left leg"));
    	setCornerVisible(1, cornerLayout.equals("10") || cornerLayout.equals("6") || cornerLayout.equals("front") || cornerLayout.equals("right leg"));
    	setCornerVisible(2, cornerLayout.equals("10") || cornerLayout.equals("front") || cornerLayout.equals("right leg"));
    	setCornerVisible(3, cornerLayout.equals("10") || cornerLayout.equals("front") || cornerLayout.equals("left leg"));
    	setCornerVisible(4, cornerLayout.equals("10") || cornerLayout.equals("6") || cornerLayout.equals("front") || cornerLayout.equals("back") || cornerLayout.equals("right leg"));
    	setCornerVisible(5, cornerLayout.equals("10") || cornerLayout.equals("6") || cornerLayout.equals("front") || cornerLayout.equals("back") || cornerLayout.equals("left leg"));
    	setCornerVisible(6, cornerLayout.equals("10") || cornerLayout.equals("back") || cornerLayout.equals("right leg"));
    	setCornerVisible(7, cornerLayout.equals("10") || cornerLayout.equals("back") || cornerLayout.equals("left leg"));
    	setCornerVisible(8, cornerLayout.equals("10") || cornerLayout.equals("6") || cornerLayout.equals("back") || cornerLayout.equals("left leg"));
    	setCornerVisible(9, cornerLayout.equals("10") || cornerLayout.equals("6") || cornerLayout.equals("back") || cornerLayout.equals("right leg"));

//    	Log.i(TAG, "set corner visibility to " + cornerLayout);
    }
    
	public static void setCornerVisible(int corner, boolean visible) {
		mInstance.mRenderer.setCornerVisible(mInstance, corner, visible);
	}
	public static boolean getCornerVisible(int corner) {
		return mInstance.mRenderer.getCornerVisible(corner);
	}
	public static void setCornerColor(int corner, int color){
		setCornerColor(corner, (float)((color & 0xff0000) >> 16) / 255, (float)((color & 0xff00) >> 8) / 255, (float)((color & 0xff)) / 255, 1);
	}
	public static void setCornerColor(int corner, float r, float g, float b, float a){
//		Log.i(TAG, "Setting color of corner " + corner + " to r=" + r + ", g=" + g + ", b=" + b);
		mInstance.mRenderer.setCornerColor(mInstance, corner, r, g, b, a);
	}
	public static void clearCorners() {
		for (int i = 0; i < 10; i++)
			setCornerVisible(i, false);
	}
}