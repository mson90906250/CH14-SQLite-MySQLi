package tw.tcnr01.m1417.providers;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class FriendsContentProvider extends ContentProvider {
    private static final String AUTHORITY = "tw.tcnr01.m1417.providers.FriendsContentProvider";
    private static final String DB_FILE = "friends.db", DB_TABLE = "member";
    private static final int URI_ROOT = 0, DB_TABLE_FRIENDS = 1;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TABLE);
    private static final UriMatcher sUriMatcher = new UriMatcher(URI_ROOT);

    static {
        sUriMatcher.addURI(AUTHORITY, DB_TABLE, DB_TABLE_FRIENDS);
    }

    private SQLiteDatabase mFriendDb;
    String TAG = "tcnr01=>";
    private static final String crTBsql = "CREATE TABLE " + DB_TABLE + " ( "
            + "id INTEGER PRIMARY KEY," + "name TEXT NOT NULL," + "grp TEXT,"
            + "address TEXT);";

    @Override
    public boolean onCreate() {
        // ---宣告 使用Class DbOpenHelper.java 作為處理SQLite介面
        // Content Provider 就是 data Server, 負責儲存及提供資料, 他允許任何不同的APP使用
        // 共同的資料(不同的APP用同一個SQLite).

        DbOpenHelper friendDbOpenHelper = new DbOpenHelper(getContext(), DB_FILE, null, 1);

        mFriendDb = friendDbOpenHelper.getWritableDatabase();
//        String aa = "a";
        // 檢查資料表是否已經存在，如果不存在，就建立一個。
        Cursor cursor = mFriendDb
                .rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DB_TABLE + "'", null);
        if (cursor != null) {
            if (cursor.getCount() == 0) // 沒有資料表，要建立一個資料表。
                mFriendDb.execSQL(crTBsql);
            cursor.close();
        }
        return true;
    }

    @Override
    //selection 是指要選擇的欄位
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (sUriMatcher.match(uri) != DB_TABLE_FRIENDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
//  Cursor c = mFriendDb.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
        Cursor c = mFriendDb.query(true, DB_TABLE, projection, selection, selectionArgs, null, null, sortOrder, null); //"ASC DESC"
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != DB_TABLE_FRIENDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        long rowId = mFriendDb.insert(DB_TABLE, null, values);
        if (rowId > 0) {
            // 在已有的 Uri的後面追加ID數據
            Uri insertedRowUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            ;
            // 通知數據已經改變
            getContext().getContentResolver().notifyChange(insertedRowUri, null);
            return insertedRowUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (sUriMatcher.match(uri) != DB_TABLE_FRIENDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int rowsAffected = mFriendDb.delete(DB_TABLE, selection, null);
        return rowsAffected;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (sUriMatcher.match(uri) != DB_TABLE_FRIENDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int rowsAffected = mFriendDb.update(DB_TABLE, values, selection, null);
        return rowsAffected;
    }
    // ------------------------

}