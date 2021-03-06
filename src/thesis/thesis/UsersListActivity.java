/*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*/

package thesis.thesis;

import haibison.android.lockpattern.LockPatternActivity;
import thesis.thesis.contentprovider.UsersDataContentProvider;
import thesis.thesis.database.UserTable;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * 
 * @author Enno Eller
 *
 */
public class UsersListActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private String username;
	private String password;
	private String pattern;
	
	private Uri uri;

	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_DELETE = 2;

	private static final int REQ_ENTER_PATTERN = 2;

	private SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_list);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			Intent createNew = new Intent(this, NewUserActivity.class);
			if (getIntent().hasExtra("username")) {
				createNew.putExtra("username", getIntent().getStringExtra("username"));
			}
			if (getIntent().hasExtra("password")) {
				createNew.putExtra("password", getIntent().getStringExtra("password"));
			}
			startActivity(createNew);
		}
		
		this.getListView().setDividerHeight(2);
		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.insert) {
			createUser();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ACTIVITY_DELETE:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			uri = Uri.parse(UsersDataContentProvider.CONTENT_URI + "/"
					+ info.id);
			getContentResolver().delete(uri, null, null);
			fillData();
			return true;
		case ACTIVITY_EDIT:
			AdapterContextMenuInfo info1 = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Intent i = new Intent(this, EditUserActivity.class);
			uri = Uri.parse(UsersDataContentProvider.CONTENT_URI + "/"
					+ info1.id);
			i.putExtra(UsersDataContentProvider.CONTENT_ITEM_TYPE, uri);

			startActivity(i);

		}
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Starts activity NewUserActivity.
	 */
	private void createUser() {
		Intent i = new Intent(this, NewUserActivity.class);
		startActivity(i);
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		uri = Uri.parse(UsersDataContentProvider.CONTENT_URI + "/" + id);
		String[] projection = { UserTable.COLUMN_USER_NAME,
				UserTable.COLUMN_PASSWORD, UserTable.COLUMN_PATTERN };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);

		if (cursor != null) {
			cursor.moveToFirst();
			username = cursor.getString(cursor
					.getColumnIndexOrThrow(UserTable.COLUMN_USER_NAME));
			password = cursor.getString(cursor
					.getColumnIndexOrThrow(UserTable.COLUMN_PASSWORD));
			pattern = cursor.getString(cursor
					.getColumnIndexOrThrow(UserTable.COLUMN_PATTERN));	
		} 

		char[] savedPattern = pattern.toCharArray();
		
		Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN,
				null, this.getApplicationContext(), LockPatternActivity.class);
		intent.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
		startActivityForResult(intent, REQ_ENTER_PATTERN);
	}

	/**
	 * Loads the usernames to the listview.
	 */
	private void fillData() {

		String[] from = new String[] { UserTable.COLUMN_USER_NAME };
		int[] to = new int[] { R.id.label };
		
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.list_item, null, from,
				to, 0);

		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, ACTIVITY_EDIT, 0, "Edit");
		menu.add(0, ACTIVITY_DELETE, 0, "Delete");
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { UserTable.COLUMN_ID, UserTable.COLUMN_USER_NAME };
		CursorLoader cursorLoader = new CursorLoader(this,
				UsersDataContentProvider.CONTENT_URI, projection, null, null,
				null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {
			switch (resultCode) {
			case RESULT_OK:
				Intent returnIntent = new Intent();
				returnIntent.putExtra("username", username);
				returnIntent.putExtra("password", password);
				setResult(RESULT_OK, returnIntent);
				finish();
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
