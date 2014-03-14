package cn.mointe.vaccination.provider;

import cn.mointe.vaccination.db.DBHelper;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class VaccineProvider extends ContentProvider {

	private ContentResolver mContentResolver;
	private DBHelper mDBHelper;

	public static final Uri CONTENT_URI = Uri
			.parse("content://cn.mointe.baby.cn.mointe.vaccination.provider.VaccineProvider/vaccine");
	
	public static final int ALL_ROWS = 1;
	public static final int SINGLE_ROW = 2;
	public static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("cn.mointe.baby.babyprovider", "vaccine", ALL_ROWS);
		uriMatcher.addURI("cn.mointe.baby.babyprovider", "vaccine/#", SINGLE_ROW);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS:
			count = db.delete(DBHelper.VACCINE_TABLE_NAME, selection,
					selectionArgs);
			break;
		case SINGLE_ROW:
			long babyid = ContentUris.parseId(uri);
			String where = DBHelper.VACCINE_COLUMN_ID + "=" + babyid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			count = db.delete(DBHelper.VACCINE_TABLE_NAME, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		mContentResolver.notifyChange(uri, null);
		db.close();
		return count;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		long rowid = db.insert(DBHelper.VACCINE_TABLE_NAME, null, contentValues);
		if (rowid > 0) {
			Uri insertUri = ContentUris.withAppendedId(CONTENT_URI, rowid);
			mContentResolver.notifyChange(uri, null);
			return insertUri;
		} else {
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}

	}

	@Override
	public boolean onCreate() {
		mDBHelper = new DBHelper(getContext());
		mContentResolver = getContext().getContentResolver();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS:
			cursor = db.query(DBHelper.VACCINE_TABLE_NAME, projection,
					selection, selectionArgs, null, null, sortOrder);
			break;
		case SINGLE_ROW:
			long rowid = ContentUris.parseId(uri);
			String where = DBHelper.VACCINATION_COLUMN_ID + "=" + rowid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			cursor = db.query(DBHelper.VACCINE_TABLE_NAME, projection,
					where, selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}
		cursor.setNotificationUri(mContentResolver, uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS:
			count = db.update(DBHelper.VACCINE_TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case SINGLE_ROW:
			long questionid = ContentUris.parseId(uri);
			String where = "id = " + questionid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			count = db.update(DBHelper.VACCINE_TABLE_NAME, values, where,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		mContentResolver.notifyChange(uri, null);
		db.close();
		return count;
	}

}
