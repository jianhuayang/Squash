package ch.squash.ghosting.main;

import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import ch.squash.ghosting.R;
import ch.squash.ghosting.graphical.SquashView;
import ch.squash.ghosting.setting.SettingViews;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	private final static String TAG = MainActivity.class.getSimpleName();

	private static MainActivity mInstance;

	private ViewPager mViewPager;

	private SquashView mSquashView;

	private int mSquashViewHeight;

	public static MainActivity getActivity() {
		return mInstance;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.d(TAG, "starting initialization");

		// TODO: handle corner click events to select custom corner range

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mInstance = this;
		mSquashView = new SquashView(this);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each section
		final SectionsPagerAdapter mPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);

		// Enable swiping between tabs
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(final int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		Log.i(TAG, "initialization done");
	}

	public void onButStartClick(final View view) {
		// save "original" height with appropriate aspect ratio
		if (mSquashViewHeight == 0) {
			mSquashViewHeight = mSquashView.getHeight();
		}

		// toggle ghosting session
		if (Ghosting.running) {
			Ghosting.cancelSession();
		} else {
			Ghosting.startSession();
		}
	}

	@Override
	public void onTabSelected(final ActionBar.Tab tab,
			final FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());

		// ensure the proper corners are visible after possibly changing them
		if (tab.getPosition() == 0) {
			SquashView.setOverallCornerVisibility();
		}

		// ensure settings views are properly displayed
		if (tab.getPosition() == 1) {
			SettingViews.initialize();
		}
	}

	public void setActionBarVisibility(final boolean visible) {
		if (visible) {
			getActionBar().show();
		} else {
			getActionBar().hide();
			// ensure proper height (and aspect ratio) of squash view
			mSquashView.setLayoutParams(new LayoutParams(
					mSquashView.getWidth(), mSquashViewHeight));
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private final String TAG = SectionsPagerAdapter.class.getSimpleName();

		public SectionsPagerAdapter(final FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(final int position) {
			Fragment fragment = null;
			// select proper fragment
			if (position == 0) {
				fragment = new MainSectionFragment();
			} else if (position == 1) {
				fragment = new SettingsSectionFragment();
			} else {
				Log.e(TAG, "invalid position: " + position);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(final int position) {
			final Locale loc = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(loc);
			case 1:
				return getString(R.string.title_section2).toUpperCase(loc);
			default:
				Log.e(TAG, "Invalid tab position: " + position);
			}
			return null;
		}
	}

	public static class MainSectionFragment extends Fragment {
		@Override
		public View onCreateView(final LayoutInflater inflater,
				final ViewGroup container, final Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_main,
					container, false);

			// add squash view
			final LinearLayout linlay = (LinearLayout) rootView
					.findViewById(R.id.squashViewLayout);
			linlay.addView(MainActivity.getActivity().mSquashView);

			return rootView;
		}
	}

	public static class SettingsSectionFragment extends Fragment {
		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_settings,
					container, false);

			return rootView;
		}
	}

	// unused methods
	@Override
	public void onTabReselected(final Tab arg0, final FragmentTransaction arg1) {
		// unused
	}

	@Override
	public void onTabUnselected(final Tab arg0, final FragmentTransaction arg1) {
		// unused
	}
}
