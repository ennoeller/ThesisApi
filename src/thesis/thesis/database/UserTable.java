/*
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package thesis.thesis.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author Enno Eller
 *
 */
public class UserTable {

	//Table name
	public static final String TABLE_USERS = "users";
	//Columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_USER_NAME = "user_name";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_PATTERN = "pattern";
	
	//Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_USERS 
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_USER_NAME + " text not null, "
			+ COLUMN_PASSWORD + " text not null, "
			+ COLUMN_PATTERN + " text not null"
			+");";
	
	 //Database creation
	 public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(DATABASE_CREATE);
	 }

	 
	 public static void onUpgrade(SQLiteDatabase database, int oldVersion,
		      int newVersion) {
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		    onCreate(database);
	 }
}
