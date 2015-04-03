package thesis.thesis;

import haibison.android.lockpattern.LockPatternActivity;
import thesis.thesis.contentprovider.UsersDataContentProvider;
import thesis.thesis.database.UserTable;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditUser extends Activity {

	// Layouts
	EditText currentPass;
	EditText newPass;
	EditText verifyNewPass;

	Button bChangePass;
	Button bChangePattern;

	// Value holders
	String oldPass;
	String newPassword;
	String verifyNewPassword;

	char[] pattern;

	private static final int REQ_CREATE_PATTERN = 1;

	private Uri userUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_user);

		currentPass = (EditText) this.findViewById(R.id.enter_old_password);
		newPass = (EditText) this.findViewById(R.id.enter_new_password);
		verifyNewPass = (EditText) this.findViewById(R.id.verify_new_password);

		Bundle extras = getIntent().getExtras();

		userUri = (savedInstanceState == null) ? null
				: (Uri) savedInstanceState
						.getParcelable(UsersDataContentProvider.CONTENT_ITEM_TYPE);

		if (extras != null) {
			userUri = extras
					.getParcelable(UsersDataContentProvider.CONTENT_ITEM_TYPE);

			// fillData(usersUri);
		}

		bChangePass = (Button) this.findViewById(R.id.bChangePassword);
		bChangePass.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (TextUtils.isEmpty(currentPass.getText().toString())) {
					Toast.makeText(getApplicationContext(),
							"You need to enter the Current Password!",
							Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(newPass.getText().toString())
						|| !newPass.getText().toString()
								.equals(verifyNewPass.getText().toString())) {
					Toast.makeText(
							getApplicationContext(),
							"Enter New Password or make sure the New Password and verification match!",
							Toast.LENGTH_SHORT).show();
				} else {

					String[] projection = { UserTable.COLUMN_PASSWORD };
					Cursor cursor = getContentResolver().query(userUri,
							projection, null, null, null);

					if (cursor != null) {
						cursor.moveToFirst();
						oldPass = cursor.getString(cursor
								.getColumnIndexOrThrow(UserTable.COLUMN_PASSWORD));
					}

					if (oldPass.equals(currentPass.getText().toString())) {
						ContentValues values = new ContentValues();
						values.put(UserTable.COLUMN_PASSWORD, newPass.getText()
								.toString());
						getContentResolver()
								.update(userUri, values, null, null);
						finish();
					}
				}
			}
		});

		bChangePattern = (Button) this.findViewById(R.id.bChangePattern);
		bChangePattern.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (TextUtils.isEmpty(currentPass.getText().toString())) {
					Toast.makeText(getApplicationContext(),
							"You need to enter the Current Password!",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(
							LockPatternActivity.ACTION_CREATE_PATTERN, null,
							getApplicationContext(), LockPatternActivity.class);
					startActivityForResult(intent, REQ_CREATE_PATTERN);
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_CREATE_PATTERN: {
			if (resultCode == RESULT_OK) {
				pattern = data
						.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
				String patternText = new String(pattern);

				ContentValues values = new ContentValues();
				values.put(UserTable.COLUMN_PATTERN, patternText);

				getContentResolver().update(userUri, values, null, null);
				finish();
			}
			break;
		}// REQ_CREATE_PATTERN
		}
	}
}
