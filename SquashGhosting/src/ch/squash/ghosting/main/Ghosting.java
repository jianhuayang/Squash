package ch.squash.ghosting.main;

import java.util.Arrays;
import java.util.Calendar;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import ch.squash.ghosting.R;
import ch.squash.ghosting.graphical.SquashView;
import ch.squash.ghosting.setting.Settings;

public class Ghosting {
	private static Ghosting mInstance;
	private final ProgressBar progress;
	private final Button butStart;
	private final TextView txtShotDuration;
	private final TextView txtSeries;
	private final TextView txtShotsTime;	 // unused atm
	
	private final static String TAG = Ghosting.class.getSimpleName();
	
	public static boolean running;
	
	private final static int PROGRESS_UPDATE_INTERVAL = 20;		// ms
	
	private static int[] corners;
	
	private int remaining;
	
	private Ghosting(){
		mInstance = this;
		
		progress = (ProgressBar) MainActivity.getActivity().findViewById(R.id.progress);
		butStart = (Button) MainActivity.getActivity().findViewById(R.id.butStart);
		txtShotDuration = (TextView) MainActivity.getActivity().findViewById(R.id.txtShotDuration);
		txtSeries = (TextView) MainActivity.getActivity().findViewById(R.id.txtSeries);
		txtShotsTime = (TextView) MainActivity.getActivity().findViewById(R.id.txtShotsTime);
		
		progress.setMax(100);
	}
	
	
	public static void startSession(){
		if (mInstance == null)
			mInstance = new Ghosting();
		
		// get active corners (before they're cleared!)
		int[] corners = new int[10];
		int index = 0;
		
		for (int i = 0; i < 10; i++)
			if (SquashView.getCornerVisible(i))
				corners[index++] = i;
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

	public static void cancelSession(){
		if (mInstance == null)
			mInstance = new Ghosting();
		
		MainActivity.getActivity().runOnUiThread(new Runnable() {
		    public void run() {
		    	mInstance.postpareViews();
		    }
		});
		
		running = false;
	}


	private void prepareViews(){
		// keep awake
		MainActivity.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// button
		butStart.setText("Cancel");
		MainActivity.getActivity().setActionBarVisibility(false);
		
		// progress
		progress.setVisibility(View.VISIBLE);
		
		// corners
		SquashView.clearCorners();
	}
	
	private void postpareViews(){
		// no longer keep awake
		MainActivity.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
		for (int i = 0; i < 10; i++)
			SquashView.setCornerColor(i, 0, 0, 1, 1);
		SquashView.setOverallCornerVisibility();
	}
	
	
	
	private static void runSession(){
		// read settings to reduce overhead during session
		final boolean cornersOrTime = Settings.getCornersOrTime();
		final float minDuration = Float.parseFloat(Settings.getMinTime());
		final float maxDuration = Float.parseFloat(Settings.getMaxTime());
		final int deltaSteps = (int) ((maxDuration - minDuration) * 4);
		final float frontDuration = Float.parseFloat(Settings.getFrontTime());
		final float backDuration = Float.parseFloat(Settings.getBackTime());
		final int totalSeries = Settings.getSeries();
		final int breakDuration = Settings.getBreak();
		
		// countdown
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Error while sleeping", e);
		}		
		MainActivity.getActivity().runOnUiThread(new Runnable(){
			public void run(){
				mInstance.progress.setMax(5000);
			}
		});
		for (int i = 0; i < 5000 / PROGRESS_UPDATE_INTERVAL; i++) {
			final int finalI = i;
			MainActivity.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					mInstance.progress.setProgress(5000 - PROGRESS_UPDATE_INTERVAL * finalI);
					mInstance.txtShotDuration.setText(Integer.toString(5 - PROGRESS_UPDATE_INTERVAL * finalI / 1000) + "s");
				}
			});
			try {
				Thread.sleep(PROGRESS_UPDATE_INTERVAL);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error while sleeping", e);
			}
		}
		
		for (int curSeries = 0; curSeries < totalSeries; curSeries++){
			Log.w(TAG, "Starting series " + (curSeries + 1) + " of " + totalSeries);

			mInstance.remaining = cornersOrTime ? Settings.getCornerCount()
					: Settings.getCornerTime() * 1000; // ms

			final int finalCurSeries = curSeries + 1;
			MainActivity.getActivity().runOnUiThread(new Runnable(){
				public void run(){
					mInstance.progress.setMax(Settings.getCornersOrTime() ? Settings.getCornerCount() : Settings.getCornerTime() * 1000);
					mInstance.progress.setProgress(mInstance.progress.getMax());
					mInstance.txtSeries.setText("Series " + finalCurSeries + " / " + totalSeries);
				}
			});
			
			// if we're doing a time session, start thread that updates the progressbar
			if (!cornersOrTime)
				startTimeProgressBarThread();

			while (mInstance.remaining > 0 && running) {
				final long start = Calendar.getInstance().getTimeInMillis();
				
				// if we're doing a corners session, update progressbar manually
				if (cornersOrTime)
					MainActivity.getActivity().runOnUiThread(new Runnable() {
						public void run() {
							mInstance.progress.setProgress(mInstance.remaining - 1);
						}
					});

				// draw new corner randomly
				int curCorner = corners[die(corners.length) - 1];
				int curSteps = die(deltaSteps + 1) - 1;
				float curDuration = minDuration + curSteps * 0.25f;
				if (curCorner < 2)
					curDuration += frontDuration;
				if (curCorner > 7)
					curDuration += backDuration;
	
				Log.i(TAG, "deltasteps=" + deltaSteps + ", steps=" + curSteps + ", perc=" + (float)curSteps / deltaSteps + ", duration=" + curDuration);
				
				// update views; corner and text that shows duration
				SquashView.setCornerColor(curCorner, getColor((float)curSteps / deltaSteps));
				SquashView.setCornerVisible(curCorner, true);
				final String durationString = Float.toString(curDuration);
				MainActivity.getActivity().runOnUiThread(new Runnable(){
					public void run(){
						mInstance.txtShotDuration.setText(durationString + "s");
					}
				});
				try {
					Thread.sleep((long)(curDuration * 1000));
				} catch (InterruptedException e) {
					Log.e(TAG, "Error while sleeping", e);
				}
				
				// hide last corner
				SquashView.setCornerVisible(curCorner, false);
				
				if (cornersOrTime)
					mInstance.remaining--;
				else
					mInstance.remaining -= Calendar.getInstance().getTimeInMillis() - start;
			}
			
			MainActivity.getActivity().runOnUiThread(new Runnable(){
				public void run(){
					mInstance.progress.setMax(breakDuration * 1000);
				}
			});
			
			// take a break :)
			if (running && curSeries < totalSeries - 1){
				Log.w(TAG, "Starting break after series " + (curSeries + 1) + " of " + totalSeries);
				final int series = curSeries + 1;
				MainActivity.getActivity().runOnUiThread(new Runnable(){
					public void run(){
						mInstance.progress.setMax(breakDuration * 1000);
						mInstance.txtSeries.setText("Series " + series + " / " + Settings.getSeries());
					}
				});
				for (int i = 0; i < breakDuration * 1000 / PROGRESS_UPDATE_INTERVAL && running; i++) {
					final int finalI = i;
					MainActivity.getActivity().runOnUiThread(new Runnable() {
						public void run() {
							mInstance.progress.setProgress(breakDuration * 1000 - PROGRESS_UPDATE_INTERVAL * finalI);
							mInstance.txtShotDuration.setText(Integer.toString(breakDuration - PROGRESS_UPDATE_INTERVAL * finalI / 1000) + "s");
						}
					});
					try {
						Thread.sleep(PROGRESS_UPDATE_INTERVAL);
					} catch (InterruptedException e) {
						Log.e(TAG, "Error while sleeping", e);
					}
				}
				
				MainActivity.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						mInstance.progress.setProgress(mInstance.progress.getMax());
						mInstance.txtShotDuration.setText("");
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.e(TAG, "Error while sleeping", e);
				}
			}
		}			
		Log.w(TAG, "Finished doing " + Settings.getSeries() + " series.");
	}

	private static void startTimeProgressBarThread() {
		new Thread() {
			@Override
			public void run() {
				final long end = Calendar.getInstance().getTimeInMillis() + mInstance.remaining;
				while (running && Calendar.getInstance().getTimeInMillis() < end) {
					MainActivity.getActivity().runOnUiThread(new Runnable() {
						public void run() {
							mInstance.progress.setProgress((int)(end - Calendar.getInstance().getTimeInMillis()));
						}
					});
					try {
						Thread.sleep(PROGRESS_UPDATE_INTERVAL);
					} catch (InterruptedException e) {
						Log.e(TAG, "Error while sleeping", e);
					}
				}
			}
		}.start();
	}

	private static int getColor(float percentage){
		float additional = percentage < 0.5f ? percentage : 1 - percentage;
		
		int r = (int)((1 - percentage + additional) * 255) << 16;
		int g = (int)((percentage + additional) * 255) << 8;
		
		return r + g;
	}
	
	public static int die(int max){
		return (int) (Math.random() * max + 1);
	}
}
