package net.geekzy.mobile.brewclock.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.geekzy.mobile.brewclock.R;
import net.geekzy.mobile.brewclock.activities.MainActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

import com.jayway.android.robotium.solo.Solo;

public class AddTeaTest extends ActivityInstrumentationTestCase2<MainActivity> {
	protected final CountDownLatch signal = new CountDownLatch(1);
	protected Solo solo;

	private static final String INPUT_TEA_NAME = "Tea Name";
	private static final String SOSRO = "Sosro";
	private static final String MENU_ADD_TEA = "Add Tea";
	private static final String MENU_SAVE = "Save";
	private static final int FIVE_ITEMS = 5;
	private static final int FOUR_ITEMS = 4;
	private static final String MENU_REMOVE_TEA = "Remove Tea";

	/** Properties **/
	protected EditText mAddTea;

	public AddTeaTest(String name) {
		super(MainActivity.class);
		setName(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testOpenAddBrew() throws Throwable {
		// access add tea screen
		solo.sendKey(Solo.MENU);
		solo.clickOnText(MENU_ADD_TEA);

		// check input tea name
		assertTrue(solo.searchText(INPUT_TEA_NAME));
		// input text into input tea name
		mAddTea = (EditText) solo.getView(R.id.tea_name);
		solo.enterText(mAddTea, SOSRO);

		// save
		solo.sendKey(Solo.MENU);
		solo.clickOnText(MENU_SAVE);

		signal.await(1, TimeUnit.SECONDS);

		// go back twice
		solo.goBack();
		solo.goBack();

		// check spinner should have 5 items
		Spinner mTeaSpinner = (Spinner) solo.getView(R.id.tea_spinner);
		int totalItem = mTeaSpinner.getAdapter().getCount();
		assertEquals(FIVE_ITEMS, totalItem);

		// load last saved profile
		solo.pressSpinnerItem(0, 4);

		// remove it
		solo.sendKey(Solo.MENU);
		solo.clickOnText(MENU_REMOVE_TEA);

		// validate again
		signal.await(100, TimeUnit.MILLISECONDS);
		totalItem = mTeaSpinner.getAdapter().getCount();
		assertEquals(FOUR_ITEMS, totalItem);

		signal.await(1, TimeUnit.SECONDS);
	}
}
