package thesis.thesis;

import haibison.android.lockpattern.LockPatternActivity;
import thesis.thesis.R;
import thesis.thesis.contentprovider.UsersDataContentProvider;
import thesis.thesis.database.UserTable;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
			if (getIntent().hasExtra("username")) {
				uname.setText(getIntent().getStringExtra("username"));
			}
			if (getIntent().hasExtra("password")) {
				passw.setText(getIntent().getStringExtra("password"));
			}
		}

		bCreate = (Button) this.findViewById(R.id.bCreate);
		bCreate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// query for usernames and check if entered username already exists
				boolean exists = false;
				Cursor mCursor = getContentResolver().query(UsersDataContentProvider.CONTENT_URI, new String[] {UserTable.COLUMN_USER_NAME}, null, null, null);
				if (mCursor.moveToFirst()) {
					do {
						if (mCursor.getString(mCursor.getColumnIndex(UserTable.COLUMN_USER_NAME)).equals(uname.getText().toString())) {
							exists = true;
						}
					} while(mCursor.moveToNext());
				}
				
				if (TextUtils.isEmpty(uname.getText().toString())
						|| TextUtils.isEmpty(passw.getText().toString())
						|| TextUtils.isEmpty(verify.getText().toString())) {
					Toast.makeText(getApplicationContext(), "There is an empty field!", Toast.LENGTH_SHORT).show();
				} else if (!passw.getText().toString().equals(verify.getText().toString())) {
					Toast.makeText(getApplicationContext(), "Passwords don´t match!", Toast.LENGTH_SHORT).show();
				} else if(exists == true) {
					Toast.makeText(getApplicationContext(), "User name already exists! Edit or Delete the old!", Toast.LENGTH_SHORT).show();
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

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(UsersDataContentProvider.CONTENT_ITEM_TYPE,
				usersUri);
	}

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
			break;
		}// REQ_ENTER_PATTERN
		}
	}
}
