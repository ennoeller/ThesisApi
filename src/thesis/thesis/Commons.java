package thesis.thesis;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class Commons extends Activity {

	protected static final int REQ_GET_CREDENTIALS = 1;
	public EditText userName;
	public EditText passWord;

	public void saveGetCredentials(String username, String password) {
		Intent intent = new Intent(getApplicationContext(), UsersList.class);

		if (!TextUtils.isEmpty(username)) {
			intent.putExtra("username", username);
		}
		if (!TextUtils.isEmpty(password)) {
			intent.putExtra("password", password);
		}
		startActivityForResult(intent, REQ_GET_CREDENTIALS);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQ_GET_CREDENTIALS) {
			if (resultCode == RESULT_OK) {
				String uname = data.getStringExtra("username");
				String passw = data.getStringExtra("password");

				Toast.makeText(getApplicationContext(), "Works so far!",
						Toast.LENGTH_SHORT).show();

				userName.setText(uname);
				passWord.setText(passw);
			}
		}
	}
}
