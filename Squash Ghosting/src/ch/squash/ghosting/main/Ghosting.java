package ch.squash.ghosting.main;

import java.util.Arrays;
import java.util.Calendar;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import ch.squash.ghosting.R;
import ch.squash.ghosting.graphical.SquashView;
import ch.squash.ghosting.setting.Settings;

public final class Ghosting {
	private static Ghosting mInstance;
	private final ProgressBar progress;
	private final Button butStart;
	private final TextView txtShotDuration;
	private final TextView txtSeries;
	private final TextView txtShotsTime; // unused atm
	private final int mSoundBeep;
	private final SoundPool mSoundPool;

	private final static String TAG = Ghosting.class.getSimpleName();

	public static boolean running;

	private final static int UPDATE_INTERVAL = 20; // ms

	private static int[] corners;

	private final static Object LOCK = new Object();

	private int remaining;

	private Ghosting() {
		mInstance = this;

		progress = (ProgressBar) MainActivity.getActivity().findViewById(
				R.id.progress);
		butStart = (Button) MainActivity.getActivity().findViewById(
				R.id.butStart);
		txtShotDuration = (TextView) MainActivity.getActivity().findViewById(
				R.id.txtShotDuration);
		txtSeries = (TextView) MainActivity.getActivity().findViewById(
				R.id.txtSeries);
		txtShotsTime = (TextView) MainActivity.getActivity().findViewById(
				R.id.txtShotsTime);

		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		mSoundBeep = mSoundPool.load(MainActivity.getActivity(), R.raw.beep, 1);
		
		progress.setMax(100);
	}

	public static void startSession() {
		synchronized (LOCK) {
			if (mInstance == null) {
				mInstance = new Ghosting();
			}
		}

		// get active corners (before they're cleared!)
		final int[] corners = new int[10];
		int index = 0;

		for (int i = 0; i < 10; i++) {
			if (SquashView.getCornerVisible(i)) {
				corners[index++] = i;
			}
		}

		Ghosting.corners = Arrays.copyOf(corners, index);

		MainActivity.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				mInstance.prepareViews();
			}
		});

		running = true;

		new Thread() {
			@Override
			public void run() {
				try {
					runSession();
				} catch (Exception e) {
					Log.e(TAG, "Error during Ghosting session", e);
				} finally {
					cancelSession();
				}
			}
		}.start();
	}

	public static void cancelSession() {
		synchronized (LOCK) {
			if (mInstance == null) {
				mInstance = new Ghosting();
			}
		}

		MainActivity.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				mInstance.postpareViews();
			}
		});

		running = false;
	}

	private void prepareViews() {
		// keep awake
		MainActivity.getActivity().getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// button
		butStart.setText("Cancel");
		MainActivity.getActivity().setActionBarVisibility(false);

		// progress
		progress.setVisibility(View.VISIBLE);

		// corners
		SquashView.clearCorners();
	}

	private void postpareViews() {
		// no longer keep awake
		MainActivity.getActivity().getWindow()
				.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// button
		butStart.setText("Start Ghosting");
		MainActivity.getActivity().setActionBarVisibility(true);

		// progress
		progress.setVisibility(View.INVISIBLE);

		// textview
		txtShotDuration.setText("");
		txtShotsTime.setText("");
		txtSeries.setText("");

		// corners
		for (int i = 0; i < 10; i++) {
			SquashView.setCornerColor(i, 0, 0, 1, 1);
		}
		SquashView.setOverallCornerVisibility();
	}

	private static void runSession() {
		// read settings to reduce overhead during session
		final boolean cornersOrTime = Settings.isCornersOrTime();
		final float minDuration = Float.parseFloat(Settings.getMinTime());
		final float maxDuration = Float.parseFloat(Settings.getMaxTime());
		final int deltaSteps = (int) ((maxDuration - minDuration) * 4);
		final float frontDuration = Float.parseFloat(Settings.getFrontTime());
		final float backDuration = Float.parseFloat(Settings.getBackTime());
		final int totalSeries = Settings.getSeries();
		final int breakDuration = Settings.getBreak();
		
		int lastCorner = -1;
		boolean lastCornerWhite = false;
		
		final int preparationTime = 10000;	// 10s to prepare
		final int notificationTime = 3000;	// beep 3s before start
		
		// countdown
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Error while sleeping in countdown", e);
		}
		MainActivity.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				mInstance.progress.setMax(preparationTime);
			}
		});
		boolean notificationBeep = false;
		for (int i = 0; i < preparationTime / UPDATE_INTERVAL; i++) {
			if (!running)
				return;
			
			if (!notificationBeep && i >= (preparationTime - notificationTime) / UPDATE_INTERVAL){
				mInstance.mSoundPool.play(mInstance.mSoundBeep, 1, 1, 1, 0, 0.5f);
				notificationBeep = true;
			}
			
			final int finalI = i;
			MainActivity.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					mInstance.progress.setProgress(preparationTime - UPDATE_INTERVAL
							* finalI);
					mInstance.txtShotDuration.setText(Integer.toString((preparationTime
							- UPDATE_INTERVAL * finalI) / 1000)
							+ "s");
				}
			});
			
			try {
				Thread.sleep(UPDATE_INTERVAL);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error while sleeping in session progress update", e);
			}
		}

		for (int curSeries = 0; curSeries < totalSeries; curSeries++) {
			if (!running)
				return;
			
			Log.w(TAG, "Starting series " + (curSeries + 1) + " of "
					+ totalSeries);

			mInstance.remaining = cornersOrTime ? Settings.getCornerCount()
					: Settings.getCornerTime() * 1000; // ms

			final int finalCurSeries = curSeries + 1;
			MainActivity.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					mInstance.progress.setMax(Settings.isCornersOrTime() ? Settings
							.getCornerCount() : Settings.getCornerTime() * 1000);
					mInstance.progress.setProgress(mInstance.progress.getMax());
					mInstance.txtSeries.setText("Series " + finalCurSeries
							+ " / " + totalSeries);
				}
			});

			// if we're doing a time session, start thread that updates the
			// progressbar
			if (!cornersOrTime) {
				startTimeProgressBarThread();
			}

			while (mInstance.remaining > 0 && running) {
				final long start = Calendar.getInstance().getTimeInMillis();

				// if we're doing a corners session, update progressbar manually
				if (cornersOrTime) {
					MainActivity.getActivity().runOnUiThread(new Runnable() {
						public void run() {
							mInstance.progress
									.setProgress(mInstance.remaining - 1);
						}
					});
				}

				// draw new corner randomly
				final int curCorner = corners[die(corners.length) - 1];
				final int curSteps = die(deltaSteps + 1) - 1;
				float curDuration = minDuration + curSteps * 0.25f;
				if (curCorner < 2) {
					curDuration += frontDuration;
				}
				if (curCorner > 7) {
					curDuration += backDuration;
				}

				Log.i(TAG, "deltasteps=" + deltaSteps + ", steps=" + curSteps
						+ ", perc=" + (float) curSteps / deltaSteps
						+ ", duration=" + curDuration);

				// update views; corner and text that shows duration
				SquashView.setCornerLineWhite(curCorner, curCorner == lastCorner && !lastCornerWhite);
				lastCornerWhite = curCorner == lastCorner && !lastCornerWhite;
				lastCorner = curCorner;
				
				SquashView.setCornerColor(curCorner, getColor((float) curSteps
						/ deltaSteps));
				SquashView.setCornerVisible(curCorner, true);

				mInstance.mSoundPool.play(mInstance.mSoundBeep, 1, 1, 1, 0, 1);
				
				final String durationString = Float.toString(curDuration);
				MainActivity.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						mInstance.txtShotDuration.setText(durationString + "s");
					}
				});
				try {
					Thread.sleep((long) (curDuration * 1000));
				} catch (InterruptedException e) {
					Log.e(TAG, "Error while sleeping in break", e);
				}
				if (!running)
					return;

				// hide last corner
				SquashView.setCornerVisible(curCorner, false);

				if (cornersOrTime) {
					mInstance.remaining--;
				} else {
					mInstance.remaining -= Calendar.getInstance()
							.getTimeInMillis() - start;
				}
			}
			// series is done, make a low beep
			mInstance.mSoundPool.play(mInstance.mSoundBeep, 1, 1, 1, 0, 0.5f);

			MainActivity.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					mInstance.progress.setMax(breakDuration * 1000);
				}
			});

			// take a break :)
			if (running && curSeries < totalSeries - 1) {
				Log.w(TAG, "Starting break after series " + (curSeries + 1)
						+ " of " + totalSeries);
				final int series = curSeries + 1;
				MainActivity.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						mInstance.progress.setMax(breakDuration * 1000);
						mInstance.txtSeries.setText("Series " + series + " / "
								+ Settings.getSeries());
					}
				});
				notificationBeep = false;
				for (int i = 0; i < breakDuration * 1000 / UPDATE_INTERVAL
						&& running; i++) {

					if (!notificationBeep && i >= (breakDuration * 1000 - notificationTime + 1000) / UPDATE_INTERVAL){
						mInstance.mSoundPool.play(mInstance.mSoundBeep, 1, 1, 1, 0, 0.5f);
						notificationBeep = true;
					}
					
					final int finalI = i;
					MainActivity.getActivity().runOnUiThread(new Runnable() {
						public void run() {
							mInstance.progress.setProgress(breakDuration * 1000
									- UPDATE_INTERVAL * finalI);
							mInstance.txtShotDuration.setText(Integer
									.toString(breakDuration - UPDATE_INTERVAL
											* finalI / 1000)
									+ "s");
						}
					});
					try {
						Thread.sleep(UPDATE_INTERVAL);
					} catch (InterruptedException e) {
						Log.e(TAG, "Error while sleeping", e);
					}
				}

				MainActivity.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						mInstance.progress.setProgress(mInstance.progress
								.getMax());
						mInstance.txtShotDuration.setText("");
					}
				});
			}
		}
		Log.w(TAG, "Finished doing " + Settings.getSeries() + " series.");
	}

	private static void startTimeProgressBarThread() {
		new Thread() {
			@Override
			public void run() {
				final long end = Calendar.getInstance().getTimeInMillis()
						+ mInstance.remaining;
				while (running
						&& Calendar.getInstance().getTimeInMillis() < end) {
					MainActivity.getActivity().runOnUiThread(new Runnable() {
						public void run() {
							mInstance.progress
									.setProgress((int) (end - Calendar
											.getInstance().getTimeInMillis()));
						}
					});
					try {
						Thread.sleep(UPDATE_INTERVAL);
					} catch (InterruptedException e) {
						Log.e(TAG, "Error while sleeping", e);
					}
				}
			}
		}.start();
	}

	private static int getColor(final float percentage) {
		float additional = percentage < 0.5f ? percentage : 1 - percentage;

		final int max = 180;		// should be 255 but I want darker colors for contrast (thus visibility)
		final int red = (int) ((1 - percentage + additional) * max) << 16;
		final int green = (int) ((percentage + additional) * max) << 8;

		return red + green;
	}

	public static int die(final int max) {
		return (int) (Math.random() * max + 1);
	}
}
