package ch.squash.simulation.main;

import java.util.Calendar;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import ch.squash.simulation.R;
import ch.squash.simulation.shapes.common.Movable;

public final class MovementEngine {
	private final static String TAG = MovementEngine.class.getSimpleName();
	private final Movable[] mMovables;
	private boolean isRunning = true;
	private static MovementEngine mInstance;
	private final static Object LOCK = new Object();
	private final static int INTERVAL = 10; // ms
	public final static int DELAY_BETWEEN_MOVEMENTS = 10; // ms
	public final static int SLOW_FACTOR = 1;
	public final static float AIR_FRICTION_FACTOR = 0.99f;
	public final static float COLLISION_FRICTION_FACTOR = 0.75f;
	private static float[] startSpeed = new float[] { 0, 0, 0, }; //7, 1, -4 };
	private final int mSoundBounce;
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

		final long end = Calendar.getInstance().getTimeInMillis() + 2000;
		while (isRunning && Calendar.getInstance().getTimeInMillis() < end) {
			for (final Movable im : mMovables)
				im.move();

			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error while sleepint", e);
			}
		}
	}

	public static void playBounceSound() {
		mInstance.mSoundPool.play(mInstance.mSoundBounce, 1, 1, 1, 0, 1);
	}

	public static void initialize(final Movable[] movables) {
		synchronized (LOCK) {
			if (mInstance == null)
				mInstance = new MovementEngine(movables.clone());
			else {
				Log.e(TAG, "Can only have one movement engine, aborting...");
				return;
			}
		}
	}

	private MovementEngine(final Movable[] movables) {
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		mSoundBounce = mSoundPool.load(SquashActivity.getInstance(),
				R.raw.ball_bounce, 1);

		mMovables = movables.clone();

		for (final Movable m : mMovables)
			m.speed.setDirection(startSpeed[0], startSpeed[1], startSpeed[2]);

		new Thread() {
			@Override
			public void run() {
				MovementEngine.mInstance.doWork();
			}
		}.start();
	}

	public static void resetMovables() {
		for (final Movable im : mInstance.mMovables) {
			im.reset();
			im.speed.setDirection(startSpeed[0], startSpeed[1], startSpeed[2]);
		}
	}
}
