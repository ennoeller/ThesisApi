package thesis.thesis;

import haibison.android.lockpattern.LockPatternActivity;
import thesis.thesis.R;
import thesis.thesis.contentprovider.UsersDataContentProvider;
import thesis.thesis.database.UserTable;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewUser extends Activity {

	// Layouts
	EditText uname;
	EditText passw;
	EditText verify;

	Button bCreate;

	// Value holders
	String username;
	String password;
	String verifypassword;

	char[] pattern;

	private static final int REQ_CREATE_PATTERN = 1;
	private static final int REQ_ENTER_PATTERN = 2;

	private Uri usersUri;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new);

		uname = (EditText) this.findViewById(R.id.enter_username);
		passw = (EditText) this.findViewById(R.id.enter_password);
		verify = (EditText) this.findViewById(R.id.verify_password);

		Bundle extras = getIntent().getExtras();

		usersUri = (savedInstanceState == null) ? null
				: (Uri) savedInstanceState
						.getParcelable(UsersDataContentProvider.CONTENT_ITEM_TYPE);

		if (extras != null) {
			usersUri = extras
					.getParcelable(UsersDataContentProvider.CONTENT_ITEM_TYPE);

			// fillData(usersUri);
		}

		bCreate = (Button) this.findViewById(R.id.bCreate);
		bCreate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (TextUtils.isEmpty(uname.getText().toString())
						|| TextUtils.isEmpty(passw.getText().toString())
						|| !passw.getText().toString()
								.equals(verify.getText().toString())) {
					makeToast();
				} else {

					username = uname.getText().toString();
					password = passw.getText().toString();
					verifypassword = verify.getText().toString();

					Intent intent = new Intent(
							LockPatternActivity.ACTION_CREATE_PATTERN, null,
							getApplicationContext(), LockPatternActivity.class);
					startActivityForResult(intent, REQ_CREATE_PATTERN);
				}
			}
		});
	}

	/*
	 * private void fillData(Uri uri) { String[] projection = {
	 * UsersTable.COLUMN_USER_NAME, UsersTable.COLUMN_PASSWORD,
	 * TodoTable.COLUMN_CATEGORY }; Cursor cursor =
	 * getContentResolver().query(uri, projection, null, null, null); if (cursor
	 * != null) { cursor.moveToFirst(); String category =
	 * cursor.getString(cursor
	 * .getColumnIndexOrThrow(TodoTable.COLUMN_CATEGORY));
	 * 
	 * for (int i = 0; i < mCategory.getCount(); i++) {
	 * 
	 * String s = (String) mCategory.getItemAtPosition(i); if
	 * (s.equalsIgnoreCase(category)) { mCategory.setSelection(i); } }
	 * 
	 * mTitleText.setText(cursor.getString(cursor
	 * .getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY)));
	 * mBodyText.setText(cursor.getString(cursor
	 * .getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION)));
	 * 
	 * // always close the cursor cursor.close(); } }
	 */

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(UsersDataContentProvider.CONTENT_ITEM_TYPE,
				usersUri);
	}

	/*@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}*/

	private void saveState() {
		username = uname.getText().toString();
		password = passw.getText().toString();
		verifypassword = verify.getText().toString();

		// only save if no field is empty

		if (username.length() == 0 && password.length() == 0
				&& verifypassword.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(UserTable.COLUMN_USER_NAME, username);
		values.put(UserTable.COLUMN_PASSWORD, password);

		if (usersUri == null) {
			// New user
			usersUri = getContentResolver().insert(
					UsersDataContentProvider.CONTENT_URI, values);
		} else {
			// Update user
			getContentResolver().update(usersUri, values, null, null);
		}
	}

	private void makeToast() {
		Toast.makeText(NewUser.this, "Please maintain a summary",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_CREATE_PATTERN: {
			if (resultCode == RESULT_OK) {
				pattern = data
						.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);

				Context context = getApplicationContext();
				String patternText = new String(pattern);

				ContentValues values = new ContentValues();
				values.put(UserTable.COLUMN_USER_NAME, username);
				values.put(UserTable.COLUMN_PASSWORD, password);
				values.put(UserTable.COLUMN_PATTERN, patternText);

				usersUri = getContentResolver().insert(
						UsersDataContentProvider.CONTENT_URI, values);

				setResult(RESULT_OK);
				finish();
			}
			break;
		}// REQ_CREATE_PATTERN
		case REQ_ENTER_PATTERN: {
			/*
			 * NOTE that there are 4 possible result codes!!!
			 */
			switch (resultCode) {
			case RESULT_OK:
				// The user passed
				Context context = getApplicationContext();
				CharSequence text = "All good!";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				break;
			case RESULT_CANCELED:
				// The user cancelled the task
				break;
			case LockPatternActivity.RESULT_FAILED:
				// The user failed to enter the pattern
				break;
			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				// The user forgot the pattern and invoked your recovery
				// Activity.
				break;
			}

			/*
			 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
			 * the number of tries that the user did.
			 */
			int retryCount = data.getIntExtra(
					LockPatternActivity.EXTRA_RETRY_COUNT, 0);

			break;
		}// REQ_ENTER_PATTERN
		}
	}

}
