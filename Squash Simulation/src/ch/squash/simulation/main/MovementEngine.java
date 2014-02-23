package ch.squash.simulation.main;

import java.util.Calendar;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Toast;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.shapes.common.Movable;

public final class MovementEngine {
	private final static String TAG = MovementEngine.class.getSimpleName();
	private final Movable[] mMovables;
	private boolean mIsRunning;
	private static MovementEngine mInstance;
	private final static Object LOCK = new Object();
	public final static int DELAY_BETWEEN_MOVEMENTS = 50; // ms
	private final static int ENGINE_DURATION = 500000;		// ms
	private final int mSoundBounce;
//	private final int mSoundFloor;
	private final int mSoundFrontWall;
	private final int mSoundTin;
	private final int mSoundBeep;
	private final SoundPool mSoundPool;

	private float mps;	// movements per second
	private long lastFrame;
	
	public static void pause() {
		mInstance.mIsRunning = false;
	}

	public static void resume() {
		if (mInstance.mIsRunning)
			return;

		mInstance.mIsRunning = true;

		new Thread() {
			@Override
			public void run() {
				MovementEngine.mInstance.doWork();
			}
		}.start();
	}

	public static float getMps(){
		return mInstance.mps;
	}
	
	private void doWork() {
		Log.w(TAG, "New thread starting to do work of MovementEngine");
		
		for (final Movable im : mMovables)
			im.resetClock();

		final long end = Calendar.getInstance().getTimeInMillis() + ENGINE_DURATION;
		while (mIsRunning && Calendar.getInstance().getTimeInMillis() < end) {
			for (final Movable im : mMovables)
				im.move();
			

			final long now = System.currentTimeMillis();
			final long delta = now - lastFrame;
			if (delta != 0)
				mps = 0.5f * (mps + 1000 / delta);
			lastFrame = now;
		}
		mIsRunning = false;
		
		Log.w(TAG, "MovementEngine has stopped");

		if (Calendar.getInstance().getTimeInMillis() >= end)
			SquashActivity.getInstance().runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(SquashActivity.getInstance(), "MovementEngine stopped automatically", Toast.LENGTH_SHORT).show();
				}
			});
		mps = 0;
	}

	public static void playSound(final String desc) {
		if (Settings.isMute())
			return;
		
		if ("floor inside".equals(desc))
			mInstance.mSoundPool.play(mInstance.mSoundBounce, 1, 1, 1, 0, 1);
		else if ("front wall inside".equals(desc))
			mInstance.mSoundPool.play(mInstance.mSoundFrontWall, 1, 1, 1, 0, 1);
		else if ("tin wall inside".equals(desc) || "tin line".equals(desc))
			mInstance.mSoundPool.play(mInstance.mSoundTin, 0.5f, 0.5f, 1, 0, 1);
		else
			mInstance.mSoundPool.play(mInstance.mSoundBeep, 0.05f, 0.05f, 1, 0, 1);
	}

	public static void initialize(final Movable[] movables) {
		synchronized (LOCK) {
			if (mInstance == null){
				mInstance = new MovementEngine(movables.clone());
			} 
			else {
				Log.e(TAG, "Can only have one movement engine, aborting...");
				return;
			}
		}
	}

	private MovementEngine(final Movable[] movables) {
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		mSoundBounce = mSoundPool.load(SquashActivity.getInstance(),
				R.raw.bounce, 1);
//		mSoundFloor = mSoundPool.load(SquashActivity.getInstance(),
//				R.raw.floor, 1);
		mSoundFrontWall = mSoundPool.load(SquashActivity.getInstance(),
				R.raw.frontwall, 1);
		mSoundTin = mSoundPool.load(SquashActivity.getInstance(),
				R.raw.tin, 1);
		mSoundBeep = mSoundPool.load(SquashActivity.getInstance(),
				R.raw.beep, 1);

		mMovables = movables.clone();

		for (final Movable m : mMovables)		// watch out with new movables...
			m.reset();
	}

	public static void resetMovables() {
		for (final Movable im : mInstance.mMovables) {
			im.reset();
		}
		Log.i(TAG, "Reset movables");
	}

	public static void toggleRunning() {
		if (mInstance.mIsRunning)
			pause();
		else
			resume();
	}
	
	public static boolean isRunning(){
		return mInstance.mIsRunning;
	}
}
