package cn.mointe.vaccination.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import cn.mointe.vaccination.db.DBHelper;

public class VaccinationProvider extends ContentProvider {

	private DBHelper mDBHelper;
	private ContentResolver mContentResolver;

	private static final UriMatcher URI_MATCHER;
	private static final int ALL_ROW = 1;
	private static final int SINGLE_ROW = 2;

	private static final String AUTHORITIES = "cn.mointe.vaccination.providers.VaccinationProvider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITIES
			+ "/vaccination");

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(AUTHORITIES, "vaccination", ALL_ROW);
		URI_MATCHER.addURI(AUTHORITIES, "vaccination/#", SINGLE_ROW);
	}

	@Override
	public boolean onCreate() {
		mDBHelper = new DBHelper(getContext());
		mContentResolver = getContext().getContentResolver();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		switch (URI_MATCHER.match(uri)) {
		case ALL_ROW:
			cursor = db.query(DBHelper.VACCINATION_TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case SINGLE_ROW:
			long rowid = ContentUris.parseId(uri);
			String where = DBHelper.VACCINATION_COLUMN_ID + "=" + rowid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			cursor = db.query(DBHelper.VACCINATION_TABLE_NAME, projection, where,
					selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}
		cursor.setNotificationUri(mContentResolver, uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {

		switch (URI_MATCHER.match(uri)) {
		case ALL_ROW:
			return "vnd.android.cursor.dir/vaccination";
		case SINGLE_ROW:
			return "vnd.android.cursor.item/vaccination";
		default:
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long rowid = db.insert(DBHelper.VACCINATION_TABLE_NAME, null, values);
		if (rowid > 0) {
			Uri insertUri = ContentUris.withAppendedId(CONTENT_URI, rowid);
			mContentResolver.notifyChange(uri, null);
			return insertUri;
		} else {
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int num = 0;
		switch (URI_MATCHER.match(uri)) {
		case ALL_ROW:
			num = db.delete(DBHelper.VACCINATION_TABLE_NAME, selection, selectionArgs);
			break;
		case SINGLE_ROW:
			long rowid = ContentUris.parseId(uri);
			String where = DBHelper.VACCINATION_COLUMN_ID + "=" + rowid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			num = db.delete(DBHelper.VACCINATION_TABLE_NAME, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}
		mContentResolver.notifyChange(uri, null);
		return num;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int num = 0;
		switch (URI_MATCHER.match(uri)) {
		case ALL_ROW:
			num = db.update(DBHelper.VACCINATION_TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case SINGLE_ROW:
			long rowid = ContentUris.parseId(uri);
			String where = DBHelper.VACCINATION_COLUMN_ID + "=" + rowid;
			if (selection != null && !"".equals(selection.trim())) {
				where += " and " + selection;
			}
			num = db.update(DBHelper.VACCINATION_TABLE_NAME, values, where,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("this is Unknown Uri:" + uri);
		}

		mContentResolver.notifyChange(uri, null);
		return num;
	}

}
