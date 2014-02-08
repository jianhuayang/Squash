package ch.squash.simulation.main;

import java.util.Calendar;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import ch.squash.simulation.R;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.shapes.common.Movable;

public final class MovementEngine {
	private final static String TAG = MovementEngine.class.getSimpleName();
	private final Movable[] mMovables;
	private boolean isRunning;
	private static MovementEngine mInstance;
	private final static Object LOCK = new Object();
	private final static int INTERVAL = 10; // ms
	public final static int DELAY_BETWEEN_MOVEMENTS = 50; // ms
	private final static int ENGINE_DURATION = 4000;		// ms
	public final static int SLOW_FACTOR = 1;
	public final static float AIR_FRICTION_FACTOR = 0.99f;
	public final static float COLLISION_FRICTION_FACTOR = 0.75f;
	private final int mSoundBounce;
//	private final int mSoundFloor;
	private final int mSoundFrontWall;
	private final int mSoundTin;
	private final int mSoundBeep;
	private final SoundPool mSoundPool;

	public static void pause() {
		mInstance.isRunning = false;
	}

	public static void resume() {
		if (mInstance.isRunning)
			return;

		mInstance.isRunning = true;

		new Thread() {
			@Override
			public void run() {
				MovementEngine.mInstance.doWork();
			}
		}.start();
	}

	private void doWork() {
		Log.w(TAG, "New thread starting to do work of MovementEngine");
		
		for (final Movable im : mMovables)
			im.resetClock();

		final long end = Calendar.getInstance().getTimeInMillis() + ENGINE_DURATION;
		while (isRunning && Calendar.getInstance().getTimeInMillis() < end) {
			for (final Movable im : mMovables)
				im.move();

			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {		
				Log.e(TAG, "Error while sleepint", e);
			}
		}
		
		Log.w(TAG, "MovementEngine has stopped");
	}

	public static void playSound(final String desc) {
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
//				new Thread() {
//					@Override
//					public void run() {
//						mInstance.isRunning = true;
//						MovementEngine.mInstance.doWork();
//						mInstance.isRunning = false;
//					}
//				}.start();
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
			m.speed.setDirection(Settings.getBallStartSpeed());
	}

	public static void resetMovables() {
		for (final Movable im : mInstance.mMovables) {		// watch out with new movables...
			im.reset();
			im.speed.setDirection(Settings.getBallStartSpeed());
		}
	}

	public static void toggleRunning() {
		if (mInstance.isRunning)
			pause();
		else
			resume();
	}
}
