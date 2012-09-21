package net.geekzy.mobile.brewclock;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	/** Properties **/
	protected Button btnPlusTime;
	protected Button btnMinusTime;
	protected Button btnStartBrew;
	protected TextView txtCountLabel;
	protected TextView txtTimeLabel;

	protected int brewTime = 3;
	protected CountDownTimer timerCountDown;
	protected int brewCount = 0;
	protected boolean isBrewing = false;
	protected boolean finishedBrew = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Connect interface elements to properties
		btnPlusTime = (Button) findViewById(R.id.brew_time_up);
		btnMinusTime = (Button) findViewById(R.id.brew_time_down);
		btnStartBrew = (Button) findViewById(R.id.brew_start);
		txtCountLabel = (TextView) findViewById(R.id.brew_count_label);
		txtTimeLabel = (TextView) findViewById(R.id.brew_time);

		// Setup ClickListeners
		btnPlusTime.setOnClickListener(this);
		btnMinusTime.setOnClickListener(this);
		btnStartBrew.setOnClickListener(this);

		// Set the initial brew values
		setBrewCount(0);
		setBrewTime(3);
	}

	/**
	 * Set an absolute value for the number of minutes to brew.
	 * has no effect if a brew is currently running.
	 * @param minutes The number of minutes to brew.
	 */
	public void setBrewTime(int minutes) {
		// skip set time while brewing
		if (isBrewing) {
			return;
		}

		// set brew time in minutes
		brewTime = minutes;

		// no zero brew time values; default to 1
		if (brewTime < 1) {
			brewTime = 1;
		}

		txtTimeLabel.setText(String.valueOf(brewTime) + "m");
	}

	/**
	 * Set the number of brews that have been made,
	 * and update the interface.
	 * @param count the new number of the brews.
	 */
	public void setBrewCount(int count) {
		brewCount = count;
		txtCountLabel.setText(String.valueOf(brewCount));
	}

	/**
	 * Start the brew timer
	 */
	public void startBrew() {
		// create a new CountDownTimer to track the brew time
		timerCountDown = new CountDownTimer(brewTime * 60 * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// update timer label
				txtTimeLabel.setText(String.valueOf(millisUntilFinished / 1000) + "s");
			}

			@Override
			public void onFinish() {
				// reset flag
				isBrewing = false;
				// add brewing counter
				setBrewCount(++brewCount);
				// mark finished
				finishedBrew = true;

				// update timer label
				txtTimeLabel.setText(R.string.lbl_brew_up);
				// set button label to more
				btnStartBrew.setText(R.string.lbl_more);
			}
		};

		// start the timer
		timerCountDown.start();
		// change button label
		btnStartBrew.setText(R.string.lbl_stop);
		// set flag
		isBrewing = true;
	}

	/**
	 * Stop the brew timer
	 */
	public void stopBrew() {
		// cancel the timer
		if (timerCountDown != null) {
			timerCountDown.cancel();
		}

		// reset flag
		isBrewing = false;
		// reset label
		btnStartBrew.setText(R.string.lbl_start);
	}

	/**
	 * Reset to initial state to do more brewing
	 */
	public void moreBrew() {
		// reset brew time
		setBrewTime(3);
		// reset flag
		finishedBrew = false;
		// reset label
		btnStartBrew.setText(R.string.lbl_start);
	}

	@Override
	public void onClick(View v) {
		// plus button clicked
		if (v == btnPlusTime) {
			setBrewTime(++brewTime);
		}
		// minus button clicked
		else if (v == btnMinusTime) {
			setBrewTime(--brewTime);
		}
		// start button clicked
		else if (v == btnStartBrew) {
			// is brewing the stop
			if (isBrewing) {
				stopBrew();
			}
			else if (finishedBrew) {
				moreBrew();
			}
			// not brewing the start
			else { startBrew(); }
		}
	}

}
