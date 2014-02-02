package ch.squash.ghosting.setting;

import java.util.Arrays;

import android.util.Log;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import ch.squash.ghosting.R;
import ch.squash.ghosting.main.MainActivity;

public class SettingViews {
	private final static String TAG = SettingViews.class.getSimpleName();

	// views
	private final NumberPicker numSeries;
	private final NumberPicker numBreak;
	private final NumberPicker numCornerTime;
	private final NumberPicker numCorners;
	private final NumberPicker numMinTime;
	private final NumberPicker numMaxTime;
	private final NumberPicker numTimeFront;
	private final NumberPicker numTimeBack;
	private final RadioButton radCorners;
	private final RadioButton radTime;
	private final RadioGroup radioGroup;

	// values
	private final static String[] CORNERS = { "custom", "10", "6", "front", "back",
		"right leg", "left leg" };
	private final String[] LONG_TIMES;
	private final String[] SHORT_TIMES;
	
	// listeners
	private final OnValueChangeListener valueChangeListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//			Log.i(TAG, "change of value detected: id " + picker.getId()
//					+ " from " + oldVal + " to " + newVal);
			switch (picker.getId()) {
			case R.id.numSeries:
				if (!Settings.setKeyValue("series", newVal))
					Log.e(TAG, "couldnt save new value for series, " + newVal);
				break;
			case R.id.numBreak:
				if (!Settings.setKeyValue("break", newVal))
					Log.e(TAG, "couldnt save new value for break, " + newVal);
				break;
			case R.id.numCornerTime:
				if (radCorners.isChecked()) {
					if (!Settings.setKeyValue("cornercount", newVal * 5 + 5))
						Log.e(TAG, "couldnt save new value for cornercount, "
								+ newVal * 5 + 5);
				} else {
					if (!Settings.setKeyValue("time", newVal * 5 + 5))
						Log.e(TAG, "couldnt save new value for time, " + newVal * 5 + 5);
				}
				break;
			case R.id.numCorners:
				if (!Settings.setKeyValue("corners", CORNERS[newVal]))
					Log.e(TAG, "couldnt save new value for corners, " + CORNERS[newVal]);
				break;
			case R.id.numMinTime:
				if (!Settings.setKeyValue("mintime", LONG_TIMES[newVal]))
					Log.e(TAG, "couldnt save new value for mintime, " + LONG_TIMES[newVal]);
				break;
			case R.id.numMaxTime:
				if (!Settings.setKeyValue("maxtime", LONG_TIMES[newVal]))
					Log.e(TAG, "couldnt save new value for maxtime, " + LONG_TIMES[newVal]);
				break;
			case R.id.numTimeFront:
				if (!Settings.setKeyValue("fronttime", SHORT_TIMES[newVal]))
					Log.e(TAG, "couldnt save new value for fronttime, "
							+ SHORT_TIMES[newVal]);
				break;
			case R.id.numTimeBack:
				if (!Settings.setKeyValue("backtime", SHORT_TIMES[newVal]))
					Log.e(TAG, "couldnt save new value for backtime, " + SHORT_TIMES[newVal]);
				break;
			default:
				Log.w(TAG,
						"unhandled change of value with id " + picker.getId()
								+ " from " + oldVal + " to " + newVal);
			}
		}
	};
	private final OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			String[] nums;
//			Log.i(TAG, "change of checked detected. new checked id: "
//					+ checkedId);

			if (!Settings.setKeyValue("cornersortime",
					checkedId == R.id.radCorners, boolean.class))
				Log.e(TAG, "couldnt save new value for cornersortime, "
						+ (checkedId == R.id.radCorners));

			switch (checkedId) {
			case R.id.radCorners:
				nums = new String[100 / 5];
				for (int i = 1; i <= 100 / 5; i++)
					nums[i - 1] = "" + (i * 5);

				numCornerTime.setMaxValue(nums.length - 1);
				numCornerTime.setMinValue(0);
				numCornerTime.setDisplayedValues(nums);
				numCornerTime.setValue(Settings.getCornerCount() / 5 - 1);

				if (!Settings.setKeyValue("cornersortime", true, boolean.class))
					Log.e(TAG,
							"couldnt save new value for cornersortime, " + true);
				break;
			case R.id.radTime:
				nums = new String[100 / 5];
				for (int i = 1; i <= 100 / 5; i++)
					nums[i - 1] = "" + (i * 5);

				numCornerTime.setMaxValue(nums.length - 1);
				numCornerTime.setMinValue(0);
				numCornerTime.setDisplayedValues(nums);
				numCornerTime.setValue(Settings.getCornerTime() / 5 - 1);

				if (!Settings
						.setKeyValue("cornersortime", false, boolean.class))
					Log.e(TAG,
							"couldnt save new value for cornersortime, " + false);
				break;
			default:
				Log.e(TAG, "unexpected radioubutton id: " + checkedId);
			}
		}
	};
	
	public static void initialize(){
		if (mInstance == null){
			mInstance = new SettingViews();
		}
	}
	
	private static SettingViews mInstance;
	
	private SettingViews() {
		// find views
		numSeries = (NumberPicker) MainActivity.getActivity().findViewById(R.id.numSeries);
		numBreak = (NumberPicker) MainActivity.getActivity().findViewById(R.id.numBreak);
		numCornerTime = (NumberPicker) MainActivity.getActivity().findViewById(R.id.numCornerTime);
		numCorners = (NumberPicker) MainActivity.getActivity().findViewById(R.id.numCorners);
		numMinTime = (NumberPicker) MainActivity.getActivity().findViewById(R.id.numMinTime);
		numMaxTime = (NumberPicker) MainActivity.getActivity().findViewById(R.id.numMaxTime);
		numTimeFront = (NumberPicker) MainActivity.getActivity().findViewById(R.id.numTimeFront);
		numTimeBack = (NumberPicker) MainActivity.getActivity().findViewById(R.id.numTimeBack);

		radCorners = (RadioButton) MainActivity.getActivity().findViewById(R.id.radCorners);
		radTime = (RadioButton) MainActivity.getActivity().findViewById(R.id.radTime);

		radioGroup = ((RadioGroup) MainActivity.getActivity().findViewById(R.id.radioGroup1));
		
		// calculate some final values
		LONG_TIMES = new String[4 * 9 + 1];
		for (int i = 0; i < LONG_TIMES.length; i++)
			LONG_TIMES[i] = "" + (1f + 0.25f * i);

		SHORT_TIMES = new String[4 * 5 + 1];
		for (int i = 0; i < SHORT_TIMES.length; i++)
			SHORT_TIMES[i] = "" + (0.25f * i);
		
		// set range, value, etc
		initializeViews();

	}
	
	private void initializeViews(){
		// series: values from 1 to 20
		numSeries.setMinValue(1);
		numSeries.setMaxValue(20);
		numSeries.setValue(Settings.getSeries());
		numSeries.setOnValueChangedListener(valueChangeListener);
		numSeries.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		// break: values from 10 to 100
		numBreak.setOnValueChangedListener(valueChangeListener);
		numBreak.setMinValue(10);
		numBreak.setMaxValue(100);
		numBreak.setValue(Settings.getBreak());
		numBreak.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		// cornertime: handled in checkedChangedListener 
		numCornerTime.setOnValueChangedListener(valueChangeListener);
		numCornerTime.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		// radiogroup: bool
		boolean b = Settings.getCornersOrTime();
		radioGroup.setOnCheckedChangeListener(checkedChangeListener);
		radCorners.setChecked(b);
		radTime.setChecked(!b);
		checkedChangeListener.onCheckedChanged(radioGroup,
				b ? radCorners.getId() : radTime.getId());

		// corners: CORNERS
		numCorners.setMaxValue(CORNERS.length - 1);
		numCorners.setMinValue(0);
		numCorners.setDisplayedValues(CORNERS);
//		numCorners.setValue(Arrays.binarySearch(CORNERS, Settings.getCorners()));
		for (int i = 0; i < CORNERS.length; i++)
			if (CORNERS[i].equals(Settings.getCorners())){
				numCorners.setValue(i);
				break;
			}
		numCorners.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numCorners.setOnValueChangedListener(valueChangeListener);

		// min-/maxtime: LONG_TIMES
		numMinTime.setMaxValue(LONG_TIMES.length - 1);
		numMinTime.setMinValue(0);
		numMinTime.setDisplayedValues(LONG_TIMES);
		numMinTime.setValue(Arrays.binarySearch(LONG_TIMES, Settings.getMinTime()));
		numMinTime
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numMinTime.setOnValueChangedListener(valueChangeListener);

		numMaxTime.setMaxValue(LONG_TIMES.length - 1);
		numMaxTime.setMinValue(0);
		numMaxTime.setDisplayedValues(LONG_TIMES);
		numMaxTime
				.setValue(Arrays.binarySearch(LONG_TIMES, Settings.getMaxTime()));
		numMaxTime
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numMaxTime.setOnValueChangedListener(valueChangeListener);

		// front-/backtime: SHORT_TIMES
		numTimeFront.setMaxValue(SHORT_TIMES.length - 1);
		numTimeFront.setMinValue(0);
		numTimeFront.setDisplayedValues(SHORT_TIMES);
		numTimeFront.setValue(Arrays.binarySearch(SHORT_TIMES,
				Settings.getFrontTime()));
		numTimeFront
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numTimeFront.setOnValueChangedListener(valueChangeListener);

		numTimeBack.setMaxValue(SHORT_TIMES.length - 1);
		numTimeBack.setMinValue(0);
		numTimeBack.setDisplayedValues(SHORT_TIMES);
		numTimeBack.setValue(Arrays.binarySearch(SHORT_TIMES,
				Settings.getBackTime()));
		numTimeBack
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numTimeBack.setOnValueChangedListener(valueChangeListener);
	}
}
