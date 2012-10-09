package net.geekzy.mobile.brewclock.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.geekzy.mobile.brewclock.R;
import net.geekzy.mobile.brewclock.activities.MainActivity;
import android.app.Activity;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	protected final CountDownLatch signal = new CountDownLatch(1);

	/** Constants **/
	protected static final int INITIAL_PROFILE_POS = 0;
	protected static final String INITIAL_PROFILE_SELECTION = "Earl Grey";
	protected static final String INITIAL_PROFILE_TIME = "3m";
	protected static final String ONE_MINUTE = "1m";
	protected static final String INIT_BREW_COUNT = "0";
	protected static final String BREW_COUNT_ONE = "1";
	protected static final String BUTTON_STOP = "Stop";
	protected static final String BUTTON_START = "Start";

	/** Properties **/
	protected Activity mActivity;
	protected Spinner mSpinnerPorfile;
	protected TextView mTextViewTime;
	protected TextView mTextViewCount;
	protected Button mButtonPlus;
	protected Button mButtonMinus;
	protected Button mButtonStart;

	public MainActivityTest(String name) {
		super(MainActivity.class);
		setName(name);
	}

	public MainActivityTest(Class<MainActivity> activityClass) {
		super(activityClass);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// this must be called before getActivity()
		// disabling touch mode allows for sending key events
		setActivityInitialTouchMode(false);
		// get the respective activity
		mActivity = getActivity();

		// Connect interface elements to properties
		mSpinnerPorfile = (Spinner) mActivity.findViewById(R.id.tea_spinner);
		mTextViewTime = (TextView) mActivity.findViewById(R.id.brew_time);
		mTextViewCount = (TextView) mActivity.findViewById(R.id.brew_count_label);
		mButtonPlus = (Button) mActivity.findViewById(R.id.brew_time_up);
		mButtonMinus = (Button) mActivity.findViewById(R.id.brew_time_down);
		mButtonStart = (Button) mActivity.findViewById(R.id.brew_start);
	}

	public void testInitAndStart() throws Throwable {
		// get selected spinner item as a Cursor
		Cursor cursor = (Cursor) mSpinnerPorfile.getSelectedItem();
		String mProfile = cursor.getString(1);
		String mTimeInit = (String) mTextViewTime.getText();

		// check selected profile name and value
		assertEquals(INITIAL_PROFILE_SELECTION, mProfile);
		assertEquals(INITIAL_PROFILE_TIME, mTimeInit);

		// set to 1m
		TouchUtils.tapView(this, mButtonMinus);
		TouchUtils.tapView(this, mButtonMinus);

		// check modified brewing time
		String mTimeOne = (String) mTextViewTime.getText();
		assertEquals(ONE_MINUTE, mTimeOne);

		// start brew
		TouchUtils.tapView(this, mButtonStart);

		signal.await(61, TimeUnit.SECONDS); // delay while brewing

		// check brew count label after completed brewing
		String mCount = (String) mTextViewCount.getText();
		assertEquals(BREW_COUNT_ONE, mCount);
	}

	public void testPickProfile() throws Throwable {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// set focus and selection to intial
				mSpinnerPorfile.requestFocus();
				mSpinnerPorfile.setSelection(INITIAL_PROFILE_POS);
			}
		});

		// scroll to index 2
		sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

		// check selected time
		String mTime = (String) mTextViewTime.getText();
		assertEquals(ONE_MINUTE, mTime);

		// start brew
		TouchUtils.tapView(this, mButtonStart);
		// check button start label should be switched to stop
		String mStop = (String) mButtonStart.getText();
		assertEquals(BUTTON_STOP, mStop);

		signal.await(10, TimeUnit.SECONDS); // delay while brewing

		// stop brew
		TouchUtils.tapView(this, mButtonStart);
		// check button stop label should be switched back to start
		String mStart = (String) mButtonStart.getText();
		assertEquals(BUTTON_START, mStart);

		// check brew count
		String mCount = (String) mTextViewCount.getText();
		assertEquals(INIT_BREW_COUNT, mCount);
	}
}
