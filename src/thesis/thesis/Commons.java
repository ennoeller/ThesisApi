/*
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package thesis.thesis;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

/**
 * 
 * @author Enno Eller
 * 
 */

public class Commons extends Activity {

	public static final int REQ_GET_CREDENTIALS = 1;
	public EditText userName;
	public EditText passWord;

	/**
	 * Calls the activity UsersListActivity on the library.
	 * 
	 * @param username
	 * @param password
	 */
	public void saveGetCredentials(String username, String password) {
		Intent intent = new Intent(getApplicationContext(),
				UsersListActivity.class);

		if (!TextUtils.isEmpty(username)) {
			intent.putExtra("username", username);
		}
		if (!TextUtils.isEmpty(password)) {
			intent.putExtra("password", password);
		}
		startActivityForResult(intent, REQ_GET_CREDENTIALS);
	}

	/**
	 * Returns the results from the library if RESULT_OK and sets them to
	 * edittexts accordingly.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQ_GET_CREDENTIALS) {
			if (resultCode == RESULT_OK) {
				String uname = data.getStringExtra("username");
				String passw = data.getStringExtra("password");

				userName.setText(uname);
				passWord.setText(passw);
			}
		}
	}
}
