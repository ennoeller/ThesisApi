/*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* Please send inquiries to huber AT ut DOT ee
*/


package thesis.thesis.database;

//import org.gChess.manager.MyEventContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

/**
 * 
 * @author Huber Flores
 *
 */

public class DatabaseManager {
		
	private Uri dbUri;
	private Context dContext;
	
	
	public DatabaseManager(Context c){
		this.dContext = c;
	}
	
	public void saveData(String usern, String passw, String pattern){
		ContentValues values = new ContentValues();
		values.put(UserTable.COLUMN_USER_NAME, usern);
		values.put(UserTable.COLUMN_PASSWORD, passw);
		values.put(UserTable.COLUMN_PATTERN, pattern);
		 	
		//dbUri = dContext.getContentResolver().insert(MyEventContentProvider.CONTENT_URI, values);
		
	 }
	
	public void setDbUri(Uri cursor){
		this.dbUri = cursor;
	}


}
