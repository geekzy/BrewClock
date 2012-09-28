package net.geekzy.mobile.brewclock.activities;

import net.geekzy.mobile.brewclock.R;
import net.geekzy.mobile.brewclock.data.TeaData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AddTeaActivity extends Activity implements OnSeekBarChangeListener {
	/** Properties **/
	protected EditText txtTeaName;
	protected SeekBar seekBrewTime;
	protected TextView lblBrewTime;

	protected InputMethodManager imm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_tea);
		final Activity parentActivity = this;

		// Connect interface elements to properties
		txtTeaName = (EditText) findViewById(R.id.tea_name);
		seekBrewTime = (SeekBar) findViewById(R.id.brew_time_seekbar);
		lblBrewTime = (TextView) findViewById(R.id.brew_time_value);

		// set seekbar change listener
		seekBrewTime.setOnSeekBarChangeListener(this);

		// fokus tea name input
		txtTeaName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					parentActivity
					.getWindow()
					.setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		txtTeaName.requestFocus();
	}

	/**
	 * Save tea brew time profile
	 */
	public boolean saveTea() {
		// read values from the interface
		String teaName = txtTeaName.getText().toString();
		int brewTime = seekBrewTime.getProgress() + 1;

		// validate a name has been entered for the tea
		if (teaName.length() < 2) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.err_tea_title);
			dialog.setMessage(R.string.err_tea_no_name);
			dialog.show();

			return false;
		}

		// the tea is valid, so connect to thea db and inssert the tea
		TeaData teaData = new TeaData(this);
		teaData.insert(teaName, brewTime);
		teaData.close();

		// hide soft keyboard
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		imm.toggleSoftInputFromWindow(txtTeaName.getWindowToken(), 0, 0);

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_tea, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_tea:
			// save tea brew config
			if (saveTea()) {
				Toast.makeText(
						this,
						getString(R.string.suc_save_tea, txtTeaName.getText()
								.toString()), Toast.LENGTH_LONG).show();
				txtTeaName.setText("");
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar == seekBrewTime) {
			// update the brew time label with the chosen value
			lblBrewTime.setText(++progress + "m");
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
